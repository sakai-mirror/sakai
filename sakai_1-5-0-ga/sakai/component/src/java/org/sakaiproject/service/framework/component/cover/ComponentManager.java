/**********************************************************************************
*
* $Header: /cvs/sakai/component/src/java/org/sakaiproject/service/framework/component/cover/ComponentManager.java,v 1.9 2004/07/30 03:20:12 janderse.umich.edu Exp $
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

package org.sakaiproject.service.framework.component.cover;

import java.io.InputStream;
import java.util.Set;

import org.sakaiproject.component.framework.component.SpringCompMgr;

/**
 * <p>ComponentManager is a static Cover for the {@link org.sakaiproject.service.framework.component.ComponentManager Component Manager};
 * see that interface for usage details.</p>
 * <p>This cover is special.  As a cover for the component manager, it cannot use the component manager to find the instance.
 * Instead, this is where a static single-instance singleton ComponentManger of a particular type is created.</p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.9 $
 */
public class ComponentManager
{
	/** A component manager - use the Spring based one. */
	private static org.sakaiproject.service.framework.component.ComponentManager m_componentManager = new SpringCompMgr(null);

	/** Whether the static Cover classes should cache the singleton
	 * they operate on.  If false, every call to a Cover class
	 * will result in a get() on the ComponentManager (inefficient!!)
	 * THIS SHOULD BE SET TO TRUE FOR THE PRODUCTION ENVIRONMENT!
	 */
	public static final boolean CACHE_SINGLETONS = true;
	public static final boolean CACHE_MESSAGE_SERVICES = true;
	
	/**
	 * Access the component manager of the single instance.
	 * @return The ComponentManager.
	 */
	public static org.sakaiproject.service.framework.component.ComponentManager getInstance()
	{
		return m_componentManager;
	}

	public static final int LIFESTYLE_UNKNOWN = org.sakaiproject.service.framework.component.ComponentManager.LIFESTYLE_UNKNOWN;

	public static final int LIFESTYLE_SINGLE = org.sakaiproject.service.framework.component.ComponentManager.LIFESTYLE_SINGLE;

	public static final int LIFESTYLE_MANY = org.sakaiproject.service.framework.component.ComponentManager.LIFESTYLE_MANY;

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

	public static int getLifestyle(Class iface)
	{
		return getInstance().getLifestyle(iface);
	}

	public static Set getRegisteredInterfaces()
	{
		return getInstance().getRegisteredInterfaces();
	}

	public static Set getLocalRegisteredInterfaces()
	{
		return getInstance().getLocalRegisteredInterfaces();
	}

	public static void unload()
	{
		getInstance().unload();
	}

	public static void loadXmlFile(String filename)
	{
		getInstance().loadXmlFile(filename);
	}

	public static void loadXmlStream(InputStream stream)
	{
		getInstance().loadXmlStream(stream);
	}

	public static void loadComponent(Class iface, Object component)
	{
		getInstance().loadComponent(iface, component);
	}

	public static void loadComponent(String ifaceName, Object component)
	{
		getInstance().loadComponent(ifaceName, component);
	}

	public static org.sakaiproject.service.framework.component.ComponentManager nestComponentManager()
	{
		return getInstance().nestComponentManager();
	}

	public static boolean isReady()
	{
		return getInstance().isReady();
	}

	public static void setReady(boolean ready)
	{
		getInstance().setReady(ready);
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/component/src/java/org/sakaiproject/service/framework/component/cover/ComponentManager.java,v 1.9 2004/07/30 03:20:12 janderse.umich.edu Exp $
*
**********************************************************************************/
