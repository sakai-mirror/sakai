/**********************************************************************************
 *
 * $Header: /cvs/sakai2/kernel/session/src/java/org/sakaiproject/api/kernel/session/cover/SessionManager.java,v 1.7 2005/05/30 03:39:09 ggolden.umich.edu Exp $
 *
 ***********************************************************************************
 *
 * Copyright (c) 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.sakaiproject.api.kernel.session.cover;

import org.sakaiproject.api.kernel.component.cover.ComponentManager;

/**
 * <p>
 * UsageSessionManager is a static Cover for the {@link org.sakaiproject.api.kernel.session.UsageSessionManager UsageSessionManager}; see that interface for usage details.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.7 $
 */
public class SessionManager
{
	/** Possibly cached component instance. */
	private static org.sakaiproject.api.kernel.session.SessionManager m_instance = null;

	/**
	 * Access the component instance: special cover only method.
	 * 
	 * @return the component instance.
	 */
	public static org.sakaiproject.api.kernel.session.SessionManager getInstance()
	{
		if (ComponentManager.CACHE_COMPONENTS)
		{
			if (m_instance == null)
			{
				m_instance = (org.sakaiproject.api.kernel.session.SessionManager) ComponentManager
						.get(org.sakaiproject.api.kernel.session.SessionManager.class);
			}
			return m_instance;
		}
		else
		{
			return (org.sakaiproject.api.kernel.session.SessionManager) ComponentManager
					.get(org.sakaiproject.api.kernel.session.SessionManager.class);
		}
	}

	public static org.sakaiproject.api.kernel.session.Session startSession()
	{
		org.sakaiproject.api.kernel.session.SessionManager manager = getInstance();
		if (manager == null) return null;

		return manager.startSession();
	}

	public static org.sakaiproject.api.kernel.session.Session startSession(java.lang.String param0)
	{
		org.sakaiproject.api.kernel.session.SessionManager manager = getInstance();
		if (manager == null) return null;

		return manager.startSession(param0);
	}

	public static org.sakaiproject.api.kernel.session.Session getSession(java.lang.String param0)
	{
		org.sakaiproject.api.kernel.session.SessionManager manager = getInstance();
		if (manager == null) return null;

		return manager.getSession(param0);
	}

	public static org.sakaiproject.api.kernel.session.Session getCurrentSession()
	{
		org.sakaiproject.api.kernel.session.SessionManager manager = getInstance();
		if (manager == null) return null;

		return manager.getCurrentSession();
	}

	public static org.sakaiproject.api.kernel.session.ToolSession getCurrentToolSession()
	{
		org.sakaiproject.api.kernel.session.SessionManager manager = getInstance();
		if (manager == null) return null;

		return manager.getCurrentToolSession();
	}

	public static void setCurrentSession(org.sakaiproject.api.kernel.session.Session param0)
	{
		org.sakaiproject.api.kernel.session.SessionManager manager = getInstance();
		if (manager == null) return;

		manager.setCurrentSession(param0);
	}

	public static void setCurrentToolSession(org.sakaiproject.api.kernel.session.ToolSession param0)
	{
		org.sakaiproject.api.kernel.session.SessionManager manager = getInstance();
		if (manager == null) return;

		manager.setCurrentToolSession(param0);
	}
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/kernel/session/src/java/org/sakaiproject/api/kernel/session/cover/SessionManager.java,v 1.7 2005/05/30 03:39:09 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
