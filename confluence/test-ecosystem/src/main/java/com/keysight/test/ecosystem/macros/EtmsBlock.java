package com.keysight.test.ecosystem.macros;

import java.util.Map;
import org.apache.commons.lang.StringUtils;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.velocity.htmlsafe.HtmlFragment;
import com.atlassian.renderer.RenderContext;

public class EtmsBlock implements Macro
{
    private final VelocityHelperService velocityHelperService;
    private boolean showFse = false;
    private boolean showIge = false;
    private boolean showMma = false;
    private boolean showTce = false;

    private static final String TITLE_KEY     = "title";
    private static final String FSE_KEY       = "fse";
    private static final String IGE_KEY       = "ige";
    private static final String MMA_KEY       = "mma";
    private static final String TCE_KEY       = "tce";

    public EtmsBlock(VelocityHelperService velocityHelperService)
    {
        this.velocityHelperService = velocityHelperService;
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context)
            throws MacroExecutionException
    {
        String template = "/com/keysight/test-ecosystem/templates/etms-block.vm";
        Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();
        
        if( parameters.containsKey( TITLE_KEY ) ) {
           velocityContext.put(TITLE_KEY, parameters.get(TITLE_KEY));
        } 

        if( parameters.containsKey( FSE_KEY ) ){ setShowFse( true ); } else { setShowFse( false ); }
        if( parameters.containsKey( IGE_KEY ) ){ setShowIge( true ); } else { setShowIge( false ); }
        if( parameters.containsKey( MMA_KEY ) ){ setShowMma( true ); } else { setShowMma( false ); }
        if( parameters.containsKey( TCE_KEY ) ){ setShowTce( true ); } else { setShowTce( false ); }

        if( getShowFse() ){ velocityContext.put(FSE_KEY, "true"); }
        if( getShowIge() ){ velocityContext.put(IGE_KEY, "true"); }
        if( getShowMma() ){ velocityContext.put(MMA_KEY, "true"); }
        if( getShowTce() ){ velocityContext.put(TCE_KEY, "true"); }
        
        if (body == null) { body = ""; }
        velocityContext.put("body", new HtmlFragment(body));

        return velocityHelperService.getRenderedTemplate(template, velocityContext);
    }

    public void setShowFse( boolean flag ){ this.showFse = flag; }
    public boolean getShowFse(){ return this.showFse; }
    
    public void setShowIge( boolean flag ){ this.showIge = flag; }
    public boolean getShowIge(){ return this.showIge; }
    
    public void setShowMma( boolean flag ){ this.showMma = flag; }
    public boolean getShowMma(){ return this.showMma; }
    
    public void setShowTce( boolean flag ){ this.showTce = flag; }
    public boolean getShowTce(){ return this.showTce; }
    
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
