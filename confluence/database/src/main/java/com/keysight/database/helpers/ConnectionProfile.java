package com.keysight.database.helpers;


import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Arrays;

@XmlRootElement
public class ConnectionProfile {
    @XmlElement
    public String profileName;
    @XmlElement
    public String profileDescription;
    @XmlElement
    public String authorizedUsers;
    @XmlElement
    public String authorizedGroups;
    @XmlElement
    public String databaseType;
    @XmlElement
    public String databaseName;
    @XmlElement
    public String databaseServer;
    @XmlElement
    public String databasePort;
    @XmlElement
    public String databaseUsername;
    public String databasePassword;
    @XmlElement
    public String connectionStringSuffix;

    public ConnectionProfile(Element profile) {
        try {
            profileName = new String(Base64.decodeBase64(profile.getElementsByTagName("profile-name").item(0).getTextContent()), "UTF-8");
            profileDescription = new String(Base64.decodeBase64(profile.getElementsByTagName("profile-description").item(0).getTextContent()), "UTF-8");
            authorizedUsers = new String(Base64.decodeBase64(profile.getElementsByTagName("authorized-users").item(0).getTextContent()), "UTF-8");
            authorizedGroups = new String(Base64.decodeBase64(profile.getElementsByTagName("authorized-groups").item(0).getTextContent()), "UTF-8");
            databaseType = new String(Base64.decodeBase64(profile.getElementsByTagName("database-type").item(0).getTextContent()), "UTF-8");
            databaseName = new String(Base64.decodeBase64(profile.getElementsByTagName("database-name").item(0).getTextContent()), "UTF-8");
            databaseServer = new String(Base64.decodeBase64(profile.getElementsByTagName("database-server").item(0).getTextContent()), "UTF-8");
            databasePort = new String(Base64.decodeBase64(profile.getElementsByTagName("database-port").item(0).getTextContent()), "UTF-8");
            databaseUsername = new String(Base64.decodeBase64(profile.getElementsByTagName("database-username").item(0).getTextContent()), "UTF-8");
            databasePassword = new String(Base64.decodeBase64(profile.getElementsByTagName("database-password").item(0).getTextContent()), "UTF-8");
            connectionStringSuffix = new String(Base64.decodeBase64(profile.getElementsByTagName("connection-string-suffix").item(0).getTextContent()), "UTF-8");
        } catch (Exception e) {
            System.out.println("Failed to get XML details");
        }
    }

    public String getName() {
        return profileName;
    }

    public ArrayList<String> getAuthorizedUsers() {
        return new ArrayList<>(Arrays.asList(StringUtils.split(this.authorizedUsers, ",")));
    }

    public ArrayList<String> getAuthorizedGroups() {
        return new ArrayList<>(Arrays.asList(StringUtils.split(this.authorizedGroups, ",")));
    }
}
