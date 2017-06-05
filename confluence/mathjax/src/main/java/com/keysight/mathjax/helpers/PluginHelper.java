package com.keysight.mathjax.helpers;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.keysight.mathjax.rest.RestAdminConfigService;
import org.apache.commons.validator.routines.UrlValidator;

// Helper functions for use throughout the plugin
public abstract class PluginHelper {
    public static String getStoredUrl(PluginSettingsFactory pluginSettingsFactory) {
        UrlValidator urlValidator = new UrlValidator();
        PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
        Object url;

        try
        {
            url = settings.get(RestAdminConfigService.Config.class.getName() + ".url");
            if (!urlValidator.isValid(url.toString())) {
                url = Constants.DEFAULT_URL;
            }
            return (String)url;
        }
        catch( Exception exception )
        {
            return Constants.DEFAULT_URL;
        }
    }
}
