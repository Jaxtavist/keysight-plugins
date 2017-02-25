package com.keysight.confluence.support.macros;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import java.util.Map;
import java.util.ArrayList;
import org.apache.commons.lang.StringUtils;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.velocity.htmlsafe.HtmlFragment;
import com.atlassian.renderer.RenderContext;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;

public class EntityAuthorship implements Macro
{
    protected static final String PAGE_KEY     = "page";
    protected static final String USERNAME_KEY = "username";
    protected static final String DATE_KEY     = "creation-date";

    private final PageManager pageManager;
    private final SpaceManager spaceManager;
    private final VelocityHelperService velocityHelperService;
    private final UserAccessor userAccessor;

    public EntityAuthorship(PageManager pageManager,
                            SpaceManager spaceManager, 
		            UserAccessor userAccessor,
                            VelocityHelperService velocityHelperService)
    {
        this.pageManager = pageManager;
        this.spaceManager = spaceManager;
	this.userAccessor = userAccessor;
        this.velocityHelperService = velocityHelperService;
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context)
            throws MacroExecutionException
    {
        String template = "/com/keysight/confluence-support/templates/entity-authorship.vm";
        Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();
	Page currentPage;
        String pageTitle;
	String spaceKey = context.getSpaceKey();
	
        if( parameters.containsKey( PAGE_KEY ) ){
           pageTitle = parameters.get( PAGE_KEY );
	   if( pageTitle.matches( ".*:.*" ) ){
              String[] parts = pageTitle.split( ":", 2 );
	      spaceKey  = parts[0];
	      pageTitle = parts[1];
	   } 
	   currentPage = pageManager.getPage( spaceKey, pageTitle );
	} else {
           currentPage = (Page) context.getEntity();
	}
        
	if( parameters.containsKey( "autoclean" ) ){
	       
           if( parameters.containsKey( USERNAME_KEY ) ){
              ConfluenceUser user = userAccessor.getUserByName( parameters.get( USERNAME_KEY ) );
	      if( user != null ){
                 currentPage.setCreator( user );
	      }
	   }

           if( parameters.containsKey( DATE_KEY ) ){
              try{
		 SimpleDateFormat dateFormat = new SimpleDateFormat( "M/dd/yyyy HH:mm:ss" );
                 Date date = dateFormat.parse( parameters.get( DATE_KEY ) );
                 currentPage.setCreationDate( date );
	      } catch( ParseException e )
	      {}
	   }

        }

        velocityContext.put( "page", currentPage  );

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
