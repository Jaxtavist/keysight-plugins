package com.keysight.keysight.theme.helpers;

import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.labels.Label;

public class CanActivatePageStateCondition extends BaseConfluenceCondition
{
   @Override
   public boolean shouldDisplay(WebInterfaceContext context)
   {
      boolean bFlag = true;
      try{
         for( Label label : context.getPage().getSpace().getDescription().getLabels() ){
            if( label.toString().equals( KeysightPageStateConstants.PAGE_STATE_ACTIVATION_KEY ) ){
               bFlag = false;
               break;
            }
         }

         if( bFlag ){
            for( Label label : context.getPage().getLabels() ){
               if( label.toString().equals( KeysightPageStateConstants.PAGE_STATE_ACTIVATION_KEY ) ){
                  bFlag = false;
                  break;
               }
            }
         }
      } catch( Exception e ){
         bFlag = false;
      }

      return bFlag;
   }
}
