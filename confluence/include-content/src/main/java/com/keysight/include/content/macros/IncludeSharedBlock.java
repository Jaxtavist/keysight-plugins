package com.keysight.include.content.macros;

import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.confluence.security.PermissionManager;

public class IncludeSharedBlock extends IncludeSharedBlockWithReplacement
{
    public IncludeSharedBlock( PageManager pageManager,
                               PermissionManager permissionManager,
                               Renderer renderer,
                               XhtmlContent xhtmlUtils
                             ){
        super( pageManager, permissionManager, renderer, xhtmlUtils );
    }

    @Override
    public BodyType getBodyType()
    {
        return BodyType.NONE;
    }
}
