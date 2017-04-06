package com.keysight.confluence.support.helpers;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.pages.Page;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.lang.StringBuilder;
import com.atlassian.core.util.XMLUtils;
import java.text.SimpleDateFormat;
import com.atlassian.confluence.spaces.Space;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SpaceInfo
{
   @XmlElement private Space space = null;
   @XmlElement private long pageCount = 0;
   @XmlElement private Date mostRecentModificationDate = null;
   @XmlElement private ArrayList<String> adminUsers;
   @XmlElement private ArrayList<String> authorUsers;
   @XmlElement private ArrayList<String> readerUsers;
   @XmlElement private ArrayList<String> adminGroups;
   @XmlElement private ArrayList<String> authorGroups;
   @XmlElement private ArrayList<String> readerGroups;

   @XmlElement private String spaceKey = null;
   @XmlElement private String spaceName = null;
   @XmlElement private String spaceType = null;
   @XmlElement private String spaceStatus = null;
   @XmlElement private String lastModificationDate = null;
   @XmlElement private String xDaysAgo = null;

   public SpaceInfo( ){
      adminUsers = new ArrayList<String>();
      authorUsers = new ArrayList<String>();
      readerUsers = new ArrayList<String>();
      adminGroups = new ArrayList<String>();
      authorGroups = new ArrayList<String>();
      readerGroups = new ArrayList<String>();
      updateSpaceInfo();
   }
   
   public SpaceInfo( Space mySpace ){
      this.space = mySpace;
      adminUsers = new ArrayList<String>();
      authorUsers = new ArrayList<String>();
      readerUsers = new ArrayList<String>();
      adminGroups = new ArrayList<String>();
      authorGroups = new ArrayList<String>();
      readerGroups = new ArrayList<String>();
      updateSpaceInfo();
   }

   public SpaceInfo( Space mySpace, long pageCount ){
      this.space = mySpace;
      this.pageCount = pageCount;
      adminUsers = new ArrayList<String>();
      authorUsers = new ArrayList<String>();
      readerUsers = new ArrayList<String>();
      adminGroups = new ArrayList<String>();
      authorGroups = new ArrayList<String>();
      readerGroups = new ArrayList<String>();
      updateSpaceInfo();
   }
   
   public SpaceInfo( Space mySpace, long pageCount, Date modificationDate ){
      this.space = mySpace;
      this.pageCount = pageCount;
      this.mostRecentModificationDate = modificationDate;

      adminUsers = new ArrayList<String>();
      authorUsers = new ArrayList<String>();
      readerUsers = new ArrayList<String>();
      adminGroups = new ArrayList<String>();
      authorGroups = new ArrayList<String>();
      readerGroups = new ArrayList<String>();
      updateSpaceInfo();
   }

   private void updateSpaceInfo()
   {
      if( this.space != null )
      {
         this.spaceKey = this.space.getKey();
         this.spaceName = this.space.getName();
         this.spaceType = this.space.getSpaceType().toString();
         this.spaceStatus = this.spaceStatus();
         this.lastModificationDate = this.lastModificationDate();
         this.xDaysAgo = this.daysSinceLastLogin();
      }
   }

   public void setSpace( Space mySpace ){
      this.space = mySpace;
   }
   public Space getSpace(){
      return this.space;
   }

   public void setAdminUsers( ArrayList<String> adminUsers ){
      this.adminUsers = adminUsers;
   }
   public ArrayList<String> getAdminUsers(){
      return this.adminUsers;
   }
   
   public void setAuthorUsers( ArrayList<String> authorUsers ){
      this.authorUsers = authorUsers;
   }
   public ArrayList<String> getAuthorUsers(){
      return this.authorUsers;
   }

   public void setReaderUsers( ArrayList<String> readerUsers ){
      this.readerUsers = readerUsers;
   }
   public ArrayList<String> getReaderUsers(){
      return this.readerUsers;
   }

   public void setAdminGroups( ArrayList<String> adminGroups ){
      this.adminGroups = adminGroups;
   }
   public ArrayList<String> getAdminGroups(){
      return this.adminGroups;
   }

   public void setAuthorGroups( ArrayList<String> authorGroups ){
      this.authorGroups = authorGroups;
   }
   public ArrayList<String> getAuthorGroups(){
      return this.authorGroups;
   }

   public void setReaderGroups( ArrayList<String> readerGroups ){
      this.readerGroups = readerGroups;
   }
   public ArrayList<String> getReaderGroups(){
      return this.readerGroups;
   }

   public String pageCount()
   {
      return String.valueOf( pageCount );
   }

   public String spaceStatus()
   {
      String status = "Active";
      if( this.space != null && this.space.isArchived() ){
         status = "Archived";
      }
      return status;
   }

   public String lastModificationDate()
   {
      SimpleDateFormat format = new SimpleDateFormat( "yyyy.MM.dd" );
      String dateString = "Never modified";
      if( this.space != null ){
         dateString = format.format( this.mostRecentModificationDate );
      }
      return( dateString );
   }

   public String daysSinceLastLogin()
   {
      Date today = new Date();
      String dateString = "Never modified";
      
      try{ 
         if( this.space != null ){
            long diff = (today.getTime() - this.mostRecentModificationDate.getTime())/86400000;
	    dateString = String.valueOf( diff );
         }
      } catch( Exception ex ){
         System.out.println( "Exception " + ex );
      }

      return( dateString );
   }
}
