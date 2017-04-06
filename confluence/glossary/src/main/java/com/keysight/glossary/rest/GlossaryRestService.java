package com.keysight.glossary.rest;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.setup.settings.SettingsManager;

import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.spring.container.ContainerManager;

import com.atlassian.confluence.mail.template.ConfluenceMailQueueItem;
import com.atlassian.core.task.MultiQueueTaskManager;
import com.atlassian.mail.queue.MailQueueItem;
import static com.atlassian.confluence.mail.template.ConfluenceMailQueueItem.MIME_TYPE_HTML;
import static com.atlassian.confluence.mail.template.ConfluenceMailQueueItem.MIME_TYPE_TEXT;

import com.keysight.glossary.helpers.MailService;
import com.keysight.glossary.helpers.MailServiceImpl;


import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultXmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactoryFactoryBean;
import com.atlassian.confluence.renderer.ContentIncludeStack;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.confluence.pages.Comment;

import com.atlassian.user.User;

@Path("/")
public class GlossaryRestService
{
   private final AttachmentManager attachmentManager;
   private final ContentEntityManager contentEntityManager;
   private final LabelManager labelManager;
   private final PageManager pageManager;
   private final PermissionManager permissionManager;
   private final SpaceManager spaceManager;
   private final UserAccessor userAccessor;
   private final TransactionTemplate transactionTemplate;
   private final MailService mailService;
   private final MultiQueueTaskManager taskManager;
   private final Renderer renderer;
   private final XhtmlContent xhtmlUtils;
   private final SettingsManager settingsManager;

   public GlossaryRestService( AttachmentManager attachmentManager,
                               LabelManager labelManager,
                               PageManager pageManager,
                               PermissionManager permissionManager,
                               SettingsManager settingsManager,
                               SpaceManager spaceManager, 
                               TransactionTemplate transactionTemplate,
                               UserAccessor userAccessor,
                               Renderer renderer,
                               XhtmlContent xhtmlUtils,
                               MultiQueueTaskManager taskManager )
   {
       this.attachmentManager          = attachmentManager;
       this.labelManager               = labelManager;
       this.pageManager                = pageManager;
       this.permissionManager          = permissionManager;
       this.settingsManager            = settingsManager;
       this.spaceManager               = spaceManager;
       this.transactionTemplate        = transactionTemplate;
       this.userAccessor               = userAccessor;
       this.taskManager                = taskManager;
       this.renderer                   = renderer;
       this.xhtmlUtils                 = xhtmlUtils;
 
       mailService = new MailServiceImpl( taskManager );
       // don't know why the auto-wire doesn't work for the Content Entity Manager
       // but this method does.
       this.contentEntityManager = (ContentEntityManager) ContainerManager.getComponent("contentEntityManager");
   }

