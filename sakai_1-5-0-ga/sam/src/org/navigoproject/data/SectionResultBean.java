/*
 * Created on May 13, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.data;

/**
 * @author palcasi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @hibernate.class
 *            table="SECTION_RESULT"
 */
public class SectionResultBean
{
  private SectionResultPK sectionResultPK;
  private String elementId;
  
  public SectionResultBean()
  {
    this.sectionResultPK = new SectionResultPK();    
  }
  
  private SectionResultBean(SectionResultPK sectionResultPK, String elementId)
  {
    this.sectionResultPK = sectionResultPK;
    this.elementId = elementId;
  }
  
  /**
   * @hibernate.id
   *            generator-class="assigned"    
   */ 
  public SectionResultPK getSectionResultPK()
  {
    return this.sectionResultPK;
  }

  /**
   * @param resultPK
   */
  public void setSectionResultPK(SectionResultPK sectionResultPK)
  {
    this.sectionResultPK = sectionResultPK;
  }
  
  /**
   * @hibernate.property
   *            column="ELEMENT_ID"
   */
  public String getElementId()
  {
    return this.elementId;
  }

  /**
   * @param string
   */
  public void setElementId(String elementId)
  {
    this.elementId = elementId;
  }

}
