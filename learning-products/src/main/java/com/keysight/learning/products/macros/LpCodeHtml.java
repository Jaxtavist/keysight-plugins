package com.keysight.learning.products.macros; 

import java.util.Map;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;

public class LpCodeHtml extends LpCode
{
    public LpCodeHtml( VelocityHelperService velocityHelperService )
    {
       super( velocityHelperService );
    }

    @Override
    protected String getLanguage(){ return "xml"; }
    @Override
    protected String getLpTag(){ return "@LP_CODE_HTML_START@"; }
}
