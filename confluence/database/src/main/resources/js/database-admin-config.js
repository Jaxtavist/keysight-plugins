databaseConfigHelper = (function($) {

    var methods = new Object();
    var url = AJS.contextPath() + "/rest/database/1.0/admin-config/configuration";

    methods['saveJdbcDriverDirectory'] = function() {
        saveConfig();
        location.reload();
    }

    methods['saveConfigAuth'] = function() {
        saveConfig();
        location.reload();
    }

    methods['addGroup'] = function() {
        if (AJS.$("#group-list").length == 0) {
            AJS.$("#group-list-container").html("<ul id=\"group-list\"></ul>");
        }

        appendGroup(AJS.$("#new-group-name").val());
        AJS.$("#new-group-name").val("");

        saveConfig();
    }

    methods['deleteProfile'] = function(profileId) {
        $("#connection-profile-" + profileId).remove();
        saveConfig();
    }

    methods['updateProfile'] = function(profileId) {
        $("#view-profile-name-" + profileId).html($("#profile-name-" + profileId).val());
        $("#view-profile-description-" + profileId).html($("#profile-description-" + profileId).val());
        $("#view-authorized-users-" + profileId).html($("#authorized-users-" + profileId).val());
        $("#view-authorized-groups-" + profileId).html($("#authorized-groups-" + profileId).val());
        $("#view-database-type-" + profileId).html($("#database-type-" + profileId).val());
        $("#view-database-name-" + profileId).html($("#database-name-" + profileId).val());
        $("#view-database-port-" + profileId).html($("#database-port-" + profileId).val());
        $("#view-database-username-" + profileId).html($("#database-username-" + profileId).val());
        $("#view-database-password-" + profileId).html("*********");
        $("#view-connection-string-suffix-" + profileId).html($("#connection-string-suffix-" + profileId).val());
        $("#view-connection-string-" + profileId).html("jdbc:dbtype://server:port/db");
        saveConfig();
    }

    methods['addConnectionProfile'] = function(e) {
        var id = makeRandomViewId();
        AJS.$("#connection-profiles").append(Keysight.Database.Admin.Config.Templates.connectionProfile({
            id: id
        }));
        AJS.tabs.setup();
        AJS.tabs.change(jQuery('a[href="#edit-' + id + '"]'));
        bindButtons();
    }

    methods['removeEntry'] = function(e) {
        e.preventDefault();
        $(e.currentTarget).parent().remove();

        if (AJS.$(".saved-group").length == 0) {
            AJS.$("#group-list-container").html("<div class=\"none-saved\">No saved groups</div>");
        }

        if (AJS.$(".saved-address").length == 0) {
            AJS.$("#email-list-container").html("<div class=\"none-saved\">No saved addresses.</div>");
        }

        saveConfig();
    }

    function makeRandomViewId() {
        var size = 10000000;
        var number = Math.floor((Math.random() * size) + 1);
        while (idExists("view-" + number)) {
            number = Math.floor((Math.random() * size) + 1);
        }
        return number;
    }

    function idExists(id) {
        var flag = false;
        if ($("#" + id).length != 0) {
            flag = true;
        }
        return flag;
    }

    methods['loadConfig'] = function() {

        $.ajax({
            url: url,
            dataType: "json"
        }).done(function(pluginConfiguration) {
            if (pluginConfiguration.xml == null) {
                return;
            }

            // If XML doesn't validate, just give an empty document
            try {
                $xml = AJS.$(AJS.$.parseXML(decodeURIComponent(pluginConfiguration.xml)));
            } catch(err) {
                console.error("Failed to parse XML: ", err);
                $xml = AJS.$(AJS.$.parseXML(
                    "<plugin-configuration>\n"+
                    "    <jdbc-driver-directory></jdbc-driver-directory>\n"+
                    "    <saved-profiles></saved-profiles>\n"+
                    "</plugin-configuration>"
                ));
            }

            $("#jdbc-driver-directory").val(atob($xml.find("jdbc-driver-directory").html()));
            var configAuthorizedUsers = $xml.find("config-authorized-users").html();
            var configAuthorizedGroups = $xml.find("config-authorized-groups").html();
            // If we failed to get the fields from the xml, default to empty string;
            if (configAuthorizedUsers  == null) {
                configAuthorizedUsers = "";
            }
            if (configAuthorizedGroups  == null) {
                configAuthorizedGroups = "";
            }

            // Load saved connection profiles in and connect them to templates
            $xml.find("saved-profile").each(function(index) {
                var id = makeRandomViewId();
                var profileName = atob($(this).find("profile-name").html());
                var profileDescription = atob($(this).find("profile-description").html());
                var authorizedUsers = atob($(this).find("authorized-users").html());
                var authorizedGroups = atob($(this).find("authorized-groups").html());
                var databaseType = atob($(this).find("database-type").html());
                var databaseName = atob($(this).find("database-name").html());
                var databaseServer = atob($(this).find("database-server").html());
                var databasePort = atob($(this).find("database-port").html());
                var databaseUsername = atob($(this).find("database-username").html());
                var databasePassword = atob($(this).find("database-password").html());
                var connectionStringSuffix = atob($(this).find("connection-string-suffix").html());
                var db2Selected = "";
                var derbySelected = "";
                var microsoftSqlServerSelected = "";
                var mongoDbSelected = "";
                var mySqlSelected = "";
                var oracleSelected = "";
                var postgresSqlSelected = "";
                var sybaseSelected = "";

                if (databaseType == "DB2") {
                    db2Selected = "selected";
                } else if (databaseType == "Derby") {
                    derbySelected = "selected";
                } else if (databaseType == "Microsoft SQL Server") {
                    microsoftSqlServerSelected = "selected";
                } else if (databaseType == "MongoDB") {
                    mongoDbSelected = "selected";
                } else if (databaseType == "MySQL") {
                    mySqlSelected = "selected";
                } else if (databaseType == "Oracle") {
                    oracleSelected = "selected";
                } else if (databaseType == "PostgresSQL") {
                    postgresSqlSelected = "selected";
                } else if (databaseType == "Sybase") {
                    sybaseSelected = "selected";
                }

                AJS.$("#connection-profiles").append(Keysight.Database.Admin.Config.Templates.connectionProfile({
                    id: id,
                    profileName: profileName,
                    profileDescription: profileDescription,
                    authorizedUsers: authorizedUsers,
                    authorizedGroups: authorizedGroups,
                    databaseType: databaseType,
                    databaseName: databaseName,
                    databaseServer: databaseServer,
                    databasePort: databasePort,
                    databaseUsername: databaseUsername,
                    databasePassword: databasePassword,
                    connectionStringSuffix: connectionStringSuffix,
                    db2Selected: db2Selected,
                    derbySelected: derbySelected,
                    microsoftSqlServerSelected: microsoftSqlServerSelected,
                    mongoDbSelected: mongoDbSelected,
                    mySqlSelected: mySqlSelected,
                    oracleSelected: oracleSelected,
                    postgresSqlSelected: postgresSqlSelected,
                    sybaseSelected: sybaseSelected,
                }));
            });

            // Load access profiles for this page.
            AJS.$("#configuration-permissions").append(Keysight.Database.Admin.Config.Templates.accessProfile({
                configAuthorizedUsers: configAuthorizedUsers,
                configAuthorizedGroups: configAuthorizedGroups
            }));

            AJS.$("#save-config-auth").click(function(e) {
                e.preventDefault();
                databaseConfigHelper.saveConfigAuth();
            });

            AJS.tabs.setup();
            bindButtons();
        }).fail(function(self, status, error) {
            alert(error);
        });
    }

    function saveConfig() {
        var savedProfiles = new Array();

        AJS.$(".edit-connection-profile").each(function(index) {
            var profileId = $(this).attr("connection-profile-id");

            savedProfiles.push("<saved-profile>" +
                "<profile-name>" + btoa($("#profile-name-" + profileId).val()) + "</profile-name>" +
                "<profile-description>" + btoa($("#profile-description-" + profileId).val()) + "</profile-description>" +
                "<authorized-users>" + btoa($("#authorized-users-" + profileId).val()) + "</authorized-users>" +
                "<authorized-groups>" + btoa($("#authorized-groups-" + profileId).val()) + "</authorized-groups>" +
                "<database-type>" + btoa($("#database-type-" + profileId + " option:selected").text()) + "</database-type>" +
                "<database-name>" + btoa($("#database-name-" + profileId).val()) + "</database-name>" +
                "<database-server>" + btoa($("#database-server-" + profileId).val()) + "</database-server>" +
                "<database-port>" + btoa($("#database-port-" + profileId).val()) + "</database-port>" +
                "<database-username>" + btoa($("#database-username-" + profileId).val()) + "</database-username>" +
                "<database-password>" + btoa($("#database-password-" + profileId).val()) + "</database-password>" +
                "<connection-string-suffix>" + btoa($("#connection-string-suffix-" + profileId).val()) + "</connection-string-suffix>" +
                '</saved-profile>');
        });


        var xmlString = '<?xml version="1.0" encoding="UTF-8"?>' + "\n" +
            '<plugin-configuration>' + "\n" +
            '   <config-authorized-users>' + $("#config-authorized-users").val() + "</config-authorized-users>\n" +
            '   <config-authorized-groups>' + $("#config-authorized-groups").val() + "</config-authorized-groups>\n" +
            '   <jdbc-driver-directory>' + btoa($("#jdbc-driver-directory").val()) + "</jdbc-driver-directory>\n" +
            '   <saved-profiles>' + "\n" +
            savedProfiles.join("\n") +
            '   </saved-profiles>' + "\n" +
            '</plugin-configuration>' + "\n";

        // Validate XML
        try {
            AJS.$(AJS.$.parseXML(xmlString))
        } catch(err) {
            console.error("Malformed XML!: ", err);
            return;
        }

        AJS.$.ajax({
            url: url,
            type: "PUT",
            contentType: "application/json",
            data: '{"xml":"' + encodeURIComponent(xmlString) + '"}',
            processData: false
        }).done(function() {
            alert("Config Saved");
        }).fail(function(self, status, error) {
            alert(error);
        });
    }

    function bindButtons() {
        AJS.$(".update-profile-button").unbind("click");
        AJS.$(".update-profile-button").click(function(e) {
            e.preventDefault();
            databaseConfigHelper.updateProfile($(e.target).attr("connection-profile"));
        });

        AJS.$(".delete-profile-button").unbind("click");
        AJS.$(".delete-profile-button").click(function(e) {
            e.preventDefault();
            databaseConfigHelper.deleteProfile($(e.target).attr("connection-profile"));
        });
    }

    function appendGroup(groupName) {
        AJS.$("#group-list").append("<li>" +
            "<span class=\"saved-group\">" + groupName + "</span>" + "&nbsp;<a href=\"#\" onclick=\"confluenceSupportConfigHelper.removeEntry(event)\">[remove]</a>" +
            "</li>\n");
    }

    return methods;
})(AJS.$ || jQuery);

AJS.toInit(function() {

    AJS.$("#add-group").click(function(e) {
        e.preventDefault();
        //confluenceSupportConfigHelper.addGroup();
    });

    AJS.$("#save-jdbc-driver-directory").click(function(e) {
        e.preventDefault();
        databaseConfigHelper.saveJdbcDriverDirectory();
    });

    AJS.$("#add-connection-profile").click(function(e) {
        e.preventDefault();
        databaseConfigHelper.addConnectionProfile();
    });

    AJS.$(".update-profile-button").unbind("click");
    AJS.$(".update-profile-button").click(function(e) {
        e.preventDefault();
        databaseConfigHelper.updateProfile($(e.target).attr("connection-profile"));
    });

    databaseConfigHelper.loadConfig();
});