package nl.avisi.numbered.headings;

import java.util.Locale;

/**
 * This enum contains all possible HTML headings and some utility static methods.
 */
public enum Heading {

    H1(1),
    H2(2),
    H3(3),
    H4(4),
    H5(5),
    H6(6);

    public static final int NUMBER_OF_HEADINGS = 6;

    private final int level;

    private Heading(final int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    /**
     * Returns the enum with the given name. The given name can be lower or uppercase (and thus differs from {@link Heading#valueOf(String)}).
     *
     * @param name case insensitive heading name
     * @return the correct heading name, otherwise throws {@link IllegalArgumentException}
     */
    public static Heading getEnum(final String name) {
        return valueOf(name.toUpperCase(Locale.ENGLISH));
    }
}
