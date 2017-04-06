package com.keysight.keysight.theme.helpers;

import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;

public class HasChildrenCondition extends BaseConfluenceCondition
{
   private PageManager pageManager;

   @Override
   public boolean shouldDisplay(WebInterfaceContext context)
   {
      boolean bFlag = false;
      AbstractPage abstractPage = context.getPage();
      Page page = pageManager.getPage( abstractPage.getId() );
      if( page != null && page.getDescendants().size() > 0 ){ 
         bFlag = true;
      }

      return bFlag;
   }

   public void setPageManager(PageManager pageManager) {
      this.pageManager = pageManager;
   }

}
