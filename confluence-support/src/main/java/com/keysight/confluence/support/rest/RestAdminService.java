package com.keysight.confluence.support.rest;

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
import com.atlassian.confluence.core.Modification;

import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.confluence.security.login.LoginManager;
import com.atlassian.confluence.setup.settings.SettingsManager;

import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.login.LoginInfo;

import com.atlassian.confluence.pages.TrashManager;
import com.keysight.confluence.support.helpers.ActiveUserInfo;
import com.keysight.confluence.support.helpers.SpaceInfo;
import com.keysight.confluence.support.helpers.CompactSpaceInfo;
import org.apache.commons.lang.StringUtils;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.user.GroupManager;
import com.atlassian.user.Group;


@Path("/admin/")
public class RestAdminService
{
   private final LoginManager loginManager;
   private final GroupManager groupManager;
   private final PageManager pageManager;
   private final SettingsManager settingsManager;
   private final SpaceManager spaceManager;
   private final SpacePermissionManager spacePermissionManager;
   private final TrashManager trashManager;
   private final UserAccessor userAccessor;
   private final UserManager userManager;

   private final static String CATEGORIES = "space-categories";
   private final static String ORIGINAL_TEXT = "original-text";
   private final static String PATTERN = "pattern-text";
   private final static String REPLACEMENT_TEXT = "replacement-text";
   private final static String GROUPS = "groups";

   private String body;

   public RestAdminService( LoginManager loginManager,
                            GroupManager groupManager,
                            PageManager pageManager,
                            SettingsManager settingsManager,
                            SpaceManager spaceManager,
                            SpacePermissionManager spacePermissionManager,
                            TrashManager trashManager,
                            UserAccessor userAccessor,
                            UserManager userManager){
      this.loginManager = loginManager;
      this.groupManager = groupManager;
      this.pageManager = pageManager;
      this.settingsManager = settingsManager;
      this.spaceManager = spaceManager;
      this.spacePermissionManager = spacePermissionManager;
      this.trashManager = trashManager;
      this.userAccessor = userAccessor;
      this.userManager = userManager;
   }

   @GET
   @Path("get-spaces")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getSpaces(@Context HttpServletRequest request)
   {
      String username = userManager.getRemoteUsername(request);
      if (username == null || !userManager.isSystemAdmin(username)){
         return Response.status(Status.UNAUTHORIZED).build();
      }

      Map<String, String[]> parameterMap = request.getParameterMap();
      List<CompactSpaceInfo> spaces = new ArrayList<CompactSpaceInfo>();
      Space space = null;

      if( parameterMap.containsKey( "space-key" ) && ! StringUtils.isEmpty(parameterMap.get( "space-key" )[0]) ){
         space = spaceManager.getSpace( parameterMap.get("space-key")[0] );
      }

      if( space != null ){
         spaces.add( new CompactSpaceInfo( space.getName(), space.getKey() ) );
      } else { 
         for( Space currentSpace : spaceManager.getAllSpaces() ){
            if( parameterMap.containsKey( CATEGORIES ) && ! StringUtils.isEmpty(parameterMap.get( CATEGORIES )[0]) ){
               String[] labels = parameterMap.get( CATEGORIES )[0].split( "/\\s*,\\s*/" );
               for( String label : labels ){
                  for( Label category : currentSpace.getDescription().getLabels() ){
                     if( label.matches( category.toString() ) ){
                        spaces.add( new CompactSpaceInfo( currentSpace.getName(), currentSpace.getKey() ) );
                     }
                  }
               }
            } else {
               spaces.add( new CompactSpaceInfo( currentSpace.getName(), currentSpace.getKey() ) );
            }
         }
      }

      return Response.ok(spaces).build();
   }


