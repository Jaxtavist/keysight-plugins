package com.keysight.mathjax.helpers;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;

/**
 * This class is used to replace <component-import /> declarations in the atlassian-plugin.xml.
 * This class will be scanned by the atlassian spring scanner at compile time.
 * There are no situations this class needs to be instantiated, it's sole purpose
 * is to collect the component-imports in the one place.
 * <p>
 * The list of components that may be imported can be found at: <your-confluence-url>/admin/pluginexports.action
 */

@SuppressWarnings("UnusedDeclaration")
@Scanned
public class ComponentImports {
    @ComponentImport
    com.atlassian.confluence.setup.settings.SettingsManager settingsManager;
    @ComponentImport
    com.atlassian.confluence.plugin.services.VelocityHelperService velocityHelperService;
    @ComponentImport
    com.atlassian.confluence.xhtml.api.XhtmlContent xhtmlUtils;

    private ComponentImports() {
        throw new Error("This class should not be instantiated");
    }
}
