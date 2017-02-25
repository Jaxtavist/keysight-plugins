(function ($) { // this closure helps us keep our variables to ourselves.
    // This pattern is known as an "iife" - immediately invoked function expression

    // form the URL
    var spaceListUrl = AJS.contextPath() + "/rest/confluence-support/1.0/admin/space-list";
    var spaceInfoUrl = AJS.contextPath() + "/rest/confluence-support/1.0/admin/space-info";
    var spinning = false;
    var index = 0;
    var listedCount = 0;
    var count = 0;
    var itemsToGetPerCall = 2;
    var spaces;

    function initializeProgressContainer(){
       AJS.$("#keysight-space-inventory-progress-container").html( "<p>Getting initial list of spaces</p>");
    }

    // methods with access to local variables...
    function fillProgressContainer(){
       AJS.$("#keysight-space-inventory-progress-container").html( Keysight.Confluence.Support.Soy.Templates.progressContainer({index:listedCount.toString(), count:count.toString()}));
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
       spaces = data;
       count = data.length;
       fillProgressContainer();
       spinnerOn();
       AJS.$("#keysight-space-inventory-container").html(Keysight.Confluence.Support.Soy.Templates.spaceInventoryContainer());
    }

    function makeList(list1, list2){
       var html = "<ul>\n";
       for( var i = 0; i< list1.length; i++ ){
          html = html + "<li>" + list1[i] + "</li>\n";
       }
       for( var i = 0; i< list2.length; i++ ){
          html = html + "<li>" + list2[i] + "</li>\n";
       }
       html = html + "</ul>\n";
       return html;
    }

    function addSpaceInfo(spaceInfo){
       listedCount++;
       AJS.$("#keysight-space-inventory-ajax-table tr:last").after(
		       Keysight.Confluence.Support.Soy.Templates.spaceInfoRow({
                          count: listedCount,
		          key:spaceInfo["spaceKey"],
		          name:spaceInfo["spaceName"],    
		          type:spaceInfo["spaceType"],    
		          status:spaceInfo["spaceStatus"],    
		          pageCount:spaceInfo["pageCount"],
		          lastModificationDate:spaceInfo["lastModificationDate"],
		          xDaysAgo:spaceInfo["xDaysAgo"],
		          adminUsers:makeList( spaceInfo["adminUsers"], spaceInfo["adminGroups"] ),
		          readWriteUsers:makeList( spaceInfo["authorUsers"], spaceInfo["authorGroups"] ),
		          readOnlyUsers:makeList( spaceInfo["readerUsers"], spaceInfo["readerGroups"] )
		       })
       );
    }

    function getSpaceInfo()
    {
       var endIndex = index + itemsToGetPerCall;
       if( endIndex > spaces.length ){
          endIndex = spaces.length;
       }
       if( index < count ){
          AJS.$.ajax({
             url: spaceInfoUrl,
             type: "GET",
             contentType: "application/json",
             dataType: "json",
             data: {
                "spaceKeys":spaces.slice(index,endIndex)
	     },
          }).done(function (data){ 
	     index = endIndex;
	     for( var i = 0; i < data.length; i++ ){
                addSpaceInfo( data[i] );
	     }
             fillProgressContainer();
             spinnerOn();
	     getSpaceInfo();
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
           url: spaceListUrl,
           dataType: "json"
       }).done(function(data) { // when the configuration is returned...
           // ...populate the form.
	   initializePage(data);
	   getSpaceInfo();
       }).fail(function(self,status,error){
          alert( error );
       });
   });

})(AJS.$ || jQuery);

