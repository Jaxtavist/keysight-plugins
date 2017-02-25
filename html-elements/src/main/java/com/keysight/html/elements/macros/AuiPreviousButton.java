package com.keysight.html.elements.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.security.PermissionManager;

import java.util.Map;
import java.util.List;

import com.keysight.html.elements.helpers.NavigationPageResolver;

public class AuiPreviousButton extends AuiButton
{
   public AuiPreviousButton( AttachmentManager attachmentManager,
                             PageManager pageManager,
                             PermissionManager permissionManager,
                             SettingsManager settingsManager,
                             SpaceManager spaceManager,
                             VelocityHelperService velocityHelperService)
   {
      super( attachmentManager, pageManager, settingsManager, spaceManager, velocityHelperService );
      this.navigationPageResolver = new NavigationPageResolver( pageManager, permissionManager, settingsManager, spaceManager );
   }

   @Override
   protected String[] resolveUrlAndText( Map<String, String> parameters, ConversionContext context){  
      return navigationPageResolver.resolveUrlAndTextForPreviousPage( parameters, context );
   }
}
