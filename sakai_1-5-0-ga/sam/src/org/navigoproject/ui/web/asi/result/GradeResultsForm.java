/*
 * Created on Jan 26, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.result;

import org.apache.struts.action.ActionForm;

/**
 * @author palcasi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class GradeResultsForm extends ActionForm
{
  private String assessmentId;
  
  public GradeResultsForm()
  {
    this.assessmentId = null;
  }
  
  public String getAssessmentId()
  {
    return this.assessmentId;
  }
  
  public void setAssessmentId(String assessmentId)
  {
    this.assessmentId = assessmentId;
  }

}