   @GET
   @Path("space-list")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getSpaceList(@Context HttpServletRequest request)
   {
      String username = userManager.getRemoteUsername(request);
      if (username == null || !userManager.isSystemAdmin(username)){
         return Response.status(Status.UNAUTHORIZED).build();
      }

      List<Space> spaces = spaceManager.getAllSpaces();
      List<String> spaceKeys = new ArrayList<String>();
      
      ArrayList<String> list = new ArrayList<String>();
      for( Space space : spaces ){
         list.add( space.getKey() );
      }

      return Response.ok(list).build();
   }
   
   @GET
   @Path("space-info")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response getSpaceInfo( @Context HttpServletRequest request)
   {
      String username = userManager.getRemoteUsername(request);
      if (username == null || !userManager.isSystemAdmin(username)){
         return Response.status(Status.UNAUTHORIZED).build();
      }

      List<SpaceInfo> spaceInventory = new ArrayList<SpaceInfo>();
      Date modificationDate;
      List<Page> pages;

      List<String> adminPermissions = new ArrayList<String>();
      adminPermissions.add( SpacePermission.ADMINISTER_SPACE_PERMISSION );

      List<String> authorPermissions = new ArrayList<String>();
      authorPermissions.add( SpacePermission.CREATEEDIT_PAGE_PERMISSION );
      authorPermissions.add( SpacePermission.EDITBLOG_PERMISSION );
      authorPermissions.add( SpacePermission.COMMENT_PERMISSION );
      authorPermissions.add( SpacePermission.CREATE_ATTACHMENT_PERMISSION );

      List<String> readerPermissions = new ArrayList<String>();
      readerPermissions.add( SpacePermission.VIEWSPACE_PERMISSION );

      Map<String, Long> groupsWithPermission;
      Map<String, Long> usersWithPermission;
 
      String[] spaceKeys = new String[0];
      Map<String, String[]> parameterMap = request.getParameterMap();
      if( parameterMap.containsKey( "spaceKeys" ) ){
         spaceKeys = parameterMap.get("spaceKeys");
      }

      for( String spaceKey : spaceKeys ){

         Space currentSpace = spaceManager.getSpace(spaceKey);
     //pages = pageManager.getPages( currentSpace, true );
     //int pageCount = pages.size(); 
     long pageCount = spaceManager.findPageTotal( currentSpace );

     pages = pageManager.getRecentlyUpdatedPages( 1, currentSpace.getKey() );
     Date mostRecentUpdateDate = pages.get(0).getLastModificationDate();

     pages = pageManager.getRecentlyAddedPages( 1, currentSpace.getKey() );
     Date mostRecentCreatedDate = pages.get(0).getLastModificationDate();

     if( mostRecentUpdateDate.getTime() >= mostRecentCreatedDate.getTime() ){
            modificationDate = mostRecentUpdateDate;
     } else {
            modificationDate = mostRecentCreatedDate;
     }
 
         Map<String, Long> adminUsers  = new HashMap<String, Long>();
         Map<String, Long> authorUsers = new HashMap<String, Long>();
         Map<String, Long> readerUsers = new HashMap<String, Long>();
         Map<String, Long> adminGroups  = new HashMap<String, Long>();
         Map<String, Long> authorGroups = new HashMap<String, Long>();
         Map<String, Long> readerGroups = new HashMap<String, Long>();
         Map<String, Long> adminGroupsText  = new HashMap<String, Long>();
         Map<String, Long> authorGroupsText = new HashMap<String, Long>();
         Map<String, Long> readerGroupsText = new HashMap<String, Long>();
         ArrayList<String> permissionsList;

         ConfluenceUser anonymousUser = userAccessor.getUserByName( "Anonymous" );

         for( String permissionType : adminPermissions ){
            permissionsList = new ArrayList<String>();
            permissionsList.add( permissionType );
            if( spacePermissionManager.hasAllPermissions( permissionsList, currentSpace, anonymousUser  ) ){
               adminUsers.put( "Anonymous", new Long( 1 ) );
            }

            usersWithPermission = spacePermissionManager.getUsersForPermissionType( permissionType, currentSpace );
            for( Map.Entry<String,Long> entry : usersWithPermission.entrySet() ){
               if( !adminUsers.containsKey( entry.getKey() ) ){
                  adminUsers.put( entry.getKey(), new Long( 1 ) );
               }
            }

            groupsWithPermission = spacePermissionManager.getGroupsForPermissionType( permissionType, currentSpace );
            for( Map.Entry<String,Long> entry : groupsWithPermission.entrySet() ){
               if( !adminGroups.containsKey( entry.getKey() ) ){
                  List<String> memberNames = userAccessor.getMemberNamesAsList( userAccessor.getGroup( entry.getKey() ) );
                  adminGroups.put( entry.getKey(), new Long( 1 ) );
                  adminGroupsText.put( entry.getKey() + " (" + String.valueOf(memberNames.size()) + ")", new Long( 1 ) );
               }
            }
         }

         for( String permissionType : authorPermissions ){
            permissionsList = new ArrayList<String>();
            permissionsList.add( permissionType );
            if( spacePermissionManager.hasAllPermissions( permissionsList, currentSpace, anonymousUser  ) ){
               authorUsers.put( "Anonymous", new Long( 1 ) );
            }

            usersWithPermission = spacePermissionManager.getUsersForPermissionType( permissionType, currentSpace );
            for( Map.Entry<String,Long> entry : usersWithPermission.entrySet() ){
               if( !(authorUsers.containsKey( entry.getKey() ) 
                   || adminUsers.containsKey( entry.getKey() ) ) ){
                  authorUsers.put( entry.getKey(), new Long( 1 ) );
               }
            }

            groupsWithPermission = spacePermissionManager.getGroupsForPermissionType( permissionType, currentSpace );
            for( Map.Entry<String,Long> entry : groupsWithPermission.entrySet() ){
               if( !(authorGroups.containsKey( entry.getKey() )
                    || adminGroups.containsKey( entry.getKey() ) ) ){
                  List<String> memberNames = userAccessor.getMemberNamesAsList( userAccessor.getGroup( entry.getKey() ) );
                  authorGroups.put( entry.getKey(), new Long( 1 ) );
                  authorGroupsText.put( entry.getKey() + " (" + String.valueOf(memberNames.size()) + ")", new Long( 1 ) );
               }
            }
         }

         for( String permissionType : readerPermissions ){
            permissionsList = new ArrayList<String>();
            permissionsList.add( permissionType );
            if( spacePermissionManager.hasAllPermissions( permissionsList, currentSpace, anonymousUser  ) ){
               readerUsers.put( "Anonymous", new Long( 1 ) );
            }

            usersWithPermission = spacePermissionManager.getUsersForPermissionType( permissionType, currentSpace );
            for( Map.Entry<String,Long> entry : usersWithPermission.entrySet() ){
               if( !(readerUsers.containsKey( entry.getKey() ) 
                    || authorUsers.containsKey( entry.getKey() ) 
                    || adminUsers.containsKey( entry.getKey() ) ) ){
                  readerUsers.put( entry.getKey(), new Long( 1 ) );
               }
            }

            groupsWithPermission = spacePermissionManager.getGroupsForPermissionType( permissionType, currentSpace );
            for( Map.Entry<String,Long> entry : groupsWithPermission.entrySet() ){
               if( !(readerGroups.containsKey( entry.getKey() ) 
                    || authorGroups.containsKey( entry.getKey() ) 
                    || adminGroups.containsKey( entry.getKey() ) ) ){
                  List<String> memberNames = userAccessor.getMemberNamesAsList( userAccessor.getGroup( entry.getKey() ) );
                  readerGroups.put( entry.getKey(), new Long( 1 ) );
                  readerGroupsText.put( entry.getKey() + " (" + String.valueOf(memberNames.size()) + ")", new Long( 1 ) );
               }
            }
         }

         SpaceInfo infoItem = new SpaceInfo( currentSpace, pageCount, modificationDate );
         infoItem.setAdminUsers( new ArrayList( adminUsers.keySet() ) );
         infoItem.setAuthorUsers( new ArrayList( authorUsers.keySet() ) );
         infoItem.setReaderUsers( new ArrayList( readerUsers.keySet() ) );
         infoItem.setAdminGroups( new ArrayList( adminGroupsText.keySet() ) );
         infoItem.setAuthorGroups( new ArrayList( authorGroupsText.keySet() ) );
         infoItem.setReaderGroups( new ArrayList( readerGroupsText.keySet() ) );
         spaceInventory.add( infoItem );
      }

      return Response.ok( spaceInventory ).build();
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

      List<String> activeUsernames = userAccessor.getUserNamesWithConfluenceAccess();
      return Response.ok(activeUsernames).build();
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

      //List<String> activeUsernames = userAccessor.getUserNamesWithConfluenceAccess();
      //List<String> shortList = activeUsernames.subList( 0, 1 );
      
      for( String activeUsername : usernames ){
         ConfluenceUser activeUser = userAccessor.getUserByName( activeUsername );
         LoginInfo activeUserLoginInfo = loginManager.getLoginInfo( activeUser );
         currentUsers.add( new ActiveUserInfo( activeUsername, 
                               activeUser.getFullName(), 
                           activeUserLoginInfo.getLastSuccessfulLoginDate() ) );
      }

      return Response.ok( currentUsers ).build();
   }

