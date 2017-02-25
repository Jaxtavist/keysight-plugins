package com.keysight.test.ecosystem.rest;

import java.util.Map;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.setup.settings.SettingsManager;

@Path("/")
public class RestService
{
   private final SettingsManager settingsManager;
   private final VelocityHelperService velocityHelperService;

   public RestService( SettingsManager settingsManager,
                       VelocityHelperService velocityHelperService
   ){
       this.settingsManager            = settingsManager;
       this.velocityHelperService      = velocityHelperService;
   }

   @GET
   @Path("help/test-ecosystem-navbar")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response navBarHelp( ){
      String title = "Test Ecosystem Navigation Bar Help";
      String bodyTemplate = "/com/keysight/test-ecosystem/templates/navbar-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/etms-block")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response etmsBlockHelp( ){
      String title = "ETMS Note Help";
      String bodyTemplate = "/com/keysight/test-ecosystem/templates/etms-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/sharedoc-link")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response shareDocLinkHelp( ){
      String title = "Sharedoc Link Help";
      String bodyTemplate = "/com/keysight/test-ecosystem/templates/sharedoc-link-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/sharedoc-link-bank")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response shareDocLinkBankHelp( ){
      String title = "Sharedoc Link Bank Help";
      String bodyTemplate = "/com/keysight/test-ecosystem/templates/sharedoc-link-bank-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/special-handling-properties")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response specialHandlingPropertiesHelp( ){
      String title = "Special Handling Properties Help";
      String bodyTemplate = "/com/keysight/test-ecosystem/templates/special-handling-properties-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/special-handling-documents")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response specialHandlingDocumentsHelp( ){
      String title = "Special Handling Documents Help";
      String bodyTemplate = "/com/keysight/test-ecosystem/templates/special-handling-documents-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   private Response getMacroHelp( String title, String bodyTemplate ){
      StringBuilder html = new StringBuilder();
      String headerTemplate = "/com/keysight/test-ecosystem/templates/help-header.vm";
      String footerTemplate = "/com/keysight/test-ecosystem/templates/help-footer.vm";

      Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();
      velocityContext.put( "title", title );
      velocityContext.put( "baseUrl", settingsManager.getGlobalSettings().getBaseUrl() );

      html.append( velocityHelperService.getRenderedTemplate( headerTemplate, velocityContext ) );
      html.append( velocityHelperService.getRenderedTemplate( bodyTemplate,   velocityContext ) );
      html.append( velocityHelperService.getRenderedTemplate( footerTemplate, velocityContext ) );

      return Response.ok( new RestResponse( html.toString() ) ).build();
   }
}
