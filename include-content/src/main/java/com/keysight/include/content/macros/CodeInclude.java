package com.keysight.include.content.macros; 

import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashSet;
import java.util.Collections;
import java.lang.StringBuilder;

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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.split;
import static org.apache.commons.lang.StringUtils.trim;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.velocity.htmlsafe.HtmlFragment;
import com.atlassian.renderer.RenderContext;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.MacroDefinitionHandler;
import com.atlassian.confluence.xhtml.api.XhtmlContent;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;

import com.keysight.include.content.helpers.Credentials;

public class CodeInclude implements Macro
{
    private static final String URL_KEY          = "url";
    private static final String USERNAME_KEY     = "username";
    private static final String PASSWORD_KEY     = "password";
    private static final String TITLE_KEY        = "title";
    private static final String FIRST_LINE_KEY   = "firstline";
    private static final String LINE_NUMBERS_KEY = "linenumbers";
    private static final String COLLAPSE_KEY     = "collapse";
    private static final String THEME_KEY        = "theme";
    private static final String LANGUAGE_KEY     = "language";
    private static final String START_REGEX_KEY  = "start-regex";
    private static final String END_REGEX_KEY    = "end-regex";
    private static final String MULTIPLE_REGIONS_KEY = "multiple-regions";
    private static final String INITIAL_LINE_KEY = "initial-line";
    private static final String JOIN_LINE_KEY    = "join-line";
    private static final String FINAL_LINE_KEY   = "final-line";

    private static final String EXCLUDE_FIRST_MATCHED_LINE_KEY = "exclude-first-matched-line";
    private static final String INCLUDE_LAST_MATCHED_LINE_KEY  = "include-last-matched-line";
    
    protected final VelocityHelperService velocityHelperService;
    protected final XhtmlContent xhtmlUtils;
    protected final PluginSettingsFactory pluginSettingsFactory;
    protected final TransactionTemplate transactionTemplate;

    private int startLineNumber = 0;

    public CodeInclude( PluginSettingsFactory pluginSettingsFactory,
                        TransactionTemplate transactionTemplate,
                        VelocityHelperService velocityHelperService,
                        XhtmlContent xhtmlUtils)
    {
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.transactionTemplate = transactionTemplate;
        this.velocityHelperService = velocityHelperService;
        this.xhtmlUtils = xhtmlUtils;
    }

    private void setStartLineNumber( int number ){
       startLineNumber = number;
    }
    private int getStartLineNumber(){
       return startLineNumber;
    }

    private String getUrlContents( Map<String, String> parameters ) throws MacroExecutionException{
       boolean bHasStartRegex = false;
       boolean bHasEndRegex   = false;
       boolean bIncludeLine   = true;
       boolean bExcludeFirstLine = false;
       boolean bIncludeLastLine  = false;
       boolean bMultipleRegions  = false;
       boolean bFirstSection     = true;
       int lineNumber = 1;
       String username = null;
       String password = null;

       StringBuilder text = new StringBuilder();

       try{
          String inputLine; 

          if( parameters.containsKey( URL_KEY ) ){
             URL url = new URL( parameters.get( URL_KEY ) );
             URLConnection browser = url.openConnection();

             if( parameters.containsKey( USERNAME_KEY ) && parameters.containsKey( PASSWORD_KEY ) ){
                username = parameters.get( USERNAME_KEY );
                password = parameters.get( PASSWORD_KEY );
             } else { 
                try{
                   Credentials credentials = (Credentials) transactionTemplate.execute(new TransactionCallback(){
                      public Object doInTransaction(){
                         PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
                         Credentials credentials = new Credentials();
                         credentials.setXml( (String) settings.get(Credentials.class.getName() + ".xml"));
                         return credentials;
                      };
                   });

                   String credentialsXml = URLDecoder.decode( credentials.getXml() );
                   //System.out.println( credentialsXml );

                   String savedUrl = null;
                   String savedUsername = null;
                   String savedPassword = null;
                   DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                   DocumentBuilder builder = factory.newDocumentBuilder();
                   Document document = builder.parse( new InputSource( new StringReader(credentialsXml) ) );
                   Element root = document.getDocumentElement();
                   NodeList presavedCredentials = root.getElementsByTagName( "credentials" );
                   for( int i = 0; i < presavedCredentials.getLength(); i++ ){
                      if( presavedCredentials.item(i).getNodeType() == Node.ELEMENT_NODE){ 
                         savedUrl      = ((Element) presavedCredentials.item(i)).getElementsByTagName( "url" ).item(0).getTextContent();
                         savedUsername = ((Element) presavedCredentials.item(i)).getElementsByTagName( "user" ).item(0).getTextContent();
                         savedPassword = ((Element) presavedCredentials.item(i)).getElementsByTagName( "password" ).item(0).getTextContent();

                         Pattern p = Pattern.compile( savedUrl + ".*" );
                         Matcher m = p.matcher( parameters.get( URL_KEY ) );
                         if( m.matches() ){
                            username = savedUsername;
                            password = savedPassword;
                            break;
                         }
                      }
                   }
                } catch( Exception e) {
                }
             }

             if( username != null && password != null ){
                final String finalUsername = username;
                final String finalPassword = password;
                //System.out.println( "Set username/password: " + username + ":" + password );
                Authenticator.setDefault (new Authenticator() {
                   protected PasswordAuthentication getPasswordAuthentication() {
                      return new PasswordAuthentication (finalUsername, finalPassword.toCharArray());
                   }
                });
             }

             BufferedReader reader = new BufferedReader( new InputStreamReader( browser.getInputStream() ) );

             if( parameters.containsKey( START_REGEX_KEY ) ){
                bHasStartRegex = true;
                bIncludeLine = false;   
             } 
             
             if( parameters.containsKey( END_REGEX_KEY ) ){
                bHasEndRegex = true;
             }
             
             if( parameters.containsKey( INITIAL_LINE_KEY ) ){
                String buffer = insertLineReturns( parameters.get( INITIAL_LINE_KEY ) ); 
                String lines[] = buffer.split( "\\n" );

                text.append( buffer );
                text.append( "\n" );

                lineNumber += lines.length;
             } 

             if( parameters.containsKey( EXCLUDE_FIRST_MATCHED_LINE_KEY ) ){
                bExcludeFirstLine = true;
             }
             
             if( parameters.containsKey( INCLUDE_LAST_MATCHED_LINE_KEY ) ){
                bIncludeLastLine = true;
             }

             if( parameters.containsKey( MULTIPLE_REGIONS_KEY ) ){
                bMultipleRegions = true;
             }

             setStartLineNumber( 0 );
             while(( inputLine = reader.readLine()) != null ){
                if( bHasStartRegex && inputLine.matches( ".*" + parameters.get( START_REGEX_KEY ) + ".*" ) ){
                   bIncludeLine = true;
                   
                   if( getStartLineNumber() == 0 ){
                      setStartLineNumber( lineNumber );
                   }

                   if( !bFirstSection && parameters.containsKey( JOIN_LINE_KEY ) ){
                      text.append( insertLineReturns( parameters.get( JOIN_LINE_KEY ) ) );
                      text.append( "\n" );
                   }
                   
                   if( !bExcludeFirstLine ){
                      text.append( inputLine );
                      text.append( "\n" );
                   }
                } else if( bHasEndRegex && inputLine.matches( ".*" + parameters.get( END_REGEX_KEY ) + ".*" ) ){
                   if( bIncludeLine ){
                      bFirstSection = false;
                      if( bIncludeLine && bIncludeLastLine ){
                         text.append( inputLine );
                         text.append( "\n" );
                      }

                      if( !bMultipleRegions ){
                         break;
                      } else {
                         bIncludeLine = false;
                      }
                   }
                } else {
                   if( bIncludeLine ){
                      text.append( inputLine );
                      text.append( "\n" );
                   }
                }
                lineNumber++;
             }

             if( parameters.containsKey( FINAL_LINE_KEY ) ){
                text.append( insertLineReturns( parameters.get( FINAL_LINE_KEY ) ) );        
             } 
          }
       } catch (MalformedURLException e) {
          throw new MacroExecutionException( e );
       } catch (IOException e) {
          throw new MacroExecutionException( e );
       }
       return text.toString();
    }

