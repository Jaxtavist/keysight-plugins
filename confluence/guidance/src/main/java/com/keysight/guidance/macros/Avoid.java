package com.keysight.guidance.macros; 

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.confluence.plugin.services.VelocityHelperService;

public class Avoid extends GuideLine
{
    public Avoid( PluginSettingsFactory pluginSettingsFactory,
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
    protected String getGuidance(){ return AVOID; }
    @Override
    protected String getGuidanceGlyph(){ return BALLOT_X; }
    @Override
    protected String getGuidanceClass(){ return GUIDANCE_BALLOT_X; }
    @Override
    protected String getLpStartTag(){ return LP_AVOID_START_TAG; }
    @Override
    protected boolean allowLpTag(){ return false; }
    @Override
    protected boolean fixedGuidance(){ return true; }
 
}
