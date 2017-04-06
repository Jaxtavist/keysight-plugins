package nl.avisi.numbered.headings.macro.parameters;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import com.atlassian.confluence.util.GeneralUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import nl.avisi.numbered.headings.Heading;
import nl.avisi.numbered.headings.NumberFormatterManager;
import nl.avisi.numbered.headings.Utils;
import nl.avisi.numbered.headings.exception.NumberedHeadingsMacroException;
import nl.avisi.numbered.headings.format.CustomNumberingFormat;
import nl.avisi.numbered.headings.format.DecimalNumberingFormat;
import nl.avisi.numbered.headings.format.LiteralFormat;
import nl.avisi.numbered.headings.format.NumberingFormat;
import nl.avisi.numbered.headings.i18n.I18nMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static nl.avisi.numbered.headings.Heading.NUMBER_OF_HEADINGS;

/**
 * Responsible for checking the parameters passed to the Macro.
 */
@Component
public class NumberedHeadingsParameterSupport {

    private final NumberFormatterManager numberFormatterManager;

    private static final String SKIP_HEADINGS_REGEX = "(h|H)[1-6]";
    private static final String CUSTOM_HEADING_REGEX = "\\[h[1-6]\\.[a-zA-Z0-9\\-]+\\]";
    private static final String CUSTOM_FORMATTER_NAME = "custom";

    /* Parameter keys */
    public static final String NUMBER_FORMAT_KEY = "number-format";
    public static final String START_NUMBERING_AT_KEY = "start-numbering-at";
    public static final String START_NUMBERING_WITH_KEY = "start-numbering-with";
    public static final String SKIP_HEADINGS_KEY = "skip-headings";

    /* Default parameter values */
    private static final Heading DEFAULT_START_NUMBERING_AT = Heading.H1;
    private static final NumberingFormat DEFAULT_NUMBERING_FORMAT = new DecimalNumberingFormat();

    @Autowired
    public NumberedHeadingsParameterSupport(final NumberFormatterManager numberFormatterManager) {
        this.numberFormatterManager = numberFormatterManager;
    }

    public NumberedHeadingsParameters parseParameters(Map<String, String> parameters) {
        NumberedHeadingsParameters parsedParameters = new NumberedHeadingsParameters();

        NumberingFormat numberingFormatParameter = parseNumberFormatParameter(parameters);
        Heading startNumberingAt = parseStartNumberingAtParameter(parameters);
        parsedParameters.setStartingNumbers(parseStartNumberingWithParameter(parameters, numberingFormatParameter, startNumberingAt));
        parsedParameters.setStartNumberingAt(startNumberingAt);
        parsedParameters.setSkipHeadings(parseSkipHeadings(parameters));
        parsedParameters.setNumberingFormat(numberingFormatParameter);
        parsedParameters.setNumberFormatters(parseNumberFormatters(parameters, numberingFormatParameter, startNumberingAt));

        return parsedParameters;
    }

    @SuppressWarnings("PMD.PreserveStackTrace")
    private int[] parseStartNumberingWithParameter(Map<String, String> parameters, NumberingFormat numberingFormat, Heading startNumberingAt) {
        List<Integer> startNumberingWith = Lists.newArrayList(0, 0, 0, 0, 0, 0);

        String parameterValue = parameters.get(START_NUMBERING_WITH_KEY);

        if (!StringUtils.isEmpty(parameterValue)) {
            List<String> parameterParts = Lists.newArrayList(Splitter.on(',').trimResults().split(parameterValue));

            for (int i = 0; i < parameterParts.size(); i++) {
                try {
                    // Parse as double for backwards compatibility, see issue #34
                    double startNumberingWithDouble = Double.parseDouble(parameterParts.get(i));
                    if (startNumberingWithDouble > Integer.MAX_VALUE) {
                        throw new NumberFormatException();
                    }

                    if (numberingFormat.supportsZeroBasedNumbering() && startNumberingWithDouble == 0 || startNumberingWithDouble > 0) {
                        // Both are zero based, so subtract one
                        startNumberingWith.set(startNumberingAt.getLevel() + i - 1, (int) startNumberingWithDouble - 1);
                    } else {
                        I18nMessage message = new I18nMessage(I18nMessage.I18N_KEY_PREFIX + ".param.start-numbering-with.not-supported", startNumberingWithDouble);

                        throw new NumberedHeadingsMacroException(message);
                    }
                } catch (NumberFormatException e) {
                    I18nMessage message = new I18nMessage(I18nMessage.I18N_KEY_PREFIX + ".param.start-numbering-with.invalid-number");

                    throw new NumberedHeadingsMacroException(message);
                }
            }
        }

        return ArrayUtils.toPrimitive(startNumberingWith.toArray(new Integer[startNumberingWith.size()]));
    }

