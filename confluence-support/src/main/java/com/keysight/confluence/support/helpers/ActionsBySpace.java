package com.keysight.confluence.support.helpers;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.URI;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.setup.settings.SettingsManager;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import java.net.URLDecoder;
import java.io.StringReader;


import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.split;
import static org.apache.commons.lang.StringUtils.trim;

import com.keysight.confluence.support.helpers.PluginConfig;
import com.atlassian.confluence.security.SpacePermission;

public class ActionsBySpace extends HttpServlet
{
   private final LoginUriProvider loginUriProvider;
   private final PluginSettingsFactory pluginSettingsFactory;
   private final SettingsManager settingsManager;
   private final TemplateRenderer templateRenderer;
   private final TransactionTemplate transactionTemplate;
   private final UserManager userManager;
   private final VelocityHelperService velocityHelperService;


   public ActionsBySpace( PluginSettingsFactory pluginSettingsFactory,
                          LoginUriProvider loginUriProvider, 
                          SettingsManager settingsManager,
                          TemplateRenderer templateRenderer,
                          TransactionTemplate transactionTemplate,
                          UserManager userManager, 
                          VelocityHelperService velocityHelperService){
      this.pluginSettingsFactory = pluginSettingsFactory;
      this.loginUriProvider = loginUriProvider;
      this.settingsManager = settingsManager;
      this.templateRenderer = templateRenderer;
      this.transactionTemplate = transactionTemplate;
      this.userManager = userManager;
      this.velocityHelperService = velocityHelperService;
   }

   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
      String username = userManager.getRemoteUsername(request);
      if (username == null || !userManager.isSystemAdmin(username)){
         redirectToLogin(request, response);
         return;
      }

      Map<String, String[]> params = request.getParameterMap();
      Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();
      velocityContext.put( "baseUrl", settingsManager.getGlobalSettings().getBaseUrl() );

      List<String> allUsersGroups = new ArrayList<String>();
      String template = "/com/keysight/confluence-support/templates/actions-by-space.vm";

      try{
          PluginConfig pluginConfig = (PluginConfig) transactionTemplate.execute(new TransactionCallback(){
              public Object doInTransaction(){
                  PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
                  PluginConfig pluginConfig = new PluginConfig();
                  pluginConfig.setXml( (String) settings.get(PluginConfig.class.getName() + ".xml"));
                  return pluginConfig;
              };
          });

          String pluginConfigXml = URLDecoder.decode( pluginConfig.getXml() );

          String allUsersGroup = null;
          DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
          DocumentBuilder builder = factory.newDocumentBuilder();
          Document document = builder.parse( new InputSource( new StringReader(pluginConfigXml) ) );
          Element root = document.getDocumentElement();
          NodeList allUsersGroupNodes = root.getElementsByTagName( "all-users-group" );
          for( int i = 0; i < allUsersGroupNodes.getLength(); i++ ){
             if( allUsersGroupNodes.item(i).getNodeType() == Node.ELEMENT_NODE){ 
                allUsersGroups.add( allUsersGroupNodes.item(i).getTextContent() );
             }
          }

          velocityContext.put( "allUsersGroups", String.join(",", allUsersGroups ) );
      } catch( Exception e ){
      }

      response.setContentType("text/html;charset=utf-8");
      templateRenderer.render( template, velocityContext, response.getWriter());
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
