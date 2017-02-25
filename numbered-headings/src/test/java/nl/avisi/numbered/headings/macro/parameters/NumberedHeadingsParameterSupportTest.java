package nl.avisi.numbered.headings.macro.parameters;

import nl.avisi.numbered.headings.NumberFormatterManager;
import nl.avisi.numbered.headings.exception.NumberedHeadingsMacroException;
import nl.avisi.numbered.headings.format.*;
import nl.avisi.numbered.headings.i18n.I18nMessage;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NumberedHeadingsParameterSupportTest {

    private static final String I18N_MESSAGE_BASE = "nl.avisi.numbered-headings.numbered-headings";

    private NumberedHeadingsParameterSupport numberedHeadingsParameterSupport;

    @Before
    public void beforeTest() throws Exception {
        NumberFormatterManager numberFormatterManager = mock(NumberFormatterManager.class);
        when(numberFormatterManager.fromString("custom")).thenReturn(new CustomNumberingFormat());
        when(numberFormatterManager.fromString("decimal")).thenReturn(new DecimalNumberingFormat());
        when(numberFormatterManager.fromString("upper-roman")).thenReturn(new UpperRomanNumberingFormat());
        when(numberFormatterManager.fromString("FOO")).thenReturn(new DecimalNumberingFormat());

        numberedHeadingsParameterSupport = new NumberedHeadingsParameterSupport(numberFormatterManager);
    }

    @Test
    public void testValidCustomFormat() throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();
        int[] breadcrumbs = { 2 };

        parameters.put("number-format", "custom");
        parameters.put("h1", "Chapter [h1.decimal]");

        NumberedHeadingsParameters pars = numberedHeadingsParameterSupport.parseParameters(parameters);

        List<List<NumberingFormat>> numberFormatters = pars.getNumberFormatters();
        List<NumberingFormat> Heading1Formatters = numberFormatters.get(0);

        assertEquals(2, Heading1Formatters.size());
        assertEquals(LiteralFormat.class, Heading1Formatters.get(0).getClass());
        assertEquals(DecimalNumberingFormat.class, Heading1Formatters.get(1).getClass());

        assertEquals("Chapter ", Heading1Formatters.get(0).format(null));
        assertEquals("2", Heading1Formatters.get(1).format(breadcrumbs));
    }

    @Test
    public void testInvalidCustomFormat1() throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();
        List<Integer> breadcrumbs = new ArrayList<Integer>();
        breadcrumbs.add(2);

        parameters.put("number-format", "custom");
        parameters.put("h1", "Chapter [a1.decimal]");

        NumberedHeadingsParameters pars = numberedHeadingsParameterSupport.parseParameters(parameters);

        List<List<NumberingFormat>> numberFormatters = pars.getNumberFormatters();
        List<NumberingFormat> Heading1Formatters = numberFormatters.get(0);

        assertEquals(1, Heading1Formatters.size());
        assertEquals(LiteralFormat.class, Heading1Formatters.get(0).getClass());
        assertEquals("Chapter [a1.decimal]", Heading1Formatters.get(0).format(null));
    }

    @Test
    public void testInvalidCustomFormat2() throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();
        int[] breadcrumbs = { 2 };

        parameters.put("number-format", "custom");
        parameters.put("h1", "Chapter [h1.FOO]");

        NumberedHeadingsParameters pars = numberedHeadingsParameterSupport.parseParameters(parameters);

        List<List<NumberingFormat>> numberFormatters = pars.getNumberFormatters();
        List<NumberingFormat> Heading1Formatters = numberFormatters.get(0);

        assertEquals(2, Heading1Formatters.size());
        assertEquals(LiteralFormat.class, Heading1Formatters.get(0).getClass());
        assertEquals(DecimalNumberingFormat.class, Heading1Formatters.get(1).getClass());

        assertEquals("Chapter ", Heading1Formatters.get(0).format(null));
        assertEquals("2", Heading1Formatters.get(1).format(breadcrumbs));
    }

    @Test
    public void shouldReturnStartNumberingWithValueWhenBiggerThanZero() throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("number-format", "upper-roman");
        parameters.put("start-numbering-with", "2");

        NumberedHeadingsParameters numberedHeadingsParameters = numberedHeadingsParameterSupport.parseParameters(parameters);
        assertArrayEquals(new int[]{ 1, 0, 0, 0, 0, 0 }, numberedHeadingsParameters.getStartingNumbers());
    }

    @Test
    public void shouldReturnStartNumberingWithValueWhenZeroAndFormatterSupportsIt() throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("number-format", "decimal");
        parameters.put("start-numbering-with", "0");

        NumberedHeadingsParameters numberedHeadingsParameters = numberedHeadingsParameterSupport.parseParameters(parameters);
        assertArrayEquals(new int[]{ -1, 0, 0, 0, 0, 0 }, numberedHeadingsParameters.getStartingNumbers());
    }

    @Test
    public void shouldThrowAnExceptionWhenFormatterDoesNotSupportZeroBasedFormatting() throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("number-format", "upper-roman");
        parameters.put("start-numbering-with", "0");

        try {
            numberedHeadingsParameterSupport.parseParameters(parameters);
            fail("Expected MacroParameterValidationException");
        } catch (NumberedHeadingsMacroException e) {
            assertEquals(I18N_MESSAGE_BASE + ".param.start-numbering-with.not-supported", e.getMessage());
        }
    }

    @Test(expected = NumberedHeadingsMacroException.class)
    public void shouldThrowMacroParameterValidationExceptionWhenStartingWithIsNotValidNumber() throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("number-format", "decimal");
        parameters.put("start-numbering-with", "FOOBAR");

        numberedHeadingsParameterSupport.parseParameters(parameters);
    }

    @Test
    public void shouldThrowMacroParameterValidationExceptionWhenStartingWithIsTooBig() throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("number-format", "decimal");
        parameters.put("start-numbering-with", "2147483648");

        try {
            numberedHeadingsParameterSupport.parseParameters(parameters);
            fail("Expected MacroParameterValidationException");
        } catch (NumberedHeadingsMacroException e) {
            assertEquals(I18N_MESSAGE_BASE + ".param.start-numbering-with.invalid-number", e.getMessage());
        }
    }

    @Test
    public void shouldReturnCorrectlyParseSkipHeadingsParameter() throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("number-format", "decimal");
        parameters.put("skip-headings", "h1,h4");

        NumberedHeadingsParameters numberedHeadingsParameters = numberedHeadingsParameterSupport.parseParameters(parameters);

        assertArrayEquals(new int[]{ 1, 4 }, numberedHeadingsParameters.getSkipHeadings());
    }

    @Test
    public void shouldIgnoreCasingWhenParsingSkipHeadingsParameter() throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("number-format", "decimal");
        parameters.put("skip-headings", "H1,h4");

        NumberedHeadingsParameters numberedHeadingsParameters = numberedHeadingsParameterSupport.parseParameters(parameters);

        assertArrayEquals(new int[]{ 1, 4 }, numberedHeadingsParameters.getSkipHeadings());
    }

    @Test
    public void shouldIgnoreSpacesWhenParsingSkipHeadingsParameter() throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("number-format", "decimal");
        parameters.put("skip-headings", "h1 ,  h4");

        NumberedHeadingsParameters numberedHeadingsParameters = numberedHeadingsParameterSupport.parseParameters(parameters);

        assertArrayEquals(new int[]{ 1, 4 }, numberedHeadingsParameters.getSkipHeadings());
    }

    @Test
    public void shouldIgnoreInvalidHeadingsWhenParsingSkipheadingsParameter() throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("number-format", "decimal");
        parameters.put("skip-headings", "h1,a4");

        NumberedHeadingsParameters numberedHeadingsParameters = numberedHeadingsParameterSupport.parseParameters(parameters);

        assertArrayEquals(new int[]{ 1 }, numberedHeadingsParameters.getSkipHeadings());
    }

    @Test
    public void shouldReturnEmptyArrayWhenSkipHeadingsParameterDoesNotExist() throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("number-format", "decimal");

        NumberedHeadingsParameters numberedHeadingsParameters = numberedHeadingsParameterSupport.parseParameters(parameters);

        assertTrue(ArrayUtils.isEmpty(numberedHeadingsParameters.getSkipHeadings()));
    }

    @Test
    public void shouldIgnoreCustomFormattersInCustomHeadings() throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("number-format", "custom");
        parameters.put("h1", "[h1.custom]Should ignore custom format");

        NumberedHeadingsParameters numberedHeadingsParameters = numberedHeadingsParameterSupport.parseParameters(parameters);
        List<NumberingFormat> headingOneNumberingFormatters = numberedHeadingsParameters.getNumberFormatters().get(0);

        assertTrue(headingOneNumberingFormatters.get(0) instanceof LiteralFormat);
        assertEquals("Should ignore custom format", headingOneNumberingFormatters.get(0).format(null));
    }

    @Test
    public void shouldIgnoreFormattersAboveCurrentHeading() throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("number-format", "custom");
        parameters.put("h1", "[h1.decimal].[h2.decimal].");
        int[] breadcrumbs = { 1, 2, 3 };

        NumberedHeadingsParameters numberedHeadingsParameters = numberedHeadingsParameterSupport.parseParameters(parameters);
        List<NumberingFormat> headingOneNumberingFormatters = numberedHeadingsParameters.getNumberFormatters().get(0);

        assertEquals(3, headingOneNumberingFormatters.size());
        assertEquals("1", headingOneNumberingFormatters.get(0).format(breadcrumbs));
        assertEquals(".", headingOneNumberingFormatters.get(1).format(breadcrumbs));
        assertEquals(".", headingOneNumberingFormatters.get(2).format(breadcrumbs));
    }

    @Test
    public void shouldIgnoreFormattersBelowNumberingAtStart() throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("number-format", "custom");
        parameters.put("start-numbering-at", "h2");
        parameters.put("h2", "[h1.decimal].[h2.decimal].");
        int[] breadcrumbs = { 1, 2, 3 };

        NumberedHeadingsParameters numberedHeadingsParameters = numberedHeadingsParameterSupport.parseParameters(parameters);
        List<NumberingFormat> headingTwoNumberingFormatters = numberedHeadingsParameters.getNumberFormatters().get(1);

        assertEquals(3, headingTwoNumberingFormatters.size());
        assertEquals(".", headingTwoNumberingFormatters.get(0).format(breadcrumbs));
        assertEquals("2", headingTwoNumberingFormatters.get(1).format(breadcrumbs));
        assertEquals(".", headingTwoNumberingFormatters.get(2).format(breadcrumbs));
    }

    @Test
    public void shouldThrowMacroParameterValidationExceptionWhenFormatterDoesNotSupportNumberingAt() throws Exception {
        try {
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("start-numbering-with", "-1");

            numberedHeadingsParameterSupport.parseParameters(parameters);
        } catch (NumberedHeadingsMacroException e) {
            I18nMessage i18nMessage = e.getI18nMessage();

            assertEquals(I18N_MESSAGE_BASE + ".param.start-numbering-with.not-supported", i18nMessage.getKey());
            assertArrayEquals(new Double[]{ -1.0 }, i18nMessage.getArguments());
        }
    }

    @Test
    public void shouldSupportDoubleValuesForStartNumberingWithParameter() throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("start-numbering-with", "2.0");

        NumberedHeadingsParameters numberedHeadingsParameters = numberedHeadingsParameterSupport.parseParameters(parameters);

        assertEquals(1, numberedHeadingsParameters.getStartingNumbers()[0]);
    }
}
