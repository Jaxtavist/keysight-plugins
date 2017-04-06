package com.keysight.test.ecosystem.events;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;

import com.atlassian.confluence.plugins.createcontent.api.events.BlueprintPageCreateEvent;
import com.atlassian.plugin.ModuleCompleteKey;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

@Component
public class BasicPageEventListener implements DisposableBean
{
    private static final String GROUP_ID    = "com.keysight";
    private static final String ARTIFACT_ID = "test-ecosystem";
    private static final String MODULE      = "test-ecosystem-basic-page-blueprint";
    private static final ModuleCompleteKey BLUEPRINT_KEY = new ModuleCompleteKey( GROUP_ID + "." + ARTIFACT_ID, MODULE );
    
    private final EventPublisher eventPublisher;

    @Autowired
    public BasicPageEventListener(EventPublisher eventPublisher)
    {
        this.eventPublisher         = eventPublisher;

        eventPublisher.register(this);
    }


    @EventListener
    public void onPageCreateEvent(BlueprintPageCreateEvent event) {

        ModuleCompleteKey moduleCompleteKey = event.getBlueprintKey();
        if( !BLUEPRINT_KEY.equals( event.getBlueprintKey() ) ){
           return;
        }
	
        //System.out.println( event.getBlueprint().toString() );
        //System.out.println( event.getContext().toString() );
        //System.out.println( event.getPage().toString() );
    }

    @Override
    public void destroy() throws Exception
    {
        eventPublisher.unregister(this);
    }
}
