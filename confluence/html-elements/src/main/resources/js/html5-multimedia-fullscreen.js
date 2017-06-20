AJS.toInit(function(){
   AJS.$(".html5-fullscreen-button").each( function( index ){

      $(this).off("click");
      $(this).on("click", function() {

         var video = $(this).parents(".html5-multimedia-container").children("video").get(0)

         if (video.requestFullscreen) {
           video.requestFullscreen();
         } else if (video.mozRequestFullScreen) {
           video.mozRequestFullScreen(); // Firefox
         } else if (video.webkitRequestFullscreen) {
           video.webkitRequestFullscreen(); // Chrome and Safari
         }

      });
   });
});
