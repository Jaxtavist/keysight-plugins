package it;

import com.atlassian.confluence.plugin.functest.AbstractConfluencePluginWebTestCase;

import org.junit.Ignore;
import org.junit.Test;

public class NUMHEAD34 extends AbstractConfluencePluginWebTestCase {

	@Test
    public void testIsEmpty() {
        gotoPage("/display/nh/NUMHEAD-34");
        assertTextNotPresent("java.lang.String.isEmpty()");
    }
}

