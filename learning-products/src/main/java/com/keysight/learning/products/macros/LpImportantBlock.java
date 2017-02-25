package com.keysight.learning.products.macros; 

import java.util.Map;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;

public class LpImportantBlock extends LpHighlight
{
    public LpImportantBlock( VelocityHelperService velocityHelperService )
    {
       super( velocityHelperService );
    }

    @Override
    protected String getIconColorClass(){ return "keysight-lp-icon-blue-background"; }
    
    @Override
    protected String getIconText(){ return "IMPORTANT"; };

    @Override
    protected String getColorClass(){ return "keysight-lp-blue-background"; }

    @Override
    protected String getImage(){ return "important.png"; }

    @Override
    protected String getLpTag(){ return "@LP_IMPORTANT_START@"; }
}
