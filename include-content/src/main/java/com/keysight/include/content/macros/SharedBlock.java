package com.keysight.include.content.macros;

import java.util.Map;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;

public class SharedBlock implements Macro
{
    private static final String HIDDEN = "hidden";

    public SharedBlock(){
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context)
            throws MacroExecutionException
    {
        if( parameters.containsKey( HIDDEN ) ){
            return "";
        } else {
            return body;
        }
    }

    @Override
    public BodyType getBodyType()
    {
        return BodyType.RICH_TEXT;
    }

    @Override
    public OutputType getOutputType()
    {
        return OutputType.BLOCK;
    }
}
