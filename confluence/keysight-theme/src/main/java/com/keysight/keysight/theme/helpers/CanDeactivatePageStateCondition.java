package com.keysight.keysight.theme.helpers;

import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.labels.Label;

public class CanDeactivatePageStateCondition extends BaseConfluenceCondition
{
   @Override
   public boolean shouldDisplay(WebInterfaceContext context)
   {
      boolean bFlag = false;
      
      try{
         for( Label label : context.getPage().getLabels() ){
            if( label.toString().equals( KeysightPageStateConstants.PAGE_STATE_ACTIVATION_KEY ) ){
               bFlag = true;
               break;
            }
         }

         for( Label label : context.getPage().getSpace().getDescription().getLabels() ){
            if( label.toString().equals( KeysightPageStateConstants.PAGE_STATE_ACTIVATION_KEY ) ){
               bFlag = false;
               break;
            }
         }
      } catch( Exception e ){}

      return bFlag;
   }
}
