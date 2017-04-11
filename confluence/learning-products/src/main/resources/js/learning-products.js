// The pattern below is a 'module' pattern based upon iife (immediately invoked function expressions) closures.
// see: http://benalman.com/news/2010/11/immediately-invoked-function-expression/ for a nice discussion of the pattern
// The value of this pattern is to help us keep our variables to ourselves.
var learningProductsHelp = (function( $ ){
	
   // module variables
   var methods     = new Object();
   var pluginId    = "learning-products";
   var restVersion = "1.0";

   // module methods
   methods[ 'showLpBlockQuoteHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "lp-block-quote" );
   }
   methods[ 'showLpNoteBlockHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "lp-note-block" );
   }
   methods[ 'showLpImportantBlockHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "lp-important-block" );
   }
   methods[ 'showLpTipBlockHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "lp-tip-block" );
   }
   methods[ 'showLpCautionBlockHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "lp-caution-block" );
   }
   methods[ 'showLpWarningBlockHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "lp-warning-block" );
   }
   methods[ 'showLpCodeHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "lp-code" );
   }
   methods[ 'showLpInternalHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "lp-internal" );
   }
   methods[ 'showLpHideModularHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "lp-hide-modular" );
   }
   //

   // return the object with the methods
   return methods;

// end closure
})( AJS.$ || jQuery );

