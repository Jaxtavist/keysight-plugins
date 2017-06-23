package com.keysight.database.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.mail.template.ConfluenceMailQueueItem;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.core.task.MultiQueueTaskManager;
import com.atlassian.mail.queue.MailQueueItem;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.keysight.database.helpers.ConnectionProfile;
import com.keysight.database.helpers.PluginHelper;
import com.keysight.database.helpers.mail.MailService;
import com.keysight.database.helpers.mail.MailServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.sql.*;
import java.util.*;
import java.util.Date;

import static com.atlassian.confluence.mail.template.ConfluenceMailQueueItem.MIME_TYPE_HTML;

public class DatabaseQuery implements Macro {
    private final static String PROFILE_NAME = "profile";
    private final static String QUERY_ISVISIBLE = "show-query";
    private final static String COLLAPSE_QUERY = "collapse-query";

    private final PluginSettingsFactory pluginSettingsFactory;
    private final TransactionTemplate transactionTemplate;
    private final VelocityHelperService velocityHelperService;
    private final UserManager userManager;
    private final UserAccessor userAccessor;
    private final MultiQueueTaskManager taskManager;


    private final MailService mailService;

    private Connection connect = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private String databaseType = null;

    public DatabaseQuery(PluginSettingsFactory pluginSettingsFactory,
                         VelocityHelperService velocityHelperService,
                         TransactionTemplate transactionTemplate,
                         UserManager userManger,
                         UserAccessor userAccessor,
                         MultiQueueTaskManager taskManager
    ) {
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.transactionTemplate = transactionTemplate;
        this.velocityHelperService = velocityHelperService;
        this.userAccessor = userAccessor;
        this.userManager = userManger;
        this.taskManager     = taskManager;
        mailService = new MailServiceImpl( taskManager );
    }

    private String getDatabaseType() {
        return this.databaseType;
    }

    private void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    public String execute(Map<String, String> parameters, String body, ConversionContext context)
            throws MacroExecutionException {
        String template = "/com/keysight/database/templates/database-query.vm";
        Map<String, Object> velocityContext = velocityHelperService.createDefaultVelocityContext();

        PluginHelper pluginHelper = new PluginHelper(pluginSettingsFactory, transactionTemplate);

        // Clean up body by removing extra whitespace, newlines, etc.
        body = body.replaceAll("\\s+", " ");

        ArrayList<String> columnNames = new ArrayList<String>();
        ArrayList<ArrayList<String>> dataRows = new ArrayList<ArrayList<String>>();
//       File mysqlJar = new File("C:\\jdbc-drivers\\mysql-connector-java-5.1.40-bin.jar");
//       URLClassLoader urlClassLoader;
//       Driver driver;
//       String classname;

        // Get all stored profiles
        NodeList profileNodes = pluginHelper.getProfiles();
        ArrayList<ConnectionProfile> profiles = new ArrayList<>();

        for (int i = 0; i < profileNodes.getLength(); i++) {
            Element profileNode = (Element) profileNodes.item(i);
            profiles.add(new ConnectionProfile(profileNode));
        }

        ConnectionProfile profile = null;
        for (ConnectionProfile tempProfile : profiles) {
            if (tempProfile.getName() != null && tempProfile.getName().equals(parameters.get(PROFILE_NAME))) {
                profile = tempProfile;
            }
        }

        if (profile == null) {
            throw new MacroExecutionException("No profile found with that name for which you are authorized: " + parameters.get(PROFILE_NAME));
        }

        setDatabaseType(profile.databaseType);
        String username = profile.databaseUsername;
        String password = profile.databasePassword;
        String subProtocol = StringUtils.lowerCase(getDatabaseType());

        if (subProtocol.equals("microsoft sql server")) {
            subProtocol = "sqlserver";
        } else if (subProtocol.equals("mongodb")) {
            subProtocol = "mongo";
        }
        String connectionString = "jdbc:" + subProtocol + "://" + profile.databaseServer;
        if (!StringUtils.isEmpty(profile.databasePort)) {
            connectionString += ":" + profile.databasePort;
        }

        if( profile.databaseType.matches("Microsoft SQL Server")) {
            connectionString += ";databaseName=" + profile.databaseName;
        } else {
            connectionString += "/" + profile.databaseName;
        }

        if (!StringUtils.isEmpty(profile.connectionStringSuffix)) {
            connectionString += "?" + profile.connectionStringSuffix;
        }

        //System.out.println( "Connection String: " + connectionString );

        try {
            connect = DriverManager.getConnection(connectionString, username, password);
        } catch (SQLException exception) {
            if (exception.getMessage().matches(".*No suitable driver found.*")) {
                pluginHelper.loadJdbcDriver(getDatabaseType());
                try {
                    connect = DriverManager.getConnection(connectionString, username, password);
                } catch (Exception exception2) {
                    throw new MacroExecutionException(exception2);
                }
            } else {
                throw new MacroExecutionException(exception);
            }
        } catch (Exception exception) {
            throw new MacroExecutionException(exception);
        }

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
                    sendEmail(pluginHelper, "Soft Query Limit Warning", userManager.getRemoteUser().getUsername() + " has " +
                            "preformed a query which took longer than " + softTimer + " seconds. Seconds Taken: " + (endQuery - startQuery));
                }

                // Get the Column Names
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    columnNames.add(resultSet.getMetaData().getColumnName(i));
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
                    sendEmail(pluginHelper, "Soft Query Limit Warning", userManager.getRemoteUser().getUsername() + " has " +
                            "preformed a query returned more than " + softRowLimit + " rows. Rows Returned: " + rowCount);
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
        codeViewOfBody.append(body);
        codeViewOfBody.append("]]></ac:plain-text-body>");
        codeViewOfBody.append("</ac:structured-macro>");

        velocityContext.put("showQuery", parameters.get(QUERY_ISVISIBLE));
        velocityContext.put("code", codeViewOfBody.toString());
        return velocityHelperService.getRenderedTemplate(template, velocityContext);
    }

    private void sendEmail(PluginHelper helper, String subject, String body) {
        try{
            MailQueueItem mailQueueItem = new ConfluenceMailQueueItem( helper.getNotificationEmail(), subject, body, MIME_TYPE_HTML);
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
