package nl.avisi.numbered.headings.format;

/**
 * Base implementation for {@link NumberingFormat}, all numbering formats should extend this class instead of implementing
 * the {@link NumberingFormat} interface.
 */
public abstract class AbstractFormat implements NumberingFormat {

    private Integer level;

    @Override
    public final void setLevel(Integer level) {
        this.level = level;
    }

    @Override
    public final String format(int[] breadcrumbs) {
        return format(breadcrumbs, this.level);
    }

    @Override
    public boolean shouldRenderSeparator(boolean last) {
        return true;
    }

    @Override
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    public boolean supportsZeroBasedNumbering() {
        return false;
    }
}