   @GET
   @Path("term")
   @AnonymousAllowed
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public Response defaultGetAction( @QueryParam("spaceKey")  String spaceKey,
                                     @QueryParam("pageTitle") String pageTitle,
                                     @QueryParam("term")      String term,
                                     @QueryParam("username")  String username ){

      ArrayList<Space> glossaries = new ArrayList<Space>();
      StringBuilder html = new StringBuilder();
      Space startingSpace = spaceManager.getSpace( spaceKey );
      String response = "";
      ArrayList<ConfluenceUser> glossaryAdmins = new ArrayList<ConfluenceUser>();
      ArrayList<ConfluenceUser> admins = new ArrayList<ConfluenceUser>();
      ArrayList<String> createUrls = new ArrayList<String>();
      ArrayList<String> glossaryNames = new ArrayList<String>();
      ConfluenceUser remoteUser = userAccessor.getUserByName( username );
      String encoding = "UTF-8";
      Page parentPage;
      Page page;

      for( Label spaceCategory : startingSpace.getDescription().getLabels() ){
         for( Space potentialGlossary : labelManager.getSpacesWithLabel( spaceCategory ) ){
            if( !glossaries.contains( potentialGlossary )
                && bIn( "glossary", potentialGlossary.getDescription().getLabels() ) ){
               glossaries.add( potentialGlossary );
            }
         }
      }

      for( Space glossary : glossaries ){
         page = pageManager.getPage( glossary.getKey(), term );
         if( page != null ){
            if( !permissionManager.hasPermission( remoteUser, Permission.VIEW, page ) ){
               html.append( RenderUtils.blockError( "Error: You do not have permissions to view the page \"" + page.getTitle() + "\" in the space \"" + page.getSpace().getName() + "\".", "" ));
            } else {
               try{
                  String editUrl = settingsManager.getGlobalSettings().getBaseUrl() + "/pages/editpage.action?"
                                 + "&pageId=" + URLEncoder.encode( page.getIdAsString(), encoding );
                  String viewUrl = settingsManager.getGlobalSettings().getBaseUrl() + "/pages/viewpage.action?"
                                 + "&pageId=" + URLEncoder.encode( page.getIdAsString(), encoding );
   
                  html.append( "<h2>" + page.getTitle() + "</h2>\n" );
                  DefaultConversionContext context = new DefaultConversionContext(new PageContext(page));
                  html.append( renderer.render(page.getBodyContent(BodyType.XHTML).getBody(), context) );
   
                  if( permissionManager.hasPermission( remoteUser, Permission.EDIT, page )
                      && permissionManager.hasCreatePermission( remoteUser, glossary, Comment.class ) ){
                     html.append( "<p><a href=\""+editUrl+"\" target=\"_blank\">edit</a> - <a href=\""+viewUrl+"\" target=\"_blank\">comment</a></p>" );
                  } else if( permissionManager.hasPermission( remoteUser, Permission.EDIT, page ) ){
                     html.append( "<p><a href=\""+editUrl+"\" target=\"_blank\">edit</a></p>" );
                  } else if( permissionManager.hasCreatePermission( remoteUser, glossary, Comment.class ) ){
                     html.append( "<p><a href=\""+viewUrl+"\" target=\"_blank\">comment</a></p>" );
                  }
               } catch(Exception e){
                  System.out.println( e );
               }
            }
         }
      }

      if( html.toString().isEmpty() ){
         if( glossaries.size() > 0 ){
            html.append( "<p>The term &quot;"+term+"&quot; was not found.</p>"
                        +"<p>Would you like to submit a request to the glossary admins to have the term defined?</p>"
                        +"<p style=\"text-align:center\">"
                        +"<button class=\"aui-button aui-button-primary\" onclick=\"requestTerm( \'"+term+"\', \'keysight-definition\');\">Submit Request</button></p>"
                        +"<br />\n" );

            for( Space glossary : glossaries ){
               parentPage = pageManager.getPage( glossary.getKey(), term.substring(0,1).toUpperCase() );
               if( parentPage == null ){
                  parentPage = pageManager.getPage( glossary.getKey(), "&" );
               }
               if( parentPage != null && permissionManager.hasCreatePermission( remoteUser, glossary, Page.class )){
                  try {
                     String createUrl = settingsManager.getGlobalSettings().getBaseUrl() + "/pages/createpage.action?"
                                      + "spaceKey=" + URLEncoder.encode( glossary.getKey(), encoding )
                                      + "&title=" + URLEncoder.encode( term, encoding )
                                      + "&fromPageId=" + URLEncoder.encode( parentPage.getIdAsString(), encoding )
                                      + "&labelsString=" + URLEncoder.encode( "glossary-term", encoding );

                     createUrls.add( createUrl );
                     glossaryNames.add( glossary.getName() );
                  } catch (UnsupportedEncodingException ex ){
                     System.out.println( ex );
                  }
               }
            }

            if( createUrls.size() > 0 ){
               html.append("<p style=\"text-align:center\">");
               if( createUrls.size() == 1 ){
                        html.append( "<a href=\""+createUrls.get(0)+"\" target=\"_blank\">Define the term</a>\n" );
               } else {
                  for( int i = 0; i < createUrls.size(); i++ ){
                        if( i > 0 ){ html.append( "<br />\n" ); }
                        html.append( "<a href=\""+createUrls.get(i)+"\" target=\"_blank\">Define the term in \"" + glossaryNames.get(i) + "\"</a>\n" );
                  }
               }
               html.append("</p>\n");
            }
         } else {
            html.append( "<p>No glossaries have been setup for this space. "
                        +"Please contact the space administrator to enable "
                        +"this functionality.</p>\n" );
            for( User admin : spaceManager.getSpaceAdmins( startingSpace ) ){
               if( !admins.contains( userAccessor.getUserByName( admin.getName() ) ) ){
                  admins.add( userAccessor.getUserByName( admin.getName() ) );
               }
            }
            html.append( "<p>Space Admins</p>\n" );
            html.append( "<ul>\n" );
            for( ConfluenceUser admin : admins ){
               html.append( "<li>" + admin.getFullName() + "</li>\n" );
            }
         }
      }

      return Response.ok( new RestResponse( html.toString() ) ).build();
   }

