package com.keysight.learning.products.macros; 

import java.util.Map;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;

public class LpTipBlock extends LpHighlight
{
    public LpTipBlock( VelocityHelperService velocityHelperService )
    {
       super( velocityHelperService );
    }

    @Override
    protected String getIconColorClass(){ return "keysight-lp-icon-green-background"; }
    
    @Override
    protected String getIconText(){ return "TIP"; }

    @Override
    protected String getColorClass(){ return "keysight-lp-green-background"; }

    @Override
    protected String getImage(){ return "tip.png"; }

    @Override
    protected String getLpTag(){ return "@LP_TIP_START@"; }

}
