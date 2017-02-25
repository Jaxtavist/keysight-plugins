AJS.bind("blueprint.wizard-register.ready", function () {

    // FAQ Methods
    function labelPickerPostRender(e, state) {
       var wizardForm = state.$container;
       var labelsField = $("#faq-blueprint-labels", wizardForm);
       labelsField.auiSelect2(Confluence.UI.Components.LabelPicker.build({
               separator: " ",
               queryOpts: {
                       spaceKey: state.wizardData.spaceKey
               }
       }));
    }

    function validateFaqForm($container, spaceKey) {
       var wizardForm = $container;
       var $titleField = wizardForm.find('#faq-create-page-dialog-title');
       var pageTitle = $.trim($titleField.val());
       var error;

        wizardForm.find('.error').html(''); // clear all existing errors

        if (!pageTitle) {
            error = "Title is required.";
        }
        else if (!Confluence.Blueprint.canCreatePage(spaceKey, pageTitle)) {
            error = "A page with this name already exists.";
        }

        if (error) {
            $titleField.focus().siblings(".error").html(error);
            return false;
        }

        return true;
    }

    
    function submitFaqForm(e, state) {
        return validateFaqForm(state.$container, state.wizardData.spaceKey);
    }
    
    // Register tutorial blueprint hooks
    Confluence.Blueprint.setWizard('com.keysight.include-content:faq-web-item', function(wizard) {
        wizard.on("post-render.faq-create-dialog-page-1", labelPickerPostRender);
        wizard.on("submit.faq-create-dialog-page-1", submitFaqForm);
    });
});


