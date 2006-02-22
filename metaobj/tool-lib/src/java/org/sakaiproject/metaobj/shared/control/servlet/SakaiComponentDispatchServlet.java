/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/trunk/sakai/legacy-service/service/src/java/org/sakaiproject/exception/InconsistentException.java $
 * $Id: InconsistentException.java 632 2005-07-14 21:22:50Z janderse@umich.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
package org.sakaiproject.metaobj.shared.control.servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.kernel.component.cover.ComponentManager;
import org.sakaiproject.api.kernel.session.Session;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.metaobj.shared.model.*;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.framework.session.SessionState;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SakaiComponentDispatchServlet extends DispatcherServlet {


   private class SimpleAgent2 implements Agent {

      String uid = "";
      String eid = "";

      SimpleAgent2(String eid, String uid) {
         this.eid = eid;
         this.uid = uid;
      }

      public Id getId() {
         return new IdImpl(eid, null);
      }

      public Artifact getProfile() {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public Object getProperty(String key) {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getDisplayName() {
         return this.uid;
      }

      public boolean isInRole(String role) {
         return false;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public boolean isInitialized() {
         return false;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public String getRole() {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public List getWorksiteRoles(String worksiteId) {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public List getWorksiteRoles() {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

      public boolean isRole() {
         return false;
      }

      public String getName() {
         return null;  //To change body of implemented methods use File | Settings | File Templates.
      }
   };


   protected final transient Log logger = LogFactory.getLog(getClass());
   public static final String TOOL_STATE_VIEW_KEY = "osp.tool.state.view";
   public static final String TOOL_STATE_VIEW_REQUEST_PARAMS_KEY = "osp.tool.state.request.params";

   /**
    * Obtain and use the handler for this method.
    * The handler will be obtained by applying the servlet's HandlerMappings in order.
    * The HandlerAdapter will be obtained by querying the servlet's
    * installed HandlerAdapters to find the first that supports the handler class.
    * Both doGet() and doPost() are handled by this method.
    * It's up to HandlerAdapters to decide which methods are acceptable.
    */
   protected void doService(HttpServletRequest req, HttpServletResponse resp) throws Exception {
      if (/* getFilter().processRequest(req)*/ true) {
         try {
            //req = getFilter().wrapRequest(req, resp);
            if (req == null) {
               return;
            }

            if ("Title".equals(req.getParameter("panel"))) {
               resp.sendRedirect(req.getContextPath() + "/title.osp?pid=" + req.getParameter("pid"));
               return;
            }


            Session s = SessionManager.getCurrentSession();
            if (s == null) {
               logger.error("can't determine user");
            }


            SimpleAgent2 agent = new SimpleAgent2(s.getUserEid(), s.getUserId());


            //RepositoryManager rm=(RepositoryManager)BeanFactory.getInstance().getBean("repositoryManager");



            //logger.debug("Global root: "+rm.getGlobalRoot().getDisplayName());
            //logger.debug("Agent's root: "+rm.getRootNode(agent));





            logger.error("TOOL STATE IS NOT BEING CONSIDERED. FIX ME!!!");
            //TODO

            // workaround to force tools into a certain state

            //

            // relies on "osp.tool.view" param being in tool session state
            SessionState toolState = PortalService.getCurrentToolState();
            if (toolState != null) {
               String redirectPath = (String) toolState.getAttribute(TOOL_STATE_VIEW_KEY);

               if (redirectPath != null) {
                  StringBuffer redirectUrl = new StringBuffer(redirectPath + "?pid=" + req.getParameter("pid"));
                  Map requestParams = (Map) toolState.getAttribute(TOOL_STATE_VIEW_REQUEST_PARAMS_KEY);
                  for (Iterator i = requestParams.keySet().iterator(); i.hasNext();) {
                     String name = (String) i.next();
                     redirectUrl.append("&" + name + "=" + requestParams.get(name));
                  }
                  toolState.removeAttribute(TOOL_STATE_VIEW_KEY);
                  toolState.removeAttribute(TOOL_STATE_VIEW_REQUEST_PARAMS_KEY);
                  resp.sendRedirect(redirectUrl.toString());
                  return;
               }
            }

            super.doService(req, resp);
         }
         catch (Exception e) {
            logger.error("", e);
            throw new OspException(e);
         } finally {
            getFilter().tearDown(req);
         }
      }

   }


   /**
    * Called by the servlet container to indicate to a servlet that the
    * servlet is being placed into service.  See {@link javax.servlet.Servlet#init}.
    * <p/>
    * <p>This implementation stores the {@link javax.servlet.ServletConfig}
    * object it receives from the servlet container for later use.
    * When overriding this form of the method, call
    * <code>super.init(config)</code>.
    *
    * @param config the <code>ServletConfig</code> object
    *               that contains configutation
    *               information for this servlet
    * @throws javax.servlet.ServletException if an exception occurs that
    *                                        interrupts the servlet's normal
    *                                        operation
    * @see javax.servlet.UnavailableException
    */

   public void init(ServletConfig config) throws ServletException {
      super.init(config);
   }

   protected RequestSetupFilter getFilter() {
      return (RequestSetupFilter) ComponentManager.getInstance().get(RequestSetupFilter.class.getName());
   }

}
