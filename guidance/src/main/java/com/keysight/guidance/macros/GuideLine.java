package com.keysight.guidance.macros; 

import java.util.Map;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.setup.settings.SettingsManager;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import java.net.URLDecoder;
import java.io.StringReader;

import com.keysight.guidance.helpers.PluginConfig;
import com.keysight.guidance.helpers.LpHelper;

public class GuideLine implements Macro
{
    protected final static String BODY                  = "body";        
    protected final static String LEADING_TAG           = "leadingTagAsHtml";        
    protected final static String GUIDANCE              = "guidance";        
    protected final static String GUIDANCE_CLASS        = "guidanceClass";        
    protected final static String GUIDANCE_GLYPH        = "guidanceGlyphAsHtml";        
    protected final static String LP_EXPORT             = "lpExport";
    protected final static String WORD_OR_PDF_EXPORT    = "wordOrPdfExport";
    protected final static String AUTO_DETECTION_FAILED = "autoGuidanceDetectionFailed";
    protected final static String BASE_URL              = "baseUrl";

    protected final static String DO       = "DO";
    protected final static String CONSIDER = "CONSIDER";
    protected final static String AVOID    = "AVOID";
    protected final static String DO_NOT   = "DO NOT";

    protected final static String LP_START_TAG          = "lpStartTag";
    protected final static String LP_END_TAG            = "lpEndTag";
    protected final static String LP_DO_START_TAG       = "@LP_DO_START@";
    protected final static String LP_CONSIDER_START_TAG = "@LP_CONSIDER_START@";
    protected final static String LP_AVOID_START_TAG    = "@LP_AVOID_START@";
    protected final static String LP_DO_NOT_START_TAG   = "@LP_DO_NOT_START@";
    
    protected final static String GUIDANCE_CHECK    = "guidance-check";
    protected final static String GUIDANCE_BALLOT_X = "guidance-ballot-x";

    protected final static String LIGHT_CHECK    = "&#10003;";
    protected final static String HEAVY_CHECK    = "&#10004;";
    protected final static String LIGHT_BALLOT_X = "&#10007;";
    protected final static String HEAVY_BALLOT_X = "&#10008;";
    protected final static String CHECK          = HEAVY_CHECK;
    protected final static String BALLOT_X       = HEAVY_BALLOT_X;

    protected final static String RED_CHECK_PNG      = "red-check.10x10.png";
    protected final static String RED_BALLOT_X_PNG   = "red-x.8x12.png";
    protected final static String BLACK_CHECK_PNG    = "check.10x10.png";
    protected final static String BLACK_BALLOT_X_PNG = "x.8x12.png";
    protected final static String CHECK_PNG        = BLACK_CHECK_PNG;
    protected final static String BALLOT_X_PNG     = BLACK_BALLOT_X_PNG;
    protected final static int CHECK_PNG_WIDTH     = 10;
    protected final static int CHECK_PNG_HEIGHT    = 10;
    protected final static int BALLOT_X_PNG_WIDTH  = 8;
    protected final static int BALLOT_X_PNG_HEIGHT = 12;
    
    protected final static String GUIDANCE_IMAGE = "guidanceImage";
    protected final static String GUIDANCE_IMAGE_WIDTH = "guidanceImageWidth";
    protected final static String GUIDANCE_IMAGE_HEIGHT = "guidanceImageHeight";

    private boolean renderHtmlWithPngImage   = false;
    private boolean renderPdfWithPngImage    = true;
    private boolean renderWordWithPngImage   = false;
    private boolean renderScrollWithPngImage = false;

    protected final PluginSettingsFactory pluginSettingsFactory;
    protected final TransactionTemplate transactionTemplate;
    protected final VelocityHelperService velocityHelperService;
    protected final SettingsManager settingsManager;

    public GuideLine( PluginSettingsFactory pluginSettingsFactory,
                      SettingsManager settingsManager,
                      TransactionTemplate transactionTemplate,
                      VelocityHelperService velocityHelperService )
    {
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.settingsManager = settingsManager;
        this.transactionTemplate = transactionTemplate;
        this.velocityHelperService = velocityHelperService;
    }

