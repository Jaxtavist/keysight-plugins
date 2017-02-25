package com.keysight.keysight.theme.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.renderer.v2.RenderUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.Authenticator;
import java.net.URLDecoder;
import java.net.PasswordAuthentication;
import org.apache.commons.codec.binary.Base64;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class ApiDocumentation implements Macro
{
   public final static String APPLICATION   = "application";
   public final static String SHOW_COLLAPSED = "show-collapsed";
   public final static String HIDE_APPLICATION_NAME = "hide-application-name";
   public final static String API_SERVER = "api.is.keysight.com";
   //public final static String API_URL = "http://ofweb4.srs.is.keysight.com/cgi-bin/pers/selberg/of_apps/apiManager/apiManager.cgi";
   public final static String API_URL = "https://"+API_SERVER+"/cgi-bin/org/of_apps/apiManager/apiManager.cgi";

   protected final VelocityHelperService velocityHelperService;
   protected final SettingsManager settingsManager;

   public ApiDocumentation( SettingsManager settingsManager,
		                 VelocityHelperService velocityHelperService)
   {
      this.settingsManager = settingsManager;
      this.velocityHelperService = velocityHelperService;
   }

   @Override
   public String execute(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException
   {
      String template = "/com/keysight/keysight-theme/templates/api-documentation.vm";
      Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();
      String baseUrl   = settingsManager.getGlobalSettings().getBaseUrl();
      String inputLine;
      String html;
      StringBuilder text = new StringBuilder();
      Map<String, String[]> applicationVersions = new HashMap<String, String[]>();

      velocityContext.put( "apiServer", API_SERVER );

      if( parameters.containsKey( SHOW_COLLAPSED ) ){
         velocityContext.put( "collapsed", "true" );
      }

      if( parameters.containsKey( APPLICATION ) ){
         try{
            URL url = new URL( API_URL + "?GET_APPLICATION_VERSION_TREE=TRUE&text=" + parameters.get( APPLICATION ) );
            URLConnection browser = url.openConnection();
            BufferedReader reader = new BufferedReader( new InputStreamReader( browser.getInputStream() ) );
            while(( inputLine = reader.readLine()) != null ){
               text.append( inputLine );
            }

            String json = text.toString();
            velocityContext.put( "json", json );

            if( json.matches( "\\s*\\{.*\\}\\s*" ) ){
               Type collectionType = new TypeToken<Map<String,String[]>>(){}.getType();
               Map<String, String[]> applicationTree = new Gson().fromJson( json, collectionType );
               velocityContext.put( "applicationTree", applicationTree );

               if( parameters.containsKey( HIDE_APPLICATION_NAME ) && applicationTree.size() == 1 ){
                  velocityContext.put( "oneApplication", "true" );
               }
         
               if( applicationTree.size() <= 0 ){
                  html = RenderUtils.blockError("Error: No applications found.", "" );
               } else {
                  html = velocityHelperService.getRenderedTemplate(template, velocityContext);
               }
            } else {
               html = RenderUtils.blockError("Error: No applications found.", "" );
            }
         } catch (MalformedURLException e ){
            throw new MacroExecutionException( e );
         } catch (IOException e){
            throw new MacroExecutionException( e );
         }
      }
      else 
      {
         html = RenderUtils.blockError("Error: No applications found.", "" );
      }


      return html;

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
