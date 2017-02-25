package com.keysight.learning.products.macros; 

import java.util.Map;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.keysight.learning.products.helpers.LpHelper;

public class LpBlockQuote implements Macro
{
    private final static String BODY          = "body";        
    private final static String ENABLE_LP_TAG = "enableLpTag";        

    private final VelocityHelperService velocityHelperService;

    public LpBlockQuote( VelocityHelperService velocityHelperService )
    {
       this.velocityHelperService = velocityHelperService;
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context)
            throws MacroExecutionException
    {
        String template = "/com/keysight/learning-products/templates/lp-block-quote.vm";
        Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();

	if( LpHelper.IsLpExport( context ) ){
           velocityContext.put( ENABLE_LP_TAG, "true");
	}

        velocityContext.put( BODY, body );
        return velocityHelperService.getRenderedTemplate(template, velocityContext);
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
