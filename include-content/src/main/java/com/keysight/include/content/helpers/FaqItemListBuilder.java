package com.keysight.include.content.helpers;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;

import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.plugin.services.VelocityHelperService;

import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.MacroDefinitionHandler;
import com.atlassian.confluence.xhtml.api.MacroDefinitionMarshallingStrategy;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.PartialList;
import com.atlassian.confluence.like.LikeManager;
import com.atlassian.confluence.like.Like;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.security.Permission;

import static com.atlassian.confluence.labels.LabelManager.NO_MAX_RESULTS;
import static com.atlassian.confluence.labels.LabelManager.NO_OFFSET;


import com.keysight.include.content.helpers.SharedBlockIncludeStack;
import com.keysight.include.content.helpers.FaqItem;

public class FaqItemListBuilder
{
   private static final String ERROR              = "Error: ";

   private static final String ITEMS_PER_CALL     = "itemsPerCall";
   private static final String LAST_ITEM_INDEX    = "lastItemIndex";
   private static final String LABELS             = "labels";
   private static final String SPACE_KEYS         = "spaceKeys";
   private static final String PAGE_ID            = "pageId";
   private static final String AND_LABELS         = "andLabels";
   private static final String INCLUDE_DECENDANTS = "includeDecendants";
   private static final int SECONDS = 1000;

   private ConfluenceUser m_currentUser;

   private final LabelManager labelManager;
   private final LikeManager likeManager;
   private final PageManager pageManager;
   private final PermissionManager permissionManager;
   private final Renderer renderer;
   private final SettingsManager settingsManager;
   private final SpaceManager spaceManager;
   private final UserManager userManager;
   private final VelocityHelperService velocityHelperService;
   private final XhtmlContent xhtmlUtils;

   public FaqItemListBuilder( LabelManager labelManager,
                              LikeManager likeManager,
                              PageManager pageManager,
                              PermissionManager permissionManager,
                              Renderer renderer,
                              SettingsManager settingsManager,
                              SpaceManager spaceManager,
                              UserManager userManager,
                              VelocityHelperService velocityHelperService,
                              XhtmlContent xhtmlUtils
   ){
       this.labelManager          = labelManager;
       this.likeManager           = likeManager;
       this.pageManager           = pageManager;
       this.permissionManager     = permissionManager;
       this.renderer              = renderer;
       this.settingsManager       = settingsManager;
       this.spaceManager          = spaceManager;
       this.userManager           = userManager;
       this.velocityHelperService = velocityHelperService;
       this.xhtmlUtils            = xhtmlUtils;
   }

   public List<FaqItem> getFaqItems( Map<String, String[]> parameterMap ){
      List<FaqItem> pages = new ArrayList<FaqItem>();
      m_currentUser = AuthenticatedUserThreadLocal.get();

      int offset = 0;
      int maxResults = -1;
      int maxIndex;
      Page ancestorPage;

      if( parameterMap.containsKey( LAST_ITEM_INDEX ) ){
         offset = Integer.parseInt(parameterMap.get( LAST_ITEM_INDEX )[0]);
      } 
      if( parameterMap.containsKey( ITEMS_PER_CALL ) ){
         maxResults = Integer.parseInt(parameterMap.get( ITEMS_PER_CALL )[0]);
      } 

      if( parameterMap.containsKey(LABELS) && parameterMap.containsKey(PAGE_ID) ){
          Map<String, String[]> mutableParameterMap = new HashMap<String, String[]>( parameterMap );
          ancestorPage = pageManager.getPage( Long.parseLong( mutableParameterMap.get(PAGE_ID)[0] ) );
          
          mutableParameterMap.put( SPACE_KEYS, new String[]{ ancestorPage.getSpaceKey() } );
          List<Page> allPages = getPagesByLabel( mutableParameterMap );
          List<Page> decendantPages = new ArrayList<Page>();

          if( ancestorPage != null ){
              for( Page page : allPages ){
                  //if( parameterMap.containsKey(INCLUDE_DECENDANTS) ){
                      if( isDecendantPage( ancestorPage.getContentId().asLong(), page ) ){
                         decendantPages.add( page );
                      }
                  //} else {
                      //if( isChildPage( ancestorPage.getContentId().asLong(), page ) ){
                         //decendantPages.add( page );
                      //}
                  //}
              }
          }

          pages = createFilteredFaqItemList( offset, maxResults, decendantPages );

      } else if( parameterMap.containsKey(LABELS) && parameterMap.containsKey(SPACE_KEYS) ){
          List<Page> allPages = getPagesByLabel( parameterMap );
          pages = createFilteredFaqItemList( offset, maxResults, allPages );
      } else if( parameterMap.containsKey(PAGE_ID) ){
          ancestorPage = pageManager.getPage( Long.parseLong( parameterMap.get(PAGE_ID)[0] ) );
          List<Page> decendantPages;

          if( parameterMap.containsKey(INCLUDE_DECENDANTS) ){
              decendantPages = getSortedDecendants( ancestorPage, false );
          } else {
              decendantPages = ancestorPage.getSortedChildren();
          }

          pages = createFilteredFaqItemList( offset, maxResults, decendantPages );

      } else if( parameterMap.containsKey(SPACE_KEYS) ){
          String[] spaceKeys = parameterMap.get(SPACE_KEYS)[0].split(",\\s*");
          List<Page> allPages = new ArrayList<Page>();

          for( String spaceKey : spaceKeys ){
              Space currentSpace = spaceManager.getSpace( spaceKey );
              if( currentSpace != null ){
                  Page homePage = currentSpace.getHomePage();
                  allPages.addAll( getSortedDecendants( homePage, true ) );
              }
          }

          pages = createFilteredFaqItemList( offset, maxResults, allPages );

      } else if( parameterMap.containsKey(LABELS) ){
          List<Page> allPages = getPagesByLabel( parameterMap );
          pages = createFilteredFaqItemList( offset, maxResults, allPages );
      }

      return pages;
   }

