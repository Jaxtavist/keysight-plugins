package nl.avisi.numbered.headings.format;

/**
 * A numbering format that renders full decimal headings
 *
 * Example:
 * <pre>
 *  one         Heading 1
 *  one.one     Heading 1.1
 *  one.two     Heading 1.2
 *  two         Heading 2
 * </pre>
 */
public class FullDecimalNumberingFormat extends AbstractFormat {

    private static final String DEFAULT_LOW_HIGH_SEPARATOR = "-";

    private static final int MAX_NUMBERING = 99;

    private static final String[] FULL_DECIMAL_LOW = {"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
            "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen"};

    private static final String[] FULL_DECIMAL_HIGH = {"", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"};

    @Override
    public String format(int[] breadcrumbs, Integer level) {
        Integer value = breadcrumbs[level - 1];

        if (value > MAX_NUMBERING) {
            throw new IllegalArgumentException("The full decimal formatter does not support numbering above " + MAX_NUMBERING);
        } else if (value >= FULL_DECIMAL_LOW.length) {
            return formatHighNumbers(value);
        } else {
            return formatLowNumbers(value);
        }
    }

    @Override
    public boolean supportsZeroBasedNumbering() {
        return true;
    }

    private String formatHighNumbers(Integer value) {
        StringBuilder builder = new StringBuilder();
        String valueString = Integer.toString(value);

        Integer firstDigit = getDigitAt(valueString, 0);
        Integer secondDigit = getDigitAt(valueString, 1);

        builder.append(FULL_DECIMAL_HIGH[firstDigit]);

        if (secondDigit > 0) {
            builder.append(DEFAULT_LOW_HIGH_SEPARATOR);
            builder.append(FULL_DECIMAL_LOW[secondDigit]);
        }

        return builder.toString();
    }

    private Integer getDigitAt(String number, Integer position) {
        return Integer.parseInt(Character.toString(number.charAt(position)));
    }

    private String formatLowNumbers(Integer value) {
        return FULL_DECIMAL_LOW[value];
    }
}
