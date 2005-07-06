/*
 * The Open Source Portfolio Initiative Software is Licensed under the Educational Community License Version 1.0:
 *
 * This Educational Community License (the "License") applies to any original work of authorship
 * (the "Original Work") whose owner (the "Licensor") has placed the following notice immediately
 * following the copyright notice for the Original Work:
 *
 * Copyright (c) 2004 Trustees of Indiana University and r-smart Corporation
 *
 * This Original Work, including software, source code, documents, or other related items, is being
 * provided by the copyright holder(s) subject to the terms of the Educational Community License.
 * By obtaining, using and/or copying this Original Work, you agree that you have read, understand,
 * and will comply with the following terms and conditions of the Educational Community License:
 *
 * Permission to use, copy, modify, merge, publish, distribute, and sublicense this Original Work and
 * its documentation, with or without modification, for any purpose, and without fee or royalty to the
 * copyright holder(s) is hereby granted, provided that you include the following on ALL copies of the
 * Original Work or portions thereof, including modifications or derivatives, that you make:
 *
 * - The full text of the Educational Community License in a location viewable to users of the
 * redistributed or derivative work.
 *
 * - Any pre-existing intellectual property disclaimers, notices, or terms and conditions.
 *
 * - Notice of any changes or modifications to the Original Work, including the date the changes were made.
 *
 * - Any modifications of the Original Work must be distributed in such a manner as to avoid any confusion
 *  with the Original Work of the copyright holders.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) may NOT be used in advertising or publicity pertaining
 * to the Original or Derivative Works without specific, written prior permission. Title to copyright
 * in the Original Work and any associated documentation will at all times remain with the copyright holders.
 *
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/shared/control/servlet/SakaiComponentDispatchServlet.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/29 18:36:41 $
 */
package org.sakaiproject.metaobj.shared.control.servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.utils.BeanFactory;
import org.sakaiproject.metaobj.shared.model.OspException;
import org.springframework.web.servlet.DispatcherServlet;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.framework.session.SessionState;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import java.util.Map;
import java.util.Iterator;

public class SakaiComponentDispatchServlet extends DispatcherServlet {
   protected final transient Log logger = LogFactory.getLog(getClass());
   public static final String TOOL_STATE_VIEW_KEY ="osp.tool.state.view";
   public static final String TOOL_STATE_VIEW_REQUEST_PARAMS_KEY ="osp.tool.state.request.params";

   /**
    * Obtain and use the handler for this method.
    * The handler will be obtained by applying the servlet's HandlerMappings in order.
    * The HandlerAdapter will be obtained by querying the servlet's
    * installed HandlerAdapters to find the first that supports the handler class.
    * Both doGet() and doPost() are handled by this method.
    * It's up to HandlerAdapters to decide which methods are acceptable.
    */
   protected void doService(HttpServletRequest req, HttpServletResponse resp) throws Exception {
      if (getFilter().processRequest(req)) {
         try {
            req = getFilter().wrapRequest(req, resp);
            if (req == null) {
               return;
            }

            if ("Title".equals(req.getParameter("panel"))) {
               resp.sendRedirect(req.getContextPath() + "/title.osp?pid=" + req.getParameter("pid"));
               return;
            }

            // workaround to force tools into a certain state
            // relies on "osp.tool.view" param being in tool session state
            SessionState toolState = PortalService.getCurrentToolState();
            if (toolState != null){
               String redirectPath = (String) toolState.getAttribute(TOOL_STATE_VIEW_KEY);

               if (redirectPath != null){
                  StringBuffer redirectUrl = new StringBuffer(redirectPath + "?pid=" + req.getParameter("pid"));
                  Map requestParams = (Map)toolState.getAttribute(TOOL_STATE_VIEW_REQUEST_PARAMS_KEY);
                  for (Iterator i=requestParams.keySet().iterator();i.hasNext();){
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
         } catch (Exception e) {
            logger.error("", e);
            throw new OspException(e);
         }
         finally {
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
      return (RequestSetupFilter)BeanFactory.getInstance().getBean(RequestSetupFilter.class.getName());
   }

}
