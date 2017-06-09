(function ($) {

    /*
     * This would be a really elegant way to isolate the numbered heading bodies from the 
     * main body; however, Confluence is only deploying jQuery 1.7.2 at this time and that 
     * version doesn't support this functionality (but 2 and 3 seem to)

    $.expr[":"].notInNumberedHeadingMacro = function( element, elementIndex, selector ){
       return ( $(element).parents("table[data-macro-name=numbered-headings]").length == 0
             || $(element).parents("table[data-macro-name=numberedheadings]").length == 0);
    };
    numberedHeadings.number( $("#wysiwygTextarea_ifr").contents().filter(":notInNumberedHeadingMacro()") );

    */

    tinymce.create("tinymce.plugins.NumberedHeadingsPlugin", {
        init: function (ed, url) {
            ed.onNodeChange.add(function (ed, cm, e) {
                var pTag = false;
                var bRenumber = false;

                if (e != null && e.nodeType === 1 ){
                    // if a line is converted to a heading type
                    if (e.tagName.length === 2 && e.tagName[0] === "H") {
                       bRenumber = true;
                    // if a line is converted from a heading type to P or PRE
                    } else if (e.tagName[0] === "P" && $(e).children().first().is("span.data-nh-numbering") ){
                       bRenumber = true;
                       pTag = true;
                    // if a bodyless macro is updated
                    } else if (e.tagName === "IMG" && $(e).is("img[data-macro-name=numbered-headings-for-page]") ){
                       bRenumber = true;
                    // if a macro with a body and the first line is a inline-inserted numbered heading
                    } else if( isFirstLineOfNumberedHeadingBlock( e ) ){
                       bRenumber = true;
                    }
                }

                if ( bRenumber ){
                    if( pTag ){
                       $(e).children().first().remove();
                    }

                    if( $(e).parents("table[data-macro-name=numbered-headings]").length > 0 ){
                       numberedHeadings.number( $(e).parents("table[data-macro-name=numbered-headings]"), false, false );
                    } else if( usingTransitionalNumberedHeadingMacro() && $(e).parents("table[data-macro-name=numberedheadings]").length > 0 ){
                       numberedHeadings.number( $(e).parents("table[data-macro-name=numberedheadings]"), false, false );
                    } else if( numberEntirePage($) ){
                       numberedHeadings.number( $("#wysiwygTextarea_ifr").contents(), true, false );
                    }
                }
            });

            ed.onInit.add(function (ed) {
              $("#wysiwygTextarea_ifr").load(function() {
                  $("#wysiwygTextarea_ifr").contents().find("table[data-macro-name=numbered-headings]").each(function (index, macroTable) {
                      numberedHeadings.number(macroTable, false, true);
                  });
                  if( usingTransitionalNumberedHeadingMacro() ){
                     $("#wysiwygTextarea_ifr").contents().find("table[data-macro-name=numberedheadings]").each(function (index, macroTable) {
                         numberedHeadings.number(macroTable, false, true);
                     });
                  }
                  if( numberEntirePage($) ){
                     $("#wysiwygTextarea_ifr").load(function() {
                         numberedHeadings.number( $("#wysiwygTextarea_ifr").contents(), true, true );
                     });
                  }
              });
            });

            $(window).bind("updated.macro", function (event, macroTable) {
                numberedHeadings.number(macroTable, false);
                if( numberEntirePage($) ){
                    numberedHeadings.number( $("#wysiwygTextarea_ifr").contents(), true, false );
                }
            });
        },

        getInfo: function () {
            return {
                longname: "Numbered Headings TinyMCE Plugin",
                author: "Avisi B.V.",
                authorurl: "http://avisi.nl",
                version: tinymce.majorVersion + "." + tinymce.minorVersion
            };
        }
    });

    // Register plugin
    tinymce.PluginManager.add("numberedheadings", tinymce.plugins.NumberedHeadingsPlugin);

    AJS.Editor.Adapter.addTinyMcePluginInit(function (settings) {
        settings.plugins += ",numberedheadings";
    });

    function isFirstLineOfNumberedHeadingBlock( e ){
       var bFlag = false;
       if ($(e).parent().is("table[data-macro-name=numberedheadings]") 
        || $(e).parent().is("table[data-macro-name=numbered-headings]") ){
          bFlag = true;
       } else if( $(e).parent() != null && $(e).is( ":first-child" ) ){
          bFlag = isFirstLineOfNumberedHeadingBlock( $(e).parent()[0] );
       }
       return bFlag;
    }

})(AJS.$);

