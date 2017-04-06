package com.keysight.plugins.confluence.special.handling.properties.macros;

import java.util.Map;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.ConfluenceUser;

public class SpecialHandlingProperties implements Macro
{

    private static final String PL_KEY = "pl";
    private static final String STATUS_KEY = "status";
    private static final String TYPE_KEY = "type";
    private static final String AVAILABILITY_KEY = "availability";
    private static final String PRICE_KEY = "price";
    private static final String DNQA_KEY = "dnqa"; // dnqa = do not quote after
    private static final String ORIGINATOR_KEY = "originator";
    private static final String TOTAL_OR_ADD_KEY = "ta";
    
    protected final VelocityHelperService velocityHelperService;
    protected final UserAccessor userAccessor;

    public SpecialHandlingProperties( UserAccessor userAccessor,
		                      VelocityHelperService velocityHelperService )
    {
        this.velocityHelperService = velocityHelperService;
	this.userAccessor = userAccessor;
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context)
            throws MacroExecutionException
    {
        String template = "/templates/special-handling-properties.vm";
        Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();
        int i;

        if( parameters.containsKey( PL_KEY ) ){
           velocityContext.put( PL_KEY, parameters.get( PL_KEY ) );
        } else {
           velocityContext.put( PL_KEY, "None" );
        }
        
        if( parameters.containsKey( STATUS_KEY ) ){
           velocityContext.put( STATUS_KEY, parameters.get( STATUS_KEY ) );
        } else {
           velocityContext.put( STATUS_KEY, "A - Active" );
        }
        
        if( parameters.containsKey( TYPE_KEY ) ){
           velocityContext.put( TYPE_KEY, parameters.get( TYPE_KEY ) );
        } else {
           velocityContext.put( TYPE_KEY, "SSH" );
        }
        
        if( parameters.containsKey( AVAILABILITY_KEY ) ){
           velocityContext.put( AVAILABILITY_KEY, parameters.get( AVAILABILITY_KEY ) );
        } else {
           velocityContext.put( AVAILABILITY_KEY, "" );
        }

        if( parameters.containsKey( PRICE_KEY ) ){
           velocityContext.put( PRICE_KEY, parameters.get( PRICE_KEY ) );
        } else {
           velocityContext.put( PRICE_KEY, "0" );
        }
        
        if( parameters.containsKey( DNQA_KEY ) ){
           velocityContext.put( DNQA_KEY, "<time datetime=\""+parameters.get( DNQA_KEY )+"\" />" );
        } else {
           velocityContext.put( DNQA_KEY, "2010-01-01" );
        }
        
        if( parameters.containsKey( ORIGINATOR_KEY ) ){
           ConfluenceUser operator = userAccessor.getUserByName( parameters.get( ORIGINATOR_KEY ) );
	   if( operator != null ){
              velocityContext.put( ORIGINATOR_KEY, "<ac:link><ri:user ri:userkey=\""+operator.getKey()+"\" /></ac:link>" );
           } else {
              velocityContext.put( ORIGINATOR_KEY, parameters.get( ORIGINATOR_KEY ) );
           }
        } else {
           velocityContext.put( ORIGINATOR_KEY, "" );
        }
        
        if( parameters.containsKey( TOTAL_OR_ADD_KEY ) ){
           velocityContext.put( TOTAL_OR_ADD_KEY, parameters.get( TOTAL_OR_ADD_KEY ) );
        } else {
           velocityContext.put( TOTAL_OR_ADD_KEY, "Add" );
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
