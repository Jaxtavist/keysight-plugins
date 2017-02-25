(function ($) {

    var NHConfig = function () {
        this.headingFields = ["h1", "h2", "h3", "h4", "h5", "h6"];
    };

    NHConfig.prototype.beforeParamsSet = function (selectedParams, macroSelected) {
        var selectedFormat = selectedParams["number-format"];

        if (selectedFormat !== "custom") {
            for (var i = 0; i < this.headingFields.length; i++) {
                AJS.$("#macro-param-div-" + this.headingFields[i]).hide();
            }
        }

        AJS.$.ajax({
            url: AJS.contextPath() + "/drest/numberedheadings/1.0/formatter",
            type: "GET",
            dataType: "json",
            success: function (msg) {
                var numberFormatInputField = AJS.$("#macro-param-number-format");
                numberFormatInputField.empty();

                for (var i = 0; i < msg.length; i++) {
                    var option = $("<option>" + msg[i] + "</option>");

                    if (selectedFormat === msg[i]) {
                        option.attr("selected", "selected");
                    }

                    numberFormatInputField.append(option);
                }
            }
        });

        return selectedParams;
    };

    NHConfig.prototype.fields = {
        "string": function (param) {
            var field = AJS.MacroBrowser.ParameterFields["string"](param);

            field.dependencyUpdated = function (dependencyName, dependencyValue) {
                if (dependencyName === "number-format") {
                    if (dependencyValue === "custom") {
                        this.paramDiv.show();
                    }
                    else {
                        this.paramDiv.hide();
                    }
                }
            };

            return field;
        },

        "enum": function (param) {
            if (param.name === "number-format") {
                var options = {
                    dependencies: this.headingFields
                };
            }

            return AJS.MacroBrowser.ParameterFields["enum"](param, options);
        }
    };

    // Newer versions of Confluence (don't know the exact version, but it is at least 3.4+) should use the "setMacroJsOverride" method,
    // older versions should use the "macros" object (which is deprecated in newer versions).
    if (typeof AJS.MacroBrowser.setMacroJsOverride === "function") {
        AJS.MacroBrowser.setMacroJsOverride("numberedheadings", new NHConfig());
        AJS.MacroBrowser.setMacroJsOverride("numbered-headings", new NHConfig());
        AJS.MacroBrowser.setMacroJsOverride("numbered-headings-for-page", new NHConfig());
        AJS.MacroBrowser.setMacroJsOverride("renumber-and-include-page", new NHConfig());
        AJS.MacroBrowser.setMacroJsOverride("renumber-and-include-children", new NHConfig());
    } else {
        AJS.MacroBrowser.Macros = {"numberedheadings": new NHConfig()};
        AJS.MacroBrowser.Macros = {"numbered-headings": new NHConfig()};
        AJS.MacroBrowser.Macros = {"numbered-headings-for-page": new NHConfig()};
        AJS.MacroBrowser.Macros = {"renumber-and-include-page": new NHConfig()};
        AJS.MacroBrowser.Macros = {"renumber-and-include-children": new NHConfig()};
    }

    if (typeof AJS.MacroBrowser.ParameterFields["updateDependencies"] !== "function") {
        AJS.MacroBrowser.ParameterFields["updateDependencies"] = function (paramName, dependencies, value) {
            if (dependencies && dependencies.length) {
                for (var i = 0, length = dependencies.length; i < length; i++) {
                    AJS.MacroBrowser.fields[dependencies[i]].dependencyUpdated(paramName, value);
                }
            }
        }
    }

    AJS.MacroBrowser.ParameterFields["enum"] = function (param, options) {
        if (param.multiple) {
            return AJS.MacroBrowser.ParameterFields["string"](param, options);
        }

        options = options || {};

        var paramDiv = AJS.clone("#macro-param-select-template");

        if (paramDiv.length === 0) {
            paramDiv = $(Confluence.Templates.MacroBrowser.macroParameterSelect());
        }

        var input = $("select", paramDiv);

        if (!(param.required || param.defaultValue)) {
            input.append(AJS.$("<option/>").attr("value", ""));
        }

        $(param.enumValues).each(function () {
            input.append(AJS.$("<option/>").attr("value", this).html("" + this));
        });

        options.onchange = options.onchange || function (e) {
            var val = input.val();
            AJS.MacroBrowser.ParameterFields["updateDependencies"](param.name, options.dependencies, val);
        };

        return AJS.MacroBrowser.Field(paramDiv, input, options);
    }

})(AJS.$);
