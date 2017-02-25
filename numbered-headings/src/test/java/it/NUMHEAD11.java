package it;

import com.atlassian.confluence.plugin.functest.AbstractConfluencePluginWebTestCase;

import org.junit.Ignore;
import org.junit.Test;

public class NUMHEAD11 extends AbstractConfluencePluginWebTestCase {

	@Test
    public void test() {
        gotoPage("/display/nh/NUMHEAD-11");
        assertElementPresentByXPath("//div[@class='wiki-content']//h1[1]/span[starts-with(text(),'1')]");
        assertElementPresentByXPath("//div[@class='wiki-content']//h1[2]/span[starts-with(text(),'2')]");
        assertElementPresentByXPath("//div[@class='wiki-content']//table/tbody/tr/th[text()='column1']");
        assertElementPresentByXPath("//div[@class='wiki-content']//table/tbody/tr/th[text()='column1']");
        assertElementPresentByXPath("//div[@class='wiki-content']//table/tbody/tr/th[text()='column1']");
        assertElementPresentByXPath("//div[@class='wiki-content']//table/tbody/tr/td[text()='content a 1']");
        assertElementPresentByXPath("//div[@class='wiki-content']//table/tbody/tr/td[text()='content a 2']");
        assertElementPresentByXPath("//div[@class='wiki-content']//table/tbody/tr/td[text()='content a 3']");
    }
	
}

