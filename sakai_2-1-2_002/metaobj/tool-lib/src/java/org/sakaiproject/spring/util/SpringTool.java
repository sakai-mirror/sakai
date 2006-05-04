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
package org.sakaiproject.spring.util;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.api.kernel.tool.ActiveTool;
import org.sakaiproject.api.kernel.tool.Tool;
import org.sakaiproject.api.kernel.tool.ToolException;
import org.sakaiproject.api.kernel.tool.cover.ActiveToolManager;
import org.sakaiproject.util.web.Web;


public class SpringTool extends HttpServlet
{
   /** Our log (commons). */
   private static Log M_log = LogFactory.getLog(SpringTool.class);

   private static final String HELPER_EXT = ".helper";

   /** The file extension to get to JSF. */
   protected static final String JSF_EXT = ".osp";

   /** Session attribute to hold the last view visited. */
   public static final String LAST_VIEW_VISITED = "sakai.jsf.tool.last.view.visited";

   //  TODO: Note, these two values must match those in jsf-app's SakaiViewHandler

   /** Request attribute we set to help the return URL know what extension we (or jsf) add (does not need to be in the URL. */
   public static final String URL_EXT = "sakai.jsf.tool.URL.ext";

   /** Request attribute we set to help the return URL know what path we add (does not need to be in the URL. */
   public static final String URL_PATH = "sakai.jsf.tool.URL.path";

   /** The default target, as configured. */
   protected String m_default = null;

   /** if true, we preserve the last visit per placement / user, and use it if we get a request with no path. */
   protected boolean m_defaultToLastView = true;

   /** The folder to the jsf files, as configured. Does not end with a "/". */
   protected String m_path = null;
   
   private static final String HELPER_SESSION_PREFIX = "session.";

   /**
    * Compute a target (i.e. the servlet path info, not including folder root or jsf extension) for the case of the actual path being empty.
    * 
    * @return The servlet info path target computed for the case of empty actual path.
    */
   protected String computeDefaultTarget(boolean lastVisited)
   {
      // setup for the default view as configured
      String target = "/" + m_default;

      // if we are doing lastVisit and there's a last-visited view, for this tool placement / user, use that
      if (lastVisited)
      {
         ToolSession session = SessionManager.getCurrentToolSession();
         String last = (String) session.getAttribute(LAST_VIEW_VISITED);
         if (last != null)
         {
            target = last;
         }
      }

      return target;
   }

   protected String computeDefaultTarget()
   {
      return computeDefaultTarget(m_defaultToLastView);
   }

   /**
    * Shutdown the servlet.
    */
   public void destroy()
   {
      M_log.info("destroy");

      super.destroy();
   }

