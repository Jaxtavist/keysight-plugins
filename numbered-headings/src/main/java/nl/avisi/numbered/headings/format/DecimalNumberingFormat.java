package nl.avisi.numbered.headings.format;

/**
 * A numbering format that renders 'normal' decimal heading. Almost the same as {@link ISO2145NumberingFormat}, but with a ending separator.
 *
 * Example:
 * <pre>
 *  1.      Heading 1
 *  1.1.    Heading 1.1
 *  1.2.    Heading 1.2
 *  2.      Heading 2
 * </pre>
 */
public class DecimalNumberingFormat extends AbstractFormat {

    @Override
    public String format(int[] breadcrumbs, Integer level) {
        return Integer.toString(breadcrumbs[level - 1]);
    }

    @Override
    public boolean supportsZeroBasedNumbering() {
        return true;
    }
}
