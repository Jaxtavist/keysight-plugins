package com.keysight.html.elements.helpers;

import java.util.ArrayList;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;

public class FormElement
{
   private String definition   = "";
   private String key          = "";
   private String keyLabel     = null;
   private String selectedItem = "";
   private ArrayList<String> selections = new ArrayList<String>();
   private ArrayList<String> labels     = new ArrayList<String>();
   boolean hidden              = false;
   private String seperator    = null;

   public FormElement(){
   }

   public FormElement( String definition ){
      this.definition = definition;
      this.parseDefinition();
   }

   public void setDefinition( String definition ){
      this.definition = definition;
   }
   public String getDefinition(){
      return this.definition;
   }

   public void setSeperator( String seperator ){
      this.seperator = seperator;
   }
   public String getSeperator(){
      return this.seperator;
   }

   private void parseDefinition(){
      String elements[] = StringUtils.strip(this.definition).split(":", 2);
      String choices[] = null;
      String buffer[]  = null;
      String temp      = null;
      this.selections = new ArrayList<String>();
      this.labels     = new ArrayList<String>();
      this.hidden     = false;

      if( !StringUtils.isEmpty(elements[0]) ){
         elements[0] = StringUtils.strip( elements[0] );
	 if( elements[0].startsWith("*") ){
            elements[0] = elements[0].substring(1);
            this.hidden = true;
	 }

         buffer = elements[0].split("->", 2);
	 if( buffer.length > 1 && !StringUtils.isEmpty( buffer[1] )){
	    this.key      = buffer[0];
	    this.keyLabel = buffer[1];
	 } else {
	    this.key      = buffer[0];
	 }
      }
      
      if( elements.length > 1 && !StringUtils.isEmpty( elements[1] ) ){
	 choices = StringUtils.strip(elements[1]).split(","); 
         for( String choice : choices ){
            buffer = choice.split("->", 2);

	    if( buffer[0].startsWith( "!" ) ){
               buffer[0] = buffer[0].substring(1);
	       this.selectedItem = buffer[0];
	    }

	    if( buffer.length > 1 && !StringUtils.isEmpty( buffer[1] )){
               this.selections.add( StringUtils.strip(buffer[0]) );
               this.labels.add( StringUtils.strip(buffer[1]) );
	    } else {
               this.selections.add( StringUtils.strip(buffer[0]) );
               this.labels.add( StringUtils.strip(buffer[0]) );
	    }
	 }
      }
   }

   @HtmlSafe  
   public String html(){
      StringBuilder html = new StringBuilder();
      String selected = "";

      if( !StringUtils.isEmpty( this.key ) ){ 
	 if( this.keyLabel != null ){
            html.append( this.keyLabel + " " );
	 }
	 
	 if( this.hidden && this.selections.size() > 0 ){
	    html.append( "<input type=\"hidden\" name=\""+this.key+"\" value=\""+this.selections.get(0)+"\" />\n" );
	 } else if( this.selections.size() > 0 ){
            html.append( "<select name=\"" + this.key + "\">\n" );
            for( int i = 0; i < this.selections.size(); i++ ){
               if( this.selections.get(i).matches( this.selectedItem ) ){
                  selected = "selected";
	       } else {
                  selected = "";
	       }
               html.append( "<option value=\"" + this.selections.get(i) + "\" " + selected + ">" + this.labels.get(i) + "</option>\n" );
            }
            html.append( "</select>\n" );
	 } else {
	    html.append( "<input type=\"text\" name=\""+this.key+"\" />\n" );
	 }
	 if( this.seperator != null ){
            html.append( this.seperator );
	 }
      }
      return html.toString();
   }

   @HtmlSafe  
   public String htmlAui(){
      StringBuilder html = new StringBuilder();
      String selected = "";

      if( !StringUtils.isEmpty( this.key ) ){ 
         if( this.hidden && this.selections.size() > 0 ){
            html.append( "<input type=\"hidden\" name=\""+this.key+"\" value=\""+this.selections.get(0)+"\" />\n" );
         } else if( this.selections.size() > 0 ){
            html.append( "<div class=\"field-group\">\n" );
            if( this.keyLabel != null ){
               html.append( "<label for=\""+ this.key + "\">" + this.keyLabel + "</label>\n" );
            }
            html.append( "<select class=\"select\" name=\"" + this.key + "\" id=\""+this.key+"\" >\n" );
            for( int i = 0; i < this.selections.size(); i++ ){
               if( this.selections.get(i).matches( this.selectedItem ) ){
                  selected = "selected";
               } else {
                  selected = "";
               }
               html.append( "<option value=\"" + this.selections.get(i) + "\" " + selected + ">" + this.labels.get(i) + "</option>\n" );
            }
            html.append( "</select>\n" );
            //html.append( "<div class=\"description\">My Description</div>\n" );
            html.append( "</div>\n" );
         } else {
            html.append( "<div class=\"field-group\">\n" );
            if( this.keyLabel != null ){
               html.append( "<label for=\""+ this.key + "\">" + this.keyLabel + "</label>\n" );
            }
            
            html.append( "<input class=\"text\" type=\"text\" id=\""+this.key+"\" name=\""+this.key+"\" />\n" );
            //html.append( "<div class=\"description\">My Description</div>\n" );
            html.append( "</div>\n" );
         }
      }
      return html.toString();
   }
}
