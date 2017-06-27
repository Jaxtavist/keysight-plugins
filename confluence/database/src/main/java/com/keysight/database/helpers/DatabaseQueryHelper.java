package com.keysight.database.helpers;

import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.stream.XMLStreamException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseQueryHelper
{
    public static Connection createConnection(String profileName, PluginHelper pluginHelper) throws MacroExecutionException {
        // Get all stored profiles
        NodeList profileNodes = pluginHelper.getProfiles();
        ArrayList<ConnectionProfile> profiles = new ArrayList<>();
        Connection connect;

        for (int i = 0; i < profileNodes.getLength(); i++) {
            Element profileNode = (Element) profileNodes.item(i);
            profiles.add(new ConnectionProfile(profileNode));
        }

        ConnectionProfile profile = null;
        for (ConnectionProfile tempProfile : profiles) {
            if (tempProfile.getName() != null && tempProfile.getName().equals(profileName)) {
                profile = tempProfile;
            }
        }

        if (profile == null) {
            throw new MacroExecutionException("No profile found with that name for which you are authorized: " + profileName);
        }

        String username = profile.databaseUsername;

        // Fetch the password from the authmap, where the profile name is the key.
        String password = pluginHelper.getAuthMap().get(profile.profileName);
        System.out.println("Pass: "+password);

        String subProtocol = StringUtils.lowerCase(profile.databaseType);

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
                pluginHelper.loadJdbcDriver(profile.databaseType);
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
