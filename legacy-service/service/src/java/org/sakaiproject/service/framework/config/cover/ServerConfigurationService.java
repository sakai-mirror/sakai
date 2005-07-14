/**********************************************************************************
* $URL$
* $Id$
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

package org.sakaiproject.service.framework.config.cover;

import org.sakaiproject.service.framework.component.cover.ComponentManager;

/**
* <p>ServerConfigurationService is a static Cover for the {@link org.sakaiproject.service.framework.config.ServerConfigurationService ServerConfigurationService};
* see that interface for usage details.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
*/
public class ServerConfigurationService
{
	/**
	 * Access the component instance: special cover only method.
	 * @return the component instance.
	 */
	public static org.sakaiproject.service.framework.config.ServerConfigurationService getInstance()
	{
		if (ComponentManager.CACHE_SINGLETONS)
		{
			if (m_instance == null) m_instance = (org.sakaiproject.service.framework.config.ServerConfigurationService) ComponentManager.get(org.sakaiproject.service.framework.config.ServerConfigurationService.class);
			return m_instance;
		}
		else
		{
			return (org.sakaiproject.service.framework.config.ServerConfigurationService) ComponentManager.get(org.sakaiproject.service.framework.config.ServerConfigurationService.class);
		}
	}
	private static org.sakaiproject.service.framework.config.ServerConfigurationService m_instance = null;


	public static java.lang.String SERVICE_NAME = org.sakaiproject.service.framework.config.ServerConfigurationService.SERVICE_NAME;

	public static java.lang.String getServerId()
	{
		org.sakaiproject.service.framework.config.ServerConfigurationService service = getInstance();
		if (service == null)
			return null;

		return service.getServerId();
	}

	public static java.lang.String getServerInstance()
	{
		org.sakaiproject.service.framework.config.ServerConfigurationService service = getInstance();
		if (service == null)
			return null;

		return service.getServerInstance();
	}

	public static java.lang.String getServerIdInstance()
	{
		org.sakaiproject.service.framework.config.ServerConfigurationService service = getInstance();
		if (service == null)
			return null;

		return service.getServerIdInstance();
	}

	public static java.lang.String getServerName()
	{
		org.sakaiproject.service.framework.config.ServerConfigurationService service = getInstance();
		if (service == null)
			return null;

		return service.getServerName();
	}

	public static java.lang.String getServerUrl()
	{
		org.sakaiproject.service.framework.config.ServerConfigurationService service = getInstance();
		if (service == null)
			return null;

		return service.getServerUrl();
	}

	public static java.lang.String getAccessUrl()
	{
		org.sakaiproject.service.framework.config.ServerConfigurationService service = getInstance();
		if (service == null)
			return null;

		return service.getAccessUrl();
	}

	public static java.lang.String getHelpUrl(java.lang.String param0)
	{
		org.sakaiproject.service.framework.config.ServerConfigurationService service = getInstance();
		if (service == null)
			return null;

		return service.getHelpUrl(param0);
	}

	public static java.lang.String getPortalUrl()
	{
		org.sakaiproject.service.framework.config.ServerConfigurationService service = getInstance();
		if (service == null)
			return null;

		return service.getPortalUrl();
	}

	public static java.lang.String getGatewaySiteId()
	{
		org.sakaiproject.service.framework.config.ServerConfigurationService service = getInstance();
		if (service == null)
			return null;

		return service.getGatewaySiteId();
	}

	public static java.lang.String getLoginUrl()
	{
		org.sakaiproject.service.framework.config.ServerConfigurationService service = getInstance();
		if (service == null)
			return null;

		return service.getLoginUrl();
	}

	public static java.lang.String getLoggedOutUrl()
	{
		org.sakaiproject.service.framework.config.ServerConfigurationService service = getInstance();
		if (service == null)
			return null;

		return service.getLoggedOutUrl();
	}

	public static java.lang.String getUserHomeUrl()
	{
		org.sakaiproject.service.framework.config.ServerConfigurationService service = getInstance();
		if (service == null)
			return null;

		return service.getUserHomeUrl();
	}

	public static java.lang.String getSakaiHomePath()
	{
		org.sakaiproject.service.framework.config.ServerConfigurationService service = getInstance();
		if (service == null)
			return null;

		return service.getSakaiHomePath();
	}

	public static java.util.List getLocks()
	{
		org.sakaiproject.service.framework.config.ServerConfigurationService service = getInstance();
		if (service == null)
			return null;

		return service.getLocks();
	}

	public static java.util.List getResourceServices()
	{
		org.sakaiproject.service.framework.config.ServerConfigurationService service = getInstance();
		if (service == null)
			return null;

		return service.getResourceServices();
	}

	public static boolean getBoolean(java.lang.String param0, boolean param1)
	{
		org.sakaiproject.service.framework.config.ServerConfigurationService service = getInstance();
		if (service == null)
			return false;

		return service.getBoolean(param0, param1);
	}

	public static java.lang.String getString(java.lang.String param0, java.lang.String param1)
	{
		org.sakaiproject.service.framework.config.ServerConfigurationService service = getInstance();
		if (service == null)
			return null;

		return service.getString(param0, param1);
	}

	public static java.lang.String getString(java.lang.String param0)
	{
		org.sakaiproject.service.framework.config.ServerConfigurationService service = getInstance();
		if (service == null)
			return null;

		return service.getString(param0);
	}

	public static java.lang.String[] getStrings(java.lang.String param0)
	{
		org.sakaiproject.service.framework.config.ServerConfigurationService service = getInstance();
		if (service == null)
			return null;

		return service.getStrings(param0);
	}

	public static java.util.List getToolOrder(java.lang.String param0)
	{
		org.sakaiproject.service.framework.config.ServerConfigurationService service = getInstance();
		if (service == null)
			return null;

		return service.getToolOrder(param0);
	}

	public static java.util.List getToolsRequired(java.lang.String param0)
	{
		org.sakaiproject.service.framework.config.ServerConfigurationService service = getInstance();
		if (service == null)
			return null;

		return service.getToolsRequired(param0);
	}

	public static int getInt(java.lang.String param0, int param1)
	{
		org.sakaiproject.service.framework.config.ServerConfigurationService service = getInstance();
		if (service == null)
			return 0;

		return service.getInt(param0, param1);
	}

	public static void registerResourceService(org.sakaiproject.service.legacy.resource.ResourceService param0)
	{
		org.sakaiproject.service.framework.config.ServerConfigurationService service = getInstance();
		if (service == null)
			return;

		service.registerResourceService(param0);
	}
}



