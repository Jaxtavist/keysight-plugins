package com.keysight.database.rest;

import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.keysight.database.helpers.PluginConfig;
import com.keysight.database.helpers.PluginHelper;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.List;

@Path("/admin-config/")
public class RestAdminConfigService {
    private final UserManager userManager;
    private final UserAccessor userAccessor;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final TransactionTemplate transactionTemplate;

    public RestAdminConfigService(UserManager userManager,
                                  UserAccessor userAccessor,
                                  PluginSettingsFactory pluginSettingsFactory,
                                  TransactionTemplate transactionTemplate) {
        this.userManager = userManager;
        this.userAccessor = userAccessor;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.transactionTemplate = transactionTemplate;
    }

    @GET
    @Path("configuration")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConfiguration(@Context HttpServletRequest request) {
        if (!isAuthorized(request)) {
            return Response.status(Status.UNAUTHORIZED).build();
        }

        return Response.ok(transactionTemplate.execute((TransactionCallback<Object>) () -> {
            PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
            PluginConfig pluginConfig = new PluginConfig();
            pluginConfig.setXml((String) settings.get(PluginConfig.class.getName() + ".xml"));
            return pluginConfig;
        })).build();
    }

    @PUT
    @Path("configuration")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response putConfiguration(final PluginConfig pluginConfig, @Context HttpServletRequest request) {
        if (!isAuthorized(request)) {
            return Response.status(Status.UNAUTHORIZED).build();
        }

        transactionTemplate.execute(() -> {
            PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
            pluginSettings.put(PluginConfig.class.getName() + ".xml", pluginConfig.getXml());
            return null;
        });

        return Response.noContent().build();
    }

    @GET
    @Path("get-installed-drivers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInstalledDrivers(@Context HttpServletRequest request) {
        if (!isAuthorized(request)) {
            return Response.status(Status.UNAUTHORIZED).build();
        }

        return Response.ok(transactionTemplate.execute((TransactionCallback<Object>) () -> {
            PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
            PluginConfig pluginConfig = new PluginConfig();
            pluginConfig.setXml((String) settings.get(PluginConfig.class.getName() + ".xml"));
            return pluginConfig;
        })).build();
    }

    // Checks if the user has permissions for the admin page
    // User must be: System Admin OR in user list OR be a member of a group in group list
    private boolean isAuthorized(HttpServletRequest request) {
        UserKey userKey = userManager.getRemoteUserKey(request);

        // Check if logged in and admin
        if (userKey == null) {
            return false;
        } else if (userManager.isSystemAdmin(userKey)) {
            return true;
        }


        // CURRENT STATE: User is not an admin, username or group might have permissions
        UserProfile currentProfile = userManager.getUserProfile(userKey);
        assert (currentProfile != null);
        String currentUsername = currentProfile.getUsername();

        List<String> currentGroups = userAccessor.getGroupNamesForUserName(currentUsername);
        PluginHelper pluginHelper = new PluginHelper(pluginSettingsFactory, transactionTemplate);

        // Check username
        if (pluginHelper.getAuthorizedUsers().contains(currentUsername)) {
            return true;
        }

        // CURRENT STATE: Username doesn't have permission, group might

        List<String> tempList = new ArrayList<>(pluginHelper.getAuthorizedGroups());
        tempList.retainAll(currentGroups); // Shared elements between allowed groups and groups user is in.
        return !tempList.isEmpty(); // If none of the user's groups are allowed, reject them.
    }
}
