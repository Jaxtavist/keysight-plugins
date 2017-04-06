package com.keysight.keysight.theme.events;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.createcontent.api.events.SpaceBlueprintCreateEvent;
import com.atlassian.confluence.plugins.createcontent.api.events.SpaceBlueprintHomePageCreateEvent;
import com.atlassian.confluence.plugins.ia.service.SidebarLinkService;
import com.atlassian.confluence.security.ContentPermission;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.confluence.spaces.SpaceDescriptionManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.SpaceLabelManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.renderer.v2.components.HtmlEscaper;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.atlassian.confluence.mail.template.ConfluenceMailQueueItem;
import com.atlassian.core.task.MultiQueueTaskManager;
import com.atlassian.mail.queue.MailQueueItem;
import static com.atlassian.confluence.mail.template.ConfluenceMailQueueItem.MIME_TYPE_HTML;
import static com.atlassian.confluence.mail.template.ConfluenceMailQueueItem.MIME_TYPE_TEXT;
import com.keysight.keysight.theme.helpers.MailService;
import com.keysight.keysight.theme.helpers.MailServiceImpl;

@Component
public class KeysightSpaceEventListener implements DisposableBean
{
    private static final String GROUP_ID    = "com.keysight";
    private static final String ARTIFACT_ID = "keysight-theme";
    private static final String MODULE      = "keysight-space-blueprint";
    private static final ModuleCompleteKey BLUEPRINT_KEY = new ModuleCompleteKey( GROUP_ID + "." + ARTIFACT_ID, MODULE );

    private static final String SPACE_CATEGORIES       = "keysight-theme-space-categories";
    private static final String SPACE_ADMINS           = "administrators";
    private static final String ADMIN_GROUP_UNIQUE_KEY = "admin-group-unique-key";
    private static final String ANONYMOUS_ALLOWED      = "anonymous-allowed";
    private static final String SPACE_DESCRIPTION      = "space-description";

    private final EventPublisher eventPublisher;
    private final LabelManager labelManager;
    private final SpaceLabelManager spaceLabelManager;
    private final PageManager pageManager;
    private final PermissionManager permissionManager;
    private final SidebarLinkService sidebarLinkService;
    private final SpaceManager spaceManager;
    private final SpaceDescriptionManager spaceDescriptionManager;
    private final SpacePermissionManager spacePermissionManager;
    private final UserAccessor userAccessor;
    private final MailService mailService;
    private final SettingsManager settingsManager;
    private final MultiQueueTaskManager taskManager;

    @Autowired
    public KeysightSpaceEventListener(EventPublisher eventPublisher, 
                                      LabelManager labelManager,
                                      MultiQueueTaskManager taskManager,
                                      PageManager pageManager,
                                      PermissionManager permissionManager,
                                      SidebarLinkService sidebarLinkService,
                                      SettingsManager settingsManager,
                                      SpaceDescriptionManager spaceDescriptionManager,
                                      SpaceLabelManager spaceLabelManager,
                                      SpaceManager spaceManager,
                                      SpacePermissionManager spacePermissionManager,
                                      UserAccessor userAccessor )
    {
        this.eventPublisher          = eventPublisher;
        this.labelManager            = labelManager;
        this.pageManager             = pageManager;
        this.permissionManager       = permissionManager;
        this.settingsManager         = settingsManager;
        this.sidebarLinkService      = sidebarLinkService;
        this.spaceDescriptionManager = spaceDescriptionManager;
        this.spaceLabelManager       = spaceLabelManager;
        this.spaceManager            = spaceManager;
        this.spacePermissionManager  = spacePermissionManager;
        this.taskManager             = taskManager;
        this.userAccessor            = userAccessor;

    mailService = new MailServiceImpl( taskManager );
        eventPublisher.register(this);
    }

