/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/presence/Presence.java,v 1.10 2004/09/30 20:20:51 ggolden.umich.edu Exp $
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

// package
package org.sakaiproject.tool.presence;

// imports
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.cheftool.VmServlet;
import org.sakaiproject.service.framework.log.cover.Log;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.legacy.presence.cover.PresenceService;
import org.sakaiproject.util.ParameterParser;
import org.sakaiproject.util.observer.PresenceObservingCourier;
import org.sakaiproject.vm.ActionURL;

/**
* <p>Presence is an action which prepares a presence list.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Id: Presence.java,v 1.10 2004/09/30 20:20:51 ggolden.umich.edu Exp $
*/
public class Presence extends VmServlet
{
	/** State attribute names. */
	private static final String STATE_OBSERVER = "obsever";

	/** Context variable names. */
	private static final String ATTR_USERS = "users";

	/**
	 * Respond to a request by dispatching to a portlet like "do" method based on the portlet mode and tool mode
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException
	{
		// this is the presence location (if tool id is in request, it will be part of the location)
		String location = PortalService.getCurrentLocationId();

		// get state for "presence" + pid if present in the request
		ParameterParser parser = (ParameterParser) req.getAttribute(ATTR_PARAMS);
		String pid = "presence." + parser.getString(ActionURL.PARAM_SITE);
		String reqPid = parser.getString(ActionURL.PARAM_PID);
		if (reqPid != null)
		{
			pid += "." + reqPid;
		}
		SessionState state = UsageSessionService.getSessionState(pid);

		// if we have a session (i.e. logged in), then make sure we have an observer
		if (UsageSessionService.getSession() != null)
		{
			// make sure there's an observer in there
			PresenceObservingCourier observer = (PresenceObservingCourier) state.getAttribute(STATE_OBSERVER);

			// if not, make one and store it
			if (observer == null)
			{
				// this is where deliveries go (if tool id is part of the request, it will be part of the delivery id)
				String deliveryId = PortalService.getCurrentClientWindowId();

				// this is the HTML element holding presence display
				String elementId = PortalService.getPresenceElementId(reqPid);

				// setup an observer to notify us
				observer = new PresenceObservingCourier(deliveryId, elementId, location, true);
				state.setAttribute(STATE_OBSERVER, observer);

				if (Log.getLogger("chef").isDebugEnabled())
					Log.debug(
						"chef",
						this +": observer: " + observer + " location: " + location + " deliveryAddress: " + deliveryId);
			}
		}
		// get the current presence list (Session objects)
		List users = PresenceService.getPresentUsers(location);
		setVmReference(ATTR_USERS, users, req);

		// process the response
		includeVm("/vm/presence/presence.vm", req, res);
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/presence/Presence.java,v 1.10 2004/09/30 20:20:51 ggolden.umich.edu Exp $
*
**********************************************************************************/
