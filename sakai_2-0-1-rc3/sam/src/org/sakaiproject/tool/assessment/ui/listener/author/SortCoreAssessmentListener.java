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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.tool.assessment.facade.AssessmentFacadeQueries;
import org.sakaiproject.tool.assessment.services.assessment.AssessmentService;
import org.sakaiproject.tool.assessment.ui.bean.author.AuthorBean;
import org.sakaiproject.tool.assessment.ui.listener.util.ContextUtil;

/**
 * <p>Title: Samigo</p>2
 * <p>Description: Sakai Assessment Manager</p>
 * <p>Copyright: Copyright (c) 2004 Sakai Project</p>
 * <p>Organization: Sakai Project</p>
 * @author Ed Smiley
 * @version $Id$
 */

public class SortCoreAssessmentListener
    implements ActionListener
{
  private static Log log = LogFactory.getLog(SortCoreAssessmentListener.class);
  private static ContextUtil cu;

  public SortCoreAssessmentListener()
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
    AuthorBean author = (AuthorBean) cu.lookupBean(
                       "author");

    // look for some sort information passed as parameters
    processSortInfo(author);

    //String orderBy = (String) FacesContext.getCurrentInstance().
    //   getExternalContext().getRequestParameterMap().get("coreOrderBy");
    //author.setCoreAssessmentOrderBy(orderBy);

    ArrayList assessmentList = new ArrayList();

      assessmentList = assessmentService.getBasicInfoOfAllActiveAssessments(
        this.getCoreOrderBy(author), author.isCoreAscending());

    // get the managed bean, author and set the list
    author.setAssessments(assessmentList);

  }

/**
   * get orderby parameter for takable table
   * @param select the SelectAssessment bean
   * @return
   */
  private String getCoreOrderBy(AuthorBean author) {
    String sort = author.getCoreAssessmentOrderBy();
    String returnType =  AssessmentFacadeQueries.TITLE;
    if (sort != null && sort.equals("lastModifiedDate"))
    {
	returnType = AssessmentFacadeQueries.LASTMODIFIEDDATE;
    }

    return returnType;
  }

/**
   * look at sort info from post and set bean accordingly
   * @param bean the select index managed bean
   */
  private void processSortInfo(AuthorBean bean) {
    String coreOrder = cu.lookupParam("coreSortType");
    String coreAscending = cu.lookupParam("coreAscending");

    if (coreOrder != null && !coreOrder.trim().equals("")) {
      bean.setCoreAssessmentOrderBy(coreOrder);
    }

    if (coreAscending != null && !coreAscending.trim().equals("")) {
      try {
        bean.setCoreAscending((Boolean.valueOf(coreAscending)).booleanValue());
      }
      catch (Exception ex) { //skip
      }
    }
    else
    {
	bean.setCoreAscending(true);
    }

  }

}