package nl.avisi.numbered.headings.format;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UpperLatinNumberingFormatTest extends AbstractNumberingFormatTest {

    @Test
    public void testNumbering() {
        int n = 1;

        UpperLatinNumberingFormat nf = new UpperLatinNumberingFormat();
        assertEquals("A", nf.format(breadcrumbs, n++));
        assertEquals("B", nf.format(breadcrumbs, n++));
        assertEquals("C", nf.format(breadcrumbs, n++));
        assertEquals("D", nf.format(breadcrumbs, n++));
        assertEquals("E", nf.format(breadcrumbs, n++));
        assertEquals("F", nf.format(breadcrumbs, n++));
        assertEquals("G", nf.format(breadcrumbs, n++));
        assertEquals("H", nf.format(breadcrumbs, n++));
        assertEquals("I", nf.format(breadcrumbs, n++));
        assertEquals("J", nf.format(breadcrumbs, n++));
        assertEquals("K", nf.format(breadcrumbs, n++));
        assertEquals("L", nf.format(breadcrumbs, n++));
        assertEquals("M", nf.format(breadcrumbs, n++));
        assertEquals("N", nf.format(breadcrumbs, n++));
        assertEquals("O", nf.format(breadcrumbs, n++));
        assertEquals("P", nf.format(breadcrumbs, n++));
        assertEquals("Q", nf.format(breadcrumbs, n++));
        assertEquals("R", nf.format(breadcrumbs, n++));
        assertEquals("S", nf.format(breadcrumbs, n++));
        assertEquals("T", nf.format(breadcrumbs, n++));
        assertEquals("U", nf.format(breadcrumbs, n++));
        assertEquals("V", nf.format(breadcrumbs, n++));
        assertEquals("W", nf.format(breadcrumbs, n++));
        assertEquals("X", nf.format(breadcrumbs, n++));
        assertEquals("Y", nf.format(breadcrumbs, n++));
        assertEquals("Z", nf.format(breadcrumbs, n++));
        assertEquals("AA", nf.format(breadcrumbs, n++));
        assertEquals("AB", nf.format(breadcrumbs, n++));
        assertEquals("AC", nf.format(breadcrumbs, n++));
        assertEquals("BA", nf.format(breadcrumbs, n++));
        assertEquals("BB", nf.format(breadcrumbs, n++));
        assertEquals("LD", nf.format(breadcrumbs, n++));
        assertEquals("AMJ", nf.format(breadcrumbs, n++));
        assertEquals("BDWGM", nf.format(breadcrumbs, n++));
    }
}
