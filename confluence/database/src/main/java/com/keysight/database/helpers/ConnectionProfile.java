package com.keysight.database.helpers;


import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Arrays;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ConnectionProfile {
    @XmlElement
    private String profileName;
    @XmlElement
    private String profileId;
    @XmlElement
    private String profileDescription;
    @XmlElement
    public String authorizedUsers;
    @XmlElement
    public String authorizedGroups;
    @XmlElement
    private String databaseType;
    @XmlElement
    private String databaseName;
    @XmlElement
    private String databaseServer;
    @XmlElement
    private String databasePort;
    @XmlElement
    private String databaseUsername;
    @XmlElement
    private String databasePassword;
    @XmlElement
    private String connectionStringSuffix;

    // Dummy constructor to make JAXB happy
    public ConnectionProfile() {
    }

    public ConnectionProfile(Element profile) {
        try {
            profileName = new String(Base64.decodeBase64(profile.getElementsByTagName("profile-name").item(0).getTextContent()), "UTF-8");
            profileId = new String(Base64.decodeBase64(profile.getElementsByTagName("profile-id").item(0).getTextContent()), "UTF-8");
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
        return getProfileName();
    }

    public String getProfileDescription() {
        return profileDescription;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getDatabaseServer() {
        return databaseServer;
    }

    public String getDatabasePort() {
        return databasePort;
    }

    public String getDatabaseUsername() {
        return databaseUsername;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }

    public String getConnectionStringSuffix() {
        return connectionStringSuffix;
    }

    public String getId() {
        return getProfileId();
    }

    public ArrayList<String> getAuthorizedUsers() {
        return new ArrayList<>(Arrays.asList(StringUtils.split(this.authorizedUsers, ",")));
    }

    public ArrayList<String> getAuthorizedGroups() {
        return new ArrayList<>(Arrays.asList(StringUtils.split(this.authorizedGroups, ",")));
    }

    public String getProfileName() {
        return profileName;
    }

    public String getProfileId() {
        return profileId;
    }
}
