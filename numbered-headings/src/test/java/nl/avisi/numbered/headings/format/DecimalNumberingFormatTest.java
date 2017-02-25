package nl.avisi.numbered.headings.format;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DecimalNumberingFormatTest extends AbstractNumberingFormatTest {

    @Test
    public void testNumbering() {
        breadcrumbs[34] = 999999;

        int n = 1;

        DecimalNumberingFormat nf = new DecimalNumberingFormat();
        assertEquals("1", nf.format(breadcrumbs, n++));
        assertEquals("2", nf.format(breadcrumbs, n++));
        assertEquals("3", nf.format(breadcrumbs, n++));
        assertEquals("4", nf.format(breadcrumbs, n++));
        assertEquals("5", nf.format(breadcrumbs, n++));
        assertEquals("6", nf.format(breadcrumbs, n++));
        assertEquals("7", nf.format(breadcrumbs, n++));
        assertEquals("8", nf.format(breadcrumbs, n++));
        assertEquals("9", nf.format(breadcrumbs, n++));
        assertEquals("10", nf.format(breadcrumbs, n++));
        assertEquals("11", nf.format(breadcrumbs, n++));
        assertEquals("12", nf.format(breadcrumbs, n++));
        assertEquals("13", nf.format(breadcrumbs, n++));
        assertEquals("14", nf.format(breadcrumbs, n++));
        assertEquals("15", nf.format(breadcrumbs, n++));
        assertEquals("16", nf.format(breadcrumbs, n++));
        assertEquals("17", nf.format(breadcrumbs, n++));
        assertEquals("18", nf.format(breadcrumbs, n++));
        assertEquals("19", nf.format(breadcrumbs, n++));
        assertEquals("20", nf.format(breadcrumbs, n++));
        assertEquals("21", nf.format(breadcrumbs, n++));
        assertEquals("22", nf.format(breadcrumbs, n++));
        assertEquals("23", nf.format(breadcrumbs, n++));
        assertEquals("24", nf.format(breadcrumbs, n++));
        assertEquals("25", nf.format(breadcrumbs, n++));
        assertEquals("26", nf.format(breadcrumbs, n++));
        assertEquals("27", nf.format(breadcrumbs, n++));
        assertEquals("28", nf.format(breadcrumbs, n++));
        assertEquals("29", nf.format(breadcrumbs, n++));
        assertEquals("53", nf.format(breadcrumbs, n++));
        assertEquals("54", nf.format(breadcrumbs, n++));
        assertEquals("316", nf.format(breadcrumbs, n++));
        assertEquals("1024", nf.format(breadcrumbs, n++));
        assertEquals("999999", nf.format(breadcrumbs, n++));
    }
}
