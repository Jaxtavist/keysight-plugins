package com.keysight.confluence.support.rest;

import java.util.Map;
import java.util.List;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.manager.directory.SynchronisationMode;

@Path("/directory")
public class ADRestService
{
   private final SettingsManager settingsManager;
   private final VelocityHelperService velocityHelperService;
   private final DirectoryManager directoryManager;

   public ADRestService( DirectoryManager directoryManager,
		         SettingsManager settingsManager,
                         VelocityHelperService velocityHelperService
   ){
       this.directoryManager      = directoryManager;
       this.settingsManager       = settingsManager;
       this.velocityHelperService = velocityHelperService;
   }

   @GET
   @AnonymousAllowed
   @Path("sync")
   public Response syncAllDirectories( ){
      String response = "<p>Syncing</p>";

      List directories = directoryManager.findAllDirectories();
      for( Directory directory : directoryManager.findAllDirectories() ){
	 try{ 
            if( directoryManager.isSynchronisable( directory.getId() ) 
                && !directoryManager.isSynchronising( directory.getId() )){
               directoryManager.synchroniseCache( directory.getId(), SynchronisationMode.INCREMENTAL );
	    }
	 } catch( Exception e ){
            // com.atlassian.crowd.exception.DirectoryInstantiationException
	    // com.atlassian.crowd.exception.OperationFailedException
	 }

      }

      return Response.ok( new RestResponse( response ) ).build();
   }
}
