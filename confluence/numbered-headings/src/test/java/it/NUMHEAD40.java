package it;

import com.atlassian.confluence.plugin.functest.AbstractConfluencePluginWebTestCase;

import org.junit.Ignore;
import org.junit.Test;

public class NUMHEAD40 extends AbstractConfluencePluginWebTestCase {

	@Test
    public void test() {
        gotoPage("/display/nh/NUMHEAD-40");
        assertTextPresent("heading h1");
        assertMatchInElement("NUMHEAD-40-headingh1", "^heading h1");
    }
}

