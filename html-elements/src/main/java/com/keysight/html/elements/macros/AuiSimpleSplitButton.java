package com.keysight.html.elements.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.setup.settings.SettingsManager;

import java.util.Map;

public class AuiSimpleSplitButton extends AuiSplitButton
{
   public AuiSimpleSplitButton( AttachmentManager attachmentManager,
                                PageManager pageManager,
                                SettingsManager settingsManager,
                                SpaceManager spaceManager,
                                VelocityHelperService velocityHelperService)
   {
      super( attachmentManager, pageManager, settingsManager, spaceManager, velocityHelperService );
   }

   @Override
   public boolean createButtonGroup(){
      return true;
   }
    
   @Override
   public OutputType getOutputType()
   {
      return OutputType.BLOCK;
   }
}
