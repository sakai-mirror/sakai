/*
 * Copyright 2002 by Leland Stanford Junior University Board of Trustees.
 * The code contained herein is subject to license terms available at 
 * http://aboutcoursework.stanford.edu/opensource/terms.html
 */
package org.navigoproject.data;

import java.io.Serializable;

/**
 * @author Jarrod Lannan
 */

/**
 * @hibernate.class
 *            table="ITEM_RESULT"
 */
public class ItemResultPK implements Serializable{

  private String assessmentId, itemId;
  
  public ItemResultPK() {}

  public ItemResultPK (String assessmentId, String itemId){
  	this.assessmentId = assessmentId;
  	this.itemId = itemId;  	  
  }
		
	/**
	 * @hibernate.property
	 *            column="ASSESSMENT_ID"
	 */
  public String getAssessmentId(){
	  return this.assessmentId;
  }

  public void setAssessmentId(String assessmentId){
	  this.assessmentId = assessmentId;
  }


	/**
	 * @hibernate.property
	 *            column="ITEM_ID"
	 */
  public String getItemId(){
	  return this.itemId;
  }

  public void setItemId(String itemId){
	  this.itemId = itemId;
  }
  
	public boolean equals(Object itemResultPK){
		boolean returnValue=false;

		if (this == itemResultPK)
			returnValue=true;
		else if (!(itemResultPK instanceof ItemResultPK) || itemResultPK == null)
      returnValue=false;
		else {
			ItemResultPK i = (ItemResultPK)itemResultPK;
			if (this.getAssessmentId().equals(i.getAssessmentId())
			&& this.getItemId().equals(i.getItemId()))
				returnValue= true;
		}
		return returnValue;
	}

	public int hashCode(){
		return (this.getAssessmentId() + this.getItemId()).hashCode();
	}  
}
