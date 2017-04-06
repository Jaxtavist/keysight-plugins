package com.keysight.learning.products.macros; 

import java.util.Map;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;

public class LpCautionBlock extends LpHighlight
{
    public LpCautionBlock( VelocityHelperService velocityHelperService )
    {
       super( velocityHelperService );
    }
    @Override
    protected String getIconColorClass(){ return "keysight-lp-icon-yellow-background"; }
    
    @Override
    protected String getIconText(){ return "CAUTION"; } 

    @Override
    protected String getColorClass(){ return "keysight-lp-yellow-background"; }

    @Override
    protected String getImage(){ return "caution.png"; }

    @Override
    protected String getLpTag(){ return "@LP_CAUTION_START@"; }
}
