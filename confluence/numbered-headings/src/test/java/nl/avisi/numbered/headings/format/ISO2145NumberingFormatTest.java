package nl.avisi.numbered.headings.format;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ISO2145NumberingFormatTest {

    @Test
    public void shouldRenderSeparatorWhenHeadingLevelIsLast() throws Exception {
        assertTrue(new ISO2145NumberingFormat().shouldRenderSeparator(false));
    }

    @Test
    public void shouldNotRenderSeparatorWhenHeadingLevelIsLast() throws Exception {
        assertFalse(new ISO2145NumberingFormat().shouldRenderSeparator(true));
    }
}
