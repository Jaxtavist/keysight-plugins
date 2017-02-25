package it;

import com.atlassian.confluence.plugin.functest.AbstractConfluencePluginWebTestCase;

import org.junit.Ignore;
import org.junit.Test;

public class NUMHEAD1 extends AbstractConfluencePluginWebTestCase {

	@Test
    public void test() {
        gotoPage("/display/nh/NUMHEAD-1");
        assertElementPresentByXPath("//div[@class='wiki-content']/h1[1 and @id='NUMHEAD-1-Includingpageh1']/span[starts-with(text(),'1.')]");
        assertElementPresentByXPath("//div[@class='wiki-content']/h1[2 and @id='NUMHEAD-1-Includedpageheaderh1']/span[starts-with(text(),'2.')]");
        assertElementPresentByXPath("//div[@class='wiki-content']/h1[3 and @id='NUMHEAD-1-Includedpageheaderh1.1']/span[starts-with(text(),'3.')]");
        assertElementPresentByXPath("//div[@class='wiki-content']/h1[4 and @id='NUMHEAD-1-Includingpageh1.1']/span[starts-with(text(),'4.')]");
        assertElementPresentByXPath("//div[@class='wiki-content']/h2[1 and @id='NUMHEAD-1-Includedpageheaderh2']/span[starts-with(text(),'3.1')]");
    }
}

