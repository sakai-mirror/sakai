/**********************************************************************************
 *
 * $Header: /cvs/sakai2/admin-tools/preferences/src/java/org/sakaiproject/tool/preferences/AdminPrefsTool.java,v 1.1 2005/04/25 03:29:42 ggolden.umich.edu Exp $
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

package org.sakaiproject.tool.preferences;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.kernel.session.SessionManager;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.service.legacy.preference.PreferencesEdit;
import org.sakaiproject.service.legacy.preference.PreferencesService;
import org.sakaiproject.service.legacy.resource.ResourceProperties;
import org.sakaiproject.service.legacy.resource.ResourcePropertiesEdit;

/**
 * <p>
 * AdminPrefsTool is a Sakai Admin tool to view and edit anyone's preferences.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.1 $
 */
public class AdminPrefsTool
{
	/**
	 * Represents a name value pair in a keyed preferences set.
	 */
	public class KeyNameValue
	{
		/** Is this value a list?. */
		protected boolean m_isList = false;

		/** The key. */
		protected String m_key = null;

		/** The name. */
		protected String m_name = null;

		/** The original is this value a list?. */
		protected boolean m_origIsList = false;

		/** The original key. */
		protected String m_origKey = null;

		/** The original name. */
		protected String m_origName = null;

		/** The original value. */
		protected String m_origValue = null;

		/** The value. */
		protected String m_value = null;

		public KeyNameValue(String key, String name, String value, boolean isList)
		{
			m_key = key;
			m_origKey = key;
			m_name = name;
			m_origName = name;
			m_value = value;
			m_origValue = value;
			m_isList = isList;
			m_origIsList = isList;
		}

		public String getKey()
		{
			return m_key;
		}

		public String getName()
		{
			return m_name;
		}

		public String getOrigKey()
		{
			return m_origKey;
		}

		public String getOrigName()
		{
			return m_origName;
		}

		public String getOrigValue()
		{
			return m_origValue;
		}

		public String getValue()
		{
			return m_value;
		}

		public boolean isChanged()
		{
			return ((!m_name.equals(m_origName)) || (!m_value.equals(m_origValue)) || (!m_key.equals(m_origKey)) || (m_isList != m_origIsList));
		}

		public boolean isList()
		{
			return m_isList;
		}

		public boolean origIsList()
		{
			return m_origIsList;
		}

		public void setKey(String value)
		{
			if (!m_key.equals(value))
			{
				m_key = value;
			}
		}

		public void setList(boolean b)
		{
			m_isList = b;
		}

		public void setName(String value)
		{
			if (!m_name.equals(value))
			{
				m_name = value;
			}
		}

		public void setValue(String value)
		{
			if (!m_value.equals(value))
			{
				m_value = value;
			}
		}
	}

	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(AdminPrefsTool.class);

	/** The PreferencesEdit being worked on. */
	protected PreferencesEdit m_edit = null;

	/** Preferences service (injected dependency) */
	protected PreferencesService m_preferencesService = null;

	/** Session manager (injected dependency) */
	protected SessionManager m_sessionManager = null;

	/** The PreferencesEdit in KeyNameValue collection form. */
	protected Collection m_stuff = null;

	/** The user id (from the end user) to edit. */
	protected String m_userId = null;

	/**
	 * no-arg constructor.
	 */
	public AdminPrefsTool()
	{
		M_log.info("constructed");
	}

	/**
	 * Cancel the edit and cleanup.
	 */
	protected void cancelEdit()
	{
		// cancel
		m_preferencesService.cancel(m_edit);

		// cleanup
		m_stuff = null;
		m_edit = null;
	}

	/**
	 * Access the prefs being edited, in KeyNameValue collection form.
	 * 
	 * @return a collection of name,value options for the current configuration.
	 */
	public Collection getPreferences()
	{
		// make sure we have this setup
		setupEdit();

		return m_stuff;
	}

