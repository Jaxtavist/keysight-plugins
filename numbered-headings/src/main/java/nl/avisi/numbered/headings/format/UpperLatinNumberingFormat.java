package nl.avisi.numbered.headings.format;

/**
 * A numbering format that renders headings using the latin alphabet in lowercase.
 *
 * Example:
 * <pre>
 *  A.      Heading 1
 *  A.B.    Heading 1.1
 *  A.B.    Heading 1.2
 *  B.      Heading 2
 * </pre>
 */
public class UpperLatinNumberingFormat extends AbstractCharacterRangeFormat {

    private static final String[] UPPER_LATIN_CHARACTERS = {
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
        "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

    public UpperLatinNumberingFormat() {
        super(UPPER_LATIN_CHARACTERS);
    }
}
