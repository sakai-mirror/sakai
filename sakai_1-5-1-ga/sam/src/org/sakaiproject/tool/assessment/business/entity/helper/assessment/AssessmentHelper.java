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

package org.sakaiproject.tool.assessment.business.entity.helper.assessment;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import org.sakaiproject.tool.assessment.business.entity.asi.Assessment;
import org.sakaiproject.tool.assessment.business.entity.asi.Section;
import org.sakaiproject.tool.assessment.business.entity.helper.AuthoringHelper;
import org.sakaiproject.tool.assessment.business.entity.helper.section.SectionHelper;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AssessmentAccessControlIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AssessmentFeedbackIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AssessmentMetaDataIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.EvaluationModelIfc;
import org.sakaiproject.tool.assessment.util.Iso8601DateFormat;
import org.sakaiproject.tool.assessment.util.Iso8601TimeInterval;
import org.sakaiproject.tool.assessment.facade.AssessmentFacade;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.apache.xerces.dom.CharacterDataImpl;

/**
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organization: Sakai Project</p>
 * @author rshastri
 * @author Ed Smiley esmiley@stanford.edu
 * @version $Id: AssessmentHelper.java,v 1.10.2.3 2005/03/31 01:50:41 esmiley.stanford.edu Exp $
 */
public class AssessmentHelper
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(AssessmentHelper.class);

  private Document doc;

  /**
   * DOCUMENTATION PENDING
   *
   * @param inputStream DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Assessment readXMLDocument(
    InputStream inputStream)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("readDocument(InputStream " + inputStream);
    }

    Assessment assessXml = null;

    try
    {
      AuthoringHelper authoringHelper = new AuthoringHelper();
      assessXml =
        new Assessment(
        authoringHelper.readXMLDocument(inputStream).getDocument());
    }
    catch (ParserConfigurationException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch (SAXException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch (IOException e)
    {
      LOG.error(e.getMessage(), e);
    }

    return assessXml;
  }

  /**
   * Set feedback settings in XML
   * @param assessmentXml
   * @param feedback
   */
  public void updateFeedbackModel(Assessment assessmentXml,
    AssessmentFeedbackIfc feedback)
  {
    Integer feedbackDelivery = feedback.getFeedbackDelivery();
    if (feedback.FEEDBACK_BY_DATE.equals(feedbackDelivery))
    {
      assessmentXml.setFieldentry("FEEDBACK_DELIVERY", "IMMEDIATE");
    }
    else if (feedback.IMMEDIATE_FEEDBACK.equals(feedbackDelivery))
    {
      assessmentXml.setFieldentry("FEEDBACK_DELIVERY", "DATED");
    }
    else //feedback.NO_FEEDBACK
    {
      assessmentXml.setFieldentry("FEEDBACK_DELIVERY", "NONE");
    }

    assessmentXml.setFieldentry("FEEDBACK_SHOW_QUESTION",
      qtiBooleanString(feedback.getShowQuestionText()));
    assessmentXml.setFieldentry("FEEDBACK_SHOW_RESPONSE",
      qtiBooleanString(feedback.getShowStudentResponse()));
    assessmentXml.setFieldentry("FEEDBACK_SHOW_CORRECT_RESPONSE",
      qtiBooleanString(feedback.getShowCorrectResponse()));
    assessmentXml.setFieldentry("FEEDBACK_SHOW_STUDENT_SCORE",
      qtiBooleanString(feedback.getShowStudentScore()));
    assessmentXml.setFieldentry("FEEDBACK_SHOW_ITEM_LEVEL",
      qtiBooleanString(feedback.getShowQuestionLevelFeedback()));
    assessmentXml.setFieldentry("FEEDBACK_SHOW_SELECTION_LEVEL",
      qtiBooleanString(feedback.getShowSelectionLevelFeedback()));
    assessmentXml.setFieldentry("FEEDBACK_SHOW_GRADER_COMMENT",
      qtiBooleanString(feedback.getShowGraderComments()));
    assessmentXml.setFieldentry("FEEDBACK_SHOW_STATS",
      qtiBooleanString(feedback.getShowStatistics()));
  }

  /**
   * Set evaluation settings in XML.
   * @param assessmentXml
   * @param evaluationModel
   */
  public void updateEvaluationModel(Assessment assessmentXml,
    EvaluationModelIfc evaluationModel)
  {
    // anonymous
    Integer anon = evaluationModel.getAnonymousGrading();
    assessmentXml.setFieldentry("ANONYMOUS_GRADING", qtiBooleanString(anon));

    // to gradebook
    if ("1".equals(evaluationModel.getToGradeBook()))
    {
      assessmentXml.setFieldentry("GRADEBOOK_OPTIONS", "DEFAULT");
    }
    else
    {
      assessmentXml.setFieldentry("GRADEBOOK_OPTIONS", "SELECTED");
    }

    // scoring type
    if ("1".equals(evaluationModel.getScoringType()))
    {
      assessmentXml.setFieldentry("GRADE_SCORE", "HIGHEST");
    }
    else
    {
      assessmentXml.setFieldentry("GRADE_SCORE", "AVERAGE");
    }

  }

  /**
   * Set access control settings in XML.
   * @param assessmentXml
   * @param accessControl
   */
  public void updateAccessControl(Assessment assessmentXml,
    AssessmentAccessControlIfc accessControl)
  {
    // DATES
    Date dueDate = accessControl.getDueDate();
    Date startDate = accessControl.getStartDate();
    Date scoreDate = accessControl.getScoreDate();
    Date restractDate = accessControl.getRetractDate();
    Date feedbackDate = accessControl.getFeedbackDate();
    assessmentXml.setFieldentry("END_DATE", formatDate(dueDate));
    assessmentXml.setFieldentry("FEEDBACK_DELIVERY_DATE",
      formatDate(feedbackDate));
    assessmentXml.setFieldentry("RETRACT_DATE", formatDate(restractDate));
    assessmentXml.setFieldentry("START_DATE", formatDate(startDate));

    //MAX_ATTEMPTS
    Integer submissionsAllowed = accessControl.getSubmissionsAllowed();
    System.out.println("submissionsAllowed="+submissionsAllowed);
    // set if unlimited
    Boolean unlimitedSubmissions = accessControl.getUnlimitedSubmissions();
    boolean unlimited = false;
    if (unlimitedSubmissions != null)
    {
      unlimited = unlimitedSubmissions.booleanValue();
      System.out.println("unlimited="+unlimited);
    }
    if (unlimited)
    {
      submissionsAllowed = accessControl.UNLIMITED_SUBMISSIONS_ALLOWED;
    }
    System.out.println("Setting MAX_ATTEMPTS to submissionsAllowed="+submissionsAllowed);
    assessmentXml.setFieldentry("MAX_ATTEMPTS",
      submissionsAllowed.toString());

    // OTHER CONTROLS
    Integer autoSubmit = accessControl.getAutoSubmit();
    Integer bookmarking = accessControl.getBookMarkingItem();
    Integer itemNavigation = accessControl.getItemNavigation();
    Integer itemNumbering = accessControl.getItemNumbering();
    Integer lateHandling = accessControl.getLateHandling();
    Integer retryAllowed = accessControl.getRetryAllowed();
    Integer submissionsSaved = accessControl.getSubmissionsSaved();
    Integer timeLimit = accessControl.getTimeLimit();
    String submissionMessage = accessControl.getSubmissionMessage();
    String password = accessControl.getPassword();
    String releaseTo = accessControl.getReleaseTo();
    String userName = accessControl.getUsername();
    System.out.println("AUTO_SUBMIT " + autoSubmit);
    assessmentXml.setFieldentry("AUTO_SUBMIT", qtiBooleanString(autoSubmit));

    // getTimedAssessment() does not always tell us
    if (timeLimit != null && timeLimit != new Integer(0))
    {
      System.out.println("timeLimit="+timeLimit);
      setDuration(timeLimit, assessmentXml);
    }

    if (accessControl.BY_QUESTION.equals(itemNumbering))
    {
      assessmentXml.setFieldentry("QUESTION_LAYOUT", "I");
    }
    else if (accessControl.BY_PART.equals(itemNumbering))
    {
      assessmentXml.setFieldentry("QUESTION_LAYOUT", "S");
    }
    else if (accessControl.BY_ASSESSMENT.equals(itemNumbering))
    {
      assessmentXml.setFieldentry("QUESTION_LAYOUT", "A");
    }

    if (accessControl.LINEAR_ACCESS.equals(itemNavigation))
    {
      assessmentXml.setFieldentry("NAVIGATION", "LINEAR");
    }
    else if (accessControl.RANDOM_ACCESS.equals(itemNavigation))
    {
      assessmentXml.setFieldentry("NAVIGATION", "RANDOM");
    }

    if (accessControl.CONTINUOUS_NUMBERING.equals(itemNumbering))
    {
      assessmentXml.setFieldentry("QUESTION_NUMBERING", "CONTINUOUS");
    }
    else if (accessControl.RESTART_NUMBERING_BY_PART.equals(itemNumbering))
    {
      assessmentXml.setFieldentry("QUESTION_NUMBERING", "RESTART");
    }

    if (accessControl.ACCEPT_LATE_SUBMISSION.equals(lateHandling))
    {
      assessmentXml.setFieldentry("LATE_HANDLING", "True");
    }
    else if (accessControl.NOT_ACCEPT_LATE_SUBMISSION.equals(lateHandling))
    {
      assessmentXml.setFieldentry("LATE_HANDLING", "False");
    }

    if (password != null)
    {
      assessmentXml.setFieldentry("PASSWORD", password);
    }
    if (releaseTo != null)
    {
      assessmentXml.setFieldentry("ASSESSMENT_RELEASED_TO", releaseTo);
    }
    if (userName != null)
    {
      assessmentXml.setFieldentry("USERID", userName);
    }
  }

  /**
   * Look up and set any xxx_isInstructorEditable fields
   * @param assessmentXml
   * @param assessment
   */
  public void updateMetaData(Assessment assessmentXml,
    AssessmentFacade assessment)
  {
    String[] editKeys = {
        "templateInfo_isInstructorEditable",
        "assessmentAuthor_isInstructorEditable",
        "assessmentCreator_isInstructorEditable",
        "description_isInstructorEditable",
        "dueDate_isInstructorEditable",
        "retractDate_isInstructorEditable",
        "anonymousRelease_isInstructorEditable",
        "authenticatedRelease_isInstructorEditable",
        "ipAccessType_isInstructorEditable",
        "passwordRequired_isInstructorEditable",
        "timedAssessment_isInstructorEditable",
        "timedAssessmentAutoSubmit_isInstructorEditable",
        "itemAccessType_isInstructorEditable",
        "displayChunking_isInstructorEditable",
        "displayNumbering_isInstructorEditable",
        "submissionModel_isInstructorEditable",
        "lateHandling_isInstructorEditable",
        "autoSave_isInstructorEditable",
        "submissionMessage_isInstructorEditable",
        "finalPageURL_isInstructorEditable",
        "feedbackType_isInstructorEditable",
        "feedbackComponents_isInstructorEditable",
        "testeeIdentity_isInstructorEditable",
        "toGradebook_isInstructorEditable",
        "recordedScore_isInstructorEditable",
        "bgColor_isInstructorEditable",
        "bgImage_isInstructorEditable",
        "metadataAssess_isInstructorEditable",
        "metadataParts_isInstructorEditable",
        "metadataQuestions_isInstructorEditable",
    };
    for (int i = 0; i < editKeys.length; i++)
    {
      String canEdit =
          assessment.getAssessmentMetaDataByLabel(editKeys[i]);
      if (canEdit==null) continue;

      LOG.info("\nassessmentXml.setFieldentry('" +
               editKeys[i] + "', '" + canEdit + "')");

      assessmentXml.setFieldentry(editKeys[i], canEdit);
    }
  }

  /**
   * format Iso8601 Date
   * @param date Date object
   * @return Iso8601 date string or "" if not set
   */
  private String formatDate(Date date)
  {
    if (date == null)
    {
      return "";
    }
    Iso8601DateFormat iso = new Iso8601DateFormat();
    return iso.format(date);
  }


  /**
   * Map Boolean to text string
   * @param b Boolean
   * @return "True"|"False"
   */
  public String qtiBooleanString(Boolean b)
  {
    if (b !=null && b.booleanValue())
    {
      return "True";
    }
    return "False";
  }

  /**
   * Map Integer to text string
   * @param i Integer
   * @return "True"|"False"
   */
  public String qtiBooleanString(Integer i)
  {
    if (i !=null && i.intValue() != 0)
    {
      return "True";
    }
    return "False";
  }

  /**
   * Set the assessment description.
   * This is valid for all undelimited single item texts.
   * Not valid for matching or fill in the blank
   * @param description assessment description
   * @param assessmentXml the xml
   */
  public void setDescriptiveText(String description, Assessment assessmentXml)
  {
    String xpath = "questestinterop/assessment/presentation_material/flow_mat/material/mattext";

    List list = assessmentXml.selectNodes(xpath);
    LOG.info("description xpath:" + xpath);
    LOG.info("description xpath size:" + list.size());
    LOG.info("setting element for description to: " + description);
    try
    {
      assessmentXml.update(xpath, description);
    }
    catch (Exception ex)
    {
      LOG.error(ex.getMessage(), ex);
    }
  }

  /**
   * Set the assessment duration.
   * @param duration assessment duration in seconds
   * @param assessmentXml the xml
   */
  public void setDuration(Integer duration, Assessment assessmentXml)
  {
    String xpath = "questestinterop/assessment/duration";

    List list = assessmentXml.selectNodes(xpath);
    try
    {
      Iso8601TimeInterval isoTime =
          new Iso8601TimeInterval(1000 * duration.longValue());
      assessmentXml.update(xpath, isoTime.toString());
    }
    catch (Exception ex)
    {
      LOG.error(ex.getMessage(), ex);
    }
  }



  /**
   * look u an assessment section by title
   *
   * @param assessment
   * @param sectionTitle
   *
   * @return Section XML
   */
  public Section getSectionByTitle(
    Assessment assessment, String sectionTitle)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug(
        "getSectionByTitle( )");
    }

    Section sectionXml = null;

    Collection secs = assessment.getSections();
    Iterator iter = secs.iterator();
    if ( (secs != null) && (secs.size() > 0) && (sectionTitle != null))
    {
      for (int i = 0; i < secs.size(); i++)
      {
        while (iter.hasNext())
        {
          sectionXml = (Section) iter.next();
          String title =
            sectionXml.selectSingleValue("section/@title", "attribute");
          if ( (title != null) && title.equals(sectionTitle))
          {
            break;
          }
        }
      }
    }

    return sectionXml;
  }


}
