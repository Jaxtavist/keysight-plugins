package com.keysight.guidance.macros; 

import com.atlassian.confluence.plugin.services.VelocityHelperService;

public class LpDo extends Do
{
    public LpDo( VelocityHelperService velocityHelperService )
    {
       super( velocityHelperService );
    }

    @Override
    protected boolean allowLpTag(){ return true; }
}
