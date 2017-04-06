AJS.toInit(function($) { 
    var PLUGIN_KEY = "com.keysight.glossary:show-glossary-container";
    Confluence && Confluence.HighlightAction && Confluence.HighlightAction.registerButtonHandler( PLUGIN_KEY, {
         onClick: function(selectionObject) { Confluence.KeysightGlossaryDialogs.showKeysightGlossaryDialog(selectionObject); },
         shouldDisplay: Confluence.HighlightAction.WORKING_AREA.MAINCONTENT_ONLY
    });
});

function requestTerm( text, divId ){
   var url = AJS.Data.get( "base-url" ) + "/rest/glossary/1.0/request";
   var xmlhttp;
   if( window.XMLHttpRequest ){
      xmlhttp = new XMLHttpRequest();
   } else {
      xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
   }

   xmlhttp.onreadystatechange=function()
   {
      if( xmlhttp.readyState==4 && xmlhttp.status==200 ){
         var htmlResponse = JSON.parse(xmlhttp.response)["message-body"];
         document.getElementById(divId).innerHTML=htmlResponse;
      }
   }

   var username  = encodeURIComponent( AJS.Data.get( "remote-user" ) );
   var pageTitle = encodeURIComponent( AJS.Data.get( "page-title"  ) );
   var spaceKey  = encodeURIComponent( AJS.Data.get( "space-key"   ) );

   url +="?spaceKey="+spaceKey
        +"&pageTitle="+pageTitle
        +"&term="+$.trim(text)
        +"&username="+username;
   xmlhttp.open( "GET", url, true );
   xmlhttp.setRequestHeader("Accept", "application/json" );
   xmlhttp.send( );
}

function getDefinition( text, divId ){
   var url = AJS.Data.get( "base-url" ) + "/rest/glossary/1.0/term";
   var xmlhttp;
   if( window.XMLHttpRequest ){
      xmlhttp = new XMLHttpRequest();
   } else {
      xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
   }

   xmlhttp.onreadystatechange=function()
   {
      if( xmlhttp.readyState==4 && xmlhttp.status==200 ){
         var htmlResponse = JSON.parse(xmlhttp.response)["message-body"];
         document.getElementById(divId).innerHTML=htmlResponse;
      }
   }

   //var labels = new Array();
   //$( "li.aui-label" ).find( "a" ).each( function(){ labels.push( $( this ).text() ) } );
   //var joinedLabels = encodeURIComponent(labels.join());
   var username  = encodeURIComponent( AJS.Data.get( "remote-user" ) );
   var pageTitle = encodeURIComponent( AJS.Data.get( "page-title"  ) );
   var spaceKey  = encodeURIComponent( AJS.Data.get( "space-key"   ) );

   url +="?spaceKey="+spaceKey
        +"&pageTitle="+pageTitle
        +"&term="+$.trim(text)
        +"&username="+username;
   xmlhttp.open( "GET", url, true );
   xmlhttp.setRequestHeader("Accept", "application/json" );
   xmlhttp.send();
}

Confluence.KeysightGlossaryDialogs = Confluence.KeysightGlossayDialogs || (function($) {

    var DIALOG_MAX_HEIGHT = 200;
    var DIALOG_WIDTH = 300;
    var keysightGlossaryDialog;
    var defaultDialogOptions = {
        hideDelay: null,
        width : DIALOG_WIDTH,
        maxHeight: DIALOG_MAX_HEIGHT
    };

    function showKeysightGlossaryDialog(selectionObject) {
        keysightGlossaryDialog && keysightGlossaryDialog.remove();
        var displayFn = function(content, trigger, showPopup) {
            $(content).html( "<div id=\"keysight-definition\"></div>" );
            getDefinition( selectionObject.text, "keysight-definition" );
            //$(content).html(Keysight.Glossary.Soy.Templates.glossaryContainer(
                //{
                    //text: definition
                //}
            //));
            showPopup();
            return false;
        };
        keysightGlossaryDialog = _openDialog(selectionObject, 'keysight-glossary-dialog', defaultDialogOptions, displayFn);
    };

    function _openDialog(selectionObject, id, options, displayFn) {
        var $target = $("<div>");
        _appendDialogTarget(selectionObject.area.average, $target);
        var originalCallback = options.hideCallback;
        options.hideCallback = function() {
            $target.remove(); // clean up dialog target element when hiding the dialog
            originalCallback && originalCallback();
        };
        var dialog = Confluence.ScrollingInlineDialog($target, id, displayFn, options);
        dialog.show();
        return dialog;
    };

    function _appendDialogTarget(targetDimensions, $target) {
        Confluence.DocThemeUtils.appendAbsolutePositionedElement($target);
        $target.css({
            top: targetDimensions.top,
            height: targetDimensions.height,
            left: targetDimensions.left,
            width: targetDimensions.width,
            "z-index": -9999,
            position: 'absolute'
        });
    };

    return {
        showKeysightGlossaryDialog: showKeysightGlossaryDialog
    };
})(AJS.$);
