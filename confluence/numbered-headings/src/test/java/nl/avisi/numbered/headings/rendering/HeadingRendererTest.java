package nl.avisi.numbered.headings.rendering;

import com.atlassian.renderer.v2.macro.basic.validator.MacroParameterValidationException;

import nl.avisi.numbered.headings.Heading;
import nl.avisi.numbered.headings.NumberFormatterManager;
import nl.avisi.numbered.headings.format.CustomNumberingFormat;
import nl.avisi.numbered.headings.format.DecimalNumberingFormat;
import nl.avisi.numbered.headings.format.ISO2145NumberingFormat;
import nl.avisi.numbered.headings.format.NumberingFormat;
import nl.avisi.numbered.headings.macro.parameters.NumberedHeadingsParameterSupport;
import nl.avisi.numbered.headings.macro.parameters.NumberedHeadingsParameters;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HeadingRendererTest {

    private NumberedHeadingsParameterSupport numberedHeadingsParameterSupport;

    @Before
    public void beforeTest() throws Exception {
        NumberFormatterManager numberFormatterManager = mock(NumberFormatterManager.class);
        when(numberFormatterManager.fromString("")).thenAnswer(new Answer<NumberingFormat>() {

            @Override
            public NumberingFormat answer(InvocationOnMock invocation) throws Throwable {
                return new DecimalNumberingFormat();
            }

        });
        when(numberFormatterManager.fromString("decimal")).thenAnswer(new Answer<NumberingFormat>() {

            @Override
            public NumberingFormat answer(InvocationOnMock invocation) throws Throwable {
                return new DecimalNumberingFormat();
            }

        });
        when(numberFormatterManager.fromString("custom")).thenReturn(new CustomNumberingFormat());

        numberedHeadingsParameterSupport = new NumberedHeadingsParameterSupport(numberFormatterManager);
    }

    @Test
    public void testRenderOutline1Level() throws MacroParameterValidationException {
        Map<String, String> parameters = new HashMap<String, String>();
        NumberedHeadingsParameters parsedParameters = numberedHeadingsParameterSupport.parseParameters(parameters);

        String src1 = "<h1>Dit is een test</h1>";
        String expectedResult1 = "<h1><span class=\"nh-number\">1. </span>Dit is een test</h1>";

        String src2 = "<h1>Dit is een test</h1>\n<h1>Tweede keer</h1>\n";
        String expectedResult2 = "<h1><span class=\"nh-number\">1. </span>Dit is een test</h1>\n<h1><span class=\"nh-number\">2. </span>Tweede keer</h1>\n";

        assertEquals(expectedResult1, new HeadingRenderer(parsedParameters).render(src1).getResult());
        assertEquals(expectedResult2, new HeadingRenderer(parsedParameters).render(src2).getResult());
    }

    @Test
    public void testRenderOutline2Levels() throws MacroParameterValidationException {
        Map<String, String> parameters = new HashMap<String, String>();
        NumberedHeadingsParameters parsedParameters = numberedHeadingsParameterSupport.parseParameters(parameters);

        String src = "<h1>lA</h1>\n\n" +
                "<h2>lB</h2>\n\n" +
                "<h1>lA</h1>\n";
        String expectedResult = "<h1><span class=\"nh-number\">1. </span>lA</h1>\n\n" +
                "<h2><span class=\"nh-number\">1.1. </span>lB</h2>\n\n" +
                "<h1><span class=\"nh-number\">2. </span>lA</h1>\n";

        RenderResult result = new HeadingRenderer(parsedParameters).render(src);
        assertEquals(expectedResult, result.getResult());
    }

    @Test
    public void testRenderOutline6Levels() throws MacroParameterValidationException {
        Map<String, String> parameters = new HashMap<String, String>();
        NumberedHeadingsParameters parsedParameters = numberedHeadingsParameterSupport.parseParameters(parameters);

        String src = "<h1>lorem ipsum</h1>\n\n" +
                "<h2>lorem ipsum</h2>\n\n" +
                "<h3>lorem ipsum</h3>\n\n" +
                "<h4>lorem ipsum</h4>\n\n" +
                "<h5>lorem ipsum</h5>\n\n" +
                "<h6>lorem ipsum</h6>";
        String expectedResult = "<h1><span class=\"nh-number\">1. </span>lorem ipsum</h1>\n\n" +
                "<h2><span class=\"nh-number\">1.1. </span>lorem ipsum</h2>\n\n" +
                "<h3><span class=\"nh-number\">1.1.1. </span>lorem ipsum</h3>\n\n" +
                "<h4><span class=\"nh-number\">1.1.1.1. </span>lorem ipsum</h4>\n\n" +
                "<h5><span class=\"nh-number\">1.1.1.1.1. </span>lorem ipsum</h5>\n\n" +
                "<h6><span class=\"nh-number\">1.1.1.1.1.1. </span>lorem ipsum</h6>";

        RenderResult result = new HeadingRenderer(parsedParameters).render(src);
        assertEquals(expectedResult, result.getResult());
    }

    @Test
    public void testRenderOutline6LevelsMultiple() throws MacroParameterValidationException {
        Map<String, String> parameters = new HashMap<String, String>();
        NumberedHeadingsParameters parsedParameters = numberedHeadingsParameterSupport.parseParameters(parameters);

        String src = "<h1>lorem ipsum</h1>\n\n" +
                "<h2>lorem ipsum</h2>\n\n" +
                "<h3>lorem ipsum</h3>\n\n" +
                "<h4>lorem ipsum</h4>\n\n" +
                "<h5>lorem ipsum</h5>\n\n" +
                "<h6>lorem ipsum</h6>\n\n" +
                "<h1>lorem ipsumB</h1>\n\n" +
                "<h2>lorem ipsumB</h2>\n\n" +
                "<h3>lorem ipsumB</h3>\n\n" +
                "<h4>lorem ipsumB</h4>\n\n" +
                "<h5>lorem ipsumB</h5>\n\n" +
                "<h6>lorem ipsumB</h6>\n\n" +
                "<h1>lorem ipsumC</h1>\n\n" +
                "<h2>lorem ipsumC</h2>\n\n" +
                "<h3>lorem ipsumC</h3>\n\n" +
                "<h4>lorem ipsumC</h4>\n\n" +
                "<h5>lorem ipsumC</h5>\n\n" +
                "<h6>lorem ipsumC</h6>\n\n" +
                "extra text";

        String expectedResult = "<h1><span class=\"nh-number\">1. </span>lorem ipsum</h1>\n\n" +
                "<h2><span class=\"nh-number\">1.1. </span>lorem ipsum</h2>\n\n" +
                "<h3><span class=\"nh-number\">1.1.1. </span>lorem ipsum</h3>\n\n" +
                "<h4><span class=\"nh-number\">1.1.1.1. </span>lorem ipsum</h4>\n\n" +
                "<h5><span class=\"nh-number\">1.1.1.1.1. </span>lorem ipsum</h5>\n\n" +
                "<h6><span class=\"nh-number\">1.1.1.1.1.1. </span>lorem ipsum</h6>\n\n" +
                "<h1><span class=\"nh-number\">2. </span>lorem ipsumB</h1>\n\n" +
                "<h2><span class=\"nh-number\">2.1. </span>lorem ipsumB</h2>\n\n" +
                "<h3><span class=\"nh-number\">2.1.1. </span>lorem ipsumB</h3>\n\n" +
                "<h4><span class=\"nh-number\">2.1.1.1. </span>lorem ipsumB</h4>\n\n" +
                "<h5><span class=\"nh-number\">2.1.1.1.1. </span>lorem ipsumB</h5>\n\n" +
                "<h6><span class=\"nh-number\">2.1.1.1.1.1. </span>lorem ipsumB</h6>\n\n" +
                "<h1><span class=\"nh-number\">3. </span>lorem ipsumC</h1>\n\n" +
                "<h2><span class=\"nh-number\">3.1. </span>lorem ipsumC</h2>\n\n" +
                "<h3><span class=\"nh-number\">3.1.1. </span>lorem ipsumC</h3>\n\n" +
                "<h4><span class=\"nh-number\">3.1.1.1. </span>lorem ipsumC</h4>\n\n" +
                "<h5><span class=\"nh-number\">3.1.1.1.1. </span>lorem ipsumC</h5>\n\n" +
                "<h6><span class=\"nh-number\">3.1.1.1.1.1. </span>lorem ipsumC</h6>\n\n" +
                "extra text";

        RenderResult result = new HeadingRenderer(parsedParameters).render(src);
        assertEquals(expectedResult, result.getResult());
    }

    @Test
    public void testRender() throws MacroParameterValidationException {
        Map<String, String> parameters = new HashMap<String, String>();

        parameters.put("start-numbering-at", "h1");
        parameters.put("start-numbering-with", "1");
        NumberedHeadingsParameters parsedParameters = numberedHeadingsParameterSupport.parseParameters(parameters);

        RenderResult one = new HeadingRenderer(parsedParameters).render("<h1>Heading 1</h1>");
        RenderResult two = new HeadingRenderer(parsedParameters).render("<h1>Heading 1</h1>\n\n<h1>Heading 2</h1>");

        assertEquals("<h1><span class=\"nh-number\">1. </span>Heading 1</h1>", one.getResult());
        assertEquals("<h1><span class=\"nh-number\">1. </span>Heading 1</h1>\n\n<h1><span class=\"nh-number\">2. </span>Heading 2</h1>", two.getResult());
    }

    @Test
    public void testRenderStartingAtLevel() throws MacroParameterValidationException {
        Map<String, String> parameters = new HashMap<String, String>();

        parameters.put("start-numbering-at", "h2");
        parameters.put("start-numbering-with", "1");
        NumberedHeadingsParameters parsedParameters = numberedHeadingsParameterSupport.parseParameters(parameters);

        RenderResult one = new HeadingRenderer(parsedParameters).render("<h1>Heading 1</h1>");
        RenderResult two = new HeadingRenderer(parsedParameters).render("<h1>Heading 1</h1>\n\n<h2>Heading 2</h2>");

        parameters.put("start-numbering-at", "h3");
        parsedParameters = numberedHeadingsParameterSupport.parseParameters(parameters);

        RenderResult three = new HeadingRenderer(parsedParameters).render("<h1>Heading 1</h1>\n\n<h2>Heading 2</h2>\n\n<h3>Heading 3</h3>");

        assertEquals("should not effect h1", "<h1>Heading 1</h1>", one.getResult());
        assertEquals("should start numbering at h2", "<h1>Heading 1</h1>\n\n<h2><span class=\"nh-number\">1. </span>Heading 2</h2>", two.getResult());
        assertEquals("should start numbering at h3", "<h1>Heading 1</h1>\n\n<h2>Heading 2</h2>\n\n<h3><span class=\"nh-number\">1. </span>Heading 3</h3>", three.getResult());

    }

    @Test
    public void testRenderStartingWithOtherNumberThenOne() throws MacroParameterValidationException {
        Map<String, String> parameters = new HashMap<String, String>();

        parameters.put("start-numbering-at", "h1");
        parameters.put("start-numbering-with", "43");
        NumberedHeadingsParameters parsedParameters = numberedHeadingsParameterSupport.parseParameters(parameters);

        RenderResult zero = new HeadingRenderer(parsedParameters).render("<h1>Heading 1</h1>");

        parameters.put("start-numbering-at", "h2");
        parameters.put("start-numbering-with", "2");
        parsedParameters = numberedHeadingsParameterSupport.parseParameters(parameters);

        RenderResult one = new HeadingRenderer(parsedParameters).render("<h1>Heading 1</h1>");

        parameters.put("start-numbering-with", "3");
        parsedParameters = numberedHeadingsParameterSupport.parseParameters(parameters);

        RenderResult two = new HeadingRenderer(parsedParameters).render("<h1>Heading 1</h1>\n\n<h2>Heading 2</h2>");

        parameters.put("start-numbering-at", "h3");
        parameters.put("start-numbering-with", "99");
        parsedParameters = numberedHeadingsParameterSupport.parseParameters(parameters);

        RenderResult three = new HeadingRenderer(parsedParameters).render("<h1>Heading 1</h1>\n\n<h2>Heading 2</h2>\n\n<h3>Heading 3</h3>");

        assertEquals("should not effect anything", "<h1><span class=\"nh-number\">43. </span>Heading 1</h1>", zero.getResult());

        assertEquals("should not effect anything", "<h1>Heading 1</h1>", one.getResult());
        assertEquals("should start numbering the h2 with 3", "<h1>Heading 1</h1>\n\n<h2><span class=\"nh-number\">3. </span>Heading 2</h2>", two.getResult());
        assertEquals("should start numbering the h3 with 99", "<h1>Heading 1</h1>\n\n<h2>Heading 2</h2>\n\n<h3><span class=\"nh-number\">99. </span>Heading 3</h3>", three.getResult());
    }

    @Test
    public void testCustomNumberingFormat() throws MacroParameterValidationException {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("number-format", "custom");
        parameters.put("h1", "Chapter [h1.decimal].");

        NumberedHeadingsParameters parsedParameters = numberedHeadingsParameterSupport.parseParameters(parameters);

        RenderResult renderResult = new HeadingRenderer(parsedParameters).render("<h1>Heading 1</h1>");

        assertEquals("<h1><span class=\"nh-number\">Chapter 1. </span>Heading 1</h1>", renderResult.getResult());
    }

    @Test
    public void testSkipHeader() throws MacroParameterValidationException {
        Map<String, String> parameters = new HashMap<String, String>();
        NumberedHeadingsParameters parsedParameters = numberedHeadingsParameterSupport.parseParameters(parameters);
        String src = "<h1>lorem ipsum</h1>\n\n" +
                "<h2>lorem ipsum</h2>\n\n" +
                "<h5>lorem ipsum</h5>\n\n";
        String expectedResult = "<h1><span class=\"nh-number\">1. </span>lorem ipsum</h1>\n\n" +
                "<h2><span class=\"nh-number\">1.1. </span>lorem ipsum</h2>\n\n" +
                "<h5><span class=\"nh-number\">1.1.1.1.1. </span>lorem ipsum</h5>\n\n";

        RenderResult result = new HeadingRenderer(parsedParameters).render(src);
        assertEquals(expectedResult, result.getResult());
    }

    @Test
    public void testAnotherSkipHeader() throws MacroParameterValidationException {
        Map<String, String> parameters = new HashMap<String, String>();
        NumberedHeadingsParameters parsedParameters = numberedHeadingsParameterSupport.parseParameters(parameters);
        String src = "<h1 id=\"NUMHEAD-28-headingh1\">heading h1</h1>\n\n" +
                "<h2 id=\"NUMHEAD-28-headingh2\">heading h2</h2>\n\n" +
                "<h5 id=\"NUMHEAD-28-Andthensuddenlyh5\">And then suddenly... h5</h5>\n\n" +
                "<p>but it gets numbered all the same!</p>";
        String expectedResult = "<h1 id=\"NUMHEAD-28-headingh1\"><span class=\"nh-number\">1. </span>heading h1</h1>\n\n" +
                "<h2 id=\"NUMHEAD-28-headingh2\"><span class=\"nh-number\">1.1. </span>heading h2</h2>\n\n" +
                "<h5 id=\"NUMHEAD-28-Andthensuddenlyh5\"><span class=\"nh-number\">1.1.1.1.1. </span>And then suddenly... h5</h5>\n\n" +
                "<p>but it gets numbered all the same!</p>";

        RenderResult result = new HeadingRenderer(parsedParameters).render(src);
        assertEquals(expectedResult, result.getResult());
    }

    @Test
    public void testSkipHeaderSkippingH1() throws MacroParameterValidationException {
        Map<String, String> parameters = new HashMap<String, String>();
        NumberedHeadingsParameters parsedParameters = numberedHeadingsParameterSupport.parseParameters(parameters);
        String src = "<h2 id=\"NUMHEAD-28-headingh2\">heading h2</h2>\n\n" +
                "<h5 id=\"NUMHEAD-28-Andthensuddenlyh5\">And then suddenly... h5</h5>\n\n" +
                "<p>but it gets numbered all the same!</p>";
        String expectedResult = "<h2 id=\"NUMHEAD-28-headingh2\"><span class=\"nh-number\">1.1. </span>heading h2</h2>\n\n" +
                "<h5 id=\"NUMHEAD-28-Andthensuddenlyh5\"><span class=\"nh-number\">1.1.1.1.1. </span>And then suddenly... h5</h5>\n\n" +
                "<p>but it gets numbered all the same!</p>";

        RenderResult result = new HeadingRenderer(parsedParameters).render(src);
        assertEquals(expectedResult, result.getResult());
    }

    @Test
    public void testSkipHeaderStartingAtZero() throws MacroParameterValidationException {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("start-numbering-with", "0");
        NumberedHeadingsParameters parsedParameters = numberedHeadingsParameterSupport.parseParameters(parameters);

        String src = "<h1>lorem ipsum</h1>\n\n" +
                "<h2>lorem ipsum</h2>\n\n" +
                "<h5>lorem ipsum</h5>\n\n";
        String expectedResult = "<h1><span class=\"nh-number\">0. </span>lorem ipsum</h1>\n\n" +
                "<h2><span class=\"nh-number\">0.1. </span>lorem ipsum</h2>\n\n" +
                "<h5><span class=\"nh-number\">0.1.1.1.1. </span>lorem ipsum</h5>\n\n";

        RenderResult result = new HeadingRenderer(parsedParameters).render(src);
        assertEquals(expectedResult, result.getResult());
    }

    @Test
    public void testSkipHeaderCustomFormat() throws MacroParameterValidationException {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("start-numbering-at", "h4");
        parameters.put("number-format", "custom");
        parameters.put("h3", "[h3.decimal]");
        parameters.put("h4", "[h3.decimal].[h4.decimal]");
        parameters.put("h5", "[h3.decimal].[h4.decimal].[h5.decimal]");
        NumberedHeadingsParameters parsedParameters = numberedHeadingsParameterSupport.parseParameters(parameters);

        String src = "<h3>lorem ipsum</h3>\n\n" +
                "<h3>lorem ipsum</h3>\n\n" +
                "<h4>lorem ipsum</h4>\n\n";
        String expectedResult = "<h3>lorem ipsum</h3>\n\n" +
                "<h3>lorem ipsum</h3>\n\n" +
                "<h4><span class=\"nh-number\">.1 </span>lorem ipsum</h4>\n\n";

        RenderResult result = new HeadingRenderer(parsedParameters).render(src);
        assertEquals(expectedResult, result.getResult());
    }

    @Test
    public void shouldSkipHeadingsDuringRendering() throws Exception {
        NumberedHeadingsParameters numberedHeadingsParameters = new NumberedHeadingsParameters();
        numberedHeadingsParameters.setStartNumberingAt(Heading.H1);
        numberedHeadingsParameters.setNumberingFormat(new DecimalNumberingFormat());
        numberedHeadingsParameters.setSkipHeadings(new int[]{2});

        String macroBody = "<h1>Heading 1</h1><h2>Heading 2</h2><h3>Heading 3</h3>";
        String expectedResult = "<h1><span class=\"nh-number\">1. </span>Heading 1</h1><h2>Heading 2</h2><h3><span class=\"nh-number\">1.1. </span>Heading 3</h3>";

        RenderResult renderResult = new HeadingRenderer(numberedHeadingsParameters).render(macroBody);

        assertEquals(expectedResult, renderResult.getResult());
        assertArrayEquals(new int[]{1, 0, 1, 0, 0, 0}, renderResult.getEndingNumbers());
    }

    @Test
    public void shouldRenderSeparatorWhenFormatterSaysSo() throws Exception {
        NumberedHeadingsParameters numberedHeadingsParameters = new NumberedHeadingsParameters();
        numberedHeadingsParameters.setStartNumberingAt(Heading.H1);
        numberedHeadingsParameters.setNumberingFormat(new DecimalNumberingFormat());

        String macroBody = "<h1>Heading 1</h1><h2>Heading 2</h2>";
        String expectedResult = "<h1><span class=\"nh-number\">1. </span>Heading 1</h1><h2><span class=\"nh-number\">1.1. </span>Heading 2</h2>";

        RenderResult renderResult = new HeadingRenderer(numberedHeadingsParameters).render(macroBody);

        assertEquals(expectedResult, renderResult.getResult());
    }

    @Test
    public void shouldNotRenderSeparatorWhenFormatterSaysSo() throws Exception {
        NumberedHeadingsParameters numberedHeadingsParameters = new NumberedHeadingsParameters();
        numberedHeadingsParameters.setStartNumberingAt(Heading.H1);
        numberedHeadingsParameters.setNumberingFormat(new ISO2145NumberingFormat());

        String macroBody = "<h1>Heading 1</h1><h2>Heading 2</h2>";
        String expectedResult = "<h1><span class=\"nh-number\">1 </span>Heading 1</h1><h2><span class=\"nh-number\">1.1 </span>Heading 2</h2>";

        RenderResult renderResult = new HeadingRenderer(numberedHeadingsParameters).render(macroBody);

        assertEquals(expectedResult, renderResult.getResult());
    }

    @Test
    public void shouldMatchNewLinesInHeadings() throws Exception {
        NumberedHeadingsParameters numberedHeadingsParameters = new NumberedHeadingsParameters();
        numberedHeadingsParameters.setStartNumberingAt(Heading.H1);
        numberedHeadingsParameters.setNumberingFormat(new DecimalNumberingFormat());

        String macroBody = "<h1>Heading\r\n 1</h1><h2>Heading 2</h2>";
        String expectedResult = "<h1><span class=\"nh-number\">1. </span>Heading\r\n 1</h1><h2><span class=\"nh-number\">1.1. </span>Heading 2</h2>";

        RenderResult renderResult = new HeadingRenderer(numberedHeadingsParameters).render(macroBody);

        assertEquals(expectedResult, renderResult.getResult());
    }
}
