(function ($) { // this closure helps us keep our variables to ourselves.
    // This pattern is known as an "iife" - immediately invoked function expression

    // form the URL
    var listUrl = AJS.contextPath() + "/rest/jira-support/1.0/admin/user-list";
    var infoUrl = AJS.contextPath() + "/rest/jira-support/1.0/admin/user-info";
    var spinning = false;
    var index = 0;
    var listedCount = 0;
    var count = 0;
    var itemsToGetPerCall = 50;
    var usernames;
    var neverLoggedInCount = 0;
    var hasLoggedInCount = 0;

    function initializeProgressContainer(){
       AJS.$("#keysight-users-list-progress-container").html( "<p>Getting initial user list...</p>" );
    }

    // methods with access to local variables...
    function fillProgressContainer(){
       AJS.$("#keysight-users-list-progress-container").html( Keysight.JIRA.Support.Soy.Templates.usersListProgressContainer({index:listedCount.toString(), count:count.toString()}));
    }

    function toggleSpinner(){
       if (!spinning) {
	  spinnerOn();
       } else {
	  spinnerOff();
       }
    }

    function spinnerOn(){
       AJS.$('#keysight-progress-spinner').spin();
       spinning = true;
    }

    function spinnerOff(){
       AJS.$('#keysight-progress-spinner').spinStop();
       spinning = false;
    }

    function initializePage(data) {
       usernames = data;
       count = data.length;
       fillProgressContainer();
       spinnerOn();
       AJS.$("#keysight-users-list-container").html(Keysight.JIRA.Support.Soy.Templates.usersListContainer());
    }

    function addUserInfo(userInfo){
       listedCount++;
       if( userInfo["lastLoginDate"] == "Never" ){
          neverLoggedInCount++;
          AJS.$("#keysight-never-logged-in-user-list").after( "<li>" + userInfo["fullname"] + "(" + userInfo["username"] + ")</li>" );
       } else {
          hasLoggedInCount++;
          AJS.$("#keysight-active-user-ajax-table tr:last").after(
		          Keysight.JIRA.Support.Soy.Templates.userInfoRow({
                             count: listedCount,
		             loginName:userInfo["username"],
		             userName:userInfo["fullname"],
		             lastLogin:userInfo["lastLoginDate"],
		             loggedInXDaysAgo:userInfo["xDaysAgo"],
		          })
          );
       }
    }

    function getUserInfo()
    {
       var endIndex = index + itemsToGetPerCall;
       if( endIndex > usernames.length ){
          endIndex = usernames.length;
       }
       if( index < count ){
          AJS.$.ajax({
             url: infoUrl,
             type: "GET",
             contentType: "application/json",
             dataType: "json",
             data: {
                "usernames":usernames.slice(index,endIndex)
	     },
          }).done(function (data){ 
	     index = endIndex;
	     for( var i = 0; i < data.length; i++ ){
                addUserInfo( data[i] );
	     }
             fillProgressContainer();
             spinnerOn();
	     getUserInfo();
          }).fail(function (self, status, error) { 
             alert("Error" + error); 
             spinnerOff();
          });
       } else {
          spinnerOff();
       }
    }
	    
    // wait for the DOM (i.e., document "skeleton") to load. This likely isn't necessary for the current case,
    // but may be helpful for AJAX that provides secondary content.
    $(document).ready(function() {
       initializeProgressContainer();
       toggleSpinner();

       // request the config information from the server
       AJS.$.ajax({
           url: listUrl,
           dataType: "json"
       }).done(function(data) { // when the configuration is returned...
           // ...populate the form.
	   initializePage(data);
	   getUserInfo();
       }).fail(function(self,status,error){
          alert( error );
       });
   });

})(AJS.$ || jQuery);

alert( "loading keysight stuff!" );

