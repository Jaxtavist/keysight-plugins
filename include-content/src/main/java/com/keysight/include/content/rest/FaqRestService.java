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
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.MacroDefinitionHandler;
import com.atlassian.confluence.xhtml.api.MacroDefinitionMarshallingStrategy;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.PartialList;
import com.atlassian.confluence.like.LikeManager;
import com.atlassian.confluence.like.Like;

import com.keysight.include.content.helpers.FaqItem;
import com.keysight.include.content.helpers.FaqItemListBuilder;

@Path("/faq/")
public class FaqRestService
{
   private static final String ERROR              = "Error: ";

   private static final String ITEMS_PER_CALL     = "itemsPerCall";
   private static final String LAST_ITEM_INDEX    = "lastItemIndex";
   private static final String LABELS             = "labels";
   private static final String SPACE_KEYS         = "spaceKeys";
   private static final String PAGE_ID            = "pageId";
   private static final String AND_LABELS         = "andLabels";
   private static final String INCLUDE_DECENDANTS = "includeDecendants";
   private static final int SECONDS = 1000;

   private final LabelManager labelManager;
   private final LikeManager likeManager;
   private final PageManager pageManager;
   private final PermissionManager permissionManager;
   private final Renderer renderer;
   private final SettingsManager settingsManager;
   private final SpaceManager spaceManager;
   private final UserManager userManager;
   private final VelocityHelperService velocityHelperService;
   private final XhtmlContent xhtmlUtils;

   public FaqRestService( LabelManager labelManager,
                          LikeManager likeManager,
                          PageManager pageManager,
                          PermissionManager permissionManager,
                          Renderer renderer,
                          SettingsManager settingsManager,
                          SpaceManager spaceManager,
                          UserManager userManager,
                          VelocityHelperService velocityHelperService,
                          XhtmlContent xhtmlUtils
   ){
       this.labelManager = labelManager;
       this.likeManager = likeManager;
       this.pageManager           = pageManager;
       this.permissionManager     = permissionManager;
       this.renderer              = renderer;
       this.settingsManager       = settingsManager;
       this.spaceManager          = spaceManager;
       this.userManager           = userManager;
       this.velocityHelperService = velocityHelperService;
       this.xhtmlUtils            = xhtmlUtils;
   }

   @GET
   @Path("items")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public Response getFaqItems( @Context HttpServletRequest request ){
      List<FaqItem> pages;
      Map<String, String[]> parameterMap = request.getParameterMap();

      FaqItemListBuilder faqItemListBuilder = new FaqItemListBuilder( labelManager,
                                                                      likeManager,
                                                                      pageManager,
                                                                      permissionManager,
                                                                      renderer,
                                                                      settingsManager,
                                                                      spaceManager,
                                                                      userManager,
                                                                      velocityHelperService,
                                                                      xhtmlUtils );
      pages = faqItemListBuilder.getFaqItems( parameterMap );
      return Response.ok( pages ).build();
   }
}
