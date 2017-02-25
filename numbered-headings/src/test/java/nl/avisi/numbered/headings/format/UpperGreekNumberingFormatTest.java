package nl.avisi.numbered.headings.format;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UpperGreekNumberingFormatTest extends AbstractNumberingFormatTest {

    @Test
    public void testNumbering() {
        int n = 1;

        UpperGreekNumberingFormat nf = new UpperGreekNumberingFormat();
        assertEquals("\u0391", nf.format(breadcrumbs, n++));
        assertEquals("\u0392", nf.format(breadcrumbs, n++));
        assertEquals("\u0393", nf.format(breadcrumbs, n++));
        assertEquals("\u0394", nf.format(breadcrumbs, n++));
        assertEquals("\u0395", nf.format(breadcrumbs, n++));
        assertEquals("\u0396", nf.format(breadcrumbs, n++));
        assertEquals("\u0397", nf.format(breadcrumbs, n++));
        assertEquals("\u0398", nf.format(breadcrumbs, n++));
        assertEquals("\u0399", nf.format(breadcrumbs, n++));
        assertEquals("\u039A", nf.format(breadcrumbs, n++));
        assertEquals("\u039B", nf.format(breadcrumbs, n++));
        assertEquals("\u039C", nf.format(breadcrumbs, n++));
        assertEquals("\u039D", nf.format(breadcrumbs, n++));
        assertEquals("\u039E", nf.format(breadcrumbs, n++));
        assertEquals("\u039F", nf.format(breadcrumbs, n++));
        assertEquals("\u03A0", nf.format(breadcrumbs, n++));
        assertEquals("\u03A1", nf.format(breadcrumbs, n++));
        assertEquals("\u03A3", nf.format(breadcrumbs, n++));
        assertEquals("\u03A4", nf.format(breadcrumbs, n++));
        assertEquals("\u03A5", nf.format(breadcrumbs, n++));
        assertEquals("\u03A6", nf.format(breadcrumbs, n++));
        assertEquals("\u03A7", nf.format(breadcrumbs, n++));
        assertEquals("\u03A8", nf.format(breadcrumbs, n++));
        assertEquals("\u03A9", nf.format(breadcrumbs, n++));
        assertEquals("\u0391\u0391", nf.format(breadcrumbs, n++));
        assertEquals("\u0391\u0392", nf.format(breadcrumbs, n++));
        assertEquals("\u0391\u0393", nf.format(breadcrumbs, n++));
        assertEquals("\u0391\u0394", nf.format(breadcrumbs, n++));
        assertEquals("\u0391\u0395", nf.format(breadcrumbs, n++));
        assertEquals("\u0392\u0395", nf.format(breadcrumbs, n++));
        assertEquals("\u0392\u0396", nf.format(breadcrumbs, n++));
        assertEquals("\u039D\u0394", nf.format(breadcrumbs, n++));
        assertEquals("\u0391\u03A3\u03A0", nf.format(breadcrumbs, n++));
        assertEquals("\u0392\u03A9\u0398\u0392\u039F", nf.format(breadcrumbs, n++));
    }
}
