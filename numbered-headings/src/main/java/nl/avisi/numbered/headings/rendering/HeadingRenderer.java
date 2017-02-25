package nl.avisi.numbered.headings.rendering;

import nl.avisi.numbered.headings.format.CustomNumberingFormat;
import nl.avisi.numbered.headings.format.NumberingFormat;
import nl.avisi.numbered.headings.macro.parameters.NumberedHeadingsParameters;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static nl.avisi.numbered.headings.Heading.NUMBER_OF_HEADINGS;

public class HeadingRenderer {

    private static final String HEADING_PATTERN = "(<h[1-6](.*?)?>(<a name=.*?</a>)?)((.)*?</h[1-6]>)";
    private static final String DEFAULT_FORMAT_SEPARATOR = ".";

    private final NumberedHeadingsParameters parameters;
    private Integer lastLevel = 0;
    private int[] counts;

    public HeadingRenderer(final NumberedHeadingsParameters parameters) {
        this.parameters = parameters;
    }

    public RenderResult render(String macroBody) {
        NumberingFormat numberingFormat = parameters.getNumberingFormat();
        List<List<NumberingFormat>> numberingFormatters = parameters.getNumberFormatters();
        Integer startNumberingAt = parameters.getStartNumberingAt().getLevel();
        counts = parameters.getStartingNumbers();

        Pattern pattern = Pattern.compile(HEADING_PATTERN, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(macroBody);

        StringBuffer resultString = new StringBuffer();

        while (matcher.find()) {
            MatchResult result = matcher.toMatchResult();
            int level = getLevelFromMatchResult(macroBody, result);

            if (shouldRenderHeading(level)) {
                String renderedNumber = renderHeading(level, counts, numberingFormat, numberingFormatters, startNumberingAt);
                String renderedNumberSurroundedWithHtmlSpanTag = surroundWithHtmlSpanTag(renderedNumber);

                matcher.appendReplacement(resultString, "$1" + renderedNumberSurroundedWithHtmlSpanTag + "$4");
            }
        }

        matcher.appendTail(resultString);


        return new RenderResult(resultString.toString(), counts);
    }

    private boolean shouldRenderHeading(int level) {
        return !ArrayUtils.contains(parameters.getSkipHeadings(), level);
    }

    // Gets the heading level from a string like; "<h1>...</h1>
    private int getLevelFromMatchResult(String macroBody, MatchResult result) {
        return Character.getNumericValue(macroBody.charAt(result.start() + 2));
    }

    private String renderHeading(int level, int[] counts, NumberingFormat numberingFormat, List<List<NumberingFormat>> numberingFormatters, Integer startNumberingAt) {
        Integer currentLevel = level;

        if (currentLevel >= (startNumberingAt)) {
            if (lastLevel > currentLevel) {
                clearCount(level);
            }

            counts[level - 1] = counts[level - 1] + 1;

            String renderedHeading;
            if (numberingFormat instanceof CustomNumberingFormat) {
                renderedHeading = renderCustomFormat(counts, numberingFormatters.get(level - 1));
            } else {
                renderedHeading = renderNormalFormat(counts, level, numberingFormat, startNumberingAt);
            }

            lastLevel = currentLevel;
            return renderedHeading;
        }

        return "";
    }

    private String renderCustomFormat(int[] counts, List<NumberingFormat> numberingFormatters) {
        StringBuffer result = new StringBuffer();

        for (NumberingFormat numberingFormat : numberingFormatters) {
            result.append(numberingFormat.format(counts));
        }

        result.append(" ");

        return result.toString();
    }

    private String renderNormalFormat(int[] counts, Integer level, NumberingFormat numberingFormat, Integer startNumberingAt) {
        StringBuffer result = new StringBuffer();

        // Render the previous headings
        for (int i = startNumberingAt - 1; i < level; i++) {
            if (shouldRenderHeading(i + 1)) {
                // This is to correct skipped levels (NUMHEAD-26)
                if (i + 1 > lastLevel && i + 1 < level) {
                    counts[i] = counts[i] + 1;
                }

                result.append(numberingFormat.format(counts, i + 1));

                if (numberingFormat.shouldRenderSeparator(i + 1 >= level)) {
                    result.append(DEFAULT_FORMAT_SEPARATOR);
                }
            }
        }

        result.append(" ");

        return result.toString();
    }

    private String surroundWithHtmlSpanTag(String textToSurround) {
        if (StringUtils.isEmpty(textToSurround)) {
            return "";
        }

        return "<span class=\"nh-number\">" + textToSurround + "</span>";
    }

    private void clearCount(Integer level) {
        for (int i = level; i < NUMBER_OF_HEADINGS; i++) {
            counts[i] = 0;
        }
    }
}
