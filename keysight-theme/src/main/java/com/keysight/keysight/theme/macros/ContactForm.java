package com.keysight.keysight.theme.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;

import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;

import java.util.Map;

public class ContactForm extends BaseMacro implements Macro
{
   public final static String IDENTIFIER_KEY        = "identifier";
   public final static String CONTACT_KEY           = "contact";
   public final static String DEFAULT_NAME_KEY      = "defaultName";
   public final static String DEFAULT_EMAIL_KEY     = "defaultEmail";
   public final static String CC_TO_EMAIL_KEY       = "ccToEmail";
   public final static String TEMPLATE_KEY          = "template";
   public final static String ANONYMOUS_NOT_ALLOWED = "anonymous-not-allowed";
   public final static String TEXT_KEY              = "text";
   public final static String TITLE_KEY             = "dialog-title";
   public final static String HELP_KEY              = "dialog-help-text";

   public final static String VELOCITY_TEXT_KEY     = "textWithHtml";
   public final static String VELOCITY_TITLE_KEY    = "dialogTitle";
   public final static String VELOCITY_HELP_KEY     = "dialogHelpText";

   protected final VelocityHelperService velocityHelperService;
   protected final SettingsManager settingsManager;

   public ContactForm( SettingsManager settingsManager,
		       VelocityHelperService velocityHelperService)
   {
      this.settingsManager = settingsManager;
      this.velocityHelperService = velocityHelperService;
   }

   @Override
   public String execute(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException
   {
      String template = "/com/keysight/keysight-theme/templates/contact-form.vm";
      Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();
      String baseUrl   = settingsManager.getGlobalSettings().getBaseUrl();
      String image;
      String identifierKey = "Contact-Form";
      String selectedTemplate = "Keysight Ask";

      ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
      if( currentUser != null ){
         velocityContext.put( DEFAULT_NAME_KEY, currentUser.getFullName() );
         velocityContext.put( DEFAULT_EMAIL_KEY, currentUser.getEmail() );
      } else {
         velocityContext.put( DEFAULT_NAME_KEY, "" );
         velocityContext.put( DEFAULT_EMAIL_KEY, "" );
      }

      if( parameters.containsKey( IDENTIFIER_KEY ) ){
         identifierKey = parameters.get( IDENTIFIER_KEY );
         identifierKey = identifierKey.replaceAll( "'", "" );
      }
      velocityContext.put( IDENTIFIER_KEY, identifierKey );

      if( parameters.containsKey( CONTACT_KEY ) ){
         velocityContext.put( CONTACT_KEY, parameters.get( CONTACT_KEY ) );
      }

      if( parameters.containsKey( CC_TO_EMAIL_KEY ) ){
         velocityContext.put( CC_TO_EMAIL_KEY, "true" );
      } else {
         velocityContext.put( CC_TO_EMAIL_KEY, "false" );
      }

      selectedTemplate = parameters.containsKey( TEMPLATE_KEY ) ? parameters.get( TEMPLATE_KEY ) : "Keysight Ask";
      if( selectedTemplate.matches( "Keysight Ask" )){
	 template = "/com/keysight/keysight-theme/templates/contact-form-keysight-ask.vm";
      } else if( selectedTemplate.matches( "Link - ID Required" )){
	 template = "/com/keysight/keysight-theme/templates/contact-form-link-identification-required.vm";
      } else {
	 template = "/com/keysight/keysight-theme/templates/contact-form-link.vm";
      }

      if( parameters.containsKey( TEXT_KEY ) ){
         velocityContext.put( VELOCITY_TEXT_KEY, parameters.get( TEXT_KEY ) );
      }

      if( parameters.containsKey( TITLE_KEY ) ){
         velocityContext.put( VELOCITY_TITLE_KEY, parameters.get( TITLE_KEY ) );
      } else {
         velocityContext.put( VELOCITY_TITLE_KEY, "Questions or Comments" );
      }

      if( parameters.containsKey( HELP_KEY ) ){
         velocityContext.put( VELOCITY_HELP_KEY, parameters.get( HELP_KEY ) );
      } else {
         if( selectedTemplate.matches( "Link - ID Required" )){
            velocityContext.put( VELOCITY_HELP_KEY, "Please include your name and email address." );
	 } else {
            velocityContext.put( VELOCITY_HELP_KEY, "To receive feedback, please include your name and email address." );
	 }
      }

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
      return OutputType.INLINE;
   }

   /**
    * @see #execute(Map, String, ConversionContext)
    * @deprecated legacy support (pre 4.0) for macro descriptors, kept for ongoing wiki markup conversion support
    */
   @Override
   public boolean hasBody() {
      return false;
   }

   @Override
   public RenderMode getBodyRenderMode() {
       return RenderMode.INLINE;
   }

   @SuppressWarnings({"rawtypes", "unchecked"})
   @Override
   public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
      try {
         return execute(parameters, body, new DefaultConversionContext(renderContext));
      } catch (MacroExecutionException e) {
         throw new MacroException(e);
      }
   }
}
