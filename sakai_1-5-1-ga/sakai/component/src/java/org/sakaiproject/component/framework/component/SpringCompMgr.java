/**********************************************************************************
*
* $Header: /cvs/sakai/component/src/java/org/sakaiproject/component/framework/component/SpringCompMgr.java,v 1.14 2004/09/24 19:10:47 ggolden.umich.edu Exp $
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

package org.sakaiproject.component.framework.component;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.sakaiproject.service.framework.component.ComponentManager;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;

/**
 * <p>SpringCompMgr manages service implementation components using the Springframework bean factory.</p>
 * <p>See the {@link org.sakaiproject.service.framework.component.ComponentManager} interface for details.</p>
 * <p>A {@link SakaiBeanFactory SakaiBeanFactory} is used to handle non-root-wise hierarchy extensions,
 * along with a Spring XML reader, and locally tracked "loaded" (i.e. already constructed) components.</p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.14 $
 */
public class SpringCompMgr implements ComponentManager
{
	/** class wide synchronization lock. */
	protected static Object lock = new Object();

	/** The bean factory. */
	protected SakaiBeanFactory m_components = null;

	/** The XML registration file reader. */
	protected XmlBeanDefinitionReader m_reader = null;

	/** The already created components given to manage (their interface names). */
	protected Set m_loadedComponents = new HashSet();

	/** Ready flag. */
	protected boolean m_ready = false;

	/** Parent ComponentManager (or null if this is the top). */
	protected ComponentManager m_parent = null;

	/**
	 * Initialize.
	 * @param parent A ComponentManager in which this one gets nested, or NULL if this is this top one.
	 */
	public SpringCompMgr(ComponentManager parent)
	{
		if (parent == null)
		{
			// construct with no parent
			m_components = new SakaiBeanFactory();
		}
		else
		{
			m_parent = parent;

			// construct with the parent's factory (it MUST be a SpringCompMgr) as parent
			m_components = new SakaiBeanFactory(((SpringCompMgr) parent).m_components);
		}

		m_reader = new XmlBeanDefinitionReader(m_components);
	}

	/**
	 * Finalize.
	 */
	protected void finalize()
	{
		synchronized (lock)
		{
			// remove any constructed singletons
			m_components.destroySingletons();
		}

		// TODO: remove from my parent?
	}

	/**
	 * {@inheritDoc}
	 */
	public Object get(Class iface)
	{
		Object component = null;

		synchronized (lock)
		{
			try
			{
				component = m_components.getBean(iface.getName(), iface);
			}
			catch (Throwable t)
			{
				// TODO: keep this? or be quite.  Can't use a logger by the component manager (i.e. me)
				System.out.println(this +".get(" + iface.getName() + "): " + t);
			}
		}
		return component;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object get(String ifaceName)
	{
		Object component = null;

		synchronized (lock)
		{
			try
			{
				component = m_components.getBean(ifaceName);
			}
			catch (Throwable t)
			{
				// TODO: keep this? or be quite.  Can't use a logger by the component manager (i.e. me)
				System.out.println(this +".get(" + ifaceName + "): " + t);
			}
		}
		return component;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean contains(Class iface)
	{
		boolean found = m_components.containsBeanDefinition(iface.getName());

		return found;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean contains(String ifaceName)
	{
		boolean found = m_components.containsBeanDefinition(ifaceName);

		return found;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getLifestyle(Class iface)
	{
		int rv = LIFESTYLE_UNKNOWN;

		try
		{
			if (m_components.isSingleton(iface.getName()))
			{
				rv = LIFESTYLE_SINGLE;
			}
			else
			{
				rv = LIFESTYLE_MANY;
			}
		}
		catch (Throwable ignore)
		{
		}

		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set getRegisteredInterfaces()
	{
		Set rv = new HashSet();

		// get the registered ones
		String[] names = m_components.getBeanDefinitionNames();
		for (int i = 0; i < names.length; i++)
		{
			rv.add(names[i]);
		}

		// add the loaded ones
		for (Iterator iLoaded = m_loadedComponents.iterator(); iLoaded.hasNext();)
		{
			String loaded = (String) iLoaded.next();
			rv.add(loaded);
		}

		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set getLocalRegisteredInterfaces()
	{
		Set rv = new HashSet();

		// get the registered ones
		String[] names = m_components.getLocalBeanDefinitionNames();
		for (int i = 0; i < names.length; i++)
		{
			rv.add(names[i]);
		}

		// add the loaded ones
		for (Iterator iLoaded = m_loadedComponents.iterator(); iLoaded.hasNext();)
		{
			String loaded = (String) iLoaded.next();
			rv.add(loaded);
		}

		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public void unload()
	{
		synchronized (lock)
		{
			// TODO: unload only some, those of a particular webapp
			m_components.destroySingletons();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void loadXmlFile(String filename)
	{
		m_reader.loadBeanDefinitions(new FileSystemResource(filename));
	}

	/**
	 * {@inheritDoc}
	 */
	public void loadXmlStream(InputStream stream)
	{
		m_reader.loadBeanDefinitions(new InputStreamResource(stream, "XML"));
	}

	/**
	 * {@inheritDoc}
	 */
	public void loadComponent(Class iface, Object component)
	{
		// Spring doesn't list these in getBeanDefinitionNames, so we keep track
		m_loadedComponents.add(iface.getName());

		m_components.registerSingleton(iface.getName(), component);
	}

	/**
	 * {@inheritDoc}
	 */
	public void loadComponent(String ifaceName, Object component)
	{
		// Spring doesn't list these in getBeanDefinitionNames, so we keep track
		m_loadedComponents.add(ifaceName);

		m_components.registerSingleton(ifaceName, component);
	}

	/**
	 * {@inheritDoc}
	 */
	public ComponentManager nestComponentManager()
	{
		// create a new one nested (Spring-wise).  This nesting is the reverse of what we need! -ggolden
		ComponentManager mgr = new SpringCompMgr(this);

		return mgr;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isReady()
	{
		// use the setting at the very top only
		if (m_parent == null)
		{
			return m_ready;
		}

		return m_parent.isReady();
	}

	/**
	 * {@inheritDoc}
	 */
	public void setReady(boolean ready)
	{
		// set at the very top only
		if (m_parent == null)
		{
			m_ready = ready;
		}
		else
		{
			m_parent.setReady(ready);
		}
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/component/src/java/org/sakaiproject/component/framework/component/SpringCompMgr.java,v 1.14 2004/09/24 19:10:47 ggolden.umich.edu Exp $
*
**********************************************************************************/
