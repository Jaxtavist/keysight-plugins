package com.keysight.database.rest;

import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.keysight.database.helpers.ConnectionProfile;
import com.keysight.database.helpers.PluginHelper;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Path("/")
public class RestService {
    private static ResourceBundle literals;
    private final SettingsManager settingsManager;
    private final VelocityHelperService velocityHelperService;
    private final TransactionTemplate transactionTemplate;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final UserManager userManager;
    private final UserAccessor userAccessor;

    public RestService(SettingsManager settingsManager,
                       VelocityHelperService velocityHelperService,
                       TransactionTemplate transactionTemplate,
                       PluginSettingsFactory pluginSettingsFactory,
                       UserManager userManger,
                       UserAccessor userAccessor
    ) {
        this.settingsManager = settingsManager;
        this.velocityHelperService = velocityHelperService;
        this.transactionTemplate = transactionTemplate;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.userAccessor = userAccessor;
        this.userManager = userManger;

        literals = ResourceBundle.getBundle("/com/keysight/database/i18n/database");
    }

    // Documentation routes

    @GET
    @Path("help/database-query")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response databaseQueryHelp() {
        String title = "Database Query";
        String bodyTemplate = "/com/keysight/database/templates/database-query-help.vm";

        return getMacroHelp(title, bodyTemplate);
    }

    @GET
    @Path("help/mysql-query")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public Response mysqlQueryHelp() {
        String title = "Mysql Query";
        String bodyTemplate = "/com/keysight/database/templates/mysql-query-help.vm";

        return getMacroHelp(title, bodyTemplate);
    }

    // Configuration data routes

    @GET
    @Path("config/profiles")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConnectionProfiles(@Context HttpServletRequest request) {
        return Response
                .ok(transactionTemplate.execute((TransactionCallback<Object>) () -> generateProfileArray(request)))
                .build();
    }

    @GET
    @Path("config/profiles/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSingleProfile(@Context HttpServletRequest request, @PathParam("name") String name) {
        for (ConnectionProfile profile : generateProfileArray(request)) {
            if (profile.getName() != null && profile.getName().equals(name)) {
                return Response
                        .ok(transactionTemplate.execute(() -> profile))
                        .build();
            }
        }

        return Response
                .status(204)
                .entity(transactionTemplate.execute(() -> literals.getString("com.keysight.database.api.accessError")))
                .build();
    }

    // Helpers

    private Response getMacroHelp(String title, String bodyTemplate) {
        StringBuilder html = new StringBuilder();
        String headerTemplate = "/com/keysight/database/templates/help-header.vm";
        String footerTemplate = "/com/keysight/database/templates/help-footer.vm";
        String fossTemplate = "/com/keysight/database/templates/foss.vm";

        Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();
        velocityContext.put( "title", title );
        velocityContext.put( "baseUrl", settingsManager.getGlobalSettings().getBaseUrl() );

        html.append( velocityHelperService.getRenderedTemplate( headerTemplate, velocityContext ) );
        html.append( velocityHelperService.getRenderedTemplate( bodyTemplate,   velocityContext ) );
        html.append( velocityHelperService.getRenderedTemplate( fossTemplate,   velocityContext ) );
        html.append( velocityHelperService.getRenderedTemplate( footerTemplate, velocityContext ) );

        return Response.ok( new RestResponse( html.toString() ) ).build();
    }

    private List<ConnectionProfile> generateProfileArray(HttpServletRequest request) {
        // Get all stored profiles
        PluginHelper pluginHelper = new PluginHelper(pluginSettingsFactory, transactionTemplate);
        NodeList profileNodes = pluginHelper.getProfiles();
        ArrayList<ConnectionProfile> profiles = new ArrayList<>();

        for (int i = 0; i < profileNodes.getLength(); i++) {
            Element profileNode = (Element) profileNodes.item(i);
            profiles.add(new ConnectionProfile(profileNode));
        }

        // Get groups for user
        UserKey userKey = userManager.getRemoteUserKey(request);
        UserProfile currentProfile = userManager.getUserProfile(userKey);
        assert (currentProfile != null);
        String currentUsername = currentProfile.getUsername();
        List<String> currentGroups = userAccessor.getGroupNamesForUserName(currentUsername);

        // Return the list of authorized profiles
        return profiles.stream().filter(profile -> {
            // Check if logged in and admin
            if (userKey == null) {
                return false;
            } else if (userManager.isSystemAdmin(userKey)) {
                return true;
            }

            // CURRENT STATE: User is not an admin, username or group might have permissions
            // Check username
            if (profile.getAuthorizedUsers().contains(currentUsername)) {
                return true;
            }

            // CURRENT STATE: Username doesn't have permission, group might
            // Check group membership with union
            List<String> tempList = new ArrayList<>(profile.getAuthorizedGroups());
            tempList.retainAll(currentGroups); // Shared elements between allowed groups and groups user is in.
            return !tempList.isEmpty(); // If none of the user's groups are allowed, reject them.
        }).collect(Collectors.toList());
    }
}
