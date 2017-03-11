package com.keysight.guidance.macros; 

import com.atlassian.confluence.plugin.services.VelocityHelperService;

public class LpAvoid extends Avoid
{
    public LpAvoid( VelocityHelperService velocityHelperService )
    {
       super( velocityHelperService );
    }

    @Override
    protected boolean allowLpTag(){ return true; }
}
