/*
 * Copyright 2002 by Leland Stanford Junior University Board of Trustees.
 * The code contained herein is subject to license terms available at 
 * http://aboutcoursework.stanford.edu/opensource/terms.html
 */
package org.navigoproject.data;



/**
 *
 * @author Jarrod Lannan
 * @version // 	$Id: ItemResultBean.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 */

/**
 * @hibernate.class
 *            table="ITEM_RESULT"
 */
public class ItemResultBean {

  private ItemResultPK itemResultPK;
  private String elementId;
  
  public ItemResultBean() {
    itemResultPK = new ItemResultPK();
  }

  public ItemResultBean (ItemResultPK pk, String elementId){
  	this.itemResultPK = pk;
  	this.elementId = elementId;  		  
  }
	
	/**
	 * @hibernate.id
	 *            generator-class="assigned"    
	 */	
  public ItemResultPK getItemResultPK(){
	  return this.itemResultPK;
  }

  private void setItemResultPK(ItemResultPK pk){
	  this.itemResultPK = pk;
  }

	/**
	 * @hibernate.property
	 *            column="ELEMENT_ID"
	 */
  public String getElementId(){
  	return this.elementId;
  }
  
  public void setElementId(String elementId){
  	this.elementId = elementId;
  }
}
