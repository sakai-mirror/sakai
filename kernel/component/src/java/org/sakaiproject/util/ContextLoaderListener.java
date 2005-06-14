/**********************************************************************************
 *
 * $Header: /cvs/sakai2/kernel/component/src/java/org/sakaiproject/util/ContextLoaderListener.java,v 1.4 2005/03/07 03:15:26 ggolden.umich.edu Exp $
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

package org.sakaiproject.util;

import javax.servlet.ServletContextEvent;

import org.sakaiproject.component.kernel.component.ContextLoader;
import org.sakaiproject.component.kernel.component.SpringCompMgr;
import org.sakaiproject.api.kernel.component.cover.ComponentManager;

/**
 * <p>
 * Sakai's extension to the Spring ContextLoaderListener - use our ContextLoader, and increment / decrement the child count of the ComponentManager on init / destroy.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.4 $
 */
public class ContextLoaderListener extends
		org.springframework.web.context.ContextLoaderListener
{
	/**
	 * Create the ContextLoader to use. Can be overridden in subclasses.
	 * 
	 * @return the new ContextLoader
	 */
	protected org.springframework.web.context.ContextLoader createContextLoader()
	{
		return new ContextLoader();
	}

	/**
	 * Initialize the root web application context.
	 */
	public void contextInitialized(ServletContextEvent event)
	{
		super.contextInitialized(event);

		// increment the count of children for the component manager
		((SpringCompMgr) ComponentManager.getInstance()).addChildAc();
	}

	/**
	 * Close the root web application context.
	 */
	public void contextDestroyed(ServletContextEvent event)
	{
		super.contextDestroyed(event);

		// decrement the count of children for the component manager
		((SpringCompMgr) ComponentManager.getInstance()).removeChildAc();
	}
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/kernel/component/src/java/org/sakaiproject/util/ContextLoaderListener.java,v 1.4 2005/03/07 03:15:26 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