    @EventListener
    public void onSpaceCreated(SpaceBlueprintCreateEvent event)
    {
        if (!BLUEPRINT_KEY.getCompleteKey().equals(event.getSpaceBlueprint().getModuleCompleteKey())) {
            return;
        }
        
        ConfluenceUser user;
        Map <String, Object> eventContext = event.getContext();
        String group                      = "";
        Space space                       = event.getSpace();
        SpaceDescription spaceDescription = space.getDescription();
        String emailSubject;
        StringBuilder emailText = new StringBuilder();
        boolean bGrantCurrentUserAdminRights = true; 
        boolean bAdminGroupsSet = false;

        // First, deal with permissions.
        // Remove the default permissions
        // Add the TEST-CONFL-2-ADMIN group
        // If specified, add the TEAM-CONFL-2-* groups
        // If specified, add individual administrators
        // If specified, add TEAM-CONFL-2-ALL_UESRS and Anonymous user
    
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

        if( eventContext.containsKey( ADMIN_GROUP_UNIQUE_KEY ) && !StringUtils.isEmpty( (String) eventContext.get( ADMIN_GROUP_UNIQUE_KEY ) ) ){
            bAdminGroupsSet = true;
            String adminGroup   = "TEAM-CONFL-2-" + (String) eventContext.get(ADMIN_GROUP_UNIQUE_KEY) + "-ADMIN";
            String authorsGroup = "TEAM-CONFL-2-" + (String) eventContext.get(ADMIN_GROUP_UNIQUE_KEY) + "-AUTHORS";
            String usersGroup   = "TEAM-CONFL-2-" + (String) eventContext.get(ADMIN_GROUP_UNIQUE_KEY) + "-USERS";

            group = adminGroup;
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

            group = authorsGroup;
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
            spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission(SpacePermission.ADMINISTER_SPACE_PERMISSION, space, group));

            if( !eventContext.containsKey( ANONYMOUS_ALLOWED ) ){
                group = usersGroup;
                spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission(SpacePermission.VIEWSPACE_PERMISSION, space, group));
                spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission(SpacePermission.REMOVE_OWN_CONTENT_PERMISSION, space, group));
                spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission(SpacePermission.COMMENT_PERMISSION, space, group));
            }
        }

        List<String> currentUsersGroups = userAccessor.getGroupNames( AuthenticatedUserThreadLocal.get() );
        for( String groupName : currentUsersGroups ){
            if( groupName.equals( "TEAM-CONFL-2-ADMIN" ) ||
                (bAdminGroupsSet && groupName.equals( "TEAM-CONFL-2" + (String) eventContext.get(ADMIN_GROUP_UNIQUE_KEY) + "-ADMIN" ) ) ){
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
           
            emailSubject = "Created Confluence Space: " + space.getName();
            emailText.append( "<h1>Your new space in Confluence is ready!</h1>\n"
                            + "<p><a href=\"" + settingsManager.getGlobalSettings().getBaseUrl() + space.getUrlPath() + "\">"+space.getName()+"</a></p>\n"
                            + "<p>Note, you are receiving this email either because you\n"
                            + "requested the space or the requestor asked for you to be\n"
                            + "added as an administrator of the space.</p>\n"
                            + "<p>If you have any questions or comments, feel free to email "
                            + "the Confluence Support Team at <a href=\"mailto:pdl-confluence-admin@keysight.com\">"
                            + "pdl-confluence-admin@keysight.com</a></p>");

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

                    try{
                        sendEmail( user.getEmail(), emailSubject, emailText.toString() );
                    } catch( Exception e ){
                        System.out.println( "Failed to sending email test." );
                        System.out.println( e );
                    }
                }
            }
        }

        if( eventContext.containsKey( ANONYMOUS_ALLOWED ) ){
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
        }

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
        int status;
        boolean bSomethingToSave = false;

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

    private void sendEmail( String toAddress, String subject, String message ) throws Exception
    {
        MailQueueItem mailQueueItem = new ConfluenceMailQueueItem( toAddress, subject, message, MIME_TYPE_HTML);
        mailService.sendEmail( mailQueueItem );
    }
}
