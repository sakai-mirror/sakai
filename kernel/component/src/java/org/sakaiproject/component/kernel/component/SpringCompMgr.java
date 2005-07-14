/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
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

package org.sakaiproject.component.kernel.component;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.kernel.component.ComponentManager;
import org.sakaiproject.api.kernel.component.ComponentsLoader;
import org.sakaiproject.util.PropertyOverrideConfigurer;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

/**
 * <p>
 * SpringCompMgr manages API implementation components using the Springframework ApplicationContext.
 * </p>
 * <p>
 * See the {@link org.sakaiproject.api.kernel.component.ComponentManager}interface for details.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class SpringCompMgr implements ComponentManager
{
	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(SpringCompMgr.class);

	/** The Spring Application Context. */
	protected ConfigurableApplicationContext m_ac = null;

	/** The already created components given to manage (their interface names). */
	protected Set m_loadedComponents = new HashSet();

	/** A count of the # of child AC's that call us parent. */
	protected int m_childCount = 0;

	/**
	 * Initialize.
	 * 
	 * @param parent
	 *        A ComponentManager in which this one gets nested, or NULL if this is this top one.
	 */
	public SpringCompMgr(ComponentManager parent)
	{
		// Note: don't init here, init after it's fully constructed
		// (and if it's being constructed by the cover, after the cover has set it's instance variable).
		// othewise when singletons are instantiated, if they call a Cover or Discovery in the init(),
		// the component manager cover will not yet have this object! -ggolden
	}

	/**
	 * Initialize the component manager.
	 */
