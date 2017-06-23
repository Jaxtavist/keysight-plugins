package com.keysight.database.helpers;

import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.templaterenderer.TemplateRenderer;
import org.h2.engine.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileConfigUI extends HttpServlet {
   private final UserManager userManager;
   private final LoginUriProvider loginUriProvider;
   private final TemplateRenderer templateRenderer;
   private final UserAccessor userAccessor;
   private final PluginSettingsFactory pluginSettingsFactory;
   private final TransactionTemplate transactionTemplate;

   public ProfileConfigUI(LoginUriProvider loginUriProvider,
                          TemplateRenderer templateRenderer,
                          UserManager userManager,
                          UserAccessor userAccessor,
                          PluginSettingsFactory pluginSettingsFactory,
                          TransactionTemplate transactionTemplate) {
      this.loginUriProvider = loginUriProvider;
      this.templateRenderer = templateRenderer;
      this.userManager = userManager;
      this.userAccessor = userAccessor;
      this.pluginSettingsFactory = pluginSettingsFactory;
      this.transactionTemplate = transactionTemplate;
   }

   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
      if (!isAuthorized(request)) {
         redirectToLogin(request, response);
         return;
      }

      DriverDetails driverDetails;
      Map<String, Object> velocityContext = new HashMap<String, Object>();
      velocityContext.put("relativeHome", new File(".").getCanonicalPath());
      PluginHelper pluginHelper = new PluginHelper(pluginSettingsFactory, transactionTemplate);

      Map<String, String> driverKeyMap = new HashMap<String, String>();
      driverKeyMap.put("DB2", "db2");
      driverKeyMap.put("Derby", "derby");
      driverKeyMap.put("Microsoft SQL Server", "microsoftSqlServer");
      driverKeyMap.put("MongoDB", "mongoDb");
      driverKeyMap.put("MySQL", "mySql");
      driverKeyMap.put("Oracle", "oracle");
      driverKeyMap.put("PostgreSQL", "postgreSql");
      driverKeyMap.put("Sybase", "sybase");

      for (String key : driverKeyMap.keySet()) {
         driverDetails = pluginHelper.getDriverDetails(key);
         if (!driverDetails.isOk) {
            velocityContext.put(driverKeyMap.get(key), "error");
            System.out.println(driverDetails.className);
            System.out.println(driverDetails.url);
         } else if (!driverDetails.isLocal) {
            velocityContext.put(driverKeyMap.get(key), "warning");
         } else {
            velocityContext.put(driverKeyMap.get(key), "ok");
         }
      }

      response.setContentType("text/html;charset=utf-8");
      templateRenderer.render("/com/keysight/database/templates/profile-config-ui.vm", velocityContext, response.getWriter());
   }

   private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
      response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
   }

   private URI getUri(HttpServletRequest request) {
      StringBuffer builder = request.getRequestURL();
      if (request.getQueryString() != null) {
         builder.append("?");
         builder.append(request.getQueryString());
      }
      return URI.create(builder.toString());
   }

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
