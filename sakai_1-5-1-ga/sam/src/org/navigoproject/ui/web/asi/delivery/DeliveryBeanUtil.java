/*
 * Created on Mar 8, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.delivery;

import java.util.List;

import org.apache.xerces.dom.DocumentImpl;
import org.navigoproject.QTIConstantStrings;
import org.navigoproject.business.entity.Assessment;
import org.navigoproject.business.entity.XmlStringBuffer;
import org.navigoproject.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author casong
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DeliveryBeanUtil
{

  private static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(DeliveryBeanUtil.class);
  
  private Assessment assessment = null;
  public DeliveryBeanUtil(Document assessmentTaken)
  {
    Document assessmentDoc = getAssessmentDoc(assessmentTaken);
    assessment = new Assessment(assessmentDoc);
  }
  
  private Document getAssessmentDoc(Document assessmentTaken)
  {
    Document document = null;
    try
    {
//      XmlStringBuffer xsb = new XmlStringBuffer(assessmentTaken);
//      List assessments = xsb.selectNodes(XmlDeliveryService.ASSESSMENT_PATH);
      NodeList assessments = assessmentTaken.getElementsByTagName(QTIConstantStrings.QUESTESTINTEROP);
      if(assessments == null)
      {
        LOG.debug("assessments is null");
      }
      else
      {
        if(assessments.getLength()>0)
        {
          Element assessment = (Element) assessments.item(0);
          document = XmlUtil.createDocument();
          Element newNode = (Element)document.importNode(assessment, true);
          document.appendChild(newNode);
        }
      }
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
    }

    return document;
  }
  
  /*
   *
   */
  public String getAssessmentTitle(Document document)
  {
    String title = "";
    try
    {
      XmlStringBuffer xsb = new XmlStringBuffer(document);
      List assessments = xsb.selectNodes(XmlDeliveryService.QUESTTESTINTEROP_ASSESSMENT);
      if(assessments == null)
      {
        LOG.debug("assessments is null");
      }
      else
      {
        if(assessments.iterator().hasNext())
        {
          Element assessment = (Element) assessments.iterator().next();
          title = assessment.getAttribute(QTIConstantStrings.TITLE);
        }
      }
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
    }

    return title;
  }
  
  /**
   * DOCUMENTATION PENDING
   *
   * @param document DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getAssessmentId(Document document)
  {
    String id = "";
    XmlStringBuffer xsb = new XmlStringBuffer(document);
    List assessments;
    try
    {
      assessments = xsb.selectNodes(XmlDeliveryService.QUESTTESTINTEROP_ASSESSMENT);
      if(assessments == null)
      {
        LOG.debug("assessments is null");
      }
      else
      {
        if(assessments.iterator().hasNext())
        {
          Element assessment = (Element) assessments.iterator().next();
          id = assessment.getAttribute(QTIConstantStrings.IDENT);
        }
      }
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
    }

    return id;
  }
  
  public String getFieldentry(String fieldlabel)
  {
    return assessment.getFieldentry(fieldlabel);
  }
  
  public FeedbackComponent getFeedbackComponent(){
    /**
     * FEEDBACK_SHOW_CORRECT_SCORE
       FEEDBACK_SHOW_STUDENT_SCORE
       FEEDBACK_SHOW_ITEM_LEVEL
       FEEDBACK_SHOW_SECTION_LEVEL
       FEEDBACK_SHOW_GRADER_COMMENT
       FEEDBACK_SHOW_STATS
       FEEDBACK_SHOW_QUESTION
       FEEDBACK_SHOW_RESPONSE
     **/
    FeedbackComponent feedbackComponent = new FeedbackComponent();
    feedbackComponent.showCorrectResponse = this.getFieldentry("FEEDBACK_SHOW_CORRECT_RESPONSE");
    feedbackComponent.showStudentScore = this.getFieldentry("FEEDBACK_SHOW_STUDENT_SCORE");
    feedbackComponent.showItemLevel = this.getFieldentry("FEEDBACK_SHOW_ITEM_LEVEL");
    feedbackComponent.showSelectionLevel = this.getFieldentry("FEEDBACK_SHOW_SELECTION_LEVEL");
    feedbackComponent.showGraderComment = this.getFieldentry("FEEDBACK_SHOW_GRADER_COMMENT");
    feedbackComponent.showStats = this.getFieldentry("FEEDBACK_SHOW_STATS");
    feedbackComponent.showQuestion = this.getFieldentry("FEEDBACK_SHOW_QUESTION");
    feedbackComponent.showResponse = this.getFieldentry("FEEDBACK_SHOW_RESPONSE");
    return feedbackComponent;
  }

}
