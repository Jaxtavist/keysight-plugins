package com.keysight.html.elements.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;

import java.util.Map;

public class AuiTab implements Macro
{
   public AuiTab()
   {
   }

   @Override
   public String execute(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException
   {
      return "";
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
