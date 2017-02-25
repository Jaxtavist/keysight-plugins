package com.keysight.include.content.macros;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.PartialList;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.like.Like;
import com.atlassian.confluence.like.LikeManager;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.MacroDefinitionHandler;
import com.atlassian.confluence.xhtml.api.MacroDefinitionMarshallingStrategy;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.sal.api.user.UserManager;

import com.keysight.include.content.helpers.FaqItem;
import com.keysight.include.content.helpers.FaqItemListBuilder;

public class Faq implements Macro
{
    private final LabelManager labelManager;
    private final LikeManager likeManager;
    private final PageManager pageManager;
    private final PermissionManager permissionManager;
    private final Renderer renderer;
    private final SettingsManager settingsManager;
    private final SpaceManager spaceManager;
    private final UserManager userManager;
    private final VelocityHelperService velocityHelperService;
    private final XhtmlContent xhtmlUtils;

    private static final String ID                               = "id";
    private static final String EXPAND                           = "Expand";
    private static final String SHARED_BLOCK_OPTION              = "First Shared Block";
    private static final String EXCERPT_OPTION                   = "First Excerpt";
    private static final String LABELS                           = "labels";
    private static final String SPACEKEYS                        = "spaceKeys";
    private static final String PAGE                             = "page";
    private static final String PAGE_ID                          = "pageId";
    private static final String RENDER_STYLE                     = "renderStyle";
    private static final String RENDER_CONTENT                   = "renderContent";
    private static final String POPUP_TITLE                      = "popupTitle";
    private static final String AND_LABELS                       = "andLabels";
    private static final String INCLUDE_DECENDANTS               = "includeDecendants";
    private static final String INCLUDE_SPACE                    = "includeSpace";
    private static final String INCLUDE_LAST_MODIFICATION_DATE   = "includeLastModificationDate";
    private static final String INCLUDE_LAST_MODIFICATION_AUTHOR = "includeLastModificationAuthor";
    private static final String INCLUDE_LABELS                   = "includeLabels";
    private static final String INCLUDE_LIKES                    = "includeLikes";

