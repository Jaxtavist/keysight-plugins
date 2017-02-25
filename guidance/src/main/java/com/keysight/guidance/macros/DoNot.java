package com.keysight.guidance.macros; 

import com.atlassian.confluence.plugin.services.VelocityHelperService;

public class DoNot extends GuideLine
{
    public DoNot( VelocityHelperService velocityHelperService )
    {
       super( velocityHelperService );
    }

    @Override
    protected String getGuidance(){ return DO_NOT; }
    @Override
    protected String getGuidanceGlyph(){ return BALLOT_X; }
    @Override
    protected String getGuidanceClass(){ return GUIDANCE_BALLOT_X; }
    @Override
    protected String getLpStartTag(){ return LP_DO_NOT_START_TAG; }
    @Override
    protected boolean allowLpTag(){ return false; }
    @Override
    protected boolean fixedGuidance(){ return true; }
   
}
