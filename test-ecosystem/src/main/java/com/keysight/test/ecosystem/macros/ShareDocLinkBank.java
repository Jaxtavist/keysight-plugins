package com.keysight.test.ecosystem.macros;

import java.util.Map;
import java.net.URLDecoder;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.velocity.htmlsafe.HtmlFragment;
import com.atlassian.renderer.RenderContext;

import com.keysight.test.ecosystem.helpers.ShareDocLinkBankItem;

public class ShareDocLinkBank implements Macro
{
   private final VelocityHelperService velocityHelperService;
    
   public static final String BASE_URL_KEY        = "base-url";
   public static final String URL_KEY             = "url";
   public static final String LINK_KEY            = "link";
   public static final String INCLUDE_PDF_KEY     = "include-pdf";
   public static final String INFO_URL_KEY        = "info-url";
   public static final String SHARE_DOC_LINKS_KEY = "shareDocLinks";
   
   //Global Keys
   public static final String SHAREDOC = "http://sharedoc.collaboration.is.keysight.com";
    
   public ShareDocLinkBank( VelocityHelperService velocityHelperService )
   {
      this.velocityHelperService = velocityHelperService;
   }

   @Override
   public String execute(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException
   {
      Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();
      String template = "/com/keysight/test-ecosystem/templates/sharedoc-link-bank.vm";
      ArrayList<ShareDocLinkBankItem> shareDocLinks = new ArrayList<ShareDocLinkBankItem>();

      String lines[] = null;
      String parts[] = null;
        
      StringBuilder url   = null;
      String providedUrl  = null;
      String link         = null;
      String infoUrl      = null;
      
      lines = body.split("\n");
      for( String line: lines ) {
         // initialize values
         providedUrl  = null;
         url          = new StringBuilder();
         infoUrl      = null;
         link         = null;

         if( ! StringUtils.isBlank( line ) ){
            // remove leading or trailing whitespace
            line = line.trim();
            
            // remove leading [ if present
            if( line.startsWith("[") ) {
               line = line.substring(1,line.length());
            }
           
            // remove trailing ] if present
            if( line.endsWith("]") ) {
               line = line.substring(0,line.length()-1);
            }
           
            if( line.contains( "|" ) ) {
               parts = line.split("\\|");
               if( parts[0].length() > 0 ) {
                  providedUrl  = parts[0];
               }
               if( parts[1].length() > 0 ) {
                  link = parts[1];
               }
            } else {
               providedUrl = line;
            }
           
            if( providedUrl != null && providedUrl.toLowerCase().startsWith( "http" ) ) {
               url.append( providedUrl );

               int slashCount = line.length() - line.replace("/", "").length();
               int endIndex = providedUrl.lastIndexOf("/");
               if( slashCount > 2 && endIndex != -1 ) {
                  infoUrl = providedUrl.substring(0, endIndex);
                  if( link == null ) {
                     link = providedUrl.substring(endIndex+1, providedUrl.length() );
                  }
               } else {
                 infoUrl = SHAREDOC;
                 if( link == null ) {
                    link = providedUrl;
                 }
              }

            } else {
               if( parameters.containsKey( BASE_URL_KEY ) ) {
                  if( !parameters.get( BASE_URL_KEY ).startsWith( "http" ) ){
                     url.append( SHAREDOC );
                  
                     if( !parameters.get( BASE_URL_KEY ).startsWith("/") ) {
                        url.append( "/" );
                     } 
                  } 

                  if( parameters.get( BASE_URL_KEY ).endsWith("/") ) {
                     url.append( parameters.get( BASE_URL_KEY ).substring(0,parameters.get( BASE_URL_KEY ).length()-1) );
                  } else {
                     url.append( parameters.get( BASE_URL_KEY ) );
                  }
               }
           
               infoUrl = url.toString();
               if( link == null ) {
                  if( providedUrl != null ){
                     link = providedUrl;
                  } else {
                     link= "#";
                  }
               }
           
               url.append( "/" );
               if( providedUrl != null ){
                  url.append( providedUrl.replace( " ", "%20" ) );
               }
            }
               
            if( parameters.containsKey( INFO_URL_KEY ) ){
               infoUrl = parameters.get( INFO_URL_KEY );
            }

            
            if( parameters.containsKey( LINK_KEY ) ){
               link = parameters.get( LINK_KEY );
            }

            try{
               link = URLDecoder.decode( link, "UTF-8" );
            } catch( Exception e ) { }
            
            shareDocLinks.add( new ShareDocLinkBankItem( url.toString(), link, infoUrl ) );
         }
      }

      if( parameters.containsKey( INCLUDE_PDF_KEY ) ){
         velocityContext.put("pdf", "true" );
      }
      
      velocityContext.put(SHARE_DOC_LINKS_KEY, shareDocLinks );
      
      if( isLinkBank() ){
         velocityContext.put("linkBank", "true" );
      }

      return velocityHelperService.getRenderedTemplate(template, velocityContext);
   }

   public boolean isLinkBank()
   {
      return( true );
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
}