   @GET
   @AnonymousAllowed
   @Path("request")
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public Response defaultPostRequestAction( @QueryParam("spaceKey")  String spaceKey,
                                             @QueryParam("pageTitle") String pageTitle,
                                             @QueryParam("term")      String term,
                                             @QueryParam("username")  String username ){

      StringBuilder html                       = new StringBuilder();
      StringBuilder emailText                  = new StringBuilder();
      Space startingSpace                      = spaceManager.getSpace( spaceKey );
      ArrayList<ConfluenceUser> glossaryAdmins = new ArrayList<ConfluenceUser>();
      ArrayList<ConfluenceUser> admins         = new ArrayList<ConfluenceUser>();
      ArrayList<Space> glossaries              = new ArrayList<Space>();
      String emailSubject;

      for( Label spaceCategory : startingSpace.getDescription().getLabels() ){
         for( Space potentialGlossary : labelManager.getSpacesWithLabel( spaceCategory ) ){
            if( !glossaries.contains( potentialGlossary )
                && bIn( "glossary", potentialGlossary.getDescription().getLabels() ) ){
               glossaries.add( potentialGlossary );
            }
         }
      }

      if( glossaries.size() > 0 ){
         html.append( "<p>Request Submitted.</p>" );

         emailSubject = "Confluence Glossary Term Request: " + term;
         ConfluenceUser remoteUser = userAccessor.getUserByName( username );
         if( remoteUser == null ){
            emailText.append( "<p>An anonymous user " );
         } else {
            emailText.append( "<p>"+remoteUser.getFullName()+" " );
         }
         emailText.append( "has requested the term &quot;"+term+"&quot; be added to the "
                          +"glossary related to the page &quot;"+pageTitle+"&quot; in the space "
                          +startingSpace.getName() + " (" + spaceKey + ")." );

         for( Space glossary : glossaries ){
            for( User admin : spaceManager.getSpaceAdmins( glossary ) ){
               if( !glossaryAdmins.contains( userAccessor.getUserByName( admin.getName() ) ) ){
                  glossaryAdmins.add( userAccessor.getUserByName( admin.getName() ) );
               }
            }
         }

         try{
            sendEmail( glossaryAdmins, emailSubject, emailText.toString() );
         } catch( Exception e ){
            System.out.println( "Failed to sending email test." );
            System.out.println( e );
         }
      } else {
         html.append( "<p>No glossaries have been setup for this space. "
                     +"Please contact the space administrator to enable "
                     +"this functionality.</p>\n" );
         for( User admin : spaceManager.getSpaceAdmins( startingSpace ) ){
            if( !admins.contains( userAccessor.getUserByName( admin.getName() ) ) ){
               admins.add( userAccessor.getUserByName( admin.getName() ) );
            }
         }
         html.append( "<p>Space Admins</p>\n" );
         html.append( "<ul>\n" );
         for( ConfluenceUser admin : admins ){
            html.append( "<li>" + admin.getFullName() + "</li>\n" );
         }
         html.append( "</ul>\n" );
      }

      return Response.ok( new RestResponse( html.toString() ) ).build();
   }

   private boolean bIn( String string, List<Label> list ){
      boolean bFlag = false;
      for( Label label : list ){
         if( label.toString().equals( string ) ){
            bFlag = true;
         }         
      }
      return bFlag;
   }
   
    public void sendEmail( ArrayList<ConfluenceUser> admins, String subject, String message) throws Exception
    {
        for( ConfluenceUser admin : admins ){
           MailQueueItem mailQueueItem = new ConfluenceMailQueueItem( admin.getEmail(), subject, message, MIME_TYPE_HTML);
           mailService.sendEmail(mailQueueItem);
           //taskManager.addTask(MailServiceImpl.MAIL, mailQueueItem);
        }
    }
}