// This closure helps us keep our variables to ourselves.
// This pattern is known as "iife" - immediately invoked function expression
//
faqControllerFactory = (function($){
    var factoryMethods = new Object();

    factoryMethods.closeOpenDialogs = function(){
        for(var id in factoryMethods){
            if( id !== "create" && factoryMethods[id].isOpen ){
                factoryMethods[id].close();
            }
        }
    }

    factoryMethods.create = function(id){

        // module variables
        var methods = new Object();

        var pluginId                = "include-content";
        var restVersion             = "1.0";
        var baseUrl                 = AJS.Data.get( "base-url" );
        var getPageUrl              = baseUrl + "/rest/api/content/";
        var getPageListContentUrl   = baseUrl + "/rest/"+pluginId+"/"+restVersion+"/faq/items";
        var getSharedBlockUrl       = baseUrl + "/rest/"+pluginId+"/"+restVersion+"/get-shared-block";

        var containerId    = id 
        var container      = $("#"+id); 
        var parameters     = getParametersFromElement( container );
        var includeOptions = parameters;

        methods.isOpen        = false;
        var spinning          = false;
        var itemsPerCall      = 50;
        var lastItemIndex     = 0;
        var contentElementId;
   
        methods.start = function(id){
            initializeContainer();
            getItems();
        }

        methods.cancel = function(){
            methods.shouldCancel = true;
        }

        function initializeContainer(){
            container.append( Keysight.Include.Content.FAQ.Soy.Templates.initializeContainer( ) );
            container.find( ".keysight-faq-header" ).html( Keysight.Include.Content.FAQ.Soy.Templates.header() );
            updateProgress();
        }

        function updateProgress(){
            container.find( ".keysight-faq-footer" ).html( Keysight.Include.Content.FAQ.Soy.Templates.footer({i:lastItemIndex}) );
        }

        function getItems(){
            methods.shouldCancel = false;
            var buffer;

            if( !spinning ){
                spinnerOn();
            }

            parameters["itemsPerCall"]  = itemsPerCall;
            parameters["lastItemIndex"] = lastItemIndex;

            $.ajax({
                url: getPageListContentUrl,
                type: "GET",
                dataType: "json",
                data: parameters,
            }).done(function(data) {
                if( data.length > 0 && !methods.shouldCancel ){
                    var body = container.find( ".keysight-faq-body" );

                    // For the Link Render Style, place the table header...
                    if( parameters["renderStyle"] == "Link" || parameters["renderStyle"] == "Popup" ){
                        buffer = container.find( ".keysight-faq-link-table" );
                        if( buffer.length == 0  ){
                            body.append( "<table class=\"keysight-faq-link-table confluenceTable tablesorter tablesorter-default\" role=\"grid\">"
                                         + Keysight.Include.Content.FAQ.Soy.Templates.linkTableHeaderRow({includeSpace:includeOptions.includeSpace,
                                                                                                          includeLastModificationDate:includeOptions.includeLastModificationDate,
                                                                                                          includeLastModificationAuthorName:includeOptions.includeLastModificationAuthorName,
                                                                                                          includeLastModificationAuthorUserName:includeOptions.includeLastModificationAuthorUserName,
                                                                                                          includeLikes:includeOptions.includeLikes,
                                                                                                          includeLabels:includeOptions.includeLabels })
                                         + "<tbody class=\"keysight-faq-link-table-body\"></tbody></table>" );
                            buffer = container.find( ".keysight-faq-link-table-body" );
                        }
                        body = buffer;
                    }
             
                    for( i = 0; i < data.length; i++ ){
                        if( parameters["renderStyle"] == "Link" ){
                            body.append( Keysight.Include.Content.FAQ.Soy.Templates.popupTableDataRow({ popupAnchor:0,
                                                                                                        pageId:data[i]["pageId"],
                                                                                                        uniqueId:generateRandomId(),
                                                                                                        includeSpace:includeOptions.includeSpace,
                                                                                                        includeLastModificationDate:includeOptions.includeLastModificationDate,
                                                                                                        includeLastModificationAuthorName:includeOptions.includeLastModificationAuthorName,
                                                                                                        includeLastModificationAuthorUserName:includeOptions.includeLastModificationAuthorUserName,
                                                                                                        includeLikes:includeOptions.includeLikes,
                                                                                                        includeLabels:includeOptions.includeLabels,
                                                                                                        pageTitle:data[i]["pageTitle"],
                                                                                                        pageUrl:baseUrl + data[i]["pageUrl"],
                                                                                                        spaceName:data[i].spaceName,
                                                                                                        spaceUrl:baseUrl + data[i].spaceUrl,
                                                                                                        lastModificationDate:data[i].lastModificationTimeShort,
                                                                                                        lastModificationAuthorName:data[i].lastModificationAuthorName,
                                                                                                        lastModificationAuthorUserName:data[i].lastModificationAuthorUserName,
                                                                                                        likeCount:data[i].likeCount,
                                                                                                        labels:data[i].labels
                                                                                                       })
                            );
                        } else if( parameters["renderStyle"] == "Popup" ){
                            body.append( Keysight.Include.Content.FAQ.Soy.Templates.popupTableDataRow({popupAnchor:1,
                                                                                                       pageId:data[i]["pageId"],
                                                                                                       uniqueId:generateRandomId(),
                                                                                                       includeSpace:includeOptions.includeSpace,
                                                                                                       includeLastModificationDate:includeOptions.includeLastModificationDate,
                                                                                                       includeLastModificationAuthorName:includeOptions.includeLastModificationAuthorName,
                                                                                                       includeLastModificationAuthorUserName:includeOptions.includeLastModificationAuthorUserName,
                                                                                                       includeLikes:includeOptions.includeLikes,
                                                                                                       includeLabels:includeOptions.includeLabels,
                                                                                                       pageTitle:data[i]["pageTitle"],
                                                                                                       pageUrl:baseUrl + data[i]["pageUrl"],
                                                                                                       spaceName:data[i].spaceName,
                                                                                                       spaceUrl:baseUrl + data[i].spaceUrl,
                                                                                                       lastModificationDate:data[i].lastModificationTimeShort,
                                                                                                       lastModificationAuthorName:data[i].lastModificationAuthorName,
                                                                                                       lastModificationAuthorUserName:data[i].lastModificationAuthorUserName,
                                                                                                       likeCount:data[i].likeCount,
                                                                                                       labels:data[i].labels
                                                                                                     })
                            );
                            itemContainer = $(".faq-popup-item", body).last();
                            itemContainer.bind( "click keyup", function(e){
                                if( !( e.type == "keyup" && e.keyCode != 13 ) ){
                                    methods.getPopupContent(e);
                                }
                            });
                        } else {
                            body.append( Keysight.Include.Content.FAQ.Soy.Templates.expandItem({text:data[i]["pageTitle"],
                                                                                                pageId:data[i]["pageId"],
                                                                                                uniqueId:generateRandomId,
                                                                                                pageUrl:baseUrl + data[i]["pageUrl"],
                                                                                                spaceName:data[i].spaceName,
                                                                                                spaceUrl:baseUrl + data[i].spaceUrl,
                                                                                                lastModificationDate:data[i].lastModificationTimeLong,
                                                                                                lastModificationAuthorName:data[i].lastModificationAuthorName,
                                                                                                lastModificationAuthorUserName:data[i].lastModificationAuthorUserName,
                                                                                                likeCount:data[i].likeCount,
                                                                                                labels:data[i].labels.join(",")
                                                                                              })
                            );
                            itemContainer = $(".expand-container", body).last().bind( "click keyup", function(e){
                                if( !( e.type == "keyup" && e.keyCode != 13 ) ){
                                    methods.getExpandContent(e);
                                }
                            });
                        }
                    }
                    lastItemIndex += data.length;
                    updateProgress(container);

                    getItems();
                } else {
                    $( ".keysight-faq-loading", container ).remove();
                    spinnerOff(id);
                }
            }).fail(function(self, status, error) {
                handleError(self, status, error);
            });
        }

        function spinnerOn(){
            spinning = true;
            AJS.$("#"+containerId).find(".keysight-spinner").spin();
        }

        function spinnerOff(){
            spinning = false;
            AJS.$("#"+containerId).find(".keysight-spinner").spinStop();
        }

        function handleError(self, status, error){
            //alert( error );
        }

        function generateRandomId() {
            return Math.floor( (Math.random() * 10000000000) + 1 );
        }

        function preventIt( e ){
            // Prevent any attached event handlers from executing as that can result
            // in opening unwanted windows or closing the macro browser
            e.preventDefault();
            e.stopPropagation();
            $( e.target ).unbind();
        }

        function getParametersFromElement( container ) {
            var parameters = new Object();
            var buffer;
            var i;
            var keyAttributes = ["labels", "spaceKeys", "page", "pageId", "renderStyle", "renderContent", "popupTitle",
                                 "andLabels", "includeDecendants", "includeSpace", "includeLastModificationDate",
                                 "includeLastModificationAuthor", "includeLabels", "includeLikes"];

            for( i = 0; i < keyAttributes.length; i++ )
            {
                buffer = container.attr( keyAttributes[i] );
                if( keyAttributes[i].indexOf("include") == 0 ){
                    if( buffer != null ){
                        parameters[keyAttributes[i]] = 1;
                    } else {
                        parameters[keyAttributes[i]] = 0;
                    }
                } else {
                    buffer = container.attr( keyAttributes[i] );
                    if( buffer != null ){
                        parameters[keyAttributes[i]] = buffer;
                    }
                }
            }

            if( container.attr( "includeLastModificationAuthor" ) != null ){
                parameters["includeLastModificationAuthorName"] = 1;
                parameters["includeLastModificationAuthorUserName"] = 1;
            }

            return parameters;
        }

        methods.getExpandContent = function(e){
            var renderStyle = "Expand";
            var renderContent = "Page";
            var buffer;
            var itemContainer = $(e.currentTarget)
            var pageId    = itemContainer.attr("page-id");
            var requestUrl = getPageUrl + pageId;
            var requestData = { "expand":"body.view" };
            var contentBody;
                
            var pageTitle                      = itemContainer.attr("page-title");
            var pageUrl                        = itemContainer.attr("page-url");
            var spaceName                      = itemContainer.attr("space-name");
            var spaceUrl                       = itemContainer.attr("space-url");
            var lastModificationDate           = itemContainer.attr("last-modification-date");
            var lastModificationAuthorName     = itemContainer.attr("last-modification-author-name");
            var lastModificationAuthorUserName = itemContainer.attr("last-modification-author-user-name");
            var likeCount                      = itemContainer.attr("like-count");
            var labels                         = itemContainer.attr("labels");

            preventIt(e) ;

            if( parameters.renderContent ){
                renderContent = parameters.renderContent;
            }

            if( renderContent == "First Excerpt" ){
                requestUrl = getSharedBlockUrl;
                requestData = { "page-id":pageId, "excerpt":"true" };
            } else if( renderContent == "First Shared Block" ){
                requestUrl = getSharedBlockUrl;
                requestData = { "page-id":pageId };
            }

            // request the page from the server
            $.ajax({
                url: requestUrl,
                type: "GET",
                dataType: "json",
                data: requestData
            }).done(function(data) { // when the configuration is returned...

                if( renderContent == "First Excerpt" || renderContent == "First Shared Block" ){
                    contentBody = data["message-body"];
                } else if( renderContent == "Page" ){
                    contentBody = data.body.view.value;
                } else {
                    contentBody = "<p>Error.  Unknown render content setting: " + renderContent + ". Valid values are Page, First Excerpt and First Shared Block</p>";
                }

                // populate the div body
                var element = itemContainer.find(".expand-content" );
                element.append( Keysight.Include.Content.FAQ.Soy.Templates.pageInfo({ includeSpace:includeOptions.includeSpace,
                                                                                      includeLastModificationDate:includeOptions.includeLastModificationDate,
                                                                                      includeLastModificationAuthorName:includeOptions.includeLastModificationAuthorName,
                                                                                      includeLastModificationAuthorUserName:includeOptions.includeLastModificationAuthorUserName,
                                                                                      includeLikes:includeOptions.includeLikes,
                                                                                      includeLabels:includeOptions.includeLabels,
                                                                                      spaceName:spaceName,
                                                                                      spaceUrl:spaceUrl,
                                                                                      lastModificationDate:lastModificationDate,
                                                                                      lastModificationAuthorName:lastModificationAuthorName,
                                                                                      lastModificationAuthorUserName:lastModificationAuthorUserName,
                                                                                      likeCount:likeCount,
                                                                                      labels:labels.split(",")
                                                                                    })
                );
                element.append( contentBody );
   
                // unbind the method to populate the content
                itemContainer.unbind( "click keyup" );
    
                // remove the page-id, so it looks like a regular Confluence expand item.
                itemContainer.removeAttr( "page-id" );
    
                // bind the Confluence expand macro functionality
                Confluence.Plugins.ExpandMacro.bind( $, itemContainer, "click keyup", function(e) {
                    return !(e.type == "keyup" && e.keyCode != 13);
                });
    
                // pass the event to the newly bound expand macro methods.
                itemContainer.find( ".expand-control-text" ).first().trigger("click");

            }).fail(function(self, status, error) {
                handleError(self, status, error);
            });
        }

        methods.getPopupContent = function(e){
            var renderStyle = "Popup";
            var renderContent = "Page";
            var buffer;
            var title  = "Preview";
            var tip    = "";
            var itemContainer = $(e.currentTarget);
            var pageId    = itemContainer.attr("page-id");
            var pageTitle = itemContainer.attr("page-title");
            var requestUrl = getPageUrl + pageId;
            var requestData = { "expand":"body.view" };
            var contentBody;
            var includeLinkToPage = 0;
                
            preventIt( e );

            if( parameters.renderContent ){
                renderContent = parameters.renderContent;
            }

            if( renderContent == "First Excerpt" ){
                requestUrl = getSharedBlockUrl;
                requestData = { "page-id":pageId, "excerpt":"true" };
            } else if( renderContent == "First Shared Block" ){
                requestUrl = getSharedBlockUrl;
                requestData = { "page-id":pageId };
            }

            if( !methods.isOpen ){
                contentElementId = itemContainer.attr("content-id");
                if( $( "#"+contentElementId ).length == 0 ){
                    // create div element
                    if( renderContent == "Page" ){
                        includeLinkToPage = 0;
                    } else {
                        includeLinkToPage = 1;
                    }

                    if( parameters["popupTitle"] != null ){
                        title = parameters["popupTitle"];
                        if( title.contains( "$title" ) && pageTitle != null ){
                            title.replace( "$title", pageTitle );
                        }
                    } else if( pageTitle != null ){
                        title = pageTitle;
                    } 

                    $( "body" ).append( Keysight.Include.Content.FAQ.Soy.Templates.dialog2section( {sectionId:contentElementId,
                                                                                                    title:title, tip:tip,
                                                                                                    includeLinkToPage:includeLinkToPage,
                                                                                                    openMethod:"keysightOpenPage(\'"+pageId+"\');",
                                                                                                    closeMethod:"faqControllerFactory[\'"+containerId+"\'].close(\'"+contentElementId+"\');"}));
                    var contentElement = AJS.$("#"+contentElementId);
                    var spinner = contentElement.find(".keysight-spinner");

                    // start the spinner
                    spinner.spin();
                      
                    // request the page from the server
                    $.ajax({
                        url: requestUrl,
                        type: "GET",
                        dataType: "json",
                        data: requestData
                    }).done(function(data) { // when the configuration is returned...
                        // stop the spinner
                        spinner.spinStop();

                        if( renderContent == "First Excerpt" || renderContent == "First Shared Block" ){
                            contentBody = data["message-body"];
                        } else if( renderContent == "Page" ){
                            contentBody = data.body.view.value;
                        } else {
                            contentBody = "<p>Error.  Unknown render content setting: " + renderContent + ". Valid values are Page, First Excerpt and First Shared Block</p>";
                        }

                        // populate the div body
                        contentElement.find(".aui-dialog2-content").html( contentBody );
                    }).fail(function(self,status,error){
                        handleError(self, status, error);
                    });
                }

                // show the help dialog
                AJS.dialog2( "#"+contentElementId ).show();
                methods.isOpen = true;
            }
        }
   
        methods.close = function(id){
            if( methods.isOpen )
            {
                AJS.dialog2("#"+id).hide();
                methods.isOpen = false;

                // For reasons I don't understand, the binding to this function
                // tends to get blown away.  Perhaps Confluence is resting the anchor tags.
                $( ".faq-popup-item[content-id=\'"+id+"\']" ).bind( "click keyup", function(e){
                    if( !( e.type == "keyup" && e.keyCode != 13 ) ){
                        faqControllerFactory[containerId].getPopupContent( e );
                    }
                });
            }
        }

        // return the object with the methods
        return methods;
        }

// return the object with the methods return factoryMethods;

    return factoryMethods;
// End closure
})( AJS.$ || jQuery );

AJS.toInit(function() {
   AJS.$( ".keysight-faq" ).each( function(){
      var id = $(this).attr("id");
      faqControllerFactory[id] = faqControllerFactory.create(id);

      AJS.$( ".keysight-faq-cancel[cancels-faq-id=\'"+id+"\']" ).bind( "click", function(e){ faqControllerFactory[id].cancel() = true });
      faqControllerFactory[id].start();
   });
});

function keysightOpenPage( pageId ){
    window.open( AJS.Data.get( "base-url" ) + "/pages/viewpage.action?pageId=" + pageId, "_self" );
}

