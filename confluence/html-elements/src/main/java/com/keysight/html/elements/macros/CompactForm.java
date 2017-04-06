package com.keysight.html.elements.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;

import com.keysight.html.elements.helpers.FormElement;
import com.keysight.html.elements.helpers.SubmitSeperator;

import java.util.Map;
import java.util.ArrayList;

public class CompactForm implements Macro
{
   public final static String STYLE_KEY         = "style";
   public final static String URL_KEY           = "url";
   public final static String TARGET_KEY        = "target";
   public final static String SUBMIT_KEY        = "submit";
   public final static String SEPERATOR_KEY     = "seperator";
   public final static String ALIGNMENT_KEY     = "alignment";
   public final static String SUBMIT_SEPERATOR_KEY = "submit-seperator";
   public final static String SUBMIT_ALIGNMENT_KEY = "submit-alignment";
   public final static String FORM_ELEMENTS_KEY = "formElements";
   public final static String FORM_LAYOUT_KEY   = "form-layout";

   protected final VelocityHelperService velocityHelperService;

   public CompactForm( VelocityHelperService velocityHelperService )
   {
      this.velocityHelperService = velocityHelperService;
   }

   @Override
   public String execute(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException
   {
      String template;
      Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();
      String lines[] = null;
      velocityContext.put( URL_KEY, parameters.get(URL_KEY) );
      ArrayList<FormElement> formElements = new ArrayList<FormElement>();
      FormElement formElement;
  
      if( parameters.containsKey( STYLE_KEY ) && parameters.get( STYLE_KEY ).matches("Plain HTML") ){
         template = "/com/keysight/html-elements/templates/simple-form.vm";
      } else {
         template = "/com/keysight/html-elements/templates/aui-form.vm";
      }

      if( parameters.containsKey( TARGET_KEY ) && parameters.get(TARGET_KEY).matches("Same Window or Tab") ){
         velocityContext.put( TARGET_KEY, "_self" );
      } else {
         velocityContext.put( TARGET_KEY, "_blank" );
      }
      
      if( parameters.containsKey( ALIGNMENT_KEY ) ){
         velocityContext.put( ALIGNMENT_KEY, parameters.get(ALIGNMENT_KEY) );
      }
      if( parameters.containsKey( SUBMIT_ALIGNMENT_KEY ) ){
         velocityContext.put( SUBMIT_ALIGNMENT_KEY, parameters.get(SUBMIT_ALIGNMENT_KEY) );
      }
      if( parameters.containsKey( SUBMIT_SEPERATOR_KEY ) ){
         velocityContext.put( SUBMIT_SEPERATOR_KEY, new SubmitSeperator( parameters.get(SUBMIT_SEPERATOR_KEY) ) );
      } else if( parameters.containsKey( SEPERATOR_KEY ) ){
         velocityContext.put( SUBMIT_SEPERATOR_KEY, new SubmitSeperator( parameters.get(SEPERATOR_KEY) ) );
      }

      if( parameters.containsKey( SUBMIT_KEY ) ){
         velocityContext.put( SUBMIT_KEY, parameters.get(SUBMIT_KEY) );
      } else {
         velocityContext.put( SUBMIT_KEY, "Submit" );
      }
      
      if( parameters.containsKey( FORM_LAYOUT_KEY ) ){
	 if( parameters.get(FORM_LAYOUT_KEY).matches( "Long Label" ) ){
            velocityContext.put( FORM_LAYOUT_KEY, "long-label" );
	 } else if( parameters.get(FORM_LAYOUT_KEY).matches("Top Label") ){
            velocityContext.put( FORM_LAYOUT_KEY, "top-label" );
	 } else if( parameters.get(FORM_LAYOUT_KEY).matches( "Unsectioned" ) ){
            velocityContext.put( FORM_LAYOUT_KEY, "unsectioned" );
	 } else {
            velocityContext.put( FORM_LAYOUT_KEY, "" );
	 }
      } else {
         velocityContext.put( FORM_LAYOUT_KEY, "" );
      }

      lines = body.split("\n");
      for( String line : lines ){
         formElement = new FormElement( line );
         if( parameters.containsKey( SEPERATOR_KEY ) ){
            formElement.setSeperator( parameters.get( SEPERATOR_KEY ) );
         }
         formElements.add( formElement );
      }
      velocityContext.put( FORM_ELEMENTS_KEY, formElements );

      return velocityHelperService.getRenderedTemplate(template, velocityContext);
   }

   @Override
   public BodyType getBodyType()
   {
      return BodyType.PLAIN_TEXT;
   }

   @Override
   public OutputType getOutputType()
   {
      return OutputType.INLINE;
   }
}
