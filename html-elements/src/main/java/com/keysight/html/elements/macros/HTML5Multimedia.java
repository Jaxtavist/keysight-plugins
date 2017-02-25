package com.keysight.html.elements.macros;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;

import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.setup.settings.SettingsManager;

import com.atlassian.confluence.velocity.htmlsafe.HtmlFragment;
import com.atlassian.renderer.RenderContext;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.util.AttachmentMimeTypeTranslator;
import com.atlassian.renderer.v2.RenderUtils;

public class HTML5Multimedia implements Macro
{
    private final AttachmentManager     attachmentManager;
    private final SettingsManager       settingsManager;
    private final VelocityHelperService velocityHelperService;
    
    //Macro Browser Keys
    public static final String MACRO_EDITOR_PAGE_KEY               = "page";  // Not used
    public static final String MACRO_EDITOR_ATTACHMENT_KEY         = "name";
    public static final String MACRO_EDITOR_URL_KEY                = "url";
    public static final String MACRO_EDITOR_URL_MEDIA_TYPE_KEY     = "url-media-type";
    public static final String MACRO_EDITOR_POSTER_KEY             = "poster";
    public static final String MACRO_EDITOR_PRESET_KEY             = "preset";
    public static final String MACRO_EDITOR_HEIGHT_KEY             = "height";
    public static final String MACRO_EDITOR_WIDTH_KEY              = "width";
    public static final String MACRO_EDITOR_LOOP_KEY               = "loop";
    public static final String MACRO_EDITOR_AUTOSTART_KEY          = "autostart";
    public static final String MACRO_EDITOR_AUTOPLAY_KEY           = "autoplay";
    public static final String MACRO_EDITOR_HIDE_DOWNLOAD_LINK_KEY = "hide-download-link";
    public static final String MACRO_EDITOR_IS_AUDIO_KEY           = "is-audio";
    public static final String MACRO_EDITOR_ADD_CONTROLS_KEY       = "add-contols";
    
    //Velocity Template Keys
    public static final String INCLUDE_MEDIA_KEY      = "includeMedia";
    public static final String ATTACHMENT_KEY         = "attachment";
    public static final String POSTER_KEY             = MACRO_EDITOR_POSTER_KEY;
    public static final String HEIGHT_KEY             = MACRO_EDITOR_HEIGHT_KEY;
    public static final String WIDTH_KEY              = MACRO_EDITOR_WIDTH_KEY;
    public static final String LOOP_KEY               = MACRO_EDITOR_LOOP_KEY;
    public static final String AUTOPLAY_KEY           = MACRO_EDITOR_AUTOPLAY_KEY;
    public static final String ADD_DOWNLOAD_LINK_KEY  = "addDownloadLink";
    public static final String ADD_CONTROLS_KEY       = "addControls";
    public static final String URL_KEY                = "url";
    public static final String FILE_NAME_KEY          = "filename";
    public static final String FILE_EXTENSION_KEY     = "fileextension";

