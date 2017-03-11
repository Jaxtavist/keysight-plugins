package com.keysight.guidance.macros; 

import com.atlassian.confluence.plugin.services.VelocityHelperService;

public class LpGuideLine extends GuideLine
{
    public LpGuideLine( VelocityHelperService velocityHelperService )
    {
       super( velocityHelperService );
    }

    @Override
    protected boolean allowLpTag(){ return true; }
}