   @GET
   @Path("replace-text-in-space")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response replaceTextInSpace(@Context HttpServletRequest request)
   {
      String username = userManager.getRemoteUsername(request);
      if (username == null || !userManager.isSystemAdmin(username)){
         return Response.status(Status.UNAUTHORIZED).build();
      }

      boolean bReplaceText = false;
      boolean bOK = true;
      List<String> updates = new ArrayList<String>();
      Map<String, String[]> parameterMap = request.getParameterMap();
      String baseUrl = settingsManager.getGlobalSettings().getBaseUrl();
      String patternText = "";
      String replacementText = "";

      if( parameterMap.containsKey( "mode" ) && parameterMap.get( "mode" )[0].matches( "replace-text" ) ){
         bReplaceText = true;
      }

      if( parameterMap.containsKey( PATTERN ) && !StringUtils.isEmpty( parameterMap.get( PATTERN )[0] ) ){
         patternText = parameterMap.get(PATTERN)[0];
      }

      if( parameterMap.containsKey( REPLACEMENT_TEXT ) && !StringUtils.isEmpty( parameterMap.get( REPLACEMENT_TEXT )[0] ) ){
         replacementText = parameterMap.get(REPLACEMENT_TEXT)[0];
      }

      if( StringUtils.isEmpty(patternText) ){
         return Response.status( Response.Status.BAD_REQUEST ).entity( "The value for pattern-text cannot be empty.").build();
      }

      if( parameterMap.containsKey( "space-key" ) && !StringUtils.isEmpty(parameterMap.get( "space-key" )[0]) ){
         Space space = spaceManager.getSpace(parameterMap.get("space-key")[0]);
         if( space != null ){
            if( bReplaceText ){
               if( parameterMap.containsKey( "return-space-key" ) && parameterMap.get( "return-space-key" )[0].matches( "true" ) ){
                  updates.add( space.getKey() );
               }
               updates.add( updatePagesInSpace( space, patternText, replacementText ) );
            } else {
               List<Page> pages = pageManager.getPages( space, true );
               int wouldUpdate = 0;
               for( Page page : pages ){
                  if( page.getBodyAsString().matches( ".*" + patternText + ".*" ) ){
                     wouldUpdate++;
                  }
               }
               List<BlogPost> posts = pageManager.getBlogPosts( space, true );
               for( BlogPost post : posts ){
                  if( post.getBodyAsString().matches( ".*" + patternText + ".*" ) ){
                     wouldUpdate++;
                  }
               }
               updates.add( String.format( "<div id=\"replace-text-in-%s\"><a href=\"#\" replace-text-in-space-with-key=\"%s\" class=\"replace-text-in-space-anchor\">Replace Text:</a> There are %d of %d pages and posts to update in space %s (%s).",
                                           space.getKey(), space.getKey(), 
                                           wouldUpdate,
                                           pages.size() + posts.size(), 
                                           space.getName(), 
                                           space.getKey() ) );
            }
         } else {
            return Response.status( Response.Status.BAD_REQUEST ).entity( "No space was found with the space-key " + parameterMap.get("space-key") ).build();
         }
      } else {
         return Response.status( Response.Status.BAD_REQUEST ).entity( "Parameter space-key was not provided" ).build();
      }

      return Response.ok(updates).build();
   }

