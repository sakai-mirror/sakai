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

package org.navigoproject.ui.web.form.edit;

import org.navigoproject.business.entity.assessment.model.AssessmentPropertiesImpl;
import org.navigoproject.business.entity.assessment.model.FeedbackModel;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorForm;

import osid.assessment.Assessment;

/**
 * <p>
 * Title: NavigoProject.org
 * </p>
 *
 * <p>
 * Description: AAM - form class for edit/access.jsp
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 *
 * <p>
 * Company: Stanford University
 * </p>
 *
 * @author Marc Brierley
 * @author Ed Smiley
 * @version $Id: FeedbackForm.java,v 1.1.1.1 2004/07/28 21:32:07 rgollub.stanford.edu Exp $
 */
public class FeedbackForm
  extends ValidatorForm
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(FeedbackForm.class);
    
  private String feedbackType;
  private boolean feedbackType_isInstructorViewable;
  private boolean feedbackType_isInstructorEditable;
  private boolean feedbackType_isStudentViewable;
  private boolean feedbackReleaseDate_isInstructorViewable;
  private boolean feedbackReleaseDate_isInstructorEditable;
  private boolean feedbackReleaseDate_isStudentViewable;
  private boolean scoreReleaseDate_isInstructorViewable;
  private boolean scoreReleaseDate_isInstructorEditable;
  private boolean scoreReleaseDate_isStudentViewable;
  private String immediateFeedbackType_q;
  private String immediateFeedbackType_p;
  private String immediateFeedbackType_a;
  private String datedFeedbackType;
  private String totalScoreMonth;
  private String totalScoreDay;
  private String totalScoreYear;
  private String totalScoreHour = "00";
  private String totalScoreMinute = "00";
  private String totalScoreAmPm = "AM";
  private String perQuestionFeedbackType;
  private String feedbackMonth;
  private String feedbackDay;
  private String feedbackYear;
  private String feedbackHour = "00";
  private String feedbackMinute = "00";
  private String feedbackAmPm = "AM";
  static Logger logger = Logger.getLogger(FeedbackForm.class.getName());

  /**
   * Creates a new FeedbackForm object.
   */
  public FeedbackForm()
  {
    super();
    resetFields();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param actionMapping DOCUMENTATION PENDING
   * @param httpServletRequest DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ActionErrors validate(
    ActionMapping actionMapping, HttpServletRequest httpServletRequest)
  {
    ActionErrors errors = new ActionErrors();
    if(
      (
          ((this.feedbackDay == null) || this.feedbackDay.equals("")) &&
          ((this.feedbackMonth == null) || this.feedbackMonth.equals("")) &&
          ((this.feedbackYear == null) || this.feedbackYear.equals(""))
        ) ||
        (
          ((this.feedbackDay != null) && ! this.feedbackDay.equals("")) &&
          ((this.feedbackMonth != null) && ! this.feedbackMonth.equals("")) &&
          ((this.feedbackYear != null) && ! this.feedbackYear.equals(""))
        ))
    {
      logger.info("feedbackdate is valid");
    }
    else
    {
      logger.info("feedbackdate is not valid");
      errors.add(
        ActionErrors.GLOBAL_ERROR,
        new ActionError(
          "errors.date_incomplete",
          "Feedback Date " + this.feedbackDay + "/" + this.feedbackMonth + "/" +
          this.feedbackYear));

      // reset Feedback Date 
      try
      {
        HttpSession session = httpServletRequest.getSession(false);
        FeedbackForm ff = (FeedbackForm) session.getAttribute("feedback");
        Assessment assessment =
          (Assessment) session.getAttribute("assessmentTemplate");
        AssessmentPropertiesImpl props =
          (AssessmentPropertiesImpl) assessment.getData();
        FeedbackModel model = (FeedbackModel) props.getFeedbackModel();
        if(model != null)
        {
          ff.setFeedbackReleaseDate(model.getFeedbackDate());
        }
      }
      catch(Exception e)
      {
        LOG.error(e); throw new Error(e);
      }
    }

    return errors;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param actionMapping DOCUMENTATION PENDING
   * @param httpServletRequest DOCUMENTATION PENDING
   */
  public void reset(
    ActionMapping actionMapping, HttpServletRequest httpServletRequest)
  {
    HttpSession session = httpServletRequest.getSession(true);
    if(session.getAttribute("editorRole").equals("templateEditor"))
    { // This is so we don't remove the correct values from the session
      this.feedbackType_isInstructorViewable = false;
      this.feedbackType_isInstructorEditable = false;

      this.feedbackReleaseDate_isInstructorViewable = false;
      this.feedbackReleaseDate_isInstructorEditable = false;

      this.scoreReleaseDate_isInstructorViewable = false;
      this.scoreReleaseDate_isInstructorEditable = false;
      this.feedbackType_isStudentViewable = false;
      this.feedbackReleaseDate_isStudentViewable = false;
      this.scoreReleaseDate_isStudentViewable = false;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param editorRole DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public int getLength(String editorRole)
  {
    if(editorRole.equals("templateEditor"))
    {
      return 3;
    }
    else
    {
      int i = 0;
      if(feedbackType_isInstructorEditable ||
          feedbackType_isInstructorViewable)
      {
        i = 3; // This one takes three rows if it's there at all.
      }

      return i;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getFeedbackType()
  {
    return this.feedbackType;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param feedbackType DOCUMENTATION PENDING
   */
  public void setFeedbackType(String feedbackType)
  {
    if(feedbackType != null)
    {
      this.feedbackType = feedbackType;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getFeedbackType_isInstructorViewable()
  {
    return this.feedbackType_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param feedbackType_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setFeedbackType_isInstructorViewable(
    boolean feedbackType_isInstructorViewable)
  {
    this.feedbackType_isInstructorViewable = feedbackType_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getFeedbackType_isInstructorEditable()
  {
    return this.feedbackType_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param feedbackType_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setFeedbackType_isInstructorEditable(
    boolean feedbackType_isInstructorEditable)
  {
    this.feedbackType_isInstructorEditable = feedbackType_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getFeedbackType_isStudentViewable()
  {
    return this.feedbackType_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param feedbackType_isStudentViewable DOCUMENTATION PENDING
   */
  public void setFeedbackType_isStudentViewable(
    boolean feedbackType_isStudentViewable)
  {
    this.feedbackType_isStudentViewable = feedbackType_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getFeedbackReleaseDate_isInstructorViewable()
  {
    return this.feedbackReleaseDate_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param feedbackReleaseDate_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setFeedbackReleaseDate_isInstructorViewable(
    boolean feedbackReleaseDate_isInstructorViewable)
  {
    this.feedbackReleaseDate_isInstructorViewable =
      feedbackReleaseDate_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getFeedbackReleaseDate_isInstructorEditable()
  {
    return this.feedbackReleaseDate_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param feedbackReleaseDate_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setFeedbackReleaseDate_isInstructorEditable(
    boolean feedbackReleaseDate_isInstructorEditable)
  {
    this.feedbackReleaseDate_isInstructorEditable =
      feedbackReleaseDate_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getFeedbackReleaseDate_isStudentViewable()
  {
    return this.feedbackReleaseDate_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param feedbackReleaseDate_isStudentViewable DOCUMENTATION PENDING
   */
  public void setFeedbackReleaseDate_isStudentViewable(
    boolean feedbackReleaseDate_isStudentViewable)
  {
    this.feedbackReleaseDate_isStudentViewable =
      feedbackReleaseDate_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getScoreReleaseDate_isInstructorViewable()
  {
    return this.scoreReleaseDate_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param scoreReleaseDate_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setScoreReleaseDate_isInstructorViewable(
    boolean scoreReleaseDate_isInstructorViewable)
  {
    this.scoreReleaseDate_isInstructorViewable =
      scoreReleaseDate_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getScoreReleaseDate_isInstructorEditable()
  {
    return this.scoreReleaseDate_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param scoreReleaseDate_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setScoreReleaseDate_isInstructorEditable(
    boolean scoreReleaseDate_isInstructorEditable)
  {
    this.scoreReleaseDate_isInstructorEditable =
      scoreReleaseDate_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getScoreReleaseDate_isStudentViewable()
  {
    return this.scoreReleaseDate_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param scoreReleaseDate_isStudentViewable DOCUMENTATION PENDING
   */
  public void setScoreReleaseDate_isStudentViewable(
    boolean scoreReleaseDate_isStudentViewable)
  {
    this.scoreReleaseDate_isStudentViewable =
      scoreReleaseDate_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getImmediateFeedbackType_q()
  {
    return this.immediateFeedbackType_q;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param immediateFeedbackType_q DOCUMENTATION PENDING
   */
  public void setImmediateFeedbackType_q(String immediateFeedbackType_q)
  {
    if(immediateFeedbackType_q != null)
    {
      this.immediateFeedbackType_q = immediateFeedbackType_q;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getImmediateFeedbackType_p()
  {
    return this.immediateFeedbackType_p;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param immediateFeedbackType_p DOCUMENTATION PENDING
   */
  public void setImmediateFeedbackType_p(String immediateFeedbackType_p)
  {
    if(immediateFeedbackType_p != null)
    {
      this.immediateFeedbackType_p = immediateFeedbackType_p;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getImmediateFeedbackType_a()
  {
    return this.immediateFeedbackType_a;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param immediateFeedbackType_a DOCUMENTATION PENDING
   */
  public void setImmediateFeedbackType_a(String immediateFeedbackType_a)
  {
    if(immediateFeedbackType_a != null)
    {
      this.immediateFeedbackType_a = immediateFeedbackType_a;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getDatedFeedbackType()
  {
    return this.datedFeedbackType;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param datedFeedbackType DOCUMENTATION PENDING
   */
  public void setDatedFeedbackType(String datedFeedbackType)
  {
    if(datedFeedbackType != null)
    {
      this.datedFeedbackType = datedFeedbackType;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getTotalScoreMonth()
  {
    return this.totalScoreMonth;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param totalScoreMonth DOCUMENTATION PENDING
   */
  public void setTotalScoreMonth(String totalScoreMonth)
  {
    this.totalScoreMonth = totalScoreMonth;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getTotalScoreDay()
  {
    return this.totalScoreDay;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param totalScoreDay DOCUMENTATION PENDING
   */
  public void setTotalScoreDay(String totalScoreDay)
  {
    this.totalScoreDay = totalScoreDay;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getTotalScoreYear()
  {
    return this.totalScoreYear;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param totalScoreYear DOCUMENTATION PENDING
   */
  public void setTotalScoreYear(String totalScoreYear)
  {
    this.totalScoreYear = totalScoreYear;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getTotalScoreHour()
  {
    if(totalScoreHour.compareTo("00") == 0)
    {
      return "12";
    }
    else
    {
      return this.totalScoreHour;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param totalScoreHour DOCUMENTATION PENDING
   */
  public void setTotalScoreHour(String totalScoreHour)
  {
    if(totalScoreHour.compareTo("12") == 0)
    {
      totalScoreHour = "0";
    }

    if(totalScoreHour.length() < 2)
    {
      this.totalScoreHour = "0" + totalScoreHour;
    }
    else
    {
      this.totalScoreHour = totalScoreHour;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getTotalScoreMinute()
  {
    return this.totalScoreMinute;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param totalScoreMinute DOCUMENTATION PENDING
   */
  public void setTotalScoreMinute(String totalScoreMinute)
  {
    if(totalScoreMinute.length() < 2)
    {
      this.totalScoreMinute = "0" + totalScoreMinute;
    }
    else
    {
      this.totalScoreMinute = totalScoreMinute;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getTotalScoreAmPm()
  {
    return this.totalScoreAmPm;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param totalScoreAmPm DOCUMENTATION PENDING
   */
  public void setTotalScoreAmPm(String totalScoreAmPm)
  {
    this.totalScoreAmPm = totalScoreAmPm;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getPerQuestionFeedbackType()
  {
    return this.perQuestionFeedbackType;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param perQuestionFeedbackType DOCUMENTATION PENDING
   */
  public void setPerQuestionFeedbackType(String perQuestionFeedbackType)
  {
    if(perQuestionFeedbackType != null)
    {
      this.perQuestionFeedbackType = perQuestionFeedbackType;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getFeedbackMonth()
  {
    return this.feedbackMonth;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param feedbackMonth DOCUMENTATION PENDING
   */
  public void setFeedbackMonth(String feedbackMonth)
  {
    this.feedbackMonth = feedbackMonth;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getFeedbackDay()
  {
    return this.feedbackDay;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param feedbackDay DOCUMENTATION PENDING
   */
  public void setFeedbackDay(String feedbackDay)
  {
    this.feedbackDay = feedbackDay;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getFeedbackYear()
  {
    return this.feedbackYear;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param feedbackYear DOCUMENTATION PENDING
   */
  public void setFeedbackYear(String feedbackYear)
  {
    this.feedbackYear = feedbackYear;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getFeedbackHour()
  {
    if(feedbackHour.compareTo("00") == 0)
    {
      return "12";
    }
    else
    {
      return this.feedbackHour;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param feedbackHour DOCUMENTATION PENDING
   */
  public void setFeedbackHour(String feedbackHour)
  {
    if(feedbackHour.compareTo("12") == 0)
    {
      feedbackHour = "0";
    }

    if(feedbackHour.length() < 2)
    {
      this.feedbackHour = "0" + feedbackHour;
    }
    else
    {
      this.feedbackHour = feedbackHour;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getFeedbackMinute()
  {
    return this.feedbackMinute;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param feedbackMinute DOCUMENTATION PENDING
   */
  public void setFeedbackMinute(String feedbackMinute)
  {
    if(feedbackMinute.length() < 2)
    {
      this.feedbackMinute = "0" + feedbackMinute;
    }
    else
    {
      this.feedbackMinute = feedbackMinute;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getFeedbackAmPm()
  {
    return this.feedbackAmPm;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param feedbackAmPm DOCUMENTATION PENDING
   */
  public void setFeedbackAmPm(String feedbackAmPm)
  {
    this.feedbackAmPm = feedbackAmPm;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Date getFeedbackReleaseDate()
  {
    try
    {
      SimpleDateFormat dateFormatter =
        new SimpleDateFormat("MM/dd/yyyy/K/mm/a");
      Date tempDate =
        dateFormatter.parse(
          feedbackMonth + "/" + feedbackDay + "/" + feedbackYear + "/" +
          feedbackHour + "/" + feedbackMinute + "/" + feedbackAmPm,
          new ParsePosition(0));
      logger.debug("feedback date " + tempDate.toString());

      return tempDate;
    }
    catch(Exception e)
    {
      logger.debug("Feedback Release Date is null.");
    }

    return null;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Date getTotalScoreReleaseDate()
  {
    try
    {
      //go to simpledate from java.text.simpledateformat to put in hour min and
      //change format
      SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
      Date tempDate =
        dateFormatter.parse(
          totalScoreMonth + "/" + totalScoreDay + "/" + totalScoreYear,
          new ParsePosition(0));
      logger.debug("total score release date " + tempDate.toString());

      return tempDate;
    }
    catch(Exception e)
    {
      logger.debug("Total score release date is null.");
    }

    return null;
  }

  // need one of these for each date element
  // but is this duplicating get/sets above?
  // also need to put in assessmentAction:
  // ff.setFeedbackDate(model.getFeedbackDate()); AND
  // model.setFeedbackDate(form.getFeedbackReleaseDate());
  public void setFeedbackReleaseDate(Date date)
  {
    if(date == null)
    {
      /*      Calendar temp = Calendar.getInstance();
         temp.add(Calendar.YEAR, 1);
         date = temp.getTime(); */
      setFeedbackDay("--");
      setFeedbackMonth("--");
      setFeedbackYear("--");
      setFeedbackHour("08");
      setFeedbackMinute("00");
      setFeedbackAmPm("AM");

      return;
    }

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);

    int tempDay = calendar.get(Calendar.DAY_OF_MONTH);
    int tempMonth = calendar.get(Calendar.MONTH) + 1;
    int tempYear = calendar.get(Calendar.YEAR);
    int tempHour = calendar.get(Calendar.HOUR);
    int tempMinute = calendar.get(Calendar.MINUTE);
    int tempAM_PM = calendar.get(Calendar.AM_PM);
    setFeedbackDay("" + tempDay);
    setFeedbackMonth("" + tempMonth);
    setFeedbackYear("" + tempYear);
    if(tempHour < 10)
    {
      setFeedbackHour("0" + tempHour);
    }
    else
    {
      setFeedbackHour("" + tempHour);
    }

    if(tempMinute < 10)
    {
      setFeedbackMinute("0" + tempMinute);
    }
    else
    {
      setFeedbackMinute("" + tempMinute);
    }

    if(tempAM_PM == Calendar.AM)
    {
      setFeedbackAmPm("AM");
    }
    else
    {
      setFeedbackAmPm("PM");
    }

    LOG.debug(
      "day=" + tempDay + " month=" + tempMonth + " year=" + tempYear);
  }

  //do the same for scorereleasedate
  public void setTotalScoreReleaseDate(Date date)
  {
    if(date == null)
    {
      date = new Date((new Date().getTime()) + (1000 * 60 * 60 * 24 * 365));
    }

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);

    int tempDay = calendar.get(Calendar.DAY_OF_MONTH);
    int tempMonth = calendar.get(Calendar.MONTH) + 1;
    int tempYear = calendar.get(Calendar.YEAR);

    setTotalScoreDay("" + tempDay);
    setTotalScoreMonth("" + tempMonth);
    setTotalScoreYear("" + tempYear);
    LOG.debug(
      "day=" + tempDay + " month=" + tempMonth + " year=" + tempYear);
  }

  /**
   * DOCUMENTATION PENDING
   */
  private void resetFields()
  {
    LOG.debug("u r in feed back form");
    this.feedbackType = "1";

    this.feedbackType_isInstructorViewable = true;
    this.feedbackType_isInstructorEditable = true;
    this.feedbackType_isStudentViewable = false;

    this.feedbackReleaseDate_isInstructorViewable = true;
    this.feedbackReleaseDate_isInstructorEditable = true;
    this.feedbackReleaseDate_isStudentViewable = true;

    this.scoreReleaseDate_isInstructorViewable = true;
    this.scoreReleaseDate_isInstructorEditable = true;
    this.scoreReleaseDate_isStudentViewable = true;

    this.immediateFeedbackType_q = "";
    this.immediateFeedbackType_p = "";
    this.immediateFeedbackType_a = "";
    this.datedFeedbackType = "0";
    this.perQuestionFeedbackType = "";

    // Set them to defaults.
    setFeedbackReleaseDate(null);
    setTotalScoreReleaseDate(null);
  }
}
