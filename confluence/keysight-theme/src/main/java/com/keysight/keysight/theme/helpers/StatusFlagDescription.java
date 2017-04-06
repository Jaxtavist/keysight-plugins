package com.keysight.keysight.theme.helpers;

import java.util.Map;

public class StatusFlagDescription
{
    private String baseUrl;
    private String color;
    private String style;
    private String image;
    private String altText;
    private int height;
    private int width;
    private boolean isFlag;

    public final static String BAR            = "bar";
    public final static String TRACK          = "track";
    public final static String STYLE          = "style";
    public final static String COLOR          = "color";

    public final static String GREY           = "Grey";
    public final static String GREEN          = "Green";
    public final static String YELLOW         = "Yellow";
    public final static String RED            = "Red";
    public final static String BLUE           = "Blue";
    public final static String BLACK          = "Black";
    public final static String WHITE          = "White";
    public final static String[] COLORS = new String[]{ GREY,
                                                        GREEN,
                                                        YELLOW,
                                                        RED,
                                                        BLUE,
                                                        BLACK,
                                                        WHITE }; 

    public final static String LARGE_FLAG_STYLE        = "Large Flag";
    public final static String MEDIUM_FLAG_STYLE       = "Medium Flag";
    public final static String SMALL_FLAG_STYLE        = "Small Flag";
    public final static String ROUNDED_RECTANGLE_STYLE = "Rounded Rectangle";
    public final static String ON_TRACK_STYLE          = "On-Track";
    public final static String[] STYLES = new String[]{ LARGE_FLAG_STYLE, 
                                                        MEDIUM_FLAG_STYLE,
                                                        SMALL_FLAG_STYLE,
                                                        ROUNDED_RECTANGLE_STYLE,
                                                        ON_TRACK_STYLE };


    public final static int FLAG_SMALL_SIZE = 24;
    public final static int FLAG_MEDIUM_SIZE = 48;
    public final static int FLAG_LARGE_SIZE = 72;
    public final static int BAR_WIDTH = 88;
    public final static int BAR_HEIGHT = 18;

    public final static String DEFAULT_STYLE = ROUNDED_RECTANGLE_STYLE;
    public final static String DEFAULT_COLOR = GREY;

    private static final String RESOURCE_DIR = "/download/resources/com.keysight.keysight-theme:keysight-theme-resources/images/StatusIcons";

    public StatusFlagDescription()
    {
       this.setBaseUrl( "http://localhost:1990/confluence" );
       this.setStyleAndColor( DEFAULT_STYLE, DEFAULT_COLOR );
    }

    public StatusFlagDescription( String baseUrl )
    {
       this.setBaseUrl( baseUrl );
       this.setStyleAndColor( DEFAULT_STYLE, DEFAULT_COLOR );
    }

    public StatusFlagDescription( String baseUrl, String style, String color )
    {
       this.setBaseUrl( baseUrl );
       this.setStyleAndColor( style, color );
    }

    public StatusFlagDescription( String baseUrl, Map<String, String> parameters )
    {
       String style = DEFAULT_STYLE;
       if (parameters.containsKey(STYLE)){
          style = parameters.get(STYLE);
       }

       String color = DEFAULT_COLOR;
       if (parameters.containsKey(COLOR)){
          color = parameters.get(COLOR);
       }

       this.setBaseUrl( baseUrl );
       this.setStyleAndColor( style, color );
    }

    public void setBaseUrl( String baseUrl ){
       this.baseUrl = baseUrl;
    }
    public String getBaseUrl(){
        return this.baseUrl;
    }

    public void setIsFlag( boolean isFlag ){
       this.isFlag = isFlag;
    }
    public boolean getIsFlag(){
        return this.isFlag;
    }