    public Faq( LabelManager labelManager,
                LikeManager likeManager,
                PageManager pageManager,
                PermissionManager permissionManager,
                Renderer renderer,
                SettingsManager settingsManager,
                SpaceManager spaceManager,
                UserManager userManager,
                VelocityHelperService velocityHelperService,
                XhtmlContent xhtmlUtils
    ){
        this.labelManager          = labelManager;
        this.likeManager           = likeManager;
        this.pageManager           = pageManager;
        this.permissionManager     = permissionManager;
        this.renderer              = renderer;
        this.settingsManager       = settingsManager;
        this.spaceManager          = spaceManager;
        this.userManager           = userManager;
        this.velocityHelperService = velocityHelperService;
        this.xhtmlUtils            = xhtmlUtils;
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context)
            throws MacroExecutionException
    { 
        String baseUrl = settingsManager.getGlobalSettings().getBaseUrl();
        Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();
        String expandTemplate  = "/com/keysight/include-content/templates/faq-expand.vm";
        String listTemplate    = "/com/keysight/include-content/templates/faq-list.vm";
        StringBuilder html = new StringBuilder();
        String id = generateId();
        String result;
        String template;
        boolean[] includeFlags = new boolean[]{false, false, false, false, false};
        List<FaqItem> pages;

        if( parameters.containsKey(RENDER_STYLE) && !parameters.get(RENDER_STYLE).equals(EXPAND) ){
            template = listTemplate;
        } else {
            template = expandTemplate;
        }

        if( isPrintedOutput( context ) ){
            Map<String, String[]> parameterMap = convertParametersToParameterMap( parameters );
            if( !(parameters.containsKey( LABELS ) || parameters.containsKey( PAGE ) || parameters.containsKey( SPACEKEYS )))
            {
                ContentEntityObject entity = context.getEntity();
                parameterMap.put( PAGE, new String[]{entity.getTitle()} );
                parameterMap.put( PAGE_ID, new String[]{entity.getIdAsString()} );
            }

            if( parameters.containsKey( INCLUDE_SPACE ) ){
                System.out.println( "\n\n\ninclude space!\n\n\n" );
                includeFlags[0] = true;
                velocityContext.put( "includeSpace", "true" );
            }
            if( parameters.containsKey( INCLUDE_LAST_MODIFICATION_DATE ) ){
                includeFlags[1] = true;
                velocityContext.put( "includeLastModificationDate", "true" );
            }
            if( parameters.containsKey( INCLUDE_LAST_MODIFICATION_AUTHOR ) ){
                includeFlags[2] = true;
                velocityContext.put( "includeLastModificationAuthor", "true" );
                velocityContext.put( "includeLastModificationAuthorName", "true" );
                velocityContext.put( "includeLastModificationAuthorUserName", "true" );
            }
            if( parameters.containsKey( INCLUDE_LABELS ) ){
                includeFlags[3] = true;
                velocityContext.put( "includeLabels", "true" );
            }
            if( parameters.containsKey( INCLUDE_LIKES ) ){
                includeFlags[4] = true;
                velocityContext.put( "includeLikes", "true" );
            }

            FaqItemListBuilder faqItemListBuilder = new FaqItemListBuilder( labelManager,
                                                                            likeManager,
                                                                            pageManager,
                                                                            permissionManager,
                                                                            renderer,
                                                                            settingsManager,
                                                                            spaceManager,
                                                                            userManager,
                                                                            velocityHelperService,
                                                                            xhtmlUtils );
            pages = faqItemListBuilder.getFaqItems( parameterMap );

            for( FaqItem page : pages ){
                page.setBaseUrl( baseUrl );
                page.setIncludeFlags( includeFlags );
            }

            if( parameters.containsKey(RENDER_CONTENT) ){
                if( parameters.get(RENDER_CONTENT).equals( SHARED_BLOCK_OPTION ) ){
                    velocityContext.put( "sharedBlock", "true" );
                } else if( parameters.get(RENDER_CONTENT).equals( EXCERPT_OPTION ) ){
                    velocityContext.put( "excerpt", "true" );
                }
            }

            velocityContext.put( "uniqueId", id );
            velocityContext.put( "baseUrl", baseUrl );
            velocityContext.put( "pages", pages );
            result = velocityHelperService.getRenderedTemplate( template, velocityContext);
        } else {
            html.append( "<div class=\"keysight-faq\" id=\"" + id + "\" " );
    
            String[] params = new String[]{ LABELS, 
                                            SPACEKEYS, 
                                            PAGE, 
                                            RENDER_STYLE, 
                                            RENDER_CONTENT, 
                                            POPUP_TITLE, 
                                            AND_LABELS,
                                            INCLUDE_DECENDANTS,
                                            INCLUDE_SPACE,
                                            INCLUDE_LAST_MODIFICATION_DATE, 
                                            INCLUDE_LAST_MODIFICATION_AUTHOR,
                                            INCLUDE_LABELS, 
                                            INCLUDE_LIKES };
       
            for (String item : params){
                if (parameters.containsKey(item)){
                    if (item.matches("include.*")){
                        html.append( item + "=" + "\"true\" " );
                    } else {
                        html.append( item + "=" + "\""+parameters.get(item)+"\" " );
                    }
                    if( item.equals( PAGE ) ){
                        String pageName = parameters.get(item);
                        String spaceKey = context.getSpaceKey();
                        String pageTitle = pageName;
    
                        if( pageName.matches( ".*:.*" ) ){
                            String[] pageParts = pageName.split( ":", 2 );
                            spaceKey = pageParts[0];
                            pageTitle = pageParts[1];
                        }
                        Page ancestorPage = pageManager.getPage( spaceKey, pageTitle );
                        html.append( PAGE_ID + "=" + "\""+ancestorPage.getIdAsString()+"\" " );
                    }
    
                } else if( item.equals( RENDER_STYLE ) ) {
                    html.append( RENDER_STYLE + "=\"Expand\" " );
                } else if( item.equals( RENDER_CONTENT ) ) {
                    html.append( RENDER_CONTENT + "=\"Page\" " );
                }
            }
    
            if( !(parameters.containsKey( LABELS ) || parameters.containsKey( PAGE ) || parameters.containsKey( SPACEKEYS )))
            {
                ContentEntityObject entity = context.getEntity();
                html.append( PAGE + "=\""+entity.getTitle()+"\" " );
                html.append( PAGE_ID + "=\""+entity.getIdAsString()+"\" " );
            }

            html.append( ">\n" );
            html.append( "   <div class=\"keysight-faq-loading\"><button class=\"aui-button keysight-faq-cancel\" cancels-faq-id=\""+id+"\">Cancel</button> Loading...</div>\n" );
            html.append( "</div>\n" );

            result = html.toString();
        }
        return result;
    }

    private Map<String, String[]> convertParametersToParameterMap( Map<String, String>parameters ){
        Map<String, String[]> parameterMap = new HashMap<String, String[]>();

        for( String key : parameters.keySet() ){
            if( parameters.get( key ) != null ){
                parameterMap.put( key, new String[]{ parameters.get(key) } );
            } else {
                parameterMap.put( key, null );
            }

        }

        return parameterMap;
    }

    protected String generateId() {
        Random randomNumberGenerator = new Random();
        int size = 10000;
        return( String.format("keysight-faq-id-%d-%d",  
                               randomNumberGenerator.nextInt( size ),
                               randomNumberGenerator.nextInt( size )));
    }

    protected boolean isPrintedOutput( ConversionContext context )
    {
        boolean bIsPrintedOutput = false;
	    if( context.getOutputType().matches( "(word|pdf)" ) ){
            bIsPrintedOutput = true;
	    }
	    return bIsPrintedOutput;
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
