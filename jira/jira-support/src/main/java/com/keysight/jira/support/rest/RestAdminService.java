package com.keysight.jira.support.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import org.apache.commons.lang.StringUtils;

import com.keysight.jira.support.helpers.ActiveUserInfo;

import com.atlassian.jira.bc.security.login.LoginInfo;
import com.atlassian.jira.bc.security.login.LoginService;
import com.atlassian.jira.bc.user.search.UserSearchService;
import com.atlassian.jira.bc.user.search.UserSearchParams;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.application.ApplicationAuthorizationService;

@Path("/admin/")
public class RestAdminService
{
   private final GlobalPermissionManager globalPermissionManager;
   private final LoginService loginService;
   private final UserManager userManager;
   private final UserSearchService userSearchService;
   private final ApplicationAuthorizationService applicationAuthorizationService;

   public RestAdminService( ApplicationAuthorizationService applicationAuthorzationService,
                            GlobalPermissionManager globalPermissionManager,
                            LoginService loginService,
                            UserManager userManager,
                            UserSearchService userSearchService){

      this.applicationAuthorizationService = applicationAuthorzationService;
      this.globalPermissionManager         = globalPermissionManager;
      this.loginService                    = loginService;
      this.userManager                     = userManager;
      this.userSearchService               = userSearchService;
   }

   @GET
   @Path("user-list")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getUserList(@Context HttpServletRequest request)
   {
      String username = userManager.getRemoteUsername(request);
      if (username == null || !userManager.isSystemAdmin(username)){
         return Response.status(Status.UNAUTHORIZED).build();
      }
      
      List<String> activeUsernames = new ArrayList<String>();

      UserSearchParams userSearchParams = new UserSearchParams( true, true, false );
      List<ApplicationUser> applicationUsers = userSearchService.findUsers( "a", userSearchParams );
      for( ApplicationUser user : applicationUsers ){
         activeUsernames.add( user.getUsername() );
      } 
      
      return Response.ok(activeUsernames).build();
   }

   @GET
   @Path("user-info")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getUserInfo( @Context HttpServletRequest request)
   {
      String username = userManager.getRemoteUsername(request);
      if (username == null || !userManager.isSystemAdmin(username)){
         return Response.status(Status.UNAUTHORIZED).build();
      }

      List<ActiveUserInfo> currentUsers = new ArrayList<ActiveUserInfo>();
      String[] usernames = new String[0];
      Map<String, String[]> parameterMap = request.getParameterMap();
      if( parameterMap.containsKey( "usernames" ) ){
         usernames = parameterMap.get("usernames");
      }

      for( String activeUsername : usernames ){
         LoginInfo activeUserLoginInfo = loginService.getLoginInfo( activeUsername );
         currentUsers.add( new ActiveUserInfo( activeUsername, 
                                               userManager.getUserProfile( activeUsername ).getFullName(), 
                                               activeUserLoginInfo.getLastLoginTime() ) );
      }

      return Response.ok( currentUsers ).build();
   }

/*
   @GET
   @Path("count-of-users-who-can-authenticate")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getCountOfUsersWhoCanAuthenticate(@Context HttpServletRequest request)
   {
      String username = userManager.getRemoteUsername(request);
      if (username == null || !userManager.isSystemAdmin(username)){
         return Response.status(Status.UNAUTHORIZED).build();
      }
      String response = String.valueOf(userAccessor.countUsersWithConfluenceAccess());
      return Response.ok(response);
   }
   @GET
   @Path("count-of-license-consuming-users")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getCountOfLicenseConsumingUsers(@Context HttpServletRequest request)
   {
      String username = userManager.getRemoteUsername(request);
      if (username == null || !userManager.isSystemAdmin(username)){
         return Response.status(Status.UNAUTHORIZED).build();
      }
      String response = String.valueOf(userAccessor.countLicenseConsumingUsers());
      return Response.ok(response);
   }
*/

}