    private String insertLineReturns( String line ){
       return line.replaceAll( "\\\\n", "\n" );
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context)
            throws MacroExecutionException
    {
        String template = "/com/keysight/include-content/templates/code-include.vm";
        Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();
        StringBuilder html = new StringBuilder();

        html.append( "<ac:structured-macro ac:name=\"code\">" );
        if( parameters.containsKey( TITLE_KEY ) ){
           html.append(    "<ac:parameter ac:name=\"title\">" + parameters.get(TITLE_KEY) + "</ac:parameter>" );
        }
        if( parameters.containsKey( THEME_KEY ) ){
           html.append(    "<ac:parameter ac:name=\"theme\">" + parameters.get(THEME_KEY) + "</ac:parameter>" );
        }
        if( parameters.containsKey( LANGUAGE_KEY ) ){
           html.append(    "<ac:parameter ac:name=\"language\">" + parameters.get(LANGUAGE_KEY) + "</ac:parameter>" );
        }
        
        // need to do this before the line number parameter as it has some logic to 
        // automatically determine the line number;
        String urlContents = getUrlContents( parameters );
        
        if( parameters.containsKey( FIRST_LINE_KEY ) && parameters.containsKey( LINE_NUMBERS_KEY ) ){
           html.append(    "<ac:parameter ac:name=\"linenumbers\">" + parameters.get(LINE_NUMBERS_KEY) + "</ac:parameter>" );
           html.append(    "<ac:parameter ac:name=\"firstline\">" + parameters.get(FIRST_LINE_KEY) + "</ac:parameter>" );
        } else if( parameters.containsKey( LINE_NUMBERS_KEY ) ){
           html.append(    "<ac:parameter ac:name=\"linenumbers\">" + parameters.get(LINE_NUMBERS_KEY) + "</ac:parameter>" );
           html.append(    "<ac:parameter ac:name=\"firstline\">" + Integer.toString( getStartLineNumber() ) + "</ac:parameter>" );
        } else if( parameters.containsKey( FIRST_LINE_KEY ) ){
           html.append(    "<ac:parameter ac:name=\"firstline\">" + parameters.get(FIRST_LINE_KEY) + "</ac:parameter>" );
        }

        if( parameters.containsKey( COLLAPSE_KEY ) ){
           html.append(    "<ac:parameter ac:name=\"collapse\">" + parameters.get(COLLAPSE_KEY) + "</ac:parameter>" );
        }
        
        html.append( "<ac:plain-text-body><![CDATA[" );
        html.append( urlContents );
        html.append( "]]></ac:plain-text-body>" );
        html.append( "</ac:structured-macro>" );
        
        velocityContext.put( "code", html.toString() );
        return velocityHelperService.getRenderedTemplate(template, velocityContext);
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
