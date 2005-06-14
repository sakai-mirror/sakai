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
 *            table="ASSESSMENT_RESULT"
 */
public class AssessmentResultBean
{
  private String assessmentId;
  private String elementId;
  
  public AssessmentResultBean()
  {
  }
  
  public AssessmentResultBean(String assessmentId, String elementId)
  {
    this.assessmentId = assessmentId;
    this.elementId = elementId;
  }
  
  /**
    * @hibernate.id
    *            generator-class="assigned"
    *            column="ASSESSMENT_ID"                
    */
  public String getAssessmentId()
  {
    return this.assessmentId;
  }

  /**
   * @param string
   */
  public void setAssessmentId(String string)
  {
    this.assessmentId = string;
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
  public void setElementId(String string)
  {
    this.elementId = string;
  }

}