   @GET
   @Path("empty-trash-in-space")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response emptyTrashInSpace(@Context HttpServletRequest request)
   {
      String username = userManager.getRemoteUsername(request);
      if (username == null || !userManager.isSystemAdmin(username)){
         return Response.status(Status.UNAUTHORIZED).build();
      }

      boolean bEmptyTrash = false;
      List<String> updates = new ArrayList<String>();
      Map<String, String[]> parameterMap = request.getParameterMap();
      String baseUrl = settingsManager.getGlobalSettings().getBaseUrl();

      if( parameterMap.containsKey( "mode" ) && parameterMap.get( "mode" )[0].matches( "empty-space-trash" ) ){
         bEmptyTrash = true;
      }

      if( parameterMap.containsKey( "space-key" ) && !StringUtils.isEmpty(parameterMap.get( "space-key" )[0]) ){
         Space space = spaceManager.getSpace(parameterMap.get("space-key")[0]);
         if( space != null ){
            if( bEmptyTrash ){
               if( parameterMap.containsKey( "return-space-key" ) && parameterMap.get( "return-space-key" )[0].matches( "true" ) ){
                  updates.add( space.getKey() );
               }
               updates.add( String.format( "Emptied %d items in the trash of space %s (%s).",
                                      trashManager.getNumberOfItemsInTrash(space), space.getName(), space.getKey() ) );
               trashManager.emptyTrash( space );
            } else {
               updates.add( String.format( "<div id=\"empty-trash-in-%s\"><a href=\"#\" empty-trash-in-space-with-key=\"%s\" class=\"empty-trash-in-space-anchor\">Empty Trash:</a> There are %d items in the trash of space %s (%s).",
                                           space.getKey(), space.getKey(), 
                                           trashManager.getNumberOfItemsInTrash(space), 
                                           space.getName(), 
                                           space.getKey() ) );
            }
         } else {
            return Response.status( Response.Status.BAD_REQUEST ).entity( "No space was found with the space-key " + parameterMap.get("space-key") ).build();
         }
      } else {
         return Response.status( Response.Status.BAD_REQUEST ).entity( "Parameter space-key was not provided" ).build();
      }

      return Response.ok(updates).build();
   }

