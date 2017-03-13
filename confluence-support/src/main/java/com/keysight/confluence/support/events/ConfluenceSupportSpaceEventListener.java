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
import java.util.ArrayList;
import java.util.Map;

import com.atlassian.confluence.mail.template.ConfluenceMailQueueItem;
import com.atlassian.core.task.MultiQueueTaskManager;
import com.atlassian.mail.queue.MailQueueItem;
import static com.atlassian.confluence.mail.template.ConfluenceMailQueueItem.MIME_TYPE_HTML;
import static com.atlassian.confluence.mail.template.ConfluenceMailQueueItem.MIME_TYPE_TEXT;
import com.keysight.confluence.support.helpers.MailService;
import com.keysight.confluence.support.helpers.MailServiceImpl;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import java.net.URLDecoder;
import java.io.StringReader;

import com.keysight.confluence.support.helpers.PluginConfig;

@Component
public class ConfluenceSupportSpaceEventListener implements DisposableBean
{
    private final EventPublisher eventPublisher;
    private final SettingsManager settingsManager;
    private final SpaceManager spaceManager;
    private final UserAccessor userAccessor;
    private final MailService mailService;
    private final MultiQueueTaskManager taskManager;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public ConfluenceSupportSpaceEventListener(EventPublisher eventPublisher,
                                               MultiQueueTaskManager taskManager,
                                               PluginSettingsFactory pluginSettingsFactory,
                                               SettingsManager settingsManager,
                                               SpaceManager spaceManager,
                                               TransactionTemplate transactionTemplate,
                                               UserAccessor userAccessor )
    {
        this.eventPublisher  = eventPublisher;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.settingsManager = settingsManager;
        this.spaceManager    = spaceManager;
        this.taskManager     = taskManager;
        this.transactionTemplate = transactionTemplate;
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
        List<String> emailAddresses = new ArrayList<String>();
        User loggedInUser = AuthenticatedUserThreadLocal.getUser();

        try{
            PluginConfig pluginConfig = (PluginConfig) transactionTemplate.execute(new TransactionCallback(){
                public Object doInTransaction(){
                    PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
                    PluginConfig pluginConfig = new PluginConfig();
                    pluginConfig.setXml( (String) settings.get(PluginConfig.class.getName() + ".xml"));
                    return pluginConfig;
                };
            });

            String pluginConfigXml = URLDecoder.decode( pluginConfig.getXml() );

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse( new InputSource( new StringReader(pluginConfigXml) ) );
            Element root = document.getDocumentElement();
            NodeList allNodes = root.getElementsByTagName( "on-space-creation-notification-email-address" );
            for( int i = 0; i < allNodes.getLength(); i++ ){
               if( allNodes.item(i).getNodeType() == Node.ELEMENT_NODE){ 
                  emailAddresses.add( allNodes.item(i).getTextContent() );
               }
            }

        } catch( Exception e ){
        }

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
    
        for( String emailAddress : emailAddresses ){
           try{
              MailQueueItem mailQueueItem = new ConfluenceMailQueueItem( emailAddress, emailSubject, emailText.toString(), MIME_TYPE_HTML);
              mailService.sendEmail( mailQueueItem );
           } catch( Exception e ){
              System.out.println( "Failed to sending email test." );
              System.out.println( e );
           }
        }

        return;
    }

    @Override
    public void destroy() throws Exception
    {
        eventPublisher.unregister(this);
    }

}
