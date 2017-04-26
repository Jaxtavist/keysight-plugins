// The pattern below is a 'module' pattern based upon iife (immediately invoked function expressions) closures.
// see: http://benalman.com/news/2010/11/immediately-invoked-function-expression/ for a nice discussion of the pattern
// The value of this pattern is to help us keep our variables to ourselves.
var htmlElementsHelp = (function( $ ){
	
   // module variables
   var methods     = new Object();
   var pluginId    = "html-elements";
   var restVersion = "1.0";

   // module methods
   methods[ 'showHtml5MultimediaHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "html5-multimedia" );
   }
   methods[ 'showMultimediaHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "multimedia" );
   }
   methods[ 'showCompactFormHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "compact-form" );
   }
   methods[ 'showAuiTabHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "aui-tab" );
   }
   methods[ 'showAuiTabGroupHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "aui-tab-group" );
   }
   methods[ 'showAuiChildTabsHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "aui-child-tabs" );
   }
   methods[ 'showAuiPagesTabsHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "aui-pages-tabs" );
   }
   methods[ 'showAuiPagesByLabelTabsHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "aui-pages-by-label-tabs" );
   }
   methods[ 'showAuiButtonHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "aui-buttons" );
   }
   methods[ 'showExtraTablePropertiesHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "extra-table-properties" );
   }
   methods[ 'showAuthorsOnlyBlockHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "authors-only-block" );
   }
   methods[ 'showLayoutWidthHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "layout-width" );
   }
   methods[ 'showBlockHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "block" );
   }
   methods[ 'showLinkToWindowHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "link-to-window" );
   }

   // return the object with the methods
   return methods;

// end closure
})( AJS.$ || jQuery );
