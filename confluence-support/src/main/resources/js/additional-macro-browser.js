(function($) {
   var MacroJsAddOrOverrideFieldTypes = function(){
   };

   // this is run before the information is displayed in the macro browser
   MacroJsAddOrOverrideFieldTypes.prototype.beforeParamsSet = function( params, inserting ){
      //params.price = "today! today!";
      //AJS.$('#macro-param-dnqa').datePicker({'overrideBrowserDefault': true});
      AJS.$('macro-param-price').onchange = function(){ alert( "changed price" ); };
      return params;
   };

   // this is run before the information is saved from the macro browser
   MacroJsAddOrOverrideFieldTypes.prototype.beforeParamsRetrieved = function( params ){
      //params.price = "tomorrow! tomorrow!";
      return params;
   };

   // The parameter type needs to be defined in
   // com.atlassian.confluence.macro.browser.beans.MacroParamterType
   // string, boolean, username, enum, int, spacekey, relativedate, percentage
   // confluence-content, url, color, attachment, full_attachment, label,
   // date, group, cql
   MacroJsAddOrOverrideFieldTypes.prototype.fields = {
      "date" : function(param, options){
         var paramDiv = $(MacroBrowser.AdditionalFieldTypes.macroParameterDate());
         var input = $("input", paramDiv);
         input.datePicker({'overrideBrowserDefault': true});

         if( param.required) {
            input.keyup(AJS.MacroBrowser.processRequiredParameters);
         }

         return AJS.MacroBrowser.Field(paramDiv, input, options );
      },
      "int" : function(param, options){
         var paramDiv = $(MacroBrowser.AdditionalFieldTypes.macroParameterInteger());
         var input = $("input", paramDiv);
         input.change( function(){ 
            var isInt = /^\d+$/;
            if( !( $(this).val() == "" || isInt.test( $(this).val() ) ) ){
               alert( "An integer value is required\n" );
               $(this).val( "" );
            }
         });
         if( param.required) {
            input.keyup(AJS.MacroBrowser.processRequiredParameters);
         }

         return AJS.MacroBrowser.Field(paramDiv, input, options );
      },
      "float" : function(param, options){
         var paramDiv = $(MacroBrowser.AdditionalFieldTypes.macroParameterFloat());
         var input = $("input", paramDiv);

         if( param.required) {
            input.keyup(AJS.MacroBrowser.processRequiredParameters);
         }

         return AJS.MacroBrowser.Field(paramDiv, input, options );
      }
   };
   AJS.MacroBrowser.activateAdditionalMacroBrowserFieldTypes = function(macroName) {
      //alert( "Activate additional macro fields for " + macroName );
      AJS.MacroBrowser.setMacroJsOverride(macroName, new MacroJsAddOrOverrideFieldTypes());
   }
})(AJS.$); 
      
