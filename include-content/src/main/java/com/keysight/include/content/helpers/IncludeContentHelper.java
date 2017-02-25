package com.keysight.include.content.helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IncludeContentHelper
{
    public static String replaceText( String originalText, String delimiter, String replacementText )
    {
       String lines[] = null;
       String parts[] = null;

       delimiter = Pattern.quote( delimiter );
       Pattern pattern = Pattern.compile( delimiter + "(.*)" + delimiter + "(.*)" + delimiter );
       lines = replacementText.split("\n");
       for( String line : lines ){
          Matcher matcher = pattern.matcher( line );
          if( matcher.find() ){
             originalText = originalText.replaceAll( matcher.group(1), matcher.group(2) );
          }
       }
       return originalText;
    }

}
