/**********************************************************************************
 *
 * $Header: /cvs/sakai2/kernel/components-loader/src/java/org/sakaiproject/util/ComponentsLoader.java,v 1.4 2005/03/07 03:15:26 ggolden.umich.edu Exp $
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

package org.sakaiproject.util;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.sakaiproject.component.kernel.component.SpringCompMgr;
import org.sakaiproject.api.kernel.component.ComponentManager;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.FileSystemResource;

/**
 * <p>
 * Load the available Sakai components into the shared component manager's Spring ApplicationContext
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class ComponentsLoader implements
		org.sakaiproject.api.kernel.component.ComponentsLoader
{
	/** Our logger (log4j). */
	private static Logger M_log = Logger.getLogger(ComponentsLoader.class);

	public ComponentsLoader()
	{
	}

	/**
	 * 
	 */
	public void load(ComponentManager mgr, String componentsRoot)
	{
		try
		{
			// get the ComponentManager's AC - assuming this is a SpringCompMgr. If not, this will throw.
			ConfigurableApplicationContext ac = ((SpringCompMgr) mgr)
					.getApplicationContext();

			// get a list of the folders in the root
			File root = new File(componentsRoot);

			// make sure it's a dir.
			if (!root.isDirectory())
			{
				M_log.warn("load: root not directory: " + componentsRoot);
				return;
			}

			// what component packages are there?
			File[] packages = root.listFiles();
			if (packages == null)
			{
				M_log.warn("load: empty directory: "
						+ componentsRoot);
				return;
			}

			M_log.info("load: loading components from: " + componentsRoot);

			// process the packages
			for (int p = 0; p < packages.length; p++)
			{
				// if a valid components directory
				if (validComponentsPackage(packages[p]))
				{
					loadComponentPackage(packages[p], ac);
				}
				else
				{
					M_log.warn("load: skipping non-package entry: "
							+ packages[p]);
				}
			}
		}
		catch (Throwable t)
		{
			M_log.warn("load: exception: " + t);
		}
	}

	/**
	 * Load one component package into the AC
	 * 
	 * @param packageRoot
	 *        The file path to the component package
	 * @param ac
	 *        The ApplicationContext to load into
	 */
	protected void loadComponentPackage(File dir,
			ConfigurableApplicationContext ac)
	{
		// setup the classloader onto the thread
		ClassLoader current = Thread.currentThread().getContextClassLoader();
		ClassLoader loader = newPackageClassLoader(dir);

		M_log.info("loadComponentPackage: " + dir);

		Thread.currentThread().setContextClassLoader(loader);

		File xml = null;

		try
		{
			// load this xml file
			File webinf = new File(dir, "WEB-INF");
			xml = new File(webinf, "components.xml");

			// make a reader
			XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(
					(BeanDefinitionRegistry) ac.getBeanFactory());

			reader.loadBeanDefinitions(new FileSystemResource(xml
					.getCanonicalPath()));
		}
		catch (Throwable t)
		{
			M_log.warn("loadComponentPackage: exception loading: " + xml
					+ " : " + t);
		}
		finally
		{
			// restore the context loader
			Thread.currentThread().setContextClassLoader(current);
		}
	}

	/**
	 * Test if this File is a valid components package directory.
	 * 
	 * @param dir
	 *        The file to test
	 * @return true if it is a valid components package directory, false if not.
	 */
	protected boolean validComponentsPackage(File dir)
	{
		// valid if this is a directory with a WEB-INF directory below with a components.xml file
		if ((dir != null) && (dir.isDirectory()))
		{
			File webinf = new File(dir, "WEB-INF");
			if ((webinf != null) && (webinf.isDirectory()))
			{
				File xml = new File(webinf, "components.xml");
				if ((xml != null) && (xml.isFile()))
				{
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Create the class loader for this component package
	 * 
	 * @param dir
	 *        The package's root directory.
	 * @return A class loader, whose parent is this class's loader, which has the classes/ and jars for this component.
	 */
	protected ClassLoader newPackageClassLoader(File dir)
	{
		// collect as a List, turn into an array after
		List urls = new Vector();

		File webinf = new File(dir, "WEB-INF");

		// put classes/ on the classpath
		File classes = new File(webinf, "classes");
		if ((classes != null) && (classes.isDirectory()))
		{
			try
			{
				URL url = new URL("file:" + classes.getCanonicalPath() + "/");
				urls.add(url);
			}
			catch (Throwable t)
			{
			}
		}

		// put each .jar file onto the classpath
		File lib = new File(webinf, "lib");
		if ((lib != null) && (lib.isDirectory()))
		{
			File[] jars = lib.listFiles(new FileFilter()
			{
				public boolean accept(File file)
				{
					return (file.isFile() && file.getName().endsWith(".jar"));
				}
			});

			if (jars != null)
			{
				for (int j = 0; j < jars.length; j++)
				{
					try
					{
						URL url = new URL("file:" + jars[j].getCanonicalPath());
						urls.add(url);
					}
					catch (Throwable t)
					{
					}
				}
			}
		}

		// make the array from the list
		URL[] urlArray = (URL[]) urls.toArray(new URL[urls.size()]);

		// make the classloader - my loader is parent
		URLClassLoader loader = new URLClassLoader(urlArray, getClass()
				.getClassLoader());

		return loader;
	}
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/kernel/components-loader/src/java/org/sakaiproject/util/ComponentsLoader.java,v 1.4 2005/03/07 03:15:26 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
