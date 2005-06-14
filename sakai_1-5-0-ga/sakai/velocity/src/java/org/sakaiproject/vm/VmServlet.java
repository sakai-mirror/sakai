/**********************************************************************************
*
* $Header: /cvs/sakai/velocity/src/java/org/sakaiproject/vm/VmServlet.java,v 1.20 2005/01/28 18:04:59 dlhaines.umich.edu Exp $
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

import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.util.ParameterParser;

/**
* <p>VmServlet is a Servlet that makes use of the Velocity Template Engine.</p>
* <p>This extends our ComponentServlet, giving us also the ability to find registered service components.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.20 $
*/
public abstract class VmServlet extends ComponentServlet
{
	/**
	 * Access the object set in the velocity context for this name, if any.
	 * @param name The reference name.
	 * @param request The request.
	 * @return The reference value object, or null if none
	 */
	public Object getVmReference(String name, HttpServletRequest request)
	{
		return request.getAttribute(name);
	}

	/**
	 * Add a reference object to the velocity context by name - if it's not already defined
	 * @param name The reference name.
	 * @param value The reference value object.
	 * @param request The request.
	 */
	public void setVmReference(String name, Object value, HttpServletRequest request)
	{
		if (request.getAttribute(name) == null)
		{
			request.setAttribute(name, value);
		}
//		else
//		{
//			Object old = request.getAttribute(name);
//			if (!old.equals(value))
//			{
//				log("double setting vmReference: " + name + " was: " + old + " to: " + value);
//			}
//		}
	}

	/**
	 * Add some standard references to the vm context.
	 * @param request The request.
	 * @param response The response.
	 */
	protected void setVmStdRef(HttpServletRequest request, HttpServletResponse response)
	{
		// include some standard references

		setVmReference("sakai_ActionURL", getActionURL(request), request);
		
		boolean useHelp = ServerConfigurationService.getBoolean("helpEnabled", true);
		if (useHelp) 
		{
			setVmReference("sakai_HelpURL", getHelpURL(request), request);
		}

		// add our fragment flag (false)
		setVmReference("sakai_fragment", Boolean.FALSE, request);

		// form the skin based on the site in the request (if any), and the defaults as configured
		if (getVmReference("sakai_skin", request) == null)
		{
			String skinRoot = ServerConfigurationService.getString("skin.root", "/sakai-portal/css/");

			// get a siteId.  Use an explicit one if it is set, otherwise use the parameter parser.
			String siteId = null;
			siteId = (String) request.getAttribute("siteId");
			if (siteId == null)
			{
				siteId = ((ParameterParser) request.getAttribute(ATTR_PARAMS)).getString("site");
			}

//			String siteId = ((ParameterParser) request.getAttribute(ATTR_PARAMS)).getString("site");
			String skin = SiteService.getSiteSkin(siteId);

			setVmReference("sakai_skin", skinRoot + skin, request);
			// form the portal root for the skin - removing the .css and adding "portalskins" before
			int pos = skin.indexOf(".css");
			if (pos != -1)
			{
				skin = skin.substring(0, pos);
			}
			setVmReference("sakai_portalskin", skinRoot + "portalskins" + "/" + skin + "/", request);
			setVmReference("sakai_skin_id", skin, request);
		}
	}

	/**
	 * Include the Velocity template, expanded with the current set of references
	 * @param template The path, relative to the webapp context, of the template file
	 * @param request The render request.
	 * @param response The render response.
	 * @throws PortletException if something goes wrong.
	 */
	protected void includeVm(String template, HttpServletRequest request, HttpServletResponse response)
		throws ServletException
	{
		// include some standard references
		setVmStdRef(request, response);

		response.setContentType("text/html");

		try
		{
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(template);
			dispatcher.include(request, response);
		}
		catch (IOException e)
		{
			throw new ServletException("includeVm: template: " + template, e);
		}
	}

	/**
	 * Get a new ActionURL.
	 * @param req The current request.
	 * @return A new ActionURL.
	 */
	protected ActionURL getActionURL(HttpServletRequest request)
	{
		// form the URL to this servlet (not including the path or query of this request)
		StringBuffer base = request.getRequestURL();

		String servletPath = request.getServletPath();
		int pos = base.indexOf(servletPath);
		if (pos != -1)
		{
			base.setLength(pos + servletPath.length());
		}

		ParameterParser parser = (ParameterParser) request.getAttribute(ATTR_PARAMS);

		ActionURL a = new ActionURL(base.toString());

		// set the pid and panel, if present in the request
		a.setPid(parser.getString(ActionURL.PARAM_PID));
		a.setPanel(parser.getString(ActionURL.PARAM_PANEL));
		a.setSite(parser.getString(ActionURL.PARAM_SITE));
		a.setPage(parser.getString(ActionURL.PARAM_PAGE));

		return a;
	}

	protected ActionURL getHelpURL(HttpServletRequest request)
	{
		// form the URL to this servlet (not including the path or query of this request)
		StringBuffer base = request.getRequestURL();

		String servletPath = request.getServletPath();
		String contextPath = request.getContextPath();
		String helpToolPath = "/sakai-help-tool/help/jsf.tool";
		String pid = "/tunnel/sakai-help-tool/help/jsf.tool";

		int pos = base.indexOf(contextPath);
		if (pos != -1)
		{
			base.setLength(pos);
		}


		String url = base.toString() + helpToolPath;

		ActionURL a = new ActionURL(url);

		a.setPid(pid);
		a.setParameter("helpDocId", servletPath.substring(1));

		return a;
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/velocity/src/java/org/sakaiproject/vm/VmServlet.java,v 1.20 2005/01/28 18:04:59 dlhaines.umich.edu Exp $
*
**********************************************************************************/
