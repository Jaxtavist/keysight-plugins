package com.keysight.bitbucket.support.rest;

import java.util.Map;
import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.manager.directory.SynchronisationMode;

@Path("/directory")
public class ADRestService
{
   private final DirectoryManager directoryManager;

   public ADRestService( DirectoryManager directoryManager)
   {
       this.directoryManager = directoryManager;
   }

   @GET
   @AnonymousAllowed
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   @Path("sync")
   public Response syncAllDirectories( ){
      StringBuilder response = new StringBuilder();
      response.append( "Syncing Directories\n\n" );

      List directories = directoryManager.findAllDirectories();
      for( Directory directory : directoryManager.findAllDirectories() ){
         try{ 
            if( !directoryManager.isSynchronisable( directory.getId() )){
               response.append( "Directory " + directory.getName() + " can not be synchronized.\n" );
            } else if( directoryManager.isSynchronising( directory.getId() )){
               response.append( "Directory " + directory.getName() + " was already syncing.\n" );
            } else {
               directoryManager.synchroniseCache( directory.getId(), SynchronisationMode.INCREMENTAL );
               response.append( "Directory " + directory.getName() + " is now syncing.\n" );
            }
         } catch( Exception e ){
            // com.atlassian.crowd.exception.DirectoryInstantiationException
            // com.atlassian.crowd.exception.OperationFailedException
         }
      }

      return Response.ok( new RestResponse( response.toString() ) ).build();
   }
}
