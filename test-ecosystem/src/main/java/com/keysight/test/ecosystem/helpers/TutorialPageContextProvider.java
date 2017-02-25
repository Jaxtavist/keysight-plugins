package com.keysight.test.ecosystem.helpers;

import java.util.Date;

import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;

public class TutorialPageContextProvider extends AbstractBlueprintContextProvider
{
    private static final String BLUEPRINT_LABEL = "test-ecosystem-tutorial";
    public TutorialPageContextProvider( )
    {
    }

    @Override
    protected BlueprintContext updateBlueprintContext(BlueprintContext context)
    {
        String spaceKey     = context.getSpaceKey();
        String contentByLabel = "<ac:structured-macro ac:name=\"contentbylabel\">"
                               +"   <ac:parameter ac:name=\"spaces\"><ri:space ri:space-key=\""+spaceKey+"\" /></ac:parameter>"
                               +"   <ac:parameter ac:name=\"max\">5</ac:parameter>"
                               +"   <ac:parameter ac:name=\"labels\">"+BLUEPRINT_LABEL+"</ac:parameter>"
                               +"   <ac:parameter ac:name=\"type\">page</ac:parameter>"
                               +"</ac:structured-macro>";

        context.put("contentByLabelMacro", contentByLabel ); 
        return context;
    }
}
