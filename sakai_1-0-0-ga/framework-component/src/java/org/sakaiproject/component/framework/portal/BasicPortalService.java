/**********************************************************************************
*
* $Header: /cvs/sakai/framework-component/src/java/org/sakaiproject/component/framework/portal/BasicPortalService.java,v 1.18 2004/09/30 20:20:04 ggolden.umich.edu Exp $
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
package org.sakaiproject.component.framework.portal;

// imports
import org.sakaiproject.service.framework.current.CurrentService;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.portal.PortalService;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.legacy.presence.cover.PresenceService;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.util.ParameterParser;

/**
 * <p>PortalService is currently just a place where some definitions pertaining to
 * portal access references can be centralized.</p>
 *
 * @author University of Michigan, CHEF Software Development Team
 * @version $Revision: 1.18 $
 */
public class BasicPortalService
	implements PortalService
{
	/*******************************************************************************
	* Dependencies and their setter methods
	*******************************************************************************/

	/** Dependency: logging service */
	protected Logger m_logger = null;

	/**
	 * Dependency: logging service.
	 * @param service The logging service.
	 */
	public void setLogger(Logger service)
	{
		m_logger = service;
	}

	/** Dependency: current service */
	protected CurrentService m_currentService = null;

	/**
	 * Dependency: current service.
	 * @param service The current service.
	 */
	public void setCurrentService(CurrentService service)
	{
		m_currentService = service;
	}

	/*******************************************************************************
	* Init and Destroy
	*******************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		m_logger.info(this + ".init()");
	}

	/**
	* Returns to uninitialized state.
	*/
	public void destroy()
	{
		m_logger.info(this + ".destroy()");
	}

	/*******************************************************************************
	* PortalService implementation
	*******************************************************************************/

	/**
	* Extract the site id from the current request.
	* @return The site id implied from the current request.
	*/
	public String getCurrentSiteId()
	{
		// if the current request has a portlet window id which matches a tool id in the site service,
		// use that tool's site as the current site id.
		ParameterParser params = (ParameterParser) m_currentService.getInThread(ParameterParser.class.getName());
		if (params != null)
		{
			// Note: matches the ActionURL.PARAM_PID
			String pid = params.getString("pid");
			if (pid != null)
			{
				ToolConfiguration tool = SiteService.findTool(pid);
				if (tool != null)
				{
					return tool.getContainingPage().getContainingSite().getId();
				}
			}

			// if no tool id, try for a direct site id
			String siteId = params.getString("site");
			if (siteId != null)
			{
				return siteId;
			}
		}

		return null;

	}	// getCurrentSiteId

	/**
	* Extract the site page id from the current request.
	* @return The site page id implied from the current request.
	*/
	public String getCurrentSitePageId()
	{
		// if the current request has a portlet window id which matches a tool id in the site service,
		// use that tool's page as the current page id.
		ParameterParser params = (ParameterParser) m_currentService.getInThread(ParameterParser.class.getName());
		if (params != null)
		{
			// Note: matches the ActionURL.PARAM_PID
			String pid = params.getString("pid");
			if (pid != null)
			{
				ToolConfiguration tool = SiteService.findTool(pid);
				if (tool != null)
				{
					return tool.getContainingPage().getId();
				}
			}

			// if no tool id, try for a direct page id
			String pageId = params.getString("page");
			if (pageId != null)
			{
				return pageId;
			}
		}

		return null;

	}	// getCurrentSitePageId

	/**
	* Extract the tool id from the current request.
	* @return The tool id implied from the current request.
	*/
	public String getCurrentToolId()
	{
		// if the current request has a portlet window id which matches a tool id in the site service,
		// use that tool's id.
		ParameterParser params = (ParameterParser) m_currentService.getInThread(ParameterParser.class.getName());
		if (params != null)
		{
			// Note: matches the ActionURL.PARAM_PID
			String pid = params.getString("pid");
			if (pid != null)
			{
				ToolConfiguration tool = SiteService.findTool(pid);
				if (tool != null)
				{
					return tool.getId();
				}
			}
		}

		return null;

	}	// getCurrentToolId

	/**
	* Get the SessionState object for the tool in the current request.
	* @return The SessionState object for the tool in the current request.
	*/
	public SessionState getCurrentToolState()
	{
		// the tool, if present, is identified by the pid parameter
		ParameterParser params = (ParameterParser) m_currentService.getInThread(ParameterParser.class.getName());
		if (params != null)
		{
			String pid = params.getString("pid");
			if (pid != null)
			{
				return UsageSessionService.getSessionState(pid);
			}
		}

		return null;

	}	// getCurrentToolState

	/**
	* Format an identity string for the current request's site-tool location:
	* identifying the specific site, and if present, this particular tool in the site.
	* @return The current request's location id.
	*/
	public String getCurrentLocationId()
	{
		// use the tool id
		return getCurrentLocationId(getCurrentToolId());

	}	// getCurrentLocationId

	/**
	* Format an identity string for the current request's site-tool location:
	* identifying the specific site, and if specified, the id parameter in place of a tool in the site.
	* @param id The tool id to append, if any
	* @return The current request's location id.
	*/
	public String getCurrentLocationId(String id)
	{
		// TODO: could be more efficient, getting the site and page with one search... -ggolden
		// use the current site, page, and if present, the id as the tool
		return PresenceService.locationId(getCurrentSiteId(), getCurrentSitePageId(), id);

	}	// getCurrentLocationId

	/**
	* Format an identity string for the current request's client window:
	* identifying the session, the specific site, and if present, a particular tool in the site.
	* @return The current request's location id.
	*/
	public String getCurrentClientWindowId()
	{
		// start with this session's id
		String sessionId = UsageSessionService.getSessionId();
		if (sessionId == null)
		{
			sessionId = "";
		}

		// end with the locationId - site only!
//		String location = getCurrentLocationId();
		String location = PresenceService.locationId(getCurrentSiteId(), null, null);
		
		String rv = sessionId + location;
		return rv;

	}	// getCurrentClientWindowId

	/**
	* Format an identity string for the current request's client window:
	* identifying the session, the specific site, and if provided, modified by this particular id.
	* @param id The id modifier, skipped if null.
	* @return The current request's location id.
	*/
	public String getCurrentClientWindowId(String id)
	{
		// start with this session's id
		String sessionId = UsageSessionService.getSessionId();
		if (sessionId == null)
		{
			sessionId = "";
		}

		// end with the locationId - site only!
//		String location = getCurrentLocationId(id);
		String location = PresenceService.locationId(getCurrentSiteId(), null, null);

		String rv = sessionId + location;
		return rv;

	}	// getCurrentClientWindowId

	/**
	 * {@inheritDoc}
	 */
	public String getToolDocElementId(String toolId)
	{
		return "Main" + toolId;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getToolTitleElementId(String toolId)
	{
		return "Title" + toolId;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getPresenceElementId(String toolId)
	{
		return "presence";
	}

}	// BasicPortalService

/**********************************************************************************
*
* $Header: /cvs/sakai/framework-component/src/java/org/sakaiproject/component/framework/portal/BasicPortalService.java,v 1.18 2004/09/30 20:20:04 ggolden.umich.edu Exp $
*
**********************************************************************************/
