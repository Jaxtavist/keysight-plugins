package com.keysight.mathjax.macros; 

import java.util.Map;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.renderer.v2.components.HtmlEscaper;

public class MathJaxInline implements Macro
{
    public final static String EQUATION = "equation";

    public MathJaxInline()
    {
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context)
            throws MacroExecutionException
    {
	String equation = "No equation provided";

        if( parameters.containsKey( EQUATION ) ){
           equation = parameters.get( EQUATION );  
	}

	return( "\\(" + HtmlEscaper.escapeAll( equation, true ) + "\\)" );
    }

    @Override
    public BodyType getBodyType()
    {
        return BodyType.NONE;
    }

    @Override
    public OutputType getOutputType()
    {
        return OutputType.INLINE;
    }
}
