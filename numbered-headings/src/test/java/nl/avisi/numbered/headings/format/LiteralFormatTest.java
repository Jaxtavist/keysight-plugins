package nl.avisi.numbered.headings.format;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LiteralFormatTest {

    @Test
    public void testFormatWithEmptyLiteral() {
        int[] breadcrumbs = new int[0];

        LiteralFormat literalFormat = new LiteralFormat("");

        assertEquals("", literalFormat.format(breadcrumbs));
        assertEquals("", literalFormat.format(breadcrumbs, 1));
    }

    @Test
    public void testFormatWithLiteral() {
        int[] breadcrumbs = new int[0];

        LiteralFormat literalFormat = new LiteralFormat("Chapter");

        assertEquals("Chapter", literalFormat.format(breadcrumbs));
        assertEquals("Chapter", literalFormat.format(breadcrumbs, 1));
    }
}
