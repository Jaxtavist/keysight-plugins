package com.keysight.mathjax.helpers;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.keysight.mathjax.rest.RestAdminConfigService;
import org.apache.commons.validator.routines.UrlValidator;

// Helper functions for use throughout the plugin
public class PluginHelper {
    private final PluginSettingsFactory pluginSettingsFactory;
    private String m_url;

    public PluginHelper(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;

        PluginSettings pluginSettings = this.pluginSettingsFactory.createGlobalSettings();
        UrlValidator urlValidator = new UrlValidator();
        String url;

        this.setUrl( Constants.DEFAULT_URL );

        try {
            url = (String) pluginSettings.get(RestAdminConfigService.Config.class.getName() + ".url");
            if (urlValidator.isValid(url)) {
                this.setUrl( url );
            }
        } catch (Exception exception) { }
    }

    public String getUrl(){ return this.m_url; }
    public void setUrl( String url ){ this.m_url = url; }
}
