package nl.avisi.numbered.headings.format;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UpperRomanNumberingFormatTest extends AbstractNumberingFormatTest {

    @Test
    public void testNumbering() {
        breadcrumbs[33] = 999;

        int n = 1;
        NumberingFormat nf = new UpperRomanNumberingFormat();
        assertEquals("I", nf.format(breadcrumbs, n++));
        assertEquals("II", nf.format(breadcrumbs, n++));
        assertEquals("III", nf.format(breadcrumbs, n++));
        assertEquals("IV", nf.format(breadcrumbs, n++));
        assertEquals("V", nf.format(breadcrumbs, n++));
        assertEquals("VI", nf.format(breadcrumbs, n++));
        assertEquals("VII", nf.format(breadcrumbs, n++));
        assertEquals("VIII", nf.format(breadcrumbs, n++));
        assertEquals("IX", nf.format(breadcrumbs, n++));
        assertEquals("X", nf.format(breadcrumbs, n++));
        assertEquals("XI", nf.format(breadcrumbs, n++));
        assertEquals("XII", nf.format(breadcrumbs, n++));
        assertEquals("XIII", nf.format(breadcrumbs, n++));
        assertEquals("XIV", nf.format(breadcrumbs, n++));
        assertEquals("XV", nf.format(breadcrumbs, n++));
        assertEquals("XVI", nf.format(breadcrumbs, n++));
        assertEquals("XVII", nf.format(breadcrumbs, n++));
        assertEquals("XVIII", nf.format(breadcrumbs, n++));
        assertEquals("XIX", nf.format(breadcrumbs, n++));
        assertEquals("XX", nf.format(breadcrumbs, n++));
        assertEquals("XXI", nf.format(breadcrumbs, n++));
        assertEquals("XXII", nf.format(breadcrumbs, n++));
        assertEquals("XXIII", nf.format(breadcrumbs, n++));
        assertEquals("XXIV", nf.format(breadcrumbs, n++));
        assertEquals("XXV", nf.format(breadcrumbs, n++));
        assertEquals("XXVI", nf.format(breadcrumbs, n++));
        assertEquals("XXVII", nf.format(breadcrumbs, n++));
        assertEquals("XXVIII", nf.format(breadcrumbs, n++));
        assertEquals("XXIX", nf.format(breadcrumbs, n++));
        assertEquals("LIII", nf.format(breadcrumbs, n++));
        assertEquals("LIV", nf.format(breadcrumbs, n++));
        assertEquals("CCCXVI", nf.format(breadcrumbs, n++));
    }
}
