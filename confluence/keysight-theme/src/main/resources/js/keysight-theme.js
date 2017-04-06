AJS.toInit(function(){

   // Atlassian has the ability to insert some css when viewing,
   // and not when editing; however, the plugin css is always preset
   // this is adding a class to the body tag so that I can override
   // the confluence view css styles without adding the overrides
   // to the edit mode css.
   if( AJS.params.browsePageTreeMode == "view" ){
      AJS.$("body").addClass( "keysight-view-page-mode" );
   }

   if( AJS.$("#page-state-button").length ){
      keysightPageStateManager.setPageStateLabel();
   }

   AJS.$("#page-state-note").click( function(e){
      e.preventDefault();
      keysightPageStateManager.setPageStateLabel( "note" );
   });
   AJS.$("#page-state-draft").click( function(e){
      e.preventDefault();
      keysightPageStateManager.setPageStateLabel( "draft" );
   });
   AJS.$("#page-state-reviewed").click( function(e){
      e.preventDefault();
      keysightPageStateManager.setPageStateLabel( "reviewed" );
   });
   AJS.$("#page-state-finalized").click( function(e){
      e.preventDefault();
      keysightPageStateManager.setPageStateLabel( "finalized" );
   });
   AJS.$("#page-state-has-errors").click( function(e){
      e.preventDefault();
      keysightPageStateManager.setPageStateLabel( "has-errors" );
   });
   AJS.$("#page-state-incomplete").click( function(e){
      e.preventDefault();
      keysightPageStateManager.setPageStateLabel( "incomplete" );
   });
   AJS.$("#page-state-deprecated").click( function(e){
      e.preventDefault();
      keysightPageStateManager.setPageStateLabel( "deprecated" );
   });

});


// The pattern below is a 'module' pattern based upon iife (immediately invoked function expressions) closures.
// see: http://benalman.com/news/2010/11/immediately-invoked-function-expression/ for a nice discussion of the pattern
// The value of this pattern is to help us keep our variables to ourselves.
var keysightThemeHelp = (function( $ ){
        
   // module variables
   var methods     = new Object();
   var pluginId    = "keysight-theme";
   var restVersion = "1.0";

   methods[ 'showNavigationBoxHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "navigation-box" );
   }
   methods[ 'showPanelBoxHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "panel-box" );
   }
   methods[ 'showContactFormHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "contact-form" );
   }
   methods[ 'showStatusFlagHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "status-flag" );
   }
   methods[ 'showKeysightPlcCheckpointHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "keysight-plc-checkpoint" );
   }
   methods[ 'showApiDocumentationHelp' ] = function( e ){
      macroHelpDocumentation.getMacroHelp( e, pluginId, restVersion, "api-documentation" );
   }


   // return the object with the methods
   return methods;

// end closure
})( AJS.$ || jQuery );


// The pattern below is a 'module' pattern based upon iife (immediately invoked function expressions) closures.
// see: http://benalman.com/news/2010/11/immediately-invoked-function-expression/ for a nice discussion of the pattern
// The value of this pattern is to help us keep our variables to ourselves.
var keysightPageStateManager = (function( $ ){
   // The addLabel and removeLabel are asyncronous calls; but I want them to be 
   // syncronous to prevent race conditions.  So, I'm going to queue up the work
   // to manage it myself.

   var methods = new Object();
   var states = ["note", "draft", "reviewed", "finalized", "has-errors", "incomplete", "deprecated"];
   var defaultStateIndex = 1;
   var niceNames = {};
   var currentLabel;

   niceNames = { "note":       "Note",
                 "draft":      "Draft",
                 "reviewed":   "Reviewed",
                 "finalized":  "Finalized",
                 "has-errors": "Has Errors",
                 "incomplete": "Incomplete",
                 "deprecated": "Deprecated" };

   methods[ 'setPageStateLabel' ] = function( labelToSet ){
      var labels = $(".aui-label-split-main");
      var labelIsSet = false;
      var actions = new Array();
      var i;
      var j;

      // See if there is a current page state label
      // If there are two, use the first one.
      if( !labelToSet ){ 
         outerLoop:
         for( i = 0; i < states.length; i++ ){
            for( j = 0; j < labels.length; j++ ){
               if( states[i] == $(labels[j]).html() ){
                  labelToSet = states[ i ];
		  break outerLoop;
	       }
	    }
	 }
      }

      if( !labelToSet ){ 
         labelToSet = states[ defaultStateIndex ];
      }

      //alert( "set " + labelToSet );

      // This only works if the page-state-button is present
      if( $( "#page-state-button" ) ){

         // scan the state labels against the page labels
         // to see if the label is already set, or which 
         // labels need to be removed
         for( i = 0; i < states.length; i++ ){
            for( j = 0; j < labels.length; j++ ){
               if( states[i] == $(labels[j]).html() ){
                  if( labelToSet == states[i] ){
                     labelIsSet = true;
                     actions.push( { action:"sync", label:states[i] } );
                  } else {
                     actions.push( { action:"remove", label:states[i], li:$(labels[j]).closest("li") } );
                  }
               } 
            }
         }

         // add the target label if it was not in the pre-existing list of labels
         if( !labelIsSet ){
            actions.push( { action:"add", label:labelToSet } );
         }
         
         methods.applyActions( actions );
      }
      return;
   };

   methods[ 'applyActions' ] = function( actions ){
      var currentAction;
      //alert( "Action Count: " + actions.length )

      if( actions.length > 0 ){
         currentAction = actions.shift();
         if( currentAction.action == "add" ){
            //alert( "Add " + currentAction.label );
            AJS.Labels.addLabel( currentAction.label, AJS.params.pageId, AJS.params.contentType)
               .done( function(){ 
                  //alert( "Done adding " + currentAction.label );
                  methods.setButtonStyle( currentAction );
                  methods.applyActions( actions );
               });
         } else if( currentAction.action == "remove" ) {
            //alert( "Remove " + currentAction.label );
            AJS.Labels.removeLabel( currentAction.li, AJS.params.pageId, AJS.params.contentType)
               .done( function(){ 
                  //alert( "Done removing " + currentAction.label );
                  methods.applyActions( actions );
               });
         } else if( currentAction.action == "sync" ) {
            //alert( "Sync " + currentAction.label );
            methods.setButtonStyle( currentAction );
            //alert( "Done adding " + currentAction.label );
            methods.applyActions( actions );
         }

      } else {
         //alert( "All done!" );
      }

      return;
   };

   methods[ 'setButtonStyle' ] = function( currentAction ){
      $("#page-state-button").html( niceNames[ currentAction.label ] );
      $("#keysight-title-page-state-content-box").removeClass().addClass("page-state-box page-state-"+currentAction.label);
      return;
   }

   return methods;
// end closure
})( AJS.$ || jQuery );

function toggleSubListVisibility(e){
   e.preventDefault();
   e.stopPropagation();
   $(e.currentTarget).next().slideToggle(300);

   if( $(e.currentTarget).parent().hasClass( "keysight-api-documentation-right" ) ){
      $(e.currentTarget).parent().removeClass( "keysight-api-documentation-right" );
      $(e.currentTarget).parent().addClass( "keysight-api-documentation-down" );
   } else {
      $(e.currentTarget).parent().removeClass( "keysight-api-documentation-down" );
      $(e.currentTarget).parent().addClass( "keysight-api-documentation-right" );
   }
}
