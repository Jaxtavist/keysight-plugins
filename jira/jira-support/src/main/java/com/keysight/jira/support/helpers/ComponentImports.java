package com.keysight.jira.support.helpers;
 
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
 
/**
 * This class is used to replace <component-import /> declarations in the atlassian-plugin.xml.
 * This class will be scanned by the atlassian spring scanner at compile time.
 * There are no situations this class needs to be instantiated, it's sole purpose 
 * is to collect the component-imports in the one place.
 *
 * The list of components that may be imported can be found at: <your-confluence-url>/admin/pluginexports.action
 */

@SuppressWarnings("UnusedDeclaration")
@Scanned
public class ComponentImports
{
    @ComponentImport com.atlassian.crowd.manager.directory.DirectoryManager         directoryManager;
    @ComponentImport com.atlassian.templaterenderer.TemplateRenderer                templateRenderer;
    @ComponentImport com.atlassian.sal.api.auth.LoginUriProvider                    loginUriProvider;
    @ComponentImport com.atlassian.sal.api.user.UserManager                         userManager;
    @ComponentImport com.atlassian.jira.bc.user.search.UserSearchService            userSearchService;
    @ComponentImport com.atlassian.jira.bc.security.login.LoginService              loginService;
    @ComponentImport com.atlassian.jira.security.GlobalPermissionManager            globalPermissionManager;
    @ComponentImport com.atlassian.jira.application.ApplicationAuthorizationService applicationAuthorizationService;

    private ComponentImports()
    {
        throw new Error("This class should not be instantiated");
    }
}
