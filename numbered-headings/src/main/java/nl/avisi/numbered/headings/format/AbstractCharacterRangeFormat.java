package nl.avisi.numbered.headings.format;

/**
 * A base implementation for numbering formats that format based on an array of values, like the {@link UpperLatinNumberingFormat}.
 */
public abstract class AbstractCharacterRangeFormat extends AbstractFormat {

    private final String[] values;

    protected AbstractCharacterRangeFormat(String[] values) {
        this.values = values.clone();
    }

    @Override
    public String format(int[] breadCrumbs, Integer level) {
        Integer headerCounter = breadCrumbs[level - 1];

        StringBuffer buffer = new StringBuffer();
        do {
            // number value is 1 based but the alphabet array is zero based
            // so convert it to a zero base value by subtracting 1.
            int zeroBasedNumberValue = (headerCounter - 1);

            // Calculate the numeric value of the digit, get the character
            // from the alphabet and insert it at the start of the buffer.
            int digit = zeroBasedNumberValue % values.length;
            buffer.insert(0, values[digit]);

            // Drop the digit and move onto the next one.
            headerCounter = zeroBasedNumberValue / values.length;

        } while (headerCounter > 0);

        return buffer.toString();
    }
}
