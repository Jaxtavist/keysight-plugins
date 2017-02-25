package nl.avisi.numbered.headings;

import com.atlassian.plugin.PluginAccessor;

import nl.avisi.numbered.headings.format.DecimalNumberingFormat;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NumberingFormatManagerTest {

    private NumberFormatterManager numberFormatManager;

    @Before
    public void before() throws Exception {
        PluginAccessor pluginAccessor = mock(PluginAccessor.class);
        NumberFormatterManager numberFormatManager = new NumberFormatterManager(pluginAccessor);

        NumberFormatterDescriptor numberFormatterDescriptor = mock(NumberFormatterDescriptor.class);
        when(numberFormatterDescriptor.getName()).thenReturn("decimal");
        when(numberFormatterDescriptor.getModule()).thenReturn(new DecimalNumberingFormat());


        when(pluginAccessor.getEnabledModuleDescriptorsByClass(NumberFormatterDescriptor.class)).thenReturn(Arrays.asList(numberFormatterDescriptor));

        this.numberFormatManager = numberFormatManager;
    }

    @Test
    public void shouldReturnDecimalNumberingFormatWhenNoNumberingFormatterCouldBeFound() throws Exception {
        assertTrue(numberFormatManager.fromString("FOO") instanceof DecimalNumberingFormat);
    }

    @Test
    public void shouldReturnCorrectNumberingFormat() throws Exception {
        assertTrue(numberFormatManager.fromString("decimal") instanceof DecimalNumberingFormat);
    }

    @Test
    public void shouldReturnNumberFormatterDescriptorNames() throws Exception {
        List<String> numberFormatterDescriptorNames = numberFormatManager.getNumberFormatterDescriptorNames();

        assertEquals(1, numberFormatterDescriptorNames.size());
        assertEquals("decimal", numberFormatterDescriptorNames.get(0));
    }
}
