package com.keysight.test.ecosystem.helpers;

import java.util.Date;

import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;
import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.UserAccessor;

public class MeetingNotesPageContextProvider extends AbstractBlueprintContextProvider
{
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    private final LocaleManager localeManager;
    private final UserAccessor userAccessor;
    private final FormatSettingsManager formatSettingsManager;

    public MeetingNotesPageContextProvider( LocaleManager localeManager, 
                                            UserAccessor userAccessor,
                                            FormatSettingsManager formatSettingsManager )
    {
        this.localeManager = localeManager;
        this.userAccessor = userAccessor;
        this.formatSettingsManager = formatSettingsManager;
    }

    @Override
    protected BlueprintContext updateBlueprintContext(BlueprintContext context)
    {
        String today = getFormattedLocalDate( DATE_FORMAT );

        String pageTitle = "Meeting Notes for " + today;
        String author   = "<ac:link><ri:user ri:username=\"" + AuthenticatedUserThreadLocal.getUsername() + "\" /></ac:link>";
        String attendee = "<ac:placeholder ac:type=\"mention\">@mention the attendees</ac:placeholder>";
        String currentDate = "<time datetime=\"" + today + "\" />";
        
        context.setTitle(pageTitle);
        context.put("author", author );
        context.put("attendee", attendee );
        context.put("currentDate", currentDate );

        return context;
    }

    public String getFormattedLocalDate( String dateFormat )
    {
       Date today = new Date();
       ConfluenceUserPreferences preferences = userAccessor.getConfluenceUserPreferences( AuthenticatedUserThreadLocal.get() );
       DateFormatter dateFormatter = preferences.getDateFormatter(formatSettingsManager, localeManager );
       if( null == dateFormat ){
          return dateFormatter.format(today);
       } else {
          return dateFormatter.formatGivenString( dateFormat, today );
       }
    }
}
