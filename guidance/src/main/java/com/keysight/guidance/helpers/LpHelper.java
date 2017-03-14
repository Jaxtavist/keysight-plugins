package com.keysight.guidance.helpers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
 
public class LpHelper
{
    public static boolean IsLpExport(ConversionContext context)
    {
        return IsWordOrPdfExport( context );
    }

    public static boolean IsWordOrPdfExport(ConversionContext context)
    {
        boolean bFlag = false;
        // Note, scroll office output to Word using the display type of pdf
        if( context.getOutputType().matches( "(word|pdf)" ) ){
            bFlag = true;
        }
        return bFlag;
    }

    public static boolean IsPdfExport(ConversionContext context)
    {
        boolean bFlag = false;
        if( context.getOutputType().matches( "pdf" ) ){
            bFlag = true;
        }
        return bFlag;
    }

    public static boolean IsWordExport(ConversionContext context)
    {
        boolean bFlag = false;
        // Note, scroll office output to Word triggers the output type to be pdf
        if( context.getOutputType().matches( "word" ) ){
            bFlag = true;
        }
        return bFlag;
    }

    public static boolean IsScrollExport(ConversionContext context)
    {
        boolean bFlag = false;
        if( context.hasProperty("scroll-mode") ){
            bFlag = true;
        }
        return bFlag;
    }
}
