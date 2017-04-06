package nl.avisi.numbered.headings;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Constructor;

import static org.junit.Assert.assertEquals;

public class UtilsTest {

    @Test
    public void testSplitAndPreserveToken() {
        String StringToBeSplitted = "This- sentence-is-  weird!-";
        String splitRegex = "-";

        String[] splitResult = Utils.splitAndPreserveToken(StringToBeSplitted, splitRegex);

        assertEquals(8, splitResult.length);

        assertEquals("This", splitResult[0]);
        assertEquals("-", splitResult[1]);
        assertEquals(" sentence", splitResult[2]);
        assertEquals("-", splitResult[3]);
        assertEquals("is", splitResult[4]);
        assertEquals("-", splitResult[5]);
        assertEquals("  weird!", splitResult[6]);
        assertEquals("-", splitResult[7]);
    }

    @Test
    public void shouldRenderBlockError() throws Exception {
        String blockError = Utils.renderErrorBlock("TITLE", "MESSAGE");

        assertEquals("<div class=\"aui-message warning\">" +
                "  <p class=\"title\">" +
                "    <span class=\"aui-icon icon-warning\"></span>" +
                "    <strong>TITLE</strong>" +
                "  </p>" +
                "  <p>MESSAGE</p>" +
                "</div>", blockError);
    }

    @Test
    public void shouldNotHaveAnyPublicConstructors() {
        Constructor[] constructors = Utils.class.getConstructors();

        Assert.assertTrue(constructors.length == 0);
    }

    @Test
    public void giveSomeCoverageForPrivateConstructor() throws Exception {
        Constructor constructor = Utils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
