package nl.avisi.numbered.headings.format;

/**
 * An interface for numbering formats. It is recommended to extend {@link AbstractFormat}, since it implements most of this interface already.
 */
public interface NumberingFormat {

    /**
     * Return a formatted string with the given breadcrumbs and for the level set with {@link #setLevel(Integer)}. Only used with custom formatters.
     *
     * @param breadcrumbs the heading counts to use when formatting
     * @return a formatted string
     */
    String format(int[] breadcrumbs);

    /**
     * Return a formatted string with the given breadcrumbs and level.
     *
     * @param breadcrumbs the heading counts to use when formatting
     * @param level the heading level to format
     * @return a formatted string
     */
    String format(int[] breadcrumbs, Integer level);

    /**
     * Check if a separator should be rendered.
     *
     * @param last if this is the last formatter part
     * @return true when the last separator should be rendered, false otherwise
     */
    boolean shouldRenderSeparator(boolean last);

    /**
     * Check if this formatter supports zero based numbering.
     *
     * @return true when it supports zero based numbering, false otherwise
     */
    boolean supportsZeroBasedNumbering();

    /**
     * Set the heading level for which this formatter should format. Used in conjunction with {@link #format(int[])} and {@link #setLevel(Integer)}
     * when using custom formatters.
     *
     * @param level the heading level to use during formatting
     */
    void setLevel(Integer level);
}
