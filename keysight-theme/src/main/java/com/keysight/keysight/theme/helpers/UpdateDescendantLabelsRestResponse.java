package com.keysight.keysight.theme.helpers;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.atlassian.confluence.labels.Label;
import com.keysight.keysight.theme.helpers.LabelDescription;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UpdateDescendantLabelsRestResponse
{
   @XmlElement private int pageCount = 0;
   @XmlElement private int pagesSkipped = 0;
   @XmlElement private int pagesUpdated = 0;
   @XmlElement private LabelDescription[] labelDescriptions;

   public UpdateDescendantLabelsRestResponse( ){
   }
   
   public UpdateDescendantLabelsRestResponse( int pageCount ){
      this.setPageCount( pageCount );
   }

   public void setPageCount( int pageCount ){
      this.pageCount = pageCount;
   }
   public int getPageCount(){
      return this.pageCount;
   }

   public void setPagesSkipped( int pagesSkipped ){
      this.pagesSkipped = pagesSkipped;
   }
   public int getPagesSkipped(){
      return this.pagesSkipped;
   }

   public void setPagesUpdated( int pagesUpdated ){
      this.pagesUpdated = pagesUpdated;
   }
   public int getPagesUpdated(){
      return this.pagesUpdated;
   }

   public void setLabelDescriptions( LabelDescription[] labelDescriptions ){
      this.labelDescriptions = labelDescriptions;
   }
   public LabelDescription[] getLabelDescriptions(){
      return this.labelDescriptions;
   }
}
