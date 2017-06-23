package com.keysight.database.helpers;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

public class PluginHelper {
    // note, may need to load jdb2jcc.jar and db2jcc_license_cu.jar
    private final String DB2_DOWNLOAD = null;
    private final String DB2_DRIVER_KEY = "DB2";
    private final String DB2_REGEX = ".*db2jcc.*";
    private final String DB2_CLASS_NAME = "com.ibm.db2.jcc.DB2Driver";

    private final String DERBY_DOWNLOAD = "http://central.maven.org/maven2/org/apache/derby/derby/10.12.1.1/derby-10.12.1.1.jar";
    private final String DERBY_DRIVER_KEY = "Derby";
    private final String DERBY_REGEX = ".*derby.*";
    private final String DERBY_CLASS_NAME = "org.apache.derby.jdbc.EmbeddedDriver";

    private final String SQL_SERVER_DOWNLOAD = "http://central.maven.org/maven2/com/microsoft/sqlserver/mssql-jdbc/6.1.0.jre8/mssql-jdbc-6.1.0.jre8.jar";
    private final String SQL_SERVER_DRIVER_KEY = "Microsoft SQL Server";
    private final String SQL_SERVER_REGEX = ".*sqljdbc.*";
    private final String SQL_SERVER_CLASS_NAME = "com.microsoft.sqlserver.jdbc.SQLServerDriver";

    private final String MONGODB_DOWNLOAD = "http://central.maven.org/maven2/org/mongodb/mongodb-driver/3.4.2/mongodb-driver-3.4.2.jar";
    private final String MONGODB_DRIVER_KEY = "MongoDB";
    private final String MONGODB_REGEX = ".*mongodb.*";
    private final String MONGODB_CLASS_NAME = "mongodb.jdbc.MongoDriver";

    private final String MYSQL_DOWNLOAD = "http://central.maven.org/maven2/mysql/mysql-connector-java/6.0.6/mysql-connector-java-6.0.6.jar";
    private final String MYSQL_DRIVER_KEY = "MySQL";
    private final String MYSQL_REGEX = ".*mysql.*";
    private final String MYSQL_CLASS_NAME = "com.mysql.jdbc.Driver";

    private final String ORACLE_DOWNLOAD = null;
    private final String ORACLE_DRIVER_KEY = "Oracle";
    private final String ORACLE_REGEX = ".*ojdbc.*";
    private final String ORACLE_CLASS_NAME = "oracle.jdbc.driver.OracleDriver";

    private final String POSTGRESQL_DOWNLOAD = "https://jdbc.postgresql.org/download/postgresql-42.1.1.jar";
    private final String POSTGRESQL_DRIVER_KEY = "PostgreSQL";
    private final String POSTGRESQL_REGEX = ".*postgre.*";
    private final String POSTGRESQL_CLASS_NAME = "org.postgresql.Driver";

    private final String SYBASE_DOWNLOAD = null;
    private final String SYBASE_DRIVER_KEY = "Sybase";
    private final String SYBASE_REGEX = ".*jconn.*";
    private final String SYBASE_CLASS_NAME = "sybase.jdbc.sqlanywhere.IDriver";

    private String jdbcDriverDirectory;
    private String configAuthorizedUsers;
    private String configAuthorizedGroups;
    private String rowLimitType;
    private String rowLimitValue;
    private String timeoutLimitType;
    private String timeoutLimitValue;
    private String notificationEmail;

    private Element root;
    private Element profileRoot;

    public PluginHelper(final PluginSettingsFactory pluginSettingsFactory,
                        final TransactionTemplate transactionTemplate) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            PluginConfig pluginConfig = (PluginConfig) transactionTemplate.execute((TransactionCallback<Object>) () -> {
                PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
                PluginConfig pluginConfig1 = new PluginConfig();
                pluginConfig1.setXml((String) settings.get(PluginConfig.class.getName() + ".xml"));
                return pluginConfig1;
            });

            String pluginConfigXml = URLDecoder.decode(pluginConfig.getXml(), "UTF-8");
            Document document = builder.parse(new InputSource(new StringReader(pluginConfigXml)));
            root = document.getDocumentElement();

            PluginConfig profiles = (PluginConfig) transactionTemplate.execute((TransactionCallback<Object>) () -> {
                PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
                PluginConfig pluginConfig1 = new PluginConfig();
                pluginConfig1.setXml((String) settings.get(PluginConfig.class.getName() + "_profiles.xml"));
                return pluginConfig1;
            });

