package nl.avisi.numbered.headings.format;

/**
 * A numbering format that renders headings using the Greek alphabet in uppercase.
 *
 * Example:
 * <pre>
 *  Α.      Heading 1
 *  Α.Α.    Heading 1.1
 *  Α.Β.    Heading 1.2
 *  Β.      Heading 2
 * </pre>
 */
public class UpperGreekNumberingFormat extends AbstractCharacterRangeFormat {

    // Yes, I know u03A2 is missing, it does not exist!
    // ...so don't be smart by adding it!
    private static final String[] UPPER_GREEK_CHARACTERS = {
        "\u0391", "\u0392", "\u0393", "\u0394", "\u0395", "\u0396", "\u0397", "\u0398", "\u0399",
        "\u039A", "\u039B", "\u039C", "\u039D", "\u039E", "\u039F", "\u03A0", "\u03A1", "\u03A3",
        "\u03A4", "\u03A5", "\u03A6", "\u03A7", "\u03A8", "\u03A9" };

    public UpperGreekNumberingFormat() {
        super(UPPER_GREEK_CHARACTERS);
    }
}
