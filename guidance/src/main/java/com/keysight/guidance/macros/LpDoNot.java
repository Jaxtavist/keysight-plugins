package com.keysight.guidance.macros; 

import com.atlassian.confluence.plugin.services.VelocityHelperService;

public class LpDoNot extends DoNot
{
    public LpDoNot( VelocityHelperService velocityHelperService )
    {
       super( velocityHelperService );
    }

    @Override
    protected boolean allowLpTag(){ return true; }
}
