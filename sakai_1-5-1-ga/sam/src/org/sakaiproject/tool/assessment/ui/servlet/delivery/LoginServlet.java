
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
package org.sakaiproject.tool.assessment.ui.servlet.delivery;
import org.sakaiproject.tool.assessment.services.assessment.PublishedAssessmentService;
import org.sakaiproject.tool.assessment.facade.PublishedAssessmentFacade;
import org.sakaiproject.tool.assessment.facade.AgentFacade;
import org.sakaiproject.tool.assessment.ui.bean.delivery.DeliveryBean;
import java.io.*;
import java.util.HashMap;
import java.util.Date;
import java.util.List;
import javax.servlet.*;
import javax.servlet.http.*;
import org.sakaiproject.tool.assessment.ui.listener.util.ContextUtil;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.FactoryFinder;
import org.sakaiproject.tool.assessment.ui.listener.delivery.BeginDeliveryActionListener;
import org.navigoproject.osid.impl.PersistenceService;
import org.sakaiproject.tool.assessment.data.dao.authz.AuthorizationData;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AssessmentAccessControlIfc;

/**
 * <p>Title: Samigo</p>
 * <p>Description: Sakai Assessment Manager</p>
 * <p>Copyright: Copyright (c) 2004 Sakai Project</p>
 * <p>Organization: Sakai Project</p>
 * @author Ed Smiley
 * @version $Id: LoginServlet.java,v 1.1.2.11 2005/04/04 19:42:23 daisyf.stanford.edu Exp $
 */

