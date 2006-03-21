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

package org.sakaiproject.tool.assessment.ui.listener.evaluation;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.tool.assessment.business.entity.RecordingData;
import org.sakaiproject.tool.assessment.data.dao.assessment.AssessmentAccessControl;
import org.sakaiproject.tool.assessment.data.dao.assessment.EvaluationModel;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedAssessmentData;
import org.sakaiproject.tool.assessment.data.dao.grading.AssessmentGradingData;
import org.sakaiproject.tool.assessment.data.dao.grading.ItemGradingData;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemDataIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.PublishedAssessmentIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.SectionDataIfc;
import org.sakaiproject.tool.assessment.facade.AgentFacade;
import org.sakaiproject.tool.assessment.services.assessment.PublishedAssessmentService;
import org.sakaiproject.tool.assessment.services.GradingService;
import org.sakaiproject.tool.assessment.services.PersistenceService;
import org.sakaiproject.tool.assessment.ui.bean.evaluation.AgentResults;
import org.sakaiproject.tool.assessment.ui.bean.evaluation.TotalScoresBean;
import org.sakaiproject.tool.assessment.ui.bean.evaluation.SubmissionStatusBean;
import org.sakaiproject.tool.assessment.ui.listener.evaluation.util.EvaluationListenerUtil;
import org.sakaiproject.tool.assessment.ui.listener.util.ContextUtil;
import org.sakaiproject.tool.assessment.util.BeanSort;
import org.sakaiproject.tool.assessment.integration.helper.ifc.AgentHelper;
import org.sakaiproject.tool.assessment.integration.context.IntegrationContextFactory;


/**
 * <p>Description: Action Listener for displaying Submission Status for anonymnous grading</p>
 * @version $Id$
 */

