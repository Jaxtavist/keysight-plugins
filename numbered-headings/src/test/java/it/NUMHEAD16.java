package it;

import com.atlassian.confluence.plugin.functest.AbstractConfluencePluginWebTestCase;

import org.junit.Ignore;
import org.junit.Test;

public class NUMHEAD16 extends AbstractConfluencePluginWebTestCase {

	@Test
    public void test() {
        gotoPage("/display/nh/NUMHEAD-16");
        assertElementPresentByXPath("//div[@class='wiki-content']/h1[1]/span[starts-with(text(),'1')]");
        assertElementPresentByXPath("//div[@class='wiki-content']/h2[1]/span[starts-with(text(),'(a)')]");
        assertElementPresentByXPath("//div[@class='wiki-content']/h3[1]/span[starts-with(text(),'(1)')]");
        assertElementPresentByXPath("//div[@class='wiki-content']/h4[1]/span[starts-with(text(),'(A)')]");
        assertElementPresentByXPath("//div[@class='wiki-content']/h5[1]/span[starts-with(text(),'(i)')]");
    }
}

