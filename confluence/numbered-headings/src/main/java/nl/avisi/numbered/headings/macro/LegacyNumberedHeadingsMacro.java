package nl.avisi.numbered.headings.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.ContentTree;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.sal.api.message.I18nResolver;

import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;

import nl.avisi.numbered.headings.Utils;
import nl.avisi.numbered.headings.exception.NumberedHeadingsMacroException;
import nl.avisi.numbered.headings.macro.parameters.NumberedHeadingsParameterSupport;
import nl.avisi.numbered.headings.macro.parameters.NumberedHeadingsParameters;
import nl.avisi.numbered.headings.rendering.HeadingRenderer;
import nl.avisi.numbered.headings.rendering.RenderResult;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Render outlined numbers for all levels of the headings.
 */
public class LegacyNumberedHeadingsMacro extends BaseMacro implements Macro {

    private static final Logger LOGGER = LoggerFactory.getLogger(LegacyNumberedHeadingsMacro.class);

    private static final ThreadLocal<RenderResult> RENDER_RESULT_TL = new ThreadLocal<RenderResult>();

    private final NumberedHeadingsParameterSupport numberedHeadingsParameterSupport;
    private final I18nResolver i18nResolver;

    public LegacyNumberedHeadingsMacro( NumberedHeadingsParameterSupport numberedHeadingsParameterSupport, 
                                        I18nResolver i18nResolver) {
        this.numberedHeadingsParameterSupport = numberedHeadingsParameterSupport;
        this.i18nResolver = i18nResolver;
    }

    @Override
    public String execute(Map<String, String> parameters, String macroBody, ConversionContext conversionContext) throws MacroExecutionException {
        try {
            // Do not render if this macro is in a included page
            if (isPageIncluded(conversionContext)) {
                return macroBody;
            } else {
                return render(parameters, macroBody, conversionContext);
            }
        } catch (NumberedHeadingsMacroException e) {
            return renderErrorBlock(i18nResolver.getText(e.getI18nMessage()));
        } catch (Throwable t) {
            LOGGER.warn("The Numbered Headings plugin threw an unexpected exception...", t);

            return renderErrorBlock(i18nResolver.getText("nl.avisi.numbered-headings.unexpected-error"));
        }
    }

    /**
     * Parses the given parameters and renders the macro body with the parsed parameters
     *
     * @param parameters the parameters of the macro
     * @param macroBody  the body of the macro
     * @return a numbered headings rendered macro body
     */
    private String render(Map<String, String> parameters, String macroBody, ConversionContext conversionContext) {
        NumberedHeadingsParameters parsedParameters = numberedHeadingsParameterSupport.parseParameters(parameters);

        ContentTree contentTree = conversionContext.getContentTree();

        // Issue #31, only use starting numbers from previous macro when START_NUMBERING_WITH_KEY is not set
        if (contentTree != null && parameters.get(NumberedHeadingsParameterSupport.START_NUMBERING_WITH_KEY) == null) {
            List<Page> pages = contentTree.getPages();
            if (pages.size() > 1) { // If there is only one page, it has no use to store rendering result (includes are not in this list)
                RenderResult previousRendererResult = RENDER_RESULT_TL.get();
                if (previousRendererResult != null) {
                    LOGGER.debug("Use numbering: " + ArrayUtils.toString(previousRendererResult.getEndingNumbers()));

                    parsedParameters.setStartingNumbers(previousRendererResult.getEndingNumbers());
                }
            }
        }

        RenderResult rendererResult = new HeadingRenderer(parsedParameters).render(macroBody);

        if (contentTree != null) {
            List<Page> pages = contentTree.getPages();

            // Issue #23, #24, #25, #26...
            // There are no pages if NH is in a blogpost or when creating a global template (and maybe some other cases),
            // which will cause an index out of bounds
            if (pages.isEmpty() || conversionContext.getEntity() == pages.get(pages.size() - 1)) {    // Last page, so we have to do some cleanup
                RENDER_RESULT_TL.set(null);

                LOGGER.debug("Reset numbering");
            } else {
                RENDER_RESULT_TL.set(rendererResult);

                LOGGER.debug("Set numbering: " + ArrayUtils.toString(rendererResult.getEndingNumbers()));
            }
        }

        return rendererResult.getResult();
    }

    /**
     * Checks if the current page (where this macro lives) is included in
     * another page
     *
     * @param conversionContext the conversionContext of the page to check
     * @return true if this page is included, otherwise false
     */
    private boolean isPageIncluded(ConversionContext conversionContext) {
        PageContext pageContext = conversionContext.getPageContext();
        return pageContext.getOriginalContext() != pageContext;
    }

    private String renderErrorBlock(String errorMessage) {
        return Utils.renderErrorBlock(i18nResolver.getText("nl.avisi.numbered-headings.unable-to-render"), errorMessage);
    }

    public BodyType getBodyType() {
        return BodyType.RICH_TEXT;
    }

    public OutputType getOutputType() {
        return OutputType.BLOCK;
    }

   @Override
   public final boolean isInline() {
      return false;
   }
    
   @Override
   public boolean hasBody() {
      return true;
   }

   @Override
   public final boolean suppressSurroundingTagDuringWysiwygRendering() {
      return false;
   }

   @Override
   public final boolean suppressMacroRenderingDuringWysiwyg() {
      return true;
   }

   @Override
   public RenderMode getBodyRenderMode() {
       return RenderMode.ALL;
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
