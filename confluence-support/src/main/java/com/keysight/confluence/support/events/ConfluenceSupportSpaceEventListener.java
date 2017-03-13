package com.keysight.confluence.support.space;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.confluence.plugins.createcontent.api.events.SpaceBlueprintCreateEvent;
import com.atlassian.confluence.plugins.createcontent.api.events.SpaceBlueprintHomePageCreateEvent;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.renderer.v2.components.HtmlEscaper;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.atlassian.confluence.mail.template.ConfluenceMailQueueItem;
import com.atlassian.core.task.MultiQueueTaskManager;
import com.atlassian.mail.queue.MailQueueItem;
import static com.atlassian.confluence.mail.template.ConfluenceMailQueueItem.MIME_TYPE_HTML;
import static com.atlassian.confluence.mail.template.ConfluenceMailQueueItem.MIME_TYPE_TEXT;
import com.keysight.confluence.support.helpers.MailService;
import com.keysight.confluence.support.helpers.MailServiceImpl;

@Component
public class ConfluenceSupportSpaceEventListener implements DisposableBean
{
    private final EventPublisher eventPublisher;
    private final SettingsManager settingsManager;
    private final SpaceManager spaceManager;
    private final UserAccessor userAccessor;
    private final MailService mailService;
    private final MultiQueueTaskManager taskManager;

    @Autowired
    public ConfluenceSupportSpaceEventListener(EventPublisher eventPublisher,
                               MultiQueueTaskManager taskManager,
                           SettingsManager settingsManager,
                                               SpaceManager spaceManager,
                                               UserAccessor userAccessor )
    {
        this.eventPublisher  = eventPublisher;
        this.taskManager     = taskManager;
        this.settingsManager = settingsManager;
        this.spaceManager    = spaceManager;
        this.userAccessor    = userAccessor;

    mailService = new MailServiceImpl( taskManager );
        eventPublisher.register(this);
    }

    @EventListener
    public void onSpaceCreated(SpaceBlueprintCreateEvent event) {
        //if (!BLUEPRINT_KEY.getCompleteKey().equals(event.getSpaceBlueprint().getModuleCompleteKey())) {
            //return;
        //}

        Space space = event.getSpace();
        Map <String, Object> eventContext = event.getContext();
        String emailSubject;
        StringBuilder emailText = new StringBuilder();
        String adminEmailList = "pdl-wiki2-admin@keysight.com";

        User loggedInUser = AuthenticatedUserThreadLocal.getUser();

        emailSubject = "New Confluence Space Created: " + space.getName();
        emailText.append( "<h1>A new space in Confluence has been created!</h1>\n" );
        emailText.append( "<p><a href=\"" + settingsManager.getGlobalSettings().getBaseUrl() + space.getUrlPath() + "\">"+space.getName()+"</a></p>\n" );

        emailText.append( "<p>Created by: <b>" + loggedInUser.getFullName() + " (" + loggedInUser.getName() + ")</b></p>\n" );
        emailText.append( "<p>Blueprint Key: <b> " + event.getSpaceBlueprint().getModuleCompleteKey() + "</b></p>\n" );


        emailText.append( "<ul>\n" );
        for( String key : eventContext.keySet() ) {
            emailText.append( "   <li>" + key + " => " + eventContext.get( key ).toString() + "</li>\n" );
        }
        emailText.append( "</ul>\n" );
    
        try{
           MailQueueItem mailQueueItem = new ConfluenceMailQueueItem( adminEmailList, emailSubject, emailText.toString(), MIME_TYPE_HTML);
           mailService.sendEmail( mailQueueItem );
        } catch( Exception e ){
           System.out.println( "Failed to sending email test." );
           System.out.println( e );
        }

        return;
    }

    @Override
    public void destroy() throws Exception
    {
        eventPublisher.unregister(this);
    }

}