   @GET
   @Path("fix-titleless-pages-in-space")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response fixTitlelessPagesInSpace(@Context HttpServletRequest request)
   {
      String username = userManager.getRemoteUsername(request);
      if (username == null || !userManager.isSystemAdmin(username)){
         return Response.status(Status.UNAUTHORIZED).build();
      }

      boolean bFixPages = false;
      List<String> updates = new ArrayList<String>();
      Map<String, String[]> parameterMap = request.getParameterMap();

      if( parameterMap.containsKey( "mode" ) && parameterMap.get( "mode" )[0].matches( "fix-titleless-pages" ) ){
         bFixPages = true;
      }

      if( parameterMap.containsKey( "space-key" ) && !StringUtils.isEmpty(parameterMap.get( "space-key" )[0]) ){
         Space space = spaceManager.getSpace(parameterMap.get("space-key")[0]);
         if( space != null ){
            for( Long id : pageManager.getPageIds( space ) ){
               if( bFixPages ){
                  String newTitle = fixPageTitle( id );
                  if( newTitle != null ){
                     updates.add( fixedPageNameText( id, space, newTitle ) );
                  }
               } else {
                  if( pageIsMissingTitle( id ) ){
                     updates.add( couldFixPageNameText( id, space ) );
                  }
               }
            }
         } else {
            return Response.status( Response.Status.BAD_REQUEST ).entity( "No space was found with the space-key " + parameterMap.get("space-key") ).build();
         }
      } else {
         return Response.status( Response.Status.BAD_REQUEST ).entity( "Parameter space-key was not provided" ).build();
      }

      return Response.ok(updates).build();
   }

