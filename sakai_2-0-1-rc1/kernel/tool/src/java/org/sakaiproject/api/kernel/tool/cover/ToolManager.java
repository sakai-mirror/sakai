/**********************************************************************************
 *
 * $Header: /cvs/sakai2/kernel/tool/src/java/org/sakaiproject/api/kernel/tool/cover/ToolManager.java,v 1.3 2005/04/18 14:23:34 ggolden.umich.edu Exp $
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

package org.sakaiproject.api.kernel.tool.cover;

import org.sakaiproject.api.kernel.component.cover.ComponentManager;

/**
 * <p>
 * IdManager is a static Cover for the {@link org.sakaiproject.api.kernel.id.IdManager IdManager}; see that interface for usage details.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class ToolManager
{
	/** Possibly cached component instance. */
	private static org.sakaiproject.api.kernel.tool.ToolManager m_instance = null;

	/**
	 * Access the component instance: special cover only method.
	 * 
	 * @return the component instance.
	 */
	public static org.sakaiproject.api.kernel.tool.ToolManager getInstance()
	{
		if (ComponentManager.CACHE_COMPONENTS)
		{
			if (m_instance == null)
					m_instance = (org.sakaiproject.api.kernel.tool.ToolManager) ComponentManager
							.get(org.sakaiproject.api.kernel.tool.ToolManager.class);
			return m_instance;
		}
		else
		{
			return (org.sakaiproject.api.kernel.tool.ToolManager) ComponentManager
					.get(org.sakaiproject.api.kernel.tool.ToolManager.class);
		}
	}

	public static void register(org.sakaiproject.api.kernel.tool.Tool param0)
	{
		org.sakaiproject.api.kernel.tool.ToolManager manager = getInstance();
		if (manager == null) return;

		manager.register(param0);
	}

	public static void register(org.w3c.dom.Document param0)
	{
		org.sakaiproject.api.kernel.tool.ToolManager manager = getInstance();
		if (manager == null) return;

		manager.register(param0);
	}

	public static void register(java.io.InputStream param0)
	{
		org.sakaiproject.api.kernel.tool.ToolManager manager = getInstance();
		if (manager == null) return;

		manager.register(param0);
	}

	public static void register(java.io.File param0)
	{
		org.sakaiproject.api.kernel.tool.ToolManager manager = getInstance();
		if (manager == null) return;

		manager.register(param0);
	}

	public static org.sakaiproject.api.kernel.tool.Tool getTool(java.lang.String param0)
	{
		org.sakaiproject.api.kernel.tool.ToolManager manager = getInstance();
		if (manager == null) return null;

		return manager.getTool(param0);
	}

	public static java.util.Set findTools(java.util.Set param0, java.util.Set param1)
	{
		org.sakaiproject.api.kernel.tool.ToolManager manager = getInstance();
		if (manager == null) return null;

		return manager.findTools(param0, param1);
	}

	public static org.sakaiproject.api.kernel.tool.Tool getCurrentTool()
	{
		org.sakaiproject.api.kernel.tool.ToolManager manager = getInstance();
		if (manager == null) return null;

		return manager.getCurrentTool();
	}

	public static org.sakaiproject.api.kernel.tool.Placement getCurrentPlacement()
	{
		org.sakaiproject.api.kernel.tool.ToolManager manager = getInstance();
		if (manager == null) return null;

		return manager.getCurrentPlacement();
	}
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/kernel/tool/src/java/org/sakaiproject/api/kernel/tool/cover/ToolManager.java,v 1.3 2005/04/18 14:23:34 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
