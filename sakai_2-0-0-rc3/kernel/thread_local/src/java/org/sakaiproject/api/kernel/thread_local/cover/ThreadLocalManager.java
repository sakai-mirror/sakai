/**********************************************************************************
 *
 * $Header: /cvs/sakai2/kernel/thread_local/src/java/org/sakaiproject/api/kernel/thread_local/cover/ThreadLocalManager.java,v 1.3 2005/04/01 02:44:05 ggolden.umich.edu Exp $
 *
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.sakaiproject.api.kernel.thread_local.cover;

import org.sakaiproject.api.kernel.component.cover.ComponentManager;

/**
 * <p>
 * ThreadLocalManager is a static Cover for the {@link org.sakaiproject.api.kernel.thread_local.ThreadLocalManager ThreadLocalManager}; see that interface for usage details.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.3 $
 */
public class ThreadLocalManager
{
	/**
	 * Access the component instance: special cover only method.
	 * 
	 * @return the component instance.
	 */
	public static org.sakaiproject.api.kernel.thread_local.ThreadLocalManager getInstance()
	{
		if (ComponentManager.CACHE_COMPONENTS)
		{
			if (m_instance == null)
					m_instance = (org.sakaiproject.api.kernel.thread_local.ThreadLocalManager) ComponentManager
							.get(org.sakaiproject.api.kernel.thread_local.ThreadLocalManager.class);
			return m_instance;
		}
		else
		{
			return (org.sakaiproject.api.kernel.thread_local.ThreadLocalManager) ComponentManager
					.get(org.sakaiproject.api.kernel.thread_local.ThreadLocalManager.class);
		}
	}

	private static org.sakaiproject.api.kernel.thread_local.ThreadLocalManager m_instance = null;

	public static void set(java.lang.String param0,
			java.lang.Object param1)
	{
		org.sakaiproject.api.kernel.thread_local.ThreadLocalManager manager = getInstance();
		if (manager == null) return;

		manager.set(param0, param1);
	}

	public static void clear()
	{
		org.sakaiproject.api.kernel.thread_local.ThreadLocalManager manager = getInstance();
		if (manager == null) return;

		manager.clear();
	}

	public static java.lang.Object get(java.lang.String param0)
	{
		org.sakaiproject.api.kernel.thread_local.ThreadLocalManager manager = getInstance();
		if (manager == null) return null;

		return manager.get(param0);
	}
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/kernel/thread_local/src/java/org/sakaiproject/api/kernel/thread_local/cover/ThreadLocalManager.java,v 1.3 2005/04/01 02:44:05 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
