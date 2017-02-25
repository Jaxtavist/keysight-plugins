package com.keysight.keysight.theme.helpers;

import java.util.Map;

public class KeysightPlcCheckpointDescription
{
    private String baseUrl;
    private String checkpoint;
    private String size;
    private int height;
    private int width;

    private static final String RESOURCE_DIR = "/download/resources/com.keysight.keysight-theme:keysight-theme-resources/images/CheckpointIcons";

    public final static String CHECKPOINT = "checkpoint";
    public final static String SIZE       = "size";
    public final static String SMALL      = "Small";
    public final static String LARGE      = "Large";
    public final static String[] SIZES    = new String[]{ SMALL, LARGE };

    public final static String BLANK      = "BLANK";
    public final static String PRECON = "PRECON";
    public final static String CON = "CON";
    public final static String INV = "INV";
    public final static String DEF = "DEF";
    public final static String DEV = "DEV";
    public final static String HQ  = "HQ";
    public final static String SQ  = "SQ";
    public final static String FPR = "FPR";
    public final static String MS  = "MS";
    public final static String SHP = "SHP";
    public final static String DIS = "DIS";
    public final static String SUP = "SUP";
    public final static String CLO = "CLO";
    public final static String PRC = "PRC";
    public final static String PC  = "PC";
    public final static String LSR = "LSR";
    public final static String RTR = "RTR";
    public final static String DD  = "DD";
    public final static String[] CHECKPOINTS = new String[]{ BLANK, PRECON, CON, INV, DEF,
                                                              DEV, HQ, SQ, FPR, MS, SHP, DIS,
                                                              SUP, CLO, PRC, PC, LSR, RTR, DD };

    public final static int SMALL_SIZE       = 24;
    public final static int LARGE_SIZE       = 48;
    public final static int THUMBNAIL_HEIGHT = SMALL_SIZE;
    public final static int THUMBNAIL_WIDTH  = SMALL_SIZE;
    public final static int LARGE_HEIGHT     = LARGE_SIZE;
    public final static int LARGE_WIDTH      = LARGE_SIZE;

    public final static String DEFAULT_CHECKPOINT = PRECON;
    public final static String DEFAULT_SIZE       = LARGE;
    public final static int DEFAULT_HEIGHT        = LARGE_HEIGHT;
    public final static int DEFAULT_WIDTH         = LARGE_WIDTH;

    public KeysightPlcCheckpointDescription()
    {
       this.setBaseUrl( "http://localhost:1990/confluence" );
       this.setCheckpoint( DEFAULT_CHECKPOINT );
       this.setSize( DEFAULT_SIZE );
    }

    public KeysightPlcCheckpointDescription( String baseUrl )
    {
       this.setBaseUrl( baseUrl );
       this.setCheckpoint( DEFAULT_CHECKPOINT );
       this.setSize( DEFAULT_SIZE );
    }

    public KeysightPlcCheckpointDescription( String baseUrl, String checkpoint, String size )
    {
       this.setBaseUrl( baseUrl );
       this.setCheckpoint( checkpoint );
       this.setSize( size );
    }

    public KeysightPlcCheckpointDescription( String baseUrl, Map<String, String> parameters )
    {
       String checkpoint = DEFAULT_CHECKPOINT;
       if (parameters.containsKey(CHECKPOINT)){
          checkpoint = parameters.get(CHECKPOINT);
       }

       String size = DEFAULT_SIZE;
       if (parameters.containsKey(SIZE)){
          size = parameters.get(SIZE);
       }

       this.setBaseUrl( baseUrl );
       this.setCheckpoint( checkpoint );
       this.setSize( size );
    }

    public void setBaseUrl( String baseUrl ){
       this.baseUrl = baseUrl;
    }
    public String getBaseUrl(){
        return this.baseUrl;
    }

    public void setCheckpoint( String checkpoint )
    {
       boolean checkpointDefined = false;
       for( String knownCheckpoints : CHECKPOINTS ){
          if( checkpoint.equals( knownCheckpoints ) ){ checkpointDefined = true; }
       }
       if( !checkpointDefined ){
          throw new IllegalArgumentException( "Value for checkpoint is not in the list of known values." );
       }

       this.checkpoint = checkpoint;
    }
    public String getCheckpoint(){
       return this.checkpoint;
    }

    public void setSize( String size )
    {
       boolean checkpointDefined = false;
       for( String knownCheckpoints : CHECKPOINTS ){
          if( checkpoint.equals( knownCheckpoints ) ){ checkpointDefined = true; }
       }
       if( !checkpointDefined ){
          throw new IllegalArgumentException( "Value for checkpoint is not in the list of known values." );
       }
       this.size = size;

       if( size.equals( SMALL ) ){
          this.setHeight( THUMBNAIL_HEIGHT );
          this.setWidth( THUMBNAIL_WIDTH );
       } else {
          this.setHeight( LARGE_HEIGHT );
          this.setWidth( LARGE_WIDTH );
       }
    }
    public String getSize(){
       return this.size;
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

    public String html() 
    {
       return img();
    }

    public String img() 
    {
       baseUrlIsValid();
       return String.format( "<img src=\"%s%s/%s\" alt=\"%s\" height=\"%d\" width=\"%d\"/>",
                              this.getBaseUrl(),
                              RESOURCE_DIR, 
                              this.png(), 
                              this.getCheckpoint(), 
                              this.getHeight(), 
                              this.getWidth() );
    }

    public String url() 
    {
       baseUrlIsValid();
       return String.format( "%s%s/%s", this.getBaseUrl(), RESOURCE_DIR, this.png() );
    }

    public String png()
    {
        return String.format( "checkpoint-%s-%dx%d.png", 
                              this.getCheckpoint().toLowerCase(),
                              this.getWidth(), this.getHeight() );
    }

    public String svg()
    {
        return String.format( "checkpoint-%s.svg", this.getCheckpoint().toLowerCase() );
    }

    private boolean baseUrlIsValid(){
       if( this.getBaseUrl() == null ){
          throw new IllegalArgumentException( "Baseurl is not set.  Cannot generate html." );
       } else {
          return true;
       }
    }
}
