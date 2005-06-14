/**********************************************************************************
 *
 * $Header: /cvs/sakai2/legacy/adaptor/src/java/org/sakaiproject/component/adaptor/ComponentManagerAdaptor.java,v 1.1 2005/04/04 13:33:56 ggolden.umich.edu Exp $
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

package org.sakaiproject.component.adaptor;

import java.io.InputStream;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.kernel.component.cover.ComponentManager;

/**
 * <p>
 * ComponentManagerAdaptor implements the Sakai1 ComponentManager interface as a (read-only) adaptor to Sakai2's ComponentManager API.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class ComponentManagerAdaptor implements org.sakaiproject.service.framework.component.ComponentManager
{
	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(ComponentManagerAdaptor.class);

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Dependencies and their setter methods
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Init and Destroy
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		M_log.info("init()");
	}

	/**
	 * Final cleanup.
	 */
	public void destroy()
	{
		M_log.info("destroy()");
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Work interface methods: ComponentManager
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * @inheritDoc
	 */
	public Object get(Class iface)
	{
		return ComponentManager.get(iface);
	}

	/**
	 * @inheritDoc
	 */
	public Object get(String ifaceName)
	{
		return ComponentManager.get(ifaceName);
	}

	/**
	 * @inheritDoc
	 */
	public boolean contains(Class iface)
	{
		return ComponentManager.contains(iface);
	}

	/**
	 * @inheritDoc
	 */
	public boolean contains(String ifaceName)
	{
		return ComponentManager.contains(ifaceName);
	}

	/**
	 * @inheritDoc
	 */
	public int getLifestyle(Class iface)
	{
		return LIFESTYLE_SINGLE;
	}

	/**
	 * @inheritDoc
	 */
	public Set getRegisteredInterfaces()
	{
		return ComponentManager.getRegisteredInterfaces();
	}

	/**
	 * @inheritDoc
	 */
	public Set getLocalRegisteredInterfaces()
	{
		return ComponentManager.getRegisteredInterfaces();
	}

	/**
	 * @inheritDoc
	 */
	public void unload()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @inheritDoc
	 */
	public void loadXmlFile(String filename)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @inheritDoc
	 */
	public void loadXmlStream(InputStream stream)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @inheritDoc
	 */
	public void loadComponent(Class iface, Object component)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @inheritDoc
	 */
	public void loadComponent(String ifaceName, Object component)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @inheritDoc
	 */
	public org.sakaiproject.service.framework.component.ComponentManager nestComponentManager()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @inheritDoc
	 */
	public boolean isReady()
	{
		return true;
	}

	/**
	 * @inheritDoc
	 */
	public void setReady(boolean ready)
	{
		throw new UnsupportedOperationException();
	}
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/legacy/adaptor/src/java/org/sakaiproject/component/adaptor/ComponentManagerAdaptor.java,v 1.1 2005/04/04 13:33:56 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
