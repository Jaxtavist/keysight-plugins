package com.keysight.html.elements.rest;

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
   @Path("help/html5-multimedia")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response html5MultimediaHelp( ){
      String title = "HTML5 Multimedia Help";
      String bodyTemplate = "/com/keysight/html-elements/templates/html5-multimedia-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/multimedia")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response multimediaHelp( ){
      String title = "HTML5 Multimedia Help";
      String bodyTemplate = "/com/keysight/html-elements/templates/html5-multimedia-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/compact-form")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response compactFormHelp( ){
      String title = "Compact Form Help";
      String bodyTemplate = "/com/keysight/html-elements/templates/compact-form-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/aui-tab")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response auiTabHelp( ){
      String title = "AUI Tab Help";
      String bodyTemplate = "/com/keysight/html-elements/templates/aui-tab-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/aui-tab-group")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response auiTabGroupHelp( ){
      String title = "AUI Tab Group Help";
      String bodyTemplate = "/com/keysight/html-elements/templates/aui-tab-group-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/aui-child-tabs")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response auiChildTabsHelp( ){
      String title = "AUI Child Tabs Help";
      String bodyTemplate = "/com/keysight/html-elements/templates/aui-child-tabs-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/aui-pages-tabs")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response auiPagesTabsHelp( ){
      String title = "AUI Pages Tabs Help";
      String bodyTemplate = "/com/keysight/html-elements/templates/aui-pages-tabs-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/aui-pages-by-label-tabs")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response auiPagesByLabelTabsHelp( ){
      String title = "AUI Pages By Label Tabs Help";
      String bodyTemplate = "/com/keysight/html-elements/templates/aui-pages-by-label-tabs-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/aui-buttons")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response auiButtonsHelp( ){
      String title = "AUI Button Help";
      String bodyTemplate = "/com/keysight/html-elements/templates/aui-button-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/extra-table-properties")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response extraTablePropertiesHelp( ){
      String title = "Extra Table Properties Help";
      String bodyTemplate = "/com/keysight/html-elements/templates/extra-table-properties-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/authors-only-block")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response authorsOnlyBlockHelp( ){
      String title = "Authors Only Block Help";
      String bodyTemplate = "/com/keysight/html-elements/templates/authors-only-block-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/layout-width")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response layoutWidthHelp( ){
      String title = "Layout Width Help";
      String bodyTemplate = "/com/keysight/html-elements/templates/layout-width-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/block")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response blockHelp( ){
      String title = "Block Help";
      String bodyTemplate = "/com/keysight/html-elements/templates/block-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/link-to-window")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response linkToWindowHelp( ){
      String title = "Link To Window Help";
      String bodyTemplate = "/com/keysight/html-elements/templates/link-to-window-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   private Response getMacroHelp( String title, String bodyTemplate ){
      StringBuilder html = new StringBuilder();
      String headerTemplate = "/com/keysight/html-elements/templates/help-header.vm";
      String footerTemplate = "/com/keysight/html-elements/templates/help-footer.vm";
      String fossTemplate = "/com/keysight/html-elements/templates/foss.vm";

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
