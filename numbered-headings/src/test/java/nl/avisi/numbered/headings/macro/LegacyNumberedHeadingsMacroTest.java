package nl.avisi.numbered.headings.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.pages.ContentTree;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.renderer.PageContext;
import com.google.common.collect.Lists;

import nl.avisi.numbered.headings.Heading;
import nl.avisi.numbered.headings.KeyReturningI18NResolver;
import nl.avisi.numbered.headings.exception.NumberedHeadingsMacroException;
import nl.avisi.numbered.headings.format.DecimalNumberingFormat;
import nl.avisi.numbered.headings.i18n.I18nMessage;
import nl.avisi.numbered.headings.macro.parameters.NumberedHeadingsParameterSupport;
import nl.avisi.numbered.headings.macro.parameters.NumberedHeadingsParameters;
import nl.avisi.numbered.headings.rendering.RenderResult;

import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LegacyNumberedHeadingsMacroTest {

	private static final String TL_RENDER_RESULT_FIELD_NAME = "RENDER_RESULT_TL";
	private static final Map<String, String> defaultParameters = new HashMap<String, String>() {{
		put(NumberedHeadingsParameterSupport.START_NUMBERING_WITH_KEY, null);
	}};

	@Test
	public void shouldNotRenderMacroWhenIncludedInAnotherPage() throws Exception {
		ConversionContext conversionContext = createConversionContext(true, true);

		String macroBody = "<h1>Heading 1</h1>";

		LegacyNumberedHeadingsMacro macro = new LegacyNumberedHeadingsMacro(null, null);
		String result = macro.execute(defaultParameters, macroBody, conversionContext);
		assertEquals(macroBody, result);
	}

	@Test
	public void shouldRenderMacroWhenNotIncludedInAnotherPage() throws Exception {
		ConversionContext conversionContext = createConversionContext(false, false);

		String macroBody = "<h1>Heading 1</h1>";
		String expectedResult = "<h1><span class=\"nh-number\">1. </span>Heading 1</h1>";

		LegacyNumberedHeadingsMacro macro = new LegacyNumberedHeadingsMacro(createNumberedHeadingsParameterSupport(), null);
		String result = macro.execute(defaultParameters, macroBody, conversionContext);
		assertEquals(expectedResult, result);
	}

    /*  This test sometimes, and not others.  Either the test is bad or something in the code.
	@Test
	public void shouldUseEndingNumbersFromPreviousExecute() throws Exception {
		ConversionContext conversionContext = createConversionContext(false, true, new Page(), new Page());

		String macroBody = "<h1>Heading 1</h1>";

		LegacyNumberedHeadingsMacro macro = new LegacyNumberedHeadingsMacro(createNumberedHeadingsParameterSupport(), null);
		String result = macro.execute(defaultParameters, macroBody, conversionContext);
		assertEquals("<h1><span class=\"nh-number\">1. </span>Heading 1</h1>", result);

		result = macro.execute(defaultParameters, macroBody, conversionContext);
		assertEquals("<h1><span class=\"nh-number\">2. </span>Heading 1</h1>", result);
	}
    */

	@Test
	public void shouldCleanUpThreadLocalAfterLastPage() throws Exception {
		Page lastPage = new Page();
		ConversionContext conversionContext = createConversionContext(false, true, new Page(), lastPage);
		when(conversionContext.getEntity()).thenReturn(lastPage);

		String macroBody = "<h1>Heading 1</h1>";

		LegacyNumberedHeadingsMacro macro = new LegacyNumberedHeadingsMacro(createNumberedHeadingsParameterSupport(), null);
		macro.execute(defaultParameters, macroBody, conversionContext);
		macro.execute(defaultParameters, macroBody, conversionContext);

		RenderResult renderResult = getRenderResultFromMacro(macro);
		assertNull(renderResult);
	}

	@Test
	// Issue #23, #24, #25, #26
	public void shouldNotStoreRenderingResultInThreadLocalWhenThereAreZeroPages() throws Exception {
		Page lastPage = new Page();
		ConversionContext conversionContext = createConversionContext(false, true);
		when(conversionContext.getEntity()).thenReturn(lastPage);

		String macroBody = "<h1>Heading 1</h1>";

		LegacyNumberedHeadingsMacro macro = new LegacyNumberedHeadingsMacro(createNumberedHeadingsParameterSupport(), null);
		macro.execute(defaultParameters, macroBody, conversionContext);
		macro.execute(defaultParameters, macroBody, conversionContext);

		RenderResult renderResult = getRenderResultFromMacro(macro);
		assertNull(renderResult);
	}

	@Test
	public void shouldNotUseEndingNumbersWhenThereIsOnlyOnePage() throws Exception {
		ConversionContext conversionContext = createConversionContext(false, true, new Page());

		String macroBody = "<h1>Heading 1</h1>";

		LegacyNumberedHeadingsMacro macro = new LegacyNumberedHeadingsMacro(createNumberedHeadingsParameterSupport(), null);
		String result = macro.execute(defaultParameters, macroBody, conversionContext);
		assertEquals("<h1><span class=\"nh-number\">1. </span>Heading 1</h1>", result);

		result = macro.execute(defaultParameters, macroBody, conversionContext);
		assertEquals("<h1><span class=\"nh-number\">1. </span>Heading 1</h1>", result);
	}

	@Test
	public void macroBodyContentShouldBeRichText() {
		LegacyNumberedHeadingsMacro macro = new LegacyNumberedHeadingsMacro(null, null);
		assertEquals(Macro.BodyType.RICH_TEXT, macro.getBodyType());
	}

	@Test
	public void macroOutputTypeIsBlock() {
		LegacyNumberedHeadingsMacro macro = new LegacyNumberedHeadingsMacro(null, null);
		assertEquals(Macro.OutputType.BLOCK, macro.getOutputType());
	}

	@Test
	public void shouldCatchUnexpectedExceptions() throws Exception {
		ConversionContext conversionContext = createConversionContext(false, true, new Page());
		LegacyNumberedHeadingsMacro macro = new LegacyNumberedHeadingsMacro(null, new KeyReturningI18NResolver());
		String result = macro.execute(null, "", conversionContext);    // Causes a NullPointerException

		assertEquals("<div class=\"aui-message warning\">" +
				"  <p class=\"title\">" +
				"    <span class=\"aui-icon icon-warning\"></span>" +
				"    <strong>nl.avisi.numbered-headings.unable-to-render</strong>" +
				"  </p>" +
				"  <p>nl.avisi.numbered-headings.unexpected-error</p>" +
				"</div>", result);
	}

	@Test
	public void shouldCatchMacroException() throws Exception {
		NumberedHeadingsParameterSupport mockedParameterSupport = mock(NumberedHeadingsParameterSupport.class);
		when(mockedParameterSupport.parseParameters(anyMap())).thenThrow(new NumberedHeadingsMacroException(new I18nMessage("nl.avisi.numbered-headings.test-key")));

		LegacyNumberedHeadingsMacro macro = new LegacyNumberedHeadingsMacro(mockedParameterSupport, new KeyReturningI18NResolver());
		String result = macro.execute(Collections.EMPTY_MAP, "", createConversionContext(false, true));

		assertEquals("<div class=\"aui-message warning\">" +
				"  <p class=\"title\">" +
				"    <span class=\"aui-icon icon-warning\"></span>" +
				"    <strong>nl.avisi.numbered-headings.unable-to-render</strong>" +
				"  </p>" +
				"  <p>nl.avisi.numbered-headings.test-key</p>" +
				"</div>", result);
	}

	@Test
	public void shouldCatchNumberedHeadingsMacroException() throws Exception {
		NumberedHeadingsParameterSupport mockedParameterSupport = mock(NumberedHeadingsParameterSupport.class);
		when(mockedParameterSupport.parseParameters(anyMap())).thenThrow(new NumberedHeadingsMacroException(new I18nMessage("nl.avisi.numbered-headings.test-key")));

		LegacyNumberedHeadingsMacro macro = new LegacyNumberedHeadingsMacro(mockedParameterSupport, new KeyReturningI18NResolver());
		String result = macro.execute(Collections.EMPTY_MAP, "", createConversionContext(false, true));

		assertEquals("<div class=\"aui-message warning\">" +
				"  <p class=\"title\">" +
				"    <span class=\"aui-icon icon-warning\"></span>" +
				"    <strong>nl.avisi.numbered-headings.unable-to-render</strong>" +
				"  </p>" +
				"  <p>nl.avisi.numbered-headings.test-key</p>" +
				"</div>", result);
	}

    @Test
    public void shouldNotUsePreviousStartNumberingWithWhenThereIsNoContentTreeAndNumberingWithParameterIsSet() throws Exception {
        String macroBody = "<h1>Heading 1</h1>";

        Map<String, String> parameters = new HashMap<String, String>() {{
            put(NumberedHeadingsParameterSupport.START_NUMBERING_WITH_KEY, "5");
        }};

        LegacyNumberedHeadingsMacro macro = new LegacyNumberedHeadingsMacro(createNumberedHeadingsParameterSupport(), new KeyReturningI18NResolver());
        String result = macro.execute(parameters, macroBody, createConversionContext(false, false));

        assertEquals("<h1><span class=\"nh-number\">1. </span>Heading 1</h1>", result);
    }

    @Test
    public void shouldNotUsePreviousStartNumberingWithWhenThereIsAContentTreeAndNumberingWithParameterIsSet() throws Exception {
        String macroBody = "<h1>Heading 1</h1>";

        Map<String, String> parameters = new HashMap<String, String>() {{
            put(NumberedHeadingsParameterSupport.START_NUMBERING_WITH_KEY, "5");
        }};

        LegacyNumberedHeadingsMacro macro = new LegacyNumberedHeadingsMacro(createNumberedHeadingsParameterSupport(), new KeyReturningI18NResolver());
        String result = macro.execute(parameters, macroBody, createConversionContext(false, true));

        assertEquals("<h1><span class=\"nh-number\">1. </span>Heading 1</h1>", result);
    }

    private ConversionContext createConversionContext(boolean includedInOtherPage, boolean contentTreeExists, Page... pages) {
		PageContext mockedPageContext = mock(PageContext.class);
		DefaultConversionContext mockedConversionContext = mock(DefaultConversionContext.class);
		when(mockedConversionContext.getPageContext()).thenReturn(mockedPageContext);

		if (!includedInOtherPage) {
			when(mockedPageContext.getOriginalContext()).thenReturn(mockedPageContext);
		}

		if (contentTreeExists) {
			ContentTree mockedContentTree = mock(ContentTree.class);
			when(mockedConversionContext.getContentTree()).thenReturn(mockedContentTree);

			when(mockedContentTree.getPages()).thenReturn(Lists.newArrayList(pages));
		}

		return mockedConversionContext;
	}

	private NumberedHeadingsParameterSupport createNumberedHeadingsParameterSupport() throws Exception {
		NumberedHeadingsParameters numberedHeadingsParameters = new NumberedHeadingsParameters();
		numberedHeadingsParameters.setStartNumberingAt(Heading.H1);
		numberedHeadingsParameters.setNumberingFormat(new DecimalNumberingFormat());

		NumberedHeadingsParameterSupport mockedParameterSupport = mock(NumberedHeadingsParameterSupport.class);
		when(mockedParameterSupport.parseParameters(anyMap())).thenReturn(numberedHeadingsParameters);

		return mockedParameterSupport;
	}

	private RenderResult getRenderResultFromMacro(LegacyNumberedHeadingsMacro macro) {
		ThreadLocal<RenderResult> tl = (ThreadLocal<RenderResult>) Whitebox.getInternalState(macro, TL_RENDER_RESULT_FIELD_NAME);

		return tl.get();
	}
}
