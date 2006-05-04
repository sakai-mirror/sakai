/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2004-2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
*
**********************************************************************************/

package org.sakaiproject.tool.assessment.ui.listener.author;

import java.util.ArrayList;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.sakaiproject.tool.assessment.services.assessment.PublishedAssessmentService;
import org.sakaiproject.tool.assessment.ui.bean.author.AuthorBean;
import org.sakaiproject.tool.assessment.ui.bean.delivery.DeliveryBean;
import org.sakaiproject.tool.assessment.ui.listener.util.ContextUtil;
/**
 * <p>Title: Samigo</p>
 * <p>Description: Sakai Assessment Manager</p>
 * <p>Copyright: Copyright (c) 2004 Sakai Project</p>
 * <p>Organization: Sakai Project</p>
 * @author Ed Smiley
 * @version $Id$
 */

public class RemovePublishedAssessmentListener
    implements ActionListener
{
  private static ContextUtil cu;
  public RemovePublishedAssessmentListener()
  {
  }

  public void processAction(ActionEvent ae) throws AbortProcessingException
  {
    FacesContext context = FacesContext.getCurrentInstance();
    Map reqMap = context.getExternalContext().getRequestMap();
    Map requestParams = context.getExternalContext().getRequestParameterMap();

    // #1 - remove selected assessment
    String assessmentId = (String) FacesContext.getCurrentInstance().
        getExternalContext().getRequestParameterMap().get("publishedAssessmentId");

    if (assessmentId == null)  // means from the preview assessment button in delivery
    {
       DeliveryBean delivery = (DeliveryBean) cu.lookupBean("delivery");
       assessmentId = delivery.getAssessmentId();
       RemovePublishedAssessmentThread thread = new RemovePublishedAssessmentThread(assessmentId);
       thread.start();
    }
    else
    {

    RemovePublishedAssessmentThread thread = new RemovePublishedAssessmentThread(assessmentId);
    thread.start();
    PublishedAssessmentService assessmentService = new PublishedAssessmentService();

    //#3 - goto authorIndex.jsp so fix the assessment List in author bean after
    // removing an assessment
    AuthorBean author = (AuthorBean) cu.lookupBean(
                       "author");
    ArrayList assessmentList = assessmentService.getBasicInfoOfAllActivePublishedAssessments(
      author.getPublishedAssessmentOrderBy(),author.isPublishedAscending());
    // get the managed bean, author and set the list
    author.setPublishedAssessments(assessmentList);

    ArrayList inactivePublishedList = assessmentService.getBasicInfoOfAllInActivePublishedAssessments(
        author.getInactivePublishedAssessmentOrderBy(),author.isInactivePublishedAscending());
     // get the managed bean, author and set the list
     author.setInactivePublishedAssessments(inactivePublishedList);
    }

  }

}