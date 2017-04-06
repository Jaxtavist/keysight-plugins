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
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.security.login.LoginManager;
import com.atlassian.confluence.security.login.LoginInfo;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.Page;

import com.atlassian.confluence.security.SpacePermission;

public class SpaceInventoryAjax extends HttpServlet
{
   private final UserManager userManager;
   private final LoginUriProvider loginUriProvider;
   private final TemplateRenderer renderer;
   private final VelocityHelperService velocityHelperService;
   private final UserAccessor userAccessor;
   private final LoginManager loginManager;
   private final SpaceManager spaceManager;
   private final PageManager pageManager;
   private final SpacePermissionManager spacePermissionManager;

   public SpaceInventoryAjax( LoginUriProvider loginUriProvider, 
		              LoginManager loginManager, 
		              TemplateRenderer renderer,
		              UserManager userManager, 
			      UserAccessor userAccessor,
			      SpaceManager spaceManager,
			      PageManager pageManager,
			      SpacePermissionManager spacePermissionManager,
			      VelocityHelperService velocityHelperService){
      this.loginUriProvider = loginUriProvider;
      this.renderer = renderer;
      this.userManager = userManager;
      this.userAccessor = userAccessor;
      this.loginManager = loginManager;
      this.pageManager = pageManager;
      this.velocityHelperService = velocityHelperService;
      this.spaceManager = spaceManager;
      this.spacePermissionManager = spacePermissionManager;
   }

   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
      String username = userManager.getRemoteUsername(request);
      if (username == null || !userManager.isSystemAdmin(username)){
         redirectToLogin(request, response);
         return;
      }

      String template = "/com/keysight/confluence-support/templates/space-inventory-ajax.vm";
      Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();
      response.setContentType("text/html;charset=utf-8");
      renderer.render(template, velocityContext, response.getWriter());
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