	/**
	 * Access the user id of the preferences being edited.
	 * 
	 * @return The user id of the preferences being edited.
	 */
	public String getUserId()
	{
		return m_userId;
	}

	/**
	 * Process the add command from the edit view.
	 * 
	 * @return navigation outcome:
	 */
	public String processActionAdd()
	{
		if (M_log.isDebugEnabled()) M_log.debug("save");

		// save
		m_stuff.add(new KeyNameValue("", "", "", false));

		return null;
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
		cancelEdit();

		return "list";
	}

	/**
	 * Process the edit command.
	 * 
	 * @return navigation outcome:
	 */
	public String processActionEdit()
	{
		if (M_log.isDebugEnabled()) M_log.debug("processActionEdit");

		if (getUserId() == null)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please enter a user id."));
			return null;
		}

		try
		{
			m_edit = m_preferencesService.edit(getUserId());
			return "edit";
		}
		catch (IdUnusedException e)
		{
			try
			{
				m_edit = m_preferencesService.add(getUserId());
				return "edit";
			}
			catch (Exception ee)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(ee.toString()));
				return null;
			}
		}
		catch (Exception e)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.toString()));
			return null;
		}
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

		return "list";
	}

	/**
	 * Save any changed values from the edit and cleanup.
	 */
	protected void saveEdit()
	{
		// move the stuff from m_stuff into the edit
		for (Iterator i = m_stuff.iterator(); i.hasNext();)
		{
			KeyNameValue knv = (KeyNameValue) i.next();
			if (knv.isChanged())
			{
				// find the original to remove (unless this one was new)
				if (!knv.getOrigKey().equals(""))
				{
					ResourcePropertiesEdit props = m_edit.getPropertiesEdit(knv.getOrigKey());
					props.removeProperty(knv.getOrigName());
				}

				// add the new if we have a key and name and value
				if ((!knv.getKey().equals("")) && (!knv.getName().equals("")) && (!knv.getValue().equals("")))
				{
					ResourcePropertiesEdit props = m_edit.getPropertiesEdit(knv.getKey());

					if (knv.isList())
					{
						// split by ", "
						String[] parts = knv.getValue().split(", ");
						for (int p = 0; p < parts.length; p++)
						{
							props.addPropertyToList(knv.getName(), parts[p]);
						}
					}
					else
					{
						props.addProperty(knv.getName(), knv.getValue());
					}
				}
			}
		}

		// save the preferences, release the edit
		m_preferencesService.commit(m_edit);

		m_stuff = null;
		m_edit = null;
	}

	/**
	 * Set the preferences service.
	 * 
	 * @param mgr
	 *        The preferences service.
	 */
	public void setPreferencesService(PreferencesService mgr)
	{
		m_preferencesService = mgr;
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
	 * If we have not yet setup our m_stuff edit buffer, do so.
	 */
	protected void setupEdit()
	{
		if (m_stuff != null) return;

		Vector rv = new Vector();

		if (m_edit != null)
		{
			Collection keys = m_edit.getKeys();
			for (Iterator i = keys.iterator(); i.hasNext();)
			{
				String key = (String) i.next();
				ResourceProperties props = m_edit.getProperties(key);
				for (Iterator names = props.getPropertyNames(); names.hasNext();)
				{
					String name = (String) names.next();
					String value = props.getPropertyFormatted(name);

					List values = props.getPropertyList(name);
					boolean isList = values.size() > 1;

					rv.add(new KeyNameValue(key, name, value, isList));
				}
			}
		}

		m_stuff = rv;
	}

	/**
	 * Set the user id to edit.
	 * 
	 * @param id
	 *        The user id to edit.
	 */
	public void setUserId(String id)
	{
		m_userId = id;
	}
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/admin-tools/preferences/src/java/org/sakaiproject/tool/preferences/AdminPrefsTool.java,v 1.1 2005/04/25 03:29:42 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
