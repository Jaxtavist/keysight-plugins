package nl.avisi.numbered.headings.format;

import nl.avisi.numbered.headings.exception.NumberedHeadingsException;

/**
 * A very special numbering format that should never be invoked! This is just a placeholder class for custom numbering formats.
 */
public class CustomNumberingFormat extends AbstractFormat {

    @Override
    public String format(int[] breadcrumbs, Integer level) {
        throw new NumberedHeadingsException("This is a placeholder class, it should never be invoked!");
    }
}
