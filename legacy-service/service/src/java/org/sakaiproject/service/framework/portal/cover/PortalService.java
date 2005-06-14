/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/framework/portal/cover/PortalService.java,v 1.1 2005/05/12 15:45:36 ggolden.umich.edu Exp $
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

package org.sakaiproject.service.framework.portal.cover;

import org.sakaiproject.service.framework.component.cover.ComponentManager;

/**
* <p>PortalService is a static Cover for the {@link org.sakaiproject.service.framework.portal.PortalService PortalService};
* see that interface for usage details.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
*/
public class PortalService
{
	/**
	 * Access the component instance: special cover only method.
	 * @return the component instance.
	 */
	public static org.sakaiproject.service.framework.portal.PortalService getInstance()
	{
		if (ComponentManager.CACHE_SINGLETONS)
		{
			if (m_instance == null) m_instance = (org.sakaiproject.service.framework.portal.PortalService) ComponentManager.get(org.sakaiproject.service.framework.portal.PortalService.class);
			return m_instance;
		}
		else
		{
			return (org.sakaiproject.service.framework.portal.PortalService) ComponentManager.get(org.sakaiproject.service.framework.portal.PortalService.class);
		}
	}
	private static org.sakaiproject.service.framework.portal.PortalService m_instance = null;



	public static java.lang.String SERVICE_NAME = org.sakaiproject.service.framework.portal.PortalService.SERVICE_NAME;
	public static java.lang.String PREF_REFRESH = org.sakaiproject.service.framework.portal.PortalService.PREF_REFRESH;
	public static int PREF_REFRESH_SUPPRESS = org.sakaiproject.service.framework.portal.PortalService.PREF_REFRESH_SUPPRESS;

	public static java.lang.String getCurrentSiteId()
	{
		org.sakaiproject.service.framework.portal.PortalService service = getInstance();
		if (service == null)
			return null;

		return service.getCurrentSiteId();
	}

	public static java.lang.String getCurrentSitePageId()
	{
		org.sakaiproject.service.framework.portal.PortalService service = getInstance();
		if (service == null)
			return null;

		return service.getCurrentSitePageId();
	}

	public static java.lang.String getCurrentToolId()
	{
		org.sakaiproject.service.framework.portal.PortalService service = getInstance();
		if (service == null)
			return null;

		return service.getCurrentToolId();
	}

	public static org.sakaiproject.service.framework.session.SessionState getCurrentToolState()
	{
		org.sakaiproject.service.framework.portal.PortalService service = getInstance();
		if (service == null)
			return null;

		return service.getCurrentToolState();
	}

	public static java.lang.String getCurrentLocationId(java.lang.String param0)
	{
		org.sakaiproject.service.framework.portal.PortalService service = getInstance();
		if (service == null)
			return null;

		return service.getCurrentLocationId(param0);
	}

	public static java.lang.String getCurrentLocationId()
	{
		org.sakaiproject.service.framework.portal.PortalService service = getInstance();
		if (service == null)
			return null;

		return service.getCurrentLocationId();
	}

	public static java.lang.String getCurrentClientWindowId(java.lang.String param0)
	{
		org.sakaiproject.service.framework.portal.PortalService service = getInstance();
		if (service == null)
			return null;

		return service.getCurrentClientWindowId(param0);
	}

	public static java.lang.String getCurrentClientWindowId()
	{
		org.sakaiproject.service.framework.portal.PortalService service = getInstance();
		if (service == null)
			return null;

		return service.getCurrentClientWindowId();
	}

	public static String getToolDocElementId(String param0)
	{
		org.sakaiproject.service.framework.portal.PortalService service = getInstance();
		if (service == null)
			return null;

		return service.getToolDocElementId(param0);
	}

	public static String getToolTitleElementId(String param0)
	{
		org.sakaiproject.service.framework.portal.PortalService service = getInstance();
		if (service == null)
			return null;

		return service.getToolTitleElementId(param0);
	}

	public static String getPresenceElementId(String param0)
	{
		org.sakaiproject.service.framework.portal.PortalService service = getInstance();
		if (service == null)
			return null;

		return service.getPresenceElementId(param0);
	}

}

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/framework/portal/cover/PortalService.java,v 1.1 2005/05/12 15:45:36 ggolden.umich.edu Exp $
*
**********************************************************************************/
