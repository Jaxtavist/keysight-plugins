(function ($) {

    // form the URL
    var url = AJS.contextPath() + "/rest/keysight-theme/1.0/admin-config/configuration";

    function addConfigBox(){
       AJS.$("#config-body").append( Keysight.Keysight.Theme.Admin.Config.Templates.config() );

       AJS.$(".on-space-creation-email-remove").unbind( "click" );
       AJS.$(".on-space-creation-email-remove").click(function(e) { 
          $(this).closest( ".config-container" ).remove();
       });
    }

    AJS.toInit(function() {
       addConfigBox();

       AJS.$("#keysight-theme-add-on-space-creation-email").click(function(e) { 
          e.preventDefault();
          addConfigBox();
       });

       AJS.$("#keysight-theme-admin-config").submit(function(e) { 
          e.preventDefault();

          var config = new Array();

          AJS.$(".config-container").each( function( index ) {
             config.push( '<on-space-creation-email>' 
                          + AJS.$(this).find(".keysight-theme-on-space-creation-email").attr("value") 
                          + '</on-space-creation-email>'
                        );
          });

          var xmlString = '<?xml version="1.0" encoding="UTF-8"?>' + "\n"
                        + '<plugin-configuration>' + "\n"
                        + '   <on-space-creation-emails>' + "\n"
                        + config.join( "\n" )  
                        + '   </on-space-creation-emails>' + "\n"
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
           $xml.find( "on-space-creation-email" ).each( function( index ) {
              if( index > 0 ){ addConfigBox(); }
              container = AJS.$(".config-container").last();
              container.find(".keysight-theme-on-space-creation-email").val( $(this).html() );
           });
       }).fail(function(self,status,error){
          alert( error );
       });
   });

})(AJS.$ || jQuery);
