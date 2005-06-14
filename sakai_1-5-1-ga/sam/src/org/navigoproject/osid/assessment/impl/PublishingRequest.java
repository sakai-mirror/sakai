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
 * Created on Apr 21, 2004
 *
 */
package org.navigoproject.osid.assessment.impl;

import org.navigoproject.osid.OsidManagerFactory;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;

import osid.OsidException;

import osid.shared.SharedException;
import osid.shared.SharedManager;

/**
 * @author ajpoland@iupui.edu
 *
 */
public class PublishingRequest
{
  private static Logger LOG = Logger.getLogger(PublishingRequest.class);
  private List agentIdStrings;
  private String coreAssessmentIdString;  
  private String assessmentDisplayName;
  private String assessmentDescription;
  private String authorId;
  private Calendar beginDate;
  private Calendar dueDate;
  private Calendar feedbackDate;
  private Calendar retractDate;
  private String feedbackType;
  private Character lateHandling;

  // Qti Settings Variables
  private Integer maxAttempts;
  private String autoSubmit;
  private Character autoSave;
  private String testDisabled;
  private Calendar createdDate;
  private String ipRestrictions;
  private String usernameRestriction;
  private String passwordRestriction;

  /**
   *
   */
  public PublishingRequest()
  {
    LOG.debug("new PublishingRequest()");
  }

  /**
   * Creates a new PublishingRequest object.
   *
   * @param newagents DOCUMENTATION PENDING
   * @param newassessment DOCUMENTATION PENDING
   * @param newauthorId DOCUMENTATION PENDING
   * @param newbeginDate DOCUMENTATION PENDING
   * @param newdueDate DOCUMENTATION PENDING
   * @param newfeedbackDate DOCUMENTATION PENDING
   * @param newretractDate DOCUMENTATION PENDING
   * @param newfeedbackEnabled DOCUMENTATION PENDING
   */
  /**
   * @return
   */
  public ListIterator getAgentIdStrings()
  {
    return agentIdStrings.listIterator();
  }

  /**
   * @return
   */
  public Calendar getBeginDate()
  {
    return beginDate;
  }

  /**
   * @return
   */
  public Calendar getDueDate()
  {
    return dueDate;
  }

  /**
   * @return
   */
  public Calendar getFeedbackDate()
  {
    return feedbackDate;
  }

  /**
   * @return
   */
  public Calendar getRetractDate()
  {
    return retractDate;
  }

  /**
   * @param ids
   */
  public void setAgentIdStrings(List in_agentIdStrings)
  {
    this.agentIdStrings = in_agentIdStrings;
  }

  /**
   * @param calendar
   */
  public void setBeginDate(Calendar calendar)
  {
    beginDate = calendar;
  }

  /**
   * @param calendar
   */
  public void setDueDate(Calendar calendar)
  {
    dueDate = calendar;
  }

  /**
   * @param calendar
   */
  public void setFeedbackDate(Calendar calendar)
  {
    feedbackDate = calendar;
  }

  /**
   * @param calendar
   */
  public void setRetractDate(Calendar calendar)
  {
    retractDate = calendar;
  }

  /**
   * @return
   */
  public String getAuthorId()
  {
    return authorId;
  }

  /**
   * @param id
   */
  public void setAuthorId(String id)
  {
    authorId = id;
  }

  /**
   * @return
   */
  public String getAssessmentDescription()
  {
    return assessmentDescription;
  }

  /**
   * @return
   */
  public String getAssessmentDisplayName()
  {
    return assessmentDisplayName;
  }

  /**
   * @return
   */
  public String getFeedbackType()
  {
    return feedbackType;
  }

  /**
   * @param string
   */
  public void setAssessmentDescription(String string)
  {
    assessmentDescription = string;
  }

  /**
   * @param string
   */
  public void setAssessmentDisplayName(String string)
  {
    assessmentDisplayName = string;
  }

  /**
   * @param string
   */
  public void setFeedbackType(String string)
  {
    feedbackType = string;
  }

  /**
   * @return
   */
  public String getCoreAssessmentIdString()
  {
    return coreAssessmentIdString;
  }

