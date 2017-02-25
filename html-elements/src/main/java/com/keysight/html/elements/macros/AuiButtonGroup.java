package com.keysight.html.elements.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;

import java.util.Map;

public class AuiButtonGroup implements Macro
{
   public final static String RENDERED_BODY_KEY = "renderedBodyHtml";
   
   protected final VelocityHelperService velocityHelperService;

   public AuiButtonGroup( VelocityHelperService velocityHelperService)
   {
      this.velocityHelperService = velocityHelperService;
   }

   @Override
   public String execute(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException
   {
      String template = "/com/keysight/html-elements/templates/aui-button-group.vm";
      Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();
      velocityContext.put( RENDERED_BODY_KEY, body );
      return velocityHelperService.getRenderedTemplate(template, velocityContext);
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
