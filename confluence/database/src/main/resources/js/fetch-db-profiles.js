// This closure helps us keep our variables to ourselves.
// This pattern is known as "iife" - immediately invoked function expression
// Start closure
var dbProfiles = (function($) {
    // module variables
    var methods = new Object();
    var apiUrl = AJS.Data.get("base-url") + "/rest/database/1.0/config/";
    var profileList = [];

    var DBProfileConfig = function() {};

    DBProfileConfig.prototype.fields = {
        "string": {
            "Profile": function(param, options) {

                var paramDiv = $(Confluence.Templates.MacroBrowser.macroParameterSelect());
                var input = $("select", paramDiv);

                // we need to do some trickery when the value is first set, and when the value is subsequently changed,
                // so set this up.
                if (options == null) {
                    options = {};
                }
                options.setValue = function(value) {
                    var targetOption = input.find("option[value='" + value + "']");
                    if (targetOption.length == 0) {
                        var option = $("<option/>");
                        option.val(value);
                        option.text(value);
                        input.append(option);
                    }
                };

                bindAsyncDropDown(input);
                return new AJS.MacroBrowser.Field(paramDiv, input, options);
            }
        }
    };

    AJS.MacroBrowser.Macros["database-query"] = new DBProfileConfig();

    /**
     * Populates an HTML Select element with the list of configured syntax highlighters.
     * @param dropDown JQuery selector for the HTML select element to be populated.
     */
    function bindAsyncDropDown(dropDown) {
        // Load the profiles from Confluence
        getProfilesAsync(function(profiles) {
                if (!profiles.length) {
                    AJS.log("Configured database profiles result was not in the expected format.");
                    return;
                }

                profileList = profiles;

                var currentValue = dropDown.val();

                dropDown.empty();

                $.each(profiles, function(index, profile) {
                    var option = $("<option />");
                    option.val(profile.profileName);
                    option.text(profile.profileName);

                    dropDown.append(option);
                });

                // restore the currently selected value.
                dropDown.val(currentValue);
            },
            function(xhr, textStatus, errorThrown) {
                AJS.log("Failed to retrieve profiles: " + textStatus + " - " + errorThrown);
            });
    }

    /**
     * Asynchronously retrieves the current set of syntax highlighters currently installed for the Code macro.
     *
     * @param successHandler Callback to invoke if the retrieval is successful
     * @param errorHandler Callback to invoke if the retrieval fails.
     *
     * Example Response:
     *     [{"profileName":"world","profileDescription":"","authorizedUsers":"admin","authorizedGroups":"confluence-administrators",
     *     "databaseType":"MySQL","databaseName":"world","databaseServer":"127.0.0.1","databasePort":"3306","databaseUsername":"root",
     *     "databasePassword":"admin","connectionStringSuffix":""}]
     *
    */
    function getProfilesAsync(successHandler, errorHandler) {
        AJS.$.ajax({
            async: true,
            url: apiUrl + "profiles",
            dataType: "json",
            timeout: 10000, // 10 seconds,
            error: function(xhr, textStatus, errorThrown) {
                if (errorHandler && typeof(errorHandler) == "function") {
                    errorHandler(xhr, textStatus, errorThrown);
                }
            },
            success: function(data) {
                if (successHandler && typeof(successHandler) == "function") {
                    successHandler(data);
                }
            }
        });
    }

    // return the object with the methods
    return methods;

    // End closure
})(AJS.$ || jQuery);