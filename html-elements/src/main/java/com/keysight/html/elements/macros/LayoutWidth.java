package com.keysight.html.elements.macros; 

import java.util.Map;
import java.util.UUID;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.xhtml.api.XhtmlContent;

public class LayoutWidth implements Macro
{
    private static final String LAYOUT_WIDTH_KEY   = "layout-width";
    protected final XhtmlContent xhtmlUtils;

    public LayoutWidth( XhtmlContent xhtmlUtils )
    {
        this.xhtmlUtils = xhtmlUtils;
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context)
            throws MacroExecutionException
    {
        String tableWidth   = "";

        if( parameters.containsKey( LAYOUT_WIDTH_KEY ) ){
           tableWidth = parameters.get( LAYOUT_WIDTH_KEY );
	   UUID id = UUID.randomUUID();
	   return "<script type=\"text/javascript\" id=\""+id+"\">\n"
		 +"//<![CDATA[\n"
		 +"setLayoutWidth(\"" + id + "\",\"" + parameters.get( LAYOUT_WIDTH_KEY ) + "\");"
		 +"//]]>"
		 +"</script>\n";
        } else {
           return "";
	}
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
