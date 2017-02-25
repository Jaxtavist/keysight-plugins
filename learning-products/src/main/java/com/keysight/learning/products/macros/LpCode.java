package com.keysight.learning.products.macros; 

import java.util.Map;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.keysight.learning.products.helpers.LpHelper;

import static org.apache.commons.lang.StringUtils.isEmpty;

public class LpCode implements Macro
{
    protected final static String CODE           = "code";        
    protected final static String LANGUAGE       = "language";        
    protected final static String LP_TAG         = "lpTag";        
    protected final static String END_LP_TAG     = "endLpTag";        

    protected final VelocityHelperService velocityHelperService;

    public LpCode( VelocityHelperService velocityHelperService )
    {
       this.velocityHelperService = velocityHelperService;
    }

    protected String getLanguage()
    {
       return "none";
    }
    protected String getLpTag()
    {
       return "none";
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context)
            throws MacroExecutionException
    {
        StringBuilder code = new StringBuilder();
        String template = "/com/keysight/learning-products/templates/lp-code.vm";
        Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();
        String language = "";
        String lpTag    = "";
        String endLpTag = "";
	String bodyToRender = "";
	boolean enableLpTag = LpHelper.IsLpExport( context );
       
        if( parameters.containsKey( LANGUAGE ) ){
           if( parameters.get( LANGUAGE ).matches( "C\\+\\+" ) ){
              language = "cpp";
              lpTag    = "@LP_CODE_CPLUS_START@";
           } else if( parameters.get( LANGUAGE ).matches( "C#" ) ){
              language = "c#";
              lpTag    = "@LP_CODE_CSHARP_START@";
           } else if( parameters.get( LANGUAGE ).matches( "XML" ) ){
              language = "xml";
              lpTag    = "@LP_CODE_XML_START@";
           } else if( parameters.get( LANGUAGE ).matches( "HTML" ) ){
              language = "xml";
              lpTag    = "@LP_CODE_HTML_START@";
           } else if( parameters.get( LANGUAGE ).matches( "Plain Text" ) ){
              language = "text";
              lpTag    = "@LP_CODE_PLAIN_TEXT_START@";
           }
        } else {
           language = getLanguage();
           lpTag    = getLpTag();
        }

        if( !isEmpty( language ) ){

           if( enableLpTag ){ 
	       endLpTag = lpTag.replaceAll( "START@$", "END@" );
	       bodyToRender = lpTag + "\n"
		            + body + "\n"
			    + endLpTag + "\n";
	   } else {
              bodyToRender = body;
	   }

           code.append( "<ac:structured-macro ac:name=\"code\" ac:schema-version=\"1\">\n" );
           code.append( "   <ac:parameter ac:name=\"language\">" + language + "</ac:parameter>\n" );
           code.append( "   <ac:plain-text-body><![CDATA[" + bodyToRender + "]]></ac:plain-text-body>\n" );
           code.append( "</ac:structured-macro>\n" );
        } else {
           code.append( "<ac:structured-macro ac:name=\"code\" ac:schema-version=\"1\">\n" );
           code.append( "   <ac:plain-text-body><![CDATA[" + body + "]]></ac:plain-text-body>\n" );
           code.append( "</ac:structured-macro>\n" );
        }

        velocityContext.put( CODE, code.toString() );
        return velocityHelperService.getRenderedTemplate( template, velocityContext);
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
