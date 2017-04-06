package com.keysight.learning.products.macros; 

import java.util.Map;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.keysight.learning.products.helpers.LpHelper;

public class LpInternal implements Macro
{
    private final static String BODY        = "body";        
    private final static String LP_EXPORT   = "lpExport";        
    private final static String LEADING_TAG = "leadingTagAsHtml";        

    private final VelocityHelperService velocityHelperService;

    public LpInternal( VelocityHelperService velocityHelperService )
    {
       this.velocityHelperService = velocityHelperService;
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context)
            throws MacroExecutionException
    {
        String template = "/com/keysight/learning-products/templates/lp-internal.vm";
        Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();
	String leadingTag = "";

	if( LpHelper.IsLpExport( context ) ){
           velocityContext.put( LP_EXPORT, "true" );

           // This code is used to move the start of the html tag before the guidance term rather 
           // than after it.
           int gtIndex = body.indexOf( ">" );
           if( gtIndex >= 0 ){
              if( body.matches( "^(?s)\\s*<.*" ) ){
                 leadingTag = body.substring( 0, gtIndex + 1 );
                 body = body.substring( gtIndex + 1, body.length() );
                 velocityContext.put( LEADING_TAG, leadingTag );
	      } else {
                 int ltIndex = body.indexOf( "<" );
                 leadingTag = "<p>";
                 body = body.substring( 0, ltIndex) + "</p>\n" + body.substring( ltIndex, body.length() );
                 velocityContext.put( LEADING_TAG, leadingTag );
	      }
           } else {
              leadingTag = "<p>";
              body = body + "</p>";
              velocityContext.put( LEADING_TAG, leadingTag );
	   }
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
