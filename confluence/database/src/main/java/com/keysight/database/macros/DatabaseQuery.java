package com.keysight.database.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.mail.template.ConfluenceMailQueueItem;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.core.task.MultiQueueTaskManager;
import com.atlassian.mail.queue.MailQueueItem;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.keysight.database.helpers.DatabaseQueryHelper;
import com.keysight.database.helpers.PluginHelper;
import com.keysight.database.helpers.mail.MailService;
import com.keysight.database.helpers.mail.MailServiceImpl;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Date;

import static com.atlassian.confluence.mail.template.ConfluenceMailQueueItem.MIME_TYPE_HTML;

public class DatabaseQuery implements Macro {
    private final static String PROFILE_ID = "profile";
    private final static String QUERY_ISVISIBLE = "show-query";
    private final static String COLLAPSE_QUERY = "collapse-query";

    private final PageManager pageManager;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final SettingsManager settingsManager;
    private final SpaceManager spaceManager;
    private final MultiQueueTaskManager taskManager;
    private final TransactionTemplate transactionTemplate;
    private final UserAccessor userAccessor;
    private final UserManager userManager;
    private final VelocityHelperService velocityHelperService;
    private final MailService mailService;

    private Connection connect = null;
    private Statement statement = null;
    private ResultSet resultSet = null;

    public DatabaseQuery( PageManager pageManager,
                          PluginSettingsFactory pluginSettingsFactory,
                          SettingsManager settingsManager,
                          SpaceManager spaceManager,
                          MultiQueueTaskManager taskManager,
                          TransactionTemplate transactionTemplate,
                          UserAccessor userAccessor,
                          UserManager userManger,
                          VelocityHelperService velocityHelperService
    ) {
        this.pageManager = pageManager;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.settingsManager = settingsManager;
        this.spaceManager    = spaceManager;
        this.taskManager     = taskManager;
        this.transactionTemplate = transactionTemplate;
        this.userAccessor = userAccessor;
        this.userManager = userManger;
        this.velocityHelperService = velocityHelperService;

        mailService = new MailServiceImpl( taskManager );
    }

