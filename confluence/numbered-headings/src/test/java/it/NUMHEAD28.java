package it;

import com.atlassian.confluence.plugin.functest.AbstractConfluencePluginWebTestCase;

import org.junit.Ignore;
import org.junit.Test;

public class NUMHEAD28 extends AbstractConfluencePluginWebTestCase {

	@Test
    public void test() {
        gotoPage("/display/nh/NUMHEAD-28");
        assertTextPresent("1. heading h1");
        assertTextPresent("1.1. heading h2");
        assertTextPresent("1.1.1.1.1. And then suddenly... h5");
    }
}

