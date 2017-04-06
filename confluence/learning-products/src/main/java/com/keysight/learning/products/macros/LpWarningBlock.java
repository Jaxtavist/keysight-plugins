package com.keysight.learning.products.macros; 

import java.util.Map;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;

public class LpWarningBlock extends LpHighlight
{
    public LpWarningBlock( VelocityHelperService velocityHelperService )
    {
       super( velocityHelperService );
    }

    @Override
    protected String getIconColorClass(){ return "keysight-lp-icon-red-background"; }
    
    @Override
    protected String getIconText(){ return "WARNING"; }

    @Override
    protected String getColorClass(){ return "keysight-lp-red-background"; }

    @Override
    protected String getImage(){ return "warning.png"; }

    @Override
    protected String getLpTag(){ return "@LP_WARNING_START@"; }

}
