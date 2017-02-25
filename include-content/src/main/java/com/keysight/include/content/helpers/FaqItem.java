package com.keysight.include.content.helpers;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class FaqItem
{
   @XmlElement private String pageId = null;
   @XmlElement private String pageTitle = null;
   @XmlElement private String pageUrl = null;
   @XmlElement private String spaceKey = null;
   @XmlElement private String spaceName = null;
   @XmlElement private String spaceUrl = null;
   @XmlElement private String lastModificationTimeShort = null;
   @XmlElement private String lastModificationTimeLong = null;
   @XmlElement private String lastModificationDateAsString = null;
   @XmlElement private String lastModificationDaysAgoAsString = null;
   @XmlElement private String lastModificationAuthorName = null;
   @XmlElement private String lastModificationAuthorUserName = null;
   @XmlElement private ArrayList<String> labels;
   @XmlElement private String likeCount = "?";

   private String baseUrl                               = null;
   private boolean includeSpace                          = false;
   private boolean includeLastModificationDate           = false;
   private boolean includeLikes                          = false;
   private boolean includeLabels                         = false;
   private boolean includeLastModificationAuthor         = false;
   private boolean includeLastModificationAuthorName     = false;
   private boolean includeLastModificationAuthorUserName = false;

   private Page page;
   private enum Style { PAGE, EXCERPT, SHARED_BLOCK }

   private final long MILLISECONDS_PER_MINUTE = 1000 * 60;
   private final long MILLISECONDS_PER_HOUR = 60 * MILLISECONDS_PER_MINUTE;
   private final long MILLISECONDS_PER_DAY = 24 * MILLISECONDS_PER_HOUR;
   private final SimpleDateFormat SHORT_DATE_FORMAT = new SimpleDateFormat( "EEE MMM d, YYYY" );
   private final SimpleDateFormat LONG_DATE_FORMAT = new SimpleDateFormat( "EEEE MMMM d, YYYY" );

   public FaqItem( ){
      this.Initialize();
   }
   
   public FaqItem( Page page, String likeCount ){
      this.Initialize();
      this.DiscoverPageInfo( page );

      this.setLikeCount( likeCount );
   }

   private void Initialize(){
      labels = new ArrayList<String>();
   }

   private void DiscoverPageInfo(Page page)
   {
      if( page != null )
      {
         this.setPage( page );
         this.setPageId( page.getIdAsString() );
         this.setPageTitle( page.getTitle() );
         this.setPageUrl( page.getUrlPath() );
         this.setSpaceKey( page.getSpaceKey() );
         this.setSpaceName( page.getSpace().getName() );
         this.setSpaceUrl( page.getSpace().getUrlPath() );
         this.setLastModificationDateAsString( computeLastModificationDateAsString( page.getLastModificationDate()) );
         this.setLastModificationDaysAgoAsString( computeLastModificationDaysAgoAsString( page.getLastModificationDate()) );
         this.setLastModificationTimeShort( computeLastModificationTimeShort( page.getLastModificationDate()) );
         this.setLastModificationTimeLong( computeLastModificationTimeLong( page.getLastModificationDate()) );

         ConfluenceUser lastModifier = page.getLastModifier();
         this.setLastModificationAuthorUserName( lastModifier.getName() );
         this.setLastModificationAuthorName( lastModifier.getFullName() );

         for( Label label : page.getLabels() ){
            labels.add( label.toString() );
         }
         this.setLabels( labels );
      }
   }

   public void setBaseUrl( String baseUrl ){
      this.baseUrl = baseUrl;
   }
   public String getBaseUrl(){
      return this.baseUrl;
   }

   public void setPage( Page page ){
      this.page = page;
   }
   public Page getPage(){
      return this.page;
   }

   public void setPageUrl( String pageUrl ){
      this.pageUrl = pageUrl;
   }
   public String getPageUrl(){
      return this.pageUrl;
   }

   public void setPageId( String pageId ){
      this.pageId = pageId;
   }
   public String getPageId(){
      return this.pageId;
   }

   public void setPageTitle( String pageTitle ){
      this.pageTitle = pageTitle;
   }
   public String getPageTitle(){
      return this.pageTitle;
   }

   public void setSpaceUrl( String spaceUrl ){
      this.spaceUrl = spaceUrl;
   }
   public String getSpaceUrl(){
      return this.spaceUrl;
   }

   public void setSpaceKey( String spaceKey ){
      this.spaceKey = spaceKey;
   }
   public String getSpaceKey(){
      return this.spaceKey;
   }

   public void setSpaceName( String spaceName ){
      this.spaceName = spaceName;
   }
   public String getSpaceName(){
      return this.spaceName;
   }

   public void setLastModificationDateAsString( String lastModificationDateAsString ){
      this.lastModificationDateAsString = lastModificationDateAsString;
   }
   public String getLastModificationDateAsString(){
      return this.lastModificationDateAsString;
   }

   public void setLastModificationTimeShort( String lastModificationTimeShort ){
      this.lastModificationTimeShort = lastModificationTimeShort;
   }
   public String getLastModificationTimeShort(){
      return this.lastModificationTimeShort;
   }

   public void setLastModificationTimeLong( String lastModificationTimeLong ){
      this.lastModificationTimeLong = lastModificationTimeLong;
   }
   public String getLastModificationTimeLong(){
      return this.lastModificationTimeLong;
   }

   public void setLastModificationDaysAgoAsString( String lastModificationDaysAgoAsString ){
      this.lastModificationDaysAgoAsString = lastModificationDaysAgoAsString;
   }
   public String getLastModificationDaysAgoAsString(){
      return this.lastModificationDaysAgoAsString;
   }

   public void setLastModificationAuthorUserName( String lastModificationAuthorUserName ){
      this.lastModificationAuthorUserName = lastModificationAuthorUserName;
   }
   public String getLastModificationAuthorUserName(){
      return this.lastModificationAuthorUserName;
   }

   public void setLastModificationAuthorName( String lastModificationAuthorName ){
      this.lastModificationAuthorName = lastModificationAuthorName;
   }
   public String getLastModificationAuthorName(){
      return this.lastModificationAuthorName;
   }

   public void setLabels( ArrayList<String> labels ){
      this.labels = labels;
   }
   public ArrayList<String> getLabels(){
      return this.labels;
   }

   public void setLikeCount( String likeCount ){
      this.likeCount = likeCount;
   }
   public String getLikeCount(){
      return this.likeCount;
   }

   public void setIncludeFlags( boolean[] flags ){
      if( flags.length == 5 ){
         this.setIncludeSpace( flags[0] );
         this.setIncludeLastModificationDate( flags[1] );
         this.setIncludeLastModificationAuthor( flags[2] );
         this.setIncludeLikes( flags[3] );
         this.setIncludeLabels( flags[4] );

         if( this.getIncludeLastModificationAuthor() ){
            this.setIncludeLastModificationAuthorName( true );
            this.setIncludeLastModificationAuthorUserName( true );
         }
      }     
   }

   public void setIncludeSpace( boolean flag ){
      this.includeSpace = flag;
   }
   public boolean getIncludeSpace(){
      return this.includeSpace;
   }

   public void setIncludeLastModificationDate( boolean flag ){
      this.includeLastModificationDate = flag;
   }
   public boolean getIncludeLastModificationDate(){
      return this.includeLastModificationDate;
   }

   public void setIncludeLikes( boolean flag ){
      this.includeLikes = flag;
   }
   public boolean getIncludeLikes(){
      return this.includeLikes;
   }

   public void setIncludeLabels( boolean flag ){
      this.includeLabels = flag;
   }
   public boolean getIncludeLabels(){
      return this.includeLabels;
   }

   public void setIncludeLastModificationAuthor( boolean flag ){
      this.includeLastModificationAuthor = flag;
   }
   public boolean getIncludeLastModificationAuthor(){
      return this.includeLastModificationAuthor;
   }

   public void setIncludeLastModificationAuthorName( boolean flag ){
      this.includeLastModificationAuthorName = flag;
   }
   public boolean getIncludeLastModificationAuthorName(){
      return this.includeLastModificationAuthorName;
   }

   public void setIncludeLastModificationAuthorUserName( boolean flag ){
      this.includeLastModificationAuthorUserName = flag;
   }
   public boolean getIncludeLastModificationAuthorUserName(){
      return this.includeLastModificationAuthorUserName;
   }

   public String computeLastModificationDateAsString(Date date)
   {
      String dateString = "Never modified";
      if( date != null ){
         dateString = SHORT_DATE_FORMAT.format( date );
      }
      return( dateString );
   }

   public String computeLastModificationDaysAgoAsString(Date date)
   {
      Date today = new Date();
      String dateString = "Never modified";
      
      if( date != null ){
         long diff = (today.getTime() - date.getTime())/MILLISECONDS_PER_DAY;
         dateString = String.valueOf( diff );
      }

      return( dateString );
   }

   public String computeLastModificationTimeShort(Date date)
   {
      Date today = new Date();
      String dateString = "Never modified";
      
      if( date != null ){
         long diff = (today.getTime() - date.getTime());
         
         if( diff < 1 * MILLISECONDS_PER_MINUTE ){
             dateString = "Less than a minute ago.";
         } else if( diff < 2 * MILLISECONDS_PER_MINUTE ){
             dateString = "a minute ago";
         } else if( diff < 60 * MILLISECONDS_PER_MINUTE ){
             dateString = String.format( "%d minutes ago", Math.round( diff/MILLISECONDS_PER_MINUTE ) );
         } else if( diff < 2 * MILLISECONDS_PER_HOUR ){
             dateString = "an hour ago";
         } else if( diff < 24 * MILLISECONDS_PER_HOUR ){
             dateString = String.format( "%d hours ago", Math.round( diff/MILLISECONDS_PER_HOUR ) );
         } else if( diff < 2 * MILLISECONDS_PER_DAY ){
             dateString = "a day ago";
         } else if( diff < 7 * MILLISECONDS_PER_DAY ){
             dateString = String.format( "%d days ago", Math.round( diff/MILLISECONDS_PER_DAY ) );
         } else {
             dateString = SHORT_DATE_FORMAT.format( date );
         }
      }

      return( dateString );
   }

   public String computeLastModificationTimeLong(Date date)
   {
      Date today = new Date();
      String dateString = "Never modified";
      
      if( date != null ){
         long diff = (today.getTime() - date.getTime());
         
         if( diff < 1 * MILLISECONDS_PER_MINUTE ){
             dateString = "Less than a minute ago.";
         } else if( diff < 2 * MILLISECONDS_PER_MINUTE ){
             dateString = "a minute ago";
         } else if( diff < 60 * MILLISECONDS_PER_MINUTE ){
             dateString = String.format( "%d minutes ago", Math.round( diff/MILLISECONDS_PER_MINUTE ) );
         } else if( diff < 2 * MILLISECONDS_PER_HOUR ){
             dateString = "an hour ago";
         } else if( diff < 24 * MILLISECONDS_PER_HOUR ){
             dateString = String.format( "%d hours ago", Math.round( diff/MILLISECONDS_PER_HOUR ) );
         } else if( diff < 2 * MILLISECONDS_PER_DAY ){
             dateString = "a day ago";
         } else if( diff < 7 * MILLISECONDS_PER_DAY ){
             dateString = String.format( "%d days ago", Math.round( diff/MILLISECONDS_PER_DAY ) );
         } else {
             dateString = "on " + LONG_DATE_FORMAT.format( date );
         }
      }

      return( dateString );
   }

   public String getExpandIncludedPageHtml( ){
      return getExpandHtml( Style.PAGE );
   }

   public String getExpandIncludedExcerptHtml( ){
      return getExpandHtml( Style.EXCERPT );
   }

   public String getExpandIncludedSharedBlockHtml( ){
      return getExpandHtml( Style.SHARED_BLOCK );
   }

   private String getExpandHtml( Style style )
   {
      StringBuilder html = new StringBuilder();

      if( page instanceof Page ){
         String pageTitle = escapeHtml( page.getTitle() );

         html.append( "<ac:structured-macro ac:name=\"expand\">" );
         html.append(    "<ac:parameter ac:name=\"title\">" + pageTitle + "</ac:parameter>" );
         html.append(    "<ac:rich-text-body><p>");

         if( this.getIncludeSpace() 
           || this.getIncludeLastModificationDate()
           || this.getIncludeLastModificationAuthor()
           || this.getIncludeLastModificationAuthorName()
           || this.getIncludeLastModificationAuthorUserName()
           || this.getIncludeLikes()
           || this.getIncludeLabels()
         ){

             // needed to keep the title and the metadata from overlapping
             html.append( "<br />\n" );

             html.append( "<div class=\"page-metadata\">" );
             html.append( "   <ul>\n" );

             if( this.getIncludeSpace() ){
                 html.append( "      <li class=\"page-metadata-modification-info\">\n" );
                 html.append( "          From Space \"<a href=\"" + this.getBaseUrl() + this.getSpaceUrl() + "\">"+this.getSpaceName()+"</a>\".\n" );
                 html.append( "      </li>\n");
             }

             if( this.getIncludeLastModificationDate() ){
                 html.append( "      <li class=\"page-metadata-modification-info\">\n" );
                 html.append( "       Last modified " + this.getLastModificationTimeLong() );

                 if( this.getIncludeLastModificationAuthorName() && !this.getIncludeLastModificationAuthorUserName() ){
                     html.append( "." );
                 }
                 html.append( "      </li>\n" );
             }

             if( this.getIncludeLastModificationAuthorName() || this.getIncludeLastModificationAuthorUserName() ){
                 html.append( "      <li class=\"page-metadata-modification-info\">\n" );
                 if( this.getIncludeLastModificationDate() ){
                     html.append( "by&nbsp;" );
                 } else { 
                     html.append( "Last modified by&nbsp;" );
                 }

                 if( this.getIncludeLastModificationAuthorName() && this.getIncludeLastModificationAuthorUserName() ){
                     html.append( this.getLastModificationAuthorName() + " (" + this.getLastModificationAuthorUserName() + ").\n" );
                 } else if( this.getIncludeLastModificationAuthorName() ){
                     html.append( this.getLastModificationAuthorName() + ".\n" );
                 } else if( this.getIncludeLastModificationAuthorUserName() ){
                     html.append( this.getLastModificationAuthorUserName() + ".\n" );
                 }
                 html.append( "      </li>\n" );
             }

             if( this.getIncludeLikes() ){
                 html.append( "      <li class=\"page-metadata-modification-info\">\n" );
                 html.append( "      Likes: " + this.getLikeCount() + "\n" );
                 html.append( "      </li>\n" );
             }

             html.append( "   </ul>\n" );
             html.append( "</div>\n" );

             if( this.getIncludeLabels() ){
                 html.append( "<div class=\"page-metadata\">" );
                 html.append( "   Labels:\n" );

                 // this part matches the soy generated results; but it doesn't export well to pdf
                 //html.append( "   <ul>\n" );
                 //html.append( "      <li class=\"page-metadata-modification-info\">\n" );
                 //html.append( "      Labels:\n" );
                 //html.append( "      </li>\n" );

                 html.append( "   <ul class=\"label-list\">\n" );
                 for( Label label : page.getLabels() ){
                     html.append( "   <li class=\"aui-label keysight-page-info-label\">"+label.toString()+"</li>\n" );
                 }
                 html.append( "   </ul>\n" );
                 //html.append( "   </ul>\n" );
                 html.append( "</div>\n" );
             }
         }
      
         if( style == Style.SHARED_BLOCK ){
             html.append( "<ac:structured-macro ac:name=\"include-shared-block\">" );
             html.append(    "<ac:parameter ac:name=\"page\">" );
             html.append(       "<ac:link>" );
             html.append(          "<ri:page ri:content-title=\"" + pageTitle + "\" ri:space-key=\"" + page.getSpaceKey() + "\"/>" );
             html.append(       "</ac:link>" );
             html.append(    "</ac:parameter>" );
             html.append( "</ac:structured-macro>" );
         } else if ( style == Style.EXCERPT ){
             html.append( "<ac:structured-macro ac:name=\"excerpt-include\">" );
             html.append(    "<ac:parameter ac:name=\"nopanel\">true</ac:parameter>" );
             html.append(    "<ac:parameter ac:name=\"\">" );
             html.append(       "<ac:link>" );
             html.append(          "<ri:page ri:content-title=\"" + pageTitle + "\" ri:space-key=\"" + page.getSpaceKey() + "\"/>" );
             html.append(       "</ac:link>" );
             html.append(    "</ac:parameter>" );
             html.append( "</ac:structured-macro>" );
             html.append(          "<ri:page ri:content-title=\"" + pageTitle + "\" ri:space-key=\"" + page.getSpaceKey() + "\"/>" );
         } else {
             html.append( "<ac:structured-macro ac:name=\"include\">" );
             html.append(    "<ac:parameter ac:name=\"\">" );
             html.append(       "<ac:link>" );
             html.append(          "<ri:page ri:content-title=\"" + pageTitle + "\" ri:space-key=\"" + page.getSpaceKey() + "\"/>" );
             html.append(       "</ac:link>" );
             html.append(    "</ac:parameter>" );
             html.append( "</ac:structured-macro>" );
         }

         html.append(    "</p></ac:rich-text-body>");
         html.append( "</ac:structured-macro>" );
      }

      return html.toString();
   }
}
