package nl.avisi.numbered.headings.format;

import org.junit.Test;

import static org.junit.Assert.*;

public class FullDecimalNumberingFormatTest extends AbstractNumberingFormatTest {

    @Test
    public void testNumbering() {
        breadcrumbs[31] = 99;
        breadcrumbs[32] = 100;

        int n = 1;

        FullDecimalNumberingFormat nf = new FullDecimalNumberingFormat();
        assertEquals("one", nf.format(breadcrumbs, n++));
        assertEquals("two", nf.format(breadcrumbs, n++));
        assertEquals("three", nf.format(breadcrumbs, n++));
        assertEquals("four", nf.format(breadcrumbs, n++));
        assertEquals("five", nf.format(breadcrumbs, n++));
        assertEquals("six", nf.format(breadcrumbs, n++));
        assertEquals("seven", nf.format(breadcrumbs, n++));
        assertEquals("eight", nf.format(breadcrumbs, n++));
        assertEquals("nine", nf.format(breadcrumbs, n++));
        assertEquals("ten", nf.format(breadcrumbs, n++));
        assertEquals("eleven", nf.format(breadcrumbs, n++));
        assertEquals("twelve", nf.format(breadcrumbs, n++));
        assertEquals("thirteen", nf.format(breadcrumbs, n++));
        assertEquals("fourteen", nf.format(breadcrumbs, n++));
        assertEquals("fifteen", nf.format(breadcrumbs, n++));
        assertEquals("sixteen", nf.format(breadcrumbs, n++));
        assertEquals("seventeen", nf.format(breadcrumbs, n++));
        assertEquals("eighteen", nf.format(breadcrumbs, n++));
        assertEquals("nineteen", nf.format(breadcrumbs, n++));
        assertEquals("twenty", nf.format(breadcrumbs, n++));
        assertEquals("twenty-one", nf.format(breadcrumbs, n++));
        assertEquals("twenty-two", nf.format(breadcrumbs, n++));
        assertEquals("twenty-three", nf.format(breadcrumbs, n++));
        assertEquals("twenty-four", nf.format(breadcrumbs, n++));
        assertEquals("twenty-five", nf.format(breadcrumbs, n++));
        assertEquals("twenty-six", nf.format(breadcrumbs, n++));
        assertEquals("twenty-seven", nf.format(breadcrumbs, n++));
        assertEquals("twenty-eight", nf.format(breadcrumbs, n++));
        assertEquals("twenty-nine", nf.format(breadcrumbs, n++));
        assertEquals("fifty-three", nf.format(breadcrumbs, n++));
        assertEquals("fifty-four", nf.format(breadcrumbs, n++));
        assertEquals("ninety-nine", nf.format(breadcrumbs, n++));

        try {
            nf.format(breadcrumbs, n++);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("The full decimal formatter does not support numbering above 99", e.getMessage());
        }
    }

    @Test
    public void shouldSupportZeroNumberingFormat() throws Exception {
        NumberingFormat numberingFormat = new FullDecimalNumberingFormat();

        assertTrue(numberingFormat.supportsZeroBasedNumbering());
    }
}
