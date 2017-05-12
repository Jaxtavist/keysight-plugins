package com.keysight.database.helpers;

import java.io.IOException;
import java.io.File;
import java.util.Map;
import java.util.HashMap;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.URI;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;


import com.keysight.database.helpers.PluginHelper;
import com.keysight.database.helpers.DriverDetails;

public class AdminUI extends HttpServlet
{
   private final UserManager userManager;
   private final LoginUriProvider loginUriProvider;
   private final TemplateRenderer templateRenderer;
   private final PluginSettingsFactory pluginSettingsFactory;
   private final TransactionTemplate transactionTemplate;

   public AdminUI( LoginUriProvider loginUriProvider,
                   PluginSettingsFactory pluginSettingsFactory,
                   TemplateRenderer templateRenderer,
                   TransactionTemplate transactionTemplate,
                   UserManager userManager ){
      this.loginUriProvider = loginUriProvider;
      this.pluginSettingsFactory = pluginSettingsFactory;
      this.templateRenderer = templateRenderer;
      this.transactionTemplate = transactionTemplate;
      this.userManager = userManager;
   }

   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
      String username = userManager.getRemoteUsername(request);
      if (username == null || !userManager.isSystemAdmin(username)){
         redirectToLogin(request, response);
         return;
      }

      DriverDetails driverDetails = null;  
      Map<String,Object> velocityContext = new HashMap<String, Object>();
      velocityContext.put( "relativeHome", new File(".").getCanonicalPath() );
      PluginHelper pluginHelper = new PluginHelper( pluginSettingsFactory, transactionTemplate );

      Map<String, String> driverKeyMap = new HashMap<String, String>();
      driverKeyMap.put( "DB2", "db2" );
      driverKeyMap.put( "Derby", "derby" );
      driverKeyMap.put( "Microsoft SQL Server", "microsoftSqlServer" );
      driverKeyMap.put( "MongoDB", "mongoDb" );
      driverKeyMap.put( "MySQL", "mySql" );
      driverKeyMap.put( "Oracle", "oracle" );
      driverKeyMap.put( "PostgreSQL", "postgreSql" );
      driverKeyMap.put( "Sybase", "sybase" );

      for(String key: driverKeyMap.keySet()){
         driverDetails = pluginHelper.getDriverDetails( key );
         if( !driverDetails.isOk ){
            velocityContext.put( driverKeyMap.get(key), "error" );
            System.out.println( driverDetails.className );
            System.out.println( driverDetails.url );
         } else if( !driverDetails.isLocal ){
            velocityContext.put( driverKeyMap.get(key), "warning" );
         } else {
            velocityContext.put( driverKeyMap.get(key), "ok" );
         }
      }

      response.setContentType("text/html;charset=utf-8");
      templateRenderer.render("/com/keysight/database/templates/admin-ui.vm", velocityContext, response.getWriter() );
   }

   private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException{
      response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
   }
 
   private URI getUri(HttpServletRequest request){
      StringBuffer builder = request.getRequestURL();
      if (request.getQueryString() != null){
         builder.append("?");
         builder.append(request.getQueryString());
      }
      return URI.create(builder.toString());
   }
}
