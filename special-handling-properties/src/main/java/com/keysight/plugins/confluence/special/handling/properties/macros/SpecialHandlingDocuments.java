package com.keysight.plugins.confluence.special.handling.properties.macros;

import java.util.Map;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.ConfluenceUser;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpecialHandlingDocuments implements Macro
{

    private static final String MODEL_KEY = "model";
    private static final String OPTION_KEY = "option";
    private static final String getSpecialHandlingDocsUrl = "http://ofweb.srs.is.keysight.com/cgi-bin/org/of_apps/getSpecialHandlingDocs/getSpecialHandlingDocs.cgi";

    protected final VelocityHelperService velocityHelperService;

    public SpecialHandlingDocuments( VelocityHelperService velocityHelperService )
    {
        this.velocityHelperService = velocityHelperService;
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context)
            throws MacroExecutionException
    {
        String template = "/templates/special-handling-documents.vm";
        Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();
        
        String model  = "";
        String option = "";
        String pageTitle = "";
        String inputLine;
        boolean modelOptionNotFound = false;
        ArrayList<DocumentReference> documents = new ArrayList<DocumentReference>();

        if( parameters.containsKey( MODEL_KEY ) && parameters.containsKey( OPTION_KEY ) ){
           model = parameters.get( MODEL_KEY );
           option = parameters.get( OPTION_KEY );
        } else {
           Pattern pattern = Pattern.compile( "(\\S+)\\s+(\\S+)" );
           Matcher matcher = pattern.matcher( context.getEntity().getTitle() );
           if( matcher.find() ){
              model  = matcher.group(1);
              option = matcher.group(2);
           } else {
             modelOptionNotFound = true;
           }
        }

        try{
           URL url = new URL( getSpecialHandlingDocsUrl + "?PLAIN_TEXT=TRUE&MODEL=" + model + "&OPTION=" + option );
           URLConnection browser = url.openConnection();
           BufferedReader reader = new BufferedReader( new InputStreamReader( browser.getInputStream() ) );
           while(( inputLine = reader.readLine()) != null ){
              try{
                 documents.add( new DocumentReference( inputLine ) );
              } catch ( MalformedURLException e ){
              }
           }
        } catch ( Exception e ){}
        
        velocityContext.put( "model", model );
        velocityContext.put( "option", option );
        velocityContext.put( "documents", documents );
        
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
