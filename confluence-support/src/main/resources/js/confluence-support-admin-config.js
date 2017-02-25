(function ($) {

    // form the URL
    var url = AJS.contextPath() + "/rest/confluence-support/1.0/admin-config/configuration";

    function addConfigBox(){
       AJS.$("#config-body").append( Keysight.Confluence.Support.Admin.Config.Templates.config() );

       AJS.$(".all-users-group-remove").unbind( "click" );
       AJS.$(".all-users-group-remove").click(function(e) { 
          $(this).closest( ".config-container" ).remove();
       });
    }

    AJS.toInit(function() {
       addConfigBox();

       AJS.$("#confluence-support-add-all-users-group").click(function(e) { 
          e.preventDefault();
          addConfigBox();
       });

       AJS.$("#confluence-support-admin-config").submit(function(e) { 
          e.preventDefault();

          var config = new Array();

          AJS.$(".config-container").each( function( index ) {
             config.push( '<all-users-group>' 
                          + AJS.$(this).find(".confluence-support-all-users-group").attr("value") 
                          + '</all-users-group>'
                        );
          });

          var xmlString = '<?xml version="1.0" encoding="UTF-8"?>' + "\n"
                        + '<plugin-configuration>' + "\n"
                        + '   <all-users-groups>' + "\n"
                        + config.join( "\n" )  
                        + '   </all-users-groups>' + "\n"
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
       });

       $.ajax({
           url: url,
           dataType: "json"
       }).done(function(pluginConfiguration) { 
           $xml = AJS.$( AJS.$.parseXML( decodeURIComponent(pluginConfiguration.xml) ) );
           $xml.find( "all-users-group" ).each( function( index ) {
              if( index > 0 ){ addConfigBox(); }
              container = AJS.$(".config-container").last();
              container.find(".confluence-support-all-users-group").val( $(this).html() );
           });
       }).fail(function(self,status,error){
          alert( error );
       });
   });

})(AJS.$ || jQuery);
