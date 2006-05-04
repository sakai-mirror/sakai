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

import java.io.IOException;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.kernel.component.cover.ComponentManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;

/**
 * <p>
 * Sakai's extension to Spring's ContextLoader - adds the location of the ComponentManager shared AC, linking the local AC to it as parent, and loading localy hosted components into shared.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class ContextLoader extends
		org.springframework.web.context.ContextLoader
{
	/** Our logger. */
	private static Log M_log = LogFactory.getLog(ContextLoader.class);

	/** Name of servlet context parameter that can specify the config location for loading into the shared component set. */
	public static final String SHARED_LOCATION_PARAM = "contextSharedLocation";

	/**
	 * Initialize the local ApplicationContext, link it to the shared context, and load shared definitions into the shared context.
	 * 
	 * @param servletContext
	 *        current servlet context
	 * @return the new WebApplicationContext
	 * @throws BeansException
	 *         if the context couldn't be initialized
	 */
	public WebApplicationContext initWebApplicationContext(
			ServletContext servletContext) throws BeansException
	{
		WebApplicationContext rv = super
				.initWebApplicationContext(servletContext);

		// if we have a parent and any shared bean definitions, load them into the parent
		ConfigurableApplicationContext parent = (ConfigurableApplicationContext) rv
				.getParent();
		if (parent != null)
		{
			String sharedConfig = servletContext
					.getInitParameter(SHARED_LOCATION_PARAM);
			if (sharedConfig != null)
			{
				String[] locations = StringUtils
						.tokenizeToStringArray(
								sharedConfig,
								ConfigurableWebApplicationContext.CONFIG_LOCATION_DELIMITERS);
				if (locations != null)
				{
					XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(
							(BeanDefinitionRegistry) parent.getBeanFactory());

					for (int i = 0; i < locations.length; i++)
					{
						try
						{
							reader.loadBeanDefinitions(rv
									.getResources(locations[i]));
						}
						catch (IOException e)
						{
							M_log.warn("exception loading into parent: " + e);
						}
					}
				}
			}
		}

		return rv;
	}

	/**
	 * Access the shared ApplicationContext
	 * 
	 * @param servletContext
	 *        (not used)
	 * @return The shared application context
	 */
	protected ApplicationContext loadParentContext(ServletContext servletContext)
			throws BeansException
	{
		// get the component manager (we know it's a SpringCompMgr) and from that the shared AC
		ConfigurableApplicationContext sharedAc = ((SpringCompMgr) ComponentManager
				.getInstance()).getApplicationContext();

		return sharedAc;
	}
}



