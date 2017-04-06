package com.keysight.html.elements.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.renderer.v2.RenderUtils;

import java.util.Map;

public class LinkToWindow implements Macro
{
   public final static String PAGE_KEY                = "page";
   public final static String LINK_TEXT_KEY           = "link-text";

   protected final PageManager pageManager;
   protected final Renderer renderer;
   protected final SettingsManager settingsManager;
   protected final VelocityHelperService velocityHelperService;

   public LinkToWindow(
                  PageManager pageManager,
                  SettingsManager settingsManager,
                  Renderer renderer,
                  VelocityHelperService velocityHelperService)
   {
      this.pageManager = pageManager;
      this.settingsManager = settingsManager;
      this.renderer = renderer;
      this.velocityHelperService = velocityHelperService;
   }

   @Override
   public String execute(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException
   {
      String template = "/com/keysight/html-elements/templates/link-to-window.vm";
      Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();
      String baseUrl = settingsManager.getGlobalSettings().getBaseUrl();
      String url = null;
      String spaceKey = null;
      String pageTitle = null;
      Page   page = null;

      if( !parameters.containsKey( PAGE_KEY ) ){
        return RenderUtils.blockError( "No URL or Page specified", "" );
      } else if( parameters.get( PAGE_KEY ).matches( "http.*" ) ){
         url = parameters.get( PAGE_KEY );
      } else {
         if( parameters.get( PAGE_KEY ).matches( ".*:.*" ) ){
            String[] pageParts = parameters.get(PAGE_KEY).split( ":", 2 );
            spaceKey = pageParts[0];
            pageTitle = pageParts[1];
         } else {
            spaceKey = context.getSpaceKey();
            pageTitle = parameters.get(PAGE_KEY);
         }

         page = pageManager.getPage( spaceKey, pageTitle );
         if( page == null ){
            return RenderUtils.blockError( "Page \"" + parameters.get( PAGE_KEY ) + "\" not found", "" );
         } else {
            url = baseUrl + page.getUrlPath();
         }
      }
      velocityContext.put( "url", url );

      if( parameters.containsKey( LINK_TEXT_KEY ) ){
         velocityContext.put( "url-text", parameters.get( LINK_TEXT_KEY ));
      } else if( page != null ) {
         velocityContext.put( "url-text", page.getTitle());
      } else {
         velocityContext.put( "url-text", url);
      }
        
      return velocityHelperService.getRenderedTemplate(template, velocityContext);
   }

   @Override
   public BodyType getBodyType()
   {
      return BodyType.NONE;
   }

   @Override
   public OutputType getOutputType()
   {
      return OutputType.INLINE;
   }
}
