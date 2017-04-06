(function($) {
    /* I can not get the _renderItem to execute to add custom classes to the
     * list and I think it's Dangerous to muck with tihe .ui-menu classes. so
     * for now we'll just disable this
    
    var ApiDocumentationMacroConfig = function() {};

    ApiDocumentationMacroConfig.prototype.beforeParamsRetrieved = function( params ){
        var input;
        var inputElements = $("#macro-browser-dialog").find( "#macro-param-application" );
        if( inputElements.length > 0 ){
           input = $(inputElements[0]);
           input.addClass( "keysight-junk" );
           input.autocomplete({
              minLength: 1,
              source: function( request, response ){
                 $.ajax({
                    //url: "https://api.is.keysight.com/cgi-bin/org/of_apps/apiManager/apiManager.cgi",
                    url: "http://ofweb4.srs.is.keysight.com/cgi-bin/pers/selberg/of_apps/apiManager/apiManager.cgi",
                    jsonp: "jsonCallback",
                    dataType: "jsonp",
                    data: {
                      "GET_APPLICATION" : "true",
                      "text": input.val(),
                    },
                    success: function( data ){
                      //alert( "got " + data );
                      response( data );
                    },
                    fail: function(){
                      //alert( "failed" );
                    },
                 });
              },
              create: function() {
                 $(this).data('ui-autocomplete')._renderItem = function( ul, item ){
                    alert( "item create!" );
                    return $("<li>")
                       .addClass("please-work")
                       .attr('data-value',item.value)
                       .append(item.label)
                       .appendTo(ul);
                 };
              }
           });
        }
        return params;
    };

    AJS.MacroBrowser.Macros["api-documentation"] = new ApiDocumentationMacroConfig();
    */

})(AJS.$);

