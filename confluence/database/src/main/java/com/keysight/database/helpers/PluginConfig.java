package com.keysight.database.helpers;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PluginConfig {
   @XmlElement private String xml;
   @XmlElement private Map<String, String> password;

   public String getXml()           { return xml;     }
   public void   setXml(String xml) { this.xml = xml; }

   public Map<String, String> getPass()           { return password;     }
   public void   setPass(Map<String, String> password) { this.password = password; }
}