            String profilesXml = URLDecoder.decode(profiles.getXml(), "UTF-8");
            Document profileDocument = builder.parse(new InputSource(new StringReader(profilesXml)));
            profileRoot = profileDocument.getDocumentElement();

            String jdbcDriverDirectory = new String( Base64.decodeBase64(getValueOfElement("jdbc-driver-directory")), "UTF-8" );
            configAuthorizedUsers = new String( Base64.decodeBase64(getValueOfElement("config-authorized-users")), "UTF-8");
            configAuthorizedGroups = new String( Base64.decodeBase64(getValueOfElement("config-authorized-groups")), "UTF-8");
            rowLimitType = new String( Base64.decodeBase64(getValueOfElement("row-limit-type")), "UTF-8");
            rowLimitValue = new String( Base64.decodeBase64(getValueOfElement("row-limit-value")), "UTF-8");
            timeoutLimitType = new String( Base64.decodeBase64(getValueOfElement("timeout-limit-type")), "UTF-8");
            timeoutLimitValue = new String( Base64.decodeBase64(getValueOfElement("timeout-limit-value")), "UTF-8");
            notificationEmail = new String( Base64.decodeBase64(getValueOfElement("notification-email")), "UTF-8");

            if (!StringUtils.isEmpty(jdbcDriverDirectory)) {
                this.setJdbcDriverDirectory(jdbcDriverDirectory);
            }

        } catch (Exception e) {
        }
    }

    private String getValueOfElement(String tagName) {
        NodeList allNodes = root.getElementsByTagName(tagName);
        String text = null;
        for (int i = 0; i < allNodes.getLength(); i++) {
            if (allNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                text = allNodes.item(i).getTextContent();
                break;
            }
        }
        return text;
    }

    private void printLoadedDrivers() {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver jdbcDriver = drivers.nextElement();
            if (jdbcDriver.getClass() == DatabaseDriverDelegate.class) {
                DatabaseDriverDelegate delegateDriver = (DatabaseDriverDelegate) drivers.nextElement();
                jdbcDriver = delegateDriver.getRealDriver();
            }
            //System.out.println("Driver: " + jdbcDriver.getClass().getName());
        }
    }

    DriverDetails getDriverDetails(String driverKey) {
        DriverDetails driverDetails = null;

        if (driverKey.equals(MYSQL_DRIVER_KEY)) {
            try {
                driverDetails = getJdbcDriver(MYSQL_DOWNLOAD, MYSQL_DRIVER_KEY, MYSQL_REGEX);
            } catch( Exception e ){}
            driverDetails.className = MYSQL_CLASS_NAME;
        } else if (driverKey.equals(SQL_SERVER_DRIVER_KEY)) {
            driverDetails = getJdbcDriver(SQL_SERVER_DOWNLOAD, SQL_SERVER_DRIVER_KEY, SQL_SERVER_REGEX);
            driverDetails.className = SQL_SERVER_CLASS_NAME;
        } else if (driverKey.equals(ORACLE_DRIVER_KEY)) {
            driverDetails = getJdbcDriver(ORACLE_DOWNLOAD, ORACLE_DRIVER_KEY, ORACLE_REGEX);
            driverDetails.className = ORACLE_CLASS_NAME;
        } else if (driverKey.equals(POSTGRESQL_DRIVER_KEY)) {
            driverDetails = getJdbcDriver(POSTGRESQL_DOWNLOAD, POSTGRESQL_DRIVER_KEY, POSTGRESQL_REGEX);
            driverDetails.className = POSTGRESQL_CLASS_NAME;
        } else if (driverKey.equals(DB2_DRIVER_KEY)) {
            driverDetails = getJdbcDriver(DB2_DOWNLOAD, DB2_DRIVER_KEY, DB2_REGEX);
            driverDetails.className = DB2_CLASS_NAME;
        } else if (driverKey.equals(DERBY_DRIVER_KEY)) {
            driverDetails = getJdbcDriver(DERBY_DOWNLOAD, DERBY_DRIVER_KEY, DERBY_REGEX);
            driverDetails.className = DERBY_CLASS_NAME;
        } else if (driverKey.equals(MONGODB_DRIVER_KEY)) {
            driverDetails = getJdbcDriver(MONGODB_DOWNLOAD, MONGODB_DRIVER_KEY, MONGODB_REGEX);
            driverDetails.className = MONGODB_CLASS_NAME;
        } else if (driverKey.equals(SYBASE_DRIVER_KEY)) {
            driverDetails = getJdbcDriver(SYBASE_DOWNLOAD, SYBASE_DRIVER_KEY, SYBASE_REGEX);
            driverDetails.className = SYBASE_CLASS_NAME;
        }

        return driverDetails;
    }

    public void loadJdbcDriver(String driverKey) {
        DriverDetails driverDetails = getDriverDetails(driverKey);

        try {
            if (!StringUtils.isEmpty(driverDetails.className)) {
                URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{driverDetails.url}, System.class.getClassLoader());
                Driver driver = (Driver) Class.forName(driverDetails.className, true, urlClassLoader).newInstance();
                DriverManager.registerDriver(new DatabaseDriverDelegate(driver));
            }
            printLoadedDrivers();
        } catch (Exception exception) {
        }
    }

    private DriverDetails getJdbcDriver(String url, String driverKey, String regex) {
        DriverDetails driverDetails = new DriverDetails();
        Path jdbcDriverDirectoryPath;

        driverDetails.isLocal = false;
        if (!StringUtils.isEmpty(url)) {
            try {
                driverDetails.url = new URL(url);
                driverDetails.isOk = true;
                //System.out.println("url did parse: " + url);
            } catch (Exception exception) {
                driverDetails.url = null;
                driverDetails.isOk = false;
                //System.out.println("url did not parse: " + url + " " + exception);
            }
        } else {
            driverDetails.url = null;
            driverDetails.isOk = false;
            //System.out.println("no url");
        }

        if (!StringUtils.isEmpty(getJdbcDriverDirectory())) {
            jdbcDriverDirectoryPath = Paths.get(this.getJdbcDriverDirectory());
            if (Files.isDirectory(jdbcDriverDirectoryPath)) {
                try {
                    // first look for any jar file in the child directory identifyed by the driverKey
                    jdbcDriverDirectoryPath = Paths.get(this.getJdbcDriverDirectory(), driverKey);
                    if (Files.isDirectory(jdbcDriverDirectoryPath)) {
                        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(jdbcDriverDirectoryPath);
                        for (Path path : directoryStream) {
                            if (path.toString().matches(".*\\.jar")) {
                                driverDetails.isLocal = true;
                                driverDetails.url = path.toUri().toURL();
                                driverDetails.isOk = true;
                                break;
                            }
                        }
                    }
                } catch (Exception exception) {
                }

                // if not found, look for any jar file in the jdbc driver directory matching the regex
                if (!driverDetails.isLocal) {
                    jdbcDriverDirectoryPath = Paths.get(this.getJdbcDriverDirectory());
                    try {
                        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(jdbcDriverDirectoryPath);
                        for (Path path : directoryStream) {
                            if (path.toString().matches(regex)) {
                                driverDetails.isLocal = true;
                                driverDetails.url = path.toUri().toURL();
                                driverDetails.isOk = true;
                                break;
                            }
                        }
                    } catch (Exception exception) {
                    }
                }
            }
        }

        return driverDetails;
    }

    public NodeList getProfiles() {
        return profileRoot.getElementsByTagName("saved-profile");
    }

    private String getJdbcDriverDirectory() {
        return this.jdbcDriverDirectory;
    }

    private void setJdbcDriverDirectory(String jdbcDriverDirectory) {
        this.jdbcDriverDirectory = jdbcDriverDirectory;
    }
    public String getRowLimitType() { return rowLimitType; }
    public String getRowLimitValue() { return rowLimitValue; }
    public String getTimeoutLimitType() { return timeoutLimitType; }
    public String getTimeoutLimitValue() { return timeoutLimitValue; }
    public String getNotificationEmail() { return notificationEmail; }

    // Get authorized user or group by splitting the xml attribute on commas

    public ArrayList<String> getAuthorizedUsers() {
        return new ArrayList<String>(Arrays.asList(StringUtils.split(this.configAuthorizedUsers, ",")));
    }

    public ArrayList<String> getAuthorizedGroups() {
        return new ArrayList<String>(Arrays.asList(StringUtils.split(this.configAuthorizedGroups, ",")));
    }
}


