package com.keysight.html.elements.helpers;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.core.util.XMLUtils;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.lang.StringBuilder;

import org.apache.commons.lang.RandomStringUtils;

public class TabInfo
{
   private String    title  = null;
   private boolean   active = false;
   private String    tabId  = null;
   private String    paneId = null;
   private int       leftTitleTruncationCount = 0;
   private int       rightTitleTruncationCount = 0;
   private String    body   = null;
   private String    authorFullName = null;
   private String    authorUserName = null;
   private Date      creationDate = null;
   private Boolean   stalePage = null;
   private SimpleDateFormat dateFormat;
   private String    pageUrl = "";

   public TabInfo( String title, String body ){
      this.title = title;
      this.body  = body;
      this.stalePage = false;
      this.creationDate = new Date();
      this.dateFormat = new SimpleDateFormat( "E MMM dd, yyyy" );

      this.setAuthorFullName( "Anonymous" );
      this.setAuthorUserName( "anonymous" );

      this.tabId  = "aui-tab-uid-" + RandomStringUtils.randomAlphanumeric( 16 );
      this.paneId = "aui-pane-uid-" + RandomStringUtils.randomAlphanumeric( 16 );
   }

   public void setCreationDate( Date date ){
      this.creationDate = date;
   }
   public Date getCreationDate(){
      return this.creationDate;
   }
   public String showCreationDate(){
      return this.dateFormat.format( this.creationDate );
   }

   public void setAuthorFullName( String author ){
      if( author != null && author.matches( ".*\\w.*" )){
         this.authorFullName = author;
      }
   }
   public String getAuthorFullName(){
      return this.authorFullName;
   }

   public void setAuthorUserName( String author ){
      if( author != null && author.matches( ".*\\w.*" )){
         this.authorUserName = author;
      }
   }
   public String getAuthorUserName(){
      return this.authorUserName;
   }

   public void setPageUrl( String url ){
      if( url != null && url.matches( ".*\\w.*" ) ){
         this.pageUrl = url;
      }
   }
   public String getPageUrl(){
      return this.pageUrl;
   }

   public void setStaleFlag( boolean isStale ){
      this.stalePage = isStale;
   }
   public boolean getStaleFlag(){
      return this.stalePage;
   }

   public void setTitle( String title ){
      this.title = title;
   }
   public String getTitle(){
      return this.title;
   }
   
   public void setActive( boolean active ){
      this.active = active;
   }
   public boolean getActive(){
      return this.active;
   }
   public boolean isActive(){
      return this.getActive();
   }

   public void setTabId( String id ){
      this.tabId = id;
   }
   public String getTabId(){
      return this.tabId;
   }
   
   public void setPaneId( String id ){
      this.paneId = id;
   }
   public String getPaneId(){
      return this.paneId;
   }
   
   public void setBody( String body ){
      this.body = body;
   }
   public String getBody(){
      return this.body;
   }

   public void setLeftTitleTruncationCount( String count ){
      this.leftTitleTruncationCount = Integer.parseInt(count);
   }
   public void setLeftTitleTruncationCount( int count ){
      this.leftTitleTruncationCount = count;
   }
   public int getLeftTitleTruncationCount(){
      return this.leftTitleTruncationCount;
   }
   
   public void setRightTitleTruncationCount( String count ){
      this.rightTitleTruncationCount = Integer.parseInt(count);
   }
   public void setRightTitleTruncationCount( int count ){
      this.rightTitleTruncationCount = count;
   }
   public int getRightTitleTruncationCount(){
      return this.rightTitleTruncationCount;
   }

   public String title(){
      String title = this.getTitle();
      if( this.leftTitleTruncationCount > 0 || this.rightTitleTruncationCount > 0 ){
         title = title.substring( this.leftTitleTruncationCount, title.length() - this.rightTitleTruncationCount );
      }
      return( title );
   }
 
   @HtmlSafe  
   public String body(){
      return this.getBody();
   }
   public String tabId(){
      return this.getTabId();
   }
   public String paneId(){
      return this.getPaneId();
   }

   public String activeTab(){
      String value = "";
      if( this.isActive() ){ value = "active-tab"; }
      return( value );
   }
   
   public String activePane(){
      String value = "";
      if( this.isActive() ){ value = "active-pane"; }
      return( value );
   }
   
   public String ariaSelected(){
      String value = "false";
      if( this.isActive() ){ value = "true"; }
      return value;
   }
   public String ariaHidden(){
      String value = "true";
      if( this.isActive() ){ value = "false"; }
      return value;
   }
}
