(function($) {
    var FaqMacroConfig = function() {};

    // this is used to insert the pre-determined values in the macro
    // into the auiSelect2 widget.
    FaqMacroConfig.prototype.beforeParamsRetrieved = function( params ){
        if( params.labels != null ){
            $("div.select2-container", $("#macro-param-div-labels")).auiSelect2("val", params.labels.split(",") );
        }
        return params;
    };

    FaqMacroConfig.prototype.fields = {
        "string" : {
            "labels" : function(param, options) {
               options = options || {};
               var paramDiv = $(Confluence.Templates.MacroBrowser.macroParameter());
               var input = $("input", paramDiv);
               if (param.required) {
                  input.keyup(MacroBrowser.processRequiredParameters);
               }

               input.auiSelect2(Confluence.UI.Components.LabelPicker.build({
                       separator: ",",
               }));

               return AJS.MacroBrowser.Field(paramDiv, input, options);
            },
            "spaceKeys" : function(param, options) {
               options = options || {};
               var paramDiv = $(Confluence.Templates.MacroBrowser.macroParameter());
               var input = $("input", paramDiv);
               if (param.required) {
                  input.keyup(MacroBrowser.processRequiredParameters);
               }
 
               /*
               input.addClass("autocomplete-space")
                    .attr("data-template", "{key}")
                    .attr("data-none-message", "Not found");
               input.bind("selected.autocomplete-content", function(e, data) {
                   if (options.onselect) {
                       options.onselect(data.selection);
                   }
                   else if(options.setValue) {
                       options.setValue(data.content.key);
                   }
                   else {
                       updateDependencies(param.name, options.dependencies, input.val());
                       (typeof options.onchange === "function") && options.onchange.apply(input);
                   }
               });
               AJS.Confluence.Binder.autocompleteSpace(paramDiv);
               */

               return AJS.MacroBrowser.Field(paramDiv, input, options);
            }
        }
    };

    AJS.MacroBrowser.Macros["faq"] = new FaqMacroConfig();

})(AJS.$);

