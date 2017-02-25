package com.keysight.test.ecosystem.macros;

import java.util.Map;
import org.apache.commons.lang.StringUtils;

import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.plugin.services.VelocityHelperService;

public class ShareDocLink extends ShareDocLinkBank
{
   public ShareDocLink( VelocityHelperService velocityHelperService )
   {
      super( velocityHelperService );
   }

   @Override
   public String execute(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException
   {
      StringBuilder newBody = new StringBuilder();

      newBody.append( "[" );
      newBody.append( parameters.get( URL_KEY ) );
      if( parameters.containsKey( LINK_KEY ) ){
         newBody.append( "|" );
	 newBody.append( parameters.get( LINK_KEY ) );
      }
      newBody.append( "]" );

      return super.execute( parameters, newBody.toString(), context );
   }
 
   @Override
   public boolean isLinkBank()
   {
      return( false );
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

}
