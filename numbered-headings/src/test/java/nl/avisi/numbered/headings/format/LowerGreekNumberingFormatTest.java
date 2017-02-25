package nl.avisi.numbered.headings.format;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LowerGreekNumberingFormatTest extends AbstractNumberingFormatTest {

    @Test
    public void testNumbering() {
        int n = 1;

        LowerGreekNumberingFormat nf = new LowerGreekNumberingFormat();
        assertEquals("\u03B1", nf.format(breadcrumbs, n++));
        assertEquals("\u03B2", nf.format(breadcrumbs, n++));
        assertEquals("\u03B3", nf.format(breadcrumbs, n++));
        assertEquals("\u03B4", nf.format(breadcrumbs, n++));
        assertEquals("\u03B5", nf.format(breadcrumbs, n++));
        assertEquals("\u03B6", nf.format(breadcrumbs, n++));
        assertEquals("\u03B7", nf.format(breadcrumbs, n++));
        assertEquals("\u03B8", nf.format(breadcrumbs, n++));
        assertEquals("\u03B9", nf.format(breadcrumbs, n++));
        assertEquals("\u03BA", nf.format(breadcrumbs, n++));
        assertEquals("\u03BB", nf.format(breadcrumbs, n++));
        assertEquals("\u03BC", nf.format(breadcrumbs, n++));
        assertEquals("\u03BD", nf.format(breadcrumbs, n++));
        assertEquals("\u03BE", nf.format(breadcrumbs, n++));
        assertEquals("\u03BF", nf.format(breadcrumbs, n++));
        assertEquals("\u03C0", nf.format(breadcrumbs, n++));
        assertEquals("\u03C1", nf.format(breadcrumbs, n++));
        assertEquals("\u03C3", nf.format(breadcrumbs, n++));
        assertEquals("\u03C4", nf.format(breadcrumbs, n++));
        assertEquals("\u03C5", nf.format(breadcrumbs, n++));
        assertEquals("\u03C6", nf.format(breadcrumbs, n++));
        assertEquals("\u03C7", nf.format(breadcrumbs, n++));
        assertEquals("\u03C8", nf.format(breadcrumbs, n++));
        assertEquals("\u03C9", nf.format(breadcrumbs, n++));
        assertEquals("\u03B1\u03B1", nf.format(breadcrumbs, n++));
        assertEquals("\u03B1\u03B2", nf.format(breadcrumbs, n++));
        assertEquals("\u03B1\u03B3", nf.format(breadcrumbs, n++));
        assertEquals("\u03B1\u03B4", nf.format(breadcrumbs, n++));
        assertEquals("\u03B1\u03B5", nf.format(breadcrumbs, n++));
        assertEquals("\u03B2\u03B5", nf.format(breadcrumbs, n++));
        assertEquals("\u03B2\u03B6", nf.format(breadcrumbs, n++));
        assertEquals("\u03BD\u03B4", nf.format(breadcrumbs, n++));
        assertEquals("\u03B1\u03C3\u03C0", nf.format(breadcrumbs, n++));
        assertEquals("\u03B2\u03C9\u03B8\u03B2\u03BF", nf.format(breadcrumbs, n++));
    }
}
