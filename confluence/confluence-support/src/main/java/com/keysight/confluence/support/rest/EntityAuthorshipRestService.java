package com.keysight.confluence.support.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import java.util.Map;
import java.util.ArrayList;
import org.apache.commons.lang.StringUtils;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;

import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.spring.container.ContainerManager;

@Path("/content")
public class EntityAuthorshipRestService
{
   private final AttachmentManager attachmentManager;
   private final ContentEntityManager contentEntityManager;
   private final PageManager pageManager;
   private final SpaceManager spaceManager;
   private final UserAccessor userAccessor;
   private final TransactionTemplate transactionTemplate;

   public EntityAuthorshipRestService( AttachmentManager attachmentManager,
		                     //ContentEntityManager contentEntityManager,
                                     PageManager pageManager,
                                     SpaceManager spaceManager, 
                                     TransactionTemplate transactionTemplate,
                                     UserAccessor userAccessor )
   {
       this.attachmentManager          = attachmentManager;
       //this.contentEntityManager       = contentEntityManager;
       this.pageManager                = pageManager;
       this.spaceManager               = spaceManager;
       this.transactionTemplate        = transactionTemplate;
       this.userAccessor               = userAccessor;
      
       // don't know why the auto-wire doesn't work for the Content Entity Manager
       // but this method does.
       this.contentEntityManager = (ContentEntityManager) ContainerManager.getComponent("contentEntityManager");
   }

   @GET
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public Response defaultAction(){
      return Response.ok( new RestResponse( "No page specified" )).build();
   }
   
   @POST
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   public Response defaultAction( final RestInput restInput ){

      //System.out.println( "Space Key "         + restInput.getSpaceKey()        );
      //System.out.println( "Page Title "        + restInput.getPageTitle()       );
      //System.out.println( "Username "          + restInput.getCreator()         );
      //System.out.println( "Creation Date "     + restInput.getCreationDate()    );
      //System.out.println( "Attachment Title "  + restInput.getAttachmentTitle() );
      //System.out.println( "Content Entity ID " + restInput.getContentId()       );

      Object result = transactionTemplate.execute( new TransactionCallback(){
         @Override
         public Object doInTransaction(){
            ContentEntityObject entity = null;

            if( restInput.getContentId() != null ){
               entity = contentEntityManager.getById( Long.valueOf( restInput.getContentId() ).longValue() );
            } else if( restInput.getSpaceKey() != null && restInput.getPageTitle() != null ) {
               entity = pageManager.getPage( restInput.getSpaceKey(), restInput.getPageTitle() );
               if( restInput.getAttachmentTitle() != null ){
                  entity = attachmentManager.getAttachment( entity, restInput.getAttachmentTitle() );
               }
            }

            if( entity != null ){
               if( restInput.getCreator() != null ){
                  ConfluenceUser user = userAccessor.getUserByName( restInput.getCreator() );
                  if( user != null ){
                     entity.setCreator( user );
                  }
               }
        
               if( restInput.getCreationDate() != null ){
                  try{
                       SimpleDateFormat dateFormat = new SimpleDateFormat( "M/dd/yyyy HH:mm:ss" );
                       Date date = dateFormat.parse( restInput.getCreationDate() );
                       entity.setCreationDate( date );
                       entity.setLastModificationDate( date );
                  } catch( ParseException e ){}
               }
            }
      
            return null;
         }
      });
             
      RestResponse response = new RestResponse( "Completed" );
      return Response.ok( response ).build();
   }
   
   @POST
   @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   @Path("/{pageId}")
   public Response defaultAction( @QueryParam("username") String username ){
      if( username != null ){
         return Response.ok( new RestResponse( username )).build();
      } else {
         return Response.ok( new RestResponse( "No username given" )).build();
      }     
   }
}
