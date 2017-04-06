package com.keysight.learning.products.rest;

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
   @Path("help/lp-block-quote")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response lpBlockQuoteHelp( ){
      String title = "LP Block Quote Help";
      String bodyTemplate = "/com/keysight/learning-products/templates/lp-block-quote-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/lp-note-block")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response lpNoteBlockHelp( ){
      String title = "LP Note Block Help";
      String bodyTemplate = "/com/keysight/learning-products/templates/lp-highlight-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/lp-important-block")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response lpImportantBlockHelp( ){
      String title = "LP Important Block Help";
      String bodyTemplate = "/com/keysight/learning-products/templates/lp-highlight-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/lp-tip-block")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response lpTipBlockHelp( ){
      String title = "LP Tip Block Help";
      String bodyTemplate = "/com/keysight/learning-products/templates/lp-highlight-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/lp-caution-block")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response lpCautionBlockHelp( ){
      String title = "LP Caution Block Help";
      String bodyTemplate = "/com/keysight/learning-products/templates/lp-highlight-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/lp-warning-block")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response lpWarningBlockHelp( ){
      String title = "LP Warning Block Help";
      String bodyTemplate = "/com/keysight/learning-products/templates/lp-highlight-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/lp-code")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response lpCodeHelp( ){
      String title = "LP Code Help";
      String bodyTemplate = "/com/keysight/learning-products/templates/lp-code-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/lp-internal")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response lpInternalHelp( ){
      String title = "LP Internal Help";
      String bodyTemplate = "/com/keysight/learning-products/templates/lp-internal-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   private Response getMacroHelp( String title, String bodyTemplate ){
      StringBuilder html = new StringBuilder();
      String headerTemplate = "/com/keysight/learning-products/templates/help-header.vm";
      String footerTemplate = "/com/keysight/learning-products/templates/help-footer.vm";

      Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();
      velocityContext.put( "title", title );
      velocityContext.put( "baseUrl", settingsManager.getGlobalSettings().getBaseUrl() );

      html.append( velocityHelperService.getRenderedTemplate( headerTemplate, velocityContext ) );
      html.append( velocityHelperService.getRenderedTemplate( bodyTemplate,   velocityContext ) );
      html.append( velocityHelperService.getRenderedTemplate( footerTemplate, velocityContext ) );

      return Response.ok( new RestResponse( html.toString() ) ).build();
   }
}
