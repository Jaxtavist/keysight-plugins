package com.keysight.guidance.helpers;

import java.util.Map;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.keysight.guidance.helpers.LpHelper;

public class GuidelineDescription
{
    private String baseUrl;
    private String term;
    private String image;
    private String altText;
    private String guidanceGlyph;
    private String guidanceClass;
    private boolean renderHtmlWithPngImage   = false;
    private boolean renderPdfWithPngImage    = true;
    private boolean renderWordWithPngImage   = false;
    private boolean renderScrollWithPngImage = false;
    private int height;
    private int width;

    private final static String LIGHT_CHECK    = "&#10003;";
    private final static String HEAVY_CHECK    = "&#10004;";
    private final static String LIGHT_BALLOT_X = "&#10007;";
    private final static String HEAVY_BALLOT_X = "&#10008;";
    private final static String CHECK          = HEAVY_CHECK;
    private final static String BALLOT_X       = HEAVY_BALLOT_X;

    public final static String DO_IMAGE       = "Do.40x24.png";
    public final static String DO_NOT_IMAGE   = "DoNot.73x24.png";
    public final static String CONSIDER_IMAGE = "Consider.92x24.png";
    public final static String AVOID_IMAGE    = "Avoid.62x24.png";

    public final static String RED_CHECK_PNG    = "red-check.10x10.png";
    public final static String RED_BALLOT_X_PNG = "red-x.8x12.png";
    public final static String BLACK_CHECK_PNG    = "check.10x10.png";
    public final static String BLACK_BALLOT_X_PNG = "x.8x12.png";
    public final static String CHECK_PNG        = BLACK_CHECK_PNG;
    public final static String BALLOT_X_PNG     = BLACK_BALLOT_X_PNG;
    public final static int CHECK_PNG_WIDTH     = 10;
    public final static int CHECK_PNG_HEIGHT    = 10;
    public final static int BALLOT_X_PNG_WIDTH  = 8;
    public final static int BALLOT_X_PNG_HEIGHT = 12;

    public final static int DO_WIDTH        = 40;
    public final static int DO_NOT_WIDTH    = 73;
    public final static int CONSIDER_WIDTH  = 92;
    public final static int AVOID_WIDTH     = 62;

    public final static int GUIDANCE_HEIGHT  = 24;
    public final static int DO_HEIGHT        = GUIDANCE_HEIGHT;
    public final static int DO_NOT_HEIGHT    = GUIDANCE_HEIGHT;
    public final static int CONSIDER_HEIGHT  = GUIDANCE_HEIGHT;
    public final static int AVOID_HEIGHT     = GUIDANCE_HEIGHT;

    private final static String GUIDANCE          = "guidance";
    private final static String GUIDANCE_CHECK    = "guidance-check";
    private final static String GUIDANCE_BALLOT_X = "guidance-ballot-x";

    public final static String DO            = "Do";
    public final static String CONSIDER      = "Consider";
    public final static String AVOID         = "Avoid";
    public final static String DO_NOT        = "Do Not";
    public final static String[] GUIDANCE_TERMS = new String[]{ DO, 
                                                                DO_NOT,
                                                                CONSIDER,
                                                                AVOID };

    public final static String DEFAULT_TERM = DO;
    public final static int DEFAULT_WIDTH = DO_WIDTH;
    public final static int DEFAULT_HEIGHT = DO_HEIGHT;

    private static final String RESOURCE_DIR = "/download/resources/com.keysight.guidance:guidance-resources/images";

    public GuidelineDescription()
    {
       this.setBaseUrl( "http://localhost:1990/confluence" );
       this.setTerm( DEFAULT_TERM );
    }

    public GuidelineDescription( String baseUrl )
    {
       this.setBaseUrl( baseUrl );
       this.setTerm( DEFAULT_TERM );
    }

    public GuidelineDescription( String baseUrl, String term )
    {
       this.setBaseUrl( baseUrl );
       this.setTerm( term );
    }

    public GuidelineDescription( String baseUrl, Map<String, String> parameters )
    {
       String term = DEFAULT_TERM;
       if (parameters.containsKey(GUIDANCE)){
          term = parameters.get(GUIDANCE);
       }

       this.setBaseUrl( baseUrl );
       this.setTerm( term );
    }

    public void setTerm( String term )
    {
       boolean termDefined = false;

       for( String knownTerms : GUIDANCE_TERMS ){
          if( term.equals( knownTerms ) ){ termDefined = true; }
       }

       if( !termDefined ){
          throw new IllegalArgumentException( "Value for term is not in the lists of known values." );
       }

       this.term = term;

       if( term.equals( DO ) ){
          this.setImage( DO_IMAGE );
          this.setWidth( DO_WIDTH );
          this.setHeight( DO_HEIGHT );
          this.setGuidanceGlyph( CHECK );
          this.setGuidanceClass( GUIDANCE_CHECK );
        } else if( term.equals( CONSIDER ) ){
          this.setImage( CONSIDER_IMAGE );
          this.setWidth( CONSIDER_WIDTH );
          this.setHeight( CONSIDER_HEIGHT );
          this.setGuidanceGlyph( CHECK );
          this.setGuidanceClass( GUIDANCE_CHECK );
        } else if( term.equals( AVOID ) ){
          this.setImage( AVOID_IMAGE );
          this.setWidth( AVOID_WIDTH );
          this.setHeight( AVOID_HEIGHT );
          this.setGuidanceGlyph( BALLOT_X );
          this.setGuidanceClass( GUIDANCE_BALLOT_X );
        } else if( term.equals( DO_NOT ) ){
          this.setImage( DO_NOT_IMAGE );
          this.setWidth( DO_NOT_WIDTH );
          this.setHeight( DO_NOT_HEIGHT );
          this.setGuidanceGlyph( BALLOT_X );
          this.setGuidanceClass( GUIDANCE_BALLOT_X );
        } 

        this.setAltText( term );
    }
    public String getTerm(){
        return this.term;
    }

