package com.keysight.database.helpers;

import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
 
import javax.xml.stream.XMLStreamException;
 
public class DatabaseVelocityHelper
{
    private XhtmlContent xhtmlContent;
 
    @HtmlSafe
    public String convertStorageToView(String storage)
    {
        Page page = new Page();
        String view = "";
        try
        {
            final ConversionContext conversionContext = new DefaultConversionContext(page.toPageContext());
            view = xhtmlContent.convertStorageToView(storage, conversionContext);
        }
        catch (XhtmlException e)
        {
            e.printStackTrace();
        }
        catch (XMLStreamException e)
        {
            e.printStackTrace();
        }
        return view;
    }
 
    public void setXhtmlContent(XhtmlContent xhtmlContent) {
        this.xhtmlContent = xhtmlContent;
    }
}
