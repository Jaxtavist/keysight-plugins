package nl.avisi.numbered.headings;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for random stuff that does not fit anywhere else
 */
public final class Utils {

    private Utils() {
        // Utility class, so no instantiation
    }

    /**
     * Splits the given text with the given regex and returns an array which also contains the matched regex tokens.
     * {@link String#split(String)} does not return the tokens.
     *
     * @param text  the text to split
     * @param regex the regex to split the text on
     * @return an array that contains the splitted text and tokens
     */
    public static String[] splitAndPreserveToken(final String text, final String regex) {
        int lastMatch = 0;
        List<String> splitted = new ArrayList<String>();

        Matcher matcher = Pattern.compile(regex).matcher(text);

        while (matcher.find()) {
            String subString = text.substring(lastMatch, matcher.start());
            if (subString.length() > 0) {
                splitted.add(subString);
            }
            splitted.add(matcher.group());

            lastMatch = matcher.end();
        }

        String lastString = text.substring(lastMatch);

        if (!StringUtils.isEmpty(lastString)) {
            splitted.add(text.substring(lastMatch));
        }

        return splitted.toArray(new String[splitted.size()]);
    }

    /**
     * Renders a HTML error block based on the Atlassian Design Guidelines.
     *
     * @param title   text to show as the error block title
     * @param message text to show as the error block message
     * @return a HTML error block
     */
    public static String renderErrorBlock(final String title, final String message) {
        StringBuilder errorBlock = new StringBuilder();
        errorBlock.append("<div class=\"aui-message warning\">");
        errorBlock.append("  <p class=\"title\">");
        errorBlock.append("    <span class=\"aui-icon icon-warning\"></span>");
        errorBlock.append("    <strong>").append(title).append("</strong>");
        errorBlock.append("  </p>");
        errorBlock.append("  <p>").append(message).append("</p>");
        errorBlock.append("</div>");

        return errorBlock.toString();
    }
}
