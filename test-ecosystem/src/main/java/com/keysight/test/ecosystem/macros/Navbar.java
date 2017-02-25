package com.keysight.test.ecosystem.macros;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.velocity.htmlsafe.HtmlFragment;
import com.atlassian.renderer.RenderContext;
import com.atlassian.confluence.setup.settings.SettingsManager;

public class Navbar implements Macro
{
    private final static String BASE_URL_KEY = "baseurl";
    private final VelocityHelperService velocityHelperService;
    private final SettingsManager       settingsManager;
   
    public Navbar(SettingsManager settingsManager,
		  VelocityHelperService velocityHelperService)
    {
        this.settingsManager = settingsManager;
        this.velocityHelperService = velocityHelperService;
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context)
            throws MacroExecutionException
    {
        String baseUrl = settingsManager.getGlobalSettings().getBaseUrl();
        Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();
        String template = "/com/keysight/test-ecosystem/templates/navbar.vm";

	velocityContext.put( BASE_URL_KEY, settingsManager.getGlobalSettings().getBaseUrl() );

        return velocityHelperService.getRenderedTemplate(template, velocityContext);
    }
    
    @Override
    public BodyType getBodyType()
    {
        return BodyType.NONE;
    }

    @Override
    public OutputType getOutputType()
    {
        return OutputType.BLOCK;
    }
}
