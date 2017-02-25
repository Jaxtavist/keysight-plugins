package com.keysight.guidance.macros; 

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.macro.CustomHtmlEditorPlaceholder.PlaceholderGenerationException;
import com.atlassian.confluence.macro.ImagePlaceholder;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.macro.EditorImagePlaceholder;
import com.atlassian.confluence.macro.DefaultImagePlaceholder;
import com.atlassian.confluence.macro.StreamableMacro;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.content.render.image.ImageDimensions;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import java.util.Map;

import com.keysight.guidance.helpers.GuidelineDescription;

public class GuideLineInline extends BaseMacro implements Macro, StreamableMacro, EditorImagePlaceholder
{
    private final SettingsManager settingsManager;
    private GuidelineDescription guidelineDescription;

    public GuideLineInline( SettingsManager settingsManager )
    {
        this.settingsManager = settingsManager;
    }

    @Override
    public Streamable executeToStream(final Map<String, String> parameters, final Streamable body, final ConversionContext context) throws MacroExecutionException 
    {
        return new Streamable() {
            @Override
            public void writeTo(final Writer writer) throws IOException {
                render(writer, parameters, context);
            }
        };
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException 
    {
        final StringBuilder stringBuilder = new StringBuilder();
        try {
            render(stringBuilder, parameters, context);
        } catch (IOException exception) {
            throw new MacroExecutionException(exception);
        }
        return stringBuilder.toString();
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

    public ImagePlaceholder getImagePlaceholder(Map<String, String> parameters, ConversionContext context)
    {
        this.guidelineDescription = getGuidelineDescription( parameters );
        return new DefaultImagePlaceholder( getImageUrl( parameters, context ), false, getImageDimensions( parameters, context ) );
    }

    private String getImageUrl(Map<String, String>parameters, ConversionContext context)
    {
        return this.guidelineDescription.url();
    }

    private ImageDimensions getImageDimensions(Map<String, String>parameters, ConversionContext context)
    {
        return new ImageDimensions( this.guidelineDescription.getWidth(), this.guidelineDescription.getHeight() );
    }

    private void render( final Appendable html,
                         final Map<String, String> parameters, 
                         final ConversionContext context) throws IOException 
    {
        GuidelineDescription guidelineDescription = getGuidelineDescription( parameters );
        System.out.println( guidelineDescription.html() );
        html.append( guidelineDescription.html() );
    }
    
    private GuidelineDescription getGuidelineDescription( Map<String, String> parameters ){
        String baseUrl = settingsManager.getGlobalSettings().getBaseUrl();
        return new GuidelineDescription( baseUrl, parameters);
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
