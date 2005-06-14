/*
 * Created on Mar 30, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.author.section;
import java.util.ArrayList;

import org.apache.struts.action.ActionForm;
/**
 * @author rshastri
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SectionActionForm extends ActionForm
{
	private static final org.apache.log4j.Logger LOG =
		org.apache.log4j.Logger.getLogger(SectionActionForm.class);
		
 	private String assessTitle;
	private String assessmentID;
  private String showMetadata;
  private String sectionIdent;
  private String noOfItems;
	private ArrayList assessmentSectionIdents;
	private ArrayList poolsAvailable;
  /**
   * @return
   */
  public String getAssessmentID()
  {
    return assessmentID;
  }

    /**
     * @return
     */
    public String getAssessTitle()
    {
      return assessTitle;
    }

  /**
   * @param string
   */
  public void setAssessmentID(String string)
  {
    assessmentID = string;
  }

    /**
     * @param string
     */
    public void setAssessTitle(String string)
    {
      assessTitle = string;
    }

  /**
   * @return
   */
  public String getShowMetadata()
  {
    return showMetadata;
  }

  /**
   * @param string
   */
  public void setShowMetadata(String string)
  {
    showMetadata = string;
  }

 

  /**
   * @return
   */
  public String getSectionIdent()
  {
    return sectionIdent;
  }

  /**
   * @param string
   */
  public void setSectionIdent(String string)
  {
    sectionIdent = string;
  }

 

  /**
   * @return
   */
  public ArrayList getAssessmentSectionIdents()
  {
    return assessmentSectionIdents;
  }

  /**
   * @param list
   */
  public void setAssessmentSectionIdents(ArrayList list)
  {
    assessmentSectionIdents = list;
  }

  /**
   * @return
   */
  public ArrayList getPoolsAvailable()
  {
    return poolsAvailable;
  }

  /**
   * @param list
   */
  public void setPoolsAvailable(ArrayList list)
  {
    poolsAvailable = list;
  }

  /**
   * @return
   */
  public String getNoOfItems()
  {
    return noOfItems;
  }

  /**
   * @param string
   */
  public void setNoOfItems(String string)
  {
    noOfItems = string;
  }

}
