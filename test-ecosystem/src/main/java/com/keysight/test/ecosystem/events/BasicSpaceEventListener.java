package com.keysight.test.ecosystem.events;

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
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.renderer.v2.components.HtmlEscaper;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class BasicSpaceEventListener implements DisposableBean
{
    private static final String GROUP_ID    = "com.keysight";
    private static final String ARTIFACT_ID = "test-ecosystem";
    private static final String MODULE      = "test-ecosystem-basic-space-blueprint";
    private static final ModuleCompleteKey BLUEPRINT_KEY = new ModuleCompleteKey( GROUP_ID + "." + ARTIFACT_ID, MODULE );

    private final EventPublisher eventPublisher;
    private final PageManager pageManager;
    private final SidebarLinkService sidebarLinkService;
    private final SpaceManager spaceManager;
    private final SpacePermissionManager spacePermissionManager;
    private final UserAccessor userAccessor;

    @Autowired
    public BasicSpaceEventListener(EventPublisher eventPublisher, 
                                   PageManager pageManager,
                                   SidebarLinkService sidebarLinkService,
                                   SpaceManager spaceManager,
                                   SpacePermissionManager spacePermissionManager,
                                   UserAccessor userAccessor )
    {
        this.eventPublisher         = eventPublisher;
        this.pageManager            = pageManager;
        this.sidebarLinkService     = sidebarLinkService;
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
        
        String group = "";
        
        final Space space = event.getSpace();
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
	
        group = "TEAM-CONFL-2-ALL-USERS";
	    spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission(SpacePermission.VIEWSPACE_PERMISSION, space, group));
	    spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission(SpacePermission.REMOVE_OWN_CONTENT_PERMISSION, space, group));
        spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission(SpacePermission.COMMENT_PERMISSION, space, group));
        
	    group = "TEAM-CONFL-2-TEST-ECOSYSTEM-ADMIN";
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
	
	    group = "TEAM-CONFL-2-TEST-ECOSYSTEM-AUTHORS";
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
	
	    group = "TEAM-CONFL-2-TEST-ECOSYSTEM-USERS";
	    spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission(SpacePermission.VIEWSPACE_PERMISSION, space, group));
	    spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission(SpacePermission.REMOVE_OWN_CONTENT_PERMISSION, space, group));
        spacePermissionManager.savePermission(SpacePermission.createGroupSpacePermission(SpacePermission.COMMENT_PERMISSION, space, group));
    
        final ConfluenceUser user = userAccessor.getUserByName("Anonymous");
        spacePermissionManager.savePermission(SpacePermission.createUserSpacePermission(SpacePermission.VIEWSPACE_PERMISSION, space, user));
      
        return;
    }

    @EventListener
    public void onSpaceHomePageCreated(SpaceBlueprintHomePageCreateEvent event) {
        if (!BLUEPRINT_KEY.getCompleteKey().equals(event.getSpaceBlueprint().getModuleCompleteKey())) {
            return;
        }

	final Space space = event.getSpace();
  
        // Add the test ecosystem logo
	// Need to add the image as an attachment to the SpaceDescription titled with the space key
	
        // Add the Test Ecosystem space as a sidebar shortcut	
        try{
        final Space testEcosystem = spaceManager.getSpace( "TestEcosystem" );
        if( testEcosystem != null ){
           long id = testEcosystem.getHomePage().getId();
           boolean exists = sidebarLinkService.hasQuickLink(space.getKey(), id);
           if( !exists ){
              // sidebarLinkService.create( <spaceKey>, <pageId>, <customTitle>, <url>, <iconClass> )
              sidebarLinkService.create( space.getKey(), null, "Test Ecosystem", testEcosystem.getUrlPath(), "pinned_space" );         
           }
        }
        } catch( Exception e ){ }
	
	    ContentPermission adminEditPermission = ContentPermission.createGroupPermission( ContentPermission.EDIT_PERMISSION, "TEAM-CONFL-2-TEST-ECOSYSTEM-ADMIN" );

        Page adminPage = new Page();
        adminPage.setTitle( "Admin Page" );
        adminPage.setSpace( space );
        adminPage.setParent( space.getHomePage() );
        adminPage.setBodyAsString( "<p><ac:structured-macro ac:name=\"children\"/></p>\n" );
        adminPage.addPermission( adminEditPermission );
	    pageManager.saveContentEntity( adminPage, null );
    
        return;
    }

    @Override
    public void destroy() throws Exception
    {
        eventPublisher.unregister(this);
    }
}
