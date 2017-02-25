package com.keysight.html.elements.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.setup.settings.SettingsManager;

import java.util.Map;
import java.util.Random;

import com.keysight.html.elements.helpers.NavigationPageResolver;

public class AuiButton implements Macro
{
   public final static String PAGE_KEY                = "page";
   public final static String ATTACHMENT_KEY          = "name";
   public final static String BUTTON_URL_KEY          = "button-url";
   public final static String SPACEKEY_KEY            = "spaceKey";
   public final static String BUTTON_TEXT_KEY         = "button-text";
   public final static String BUTTON_STYLE_KEY        = "button-style";
   public final static String CREATE_BUTTON_GROUP_KEY = "create-button-group";
   public final static String RENDERED_BODY_KEY       = "renderedBodyHtml";
   public final static String DROPDOWN_MENU_ID_KEY    = "dropdown-menu-id";
   public final static String NO_BUTTON_KEY           = "no-button";

   protected NavigationPageResolver navigationPageResolver;
   protected final AttachmentManager attachmentManager;
   protected final PageManager pageManager;
   protected final SettingsManager settingsManager;
   protected final SpaceManager spaceManager;
   protected final VelocityHelperService velocityHelperService;

   public AuiButton( AttachmentManager attachmentManager,
                  PageManager pageManager,
                  SettingsManager settingsManager,
                  SpaceManager spaceManager,
                  VelocityHelperService velocityHelperService)
   {
      this.attachmentManager = attachmentManager;
      this.pageManager = pageManager;
      this.settingsManager = settingsManager;
      this.spaceManager = spaceManager;
      this.velocityHelperService = velocityHelperService;
   }

   @Override
   public String execute(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException
   {
      String template = "/com/keysight/html-elements/templates/aui-button.vm";
      Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();

      if( createButtonGroup() ){
         velocityContext.put( CREATE_BUTTON_GROUP_KEY, "true" );
      } 

      String[] parts = resolveUrlAndText( parameters, context );
      if( parts.length == 2 ){
         velocityContext.put( BUTTON_URL_KEY, parts[0] );
         if( parameters.containsKey( BUTTON_TEXT_KEY ) ){
            velocityContext.put( BUTTON_TEXT_KEY, parameters.get( BUTTON_TEXT_KEY ) );
         } else {
            velocityContext.put( BUTTON_TEXT_KEY, parts[1] );
         }

         if( parameters.containsKey( BUTTON_STYLE_KEY ) ){
            if( parameters.get( BUTTON_STYLE_KEY ).equals( "Primary" ) ){
               velocityContext.put( BUTTON_STYLE_KEY, "aui-button-primary" );
            } else if( parameters.get( BUTTON_STYLE_KEY ).equals( "Link-Style" ) ){
               velocityContext.put( BUTTON_STYLE_KEY, "aui-button-link" );
            } else if( parameters.get( BUTTON_STYLE_KEY ).equals( "Subtle" ) ){
               velocityContext.put( BUTTON_STYLE_KEY, "aui-button-subtle" );
            }
         }
      } else {
         velocityContext.put( NO_BUTTON_KEY, "true" );
      }
        
      return velocityHelperService.getRenderedTemplate(template, velocityContext);
   }

   protected String[] resolveUrlAndText( Map<String, String> parameters, ConversionContext context){  
      String baseUrl = settingsManager.getGlobalSettings().getBaseUrl();
      String[] parts = new String[2];
      String pageTitle = null;
      String spaceKey = null;
        
      if( parameters.containsKey( SPACEKEY_KEY ) ) {
         Space space = spaceManager.getSpace( parameters.get(SPACEKEY_KEY) );
         parts[0] = baseUrl + space.getUrlPath();
         if( parameters.containsKey( BUTTON_TEXT_KEY ) ){
            parts[1] = parameters.get( BUTTON_TEXT_KEY );
         } else {
            parts[1] = space.getName();
         }
      } else {
         if( parameters.get( BUTTON_URL_KEY ).matches( ".*:.*" ) ){
            String[] pageParts = parameters.get(BUTTON_URL_KEY).split( ":", 2 );
            spaceKey = pageParts[0];
            pageTitle = pageParts[1];
         } else {
            spaceKey = context.getSpaceKey();
            pageTitle = parameters.get(BUTTON_URL_KEY);
         }

         Page page = pageManager.getPage( spaceKey, pageTitle );
         if( page != null ){
            parts[0] = baseUrl + page.getUrlPath();
            if( parameters.containsKey( BUTTON_TEXT_KEY ) ){
               parts[1] = parameters.get( BUTTON_TEXT_KEY );
            } else {
               parts[1] = page.getTitle();
            }
         } else {
             parts[0] = parameters.get( BUTTON_URL_KEY );
             if( parameters.containsKey( BUTTON_TEXT_KEY ) ){
               parts[1] = parameters.get( BUTTON_TEXT_KEY );
             } else {
               parts[1] = parameters.get( BUTTON_URL_KEY );
             }
         }
      }
      return parts;
   }

   protected String generateId() {
      Random randomNumberGenerator = new Random();
      int size = 10000;
      return( "menu-id-" 
              + randomNumberGenerator.nextInt( size ) + "-" 
              + randomNumberGenerator.nextInt( size ) );
   }
   
   protected boolean createButtonGroup(){
      return false;
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