public void init()
	{
		if (m_ac != null) return;

		// create the AC, so that it loads these beans
		m_ac = new ClassPathXmlApplicationContext("org/sakaiproject/config/shared_components.xml");

		// load component packages
		loadComponents();

		// find a path to sakai files on the app server - if not set, set it
		String path = System.getProperty("sakai.home");
		if (path == null)
		{
			String catalina = getCatalina();
			if (catalina != null)
			{
				path = catalina + "/sakai/";
			}
		}

		// strange case...
		if (path == null)
		{
			path = "/usr/local/sakai/";
		}
		if (!path.endsWith("/")) path = path + "/";

		// make sure it's set properly
		System.setProperty("sakai.home", path);
		
		// check for the security home
		String securityPath = System.getProperty("sakai.security");
		if (securityPath != null)
		{
			// make sure it's properly slashed
			if (!securityPath.endsWith("/")) securityPath = securityPath + "/";
			System.setProperty("sakai.security", securityPath);
		}

		// post process the definitions from components with override property settings
		// - these get injected into the beans
		// - we run sakai.properties first, then local.properties, so that values defined in local.properties can override those in sakai.properties.
		try
		{
			File f = new File(path + "sakai.properties");
			if (f.exists())
			{
				PropertyOverrideConfigurer pushProcessor = new PropertyOverrideConfigurer();
				pushProcessor.setLocation(new FileSystemResource(f));
				pushProcessor.setIgnoreInvalidKeys(true);
				pushProcessor.postProcessBeanFactory(m_ac.getBeanFactory());

				promotePropertiesToSystem(pushProcessor);
			}
		}
		catch (Throwable t)
		{
			M_log.warn(t.toString());
		}

		try
		{
			File f = new File(path + "local.properties");
			if (f.exists())
			{
				PropertyOverrideConfigurer pushProcessor = new PropertyOverrideConfigurer();
				pushProcessor.setLocation(new FileSystemResource(f));
				pushProcessor.setIgnoreInvalidKeys(true);
				pushProcessor.postProcessBeanFactory(m_ac.getBeanFactory());

				promotePropertiesToSystem(pushProcessor);
			}
		}
		catch (Throwable t)
		{
			M_log.warn(t.toString());
		}

		try
		{
			File f = new File(securityPath + "security.properties");
			if (f.exists())
			{
				PropertyOverrideConfigurer pushProcessor = new PropertyOverrideConfigurer();
				pushProcessor.setLocation(new FileSystemResource(f));
				pushProcessor.setIgnoreInvalidKeys(true);
				pushProcessor.postProcessBeanFactory(m_ac.getBeanFactory());
				
				promotePropertiesToSystem(pushProcessor);
			}
		}
		catch (Throwable t)
		{
			M_log.warn(t.toString());
		}

		// post process the definitions from components (now overridden with our property files) to satisfy any placeholder values
		// - only one file is run, so it must be a complet set of values
		// - if the placeholder.properties file exists in the sakai.home, we run that,
		// otherwise we use the file we distribute in the class path.
		boolean done = false;
		try
		{
			File f = new File(path + "placeholder.properties");
			if (f.exists())
			{
				PropertyPlaceholderConfigurer pullProcessor = new PropertyPlaceholderConfigurer();
				pullProcessor.setLocation(new FileSystemResource(f));
				pullProcessor.postProcessBeanFactory(m_ac.getBeanFactory());
				done = true;
			}
		}
		catch (Throwable t)
		{
			M_log.warn(t.toString());
		}

		if (!done)
		{
			try
			{
				PropertyPlaceholderConfigurer pullProcessor = new PropertyPlaceholderConfigurer();
				pullProcessor.setLocation(new ClassPathResource("org/sakaiproject/config/placeholder.properties"));
				pullProcessor.postProcessBeanFactory(m_ac.getBeanFactory());
			}
			catch (Throwable t)
			{
				M_log.warn(t.toString());
			}
		}

		try
		{
			// get the singletons loaded
			m_ac.getBeanFactory().preInstantiateSingletons();
		}
		catch (Throwable t)
		{
			M_log.warn(t.toString());
		}
	}
	/**
	 * Access the ApplicationContext
	 * 
	 * @return the ApplicationContext
	 */
	public ConfigurableApplicationContext getApplicationContext()
	{
		return m_ac;
	}

	/**
	 * Finalize.
	 */
	protected void finalize()
	{
		close();
	}

	/**
	 * {@inheritDoc}
	 */
	public Object get(Class iface)
	{
		Object component = null;

		try
		{
			component = m_ac.getBean(iface.getName(), iface);
		}
		catch (Throwable t)
		{
			M_log.warn("get(" + iface.getName() + "): " + t);
		}

		return component;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object get(String ifaceName)
	{
		Object component = null;

		try
		{
			component = m_ac.getBean(ifaceName);
		}
		catch (Throwable t)
		{
			M_log.warn("get(" + ifaceName + "): " + t);
		}

		return component;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean contains(Class iface)
	{
		boolean found = m_ac.containsBeanDefinition(iface.getName());

		return found;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean contains(String ifaceName)
	{
		boolean found = m_ac.containsBeanDefinition(ifaceName);

		return found;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set getRegisteredInterfaces()
	{
		Set rv = new HashSet();

		// get the registered ones
		String[] names = m_ac.getBeanDefinitionNames();
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
	public void close()
	{
		m_ac.close();
	}

	/**
	 * {@inheritDoc}
	 */
	public void loadComponent(Class iface, Object component)
	{
		// Spring doesn't list these in getBeanDefinitionNames, so we keep track
		m_loadedComponents.add(iface.getName());

		m_ac.getBeanFactory().registerSingleton(iface.getName(), component);
	}

	/**
	 * {@inheritDoc}
	 */
	public void loadComponent(String ifaceName, Object component)
	{
		// Spring doesn't list these in getBeanDefinitionNames, so we keep track
		m_loadedComponents.add(ifaceName);

		m_ac.getBeanFactory().registerSingleton(ifaceName, component);
	}

	/**
	 * Locate the component loader, and load any available components.
	 */
	protected void loadComponents()
	{
		// see if we can find a components loader
		ComponentsLoader loader = null;

		// TODO: configure this?
		String loaderClassName = "org.sakaiproject.util.ComponentsLoader";

		// first see if it can be located via the thread class loader
		try
		{
			loader = (ComponentsLoader) Thread.currentThread().getContextClassLoader().loadClass(loaderClassName).newInstance();
		}
		catch (Throwable any)
		{
		}

		// next try this class's loader
		if (loader == null)
		{
			try
			{
				loader = (ComponentsLoader) getClass().getClassLoader().loadClass(loaderClassName).newInstance();
			}
			catch (Throwable any)
			{
			}
		}

		// if we found the class
		if (loader != null)
		{
			// locate the components root
			// if we have our system property set, use it
			String componentsRoot = System.getProperty("sakai.components.root");
			if (componentsRoot == null)
			{
				// if we are in Catalina, place it at ${catalina.home}/components/
				String catalina = getCatalina();
				if (catalina != null)
				{
					componentsRoot = catalina + "/components/";
				}
			}

			if (componentsRoot == null)
			{
				M_log.warn("loadComponents: cannot estabish a root directory for the components packages");
				return;
			}

			// load components
			loader.load(this, componentsRoot);
		}

		else
		{
			M_log.warn("loadComponents: no component loader class found");
		}
	}

	/**
	 * Increment the count of ACs that call this one parent.
	 */
	public synchronized void addChildAc()
	{
		m_childCount++;
	}

	/**
	 * Decrement the count of ACs that call this one parent. When we reach 0,
	 */
	public synchronized void removeChildAc()
	{
		m_childCount--;

		// when we get back to 0, close our AC
		if (m_childCount == 0)
		{
			close();
		}
	}

	/**
	 * Check the environment for catalina's base or home directory.
	 * 
	 * @return Catalina's base or home directory.
	 */
	protected String getCatalina()
	{
		String catalina = System.getProperty("catalina.base");
		if (catalina == null)
		{
			catalina = System.getProperty("catalina.home");
		}

		return catalina;
	}

	/**
	 * If the properties has any of the values we need to set as sakai system properties, set them.
	 * @param props The property override configurer with some override settings.
	 */
	protected void promotePropertiesToSystem(PropertyOverrideConfigurer props)
	{
		String serverId = props.getValue("serverId");
		if (serverId != null)
		{
			System.setProperty("sakai.serverId", serverId);
		}
		
		String uploadMax = props.getValue("content.upload.max");
		if (uploadMax != null)
		{
			System.setProperty("sakai.content.upload.max", uploadMax);
		}
	}
}



