package com.keysight.guidance.helpers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
 
public class LpHelper
{
    public static boolean IsLpExport(ConversionContext context)
    {
        boolean bIsLpExport = false;
	// Note, scroll office output to Word using the display type of pdf
	if( context.getOutputType().matches( "(word|pdf)" ) ){
            bIsLpExport = true;
	}
	return bIsLpExport;
    }
}
