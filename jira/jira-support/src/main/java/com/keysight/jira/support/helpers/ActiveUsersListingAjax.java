package com.keysight.jira.support.helpers;

import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.URI;
import com.atlassian.sal.api.auth.LoginUriProvider;
//import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;

import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.jira.user.ApplicationUser;

public class ActiveUsersListingAjax extends HttpServlet
{
   private final GlobalPermissionManager globalPermissionManager;
   private final LoginUriProvider loginUriProvider;
   private final TemplateRenderer templateRenderer;
   private final UserManager userManager;

   public ActiveUsersListingAjax( GlobalPermissionManager globalPermissionManager,
                                  LoginUriProvider loginUriProvider, 
			                      TemplateRenderer templateRenderer,
		                          UserManager userManager ){

      this.globalPermissionManager = globalPermissionManager;
      this.loginUriProvider = loginUriProvider;
      this.templateRenderer = templateRenderer;
      this.userManager = userManager;
   }

   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{

      String template = "/com/keysight/jira-support/templates/active-users-list-ajax.vm";
      Map<String, Object> velocityContext = new HashMap<String, Object>();

      String username = request.getRemoteUser();
      if( username == null ){
         redirectToLogin(request, response);
         return;
      }

      ApplicationUser remoteUser = userManager.getUserByName( username );
      if( remoteUser == null || !globalPermissionManager.hasPermission( GlobalPermissionKey.SYSTEM_ADMIN, remoteUser ) )
      {
         redirectToLogin(request, response);
         return;
      }
      
      response.setContentType("text/html;charset=utf-8");
      templateRenderer.render(template, velocityContext, response.getWriter());
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