   @GET
   @Path("fix-titleless-page")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response fixUnnamedPage(@Context HttpServletRequest request)
   {
      String username = userManager.getRemoteUsername(request);
      if (username == null || !userManager.isSystemAdmin(username)){
         return Response.status(Status.UNAUTHORIZED).build();
      }

      Map<String, String[]> parameterMap = request.getParameterMap();
      List<String> updates = new ArrayList<String>();

      if( parameterMap.containsKey("page-id") && !StringUtils.isEmpty(parameterMap.get("page-id")[0]) ){
         Long pageId = Long.parseLong( parameterMap.get("page-id")[0] );
         AbstractPage page = pageManager.getAbstractPage( pageId );
         if( page != null ){
            String newName = fixPageTitle( pageId );
            if( newName != null ){
               updates.add( String.format( "%d", pageId ) );
               updates.add( fixedPageNameText( pageId, page.getSpace(), newName ) );
            } else {
               return Response.status( Response.Status.BAD_REQUEST ).entity( 
                      String.format( "The page identified by page-id %d already has a page title of %s", pageId, page.getTitle())).build();
            }
         } else {
            return Response.status( Response.Status.BAD_REQUEST ).entity( String.format("Could not find a page with the id of %d.", pageId ) ).build();
         }
      } else {
         return Response.status( Response.Status.BAD_REQUEST ).entity( "Parameter page-id was not provided" ).build();
      }

      return Response.ok(updates).build();
   }

   @GET
   @Path("test-replace-text")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response testReplaceText(@Context HttpServletRequest request)
   {
      String username = userManager.getRemoteUsername(request);
      if (username == null || !userManager.isSystemAdmin(username)){
         return Response.status(Status.UNAUTHORIZED).build();
      }

      Map<String, String[]> parameterMap = request.getParameterMap();
      String originalText = "";
      String patternText = "";
      String replacementText = "";
      List<String> response = new ArrayList<String>();
      StringBuilder html = new StringBuilder();
      boolean bOK = true;

      if( parameterMap.containsKey( ORIGINAL_TEXT ) && !StringUtils.isEmpty( parameterMap.get( ORIGINAL_TEXT )[0] ) ){
         originalText = parameterMap.get(ORIGINAL_TEXT)[0];
      }

      if( parameterMap.containsKey( PATTERN ) && !StringUtils.isEmpty( parameterMap.get( PATTERN )[0] ) ){
         patternText = parameterMap.get(PATTERN)[0];
      }

      if( parameterMap.containsKey( REPLACEMENT_TEXT ) && !StringUtils.isEmpty( parameterMap.get( REPLACEMENT_TEXT )[0] ) ){
         replacementText = parameterMap.get(REPLACEMENT_TEXT)[0];
      }

      if( StringUtils.isEmpty( originalText ) ){
         html.append( "Original Text is empty.  Nothing to work on.\n" );
         bOK = false;
      }

      if( StringUtils.isEmpty( patternText ) ){
         html.append( "Pattern Text is empty.  Unable to execute.\n" );
         bOK = false;
      }

      if( bOK ){
         try{
            html.append( replaceText( originalText, patternText, replacementText ) );
         } catch( Exception exception ){
            html.append( exception.getMessage() );
         }
      }
      response.add( html.toString() );
      return Response.ok( response ).build();
   }

