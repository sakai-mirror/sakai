/**********************************************************************************
*
* $Header: /cvs/sakai/jsf/src/java/org/sakaiproject/jsf/custom/SakaiNavigationHandler.java,v 1.11 2004/11/09 03:43:28 janderse.umich.edu Exp $
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

package org.sakaiproject.jsf.custom;

import java.io.IOException;

import javax.faces.application.NavigationHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.sakaiproject.service.framework.config.ToolRegistration;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.current.cover.CurrentService;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.framework.session.SessionState;

/**
 * <p>SakaiNavigationHandler handles naviagation by saving state in the 
 * session and redirecting to the Sakai Tool URL.  This improves the URLs
 * to which users are exposed in their browsers.  Clean and friendly
 * Sakai Tool URLs are what the user sees in their browser, even though
 * a paremeterized URL was accessed for the navigation.  This is implemented
 * by saving the current FacesMessage(s) in the session, then redirecting 
 * to the appropriate Sakai Tool URL.  See SakaiFacesContextFactory to find
 * the restoration of the Sakai FaceMessage(s) after the URL redirection.
 * </p>
 * 
 * @author University of Michigan, CHEF Software Development Team
 * @version $Revision: 1.11 $
 */
public class SakaiNavigationHandler extends NavigationHandler
{

	/**
	 * Construct.
	 */
	public SakaiNavigationHandler()
	{
	}

	/**
	 * Handle the navigation
	 * @param context The Faces context.
	 * @param fromAction The action string that triggered the action.
	 * @param outcome The logical outcome string, which is the new tool mode, or if null, the mode does not change.
	 */
	public void handleNavigation(FacesContext context, String fromAction, String outcome)
	{
		// get the tool state
		SessionState state = PortalService.getCurrentToolState();

		// set the tool's new mode into its state
		if (outcome != null)
		{
			state.setAttribute("mode", outcome);
			
			// after the action request, picking a new mode, we are done with the current tree
			// if we leave it around, this old tree is found next time this session comes to this mode
			// and the local values are still set from the prior use -ggolden
			UIViewRoot viewRoot = context.getViewRoot();
			state.removeAttribute(SakaiStateManager.NMS + viewRoot.getViewId());
		}

		// save messages from the context for restoration on the next rendering
		MessageSaver.saveMessages(context);

		// redirect to the initial request URI
		String redirect = getToolUrl();

		try
		{
			context.getExternalContext().redirect(redirect);
		}
		catch (IOException e)
		{
		}

		context.responseComplete();
	}

	/**
	 * Compute the proper URL for the current request
	 */
	protected String getToolUrl()
	{
		String rv = null;

		HttpServletRequest req = (HttpServletRequest) CurrentService.getInThread(HttpServletRequest.class.getName());
		if (req != null)
		{
			String pid = req.getParameter("pid");
			String panel = req.getParameter("panel");

			// find the tool registration
			ToolRegistration reg = null;

			// pid should lead us to a site's tool placement somewhere
			ToolConfiguration tool = SiteService.findTool(pid);
			if (tool != null)
			{
				reg = ServerConfigurationService.getToolRegistration(tool.getToolId());
			}

			// if not in a site, see if pid is a registered tool id
			else
			{
				reg = ServerConfigurationService.getToolRegistration(pid);
			}

			// base the result on the registration if we have one
			if (reg != null)
			{
				rv = reg.getUrl();
			}

			// otherwise, we must use the pid as the root of the result
			else
			{
				rv = pid;
			}

			// add the pid parameter
			rv = rv + "?pid=" + pid;
			
			// add the panel if provided
			if (panel != null)
			{
				rv = rv + "&panel=" + panel;
			}
		}

		return rv;
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/jsf/src/java/org/sakaiproject/jsf/custom/SakaiNavigationHandler.java,v 1.11 2004/11/09 03:43:28 janderse.umich.edu Exp $
*
**********************************************************************************/
