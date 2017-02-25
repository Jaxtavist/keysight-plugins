package com.keysight.include.content.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultXmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactoryFactoryBean;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.service.NotAuthorizedException;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.ContentIncludeStack;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.confluence.xml.HTMLParagraphStripper;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.Permission;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import com.keysight.include.content.helpers.IncludeContentHelper;

public class IncludePageWithReplacement implements Macro
{
    protected ConfluenceUser m_currentUser;
    protected boolean allowAnybodyToViewContent = false;

    protected static final String PAGE_KEY         = "page";
    protected static final String DELIMITER_KEY    = "delimiter";
    protected static final String ERROR            = "Error: ";
    protected static final String NOT_FOUND        = "Page Not Found";
    protected static final String ALREADY_INCLUDED = "The page is already included";
    protected static final String ALLOW_ANONYMOUS_ACCESS = "allowAnonymousAccess";
    
    protected final PageManager pageManager;
    protected final PermissionManager permissionManager;
    protected final Renderer renderer;
    protected final HTMLParagraphStripper htmlParagraphStripper;
    protected final XhtmlContent xhtmlUtils;

    public IncludePageWithReplacement( PageManager pageManager,
                                       PermissionManager permissionManager,
                                       Renderer renderer,
                                       XhtmlContent xhtmlUtils
                                     )
    {
        this.pageManager  = pageManager;
        this.permissionManager  = permissionManager;
        this.renderer     = renderer;
        this.xhtmlUtils   = xhtmlUtils;
        
        // This is taked from the Atlassian Page Include Macro
        final XMLOutputFactory xmlOutputFactory;
        try {
            xmlOutputFactory = (XMLOutputFactory) new XmlOutputFactoryFactoryBean(true).getObject(); } catch (Exception e) {
            throw new RuntimeException("Error occurred trying to construct a XML output factory", e); // this shouldn't happen
        }
        htmlParagraphStripper = new HTMLParagraphStripper( xmlOutputFactory, new DefaultXmlEventReaderFactory());
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context)
            throws MacroExecutionException
    {
        String[] targetPage = new String[2];
        String pageTitle;
        String pageContent;
        String replacementText = body;
        m_currentUser = AuthenticatedUserThreadLocal.get();

        if( parameters.containsKey( PAGE_KEY ) ){
           pageTitle = parameters.get( PAGE_KEY );
 
           if( pageTitle.contains( ":" ) ){
              targetPage = pageTitle.split( ":", 2 );
           } else {
              targetPage[0] = context.getSpaceKey();
              targetPage[1] = pageTitle;
           }

           String delimiter = "/";
           if( parameters.containsKey( DELIMITER_KEY ) ){
              delimiter = parameters.get( DELIMITER_KEY );
           }

           ContentEntityObject page = pageManager.getPage( targetPage[0], targetPage[1] );
           pageContent = getIncludedContent( page, parameters, context, delimiter, replacementText );
        } else {
           return RenderUtils.blockError(ERROR, NOT_FOUND);
        }

        return pageContent;
    }

    @Override
    public BodyType getBodyType()
    {
        return BodyType.PLAIN_TEXT;
    }

    @Override
    public OutputType getOutputType()
    {
        return OutputType.BLOCK;
    }

    protected void setAllowAnybodyToViewContent( boolean bFlag )
    {
        this.allowAnybodyToViewContent = bFlag;
    }
    protected boolean getAllowAnybodyToViewContent()
    {
        return this.allowAnybodyToViewContent;
    }

    // Leveraged from the confluence advanced macros, page include macto
    protected String getIncludedContent(ContentEntityObject page, 
                                        Map<String, String> parameters,
                                        ConversionContext conversionContext, 
                                        String delimiter,
                                        String replacementText)
    {
        try {
            if (page == null) {
                return RenderUtils.blockError(ERROR, NOT_FOUND);
            }
            return fetchPageContent(page, parameters, conversionContext, delimiter, replacementText);
        } catch (NotAuthorizedException e) {
            // Don't let the user know they weren't allowed to see the page.
            return RenderUtils.blockError(ERROR, NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return RenderUtils.blockError(ERROR, e.getMessage());
        }
    }

    protected String fetchPageContent(ContentEntityObject page, 
                                      Map<String, String> parameters,
                                      ConversionContext conversionContext, 
                                      String delimiter,
                                      String replacementText)
    {
        if (ContentIncludeStack.contains(page))
            return RenderUtils.blockError( ERROR, ALREADY_INCLUDED );


        ContentIncludeStack.push(page);
        try {
            if( getAllowAnybodyToViewContent() || permissionManager.hasPermission(m_currentUser, Permission.VIEW, page) ){
               String strippedBody = page.getBodyAsString();
               try {
                   strippedBody = htmlParagraphStripper.stripFirstParagraph(page.getBodyAsString());
               } catch (XMLStreamException e) { }

               strippedBody = IncludeContentHelper.replaceText( strippedBody, delimiter, replacementText );

               DefaultConversionContext context = new DefaultConversionContext(new PageContext(page, conversionContext.getPageContext()));
               return renderer.render(strippedBody, context);
            } else {
               return RenderUtils.blockError(ERROR + " You do not have permissions to view this content.", "" );
            }
        } finally {
            ContentIncludeStack.pop();
        }
    }
}
