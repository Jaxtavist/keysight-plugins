package com.keysight.html.elements.helpers;

import java.util.ArrayList;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;

public class SubmitSeperator
{
   private String seperator = null;

   public SubmitSeperator(){
   }

   public SubmitSeperator( String seperator ){
      this.seperator = seperator;
   }

   public void setSeperator( String seperator ){
      this.seperator = seperator;
   }
   @HtmlSafe  
   public String getSeperator(){
      return this.seperator;
   }
}
