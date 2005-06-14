/*
 * Created on Apr 30, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.business.entity;

import java.util.List;
import java.util.Iterator;
import java.io.IOException;

import org.sakaiproject.framework.Constants;
import org.sakaiproject.framework.ThreadLocalMapProvider;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.DOMException;

import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.navigoproject.util.XmlUtil;

import org.navigoproject.ui.web.asi.delivery.XmlDeliveryService;
import org.navigoproject.data.RepositoryManager;
import org.navigoproject.data.AssessmentResult;
import org.navigoproject.data.SectionResult;
import org.navigoproject.data.ItemResult;
import org.navigoproject.data.RealizationBean;
import org.navigoproject.data.PublishedAssessmentBean;
import org.navigoproject.osid.impl.PersistenceService;
import org.navigoproject.osid.OsidManagerFactory;
import org.navigoproject.service.evaluation.EvaluationServiceDelegate;

import osid.dr.DigitalRepositoryException;
import osid.OsidException;
import osid.shared.Agent;
import osid.shared.SharedManager;
import osid.shared.PropertiesIterator;
import osid.shared.Properties;

/**
 * @author palcasi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AssessmentReport
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(AssessmentReport.class);
    
  private String assessmentId;
  private Document assessment;
  private Document report;
  private ResultProcessor processor;
  private ServletContext context;
  private RepositoryManager rm;
  private EvaluationServiceDelegate evalDelegate;
  
  private static final String INIT_ASSESSMENT_REPORT = "/xml/xsl/report/initAssessmentReport.xsl";
  private static final String GET_ASSESSMENT_RESULT_XSL = "/xml/xsl/report/getAssessmentResult.xsl";
  private static final String SET_ASSESSMENT_RESULT_XSL = "/xml/xsl/report/setAssessmentResult.xsl";
  private static final String SET_ASSESSMENT_COMMENTS_XSL = "/xml/xsl/report/setAssessmentComments.xsl";
  private static final String GET_SECTION_RESULT_XSL = "/xml/xsl/report/getSectionResult.xsl";
  private static final String SET_SECTION_RESULT_XSL = "/xml/xsl/report/setSectionResult.xsl";
  private static final String SET_SECTION_COMMENTS_XSL = "/xml/xsl/report/setSectionComments.xsl";
  private static final String GET_ITEM_RESULT_XSL = "/xml/xsl/report/getItemResult.xsl";
  private static final String SET_ITEM_RESULT_XSL = "/xml/xsl/report/setItemResult.xsl";
  private static final String SET_ITEM_COMMENTS_XSL = "/xml/xsl/report/setItemComments.xsl";
  private static final String SET_ITEM_SCORE_XSL = "/xml/xsl/report/setItemScore.xsl";
  private static final String GRADE_ITEM_XSL = "/xml/xsl/report/gradeItem.xsl";
  private static final String CLEAR_RESPONSE_XSL = "/xml/xsl/report/clearResponse.xsl";
  private static final String APPEND_RESPONSE_XSL = "/xml/xsl/report/appendResponse.xsl";
  private static final String SET_NAME_XSL = "/xml/xsl/report/setName.xsl";
  private static final String SET_USER_ID_XSL = "/xml/xsl/report/setUserId.xsl";
  private static final String SET_PUBLISHED_ID = "/xml/xsl/report/setPublishedId.xsl";
  private static final String SET_CORE_ID = "/xml/xsl/report/setCoreId.xsl";
  private static final String SET_ATTEMPTS_XSL = "/xml/xsl/report/setAttempts.xsl";
  private static final String UPDATE_ASSESSMENT_SCORE_XSL = "/xml/xsl/report/updateAssessmentScore.xsl";
  private static final String UPDATE_SECTION_SCORES_XSL = "/xml/xsl/report/updateSectionScores.xsl";
  
  private AssessmentReport(Document takenAssessment)
  {
    HttpServletRequest request = (HttpServletRequest) ThreadLocalMapProvider.getMap().get(Constants.HTTP_SERVLET_REQUEST);
    this.context = request.getSession().getServletContext();
    
    rm = new RepositoryManager();
    
    Document xsl = XmlUtil.readDocument(this.context, INIT_ASSESSMENT_REPORT);
    Transformer transformer = XmlUtil.createTransformer(xsl);
    this.report = XmlUtil.transformDocument(takenAssessment, transformer);
    this.assessment = takenAssessment;
  }
  
  public static AssessmentReport getAssessmentReport(String takenAssessmentId)
  {
    XmlDeliveryService ds = new XmlDeliveryService();
    Document takenAssessment = ds.getAssessmentQTIInterop(takenAssessmentId);
    AssessmentReport assessmentReport = new AssessmentReport(takenAssessment);
    assessmentReport.setAssessmentId(takenAssessmentId);
      
    try
    {      
      RepositoryManager rm = new RepositoryManager();
      RealizationBean realizationBean = PersistenceService.getInstance().getQtiQueries().returnRealizationBean(takenAssessmentId);
      String publishedId = realizationBean.getAssessmentPublishedId();
      assessmentReport.setPublishedId(publishedId);
      
      PublishedAssessmentBean pab = PersistenceService.getInstance().getPublishedAssessmentQueries().getByPublishedId(publishedId);
      String coreId = pab.getCoreId();
      assessmentReport.setCoreId(coreId);
      
      String agentId = realizationBean.getAgentId();
      SharedManager sm = OsidManagerFactory.createSharedManager();
      Agent agent = sm.getAgent(sm.getId(agentId));
      String name = agent.getDisplayName();
      assessmentReport.setName(name);
      
      String userId = "";
      PropertiesIterator iter = agent.getProperties();
      if (iter.hasNext())
      {
        Properties properties = (Properties) iter.next();
          userId = (String) properties.getProperty("Uid");
      }
      assessmentReport.setUserId(userId);
      
      AssessmentResult assessmentResult = rm.getAssessmentResult(rm.getId(takenAssessmentId));
      if (assessmentResult != null)
      {
        String assessmentId = assessmentResult.getAssessmentId().getIdString();
        Element assessmentResultElement = assessmentResult.getElement();
        assessmentReport.setAssessmentResult(assessmentResultElement);
      }
          
      List sectionResults = rm.getSectionResults(rm.getId(takenAssessmentId));
      if (sectionResults != null)
      {
        Iterator iterator = sectionResults.iterator();
        while (iterator.hasNext())
        {
          SectionResult sectionResult = (SectionResult) iterator.next();
          String sectionId = sectionResult.getSectionId().getIdString();
          Element sectionResultElement = sectionResult.getElement();
          assessmentReport.setSectionResult(sectionId, sectionResultElement);
        }
      }
          
      List itemResults = rm.getItemResults(rm.getId(takenAssessmentId));
      if (itemResults != null)
      {
        Iterator iterator = itemResults.iterator();
        while (iterator.hasNext())
        {
          ItemResult itemResult = (ItemResult) iterator.next();
          String itemId = itemResult.getItemId().getIdString();
          Element itemResultElement = itemResult.getElement();
          assessmentReport.setItemResult(itemId, itemResultElement);
        }
      }
      
      assessmentReport.updateScores();
    }
    catch (DigitalRepositoryException e)
    {
      LOG.error(e);
      throw new Error(e);
    }
    catch (OsidException e)
    {
      LOG.error(e);
      throw new Error(e);
    }
    catch (DOMException e)
    {
      LOG.error(e);
      throw new Error(e);
    }
      
    return assessmentReport;
    
  }
  
  public void setAssessmentId(String assessmentId)
  {
    this.assessmentId = assessmentId;
  }
  
  public String getAssessmentId()
  {
    return this.assessmentId;
  }
  
  public void setAssessment(Document assessment)
  {
    this.assessment = assessment;
  }
  
  public Document getAssessment()
  {
    return this.assessment;
  }
  
  public Document getDocument()
  {
    return this.report;
  }
  
  public Element getAssessmentResult()
  {
    DOMSource source = XmlUtil.getDocumentSource(this.context, GET_ASSESSMENT_RESULT_XSL);
    Transformer transformer = XmlUtil.createTransformer(source);
    transformer.setParameter("assessment_id", this.assessmentId);
    return XmlUtil.transformDocument(this.report, transformer).getDocumentElement();
  }
  
  public void setAssessmentResult(Element assessmentResult)
  {
    DOMSource source = XmlUtil.getDocumentSource(this.context, SET_ASSESSMENT_RESULT_XSL);
    Transformer transformer = XmlUtil.createTransformer(source);
    transformer.setParameter("assessment_id", this.assessmentId);
    transformer.setParameter("assessment_result", assessmentResult);
    this.report = XmlUtil.transformDocument(this.report, transformer);
  }
  
  public void setAssessmentComments(String comments)
    throws OsidException, IOException
  {
    DOMSource source = XmlUtil.getDocumentSource(this.context, SET_ASSESSMENT_COMMENTS_XSL);
    Transformer transformer = XmlUtil.createTransformer(source);
    transformer.setParameter("assessment_id", this.assessmentId);
    transformer.setParameter("comments", comments);
    this.report = XmlUtil.transformDocument(this.report, transformer);
    
    Element assessmentResult = this.getAssessmentResult();
    rm.setAssessmentResult(rm.getId(assessmentId), assessmentResult);
  }
  
  public Element getSectionResult(String sectionId)
  {
    DOMSource source = XmlUtil.getDocumentSource(this.context, GET_SECTION_RESULT_XSL);
    Transformer transformer = XmlUtil.createTransformer(source);
    transformer.setParameter("section_id", sectionId);
    return XmlUtil.transformDocument(this.report, transformer).getDocumentElement();
  }
  
  public void setSectionResult(String sectionId, Element sectionResult)
  {
    DOMSource source = XmlUtil.getDocumentSource(this.context, SET_SECTION_RESULT_XSL);
    Transformer transformer = XmlUtil.createTransformer(source);
    transformer.setParameter("section_id", sectionId);
    transformer.setParameter("section_result", sectionResult);
    this.report = XmlUtil.transformDocument(this.report, transformer);
  }
  
  public void setSectionComments(String sectionId, String comments)
    throws OsidException, IOException
  {
    DOMSource source = XmlUtil.getDocumentSource(this.context, SET_SECTION_COMMENTS_XSL);
    Transformer transformer = XmlUtil.createTransformer(source);
    transformer.setParameter("section_id", sectionId);
    transformer.setParameter("comments", comments);
    this.report = XmlUtil.transformDocument(this.report, transformer);
    
    Element sectionResult = this.getSectionResult(sectionId);
    rm.setSectionResult(rm.getId(this.assessmentId), rm.getId(sectionId), sectionResult);
  }
  
  public Element getItemResult(String itemId)
  {
    DOMSource source = XmlUtil.getDocumentSource(this.context, GET_ITEM_RESULT_XSL);
    Transformer transformer = XmlUtil.createTransformer(source);
    transformer.setParameter("item_id", itemId);
    return XmlUtil.transformDocument(this.report, transformer).getDocumentElement();
  }
  
  public void setItemResult(String itemId, Element itemResult)
  {
    DOMSource source = XmlUtil.getDocumentSource(this.context, SET_ITEM_RESULT_XSL);
    Transformer transformer = XmlUtil.createTransformer(source);
    transformer.setParameter("item_id", itemId);
    transformer.setParameter("item_result", itemResult);
    this.report = XmlUtil.transformDocument(this.report, transformer);
  }
  
  public void setItemComments(String itemId, String comments)
    throws OsidException, IOException
  {
    DOMSource source = XmlUtil.getDocumentSource(this.context, SET_ITEM_COMMENTS_XSL);
    Transformer transformer = XmlUtil.createTransformer(source);
    transformer.setParameter("item_id", itemId);
    transformer.setParameter("comments", comments);
    this.report = XmlUtil.transformDocument(this.report, transformer);
    
    Element itemResult = this.getItemResult(itemId);
    rm.setItemResult(rm.getId(this.assessmentId), rm.getId(itemId), itemResult);
  }
  
  public void setItemScore(String itemId, String score)
    throws OsidException, IOException
  {
    DOMSource source = XmlUtil.getDocumentSource(this.context, SET_ITEM_SCORE_XSL);
    Transformer transformer = XmlUtil.createTransformer(source);
    transformer.setParameter("item_id", itemId);
    transformer.setParameter("score", score);
    this.report = XmlUtil.transformDocument(this.report, transformer);
    
    Element itemResult = this.getItemResult(itemId);
    rm.setItemResult(rm.getId(this.assessmentId), rm.getId(itemId), itemResult);
    this.updateScores();
  }
  
  public void clearResponse(String itemId, String responseId)
  {
    DOMSource source = XmlUtil.getDocumentSource(this.context, SET_ITEM_SCORE_XSL);
    Transformer transformer = XmlUtil.createTransformer(source);
    transformer.setParameter("item_id", itemId);
    transformer.setParameter("response_id", responseId);
    this.report = XmlUtil.transformDocument(this.report, transformer);
  }
  
  public void appendResponse(String itemId, String responseId, String value)
  {
    DOMSource source = XmlUtil.getDocumentSource(this.context, SET_ITEM_SCORE_XSL);
    Transformer transformer = XmlUtil.createTransformer(source);
    transformer.setParameter("item_id", itemId);
    transformer.setParameter("response_id", responseId);
    transformer.setParameter("value", value);
    this.report = XmlUtil.transformDocument(this.report, transformer);
  }
  
  private void setName(String name)
  {
    DOMSource source = XmlUtil.getDocumentSource(this.context, SET_NAME_XSL);
    Transformer transformer = XmlUtil.createTransformer(source);
    transformer.setParameter("name", name);
    this.report = XmlUtil.transformDocument(this.report, transformer);
  }
  
  private void setUserId(String userId)
  {
    DOMSource source = XmlUtil.getDocumentSource(this.context, SET_USER_ID_XSL);
    Transformer transformer = XmlUtil.createTransformer(source);
    transformer.setParameter("user_id", userId);
    this.report = XmlUtil.transformDocument(this.report, transformer);
  }
  
  private void setPublishedId(String publishedId)
  {
    DOMSource source = XmlUtil.getDocumentSource(this.context, SET_PUBLISHED_ID);
    Transformer transformer = XmlUtil.createTransformer(source);
    transformer.setParameter("published_id", publishedId);
    this.report = XmlUtil.transformDocument(this.report, transformer);
  }
  
  private void setCoreId(String coreId)
  {
    DOMSource source = XmlUtil.getDocumentSource(this.context, SET_CORE_ID);
    Transformer transformer = XmlUtil.createTransformer(source);
    transformer.setParameter("core_id", coreId);
    this.report = XmlUtil.transformDocument(this.report, transformer);
  }
  
  public void setAttempts(String itemId, String responseId, int attempts)
  {
    DOMSource source = XmlUtil.getDocumentSource(this.context, SET_ITEM_SCORE_XSL);
    Transformer transformer = XmlUtil.createTransformer(source);
    transformer.setParameter("item_id", itemId);
    transformer.setParameter("response_id", responseId);
    transformer.setParameter("attempts", String.valueOf(attempts));
    this.report = XmlUtil.transformDocument(this.report, transformer); 
  }
  
  public void gradeItem(String itemId)
  {
    DOMSource source = XmlUtil.getDocumentSource(this.context, GRADE_ITEM_XSL);
    Transformer transformer = XmlUtil.createTransformer(source);
    transformer.setParameter("item_id", itemId);
    transformer.setParameter("assessment", this.assessment);
    this.report = XmlUtil.transformDocument(this.report, transformer);
  }
  
  public void updateScores()
  {
    this.updateSectionScores();
    this.updateAssessmentScore();
  }
  
  private void updateAssessmentScore()
  {
    DOMSource source = XmlUtil.getDocumentSource(this.context, UPDATE_ASSESSMENT_SCORE_XSL);
    Transformer transformer = XmlUtil.createTransformer(source);
    this.report = XmlUtil.transformDocument(this.report, transformer);
  }
  
  private void updateSectionScores()
  {
    DOMSource source = XmlUtil.getDocumentSource(this.context, UPDATE_SECTION_SCORES_XSL);
    Transformer transformer = XmlUtil.createTransformer(source);
    this.report = XmlUtil.transformDocument(this.report, transformer); 
  }
}
