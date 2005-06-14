/*
 * Created on Feb 12, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.business.entity;

import org.navigoproject.util.XmlUtil;

import org.w3c.dom.Document;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.Transformer;
import javax.servlet.ServletContext;


/**
 * @author palcasi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ResultProcessor
{
  private static final String GRADE_ITEM_XSL = "/xml/xsl/report/gradeItem.xsl";
  private static final String SECTION_SCORE_XSL = "/xml/xsl/report/updateSectionScores.xsl";
  private static final String ASSESSMENT_SCORE_XSL = "/xml/xsl/report/updateAssessmentScore.xsl";

  private Transformer assessmentTransformer;
  private Transformer sectionTransformer;
  private Transformer itemTransformer;
  private Document itemProcessingXsl;
  private Document assessment;

  public ResultProcessor(ServletContext context, Document assessment)
  {
    this.assessment = assessment;
    
    DOMSource assessmentSource = XmlUtil.getDocumentSource(context, ASSESSMENT_SCORE_XSL);
    this.assessmentTransformer = XmlUtil.createTransformer(assessmentSource);
    
    DOMSource sectionSource = XmlUtil.getDocumentSource(context, SECTION_SCORE_XSL);
    this.sectionTransformer = XmlUtil.createTransformer(sectionSource);
    
    DOMSource itemSource = XmlUtil.getDocumentSource(context, GRADE_ITEM_XSL);
    this.itemTransformer = XmlUtil.createTransformer(itemSource);
  }
  
  public Document getXsl()
  {
    return this.itemProcessingXsl;
  }

  public Document processResults(Document source)
  {
    Document itemResults;
    this.itemTransformer.setParameter("taken_assessment", this.assessment);
    itemResults = XmlUtil.transformDocument(source, this.itemTransformer);
    
    Document sectionResults;
    sectionResults = XmlUtil.transformDocument(itemResults, this.sectionTransformer);
    
    Document assessmentResult;
    assessmentResult = XmlUtil.transformDocument(sectionResults, this.assessmentTransformer);
    
    return assessmentResult;
  }
}
