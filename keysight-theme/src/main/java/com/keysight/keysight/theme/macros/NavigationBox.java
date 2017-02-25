package com.keysight.keysight.theme.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.setup.settings.SettingsManager;

import java.util.Map;

public class NavigationBox implements Macro
{
   public final static String TITLE_KEY        = "title";
   public final static String PAGE_KEY         = "page";
   public final static String URL_KEY          = "url";
   public final static String STYLE_KEY        = "name";
   public final static String HEADER_STYLE_KEY = "headerstyle";
   public final static String BODY_STYLE_KEY   = "bodystyle";
   public final static String BODY_KEY         = "body";

   protected final VelocityHelperService velocityHelperService;
   protected final PageManager pageManager;
   protected final SettingsManager settingsManager;

   public NavigationBox( PageManager pageManager,
		         SettingsManager settingsManager,
		         VelocityHelperService velocityHelperService)
   {
      this.pageManager = pageManager;
      this.settingsManager = settingsManager;
      this.velocityHelperService = velocityHelperService;
   }

   @Override
   public String execute(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException
   {
      String template = "/com/keysight/keysight-theme/templates/navigation-box.vm";
      Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();
      String baseUrl   = settingsManager.getGlobalSettings().getBaseUrl();
      String pageId    = null;
      String spaceKey  = null;
      String pageTitle = null;
      String url       = null;
      String title     = null;
      Page page        = null;
      boolean bUsePageTitle = false;

      if( parameters.containsKey( PAGE_KEY ) ){
         pageId = parameters.get( PAGE_KEY );
      } else if( parameters.containsKey( TITLE_KEY ) ){
	 title = parameters.get( TITLE_KEY );
	 if( title.matches( "\\[.*\\]" ) ){
            pageId = title.substring(1, title.length() - 1);
	    bUsePageTitle = true;
	 }
      }

      if( pageId != null ){
	 if( pageId.matches( "http.*" ) ){
            url = pageId;
	 } else {
	    if( pageId.matches( ".*:.*" ) ){
               String[] pageIdParts = pageId.split( ":", 2 );
	       spaceKey = pageIdParts[0];
	       pageTitle  = pageIdParts[1];
	    } else {
               spaceKey = context.getSpaceKey();
	       pageTitle = pageId;
	    }

	    page = pageManager.getPage( spaceKey, pageTitle );
	    if( page != null ){
               url = baseUrl + page.getUrlPath();
	    }
	 }
      }
      
      if( parameters.containsKey( TITLE_KEY ) ){
	 if( bUsePageTitle && page != null ){
            velocityContext.put( TITLE_KEY, page.getTitle() );
	 } else {
            velocityContext.put( TITLE_KEY, parameters.get( TITLE_KEY ) );
	 }
      } else if( parameters.containsKey( PAGE_KEY ) ){
	 if( page != null ){
            velocityContext.put( TITLE_KEY, page.getTitle() );
	 } else {
            velocityContext.put( TITLE_KEY, pageId );
	 }
      }
      
      if( url != null ){
         velocityContext.put( URL_KEY, url );
      } 

      if( parameters.containsKey( STYLE_KEY ) ){
         if( parameters.get( STYLE_KEY ).equals( "Keysight Dark Red" ) ){
            velocityContext.put( HEADER_STYLE_KEY, "navigation-box navigation-box-header navigation-box-top keysight-dark-red keysight-dark-red-header" );
            velocityContext.put( BODY_STYLE_KEY,   "navigation-box navigation-box-body   keysight-dark-red keysight-dark-red-body" );
	 } else if( parameters.get( STYLE_KEY ).equals( "Keysight Gray" ) ){
            velocityContext.put( HEADER_STYLE_KEY, "navigation-box navigation-box-header navigation-box-top keysight-gray keysight-gray-header" );
            velocityContext.put( BODY_STYLE_KEY,   "navigation-box navigation-box-body   keysight-gray keysight-gray-body" );
	 } else if( parameters.get( STYLE_KEY ).equals( "Keysight Dark Gray" ) ){
            velocityContext.put( HEADER_STYLE_KEY, "navigation-box navigation-box-header navigation-box-top keysight-dark-gray keysight-dark-gray-header" );
            velocityContext.put( BODY_STYLE_KEY,   "navigation-box navigation-box-body   keysight-dark-gray keysight-dark-gray-body" );
	 } else {
            velocityContext.put( HEADER_STYLE_KEY, "navigation-box navigation-box-header navigation-box-top " + parameters.get( HEADER_STYLE_KEY ) );
            velocityContext.put( BODY_STYLE_KEY,   "navigation-box navigation-box-body   " + parameters.get( BODY_STYLE_KEY ) );
         }
      } 
        
      velocityContext.put( BODY_KEY, body );
      return velocityHelperService.getRenderedTemplate(template, velocityContext);
   }

   @Override
   public BodyType getBodyType()
   {
      return BodyType.RICH_TEXT;
   }

   @Override
   public OutputType getOutputType()
   {
      return OutputType.BLOCK;
   }
}