    public void setStyleAndColor( String style, String color )
    {
       boolean colorDefined = false;
       boolean styleDefined = false;

       for( String knownColor : COLORS ){
          if( color.equals( knownColor ) ){ colorDefined = true; }
       }

       for( String knownStyle : STYLES ){
          if( style.equals( knownStyle ) ){ styleDefined = true; }
       }

       if( !styleDefined && !colorDefined ){
          throw new IllegalArgumentException( "Values for style and color are not in the lists of known values." );
       } else if( !styleDefined ){
          throw new IllegalArgumentException( "Value for style is not in the list of known values." );
       } else if( !colorDefined ){
          throw new IllegalArgumentException( "Value for color is not in the list of known values." );
       }

       this.style = style;
       this.color = color;

       if( style.equals( LARGE_FLAG_STYLE ) ){
          this.setImage( getFlagByColorAndSize(color, FLAG_LARGE_SIZE) );
          this.setAltText( color );
          this.setIsFlag( true );
          this.setWidth( FLAG_LARGE_SIZE );
          this.setHeight( FLAG_LARGE_SIZE );
        } else if( style.equals( MEDIUM_FLAG_STYLE ) ){
          this.setImage( getFlagByColorAndSize(color, FLAG_MEDIUM_SIZE) );
          this.setAltText( color );
          this.setIsFlag( true );
          this.setWidth( FLAG_MEDIUM_SIZE );
          this.setHeight( FLAG_MEDIUM_SIZE );
        } else if( style.equals( SMALL_FLAG_STYLE ) ){
          this.setImage( getFlagByColorAndSize(color, FLAG_SMALL_SIZE) );
          this.setAltText( color );
          this.setIsFlag( true );
          this.setWidth( FLAG_SMALL_SIZE );
          this.setHeight( FLAG_SMALL_SIZE );
        } else if( style.equals( ROUNDED_RECTANGLE_STYLE ) ){
          this.setImage( getBarByColorAndType(color, BAR) );
          this.setAltText( color );
          this.setIsFlag( false );
          this.setWidth( BAR_WIDTH );
          this.setHeight( BAR_HEIGHT );
        } else if( style.equals( ON_TRACK_STYLE ) ){
          this.setImage( getBarByColorAndType(color, TRACK) );
          if( color.equals( GREY ) ){
              this.setAltText( "Pre-CON" );
          } else if( color.equals( GREEN ) ){
              this.setAltText( "On Track" );
          } else if( color.equals( YELLOW ) ){
              this.setAltText( "On Track" );
          } else if( color.equals( RED ) ){
              this.setAltText( "Off Track" );
          } else if( color.equals( BLUE ) ){
              this.setAltText( "On Hold" );
          } else {
              this.setAltText( color );
          }
          this.setIsFlag( false );
          this.setWidth( BAR_WIDTH );
          this.setHeight( BAR_HEIGHT );
        } else {
          this.setImage( getBarByColorAndType(color, BAR) );
          this.setAltText( color );
          this.setIsFlag( false );
          this.setWidth( BAR_WIDTH );
          this.setHeight( BAR_HEIGHT );
        }
    }

    public String span()
    {
       return String.format( "<span class=\"keysight-status-flag keysight-status-%s\">%s</span>",
                             this.getColor(), this.getAltText() );
    }

    public String html() 
    {
       baseUrlIsValid();
       String html;
       if( this.getIsFlag() ){
          html = this.img();
       } else {
          html = this.span();
       }
       return html;
    }

    public String img() 
    {
       baseUrlIsValid();
       return String.format( "<img src=\"%s%s/%s\" alt=\"%s\" height=\"%d\" width=\"%d\"/>",
                              this.getBaseUrl(),
                              RESOURCE_DIR, 
                              this.getImage(), 
                              this.getAltText(), 
                              this.getHeight(), 
                              this.getWidth() );
    }

    public String url() 
    {
       baseUrlIsValid();
       return String.format( "%s%s/%s", this.getBaseUrl(), RESOURCE_DIR, this.getImage() );
    }

    private boolean baseUrlIsValid(){
       if( this.getBaseUrl() == null ){
          throw new IllegalArgumentException( "Baseurl is not set.  Cannot generate html." );
       } else {
          return true;
       }
    }

    public String getColor(){
        return this.color;
    }
    public String getStyle(){
        return this.style;
    }

    public void setImage( String image ){
       this.image = image;
    }
    public String getImage(){
        return this.image;
    }

    public void setAltText( String altText ){
       this.altText = altText;
    }
    public String getAltText(){
        return this.altText;
    }

    public void setHeight( int height ){
       this.height = height;
    }
    public int getHeight(){
        return this.height;
    }

    public void setWidth( int width ){
       this.width = width;
    }
    public int getWidth(){
        return this.width;
    }

    public static String getFlagByColorAndSize( String color, int size )
    {
         return String.format( "flag-%s-%dx%d.png", color, size, size );
    }

    public static String getBarByColorAndType( String color, String type )
    {
         return String.format( "%s-%s-%dx%d.png", type, color, BAR_WIDTH, BAR_HEIGHT );
    }

}
