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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import org.sakaiproject.tool.assessment.business.entity.asi.Assessment;
import org.sakaiproject.tool.assessment.business.entity.assessment.model.SubmissionModel;

/**
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Organization: Sakai Project</p>
 * @author casong
 * @author esmiley
 * @version $Id: PropertyToMetadataConvertor.java,v 1.3 2005/01/05 01:11:41 esmiley.stanford.edu Exp $
 */
public class PropertyToMetadataConvertor
{
  private static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(PropertyToMetadataTranslator.class);

  /**
   * Returns a org.sakaiproject.tool.assessment.business.entity.asi.Assessment
   * asi-mapping model object that wraps the XML.
   *
   * @return Assessment wrapper
   */
   private Assessment readAssessmentTemplate()
  {
    LOG.debug("readAssessmentTemplate()");
    Assessment assessmentXMLWrapper = null;
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder;
    try
    {
      String filePath = "/xml/author/";
      File file = new File(filePath + "assessmentTemplate.xml");
      InputStream inputStream = new FileInputStream(file);
      builder = dbf.newDocumentBuilder();
      org.xml.sax.InputSource is = new org.xml.sax.InputSource(inputStream);
      Document document;
      document = builder.parse(is);
      assessmentXMLWrapper = new Assessment(document);
    }
    catch(FileNotFoundException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(ParserConfigurationException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(SAXException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(IOException e)
    {
      LOG.error(e.getMessage(), e);
    }

    return assessmentXMLWrapper;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param templateProperties DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Assessment translateTemplateToMetadata(
    AssessmentTemplateProperties templateProperties, Assessment assessmentXMLWrapper )
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "translateTemplateToMetadata(AssessmentTemplatePropertiesImpl " +
        templateProperties + ")");
    }
    //Set editable metadata.
    setEditableMetadata(assessmentXMLWrapper, templateProperties);
    setMetadataValues(assessmentXMLWrapper, templateProperties);

    return assessmentXMLWrapper;
  }

  /**
   * @param assessmentXMLWrapper wrapper object
   * @param templateProperties metadata editable
   */
  private void setEditableMetadata(
    Assessment assessmentXMLWrapper, AssessmentTemplateProperties templateProperties)
  {
    List keyList = TemplateToMetadataMap.getInstance().getKeyList();
    int size = keyList.size();
    for(int i = 0; i < size; i++)
    {
      String fieldName = (String) keyList.get(i);
      String fieldLable =
        TemplateToMetadataMap.getInstance().getMetadataLabel(fieldName);

      boolean setValueBoolean =
        (boolean) templateProperties.isInstructorEditable(fieldName);
      if(setValueBoolean)
      {
        assessmentXMLWrapper.setFieldentry(fieldLable, "True");
      }
      else{
        assessmentXMLWrapper.setFieldentry(fieldLable, "False");
      }
    }
  }

  /**
   * @param assessmentXMLWrapper wrapper
   * @param templateProperties metadata
   */
  private void setMetadataValues(
    Assessment assessmentXMLWrapper, AssessmentTemplateProperties templateProperties)
  {
    String itemAssess = templateProperties.getItemAccessType();
    if(itemAssess.equals("1"))
    {
      assessmentXMLWrapper.setFieldentry("NAVIGATION", "LINEAR");
    }
    else
    {
      assessmentXMLWrapper.setFieldentry("NAVIGATION", "RANDOM");
    }

    String displayChunking = templateProperties.getDisplayChunking();
    if(displayChunking.equals("3"))
    {
      assessmentXMLWrapper.setFieldentry("QUESTION_LAYOUT", "A");
    }

    if(displayChunking.equals("1"))
    {
      assessmentXMLWrapper.setFieldentry("QUESTION_LAYOUT", "S");
    }

    String questionNumbering = templateProperties.getQuestionNumbering();
    if(questionNumbering.equals("1"))
    {
      assessmentXMLWrapper.setFieldentry("QUESTION_NUMBERING", "RESTART");
    }

    String lateHandling = templateProperties.getLateHandling();
    if(lateHandling.equals("0"))
    {
      assessmentXMLWrapper.setFieldentry("LATE_HANDLING", "True");
    }

    SubmissionModel submissionModel =
      (SubmissionModel) templateProperties.getSubmissionModel();
    if(submissionModel.getNumberSubmissions().equals("0"))
    {
      assessmentXMLWrapper.setFieldentry("MAX_ATTEMPTS", "0");
    }

    if(submissionModel.getNumberSubmissions().equals("2"))
    {
      assessmentXMLWrapper.setFieldentry(
        "MAX_ATTEMPTS", String.valueOf(submissionModel.getSubmissionsAllowed()));
    }

    String autoSave = templateProperties.getAutoSave();
    if(autoSave.equals("1"))
    {
      assessmentXMLWrapper.setFieldentry("AUTO_SAVE", "True");
    }

    String feedbackDelivery = templateProperties.getFeedbackType();
    if(feedbackDelivery.equals("0"))
    {
      assessmentXMLWrapper.setFieldentry("FEEDBACK_DELIVERY", "IMMEDIATE");
    }
    else if(feedbackDelivery.equals("1"))
    {
      assessmentXMLWrapper.setFieldentry("FEEDBACK_DELIVERY", "DATED");
    }
    else
    {
      assessmentXMLWrapper.setFieldentry("FEEDBACK_DELIVERY", "NONE");
    }

    boolean anonymouse = templateProperties.getAnonymousGrading();
    if(anonymouse)
    {
      assessmentXMLWrapper.setFieldentry("ANONYMOUS_GRADING", "True");
    }

    String gradeScore = templateProperties.getRecordedScore();
    if(gradeScore.equals("1"))
    {
      assessmentXMLWrapper.setFieldentry("GRADE_SCORE", "AVERAGE");
    }

    String gradeBookOptions = templateProperties.getToGradebook();
    if(gradeBookOptions.equals("1"))
    {
      assessmentXMLWrapper.setFieldentry("GRADEBOOK_OPTIONS", "SELECTED");
    }

    setMetadataFeedbackComponent(assessmentXMLWrapper, templateProperties);
  }

  /**
   * @param assessmentXMLWrapper wrapper
   * @param templateProperties feedback
   */
  private void setMetadataFeedbackComponent(
    Assessment assessmentXMLWrapper, AssessmentTemplateProperties templateProperties)
  {
    String feedbackComponent = templateProperties.getFeedbackComponents();
    if(feedbackComponent.charAt(0) == 'F')
    {
      assessmentXMLWrapper.setFieldentry("FEEDBACK_SHOW_QUESTION", "False");
    }
    else
    {
      assessmentXMLWrapper.setFieldentry("FEEDBACK_SHOW_QUESTION", "True");
    }

    if(feedbackComponent.charAt(2) == 'F')
    {
      assessmentXMLWrapper.setFieldentry("FEEDBACK_SHOW_RESPONSE", "False");
    }
    else
    {
      assessmentXMLWrapper.setFieldentry("FEEDBACK_SHOW_RESPONSE", "True");
    }

    if(feedbackComponent.charAt(4) == 'T')
    {
      assessmentXMLWrapper.setFieldentry("FEEDBACK_SHOW_CORRECT_RESPONSE", "True");
    }
    else
    {
      assessmentXMLWrapper.setFieldentry("FEEDBACK_SHOW_CORRECT_RESPONSE", "False");
    }

    if(feedbackComponent.charAt(6) == 'T')
    {
      assessmentXMLWrapper.setFieldentry("FEEDBACK_SHOW_STUDENT_SCORE", "True");
    }
    else
    {
      assessmentXMLWrapper.setFieldentry("FEEDBACK_SHOW_STUDENT_SCORE", "False");
    }

    if(feedbackComponent.charAt(8) == 'T')
    {
      assessmentXMLWrapper.setFieldentry("FEEDBACK_SHOW_ITEM_LEVEL", "True");
    }
    else
    {
      assessmentXMLWrapper.setFieldentry("FEEDBACK_SHOW_ITEM_LEVEL", "False");
    }

    if(feedbackComponent.charAt(10) == 'T')
    {
      assessmentXMLWrapper.setFieldentry("FEEDBACK_SHOW_SELECTION_LEVEL", "True");
    }
    else
    {
      assessmentXMLWrapper.setFieldentry("FEEDBACK_SHOW_SELECTION_LEVEL", "False");
    }

    if(feedbackComponent.charAt(12) == 'T')
    {
      assessmentXMLWrapper.setFieldentry("FEEDBACK_SHOW_GRADER_COMMENT", "True");
    }
    else
    {
      assessmentXMLWrapper.setFieldentry("FEEDBACK_SHOW_GRADER_COMMENT", "False");
    }

    if(feedbackComponent.charAt(14) == 'T')
    {
      assessmentXMLWrapper.setFieldentry("FEEDBACK_SHOW_STATS", "True");
    }
    else
    {
      assessmentXMLWrapper.setFieldentry("FEEDBACK_SHOW_STATS", "False");
    }
  }
}
