
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
package org.sakaiproject.tool.assessment.ui.listener.author;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.tool.assessment.data.ifc.assessment.SectionDataIfc;
import org.sakaiproject.tool.assessment.facade.SectionFacade;
import org.sakaiproject.tool.assessment.services.assessment.AssessmentService;
import org.sakaiproject.tool.assessment.ui.bean.author.AssessmentBean;
import org.sakaiproject.tool.assessment.ui.bean.author.ItemAuthorBean;
import org.sakaiproject.tool.assessment.ui.bean.author.SectionBean;
import org.sakaiproject.tool.assessment.ui.listener.util.ContextUtil;

/**
 * <p>Title: Samigo</p>
 * <p>Description: Sakai Assessment Manager</p>
 * <p>Copyright: Copyright (c) 2004 Sakai Project</p>
 * <p>Organization: Sakai Project</p>
 * @author Ed Smiley
 * @version $Id: AuthorPartListener.java,v 1.14 2005/06/12 23:47:46 daisyf.stanford.edu Exp $
 */

public class AuthorPartListener implements ActionListener
{
  private static Log log = LogFactory.getLog(AuthorPartListener.class);
  private static ContextUtil cu;

  public AuthorPartListener()
  {
  }

  public void processAction(ActionEvent ae) throws AbortProcessingException
  {
    FacesContext context = FacesContext.getCurrentInstance();
    Map reqMap = context.getExternalContext().getRequestMap();
    Map requestParams = context.getExternalContext().getRequestParameterMap();
    //System.out.println("debugging ActionEvent: " + ae);
    //System.out.println("debug requestParams: " + requestParams);
    //System.out.println("debug reqMap: " + reqMap);

    // #1a. prepare sectionBean
    AssessmentBean assessmentBean = (AssessmentBean) cu.lookupBean(
                         "assessmentBean");
    ItemAuthorBean itemauthorbean = (ItemAuthorBean) cu.lookupBean("itemauthor");

    SectionBean sectionBean = (SectionBean) cu.lookupBean(
                                          "sectionBean");
    // clean it
    sectionBean = new SectionBean();
    sectionBean.setSectionTitle("");
    String assessmentId = assessmentBean.getAssessmentId();

    // #1b. goto editPart.jsp
    sectionBean.setPoolsAvailable(itemauthorbean.getPoolSelectList());
    sectionBean.setHideRandom(false);
    // set default 
    sectionBean.setType(SectionDataIfc.QUESTIONS_AUTHORED_ONE_BY_ONE.toString()); 
    sectionBean.setQuestionOrdering(SectionDataIfc.AS_LISTED_ON_ASSESSMENT_PAGE.toString()); 
  }

}
