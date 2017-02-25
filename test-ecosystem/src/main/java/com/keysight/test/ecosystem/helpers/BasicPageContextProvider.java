package com.keysight.test.ecosystem.helpers;

import java.util.Date;

import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;

public class BasicPageContextProvider extends AbstractBlueprintContextProvider
{
    public BasicPageContextProvider( )
    {
    }

    @Override
    protected BlueprintContext updateBlueprintContext(BlueprintContext context)
    {
        return context;
    }
}
