package com.keysight.plugins.confluence.special.handling.properties.macros;

import java.net.URL;
import java.net.MalformedURLException;

public class DocumentReference
{
   private URL url = null;
   
   public DocumentReference( URL url ){
      setUrl( url );
   }
   public DocumentReference( String url ) throws MalformedURLException{
      setUrl( url );
   }

   public void setUrl( URL url ){
      this.url = url;
   }
   public void setUrl( String url ) throws MalformedURLException {
      try{
         this.url = new URL(url);
      } catch(MalformedURLException e){ }
   }
   public URL getUrl(){
      return this.url;
   }

   public String getLastComponent(){
      String path = this.url.getPath();
      return path.substring( path.lastIndexOf("/") + 1 );
   }
}
