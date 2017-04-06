package nl.avisi.numbered.headings.format;

import nl.avisi.numbered.headings.exception.NumberedHeadingsException;

import org.junit.Test;

public class CustomNumberingFormatTest {

    @Test(expected = NumberedHeadingsException.class)
    public void shouldThrowExceptionWhenFormatMethodIsInvoked() {
        new CustomNumberingFormat().format(new int[0]);
    }
}
