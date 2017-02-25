// Taken from http://stackoverflow.com/questions/5357442/how-to-inspect-javascript-objects
// useage: alert( xinspect( thing ) );
function xinspect(o,i){
    if(typeof i=='undefined')i='';
    if(i.length>50)return '[MAX ITERATIONS]';
    var r=[];
    for(var p in o){
        var t=typeof o[p];
        r.push(i+'"'+p+'" ('+t+') => '+(t=='object' ? 'object:'+xinspect(o[p],i+'  ') : o[p]+''));
    }
    return r.join(i+'\n');
}

(function($) {
   var ActivateStringFields = function(){
   };

   // this is run before the information is displayed in the macro browser
   ActivateStringFields.prototype.beforeParamsSet = function( params, inserting ){
      return params;
   };

   // this is run before the information is saved from the macro browser
   ActivateStringFields.prototype.beforeParamsRetrieved = function( params ){
      return params;
   };

   // The parameter type needs to be defined in
   // com.atlassian.confluence.macro.browser.beans.MacroParamterType
   // string, boolean, username, enum, int, spacekey, relativedate, percentage
   // confluence-content, url, color, attachment, full_attachment, label,
   // date, group, cql
   //
   // param.aliases (object)
   // param.hidden (boolean)
   // param.defaultValue (object)
   // param.displayName (string)
   // param.multiple (boolean)
   // param.name (string)
   // param.options (object)
   //    param.options.showValueInPlaceholder (string)
   //    param.options.showNameInPlaceholder (string)
   // param.description (string)
   // param.type (object)
   //    param.type.name (string)
   // param.required (boolean)
   // param.enumValues (object)

   ActivateStringFields.prototype.fields = {
      "string" : function(param, options){
	 var paramDiv;
	 if( param.name == "equation" ){
            paramDiv = $(MacroBrowser.StringFields.textarea());
	 } else {
            paramDiv = $(MacroBrowser.StringFields.text());
	 }
         var input = $(":input", paramDiv);

         if( param.required) {
            input.keyup(AJS.MacroBrowser.processRequiredParameters);
         }

         return AJS.MacroBrowser.Field(paramDiv, input, options );
      },
   };
   AJS.MacroBrowser.activateStringFields = function(macroName) {
      AJS.MacroBrowser.setMacroJsOverride(macroName, new ActivateStringFields());
   }
})(AJS.$); 
      

