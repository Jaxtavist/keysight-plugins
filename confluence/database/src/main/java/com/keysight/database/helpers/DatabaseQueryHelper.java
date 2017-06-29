package com.keysight.database.helpers;

import com.atlassian.confluence.macro.MacroExecutionException;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class DatabaseQueryHelper {
    public static Connection createConnection(String profileId, PluginHelper pluginHelper) throws MacroExecutionException {
        // Get all stored profiles
        NodeList profileNodes = pluginHelper.getProfiles();
        ArrayList<ConnectionProfile> profiles = new ArrayList<>();

        for (int i = 0; i < profileNodes.getLength(); i++) {
            Element profileNode = (Element) profileNodes.item(i);
            profiles.add(new ConnectionProfile(profileNode));
        }

        ConnectionProfile profile = null;
        for (ConnectionProfile tempProfile : profiles) {
            if (tempProfile.getId() != null && tempProfile.getId().equals(profileId)) {
                profile = tempProfile;
            }
        }


        if (profile == null) {
            throw new MacroExecutionException("No profile found with that ID for which you are authorized: " + profileId);
        }

        return createConnection(profile, pluginHelper);
    }

    public static Connection createConnection(ConnectionProfile profile, PluginHelper pluginHelper) throws MacroExecutionException {
        String username = profile.getDatabaseUsername();
        Connection connect;
        String password = profile.getDatabasePassword();
        System.out.println("Pass: "+password);
        if (Objects.equals(password, "*****")) {
            // Fetch the password from the authmap, where the profile name is the key.
            password = pluginHelper.getAuthMap().get(profile.getProfileId());
            System.out.println("Pass: " + password);
        }
        System.out.println("Pass: "+password);

        String subProtocol = StringUtils.lowerCase(profile.getDatabaseType());

        if (subProtocol.equals("microsoft sql server")) {
            subProtocol = "sqlserver";
        } else if (subProtocol.equals("mongodb")) {
            subProtocol = "mongo";
        }
        String connectionString = "jdbc:" + subProtocol + "://" + profile.getDatabaseServer();
        if (!StringUtils.isEmpty(profile.getDatabasePort())) {
            connectionString += ":" + profile.getDatabasePort();
        }

        if (profile.getDatabaseType().matches("Microsoft SQL Server")) {
            connectionString += ";databaseName=" + profile.getDatabaseName();
        } else {
            connectionString += "/" + profile.getDatabaseName();
        }

        if (!StringUtils.isEmpty(profile.getConnectionStringSuffix())) {
            connectionString += "?" + profile.getConnectionStringSuffix();
        }

        //System.out.println( "Connection String: " + connectionString );

        try {
            connect = DriverManager.getConnection(connectionString, username, password);
        } catch (SQLException exception) {
            if (exception.getMessage().matches(".*No suitable driver found.*")) {
                pluginHelper.loadJdbcDriver(profile.getDatabaseType());
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

        return connect;
    }

}
