package com.keysight.confluence.support.helpers;

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
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.security.login.LoginManager;
import com.atlassian.confluence.security.login.LoginInfo;

public class ActiveUsersListingAjax extends HttpServlet
{
   private final UserManager userManager;
   private final LoginUriProvider loginUriProvider;
   private final TemplateRenderer renderer;
   private final VelocityHelperService velocityHelperService;
   private final UserAccessor userAccessor;
   private final LoginManager loginManager;

   public ActiveUsersListingAjax( LoginUriProvider loginUriProvider, 
		              LoginManager loginManager, 
			      TemplateRenderer renderer,
		              UserManager userManager, 
			      UserAccessor userAccessor,
			      VelocityHelperService velocityHelperService){
      this.loginUriProvider = loginUriProvider;
      this.renderer = renderer;
      this.userManager = userManager;
      this.userAccessor = userAccessor;
      this.loginManager = loginManager;
      this.velocityHelperService = velocityHelperService;
   }

   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
      String username = userManager.getRemoteUsername(request);
      if (username == null || !userManager.isSystemAdmin(username)){
         redirectToLogin(request, response);
         return;
      }

      Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();

      //velocityContext.put( "activeUsers", currentUsers );
      //velocityContext.put( "claimedLicenseCount", String.valueOf( userAccessor.countLicenseConsumingUsers() ) );
      //velocityContext.put( "countOfUsersWhoCanAuthenticate", String.valueOf( userAccessor.countUsersWithConfluenceAccess() ) );

      response.setContentType("text/html;charset=utf-8");
      renderer.render("/com/keysight/confluence-support/templates/active-users-list-ajax.vm", velocityContext, response.getWriter());
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
