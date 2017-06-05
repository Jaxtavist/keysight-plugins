AJS.toInit(function() { 
   AJS.MacroBrowser.activateStringFields( 'mathjax-inline-macro' );
})

// The pattern below is a 'module' pattern based upon iife (immediately invoked function expressions) closures.
// see: http://benalman.com/news/2010/11/immediately-invoked-function-expression/ for a nice discussion of the pattern
// The value of this pattern is to help us keep our variables to ourselves.
var mathjaxHelp = (function( $ ){
	
   // module variables
   var methods     = new Object();
   var pluginId    = "mathjax";
   var restVersion = "1.0";

   // module methods
   methods[ 'showMathJaxInlineHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "mathjax-inline" );
   }
   methods[ 'showMathJaxBlockHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "mathjax-block" );
   }

   // return the object with the methods
   return methods;

// end closure
})( AJS.$ || jQuery );

(function ($) { // this closure helps us keep our variables to ourselves.
// This pattern is known as an "iife" - immediately invoked function expression
    // form the URL
    var pluginUrl = AJS.contextPath() + "/rest/mathjax/1.0/admin/";

    // Lookup table of known CDNs
    // We store the URL in two parts, one to the left of the version, one to the right
    // This is so we can do: downloadUrl_a + $url + downloadUrl_b to get a final url
    // The version key is just the field in the JSON that has the value for version.
    // Version fetch is the api url that will give us what versions of the javascript are available.
    var cdnList = {
        'cloudflare' : {
            downloadUrl_a: "https://cdnjs.cloudflare.com/ajax/libs/mathjax/",
            downloadUrl_b: "/MathJax.js?config=TeX-MML-AM_CHTML",
            versionFetch: "https://api.cdnjs.com/libraries/mathjax",
            versionKey: "version"
        },
        'rawgit' : {
            downloadUrl_a: "https://cdn.rawgit.com/mathjax/MathJax/",
            downloadUrl_b: "/MathJax.js",
            versionFetch: "https://api.github.com/repos/mathjax/MathJax/releases",
            versionKey: "tag_name"
        },
        'jsdelivr' : {
              downloadUrl_a: "https://cdn.jsdelivr.net/mathjax/",
              downloadUrl_b: "/MathJax.js",
              versionFetch: "https://api.jsdelivr.com/v1/jsdelivr/libraries?name=mathjax",
              versionKey: "versions"
         }
    };

    // Gets the url currently stored in Confluence's REST API, within the route for configuring MathJax
    var getActiveUrl = function() {
        // request the config information from the server
        AJS.$.ajax({
            url: pluginUrl,
            dataType: "json"
        }).done(function(config) { // when the configuration is returned...
            // ...populate the form.u
            AJS.$("#url").val(config.url);
            AJS.$("#url-info").text(config.url);
        });
    }

    // wait for the DOM (i.e., document "skeleton") to load. Needs to happen for listeners to attach
    AJS.$(document).ready(function() {
        var cdnMeta = {};
        getActiveUrl();

        // If the URL gen is submitted we populate the URL field with the resulting URL
        AJS.$("#urlGen").submit(function(e) {
            e.preventDefault();

            // Weird URL construction, but it works
            AJS.$("#url").val(cdnMeta.downloadUrl_a + AJS.$("#select-version option:selected").val() + cdnMeta.downloadUrl_b);
        });

        AJS.$("#admin").submit(function(e) {
           e.preventDefault();
           updateConfig();
       });

        // Use the selected CDN to fetch version numbers from that CDN's api
        var fetchVersion = function() {
            var selectedCDN = AJS.$("#select-cdn option:selected").val();
            cdnMeta = cdnList[selectedCDN];
            AJS.$.ajax({
                url: cdnMeta.versionFetch,
                dataType: "json"
            })
            .done(function(res) {
                var versions = [];
                if (selectedCDN == 'jsdelivr') {
                    versions = res[0][cdnMeta.versionKey];
                } else if (selectedCDN == 'rawgit') {
                    versions = res.map(function(release){
                        return release[cdnMeta.versionKey]
                    })
                } else {
                    versions = res.assets.map(function(release) {
                        return release[cdnMeta.versionKey]
                    })
                }

                console.log(versions);
                var versionSelect = AJS.$('#select-version');
                // Clear all options then repopulate
                versionSelect.html("");
                versions.forEach(function(version) {
                    versionSelect.append($("<option />").val(version).text(version));
                })
            })
        };fetchVersion();

        var apiSelect = AJS.$("#select-cdn");
        apiSelect.change(fetchVersion);
    });

    // Goes to Confluence REST API and updates the stored configuration
    // Also edits the message on the page to indicate whether the operation succeeded or not
    function updateConfig() {
        AJS.$.ajax({
        url: pluginUrl,
        type: "PUT",
        contentType: "application/json",
        data: '{ "url": "' + AJS.$("#url").attr("value") + '"' + ' }',
        processData: false
        })
        .done(function() {
            getActiveUrl();
            AJS.$("#message").text("Update Succeeded");
        })
        .fail(function(res) {
            console.error(res);
            AJS.$("#message").text("Update Failed");
        })
    }
})(AJS.$ || jQuery);