    public String htmlWithUnicodeCharacter( ConversionContext context )
    {
       String guidanceTerm = this.getTerm().toUpperCase();
       String html;

       if( LpHelper.IsWordOrPdfExport( context ) ){
          guidanceTerm = String.format( "<b>%s</b>", guidanceTerm );
       }

       html = String.format( "<span class=\"%s\">%s</span>&nbsp<span class=\"guidance-term\">%s</span>",
                             this.getGuidanceClass(),
                             this.getGuidanceGlyph(),
                             guidanceTerm );

       return html;
    }

    public String htmlWithPngImage( ConversionContext context )
    {
       baseUrlIsValid();
       String guidanceTerm = this.getTerm().toUpperCase();
       String html;

       if( LpHelper.IsWordOrPdfExport( context ) ){
          guidanceTerm = String.format( "<b>%s</b>", guidanceTerm );
       }

       if( this.getGuidanceGlyph().equals( CHECK ) ){
          html = String.format( "<img src=\"%s%s/%s\" alt=\"%s\" height=\"%d\" width=\"%d\"/>&nbsp;<span class=\"guidance-term\">%s</span>",
                                this.getBaseUrl(),
                                RESOURCE_DIR, 
                                CHECK_PNG,
                                this.getAltText().toUpperCase(), 
                                CHECK_PNG_HEIGHT,
                                CHECK_PNG_WIDTH,
                                guidanceTerm );
       } else {
          html = String.format( "<img src=\"%s%s/%s\" alt=\"%s\" height=\"%d\" width=\"%d\"/>&nbsp;<span class=\"guidance-term\">%s</span>",
                                this.getBaseUrl(),
                                RESOURCE_DIR, 
                                BALLOT_X_PNG,
                                this.getAltText().toUpperCase(), 
                                BALLOT_X_PNG_HEIGHT,
                                BALLOT_X_PNG_WIDTH,
                                guidanceTerm );
       }

       return html;
    }

    public String html( ConversionContext context ) 
    {
       String html;

       if( LpHelper.IsScrollExport( context ) ){
          if( this.getRenderScrollWithPngImage() ){
              html = htmlWithPngImage( context );
          } else {
              html = htmlWithUnicodeCharacter( context );
          }
       } else if( LpHelper.IsWordExport( context ) ){
          if( this.getRenderWordWithPngImage() ){
              html = htmlWithPngImage( context );
          } else {
              html = htmlWithUnicodeCharacter( context );
          }
       } else if( LpHelper.IsPdfExport( context ) ){
          if( this.getRenderPdfWithPngImage() ){
              html = htmlWithPngImage( context );
          } else {
              html = htmlWithUnicodeCharacter( context );
          }
       } else {
          if( this.getRenderHtmlWithPngImage() ){
              html = htmlWithPngImage( context );
          } else {
              html = htmlWithUnicodeCharacter( context );
          }
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
                              this.getAltText().toUpperCase(), 
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

    public void setBaseUrl( String baseUrl ){
       this.baseUrl = baseUrl;
    }
    public String getBaseUrl(){
        return this.baseUrl;
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

    public void setGuidanceGlyph( String guidanceGlyph ){
       this.guidanceGlyph = guidanceGlyph;
    }
    public String getGuidanceGlyph(){
        return this.guidanceGlyph;
    }

    public void setGuidanceClass( String guidanceClass ){
       this.guidanceClass = guidanceClass;
    }
    public String getGuidanceClass(){
        return this.guidanceClass;
    }

    public void setRenderHtmlWithPngImage( boolean bFlag ){
       this.renderHtmlWithPngImage = bFlag;
    }
    public boolean getRenderHtmlWithPngImage(){
        return this.renderHtmlWithPngImage;
    }

    public void setRenderPdfWithPngImage( boolean bFlag ){
       this.renderPdfWithPngImage = bFlag;
    }
    public boolean getRenderPdfWithPngImage(){
        return this.renderPdfWithPngImage;
    }

    public void setRenderWordWithPngImage( boolean bFlag ){
       this.renderWordWithPngImage = bFlag;
    }
    public boolean getRenderWordWithPngImage(){
        return this.renderWordWithPngImage;
    }

    public void setRenderScrollWithPngImage( boolean bFlag ){
       this.renderScrollWithPngImage = bFlag;
    }
    public boolean getRenderScrollWithPngImage(){
        return this.renderScrollWithPngImage;
    }
}
