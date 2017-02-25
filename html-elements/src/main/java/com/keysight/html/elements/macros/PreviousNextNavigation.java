package com.keysight.html.elements.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.security.PermissionManager;

import java.util.Map;
import java.util.Random;

import com.keysight.html.elements.helpers.NavigationPageResolver;

public class PreviousNextNavigation implements Macro
{
   public final static String EXCLUDE_PARENT_BUTTON_KEY   = "exclude-parent-button";
   public final static String PREVIOUS_BUTTON_TEXT_KEY    = "previous-button-text";
   public final static String PREVIOUS_BUTTON_URL_KEY     = "previous-button-url";
   public final static String NO_PREVIOUS_PAGE_BUTTON_KEY = "no-previous-page-button";
   public final static String PARENT_BUTTON_TEXT_KEY      = "parent-button-text";
   public final static String PARENT_BUTTON_URL_KEY       = "parent-button-url";
   public final static String NO_PARENT_PAGE_BUTTON_KEY   = "no-parent-page-button";
   public final static String NEXT_BUTTON_TEXT_KEY        = "next-button-text";
   public final static String NEXT_BUTTON_URL_KEY         = "next-button-url";
   public final static String NO_NEXT_PAGE_BUTTON_KEY     = "no-next-page-button";
   public final static String BUTTON_STYLE_KEY            = "button-style";
   
   protected final NavigationPageResolver navigationPageResolver;
   protected final PageManager pageManager;
   protected final SpaceManager spaceManager;
   protected final SettingsManager settingsManager;
   protected final VelocityHelperService velocityHelperService;

   public PreviousNextNavigation( PageManager pageManager,
                                  PermissionManager permissionManager,
                                  SettingsManager settingsManager,
                                  SpaceManager spaceManager,
                                  VelocityHelperService velocityHelperService)
   {
      this.pageManager = pageManager;
      this.settingsManager = settingsManager;
      this.spaceManager = spaceManager;
      this.velocityHelperService = velocityHelperService;

      this.navigationPageResolver = new NavigationPageResolver( pageManager, permissionManager, settingsManager, spaceManager );
   }

   @Override
   public String execute(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException
   {
      String template = "/com/keysight/html-elements/templates/previous-next-navigation.vm";
      Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();
      String[] parts = null;

      if( parameters.containsKey( BUTTON_STYLE_KEY ) ){
         if( parameters.get( BUTTON_STYLE_KEY ).equals( "Primary" ) ){
            velocityContext.put( BUTTON_STYLE_KEY, "aui-button-primary" );
         } else if( parameters.get( BUTTON_STYLE_KEY ).equals( "Link-Style" ) ){
            velocityContext.put( BUTTON_STYLE_KEY, "aui-button-link" );
         } else if( parameters.get( BUTTON_STYLE_KEY ).equals( "Subtle" ) ){
            velocityContext.put( BUTTON_STYLE_KEY, "aui-button-subtle" );
         }
      }
      
      // previous page
      parts = navigationPageResolver.resolveUrlAndTextForPreviousPage( parameters, context );
      if( parts.length == 2 ){
         velocityContext.put( PREVIOUS_BUTTON_URL_KEY, parts[0] );
         velocityContext.put( PREVIOUS_BUTTON_TEXT_KEY, parts[1] );
      } else {
         velocityContext.put( NO_PREVIOUS_PAGE_BUTTON_KEY, "true" );
      }
      
      // parent page
      if( parameters.containsKey( EXCLUDE_PARENT_BUTTON_KEY ) ){
         velocityContext.put( NO_PARENT_PAGE_BUTTON_KEY, "true" );
      } else {
         parts = navigationPageResolver.resolveUrlAndTextForParentPage( parameters, context );
         if( parts.length == 2 ){
            velocityContext.put( PARENT_BUTTON_URL_KEY, parts[0] );
            velocityContext.put( PARENT_BUTTON_TEXT_KEY, parts[1] );
         } else {
            velocityContext.put( NO_PARENT_PAGE_BUTTON_KEY, "true" );
         }
      }
      
      // next page
      parts = navigationPageResolver.resolveUrlAndTextForNextPage( parameters, context );
      if( parts.length == 2 ){
         velocityContext.put( NEXT_BUTTON_URL_KEY, parts[0] );
         velocityContext.put( NEXT_BUTTON_TEXT_KEY, parts[1] );
      } else {
         velocityContext.put( NO_NEXT_PAGE_BUTTON_KEY, "true" );
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
      return OutputType.BLOCK;
   }
}
