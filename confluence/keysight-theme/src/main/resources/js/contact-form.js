// The pattern below is a 'module' pattern based upon iife (immediately invoked function expressions) closures.
// see: http://benalman.com/news/2010/11/immediately-invoked-function-expression/ for a nice discussion of the pattern
// The value of this pattern is to help us keep our variables to ourselves.
var contactFormDialog = (function( $ ){

   var methods = new Object();
   var dialog  = null;
   var width   = 720;
   var height  = 540;
   var dialogId = 'contact-form-dialog';
   var idRequired = false;

   methods[ 'init' ] = function( template, identifier, contact, ccToEmail, defaultName, defaultEmail, dialogTitle, dialogHelp, identificationRequired, spaceKey){
      idRequired = identificationRequired;
      dialog = AJS.ConfluenceDialog({
               width : width,
               height: height,
               id: dialogId,
               onCancel: methods.close
      });
      dialog.addHeader( dialogTitle );
      dialog.addPanel("Contact Form Panel", KeysightTheme.Templates[template]( {identifier:identifier, contact:contact, ccToEmail:ccToEmail, defaultName:defaultName, defaultEmail:defaultEmail, spaceKey:spaceKey} ));
      dialog.addCancel("Close", methods.close);
      dialog.addHelpText(dialogHelp);
      $("#contact-form-form").submit(function(e) {
        e.preventDefault();
	methods.submit();
      });

      return;
   }

   methods[ 'open' ] = function(){
      dialog.show();
      return;
   }

   methods[ 'close' ] = function(){
      dialog.hide();
      dialog.remove();  // if the block is not removed, a repeated use will not work properly.
      return false;
   }

   methods[ 'submit' ] = function(){
      var url = AJS.Data.get( "base-url" ) + "/rest/keysight-theme/1.0/contact-form";
      url = url + "?" + $( "#contact-form-form" ).serialize();

      if( $("#contact-form-text").val() ){
	     if( idRequired && !( $("#contact-form-name").val() && $("#contact-form-email").val() ) ){
            alert( "Your name and email address must be provided." );
	     } else { 
            // disable the submit button to prevent multiple submissions if things are going slowly
            $( "#contact-form-submit" ).attr( "disabled", "true" );

            AJS.$.ajax({
               url: url,
	           type: "GET",
	           contentType: "application/x-www-form-urlencoded",
	           dataType: "json",
            }).done( function( data ) {
                var htmlResponse = data["message-body"];
                $("#contact-form-body").html( htmlResponse );
            });
	     }
      } else {
         alert( "No text to send!" );
      }
   }

   return methods;
})( AJS.$ || jQuery );

function contactFormLink( e, identifier, contact, ccToEmail, defaultName, defaultEmail, dialogTitle, dialogHelp ){
   e.preventDefault();
   e.stopPropagation();
   var spaceKey = AJS.Data.get("space-key");
   contactFormDialog.init( "contactForm", identifier, contact, ccToEmail, defaultName, defaultEmail, dialogTitle, dialogHelp, false, spaceKey );
   contactFormDialog.open();
}

function contactFormLinkIdentificationRequired( e, identifier, contact, ccToEmail, defaultName, defaultEmail, dialogTitle, dialogHelp ){
   e.preventDefault();
   e.stopPropagation();
   var spaceKey = AJS.Data.get("space-key");
   contactFormDialog.init( "contactFormIdentificationRequired", identifier, contact, ccToEmail, defaultName, defaultEmail, dialogTitle, dialogHelp, true, spaceKey );
   contactFormDialog.open();
}

function contactFormKeysightAsk( identifier, contact, ccToEmail, defaultName, defaultEmail, dialogTitle, dialogHelp ){
   var spaceKey = AJS.Data.get("space-key");
   contactFormDialog.init( "contactFormKeysightAsk", identifier, contact, ccToEmail, defaultName, defaultEmail, dialogTitle, dialogHelp, false, spaceKey );
   contactFormDialog.open();
}


keysightContactSpaceAdmins = (function($){
   var methods = new Object();

   methods['showContactForm'] = function(e){
      e.preventDefault();
      e.stopPropagation();
      var spaceKey = AJS.Data.get("space-key");
      var userName = "";
      var userEmail = "";

      if( AJS.Data.get("user-display-name" ) ){
         userName = AJS.Data.get("user-display-name") + " (" + AJS.Data.get("remote-user") + ")";
      }

      contactFormDialog.init( "contactForm", "Space Admin Contact", "", null, userName, userEmail, "Contact the Space Administrators", "", false, spaceKey );
      contactFormDialog.open();
      return;
   }
       
   return methods;
})(AJS.$ || jQuery)


AJS.toInit(function($){
   $(".keysight-contact-space-admins").on( "click", keysightContactSpaceAdmins.showContactForm );
});

