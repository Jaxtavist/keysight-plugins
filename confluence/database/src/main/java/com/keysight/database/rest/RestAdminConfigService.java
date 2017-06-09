package com.keysight.database.rest;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.keysight.database.helpers.PluginConfig;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/admin-config/")
public class RestAdminConfigService
{
   private final UserManager userManager;
   private final PluginSettingsFactory pluginSettingsFactory;
   private final TransactionTemplate transactionTemplate;

   public RestAdminConfigService( UserManager userManager, 
                                  PluginSettingsFactory pluginSettingsFactory, 
                                  TransactionTemplate transactionTemplate)
   {
      this.userManager = userManager;
      this.pluginSettingsFactory = pluginSettingsFactory;
      this.transactionTemplate = transactionTemplate;
   }

   @GET
   @Path("configuration")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getConfiguration(@Context HttpServletRequest request)
   {
      if (!isAuthorized(request)) {
         return Response.status(Status.UNAUTHORIZED).build();
      }

      return Response.ok(transactionTemplate.execute(new TransactionCallback(){
         public Object doInTransaction(){
            PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
            PluginConfig pluginConfig = new PluginConfig();
            pluginConfig.setXml( (String) settings.get(PluginConfig.class.getName() + ".xml"));
            return pluginConfig;
         }
      })).build();
   }

   @PUT
   @Path("configuration")
   @Consumes(MediaType.APPLICATION_JSON)
   public Response putConfiguration(final PluginConfig pluginConfig, @Context HttpServletRequest request)
   {
      if (!isAuthorized(request)) {
         return Response.status(Status.UNAUTHORIZED).build();
      }
   
      transactionTemplate.execute(new TransactionCallback() {
         public Object doInTransaction() {
            PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
            pluginSettings.put(PluginConfig.class.getName() + ".xml", pluginConfig.getXml());
            return null;
         }
      });

      return Response.noContent().build();
   }

   @GET
   @Path("get-installed-drivers")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getInstalledDrivers(@Context HttpServletRequest request)
   {
      if (!isAuthorized(request)) {
         return Response.status(Status.UNAUTHORIZED).build();
      }

      return Response.ok(transactionTemplate.execute(new TransactionCallback(){
         public Object doInTransaction(){
            PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
            PluginConfig pluginConfig = new PluginConfig();
            pluginConfig.setXml( (String) settings.get(PluginConfig.class.getName() + ".xml"));
            return pluginConfig;
         }
      })).build();
   }

   private boolean isAuthorized(HttpServletRequest request) {
      UserKey userKey = userManager.getRemoteUserKey(request);

      // Check basic conditions.
      if (userKey == null) {
         return false;
      } else if (userManager.isSystemAdmin(userKey)) {
         return true;
      }

      // Check custom list

      return false;
   }

}
