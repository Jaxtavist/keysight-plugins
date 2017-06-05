package com.keysight.mathjax.admin;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.keysight.mathjax.helpers.PluginHelper;

import java.util.Map;

public class UrlContextProvider implements ContextProvider {
    private PluginSettingsFactory pluginSettingsFactory;

    public UrlContextProvider(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    public void init(Map<String, String> params)
            throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> context) {
        // Get the url string from the admin controlled settings
        // If it's null, default to a hardcoded value.

        String url = PluginHelper.getStoredUrl(pluginSettingsFactory);

        context.put("url", url);
        return context;
    }
}
