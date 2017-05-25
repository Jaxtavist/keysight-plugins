package com.keysight.mathjax.macros; 

import java.util.Map;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.renderer.v2.components.HtmlEscaper;

import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.xhtml.api.XhtmlContent;

public class MathJaxInline implements Macro
{
    protected final VelocityHelperService velocityHelperService;
    protected final XhtmlContent xhtmlUtils;

    public final static String EQUATION     = "equation";
    public final static String BODY_AS_HTML = "bodyAsHtml";

    public MathJaxInline( VelocityHelperService velocityHelperService,
                          XhtmlContent xhtmlUtils)
    {
       this.velocityHelperService = velocityHelperService;
       this.xhtmlUtils = xhtmlUtils;
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context)
            throws MacroExecutionException
    {
	   String equation = "No equation provided";
       String template = "/com/keysight/mathjax/templates/mathjax-inline.vm";
       Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();

       if( parameters.containsKey( EQUATION ) ){
	       equation = "\\(" + HtmlEscaper.escapeAll( parameters.get( EQUATION ), true ) + "\\)";
	   }

       velocityContext.put( BODY_AS_HTML, equation );

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
        return OutputType.INLINE;
    }
}
