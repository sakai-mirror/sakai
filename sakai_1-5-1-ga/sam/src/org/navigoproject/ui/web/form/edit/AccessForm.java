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

import org.navigoproject.business.entity.assessment.model.AccessGroup;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorForm;

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
 * @author Rachel Gollub
 * @version 1.0
 */
public class AccessForm
  extends ValidatorForm
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(AccessForm.class);
    
  private Collection groups;
  private String releaseMonth;
  private String releaseDay;
  private String releaseYear;
  private String releaseHour = "00";
  private String releaseMinute = "00";
  private String releaseAmPm = "AM";
  private String retractMonth;
  private String retractDay;
  private String retractYear;
  private String retractHour = "00";
  private String retractMinute = "00";
  private String retractAmPm = "AM";
  private String dueMonth;
  private String dueDay;
  private String dueYear;
  private String dueHour = "00";
  private String dueMinute = "00";
  private String dueAmPm = "AM";
  private boolean groups_isInstructorViewable;
  private boolean groups_isInstructorEditable;
  private boolean groups_isStudentViewable;
  private boolean releaseType_isInstructorViewable;
  private boolean releaseType_isInstructorEditable;
  private boolean releaseType_isStudentViewable;
  private boolean releaseDate_isInstructorViewable;
  private boolean releaseDate_isInstructorEditable;
  private boolean releaseDate_isStudentViewable;
  private boolean retractType_isInstructorViewable;
  private boolean retractType_isInstructorEditable;
  private boolean retractType_isStudentViewable;
  private boolean retractDate_isInstructorViewable;
  private boolean retractDate_isInstructorEditable;
  private boolean retractDate_isStudentViewable;
  private boolean dueDate_isInstructorViewable;
  private boolean dueDate_isInstructorEditable;
  private boolean dueDate_isStudentViewable;
  private boolean retryAllowed_isInstructorViewable;
  private boolean retryAllowed_isInstructorEditable;
  private boolean retryAllowed_isStudentViewable;
  private boolean timedAssessment_isInstructorViewable;
  private boolean timedAssessment_isInstructorEditable;
  private boolean timedAssessment_isStudentViewable;
  private boolean passwordRequired_isInstructorViewable;
  private boolean passwordRequired_isInstructorEditable;
  private boolean passwordRequired_isStudentViewable;
  private boolean ipAccessType_isInstructorViewable;
  private boolean ipAccessType_isInstructorEditable;
  private boolean ipAccessType_isStudentViewable;
  static Logger logger = Logger.getLogger(AccessForm.class.getName());

  /**
   * Creates a new AccessForm object.
   */
  public AccessForm()
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
          ((this.dueDay == null) || this.dueDay.equals("")) &&
          ((this.dueMonth == null) || this.dueMonth.equals("")) &&
          ((this.dueYear == null) || this.dueYear.equals(""))
        ) ||
        (
          ((this.dueDay != null) && ! this.dueDay.equals("")) &&
          ((this.dueMonth != null) && ! this.dueMonth.equals("")) &&
          ((this.dueYear != null) && ! this.dueYear.equals(""))
        ))
    {
      logger.info("duedate is valid");
    }
    else
    {
      errors.add(
        ActionErrors.GLOBAL_ERROR,
        new ActionError("errors.date_incomplete", "Due Date"));
      logger.info("duedate is not valid");
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
    if(actionMapping.getPath().indexOf("editAccess") > -1)
    {
      if(session.getAttribute("editorRole").equals("templateEditor"))
      { // This is so we don't remove the correct values from the session
        this.releaseType_isInstructorViewable = false;
        this.releaseType_isInstructorEditable = false;
        this.releaseDate_isInstructorViewable = false;
        this.releaseDate_isInstructorEditable = false;
        this.retractType_isInstructorViewable = false;
        this.retractType_isInstructorEditable = false;
        this.retractDate_isInstructorViewable = false;
        this.retractDate_isInstructorEditable = false;
        this.dueDate_isInstructorViewable = false;
        this.dueDate_isInstructorEditable = false;
        this.retryAllowed_isInstructorViewable = false;
        this.retryAllowed_isInstructorEditable = false;
        this.timedAssessment_isInstructorViewable = false;
        this.timedAssessment_isInstructorEditable = false;
        this.passwordRequired_isInstructorViewable = false;
        this.passwordRequired_isInstructorEditable = false;
        this.ipAccessType_isInstructorViewable = false;
        this.ipAccessType_isInstructorEditable = false;
        this.releaseType_isStudentViewable = false;
        this.releaseDate_isStudentViewable = false;
        this.retractType_isStudentViewable = false;
        this.retractDate_isStudentViewable = false;
        this.dueDate_isStudentViewable = false;
        this.retryAllowed_isStudentViewable = false;
        this.timedAssessment_isStudentViewable = false;
        this.passwordRequired_isStudentViewable = false;
        this.ipAccessType_isStudentViewable = false;
      }
    }
    else if(actionMapping.getPath().indexOf("editGroups") > -1)
    {
      if(session.getAttribute("editorRole").equals("templateEditor"))
      {
        this.groups_isInstructorViewable = false;
        this.groups_isInstructorEditable = false;
      }

      this.groups_isStudentViewable = false;

      // Handle checkboxes inside groups
      Iterator iter = groups.iterator();
      while(iter.hasNext())
      {
        AccessGroup group = (AccessGroup) iter.next();
        group.setIsActive(false);
      }
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
      return 10;
    }
    else
    {
      int i = 0;
      if(groups_isInstructorEditable || groups_isInstructorViewable)
      {
        i++;
      }

      if(releaseType_isInstructorEditable || releaseType_isInstructorViewable)
      {
        i++;
      }

      if(releaseDate_isInstructorEditable || releaseDate_isInstructorViewable)
      {
        i++;
      }

      if(retractType_isInstructorEditable || retractType_isInstructorViewable)
      {
        i++;
      }

      if(retractDate_isInstructorEditable || retractDate_isInstructorViewable)
      {
        i++;
      }

      if(dueDate_isInstructorEditable || dueDate_isInstructorViewable)
      {
        i++;
      }

      if(retryAllowed_isInstructorEditable ||
          retryAllowed_isInstructorViewable)
      {
        i++;
      }

      if(
        timedAssessment_isInstructorEditable ||
          timedAssessment_isInstructorViewable)
      {
        i++;
      }

      if(
        passwordRequired_isInstructorEditable ||
          passwordRequired_isInstructorViewable)
      {
        i++;
      }

      if(ipAccessType_isInstructorEditable ||
          ipAccessType_isInstructorViewable)
      {
        i++;
      }

      return i;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getGroups()
  {
    return groups;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pgroups DOCUMENTATION PENDING
   */
  public void setGroups(Collection pgroups)
  {
    if(pgroups != null)
    {
      groups = pgroups;
    }

    logger.debug("Set number of groups to " + groups.size());
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Object[] getGroupArray()
  {
    if(groups == null)
    {
      logger.debug("Groups is null.");
      groups = new ArrayList();
    }

    return groups.toArray();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pgroups DOCUMENTATION PENDING
   */
  public void setGroupArray(Object[] pgroups)
  {
    // This isn't used.
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getReleaseMonth()
  {
    return this.releaseMonth;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param releaseMonth DOCUMENTATION PENDING
   */
  public void setReleaseMonth(String releaseMonth)
  {
    this.releaseMonth = releaseMonth;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getReleaseDay()
  {
    return this.releaseDay;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param releaseDay DOCUMENTATION PENDING
   */
  public void setReleaseDay(String releaseDay)
  {
    this.releaseDay = releaseDay;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getReleaseYear()
  {
    return this.releaseYear;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param releaseYear DOCUMENTATION PENDING
   */
  public void setReleaseYear(String releaseYear)
  {
    this.releaseYear = releaseYear;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getReleaseHour()
  {
    if(releaseHour.compareTo("00") == 0)
    {
      return "12";
    }
    else
    {
      return this.releaseHour;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param releaseHour DOCUMENTATION PENDING
   */
  public void setReleaseHour(String releaseHour)
  {
    if(releaseHour.compareTo("12") == 0)
    {
      releaseHour = "0";
    }

    if(releaseHour.length() < 2)
    {
      this.releaseHour = "0" + releaseHour;
    }
    else
    {
      this.releaseHour = releaseHour;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getReleaseMinute()
  {
    return this.releaseMinute;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param releaseMinute DOCUMENTATION PENDING
   */
  public void setReleaseMinute(String releaseMinute)
  {
    if(releaseMinute.length() < 2)
    {
      this.releaseMinute = "0" + releaseMinute;
    }
    else
    {
      this.releaseMinute = releaseMinute;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getReleaseAmPm()
  {
    return this.releaseAmPm;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param releaseAmPm DOCUMENTATION PENDING
   */
  public void setReleaseAmPm(String releaseAmPm)
  {
    this.releaseAmPm = releaseAmPm;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getRetractMonth()
  {
    return this.retractMonth;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param retractMonth DOCUMENTATION PENDING
   */
  public void setRetractMonth(String retractMonth)
  {
    this.retractMonth = retractMonth;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getRetractDay()
  {
    return this.retractDay;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param retractDay DOCUMENTATION PENDING
   */
  public void setRetractDay(String retractDay)
  {
    this.retractDay = retractDay;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getRetractYear()
  {
    return this.retractYear;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param retractYear DOCUMENTATION PENDING
   */
  public void setRetractYear(String retractYear)
  {
    this.retractYear = retractYear;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getRetractHour()
  {
    if(retractHour.compareTo("00") == 0)
    {
      return "12";
    }
    else
    {
      return this.retractHour;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param retractHour DOCUMENTATION PENDING
   */
  public void setRetractHour(String retractHour)
  {
    if(retractHour.compareTo("12") == 0)
    {
      retractHour = "0";
    }

    if(retractHour.length() < 2)
    {
      this.retractHour = "0" + retractHour;
    }
    else
    {
      this.retractHour = retractHour;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getRetractMinute()
  {
    return this.retractMinute;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param retractMinute DOCUMENTATION PENDING
   */
  public void setRetractMinute(String retractMinute)
  {
    if(retractMinute.length() < 2)
    {
      this.retractMinute = "0" + retractMinute;
    }
    else
    {
      this.retractMinute = retractMinute;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getRetractAmPm()
  {
    return this.retractAmPm;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param retractAmPm DOCUMENTATION PENDING
   */
  public void setRetractAmPm(String retractAmPm)
  {
    this.retractAmPm = retractAmPm;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getDueMonth()
  {
    return this.dueMonth;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param dueMonth DOCUMENTATION PENDING
   */
  public void setDueMonth(String dueMonth)
  {
    this.dueMonth = dueMonth;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getDueDay()
  {
    return this.dueDay;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param dueDay DOCUMENTATION PENDING
   */
  public void setDueDay(String dueDay)
  {
    this.dueDay = dueDay;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getDueYear()
  {
    return this.dueYear;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param dueYear DOCUMENTATION PENDING
   */
  public void setDueYear(String dueYear)
  {
    this.dueYear = dueYear;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getDueHour()
  {
    if(dueHour.compareTo("00") == 0)
    {
      return "12";
    }
    else
    {
      return this.dueHour;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param dueHour DOCUMENTATION PENDING
   */
  public void setDueHour(String dueHour)
  {
    if(dueHour.compareTo("12") == 0)
    {
      dueHour = "0";
    }

    if(dueHour.length() < 2)
    {
      this.dueHour = "0" + dueHour;
    }
    else
    {
      this.dueHour = dueHour;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getDueMinute()
  {
    return this.dueMinute;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param dueMinute DOCUMENTATION PENDING
   */
  public void setDueMinute(String dueMinute)
  {
    if(dueMinute.length() < 2)
    {
      this.dueMinute = "0" + dueMinute;
    }
    else
    {
      this.dueMinute = dueMinute;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getDueAmPm()
  {
    return this.dueAmPm;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param dueAmPm DOCUMENTATION PENDING
   */
  public void setDueAmPm(String dueAmPm)
  {
    this.dueAmPm = dueAmPm;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getGroups_isInstructorViewable()
  {
    return this.groups_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param groups_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setGroups_isInstructorViewable(
    boolean groups_isInstructorViewable)
  {
    this.groups_isInstructorViewable = groups_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getGroups_isInstructorEditable()
  {
    return this.groups_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param groups_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setGroups_isInstructorEditable(
    boolean groups_isInstructorEditable)
  {
    this.groups_isInstructorEditable = groups_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getGroups_isStudentViewable()
  {
    return this.groups_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param groups_isStudentViewable DOCUMENTATION PENDING
   */
  public void setGroups_isStudentViewable(boolean groups_isStudentViewable)
  {
    this.groups_isStudentViewable = groups_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getReleaseType_isInstructorViewable()
  {
    return this.releaseType_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param releaseType_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setReleaseType_isInstructorViewable(
    boolean releaseType_isInstructorViewable)
  {
    this.releaseType_isInstructorViewable = releaseType_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getReleaseType_isInstructorEditable()
  {
    return this.releaseType_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param releaseType_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setReleaseType_isInstructorEditable(
    boolean releaseType_isInstructorEditable)
  {
    this.releaseType_isInstructorEditable = releaseType_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getReleaseType_isStudentViewable()
  {
    return this.releaseType_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param releaseType_isStudentViewable DOCUMENTATION PENDING
   */
  public void setReleaseType_isStudentViewable(
    boolean releaseType_isStudentViewable)
  {
    this.releaseType_isStudentViewable = releaseType_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getReleaseDate_isInstructorViewable()
  {
    return this.releaseDate_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param releaseDate_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setReleaseDate_isInstructorViewable(
    boolean releaseDate_isInstructorViewable)
  {
    this.releaseDate_isInstructorViewable = releaseDate_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getReleaseDate_isInstructorEditable()
  {
    return this.releaseDate_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param releaseDate_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setReleaseDate_isInstructorEditable(
    boolean releaseDate_isInstructorEditable)
  {
    this.releaseDate_isInstructorEditable = releaseDate_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getReleaseDate_isStudentViewable()
  {
    return this.releaseDate_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param releaseDate_isStudentViewable DOCUMENTATION PENDING
   */
  public void setReleaseDate_isStudentViewable(
    boolean releaseDate_isStudentViewable)
  {
    this.releaseDate_isStudentViewable = releaseDate_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getRetractType_isInstructorViewable()
  {
    return this.retractType_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param retractType_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setRetractType_isInstructorViewable(
    boolean retractType_isInstructorViewable)
  {
    this.retractType_isInstructorViewable = retractType_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getRetractType_isInstructorEditable()
  {
    return this.retractType_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param retractType_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setRetractType_isInstructorEditable(
    boolean retractType_isInstructorEditable)
  {
    this.retractType_isInstructorEditable = retractType_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getRetractType_isStudentViewable()
  {
    return this.retractType_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param retractType_isStudentViewable DOCUMENTATION PENDING
   */
  public void setRetractType_isStudentViewable(
    boolean retractType_isStudentViewable)
  {
    this.retractType_isStudentViewable = retractType_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getRetractDate_isInstructorViewable()
  {
    return this.retractDate_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param retractDate_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setRetractDate_isInstructorViewable(
    boolean retractDate_isInstructorViewable)
  {
    this.retractDate_isInstructorViewable = retractDate_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getRetractDate_isInstructorEditable()
  {
    return this.retractDate_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param retractDate_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setRetractDate_isInstructorEditable(
    boolean retractDate_isInstructorEditable)
  {
    this.retractDate_isInstructorEditable = retractDate_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getRetractDate_isStudentViewable()
  {
    return this.retractDate_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param retractDate_isStudentViewable DOCUMENTATION PENDING
   */
  public void setRetractDate_isStudentViewable(
    boolean retractDate_isStudentViewable)
  {
    this.retractDate_isStudentViewable = retractDate_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getDueDate_isInstructorViewable()
  {
    return this.dueDate_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param dueDate_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setDueDate_isInstructorViewable(
    boolean dueDate_isInstructorViewable)
  {
    this.dueDate_isInstructorViewable = dueDate_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getDueDate_isInstructorEditable()
  {
    return this.dueDate_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param dueDate_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setDueDate_isInstructorEditable(
    boolean dueDate_isInstructorEditable)
  {
    this.dueDate_isInstructorEditable = dueDate_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getDueDate_isStudentViewable()
  {
    return this.dueDate_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param dueDate_isStudentViewable DOCUMENTATION PENDING
   */
  public void setDueDate_isStudentViewable(boolean dueDate_isStudentViewable)
  {
    this.dueDate_isStudentViewable = dueDate_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getRetryAllowed_isInstructorViewable()
  {
    return this.retryAllowed_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param retryAllowed_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setRetryAllowed_isInstructorViewable(
    boolean retryAllowed_isInstructorViewable)
  {
    this.retryAllowed_isInstructorViewable = retryAllowed_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getRetryAllowed_isInstructorEditable()
  {
    return this.retryAllowed_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param retryAllowed_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setRetryAllowed_isInstructorEditable(
    boolean retryAllowed_isInstructorEditable)
  {
    this.retryAllowed_isInstructorEditable = retryAllowed_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getRetryAllowed_isStudentViewable()
  {
    return this.retryAllowed_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param retryAllowed_isStudentViewable DOCUMENTATION PENDING
   */
  public void setRetryAllowed_isStudentViewable(
    boolean retryAllowed_isStudentViewable)
  {
    this.retryAllowed_isStudentViewable = retryAllowed_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getTimedAssessment_isInstructorViewable()
  {
    return this.timedAssessment_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param timedAssessment_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setTimedAssessment_isInstructorViewable(
    boolean timedAssessment_isInstructorViewable)
  {
    this.timedAssessment_isInstructorViewable =
      timedAssessment_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getTimedAssessment_isInstructorEditable()
  {
    return this.timedAssessment_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param timedAssessment_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setTimedAssessment_isInstructorEditable(
    boolean timedAssessment_isInstructorEditable)
  {
    this.timedAssessment_isInstructorEditable =
      timedAssessment_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getTimedAssessment_isStudentViewable()
  {
    return this.timedAssessment_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param timedAssessment_isStudentViewable DOCUMENTATION PENDING
   */
  public void setTimedAssessment_isStudentViewable(
    boolean timedAssessment_isStudentViewable)
  {
    this.timedAssessment_isStudentViewable = timedAssessment_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getPasswordRequired_isInstructorViewable()
  {
    return this.passwordRequired_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param passwordRequired_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setPasswordRequired_isInstructorViewable(
    boolean passwordRequired_isInstructorViewable)
  {
    this.passwordRequired_isInstructorViewable =
      passwordRequired_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getPasswordRequired_isInstructorEditable()
  {
    return this.passwordRequired_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param passwordRequired_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setPasswordRequired_isInstructorEditable(
    boolean passwordRequired_isInstructorEditable)
  {
    this.passwordRequired_isInstructorEditable =
      passwordRequired_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getPasswordRequired_isStudentViewable()
  {
    return this.passwordRequired_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param passwordRequired_isStudentViewable DOCUMENTATION PENDING
   */
  public void setPasswordRequired_isStudentViewable(
    boolean passwordRequired_isStudentViewable)
  {
    this.passwordRequired_isStudentViewable =
      passwordRequired_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getIpAccessType_isInstructorViewable()
  {
    return this.ipAccessType_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param ipAccessType_isInstructorViewable DOCUMENTATION PENDING
   */
  public void setIpAccessType_isInstructorViewable(
    boolean ipAccessType_isInstructorViewable)
  {
    this.ipAccessType_isInstructorViewable = ipAccessType_isInstructorViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getIpAccessType_isInstructorEditable()
  {
    return this.ipAccessType_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param ipAccessType_isInstructorEditable DOCUMENTATION PENDING
   */
  public void setIpAccessType_isInstructorEditable(
    boolean ipAccessType_isInstructorEditable)
  {
    this.ipAccessType_isInstructorEditable = ipAccessType_isInstructorEditable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getIpAccessType_isStudentViewable()
  {
    return this.ipAccessType_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param ipAccessType_isStudentViewable DOCUMENTATION PENDING
   */
  public void setIpAccessType_isStudentViewable(
    boolean ipAccessType_isStudentViewable)
  {
    this.ipAccessType_isStudentViewable = ipAccessType_isStudentViewable;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Date getReleaseDate()
  {
    try
    {
      SimpleDateFormat dateFormatter =
        new SimpleDateFormat("MM/dd/yyyy/K/mm/a");
      Date tempDate =
        dateFormatter.parse(
          releaseMonth + "/" + releaseDay + "/" + releaseYear + "/" +
          releaseHour + "/" + releaseMinute + "/" + releaseAmPm,
          new ParsePosition(0));
      LOG.debug("release date " + tempDate.toString());

      return tempDate;
    }
    catch(Exception e)
    {
      LOG.debug("Exception " + e);
    }

    return null;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param date DOCUMENTATION PENDING
   */
  public void setReleaseDate(Date date)
  {
    if(date == null)
    {
      date = new Date();
    }

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);

    int tempDay = calendar.get(Calendar.DAY_OF_MONTH);
    int tempMonth = calendar.get(Calendar.MONTH) + 1;
    int tempYear = calendar.get(Calendar.YEAR);
    int tempHour = calendar.get(Calendar.HOUR);
    int tempMinute = calendar.get(Calendar.MINUTE);
    int tempAmPm = calendar.get(Calendar.AM_PM);
    setReleaseDay("" + tempDay);
    setReleaseMonth("" + tempMonth);
    setReleaseYear("" + tempYear);
    setReleaseHour("" + tempHour);
    setReleaseMinute("" + tempMinute);
    setReleaseAmPm((tempAmPm == Calendar.AM) ? "AM" : "PM");

    LOG.debug(
      "day=" + tempDay + " month=" + tempMonth + " year=" + tempYear);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Date getRetractDate()
  {
    try
    {
      SimpleDateFormat dateFormatter =
        new SimpleDateFormat("MM/dd/yyyy/K/mm/a");
      Date tempDate =
        dateFormatter.parse(
          retractMonth + "/" + retractDay + "/" + retractYear + "/" +
          retractHour + "/" + retractMinute + "/" + retractAmPm,
          new ParsePosition(0));
      LOG.debug("retract date " + tempDate.toString());

      return tempDate;
    }
    catch(Exception e)
    {
      LOG.debug("Exception " + e);
    }

    return null;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param date DOCUMENTATION PENDING
   */
  public void setRetractDate(Date date)
  {
    if(date == null)
    {
      date = new Date();
    }

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);

    int tempDay = calendar.get(Calendar.DAY_OF_MONTH);
    int tempMonth = calendar.get(Calendar.MONTH) + 1;
    int tempYear = calendar.get(Calendar.YEAR);
    int tempHour = calendar.get(Calendar.HOUR);
    int tempMinute = calendar.get(Calendar.MINUTE);
    int tempAmPm = calendar.get(Calendar.AM_PM);
    setRetractDay("" + tempDay);
    setRetractMonth("" + tempMonth);
    setRetractYear("" + tempYear);
    setRetractHour("" + tempHour);
    setRetractMinute("" + tempMinute);
    setRetractAmPm((tempAmPm == Calendar.AM) ? "AM" : "PM");

    LOG.debug(
      "day=" + tempDay + " month=" + tempMonth + " year=" + tempYear);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Date getDueDate()
  {
    try
    {
      SimpleDateFormat dateFormatter =
        new SimpleDateFormat("MM/dd/yyyy/K/mm/a");
      Date tempDate =
        dateFormatter.parse(
          dueMonth + "/" + dueDay + "/" + dueYear + "/" + dueHour + "/" +
          dueMinute + "/" + dueAmPm, new ParsePosition(0));
      LOG.debug("due date " + tempDate.toString());

      return tempDate;
    }
    catch(Exception e)
    {
      LOG.debug("Exception " + e);
    }

    return null;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param date DOCUMENTATION PENDING
   */
  public void setDueDate(Date date)
  {
    if(date == null)
    {
      date = new Date();
    }

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);

    int tempDay = calendar.get(Calendar.DAY_OF_MONTH);
    int tempMonth = calendar.get(Calendar.MONTH) + 1;
    int tempYear = calendar.get(Calendar.YEAR);
    int tempHour = calendar.get(Calendar.HOUR);
    int tempMinute = calendar.get(Calendar.MINUTE);
    int tempAmPm = calendar.get(Calendar.AM_PM);
    setDueDay("" + tempDay);
    setDueMonth("" + tempMonth);
    setDueYear("" + tempYear);
    setDueHour("" + tempHour);
    setDueMinute("" + tempMinute);
    setDueAmPm((tempAmPm == Calendar.AM) ? "AM" : "PM");

    LOG.debug(
      "due day=" + tempDay + " month=" + tempMonth + " year=" + tempYear);
  }

  /**
   * DOCUMENTATION PENDING
   */
  private void resetFields()
  {
    if(groups == null)
    {
      LOG.debug("u r in the access form");

      this.retractMonth = "";
      this.retractDay = "";
      this.retractYear = "";
      this.retractHour = "00";
      this.retractMinute = "00";
      this.retractAmPm = "AM";

      this.releaseMonth = "";
      this.releaseDay = "";
      this.releaseYear = "";
      this.releaseHour = "00";
      this.releaseMinute = "00";
      this.releaseAmPm = "AM";

      this.dueMonth = "";
      this.dueDay = "";
      this.dueYear = "";
      this.dueHour = "00";
      this.dueMinute = "00";
      this.dueAmPm = "AM";

      this.groups_isInstructorViewable = true;
      this.groups_isInstructorEditable = true;
      this.groups_isStudentViewable = true;
      this.releaseType_isInstructorViewable = true;
      this.releaseType_isInstructorEditable = true;
      this.releaseType_isStudentViewable = false;
      this.releaseDate_isInstructorViewable = true;
      this.releaseDate_isInstructorEditable = true;
      this.releaseDate_isStudentViewable = true;
      this.retractType_isInstructorViewable = true;
      this.retractType_isInstructorEditable = true;
      this.retractType_isStudentViewable = false;
      this.retractDate_isInstructorViewable = true;
      this.retractDate_isInstructorEditable = true;
      this.retractDate_isStudentViewable = false;
      this.dueDate_isInstructorViewable = true;
      this.dueDate_isInstructorEditable = true;
      this.dueDate_isStudentViewable = true;
      this.retryAllowed_isInstructorViewable = true;
      this.retryAllowed_isInstructorEditable = true;
      this.retryAllowed_isStudentViewable = false;
      this.timedAssessment_isInstructorViewable = false;
      this.timedAssessment_isInstructorEditable = false;
      this.timedAssessment_isStudentViewable = false;
      this.passwordRequired_isInstructorViewable = false;
      this.passwordRequired_isInstructorEditable = false;
      this.passwordRequired_isStudentViewable = false;
      this.ipAccessType_isInstructorViewable = false;
      this.ipAccessType_isInstructorEditable = false;
      this.ipAccessType_isStudentViewable = false;
    }
  }
}
