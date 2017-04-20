package com.keysight.jira.support.helpers;

import java.util.ArrayList;
import java.util.Date;
import java.lang.StringBuilder;
import com.atlassian.core.util.XMLUtils;
import java.text.SimpleDateFormat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ActiveUserInfo
{
   private Date lastSuccessfulLoginDate = null;

   static final private String TRUE = "true";
   static final private String FALSE = "false";

   @XmlElement private String username = "";
   @XmlElement private String fullname = "";
   @XmlElement private String lastLoginDate = "";
   @XmlElement private String xDaysAgo = "";
   @XmlElement private String jiraCoreLicense = "false";
   @XmlElement private String jiraSoftwareLicense = "false";
   @XmlElement private String jiraServiceDeskLicense = "false";
   
   public ActiveUserInfo( ){
   }
   
   public ActiveUserInfo( String username ){
      this.username = username;
   }

   public ActiveUserInfo( String username, String fullname ){
      this.username = username;
      this.fullname = fullname;
   }

   public ActiveUserInfo( String username, String fullname, Date lastSuccessfulLoginDate ){
      this.username = username;
      this.fullname = fullname;
      this.lastSuccessfulLoginDate = lastSuccessfulLoginDate;
      this.lastLoginDate = this.lastLoginDate();
      this.xDaysAgo = this.daysSinceLastLogin();
   }

   public ActiveUserInfo( String username, String fullname, Long lastLoginTime ){
      this.username = username;
      this.fullname = fullname;
      if( lastLoginTime != null && lastLoginTime > 500 ){
         this.lastSuccessfulLoginDate = new Date( lastLoginTime );
      }
      this.lastLoginDate = this.lastLoginDate();
      this.xDaysAgo = this.daysSinceLastLogin();
   }
   
   public void setJiraServiceDeskLicense( boolean flag ){
      if( flag ){
         this.jiraServiceDeskLicense = TRUE;
      } else {
         this.jiraServiceDeskLicense = FALSE;
      }
   }
   public boolean getJiraServiceDeskLicense(){
      if( this.jiraServiceDeskLicense.equals( TRUE ) ){
         return true;
      } else {
         return false;
      }
   }

   public void setJiraCoreLicense( boolean flag ){
      if( flag ){
         this.jiraCoreLicense = TRUE;
      } else {
         this.jiraCoreLicense = FALSE;
      }
   }
   public boolean getJiraCoreLicense(){
      if( this.jiraCoreLicense.equals( TRUE ) ){
         return true;
      } else {
         return false;
      }
   }

   public void setJiraSoftwareLicense( boolean flag ){
      if( flag ){
         this.jiraSoftwareLicense = TRUE;
      } else {
         this.jiraSoftwareLicense = FALSE;
      }
   }
   public boolean getJiraSoftwareLicense(){
      if( this.jiraSoftwareLicense.equals( TRUE ) ){
         return true;
      } else {
         return false;
      }
   }

   public void setUsername( String username ){
      this.username = username;
   }
   public String getUsername(){
      return this.username;
   }
   public void setFullname( String fullname ){
      this.fullname = fullname;
   }
   public String getFullname(){
      return this.fullname;
   }
   public void setLastSuccessfulLoginDate( Date lastSuccessfulLoginDate  ){
      this.lastSuccessfulLoginDate = lastSuccessfulLoginDate;
   }
   public Date getLastSuccessfulLoginDate(){
      return this.lastSuccessfulLoginDate;
   }

   public String lastLoginDate()
   {
      SimpleDateFormat format = new SimpleDateFormat( "yyyy.MM.dd" );
      String dateString = "Never";
      if( this.lastSuccessfulLoginDate != null ){
         dateString = format.format( this.lastSuccessfulLoginDate );
      }
      return( dateString );
   }

   public String daysSinceLastLogin()
   {
      Date today = new Date();
      String dateString = "Never Logged in";
      
      try{ 
         if( this.lastSuccessfulLoginDate != null ){
            long diff = (today.getTime() - this.lastSuccessfulLoginDate.getTime())/86400000;
	    dateString = String.valueOf( diff );
         }
      } catch( Exception ex ){
         System.out.println( "Exception " + ex );
      }

      return( dateString );
   }

   /*
   public String getDaysAgo(){
      long days = (new Date().getTime() - page.getLastModificationDate().getTime()) / 86400000;

      if( days == 0 ) return "today";
      else if( days == 1) return "yesterday";
      else return days + " days ago";
   }
   */
}
