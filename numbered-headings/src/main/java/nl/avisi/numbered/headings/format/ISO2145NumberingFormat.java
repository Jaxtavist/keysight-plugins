package nl.avisi.numbered.headings.format;

/**
 * A numbering format that renders headings according to http://en.wikipedia.org/wiki/ISO_2145. Almost the same as {@link DecimalNumberingFormat},
 * but without the ending separator.
 *
 * Example:
 * <pre>
 *  1       Heading 1
 *  1.1     Heading 1.1
 *  1.2     Heading 1.2
 *  2       Heading 2
 * </pre>
 */
public class ISO2145NumberingFormat extends DecimalNumberingFormat {

    @Override
    public boolean shouldRenderSeparator(boolean last) {
        return !last;
    }
}
