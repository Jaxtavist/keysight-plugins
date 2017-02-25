package nl.avisi.numbered.headings.macro;

public class NumberedHeadingsMacroForPage extends NumberedHeadingsMacro {

    public NumberedHeadingsMacroForPage(){
    }

    @Override
    public BodyType getBodyType() {
        return BodyType.NONE;
    }

    @Override
    public boolean hasBody()
    {
        return false;
    }
}
