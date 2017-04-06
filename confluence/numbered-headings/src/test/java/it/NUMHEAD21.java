package it;

import com.atlassian.confluence.plugin.functest.AbstractConfluencePluginWebTestCase;

import org.junit.Ignore;
import org.junit.Test;

public class NUMHEAD21 extends AbstractConfluencePluginWebTestCase {

	@Test
    public void test() {
        gotoPage("/display/nh/NUMHEAD-21");
        assertElementPresentByXPath("//div[@class='wiki-content']/h1[1 and @id='NUMHEAD-21-headingh1a']/span[starts-with(text(),'\u03C1')]");
        assertElementPresentByXPath("//div[@class='wiki-content']/h1[2 and @id='NUMHEAD-21-headingh1b']/span[starts-with(text(),'\u03C3')]");
        assertElementPresentByXPath("//div[@class='wiki-content']/h1[3 and @id='NUMHEAD-21-headingh1c']/span[starts-with(text(),'\u03C4')]");
    }
}

