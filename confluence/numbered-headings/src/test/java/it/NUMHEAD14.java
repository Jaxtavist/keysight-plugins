package it;

import com.atlassian.confluence.plugin.functest.AbstractConfluencePluginWebTestCase;

import org.junit.Ignore;
import org.junit.Test;

public class NUMHEAD14 extends AbstractConfluencePluginWebTestCase {

	@Test
    public void test() {
        gotoPage("/display/nh/NUMHEAD-14");
        assertElementPresentByXPath("//div[@class='wiki-content']//h1[1]/span[starts-with(text(),'1')]");
        assertElementPresentByXPath("//div[@class='wiki-content']//h2[1]/span[starts-with(text(),'1.1')]");
        assertElementPresentByXPath("//div[@class='wiki-content']//div[@class='sectionMacro']//h1/span[starts-with(text(),'2')]");
        assertElementPresentByXPath("//div[@class='wiki-content']//div[@class='sectionMacro']//h2/span[starts-with(text(),'2.1')]");
        assertElementPresentByXPath("//div[@class='wiki-content']//div[@class='sectionMacro']//h1/span[starts-with(text(),'3')]");
        assertElementPresentByXPath("//div[@class='wiki-content']//div[@class='sectionMacro']//h2/span[starts-with(text(),'3.1')]");
        assertElementPresentByXPath("//div[@class='wiki-content']//h1[2]/span[starts-with(text(),'4')]");
        assertElementPresentByXPath("//div[@class='wiki-content']//h2[2]/span[starts-with(text(),'4.1')]");
    }
	
}

