package nl.avisi.numbered.headings.format;

import org.junit.Before;

public abstract class AbstractNumberingFormatTest {

    protected int[] breadcrumbs;

    @Before
    public void setUp() throws Exception {
        breadcrumbs = new int[35];

        for (int i = 1; i < 30; i++) {
            breadcrumbs[i - 1] = i;
        }

        breadcrumbs[29] = 53;
        breadcrumbs[30] = 54;
        breadcrumbs[31] = 316;
        breadcrumbs[32] = 1024;
        breadcrumbs[33] = 999999;
    }
}
