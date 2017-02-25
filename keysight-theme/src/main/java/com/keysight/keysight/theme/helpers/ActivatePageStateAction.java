package com.keysight.keysight.theme.helpers;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.actions.PageAware;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;

public class ActivatePageStateAction extends ConfluenceActionSupport implements PageAware {

   private AbstractPage page;
   private LabelManager labelManager;
   
   public ActivatePageStateAction( LabelManager labelManager) {
      this.labelManager = labelManager;
   }

   public String execute()
   {
      Label label = new Label( KeysightPageStateConstants.PAGE_STATE_ACTIVATION_KEY );
      labelManager.addLabel( page, label );
      return "success";
   }

   @Override
   public AbstractPage getPage() {
      return page;
   }

   @Override
   public boolean isLatestVersionRequired() {
      return true;
   }


   @Override
   public boolean isPageRequired() {
      return true;
   }

   @Override
   public boolean isViewPermissionRequired() {
      return true;
   }


   @Override
   public void setPage(AbstractPage page) {
      this.page = page;
   }
}
