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
import org.sakaiproject.tool.assessment.ui.bean.author.TemplateBean;
import org.sakaiproject.tool.assessment.services.assessment.AssessmentService;
import org.sakaiproject.tool.assessment.services.assessment.PublishedAssessmentService;
import org.sakaiproject.tool.assessment.services.GradingService;
import org.sakaiproject.tool.assessment.facade.AssessmentTemplateFacade;
import org.sakaiproject.tool.assessment.facade.TypeFacade;
import org.sakaiproject.tool.assessment.facade.AssessmentFacade;
import org.sakaiproject.tool.assessment.facade.AssessmentFacadeQueries;
import org.sakaiproject.tool.assessment.facade.PublishedAssessmentFacadeQueries;
import org.sakaiproject.tool.assessment.facade.PublishedAssessmentFacade;
import java.util.*;

/**
 * <p>Title: Samigo</p>2
 * <p>Description: Sakai Assessment Manager</p>
 * <p>Copyright: Copyright (c) 2004 Sakai Project</p>
 * <p>Organization: Sakai Project</p>
 * @author Ed Smiley
 * @version $Id: AuthorActionListener.java,v 1.18.2.1 2005/03/09 16:44:04 daisyf.stanford.edu Exp $
 */

public class AuthorActionListener
    implements ActionListener
{
  private static ContextUtil cu;

  public AuthorActionListener()
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

    // get service and managed bean
    AssessmentService assessmentService = new AssessmentService();
    PublishedAssessmentService publishedAssessmentService = new PublishedAssessmentService();
    GradingService gradingService = new GradingService();
    AuthorBean author = (AuthorBean) cu.lookupBean(
                       "author");

    //#1 - prepare active template list. Note that we only need the title. We don't need the
    // full template object - be cheap.
    ArrayList templateList = assessmentService.getTitleOfAllActiveAssessmentTemplates();
    // get the managed bean, author and set the list
    author.setAssessmentTemplateList(templateList);

    //#2 - prepare core assessment list
    author.setCoreAssessmentOrderBy(AssessmentFacadeQueries.TITLE);
    ArrayList assessmentList = assessmentService.getBasicInfoOfAllActiveAssessments(
        AssessmentFacadeQueries.TITLE);
    // get the managed bean, author and set the list
    author.setAssessments(assessmentList);

    //#3 This map contains (Long, Integer)=(publishedAssessmentId, submissionSize)
    HashMap map = gradingService.getSubmissionSizeOfAllPublishedAssessments();

    //#4 - prepare published assessment list
    author.setPublishedAssessmentOrderBy(PublishedAssessmentFacadeQueries.TITLE);
    ArrayList publishedList = publishedAssessmentService.getBasicInfoOfAllActivePublishedAssessments(
        PublishedAssessmentFacadeQueries.TITLE);
    setSubmissionSize(publishedList, map);
    // get the managed bean, author and set the list
    author.setPublishedAssessments(publishedList);

    //#5 - prepare published inactive assessment list
    author.setInactivePublishedAssessmentOrderBy(PublishedAssessmentFacadeQueries.TITLE);
    ArrayList inactivePublishedList = publishedAssessmentService.getBasicInfoOfAllInActivePublishedAssessments(
       PublishedAssessmentFacadeQueries.TITLE);
    setSubmissionSize(inactivePublishedList, map);
    // get the managed bean, author and set the list
    author.setInactivePublishedAssessments(inactivePublishedList);
  }

  private void setSubmissionSize(ArrayList list, HashMap map){
    for (int i=0; i<list.size();i++){
      PublishedAssessmentFacade p =(PublishedAssessmentFacade)list.get(i);
      Integer size = (Integer) map.get(p.getPublishedAssessmentId());
      if (size != null){
        p.setSubmissionSize(size.intValue());
        //System.out.println("*** submission size" + size.intValue());
      }
    }
  }
}
