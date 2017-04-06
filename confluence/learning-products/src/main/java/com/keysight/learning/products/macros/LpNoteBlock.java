package com.keysight.learning.products.macros; 

import java.util.Map;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;

public class LpNoteBlock extends LpHighlight
{
    public LpNoteBlock( VelocityHelperService velocityHelperService )
    {
       super( velocityHelperService );
    }

    @Override
    protected String getIconColorClass(){ return "keysight-lp-icon-gray-background"; }
    
    @Override
    protected String getIconText(){ return "NOTE"; }

    @Override
    protected String getColorClass(){ return "keysight-lp-gray-background"; }

    @Override
    protected String getImage(){ return "note.png"; }

    @Override
    protected String getLpTag(){ return "@LP_NOTE_START@"; }
}
