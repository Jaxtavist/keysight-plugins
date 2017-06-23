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
            // If XML doesn't validate, just give an empty document
            try {
                $xml = AJS.$(AJS.$.parseXML(decodeURIComponent(pluginConfiguration.xml)));
            } catch(err) {
                if( pluginConfiguration.xml != null){
                   console.error("Failed to parse XML: ", err);
                }
                $xml = AJS.$(AJS.$.parseXML(
                    "<plugin-configuration>\n"+
                    "    <jdbc-driver-directory></jdbc-driver-directory>\n"+
                    "    <config-authorized-users></config-authorized-users>\n"+
                    "    <config-authorized-groups></config-authorized-groups>\n"+
                    "    <row-limit-type></row-limit-type>\n"+
                    "    <row-limit-value></row-limit-value>\n"+
                    "    <timeout-limit-type></timeout-limit-type>\n"+
                    "    <timeout-limit-value></timeout-limit-value>\n"+
                    "    <notification-email></notification-email>\n"+
                    "</plugin-configuration>"
                ));
            }

            $("#jdbc-driver-directory").val(atob($xml.find("jdbc-driver-directory").html()));
            var configAuthorizedUsers = atob($xml.find("config-authorized-users").html());
            var configAuthorizedGroups = atob($xml.find("config-authorized-groups").html());
            var rowLimitType = atob($xml.find("row-limit-type").html());
            var rowLimitValue = atob($xml.find("row-limit-value").html());
            var timeoutLimitType = atob($xml.find("timeout-limit-type").html());
            var timeoutLimitValue = atob($xml.find("timeout-limit-value").html());
            var notificationEmail = atob($xml.find("notification-email").html());

            // If we failed to get the fields from the xml, default to empty string;
            if (configAuthorizedUsers == null) {
                configAuthorizedUsers = "";
            }
            if (configAuthorizedGroups == null) {
                configAuthorizedGroups = "";
            }
            if (rowLimitType == null) {
                rowLimitType = "Soft";
            }
            if (rowLimitValue == null) {
                rowLimitValue = "";
            }
            if (timeoutLimitType == null) {
                timeoutLimitType = "Soft";
            }
            if (timeoutLimitValue == null) {
                timeoutLimitValue = "";
            }
            if (notificationEmail == null) {
                notificationEmail = "";
            }

            // Load access profiles for this page.
            AJS.$("#configuration-permissions").append(Keysight.Database.Admin.Config.Templates.accessProfile({
                configAuthorizedUsers: configAuthorizedUsers,
                configAuthorizedGroups: configAuthorizedGroups
            }));

            AJS.$("#row-limit-type").val( rowLimitType );
            AJS.$("#row-limit-value").val( rowLimitValue );
            AJS.$("#timeout-limit-type").val( timeoutLimitType );
            AJS.$("#timeout-limit-value").val( timeoutLimitValue );
            AJS.$("#notification-email").val( notificationEmail );

            AJS.$("#save-config-auth").click(function(e) {
                e.preventDefault();
                databaseConfigHelper.saveConfigAuth();
            });

            AJS.$("#save-performance-security-control-settings").click(function(e) {
                e.preventDefault();
                databaseConfigHelper.saveConfigAuth();
            });

        }).fail(function(self, status, error) {
            alert(error);
        });
    }

    function saveConfig() {
        var xmlString = '<?xml version="1.0" encoding="UTF-8"?>' + "\n" +
            '<plugin-configuration>' + "\n" +
            '   <jdbc-driver-directory>' + btoa($("#jdbc-driver-directory").val()) + "</jdbc-driver-directory>\n" +
            '   <config-authorized-users>' + btoa($("#config-authorized-users").val()) + "</config-authorized-users>\n" +
            '   <config-authorized-groups>' + btoa($("#config-authorized-groups").val()) + "</config-authorized-groups>\n" +
            '   <row-limit-type>' + btoa($("#row-limit-type").val()) + "</row-limit-type>\n" +
            '   <row-limit-value>' + btoa($("#row-limit-value").val()) + "</row-limit-value>\n" +
            '   <timeout-limit-type>' + btoa($("#timeout-limit-type").val()) + "</timeout-limit-type>\n" +
            '   <timeout-limit-value>' + btoa($("#timeout-limit-value").val()) + "</timeout-limit-value>\n" +
            '   <notification-email>' + btoa($("#notification-email").val()) + "</notification-email>\n" +
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