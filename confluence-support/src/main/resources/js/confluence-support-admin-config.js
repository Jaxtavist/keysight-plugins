(function ($) {

    // form the URL
    var url = AJS.contextPath() + "/rest/confluence-support/1.0/admin-config/configuration";

    function addGroup(){

    }

    function addEmail(){

    }

    function saveConfig(){
       var groupconfig = new Array();
       var emailconfig = new Array();

       AJS.$(".saved-group").each( function( index ) {
          groupconfig.push( '<all-users-group>' + $(this).html(); + '</all-users-group>' );
       });

       AJS.$(".saved-email").each( function( index ) {
          emailconfig.push( '<on-space-creation-notification-email>' + $(this).html(); + '</on-space-creation-notification-email>' );
       });

       var xmlString = '<?xml version="1.0" encoding="UTF-8"?>' + "\n"
                     + '<plugin-configuration>' + "\n"
                     + '   <all-users-groups>' + "\n"
                     + groupconfig.join( "\n" )  
                     + '   </all-users-groups>' + "\n"
                     + '   <on-space-creation-notification-emails>' + "\n"
                     + emailconfig.join( "\n" )  
                     + '   </on-space-creation-notification-emails>' + "\n"
                     + '</plugin-configuration>' + "\n";

       AJS.$.ajax({
          url: url,
          type: "PUT",
          contentType: "application/json",
          data: '{"xml":"' + encodeURIComponent(xmlString) + '"}',
          processData: false
       }).done(function () { alert("Configuration updated"); 
       }).fail(function (self, status, error) { alert(error); 
       });
    }

    function loadConfig(){

       $.ajax({
           url: url,
           dataType: "json"
       }).done(function(pluginConfiguration) { 
           $xml = AJS.$( AJS.$.parseXML( decodeURIComponent(pluginConfiguration.xml) ) );

           $xml.find( "all-users-group" ).each( function( index ) {
              AJS.$(".groups").append( "<li>"
                                          + "<span class="saved-group">"+$(this).html()+"</span>" + "&nbsp;<a href=\"#\" onclick=\"removeemail();\">[remove]</a>"
                                       +"</li>\n" );
           });

           $xml.find( "on-space-creation-notification-email" ).each( function( index ) {
              AJS.$(".groups").append( "<li>"
                                          + $(this).html() + "&nbsp;<a href=\"#\" onclick=\"removeemail();\">[remove]</a>"
                                       +"</li>\n" );
           });

       }).fail(function(self,status,error){
          alert( error );
       });

    }

    AJS.toInit(function() {

       AJS.$("#add-group").click(function(e) { 
          e.preventDefault();
          addGroup();
       });

       AJS.$("#add-email").click(function(e) { 
          e.preventDefault();
          addEmail();
       });

       AJS.$("#confluence-support-admin-config").submit(function(e) { 
          e.preventDefault();

          saveConfig();
          loadConfig();
       });

   });


})(AJS.$ || jQuery);
