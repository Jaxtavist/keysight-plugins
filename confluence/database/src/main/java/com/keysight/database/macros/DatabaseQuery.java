package com.keysight.database.macros;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.keysight.database.helpers.ConnectionProfile;
import com.keysight.database.helpers.PluginHelper;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DatabaseQuery implements Macro {
    private final static String PROFILE_NAME = "profile";
    private final static String QUERY_ISVISIBLE = "show-query";

    private final PluginSettingsFactory pluginSettingsFactory;
    private final TransactionTemplate transactionTemplate;
    private final VelocityHelperService velocityHelperService;
    private final UserManager userManager;
    private final UserAccessor userAccessor;


    private Connection connect = null;
    private Statement statement = null;
    private ResultSet resultSet = null;
    private String databaseType = null;

    public DatabaseQuery(PluginSettingsFactory pluginSettingsFactory,
                         VelocityHelperService velocityHelperService,
                         TransactionTemplate transactionTemplate,
                         UserManager userManger,
                         UserAccessor userAccessor
    ) {
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.transactionTemplate = transactionTemplate;
        this.velocityHelperService = velocityHelperService;
        this.userAccessor = userAccessor;
        this.userManager = userManger;
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

        String subProtocol = StringUtils.lowerCase(profile.databaseType);

        if (subProtocol.equals("microsoft sql server")) {
            subProtocol = "sqlserver";
        } else if (subProtocol.equals("mongodb")) {
            subProtocol = "mongo";
        }

        String connectionString =
                "jdbc:"+ subProtocol + "://" +
                profile.databaseServer + ":" +
                profile.databasePort + "/" +
                profile.databaseName + "?user=" +
                profile.databaseUsername +
                profile.connectionStringSuffix;
        String databaseType = profile.databaseType;
        String username = profile.databaseUsername;
        String password = profile.databasePassword;

        setDatabaseType(databaseType);

        try {
            connect = DriverManager.getConnection(connectionString,
                    username,
                    password);
        } catch (SQLException exception) {
            if (exception.getMessage().matches(".*No suitable driver found.*")) {
                pluginHelper.loadJdbcDriver(getDatabaseType());
                try {
                    connect = DriverManager.getConnection(connectionString,
                            username,
                            password);
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

                // Result set get the result of the SQL query
                resultSet = statement.executeQuery(body);

                // Get the Column Names
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                    columnNames.add(resultSet.getMetaData().getColumnName(i));
                }
                velocityContext.put("columnNames", columnNames);

                // Get the Column Names
                while (resultSet.next()) {
                    ArrayList<String> row = new ArrayList<String>();
                    for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                        row.add(resultSet.getString(i));
                    }
                    dataRows.add(row);
                }
                velocityContext.put("dataRows", dataRows);
            } catch (Exception e) {
                throw new MacroExecutionException(e);
            } finally {
                close();
            }
        }

        velocityContext.put("showQuery", parameters.get(QUERY_ISVISIBLE));
        velocityContext.put("bodyAsHtml", body);
        return velocityHelperService.getRenderedTemplate(template, velocityContext);
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
