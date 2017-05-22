package com.keysight.html.elements.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;

import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.MacroDefinitionHandler;
import com.atlassian.confluence.xhtml.api.MacroDefinitionMarshallingStrategy;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.confluence.content.render.xhtml.macro.annotation.Format;
import com.atlassian.confluence.content.render.xhtml.macro.annotation.RequiresFormat;
import com.atlassian.renderer.v2.RenderUtils;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import javax.xml.stream.XMLStreamException;

import com.keysight.html.elements.helpers.TabInfo;

public class AuiTabGroup implements Macro
{
   public final static String TITLE_KEY       = "title";
   public final static String VERTICAL_KEY    = "vertical";
   public final static String ORIENTATION_KEY = "orientation";
   public final static String ACTIVE_KEY      = "active";
   public final static String ID_KEY          = "id";
   public final static String LEFT_TITLE_TRUNCATION_COUNT_KEY = "ltrunc";
   public final static String RIGHT_TITLE_TRUNCATION_COUNT_KEY = "rtrunc";

   protected final VelocityHelperService velocityHelperService;
   protected final XhtmlContent xhtmlUtils;
   
   public AuiTabGroup( VelocityHelperService velocityHelperService,
                       XhtmlContent xhtmlUtils)
   {
      this.velocityHelperService = velocityHelperService;
      this.xhtmlUtils = xhtmlUtils;
   }

   @Override
   @RequiresFormat( Format.Storage )  // note, nested macros under an @RequiresFormat( Format.Storage ) will not be found by 
                                      // the xhtmlUtils.handleMacroDefinitions routine.  
   public String execute(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException
   {
      List<TabInfo> tabs = null;
      String template = "/com/keysight/html-elements/templates/aui-tab-group.vm";
      Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();

      if( parameters.containsKey( VERTICAL_KEY ) ){
         velocityContext.put( ORIENTATION_KEY, "vertical-tabs"  );
      } else {
         velocityContext.put( ORIENTATION_KEY, "horizontal-tabs"  );
      }

      try{
         tabs = getTabs( parameters, body, context );
      } catch( Exception e ){
         throw new MacroExecutionException(e);
      }
      velocityContext.put( "tabs", tabs );

      if( context.getOutputType().matches( "(word|pdf)" ) ){
         velocityContext.put( "pdfOrWordOutput", "true" );
      }

      if( ShowPageInfo() ){
          velocityContext.put( "show-tab-info", "true" );
      }

      if( tabs.size() > 0 ){
         return velocityHelperService.getRenderedTemplate(template, velocityContext);
      } else {
         return RenderUtils.blockError( "No pages found.", "" );
      }
   }

   protected boolean ShowPageInfo()
   {
      return false;
   }

   protected ArrayList<TabInfo> getTabs(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException
   {
      final List<MacroDefinition> macros = new ArrayList<MacroDefinition>();
      ArrayList<TabInfo>          tabs   = new ArrayList<TabInfo>();
      TabInfo tabInfo = null;
      boolean activeTabSet = false;

      try{
         xhtmlUtils.handleMacroDefinitions( body, context, new MacroDefinitionHandler(){
            @Override
            public void handle(MacroDefinition macroDefinition){
               macros.add( macroDefinition );
            }
         },
         MacroDefinitionMarshallingStrategy.MARSHALL_MACRO);
      } catch( XhtmlException e){
         throw new MacroExecutionException(e);
      }

      if(!macros.isEmpty()){
         for( MacroDefinition macroDefinition : macros ){
            if( macroDefinition.getName().equals( "aui-tab" ) || macroDefinition.getName().equals( "localtab" ) ){
               try{
                  tabInfo = new TabInfo( macroDefinition.getParameter( TITLE_KEY ), 
                                         xhtmlUtils.convertStorageToView( macroDefinition.getBodyText(), context ));
                  if( macroDefinition.getParameter( ACTIVE_KEY ) != null && !activeTabSet ){
                     tabInfo.setActive( true );
                     activeTabSet = true;
                  }
                  if( macroDefinition.getParameter( ID_KEY ) != null ){
                     tabInfo.setPaneId( macroDefinition.getParameter( ID_KEY ) );
                  }
                  if( macroDefinition.getParameter( LEFT_TITLE_TRUNCATION_COUNT_KEY ) != null ){
                     tabInfo.setLeftTitleTruncationCount( macroDefinition.getParameter( LEFT_TITLE_TRUNCATION_COUNT_KEY ) );
                  }
                  if( macroDefinition.getParameter( RIGHT_TITLE_TRUNCATION_COUNT_KEY ) != null ){
                     tabInfo.setRightTitleTruncationCount( macroDefinition.getParameter( RIGHT_TITLE_TRUNCATION_COUNT_KEY ) );
                  }
                  tabs.add( tabInfo );
               } catch( Exception e ){
                  throw new MacroExecutionException(e);
               }
            }
         }
         if( !activeTabSet && tabs.size() > 0 ){
            tabs.get(0).setActive( true );
         }
      }

      return tabs;
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
