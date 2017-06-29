profilesConfigHelper = (function($) {

    var methods = new Object();
    var url = AJS.contextPath() + "/rest/database/1.0/admin-config/profiles";
    // This dictates whether the stored xml is still valid.
    // Follows the Semver standard (First digit indicates breaking change).
    var schemaVersionString = "1.1.0";
    var schemaVersionArr = schemaVersionString.split(".");

    methods['deleteProfile'] = function(profileId) {
        $("#connection-profile-" + profileId).remove();
        saveConfig();
    }

    methods['testProfile'] = function(profileId) {
        var profile = {
            "profileName": ($("#profile-name-" + profileId).val()),
            "profileId": ($("#profile-id-" + profileId).val()),
            "profileDescription": ($("#profile-description-" + profileId).val()),
            "authorizedUsers": ($("#authorized-users-" + profileId).val()),
            "authorizedGroups": ($("#authorized-groups-" + profileId).val()),
            "databaseType": ($("#database-type-" + profileId).val()),
            "databaseName": ($("#database-name-" + profileId).val()),
            "databaseServer": ($("#database-server-" + profileId).val()),
            "databasePort": ($("#database-port-" + profileId).val()),
            "databaseUsername": ($("#database-username-" + profileId).val()),
            "databasePassword": ($("#database-password-" + profileId).val()),
            "connectionStringSuffix": ($("#connection-string-suffix-" + profileId).val())
        }

        AJS.$.ajax({
            url: url + "/test/",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(profile),
            processData: false
          }).done(function(res) {
              if (res == true) {
                  AJS.messages.success({
                      title: 'Connection test succeeded.',
                      body: '<p>Connection was established with specified database.</p>',
                      fadeout: true,
                      delay: 3000
                  });
              } else {
                  AJS.messages.error({
                      title: 'Connection test failed.',
                      body: '<p>Couldn\'t connect for an unknown reason.</p>',
                      fadeout: true,
                      delay: 4000
                  });
              }
          })
          .fail(function(self, status, error) {
              AJS.messages.warning({
                  title: 'Connection test failed.',
                  body: '<p>' + self.responseText + '</p>'
              });
          });
    }

    methods['updateProfile'] = function(profileId) {
        $("#view-profile-name-" + profileId).html($("#profile-name-" + profileId).val());
        $("#view-profile-id-" + profileId).html($("#profile-id-" + profileId).val());
        $("#view-profile-description-" + profileId).html($("#profile-description-" + profileId).val());
        $("#view-authorized-users-" + profileId).html($("#authorized-users-" + profileId).val());
        $("#view-authorized-groups-" + profileId).html($("#authorized-groups-" + profileId).val());
        $("#view-database-type-" + profileId).html($("#database-type-" + profileId).val());
        $("#view-database-name-" + profileId).html($("#database-name-" + profileId).val());
        $("#view-database-server-" + profileId).html($("#database-server-" + profileId).val());
        $("#view-database-port-" + profileId).html($("#database-port-" + profileId).val());
        $("#view-database-username-" + profileId).html($("#database-username-" + profileId).val());
        $("#view-database-password-" + profileId).html("*********");
        $("#view-connection-string-suffix-" + profileId).html($("#connection-string-suffix-" + profileId).val());
        $("#view-connection-string-" + profileId).html(generateConnectionString(profileId));
        saveConfig(profileId);
    }

    function generateConnectionString(profileId) {
        var connectionString = "";

        if ($("#database-type-" + profileId).val() != null &&
            $("#database-server-" + profileId).val() != null &&
            $("#database-name-" + profileId).val() != null) {
            connectionString = "jdbc:"
            connectionString += getDatabaseProtocolName($("#database-type-" + profileId).val()) + "://";
            connectionString += $("#database-server-" + profileId).val();

            if (isNumber($("#database-port-" + profileId).val())) {
                connectionString += $("#database-port-" + profileId).val() + "/";
            }

            connectionString += $("#database-name-" + profileId).val();

            if ($("#database-connection-string-suffix" + profileId).val() != null) {
                connectionString += "?" + $("#database-connection-string-suffix" + profileId).val();
            }
        }
        return connectionString;
    }

    function getDatabaseProtocolName(databaseType) {
        var protocolName = databaseType.toLowerCase();
        if (databaseType === "mongodb") {
            protocolName = "mongo";
        } else if (databaseType === "microsoft sql server") {
            protocolName = "sqlserver";
        }
        return protocolName;
    }

    function isNumber(n) {
        return !isNaN(parseFloat(n)) && isFinite(n);
    }

    methods['addConnectionProfile'] = function(e) {
        var id = makeRandomViewId();
        var profileName = "";
        var profileId = id;
        var profileDescription = "";
        var authorizedUsers = "";
        var authorizedGroups = "";
        var databaseType = "";
        var databaseName = "";
        var databaseServer = "";
        var databasePort = "";
        var databaseUsername = "";
        var databasePassword = "";
        var connectionStringSuffix = "";
        var db2Selected = "";
        var derbySelected = "";
        var microsoftSqlServerSelected = "";
        var mongoDbSelected = "";
        var mySqlSelected = "";
        var oracleSelected = "";
        var postgresSqlSelected = "";
        var sybaseSelected = "";
        var connectionString = "";

        AJS.$("#connection-profiles").append(Keysight.Database.Admin.Config.Templates.connectionProfile({
            id: id,
            profileId: profileId,
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
            connectionString: connectionString,
        }));

        AJS.tabs.setup();
        AJS.tabs.change(jQuery('a[href="#edit-' + id + '"]'));
        bindButtons();
    }

    methods['removeEntry'] = function(e) {
        e.preventDefault();
        $(e.currentTarget).parent().remove();
        saveConfig();
    }


    methods['loadConfig'] = function() {

        $.ajax({
                url: url,
                dataType: "json"
            }).done(function(pluginConfiguration) {
                if (pluginConfiguration.xml == null) {
                    return;
                }

                var jdbcUrl = AJS.contextPath() + "/rest/database/1.0/admin-config/get-installed-drivers";


                try {
                    $xml = AJS.$(AJS.$.parseXML(decodeURIComponent(pluginConfiguration.xml)));
                } catch (err) {
                    console.error("Failed to parse XML: ", err);
                    $xml = AJS.$(AJS.$.parseXML(
                        "<plugin-configuration>\n" +
                        "    <saved-profiles></saved-profiles>\n" +
                        "</plugin-configuration>"
                    ));
                }

                // Check for breaking changes in the schema
                // No breaking change occured when we added the schema version, so if one doesn't exist the xml
                // is still valid.
                if ($xml.find("schema-version").length != 0) {
                    var xmlVersion = $xml.find("schema-version").html();
                    var xmlMajorVer = xmlVersion.split(".")[0];
                    if (xmlMajorVer != schemaVersionArr[0]) {
                        $xml = AJS.$(AJS.$.parseXML(
                            "<plugin-configuration>\n" +
                            "    <saved-profiles></saved-profiles>\n" +
                            "</plugin-configuration>"
                        ));

                        AJS.messages.error({
                            title: 'Error fetching profiles.',
                            body: '<p>The stored profiles data is no longer compatible with this version of the plugin.</p>' +
                                '<p>If you save on this page, all old profiles will be lost. To preserve them, roll back the plugin ' +
                                'version and then make a copy of the profile information, and then reenter it after upgrading.</p>'
                        });
                    }
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
                    var databasePassword = "*****";
                    var connectionStringSuffix = atob($(this).find("connection-string-suffix").html());
                    var db2Selected = "";
                    var derbySelected = "";
                    var microsoftSqlServerSelected = "";
                    var mongoDbSelected = "";
                    var mySqlSelected = "";
                    var oracleSelected = "";
                    var postgresSqlSelected = "";
                    var sybaseSelected = "";

                    var profileId;
                    if ($(this).find("profile-id").length == 0) {
                        profileId = id;
                    } else {
                        profileId = atob($(this).find("profile-id").html());
                    }

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

                    var subProtocol = databaseType.toLowerCase();

                    if (subProtocol == "microsoft sql server") {
                        subProtocol = "sqlserver";
                    } else if (subProtocol == "mongodb") {
                        subProtocol = "mongo";
                    }

                    if (subProtocol != null &&
                        databaseServer != null &&
                        databaseName != null) {
                        connectionString = "jdbc:"
                        connectionString += getDatabaseProtocolName(subProtocol) + "://";
                        connectionString += databaseServer;

                        if (isNumber(databasePort)) {
                            connectionString += databasePort + "/";
                        }

                        connectionString += databaseName;

                        if (connectionStringSuffix) {
                            connectionString += "?" + connectionStringSuffix;
                        }
                    }

                    AJS.$("#connection-profiles").append(Keysight.Database.Admin.Config.Templates.connectionProfile({
                        id: id,
                        profileName: profileName,
                        profileId: profileId,
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
                        connectionString: connectionString,
                    }));
                });


                AJS.$("#save-config-auth").click(function(e) {
                    e.preventDefault();
                    profilesConfigHelper.saveConfigAuth();
                });

                AJS.tabs.setup();
                bindButtons();
            })
            .fail(function(self, status, error) {
                console.error(error);
                AJS.messages.error({
                    title: 'Error fetching profiles.',
                    body: 'Internal Error: Please contact your Confluence administrators.'
                });
            });
    }

    function saveConfig(savedId) {
        var savedProfiles = new Array();
        var profileAuth = {};
        var profileName = "";

        try {
            AJS.$(".edit-connection-profile").each(function(index) {
                var profileId = $(this).attr("connection-profile-id");

                // If password hasn't changed, don't send it
                // Only update the password for the one we changed.
                if (profileId == savedId) {
                    var password = $("#database-password-" + profileId).val();
                    if (password != "*****") {
                        profileAuth[$("#view-profile-id-" + profileId).html()] = password;
                    }
                }

                // If profile has the same name as a previous profile, die and show error.
                var newProfileName = $("#view-profile-name-" + profileId).html();
                if (newProfileName == profileName) {
                    throw "Canceling Save";
                } else {
                    profileName = newProfileName;
                }

                // We look at the #view-... tags here because only the profile we hit save on will update the view page
                // The others should be unchanged even if the user entered data.
                savedProfiles.push("<saved-profile>" +
                        "<profile-name>" + btoa($("#view-profile-name-" + profileId).html()) + "</profile-name>" +
                        "<profile-id>" + btoa($("#view-profile-id-" + profileId).html()) + "</profile-id>" +
                        "<profile-description>" + btoa($("#view-profile-description-" + profileId).html()) + "</profile-description>" +
                        "<authorized-users>" + btoa($("#view-authorized-users-" + profileId).html()) + "</authorized-users>" +
                        "<authorized-groups>" + btoa($("#view-authorized-groups-" + profileId).html()) + "</authorized-groups>" +
                        "<database-type>" + btoa($("#view-database-type-" + profileId).html()) + "</database-type>" +
                        "<database-name>" + btoa($("#view-database-name-" + profileId).html()) + "</database-name>" +
                        "<database-server>" + btoa($("#view-database-server-" + profileId).html()) + "</database-server>" +
                        "<database-port>" + btoa($("#view-database-port-" + profileId).html()) + "</database-port>" +
                        "<database-username>" + btoa($("#view-database-username-" + profileId).html()) + "</database-username>" +
                        "<database-password>" + "*****" + "</database-password>" +
                        "<connection-string-suffix>" + btoa($("#view-connection-string-suffix-" + profileId).html()) + "</connection-string-suffix>" +
                    '</saved-profile>');
            });
        } catch (err) {
            AJS.messages.error({
                title: 'Error saving profiles.',
                body: '<p> Two profiles cannot share the same name. Please change the name of one of your profiles.</p>',
                fadeout: true
            });

            return;
        }


        var xmlString = '<?xml version="1.0" encoding="UTF-8"?>' + "\n" +
            '<plugin-configuration>' + "\n" +
            '<schema-version>' + schemaVersionString + '</schema-version>' + "\n" +
            '   <saved-profiles>' + "\n" +
            savedProfiles.join("\n") +
            '   </saved-profiles>' + "\n" +
            '</plugin-configuration>' + "\n";

        // Validate XML
        try {
            AJS.$(AJS.$.parseXML(xmlString))
        } catch (err) {
            AJS.messages.error({
                title: 'Error saving profiles.',
                body: '<p> Internal Error: Please contact your Confluence administrators.</p>'
            });
            console.error("Malformed XML!: ", err);
            return;
        }


        AJS.$.ajax({
            url: url,
            type: "PUT",
            contentType: "application/json",
            data: '{"xml":"' + encodeURIComponent(xmlString) + '", "password":' + JSON.stringify(profileAuth) + '}',
            processData: false
        }).done(function() {
            AJS.tabs.change(jQuery('a[href="#view-' + savedId + '"]'));
            AJS.messages.success({
                title: 'Success!',
                body: '<p> You have successfully saved this profile.</p>',
                fadeout: true,
                delay: 2000
            });
        }).fail(function(self, status, error) {
            AJS.messages.error({
                title: 'Error saving profiles.',
                body: '<p>Internal Error: Please contact your Confluence administrators.</p>'
            });
            console.error("Bad Request!: ", err);
        });
    }

    function bindButtons() {
        AJS.$(".update-profile-button").unbind("click");
        AJS.$(".update-profile-button").click(function(e) {
            e.preventDefault();
            profilesConfigHelper.updateProfile($(e.target).attr("connection-profile"));
        });

        AJS.$(".delete-profile-button").unbind("click");
        AJS.$(".delete-profile-button").click(function(e) {
            e.preventDefault();
            profilesConfigHelper.deleteProfile($(e.target).attr("connection-profile"));
        });

        AJS.$(".test-profile-button").unbind("click");
        AJS.$(".test-profile-button").click(function(e) {
            e.preventDefault();
            profilesConfigHelper.testProfile($(e.target).attr("connection-profile"));
        });
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

    return methods;
})(AJS.$ || jQuery);

AJS.toInit(function() {

    AJS.$("#add-connection-profile").click(function(e) {
        e.preventDefault();
        profilesConfigHelper.addConnectionProfile();
    });

    AJS.$(".update-profile-button").unbind("click");
    AJS.$(".update-profile-button").click(function(e) {
        e.preventDefault();
        profilesConfigHelper.updateProfile($(e.target).attr("connection-profile"));
    });

    profilesConfigHelper.loadConfig();
});