(function($) {
  var audioFileTypes = ["mp3", "ogg", "wav"];
  var videoFileTypes = ["mp4", "ogg", "webm"];
  var multimediaFileTypes = videoFileTypes.concat( audioFileTypes );
 
  AJS.MacroBrowser.activateSmartFieldsAttachmentsOnPage("html5-multimedia", multimediaFileTypes );
})(AJS.$);
