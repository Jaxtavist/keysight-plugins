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

import java.util.Map;

import com.keysight.guidance.helpers.GuidelineDescription;
import com.keysight.guidance.helpers.PluginConfig;

public class GuideLineInline extends BaseMacro implements Macro, StreamableMacro, EditorImagePlaceholder
{
    private final PluginSettingsFactory pluginSettingsFactory;
    private final TransactionTemplate transactionTemplate;
    private final SettingsManager settingsManager;

    private GuidelineDescription guidelineDescription;
    private boolean renderHtmlWithPngImage   = false;
    private boolean renderPdfWithPngImage    = true;
    private boolean renderWordWithPngImage   = false;
    private boolean renderScrollWithPngImage = false;

    public GuideLineInline( PluginSettingsFactory pluginSettingsFactory,
                            SettingsManager settingsManager,
                            TransactionTemplate transactionTemplate )
    {
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.settingsManager = settingsManager;
        this.transactionTemplate = transactionTemplate;
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
        parsePluginConfiguration();
        GuidelineDescription guidelineDescription = getGuidelineDescription( parameters );
        guidelineDescription.setRenderHtmlWithPngImage( renderHtmlWithPngImage );
        guidelineDescription.setRenderPdfWithPngImage( renderPdfWithPngImage );
        guidelineDescription.setRenderWordWithPngImage( renderWordWithPngImage );
        guidelineDescription.setRenderScrollWithPngImage( renderScrollWithPngImage );
        html.append( guidelineDescription.html( context ) );
    }
    
    private GuidelineDescription getGuidelineDescription( Map<String, String> parameters ){
        String baseUrl = settingsManager.getGlobalSettings().getBaseUrl();
        return new GuidelineDescription( baseUrl, parameters);
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
