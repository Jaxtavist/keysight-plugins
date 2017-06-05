package com.keysight.mathjax.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.renderer.v2.components.HtmlEscaper;

import java.util.Map;

public class MathJaxBlock implements Macro {
    public final static String EQUATION = "equation";
    public final static String BODY_AS_HTML = "bodyAsHtml";
    protected final VelocityHelperService velocityHelperService;
    protected final XhtmlContent xhtmlUtils;

    public MathJaxBlock(VelocityHelperService velocityHelperService,
                        XhtmlContent xhtmlUtils) {
        this.velocityHelperService = velocityHelperService;
        this.xhtmlUtils = xhtmlUtils;
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context)
            throws MacroExecutionException {
        String template = "/com/keysight/mathjax/templates/mathjax-block.vm";
        Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();

        if (body.length() == 0) {
            body = "No equation provided";
        } else {
            body = "\\[" + HtmlEscaper.escapeAll(body, true) + "\\]";
        }

        velocityContext.put(BODY_AS_HTML, body);

        return velocityHelperService.getRenderedTemplate(template, velocityContext);
    }

    @Override
    public BodyType getBodyType() {
        return BodyType.PLAIN_TEXT;
    }

    @Override
    public OutputType getOutputType() {
        return OutputType.BLOCK;
    }
}
