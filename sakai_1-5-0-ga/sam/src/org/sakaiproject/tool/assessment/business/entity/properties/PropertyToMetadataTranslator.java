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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import org.sakaiproject.tool.assessment.business.entity.asi.Assessment;
import org.sakaiproject.tool.assessment.business.entity.assessment.model.AccessGroup;
import org.sakaiproject.tool.assessment.business.entity.assessment.model.FeedbackModel;
import org.sakaiproject.tool.assessment.business.entity.assessment.model.ScoringModel;
import org.sakaiproject.tool.assessment.business.entity.assessment.model.SubmissionModel;

/**
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Organization: Sakai Project</p>
 * @author casong
 * @author Ed Smiley esmiley@stanford.edu
 * @version $Id: PropertyToMetadataTranslator.java,v 1.3 2005/01/05 01:11:41 esmiley.stanford.edu Exp $
 */
public class PropertyToMetadataTranslator
{
  private static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(PropertyToMetadataTranslator.class);

  /**
   * return assessment XML Wrapper cLASS
   *
   * @return assessment XML Wrapper
   */
  private Assessment readAssessmentTemplate()
  {
    LOG.debug("readAssessmentTemplate()");
    Assessment assessmentXMLWrapper = null;
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

    Assessment assessmentXMLWrapper = this.readAssessmentTemplate();

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
              assessmentXMLWrapper, templateProperties, "datedFeedbackType", newValue);
            newValue = feedbackModel.getFeedbackType();
            this.setValue(
              assessmentXMLWrapper, templateProperties, "feedbackType", newValue);
            newValue = feedbackModel.getImmediateFeedbackType();
            this.setValue(
              assessmentXMLWrapper, templateProperties, "immediateFeedbackType", newValue);
            newValue = feedbackModel.getPerQuestionFeedbackType();
            this.setValue(
              assessmentXMLWrapper, templateProperties, "perQuestionFeedbackType",
              newValue);
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH:mm:ss");
            newValue = sdf.format(feedbackModel.getFeedbackDate());
            this.setValue(
              assessmentXMLWrapper, templateProperties, "feedbackDate", newValue);
            newValue = sdf.format(feedbackModel.getScoreDate());
            this.setValue(
              assessmentXMLWrapper, templateProperties, "scoreDate", newValue);
          }

          if(returnValue instanceof SubmissionModel)
          {
            SubmissionModel submissionModel = ((SubmissionModel) returnValue);
            String newValue = submissionModel.getNumberSubmissions();
            this.setValue(
              assessmentXMLWrapper, templateProperties, "numberSubmissions", newValue);
            newValue = String.valueOf(submissionModel.getSubmissionsAllowed());
            this.setValue(
              assessmentXMLWrapper, templateProperties, "sumissionsAllowed", newValue);
          }

          if(returnValue instanceof ScoringModel)
          {
            ScoringModel scoringModel = ((ScoringModel) returnValue);
            String newValue = scoringModel.getNumericModel();
            this.setValue(
              assessmentXMLWrapper, templateProperties, "numericModel", newValue);
            newValue = scoringModel.getScoringType();
            this.setValue(
              assessmentXMLWrapper, templateProperties, "scoringType", newValue);
            newValue = String.valueOf(scoringModel.getDefaultQuestionValue());
            this.setValue(
              assessmentXMLWrapper, templateProperties, "defaultQuestionValue", newValue);
            newValue = String.valueOf(scoringModel.getFixedTotalScore());
            this.setValue(
              assessmentXMLWrapper, templateProperties, "fixedTotalScore", newValue);
          }
          else
          {
            String fieldName = methodName.substring(3);
            fieldName =
              fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
            this.setValue(
              assessmentXMLWrapper, templateProperties, fieldName, returnValue);
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
    AssessmentTemplatePropertiesImpl templateProperties)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "translateTemplateToMetadata(AssessmentTemplatePropertiesImpl " +
        templateProperties + ")");
    }

    Assessment assessmentXMLWrapper = this.readAssessmentTemplate();

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
              assessmentXMLWrapper, templateProperties, "datedFeedbackType", newValue);
            newValue = feedbackModel.getFeedbackType();
            this.setValue(
              assessmentXMLWrapper, templateProperties, "feedbackType", newValue);
            newValue = feedbackModel.getImmediateFeedbackType();
            this.setValue(
              assessmentXMLWrapper, templateProperties, "immediateFeedbackType", newValue);
            newValue = feedbackModel.getPerQuestionFeedbackType();
            this.setValue(
              assessmentXMLWrapper, templateProperties, "perQuestionFeedbackType",
              newValue);
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH:mm:ss");
            newValue = sdf.format(feedbackModel.getFeedbackDate());
            this.setValue(
              assessmentXMLWrapper, templateProperties, "feedbackDate", newValue);
            newValue = sdf.format(feedbackModel.getScoreDate());
            this.setValue(
              assessmentXMLWrapper, templateProperties, "scoreDate", newValue);
          }

          if(returnValue instanceof SubmissionModel)
          {
            SubmissionModel submissionModel = ((SubmissionModel) returnValue);
            String newValue = submissionModel.getNumberSubmissions();
            this.setValue(
              assessmentXMLWrapper, templateProperties, "numberSubmissions", newValue);
            newValue = String.valueOf(submissionModel.getSubmissionsAllowed());
            this.setValue(
              assessmentXMLWrapper, templateProperties, "sumissionsAllowed", newValue);
          }

          if(returnValue instanceof ScoringModel)
          {
            ScoringModel scoringModel = ((ScoringModel) returnValue);
            String newValue = scoringModel.getNumericModel();
            this.setValue(
              assessmentXMLWrapper, templateProperties, "numericModel", newValue);
            newValue = scoringModel.getScoringType();
            this.setValue(
              assessmentXMLWrapper, templateProperties, "scoringType", newValue);
            newValue = String.valueOf(scoringModel.getDefaultQuestionValue());
            this.setValue(
              assessmentXMLWrapper, templateProperties, "defaultQuestionValue", newValue);
            newValue = String.valueOf(scoringModel.getFixedTotalScore());
            this.setValue(
              assessmentXMLWrapper, templateProperties, "fixedTotalScore", newValue);
          }
          else
          {
            String fieldName = methodName.substring(3);
            fieldName =
              fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
            this.setValue(
              assessmentXMLWrapper, templateProperties, fieldName, returnValue);
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

    return assessmentXMLWrapper;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentXMLWrapper DOCUMENTATION PENDING
   * @param templateProperties DOCUMENTATION PENDING
   * @param fieldName DOCUMENTATION PENDING
   * @param returnValue DOCUMENTATION PENDING
   */
  private void setValue(
    Assessment assessmentXMLWrapper, AssessmentTemplatePropertiesImpl templateProperties,
    String fieldName, Object returnValue)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "setValue(Assessment " + assessmentXMLWrapper + ", AssessmentTemplateProperties" +
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
        assessmentXMLWrapper.createInstructorFieldEntry(fieldLabel);
        assessmentXMLWrapper.setInstructorFieldEntry(fieldLabel, value);
      }

      if(templateProperties.isStudentViewable(fieldName))
      {
        assessmentXMLWrapper.createStudentFieldEntry(fieldLabel);
        assessmentXMLWrapper.setStudentFieldEntry(fieldLabel, value);
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
