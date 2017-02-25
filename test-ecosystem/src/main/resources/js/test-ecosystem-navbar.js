AJS.toInit(function(){
   
   AJS.$(".test-ecosystem-button").click(function(e){
      var $input = $(this)
      window.location.href = $input.attr( "href" );
   });

})
