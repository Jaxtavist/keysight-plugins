package nl.avisi.numbered.headings.rest;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.plugin.services.VelocityHelperService;

@Path("/")
public class RestService
{
   private final SettingsManager settingsManager;
   private final VelocityHelperService velocityHelperService;

   public RestService( SettingsManager settingsManager,
                       VelocityHelperService velocityHelperService
   ){
       this.settingsManager       = settingsManager;
       this.velocityHelperService = velocityHelperService;
   }

   @GET
   @Path("help/numbered-headings")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response numberedHeadingsHelp( ){
      String title = "Numbered Headings Help";
      String bodyTemplate = "/nl/avisi/numbered-headings/templates/numbered-headings-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/renumber-and-include-page")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response renumberAndIncludePageHelp( ){
      String title = "Renumber and Include Page Help";
      String bodyTemplate = "/nl/avisi/numbered-headings/templates/renumber-and-include-page-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/renumber-and-include-children")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response renumberAndIncludeChildrenHelp( ){
      String title = "Renumber and Include Children Help";
      String bodyTemplate = "/nl/avisi/numbered-headings/templates/renumber-and-include-children-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   private Response getMacroHelp( String title, String bodyTemplate ){
       StringBuilder html = new StringBuilder();
       String headerTemplate = "/nl/avisi/numbered-headings/templates/help-header.vm";
       String footerTemplate = "/nl/avisi/numbered-headings/templates/help-footer.vm";

       Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();
       velocityContext.put( "title", title );
       velocityContext.put( "baseUrl", settingsManager.getGlobalSettings().getBaseUrl() );

       html.append( velocityHelperService.getRenderedTemplate( headerTemplate, velocityContext ) );
       html.append( velocityHelperService.getRenderedTemplate( bodyTemplate,   velocityContext ) );
       html.append( velocityHelperService.getRenderedTemplate( footerTemplate, velocityContext ) );

       return Response.ok( new RestResponse( html.toString() ) ).build();
   }

}
