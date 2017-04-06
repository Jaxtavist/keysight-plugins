package com.keysight.include.content.macros;

import java.util.Map;
import java.util.Random;

import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.velocity.htmlsafe.HtmlFragment;
import com.atlassian.renderer.RenderContext;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.confluence.macro.DefaultImagePlaceholder;
import com.atlassian.confluence.macro.EditorImagePlaceholder;
import com.atlassian.confluence.macro.ImagePlaceholder;
import com.atlassian.confluence.content.render.image.ImageDimensions;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.Page;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;

import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.MacroException;


public class HelpTextFromPage implements Macro, EditorImagePlaceholder
{
    protected final PageManager pageManager;
    protected final VelocityHelperService velocityHelperService;
    protected final XhtmlContent xhtmlUtils;

    protected static final String PAGE_KEY            = "page";
    protected static final String TEXT_KEY            = "text";
    protected static final String TITLE_KEY           = "title";
    protected static final String TIP_KEY             = "tip";
    protected static final String TYPE_KEY            = "type";
    protected static final String METHOD_KEY          = "method";
    protected static final String HELP_ICON           = "<span class=\"aui-icon aui-icon-small aui-iconfont-help\">Help</span>";
    protected static final String ICON_PATH = "/download/resources/com.keysight.include-content:include-content-resources/images/Help-Icon-24x24.png";

    public HelpTextFromPage( PageManager pageManager,
		             VelocityHelperService velocityHelperService,
                             XhtmlContent xhtmlUtils
                   ){
        this.pageManager = pageManager;
        this.velocityHelperService = velocityHelperService;
        this.xhtmlUtils = xhtmlUtils;
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context)
            throws MacroExecutionException
    {
        Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();
        String template = "/com/keysight/include-content/templates/help-text-from-page.vm";
	    String spaceKey;
	    String pageTitle;

        if( parameters.containsKey( PAGE_KEY ) ){
           if( parameters.get( PAGE_KEY ).matches(".*:.*") ){
              String[] pageParts = parameters.get(PAGE_KEY).split(":", 2);
	          spaceKey = pageParts[0];
	          pageTitle = pageParts[1];
	       } else {
	          spaceKey = context.getSpaceKey();
	          pageTitle = parameters.get(PAGE_KEY);
	       }

	       Page page = pageManager.getPage( spaceKey, pageTitle );

           velocityContext.put( PAGE_KEY, page.getIdAsString() );
	    }
    
        
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

        if( parameters.containsKey( TYPE_KEY ) && parameters.get( TYPE_KEY ).matches( "Popup" ) ){
           velocityContext.put( METHOD_KEY, "showPageAsInlineDialog" );
        } else {
           velocityContext.put( METHOD_KEY, "showPageAsDialogBox" );
	}

        velocityContext.put( "sectionId", generateId() );
        return velocityHelperService.getRenderedTemplate(template, velocityContext);
    }

    protected String generateId() {
      Random randomNumberGenerator = new Random();
      int size = 10000;
      return( "keysight-help-block-id-" 
              + randomNumberGenerator.nextInt( size ) + "-" 
              + randomNumberGenerator.nextInt( size ) );
    }

    public ImagePlaceholder getImagePlaceholder(Map<String, String> parameters, ConversionContext context)
    {
        return new DefaultImagePlaceholder( ICON_PATH, false, new ImageDimensions(24,24));
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

    public boolean hasBody()
    {
            return false;
    }

    public RenderMode getBodyRenderMode()
    { 
            return RenderMode.INLINE;
    }

    public String execute( Map params, String body, RenderContext renderContext ) throws MacroException
    {
        try
        {
                return execute(params, body, (ConversionContext) null );
        }
        catch(MacroExecutionException e)
        {
                throw new MacroException( e );
        }
    }
}
