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

package org.navigoproject.business.entity.properties;

import org.navigoproject.business.entity.AssessmentTemplate;

import java.io.Serializable;

import java.util.Collection;
import java.util.Date;

import osid.shared.Type;

//import edu.stanford.aam.common.Type;

/**
 * <p>Title: NavigoProject.org</p>
 * <p>Description: OKI based implementation</p>
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * <p>Company: Stanford University</p>
 * @author Rachel Gollub
 * @version 1.0
 */
public interface AssessmentProperties
  extends Serializable
{
  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Type getType();

  /**
   * DOCUMENTATION PENDING
   *
   * @param ptype DOCUMENTATION PENDING
   */
  public void setType(Type ptype);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getMediaCollection();

  /**
   * DOCUMENTATION PENDING
   *
   * @param pmediaCollection DOCUMENTATION PENDING
   */
  public void setMediaCollection(Collection pmediaCollection);

  /**
   * DOCUMENTATION PENDING
   *
   * @param pmedia DOCUMENTATION PENDING
   */
  public void addMedia(Object pmedia);

  /**
   * DOCUMENTATION PENDING
   *
   * @param pmedia DOCUMENTATION PENDING
   */
  public void removeMedia(Object pmedia);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getObjectives();

  /**
   * DOCUMENTATION PENDING
   *
   * @param pobjectives DOCUMENTATION PENDING
   */
  public void setObjectives(String pobjectives);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getKeywords();

  /**
   * DOCUMENTATION PENDING
   *
   * @param pkeywords DOCUMENTATION PENDING
   */
  public void setKeywords(String pkeywords);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getRubrics();

  /**
   * DOCUMENTATION PENDING
   *
   * @param prubrics DOCUMENTATION PENDING
   */
  public void setRubrics(String prubrics);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public AssessmentTemplate getAssessmentTemplate();

  /**
   * DOCUMENTATION PENDING
   *
   * @param passessmentTemplate DOCUMENTATION PENDING
   */
  public void setAssessmentTemplate(AssessmentTemplate passessmentTemplate);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getMultiPartAllowed();

  /**
   * DOCUMENTATION PENDING
   *
   * @param pmultiPartAllowed DOCUMENTATION PENDING
   */
  public void setMultiPartAllowed(boolean pmultiPartAllowed);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getItemAccessType();

  /**
   * DOCUMENTATION PENDING
   *
   * @param pitemAccessType DOCUMENTATION PENDING
   */
  public void setItemAccessType(String pitemAccessType);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getItemBookmarking();

  /**
   * DOCUMENTATION PENDING
   *
   * @param pitemBookmarking DOCUMENTATION PENDING
   */
  public void setItemBookmarking(boolean pitemBookmarking);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getDisplayChunking();

  /**
   * DOCUMENTATION PENDING
   *
   * @param pdisplayChunking DOCUMENTATION PENDING
   */
  public void setDisplayChunking(String pdisplayChunking);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getAccessGroups();

  /**
   * DOCUMENTATION PENDING
   *
   * @param paccessGroups DOCUMENTATION PENDING
   */
  public void setAccessGroups(Collection paccessGroups);

  /**
   * DOCUMENTATION PENDING
   *
   * @param pgroup DOCUMENTATION PENDING
   */
  public void addAccessGroup(Object pgroup);

  /**
   * DOCUMENTATION PENDING
   *
   * @param pgroup DOCUMENTATION PENDING
   */
  public void removeAccessGroup(Object pgroup);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Object getFeedbackModel();

  /**
   * DOCUMENTATION PENDING
   *
   * @param pfeedbackModel DOCUMENTATION PENDING
   */
  public void setFeedbackModel(Object pfeedbackModel);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getFeedbackComponents();

  /**
   * DOCUMENTATION PENDING
   *
   * @param pfeedbackComponents DOCUMENTATION PENDING
   */
  public void setFeedbackComponents(String pfeedbackComponents);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getFeedbackType();

  /**
   * DOCUMENTATION PENDING
   *
   * @param pfeedbackTypeDOCUMENTATION PENDING
   */
  public void setFeedbackType(String pfeedbackType);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Object getSubmissionModel();

  /**
   * DOCUMENTATION PENDING
   *
   * @param psubmissionModel DOCUMENTATION PENDING
   */
  public void setSubmissionModel(Object psubmissionModel);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getLateHandling();

  /**
   * DOCUMENTATION PENDING
   *
   * @param plateHandling DOCUMENTATION PENDING
   */
  public void setLateHandling(String plateHandling);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getEvaluationDistribution();

  /**
   * DOCUMENTATION PENDING
   *
   * @param pevaluationDistribution DOCUMENTATION PENDING
   */
  public void setEvaluationDistribution(String pevaluationDistribution);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getTesteeIdentity();

  /**
   * DOCUMENTATION PENDING
   *
   * @param ptesteeIdentity DOCUMENTATION PENDING
   */
  public void setTesteeIdentity(String ptesteeIdentity);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getEvaluationComponents();

  /**
   * DOCUMENTATION PENDING
   *
   * @param pevaluationComponents DOCUMENTATION PENDING
   */
  public void setEvaluationComponents(String pevaluationComponents);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getAutoScoring();

  /**
   * DOCUMENTATION PENDING
   *
   * @param pautoScoring DOCUMENTATION PENDING
   */
  public void setAutoScoring(boolean pautoScoring);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Object getScoringModel();

  /**
   * DOCUMENTATION PENDING
   *
   * @param pscoringModel DOCUMENTATION PENDING
   */
  public void setScoringModel(Object pscoringModel);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getDistributionGroups();

  /**
   * DOCUMENTATION PENDING
   *
   * @param pdistributionGroups DOCUMENTATION PENDING
   */
  public void setDistributionGroups(Collection pdistributionGroups);

  /**
   * DOCUMENTATION PENDING
   *
   * @param pgroup DOCUMENTATION PENDING
   */
  public void addDistributionGroup(Object pgroup);

  /**
   * DOCUMENTATION PENDING
   *
   * @param pgroup DOCUMENTATION PENDING
   */
  public void removeDistributionGroup(Object pgroup);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getTesteeNotification();

  /**
   * DOCUMENTATION PENDING
   *
   * @param ptesteeNotification DOCUMENTATION PENDING
   */
  public void setTesteeNotification(String ptesteeNotification);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getInstructorNotification();

  /**
   * DOCUMENTATION PENDING
   *
   * @param pinstructorNotification DOCUMENTATION PENDING
   */
  public void setInstructorNotification(String pinstructorNotification);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public long getTimedAssessment();

  /**
   * DOCUMENTATION PENDING
   *
   * @param ptimedAssessment DOCUMENTATION PENDING
   */
  public void setTimedAssessment(long ptimedAssessment);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getSubmissionsSaved();

  /**
   * DOCUMENTATION PENDING
   *
   * @param psubmissionsSaved DOCUMENTATION PENDING
   */
  public void setSubmissionsSaved(String psubmissionsSaved);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getRetryAllowed();

  /**
   * DOCUMENTATION PENDING
   *
   * @param pretryAllowed DOCUMENTATION PENDING
   */
  public void setRetryAllowed(boolean pretryAllowed);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getQuestionNumbering();

  /**
   * DOCUMENTATION PENDING
   *
   * @param pNumbering DOCUMENTATION PENDING
   */
  public void setQuestionNumbering(String pNumbering);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getAutoSave();

  /**
   * DOCUMENTATION PENDING
   *
   * @param pAutosave DOCUMENTATION PENDING
   */
  public void setAutoSave(String pAutosave);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getAnonymousGrading();

  /**
   * DOCUMENTATION PENDING
   *
   * @param anonymousGrading DOCUMENTATION PENDING
   */
  public void setAnonymousGrading(boolean anonymousGrading);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getToGradebook();

  /**
   * DOCUMENTATION PENDING
   *
   * @param toGradebook DOCUMENTATION PENDING
   */
  public void setToGradebook(String toGradebook);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getRecordedScore();

  /**
   * DOCUMENTATION PENDING
   *
   * @param recordedScore DOCUMENTATION PENDING
   */
  public void setRecordedScore(String recordedScore);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getLastModified();

  /**
   * DOCUMENTATION PENDING
   *
   * @param lastModified DOCUMENTATION PENDING
   */
  public void setLastModified(Date lastModified);
}
