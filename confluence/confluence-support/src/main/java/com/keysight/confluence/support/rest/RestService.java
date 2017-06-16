package com.keysight.confluence.support.rest;

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
   @Path("help/empty-trash")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response emptyTrashHelp( ){
      String title = "Empty Trash Help";
      String bodyTemplate = "/com/keysight/confluence-support/templates/empty-trash-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/fix-unnamed-pages")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response fixUnnamedPagesHelp( ){
      String title = "Fix Unnamed Pages Help";
      String bodyTemplate = "/com/keysight/confluence-support/templates/fix-unnamed-pages-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }


   @GET
   @Path("help/additional-macro-parameter-types")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response additionalMacroParameterTypesHelp( ){
      String title = "Additional Macro Parameter Types Help";
      String bodyTemplate = "/com/keysight/confluence-support/templates/additional-macro-parameter-types-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/entity-authorship")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response entityAuthorshipHelp( ){
      String title = "Entity Authorship Help";
      String bodyTemplate = "/com/keysight/confluence-support/templates/entity-authorship-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   private Response getMacroHelp( String title, String bodyTemplate ){
      StringBuilder html = new StringBuilder();
      String headerTemplate = "/com/keysight/confluence-support/templates/help-header.vm";
      String footerTemplate = "/com/keysight/confluence-support/templates/help-footer.vm";
      String fossTemplate = "/com/keysight/confluence-support/templates/foss.vm";

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
