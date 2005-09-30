/**********************************************************************************
 *
 * $Header: /cvs/sakai2/kernel/tool-registration/src/java/org/sakaiproject/util/ToolListener.java,v 1.2 2005/03/10 03:49:41 ggolden.umich.edu Exp $
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

import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.kernel.tool.cover.ActiveToolManager;

/**
 * <p>
 * Sakai's extension to the Spring ContextLoaderListener - use our ContextLoader, and increment / decrement the child count of the ComponentManager on init / destroy.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.2 $
 */
public class ToolListener implements ServletContextListener
{
	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(ToolListener.class);

	/**
	 * Initialize.
	 */
	public void contextInitialized(ServletContextEvent event)
	{
		// find the resources in the webapp in the /tools/ area TODO: param this
		Set paths = event.getServletContext().getResourcePaths("/tools/");
		if (paths == null) return;

		for (Iterator i = paths.iterator(); i.hasNext();)
		{
			String path = (String) i.next();

			// skip directories
			if (path.endsWith("/")) continue;

			// load this
			M_log.info("registering tools from resource: " + path);
			ActiveToolManager.register(event.getServletContext().getResourceAsStream(path), event.getServletContext());
		}
	}

	/**
	 * Destroy.
	 */
	public void contextDestroyed(ServletContextEvent event)
	{
	}
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/kernel/tool-registration/src/java/org/sakaiproject/util/ToolListener.java,v 1.2 2005/03/10 03:49:41 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
