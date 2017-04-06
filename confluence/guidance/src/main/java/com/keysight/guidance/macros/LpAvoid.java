package com.keysight.guidance.macros; 

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.confluence.plugin.services.VelocityHelperService;

public class LpAvoid extends Avoid
{
    public LpAvoid( PluginSettingsFactory pluginSettingsFactory,
                    SettingsManager settingsManager,
                    TransactionTemplate transactionTemplate,
                    VelocityHelperService velocityHelperService )
    {
       super( pluginSettingsFactory,
              settingsManager,
              transactionTemplate,
              velocityHelperService );
    }

    @Override
    protected boolean allowLpTag(){ return true; }
}
