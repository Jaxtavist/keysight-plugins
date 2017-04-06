package com.keysight.html.elements.macros; 

import java.util.Map;
import java.util.UUID;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;

public class Block implements Macro
{
    private static final String WIDTH_KEY     = "width";
    private static final String ALIGNMENT_KEY = "alignment";
    private static final String LEFT          = "Left";
    private static final String CENTER        = "Center";
    private static final String RIGHT         = "Right";

    public Block()
    {
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context)
            throws MacroExecutionException
    {
        String tableWidth     = "";
	StringBuilder html    = new StringBuilder();
        String alignment      = null;
        boolean bAlignmentSet = false;
        String containerClass = "";
	String blockClass     = "";
	String style          = "";

        if( parameters.containsKey( ALIGNMENT_KEY ) ){
           alignment = parameters.get( ALIGNMENT_KEY );
	   bAlignmentSet = true;

	   if( alignment.matches( RIGHT ) ){
              containerClass = "keysight-container-block-right-justify";
              blockClass     = "keysight-inner-block-right-aligned";
	   } else if( alignment.matches( CENTER ) ){
              containerClass = "keysight-container-block-center-justify";
              blockClass     = "keysight-inner-block-center-aligned";
	   } else {
              containerClass = "keysight-container-block-left-justify";
              blockClass     = "keysight-inner-block-left-aligned";
	   }

	   html.append( "<div class=\"keysight-container-block " + containerClass + "\">\n" );
	}

        if( parameters.containsKey( WIDTH_KEY ) ){
           style = "style=\"width:"+parameters.get( WIDTH_KEY )+"\"";
	}

	html.append( "   <div class=\"keysight-inner-block " + blockClass + "\" " + style + ">\n" );
	html.append( body );
        html.append( "   </div>\n" );

	if( bAlignmentSet ){
	   html.append( "<div style=\"clear:both\"></div>\n" );
	   html.append( "</div>\n" );
	}
	
	return html.toString();
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
