package com.keysight.glossary.space;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.createcontent.api.events.SpaceBlueprintCreateEvent;
import com.atlassian.confluence.plugins.createcontent.api.events.SpaceBlueprintHomePageCreateEvent;
import com.atlassian.confluence.plugins.ia.service.SidebarLinkService;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.SpaceLabelManager;
import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.renderer.v2.components.HtmlEscaper;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class GlossarySpaceEventListener implements DisposableBean
{
    private static final String GROUP_ID    = "com.keysight";
    private static final String ARTIFACT_ID = "glossary";
    private static final String MODULE      = "glossary-space-blueprint";
    private static final ModuleCompleteKey BLUEPRINT_KEY = new ModuleCompleteKey( GROUP_ID + "." + ARTIFACT_ID, MODULE );

    private static final String SPACE_CATEGORIES       = "keysight-glossary-space-categories";
    private static final String SPACE_ADMINS           = "administrators";
    private static final String ADMIN_GROUP_UNIQUE_KEY = "admin-group-unique-key";
    private static final String ANONYMOUS_ALLOWED      = "anonymous-allowed";
    private static final String SPACE_DESCRIPTION      = "space-description";

    private final EventPublisher eventPublisher;
    private final PageManager pageManager;
    private final SidebarLinkService sidebarLinkService;
    private final SpaceManager spaceManager;
    private final SpaceLabelManager spaceLabelManager;
    private final SpacePermissionManager spacePermissionManager;
    private final UserAccessor userAccessor;

    @Autowired
    public GlossarySpaceEventListener(EventPublisher eventPublisher, 
                                      PageManager pageManager,
                                      SidebarLinkService sidebarLinkService,
                                      SpaceLabelManager spaceLabelManager,
                                      SpaceManager spaceManager,
                                      SpacePermissionManager spacePermissionManager,
                                      UserAccessor userAccessor )
    {
        this.eventPublisher         = eventPublisher;
        this.pageManager            = pageManager;
        this.sidebarLinkService     = sidebarLinkService;
        this.spaceLabelManager      = spaceLabelManager;
        this.spaceManager           = spaceManager;
        this.spacePermissionManager = spacePermissionManager;
        this.userAccessor           = userAccessor;

        eventPublisher.register(this);
    }

    @EventListener
    public void onSpaceCreated(SpaceBlueprintCreateEvent event) {
        if (!BLUEPRINT_KEY.getCompleteKey().equals(event.getSpaceBlueprint().getModuleCompleteKey())) {
            return;
        }

        ConfluenceUser user;
        Map <String, Object> eventContext = event.getContext();
        Space space                       = event.getSpace();
        SpaceDescription spaceDescription = space.getDescription();
        String group = "";
        boolean bGrantCurrentUserAdminRights = true; 
        boolean bAdminGroupsSet = false;

        spacePermissionManager.removeAllPermissions(space);

        group = "TEAM-CONFL-2-ADMIN";
        spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission(SpacePermission.VIEWSPACE_PERMISSION, space, group));
        spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission(SpacePermission.REMOVE_OWN_CONTENT_PERMISSION, space, group));
        spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission(SpacePermission.CREATEEDIT_PAGE_PERMISSION, space, group));
        spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission(SpacePermission.REMOVE_PAGE_PERMISSION, space, group));
        spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission(SpacePermission.EDITBLOG_PERMISSION, space, group));
        spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission(SpacePermission.REMOVE_BLOG_PERMISSION, space, group));
        spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission(SpacePermission.COMMENT_PERMISSION, space, group));
        spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission(SpacePermission.REMOVE_COMMENT_PERMISSION, space, group));
        spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission(SpacePermission.CREATE_ATTACHMENT_PERMISSION, space, group));
        spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission(SpacePermission.REMOVE_ATTACHMENT_PERMISSION, space, group));
        spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission(SpacePermission.SET_PAGE_PERMISSIONS_PERMISSION, space, group));
        spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission(SpacePermission.REMOVE_MAIL_PERMISSION, space, group));
        spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission(SpacePermission.EXPORT_SPACE_PERMISSION, space, group));
        spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission(SpacePermission.ADMINISTER_SPACE_PERMISSION, space, group));

        List<String> currentUsersGroups = userAccessor.getGroupNames( AuthenticatedUserThreadLocal.get() );
        for( String groupName : currentUsersGroups ){
            if( groupName.equals( "TEAM-CONFL-2-ADMIN" ) ){
                bGrantCurrentUserAdminRights = false; 
            }
        }

        if( (eventContext.containsKey( SPACE_ADMINS ) && !StringUtils.isEmpty( (String) eventContext.get( SPACE_ADMINS ) ) ) || bGrantCurrentUserAdminRights ){
            String spaceAdminList = AuthenticatedUserThreadLocal.get().getName();

            if( eventContext.containsKey( SPACE_ADMINS ) && !StringUtils.isEmpty( (String) eventContext.get( SPACE_ADMINS ) ) && bGrantCurrentUserAdminRights ){
                spaceAdminList = (String) eventContext.get(SPACE_ADMINS) + "," + AuthenticatedUserThreadLocal.get().getName();
            } else if( eventContext.containsKey( SPACE_ADMINS ) && !StringUtils.isEmpty( (String) eventContext.get( SPACE_ADMINS ) ) ) {
                spaceAdminList = (String) eventContext.get(SPACE_ADMINS);
            }

            String[] spaceAdmins  = spaceAdminList.split( "[,\\s]+" );
           
            for( int i = 0; i < spaceAdmins.length; i++ ){
                user = userAccessor.getUserByName(spaceAdmins[i]);
                if( user != null ){
                    spacePermissionManager.savePermission(SpacePermission.createUserSpacePermission(SpacePermission.VIEWSPACE_PERMISSION, space, user));
                    spacePermissionManager.savePermission(SpacePermission.createUserSpacePermission(SpacePermission.REMOVE_OWN_CONTENT_PERMISSION, space, user));
                    spacePermissionManager.savePermission(SpacePermission.createUserSpacePermission(SpacePermission.CREATEEDIT_PAGE_PERMISSION, space, user));
                    spacePermissionManager.savePermission(SpacePermission.createUserSpacePermission(SpacePermission.REMOVE_PAGE_PERMISSION, space, user));
                    spacePermissionManager.savePermission(SpacePermission.createUserSpacePermission(SpacePermission.EDITBLOG_PERMISSION, space, user));
                    spacePermissionManager.savePermission(SpacePermission.createUserSpacePermission(SpacePermission.REMOVE_BLOG_PERMISSION, space, user));
                    spacePermissionManager.savePermission(SpacePermission.createUserSpacePermission(SpacePermission.COMMENT_PERMISSION, space, user));
                    spacePermissionManager.savePermission(SpacePermission.createUserSpacePermission(SpacePermission.REMOVE_COMMENT_PERMISSION, space, user));
                    spacePermissionManager.savePermission(SpacePermission.createUserSpacePermission(SpacePermission.CREATE_ATTACHMENT_PERMISSION, space, user));
                    spacePermissionManager.savePermission(SpacePermission.createUserSpacePermission(SpacePermission.REMOVE_ATTACHMENT_PERMISSION, space, user));
                    spacePermissionManager.savePermission(SpacePermission.createUserSpacePermission(SpacePermission.SET_PAGE_PERMISSIONS_PERMISSION, space, user));
                    spacePermissionManager.savePermission(SpacePermission.createUserSpacePermission(SpacePermission.REMOVE_MAIL_PERMISSION, space, user));
                    spacePermissionManager.savePermission(SpacePermission.createUserSpacePermission(SpacePermission.EXPORT_SPACE_PERMISSION, space, user));
                    spacePermissionManager.savePermission(SpacePermission.createUserSpacePermission(SpacePermission.ADMINISTER_SPACE_PERMISSION, space, user));
                }
            }
        }

        group = "TEAM-CONFL-2-ALL-USERS";
        spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission(SpacePermission.VIEWSPACE_PERMISSION, space, group));
        spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission(SpacePermission.REMOVE_OWN_CONTENT_PERMISSION, space, group));
        spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission(SpacePermission.COMMENT_PERMISSION, space, group));

        group = "ORG-ALL-KEYSIGHT";
        spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission(SpacePermission.VIEWSPACE_PERMISSION, space, group));
        spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission(SpacePermission.REMOVE_OWN_CONTENT_PERMISSION, space, group));
        spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission(SpacePermission.COMMENT_PERMISSION, space, group));

        user = userAccessor.getUserByName("Anonymous");
        spacePermissionManager.savePermission(SpacePermission.createUserSpacePermission(SpacePermission.VIEWSPACE_PERMISSION, space, user));

        if( eventContext.containsKey( SPACE_DESCRIPTION ) ){
            spaceDescription.setBodyAsString( (String) eventContext.get( SPACE_DESCRIPTION ) );
        }

        return;
    }

    @EventListener
    public void onSpaceHomePageCreated(SpaceBlueprintHomePageCreateEvent event) {
        if (!BLUEPRINT_KEY.getCompleteKey().equals(event.getSpaceBlueprint().getModuleCompleteKey())) {
            return;
        }

        Map <String, Object> eventContext = event.getContext();
        Space space                       = event.getSpace();
        SpaceDescription spaceDescription = space.getDescription();
        Label spaceCategory;
        String[] alphabet = new String[]{ "&", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
                                          "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

        if( eventContext.containsKey( SPACE_CATEGORIES ) && !StringUtils.isEmpty( (String) eventContext.get( SPACE_CATEGORIES ) ) ){
           String spaceCategoriesList = (String) eventContext.get(SPACE_CATEGORIES);
           String[] spaceCategories = spaceCategoriesList.split( "[,\\s]+" );

           for( int i = 0; i < spaceCategories.length; i++ ){
              if( !isCurrentSpaceCategory( spaceDescription, spaceCategories[i] )){
                 spaceCategory = spaceLabelManager.addLabel( space, spaceCategories[i] );
                 if( spaceCategory == null ){
                    System.out.println( "Failed to add Space Category: " + spaceCategories[i] );
                 }
              }
           }
        }

        for( String letter : alphabet ){
           Page newPage = new Page();
           newPage.setTitle( letter );
           newPage.setSpace( space );
           newPage.setParent( space.getHomePage() );
           newPage.setBodyAsString( "<p><ac:structured-macro ac:name=\"include-children\" ac:schema-version=\"1\"/></p>\n" );
           pageManager.saveContentEntity( newPage, null );
        }

        return;
    }

    @Override
    public void destroy() throws Exception
    {
        eventPublisher.unregister(this);
    }

    private boolean isCurrentSpaceCategory( SpaceDescription spaceDescription, String spaceCategory ){
      boolean bFlag = false;
      List<Label> currentSpaceCategories = spaceDescription.getLabels();       
      for( Label currentSpaceCategory : currentSpaceCategories ){
         if( currentSpaceCategory.getName().equals( spaceCategory ) ){
            bFlag = true;
         }
      }
      return bFlag;
   }
}
