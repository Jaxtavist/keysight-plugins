package com.keysight.glossary.helpers;

import java.util.Date;

import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;

public class GlossaryTermContextProvider extends AbstractBlueprintContextProvider
{
    public GlossaryTermContextProvider( )
    {
    }

    @Override
    protected BlueprintContext updateBlueprintContext(BlueprintContext context)
    {
        return context;
    }
}
