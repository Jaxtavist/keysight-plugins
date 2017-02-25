// This closure helps us keep our variables to ourselves.
// This pattern is known as "iife" - immediately invoked function expression

//alert( "Loaded keysight help text" );

// Start closure
keysightHelpText = (function( $ ){

   // module variables
   var methods = new Object();
   var initialAuiBlanketZIndex = "";
   var currentDivId            = "help-text-id";
   var pluginId                = "include-content";
   var restVersion             = "1.0";
   var isOpen                  = false;
   var spinning                = false;
   var getPageUrl              = AJS.Data.get( "base-url" ) + "/rest/api/content/";
   var getSharedBlockUrl       = AJS.Data.get( "base-url" ) + "/rest/"+pluginId+"/"+restVersion+"/get-shared-block";

   methods[ 'showAsDialogBox' ] = function( e, sectionId ){
      preventIt( e );

      if( !isOpen )
      {
          // set the div id;
          currentDivId = sectionId;

          // show the help dialog
          AJS.dialog2( "#"+currentDivId ).show();

          isOpen = true;
      }
   }

   methods[ 'showInlineDialog' ] = function( e, sectionId ){
      preventIt( e );

      $(e.target).attr( 'data-aui-trigger', '' );
      $(e.target).attr( 'aria-controls', sectionId );
      
      var inlineTarget = $( "#"+sectionId );
      inlineTarget.attr('open', '');
   }
        
   methods[ 'showPageAsDialogBox' ] = function( e, sectionId, pageId, title, tip ){
      preventIt( e );

      if( !isOpen )
      {
          // set the div id;
          currentDivId = sectionId;

          if( $( "#"+currentDivId ).length == 0 ){
             // create div element
             $( "body" ).append( Keysight.Include.Content.Help.Text.Soy.Templates.dialog2section( {sectionId:currentDivId, title:title, tip:tip} ) );
             
             // start the spinner
             spinnerOn();

             // request the page from the server
             $.ajax({
                url: getPageUrl + pageId,
                type: "GET",
                dataType: "json",
                data: { "expand":"body.view" },
             }).done(function(data) { // when the configuration is returned...
                // stop the spinner
                spinnerOff();

                // populate the div body
                AJS.$("#"+currentDivId ).find(".aui-dialog2-content").html( data["body"]["view"]["value"] );
             }).fail(function(self,status,error){
                alert( error );
             });
          }

          // show the help dialog
          AJS.dialog2( "#"+currentDivId ).show();
          isOpen = true;
      } else {
          alert( "Already open" );
      }
   }

   methods[ 'showPageAsInlineDialog' ] = function( e, sectionId, pageId, title, tip ){
      preventIt( e );
      var inlineTarget;
           
      // set the div id;
      currentDivId = sectionId;

      if( $( "#"+currentDivId ).length == 0 ){
         // create div element
         $( "body" ).append( Keysight.Include.Content.Help.Text.Soy.Templates.inlineDialog( {sectionId:currentDivId, title:title, tip:tip} ) );
      
         $(e.target).attr( 'data-aui-trigger', '' );
         $(e.target).attr( 'aria-controls', sectionId );

         inlineTarget = $( "#"+currentDivId );
         inlineTarget.attr('open', '');
             
         // start the spinner
         spinnerOn();

         // request the page from the server
         $.ajax({
            url: getPageUrl + pageId,
            type: "GET",
            dataType: "json",
            data: { "expand":"body.view" },
         }).done(function(data) { // when the configuration is returned...
            // stop the spinner
            spinnerOff();

            // populate the div body
            AJS.$("#"+currentDivId ).find(".aui-inline-dialog-contents").html( data["body"]["view"]["value"] );
         }).fail(function(self,status,error){
            alert( error );
         });
      } else {
         var inlineTarget = $( "#"+currentDivId );
         inlineTarget.attr('open', '');
      }
   }

   methods[ 'showSharedBlockAsDialogBox' ] = function( e, sectionId, pageId, sharedBlockId, title, tip ){
      preventIt( e );

      if( !isOpen )
      {
          // set the div id;
          currentDivId = sectionId;

          if( $( "#"+currentDivId ).length == 0 ){
             // create div element
             $( "body" ).append( Keysight.Include.Content.Help.Text.Soy.Templates.dialog2section( {sectionId:currentDivId, title:title, tip:tip} ) );
             
             // start the spinner
             spinnerOn();

             // request the page from the server
             $.ajax({
                url: getSharedBlockUrl,
                type: "GET",
                dataType: "json",
                data: { "page-id":pageId, "shared-block-key":sharedBlockId },
             }).done(function(data) { // when the configuration is returned...
                // stop the spinner
                spinnerOff();

                // populate the div body
                AJS.$("#"+currentDivId ).find(".aui-dialog2-content").html( data["message-body"] );
             }).fail(function(self,status,error){
                alert( error );
             });
          }

          // show the help dialog
          AJS.dialog2( "#"+currentDivId ).show();
          isOpen = true;
      } else {
          alert( "Already open" );
      }
   }
        
   methods[ 'showSharedBlockAsInlineDialog' ] = function( e, sectionId, pageId, sharedBlockId, title, tip ){
      preventIt( e );
      var inlineTarget;
           
      // set the div id;
      currentDivId = sectionId;

      if( $( "#"+currentDivId ).length == 0 ){
         // create div element
         $( "body" ).append( Keysight.Include.Content.Help.Text.Soy.Templates.inlineDialog( {sectionId:currentDivId, title:title, tip:tip} ) );
      
         $(e.target).attr( 'data-aui-trigger', '' );
         $(e.target).attr( 'aria-controls', sectionId );

         inlineTarget = $( "#"+currentDivId );
         inlineTarget.attr('open', '');
             
         // start the spinner
         spinnerOn();

         // request the page from the server
         $.ajax({
            url: getSharedBlockUrl,
            type: "GET",
            dataType: "json",
            data: { "page-id":pageId, "shared-block-key":sharedBlockId },
         }).done(function(data) { // when the configuration is returned...
            // stop the spinner
            spinnerOff();

            // populate the div body
            AJS.$("#"+currentDivId ).find(".aui-inline-dialog-contents").html( data["message-body"] );
         }).fail(function(self,status,error){
            alert( error );
         });
      } else {
         var inlineTarget = $( "#"+currentDivId );
         inlineTarget.attr('open', '');
      }
   }

   methods[ 'close' ] = function(){
      if( isOpen )
      {
          AJS.dialog2("#"+currentDivId).hide();
          isOpen = false;
      }
   }

   function preventIt( e ){
      // Prevent any attached event handlers from executing as that can result
      // in opening unwanted windows or closing the macro browser
      e.preventDefault();
      e.stopPropagation();
      $( e.target ).unbind();
   }

   function toggleSpinner(){
       if (!spinning) {
          spinnerOn();
       } else {
          spinnerOff();
       }
   }

   function spinnerOn(){
       AJS.$('#keysight-help-text-progress-spinner').spin();
       spinning = true;
   }

   function spinnerOff(){
       AJS.$('#keysight-help-text-progress-spinner').spinStop();
       spinning = false;
   }

   // return the object with the methods
   return methods;

// End closure
})( AJS.$ || jQuery );
