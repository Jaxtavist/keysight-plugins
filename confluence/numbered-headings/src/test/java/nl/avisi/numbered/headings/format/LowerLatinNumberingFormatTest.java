package nl.avisi.numbered.headings.format;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LowerLatinNumberingFormatTest extends AbstractNumberingFormatTest {

    @Test
    public void testNumbering() {
        int n = 1;

        LowerLatinNumberingFormat nf = new LowerLatinNumberingFormat();
        assertEquals("a", nf.format(breadcrumbs, n++));
        assertEquals("b", nf.format(breadcrumbs, n++));
        assertEquals("c", nf.format(breadcrumbs, n++));
        assertEquals("d", nf.format(breadcrumbs, n++));
        assertEquals("e", nf.format(breadcrumbs, n++));
        assertEquals("f", nf.format(breadcrumbs, n++));
        assertEquals("g", nf.format(breadcrumbs, n++));
        assertEquals("h", nf.format(breadcrumbs, n++));
        assertEquals("i", nf.format(breadcrumbs, n++));
        assertEquals("j", nf.format(breadcrumbs, n++));
        assertEquals("k", nf.format(breadcrumbs, n++));
        assertEquals("l", nf.format(breadcrumbs, n++));
        assertEquals("m", nf.format(breadcrumbs, n++));
        assertEquals("n", nf.format(breadcrumbs, n++));
        assertEquals("o", nf.format(breadcrumbs, n++));
        assertEquals("p", nf.format(breadcrumbs, n++));
        assertEquals("q", nf.format(breadcrumbs, n++));
        assertEquals("r", nf.format(breadcrumbs, n++));
        assertEquals("s", nf.format(breadcrumbs, n++));
        assertEquals("t", nf.format(breadcrumbs, n++));
        assertEquals("u", nf.format(breadcrumbs, n++));
        assertEquals("v", nf.format(breadcrumbs, n++));
        assertEquals("w", nf.format(breadcrumbs, n++));
        assertEquals("x", nf.format(breadcrumbs, n++));
        assertEquals("y", nf.format(breadcrumbs, n++));
        assertEquals("z", nf.format(breadcrumbs, n++));
        assertEquals("aa", nf.format(breadcrumbs, n++));
        assertEquals("ab", nf.format(breadcrumbs, n++));
        assertEquals("ac", nf.format(breadcrumbs, n++));
        assertEquals("ba", nf.format(breadcrumbs, n++));
        assertEquals("bb", nf.format(breadcrumbs, n++));
        assertEquals("ld", nf.format(breadcrumbs, n++));
        assertEquals("amj", nf.format(breadcrumbs, n++));
        assertEquals("bdwgm", nf.format(breadcrumbs, n++));
    }
}
