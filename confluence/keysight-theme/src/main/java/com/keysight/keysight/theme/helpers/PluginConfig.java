package com.keysight.keysight.theme.helpers;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PluginConfig {
   @XmlElement private String xml;
   public String getXml()           { return xml;     }
   public void   setXml(String xml) { this.xml = xml; }
}


