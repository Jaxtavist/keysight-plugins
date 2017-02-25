package nl.avisi.numbered.headings.macro.parameters;

import nl.avisi.numbered.headings.Heading;
import nl.avisi.numbered.headings.format.NumberingFormat;

import java.util.Arrays;
import java.util.List;

import static nl.avisi.numbered.headings.Heading.NUMBER_OF_HEADINGS;

public final class NumberedHeadingsParameters {

    private int[] startingNumbers = new int[NUMBER_OF_HEADINGS];
    private int[] skipHeadings = new int[NUMBER_OF_HEADINGS];
    private Heading startNumberingAt;
    private NumberingFormat numberingFormat;
    private List<List<NumberingFormat>> numberFormatters;

    public Heading getStartNumberingAt() {
        return startNumberingAt;
    }

    public void setStartNumberingAt(Heading startNumberingAt) {
        this.startNumberingAt = startNumberingAt;
    }

    public int[] getStartingNumbers() {
        return Arrays.copyOf(startingNumbers, startingNumbers.length);
    }

    public int[] getSkipHeadings() {
        return Arrays.copyOf(skipHeadings, skipHeadings.length);
    }

    public void setSkipHeadings(int[] skipHeadings) {
        this.skipHeadings = skipHeadings;
    }

    public NumberingFormat getNumberingFormat() {
        return numberingFormat;
    }

    public void setNumberingFormat(NumberingFormat numberingFormat) {
        this.numberingFormat = numberingFormat;
    }

    public List<List<NumberingFormat>> getNumberFormatters() {
        return numberFormatters;
    }

    public void setNumberFormatters(List<List<NumberingFormat>> numberFormatters) {
        this.numberFormatters = numberFormatters;
    }

    public void setStartingNumbers(int[] startingNumbers) {
        this.startingNumbers = startingNumbers;
    }
}
