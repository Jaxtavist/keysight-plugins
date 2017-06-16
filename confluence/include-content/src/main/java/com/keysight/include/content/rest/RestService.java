package com.keysight.include.content.rest;

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

import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.MacroDefinitionHandler;
import com.atlassian.confluence.xhtml.api.MacroDefinitionMarshallingStrategy;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.sal.api.user.UserManager;

import com.keysight.include.content.helpers.SharedBlockIncludeStack;

@Path("/")
public class RestService
{
   private static final String PAGE_ID          = "page-id";
   private static final String EXCERPT          = "excerpt";
   private static final String SHARED_BLOCK     = "shared-block";
   private static final String SHARED_BLOCK_KEY = "shared-block-key";
   private static final String ERROR            = "Error: ";

   private final PageManager pageManager;
   private final PermissionManager permissionManager;
   private final Renderer renderer;
   private final SettingsManager settingsManager;
   private final UserAccessor userAccessor;
   private final UserManager userManager;
   private final VelocityHelperService velocityHelperService;
   private final XhtmlContent xhtmlUtils;

   public RestService( PageManager pageManager,
                       PermissionManager permissionManager,
                       Renderer renderer,
                       SettingsManager settingsManager,
                       UserManager userManager,
                       UserAccessor userAccessor,
                       VelocityHelperService velocityHelperService,
                       XhtmlContent xhtmlUtils
   ){
       this.pageManager           = pageManager;
       this.permissionManager     = permissionManager;
       this.renderer              = renderer;
       this.settingsManager       = settingsManager;
       this.userAccessor               = userAccessor;
       this.userManager           = userManager;
       this.velocityHelperService = velocityHelperService;
       this.xhtmlUtils            = xhtmlUtils;
   }

   @GET
   @Path("help/include-page-with-replacement")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response includePageWithReplacement( ){
      String title = "Include Page With Replacement Help";
      String bodyTemplate = "/com/keysight/include-content/templates/include-page-with-replacement-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/include-shared-block-with-replacement")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response includeSharedBlockWithReplacement( ){
      String title = "Include Shared Block With Replacement Help";
      String bodyTemplate = "/com/keysight/include-content/templates/include-shared-block-with-replacement-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/include-shared-block")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response includeSharedBlock( ){
      String title = "Include Shared Block Help";
      String bodyTemplate = "/com/keysight/include-content/templates/include-shared-block-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/shared-block")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response sharedBlock( ){
      String title = "Shared Block Help";
      String bodyTemplate = "/com/keysight/include-content/templates/shared-block-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/include-children")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response includeChildrenBlock( ){
      String title = "Shared Block Help";
      String bodyTemplate = "/com/keysight/include-content/templates/include-children-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/include-code")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response includeCodeBlock( ){
      String title = "Code Include Help";
      String bodyTemplate = "/com/keysight/include-content/templates/include-code-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/help-text")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response helpText( ){
      String title = "Help Text Help";
      String bodyTemplate = "/com/keysight/include-content/templates/help-text-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/help-text-from-page")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response helpTextFromPage( ){
      String title = "Help Text From Page Help";
      String bodyTemplate = "/com/keysight/include-content/templates/help-text-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/help-text-from-shared-block")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response helpTextFromSharedBlock( ){
      String title = "Help Text From Shared Block Help";
      String bodyTemplate = "/com/keysight/include-content/templates/help-text-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("help/faq")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response faq( ){
      String title = "FAQ Help";
      String bodyTemplate = "/com/keysight/include-content/templates/faq-help.vm";

      return getMacroHelp( title, bodyTemplate );
   }

