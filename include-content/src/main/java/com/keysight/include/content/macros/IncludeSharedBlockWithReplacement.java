package com.keysight.include.content.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.MacroDefinitionHandler;
import com.atlassian.confluence.xhtml.api.MacroDefinitionMarshallingStrategy;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.Permission;

import com.keysight.include.content.helpers.SharedBlockIncludeStack;
import com.keysight.include.content.helpers.IncludeContentHelper;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class IncludeSharedBlockWithReplacement extends IncludePageWithReplacement
{
    protected static final String SHARED_BLOCK_KEY = "shared-block-key";
    
    public IncludeSharedBlockWithReplacement( PageManager pageManager,
                                              PermissionManager permissionManager,
                                              Renderer renderer,
                                              XhtmlContent xhtmlUtils
                                            ){
        super( pageManager, permissionManager, renderer, xhtmlUtils );
    }

    @Override
    protected String fetchPageContent(ContentEntityObject page, 
                                      Map<String, String> parameters,
                                      ConversionContext conversionContext, 
                                      String delimiter,
                                      String replacementText)
    {
        String blockId = page.getTitle();
        if( parameters.containsKey( SHARED_BLOCK_KEY ) ){
           blockId = blockId + ":" + parameters.get( SHARED_BLOCK_KEY );
        }

        if (SharedBlockIncludeStack.contains(blockId))
            return RenderUtils.blockError( ERROR, ALREADY_INCLUDED );

        SharedBlockIncludeStack.push(blockId);
        try {
            String content = getSharedBlockHtml( parameters, page, conversionContext );
            content = IncludeContentHelper.replaceText( content, delimiter, replacementText );
            DefaultConversionContext context = new DefaultConversionContext(new PageContext(page, conversionContext.getPageContext()));
            return renderer.render(content, context);
        } catch (Exception e){
           System.out.println( e.toString() );
        } finally {
            SharedBlockIncludeStack.pop();
        }
        return RenderUtils.blockError( ERROR, "Something went wrong" );
    }

    private String getSharedBlockHtml( Map<String,String> parameters, ContentEntityObject page, ConversionContext context ) throws MacroExecutionException 
    {
       final List<MacroDefinition> macros = new ArrayList<MacroDefinition>();
       String html = "";
       try{
          xhtmlUtils.handleMacroDefinitions(page.getBodyContent().getBody(), context, new MacroDefinitionHandler(){
             @Override
             public void handle(MacroDefinition macroDefinition){
                macros.add(macroDefinition);
             }
           },
           MacroDefinitionMarshallingStrategy.MARSHALL_MACRO);
       } catch (XhtmlException e) {
          throw new MacroExecutionException(e);
       }

       if (!macros.isEmpty()) {
          for( MacroDefinition macroDefinition : macros ){
             if( parameters.containsKey( SHARED_BLOCK_KEY ) ){
                if( macroDefinition.getName().equals( "shared-block" ) 
                    && parameters.get( SHARED_BLOCK_KEY).equals( macroDefinition.getParameter( SHARED_BLOCK_KEY ) ) ){
                       if( macroDefinition.getParameter( ALLOW_ANONYMOUS_ACCESS ) != null || permissionManager.hasPermission(m_currentUser, Permission.VIEW, page) ){
                           html = macroDefinition.getBodyText();
                       } else {
                          if( parameters.containsKey( SUPPRESS_ERRORS ) ){
                              html = "";
                          } else {
                              html = RenderUtils.blockError(ERROR + " You do not have permissions to view this content.", "" );
                          }
                       }
                   break;
                }
             } else {
                if( macroDefinition.getName().equals( "shared-block" ) ){
                   if( macroDefinition.getParameter( ALLOW_ANONYMOUS_ACCESS ) != null || permissionManager.hasPermission(m_currentUser, Permission.VIEW, page) ){
                       html = macroDefinition.getBodyText();
                   } else {
                       if( parameters.containsKey( SUPPRESS_ERRORS ) ){
                           html = "";
                       } else {
                           html = RenderUtils.blockError(ERROR + " You do not have permissions to view this content.", "" );
                       }
                   }
                   break;
                }
             }
          }
       }

       return html;
    } 
}
