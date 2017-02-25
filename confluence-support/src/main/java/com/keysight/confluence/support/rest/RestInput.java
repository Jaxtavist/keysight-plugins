package com.keysight.confluence.support.rest;

import javax.xml.bind.annotation.*;
@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.FIELD)
public class RestInput {

   @XmlElement(name = "space-key")
   private String spaceKey;
    
   @XmlElement(name = "page-title")
   private String pageTitle;
   
   @XmlElement(name = "content-id")
   private String contentId;
   
   @XmlElement(name = "attachment-title")
   private String attachmentTitle;
    
   @XmlElement(name = "creator")
   private String creator;
    
   @XmlElement(name = "creation-date")
   private String creationDate;

   public RestInput() {
   }

   public String getSpaceKey() { return spaceKey; }
   public void setSpaceKey( String spaceKey ){ this.spaceKey = spaceKey; }
   
   public String getPageTitle() { return pageTitle; }
   public void setPageTitle( String pageTitle ){ this.pageTitle = pageTitle; }
   
   public String getContentId() { return contentId; }
   public void setContentId( String contentId ){ this.contentId = contentId; }
   
   public String getAttachmentTitle() { return attachmentTitle; }
   public void setAttachmentTitle( String attachmentTitle ){ this.attachmentTitle = attachmentTitle; }
   
   public String getCreator() { return creator; }
   public void setCreator( String creator ){ this.creator = creator; }
   
   public String getCreationDate() { return creationDate; }
   public void setCreationDate( String creationDate ){ this.creationDate = creationDate; }
}