   /**
    * Respond to requests.
    * 
    * @param req
    *        The servlet request.
    * @param res
    *        The servlet response.
    * @throws ServletException
    * @throws IOException
    */
   protected void dispatch(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
   {
      // NOTE: this is a simple path dispatching, taking the path as the view id = jsp file name for the view,
      //       with default used if no path and a path prefix as configured.
      // TODO: need to allow other sorts of dispatching, such as pulling out drill-down ids and making them
      //       available to the JSF

      // build up the target that will be dispatched to
      String target = req.getPathInfo();

      // see if we have a helper request
      if (sendToHelper(req, res, target)) {
         return;
      }

      // see if we have a resource request - i.e. a path with an extension, and one that is not the JSF_EXT
      if (isResourceRequest(target))
      {
         // get a dispatcher to the path
         RequestDispatcher resourceDispatcher = getServletContext().getRequestDispatcher(target);
         if (resourceDispatcher != null)
         {
            resourceDispatcher.forward(req, res);
            return;
         }
      }

      if ("Title".equals(req.getParameter("panel")))
      {
         // This allows only one Title JSF for each tool
         target = "/title.osp";
      }

      else
      {
         ToolSession session = SessionManager.getCurrentToolSession();

         if (target == null || "/".equals(target))
         {
            target = computeDefaultTarget();
            
            // make sure it's a valid path
            if (!target.startsWith("/"))
            {
               target = "/" + target;
            }

            // now that we've messed with the URL, send a redirect to make it official
            res.sendRedirect(Web.returnUrl(req, target));
            return;
         }

         // see if we want to change the specifically requested view
         String newTarget = redirectRequestedTarget(target);
         
         // make sure it's a valid path
         if (!newTarget.startsWith("/"))
         {
            newTarget = "/" + newTarget;
         }

         if (!newTarget.equals(target))
         {
            // now that we've messed with the URL, send a redirect to make it official
            res.sendRedirect(Web.returnUrl(req, newTarget));
            return;
         }
         target = newTarget;

         // store this
         session.setAttribute(LAST_VIEW_VISITED, target);
      }

      // add the configured folder root and extension (if missing)
      target = m_path + target;
      
      // add the default JSF extension (if we have no extension)
      int lastSlash = target.lastIndexOf("/");
      int lastDot = target.lastIndexOf(".");
      if (lastDot < 0 || lastDot < lastSlash)
      {
         target += JSF_EXT;
      }
            
      // set the information that can be removed from return URLs
      req.setAttribute(URL_PATH, m_path);
      req.setAttribute(URL_EXT, ".jsp");

      // set the sakai request object wrappers to provide the native, not Sakai set up, URL information
      // - this assures that the FacesServlet can dispatch to the proper view based on the path info
      req.setAttribute(Tool.NATIVE_URL, Tool.NATIVE_URL);

      // TODO: Should setting the HTTP headers be moved up to the portal level as well?
      res.setContentType("text/html; charset=UTF-8");
      res.addDateHeader("Expires", System.currentTimeMillis() - (1000L * 60L * 60L * 24L * 365L));
      res.addDateHeader("Last-Modified", System.currentTimeMillis());
      res.addHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
      res.addHeader("Pragma", "no-cache");

      // dispatch to the target
      M_log.debug("dispatching path: " + req.getPathInfo() + " to: " + target + " context: "
            + getServletContext().getServletContextName());
      RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(target);
      dispatcher.forward(req, res);

      // restore the request object
      req.removeAttribute(Tool.NATIVE_URL);
      req.removeAttribute(URL_PATH);
      req.removeAttribute(URL_EXT);
   }

   protected boolean sendToHelper(HttpServletRequest req, HttpServletResponse res, String target) throws ToolException {
      String path = req.getPathInfo();
      if (path == null) path = "/";

      // 0 parts means the path was just "/", otherwise parts[0] = "", parts[1] = item id, parts[2] if present is "edit"...
      String[] parts = path.split("/");

      if (parts.length < 2) {
         return false;
      }

      if (!parts[1].endsWith(HELPER_EXT)) {
         return false;
      }

      ToolSession toolSession = SessionManager.getCurrentToolSession();

      Enumeration params = req.getParameterNames();
      while (params.hasMoreElements()) {
         String paramName = (String)params.nextElement();
         if (paramName.startsWith(HELPER_SESSION_PREFIX)) {
            String attributeName = paramName.substring(HELPER_SESSION_PREFIX.length());
            toolSession.setAttribute(attributeName, req.getParameter(paramName));
         }
      }

      // calc helper id
      int posEnd = parts[1].lastIndexOf(".");

      String helperId = target.substring(1, posEnd + 1);
      ActiveTool helperTool = ActiveToolManager.getActiveTool(helperId);

      if (toolSession.getAttribute(helperTool.getId() + Tool.HELPER_DONE_URL) == null) {
         toolSession.setAttribute(helperTool.getId() + Tool.HELPER_DONE_URL,
               req.getContextPath() + req.getServletPath() + computeDefaultTarget(true));
      }

      String context = req.getContextPath() + req.getServletPath() + Web.makePath(parts, 1, 2);
      String toolPath = Web.makePath(parts, 2, parts.length);
      helperTool.help(req, res, context, toolPath);

      return true; // was handled as helper call
   }

   /**
    * Respond to requests.
    * 
    * @param req
    *        The servlet request.
    * @param res
    *        The servlet response.
    * @throws ServletException
    * @throws IOException
    */
   protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
   {
      dispatch(req, res);
   }

   /**
    * Respond to requests.
    * 
    * @param req
    *        The servlet request.
    * @param res
    *        The servlet response.
    * @throws ServletException
    * @throws IOException
    */
   protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
   {
      dispatch(req, res);
   }

   /**
    * Access the Servlet's information display.
    * 
    * @return servlet information.
    */
   public String getServletInfo()
   {
      return "Sakai JSF Tool Servlet";
   }

   /**
    * Initialize the servlet.
    * 
    * @param config
    *        The servlet config.
    * @throws ServletException
    */
   public void init(ServletConfig config) throws ServletException
   {
      super.init(config);

      m_default = config.getInitParameter("default");
      m_path = config.getInitParameter("path");
      m_defaultToLastView = "true".equals(config.getInitParameter("default.last.view"));

      // make sure there is no "/" at the end of the path
      if (m_path != null && m_path.endsWith("/"))
      {
         m_path = m_path.substring(0, m_path.length() - 1);
      }

      M_log.info("init: default: " + m_default + " path: " + m_path);
   }

   /**
    * Recognize a path that is a resource request. It must have an "extension", i.e. a dot followed by characters that do not include a slash.
    * 
    * @param path
    *        The path to check
    * @return true if the path is a resource request, false if not.
    */
   protected boolean isResourceRequest(String path)
   {
      // we need some path
      if ((path == null) || (path.length() == 0)) return false;

      // we need a last dot
      int pos = path.lastIndexOf(".");
      if (pos == -1) return false;

      // we need that last dot to be the end of the path, not burried in the path somewhere (i.e. no more slashes after the last dot)
      String ext = path.substring(pos);
      if (ext.indexOf("/") != -1) return false;

      // we need the ext to not be the JSF_EXT
      if (ext.equals(JSF_EXT)) return false;

      // ok, it's a resource request
      return true;
   }

   /**
    * Compute a new target (i.e. the servlet path info, not including folder root or jsf extension) if needed based on the requested target.
    * 
    * @param target
    *        The servlet path info target requested.
    * @return The target we will actually respond with.
    */
   protected String redirectRequestedTarget(String target)
   {
      return target;
   }

}


/**************************************************************************************************************************************************************************************************************************************************************
 * $URL: /opt/CVS/osp2.x/HomesTool/src/java/org/sakaiproject/spring/util/SpringTool.java,v 1.3 2005/06/28 22:35:21 chmaurer Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
