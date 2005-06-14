/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/courier/Courier.java,v 1.19 2004/09/30 20:21:43 ggolden.umich.edu Exp $
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

package org.sakaiproject.tool.courier;

import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.cheftool.VmServlet;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.service.framework.courier.cover.CourierService;
import org.sakaiproject.service.framework.log.cover.Log;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.framework.session.UsageSession;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.legacy.presence.cover.PresenceService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SitePage;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.time.cover.TimeService;

/*
 * <p>Courier is an action which is used to check check for and recieve any deliveries.</p>
 *
 * @author University of Michigan, CHEF Software Development Team
 * @version $Revision: 1.19 $
 */
public class Courier extends VmServlet
{
	/**
	 * Respond to a request by dispatching to a portlet like "do" method based on the portlet mode and tool mode
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException
	{
//		// TODO: TIMING
//		if (CurrentService.getInThread("DEBUG") == null)
//			CurrentService.setInThread("DEBUG", new StringBuffer());
//		long startTime = System.currentTimeMillis();

		// Part 1: find all deliveries for the requested deivery address

		// Note: if session is null, user might not be logged in yet...
		UsageSession session = UsageSessionService.getSession();
		if (session != null)
		{
			String address = PortalService.getCurrentClientWindowId();

			List deliveries = CourierService.getDeliveries(address);
			setVmReference("deliveries", deliveries, req);
		}

		// set the refresh rate
		setVmReference("refresh", "10", req);

		// Part 2: refresh the user's presence in the current location (portal page, or perhaps portlet)
		// identifying the root of session state keys so that when the user's presence is lost,
		// the session state is cleaned up.

		// user may have just logged out...
		if (session != null)
		{
			// get the location id of the current request
			String location = PortalService.getCurrentLocationId();
			String[] locationParsed = PresenceService.parseLocation(location);

			// the site
			if (locationParsed[0] != null)
			{
				location = PresenceService.locationId(locationParsed[0], null, null);
				PresenceService.setPresence(location);

				// the page
				if (locationParsed[1] != null)
				{
					location = PresenceService.locationId(locationParsed[0], locationParsed[1], null);
					PresenceService.setPresence(location);
					
					// the tool
					if (locationParsed[2] != null)
					{
						location = PresenceService.locationId(locationParsed[0], locationParsed[1], locationParsed[2]);
						PresenceService.setPresence(location);
					}
				}
			}

			// if we have a tool id in the request, we are done
			if (locationParsed[2] == null)
			{
				// otherwise we need to set the presence of all the tools of the active site page that are not floating
				try
				{
					// get the site
					Site site = null;
					try
					{
						site = SiteService.getSite(locationParsed[0]);
					}
					catch (IdUnusedException e)
					{
					}

					// get the page
					SitePage page = site.getPage(locationParsed[1]);
					if (page != null)
					{
						// process each tool
						for (Iterator iTools = page.getTools().iterator(); iTools.hasNext();)
						{
							ToolConfiguration tool = (ToolConfiguration) iTools.next();

							// TODO: if the tool's not here, but floating perhaps, dont sponser it's presence.

							// location is base location + / tool id
							String toolLocation =
								PresenceService.locationId(locationParsed[0], locationParsed[1], tool.getId());

							// set presence for this tool
							PresenceService.setPresence(toolLocation);
						}
					}
				}
				catch (Exception e)
				{
					Log.warn("chef", "Courier: site page tools: " + e);
				}
			}
		}

		setVmReference("message", TimeService.newTime().toStringLocalFull(), req);

		// add the logger if we want logs of each delivery delivered
		// setVmReference("logger", Logger.getInstance(), req);

		// process the response
		includeVm("/vm/courier/courier.vm", req, res);

//		// TODO: TIMING
//		long endTime = System.currentTimeMillis();
//		if (endTime-startTime > /*5*/000)
//		{
//			StringBuffer buf = (StringBuffer) CurrentService.getInThread("DEBUG");
//			if (buf != null)
//			{
//				buf.insert(0,"courier: "
//						+ PortalService.getCurrentLocationId()
//						+ " time: " + (endTime - startTime));
//			}
//		}
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/courier/Courier.java,v 1.19 2004/09/30 20:21:43 ggolden.umich.edu Exp $
*
**********************************************************************************/
