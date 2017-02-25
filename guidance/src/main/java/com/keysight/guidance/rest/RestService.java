package com.keysight.guidance.rest;

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
   @Path("help/do")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response doHelp( ){
      String title = "Do Guidance Help";
      String bodyTemplate = "/com/keysight/guidance/templates/guidance-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/consider")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response considerHelp( ){
      String title = "Consider Guidance Help";
      String bodyTemplate = "/com/keysight/guidance/templates/guidance-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/avoid")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response avoidHelp( ){
      String title = "Avoid Guidance Help";
      String bodyTemplate = "/com/keysight/guidance/templates/guidance-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/do-not")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response doNotHelp( ){
      String title = "Do Not Guidance Help";
      String bodyTemplate = "/com/keysight/guidance/templates/guidance-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/lp-guideline")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response lpGuidelineHelp( ){
      String title = "LP Guideline Help";
      String bodyTemplate = "/com/keysight/guidance/templates/guidance-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   private Response getMacroHelp( String title, String bodyTemplate ){
      StringBuilder html = new StringBuilder();
      String headerTemplate = "/com/keysight/guidance/templates/help-header.vm";
      String footerTemplate = "/com/keysight/guidance/templates/help-footer.vm";

      Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();
      velocityContext.put( "title", title );
      velocityContext.put( "baseUrl", settingsManager.getGlobalSettings().getBaseUrl() );

      html.append( velocityHelperService.getRenderedTemplate( headerTemplate, velocityContext ) );
      html.append( velocityHelperService.getRenderedTemplate( bodyTemplate,   velocityContext ) );
      html.append( velocityHelperService.getRenderedTemplate( footerTemplate, velocityContext ) );

      return Response.ok( new RestResponse( html.toString() ) ).build();
   }
}
