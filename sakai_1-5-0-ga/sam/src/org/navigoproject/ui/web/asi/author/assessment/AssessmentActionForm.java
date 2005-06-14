/*
 *                       Navigo Software License
 *
 * Copyright 2003, Trustees of Indiana University, The Regents of the University
 * of Michigan, and Stanford University, all rights reserved.
 *
 * This work, including software, documents, or other related items (the
 * "Software"), is being provided by the copyright holder(s) subject to the
 * terms of the Navigo Software License. By obtaining, using and/or copying this
 * Software, you agree that you have read, understand, and will comply with the
 * following terms and conditions of the Navigo Software License:
 *
 * Permission to use, copy, modify, and distribute this Software and its
 * documentation, with or without modification, for any purpose and without fee
 * or royalty is hereby granted, provided that you include the following on ALL
 * copies of the Software or portions thereof, including modifications or
 * derivatives, that you make:
 *
 *    The full text of the Navigo Software License in a location viewable to
 *    users of the redistributed or derivative work.
 *
 *    Any pre-existing intellectual property disclaimers, notices, or terms and
 *    conditions. If none exist, a short notice similar to the following should
 *    be used within the body of any redistributed or derivative Software:
 *    "Copyright 2003, Trustees of Indiana University, The Regents of the
 *    University of Michigan and Stanford University, all rights reserved."
 *
 *    Notice of any changes or modifications to the Navigo Software, including
 *    the date the changes were made.
 *
 *    Any modified software must be distributed in such as manner as to avoid
 *    any confusion with the original Navigo Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) and/or Indiana University,
 * The University of Michigan, Stanford University, or Navigo may NOT be used
 * in advertising or publicity pertaining to the Software without specific,
 * written prior permission. Title to copyright in the Software and any
 * associated documentation will at all times remain with the copyright holders.
 * The export of software employing encryption technology may require a specific
 * license from the United States Government. It is the responsibility of any
 * person or organization contemplating export to obtain such a license before
 * exporting this Software.
 */

