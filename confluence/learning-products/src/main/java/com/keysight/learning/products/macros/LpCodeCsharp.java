package com.keysight.learning.products.macros; 

import java.util.Map;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;

public class LpCodeCsharp extends LpCode
{
    public LpCodeCsharp( VelocityHelperService velocityHelperService )
    {
       super( velocityHelperService );
    }

    @Override
    protected String getLanguage(){ return "c#"; }
    @Override
    protected String getLpTag(){ return "@LP_CODE_CSHARP_START@"; }
}
