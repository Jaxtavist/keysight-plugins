package com.keysight.keysight.theme.rest;

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
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.MacroDefinitionHandler;
import com.atlassian.confluence.xhtml.api.MacroDefinitionMarshallingStrategy;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.core.ContentEntityObject;

import com.atlassian.confluence.core.PartialList;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;

import com.keysight.keysight.theme.helpers.UpdateDescendantLabelsRestResponse;
import com.keysight.keysight.theme.helpers.LabelDescription;

@Path("/updateDescendantLabels/")
public class UpdateDescendantLabelsRestService
{
   private static final String ERROR              = "Error: ";

   private static final String LABELS_TO_UPDATE   = "labelsToUpdate";
   private static final String PAGE_ID            = "pageId";
   private static final String REMOVE             = "remove";

   private final LabelManager labelManager;
   private final PageManager pageManager;
   private final PermissionManager permissionManager;
   private final Renderer renderer;
   private final SettingsManager settingsManager;
   private final SpaceManager spaceManager;
   private final UserAccessor userAccessor;
   private final UserManager userManager;
   private final VelocityHelperService velocityHelperService;
   private final XhtmlContent xhtmlUtils;

   public UpdateDescendantLabelsRestService( LabelManager labelManager,
                                             PageManager pageManager,
                                             PermissionManager permissionManager,
                                             Renderer renderer,
                                             SettingsManager settingsManager,
                                             SpaceManager spaceManager,
                                             UserAccessor userAccessor,
                                             UserManager userManager,
                                             VelocityHelperService velocityHelperService,
                                             XhtmlContent xhtmlUtils
   ){
       this.labelManager          = labelManager;
       this.pageManager           = pageManager;
       this.permissionManager     = permissionManager;
       this.renderer              = renderer;
       this.settingsManager       = settingsManager;
       this.spaceManager          = spaceManager;
       this.userAccessor          = userAccessor;
       this.userManager           = userManager;
       this.velocityHelperService = velocityHelperService;
       this.xhtmlUtils            = xhtmlUtils;
   }

   @GET
   @Path("updateLabels")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response updateLabels( @Context HttpServletRequest request ){
      Map<String, String[]> parameterMap = request.getParameterMap();
      UpdateDescendantLabelsRestResponse updateDescendantLabelsRestResponse = new UpdateDescendantLabelsRestResponse();

      String username = userManager.getRemoteUsername(request);
      ConfluenceUser user = userAccessor.getUserByName(username);

      if( parameterMap.containsKey( PAGE_ID ) && parameterMap.containsKey( LABELS_TO_UPDATE ) ){
         Page topPage = pageManager.getPage( Long.parseLong(parameterMap.get(PAGE_ID)[0]) );
         String[] labelsAsStrings = parameterMap.get(LABELS_TO_UPDATE);
         List<Page> pages = topPage.getDescendants();
         pages.add( 0, topPage );
         List<Label> labels = new ArrayList<Label>();
         List<LabelDescription> labelDescriptions = new ArrayList<LabelDescription>();
         String spaceKey = topPage.getSpaceKey();
         int pagesUpdated = 0;
         int pagesSkipped = 0;

         for( String labelAsString : labelsAsStrings ){
            Label label = labelManager.getLabel( labelAsString );
            if( label == null ){
               label = labelManager.createLabel( new Label(labelAsString) );
            }
            labels.add( label );
            labelDescriptions.add( new LabelDescription( label, spaceKey ) );
         }

         for( Page page : pages ){
            if( permissionManager.hasPermission(user, Permission.EDIT, page) ){
               for( Label label : labels ){
                  if( parameterMap.containsKey(REMOVE) ){
                     labelManager.removeLabel(page,label);
                  } else {
                     labelManager.addLabel(page,label);
                  }
               }
               pagesUpdated++;
            } else {
               pagesSkipped++;
            }
         }

         updateDescendantLabelsRestResponse.setPageCount(pages.size());
         updateDescendantLabelsRestResponse.setPagesSkipped(pagesSkipped);
         updateDescendantLabelsRestResponse.setPagesUpdated(pagesUpdated);
         updateDescendantLabelsRestResponse.setLabelDescriptions( labelDescriptions.toArray( new LabelDescription[ labelDescriptions.size() ] ) );
      }

      
      return Response.ok( updateDescendantLabelsRestResponse ).build();
   }
}
