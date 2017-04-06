AJS.toInit(function($){
   $(".keysight-update-descendant-labels").on( "click", keysightUpdateDescendantLabels.showUpdateDescendantLabelsDialog );
});


keysightUpdateDescendantLabels = (function($){
   var methods         = new Object();
   var pluginId        = "keysight-theme";
   var restVersion     = "1.0";
   var baseUrl         = AJS.Data.get( "base-url" );
   var updateDescendantLabelsUrl = baseUrl + "/rest/"+pluginId+"/"+restVersion+"/updateDescendantLabels/updateLabels";
   var bRemove         = false;

   methods.isOpen      = false;

   methods[ 'showUpdateDescendantLabelsDialog' ] = function( e ){ 
      preventIt(e);
  
      if( !methods.isOpen ){
         if( $( "#keysight-update-descendant-labels-dialog" ).length == 0 ){
            $( "body" ).append( Keysight.Update.Descendant.Labels.Templates.updateDescendantLabelsDialog() );
         }
       
         $("#keysight-update-descendant-labels-progress-container").hide();

         $("#keysight-labels-to-update").auiSelect2(Confluence.UI.Components.LabelPicker.build({
             separator: ",",
         }));


         // show the help dialog
         AJS.dialog2("#keysight-update-descendant-labels-dialog").show();
         methods.isOpen = true;
      }
   }

   methods[ 'updateLabels' ] = function(mode){
      var requestData = new Object();
      var labelsToUpdate = $("#keysight-labels-to-update").auiSelect2("val");
      var labelsToUpdateHtml = "<strong>" + labelsToUpdate.join( "</strong>, <strong>" ) + "</strong>";
      var spinner = $("#keysight-update-descendant-labels-progress-spinner");

      if( mode == "remove" ){
         bRemove = true;
      } else {
         bRemove = false;
      }

      // reset the labels
      $("#keysight-labels-to-update").auiSelect2("val", "");

      // show the progress container
      $("#keysight-update-descendant-labels-progress-container").show();


      if( bRemove ){
         $("#keysight-update-descendant-labels-progress-text").html( "<p>Removing labels " + labelsToUpdateHtml + " from page...</p><br/>" );
      } else {
         $("#keysight-update-descendant-labels-progress-text").html( "<p>Adding labels " + labelsToUpdateHtml + " to page...</p><br/>" );
      }

      spinner.spin();

      /*
      // update the current page
      for( i = 0; i < labelsToUpdate.length; i++ ){
         if( bRemove ){
            AJS.Labels.removeLabel( labelsToUpdate[i], AJS.params.pageId, AJS.params.contentType).done(function(){});
         } else {
            AJS.Labels.addLabel( labelsToUpdate[i], AJS.params.pageId, AJS.params.contentType).done(function(){});
         }
      }
      */

      requestData = { labelsToUpdate:labelsToUpdate,
                      pageId:AJS.params.pageId };

      if( bRemove ){
         requestData[ 'remove' ] = "true";
      }

      // request the page from the server
      $.ajax({
         url: updateDescendantLabelsUrl,
         type: "GET",
         dataType: "json",
         data: requestData
      }).done(function(data){ 
         spinner.spinStop();

         var pageLabelsContainer = $(".labels-content");

         if( pageLabelsContainer != null && data.labelDescriptions != null ){
            for( var i = 0; i < data.labelDescriptions.length; i++ ){
               var labelDescription = data.labelDescriptions[i];
               var keyElement = pageLabelsContainer.find( "li.aui-label[data-label-id="+labelDescription.labelId+"]" );
               if( bRemove ){
                  if( keyElement.length > 0 ){
                     keyElement[0].remove();

                     var ulElement = pageLabelsContainer.children( "ul.label-list" );
                     var liElements = ulElement.children("li.aui-label");
                     if( liElements.length == 0 && ulElement.children("li.no-labels-message").length == 0 ){
                        ulElement.prepend( "<li class=\"no-labels-message\">No labels</li>\n" );
                     }
                  }
               } else {
                  if( keyElement.length == 0 ){
                     var ulElement = pageLabelsContainer.children( "ul.label-list" );
                     var liElements = ulElement.children("li.aui-label");
                     var newLabelElement = "<li class=\"aui-label\" data-label-id=\""+labelDescription.labelId+"\">\n"
                                          +"   <a class=\"aui-label-split-main\" href=\""+labelDescription.urlPath+"\" rel=\"tag\">"+labelDescription.labelName+"</a>\n"
                                          +"</li>\n";

                     if( liElements.length > 0 ){
                        liElements.last().after( newLabelElement );
                     } else {
                        ulElement.children( ".no-labels-message" ).remove();
                        ulElement.prepend( newLabelElement );
                     }
                  }
               }
            }
         }

         if( bRemove ){
            $("#keysight-update-descendant-labels-progress-text").html( "<p>Removing labels " + labelsToUpdateHtml + " to page...Done.<br/>"
                                                                       +"Removed labels from " + data.pageCount + " pages. </p><br/>" );
         } else {
            $("#keysight-update-descendant-labels-progress-text").html( "<p>Adding labels " + labelsToUpdateHtml + " to page...Done.<br/>"
                                                                       +"Added labels to " + data.pageCount + " pages. </p><br/>" );

         }
      }).fail(function(self,status,error){
          handleError(self, status, error);
      });
   }

   methods[ 'closeUpdateDescendantLabelsDialog' ] = function(){
      if( methods.isOpen ){
         AJS.dialog2("#keysight-update-descendant-labels-dialog").hide();
         $("#keysight-labels-to-update").auiSelect2("val", "");
         methods.isOpen = false;
      }
   }

   function preventIt( e ){
      e.preventDefault();
      e.stopPropagation();
   }

   function handleError(self, status, error){
      alert( error );
   }

   return methods;

})(AJS.$ || jQuery );
