var actionsBySpaceController = (function ($) { // this closure helps us keep our variables to ourselves.
    // form the URL
    var methods = new Object();
    var getSpacesRestUrl = AJS.contextPath() + "/rest/confluence-support/1.0/admin/get-spaces";
    var fixPagesBySpaceRestUrl = AJS.contextPath() + "/rest/confluence-support/1.0/admin/fix-titleless-pages-in-space";
    var onePageRestUrl = AJS.contextPath() + "/rest/confluence-support/1.0/admin/fix-titleless-page";
    var emptyTrashBySpaceRestUrl = AJS.contextPath() + "/rest/confluence-support/1.0/admin/empty-trash-in-space";
    var replaceTextBySpaceRestUrl = AJS.contextPath() + "/rest/confluence-support/1.0/admin/replace-text-in-space";
    var testReplaceTextRestUrl = AJS.contextPath() + "/rest/confluence-support/1.0/admin/test-replace-text";
    var validateGroupsRestUrl = AJS.contextPath() + "/rest/confluence-support/1.0/admin/validate-groups";
    var fixAnonymousAccessIssuesBySpaceRestUrl = AJS.contextPath() + "/rest/confluence-support/1.0/admin/fix-anonymous-access-issues";
    var spinning = false;
    var mode = 'dry-run';
    var dryRun = true;
    var spaceList;
    var currentSpaceIndex;
    var runAction;
    var replaceConfirmationDialogIsOpen = false;
    var replacementPattern;
    var replacementText;

    function updateProgressContainer(text){
       $("#actions-by-space-progress-container").html( "<p>" + text + "</p>" );
    }

    function spinnerOn(){
       $('#keysight-fix-unnamed-pages-spinner').spin();
       spinning = true;
    }

    function spinnerOff(){
       $('#keysight-fix-unnamed-pages-spinner').spinStop();
       spinning = false;
    }

    methods["initializePage"] = function() {
       $("#dry-run-replace-button").on( "click", function(e){ fixAllPages( e, "dry-run-replace-text" ); } );
       $("#replace-text-button").on( "click", function(e){ confirmReplaceTextOnAllPages( e, "replace-text" ); } );
       $("#test-replace-text-button").on( "click", function(e){ testReplaceText( e ); } );

       $("#fix-title-list-pages-button").on( "click", function(e){ fixAllPages( e, "list-titleless-pages" ); } );
       $("#fix-title-fix-pages-button").on( "click", function(e){ fixAllPages( e, "fix-titleless-pages" ); } );

       $("#list-space-trash-button").on( "click", function(e){ fixAllPages( e, "list-space-trash" ); } );
       $("#empty-space-trash-button").on( "click", function(e){ fixAllPages( e, "empty-space-trash" ); } );

       $("#list-anonymous-access-issues-button").on( "click", function(e){ validateFixAnonymousIssues( e, "list-anonymous-access-issues" ); } );
       $("#fix-anonymous-access-issues-button").on( "click", function(e){ validateFixAnonymousIssues( e, "fix-anonymous-access-issues" ); } );
    }

    function validateFixAnonymousIssues(e, setmode)
    {
       preventIt(e);
       runAction = setmode;
       var allUsersGroups = $("#fix-anonymous-access-all-users-groups").val();

       if( !allUsersGroups ){
          $("#error-for-fix-anonymous-all-users-groups").html("One or more groups must be specified");
       } else {

          var groups = allUsersGroups.split(",");
          for( var i = 0; i < groups.length; i++ ){
             groups[i] = groups[i].trim();
          }
          var args = { groups:groups };

          $.ajax({
             url: validateGroupsRestUrl,
             dataType: "json",
             data: args
          }).done(function(data) {
             if( data[0] == "OK" ){
                $("#error-for-fix-anonymous-all-users-groups").html( "" );
                getSpaces();
             } else {
                $("#error-for-fix-anonymous-all-users-groups").html( data[0] );
             }
          }).fail(function(self,status,error){
             alert( error );
          });
       }

    }

    function confirmReplaceTextOnAllPages(e, setmode)
    {
       preventIt(e);
       runAction = setmode;
       var originalText = $("#find-and-replace-original-text").val();

       if( !originalText ){
          $("#error-for-find-and-replace-original-text").html("The original text cannot be empty.");
       } else {
          $("#error-for-find-and-replace-original-text").html("");
          if( !replaceConfirmationDialogIsOpen ){
             if( $("#replace-page-confirmation-dialog" ).length == 0 ){
                $( "body" ).append( Keysight.Confluence.Support.Soy.Templates.replaceTextConfirmation( 
                        { originalText: originalText,
                          replacementText: $("#find-and-replace-replacement-text").val(),
                          closeMethod:"actionsBySpaceController.closeConfirmReplaceTextOnAllPages();",
                          openMethod:"actionsBySpaceController.executeReplaceTextOnAllPages();" } ));
             }
             replaceConfirmationDialogIsOpen = true;
             AJS.dialog2( "#replace-page-confirmation-dialog" ).show();
          }
       }
    }

    function testReplaceText(e){
       preventIt(e);
       var args = { "original-text":  $("#find-and-replace-sample-text").val(),
                    "pattern-text": $("#find-and-replace-original-text").val(),
                    "replacement-text": $("#find-and-replace-replacement-text").val()
       }

       $.ajax({
          url: testReplaceTextRestUrl,
          dataType: "json",
          data: args
       }).done(function(data) {
          alert( data[0] );
       }).fail(function(self,status,error){
          alert( error );
       });
    }

    methods["executeReplaceTextOnAllPages"] = function()
    {
        methods.closeConfirmReplaceTextOnAllPages();
        getSpaces();
    }

    methods["closeConfirmReplaceTextOnAllPages"] = function()
    {
       if( replaceConfirmationDialogIsOpen ){
          AJS.dialog2( "#replace-page-confirmation-dialog" ).hide();
          replaceConfirmationDialogIsOpen = false;
       }
    }

    function getSpaces()
    {
       var args;

       // clear the space list
       $(".actions-by-space-space-list-container").html( "<ul class=\"actions-by-space-space-list\"></ul>" );

       if( runAction === "list-titleless-pages" || runAction === "fix-titleless-pages" ){
          args = { "space-categories" : $("#fix-title-space-categories").val() };
       } else if( runAction === "list-space-trash" || runAction === "empty-space-trash" ){
          args = { "space-categories" : $("#empty-trash-space-categories").val() };
       } else if( runAction === "dry-run-replace-text" || runAction === "replace-text" ){
          args = { "space-key" : $("#find-and-replace-space-key").val(),
                   "space-categories" : $("#find-and-replace-space-categories").val() };
       } else if( runAction === "list-anonymous-access-issues" || runAction === "fix-anonymous-access-issues" ){
          args = { "space-key" : $("#fix-anonymous-access-space-key").val(),
                   "space-categories" : $("#fix-anonymous-access-space-categories").val() };
       }

       $.ajax({
          url: getSpacesRestUrl,
          dataType: "json",
          data: args
       }).done(function(data) {
          spaceList = data;

          for( i = 0; i < spaceList.length; i++ ){
             var niceSpaceKey = spaceList[i].spaceKey.replace("~", "--tilda--");
             $(".actions-by-space-space-list").append( "<li class=\"actions-by-space-space-entry\" id=\"space-key-" + niceSpaceKey + "\">" 
                                                     + spaceList[i].spaceName + " (" + spaceList[i].spaceKey + ")</li>\n" );
          }

          $(".actions-by-space-results-list-container").html( "<ul class=\"actions-by-space-results-list\"></ul>" );
          currentSpaceIndex = 0;
          if( runAction === "list-titleless-pages" || runAction === "fix-titleless-pages" ){
             listTitlelessPages();
          } else if( runAction === "list-space-trash" || runAction === "empty-space-trash" ){
             listSpaceTrash();
          } else if( runAction === "dry-run-replace-text" || runAction === "replace-text" ){
             listReplaceText();
          } else if( runAction === "list-anonymous-access-issues" || runAction === "fix-anonymous-access-issues" ){
             listAnonymousAccessIssues();
          }

       }).fail(function(self,status,error){
          alert( error );
       });
    }

    function listAnonymousAccessIssues()
    {
       if( currentSpaceIndex < spaceList.length ){
          var args = { "space-key":spaceList[currentSpaceIndex].spaceKey };
          var niceSpaceKey = spaceList[currentSpaceIndex].spaceKey.replace("~", "--tilda--");

          if( runAction === "fix-anonymous-access-issues" ){
             args.mode = "fix-issues";
          }

          var groups = $("#fix-anonymous-access-all-users-groups").val().split(",");
          for( var i = 0; i < groups.length; i++ ){
             groups[i] = groups[i].trim();
          }

          args["groups"] = groups;



          updateProgressContainer( "Scanning " + spaceList[currentSpaceIndex].spaceName);
          $(".actions-by-space-active").addClass( "actions-by-space-visited" ).removeClass( "actions-by-space-active" );
          $("#space-key-"+niceSpaceKey).addClass( "actions-by-space-active" );
       
          $.ajax({
             url: fixAnonymousAccessIssuesBySpaceRestUrl,
             dataType: "json",
             data: args
          }).done(function(data) {
             for( i = 0; i < data.length; i++ ){
                $(".actions-by-space-results-list").append( "<li>" + data[i] + "</li>\n" );
                $(".fix-anonymous-access-issue-in-space-anchor").off();
                $(".fix-anonymous-access-issue-in-space-anchor").on("click", function(e){ fixAnonymousAccessIssueInSpace(e); } );
             }
          }).fail(function(self,status,error){
             alert( error );
          });

          currentSpaceIndex++;
          listAnonymousAccessIssues();
       } else {
          updateProgressContainer( "" );
          $(".actions-by-space-active").addClass( "actions-by-space-visited" ).removeClass( "actions-by-space-active" );
       }
    }

    function listReplaceText()
    {
       if( currentSpaceIndex < spaceList.length ){
          var args = { "space-key":spaceList[currentSpaceIndex].spaceKey };

          if( runAction === "replace-text" ){
             args.mode = "replace-text";
          }
          replacementPattern = $("#find-and-replace-original-text").val();
          replacementText    = $("#find-and-replace-replacement-text").val();

          args["pattern-text"] = replacementPattern;
          args["replacement-text"] = replacementText;

          updateProgressContainer( "Scanning " + spaceList[currentSpaceIndex].spaceName);
          $(".actions-by-space-active").addClass( "actions-by-space-visited" ).removeClass( "actions-by-space-active" );
          $("#space-key-"+spaceList[currentSpaceIndex].spaceKey).addClass( "actions-by-space-active" );
       
          $.ajax({
             url: replaceTextBySpaceRestUrl,
             dataType: "json",
             data: args
          }).done(function(data) {
             for( i = 0; i < data.length; i++ ){
                $(".actions-by-space-results-list").append( "<li>" + data[i] + "</li>\n" );
                $(".replace-text-in-space-anchor").off();
                $(".replace-text-in-space-anchor").on("click", function(e){ replaceTextInSpace(e); } );
             }
          }).fail(function(self,status,error){
             alert( error );
          });

          currentSpaceIndex++;
          listReplaceText();
       } else {
          updateProgressContainer( "" );
          $(".actions-by-space-active").addClass( "actions-by-space-visited" ).removeClass( "actions-by-space-active" );
       }
    }

    function listTitlelessPages()
    {
       if( currentSpaceIndex < spaceList.length ){
          var args = { "space-key":spaceList[currentSpaceIndex].spaceKey };

          if( runAction === "fix-titleless-pages" ){
             args.mode = "fix-titleless-pages";
          }

          updateProgressContainer( "Scanning " + spaceList[currentSpaceIndex].spaceName);
          $(".actions-by-space-active").addClass( "actions-by-space-visited" ).removeClass( "actions-by-space-active" );
          $("#space-key-"+spaceList[currentSpaceIndex].spaceKey).addClass( "actions-by-space-active" );
       
          $.ajax({
             url: fixPagesBySpaceRestUrl,
             dataType: "json",
             data: args
          }).done(function(data) {
             for( i = 0; i < data.length; i++ ){
                $(".actions-by-space-results-list").append( "<li>" + data[i] + "</li>\n" );
                $(".keysight-fix-page-anchor").off();
                $(".keysight-fix-page-anchor").on("click", function(e){ fixUnnamedPage(e); } );
             }
          }).fail(function(self,status,error){
             alert( error );
          });

          currentSpaceIndex++;
          listTitlelessPages();
       } else {
          updateProgressContainer( "" );
          $(".actions-by-space-active").addClass( "actions-by-space-visited" ).removeClass( "actions-by-space-active" );
       }
    }

    function listSpaceTrash()
    {
       if( currentSpaceIndex < spaceList.length ){
          var args = { "space-key":spaceList[currentSpaceIndex].spaceKey };

          if( runAction === "empty-space-trash" ){
             args.mode = "empty-space-trash";
          }

          updateProgressContainer( "Scanning " + spaceList[currentSpaceIndex].spaceName);
          $(".actions-by-space-active").addClass( "actions-by-space-visited" ).removeClass( "actions-by-space-active" );
          $("#space-key-"+spaceList[currentSpaceIndex].spaceKey).addClass( "actions-by-space-active" );
       
          $.ajax({
             url: emptyTrashBySpaceRestUrl,
             dataType: "json",
             data: args
          }).done(function(data) {
             for( i = 0; i < data.length; i++ ){
                $(".actions-by-space-results-list").append( "<li>" + data[i] + "</li>\n" );
                $(".empty-trash-in-space-anchor").off();
                $(".empty-trash-in-space-anchor").on("click", function(e){ emptyTrashInSpace(e); } );
             }
          }).fail(function(self,status,error){
             alert( error );
          });

          currentSpaceIndex++;
          listSpaceTrash();
       } else {
          updateProgressContainer( "" );
          $(".actions-by-space-active").addClass( "actions-by-space-visited" ).removeClass( "actions-by-space-active" );
       }
    }

    function fixAnonymousAccessIssueInSpace( e ){
       preventIt( e );

       var group = $(e.target).attr( "fix-anonymous-access-issues-for-group" );
       var permission = $(e.target).attr( "fix-anonymous-access-issues-for-permission" );
       var spaceKey = $(e.target).attr( "fix-anonymous-access-issues-in-space-with-key" );
       var args = { "space-key" : spaceKey, 
                    "return-space-key" : "true", 
                    "mode" : "fix-issues",
                    "groups" : [group],
                    "permissions" : [permission] };

       AJS.$.ajax({
          url: fixAnonymousAccessIssuesBySpaceRestUrl,
          dataType: "json",
          data: args
       }).done(function(data) {
          if( data.length >= 4 && data.length % 4 == 0 ){
             for( var i = 0; i < data.length; i += 4 ){
                var id = "#fix-anonymous-access-issues-for-"+data[i]+"-for-group-"+data[i+1]+"-in-space-"+data[i+2];
                $( id ).html( data[i+3] );
             }
          }
       }).fail(function(self,status,error){
          alert( error );
       });
    }

    function replaceTextInSpace( e ){
       preventIt( e );
       var spaceKey = $(e.target).attr( "replace-text-in-space-with-key" );
       var args = { "space-key" : spaceKey, 
                    "return-space-key" : "true", 
                    "mode" : "replace-text",
                    "pattern-text" : replacementPattern,
                    "replacement-text" : replacementText };

       AJS.$.ajax({
          url: replaceTextBySpaceRestUrl,
          dataType: "json",
          data: args
       }).done(function(data) {
          if( data.length == 2 ){
             $( "#replace-text-in-" + data[0] ).html( data[1] );
          }
       }).fail(function(self,status,error){
          alert( error );
       });
    }

    function emptyTrashInSpace( e ){
       preventIt( e );
       var spaceKey = $(e.target).attr( "empty-trash-in-space-with-key" );
       var args = { "space-key" : spaceKey, "return-space-key" : "true", "mode" : "empty-space-trash" };

       AJS.$.ajax({
          url: emptyTrashBySpaceRestUrl,
          dataType: "json",
          data: args
       }).done(function(data) {
          if( data.length == 2 ){
             $( "#empty-trash-in-" + data[0] ).html( data[1] );
          }
       }).fail(function(self,status,error){
          alert( error );
       });
    }

    function fixUnnamedPage( e ){
       preventIt( e );
       var pageId = $(e.target).attr( "id-of-page-to-fix" );
       var args = { "page-id" : pageId };

       AJS.$.ajax({
          url: onePageRestUrl,
          dataType: "json",
          data: args
       }).done(function(data) {
          if( data.length == 2 ){
             $( "#fixpage-" + data[0] ).html( data[1] );
          }
       }).fail(function(self,status,error){
          alert( error );
       });
    }

    function fixAllPages( e, setmode ) {
       preventIt( e );
       runAction = setmode;

       if( (setmode === "replace-text" || setmode === "dry-run-replace-text") && !$("#find-and-replace-original-text").val() )
       {
          $("#error-for-find-and-replace-original-text").html("The original text cannot be empty.");
       } else {
          $("#error-for-find-and-replace-original-text").html("");
          getSpaces();
       }
    }

    function preventIt( e ){
       e.preventDefault();
       e.stopPropagation();
    }


    return methods;
})(AJS.$ || jQuery);

AJS.toInit(function() {
   actionsBySpaceController.initializePage();
});


