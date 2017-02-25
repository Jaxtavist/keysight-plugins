package com.keysight.guidance.macros; 

import com.atlassian.confluence.plugin.services.VelocityHelperService;

public class Consider extends GuideLine
{
    public Consider( VelocityHelperService velocityHelperService )
    {
       super( velocityHelperService );
    }

    @Override
    protected String getGuidance(){ return CONSIDER; }
    @Override
    protected String getGuidanceGlyph(){ return CHECK; }
    @Override
    protected String getGuidanceClass(){ return GUIDANCE_CHECK; }
    @Override
    protected String getLpStartTag(){ return LP_CONSIDER_START_TAG; }
    @Override
    protected boolean allowLpTag(){ return false; }
    @Override
    protected boolean fixedGuidance(){ return true; }
}
