package nl.avisi.numbered.headings.format;

/**
 * A numbering format that renders headings using the Roman numerals in lowercase.
 *
 * Example:
 * <pre>
 *  i.      Heading 1
 *  i.i.    Heading 1.1
 *  i.ii.    Heading 1.2
 *  ii.      Heading 2
 * </pre>
 */
public class LowerRomanNumberingFormat extends UpperRomanNumberingFormat {

    @Override
    public String format(int[] breadCrumbs, Integer level) {
        return super.format(breadCrumbs, level).toLowerCase();
    }
}
