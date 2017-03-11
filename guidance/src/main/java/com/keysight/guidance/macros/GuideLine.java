package com.keysight.guidance.macros; 

import java.util.Map;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
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

    protected final VelocityHelperService velocityHelperService;

    public GuideLine( VelocityHelperService velocityHelperService )
    {
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


        velocityContext.put( GUIDANCE,       guidance );
        velocityContext.put( GUIDANCE_GLYPH, guidanceGlyph );
        velocityContext.put( GUIDANCE_CLASS, guidanceClass );
        velocityContext.put( LP_START_TAG,   lpStartTag );
        velocityContext.put( LP_END_TAG,     lpEndTag );
        velocityContext.put( BODY,           body );

        return velocityHelperService.getRenderedTemplate( template, velocityContext);
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
