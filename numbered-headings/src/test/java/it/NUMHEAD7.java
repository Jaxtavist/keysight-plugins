package it;

import com.atlassian.confluence.plugin.functest.AbstractConfluencePluginWebTestCase;

import org.junit.Ignore;
import org.junit.Test;

public class NUMHEAD7 extends AbstractConfluencePluginWebTestCase {

	@Test
    public void test() {
        gotoPage("/display/nh/NUMHEAD-7");
        assertElementNotPresentByXPath("//div[@class='wiki-content']//h1");
        assertElementPresentByXPath("//div[@class='wiki-content']/h2/span[starts-with(text(),'1.')]");
        assertElementPresentByXPath("//div[@class='wiki-content']/h3/span[starts-with(text(),'1.1.')]");
    }
	
}