    public String execute(Map<String, String> parameters, String body, ConversionContext context)
            throws MacroExecutionException {
        String baseUrl = settingsManager.getGlobalSettings().getBaseUrl();
        String template = "/com/keysight/database/templates/database-query.vm";
        Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();

        String fullName = userManager.getRemoteUser().getFullName();
        String userName = userManager.getRemoteUsername();
        String spaceKey = context.getSpaceKey();
        String spaceName = spaceManager.getSpace( spaceKey ).getName();
        String pageName = context.getEntity().getTitle();
        String pageUrl = baseUrl + context.getEntity().getUrlPath();

        PluginHelper pluginHelper = new PluginHelper(pluginSettingsFactory, transactionTemplate);

        // Clean up body by removing extra whitespace, newlines, etc.
        String rawBody = body;
        body = body.replaceAll("\\s+", " ");

        ArrayList<String> columnNames = new ArrayList<>();
        ArrayList<ArrayList<String>> dataRows = new ArrayList<>();

        String activeProfile = parameters.get(PROFILE_ID);

        connect = DatabaseQueryHelper.createConnection(activeProfile, pluginHelper);

        if (connect != null) {
            try {
                // Statements allow to issue SQL queries to the database
                statement = connect.createStatement();

                // Check for query constraints [pre]
                // We will set the soft timer arbitrarily high
                int softTimer = Integer.MAX_VALUE;
                int startQuery = (int) ((new Date()).getTime() / 1000); // Current time in seconds
                if (!Objects.equals(pluginHelper.getTimeoutLimitValue(), "")) {
                    // Try to get integer value of timeout value, default to 0
                    int timeVal;
                    try {
                        timeVal = Integer.parseInt(pluginHelper.getTimeoutLimitValue());
                    } catch (NumberFormatException e) {
                        // 0 seconds is interpreted as no time limit.
                        throw new MacroExecutionException("Failed to parse query timeout: " + pluginHelper.getTimeoutLimitValue());
                    }

                    // Soft or hard?
                    if (Objects.equals(StringUtils.lowerCase(pluginHelper.getTimeoutLimitType()), "soft")) {
                        softTimer = timeVal;
                    } else {
                        statement.setQueryTimeout(timeVal);
                    }
                }

                // Handle row limit
                int softRowLimit = Integer.MAX_VALUE;
                if (!Objects.equals(pluginHelper.getRowLimitValue(), "")) {
                    // Try to get integer value of timeout value, default to 0
                    int rowLimit;
                    try {
                        rowLimit = Integer.parseInt(pluginHelper.getRowLimitValue());
                    } catch (NumberFormatException e) {
                        // 0 seconds is interpreted as no time limit.
                        throw new MacroExecutionException("Failed to parse query row limit: " + pluginHelper.getTimeoutLimitValue());
                    }

                    // Soft or hard?
                    if (Objects.equals(StringUtils.lowerCase(pluginHelper.getRowLimitType()), "soft")) {
                        softRowLimit = rowLimit;
                    } else {
                        statement.setMaxRows(rowLimit);
                    }
                }

                // Result set get the result of the SQL query
                resultSet = statement.executeQuery(body);

                // Check for query constraints [post]
                int endQuery = (int) ((new Date()).getTime() / 1000); // Current time in seconds

                // Did more than softTimer pass?
                if (endQuery - startQuery > softTimer) {
                    String message = "<p>The user <b>" + fullName + " ( " + userName + " )</b> executed an sql query\n "
                            + "using the Database Connection Macro on the page <a href=\"" + pageUrl + "\">" + pageName + "</a>\n "
                            + "in the space <b>" + spaceName + "</b>. It took " + (endQuery - startQuery) + " seconds\n"
                            + "exceeding the soft time limit of " + softTimer + ".</p>\n";

                    sendEmail(pluginHelper, "Confluence DB Connctory Soft Timout Limit Warning", message, null);
                }

                // Get the Column Names
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    columnNames.add(resultSet.getMetaData().getColumnLabel(i));
                }
                velocityContext.put("columnNames", columnNames);

                // Get the Column Names
                int rowCount = 0;
                while (resultSet.next()) {
                    rowCount++;
                    ArrayList<String> row = new ArrayList<String>();
                    for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                        row.add(resultSet.getString(i));
                    }
                    dataRows.add(row);
                }

                // Did we pass the soft row limit?
                if (rowCount > softRowLimit) {

                    String message = "<p>The user <b>" + fullName + " ( " + userName + " )</b> executed an sql query\n "
                                    + "using the Database Connection Macro on the page <a href=\"" + pageUrl + "\">" + pageName + "</a>\n "
                                    + "in the space <b>" + spaceName + "</b>. It returned " + rowCount + " rows\n "
                                    + "exceeding the soft row limit of " + softRowLimit + ".</p>\n";

                    sendEmail(pluginHelper, "Confluence DB Connector Soft Row Limit Warning", message, null);
                }

                if (pluginHelper.getLogEmail() != null && !Objects.equals(pluginHelper.getLogEmail(), "")) {
                    String message = "{'fullName':'"+fullName+"'," +
                            "'userName':'"+userName+"'," +
                            "'pageName':'"+pageName+"'," +
                            "'pageUrl':'"+pageUrl+"'," +
                            "'spaceName':'"+spaceName+"'," +
                            "'rowCount':'"+rowCount+"'," +
                            "'queryTime':'"+(endQuery - startQuery)+"'," +
                            "'querySql':'"+body+"'," +
                            "'date':'"+ LocalDateTime.now()+"'}";

                    sendEmail(pluginHelper, "Confluence DB Connector Query Log", message, pluginHelper.getLogEmail());
                }

                velocityContext.put("dataRows", dataRows);
            } catch (Exception e) {
                throw new MacroExecutionException(e);
            } finally {
                close();
            }
        }

        StringBuilder codeViewOfBody = new StringBuilder();

        codeViewOfBody.append("<ac:structured-macro ac:name=\"code\" ac:schema-version=\"1\">");
        codeViewOfBody.append("<ac:parameter ac:name=\"language\">sql</ac:parameter>");
        codeViewOfBody.append("<ac:parameter ac:name=\"title\">SQL Query</ac:parameter>");
        if (parameters.containsKey(COLLAPSE_QUERY)) {
            codeViewOfBody.append("<ac:parameter ac:name=\"collapse\">true</ac:parameter>");
        }
        codeViewOfBody.append("   <ac:plain-text-body><![CDATA[");
        codeViewOfBody.append(rawBody);
        codeViewOfBody.append("]]></ac:plain-text-body>");
        codeViewOfBody.append("</ac:structured-macro>");

        velocityContext.put("showQuery", parameters.get(QUERY_ISVISIBLE));
        velocityContext.put("code", codeViewOfBody.toString());
        return velocityHelperService.getRenderedTemplate(template, velocityContext);
    }

    private void sendEmail(PluginHelper helper, String subject, String body, String customEmail) {
        if (customEmail == null || Objects.equals(customEmail, "")) {
            customEmail = helper.getNotificationEmail();
        }

        try{
            MailQueueItem mailQueueItem = new ConfluenceMailQueueItem(customEmail, subject, body, MIME_TYPE_HTML);
            mailService.sendEmail( mailQueueItem );
        } catch( Exception e ){
            System.out.println( "Failed to send email." );
            System.out.println( e.getMessage() );
        }
    }

    private void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connect != null) {
                connect.close();
            }
        } catch (Exception e) {

        }
    }

    public BodyType getBodyType() {
        return BodyType.PLAIN_TEXT;
    }

    public OutputType getOutputType() {
        return OutputType.BLOCK;
    }
}
