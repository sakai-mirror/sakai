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
 * Created on Apr 20, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.business.entity.properties;

import org.navigoproject.business.entity.Assessment;
import org.navigoproject.business.entity.assessment.model.AssessmentTemplatePropertiesImpl;
import org.navigoproject.business.entity.assessment.model.SubmissionModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.struts.action.Action;

import org.w3c.dom.Document;

import org.xml.sax.SAXException;

/**
 * @author casong
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PropertyToMetadataConvertor
  extends Action
{
  private static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(PropertyToMetadataTranslator.class);

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private Assessment readAssessmentTemplate()
  {
    LOG.debug("readAssessmentTemplate()");
    Assessment assessment = null;
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
      assessment = new org.navigoproject.business.entity.Assessment(document);
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

    return assessment;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param templateProperties DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Assessment translateTemplateToMetadata(
    AssessmentTemplateProperties templateProperties, Assessment assessment )
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "translateTemplateToMetadata(AssessmentTemplatePropertiesImpl " +
        templateProperties + ")");
    }
    //Set editable metadata.
    setEditableMetadata(assessment, templateProperties);
    setMetadataValues(assessment, templateProperties);

    return assessment;
  }

  /**
   * @param assessment
   * @param templateProperties
   */
  private void setEditableMetadata(
    Assessment assessment, AssessmentTemplateProperties templateProperties)
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
        assessment.setFieldentry(fieldLable, "True");
      }
      else{
        assessment.setFieldentry(fieldLable, "False");
      }
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessment DOCUMENTATION PENDING
   * @param templateProperties DOCUMENTATION PENDING
   */
  private void setMetadataValues(
    Assessment assessment, AssessmentTemplateProperties templateProperties)
  {
    String itemAssess = templateProperties.getItemAccessType();
    if(itemAssess.equals("1"))
    {
      assessment.setFieldentry("NAVIGATION", "LINEAR");
    }
    else
    {
      assessment.setFieldentry("NAVIGATION", "RANDOM");
    }

    String displayChunking = templateProperties.getDisplayChunking();
    if(displayChunking.equals("3"))
    {
      assessment.setFieldentry("QUESTION_LAYOUT", "A");
    }

    if(displayChunking.equals("1"))
    {
      assessment.setFieldentry("QUESTION_LAYOUT", "S");
    }

    String questionNumbering = templateProperties.getQuestionNumbering();
    if(questionNumbering.equals("1"))
    {
      assessment.setFieldentry("QUESTION_NUMBERING", "RESTART");
    }

    String lateHandling = templateProperties.getLateHandling();
    if(lateHandling.equals("0"))
    {
      assessment.setFieldentry("LATE_HANDLING", "True");
    }

    SubmissionModel submissionModel =
      (SubmissionModel) templateProperties.getSubmissionModel();
    if(submissionModel.getNumberSubmissions().equals("0"))
    {
      assessment.setFieldentry("MAX_ATTEMPTS", "0");
    }

    if(submissionModel.getNumberSubmissions().equals("2"))
    {
      assessment.setFieldentry(
        "MAX_ATTEMPTS", String.valueOf(submissionModel.getSubmissionsAllowed()));
    }

    String autoSave = templateProperties.getAutoSave();
    if(autoSave.equals("1"))
    {
      assessment.setFieldentry("AUTO_SAVE", "True");
    }

    String feedbackDelivery = templateProperties.getFeedbackType();
    if(feedbackDelivery.equals("0"))
    {
      assessment.setFieldentry("FEEDBACK_DELIVERY", "IMMEDIATE");
    }  
    else if(feedbackDelivery.equals("1"))
    {
      assessment.setFieldentry("FEEDBACK_DELIVERY", "DATED");
    }
    else
    {
      assessment.setFieldentry("FEEDBACK_DELIVERY", "NONE");
    }

    boolean anonymouse = templateProperties.getAnonymousGrading();
    if(anonymouse)
    {
      assessment.setFieldentry("ANONYMOUS_GRADING", "True");
    }

    String gradeScore = templateProperties.getRecordedScore();
    if(gradeScore.equals("1"))
    {
      assessment.setFieldentry("GRADE_SCORE", "AVERAGE");
    }

    String gradeBookOptions = templateProperties.getToGradebook();
    if(gradeBookOptions.equals("1"))
    {
      assessment.setFieldentry("GRADEBOOK_OPTIONS", "SELECTED");
    }

    setMetadataFeedbackComponent(assessment, templateProperties);
  }

  /**
   * @param assessment
   * @param templateProperties
   */
  private void setMetadataFeedbackComponent(
    Assessment assessment, AssessmentTemplateProperties templateProperties)
  {
    String feedbackComponent = templateProperties.getFeedbackComponents();
    if(feedbackComponent.charAt(0) == 'F')
    {
      assessment.setFieldentry("FEEDBACK_SHOW_QUESTION", "False");
    }
    else
    {
      assessment.setFieldentry("FEEDBACK_SHOW_QUESTION", "True");
    }

    if(feedbackComponent.charAt(2) == 'F')
    {
      assessment.setFieldentry("FEEDBACK_SHOW_RESPONSE", "False");
    }
    else
    {
      assessment.setFieldentry("FEEDBACK_SHOW_RESPONSE", "True");
    }
    
    if(feedbackComponent.charAt(4) == 'T')
    {
      assessment.setFieldentry("FEEDBACK_SHOW_CORRECT_RESPONSE", "True");
    }
    else
    {
      assessment.setFieldentry("FEEDBACK_SHOW_CORRECT_RESPONSE", "False");
    }

    if(feedbackComponent.charAt(6) == 'T')
    {
      assessment.setFieldentry("FEEDBACK_SHOW_STUDENT_SCORE", "True");
    }
    else
    {
      assessment.setFieldentry("FEEDBACK_SHOW_STUDENT_SCORE", "False");
    }

    if(feedbackComponent.charAt(8) == 'T')
    {
      assessment.setFieldentry("FEEDBACK_SHOW_ITEM_LEVEL", "True");
    }
    else
    {
      assessment.setFieldentry("FEEDBACK_SHOW_ITEM_LEVEL", "False");
    }

    if(feedbackComponent.charAt(10) == 'T')
    {
      assessment.setFieldentry("FEEDBACK_SHOW_SELECTION_LEVEL", "True");
    }
    else
    {
      assessment.setFieldentry("FEEDBACK_SHOW_SELECTION_LEVEL", "False");
    }

    if(feedbackComponent.charAt(12) == 'T')
    {
      assessment.setFieldentry("FEEDBACK_SHOW_GRADER_COMMENT", "True");
    }
    else
    {
      assessment.setFieldentry("FEEDBACK_SHOW_GRADER_COMMENT", "False");
    }

    if(feedbackComponent.charAt(14) == 'T')
    {
      assessment.setFieldentry("FEEDBACK_SHOW_STATS", "True");
    }
    else
    {
      assessment.setFieldentry("FEEDBACK_SHOW_STATS", "False");
    }
  }
}
