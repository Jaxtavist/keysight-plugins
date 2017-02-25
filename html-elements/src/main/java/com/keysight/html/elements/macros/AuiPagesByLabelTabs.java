package com.keysight.html.elements.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.core.PartialList;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.renderer.ContentIncludeStack;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.user.User;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.Permission;
import static com.atlassian.confluence.labels.LabelManager.NO_MAX_RESULTS;
import static com.atlassian.confluence.labels.LabelManager.NO_OFFSET;


import java.util.Collections;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.io.StringReader;
import org.xml.sax.InputSource;

import com.keysight.html.elements.helpers.TabInfo;

public class AuiPagesByLabelTabs extends AuiTabGroup
{
   protected final LabelManager labelManager;
   protected final PageManager pageManager;
   protected final PermissionManager permissionManager;
   protected final Renderer renderer;
   protected final SettingsManager settingsManager;

   protected ConfluenceUser m_currentUser;
   protected static final String ERROR            = "Error: ";
   protected static final String NOT_FOUND        = "Page Not Found";
   protected static final String ALREADY_INCLUDED = "The page is already rendered";
   protected static final String PAGE_NOT_FOUND   = "Unable to find the specified page";
   protected static final String ON_CREATION_DATE = "on-creation-date";
   protected static final String ON_DATE          = "on-date";
   protected static final String LABEL            = "label";
   protected static final String LIMIT            = "limit";
   protected boolean m_showPageInfo;

   public AuiPagesByLabelTabs( LabelManager labelManager,
                        PageManager pageManager, 
                        PermissionManager permissionManager,
		                Renderer renderer,
		                VelocityHelperService velocityHelperService,
                        SettingsManager settingsManager,
                        XhtmlContent xhtmlUtils)
   {
      super( velocityHelperService, xhtmlUtils );
      this.labelManager = labelManager;
      this.pageManager = pageManager;
      this.permissionManager = permissionManager;
      this.renderer = renderer;
      this.settingsManager = settingsManager;

      m_showPageInfo = true;
   }

   @Override
   protected boolean ShowPageInfo()
   {
      return m_showPageInfo;
   }

   @Override
   protected ArrayList<TabInfo> getTabs(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException
   {
      ArrayList<TabInfo> tabs = new ArrayList<TabInfo>();
      TabInfo tabInfo = null;
      String pageTitle = null;
      String displayTitle = null;
      Page page = null;
      Page thisPage = (Page) context.getEntity();
      int i = 0;
      boolean activeTabSet = false;
      SimpleDateFormat inputDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
      Date upperBoundDate = null;
      String baseUrl = settingsManager.getGlobalSettings().getBaseUrl();
      List<ContentEntityObject> list = new ArrayList<ContentEntityObject>();
      Label label = null;
      m_currentUser = AuthenticatedUserThreadLocal.get();
     
      int startIndex = NO_OFFSET;
      int maxItems = NO_MAX_RESULTS;
      boolean hideTab = false;

      if( parameters.containsKey( LIMIT ) ){
         try{
            maxItems = Integer.parseInt( parameters.get( LIMIT ) );
         } catch(Exception exception) {}
      }

      if( parameters.containsKey( ON_DATE ) ){
         try{
            upperBoundDate = inputDateFormat.parse( parameters.get( ON_DATE ) );
         } catch( Exception exception ) { }
      }
     
      if( upperBoundDate == null && parameters.containsKey( ON_CREATION_DATE ) ){
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(thisPage.getCreationDate());
         calendar.add( Calendar.DAY_OF_MONTH, 1 );
         upperBoundDate = calendar.getTime();
      }

      if( parameters.containsKey( LABEL ) ){
         label = labelManager.getLabel( parameters.get( LABEL ) );
         if( label != null ){
            PartialList<ContentEntityObject> partialList = labelManager.getContentForLabel( startIndex, maxItems, label );
            if( partialList != null ){
               list =  partialList.getList();
            }
         }
      }

      for( ContentEntityObject item : list ){
         try
         { 
            hideTab = false;
            page = (Page) item;
            Page boundedPage = GetLatestVersionBeforeDate( upperBoundDate, page );
            displayTitle = page.getTitle();

            if (ContentIncludeStack.contains(page)){
               tabInfo = new TabInfo( displayTitle, RenderUtils.blockError( ERROR + " " + ALREADY_INCLUDED, "" ) );
            } else {
               ContentIncludeStack.push(page);
               try {
                  DefaultConversionContext childPageContext = new DefaultConversionContext(new PageContext(page, context.getPageContext()));

                  if( permissionManager.hasPermission(m_currentUser, Permission.VIEW, boundedPage) ){
                     tabInfo = new TabInfo( displayTitle, renderer.render( boundedPage.getBodyAsString(), childPageContext ) );
                  } else {
                     hideTab = true;
                     tabInfo = new TabInfo( "No View Permission", 
                                            RenderUtils.blockError(ERROR + " You do not have permissions to view this content.", "" ));
                  }

                  tabInfo.setCreationDate( boundedPage.getLastModificationDate() );
                  tabInfo.setAuthorFullName( boundedPage.getLastModifierName() );
                  tabInfo.setPageUrl( baseUrl + page.getUrlPath() );
                  User author = boundedPage.getLastModifier();
                  if( author != null ){
                     tabInfo.setAuthorUserName( boundedPage.getLastModifier().getName() );
                  }
	           } finally {
                  ContentIncludeStack.pop();
	           }
	        }

            if( !activeTabSet ){
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
         } catch( Exception exception ){
            tabInfo = new TabInfo( pageTitle, RenderUtils.blockError( ERROR + "Unable to convert content entity object to a page. Exception message " + exception.toString(), "" ) );
            tabInfo.setActive( true );
            activeTabSet = true;
            tabs.add( tabInfo );
         }
      }

      return tabs;
   }

   protected Page GetLatestVersionBeforeDate( Date upperBoundDate, Page originalPage )
   {
      AbstractPage page = pageManager.getAbstractPage( originalPage.getLatestVersionId() );
      SimpleDateFormat printFormat = new SimpleDateFormat( "yyyy-MM-dd" );
      
      boolean doSearch = true;
      int i = 0;

      if( upperBoundDate != null ){
         while( doSearch && page.getLastModificationDate().after( upperBoundDate ) ){
            int versionNumber = page.getPreviousVersion();
            if( versionNumber > 0 ){
               page = pageManager.getPageByVersion( page, versionNumber );
            } else {
               doSearch = false;
            }
         } 
      }

      return (Page) page;
   }

   @Override
   public BodyType getBodyType()
   {
      return BodyType.NONE;
   }
}


