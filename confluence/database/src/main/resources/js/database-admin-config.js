DatabaseConfigHelper = (function ($) {

    var methods = new Object();
    var url = AJS.contextPath() + "/rest/database/1.0/admin-config/configuration";
    var allUsersGroup = "all-users-group";
    var onSpaceCreationNotificationEmailAddress = "on-space-creation-notification-email-address";

    methods['addGroup'] = function(){
       if( AJS.$("#group-list").length == 0 ){
           AJS.$("#group-list-container").html( "<ul id=\"group-list\"></ul>" );
       }

       appendGroup( AJS.$("#new-group-name").val() );
       AJS.$("#new-group-name").val("");

       saveConfig();
    }

    methods['addEmail'] = function(){
       if( AJS.$("#email-list").length == 0 ){
           AJS.$("#email-list-container").html( "<ul id=\"email-list\"></ul>" );
       }

       appendEmail( AJS.$("#new-email-address").val() );
       AJS.$("#new-email-address").val("");

       saveConfig();
    }

    methods['removeEntry'] = function(e){
       e.preventDefault();
       $(e.currentTarget).parent().remove(); 

       if( AJS.$(".saved-group").length == 0 ){
           AJS.$("#group-list-container").html( "<div class=\"none-saved\">No saved groups</div>" );
       }

       if( AJS.$(".saved-address").length == 0 ){
              AJS.$("#email-list-container").html( "<div class=\"none-saved\">No saved addresses.</div>" );
       }

       saveConfig();
    }

    methods['loadConfig'] = function(){

       $.ajax({
           url: url,
           dataType: "json"
       }).done(function(pluginConfiguration) { 

           $xml = AJS.$( AJS.$.parseXML( decodeURIComponent(pluginConfiguration.xml) ) );

           AJS.$("#group-list-container").children().first().remove();
           AJS.$("#email-list-container").children().first().remove();


           var groups = $xml.find( allUsersGroup );
           if( groups.length > 0 ){
              AJS.$("#group-list-container").html( "<ul id=\"group-list\"></ul>" );
              $xml.find( allUsersGroup ).each( function( index ) {
                 appendGroup( $(this).html() );
              });
           } else {
              AJS.$("#group-list-container").html( "<div class=\"none-saved\">No saved groups</div>" );
           }

           var addresses = $xml.find( onSpaceCreationNotificationEmailAddress );
           if( addresses.length > 0 ){
              AJS.$("#email-list-container").html( "<ul id=\"email-list\"></ul>" );
              $xml.find( onSpaceCreationNotificationEmailAddress ).each( function( index ) {
                 appendEmail( $(this).html() );
              });
           } else {
              AJS.$("#email-list-container").html( "<div class=\"none-saved\">No saved addresses.</div>" );
           }

       }).fail(function(self,status,error){
          alert( error );
       });

    }

    function saveConfig(){
       var groupconfig = new Array();
       var emailconfig = new Array();

       AJS.$(".saved-group").each( function( index ) {
          groupconfig.push( '<all-users-group>' + $(this).html() + '</all-users-group>' );
       });

       AJS.$(".saved-address").each( function( index ) {
          emailconfig.push( '<on-space-creation-notification-email-address>' + $(this).html() + '</on-space-creation-notification-email-address>' );
       });

       var xmlString = '<?xml version="1.0" encoding="UTF-8"?>' + "\n"
                     + '<plugin-configuration>' + "\n"
                     + '   <all-users-groups>' + "\n"
                     + groupconfig.join( "\n" )  
                     + '   </all-users-groups>' + "\n"
                     + '   <on-space-creation-notification-email-addresses>' + "\n"
                     + emailconfig.join( "\n" )  
                     + '   </on-space-creation-notification-email-addresses>' + "\n"
                     + '</plugin-configuration>' + "\n";

       AJS.$.ajax({
          url: url,
          type: "PUT",
          contentType: "application/json",
          data: '{"xml":"' + encodeURIComponent(xmlString) + '"}',
          processData: false
       }).done(function () { 
       }).fail(function (self, status, error) { alert(error); 
       });
    }

    function appendEmail( emailAddress )
    {
       AJS.$("#email-list").append( "<li>"
                                  + "<span class=\"saved-address\">"+emailAddress+"</span>" + "&nbsp;<a href=\"#\" onclick=\"confluenceSupportConfigHelper.removeEntry(event)\">[remove]</a>"
                                  +"</li>\n" );
    }

    function appendGroup( groupName )
    {
       AJS.$("#group-list").append( "<li>"
                                  + "<span class=\"saved-group\">"+groupName+"</span>" + "&nbsp;<a href=\"#\" onclick=\"confluenceSupportConfigHelper.removeEntry(event)\">[remove]</a>"
                                  +"</li>\n" );
    }

    return methods;
})(AJS.$ || jQuery);

AJS.toInit(function() {

   AJS.$("#add-group").click(function(e) { 
      e.preventDefault();
      //confluenceSupportConfigHelper.addGroup();
   });

   AJS.$("#add-email").click(function(e) { 
      e.preventDefault();
      //confluenceSupportConfigHelper.addEmail();
   });

   //confluenceSupportConfigHelper.loadConfig();
});
