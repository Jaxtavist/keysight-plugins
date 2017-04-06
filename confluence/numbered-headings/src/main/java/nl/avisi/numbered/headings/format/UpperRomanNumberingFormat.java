package nl.avisi.numbered.headings.format;

/**
 * A numbering format that renders headings using the Roman numerals in uppercase.
 *
 * Example:
 * <pre>
 *  I.      Heading 1
 *  I.I.    Heading 1.1
 *  I.II.    Heading 1.2
 *  II.      Heading 2
 * </pre>
 */
public class UpperRomanNumberingFormat extends AbstractFormat {

    enum Numeral {
        I(1), IV(4), V(5), IX(9), X(10), XL(40), L(50), XC(90), C(100), CD(400), D(500), CM(900), M(1000);

        private final int weight;

        private Numeral(final int weight) {
            this.weight = weight;
        }

        private int getWeight() {
            return weight;
        }
    }

    @Override
    public String format(int[] breadCrumbs, Integer level) {
        Integer numberToFormat = breadCrumbs[level - 1];

        StringBuilder romanNumeral = new StringBuilder();

        final Numeral[] values = Numeral.values();
        for (int i = values.length - 1; i >= 0; i--) {
            while (numberToFormat >= values[i].getWeight()) {
                romanNumeral.append(values[i]);
                numberToFormat -= values[i].getWeight();
            }
        }

        return romanNumeral.toString();
    }
}