   private boolean isChildPage( long ancestorPageId, Page page )
   {
       boolean flag = false;
       Page parentPage = page.getParent();

       if( page.getContentId().asLong() == ancestorPageId ){
           //flag = true;
       } else if( parentPage != null && parentPage.getContentId().asLong() == ancestorPageId ){
           flag = true;
       }
       return flag;
   }

   private boolean isDecendantPage( long ancestorPageId, Page page )
   {
       boolean flag = false;
       Page parentPage = page.getParent();

       if( page.getContentId().asLong() == ancestorPageId ){
           //flag = true;
       } else if( parentPage != null ){
           if( parentPage.getContentId().asLong() == ancestorPageId ){
               flag = true;
           } else if ( isDecendantPage( ancestorPageId, parentPage ) ){
               flag = true;
           } 
       }
       return flag;
   }

   private List<Page> getPagesByLabel( Map<String, String[]> parameterMap ){
      List<Label> labels = new ArrayList<Label>();
      PartialList<ContentEntityObject> matchingPages;
      Map<Long, Boolean> seenObjects = new HashMap<Long, Boolean>();
      List<Page> sortableEntities = new ArrayList<Page>();

      String[] spaceKeys;
      boolean boundBySpace = true;
      String[] labelsAsStrings = parameterMap.get(LABELS)[0].split(",\\s*");

      if( parameterMap.containsKey(SPACE_KEYS) ){
          spaceKeys = parameterMap.get(SPACE_KEYS)[0].split(",\\s*");
          boundBySpace = true;
      } else {
          spaceKeys = new String[]{ "skip" };
          boundBySpace = false;
      }

      for( String labelAsString : labelsAsStrings ){
          Label label = labelManager.getLabel( labelAsString );
          if( label != null ){
              labels.add( label );
          }
      }

      for( String spaceKey : spaceKeys ){

          if( parameterMap.containsKey(AND_LABELS) ){
              if( boundBySpace ){
                  matchingPages = labelManager.getContentInSpaceForAllLabels( NO_OFFSET, NO_MAX_RESULTS, spaceKey, labels.toArray( new Label[ labels.size() ] ) );
              } else {
                  matchingPages = labelManager.getContentForAllLabels( NO_OFFSET, NO_MAX_RESULTS, labels.toArray( new Label[ labels.size() ] ) );
              }
              for( ContentEntityObject entity : matchingPages.getList() ){
                 if( entity instanceof Page ){
                     sortableEntities.add( (Page) entity );
                 }
              }
          } else {
              for( Label label : labels ){
                  if( boundBySpace ){
                      matchingPages = labelManager.getContentInSpaceForLabel( NO_OFFSET, NO_MAX_RESULTS, spaceKey, label );
                  } else {
                      matchingPages = labelManager.getContentForLabel( NO_OFFSET, NO_MAX_RESULTS, label );
                  }
                  for( ContentEntityObject entity : matchingPages.getList() ){
                      if( !seenObjects.containsKey( entity.getContentId().asLong() ) && entity instanceof Page ){
                          seenObjects.put( entity.getContentId().asLong(),true );
                          sortableEntities.add( (Page) entity );
                      }
                  }
              }
          }
      }

      Collections.sort( sortableEntities, new Comparator<Page>() {
          @Override 
          public int compare( Page p1, Page p2) {
              return p1.getTitle().compareTo( p2.getTitle() );
          }
      });

      return sortableEntities;
   }

   private List<FaqItem> createFilteredFaqItemList( int offset, int maxResults, List<Page> allPages ){
       List<Page> pages = new ArrayList<Page>();
       List<Page> viewablePages = new ArrayList<Page>();
       List<FaqItem> faqItems = new ArrayList<FaqItem>();

       for( Page page : allPages ){
          if( permissionManager.hasPermission(m_currentUser, Permission.VIEW, page) ){
             viewablePages.add( page );
          }
       }

       if( offset == 0 && maxResults < 0 ){
           pages = viewablePages;
       } else if( offset == 0 && maxResults >= viewablePages.size() ){
           pages = viewablePages;
       } else if( offset + maxResults >= viewablePages.size() ){
           pages = new ArrayList<Page>( viewablePages.subList( offset, viewablePages.size() ) );
       } else {
           pages = new ArrayList<Page>( viewablePages.subList( offset, offset + maxResults ) );
       }

       for( Page page : pages ){
           faqItems.add( new FaqItem( (Page) page, String.format( "%d", likeManager.countLikes( page ) ) ) );
       }

       return faqItems;
   }

   private List<Page> getSortedDecendants( Page page, boolean includeParentPage ){
       List<Page> pages = new ArrayList<Page>();

       if( includeParentPage ){
           pages.add( page );
       }

       for( Page childPage : page.getSortedChildren() )
       {
           pages.addAll( getSortedDecendants( childPage, true ) );
       }

       return pages;
   }
}
