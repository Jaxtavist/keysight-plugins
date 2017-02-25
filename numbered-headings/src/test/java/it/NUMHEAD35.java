package it;

import com.atlassian.confluence.plugin.functest.AbstractConfluencePluginWebTestCase;

import org.junit.Ignore;
import org.junit.Test;

public class NUMHEAD35 extends AbstractConfluencePluginWebTestCase {

	@Test
    public void testIsEmpty() {
        gotoPage("/display/nh/NUMHEAD-35");
        assertTextPresent("0. heading h1");
        assertTextNotPresent("a. heading h1");
    }
}