var numberedHeadings = (function ($) {
    var lastLevel;
    var startAt; // The heading to start at, possible values: 0 - 5 (where 0 is h1 and 5 = h6)
    var counts = [0, 0, 0, 0, 0, 0]; // Change if you want to start with a higher number

    var formatters = {
        "decimal": {
            format: function (level) {
                return level;
            }
        },

        "iso-2145": {
            format: function (level) {
                return formatters["decimal"].format(level);
            },

            shouldRenderSeparator: function (lastLevel) {
                return !lastLevel;
            }
        },

        "full-decimal": {
            lowNumbers: ["zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen"],
            highNumbers: ["", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"],

            format: function (level) {
                if (level > this.lowNumbers.length - 1) {
                    level = level.toString();

                    var firstDigit = level[0];
                    var secondDigit = level[1];
                    var formattedString = this.highNumbers[firstDigit];

                    if (secondDigit > 0) {
                        formattedString += "-";
                        formattedString += this.lowNumbers[secondDigit];
                    }

                    return formattedString;
                }
                else {
                    return this.lowNumbers[level];
                }
            }
        },

        "lower-latin": {
            range: ["a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"],

            format: function (level) {
                return this.range[level - 1];
            }
        },

        "upper-latin": {
            format: function (level) {
                var lowerCaseLatin = formatters["lower-latin"].format(level);

                return lowerCaseLatin.toUpperCase();
            }
        },

        "lower-greek": {
            range: ["\u03B1", "\u03B2", "\u03B3", "\u03B4", "\u03B5", "\u03B6", "\u03B7", "\u03B8", "\u03B9", "\u03BA", "\u03BB", "\u03BC", "\u03BD", "\u03BE", "\u03BF", "\u03C0", "\u03C1", "\u03C3", "\u03C4", "\u03C5", "\u03C6", "\u03C7", "\u03C8", "\u03C9"],

            format: function (level) {
                return this.range[level - 1];
            }
        },

        "upper-greek": {
            range: ["\u0391", "\u0392", "\u0393", "\u0394", "\u0395", "\u0396", "\u0397", "\u0398", "\u0399", "\u039A", "\u039B", "\u039C", "\u039D", "\u039E", "\u039F", "\u03A0", "\u03A1", "\u03A3", "\u03A4", "\u03A5", "\u03A6", "\u03A7", "\u03A8", "\u03A9"],

            format: function (level) {
                return this.range[level - 1];
            }
        },

        "lower-roman": {
            format: function (level) {
                var upperCaseRoman = formatters["upper-roman"].format(level);

                return upperCaseRoman.toLowerCase();
            }
        },

        "upper-roman": {
            units: ["", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"], // Units 0 to 9
            tens: ["", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"],  // Tens 0 to 90
            hundreds: ["", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"],  // Hundreds 0 to 900

            format: function (level) {
                return (this.hundreds[level / 100] || "") + (this.tens[level / 10] || "") + (this.units[level] || "");
            }
        },

        "custom": {
            customFormatterRegex: new RegExp("(\\[h[1-6].[a-zA-Z0-9-]+\\])"),

            parseCustomFormat: function (level, customFormats) {
                return customFormats[level - 1].split(this.customFormatterRegex);
            },

            format: function (level, customFormats) {
                var parsedFormatters = this.parseCustomFormat(level, customFormats);
                var formattedString = "";

                for (var i = 0; i < parsedFormatters.length; i++) {
                    var formatterString = parsedFormatters[i];

                    if (this.customFormatterRegex.test(formatterString)) {
                        var headingNumber = formatterString[2];

                        if (headingNumber >= startAt + 1 && headingNumber <= level) {
                            var formatterType = formatterString.substring(4, formatterString.length - 1);

                            if (formatterType !== "custom") {
                                var formatter = formatters[formatterType] || formatters["decimal"];
                                var currentLevel = counts[headingNumber - 1];

                                formattedString += formatter.format(currentLevel);
                            }
                        }
                    } else {
                        formattedString += formatterString;
                    }
                }

                return formattedString;
            }
        }
    };

    function clearCount(start) {
        for (var i = start; i < 6; i++) {
            counts[i] = 0;
        }
    }

    function parseParameters(macroTable) {

        var parameters = {"number-format": "decimal"};
        var parameterString = "";

        if( $(macroTable).attr("data-macro-parameters") !== undefined ){
            parameterString = $(macroTable).attr("data-macro-parameters");
        } else if( numberEntirePage($) ){
            var editorIFrame = $("#wysiwygTextarea_ifr");
            parameterString = $("img[data-macro-name=numbered-headings-for-page]", editorIFrame.contents() ).first().attr("data-macro-parameters");
        }

        if (parameterString !== undefined) {
            var macroParameters = splitParameterString(parameterString);

            for (var i = 0; i < macroParameters.length; i++) {
                var parameterKeyAndValue = macroParameters[i].split("=");

                parameters[parameterKeyAndValue[0]] = parameterKeyAndValue[1];
            }
        }

        var startNumberingAt = parameters["start-numbering-at"];
        if (startNumberingAt !== undefined) {
            startAt = startNumberingAt[1] - 1;
        } else {
            startAt = 0;
        }

        clearCount(0);

        var startNumberingWithValue = parameters["start-numbering-with"];


        if (startNumberingWithValue !== undefined) {
            var startNumberingWithParts = startNumberingWithValue.split(",");
            for (var i = 0; i < startNumberingWithParts.length; i++) {
                var startNumberingWith = parseInt(startNumberingWithParts[i], 10);

                if (!isNaN(startNumberingWith)) {
                  counts[startAt + i] = startNumberingWith - 1;
                }
            }

        }

        lastLevel = 0;

        return parameters;
    }

    // Simulates a negative lookbehind, which is not possible with JavaScript RegEx
    function splitParameterString(parameterString) {
        var splittedParameterString = reverseString(parameterString).split(/\|(?!\\)/);

        for (var i = 0; i < splittedParameterString.length; i++) {
            splittedParameterString[i] = reverseString(splittedParameterString[i]);
            var idx = splittedParameterString[i].indexOf("\\|");

            if (idx !== undefined) {
                splittedParameterString[i] = splittedParameterString[i].substring(0, idx) + splittedParameterString[i].substring(idx + 1, splittedParameterString[i].length);
            }
        }

        return splittedParameterString;
    }

    function reverseString(text) {
        return text.split("").reverse().join("");
    }

    function number(macroTable, filter, initialization) {
        var parameters = parseParameters(macroTable);
        var doIt = true;
        var moveCaret = false;

        try{
           if( !AJS.$( tinyMCE.activeEditor.selection.getNode()).children().first().hasClass("data-nh-numbering") ){
              moveCaret = true;
           }
        } catch( error ){
        }


        $(macroTable).find("h1, h2, h3, h4, h5, h6").each(function (index, element) {
            if( filter && inNumberedHeadingBody( element ) ){
               doIt = false;
            } else {
               doIt = true;
            }

            if( doIt ){
               if (shouldRenderNumbering(element.nodeName[1], parameters)) {
                   numberHeading(element, parameters);
               } else {
                   removeNumbering(element);
               }
            }
        });

        if( moveCaret && !initialization ){
           //alert( "Should move the caret!" );
           //tinyMce.activeEditor.selection.setCursorLocation( tinyMCE.activeEditor.selection.getNode(), 1 );
        } else if( moveCaret ){
           //alert( "Initialized: Should move the caret!" );
        }
    }
    
    function inNumberedHeadingBody( element ){
       var bFlag = false;
       if( $(element).parents("table[data-macro-name=numberedheadings]").length > 0 
        || $(element).parents("table[data-macro-name=numbered-headings]").length > 0 ){
          var bFlag = true;
       }

       return bFlag;
    } 

    function shouldRenderNumbering(currentHeadingLevel, parameters) {
        var skipHeadingsParameterValue = parameters["skip-headings"];

        if (!skipHeadingsParameterValue) {
            return true;
        }

        currentHeadingLevel = parseInt(currentHeadingLevel, 10);
        var headingsToSkip = skipHeadingsParameterValue.split(",");
        for (var i = 0; i < headingsToSkip.length; i++) {
            var headingLevelToSkip = parseInt($.trim(headingsToSkip[i])[1], 10);

            if (headingLevelToSkip === currentHeadingLevel) {
                return false;
            }
        }

        return true;
    }

    function numberHeading(heading, parameters) {
        var formatterName = parameters["number-format"];
        var formatter = formatters[formatterName];

        var currentLevel = heading.nodeName[1];
        var numbering = "";
        var moveCaret = false;

        if (currentLevel >= (startAt + 1)) {
            if (lastLevel > currentLevel) {
                clearCount(currentLevel);
            }

            counts[currentLevel - 1]++;

            if (formatterName === "custom") {
                var customFormats = [];
                for (var i = 1; i < 7; i++) {
                    var parameter = parameters["h" + i] || "";

                    customFormats[i - 1] = parameter;
                }

                numbering += formatter.format(currentLevel, customFormats);
            } else {
                for (var i = startAt; i < currentLevel; i++) {
                    if (shouldRenderNumbering(i + 1, parameters)) {
                        if (i + 1 > lastLevel && i + 1 < currentLevel) {
                            counts[i] = counts[i] + 1;
                        }

                        numbering += formatter.format(counts[i]);

                        if (!formatter.shouldRenderSeparator || formatter.shouldRenderSeparator(i + 1 >= currentLevel)) {
                            numbering += ".";
                        }
                    }
                }
            }

            if (numbering.length > 0) {
                numbering += " ";
            }

            lastLevel = currentLevel;
        }

        removeNumbering(heading);

        if( $(heading).html() === "<br>" ){
           moveCaret = true;
        }

        $(heading).prepend("<span class=\"data-nh-numbering\">"+numbering+"</span>");

        if(moveCaret && navigator.userAgent.toLowerCase().indexOf('firefox') > -1){
           alert( "move it!" );
           // Do Firefox-related activities
           try{
              var element = AJS.$( tinyMCE.activeEditor.selection.getNode() ).children("span.data-nh-numbering").first()[0];
              tinyMCE.activeEditor.selection.select( element );
              var elementRange = tinyMCE.activeEditor.selection.getRng();
              elementRange.collapse(false);
              tinyMCE.activeEditor.selection.setRng( elementRange );
           } catch( error ){}
        }
    }

    function removeNumbering(heading) {
        $(heading).find(".data-nh-numbering").first().remove();
        //$(heading).removeAttr("data-nh-numbering");
    }

    return {
        number: number
    };
})(AJS.$);

function usingTransitionalNumberedHeadingMacro()
{
   return $(".insert-numbering-into-storage-format").length > 0;
}

function numberEntirePage($){
   var editorIFrame = $("#wysiwygTextarea_ifr");
   var numberedHeadingsForPage =  $("img[data-macro-name=numbered-headings-for-page]", editorIFrame.contents() );
   return numberedHeadingsForPage.length > 0;
}

function updateMacroBody($)
{
   numberedHeadings.number( $(this).parents("table[data-macro-name=numberedheadings]") );
   numberedHeadings.number( $(this).parents("table[data-macro-name=numbered-headings]") );
}

AJS.toInit(function ($) {
    var editorIFrame = $("#wysiwygTextarea_ifr");

    if( numberEntirePage($) ){
        $("img[data-macro-name=numbered-headings-for-page]", editorIFrame.contents() ).first().blur( function($){
            numberedHeadings.number( $("#wysiwygTextarea_ifr").contents() );
        });
    } else {
        if( usingTransitionalNumberedHeadingMacro() ){
           $("table[data-macro-name=numberedheadings]", editorIFrame.contents() ).each().blur( function($){
               updateMacroBody($);
           });
        }
        $("table[data-macro-name=numbered-headings]", editorIFrame.contents() ).each().blur( function($){
            updateMacroBody($);
        });
    }

})(AJS.$ || jQuery);
