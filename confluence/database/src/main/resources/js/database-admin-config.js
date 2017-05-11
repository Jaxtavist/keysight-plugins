databaseConfigHelper = (function ($) {

    var methods = new Object();
    var url = AJS.contextPath() + "/rest/database/1.0/admin-config/configuration";

    methods['save'] = function(){
       saveConfig();
    }

    methods['addGroup'] = function(){
       if( AJS.$("#group-list").length == 0 ){
           AJS.$("#group-list-container").html( "<ul id=\"group-list\"></ul>" );
       }

       appendGroup( AJS.$("#new-group-name").val() );
       AJS.$("#new-group-name").val("");

       saveConfig();
    }

    methods['addConnectionProfile'] = function(e){
       AJS.$("#connection-profiles").append( Keysight.Database.Admin.Config.Templates.connectionProfile( { id:"hello"} ) );

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

           if( pluginConfiguration.xml ){
              $xml = AJS.$( AJS.$.parseXML( decodeURIComponent(pluginConfiguration.xml) ) );
              $("#jdbc-driver-directory").val( $xml.find( "jdbc-driver-directory" ).html() );
           }

           //AJS.$("#group-list-container").children().first().remove();

           /*
           var groups = $xml.find( allUsersGroup );
           if( groups.length > 0 ){
              AJS.$("#group-list-container").html( "<ul id=\"group-list\"></ul>" );
              $xml.find( allUsersGroup ).each( function( index ) {
                 appendGroup( $(this).html() );
              });
           } else {
              AJS.$("#group-list-container").html( "<div class=\"none-saved\">No saved groups</div>" );
           }
           */

       }).fail(function(self,status,error){
          alert( error );
       });

    }

    function saveConfig(){
       var groupconfig = new Array();

       AJS.$(".saved-group").each( function( index ) {
          groupconfig.push( '<all-users-group>' + $(this).html() + '</all-users-group>' );
       });

                     //+ '   <all-users-groups>' + "\n"
                     //+ groupconfig.join( "\n" )  
                     //+ '   </all-users-groups>' + "\n"
       var xmlString = '<?xml version="1.0" encoding="UTF-8"?>' + "\n"
                     + '<plugin-configuration>' + "\n"
                     + '   <jdbc-driver-directory>' + $("#jdbc-driver-directory").val() + "</jdbc-driver-directory>\n"
                     + '</plugin-configuration>' + "\n";

       AJS.$.ajax({
          url: url,
          type: "PUT",
          contentType: "application/json",
          data: '{"xml":"' + encodeURIComponent(xmlString) + '"}',
          processData: false
       }).done(function () { 
          alert( "Config Saved" );
       }).fail(function (self, status, error) { 
          alert(error); 
       });
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

   AJS.$("#save-jdbc-driver-directory").click(function(e) { 
      e.preventDefault();
      databaseConfigHelper.save();
   });

   AJS.$("#add-connection-profile").click(function(e) { 
      e.preventDefault();
      databaseConfigHelper.addConnectionProfile();
   });

   databaseConfigHelper.loadConfig();
});
