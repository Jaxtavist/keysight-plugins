package nl.avisi.numbered.headings;

import com.atlassian.plugin.module.ModuleFactory;

import nl.avisi.numbered.headings.format.DecimalNumberingFormat;
import nl.avisi.numbered.headings.format.NumberingFormat;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NumberFormatterDescriptorTest {

    @Test
    public void shouldDelegateModuleCreationToModuleFactory() throws Exception {
        ModuleFactory mockedModuleFactory = mock(ModuleFactory.class);
        NumberFormatterDescriptor numberFormatterDescriptor = new NumberFormatterDescriptor(mockedModuleFactory);

        NumberingFormat decimalNumberingFormat = new DecimalNumberingFormat();
        when(mockedModuleFactory.createModule(anyString(), eq(numberFormatterDescriptor))).thenReturn(decimalNumberingFormat);

        assertEquals(decimalNumberingFormat, numberFormatterDescriptor.getModule());
    }
}
