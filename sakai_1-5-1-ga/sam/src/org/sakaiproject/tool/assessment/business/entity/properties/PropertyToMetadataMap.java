/*
 * Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
 *                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
 *
 * Licensed under the Educational Community License Version 1.0 (the "License");
 * By obtaining, using and/or copying this Original Work, you agree that you have read,
 * understand, and will comply with the terms and conditions of the Educational Community License.
 * You may obtain a copy of the License at:
 *
 *      http://cvs.sakaiproject.org/licenses/license_1_0.html
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


package org.sakaiproject.tool.assessment.business.entity.properties;

import java.util.HashMap;

/**
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Organization: Sakai Project</p>
 * @author casong
 * @version $Id: PropertyToMetadataMap.java,v 1.1 2005/01/04 20:44:35 esmiley.stanford.edu Exp $
 */
public class PropertyToMetadataMap
{
  private static PropertyToMetadataMap instance = null;
  private HashMap map = null;

  /**
   * Creates a new PropertyToMetadataMap object.
   */
  protected PropertyToMetadataMap()
  {
    map = new HashMap();

    //  assessmentPropertiesId
    //  assessmentId
    //  type
    //  assessmentTypeId
    //  description
    //  objectives
    //  keywords
    //  rubrics
    //TODO add these metadata into
    map.put("dueDate", "DUE_DATE");
    map.put("isLate", "IS_LATE");
    map.put("templateId", "TEMPLATE_ID");

    //  assessmentTemplate
    map.put("multiPartAllowed", "MULTI_PART_ALLOWED");
    map.put("itemAccessType", "ITEM_ACCESS_TYPE");
    map.put("itemBookmarking", "ITEM_BOOK_MARKING");
    map.put("displayChunking", "DISPLAY_CHUNKING");

    //  Collection accessGroups
    //  FeedbackModel feedbackModel
    map.put("feedbackType", "FEEDBACK_TYPE");
    map.put("immediateFeedbackType", "IMMEDIATE_FEEDBACK_TYPE");
    map.put("datedFeedbackType", "DATE_FEEDBACK_TYPE");
    map.put("perQuestionFeedbackType", "PER_QUESTION_FEEDBACK_TYPE");
    map.put("feedbackDate", "FEEDBACK_DATE");
    map.put("scoreDate", "SCORE_DATE");

    //  SubmissionModel submissionModel
    map.put("numberSubmissions", "SUBMISSION_TYPE");
    map.put("submissionsAllowed", "MAX_ATTEMPTS");

    //  Int submissionModelId
    map.put("lateHandling", "LATE_HANDLING");
    map.put("submissionsSaved", "SUBMISSION_SAVED");
    map.put("evaluationDistribution", "EVALUATION_DISTRIBUTION");
    map.put("testeeIdentity", "TESTEE_IDENTITY");
    map.put("evaluationComponents", "EVALUATION_COMPONENTS");
    map.put("autoScoring", "AUTO_SCORING");

    //  Integer scoringModelId;
    //  ScoringModel scoringModel;
    //  int scoringModelId
    map.put("scoringType", "SCORING_TYPE");
    map.put("numericModel", "NUMERIC_MODEL");
    map.put("defaultQuestionValue", "DEFAULT_QUESTION_VALUE");
    map.put("fixedTotalScore", "FIXED_TOTAL_SCORE");

    //  Collection distributionGroups
    map.put("testeeNotification", "TESTEE_NOTIFICATION");
    map.put("instructorNotification", "INSTRUCTOR_NOTIFICATION");

    //  Id okiId
    //  Collection sections
    //  Long courseId
    //  String agentId
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public static PropertyToMetadataMap getInstance()
  {
    if(instance == null)
    {
      instance = new PropertyToMetadataMap();
    }

    return instance;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param propertyName DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getMetadataLabel(String propertyName)
  {
    Object object = map.get(propertyName);
    if(object != null)
    {
      return (String) object;
    }

    return null;
  }
}
