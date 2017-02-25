package com.keysight.keysight.theme.helpers;

import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.user.ConfluenceUser;

public class CanEditLabelsCondition extends BaseConfluenceCondition
{
    private PermissionManager permissionManager;

    @Override
    public boolean shouldDisplay(WebInterfaceContext context) {
        boolean bFlag = false;
        try {
           Object page = context.getPage();
           ConfluenceUser user = context.getCurrentUser();

           if( page != null ){
              bFlag = permissionManager.hasPermission(user, Permission.EDIT, (AbstractPage) page);
           }
        } catch( Exception e ){}

        return bFlag;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }
}

