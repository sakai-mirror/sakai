/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
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

package org.sakaiproject.api.kernel.component.cover;

import java.util.Set;

import org.sakaiproject.component.kernel.component.SpringCompMgr;

/**
 * <p>
 * ComponentManager is a static Cover for the {@link org.sakaiproject.api.kernel.component.ComponentManager Component Manager}; see that interface for usage details.
 * </p>
 * <p>
 * This cover is special. As a cover for the component manager, it cannot use the component manager to find the instance. Instead, this is where a static single-instance singleton ComponentManger of a particular type is created.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class ComponentManager
{
	/** A component manager - use the Spring based one. */
	private static org.sakaiproject.api.kernel.component.ComponentManager m_componentManager = null;

	static
	{
		// construct and store first, so that in init() covers can be called -ggolden
		m_componentManager = new SpringCompMgr(null);
		((SpringCompMgr) m_componentManager).init();
	}

	/** If true, covers will cache the components they find once - good for production, bad for some unit testing. */
	public static final boolean CACHE_COMPONENTS = true;

	/**
	 * Access the component manager of the single instance.
	 * 
	 * @return The ComponentManager.
	 */
	public static org.sakaiproject.api.kernel.component.ComponentManager getInstance()
	{
		return m_componentManager;
	}

	public static Object get(Class iface)
	{
		return getInstance().get(iface);
	}

	public static Object get(String ifaceName)
	{
		return getInstance().get(ifaceName);
	}

	public static boolean contains(Class iface)
	{
		return getInstance().contains(iface);
	}

	public static boolean contains(String ifaceName)
	{
		return getInstance().contains(ifaceName);
	}

	public static Set getRegisteredInterfaces()
	{
		return getInstance().getRegisteredInterfaces();
	}

	public static void loadComponent(Class iface, Object component)
	{
		getInstance().loadComponent(iface, component);
	}

	public static void loadComponent(String ifaceName, Object component)
	{
		getInstance().loadComponent(ifaceName, component);
	}

	public static void close()
	{
		getInstance().close();
	}

	public static java.util.Properties getConfig()
	{
		return getInstance().getConfig();
	}

}



