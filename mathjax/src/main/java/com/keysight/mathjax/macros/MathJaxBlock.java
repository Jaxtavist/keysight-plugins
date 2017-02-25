package com.keysight.mathjax.macros; 

import java.util.Map;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.renderer.v2.components.HtmlEscaper;

public class MathJaxBlock implements Macro
{
    public MathJaxBlock()
    {
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context)
            throws MacroExecutionException
    {
	if( body.length() == 0 ){
           body = "No equation provided";
	}
	return( "\\[" + HtmlEscaper.escapeAll( body, true ) + "\\]" );
    }

    @Override
    public BodyType getBodyType()
    {
        return BodyType.PLAIN_TEXT;
    }

    @Override
    public OutputType getOutputType()
    {
        return OutputType.BLOCK;
    }
}
