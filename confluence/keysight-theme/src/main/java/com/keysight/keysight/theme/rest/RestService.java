package com.keysight.keysight.theme.rest;

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
   @Path("help/navigation-box")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response navigationBoxHelp( ){
      String title = "Navigation Box Help";
      String bodyTemplate = "/com/keysight/keysight-theme/templates/navigation-box-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/panel-box")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response PanelBoxHelp( ){
      String title = "Panel Box Help";
      String bodyTemplate = "/com/keysight/keysight-theme/templates/panel-box-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/contact-form")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response contactFormHelp( ){
      String title = "Contact Form Help";
      String bodyTemplate = "/com/keysight/keysight-theme/templates/contact-form-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/status-flag")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response statusFlagHelp( ){
      String title = "Status Flag Help";
      String bodyTemplate = "/com/keysight/keysight-theme/templates/status-flag-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/keysight-plc-checkpoint")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response keysightCheckpoinHelp( ){
      String title = "Keysight PLC Checkpoint Help";
      String bodyTemplate = "/com/keysight/keysight-theme/templates/keysight-plc-checkpoint-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/api-documentation")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response apiDocumentationhelp( ){
      String title = "API Documentation Help";
      String bodyTemplate = "/com/keysight/keysight-theme/templates/api-documentation-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   private Response getMacroHelp( String title, String bodyTemplate ){
      StringBuilder html = new StringBuilder();
      String headerTemplate = "/com/keysight/keysight-theme/templates/help-header.vm";
      String footerTemplate = "/com/keysight/keysight-theme/templates/help-footer.vm";
      String fossTemplate = "/com/keysight/keysight-theme/templates/foss.vm";

      Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();
      velocityContext.put( "title", title );
      velocityContext.put( "baseUrl", settingsManager.getGlobalSettings().getBaseUrl() );

      html.append( velocityHelperService.getRenderedTemplate( headerTemplate, velocityContext ) );
      html.append( velocityHelperService.getRenderedTemplate( bodyTemplate,   velocityContext ) );
      html.append( velocityHelperService.getRenderedTemplate( fossTemplate,   velocityContext ) );
      html.append( velocityHelperService.getRenderedTemplate( footerTemplate, velocityContext ) );

      return Response.ok( new RestResponse( html.toString() ) ).build();
   }
}