    protected boolean fixedGuidance(){ return false; }
    protected String getGuidance(){ return DO; }
    protected String getGuidanceGlyph(){ return CHECK; }
    protected String getGuidanceClass(){ return GUIDANCE_CHECK; }
    protected String getLpStartTag(){ return LP_DO_START_TAG; }
    protected boolean allowLpTag(){ return false; }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context)
            throws MacroExecutionException
    {
        String template = "/com/keysight/guidance/templates/guidance-block.vm";
        Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();
        String guidance      = DO;
        String guidanceGlyph = CHECK;
        String guidanceClass = GUIDANCE_CHECK;
        String lpStartTag    = LP_DO_START_TAG;
        String lpEndTag      = "";
        String leadingTag    = "";
        String providedGuidanceTerm = "";

        parsePluginConfiguration();

        // This code is used to move the start of the html tag before the guidance term rather 
        // than after it.
        int gtIndex = body.indexOf( ">" );
        if( gtIndex >= 0 ){
           if( body.matches( "^(?s)\\s*<.*" ) ){
              leadingTag = body.substring( 0, gtIndex + 1 );
              body = body.substring( gtIndex + 1, body.length() );
              velocityContext.put( LEADING_TAG, leadingTag );
	       } else {
              int ltIndex = body.indexOf( "<" );
              leadingTag = "<p>";
              body = body.substring( 0, ltIndex) + "</p>\n" + body.substring( ltIndex, body.length() );
              velocityContext.put( LEADING_TAG, leadingTag );
	       }
        } else {
           leadingTag = "<p>";
           body = body + "</p>";
           velocityContext.put( LEADING_TAG, leadingTag );
	    }

        if( !fixedGuidance() && (!parameters.containsKey( GUIDANCE ) || parameters.get(GUIDANCE).matches("Auto"))){
           if( body.matches( "^(?i)(?s)\\s*Do\\s+Not\\b.*" ) ){
              providedGuidanceTerm = "Do Not";
           } else if( body.matches( "^(?i)(?s)\\s*Avoid\\b.*" ) ){
              providedGuidanceTerm = "Avoid";
           } else if( body.matches( "^(?i)(?s)\\s*Consider\\b.*" ) ){
              providedGuidanceTerm = "Consider";
           } else if( body.matches( "^(?i)(?s)\\s*Do\\b.*" ) ){
              providedGuidanceTerm = "Do";
           } else {
              providedGuidanceTerm = "Do";
              velocityContext.put( AUTO_DETECTION_FAILED, "true" );
           }
        } else {
           providedGuidanceTerm = parameters.get(GUIDANCE);
        }

        if( fixedGuidance() ){
           guidance      = getGuidance();
           guidanceGlyph = getGuidanceGlyph();
           guidanceClass = getGuidanceClass();
           lpStartTag    = getLpStartTag();
        } else {
           if( providedGuidanceTerm.matches( "Do Not" ) ){
              guidance      = DO_NOT;
              guidanceGlyph = BALLOT_X;
              guidanceClass = GUIDANCE_BALLOT_X;
              lpStartTag    = LP_DO_NOT_START_TAG;
           } else if( providedGuidanceTerm.matches( "Avoid" ) ){
              guidance      = AVOID;
              guidanceGlyph = BALLOT_X;
              guidanceClass = GUIDANCE_BALLOT_X;
              lpStartTag    = LP_AVOID_START_TAG;
           } else if( providedGuidanceTerm.matches( "Consider" ) ){
              guidance      = CONSIDER;
              guidanceGlyph = CHECK;
              guidanceClass = GUIDANCE_CHECK;
              lpStartTag    = LP_CONSIDER_START_TAG;
           } else {
              guidance      = DO;
              guidanceGlyph = CHECK;
              guidanceClass = GUIDANCE_CHECK;
              lpStartTag    = LP_DO_START_TAG;
           }
        }

        lpEndTag = lpStartTag.replaceAll( "START@$", "END@" );
        if( allowLpTag() && LpHelper.IsLpExport( context ) ){
           velocityContext.put( LP_EXPORT, "true" );
        }

        if( LpHelper.IsWordOrPdfExport( context ) ){
           velocityContext.put( WORD_OR_PDF_EXPORT, "true" );
        }

	    if( guidance.equals( DO ) ){
               body = body.replaceAll( "^(?i)(?s)\\s*Do\\b\\s*", "" );
	    } else if( guidance.equals( CONSIDER ) ){
               body = body.replaceAll( "^(?i)(?s)\\s*Consider\\b\\s*", "" );
	    } else if( guidance.equals( AVOID ) ){
               body = body.replaceAll( "^(?i)(?s)\\s*Avoid\\b\\s*", "" );
	    } else if( guidance.equals( DO_NOT ) ){
               body = body.replaceAll( "^(?i)(?s)\\s*Do\\s*Not\\b\\s*", "" );
	    }

        if( LpHelper.IsScrollExport( context ) ){
           if( renderScrollWithPngImage ){
              setGuidanceImage( velocityContext, guidance );
           }
        } else if( LpHelper.IsWordExport( context ) ){
           if( renderWordWithPngImage ){
              setGuidanceImage( velocityContext, guidance );
           }
        } else if( LpHelper.IsPdfExport( context ) ){
           if( renderPdfWithPngImage ){
              setGuidanceImage( velocityContext, guidance );
           }
        } else if( renderHtmlWithPngImage ){
           setGuidanceImage( velocityContext, guidance );
        }

        velocityContext.put( GUIDANCE,       guidance );
        velocityContext.put( GUIDANCE_GLYPH, guidanceGlyph );
        velocityContext.put( GUIDANCE_CLASS, guidanceClass );
        velocityContext.put( LP_START_TAG,   lpStartTag );
        velocityContext.put( LP_END_TAG,     lpEndTag );
        velocityContext.put( BODY,           body );
        velocityContext.put( BASE_URL,       settingsManager.getGlobalSettings().getBaseUrl() );

        return velocityHelperService.getRenderedTemplate( template, velocityContext);
    }

    private void setGuidanceImage( Map<String, Object> velocityContext, String guidance )
    {
	    if( guidance.equals( DO_NOT ) || guidance.equals( AVOID ) ){
           velocityContext.put( GUIDANCE_IMAGE, BALLOT_X_PNG );
           velocityContext.put( GUIDANCE_IMAGE_WIDTH, BALLOT_X_PNG_WIDTH );
           velocityContext.put( GUIDANCE_IMAGE_HEIGHT, BALLOT_X_PNG_HEIGHT );
        } else {
           velocityContext.put( GUIDANCE_IMAGE, CHECK_PNG );
           velocityContext.put( GUIDANCE_IMAGE_WIDTH, CHECK_PNG_WIDTH );
           velocityContext.put( GUIDANCE_IMAGE_HEIGHT, CHECK_PNG_HEIGHT );
        }
    }

    private void parsePluginConfiguration(){
        String[] outputTypes = new String[]{ "for-html", "for-pdf", "for-word", "for-scroll" };
        boolean nodeValue = true;

        try{
            PluginConfig pluginConfig = (PluginConfig) transactionTemplate.execute(new TransactionCallback(){
                public Object doInTransaction(){
                    PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
                    PluginConfig pluginConfig = new PluginConfig();
                    pluginConfig.setXml( (String) settings.get(PluginConfig.class.getName() + ".xml"));
                    return pluginConfig;
                };
            });

            String pluginConfigXml = URLDecoder.decode( pluginConfig.getXml() );

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse( new InputSource( new StringReader(pluginConfigXml) ) );
            Element root = document.getDocumentElement();

            for( String outputType : outputTypes ){
                NodeList nodeList = root.getElementsByTagName( outputType );
                if( nodeList.getLength() > 0 && nodeList.item(0).getNodeType() == Node.ELEMENT_NODE ){
                    if( nodeList.item(0).getTextContent().equals( "true" ) ){
                        nodeValue = true;
                    } else {
                        nodeValue = false;
                    }

                    if( outputType.equals( "for-html" ) ){
                        renderHtmlWithPngImage = nodeValue;
                    } else if( outputType.equals( "for-pdf" ) ){
                        renderPdfWithPngImage = nodeValue;
                    } else if( outputType.equals( "for-word" ) ){
                        renderWordWithPngImage = nodeValue;
                    } else if( outputType.equals( "for-scroll" ) ){
                        renderScrollWithPngImage = nodeValue;
                    }
                }
            }
        } catch( Exception e ){
        }
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
