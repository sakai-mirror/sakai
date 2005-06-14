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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

 /**
  * <p>Copyright: Copyright (c) 2005</p>
  * <p>Organization: Sakai Project</p>
  * @author casong
  * @author Ed Smiley esmiley@stanford.edu
  * @version $Id: TemplateToMetadataMap.java,v 1.1 2005/01/04 20:44:35 esmiley.stanford.edu Exp $
  */
public class TemplateToMetadataMap
{
  private static TemplateToMetadataMap instance = null;
  private HashMap map = null;
  private List keyList = null;

  /**
   * Creates a new PropertyToMetadataMap object.
   */
  protected TemplateToMetadataMap()
  {
    map = new HashMap();

    //    Edit_Assessment_MetaData    metadataAssess_isInstructorEditable
    //    Edit_Collect_Section_Metadata   metadataParts_isInstructorEditable
    //    Edit_Collect_Item_Metadata    metadataQuestions_isInstructorEditable
    //    Edit_Navigation   itemAccessType_isInstructorEditable
    //    Edit_Max_Attempts   submissionModel_isInstructorEditable
    //    Edit_End_Date   dueDate_isInstructorEditable
    //    Edit_Feedback_Delivery    feedbackType_isInstructorEditable
    //    Edit_UserID   passwordRequired_isInstructorEditable
    //    EDIT_DESCRIPTION    description_isInstructorEditable
    //    EDIT_AUTO_SAVE    autoSave_isInstructorEditable
    //    EDIT_AUTHENTICATED_USERS   authenticatedRelease_isInstructorEditable
    //    EDIT_ALLOW_IP   ipAccessType_isInstructorEditable
    //    EDIT_ANONYMOUS    anonymousRelease_isInstructorEditable
    //    EDIT_GRADE_SCORE    recordedScore_isInstructorEditable
    //    EDIT_LATE_HANDLING    lateHandling_isInstructorEditable
    //    EDIT_AUTO_SUBMIT    timedAssessmentAutoSubmit_isInstructorEditable
    //    N/A   releaseDate_isInstructorEditable
    //    EDIT_FINISH_URL   submissionFinalURL_isInstructorEditable
    //    EDIT_GRADEBOOK_OPTION   toGradebook_isInstructorEditable
    //    N/A   templateName_isInstructorEditable
    //    EDIT_ASSESSFEEDBACK   submissionMessage_isInstructorEditable
    //    SHOW_CREATOR    assessmentCreator_isInstructorEditable
    //    EDIT_QUESTION_NUMBERING???   displayNumbering_isInstructorEditable
    //    EDIT_DURATION   timedAssessment_isInstructorEditable
    //    EDIT_ANONYMOUS_GRADING    testeeIdentity_isInstructorEditable
    //    Edit_Authors    assessmentAuthor_isInstructorEditable
    //    EDIT_FEEDBACK_COMPONENTS    feedbackComponents_isInstructorEditable
    map.put("displayChunking_isInstructorEditable", "EDIT_QUESTION_LAYOUT");
    map.put("metadataAssess_isInstructorEditable", "EDIT_ASSESSMENT_METADATA");
    map.put(
      "metadataParts_isInstructorEditable", "EDIT_COLLECT_SECTION_METADATA");
    map.put(
      "metadataQuestions_isInstructorEditable", "EDIT_COLLECT_ITEM_METADATA");
    map.put("itemAccessType_isInstructorEditable", "EDIT_NAVIGATION");
    map.put("submissionModel_isInstructorEditable", "EDIT_MAX_ATTEMPTS");
    map.put("dueDate_isInstructorEditable", "EDIT_END_DATE");
    map.put("feedbackType_isInstructorEditable", "EDIT_FEEDBACK_DELIVERY");
    map.put("passwordRequired_isInstructorEditable", "EDIT_USERID");
    map.put("description_isInstructorEditable", "EDIT_DESCRIPTION");
    map.put("autoSave_isInstructorEditable", "EDIT_AUTO_SAVE");
    map.put(
      "authenticatedRelease_isInstructorEditable", "EDIT_AUTHENTICATED_USERS");
    map.put("ipAccessType_isInstructorEditable", "EDIT_ALLOW_IP");
    map.put("anonymousRelease_isInstructorEditable", "EDIT_ANONYMOUS");
    map.put("recordedScore_isInstructorEditable", "EDIT_GRADE_SCORE");
    map.put("lateHandling_isInstructorEditable", "EDIT_LATE_HANDLING");
    map.put(
      "timedAssessmentAutoSubmit_isInstructorEditable", "EDIT_AUTO_SUBMIT");

    //N/A   releaseDate_isInstructorEditable
    map.put("submissionFinalURL_isInstructorEditable", "EDIT_FINISH_URL");
    map.put("toGradebook_isInstructorEditable", "EDIT_GRADEBOOK_OPTION");

    //N/A   templateName_isInstructorEditable
    map.put("submissionMessage_isInstructorEditable", "EDIT_ASSESSFEEDBACK");
    map.put("assessmentCreator_isInstructorEditable", "SHOW_CREATOR");
    map.put("displayNumbering_isInstructorEditable", "EDIT_QUESTION_NUMBERING");
    map.put("timedAssessment_isInstructorEditable", "EDIT_DURATION");
    map.put("testeeIdentity_isInstructorEditable", "EDIT_ANONYMOUS_GRADING");
    map.put("assessmentAuthor_isInstructorEditable", "EDIT_AUTHORS");
    map.put(
      "feedbackComponents_isInstructorEditable", "EDIT_FEEDBACK_COMPONENTS");

//chen change
    map.put("releaseDate_isInstructorEditable", "EDIT_RETRACT_DATE");
    map.put("bgColor_isInstructorEditable","EDIT_BGCOLOR");
    map.put("bgImage_isInstructorEditable","EDIT_BGIMG");
    map.put("templateName_isInstructorEditable", "DISPLAY_TEMPLATE");
    map.put("itemAccessType","NAVIGATION");
    map.put("feedbackType", "FEEDBACK_DELIVERY");
    map.put("feedbackComponent_QuestionText", "FEEDBACK_SHOW_QUESTION");
    map.put("feedbackComponent_StudentResp", "FEEDBACK_SHOW_RESPONSE");
    map.put("feedbackComponent_CorrectResp", "FEEDBACK_SHOW_CORRECT_RESPONSE");
    map.put("feedbackComponent_StudentScore", "FEEDBACK_SHOW_STUDENT_SCORE");
    map.put("feedbackComponent_QuestionLevel", "FEEDBACK_SHOW_ITEM_LEVEL");
    map.put("feedbackComponent_SelectionLevel", "FEEDBACK_SHOW_SELECTION_LEVEL");
    map.put("feedbackComponent_GraderComments", "FEEDBACK_SHOW_GRADER_COMMENT");
    map.put("feedbackComponent_Statistics", "FEEDBACK_SHOW_STATS");

    keyList = new ArrayList(map.keySet());
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public static TemplateToMetadataMap getInstance()
  {
    if(instance == null)
    {
      instance = new TemplateToMetadataMap();
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

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public HashMap getMap()
  {
    TemplateToMetadataMap.getInstance();

    return this.map;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public List getKeyList()
  {
    TemplateToMetadataMap.getInstance();

    return this.keyList;
  }
}