/*
 * Created on Aug 19, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.author.assessment;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts.action.ActionForm;

/**
 * @author rshastri
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AssessmentActionForm
  extends ActionForm
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(AssessmentActionForm.class);
  private String assessmentID;  
  private int hours;
  private int minutes;
  private int weeks;
  private int days;
  private String username;
  private String publish_start_day;
  private int publish_start_hours;
  private int publish_start_minutes;
  private String publish_end_day;
  private int publish_end_hours;
  private int publish_end_minutes;
  private String publish_retract_day;
  private int publish_retract_hours;
  private int publish_retract_minutes;
  private String feedback_delivery_day;
  private int feedback_delivery_hours;
  private int feedback_delivery_minutes;
  private String errorMsg;
  private String testType;
  private String templatesEnabled;
  private String editTemplate;
	private ArrayList courseList;
	private ArrayList groupList;
	private ArrayList assessmentReleasedTo_SelectedList;
  private ArrayList assessmentElements;
  private List assessmentPublishedElements;
	private ArrayList assessmentCompletedPublishedElements;
	private List assessmentExpiredPublishedElements;
	private ArrayList existingTemplates;
//*************For Publishing Confirmation**************************
	private String confirmation_publishedId;
	private String confirmation_start_date;
	private String confirmation_end_date;
	private String confirmation_retract_date;
	private String confirmation_max_attemps;
	private ArrayList confirmation_released_to;

  /**
   * Creates a new AssessmentActionForm object.
   */
  public AssessmentActionForm()
  {
    LOG.debug("Constructor AuthorActionForm()");
    hours = 0;
    minutes = 0;
    weeks = 0;
    days = 0;
    errorMsg = "";
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getTemplatesEnabled()
  {
    return templatesEnabled;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param templatesEnabled DOCUMENTATION PENDING
   */
  public void setTemplatesEnabled(String templatesEnabled)
  {
    this.templatesEnabled = templatesEnabled;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getEditTemplate()
  {
    return editTemplate;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param editTemplate DOCUMENTATION PENDING
   */
  public void setEditTemplate(String editTemplate)
  {
    this.editTemplate = editTemplate;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ArrayList getAssessmentElements()
  {
    return assessmentElements;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentElements DOCUMENTATION PENDING
   */
  public void setAssessmentElements(ArrayList assessmentElements)
  {
    this.assessmentElements = assessmentElements;
  }

  /**
   * @return
   */
  public int getDays()
  {
    return days;
  }

  /**
   * @return
   */
  public int getHours()
  {
    return hours;
  }

  /**
   * @return
   */
  public int getMinutes()
  {
    return minutes;
  }

  /**
   * @return
   */
  public int getWeeks()
  {
    return weeks;
  }

  /**
   * @param int
   */
  public void setDays(int days)
  {
    this.days = days;
  }

  /**
   * @param int
   */
  public void setHours(int hours)
  {
    this.hours = hours;
  }

  /**
   * @param int
   */
  public void setMinutes(int minutes)
  {
    this.minutes = minutes;
  }

  /**
   * @param int
   */
  public void setWeeks(int weeks)
  {
    this.weeks = weeks;
  }

  /**
   * @return
   */
  public String getTestType()
  {
    return testType;
  }

  /**
   * @param string
   */
  public void setTestType(String string)
  {
    testType = string;
  }

  /**
   * @return
   */
  public String getUsername()
  {
    return username;
  }

  /**
   * @param string
   */
  public void setUsername(String string)
  {
    username = string;
  }

  /**
   * @return
   */

  /*public int getPublish_end_date()
     {
       return publish_end_date;
     }*/

  /**
   * @return
   */
  public int getPublish_end_hours()
  {
    return publish_end_hours;
  }

  /**
   * @return
   */
  public int getPublish_end_minutes()
  {
    return publish_end_minutes;
  }

  /**
   * @return
   */

  /*  public int getPublish_end_month()
     {
       return publish_end_month;
     }*/

  /**
   * @return
   */

  /* public int getPublish_end_year()
     {
       return publish_end_year;
     }*/

  /**
   * @return
   */

  /*public int getPublish_start_date()
     {
       return publish_start_date;
     }*/

  /**
   * @return
   */
  public int getPublish_start_hours()
  {
    return publish_start_hours;
  }

  /**
   * @return
   */
  public int getPublish_start_minutes()
  {
    return publish_start_minutes;
  }

  /**
   * @return
   */

  /*public int getPublish_start_month()
     {
       return publish_start_month;
     }*/

  /**
   * @return
   */

  /* public int getPublish_start_year()
     {
       return publish_start_year;
     }*/

  /**
   * @param int
   */

  /* public void setPublish_end_date(int string)
     {
       publish_end_date = string;
     }*/

  /**
   * @param int
   */
  public void setPublish_end_hours(int string)
  {
    publish_end_hours = string;
  }

  /**
   * @param int
   */
  public void setPublish_end_minutes(int string)
  {
    publish_end_minutes = string;
  }

  /**
   * @param int
   */

  /*public void setPublish_end_month(int string)
     {
       publish_end_month = string;
     }*/

  /**
   * @param int
   */

  /*public void setPublish_end_year(int string)
     {
       publish_end_year = string;
     }*/

  /**
   * @param int
   */

  /*public void setPublish_start_date(int string)
     {
       publish_start_date = string;
     }*/

  /**
   * @param int
   */
  public void setPublish_start_hours(int string)
  {
    publish_start_hours = string;
  }

  /**
   * @param int
   */
  public void setPublish_start_minutes(int string)
  {
    publish_start_minutes = string;
  }

  /**
   * @param int
   */

  /*public void setPublish_start_month(int string)
     {
       publish_start_month = string;
     }*/

  /**
   * @param int
   */

  /*public void setPublish_start_year(int string)
     {
       publish_start_year = string;
     }*/

  /**
   * @return
   */
  public String getErrorMsg()
  {
    return errorMsg;
  }

  /**
   * @param string
   */
  public void setErrorMsg(String string)
  {
    errorMsg = string;
  }

  /**
   * @return
   */
  public List getAssessmentPublishedElements()
  {
    return assessmentPublishedElements;
  }

  /**
   * @param list
   */
  public void setAssessmentPublishedElements(List list)
  {
    assessmentPublishedElements = list;
  }

  /**
   * @return
   */
  public String getPublish_end_day()
  {
    return publish_end_day;
  }

  /**
   * @return
   */
  public String getPublish_start_day()
  {
    return publish_start_day;
  }

  /**
   * @param string
   */
  public void setPublish_end_day(String string)
  {
    publish_end_day = string;
  }

  /**
   * @param string
   */
  public void setPublish_start_day(String string)
  {
    publish_start_day = string;
  }

  /**
   * @return
   */
  public String getPublish_retract_day()
  {
    return publish_retract_day;
  }

  /**
   * @return
   */
  public int getPublish_retract_hours()
  {
    return publish_retract_hours;
  }

  /**
   * @return
   */
  public int getPublish_retract_minutes()
  {
    return publish_retract_minutes;
  }

  /**
   * @param string
   */
  public void setPublish_retract_day(String string)
  {
    publish_retract_day = string;
  }

  /**
   * @param i
   */
  public void setPublish_retract_hours(int i)
  {
    publish_retract_hours = i;
  }

  /**
   * @param i
   */
  public void setPublish_retract_minutes(int i)
  {
    publish_retract_minutes = i;
  }

  /**
   * @return
   */
  public String getFeedback_delivery_day()
  {
    return feedback_delivery_day;
  }

  /**
   * @return
   */
  public int getFeedback_delivery_hours()
  {
    return feedback_delivery_hours;
  }

  /**
   * @return
   */
  public int getFeedback_delivery_minutes()
  {
    return feedback_delivery_minutes;
  }

  /**
   * @param string
   */
  public void setFeedback_delivery_day(String string)
  {
    feedback_delivery_day = string;
  }

  /**
   * @param i
   */
  public void setFeedback_delivery_hours(int i)
  {
    feedback_delivery_hours = i;
  }

  /**
   * @param i
   */
  public void setFeedback_delivery_minutes(int i)
  {
    feedback_delivery_minutes = i;
  }

  /**
   * @return
   */
  public ArrayList getExistingTemplates()
  {
    return existingTemplates;
  }

  /**
   * @param list
   */
  public void setExistingTemplates(ArrayList list)
  {
    existingTemplates = list;
  }
  /**
   * @return
   */
  
  /**
   * @return
   */
  public ArrayList getCourseList()
  {
    return courseList;
  }

  /**
   * @param list
   */
  public void setCourseList(ArrayList list)
  {
    courseList = list;
  }

  /**
   * @return
   */
  public ArrayList getGroupList()
  {
    return groupList;
  }

  /**
   * @param list
   */
  public void setGroupList(ArrayList list)
  {
    groupList = list;
  }

  /**
   * @return
   */
  public ArrayList getAssessmentReleasedTo_SelectedList()
  {
    return assessmentReleasedTo_SelectedList;
  }

  /**
   * @param list
   */
  public void setAssessmentReleasedTo_SelectedList(ArrayList list)
  {
    assessmentReleasedTo_SelectedList = list;
  }

  /**
   * @return
   */
  public String getConfirmation_max_attemps()
  {
    return confirmation_max_attemps;
  }

/**
 * @return
 */
public String getConfirmation_publishedId()
{
  return confirmation_publishedId;
}

  /**
   * @return
   */
  public ArrayList getConfirmation_released_to()
  {
    return confirmation_released_to;
  }

  /**
   * @return
   */
  public String getConfirmation_retract_date()
  {
    return confirmation_retract_date;
  }

  /**
   * @return
   */
  public String getConfirmation_start_date()
  {
    return confirmation_start_date;
  }

  /**
   * @param string
   */
  public void setConfirmation_max_attemps(String string)
  {
    confirmation_max_attemps = string;
  }

/**
 * @param string
 */
public void setConfirmation_publishedId(String string)
{
  confirmation_publishedId = string;
}

  /**
   * @param list
   */
  public void setConfirmation_released_to(ArrayList list)
  {
    confirmation_released_to = list;
  }

  /**
   * @param string
   */
  public void setConfirmation_retract_date(String string)
  {
    confirmation_retract_date = string;
  }

  /**
   * @param string
   */
  public void setConfirmation_start_date(String string)
  {
    confirmation_start_date = string;
  }

  /**
   * @return
   */
  public String getConfirmation_end_date()
  {
    return confirmation_end_date;
  }

  /**
   * @param string
   */
  public void setConfirmation_end_date(String string)
  {
    confirmation_end_date = string;
  }

  /**
   * @return
   */
  public String getAssessmentID()
  {
    return assessmentID;
  }

  /**
   * @param string
   */
  public void setAssessmentID(String string)
  {
    assessmentID = string;
  }

  /**
   * @return
   */


  /**
   * @return
   */
  public ArrayList getAssessmentCompletedPublishedElements()
  {
    return assessmentCompletedPublishedElements;
  }

  /**
   * @return
   */
  public List getAssessmentExpiredPublishedElements()
  {
    return assessmentExpiredPublishedElements;
  }

  /**
   * @param list
   */
  public void setAssessmentCompletedPublishedElements(ArrayList list)
  {
    assessmentCompletedPublishedElements = list;
  }

  /**
   * @param list
   */
  public void setAssessmentExpiredPublishedElements(List list)
  {
    assessmentExpiredPublishedElements = list;
  }

}
