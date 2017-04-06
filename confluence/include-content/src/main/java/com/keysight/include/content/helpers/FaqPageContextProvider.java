package com.keysight.include.content.helpers;

import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;

public class FaqPageContextProvider extends AbstractBlueprintContextProvider
{
    public FaqPageContextProvider( )
    {
    }

    @Override
    protected BlueprintContext updateBlueprintContext(BlueprintContext context)
    {
        //context.setTitle(pageTitle);
        //context.put("author", author );

        return context;
    }
}
