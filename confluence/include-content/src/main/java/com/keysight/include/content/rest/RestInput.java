package com.keysight.include.content.rest;

import javax.xml.bind.annotation.*;
@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.FIELD)
public class RestInput {

   @XmlElement(name = "spaceKey")
   private String spaceKey;
    
   @XmlElement(name = "pageTitle")
   private String pageTitle;
   
   @XmlElement(name = "username" )
   private String username;
   
   public RestInput() {
   }

   public String getSpaceKey() { return spaceKey; }
   public void setSpaceKey( String spaceKey ){ this.spaceKey = spaceKey; }
   
   public String getPageTitle() { return pageTitle; }
   public void setPageTitle( String pageTitle ){ this.pageTitle = pageTitle; }
   
   public String getUsername() { return username; }
   public void setUsername( String username ){ this.username = username; }
}
