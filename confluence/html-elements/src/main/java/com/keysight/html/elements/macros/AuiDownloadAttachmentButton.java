package com.keysight.html.elements.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.setup.settings.SettingsManager;

import java.util.Map;

public class AuiDownloadAttachmentButton extends AuiButton
{
   public AuiDownloadAttachmentButton( AttachmentManager attachmentManager,
                                       PageManager pageManager,
                                       SettingsManager settingsManager,
                                       SpaceManager spaceManager,
                                       VelocityHelperService velocityHelperService)
   {
      super( attachmentManager, pageManager, settingsManager, spaceManager, velocityHelperService );
   }

   /*
   @Override
   protected boolean createButtonGroup(){
      return true;
   }
   */

   @Override
   public OutputType getOutputType()
   {
      return OutputType.BLOCK;
   }

   @Override
   protected String[] resolveUrlAndText( Map<String, String> parameters, ConversionContext context){  
      String baseUrl = settingsManager.getGlobalSettings().getBaseUrl();
      String urlAndText[] = new String[2];

      urlAndText[0] = baseUrl + attachmentManager.getAttachmentDownloadPath( context.getEntity(), parameters.get( ATTACHMENT_KEY ) );

      if( parameters.containsKey( BUTTON_TEXT_KEY ) ){
         urlAndText[1] = parameters.get( BUTTON_TEXT_KEY );
      } else {
         urlAndText[1] = parameters.get( ATTACHMENT_KEY );
      }

      return urlAndText;
   }
}
