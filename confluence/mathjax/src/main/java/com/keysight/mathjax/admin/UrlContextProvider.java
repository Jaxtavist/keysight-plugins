package com.keysight.mathjax.admin;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.keysight.mathjax.helpers.PluginHelper;

import java.util.Map;

public class UrlContextProvider implements ContextProvider {
    private final PluginSettingsFactory pluginSettingsFactory;
    private static String URL = "url";

    public UrlContextProvider(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> context) {

        PluginHelper pluginHelper = new PluginHelper( pluginSettingsFactory );
        context.put( URL, pluginHelper.getUrl() );

        return context;
    }
}
