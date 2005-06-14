/**********************************************************************************
*
* $Header: /cvs/sakai/velocity/src/java/org/sakaiproject/vm/DispatchServlet.java,v 1.14 2004/12/02 20:22:46 ggolden.umich.edu Exp $
*
***********************************************************************************
*
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
*
**********************************************************************************/

package org.sakaiproject.vm;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.service.framework.current.cover.CurrentService;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.util.ParameterParser;

/**
* <p>DispatchServlet is a Servlet that dispatches to other resources based on a tool (pid) state mode and possible panel parameter.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.14 $
*/
public class DispatchServlet extends ComponentServlet
{
	/**
	 * Override service, adding CurrentService binding of the HttpSession into the current (request) thread.
	 */
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, java.io.IOException
	{
		try
		{
			ParameterParser parser = (ParameterParser) req.getAttribute(ATTR_PARAMS);

			// grab the tool's mode from state for easy access in the request attributes
			String pid = parser.getString("pid");
			if (pid == null)
			{
				// use the path as the pid if missing
				pid = req.getServletPath();
			}

			SessionState state = UsageSessionService.getSessionState(pid);

			String mode = null;
			if (state != null)
			{
				mode = (String) state.getAttribute("mode");
			}

			if (mode == null)
			{
				mode = "main";
			}

			// getServletPath() is /path/ext.tool ... make this /path/mode-panel.ext.
			String path = req.getServletPath();
			int pos = path.lastIndexOf("/");
			String root = path.substring(0, pos + 1);
			String rest = path.substring(pos + 1);
			pos = rest.indexOf(".");
			String ext = rest.substring(0, pos);

			String panel = parser.getString("panel");
			if (panel == null)
			{
				panel = "";
			}

			// Special case: for Title panel, don't use mode
			if ("Title".equals(panel))
			{
				mode = "";
			}

			// Special case: for Main panel, use just mode
			else if ("Main".equals(panel))
			{
				panel = "";
			}

			String separator = "";
			if ((mode.length() > 0) && (panel.length() > 0))
			{
				separator = "-";
			}

			String resource = root + mode + separator + panel + "." + ext;

			try
			{
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(resource);
				dispatcher.forward(req, resp);
			}
			catch (IOException e)
			{
				throw new ServletException("service: resource: " + resource, e);
			}
		}
		finally
		{
			// clear out any current access bindings
			CurrentService.clearInThread();
		}
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/velocity/src/java/org/sakaiproject/vm/DispatchServlet.java,v 1.14 2004/12/02 20:22:46 ggolden.umich.edu Exp $
*
**********************************************************************************/
