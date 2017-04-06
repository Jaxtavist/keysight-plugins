AJS.toInit(function(){
   
   AJS.$(".keysight-button").click(function(e){
      var $input = $(this)
      window.location.href = $input.attr( "href" );
   });

})
