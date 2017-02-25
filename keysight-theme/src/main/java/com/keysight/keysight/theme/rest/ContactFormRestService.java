package com.keysight.keysight.theme.rest;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
import javax.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import org.apache.commons.lang.StringUtils;

import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.user.User;

//import com.atlassian.sal.api.transaction.TransactionTemplate;
//import com.atlassian.sal.api.transaction.TransactionCallback;
//import com.atlassian.spring.container.ContainerManager;

import com.atlassian.confluence.mail.template.ConfluenceMailQueueItem;
import com.atlassian.core.task.MultiQueueTaskManager;
import com.atlassian.mail.queue.MailQueueItem;
import static com.atlassian.confluence.mail.template.ConfluenceMailQueueItem.MIME_TYPE_HTML;
import static com.atlassian.confluence.mail.template.ConfluenceMailQueueItem.MIME_TYPE_TEXT;

import com.keysight.keysight.theme.helpers.MailService;
import com.keysight.keysight.theme.helpers.MailServiceImpl;
import com.atlassian.user.User;

@Path("/contact-form")
public class ContactFormRestService
{
   private final UserAccessor userAccessor;
   private final SpaceManager spaceManager;
   private final MailService mailService;
   private final MultiQueueTaskManager taskManager;

   public ContactFormRestService( MultiQueueTaskManager taskManager,
                                  UserAccessor userAccessor,
                                  SpaceManager spaceManager )
   {
       this.userAccessor               = userAccessor;
       this.spaceManager               = spaceManager;
       this.taskManager                = taskManager;
 
       mailService = new MailServiceImpl( taskManager );
   }

   // Note, usign POST causes XSRF errors
   // PUT had problems with parsing the paramters...
   @GET
   @AnonymousAllowed
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
   public Response feedbackAction( @QueryParam("contact-form-identifier")  String identifier,
		                           @QueryParam("contact-form-contact")     String contact,
                                   @QueryParam("contact-form-email")       String email,
                                   @QueryParam("contact-form-cc-to-email") String ccToEmail,
                                   @QueryParam("contact-form-space-key")   String spaceKey,
                                   @QueryParam("contact-form-name")        String name,
                                   @QueryParam("contact-form-type")        String type,
                                   @QueryParam("contact-form-text")        String text ){
      String emailSubject;
      StringBuilder html      = new StringBuilder();
      StringBuilder emailText = new StringBuilder();
      String destinationEmail = "";
      String ccEmail          = "";
      String fromName         = "";
      String fromEmail        = "";
      List<String> destinationEmails = new ArrayList<String>();
      Response response;

      if( !StringUtils.isEmpty( spaceKey ) && StringUtils.isEmpty( contact ) ){
         Space currentSpace = spaceManager.getSpace( spaceKey );
         if( currentSpace != null ){
            List<User> admins = spaceManager.getSpaceAdmins( currentSpace );
            for( User admin : admins ){
	           destinationEmails.add( admin.getEmail() );
            }
         }
      } else if( contact.matches( ".*@.*" ) ){
         destinationEmails.add( contact );
      } else {
	     destinationEmails.add( userAccessor.getUserByName( contact ).getEmail() );
      }

      if( destinationEmails.size() > 0 ){

         if( !StringUtils.isEmpty(ccToEmail) 
             && !StringUtils.isEmpty(email) 
	     && ccToEmail.matches( "(?i)true" ) 
	     && email.matches( ".*@.*" ) ){
            ccEmail = email;
         }

	     if( !StringUtils.isEmpty( name ) && !StringUtils.isEmpty( email ) && email.matches( ".*@.*" ) ){
            fromName = name;
	        fromEmail = email;
	     }

         html.append( "<div class=\"feedback-thankyou\">Thank you for your feedback.</div>" );

         emailSubject = "[" + identifier + "] " + "Webpage feedback of type " + type;
         emailText.append( "<p>Feedback from: " + name + " (" + email + ")</p>" );
         emailText.append( "<pre>" + text + "</pre>\n" );
      
         try{
            sendEmail( destinationEmails, ccEmail, fromEmail, fromName, emailSubject, emailText.toString() );
         } catch( Exception e ){
            System.out.println( "Failed to sending email test." );
            System.out.println( e );
         }

         response = Response.ok( new RestResponse( html.toString() ) ).build();

      } else {
         html.append( "<div class=\"feedback-thankyou\">Error: No contact has been setup to send this feedback to.</div>" );
         response = Response.ok( new RestResponse( html.toString() ) ).build();
      }

      return response;
   }

    public void sendEmail( List<String> toEmails, String ccEmail, String fromEmail, String fromName, String subject, String message) throws Exception
    {
       ConfluenceMailQueueItem mailQueueItem;
       boolean ccSent = false;

       for( String toEmail : toEmails ){

          if( !ccSent && !StringUtils.isEmpty( ccEmail ) && ccEmail.matches( ".*@.*" ) ){
             mailQueueItem = new ConfluenceMailQueueItem( toEmail, ccEmail, subject, message, MIME_TYPE_HTML);
          } else {
             mailQueueItem = new ConfluenceMailQueueItem( toEmail, subject, message, MIME_TYPE_HTML);
          }

          if( !StringUtils.isEmpty( fromName ) && !StringUtils.isEmpty( fromEmail ) && fromEmail.matches( ".*@.*" ) ){
             mailQueueItem.setFromName( fromName );
	         mailQueueItem.setFromAddress( fromEmail );
          }

          mailService.sendEmail(mailQueueItem);
       }
    }
}
