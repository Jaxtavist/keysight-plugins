package nl.avisi.numbered.headings.format;

/**
 * A numbering format that renders headings using the latin alphabet in lowercase.
 *
 * Example:
 * <pre>
 *  a.      Heading 1
 *  a.b.    Heading 1.1
 *  a.b.    Heading 1.2
 *  b.      Heading 2
 * </pre>
 */
public class LowerLatinNumberingFormat extends UpperLatinNumberingFormat {

    @Override
    public String format(int[] breadCrumbs, Integer level) {
        return super.format(breadCrumbs, level).toLowerCase();
    }
}
