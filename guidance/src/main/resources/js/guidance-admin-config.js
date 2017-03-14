guidanceConfigHelper = (function ($) {

    var methods = new Object();
    var url = AJS.contextPath() + "/rest/guidance/1.0/admin-config/configuration";

    methods['loadConfig'] = function(){

       $.ajax({
           url: url,
           dataType: "json"
       }).done(function(pluginConfiguration) { 
           var xml;

           if( pluginConfiguration.xml.indexOf( "plugin-configuration" ) >= 0 ){
              $xml = AJS.$( AJS.$.parseXML( decodeURIComponent(pluginConfiguration.xml) ) );
           } else {
              var xmlString = '<?xml version="1.0" encoding="UTF-8"?>' + "\n"
                            + '<plugin-configuration>' + "\n"
                            + '   <glyph-rendering-defaults>' + "\n"
                            + "      <for-html>false</for-html>" + "\n"
                            + "      <for-pdf>true</for-pdf>" + "\n"
                            + "      <for-word>false</for-word>" + "\n"
                            + "      <for-scroll>false</for-scroll>" + "\n"
                            + '   </glyph-rendering-defaults>' + "\n"
                            + '</plugin-configuration>' + "\n";
              $xml = AJS.$( AJS.$.parseXML( decodeURIComponent(xmlString) ) );
           }

           var forHtml = $xml.find( "for-html" );
           if( forHtml.length > 0 ){
              if( $(forHtml[0]).text() === "true" ){
                 AJS.$("#for-html").prop('checked', true );
              } else {
                 AJS.$("#for-html").prop('checked', false );
              }
           } else {
              AJS.$("#for-html").prop('checked', false );
           }

           var forPdf = $xml.find( "for-pdf" );
           if( forPdf.length > 0 ){
              if( $(forPdf[0]).text() === "false" ){
                 AJS.$("#for-pdf").prop('checked', false );
              } else {
                 AJS.$("#for-pdf").prop('checked', true );
              }
           } else {
              AJS.$("#for-pdf").prop('checked', true );
           }

           var forWord = $xml.find( "for-word" );
           if( forWord.length > 0 ){
              if( $(forWord[0]).text() === "true" ){
                 AJS.$("#for-word").prop('checked', true );
              } else {
                 AJS.$("#for-word").prop('checked', false );
              }
           } else {
              AJS.$("#for-word").prop('checked', false );
           }

           var forScroll = $xml.find( "for-scroll" );
           if( forScroll.length > 0 ){
              if( $(forScroll[0]).text() === "true" ){
                 AJS.$("#for-scroll").prop('checked', true );
              } else {
                 AJS.$("#for-scroll").prop('checked', false );
              }
           } else {
              AJS.$("#for-scroll").prop('checked', false );
           }

       }).fail(function(self,status,error){
          alert( error );
       });

    }

    methods['saveConfig'] = function(){

       var forHtmlStatus = "false";
       var forPdfStatus  = "true";
       var forWordStatus = "false";
       var forScrollStatus = "false";

       if( AJS.$("#for-html").is(":checked") ){
          forHtmlStatus = "true";
       } else {
          forHtmlStatus = "false";
       }

       if( AJS.$("#for-pdf").is(":checked") ){
          forPdfStatus = "true";
       } else {
          forPdfStatus = "false";
       }

       if( AJS.$("#for-word").is(":checked") ){
          forWordStatus = "true";
       } else {
          forWordStatus = "false";
       }

       if( AJS.$("#for-scroll").is(":checked") ){
          forScrollStatus = "true";
       } else {
          forScrollStatus = "false";
       }

       var xmlString = '<?xml version="1.0" encoding="UTF-8"?>' + "\n"
                     + '<plugin-configuration>' + "\n"
                     + '   <glyph-rendering-defaults>' + "\n"
                     + "      <for-html>" + forHtmlStatus + "</for-html>" + "\n"
                     + "      <for-pdf>" + forPdfStatus + "</for-pdf>" + "\n"
                     + "      <for-word>" + forWordStatus + "</for-word>" + "\n"
                     + "      <for-scroll>" + forScrollStatus + "</for-scroll>" + "\n"
                     + '   </glyph-rendering-defaults>' + "\n"
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

    return methods;
})(AJS.$ || jQuery);

AJS.toInit(function() {

   AJS.$("#for-html").click(function() { 
      guidanceConfigHelper.saveConfig();
   });

   AJS.$("#for-pdf").click(function() { 
      guidanceConfigHelper.saveConfig();
   });

   AJS.$("#for-word").click(function() { 
      guidanceConfigHelper.saveConfig();
   });

   AJS.$("#for-scroll").click(function() { 
      guidanceConfigHelper.saveConfig();
   });

   guidanceConfigHelper.loadConfig();
});
