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

package org.navigoproject.business.entity.assessment.model;

import org.navigoproject.business.entity.AssessmentTemplate;
import org.navigoproject.business.entity.properties.AssessmentProperties;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import osid.assessment.Section;
import osid.assessment.SectionIterator;

import osid.shared.Id;
import osid.shared.Type;

/**
 * <p>
 * Title: NavigoProject.org
 * </p>
 *
 * <p>
 * Description: OKI based implementation
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
 * @author Rachel Gollub
 * @version 1.0
 */
public class AssessmentPropertiesImpl
  implements AssessmentProperties
{
  private int assessmentPropertiesId;
  private Id assessmentId;

  // General data
  private Collection mediaCollection;
  private Type type;
  private Integer assessmentTypeId;
  private String author;
  private String description;
  private String objectives;
  private String keywords;
  private String rubrics;
  private String dueDate;
  private boolean isLate;
  private Id templateId;
  private AssessmentTemplate assessmentTemplate;

  // Features
  private boolean multiPartAllowed;
  private String itemAccessType;
  private boolean itemBookmarking;
  private String displayChunking;
  private String questionNumbering;
  private long timedAssessment;
  private boolean retryAllowed;

  // Access
  private Collection accessGroups;

  // This really belongs in AccessModel, but we're moving it here for now.
  // Feedback
  private FeedbackModel feedbackModel;
  private String feedbackType;
  private String feedbackComponents;

  // Submission
  private SubmissionModel submissionModel;
  private String lateHandling;
  private String submissionsSaved;
  private String autoSave;

  // Evaluation
  private String evaluationDistribution;
  private String testeeIdentity;
  private String evaluationComponents;
  private boolean autoScoring;
  private Integer scoringModelId;
  private ScoringModel scoringModel;

  // Distribution
  private Collection distributionGroups;

  // Notification
  private String testeeNotification;
  private String instructorNotification;

  // Assessment Sections
  private Id okiId;
  private Collection sections;

  // CourseId and AgentId - daisyf 10/21/03
  private Long courseId;
  private String agentId;

  // Grading    
  private boolean anonymousGrading;
  private String toGradebook;
  private String recordedScore;

  // Other
  private Date lastModified;

  /**
   * Creates a new AssessmentPropertiesImpl object.
   */
  public AssessmentPropertiesImpl()
  {
    mediaCollection = new ArrayList();
    accessGroups = new Vector();
    distributionGroups = new Vector();
    sections = new Vector();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Id getAssessmentId()
  {
    return assessmentId;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pId DOCUMENTATION PENDING
   */
  public void setAssessmentId(Id pId)
  {
    assessmentId = pId;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Type getType()
  {
    return type;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param ptype DOCUMENTATION PENDING
   */
  public void setType(Type ptype)
  {
    type = ptype;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getDueDate()
  {
    return dueDate;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param dateString DOCUMENTATION PENDING
   */
  public void setDueDate(String dateString)
  {
    dueDate = dateString;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getMediaCollection()
  {
    return mediaCollection;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pmediaCollection DOCUMENTATION PENDING
   */
  public void setMediaCollection(Collection pmediaCollection)
  {
    mediaCollection = pmediaCollection;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getReversedMediaCollection()
  {
    List l = new ArrayList(this.mediaCollection);
    Collections.reverse(l);

    return l;
  }

  /**
   * This should be a MediaData object.
   *
   * @param pmedia DOCUMENTATION PENDING
   */
  public void addMedia(Object pmedia)
  {
    mediaCollection.add(pmedia);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pmedia DOCUMENTATION PENDING
   */
  public void removeMedia(Object pmedia)
  {
    mediaCollection.remove(pmedia);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getObjectives()
  {
    return objectives;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pobjectives DOCUMENTATION PENDING
   */
  public void setObjectives(String pobjectives)
  {
    objectives = pobjectives;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getKeywords()
  {
    return keywords;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pkeywords DOCUMENTATION PENDING
   */
  public void setKeywords(String pkeywords)
  {
    keywords = pkeywords;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getRubrics()
  {
    return rubrics;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param prubrics DOCUMENTATION PENDING
   */
  public void setRubrics(String prubrics)
  {
    rubrics = prubrics;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public AssessmentTemplate getAssessmentTemplate()
  {
    return assessmentTemplate;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param passessmentTemplate DOCUMENTATION PENDING
   */
  public void setAssessmentTemplate(AssessmentTemplate passessmentTemplate)
  {
    assessmentTemplate = passessmentTemplate;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getMultiPartAllowed()
  {
    return multiPartAllowed;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pmultiPartAllowed DOCUMENTATION PENDING
   */
  public void setMultiPartAllowed(boolean pmultiPartAllowed)
  {
    multiPartAllowed = pmultiPartAllowed;
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
   * @param pitemAccessType DOCUMENTATION PENDING
   */
  public void setItemAccessType(String pitemAccessType)
  {
    itemAccessType = pitemAccessType;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getItemBookmarking()
  {
    return itemBookmarking;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pitemBookmarking DOCUMENTATION PENDING
   */
  public void setItemBookmarking(boolean pitemBookmarking)
  {
    itemBookmarking = pitemBookmarking;
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
   * @param pdisplayChunking DOCUMENTATION PENDING
   */
  public void setDisplayChunking(String pdisplayChunking)
  {
    displayChunking = pdisplayChunking;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getAccessGroups()
  {
    return accessGroups;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param paccessGroups DOCUMENTATION PENDING
   */
  public void setAccessGroups(Collection paccessGroups)
  {
    accessGroups = paccessGroups;
  }

  /**
   * Add an access group.
   *
   * @param pgroup an AccessGroup object.
   */
  public void addAccessGroup(Object pgroup)
  {
    accessGroups.add(pgroup);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pgroup DOCUMENTATION PENDING
   */
  public void removeAccessGroup(Object pgroup)
  {
    accessGroups.remove(pgroup);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Object getFeedbackModel()
  {
    return feedbackModel;
  }

  /**
   * Set the feedback model.
   *
   * @param pfeedbackModel a FeedbackModel object.
   */
  public void setFeedbackModel(Object pfeedbackModel)
  {
    feedbackModel = (FeedbackModel) pfeedbackModel;
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
   * @param pfeedbackType DOCUMENTATION PENDING
   */
  public void setFeedbackType(String pfeedbackType)
  {
    feedbackType = pfeedbackType;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getFeedbackComponents()
  {
    return feedbackComponents;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pfeedbackType DOCUMENTATION PENDING
   */
  public void setFeedbackComponents(String pfeedbackComponents)
  {
    feedbackComponents = pfeedbackComponents;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Object getSubmissionModel()
  {
    return submissionModel;
  }

  /**
   * Set the submission model.
   *
   * @param psubmissionModel a SubmissionModel object.
   */
  public void setSubmissionModel(Object psubmissionModel)
  {
    submissionModel = (SubmissionModel) psubmissionModel;
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

  /**
   * DOCUMENTATION PENDING
   *
   * @param plateHandling DOCUMENTATION PENDING
   */
  public void setLateHandling(String plateHandling)
  {
    lateHandling = plateHandling;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getEvaluationDistribution()
  {
    return evaluationDistribution;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pevaluationDistribution DOCUMENTATION PENDING
   */
  public void setEvaluationDistribution(String pevaluationDistribution)
  {
    evaluationDistribution = pevaluationDistribution;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getTesteeIdentity()
  {
    return testeeIdentity;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param ptesteeIdentity DOCUMENTATION PENDING
   */
  public void setTesteeIdentity(String ptesteeIdentity)
  {
    testeeIdentity = ptesteeIdentity;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getEvaluationComponents()
  {
    return evaluationComponents;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pevaluationComponents DOCUMENTATION PENDING
   */
  public void setEvaluationComponents(String pevaluationComponents)
  {
    evaluationComponents = pevaluationComponents;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getAutoScoring()
  {
    return autoScoring;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pautoScoring DOCUMENTATION PENDING
   */
  public void setAutoScoring(boolean pautoScoring)
  {
    autoScoring = pautoScoring;
  }

  /**
   * Get the scoring model.
   *
   * @return A ScoringModel object.
   */
  public Object getScoringModel()
  {
    return scoringModel;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pscoringModel DOCUMENTATION PENDING
   */
  public void setScoringModel(Object pscoringModel)
  {
    scoringModel = (ScoringModel) pscoringModel;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getDistributionGroups()
  {
    return distributionGroups;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pdistributionGroups DOCUMENTATION PENDING
   */
  public void setDistributionGroups(Collection pdistributionGroups)
  {
    distributionGroups = pdistributionGroups;
  }

  /**
   * Add a group to distribute results to.
   *
   * @param pgroup A DistributionGroup object.
   */
  public void addDistributionGroup(Object pgroup)
  {
    distributionGroups.add(pgroup);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pgroup DOCUMENTATION PENDING
   */
  public void removeDistributionGroup(Object pgroup)
  {
    distributionGroups.remove(pgroup);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getTesteeNotification()
  {
    return testeeNotification;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param ptesteeNotification DOCUMENTATION PENDING
   */
  public void setTesteeNotification(String ptesteeNotification)
  {
    testeeNotification = ptesteeNotification;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getInstructorNotification()
  {
    return instructorNotification;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pinstructorNotification DOCUMENTATION PENDING
   */
  public void setInstructorNotification(String pinstructorNotification)
  {
    instructorNotification = pinstructorNotification;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getSubmissionsSaved()
  {
    return submissionsSaved;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param psubmissionsSaved DOCUMENTATION PENDING
   */
  public void setSubmissionsSaved(String psubmissionsSaved)
  {
    submissionsSaved = psubmissionsSaved;
  }

  // the following get & set of courseId and agentId are 
  // added on 10/21/03 - daisyf
  public Long getCourseId()
  {
    return courseId;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pcourseId DOCUMENTATION PENDING
   */
  public void setCourseId(Long pcourseId)
  {
    courseId = pcourseId;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getAgentId()
  {
    return agentId;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pagentId DOCUMENTATION PENDING
   */
  public void setAgentId(String pagentId)
  {
    agentId = pagentId;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param section DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   */
  public void addSection(Section section)
    throws osid.assessment.AssessmentException
  {
    sections.add(section);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param sectionId DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   */
  public void removeSection(osid.shared.Id sectionId)
    throws osid.assessment.AssessmentException
  {
    Iterator iter = sections.iterator();
    while(iter.hasNext())
    {
      Section section = (Section) iter.next();
      if(section.getId().equals(sectionId))
      {
        sections.remove(section);

        break;
      }
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   */
  public SectionIterator getSections()
    throws osid.assessment.AssessmentException
  {
    return new SectionIteratorImpl(sections);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param psections DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   */
  public void orderSections(Section[] psections)
    throws osid.assessment.AssessmentException
  {
    sections = new Vector();
    for(int i = 0; i < psections.length; i++)
    {
      sections.add(psections[i]);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getIsLate()
  {
    return isLate;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pIsLate DOCUMENTATION PENDING
   */
  public void setIsLate(boolean pIsLate)
  {
    isLate = pIsLate;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getHasRelatedMedia()
  {
    Iterator iter = mediaCollection.iterator();
    while(iter.hasNext())
    {
      if(((MediaData) iter.next()).getIsLink())
      {
        return true;
      }
    }

    return false;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public long getTimedAssessment()
  {
    return timedAssessment;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param ptimedAssessment DOCUMENTATION PENDING
   */
  public void setTimedAssessment(long ptimedAssessment)
  {
    timedAssessment = ptimedAssessment;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getRetryAllowed()
  {
    return retryAllowed;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pretryAllowed DOCUMENTATION PENDING
   */
  public void setRetryAllowed(boolean pretryAllowed)
  {
    retryAllowed = pretryAllowed;
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
   * @param pAutosave DOCUMENTATION PENDING
   */
  public void setAutoSave(String pAutosave)
  {
    autoSave = pAutosave;
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
   * @param pQuestionNumbering DOCUMENTATION PENDING
   */
  public void setQuestionNumbering(String pQuestionNumbering)
  {
    questionNumbering = pQuestionNumbering;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getAnonymousGrading()
  {
    return anonymousGrading;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param panonymousGrading DOCUMENTATION PENDING
   */
  public void setAnonymousGrading(boolean pAnonymousGrading)
  {
    anonymousGrading = pAnonymousGrading;
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
   * @param pToGradebookDOCUMENTATION PENDING
   */
  public void setToGradebook(String pToGradebook)
  {
    toGradebook = pToGradebook;
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

  /**
   * DOCUMENTATION PENDING
   *
   * @param pRecordedScore DOCUMENTATION PENDING
   */
  public void setRecordedScore(String pRecordedScore)
  {
    recordedScore = pRecordedScore;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getLastModified()
  {
    DateFormat dateFormatter;

    dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT);

    return dateFormatter.format(lastModified);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pLastModified DOCUMENTATION PENDING
   */
  public void setLastModified(Date pLastModified)
  {
    lastModified = pLastModified;
  }
}
