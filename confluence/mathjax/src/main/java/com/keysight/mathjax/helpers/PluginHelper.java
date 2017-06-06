package com.keysight.mathjax.helpers;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.keysight.mathjax.rest.RestAdminConfigService;
import org.apache.commons.validator.routines.UrlValidator;
// Helper functions for use throughout the plugin
public class PluginHelper {
    private static PluginSettings settings;

    PluginHelper(PluginSettingsFactory pluginSettingsFactory) {
        PluginHelper.settings = pluginSettingsFactory.createGlobalSettings();
    }

    public static String getStoredUrl() {
        UrlValidator urlValidator = new UrlValidator();
        Object url;

        try {
            url = settings.get(RestAdminConfigService.Config.class.getName() + ".url");
            if (!urlValidator.isValid(url.toString())) {
                url = Constants.DEFAULT_URL;
            }
            return (String) url;
        } catch (Exception exception) {
            return Constants.DEFAULT_URL;
        }
    }
}
