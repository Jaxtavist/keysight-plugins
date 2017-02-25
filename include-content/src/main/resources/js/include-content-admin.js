(function ($) { // this closure helps us keep our variables to ourselves.
    // This pattern is known as an "iife" - immediately invoked function expression

    // form the URL
    var url = AJS.contextPath() + "/rest/include-content-admin/1.0/admin";

    // wait for the DOM (i.e., document "skeleton") to load. This likely isn't necessary for the current case,
    // but may be helpful for AJAX that provides secondary content.
    //
    function addCredentialsBox(){
       AJS.$("#credentials-body").append( Keysight.Include.Content.Soy.Templates.credentials() );

       AJS.$(".code-include-remove").unbind( "click" );
       AJS.$(".code-include-remove").click(function(e) { 
          $(this).closest( ".credentials-container" ).remove();
       });
    }

    $(document).ready(function() {
       addCredentialsBox();

       AJS.$("#code-include-add-credentials").click(function(e) { 
          e.preventDefault();
          addCredentialsBox();
       });

       AJS.$("#include-content-admin").submit(function(e) { 
          e.preventDefault();

          var credentials = new Array();

          AJS.$(".credentials-container").each( function( index ) {
             credentials.push( '<credentials>'
                            +  '   <url>'      + AJS.$(this).find(".code-include-url").attr("value") + '</url>'
                            +  '   <user>'     + AJS.$(this).find(".code-include-user").attr("value") + '</user>'
                            +  '   <password>' + AJS.$(this).find(".code-include-password").attr("value") + '</password>'
                            +  '</credentials>' );
          });

          var xmlString = '<?xml version="1.0" encoding="UTF-8"?>' + "\n"
                        + '<credentialsSet>' + "\n"
                        + credentials.join( "\n" )  
                        + '</credentialsSet>' + "\n";

          AJS.$.ajax({
             url: url,
             type: "PUT",
             contentType: "application/json",
             data: '{"xml":"' + encodeURIComponent(xmlString) + '"}',
             processData: false
          }).done(function () { alert("Configuration updated"); 
          }).fail(function (self, status, error) { alert(error); 
          });
       });

       // request the config information from the server
       $.ajax({
           url: url,
           dataType: "json"
       }).done(function(credentialsSet) { // when the configuration is returned...
           // ...populate the form.
           $xml = AJS.$( AJS.$.parseXML( decodeURIComponent(credentialsSet.xml) ) );
           $xml.find( "credentials" ).each( function( index ) {
              if( index > 0 ){ addCredentialsBox(); }
              container = AJS.$(".credentials-container").last();
              container.find(".code-include-url").val( $(this).find( "url" ).html() );
              container.find(".code-include-user").val( $(this).find( "user" ).html() );
              container.find(".code-include-password").val( $(this).find( "password" ).html() );
           });
       }).fail(function(self,status,error){
          alert( error );
       });
   });

})(AJS.$ || jQuery);
