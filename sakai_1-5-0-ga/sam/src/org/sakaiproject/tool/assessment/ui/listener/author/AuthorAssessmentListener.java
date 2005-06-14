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

import javax.faces.component.UICommand;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.context.FacesContext;
import org.sakaiproject.tool.assessment.ui.listener.util.ContextUtil;
import org.sakaiproject.tool.assessment.ui.bean.author.AuthorBean;
import org.sakaiproject.tool.assessment.ui.bean.author.AssessmentBean;
import org.sakaiproject.tool.assessment.ui.bean.author.TemplateBean;
import org.sakaiproject.tool.assessment.ui.bean.author.ItemAuthorBean;
import org.sakaiproject.tool.assessment.services.assessment.AssessmentService;
import org.sakaiproject.tool.assessment.facade.AssessmentTemplateFacade;
import org.sakaiproject.tool.assessment.facade.TypeFacade;
import org.sakaiproject.tool.assessment.facade.AssessmentFacade;
import org.sakaiproject.tool.assessment.facade.AssessmentTemplateFacade;
import org.sakaiproject.tool.assessment.facade.AssessmentFacadeQueries;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AssessmentTemplateIfc;
import java.util.*;
import javax.faces.model.SelectItem;

/**
 * <p>Title: Samigo</p>2
 * <p>Description: Sakai Assessment Manager</p>
 * <p>Copyright: Copyright (c) 2004 Sakai Project</p>
 * <p>Organization: Sakai Project</p>
 * @author Ed Smiley
 * @version $Id: AuthorAssessmentListener.java,v 1.10 2004/12/14 08:46:49 daisyf.stanford.edu Exp $
 */

public class AuthorAssessmentListener
    implements ActionListener
{
  private static ContextUtil cu;

  public AuthorAssessmentListener()
  {
  }

  public void processAction(ActionEvent ae) throws AbortProcessingException
  {
    FacesContext context = FacesContext.getCurrentInstance();
    Map reqMap = context.getExternalContext().getRequestMap();
    Map requestParams = context.getExternalContext().getRequestParameterMap();
    System.out.println("debugging ActionEvent: " + ae);
    System.out.println("debug requestParams: " + requestParams);
    System.out.println("debug reqMap: " + reqMap);

    AuthorBean author = (AuthorBean) cu.lookupBean(
                         "author");
    AssessmentBean assessmentBean = (AssessmentBean) cu.lookupBean(
                                                           "assessmentBean");


    ItemAuthorBean itemauthorBean = (ItemAuthorBean) cu.lookupBean("itemauthor");
    itemauthorBean.setTarget(itemauthorBean.FROM_ASSESSMENT); // save to assessment


    // create an assessment based on the title entered and the assessment
    // template selected
    // #1 - read from form authorIndex.jsp
    System.out.println("*** 0. DEFAULT TEMPLATEID");
    String assessmentTitle = author.getAssessTitle();
    String description = author.getAssessmentDescription();
    String typeId = author.getAssessmentTypeId();
    String templateId = author.getAssessmentTemplateId();

    System.out.println("*** 1. DEFAULT TEMPLATEID"+templateId);
    if (templateId == null){
      templateId = AssessmentTemplateFacade.DEFAULTTEMPLATE.toString();;
      System.out.println("*** 2. DEFAULT TEMPLATE"+templateId);
    }
    System.out.println("*** 3. DEFAULT TEMPLATEID"+templateId);

    // #2 - got all the info, create now
    AssessmentFacade assessment = createAssessment(
        assessmentTitle, description, typeId, templateId);

    // #3a - goto editAssessment.jsp, so prepare assessmentBean
    assessmentBean.setAssessment(assessment);
    // #3b - reset the following
    author.setAssessTitle("");
    author.setAssessmentDescription("");
    author.setAssessmentTypeId("");
    author.setAssessmentTemplateId(AssessmentTemplateFacade.DEFAULTTEMPLATE.toString());

    // #3c - update core AssessmentList
    AssessmentService assessmentService = new AssessmentService();
    ArrayList list = assessmentService.getBasicInfoOfAllActiveAssessments(AssessmentFacadeQueries.TITLE);
    // get the managed bean, author and set the list
    author.setAssessments(list);
  }

  public AssessmentFacade createAssessment(
      String assessmentTitle, String description, String typeId,
      String templateId){
    AssessmentService assessmentService = new AssessmentService();
    AssessmentFacade assessment = assessmentService.createAssessment(
        assessmentTitle, description, typeId, templateId);
    return assessment;
  }
}
