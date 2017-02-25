package nl.avisi.numbered.headings.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.renderer.v2.macro.BaseMacro;

import java.util.Map;

public class NumberedHeadingsMacro extends BaseMacro implements Macro {

    public NumberedHeadingsMacro(){
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext conversionContext) throws MacroExecutionException {
        return body;
    }

    @Override
    public BodyType getBodyType() {
        return BodyType.RICH_TEXT;
    }

    @Override
    public OutputType getOutputType() {
        return OutputType.BLOCK;
    }

    public boolean hasBody()
    {
        return true;
    }

    public RenderMode getBodyRenderMode()
    {
        return RenderMode.ALL;
    }

    public String execute(Map params, String body, RenderContext renderContext) throws MacroException
    {
        try
        {
            return execute(params, body, (ConversionContext) null);
        }
        catch(MacroExecutionException e)
        {
            throw new MacroException(e);
        }
    }
}