    private Heading parseStartNumberingAtParameter(Map<String, String> parameters) {
        String parameterValue = parameters.get(START_NUMBERING_AT_KEY);

        if (!StringUtils.isEmpty(parameterValue)) {
            return Heading.getEnum(parameterValue);
        }

        return DEFAULT_START_NUMBERING_AT;
    }

    private int[] parseSkipHeadings(Map<String, String> parameters) {
        String parameterValue = parameters.get(SKIP_HEADINGS_KEY);

        if (!StringUtils.isEmpty(parameterValue)) {
            String[] headings = parameterValue.split(",");
            int[] skipHeadings = new int[0];

            for (String heading : headings) {
                String trimmedHeading = heading.trim();

                if (trimmedHeading.matches(SKIP_HEADINGS_REGEX)) {
                    int headingLevel = Integer.parseInt(Character.toString(trimmedHeading.charAt(1)));

                    skipHeadings = ArrayUtils.add(skipHeadings, headingLevel);
                }
            }

            return skipHeadings;
        }

        return new int[0];
    }

    private NumberingFormat parseNumberFormatParameter(Map<String, String> parameters) {
        String parameterValue = parameters.get(NUMBER_FORMAT_KEY);

        if (!StringUtils.isEmpty(parameterValue)) {
            return numberFormatterManager.fromString(parameterValue);
        }

        return DEFAULT_NUMBERING_FORMAT;
    }

    private List<List<NumberingFormat>> parseNumberFormatters(Map<String, String> parameters, NumberingFormat numberingFormat, Heading startNumberingAt) {
        List<List<NumberingFormat>> numberFormatters = new ArrayList<List<NumberingFormat>>(NUMBER_OF_HEADINGS);

        if (numberingFormat instanceof CustomNumberingFormat) {
            for (Heading heading : Heading.values()) {
                String lowerCaseHeading = heading.toString().toLowerCase();

                if (parameters.containsKey(lowerCaseHeading) && heading.getLevel() >= startNumberingAt.getLevel()) {
                    String customHeadingValue = parameters.get(lowerCaseHeading);
                    numberFormatters.add(parseCustomHeadingValue(heading.getLevel(), customHeadingValue, startNumberingAt));
                } else {
                    numberFormatters.add(new ArrayList<NumberingFormat>());
                }
            }
        }

        return numberFormatters;
    }

    private List<NumberingFormat> parseCustomHeadingValue(Integer level, String customHeadingValue, Heading startNumberingAt) {
        List<NumberingFormat> customFormatters = new ArrayList<NumberingFormat>();
        String[] formatters = Utils.splitAndPreserveToken(customHeadingValue, CUSTOM_HEADING_REGEX);

        for (String formatter : formatters) {
            NumberingFormat numberingFormat = parseFormatter(level, formatter, startNumberingAt);

            if (numberingFormat != null) {
                customFormatters.add(numberingFormat);
            }
        }

        return customFormatters;
    }

    private NumberingFormat parseFormatter(Integer level, String headingFormatter, Heading startNumberingAt) {
        NumberingFormat numberingFormat = null;

        if (headingFormatter.matches(CUSTOM_HEADING_REGEX)) {
            // Remove first [ and last ]
            String headingFormatterWithoutBrackets = headingFormatter.substring(1, headingFormatter.length() - 1);

            // Separate heading and formatter; "h1.decimal" gives ["h1", "decimal"]
            String[] formatterSettings = headingFormatterWithoutBrackets.split("\\.");
            Integer formatterLevel = Heading.getEnum(formatterSettings[0]).getLevel();

            if (startNumberingAt.getLevel() <= formatterLevel && formatterLevel <= level) {
                String formatter = formatterSettings[1];

                if (!CUSTOM_FORMATTER_NAME.equals(formatter)) {
                    numberingFormat = createNumberingFormat(formatter, formatterLevel);
                }
            }
        } else {
            numberingFormat = createLiteralFormat(headingFormatter);
        }

        return numberingFormat;
    }

    private NumberingFormat createNumberingFormat(String formatter, Integer level) {
        NumberingFormat numberingFormat = numberFormatterManager.fromString(formatter);
        numberingFormat.setLevel(level);

        return numberingFormat;
    }

    private LiteralFormat createLiteralFormat(String literal) {
        String escapedLiteral = GeneralUtil.escapeXml(literal);

        return new LiteralFormat(escapedLiteral);
    }
}
