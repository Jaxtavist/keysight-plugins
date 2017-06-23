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
        var xmlString = '<?xml version="1.0" encoding="UTF-8"?>' + "\n" +
            '<plugin-configuration>' + "\n" +
            '   <config-authorized-users>' + $("#config-authorized-users").val() + "</config-authorized-users>\n" +
            '   <config-authorized-groups>' + $("#config-authorized-groups").val() + "</config-authorized-groups>\n" +
            '   <jdbc-driver-directory>' + btoa($("#jdbc-driver-directory").val()) + "</jdbc-driver-directory>\n" +
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

    return methods;
})(AJS.$ || jQuery);

AJS.toInit(function() {

    AJS.$("#save-jdbc-driver-directory").click(function(e) {
        e.preventDefault();
        databaseConfigHelper.saveJdbcDriverDirectory();
    });

    databaseConfigHelper.loadConfig();
});