   @GET
   @Path("validate-groups")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response validateGroups(@Context HttpServletRequest request)
   {
      String username = userManager.getRemoteUsername(request);
      if (username == null || !userManager.isSystemAdmin(username)){
         return Response.status(Status.UNAUTHORIZED).build();
      }

      Map<String, String[]> parameterMap = request.getParameterMap();
      List<String> response = new ArrayList<String>();
      List<String> invalidGroups = new ArrayList<String>();
      StringBuilder responseText = new StringBuilder();
      boolean bOK = true;

      if( parameterMap.containsKey( GROUPS ) ){
         for( String group : parameterMap.get( GROUPS ) ){
            if( StringUtils.isEmpty( group ) ){
               bOK = false;       
            } else if( !isValidGroup( group ) ){
               invalidGroups.add( group );
               bOK = false;       
            }
         }
      } else {
         bOK = false;
      }

      if( bOK ){
         response.add( "OK" );
      } else {
         if( invalidGroups.size() == 1 ){
            response.add( "The group \"" + invalidGroups.get(0) + "\" does not exist." );
         } else {
            response.add( "The groups \"" + String.join("\", \"", invalidGroups) + "\" do not exist." );
         }
      }

      return Response.ok( response ).build();
   }

   @GET
   @Path("fix-anonymous-access-issues")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Response fixAnonymousAccessIssues(@Context HttpServletRequest request)
   {
      String username = userManager.getRemoteUsername(request);
      if (username == null || !userManager.isSystemAdmin(username)){
         return Response.status(Status.UNAUTHORIZED).build();
      }

      Map<String, String[]> parameterMap = request.getParameterMap();
      ConfluenceUser anonymousUser = userAccessor.getUserByName( "Anonymous" );
      List<String> response = new ArrayList<String>();
      String[] groups = new String[0];
      String[] allPermissions = new String[0];
      boolean bFixIssues = false;

      if( parameterMap.containsKey( "mode" ) && parameterMap.get( "mode" )[0].matches( "fix-issues" ) ){
         bFixIssues = true;
      }

      if( parameterMap.containsKey( "groups" ) ){
         groups = parameterMap.get( "groups" );
      }

      if( parameterMap.containsKey( "permissions" ) ){
         allPermissions = parameterMap.get( "permissions" );
      } else {
         allPermissions = SpacePermission.GENERIC_SPACE_PERMISSIONS.toArray( new String[ SpacePermission.GENERIC_SPACE_PERMISSIONS.size() ]);
      }


      if( parameterMap.containsKey( "space-key" ) && !StringUtils.isEmpty(parameterMap.get( "space-key" )[0]) ){
         Space space = spaceManager.getSpace(parameterMap.get("space-key")[0]);
         String niceSpaceKey = space.getKey().replaceAll("~", "--tilda--");

         for( String spacePermission : allPermissions ){
            List<String> spacePermissions = new ArrayList<String>();
            spacePermissions.add( spacePermission );
            for( String group : groups ){
               if( spacePermissionManager.hasPermission( spacePermissions, space, anonymousUser )
                   && !spacePermissionManager.groupHasPermission( spacePermission, space, group ) )
               {
                  if( parameterMap.containsKey( "return-space-key" ) && parameterMap.get( "return-space-key" )[0].matches( "true" ) ){
                     response.add( spacePermission );
                     response.add( group );
                     response.add( niceSpaceKey );
                  }

                  if( bFixIssues ){
                     response.add( "Granted group " + group + " " + spacePermission + " in space " + space.getName() );
                     spacePermissionManager.savePermission(
                        SpacePermission.createGroupSpacePermission(spacePermission, space, group)
                     );
                  } else {
                     response.add( String.format( "<div id=\"fix-anonymous-access-issues-for-%s-for-group-%s-in-space-%s\">"
                                                     + "<a href=\"#\" "
                                                     +    "fix-anonymous-access-issues-for-permission=\"%s\" "
                                                     +    "fix-anonymous-access-issues-for-group=\"%s\" "
                                                     +    "fix-anonymous-access-issues-in-space-with-key=\"%s\" "
                                                     +    "class=\"fix-anonymous-access-issue-in-space-anchor\">Fix Issue:</a>"
                                                     + "Grant group %s %s permission in space %s (%s).",
                                           spacePermission, group, niceSpaceKey, 
                                           spacePermission, group, space.getKey(),
                                           group,
                                           spacePermission,
                                           space.getName(), 
                                           space.getKey() ) );
                  }
               }
            }
         }
      }

      return Response.ok( response ).build();
   }


