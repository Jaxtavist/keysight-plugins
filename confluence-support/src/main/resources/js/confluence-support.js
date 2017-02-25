AJS.toInit(function() { 
   AJS.MacroBrowser.activateAdditionalMacroBrowserFieldTypes( 'additional-macro-parameter-types' );
});

// The pattern below is a 'module' pattern based upon iife (immediately invoked function expressions) closures.
// see: http://benalman.com/news/2010/11/immediately-invoked-function-expression/ for a nice discussion of the pattern
// The value of this pattern is to help us keep our variables to ourselves.
var confluenceSupportHelp = (function( $ ){
	
   // module variables
   var methods     = new Object();
   var pluginId    = "confluence-support";
   var restVersion = "1.0";

   // module methods
   methods[ 'showAdditionalMacroParameterTypesHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "additional-macro-parameter-types" );
   }
   methods[ 'showEntityAuthorshipHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "entity-authorship" );
   }

   // return the object with the methods
   return methods;

// end closure
})( AJS.$ || jQuery );
