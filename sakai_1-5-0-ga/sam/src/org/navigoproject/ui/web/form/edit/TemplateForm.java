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

import org.navigoproject.osid.assessment.AssessmentServiceDelegate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * <p>
 * Title: NavigoProject.org
 * </p>
 *
 * <p>
 * Description: AAM - form class for assessDescEdit.jsp
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
 * @author Lydia Li
 *
 * @version 1.0
 */
public class TemplateForm
  extends ActionForm
{
  private HashMap values = new HashMap();
  private String templateName;
  private String templateAuthor;
  private String templateDescription;
  private String itemAccessType = "2";
  private String displayChunking = "1";
  private String questionNumbering = "1";
  private String submissionModel = "2";
  private String submissionNumber = "1";
  private String lateHandling = "0";
  private String autoSave = "0";
  private String feedbackType = "1";
  private Boolean feedbackComponent_QuestionText = Boolean.TRUE;
  private Boolean feedbackComponent_StudentResp = Boolean.TRUE;
  private Boolean feedbackComponent_CorrectResp = Boolean.TRUE;
  private Boolean feedbackComponent_StudentScore = Boolean.TRUE;
  private Boolean feedbackComponent_QuestionLevel = Boolean.TRUE;
  private Boolean feedbackComponent_SelectionLevel = Boolean.FALSE;
  private Boolean feedbackComponent_GraderComments = Boolean.TRUE;
  private Boolean feedbackComponent_Statistics = Boolean.TRUE;
  private HashMap feedbackTypes = new HashMap();
  private Boolean anonymousGrading;
  private String toGradebook = "0";
  private String recordedScore = "0";

  /**
   * This just sets some defaults.
   */
  public TemplateForm()
  {
    // Set correct defaults
    values.put("assessmentAuthor_isInstructorEditable", Boolean.TRUE);
    values.put("description_isInstructorEditable", Boolean.TRUE);
    values.put("dueDate_isInstructorEditable", Boolean.TRUE);
    values.put("releaseDate_isInstructorEditable", Boolean.TRUE);
    values.put("lateHandling_isInstructorEditable", Boolean.TRUE);
    values.put("feedbackType_isInstructorEditable", Boolean.TRUE);
  }

  /**
   * Reset checkboxes
   *
   * @param actionMapping The ActionMapping.
   * @param httpServletRequest The request.
   */
  public void reset(
    ActionMapping actionMapping, HttpServletRequest httpServletRequest)
  {
    Iterator iter = values.keySet().iterator();
    while(iter.hasNext())
    {
      values.put((String) iter.next(), Boolean.FALSE);
    }

    iter = feedbackTypes.keySet().iterator();
    while(iter.hasNext())
    {
      feedbackTypes.put((String) iter.next(), Boolean.FALSE);
    }

    //feedbackComponent_QuestionText = Boolean.FALSE;
    feedbackComponent_StudentResp = Boolean.FALSE;
    feedbackComponent_CorrectResp = Boolean.FALSE;
    feedbackComponent_StudentScore = Boolean.FALSE;
    feedbackComponent_QuestionLevel = Boolean.FALSE;
    feedbackComponent_SelectionLevel = Boolean.FALSE;
    feedbackComponent_GraderComments = Boolean.FALSE;
    feedbackComponent_Statistics = Boolean.FALSE;

    anonymousGrading = Boolean.FALSE;

    /*
       assessmentAuthor_isInstructorEditable = false;
       description_isInstructorEditable = false;
       dueDate_isInstructorEditable = false;
       releaseDate_isInstructorEditable = false;
       anonymousRelease_isInstructorEditable = false;
       authenticatedRelease_isInstructorEditable = false;
       fullClassRelease_isInstructorEditable = false;
       ipAccessType_isInstructorEditable = false;
       passwordRequired_isInstructorEditable = false;
       timedAssessment_isInstructorEditable = false;
       timedAssessmentAutoSubmit_isInstructorEditable = false;
       itemAccessType_isInstructorEditable = false;
       displayChunking_isInstructorEditable = false;
       submissionModel_isInstructorEditable = false;
       lateHandling_isInstructorEditable = false;
       submissionMessage_isInstructorEditable = false;
       submissionFinalURL_isInstructorEditable = false;
       feedbackType_isInstructorEditable = false;
       immediateFeedback_isInstructorEditable = false;
       testeeIdentity_isInstructorEditable = false;
       toGradebook_isInstructorEditable = false;
       recordedScore_isInstructorEditable = false;
       metadataAssess_isInstructorEditable = false;
       metadataParts_isInstructorEditable = false;
       metadataQuestions_isInstructorEditable = false;
     */
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param key DOCUMENTATION PENDING
   * @param value DOCUMENTATION PENDING
   */
  public void setValue(String key, Object value)
  {
    values.put(key, value);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param key DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Object getValue(String key)
  {
    if(values.get(key) == null)
    {
      return Boolean.FALSE;
    }

    return values.get(key);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newMap DOCUMENTATION PENDING
   */
  public void setValueMap(HashMap newMap)
  {
    values = newMap;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public HashMap getValueMap()
  {
    return values;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param key DOCUMENTATION PENDING
   * @param value DOCUMENTATION PENDING
   */
  public void setFeedbackType(String key, Object value)
  {
    values.put(key, value);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param key DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Object getFeedbackType(String key)
  {
    if(values.get(key) == null)
    {
      return Boolean.FALSE;
    }

    return values.get(key);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newMap DOCUMENTATION PENDING
   */
  public void setFeedbackTypeMap(HashMap newMap)
  {
    feedbackTypes = newMap;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public HashMap getFeedbackTypeMap()
  {
    return feedbackTypes;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newName DOCUMENTATION PENDING
   */
  public void setTemplateName(String newName)
  {
    templateName = newName;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getTemplateName()
  {
    return templateName;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newAuthor DOCUMENTATION PENDING
   */
  public void setTemplateAuthor(String newAuthor)
  {
    templateAuthor = newAuthor;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getTemplateAuthor()
  {
    return templateAuthor;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newDescription DOCUMENTATION PENDING
   */
  public void setTemplateDescription(String newDescription)
  {
    templateDescription = newDescription;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getTemplateDescription()
  {
    return templateDescription;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newItemAccessType DOCUMENTATION PENDING
   */
  public void setItemAccessType(String newItemAccessType)
  {
    itemAccessType = newItemAccessType;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getItemAccessType()
  {
    return itemAccessType;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newQuestionNumbering DOCUMENTATION PENDING
   */
  public void setQuestionNumbering(String newQuestionNumbering)
  {
    questionNumbering = newQuestionNumbering;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getQuestionNumbering()
  {
    return questionNumbering;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newDisplayChunking DOCUMENTATION PENDING
   */
  public void setDisplayChunking(String newDisplayChunking)
  {
    displayChunking = newDisplayChunking;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getDisplayChunking()
  {
    return displayChunking;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newSubmissionModel DOCUMENTATION PENDING
   */
  public void setSubmissionModel(String newSubmissionModel)
  {
    submissionModel = newSubmissionModel;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getSubmissionModel()
  {
    return submissionModel;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newSubmissionNumber DOCUMENTATION PENDING
   */
  public void setSubmissionNumber(String newSubmissionNumber)
  {
    submissionNumber = newSubmissionNumber;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getSubmissionNumber()
  {
    return submissionNumber;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newAutoSave DOCUMENTATION PENDING
   */
  public void setAutoSave(String newAutoSave)
  {
    autoSave = newAutoSave;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getAutoSave()
  {
    return autoSave;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newLateHandling DOCUMENTATION PENDING
   */
  public void setLateHandling(String newLateHandling)
  {
    lateHandling = newLateHandling;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getLateHandling()
  {
    return lateHandling;
  }

  // Feedback
  public void setFeedbackType(String newFeedbackType)
  {
    feedbackType = newFeedbackType;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getFeedbackType()
  {
    return feedbackType;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newComponent DOCUMENTATION PENDING
   */
  public void setFeedbackComponent_QuestionText(Boolean newComponent)
  {
    feedbackComponent_QuestionText = newComponent;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Boolean getFeedbackComponent_QuestionText()
  {
    return feedbackComponent_QuestionText;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newComponent DOCUMENTATION PENDING
   */
  public void setFeedbackComponent_StudentResp(Boolean newComponent)
  {
    feedbackComponent_StudentResp = newComponent;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Boolean getFeedbackComponent_StudentResp()
  {
    return feedbackComponent_StudentResp;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newComponent DOCUMENTATION PENDING
   */
  public void setFeedbackComponent_CorrectResp(Boolean newComponent)
  {
    feedbackComponent_CorrectResp = newComponent;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Boolean getFeedbackComponent_CorrectResp()
  {
    return feedbackComponent_CorrectResp;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newComponent DOCUMENTATION PENDING
   */
  public void setFeedbackComponent_StudentScore(Boolean newComponent)
  {
    feedbackComponent_StudentScore = newComponent;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Boolean getFeedbackComponent_StudentScore()
  {
    return feedbackComponent_StudentScore;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newComponent DOCUMENTATION PENDING
   */
  public void setFeedbackComponent_QuestionLevel(Boolean newComponent)
  {
    feedbackComponent_QuestionLevel = newComponent;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Boolean getFeedbackComponent_QuestionLevel()
  {
    return feedbackComponent_QuestionLevel;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newComponent DOCUMENTATION PENDING
   */
  public void setFeedbackComponent_SelectionLevel(Boolean newComponent)
  {
    feedbackComponent_SelectionLevel = newComponent;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Boolean getFeedbackComponent_SelectionLevel()
  {
    return feedbackComponent_SelectionLevel;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newComponent DOCUMENTATION PENDING
   */
  public void setFeedbackComponent_GraderComments(Boolean newComponent)
  {
    feedbackComponent_GraderComments = newComponent;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Boolean getFeedbackComponent_GraderComments()
  {
    return feedbackComponent_GraderComments;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newComponent DOCUMENTATION PENDING
   */
  public void setFeedbackComponent_Statistics(Boolean newComponent)
  {
    feedbackComponent_Statistics = newComponent;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Boolean getFeedbackComponent_Statistics()
  {
    return feedbackComponent_Statistics;
  }

  // Grading
  public void setAnonymousGrading(Boolean newAnonymousGrading)
  {
    anonymousGrading = newAnonymousGrading;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Boolean getAnonymousGrading()
  {
    return anonymousGrading;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newToGradebook DOCUMENTATION PENDING
   */
  public void setToGradebook(String newToGradebook)
  {
    toGradebook = newToGradebook;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getToGradebook()
  {
    return toGradebook;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newRecordedScore DOCUMENTATION PENDING
   */
  public void setRecordedScore(String newRecordedScore)
  {
    recordedScore = newRecordedScore;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getRecordedScore()
  {
    return recordedScore;
  }
}
