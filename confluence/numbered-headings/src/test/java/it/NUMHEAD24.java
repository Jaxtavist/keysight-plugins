package it;

import com.atlassian.confluence.plugin.functest.AbstractConfluencePluginWebTestCase;

import org.junit.Ignore;
import org.junit.Test;

public class NUMHEAD24 extends AbstractConfluencePluginWebTestCase {

	@Test
    public void test() {
        gotoPage("/display/nh/NUMHEAD-24");
        assertElementPresentByXPath("//div[@class='wiki-content']/div/ul/li/a[@href='#NUMHEAD-24-heading1']");
        assertElementPresentByXPath("//div[@class='wiki-content']/h1[@id='NUMHEAD-24-heading1']");
    }
}

