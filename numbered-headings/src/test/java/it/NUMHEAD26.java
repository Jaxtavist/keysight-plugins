package it;

import com.atlassian.confluence.plugin.functest.AbstractConfluencePluginWebTestCase;

import org.junit.Ignore;
import org.junit.Test;

public class NUMHEAD26 extends AbstractConfluencePluginWebTestCase {

	@Test
    public void testStandardDecimal() {
        gotoPage("/display/nh/NUMHEAD-26");
        assertTextPresent(".1 Curabitur");
        assertTextNotPresent("1.1 Curabitur");
        assertTextNotPresent("2.1 Curabitur");
        assertTextNotPresent("Error formatting macro");
        assertTextNotPresent("Exception");
    }
}

