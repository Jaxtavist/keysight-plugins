AJS.toInit(function(){
   AJS.$(".keysight-table-width").each( function( index ){
      var divs = $(this);
      var row          = divs.attr( 'row' );
      var tableWidth   = divs.attr( 'table-width' );
      var columnWidths = divs.attr( 'column-widths' ).split( "," );

      if( tableWidth != "" ){
         divs.find( 'table' ).eq( 0 ).attr("width", tableWidth);
      }

      divs.find( 'tr' ).eq( row - 1 ).find( 'th','td' ).each( function( index2 ){
         if( index2 < columnWidths.length && columnWidths[index2] != "" ){
            $(this).attr( "width", columnWidths[ index2 ] );
         }
      });
   });
})