   @GET
   @Path("get-shared-block")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response getSharedBlock( @Context HttpServletRequest request ){
      Map<String, String[]> parameterMap = request.getParameterMap();
      String response;
      String username = userManager.getRemoteUsername(request);
      ConfluenceUser remoteUser = userAccessor.getUserByName( username );
 
      if(parameterMap.containsKey(PAGE_ID)){
         String sharedBlockKey = null;
         Page page = pageManager.getPage( Long.parseLong(parameterMap.get(PAGE_ID)[0] ) );
         DefaultConversionContext context = new DefaultConversionContext(new PageContext(page));

         if( page == null ){
            response = RenderUtils.blockError(ERROR + " There is no page with the id " + parameterMap.get(PAGE_ID)[0], "" );
         } else if( !permissionManager.hasPermission( remoteUser, Permission.VIEW, page ) ){
            response = RenderUtils.blockError(ERROR + " You do not have permissions to view this content on page " + page.getTitle() + "\" in space \"" + page.getSpace().getName() + "\".", "" );
         } else {

            String blockId = page.getTitle();
            if(parameterMap.containsKey(SHARED_BLOCK_KEY) && parameterMap.get(SHARED_BLOCK_KEY)[0].matches(".*\\w.*")){
               sharedBlockKey = parameterMap.get(SHARED_BLOCK_KEY)[0];
               blockId = blockId + ":" + sharedBlockKey;
            }

            if (SharedBlockIncludeStack.contains(blockId)){
                return Response.ok( new RestResponse( RenderUtils.blockError( ERROR, "Already included" ) ) ).build();
            }

            SharedBlockIncludeStack.push(blockId);
            try {
               String content = "";
               if( parameterMap.containsKey( EXCERPT ) && parameterMap.get(EXCERPT)[0].equals("true") ){
                  content = getSharedBlockOrExcerptHtml( page, EXCERPT, null, context );
               } else {
                  content = getSharedBlockOrExcerptHtml( page, SHARED_BLOCK, sharedBlockKey, context );
               }
               response = renderer.render(content, context);
            } catch (Exception e){
               response = RenderUtils.blockError( ERROR, e.toString() );
            } finally {
               SharedBlockIncludeStack.pop();
            }
         }
      } else {
         response = RenderUtils.blockError( ERROR, "Page ID not provided" );
      }

      return Response.ok( new RestResponse( response ) ).build();
   }

   private Response getMacroHelp( String title, String bodyTemplate ){
       StringBuilder html = new StringBuilder();
       String headerTemplate = "/com/keysight/include-content/templates/help-header.vm";
       String footerTemplate = "/com/keysight/include-content/templates/help-footer.vm";
       String fossTemplate = "/com/keysight/include-content/templates/foss.vm";

       Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();
       velocityContext.put( "title", title );
       velocityContext.put( "baseUrl", settingsManager.getGlobalSettings().getBaseUrl() );

       html.append( velocityHelperService.getRenderedTemplate( headerTemplate, velocityContext ) );
       html.append( velocityHelperService.getRenderedTemplate( bodyTemplate,   velocityContext ) );
       html.append( velocityHelperService.getRenderedTemplate( fossTemplate,   velocityContext ) );
       html.append( velocityHelperService.getRenderedTemplate( footerTemplate, velocityContext ) );

       return Response.ok( new RestResponse( html.toString() ) ).build();
   }

   private String getSharedBlockOrExcerptHtml( Page page, String macroKey, String sharedBlockKey, ConversionContext context ) throws Exception 
   {
        final List<MacroDefinition> macros = new ArrayList<MacroDefinition>();
        String html = "";
        xhtmlUtils.handleMacroDefinitions(page.getBodyContent().getBody(), context, new MacroDefinitionHandler(){
            @Override
            public void handle(MacroDefinition macroDefinition){
                macros.add(macroDefinition);
            }
        },
        MacroDefinitionMarshallingStrategy.MARSHALL_MACRO);

        if (!macros.isEmpty()) {
            for( MacroDefinition macroDefinition : macros ){
                if( macroDefinition.getName().equals( macroKey ) ){
                    if( sharedBlockKey == null ){
                        html = macroDefinition.getBodyText();
                        break;
                    } else if( sharedBlockKey.equals( macroDefinition.getParameter( SHARED_BLOCK_KEY ) ) ){
                        html = macroDefinition.getBodyText();
                        break;
                    }
                }
            }
        }

        return html;
    } 
}
