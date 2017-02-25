package com.keysight.include.content.macros;

import java.util.Map;
import java.util.Random;

import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.velocity.htmlsafe.HtmlFragment;
import com.atlassian.renderer.RenderContext;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.confluence.macro.DefaultImagePlaceholder;
import com.atlassian.confluence.macro.ImagePlaceholder;
import com.atlassian.confluence.content.render.image.ImageDimensions;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;

public class HelpText implements Macro
{
    protected final VelocityHelperService velocityHelperService;
    protected final XhtmlContent xhtmlUtils;

    private static final String TEXT_KEY  = "text";
    private static final String TITLE_KEY = "title";
    private static final String TIP_KEY   = "tip";
    private static final String TYPE_KEY  = "type";
    private static final String HELP_ICON = "<span class=\"aui-icon aui-icon-small aui-iconfont-help\">Help</span>";
   

    public HelpText( VelocityHelperService velocityHelperService,
                     XhtmlContent xhtmlUtils
                   ){
        this.velocityHelperService = velocityHelperService;
        this.xhtmlUtils = xhtmlUtils;
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context)
            throws MacroExecutionException
    {
        Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();
        String dialog2Template = "/com/keysight/include-content/templates/help-text.vm";
        String inlineTemplate  = "/com/keysight/include-content/templates/help-text-inline-dialog.vm";
        String template = dialog2Template;

        if( parameters.containsKey( TEXT_KEY ) ){
           velocityContext.put( TEXT_KEY + "AsHtml", parameters.get( TEXT_KEY ).replaceAll( "\\(\\?\\)", HELP_ICON ) );
	    }

        if( parameters.containsKey( TITLE_KEY ) ){
           velocityContext.put( TITLE_KEY, parameters.get( TITLE_KEY ) );
        } else {
           velocityContext.put( TITLE_KEY, "Help Text" );
        }

        if( parameters.containsKey( TIP_KEY ) ){
           velocityContext.put( TIP_KEY, parameters.get( TIP_KEY ) );
        }

        velocityContext.put( "bodyAsHtml", body );
        velocityContext.put( "sectionId", generateId() );

        if( parameters.containsKey( TYPE_KEY ) && parameters.get( TYPE_KEY ).matches( "Popup" ) ){
           template = inlineTemplate;
        }
     
        return velocityHelperService.getRenderedTemplate(template, velocityContext);
    }

    protected String generateId() {
      Random randomNumberGenerator = new Random();
      int size = 10000;
      return( "keysight-help-block-id-" 
              + randomNumberGenerator.nextInt( size ) + "-" 
              + randomNumberGenerator.nextInt( size ) );
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
