/*
(function ($) {
    tinymce.create("tinymce.plugins.NumberedHeadingsPlugin", {
        init: function (ed, url) {
            ed.onNodeChange.add(function (ed, cm, e) {
                if (e.nodeType === 1 && e.tagName.length === 2 && e.tagName[0] === "H") {

                    if( numberEntirePage($) ){
                        numberedHeadings.number( $("#wysiwygTextarea_ifr").contents() );
                    } else {
                        numberedHeadings.number( $(e).parents("table[data-macro-name=numberedheadings]") );
                        numberedHeadings.number( $(e).parents("table[data-macro-name=numbered-headings]") );
                    }
                }
            });

            ed.onInit.add(function (ed) {
              if( numberEntirePage($) ){
                  $("#wysiwygTextarea_ifr").load(function() {
                      numberedHeadings.number( $("#wysiwygTextarea_ifr").contents() );
                  });
              } else {
                  $("#wysiwygTextarea_ifr").load(function() {
                      $("#wysiwygTextarea_ifr").contents().find("table[data-macro-name=numberedheadings]").each(function (index, macroTable) {
                          numberedHeadings.number(macroTable);
                      });
                      $("#wysiwygTextarea_ifr").contents().find("table[data-macro-name=numbered-headings]").each(function (index, macroTable) {
                          numberedHeadings.number(macroTable);
                      });
                  });
              }
            });

            $(window).bind("updated.macro", function (event, macroTable) {
                if( numberEntirePage($) ){
                    numberedHeadings.number( $("#wysiwygTextarea_ifr").contents() );
                } else {
                    numberedHeadings.number(macroTable);
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
})(AJS.$);

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
        $("table[data-macro-name=numberedheadings]", editorIFrame.contents() ).each().blur( function($){
            updateMacroBody($);
        });
        $("table[data-macro-name=numbered-headings]", editorIFrame.contents() ).each().blur( function($){
            updateMacroBody($);
        });
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
        if( numberEntirePage($) ){
            var editorIFrame = $("#wysiwygTextarea_ifr");
            parameterString = $("img[data-macro-name=numbered-headings-for-page]", editorIFrame.contents() ).first().attr("data-macro-parameters");
        } else {
            parameterString = $(macroTable).attr("data-macro-parameters");
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

    function number(macroTable) {
        var parameters = parseParameters(macroTable);

        $(macroTable).find("h1, h2, h3, h4, h5, h6").each(function (index, element) {
            if (shouldRenderNumbering(element.nodeName[1], parameters)) {
                numberHeading(element, parameters);
            } else {
                removeNumbering(element);
            }
        });
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
        $(heading).prepend("<span class=\"data-nh-numbering\">"+numbering+"</span>");
        //$(heading).attr("data-nh-numbering", numbering);
    }

    function removeNumbering(heading) {
        $(heading).find(".data-nh-numbering").first().remove();
        //$(heading).removeAttr("data-nh-numbering");
    }

    return {
        number: number
    };
})(AJS.$);

function numberEntirePage($){
    var editorIFrame = $("#wysiwygTextarea_ifr");
    var numberedHeadingsForPage =  $("img[data-macro-name=numbered-headings-for-page]", editorIFrame.contents() );
    if( numberedHeadingsForPage.length > 0 ){
        return true;
    } else {
        return false;
    }
}
*/
