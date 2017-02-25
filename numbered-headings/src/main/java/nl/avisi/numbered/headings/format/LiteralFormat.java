package nl.avisi.numbered.headings.format;

/**
 * A numbering format that renders configured text literally. Only used with the custom formatter.
 *
 * Example with custom format containing literal and ISO 2145 formatters:
 * <pre>
 *  Chapter 1       Heading 1
 *  Chapter 1.1     Heading 1.1
 *  Chapter 1.2     Heading 1.2
 *  Chapter 2       Heading 2
 * </pre>
 */
public class LiteralFormat extends AbstractFormat {

    private final String literal;

    public LiteralFormat(String literal) {
        this.literal = literal;
    }

    @Override
    public String format(int[] breadCrumbs, Integer level) {
        return literal;
    }
}
