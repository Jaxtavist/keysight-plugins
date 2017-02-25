package com.keysight.confluence.support.helpers;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CompactSpaceInfo
{
   @XmlElement private String spaceName = "";
   @XmlElement private String spaceKey  = "";
   
   public CompactSpaceInfo( ){
   }
   
   public CompactSpaceInfo( String spaceName, String spaceKey ){
      this.setSpaceName( spaceName );
      this.setSpaceKey( spaceKey );
   }

   public void setSpaceName( String spaceName ){
      this.spaceName = spaceName;
   }
   public String getSpaceName(){
      return this.spaceName;
   }
   public void setSpaceKey( String spaceKey ){
      this.spaceKey = spaceKey;
   }
   public String getSpaceKey(){
      return this.spaceKey;
   }
}
