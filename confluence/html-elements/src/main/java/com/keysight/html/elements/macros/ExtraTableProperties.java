package com.keysight.html.elements.macros; 

import java.util.Map;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.xhtml.api.XhtmlContent;

public class ExtraTableProperties implements Macro
{
    private static final String TABLE_WIDTH_KEY   = "table-width";
    private static final String COLUMN_WIDTHS_KEY = "column-widths";
    private static final String ROW_KEY           = "row";
    private static final String HIDE_BORDER_KEY   = "hide-border";
    protected final XhtmlContent xhtmlUtils;

    public ExtraTableProperties( XhtmlContent xhtmlUtils )
    {
        this.xhtmlUtils = xhtmlUtils;
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context)
            throws MacroExecutionException
    {
        String tableWidth   = "";
        String columnWidths = "";
        String row = "1";
        String divClass = "keysight-table-width";

        if( parameters.containsKey( HIDE_BORDER_KEY ) ){
           divClass = divClass + " keysight-no-table-border";
        }

        if( parameters.containsKey( TABLE_WIDTH_KEY ) ){
           tableWidth = parameters.get( TABLE_WIDTH_KEY );
        }
        
        if( parameters.containsKey( COLUMN_WIDTHS_KEY ) ){
           columnWidths = parameters.get( COLUMN_WIDTHS_KEY );
        }
        
        if( parameters.containsKey( ROW_KEY ) ){
           row = parameters.get( ROW_KEY );
        }

        return "<div class=\"" + divClass + "\" "
                   + "row=\""+row+"\" "
                   + "table-width=\""+tableWidth+"\"\n"
                   + "column-widths=\""+columnWidths+"\" >\n"
                   + body 
                   + "</div>\n"; 
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
