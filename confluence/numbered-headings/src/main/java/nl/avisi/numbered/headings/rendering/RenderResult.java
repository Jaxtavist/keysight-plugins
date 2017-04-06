package nl.avisi.numbered.headings.rendering;

public class RenderResult {

    private final String result;
    private final int[] endingNumbers;

    public RenderResult(final String result, final int[] endingNumbers) {
        this.result = result;
        this.endingNumbers = endingNumbers;
    }

    public String getResult() {
        return result;
    }

    public int[] getEndingNumbers() {
        return endingNumbers.clone();
    }
}
