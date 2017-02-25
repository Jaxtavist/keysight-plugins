package com.keysight.learning.products.macros; 

import java.util.Map;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.keysight.learning.products.helpers.LpHelper;

public class LpHighlight implements Macro
{
    protected final static String BODY               = "body";        
    protected final static String COLOR_CLASS        = "colorClass";        
    protected final static String LP_TAG             = "lpTag";        
    protected final static String END_LP_TAG         = "endLpTag";        
    protected final static String IMAGE              = "image";        
    protected final static String ICON_COLOR_CLASS   = "iconColorClass";        
    protected final static String ICON_TEXT          = "iconText";        
    protected final static String RENDER_WITH_TABLES = "renderWithTables";        
    protected final static String RENDER_WITH        = "renderWith";
    protected final static String ENABLE_LP_TAG      = "enableLpTag";

    protected final VelocityHelperService velocityHelperService;
    
    public LpHighlight( VelocityHelperService velocityHelperService )
    {
       this.velocityHelperService = velocityHelperService;
    }

    protected String getColorClass(){ return "keysight-lp-gray-background"; }
    protected String getImage(){ return "note.png"; }
    protected String getLpTag(){ return ""; }
    protected String getIconColorClass(){ return "keysight-lp-icon-gray-background"; }
    protected String getIconText(){ return "NOTE"; }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context)
            throws MacroExecutionException
    {
        Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();
        String template = "/com/keysight/learning-products/templates/lp-highlight.vm";
	String lpTag = this.getLpTag();
	String endLpTag = lpTag.replaceAll( "START@$", "END@" );

	if( LpHelper.IsLpExport( context ) ){
           velocityContext.put( ENABLE_LP_TAG, "true");
           velocityContext.put( RENDER_WITH_TABLES, "true");
        } else if( parameters.containsKey( RENDER_WITH ) && parameters.get( RENDER_WITH ).equals( "Tables" ) ){
           velocityContext.put( RENDER_WITH_TABLES, "true");
        }

        velocityContext.put( BODY, body );
        velocityContext.put( COLOR_CLASS, this.getColorClass() );
        velocityContext.put( ICON_COLOR_CLASS, this.getIconColorClass());
        velocityContext.put( ICON_TEXT, this.getIconText());
        velocityContext.put( IMAGE, this.getImage() );
        velocityContext.put( LP_TAG, lpTag);
        velocityContext.put( END_LP_TAG, endLpTag);

        return velocityHelperService.getRenderedTemplate( template, velocityContext);
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
