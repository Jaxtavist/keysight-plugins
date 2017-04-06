// The pattern below is a 'module' pattern based upon iife (immediately invoked function expressions) closures.
// see: http://benalman.com/news/2010/11/immediately-invoked-function-expression/ for a nice discussion of the pattern
// The value of this pattern is to help us keep our variables to ourselves.
var guidanceHelp = (function( $ ){
	
   // module variables
   var methods     = new Object();
   var pluginId    = "guidance";
   var restVersion = "1.0";

   // module methods
   methods[ 'showDoHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "do" );
   }
   methods[ 'showConsiderHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "consider" );
   }
   methods[ 'showAvoidHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "avoid" );
   }
   methods[ 'showDoNotHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "do-not" );
   }
   methods[ 'showLpGuidelineHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "lp-guideline" );
   }
   //

   // return the object with the methods
   return methods;

// end closure
})( AJS.$ || jQuery );

AJS.toInit(function($){
   var element = $(".auto-guidance-detection-failed");
   if( element.length > 0 ){
      alert( "There are instances of the guidance macro on this page that\n"
            +"have been set to automatically determine the guidance term\n"
            +"based on the first word(s) in the body; however, the\n"
            +"body did not start with Do, Consider, Avoid or Do Not.\n" );  

   }
});
