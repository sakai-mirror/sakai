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
 * Created on Dec 24, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.business.entity.properties;

import java.util.HashMap;

/**
 * @author casong
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
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