    // object constructor
    public HTML5Multimedia( final AttachmentManager attachmentManager,
                            final SettingsManager settingsManager,
                            final VelocityHelperService velocityHelperService)
    {
        this.attachmentManager     = attachmentManager;
        this.settingsManager       = settingsManager;
        this.velocityHelperService = velocityHelperService;
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context)
            throws MacroExecutionException
    {
        Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();
        
        String videoTemplate = "/com/keysight/html-elements/templates/html5-video.vm";
        String audioTemplate = "/com/keysight/html-elements/templates/html5-audio.vm";
        String template = "";
	String result   = "";

        String fileExtension = "";
        String url           = null;
        String fileName;

        String baseUrl  = settingsManager.getGlobalSettings().getBaseUrl();
    
        if( parameters.containsKey( MACRO_EDITOR_URL_KEY ) ){
           url = parameters.get( MACRO_EDITOR_URL_KEY );

           if( parameters.containsKey( MACRO_EDITOR_URL_MEDIA_TYPE_KEY ) && !StringUtils.isEmpty( parameters.get( MACRO_EDITOR_URL_MEDIA_TYPE_KEY ) ) ){
              fileExtension = parameters.get( MACRO_EDITOR_URL_MEDIA_TYPE_KEY );
           } else if( url.length() > 3 ) {
              fileExtension = url.substring( url.length() - 3 );
           }

           velocityContext.put(INCLUDE_MEDIA_KEY, "true" );
	} else if( parameters.containsKey( MACRO_EDITOR_ATTACHMENT_KEY ) ){
           fileName = (String) parameters.get( MACRO_EDITOR_ATTACHMENT_KEY );
           Attachment attachment  = attachmentManager.getAttachment( context.getEntity(), fileName );
        
           url           = baseUrl + (String) attachment.getDownloadPath();
           fileExtension = (String) attachment.getFileExtension();   
           velocityContext.put(FILE_NAME_KEY, fileName );
        }

        if( url != null ){

           // force the controls on.
           velocityContext.put(ADD_CONTROLS_KEY, "controls" );
           
           velocityContext.put(URL_KEY, url );
           velocityContext.put(FILE_EXTENSION_KEY, fileExtension );        
        
           if( parameters.containsKey(MACRO_EDITOR_POSTER_KEY) ){
              velocityContext.put(POSTER_KEY, (String) parameters.get(MACRO_EDITOR_POSTER_KEY));
           }
           
           if( parameters.containsKey(MACRO_EDITOR_LOOP_KEY) ){
              velocityContext.put(LOOP_KEY, "true");
           }
           
           if( parameters.containsKey(MACRO_EDITOR_AUTOPLAY_KEY) ){
              velocityContext.put(AUTOPLAY_KEY, "true");
           } else if( parameters.containsKey(MACRO_EDITOR_AUTOSTART_KEY) ){
              velocityContext.put(AUTOPLAY_KEY, "true");
           }
           
           if( !parameters.containsKey(MACRO_EDITOR_HIDE_DOWNLOAD_LINK_KEY) ){
              velocityContext.put(ADD_DOWNLOAD_LINK_KEY, "true");
           }

           if( parameters.containsKey(MACRO_EDITOR_PRESET_KEY) ){
              String preset = (String) parameters.get(MACRO_EDITOR_PRESET_KEY);
              if( preset.equals( "640x480 (4:3 VGA)" ) ){
                 velocityContext.put(WIDTH_KEY, "640" );
                 velocityContext.put(HEIGHT_KEY, "480" );
              } else if( preset.equals( "800x600 (4:3 SVGA)" ) ){
                 velocityContext.put(WIDTH_KEY, "800" );
                 velocityContext.put(HEIGHT_KEY, "600" );
              } else if( preset.equals( "1024x768 (4:3 XGA)" ) ){
                 velocityContext.put(WIDTH_KEY, "1024" );
                 velocityContext.put(HEIGHT_KEY, "768" );
              } else if( preset.equals( "1600x1200 (4:3 SXGA+)" ) ){
                 velocityContext.put(WIDTH_KEY, "1600" );
                 velocityContext.put(HEIGHT_KEY, "1200" );
              } else if( preset.equals( "1280x1024 (5:4 SXGA)" ) ){
                 velocityContext.put(WIDTH_KEY, "1280" );
                 velocityContext.put(HEIGHT_KEY, "1024" );
              } else if( preset.equals( "640x360 (16:9)" ) ){
                 velocityContext.put(WIDTH_KEY, "640" );
                 velocityContext.put(HEIGHT_KEY, "360" );
              } else if( preset.equals( "960x540 (16:9 qHD)" ) ){
                 velocityContext.put(WIDTH_KEY, "960" );
                 velocityContext.put(HEIGHT_KEY, "540" );
              } else if( preset.equals( "1280x720 (16:9 HD)" ) ){
                 velocityContext.put(WIDTH_KEY, "1280" );
                 velocityContext.put(HEIGHT_KEY, "720" );
              } else if( preset.equals( "1920x1080 (16:9 Full HD)" ) ){
                 velocityContext.put(WIDTH_KEY, "1920" );
                 velocityContext.put(HEIGHT_KEY, "1080" );
              }
           } else {
              if( parameters.containsKey(MACRO_EDITOR_WIDTH_KEY) ){
                 velocityContext.put(WIDTH_KEY, (String) parameters.get(MACRO_EDITOR_WIDTH_KEY));
              }

              if( parameters.containsKey(MACRO_EDITOR_HEIGHT_KEY) ){
                 velocityContext.put(HEIGHT_KEY, (String) parameters.get(MACRO_EDITOR_HEIGHT_KEY));
              }
           }

           if( parameters.containsKey(MACRO_EDITOR_IS_AUDIO_KEY) ){
              template = audioTemplate;
           } else if( fileExtension.equals( "mp3" ) || fileExtension.equals( "wav" ) ) {
              template = audioTemplate;
           } else {
              template = videoTemplate;
           }

	   result = velocityHelperService.getRenderedTemplate(template, velocityContext);
        } else {
	   result = RenderUtils.blockError( "HTML5 Multimedia Error: No Attachment or URL specified", "" );
	}

        return result;
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
