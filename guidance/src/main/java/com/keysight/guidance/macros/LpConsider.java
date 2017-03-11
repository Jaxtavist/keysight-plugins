package com.keysight.guidance.macros; 

import com.atlassian.confluence.plugin.services.VelocityHelperService;

public class LpConsider extends Consider
{
    public LpConsider( VelocityHelperService velocityHelperService )
    {
       super( velocityHelperService );
    }

    @Override
    protected boolean allowLpTag(){ return true; }
}
