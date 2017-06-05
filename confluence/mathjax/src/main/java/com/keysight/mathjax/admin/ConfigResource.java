package com.keysight.mathjax.admin;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Path("/admin/")
public class ConfigResource {

    private final String DEFAULT_URL = "https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.1/MathJax.js?config=TeX-MML-AM_CHTML";

    private final PluginSettingsFactory pluginSettingsFactory;
    private final TransactionTemplate transactionTemplate;
    private final UserManager userManager;

    public ConfigResource( PluginSettingsFactory pluginSettingsFactory,
                           TransactionTemplate transactionTemplate,
                           UserManager userManager ) {
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.transactionTemplate = transactionTemplate;
        this.userManager = userManager;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@Context HttpServletRequest request) {
        UserKey userkey = userManager.getRemoteUserKey(request);
        if (userkey == null || !userManager.isSystemAdmin(userkey)) {
            return Response.status(Status.UNAUTHORIZED).build();
        }

        return Response.ok(transactionTemplate.execute(new TransactionCallback() {
            public Object doInTransaction() {
                PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
                Config config = new Config();
                Object url = null;

                try
                {
                    url = settings.get(ConfigResource.Config.class.getName() + ".url");
                    if (url.toString().equals("")) {
                        url = DEFAULT_URL;
                    }
                    config.setUrl((String) url );
                }
                catch( Exception exception )
                {
                   config.setUrl( DEFAULT_URL );
                }
                return config;
            }
        })).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response put(final Config config, @Context HttpServletRequest request) {
        UserKey userkey = userManager.getRemoteUserKey(request);
        if (userkey == null || !userManager.isSystemAdmin(userkey)) {
            return Response.status(Status.UNAUTHORIZED).build();
        }

        transactionTemplate.execute(new TransactionCallback() {
            public Object doInTransaction() {
                PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
                pluginSettings.put(Config.class.getName() + ".url", config.getUrl());
                return null;
            }
        });
        return Response.ok().build();
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static final class Config {
        @XmlElement
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
