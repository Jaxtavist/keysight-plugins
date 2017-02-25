package nl.avisi.numbered.headings.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.MacroExecutionException;
import java.util.Map;

public class TransitionalNumberedHeadingsMacro extends NumberedHeadingsMacro {

    public TransitionalNumberedHeadingsMacro(){
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext conversionContext) throws MacroExecutionException {
        return "<div class=\"insert-numbering-into-storage-format\"></div>" + body;
    }
}
