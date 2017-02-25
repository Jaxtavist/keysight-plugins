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


public class HelpTextFromSharedBlock extends HelpTextFromPage implements Macro, EditorImagePlaceholder
{
    private static final String SHARED_BLOCK_KEY = "shared-block-key";

    public HelpTextFromSharedBlock( PageManager pageManager,
		                            VelocityHelperService velocityHelperService,
                                    XhtmlContent xhtmlUtils
                                  ){
        super( pageManager, velocityHelperService, xhtmlUtils );
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context)
            throws MacroExecutionException
    {
        Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();
        String template = "/com/keysight/include-content/templates/help-text-from-shared-block.vm";
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

        if( parameters.containsKey( SHARED_BLOCK_KEY ) ){
           velocityContext.put( "sharedBlockKey", parameters.get( SHARED_BLOCK_KEY ) );
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
           velocityContext.put( METHOD_KEY, "showSharedBlockAsInlineDialog" );
        } else {
           velocityContext.put( METHOD_KEY, "showSharedBlockAsDialogBox" );
	    }

        velocityContext.put( "sectionId", generateId() );
        return velocityHelperService.getRenderedTemplate(template, velocityContext);
    }
}
