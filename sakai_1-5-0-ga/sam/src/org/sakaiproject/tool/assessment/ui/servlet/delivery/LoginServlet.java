
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
import javax.servlet.*;
import javax.servlet.http.*;
import org.sakaiproject.tool.assessment.ui.listener.util.ContextUtil;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.FactoryFinder;
import org.sakaiproject.tool.assessment.ui.listener.delivery.BeginDeliveryActionListener;
/**
 * <p>Title: Samigo</p>
 * <p>Description: Sakai Assessment Manager</p>
 * <p>Copyright: Copyright (c) 2004 Sakai Project</p>
 * <p>Organization: Sakai Project</p>
 * @author Ed Smiley
 * @version $Id: LoginServlet.java,v 1.1.2.2 2005/02/28 17:18:39 daisyf.stanford.edu Exp $
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
    String alias = req.getParameter("id");
    System.out.println("**assessmentId = " + alias);
    // 1. get publishedAssessment and check if anonymous is allowed
    // 2. If so, goto welcome.faces
    // 3. If not, goto login.faces
    // both pages will set agentId and then direct user to BeginAssessment
    PublishedAssessmentService service = new PublishedAssessmentService();
    PublishedAssessmentFacade pub = service.getPublishedAssessmentIdByAlias(alias);
    RequestDispatcher dispatcher = null;
    String contextPath = req.getContextPath();
    String path = "/jsf/delivery/invalidAssessment.faces";
    if (pub != null){
      System.out.println("pub assessmentId="+pub.getPublishedAssessmentId());
      System.out.println("pub assessment relaeseTo="+pub.getAssessmentAccessControl().getReleaseTo());
      setDeliveryBean(pub, req, res);
      // boolean anonymousAllowed = AuthorizationFacade.isAuthorized(
      //    "ANONYMOUS_USERS","TAKE_ASSESSMENT",pubId.toString());
      String releaseTo = pub.getAssessmentAccessControl().getReleaseTo();
      if (releaseTo!=null){
        boolean anonymousAllowed = ( (releaseTo).indexOf(
            "Anonymous Users") > -1);
        System.out.println("***contextPath" + contextPath);
  
        // check if user is authenticated
        boolean isAuthenticated = false;
        if (AgentFacade.getAgentString()!=null && !("").equals(AgentFacade.getAgentString())){
            isAuthenticated = true;
	}
        else if (anonymousAllowed) {
          AgentFacade.createAnonymous();
          isAuthenticated = true;
	}

        if (isAuthenticated){
          BeginDeliveryActionListener listener = new BeginDeliveryActionListener();
          listener.processAction(null);
          path = "/jsf/delivery/beginTakingAssessment.faces"; 
	}
        else{
          path = "/jsf/delivery/login.faces";
        }
      }
    }
    System.out.println("***path"+path);
    dispatcher = req.getRequestDispatcher(path);
    dispatcher.forward(req, res);
  }

  private void setDeliveryBean(PublishedAssessmentFacade p,
                               HttpServletRequest req, HttpServletResponse res){
    DeliveryBean delivery = (DeliveryBean) ContextUtil.lookupBeanFromExternalServlet(
        "delivery", req, res);
    delivery.setPublishedAssessment(p);
  }
}
