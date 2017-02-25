package com.keysight.html.elements.helpers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;

import java.util.Map;
import java.util.List;

public class NavigationPageResolver
{
   protected final PageManager pageManager;
   protected final PermissionManager permissionManager;
   protected final SettingsManager settingsManager;
   protected final SpaceManager spaceManager;

   public final static String SHOW_TITLE_KEY           = "show-title";
   public final static String PREVIOUS_BUTTON_TEXT_KEY = "previous-button-text";
   public final static String PARENT_BUTTON_TEXT_KEY   = "parent-button-text";
   public final static String NEXT_BUTTON_TEXT_KEY     = "next-button-text";

   public NavigationPageResolver( PageManager pageManager,
                                  PermissionManager permissionManager,
                                  SettingsManager settingsManager,
                                  SpaceManager spaceManager )
   {
      this.pageManager = pageManager;
      this.permissionManager = permissionManager;
      this.settingsManager = settingsManager;
      this.spaceManager = spaceManager;
   }

   public String[] resolveUrlAndTextForNextPage( Map<String, String> parameters, ConversionContext context){  
      String baseUrl = settingsManager.getGlobalSettings().getBaseUrl();
      String[] parts = new String[0];
      Page nextPage = null;
      
      ContentEntityObject contentEntityObject = context.getEntity();
      if( contentEntityObject instanceof Page ){
         Page page = (Page) contentEntityObject;

         nextPage = getFirstVisibleChildPage( page );
         if( nextPage == null ){
            nextPage = getNextVisibleSiblingOrParentPage( page );
         }

         if( nextPage != null ){
            parts = new String[2];       
            parts[0] = baseUrl + nextPage.getUrlPath();
            parts[1] = nextPage.getTitle();

            if( parameters.containsKey( NEXT_BUTTON_TEXT_KEY ) ){
               parts[1] = parameters.get( NEXT_BUTTON_TEXT_KEY ).replace( "$title", parts[1] );
            } else if( !parameters.containsKey( SHOW_TITLE_KEY ) ){
               parts[1] = ">";
            }
         }
      }
      return parts;
   }

   public String[] resolveUrlAndTextForPreviousPage( Map<String, String> parameters, ConversionContext context){  
      String baseUrl = settingsManager.getGlobalSettings().getBaseUrl();
      String[] parts = new String[0];
      Page previousPage = null;
      
      ContentEntityObject contentEntityObject = context.getEntity();
      if( contentEntityObject instanceof Page ){
         Page page = (Page) contentEntityObject;
         
         // If the current page is the root or home page, there will be no previous page by definition
         if( !page.isRootLevel() && !page.isHomePage() ){
            previousPage = getPreviousVisibleSiblingOrParentPage( page );
         }

         if( previousPage != null ){
            parts = new String[2];       
            parts[0] = baseUrl + previousPage.getUrlPath();
            parts[1] = previousPage.getTitle();

            if( parameters.containsKey( PREVIOUS_BUTTON_TEXT_KEY ) ){
               parts[1] = parameters.get( PREVIOUS_BUTTON_TEXT_KEY ).replace( "$title", parts[1] );
            } else if( !parameters.containsKey( SHOW_TITLE_KEY ) ){
               parts[1] = "<";
            }
         }
      }

      return parts;
   }

   public String[] resolveUrlAndTextForParentPage( Map<String, String> parameters, ConversionContext context){  
      String baseUrl = settingsManager.getGlobalSettings().getBaseUrl();
      String[] parts = new String[0];
      Page parentPage = null;
      ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
      
      ContentEntityObject contentEntityObject = context.getEntity();
      if( contentEntityObject instanceof Page ){
         Page page = (Page) contentEntityObject;
         
         if( !page.isRootLevel() && !page.isHomePage() ){
            // checking visibility of the parent is somewhat silly, as you should be seeing
            // the child if you can't see the parent; however, checking is proper.
            if( permissionManager.hasPermission(currentUser, Permission.VIEW, page.getParent()) ){
               parentPage = page.getParent();
            } else {
               parentPage = getPreviousVisibleSiblingOrParentPage( page.getParent() );
            }
         }
        
         if( parentPage != null ){
            parts = new String[2];       
            parts[0] = baseUrl + parentPage.getUrlPath();
            parts[1] = parentPage.getTitle();

            if( parameters.containsKey( PARENT_BUTTON_TEXT_KEY ) ){
               parts[1] = parameters.get( PARENT_BUTTON_TEXT_KEY ).replace( "$title", parts[1] );
            } else if( !parameters.containsKey( SHOW_TITLE_KEY ) ){
               parts[1] = "^";
            }
         }
      }
      return parts;
   }

   private Page getFirstVisibleChildPage( Page page ){
      Page firstVisibleChildPage = null;
      ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();

      if( page.hasChildren() ){
         for( Page childPage : page.getSortedChildren() ){
            if( permissionManager.hasPermission(currentUser, Permission.VIEW, childPage) ){
               firstVisibleChildPage = childPage;
               break;
            }
         }
      }

      return( firstVisibleChildPage );
   }

   private Page getNextVisibleSiblingOrParentPage( Page page ){
      Page nextVisiblePage = null;
      ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();

      // If the page is the root or home page, there will be no next sibling by definition.
      if( !page.isRootLevel() && !page.isHomePage() ){

         List<Page> siblings = page.getParent().getSortedChildren();
         for( int i = 0; i < (siblings.size() - 1); i++ ){
            Page siblingPage = siblings.get(i);
            if( permissionManager.hasPermission(currentUser, Permission.VIEW, siblingPage) ){
               if( page.equals(siblingPage) ) {
                  for( int j = i + 1; j < siblings.size(); j++ ){
                     Page nextSibling = siblings.get(j);
                     if( permissionManager.hasPermission(currentUser, Permission.VIEW, nextSibling) ){
                        nextVisiblePage = nextSibling;
                        break;
                     }
                  }
                  break;
               }
            }
         }

         // Did not find a next sibling, so get the next sibling of the parent page
         if( nextVisiblePage == null ){
            nextVisiblePage = getNextVisibleSiblingOrParentPage( page.getParent() );
         }

      }

      return( nextVisiblePage );
   }

   private Page getPreviousVisibleSiblingOrParentPage( Page page ){
      Page previousVisiblePage = null;
      Page visiblePageFromLastIteration = null;
      ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();

      List<Page> siblings = page.getParent().getSortedChildren();
      for( int i = 1; i < siblings.size(); i++ ){
         Page siblingPage = siblings.get(i);
         if( permissionManager.hasPermission(currentUser, Permission.VIEW, siblingPage) ){
            if( page.equals(siblingPage) ) {
               previousVisiblePage = visiblePageFromLastIteration;
               break;
            }
            visiblePageFromLastIteration = siblingPage;;
         }
      }

      if( previousVisiblePage == null ){
         // checking visibility of the parent is somewhat silly, as you should be seeing
         // the child if you can't see the parent; however, checking is proper.
         if( permissionManager.hasPermission(currentUser, Permission.VIEW, page.getParent()) ){
            previousVisiblePage = page.getParent();
         } else {
            previousVisiblePage = getPreviousVisibleSiblingOrParentPage( page.getParent() );
         }
      }

      return( previousVisiblePage );
   }
}
