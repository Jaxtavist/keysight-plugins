package com.keysight.confluence.support.macros;

import java.util.Map;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;

public class AdditionalMacroParameterTypes implements Macro
{

    private static final String DATE_KEY    = "myDate";
    private static final String INTEGER_KEY = "myInteger";
    private static final String FLOAT_KEY   = "myFloat";
    
    protected final VelocityHelperService velocityHelperService;

    public AdditionalMacroParameterTypes( VelocityHelperService velocityHelperService )
    {
        this.velocityHelperService = velocityHelperService;
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context)
            throws MacroExecutionException
    {
        String template = "/templates/additional-macro-parameter-types.vm";
        Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();
        int i;

        if( parameters.containsKey( DATE_KEY ) ){
           velocityContext.put( DATE_KEY, parameters.get( DATE_KEY ) );
        } else {
           velocityContext.put( DATE_KEY, "No Date Specified" );
        }
        
        if( parameters.containsKey( INTEGER_KEY ) ){
           velocityContext.put( INTEGER_KEY, parameters.get( INTEGER_KEY ) );
        } else {
           velocityContext.put( INTEGER_KEY, "No Integer Specified" );
        }
        
        if( parameters.containsKey( FLOAT_KEY ) ){
           velocityContext.put( FLOAT_KEY, parameters.get( FLOAT_KEY ) );
        } else {
           velocityContext.put( FLOAT_KEY, "No Float Specified" );
        }

        return velocityHelperService.getRenderedTemplate(template, velocityContext);
    }

    @Override
    public BodyType getBodyType()
    {
        return BodyType.NONE;
    }

    @Override
    public OutputType getOutputType()
    {
        return OutputType.BLOCK;
    }
}
