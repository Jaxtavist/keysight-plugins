package com.keysight.html.elements.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.ContentIncludeStack;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.user.User;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.Permission;

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

public class AuiPagesTabs extends AuiTabGroup
{
   protected final PageManager pageManager;
   protected final PermissionManager permissionManager;
   protected final Renderer renderer;
   protected final SettingsManager settingsManager;
   protected ConfluenceUser m_currentUser;

   protected static final String ERROR                   = "Error: ";
   protected static final String NOT_FOUND               = "Page Not Found";
   protected static final String ALREADY_INCLUDED        = "The page is already rendered";
   protected static final String PAGE_NOT_FOUND          = "Unable to find the specified page";
   protected static final String ON_CREATION_DATE        = "on-creation-date";
   protected static final String ON_DATE                 = "on-date";
   protected static final String HIDE_NOT_PERMITTED_TABS = "hide-not-permitted-tabs";
   protected boolean m_showPageInfo;

   public AuiPagesTabs( PageManager pageManager, 
                        PermissionManager permissionManager,
		                Renderer renderer,
		                VelocityHelperService velocityHelperService,
                        SettingsManager settingsManager,
                        XhtmlContent xhtmlUtils)
   {
      super( velocityHelperService, xhtmlUtils );
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
      String spaceKey = null;
      String displayTitle = null;
      Page page = null;
      Page thisPage = (Page) context.getEntity();
      Element element;
      Node node;
      NodeList anchors;
      int i = 0;
      boolean activeTabSet = false;
      SimpleDateFormat inputDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
      Date upperBoundDate = null;
      String baseUrl = settingsManager.getGlobalSettings().getBaseUrl();
      m_currentUser = AuthenticatedUserThreadLocal.get();
      boolean hideTab = false;

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

      // examine and prepare the body...
      body = body.trim();
      if( !( body.matches( "<ul.*" ) && body.matches( ".*</ul>" ) ) ){
         body = "<ul class=\"aui-list-truncate\"><li><a href=\"#\">Improper body.  Needs to be a single unordered (bullet) list of anchors to internal confluence pages.</li></ul>";
      } else {
         try{
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource inputSource = new InputSource();
            inputSource.setCharacterStream(new StringReader(body));
            Document document = documentBuilder.parse(inputSource);

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            document.getDocumentElement().normalize();

            anchors = document.getElementsByTagName("ac:link");
            for( i = 0; i < anchors.getLength(); i++ ){
               hideTab = false;
               node = anchors.item(i);
               page = null;
               spaceKey = context.getSpaceKey();

               if( node.getNodeType() == Node.ELEMENT_NODE ){
                  element = (Element) node;

                  NodeList pageElements = element.getElementsByTagName("ri:page");
                  if( pageElements.getLength() > 0 ){
                     Element pageElement = (Element) pageElements.item(0);
                     if( pageElement != null && pageElement.hasAttribute("ri:content-title") ){
                        if( pageElement.hasAttribute("ri:space-key") ){
                           spaceKey = pageElement.getAttribute("ri:space-key");
                        }

                        pageTitle = pageElement.getAttribute( "ri:content-title" );
                        page = pageManager.getPage( spaceKey, pageTitle );
                     } else {
                        pageTitle = "Unknown";
                        page = null;
                     }
                  }

                  if( page != null ){
                     Page boundedPage = GetLatestVersionBeforeDate( upperBoundDate, page );
                     displayTitle = page.getTitle();

                     NodeList plainTextElements = element.getElementsByTagName("ac:plain-text-link-body");
                     if( plainTextElements.getLength() > 0 ){
                        Element plainTextElement = (Element) plainTextElements.item(0);
                        if( plainTextElement != null ){
                           displayTitle = plainTextElement.getTextContent();
                        }
                     }

	                 if (ContentIncludeStack.contains(page)){
                        tabInfo = new TabInfo( displayTitle, RenderUtils.blockError( ERROR + " " + ALREADY_INCLUDED, "" ) );
	                 } else {
                        ContentIncludeStack.push(page);
                        try {
                           DefaultConversionContext childPageContext = new DefaultConversionContext(new PageContext(page, context.getPageContext()));

                           if( permissionManager.hasPermission(m_currentUser, Permission.VIEW, boundedPage) ){
                              tabInfo = new TabInfo( displayTitle, renderer.render( boundedPage.getBodyAsString(), childPageContext ) );
                           } else {
                              tabInfo = new TabInfo( displayTitle, 
                                                     RenderUtils.blockError(ERROR + " You do not have permissions to view the page \"" + displayTitle + "\".", "" ));
                              if( parameters.containsKey( HIDE_NOT_PERMITTED_TABS ) ){
                                 hideTab = true;
                              }
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


                  } else {
                     tabInfo = new TabInfo( pageTitle, RenderUtils.blockError( ERROR + " " + PAGE_NOT_FOUND, "" ) );
                  }

                  if( !hideTab ){
                     if( !activeTabSet ){
                        tabInfo.setActive( true );
                        activeTabSet = true;
                     }
                     tabs.add( tabInfo );
                  }
	           }
            }
         } catch( Exception exception ){
            tabInfo = new TabInfo( pageTitle, RenderUtils.blockError( ERROR + "Unable to parse body.  It should be a bulleted list of links to Confluence pages. Exception message " + exception.toString(), "" ) );
            if( !activeTabSet ){
               tabInfo.setActive( true );
               activeTabSet = true;
            }
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
      return BodyType.RICH_TEXT;
   }
}


