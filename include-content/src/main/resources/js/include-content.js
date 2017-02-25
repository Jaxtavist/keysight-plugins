AJS.bind("init.rte", function() {
    var baseUrl = AJS.Meta.get("context-path"),
        contentId = AJS.Meta.get("content-id"),
        endpoint = "plugins/servlet/confluence/include-page-macro/goto",
        openIncludedPage = function(macroNode) {
            var $macroNode = AJS.$(macroNode);
            var matches;
           
            var regex = /page\s*=\s*(.*)/;
            var parametersString = $macroNode.attr("data-macro-parameters");
            var parameters = parametersString.split( "|" );
            for( var i = 0; i < parameters.length; i++ ){
               if ( regex.test( parameters[i] ) ){
                  matches = parameters[i].match( regex );
                  targetPage = matches[1];     
                  break;
               }
            }

            var location = targetPage,
                url = baseUrl + "/" + endpoint + "?location=" + location + "&contentId=" + contentId,
                windowName = (AJS.$.browser && AJS.$.browser.msie) ? "_blank" : "confluence-goto-link-include-macro-" + macroNode.id,
                win = window.open(url, windowName);

            if (win) win.focus();
        };

    AJS.Confluence.PropertyPanel.Macro.registerButtonHandler("goto-target-page", function(e, macroNode) {
        openIncludedPage(macroNode);
    });
});

// The pattern below is a 'module' pattern based upon iife (immediately invoked function expressions) closures.
// see: http://benalman.com/news/2010/11/immediately-invoked-function-expression/ for a nice discussion of the pattern
// The value of this pattern is to help us keep our variables to ourselves.
var includeContentHelp = (function( $ ){
	
   // module variables
   var methods     = new Object();
   var pluginId    = "include-content";
   var restVersion = "1.0";

   // module methods
   methods[ 'showIncludePageWithReplacementHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "include-page-with-replacement" );
   }
   methods[ 'showIncludeSharedBlockWithReplacementHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "include-shared-block-with-replacement" );
   }
   methods[ 'showIncludeSharedBlockHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "include-shared-block" );
   }
   methods[ 'showSharedBlockHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "shared-block" );
   }
   methods[ 'showIncludeChildrenHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "include-children" );
   }
   methods[ 'showIncludeCodeHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "include-code" );
   }
   methods[ 'showHelpTextHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "help-text" );
   }
   methods[ 'showHelpTextFromPageHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "help-text-from-page" );
   }
   methods[ 'showHelpTextFromSharedBlockHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "help-text-from-shared-block" );
   }
   methods[ 'showFaqHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "faq" );
   }

   // return the object with the methods
   return methods;

// end closure
})( AJS.$ || jQuery );