   private boolean isValidGroup( String groupName ){
      Group group = null;
      try{
         group = groupManager.getGroup( groupName );
      } catch( Exception exception ){
      }

      return group != null;
   }

   private String replaceText( String originalText, String pattern, String replacement ){
      return originalText.replaceAll( pattern, replacement );
   }

   private boolean pageIsMissingTitle( Long pageId )
   {
       boolean bFlag = false;
       AbstractPage page = pageManager.getAbstractPage( pageId );
       if( !page.isDraft() && StringUtils.isEmpty( page.getTitle() ) ){
          bFlag = true;
       }

       return bFlag;
   }

   private String fixPageTitle( Long pageId )
   {
       String newNameBase = "RenamedPage";
       String newName = null;
       AbstractPage page = pageManager.getAbstractPage( pageId );
       if( !page.isDraft() && StringUtils.isEmpty( page.getTitle() ) ){
          newName = String.format( "%s%d", newNameBase, pageId );
          pageManager.renamePageWithoutNotifications( page, newName );
       }
       return newName;
   }

   private String fixedPageNameText( Long id, Space space, String newName ){
      String baseUrl = settingsManager.getGlobalSettings().getBaseUrl();
      return String.format("Fixed the name of the page with id %d in the space %s.  It is now <a href=\"%s%d\">%s</a>.", 
                      id, space.getName(), baseUrl + "/pages/viewpage.action?pageId=", id, newName );
   }

   private String couldFixPageNameText( Long id, Space space ){
      String baseUrl = settingsManager.getGlobalSettings().getBaseUrl();
      return String.format("<div id=\"fixpage-%d\"><a href=\"#\" id-of-page-to-fix=\"%d\" class=\"keysight-fix-page-anchor\">FIX IT:</a> The <a href=\"%s%d\">page with id %d in the space %s</a> has an invalid name.", 
                           id, id, baseUrl + "/pages/viewpage.action?pageId=", id, id, space.getName() );

   }

   private String updatePagesInSpace( Space space, final String patternText, final String replacementText )
   {
      DefaultSaveContext saveContext = new DefaultSaveContext( true, false, true );
      int updatedCount = 0;
      List<SpaceContentEntityObject> items = new ArrayList<SpaceContentEntityObject>();
      items.addAll( pageManager.getPages( space, true ) );
      items.addAll( pageManager.getBlogPosts( space, true ) );

      for( SpaceContentEntityObject item : items ){
         body = item.getBodyAsString();
         if( body.matches( ".*" + patternText + ".*" ) ){
            pageManager.<SpaceContentEntityObject>saveNewVersion( item, new Modification<SpaceContentEntityObject>(){
                                           public void modify(SpaceContentEntityObject modifiedItem){
                                              modifiedItem.setBodyAsString( replaceText( body, patternText, replacementText ) );
                                           }
                                        } );
            updatedCount++;
         }
      }

      return( String.format( "Replaced text on %d of %d pages and posts in space %s (%s).",
                                           updatedCount,
                                           items.size(),
                                           space.getName(), 
                                           space.getKey() ) );
   }
}