  /**
   * @param string
   */
  public void setCoreAssessmentIdString(String string)
  {
    coreAssessmentIdString = string;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param ma DOCUMENTATION PENDING
   */
  public void setMaxAttempts(Integer ma)
  {
    maxAttempts = ma;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Integer getMaxAttempts()
  {
    return maxAttempts;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param as DOCUMENTATION PENDING
   */
  public void setAutoSubmit(String as)
  {
    autoSubmit = as;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getAutoSubmit()
  {
    return autoSubmit;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param as DOCUMENTATION PENDING
   */
  public void setAutoSave(Character as)
  {
    autoSave= as;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Character getAutoSave()
  {
    return autoSave;
  }




  /**
   * DOCUMENTATION PENDING
   *
   * @param td DOCUMENTATION PENDING
   */
  public void setTestDisabled(String td)
  {
    testDisabled = td;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getTestDisabled()
  {
    return testDisabled;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param cd DOCUMENTATION PENDING
   */
  public void setCreatedDate(Calendar cd)
  {
    createdDate = cd;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Calendar getCreatedDate()
  {
    return createdDate;
  }

  /**
   * @return
   */
  public String getIpRestrictions()
  {
    return ipRestrictions;
  }

  /**
   * @return
   */
  public String getPasswordRestriction()
  {
    return passwordRestriction;
  }

  /**
   * @return
   */
  public String getUsernameRestriction()
  {
    return usernameRestriction;
  }

  /**
   * @param string
   */
  public void setIpRestrictions(String string)
  {
    ipRestrictions = string;
  }

  /**
   * @param string
   */
  public void setPasswordRestriction(String string)
  {
    passwordRestriction = string;
  }

  /**
   * @param string
   */
  public void setUsernameRestriction(String string)
  {
    usernameRestriction = string;
  }
  
  
  public void setLateHandling(Character lh){
  	lateHandling = lh;
  }
  
  public Character getLateHandling(){
  	return lateHandling;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String toString()
  {
    StringBuffer result = new StringBuffer();
    SharedManager sm = null;
    try
    {
      sm = OsidManagerFactory.createSharedManager();
    }
    catch(OsidException e)
    {
      LOG.debug("Error getting shared manager");
    }

    Iterator iter = agentIdStrings.iterator();

    while(iter.hasNext())
    {
      String agentIdString = (String) iter.next();

      result.append("agentIdStrings        =" + agentIdString + "\n");
      try
      {
        result.append("--agent:" + sm.getAgent(sm.getId(agentIdString))+"\n");
      }
      catch(SharedException e1)
      {
        LOG.debug("Error resolving id for agentIdString=" + agentIdString);
      }
    }

    result.append("coreAssessmentIdString=" + coreAssessmentIdString + "\n");
    result.append("assessmentDisplayName =" + assessmentDisplayName + "\n");
    result.append("assessmentDescription =" + assessmentDescription + "\n");
    result.append("authorId              =" + authorId + "\n");
    try
    {
      result.append("--agent:" + sm.getAgent(sm.getId(authorId))+"\n");
    }
    catch(SharedException e1)
    {
      LOG.debug("Error resolving id for authorId=" + authorId);
    }

    result.append("beginDate             =" + beginDate + "\n");
    result.append("dueDate               =" + dueDate + "\n");
    result.append("feedbackDate          =" + feedbackDate + "\n");
    result.append("retractDate           =" + retractDate + "\n");
    result.append("feedbackType          =" + feedbackType + "\n");
		result.append("lateHandling          =" + lateHandling + "\n");

    result.append("maxAttempts           =" + maxAttempts + "\n");
    result.append("autoSubmit            =" + autoSubmit + "\n");
    result.append("autoSave		 =" + autoSave+ "\n");
    result.append("testDisabled          =" + testDisabled + "\n");
    result.append("createdDate           =" + createdDate + "\n");

    result.append("ipRestrictions        =" + ipRestrictions + "\n");
    result.append("usernameRestriction   =" + usernameRestriction + "\n");
    result.append("passwordRestriction   =" + passwordRestriction + "\n");

    return result.toString();
  }
}
