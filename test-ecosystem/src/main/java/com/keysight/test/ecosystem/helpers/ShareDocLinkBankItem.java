package com.keysight.test.ecosystem.helpers;

public class ShareDocLinkBankItem
{
    private String url     = null;
    private String link    = null;
    private String infoUrl = "#";

    public ShareDocLinkBankItem(){
    }

    public ShareDocLinkBankItem( String url ){
       this.url     = url;
    }    
    
    public ShareDocLinkBankItem( String url, String link ){
       this.url     = url;
       this.link    = link;
    }    
    
    public ShareDocLinkBankItem( String url, String link, String infoUrl ){
       this.url     = url;
       this.link    = link;
       this.infoUrl = infoUrl;
    }    

    public void setUrl( String url ){
       this.url = url;
    }
    public String getUrl(){
       return this.url;
    }
    
    public void setInfoUrl( String infoUrl ){
       this.infoUrl = infoUrl;
    }
    public String getInfoUrl(){
       return this.infoUrl;
    }

    public void setLink( String link ){
       this.link = link;
    }
    public String getLink(){
       if( this.link == null ){
          return this.url;
       } else {
          return this.link;
       }
    }
    
    public String pdfUrl(){
       String pdfUrl = "";
       String url = this.getUrl();
       if( url.contains( "." ) ){
          pdfUrl = url.substring(0, url.lastIndexOf('.')) + ".pdf";
       }
       return(pdfUrl);
    }

    public String cssClass(){
       String url = this.getUrl().toLowerCase();
       String cssClass = "";
       String brokenLink = "broken-link";
       if( url.matches( ".*agilent.com.*" ) ||
           url.matches( ".*webdoc.*" ) || 
           !url.matches( ".*http://.*" ) ){
          cssClass = brokenLink;
       }

       return cssClass;
    }
}
