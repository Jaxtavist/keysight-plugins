package com.keysight.html.elements.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.setup.settings.SettingsManager;

import java.util.Map;

public class AuiSplitButton extends AuiButton
{
   public AuiSplitButton( AttachmentManager attachmentManager,
                          PageManager pageManager,
                          SettingsManager settingsManager,
                          SpaceManager spaceManager,
                          VelocityHelperService velocityHelperService)
   {
      super( attachmentManager, pageManager, settingsManager, spaceManager, velocityHelperService );
   }

   @Override
   public String execute(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException
   {
      String template = "/com/keysight/html-elements/templates/aui-split-button.vm";
      Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();

      if( parameters.containsKey( CREATE_BUTTON_GROUP_KEY ) || createButtonGroup() ){
         velocityContext.put( CREATE_BUTTON_GROUP_KEY, "true" );
      }

      String[] parts = resolveUrlAndText( parameters, context );
      velocityContext.put( BUTTON_URL_KEY, parts[0] );
      velocityContext.put( BUTTON_TEXT_KEY, parts[1] );

      // examine and prepare the body...
      body = body.trim();
      if( !( body.matches( "<ul.*" ) && body.matches( ".*</ul>" ) ) ){
         body = "<ul class=\"aui-list-truncate\"><li><a href=\"#\">Improper body.  Needs to be a single unordered (bullet) list of anchors</li></ul>";
      } else {
         body.replaceFirst( "<ul>", "<ul class=\"aui-list-truncate\">" );
      }

      velocityContext.put( DROPDOWN_MENU_ID_KEY, generateId() );
      velocityContext.put( RENDERED_BODY_KEY, body );

      return velocityHelperService.getRenderedTemplate(template, velocityContext);
   }

   @Override
   public BodyType getBodyType()
   {
      return BodyType.RICH_TEXT;
   }
}
