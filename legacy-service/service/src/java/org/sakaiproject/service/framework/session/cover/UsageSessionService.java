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

package org.sakaiproject.service.framework.session.cover;

import org.sakaiproject.service.framework.component.cover.ComponentManager;

/**
* <p>UsageSessionService is a static Cover for the {@link org.sakaiproject.service.framework.session.UsageSessionService UsageSessionService};
* see that interface for usage details.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
*/
public class UsageSessionService
{
	/**
	 * Access the component instance: special cover only method.
	 * @return the component instance.
	 */
	public static org.sakaiproject.service.framework.session.UsageSessionService getInstance()
	{
		if (ComponentManager.CACHE_SINGLETONS)
		{
			if (m_instance == null) m_instance = (org.sakaiproject.service.framework.session.UsageSessionService) ComponentManager.get(org.sakaiproject.service.framework.session.UsageSessionService.class);
			return m_instance;
		}
		else
		{
			return (org.sakaiproject.service.framework.session.UsageSessionService) ComponentManager.get(org.sakaiproject.service.framework.session.UsageSessionService.class);
		}
	}
	private static org.sakaiproject.service.framework.session.UsageSessionService m_instance = null;

	public static java.lang.String EVENT_LOGIN = org.sakaiproject.service.framework.session.UsageSessionService.EVENT_LOGIN;
	public static java.lang.String EVENT_LOGOUT = org.sakaiproject.service.framework.session.UsageSessionService.EVENT_LOGOUT;
	public static java.lang.String SAKAI_SESSION_COOKIE = org.sakaiproject.service.framework.session.UsageSessionService.SAKAI_SESSION_COOKIE;
	public static java.lang.String USAGE_SESSION_KEY = org.sakaiproject.service.framework.session.UsageSessionService.USAGE_SESSION_KEY;

	
	public static org.sakaiproject.service.framework.session.UsageSession startSession(java.lang.String param0, java.lang.String param1, java.lang.String param2)
	{
		org.sakaiproject.service.framework.session.UsageSessionService service = getInstance();
		if (service == null)
			return null;

		return service.startSession(param0, param1, param2);
	}

	public static org.sakaiproject.service.framework.session.UsageSession closeSession()
	{
		org.sakaiproject.service.framework.session.UsageSessionService service = getInstance();
		if (service == null)
			return null;

		return service.closeSession();
	}

	public static org.sakaiproject.service.framework.session.UsageSession getSession(java.lang.String param0)
	{
		org.sakaiproject.service.framework.session.UsageSessionService service = getInstance();
		if (service == null)
			return null;

		return service.getSession(param0);
	}

	public static org.sakaiproject.service.framework.session.UsageSession getSession()
	{
		org.sakaiproject.service.framework.session.UsageSessionService service = getInstance();
		if (service == null)
			return null;

		return service.getSession();
	}

	public static java.lang.String getSessionUserId()
	{
		org.sakaiproject.service.framework.session.UsageSessionService service = getInstance();
		if (service == null)
			return null;

		return service.getSessionUserId();
	}

	public static java.lang.String getSessionId()
	{
		org.sakaiproject.service.framework.session.UsageSessionService service = getInstance();
		if (service == null)
			return null;

		return service.getSessionId();
	}

	public static org.sakaiproject.service.framework.session.SessionState getSessionState(java.lang.String param0)
	{
		org.sakaiproject.service.framework.session.UsageSessionService service = getInstance();
		if (service == null)
			return null;

		return service.getSessionState(param0);
	}

	public static org.sakaiproject.service.framework.session.UsageSession setSessionActive(boolean param0)
	{
		org.sakaiproject.service.framework.session.UsageSessionService service = getInstance();
		if (service == null)
			return null;

		return service.setSessionActive(param0);
	}

	public static java.util.List getSessions(java.lang.String param0, java.lang.Object[] param1)
	{
		org.sakaiproject.service.framework.session.UsageSessionService service = getInstance();
		if (service == null)
			return null;

		return service.getSessions(param0, param1);
	}

	public static java.util.List getSessions(java.util.List param0)
	{
		org.sakaiproject.service.framework.session.UsageSessionService service = getInstance();
		if (service == null)
			return null;

		return service.getSessions(param0);
	}

	public static int getSessionInactiveTimeout()
	{
		org.sakaiproject.service.framework.session.UsageSessionService service = getInstance();
		if (service == null)
			return 0;

		return service.getSessionInactiveTimeout();
	}

	public static int getSessionLostTimeout()
	{
		org.sakaiproject.service.framework.session.UsageSessionService service = getInstance();
		if (service == null)
			return 0;

		return service.getSessionLostTimeout();
	}

	public static java.util.List getOpenSessions()
	{
		org.sakaiproject.service.framework.session.UsageSessionService service = getInstance();
		if (service == null)
			return null;

		return service.getOpenSessions();
	}

	public static java.util.Map getOpenSessionsByServer()
	{
		org.sakaiproject.service.framework.session.UsageSessionService service = getInstance();
		if (service == null)
			return null;

		return service.getOpenSessionsByServer();
	}
}



