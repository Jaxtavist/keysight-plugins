package it;

import com.atlassian.confluence.plugin.functest.AbstractConfluencePluginWebTestCase;

import org.junit.Ignore;
import org.junit.Test;

public class NUMHEAD17 extends AbstractConfluencePluginWebTestCase {

	@Test
    public void test() {
        gotoPage("/display/nh/NUMHEAD-17");
        assertElementPresentByXPath("//div[@class='wiki-content']/h1[1]/span[starts-with(text(),'1')]");
        assertElementPresentByXPath("//div[@class='wiki-content']/h1[2]/span[starts-with(text(),'4')]");
        
        assertElementPresentByXPath("//div[@class='wiki-content']/h2[1]/span[starts-with(text(),'1.1')]");
        assertElementPresentByXPath("//div[@class='wiki-content']/h2[2]/span[starts-with(text(),'4.1')]");
    }
}

