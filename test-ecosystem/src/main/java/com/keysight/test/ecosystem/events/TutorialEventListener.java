package com.keysight.test.ecosystem.events;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.createcontent.api.events.BlueprintPageCreateEvent;
import com.atlassian.confluence.event.events.content.page.PageCreateEvent;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.plugin.ModuleCompleteKey;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

import static com.atlassian.confluence.labels.LabelManager.NO_MAX_RESULTS;
import static com.atlassian.confluence.labels.LabelManager.NO_OFFSET;

@Component
public class TutorialEventListener implements DisposableBean
{
    private static final String GROUP_ID    = "com.keysight";
    private static final String ARTIFACT_ID = "test-ecosystem";
    private static final String MODULE      = "test-ecosystem-tutorial-blueprint";
    private static final ModuleCompleteKey BLUEPRINT_KEY = new ModuleCompleteKey( GROUP_ID + "." + ARTIFACT_ID, MODULE );
    
    private static final String PAGE_LABEL  = "test-ecosystem-tutorial";
    private static final String INDEX_LABEL = PAGE_LABEL + "-index";
    
    private final EventPublisher eventPublisher;
    private final LabelManager labelManager;
    private final PageManager pageManager;

    @Autowired
    public TutorialEventListener(EventPublisher eventPublisher,
                                 LabelManager labelManager,
                                 PageManager pageManager)
    {
        this.eventPublisher = eventPublisher;
        this.labelManager = labelManager;
        this.pageManager = pageManager;

        eventPublisher.register(this);
    }
 
    @EventListener
    public void labelBlueprintIndexPage( PageCreateEvent event ){
        Page page = event.getPage();
        
        if( page.getTitle().equals( "Tutorials" ) 
            && page.getBodyAsString().matches( ".*detailssummary.*" )
        ){
           labelManager.addLabel( page, new Label( INDEX_LABEL ) );
        }
    }


    @EventListener
    public void onPageCreateEvent(BlueprintPageCreateEvent event) {
        ModuleCompleteKey moduleCompleteKey = event.getBlueprintKey();
        if( !BLUEPRINT_KEY.equals( event.getBlueprintKey() ) ){
           return;
        }

        Page page = event.getPage();
        if( !bDescendantOfIndexPage( page ) ){
           relocatePage( page );
        }
    }

    private boolean bDescendantOfIndexPage( Page page ){
       boolean bFlag = false;
       for( Page ancestor : page.getAncestors() ){
          for( Label ancestorLabels : ancestor.getLabels() ){
             if( ancestorLabels.getName().equals( INDEX_LABEL ) ){
                bFlag = true;
                break;
             }
          }
          if( bFlag ){ break; }
       }
       return( bFlag );       
    }
    
    private void relocatePage( Page page ){
       Page indexPage = null;
       Label indexPageLabel = labelManager.getLabel( INDEX_LABEL );

       ArrayList<ContentEntityObject> indexPages = new ArrayList( labelManager.getContentInSpaceForLabel( NO_OFFSET, 
                                                                                                          NO_MAX_RESULTS, 
                                                                                                          page.getSpaceKey(), 
                                                                                                          indexPageLabel ).getList() );
       if( indexPages.size() > 0 && indexPages.get(0) instanceof Page ){
           page.setParent( (Page) indexPages.get(0) );
       }

       return;
    }

    @Override
    public void destroy() throws Exception
    {
        eventPublisher.unregister(this);
    }
}
