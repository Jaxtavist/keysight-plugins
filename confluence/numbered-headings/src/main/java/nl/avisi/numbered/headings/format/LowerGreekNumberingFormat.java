package nl.avisi.numbered.headings.format;

/**
 * A numbering format that renders headings using the Greek alphabet in lowercase.
 *
 * Example:
 * <pre>
 *  α.      Heading 1
 *  α.α.    Heading 1.1
 *  α.β.    Heading 1.2
 *  β.      Heading 2
 * </pre>
 */
public class LowerGreekNumberingFormat extends AbstractCharacterRangeFormat {

    // Yes, I know u03C2 (final sigma) is missing, it is a valid modern Greek character, 
    // but it only replaces the normal sigma at the end of a word so we skip it...
    // ...else the Greek alphabet has 25 characters, which is wrong!!!
    private static final String[] LOWER_GREEK_CHARACTERS = {
        "\u03B1", "\u03B2", "\u03B3", "\u03B4", "\u03B5", "\u03B6", "\u03B7", "\u03B8", "\u03B9",
        "\u03BA", "\u03BB", "\u03BC", "\u03BD", "\u03BE", "\u03BF", "\u03C0", "\u03C1", "\u03C3",
        "\u03C4", "\u03C5", "\u03C6", "\u03C7", "\u03C8", "\u03C9"};

    public LowerGreekNumberingFormat() {
        super(LOWER_GREEK_CHARACTERS);
    }
}
