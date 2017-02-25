package com.keysight.keysight.theme.events;

import org.springframework.beans.factory.DisposableBean;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;

import com.atlassian.confluence.event.events.content.page.PageViewEvent;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.spaces.Space;

import com.keysight.keysight.theme.helpers.KeysightPageStateConstants;

import java.util.List;
import java.util.ArrayList;

public class AddHomePageLabelsOnPageEvent implements DisposableBean
{
   private final LabelManager labelManager;
   protected EventPublisher eventPublisher;
   
   public static final String ACTIVATION_KEY  = "test-ecosystem";


   public AddHomePageLabelsOnPageEvent( EventPublisher eventPublisher,
                             LabelManager labelManager ){
       
      this.eventPublisher = eventPublisher;
      this.labelManager = labelManager;
       
      eventPublisher.register(this);
   }

   @EventListener
   public void addHomePageLabels( PageViewEvent event ){
      addHomePageLabels( event.getPage() );
   }
    
   private void addHomePageLabels( Page page ){
      Space currentSpace    = page.getSpace();
      
      if( !isActive( currentSpace ) ){ return; };

      ArrayList<Label> labelsToAdd = getLabelsToAdd( page, currentSpace );
      for( Label newLabel : labelsToAdd ){
         labelManager.addLabel( page, newLabel );
      } 
   }
   
   private boolean isActive( Space space ){
      boolean isActive = false;
      List<Label> spaceCategories = space.getDescription().getLabels();
      
      for( Label currentLabel : spaceCategories ){
         if( currentLabel.getName().equals( ACTIVATION_KEY ) ){
            isActive = true;
            break;
         }
      }

      return isActive;
   }

   public ArrayList<Label> getLabelsToAdd( Page page, Space space ){
      List<Label> currentPageLabels = page.getLabels();       
      Page        spaceHomePage     = space.getHomePage();
      List<Label> homePageLabels    = spaceHomePage.getLabels();       
      
      ArrayList<Label> labelsToAdd = new ArrayList<Label>();

      for( Label currentLabel : homePageLabels ){
	 //System.out.println( "Testing " + currentLabel.toString() + " | " + currentLabel.getName() );
         if( !(
	      isCurrentPageLabel( page, currentLabel )
	   || currentLabel.toString().matches( "my:.*" ) 
           || currentLabel.toString().equals( KeysightPageStateConstants.PAGE_STATE_ACTIVATION_KEY )
           || KeysightPageStateConstants.isPageStateLabel( currentLabel.getName() )
	   )){
	    //System.out.println( "Adding " + currentLabel.toString() );
            labelsToAdd.add( currentLabel );
         }
      } 

      return( labelsToAdd );
   }

   public boolean isCurrentPageLabel( Page page, Label label ){
      boolean bFlag = false;
      List<Label> currentPageLabels = page.getLabels();       
      for( Label currentLabel : currentPageLabels ){
         if( currentLabel.getName().equals( label.getName() ) ){
            bFlag = true;
         }
      }
      return bFlag;
   }
   
   public void destroy() throws Exception {
      eventPublisher.unregister(this);
   }
}


