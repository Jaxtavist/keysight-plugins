package nl.avisi.numbered.headings.format;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LowerRomanNumberingFormatTest extends AbstractNumberingFormatTest {

    @Test
    public void testNumbering() {
        breadcrumbs[33] = 999;

        int n = 1;

        NumberingFormat nf = new LowerRomanNumberingFormat();
        assertEquals("i", nf.format(breadcrumbs, n++));
        assertEquals("ii", nf.format(breadcrumbs, n++));
        assertEquals("iii", nf.format(breadcrumbs, n++));
        assertEquals("iv", nf.format(breadcrumbs, n++));
        assertEquals("v", nf.format(breadcrumbs, n++));
        assertEquals("vi", nf.format(breadcrumbs, n++));
        assertEquals("vii", nf.format(breadcrumbs, n++));
        assertEquals("viii", nf.format(breadcrumbs, n++));
        assertEquals("ix", nf.format(breadcrumbs, n++));
        assertEquals("x", nf.format(breadcrumbs, n++));
        assertEquals("xi", nf.format(breadcrumbs, n++));
        assertEquals("xii", nf.format(breadcrumbs, n++));
        assertEquals("xiii", nf.format(breadcrumbs, n++));
        assertEquals("xiv", nf.format(breadcrumbs, n++));
        assertEquals("xv", nf.format(breadcrumbs, n++));
        assertEquals("xvi", nf.format(breadcrumbs, n++));
        assertEquals("xvii", nf.format(breadcrumbs, n++));
        assertEquals("xviii", nf.format(breadcrumbs, n++));
        assertEquals("xix", nf.format(breadcrumbs, n++));
        assertEquals("xx", nf.format(breadcrumbs, n++));
        assertEquals("xxi", nf.format(breadcrumbs, n++));
        assertEquals("xxii", nf.format(breadcrumbs, n++));
        assertEquals("xxiii", nf.format(breadcrumbs, n++));
        assertEquals("xxiv", nf.format(breadcrumbs, n++));
        assertEquals("xxv", nf.format(breadcrumbs, n++));
        assertEquals("xxvi", nf.format(breadcrumbs, n++));
        assertEquals("xxvii", nf.format(breadcrumbs, n++));
        assertEquals("xxviii", nf.format(breadcrumbs, n++));
        assertEquals("xxix", nf.format(breadcrumbs, n++));
        assertEquals("liii", nf.format(breadcrumbs, n++));
        assertEquals("liv", nf.format(breadcrumbs, n++));
        assertEquals("cccxvi", nf.format(breadcrumbs, n++));
    }
}
