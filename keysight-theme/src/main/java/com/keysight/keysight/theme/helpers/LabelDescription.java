package com.keysight.keysight.theme.helpers;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.atlassian.confluence.labels.Label;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class LabelDescription
{
   @XmlElement private Label label;
   @XmlElement private String labelName;
   @XmlElement private long labelId;
   @XmlElement private String urlPath;

   public LabelDescription( ){
   }
   
   public LabelDescription( Label label, String spaceKey ){
      this.setLabel( label, spaceKey );
   }

   public void setLabel( Label label, String spaceKey ){
      if( label != null ){
         this.label = label;
         this.setLabelId( label.getId() );
         this.setLabelName( label.getName() );
         this.setUrlPath( label.getUrlPath( spaceKey ) );
      }
   }

   public Label getLabel(){
      return this.label;
   }

   public void setLabelName( String labelName ){
      this.labelName = labelName;
   }
   public String getLabelName(){
      return this.labelName;
   }

   public void setLabelId( long labelId ){
      this.labelId = labelId;
   }
   public long getLabelId(){
      return this.labelId;
   }

   public void setUrlPath( String urlPath ){
      this.urlPath = urlPath;
   }
   public String getUrlPath(){
      return this.urlPath;
   }

}