public class SubmissionStatusListener
  implements ActionListener
{
  private static Log log = LogFactory.getLog(SubmissionStatusListener.class);
  private static EvaluationListenerUtil util;
  private static BeanSort bs;
  private static ContextUtil cu;

  /**
   * Standard process action method.
   * @param ae ActionEvent
   * @throws AbortProcessingException
   */
  public void processAction(ActionEvent ae) throws
    AbortProcessingException
  {
    FacesContext context = FacesContext.getCurrentInstance();
    Map reqMap = context.getExternalContext().getRequestMap();
    Map requestParams = context.getExternalContext().
                        getRequestParameterMap();
    log.info("requestParams: " + requestParams);
    log.info("reqMap: " + reqMap);

    log.info("Submission Status LISTENER.");
    SubmissionStatusBean bean = (SubmissionStatusBean) cu.lookupBean("submissionStatus");
    TotalScoresBean totalScoresBean = (TotalScoresBean) cu.lookupBean("totalScores");

    // we probably want to change the poster to be consistent
    String publishedId = cu.lookupParam("publishedId");
    log.info("Got publishedId " + publishedId);

    log.info("Calling totalScores.");
    if (!submissionStatus(publishedId, bean, totalScoresBean, false))
    {
      throw new RuntimeException("failed to call totalScores.");
    }
  }


  /**
   * This will populate the SubmissionStatusBean with the data associated with the
   * particular versioned assessment based on the publishedId.
   *
   * @todo Some of this code will change when we move this to Hibernate persistence.
   * @param publishedId String
   * @param bean SubmissionStatusBean
   * @return boolean
   */
  public boolean submissionStatus(
    String publishedId, SubmissionStatusBean bean, TotalScoresBean totalScoresBean, boolean isValueChange)
  {
    log.debug("submissionStatus()");
    try
    {
      TotalScoreListener totalScorelistener = new TotalScoreListener();

      GradingService delegate =	new GradingService();

      if (cu.lookupParam("sortBy") != null &&
          !cu.lookupParam("sortBy").trim().equals(""))
        bean.setSortType(cu.lookupParam("sortBy"));

/*
      String which = cu.lookupParam("allSubmissions");
      log.info("Rachel: allSubmissions = " + which);
      if (which == null)
        which = "false";
      bean.setAllSubmissions(which);
*/

      bean.setPublishedId(publishedId);
      // we are only interested in showing last submissions

      ArrayList scores = delegate.getLastAssessmentGradingList(new Long(publishedId));
      Iterator iter = scores.iterator();
      log.info("Has this many agents: " + scores.size());
      if (!iter.hasNext())
        return false;
      Object next = iter.next();
      Date dueDate = null;

      // - Collect a list of all the users in the scores list
      Map useridMap= totalScoresBean.getUserIdMap();
System.out.println("lydiatest useridmap size = " + useridMap.keySet().size());
      ArrayList agentUserIds = totalScorelistener.getAgentIds(useridMap);
System.out.println("lydiatest agentUserid size = " + agentUserIds.size());
      AgentHelper helper = IntegrationContextFactory.getInstance().getAgentHelper();
      Map userRoles = helper.getUserRolesFromContextRealm(agentUserIds);
System.out.println("lydiatest userroles size = " + userRoles.keySet().size());


      // Okay, here we get the first result set, which has a summary of
      // information and a pointer to the graded assessment we should be
      // displaying.  We get the graded assessment.
      AssessmentGradingData data = (AssessmentGradingData) next;
      PublishedAssessmentService pubService = new PublishedAssessmentService();
      PublishedAssessmentIfc pub = (PublishedAssessmentIfc) pubService.getPublishedAssessment(data.getPublishedAssessmentId().toString());
      if (pub != null)
      {
        bean.setAssessmentName(pub.getTitle());
      }

      if (cu.lookupParam("roleSelection") != null)
      {
        bean.setRoleSelection(cu.lookupParam("roleSelection"));
      }

      if (bean.getSortType() == null)
      {
          bean.setSortType("idString");
      }


      /* Dump the grading and agent information into AgentResults */
      ArrayList agents = new ArrayList();
      ArrayList students_submitted= new ArrayList();
      iter = scores.iterator();
      while (iter.hasNext())
      {
        AgentResults results = new AgentResults();
        AssessmentGradingData gdata = (AssessmentGradingData) iter.next();
        gdata.setItemGradingSet(new HashSet());
        BeanUtils.copyProperties(results, gdata);

        results.setAssessmentGradingId(gdata.getAssessmentGradingId());
        String agentid =  gdata.getAgentId();
        AgentFacade agent = new AgentFacade(agentid);
        results.setLastName(agent.getLastName());
        results.setFirstName(agent.getFirstName());
        if (results.getLastName() != null &&
            results.getLastName().length() > 0)
          results.setLastInitial(results.getLastName().substring(0,1));
        else if (results.getFirstName() != null &&
                 results.getFirstName().length() > 0)
          results.setLastInitial(results.getFirstName().substring(0,1));
        else
          results.setLastInitial("A");
        results.setIdString(agent.getIdString());
        results.setRole((String)userRoles.get(agentid));
        agents.add(results);
        students_submitted.add(agentid);
      }

System.out.println("lydiatest agents size = " + agents.size());
      ArrayList students_not_submitted= new ArrayList();
      Iterator useridIterator = useridMap.keySet().iterator();
      while (useridIterator.hasNext()) {
        String userid = (String) useridIterator.next();
        if (!students_submitted.contains(userid)) {
          students_not_submitted.add(userid);
        }
      }
      prepareNotSubmittedAgentResult(students_not_submitted.iterator(), agents, userRoles);
System.out.println("lydiatest agents size = " + agents.size());
      log.info("Sort type is " + bean.getSortType() + ".");
      bs = new BeanSort(agents, bean.getSortType());
      if (
        (bean.getSortType()).equals("assessmentGradingId") )
      {
        bs.toNumericSort();
      } else {
        bs.toStringSort();
      }


      bs.sort();
      log.info("Listing agents.");
      bean.setAgents(agents);
      bean.setTotalPeople(new Integer(bean.getAgents().size()).toString());
    }

    catch (Exception e)
    {
      e.printStackTrace();
      return false;
    }

    return true;
  }



  //add those students that have not submitted scores, need to display them
  // in the UI 
  public void prepareNotSubmittedAgentResult(Iterator notsubmitted_iter,
                                             ArrayList agents, Map userRoles){
    while (notsubmitted_iter.hasNext()){
      String studentid = (String) notsubmitted_iter.next();
      AgentResults results = new AgentResults();
      AgentFacade agent = new AgentFacade(studentid);
      results.setLastName(agent.getLastName());
      results.setFirstName(agent.getFirstName());
      if (results.getLastName() != null &&
        results.getLastName().length() > 0)
      {
        results.setLastInitial(results.getLastName().substring(0,1));
      }
      else if (results.getFirstName() != null &&
               results.getFirstName().length() > 0)
      {
        results.setLastInitial(results.getFirstName().substring(0,1));
      }
      else
      {
        results.setLastInitial("Anonymous");
      }
      results.setIdString(agent.getIdString());
      results.setRole((String)userRoles.get(studentid));
      agents.add(results);
    }
  }
}
