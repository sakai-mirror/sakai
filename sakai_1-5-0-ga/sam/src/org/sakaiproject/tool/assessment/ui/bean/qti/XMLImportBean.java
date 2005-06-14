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

package org.sakaiproject.tool.assessment.ui.bean.qti;

import java.io.Serializable;
import java.util.ArrayList;

import javax.faces.event.ValueChangeEvent;

import org.w3c.dom.Document;

import org.sakaiproject.tool.assessment.business.entity.constants.QTIVersion;
import org.sakaiproject.tool.assessment.business.entity.helper.AuthoringHelper;
import org.sakaiproject.tool.assessment.facade.AssessmentFacade;
import org.sakaiproject.tool.assessment.facade.AssessmentFacadeQueries;
import org.sakaiproject.tool.assessment.facade.AssessmentTemplateFacade;
import org.sakaiproject.tool.assessment.services.assessment.AssessmentService;
import org.sakaiproject.tool.assessment.ui.bean.author.AssessmentBean;
import org.sakaiproject.tool.assessment.ui.bean.author.AuthorBean;
import org.sakaiproject.tool.assessment.ui.bean.author.ItemAuthorBean;
import org.sakaiproject.tool.assessment.util.XmlUtil;

/**
 * <p>Bean for QTI Import Data</p>
 * <p>Copyright: Copyright (c) 2005 Sakai</p>
 * @author Ed Smiley esmiley@stanford.edu
 * @version $Id: XMLImportBean.java,v 1.4 2005/02/10 20:52:49 esmiley.stanford.edu Exp $
 */

public class XMLImportBean implements Serializable
{
  private int qtiVersion;
  private String uploadFileName;
  private String importType;
  private AuthorBean authorBean;
  private AssessmentBean assessmentBean;
  private ItemAuthorBean itemAuthorBean;

  public XMLImportBean()
  {
  }

  // put tests here...
  public static void main(String[] args)
  {
    XMLImportBean XMLImportBean1 = new XMLImportBean();
  }

  /**
   * Value change on upload
   * @param e
   */
  public void importFromQti(ValueChangeEvent e)
  {
    String uploadFile = (String) e.getNewValue();

    System.out.println(" AuthorBean =" + authorBean);
    System.out.println(" AssessmentBean =" +  assessmentBean);
    System.out.println(" ItemAuthorBean =" +  itemAuthorBean);

    processFile(uploadFile);
  }

  /**
   *
   * @return
   */
  public int getQtiVersion()
  {
    return qtiVersion;
  }

  /**
   *
   * @param qtiVersion
   */
  public void setQtiVersion(int qtiVersion)
  {
    if (!QTIVersion.isValid(qtiVersion))
    {
      throw new IllegalArgumentException("NOT Legal Qti Version.");
    }
    this.qtiVersion = qtiVersion;
  }

  /**
   *
   * @return
   */
  public String getUploadFileName()
  {
    return uploadFileName;
  }

  /**
   *
   * @param uploadFileName
   */
  public void setUploadFileName(String uploadFileName)
  {
    this.uploadFileName = uploadFileName;
  }

  /**
   * A, S, I
   * @return type of upload
   */
  public String getImportType()
  {
    return importType;
  }

  /**
   * A, S, I
   * @param importType A, S, or I
   */
  public void setImportType(String importType)
  {
    this.importType = importType;
  }

  private void processFile(String uploadFile)
  {
    itemAuthorBean.setTarget(ItemAuthorBean.FROM_ASSESSMENT); // save to assessment

    // Get the file name
    String fileName = uploadFile;

    // Create an assessment based on the uploaded file
    AssessmentFacade assessment = createImportedAssessment(fileName);

    // Go to editAssessment.jsp, so prepare assessmentBean
    assessmentBean.setAssessment(assessment);
    // reset in case anything hanging around
    authorBean.setAssessTitle("");
    authorBean.setAssessmentDescription("");
    authorBean.setAssessmentTypeId("");
    authorBean.setAssessmentTemplateId(AssessmentTemplateFacade.DEFAULTTEMPLATE.toString());

    // update core AssessmentList: get the managed bean, author and set the list
    AssessmentService assessmentService = new AssessmentService();
    ArrayList list = assessmentService.getBasicInfoOfAllActiveAssessments(
                     AssessmentFacadeQueries.TITLE);
    //
    authorBean.setAssessments(list);
  }

  private AssessmentFacade createImportedAssessment(String fullFileName)
  {
    AuthoringHelper ah = new AuthoringHelper();
    Document document = XmlUtil.readDocument(fullFileName);
    return ah.createImportedAssessment(document);
  }
  public org.sakaiproject.tool.assessment.ui.bean.author.AuthorBean getAuthorBean()
  {
    return authorBean;
  }
  public void setAuthorBean(AuthorBean authorBean)
  {
    this.authorBean = authorBean;
  }
  public AssessmentBean getAssessmentBean()
  {
    return assessmentBean;
  }
  public void setAssessmentBean(AssessmentBean assessmentBean)
  {
    this.assessmentBean = assessmentBean;
  }
  public ItemAuthorBean getItemAuthorBean()
  {
    return itemAuthorBean;
  }
  public void setItemAuthorBean(ItemAuthorBean itemAuthorBean)
  {
    this.itemAuthorBean = itemAuthorBean;
  }

}