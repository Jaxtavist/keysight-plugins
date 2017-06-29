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
            "profile": function(param, options) {

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
            },
            "row-limit": function(param, options) {
                 var paramDiv = $(Confluence.Templates.MacroBrowser.macroParameter());
                 var input = $("input", paramDiv);
                 input.prop('disabled', true);
                 if (options == null) {
                    options = {};
                 }

                 getLimitsAsync(input, "rowLimit")

                 return new AJS.MacroBrowser.Field(paramDiv, input, options);
            },
            "time-limit": function(param, options) {
                 var paramDiv = $(Confluence.Templates.MacroBrowser.macroParameter());
                 var input = $("input", paramDiv);
                 input.prop('disabled', true);
                 if (options == null) {
                    options = {};
                 }

                 getLimitsAsync(input, "timeLimit")

                 return new AJS.MacroBrowser.Field(paramDiv, input, options);
            }
        }
    };

    AJS.MacroBrowser.Macros["database-query"] = new DBProfileConfig();

    /**
     * Populates an HTML Select element with the list of configured profiles.
     * Important note: previous profiles are not cleared. This means that the last profile used will still be active and shown,
     * even if the user doesn't have access to that profile in the settings.
     * @param dropDown JQuery selector for the HTML select element to be populated.
     */
    function bindAsyncDropDown(dropDown) {


        // Add a link if the user has access.
        AJS.$.ajax({
            async: true,
            url: apiUrl + "profiles/hasAccess",
            dataType: "json",
            timeout: 10000, // 10 seconds,
            error: function(xhr, textStatus, errorThrown) {
                console.error("Couldn't determine whether the current user has profile access, assuming no. ", errorThrown);
            },
            success: function(data) {
                if (data != false) {
                  var linkHtml = '<a id="profileMod" href="'+AJS.params.baseUrl+'/plugins/servlet/database/admin/profile">Modify Profiles</a>'
                  $(".edit-profile-link").html(linkHtml);
                  $(".edit-profile-link").before("<hr/>");
                }
            }
        });

        // Load the profiles from Confluence
        getProfilesAsync(function(profiles) {
                if (!profiles.length) {
                    AJS.log("Configured database profiles result was not in the expected format.");

                    // Placeholder in the dropdown
                    var infoOption = $("<option  selected=\"true\" disabled=\"disabled\"/>");
                    infoOption.text("-- No Available Profiles --");
                    infoOption.val("");
                    dropDown.append(infoOption);
                } else {
                    // List of profiles the user has access to.
                    profileList = profiles;

                    // Profile that was placed on the macro last time
                    var currentValue = dropDown.val();

                    // Placeholder in the dropdown
                    var infoOption = $("<option  selected=\"true\" disabled=\"disabled\"/>");
                    infoOption.text("-- Select a Profile --");
                    infoOption.val("");
                    dropDown.append(infoOption);

                    $.each(profiles, function(index, profile) {
                        if (profile.profileId == currentValue) {
                            var option = $('option[value="'+currentValue+'"]');
                            option.text(profile.profileName);
                        }
                        // Don't append a duplicate profile
                        if (profile.profileId != currentValue) {
                            var option = $("<option />");
                            option.val(profile.profileId);
                            option.text(profile.profileName);

                            dropDown.append(option);
                        }
                    });

                    // restore the currently selected value.
                    dropDown.val(currentValue);
                }
            },
            function(xhr, textStatus, errorThrown) {
                AJS.log("Failed to retrieve profiles: " + textStatus + " - " + errorThrown);
            });
    }

    function getLimitsAsync(element, key) {
        AJS.$.ajax({
             async: true,
             url: apiUrl + "limits",
             dataType: "json",
             timeout: 10000, // 10 seconds,
             error: function(xhr, textStatus, errorThrown) {
                 console.error("Couldn't get limits", errorThrown);
             },
             success: function(data) {
                 element.val(data[key]);
             }
         });
    }

    /**
     * Asynchronously retrieves the current set profiles that the active user has access to.
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