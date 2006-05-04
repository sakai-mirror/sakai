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
import java.util.*;
import org.sakaiproject.tool.assessment.ui.listener.util.ContextUtil;
import org.sakaiproject.tool.assessment.ui.bean.author.SectionBean;
import org.sakaiproject.tool.assessment.ui.bean.author.AssessmentBean;
import org.sakaiproject.tool.assessment.services.assessment.AssessmentService;
import org.sakaiproject.tool.assessment.facade.AssessmentFacade;

/**
 * <p>Title: Samigo</p>
 * <p>Description: Sakai Assessment Manager</p>
 * <p>Copyright: Copyright (c) 2004 Sakai Project</p>
 * <p>Organization: Sakai Project</p>
 * @author Ed Smiley
 * @version $Id: RemovePartListener.java,v 1.6 2004/12/17 05:03:39 daisyf.stanford.edu Exp $
 */

public class RemovePartListener implements ActionListener
{
  private static ContextUtil cu;

  public RemovePartListener()
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

    AssessmentBean assessmentBean = (AssessmentBean) cu.lookupBean(
        "assessmentBean");
    SectionBean sectionBean = (SectionBean) cu.lookupBean(
        "sectionBean");

    // #1. get all the info need from bean
    String sectionId = sectionBean.getSectionId();
    String destSectionId = sectionBean.getDestSectionId();
    String removeAllQuestions = sectionBean.getRemoveAllQuestions();

    // #2 - check if we are removing all question or we
    // need to move question to another part
    AssessmentService assessmentService = new AssessmentService();
    System.out.println("** removeAll Question="+removeAllQuestions);
    if (!removeAllQuestions.equals("1")){
      // move questions to destinated Section when removing a section
      if (destSectionId == null || ("").equals(destSectionId)){
        destSectionId = assessmentBean.getFirstSectionId();
      }
      assessmentService.moveAllItems(sectionId,destSectionId);
    }
    assessmentService.removeSection(sectionId);

    // #2 - goto editAssessment.jsp, so reset assessmentBean
    System.out.println("** assessmentId in RemovePartListener ="+assessmentBean.getAssessmentId());
    AssessmentFacade assessment = assessmentService.getAssessment(
        assessmentBean.getAssessmentId());
    assessmentBean.setAssessment(assessment);

  }

}