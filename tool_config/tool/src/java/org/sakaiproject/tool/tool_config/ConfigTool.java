/**********************************************************************************
 *
 * $Header: /cvs/sakai2/tool_config/tool/src/java/org/sakaiproject/tool/tool_config/ConfigTool.java,v 1.3 2005/04/18 14:23:26 ggolden.umich.edu Exp $
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

package org.sakaiproject.tool.tool_config;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.kernel.session.SessionManager;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.api.kernel.tool.Placement;
import org.sakaiproject.api.kernel.tool.Tool;
import org.sakaiproject.api.kernel.tool.ToolManager;

/**
 * <p>
 * OptionsTool is a Sakai Helper tool that presents a tool placement options editor to the end user to edit a tool placement's options for a particular placement.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class ConfigTool
{
	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(ConfigTool.class);

	/** The tool session attribute suffix (after my tool id) used to pass the Tool object to configure between client and helper */
	public static final String ATTR_PLACEMENT = ".placement";

	/** Session manager (injected dependency) */
	protected SessionManager m_sessionManager = null;

	/** Tool manager (injected dependency) */
	protected ToolManager m_toolManager = null;

	/** The NameAndValue collection being edited. */
	protected Collection m_edit = null;

	/**
	 * no-arg constructor.
	 */
	public ConfigTool()
	{
		M_log.info("constructed");
	}

	/**
	 * Set the session manager.
	 * 
	 * @param mgr
	 *        The session manager.
	 */
	public void setSessionManager(SessionManager mgr)
	{
		m_sessionManager = mgr;
	}

	/**
	 * Set the tool manager.
	 * 
	 * @param mgr
	 *        The tool manager.
	 */
	public void setToolManager(ToolManager mgr)
	{
		m_toolManager = mgr;
	}

	/**
	 * Process the save command from the edit view.
	 * 
	 * @return navigation outcome:
	 */
	public String processActionSave()
	{
		if (M_log.isDebugEnabled()) M_log.debug("save");

		// save
		saveEdit();

		// go where we need to go when done
		goToDone();

		return "complete";
	}

	/**
	 * Process the cancel command from the edit view.
	 * 
	 * @return navigation outcome:
	 */
	public String processActionCancel()
	{
		if (M_log.isDebugEnabled()) M_log.debug("cancel");
		// cancel

		// go where we need to go when done
		goToDone();

		return "complete";
	}

	/**
	 * Access a collection of name,value options for the current configuration.
	 * 
	 * @return a collection of name,value options for the current configuration.
	 */
	public Collection getOptions()
	{
		// make sure we have this setup
		setupEdit();

		return m_edit;
	}

	/**
	 * Check that we are properly configured.
	 */
	protected void validate()
	{
		if (m_sessionManager == null)
		{
			M_log.warn("missing session manager: check managed-bean definition for a managed-property for 'sessionManager'");
		}

		if (m_toolManager == null)
		{
			M_log.warn("missing tool manager: check managed-bean definition for a managed-property for 'toolManager'");
		}
	}

	/**
	 * Cause the redirect to the done place.
	 */
	protected void goToDone()
	{
		// we are done with our edit
		m_edit = null;

		validate();

		ToolSession toolSession = m_sessionManager.getCurrentToolSession();
		Tool tool = m_toolManager.getCurrentTool();

		String url = (String) m_sessionManager.getCurrentToolSession().getAttribute(tool.getId() + Tool.HELPER_DONE_URL);

		try
		{
			FacesContext.getCurrentInstance().getExternalContext().redirect(url);
			FacesContext.getCurrentInstance().responseComplete();
		}
		catch (IOException e)
		{
			M_log.warn("IOException redirecting to: " + url);
		}
	}

	/**
	 * If we have not yet setup our m_config edit buffer, do so.
	 */
	protected void setupEdit()
	{
		if (m_edit != null) return;

		validate();

		Vector rv = new Vector();

		ToolSession toolSession = m_sessionManager.getCurrentToolSession();
		Tool me = m_toolManager.getCurrentTool();

		Placement placement = (Placement) toolSession.getAttribute(me.getId() + ATTR_PLACEMENT);
		Properties p = placement.getConfig();
		for (Enumeration e = p.propertyNames(); e.hasMoreElements();)
		{
			String name = (String) e.nextElement();
			String value = p.getProperty(name);
			rv.add(new NameAndValue(name, value));
		}

		m_edit = rv;
	}

	/**
	 * Save any changed values from the edit.
	 */
	protected void saveEdit()
	{
		validate();

		ToolSession toolSession = m_sessionManager.getCurrentToolSession();
		Tool me = m_toolManager.getCurrentTool();

		Placement placement = (Placement) toolSession.getAttribute(me.getId() + ATTR_PLACEMENT);
		Properties p = placement.getPlacementConfig();

		for (Iterator i = m_edit.iterator(); i.hasNext();)
		{
			NameAndValue edit = (NameAndValue) i.next();
			if (edit.isChanged())
			{
				if (edit.getValue().length() == 0)
				{
					p.remove(edit.getName());
				}
				else
				{
					p.setProperty(edit.getName(), edit.getValue());
				}
			}
		}
	}

	/**
	 * Represents a name value pair.
	 */
	public class NameAndValue
	{
		/** The name. */
		protected String m_name = null;

		/** The value. */
		protected String m_value = null;

		/** Was the value changed. */
		protected boolean m_changed = false;

		public NameAndValue(String name, String value)
		{
			m_name = name;
			m_value = value;
		}

		public String getName()
		{
			return m_name;
		}

		public String getValue()
		{
			return m_value;
		}

		public void setValue(String value)
		{
			if (!m_value.equals(value))
			{
				m_value = value;
				m_changed = true;
			}
		}

		public boolean isChanged()
		{
			return m_changed;
		}
	}
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/tool_config/tool/src/java/org/sakaiproject/tool/tool_config/ConfigTool.java,v 1.3 2005/04/18 14:23:26 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
