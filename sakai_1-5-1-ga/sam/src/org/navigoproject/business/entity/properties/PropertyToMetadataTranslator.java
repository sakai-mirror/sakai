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

import org.navigoproject.business.entity.Assessment;
import org.navigoproject.business.entity.assessment.model.AccessGroup;
import org.navigoproject.business.entity.assessment.model.AssessmentPropertiesImpl;
import org.navigoproject.business.entity.assessment.model.AssessmentTemplatePropertiesImpl;
import org.navigoproject.business.entity.assessment.model.FeedbackModel;
import org.navigoproject.business.entity.assessment.model.ScoringModel;
import org.navigoproject.business.entity.assessment.model.SubmissionModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.struts.action.Action;

import org.w3c.dom.Document;

import org.xml.sax.SAXException;

/**
 * DOCUMENT ME!
 *
 * @author casong To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 *         Comments
 */
public class PropertyToMetadataTranslator
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
      String filePath = "c:/java/Projects/Navigo2/webapp/xml/author/";
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
   * @param properties DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Assessment translateTemplateToMetadata(
    AssessmentTemplatePropertiesImpl templateProperties,
    AssessmentPropertiesImpl properties)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "translateTemplateToMetadata(AssessmentTemplatePropertiesImpl " +
        templateProperties + ", AssessmentPropertiesImpl " + properties + ")");
    }

    Assessment assessment = this.readAssessmentTemplate();

    Method[] methods = properties.getClass().getMethods();
    int size = methods.length;
    try
    {
      for(int i = 0; i < size; i++)
      {
        Method method = methods[i];
        String methodName = method.getName();
        if(methodName.startsWith("get"))
        {
          Object returnValue = method.invoke(properties, null);
          if(returnValue instanceof AccessGroup)
          {
            //TODO implement this later after clarify with Stanford folks. 
          }

          if(returnValue instanceof FeedbackModel)
          {
            FeedbackModel feedbackModel = ((FeedbackModel) returnValue);
            String newValue = feedbackModel.getDatedFeedbackType();
            this.setValue(
              assessment, templateProperties, "datedFeedbackType", newValue);
            newValue = feedbackModel.getFeedbackType();
            this.setValue(
              assessment, templateProperties, "feedbackType", newValue);
            newValue = feedbackModel.getImmediateFeedbackType();
            this.setValue(
              assessment, templateProperties, "immediateFeedbackType", newValue);
            newValue = feedbackModel.getPerQuestionFeedbackType();
            this.setValue(
              assessment, templateProperties, "perQuestionFeedbackType",
              newValue);
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH:mm:ss");
            newValue = sdf.format(feedbackModel.getFeedbackDate());
            this.setValue(
              assessment, templateProperties, "feedbackDate", newValue);
            newValue = sdf.format(feedbackModel.getScoreDate());
            this.setValue(
              assessment, templateProperties, "scoreDate", newValue);
          }

          if(returnValue instanceof SubmissionModel)
          {
            SubmissionModel submissionModel = ((SubmissionModel) returnValue);
            String newValue = submissionModel.getNumberSubmissions();
            this.setValue(
              assessment, templateProperties, "numberSubmissions", newValue);
            newValue = String.valueOf(submissionModel.getSubmissionsAllowed());
            this.setValue(
              assessment, templateProperties, "sumissionsAllowed", newValue);
          }

          if(returnValue instanceof ScoringModel)
          {
            ScoringModel scoringModel = ((ScoringModel) returnValue);
            String newValue = scoringModel.getNumericModel();
            this.setValue(
              assessment, templateProperties, "numericModel", newValue);
            newValue = scoringModel.getScoringType();
            this.setValue(
              assessment, templateProperties, "scoringType", newValue);
            newValue = String.valueOf(scoringModel.getDefaultQuestionValue());
            this.setValue(
              assessment, templateProperties, "defaultQuestionValue", newValue);
            newValue = String.valueOf(scoringModel.getFixedTotalScore());
            this.setValue(
              assessment, templateProperties, "fixedTotalScore", newValue);
          }
          else
          {
            String fieldName = methodName.substring(3);
            fieldName =
              fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
            this.setValue(
              assessment, templateProperties, fieldName, returnValue);
          }
        }
      }
    }
    catch(IllegalArgumentException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(IllegalAccessException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(InvocationTargetException e)
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
    AssessmentTemplatePropertiesImpl templateProperties)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "translateTemplateToMetadata(AssessmentTemplatePropertiesImpl " +
        templateProperties + ")");
    }

    Assessment assessment = this.readAssessmentTemplate();

    Method[] methods = templateProperties.getClass().getMethods();
    int size = methods.length;
    try
    {
      for(int i = 0; i < size; i++)
      {
        Method method = methods[i];
        String methodName = method.getName();
        if(methodName.startsWith("get"))
        {
          Object returnValue = method.invoke(templateProperties, null);
          if(returnValue instanceof AccessGroup)
          {
            //TODO implement this later after clarify with Stanford folks. 
          }

          if(returnValue instanceof FeedbackModel)
          {
            FeedbackModel feedbackModel = ((FeedbackModel) returnValue);
            String newValue = feedbackModel.getDatedFeedbackType();
            this.setValue(
              assessment, templateProperties, "datedFeedbackType", newValue);
            newValue = feedbackModel.getFeedbackType();
            this.setValue(
              assessment, templateProperties, "feedbackType", newValue);
            newValue = feedbackModel.getImmediateFeedbackType();
            this.setValue(
              assessment, templateProperties, "immediateFeedbackType", newValue);
            newValue = feedbackModel.getPerQuestionFeedbackType();
            this.setValue(
              assessment, templateProperties, "perQuestionFeedbackType",
              newValue);
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH:mm:ss");
            newValue = sdf.format(feedbackModel.getFeedbackDate());
            this.setValue(
              assessment, templateProperties, "feedbackDate", newValue);
            newValue = sdf.format(feedbackModel.getScoreDate());
            this.setValue(
              assessment, templateProperties, "scoreDate", newValue);
          }

          if(returnValue instanceof SubmissionModel)
          {
            SubmissionModel submissionModel = ((SubmissionModel) returnValue);
            String newValue = submissionModel.getNumberSubmissions();
            this.setValue(
              assessment, templateProperties, "numberSubmissions", newValue);
            newValue = String.valueOf(submissionModel.getSubmissionsAllowed());
            this.setValue(
              assessment, templateProperties, "sumissionsAllowed", newValue);
          }

          if(returnValue instanceof ScoringModel)
          {
            ScoringModel scoringModel = ((ScoringModel) returnValue);
            String newValue = scoringModel.getNumericModel();
            this.setValue(
              assessment, templateProperties, "numericModel", newValue);
            newValue = scoringModel.getScoringType();
            this.setValue(
              assessment, templateProperties, "scoringType", newValue);
            newValue = String.valueOf(scoringModel.getDefaultQuestionValue());
            this.setValue(
              assessment, templateProperties, "defaultQuestionValue", newValue);
            newValue = String.valueOf(scoringModel.getFixedTotalScore());
            this.setValue(
              assessment, templateProperties, "fixedTotalScore", newValue);
          }
          else
          {
            String fieldName = methodName.substring(3);
            fieldName =
              fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
            this.setValue(
              assessment, templateProperties, fieldName, returnValue);
          }
        }
      }
    }
    catch(IllegalArgumentException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(IllegalAccessException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(InvocationTargetException e)
    {
      LOG.error(e.getMessage(), e);
    }

    return assessment;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessment DOCUMENTATION PENDING
   * @param templateProperties DOCUMENTATION PENDING
   * @param fieldName DOCUMENTATION PENDING
   * @param returnValue DOCUMENTATION PENDING
   */
  private void setValue(
    Assessment assessment, AssessmentTemplatePropertiesImpl templateProperties,
    String fieldName, Object returnValue)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "setValue(Assessment " + assessment + ", AssessmentTemplateProperties" +
        templateProperties + ", String" + fieldName + ", String" + returnValue +
        ")");
    }

    String fieldLabel =
      PropertyToMetadataMap.getInstance().getMetadataLabel(fieldName);
    if(fieldLabel != null)
    {
      String value = processValue(returnValue);
      if(templateProperties.isInstructorViewable(fieldName))
      {
        assessment.createInstructorFieldEntry(fieldLabel);
        assessment.setInstructorFieldEntry(fieldLabel, value);
      }

      if(templateProperties.isStudentViewable(fieldName))
      {
        assessment.createStudentFieldEntry(fieldLabel);
        assessment.setStudentFieldEntry(fieldLabel, value);
      }
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param object DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private String processValue(Object object)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("processValue(Object " + object + ")");
    }

    String value = "";
    if(object instanceof Integer)
    {
      value = object.toString();
    }

    if(object instanceof Boolean)
    {
      value = object.toString();
    }

    if(object instanceof String)
    {
      value = (String) object;
    }

    return value;
  }
}