public class LoginServlet
    extends HttpServlet
{

  public LoginServlet()
  {
  }

  public void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException
  {
    doPost(req,res);
  }

  public void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException
  {
    HttpSession httpSession = req.getSession(true);
    httpSession.setMaxInactiveInterval(3600); // one hour 
    //we are going to use the delivery bean to flag that this access is via url
    // this is the flag that we will use in deliverAssessment.jsp to decide what
    // button to display - daisyf
    DeliveryBean delivery = (DeliveryBean) ContextUtil.lookupBeanFromExternalServlet(
       "delivery", req, res);
    delivery.setAccessViaUrl(true);

    // set path
    delivery.setContextPath(req.getContextPath());

    String alias = req.getParameter("id");
    // 1. get publishedAssessment and check if anonymous is allowed
    // 2. If so, goto welcome.faces
    // 3. If not, goto login.faces
    // both pages will set agentId and then direct user to BeginAssessment
    PublishedAssessmentService service = new PublishedAssessmentService();
    PublishedAssessmentFacade pub = service.getPublishedAssessmentIdByAlias(alias);
    RequestDispatcher dispatcher = null;
    String contextPath = req.getContextPath();
    String path = "/jsf/delivery/invalidAssessment.faces";
    String agentIdString = AgentFacade.getAgentString(req,res);
    boolean isAuthenticated = ( agentIdString!= null &&
          !("").equals(agentIdString));
    if (pub != null){
      // check if it is available
      // We are getting the total no. of submission (for grade) per assessment
      // by the given agent at the same time
      Integer submissions = new Integer(0);
      if (isAuthenticated){
        submissions = service.getTotalSubmission(agentIdString,
            pub.getPublishedAssessmentId().toString());
      }
      HashMap h = new HashMap();
      if (submissions.intValue()>0)
        h.put(pub.getPublishedAssessmentId(), submissions);
      boolean isAvailable = isAvailable(pub, h);
      //System.out.println("**** isAvailable="+isAvailable);
      //System.out.println("pub assessmentId="+pub.getPublishedAssessmentId());
      //System.out.println("pub assessment relaeseTo="+pub.getAssessmentAccessControl().getReleaseTo());
      if (!isAvailable) {
        path = "/jsf/delivery/assessmentNotAvailable.faces";
      }
      else {
        // if assessment is available, set it in delivery bean for display in deliverAssessment.jsp
        delivery.setPublishedAssessment(pub);
        // boolean anonymousAllowed = AuthorizationFacade.isAuthorized(
        //    "ANONYMOUS_USERS","TAKE_ASSESSMENT",pubId.toString());
        String releaseTo = pub.getAssessmentAccessControl().getReleaseTo();
        if (releaseTo != null) {
          boolean anonymousAllowed = ( (releaseTo).indexOf(
              "Anonymous Users") > -1);
          boolean authenticatedAllowed = ( (releaseTo).indexOf(
              "Authenticated Users") > -1);

          // check if user is member of the site which has access to the published
          // assessment
          boolean isMember = false;
          boolean isAuthenticatedAsAnonymous = false;
          if (isAuthenticated) {
            isMember = checkMembership(pub, req, res);
          }
          else if (anonymousAllowed) {
            AgentFacade.createAnonymous();
            isAuthenticatedAsAnonymous = true;
            delivery.setAnonymousLogin(true);
          }
   
          if ((authenticatedAllowed && isAuthenticated)
              || (isMember && isAuthenticated) || isAuthenticatedAsAnonymous) {
            BeginDeliveryActionListener listener = new
                BeginDeliveryActionListener();
            listener.processAction(null);
            path = "/jsf/delivery/beginTakingAssessment_viaurl.faces";
          }
          else {
            path = "/jsf/delivery/login.faces";
          }

          // if none of the 3 situation is met => access denied
          if (!authenticatedAllowed && !anonymousAllowed && !isMember){
            path = "/jsf/delivery/accessDenied.faces";
	  }
        }
      }
    }
    //System.out.println("***path"+path);
    dispatcher = req.getRequestDispatcher(path);
    dispatcher.forward(req, res);
  }

  private boolean checkMembership(PublishedAssessmentFacade pub,
       HttpServletRequest req, HttpServletResponse res){
    boolean isMember=false;
    // get list of site that this published assessment has been released to
    List l =PersistenceService.getInstance().getAuthzQueriesFacade().
        getAuthorizationByFunctionAndQualifier("VIEW_ASSESSMENT",
        pub.getPublishedAssessmentId().toString());
    for (int i=0;i<l.size();i++){
      String siteId = ((AuthorizationData)l.get(i)).getAgentIdString();
      isMember = PersistenceService.getInstance().getAuthzQueriesFacade().
          checkMembership(siteId);
      if (isMember)
        break;
    }
    return isMember;
  }

  private boolean isAvailable(PublishedAssessmentFacade f, HashMap h) {
    boolean returnValue = false;
    //1. prepare our significant parameters
    Date currentDate = new Date();
    Date startDate = f.getAssessmentAccessControl().getStartDate();
    Date retractDate = f.getAssessmentAccessControl().getRetractDate();
    Date dueDate = f.getAssessmentAccessControl().getDueDate();
    boolean acceptLateSubmission = AssessmentAccessControlIfc.
        ACCEPT_LATE_SUBMISSION.equals(
        f.getAssessmentAccessControl().getLateHandling());
    int maxSubmissionsAllowed = 9999;
    if ( (Boolean.FALSE).equals(f.getAssessmentAccessControl().getUnlimitedSubmissions()))
      maxSubmissionsAllowed = f.getAssessmentAccessControl().getSubmissionsAllowed().intValue();
    boolean notSubmitted = false;
    int totalSubmitted = 0;
    if (h.get(f.getPublishedAssessmentId()) == null)
      notSubmitted = true;
    else
      totalSubmitted = ( (Integer) h.get(f.getPublishedAssessmentId())).
          intValue();

      //2. time to go through all the criteria
    if (retractDate == null || retractDate.after(currentDate)) {
      if (dueDate == null || dueDate.after(currentDate) ||
          (dueDate.before(currentDate) && notSubmitted &&
           acceptLateSubmission)) {
        if (totalSubmitted < maxSubmissionsAllowed) {
          if (startDate == null ||
              (startDate != null && startDate.before(currentDate))){
            returnValue = true;
          }
        }
      }
    }
    return returnValue;
  }

}
