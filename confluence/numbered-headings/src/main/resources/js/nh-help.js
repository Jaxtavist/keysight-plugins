var numberedHeadingsHelp = (function( $ ){
        
   // module variables
   var methods     = new Object();
   var pluginId    = "numbered-headings";
   var restVersion = "1.0";

   methods[ 'showNumberedHeadingsHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "numbered-headings" );
   }
   methods[ 'showRenumberAndIncludePageHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "renumber-and-include-page" );
   }
   methods[ 'showRenumberAndIncludeChildrenHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "renumber-and-include-children" );
   }

   // return the object with the methods
   return methods;

// end closure
})( AJS.$ || jQuery );
