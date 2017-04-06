package nl.avisi.numbered.headings;

import com.google.common.collect.Lists;

import org.junit.Test;

import javax.ws.rs.core.Response;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NumberedHeadingsRestResourceTest {

    @Test
    public void shouldReturnAllEnabledFormatters() {
        NumberFormatterManager numberFormatterManager = mock(NumberFormatterManager.class);
        when(numberFormatterManager.getNumberFormatterDescriptorNames()).thenReturn(Lists.newArrayList("decimal"));

        NumberedHeadingsRestResource numberedHeadingsRestResource = new NumberedHeadingsRestResource(numberFormatterManager);

        Response restResponse = numberedHeadingsRestResource.getEnabledFormatters();
        List<String> enabledFormatterNames = (List<String>) restResponse.getEntity();

        assertEquals(1, enabledFormatterNames.size());
        assertEquals("decimal", enabledFormatterNames.get(0));
    }
}
