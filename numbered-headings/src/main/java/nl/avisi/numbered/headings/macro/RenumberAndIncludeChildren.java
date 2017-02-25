package nl.avisi.numbered.headings.macro;

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
import com.atlassian.confluence.pages.Page;
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

import nl.avisi.numbered.headings.macro.parameters.NumberedHeadingsParameterSupport;
import nl.avisi.numbered.headings.macro.parameters.NumberedHeadingsParameters;
import nl.avisi.numbered.headings.rendering.HeadingRenderer;

public class RenumberAndIncludeChildren implements Macro
{
    protected static final String PAGE_KEY               = "page";
    protected static final String EXCLUDE_PAGE_TITLE_KEY = "exclude-page-title";
    protected static final String LEVELS_TO_INCLUDE_KEY  = "levels-to-include";
    protected static final String ERROR                  = "Error: ";
    protected static final String NOT_FOUND              = "Page Not Found";
    protected static final String ALREADY_INCLUDED       = "The page is already included";
    
    protected final PageManager pageManager;
    protected final PermissionManager permissionManager;
    protected final Renderer renderer;
    protected final HTMLParagraphStripper htmlParagraphStripper;
    protected final XhtmlContent xhtmlUtils;
    protected final NumberedHeadingsParameterSupport numberedHeadingsParameterSupport;
    protected ConfluenceUser m_currentUser;

    public RenumberAndIncludeChildren( NumberedHeadingsParameterSupport numberedHeadingsParameterSupport,
                                       PageManager pageManager,
                                       PermissionManager permissionManager,
                                       Renderer renderer,
                                       XhtmlContent xhtmlUtils
                                     )
    {
        this.numberedHeadingsParameterSupport = numberedHeadingsParameterSupport;
        this.pageManager                      = pageManager;
        this.permissionManager                = permissionManager;
        this.renderer                         = renderer;
        this.xhtmlUtils                       = xhtmlUtils;
        
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
        StringBuilder builder = new StringBuilder();
        boolean excludePageTitle = false;
        int levelsToInclude = 0;
        ContentEntityObject page;
        String delimiter = "/";
        m_currentUser = AuthenticatedUserThreadLocal.get();

	    if( parameters.containsKey( EXCLUDE_PAGE_TITLE_KEY ) ){
           excludePageTitle = true;
        }
	
	    try { 
	       if( parameters.containsKey( LEVELS_TO_INCLUDE_KEY )){
	          levelsToInclude = Integer.parseInt( parameters.get( LEVELS_TO_INCLUDE_KEY ) );
	       }
	    } catch ( Exception e ){}

        if( parameters.containsKey( PAGE_KEY ) ){
           pageTitle = parameters.get( PAGE_KEY );
 
           if( pageTitle.contains( ":" ) ){
              targetPage = pageTitle.split( ":", 2 );
           } else {
              targetPage[0] = context.getSpaceKey();
              targetPage[1] = pageTitle;
           }
           page = pageManager.getPage( targetPage[0], targetPage[1] );
	    } else {
           page = context.getEntity();
        }
           
	    pageContent = getChildrenContent( page, parameters, context, page.getTitle(), excludePageTitle, levelsToInclude, 1,  delimiter, replacementText );
        pageContent = pageContent.replaceAll( "<span class=\"data-nh-numbering\">.*?</span>", "" );

        NumberedHeadingsParameters parsedParameters = numberedHeadingsParameterSupport.parseParameters(parameters);
        pageContent = new HeadingRenderer(parsedParameters).render(pageContent).getResult();
        
        return pageContent;
    }

    protected String getChildrenContent(ContentEntityObject page, 
                                        Map<String, String> parameters,
                                        ConversionContext conversionContext, 
                                        String parentPageTitle,
                                        boolean excludePageTitle,
                                        int levelsToInclude,
                                        int currentLevel,
                                        String delimiter,
                                        String replacementText)
    {
       int hOffset = 0;
       StringBuilder builder = new StringBuilder();
       Page currentPage = (Page) page;
       for( Page childPage : currentPage.getSortedChildren() ){
          if( permissionManager.hasPermission(m_currentUser, Permission.VIEW, childPage) ){
             if( !excludePageTitle ){
                String id = parentPageTitle + "-" + childPage.getTitle();
                id.replaceAll( " ", "" );
                builder.append( "<h" + (currentLevel + hOffset) + " id=\"" 
                                + id
                                + "\">" + childPage.getTitle() + "</h" + (currentLevel+hOffset) + ">\n" );
             }
	         builder.append( getIncludedContent( childPage, parameters, conversionContext, delimiter, replacementText ) );
             
             if( excludePageTitle ){
                builder.append( "<br />\n" );
             }
             if( levelsToInclude == 0 || currentLevel < levelsToInclude ){
	            builder.append( getChildrenContent( childPage, parameters, conversionContext, parentPageTitle, excludePageTitle, levelsToInclude, currentLevel+1,  delimiter, replacementText ) );
             }
          }
       }
       return( builder.toString() );
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
            String strippedBody = page.getBodyAsString();
            try {
                strippedBody = htmlParagraphStripper.stripFirstParagraph(page.getBodyAsString());
            } catch (XMLStreamException e) { }

            DefaultConversionContext context = new DefaultConversionContext(new PageContext(page, conversionContext.getPageContext()));
            return renderer.render(strippedBody, context);
        } finally {
            ContentIncludeStack.pop();
        }
    }
}
