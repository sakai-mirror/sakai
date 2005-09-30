/**********************************************************************************
 *
 * $Header: /cvs/sakai2/legacy/tool-support/src/java/org/sakaiproject/vm/VmServlet.java,v 1.11 2005/05/28 03:04:34 ggolden.umich.edu Exp $
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

import org.sakaiproject.api.kernel.tool.Tool;
import org.sakaiproject.api.kernel.tool.cover.ToolManager;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.util.web.Web;

/**
 * <p>
 * VmServlet is a Servlet that makes use of the Velocity Template Engine.
 * </p>
 * <p>
 * This extends our ComponentServlet, giving us also the ability to find registered service components.
 * </p>
 * 
 * @author University of Michigan, CHEF Software Development Team
 * @version $Revision: 1.11 $
 */
public abstract class VmServlet extends ComponentServlet
{
	/**
	 * Access the object set in the velocity context for this name, if any.
	 * 
	 * @param name
	 *        The reference name.
	 * @param request
	 *        The request.
	 * @return The reference value object, or null if none
	 */
	public Object getVmReference(String name, HttpServletRequest request)
	{
		return request.getAttribute(name);
	}

	/**
	 * Add a reference object to the velocity context by name - if it's not already defined
	 * 
	 * @param name
	 *        The reference name.
	 * @param value
	 *        The reference value object.
	 * @param request
	 *        The request.
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
	 * 
	 * @param request
	 *        The request.
	 * @param response
	 *        The response.
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

		// get the include (from the portal) for the HTML HEAD
		if (getVmReference("sakai_head", request) == null)
		{
			String headInclude = (String) request.getAttribute("sakai.html.head");
			if (headInclude != null)
			{
				setVmReference("sakai_head", headInclude, request);
			}
		}

		// get the include (from the portal) for the HTML BODY onload
		if (getVmReference("sakai_onload", request) == null)
		{
			String onload = (String) request.getAttribute("sakai.html.body.onload");
			if (onload != null)
			{
				setVmReference("sakai_onload", onload, request);
			}
		}

		// set the ref to the images
		if (getVmReference("sakai_image_path", request) == null)
		{
			setVmReference("sakai_image_path", "/library/image/", request);
		}

		// set the ref to the javscripts
		if (getVmReference("sakai_script_path", request) == null)
		{
			setVmReference("sakai_script_path", "/library/js/", request);
		}

		// set the ref to the library
		if (getVmReference("sakai_library_path", request) == null)
		{
			setVmReference("sakai_library_path", "/library/", request);
		}

	}

	/**
	 * Include the Velocity template, expanded with the current set of references
	 * 
	 * @param template
	 *        The path, relative to the webapp context, of the template file
	 * @param request
	 *        The render request.
	 * @param response
	 *        The render response.
	 * @throws PortletException
	 *         if something goes wrong.
	 */
	protected void includeVm(String template, HttpServletRequest request, HttpServletResponse response) throws ServletException
	{
		// include some standard references
		setVmStdRef(request, response);

		response.setContentType("text/html");

		// tell our vm processor to use this template
		request.setAttribute("sakai.vm.path", template);

		try
		{
			// get the vm by name so it does not have to have a URL mapping
			RequestDispatcher dispatcher = getServletContext().getNamedDispatcher("sakai.vm");
			dispatcher.include(request, response);
		}
		catch (IOException e)
		{
			throw new ServletException("includeVm: template: " + template, e);
		}
	}

	/**
	 * Get a new ActionURL.
	 * 
	 * @param req
	 *        The current request.
	 * @return A new ActionURL.
	 */
	protected ActionURL getActionURL(HttpServletRequest request)
	{
		ActionURL a = new ActionURL(Web.returnUrl(request, null));

		// set the pid and panel, if present in the request
		//a.setPid(request.getParameter(ActionURL.PARAM_PID));
		a.setPanel(request.getParameter(ActionURL.PARAM_PANEL));
		a.setSite(request.getParameter(ActionURL.PARAM_SITE));
		a.setPage(request.getParameter(ActionURL.PARAM_PAGE));

		return a;
	}

	/**
	 * Compute the Help url for the current tool
	 * 
	 * @param req
	 *        The current request.
	 * @return The URL to help with the current tool specified for help context.
	 */
	protected String getHelpURL(HttpServletRequest req)
	{
		// check for a current tool, for help context
		String helpContext = null;
		Tool tool = ToolManager.getCurrentTool();
		if (tool != null)
		{
			helpContext = tool.getId();
		}

		String rv = ServerConfigurationService.getHelpUrl(helpContext);

		return rv;
	}
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/legacy/tool-support/src/java/org/sakaiproject/vm/VmServlet.java,v 1.11 2005/05/28 03:04:34 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
