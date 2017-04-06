package com.keysight.learning.products.macros; 

import java.util.Map;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;

public class LpCodeCplus extends LpCode
{
    public LpCodeCplus( VelocityHelperService velocityHelperService )
    {
       super( velocityHelperService );
    }

    @Override
    protected String getLanguage(){ return "cpp"; }
    @Override
    protected String getLpTag(){ return "@LP_CODE_CPLUS_START@"; }
}
