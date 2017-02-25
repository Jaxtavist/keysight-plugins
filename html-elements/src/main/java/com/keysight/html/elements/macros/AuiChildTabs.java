package com.keysight.html.elements.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.ContentIncludeStack;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.Permission;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import com.keysight.html.elements.helpers.TabInfo;

public class AuiChildTabs extends AuiTabGroup
{
   protected final PageManager pageManager;
   protected final PermissionManager permissionManager;
   protected final Renderer renderer;
   protected ConfluenceUser m_currentUser;

   protected static final String ROOT_PAGE_KEY    = "root";
   protected static final String ERROR            = "Error: ";
   protected static final String NOT_FOUND        = "Page Not Found";
   protected static final String ALREADY_INCLUDED = "The page is already rendered";
   protected static final String REVERSE          = "reverse";

   public AuiChildTabs( PageManager pageManager, 
                        PermissionManager permissionManager,
		                Renderer renderer,
		                VelocityHelperService velocityHelperService,
                        XhtmlContent xhtmlUtils)
   {
      super( velocityHelperService, xhtmlUtils );
      this.pageManager = pageManager;
      this.permissionManager = permissionManager;
      this.renderer = renderer;
   }

   protected ArrayList<TabInfo> getTabs(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException
   {
      ArrayList<TabInfo> tabs = new ArrayList<TabInfo>();
      TabInfo tabInfo = null;
      String[] pageParts = new String[2];
      String pageTitle = null;
      String spaceKey = null;
      Page rootPage = null;
      Page thisPage = (Page) context.getEntity();
      m_currentUser = AuthenticatedUserThreadLocal.get();

      boolean activeTabSet = false;
      boolean hideTab = false;

      if( parameters.containsKey( ROOT_PAGE_KEY ) ){
	     if( parameters.get( ROOT_PAGE_KEY ).matches( ".*:.*" ) ){
            pageParts = parameters.get( ROOT_PAGE_KEY).split( ":", 2 );
            spaceKey  = pageParts[0];
            pageTitle = pageParts[1];
	     } else {
            spaceKey  = context.getSpaceKey();
	        pageTitle = parameters.get( ROOT_PAGE_KEY);
	     }

	     rootPage = pageManager.getPage( spaceKey, pageTitle );
      } else {
         rootPage = (Page) context.getEntity();
      }

      ContentIncludeStack.push((Page) context.getEntity());
      ContentIncludeStack.push(rootPage);
      try{ 
         if( rootPage != null && rootPage.hasChildren() ){
	        List<Page> childPages = rootPage.getSortedChildren();
            for( Page childPage : childPages ){
               hideTab = false;
	           if (ContentIncludeStack.contains(childPage)){
                  tabInfo = new TabInfo( childPage.getTitle(), RenderUtils.blockError( ERROR + " " + ALREADY_INCLUDED, "" ) );
	           } else {
                  ContentIncludeStack.push(childPage);
                  try {
                     DefaultConversionContext childPageContext = new DefaultConversionContext(new PageContext(childPage, context.getPageContext()));

                     if( permissionManager.hasPermission(m_currentUser, Permission.VIEW, childPage) ){
                        tabInfo = new TabInfo( childPage.getTitle(), renderer.render( childPage.getBodyAsString(), childPageContext ) );
                     } else {
                        hideTab = true;
                        tabInfo = new TabInfo( "No View Permission", 
                                               RenderUtils.blockError(ERROR + " You do not have permissions to view page \"" + childPage.getTitle() + "\" in space \"" + childPage.getSpace().getName() + "\".", "" ));
                     }

	              } finally {
                     ContentIncludeStack.pop();
	              }
	           }

               if( !hideTab && !activeTabSet ){
                  tabInfo.setActive( true );
                  activeTabSet = true;
               }
               if( parameters.containsKey( LEFT_TITLE_TRUNCATION_COUNT_KEY ) ){
                  tabInfo.setLeftTitleTruncationCount( parameters.get( LEFT_TITLE_TRUNCATION_COUNT_KEY ) );
               }
               if( parameters.containsKey( RIGHT_TITLE_TRUNCATION_COUNT_KEY ) ){
                  tabInfo.setRightTitleTruncationCount( parameters.get( RIGHT_TITLE_TRUNCATION_COUNT_KEY ) );
               }

               if( !hideTab ){
                  tabs.add( tabInfo );
               }
	        }
         }
      } finally {
         ContentIncludeStack.pop();
         ContentIncludeStack.pop();
      }

      if( parameters.containsKey( REVERSE ) ){
         Collections.reverse( tabs );
      }

      return tabs;
   }

   @Override
   public BodyType getBodyType()
   {
      return BodyType.NONE;
   }
}


