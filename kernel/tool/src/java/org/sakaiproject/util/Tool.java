/**********************************************************************************
 *
 * $Header: /cvs/sakai2/kernel/tool/src/java/org/sakaiproject/util/Tool.java,v 1.8 2005/05/26 20:19:27 ggolden.umich.edu Exp $
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

import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * <p>
 * Tool is a utility class that implements the Tool interface.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class Tool implements org.sakaiproject.api.kernel.tool.Tool, Comparable
{
	/** The set of categories. */
	protected Set m_categories = new HashSet();

	/** The description string. */
	protected String m_description = null;

	/** The well known identifier string. */
	protected String m_id = null;

	/** The set of keywords. */
	protected Set m_keywords = new HashSet();

	/** The configuration properties. */
	protected Properties m_registeredConfig = new Properties();

	/** The title string. */
	protected String m_title = null;

	/** The access security. */
	protected Tool.AccessSecurity m_accessSecurity = Tool.AccessSecurity.PORTAL;

	/**
	 * Construct
	 */
	public Tool()
	{
	}

	/**
	 * @inheritDoc
	 */
	public int compareTo(Object obj)
	{
		// let it throw a class case exception if the obj is not some sort of Tool
		org.sakaiproject.api.kernel.tool.Tool tool = (org.sakaiproject.api.kernel.tool.Tool) obj;

		// do an id based
		return getId().compareTo(tool.getId());
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Tool))
		{
			return false;
		}

		return ((Tool) obj).getId().equals(getId());
	}

	/**
	 * @inheritDoc
	 */
	public Tool.AccessSecurity getAccessSecurity()
	{
		return m_accessSecurity;
	}

	/**
	 * @inheritDoc
	 */
	public Set getCategories()
	{
		return Collections.unmodifiableSet(m_categories);
	}

	/**
	 * @inheritDoc
	 */
	public String getDescription()
	{
		return m_description;
	}

	/**
	 * @inheritDoc
	 */
	public String getId()
	{
		return m_id;
	}

	/**
	 * @inheritDoc
	 */
	public Set getKeywords()
	{
		return Collections.unmodifiableSet(m_keywords);
	}

	/**
	 * @inheritDoc
	 */
	public Properties getRegisteredConfig()
	{
		// to make this read only, make a new properties with this as the defaults
		//return new Properties(m_registeredConfig);
		Properties rv = new Properties();
		rv.putAll(m_registeredConfig);
		return rv;
	}

	/**
	 * @inheritDoc
	 */
	public String getTitle()
	{
		return m_title;
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode()
	{
		return getId().hashCode();
	}

	/**
	 * Set the access security.
	 * 
	 * @param access
	 *        The new access security setting.
	 */
	public void setAccessSecurity(Tool.AccessSecurity access)
	{
		m_accessSecurity = access;
	}

	/**
	 * Set the categories.
	 * 
	 * @param categories
	 *        The new categories set (Strings).
	 */
	public void setCategories(Set categories)
	{
		m_categories = categories;
	}

	/**
	 * Set the description.
	 * 
	 * @param description
	 *        The description to set.
	 */
	public void setDescription(String description)
	{
		m_description = description;
	}

	/**
	 * Set the id.
	 * 
	 * @param m_id
	 *        The m_id to set.
	 */
	public void setId(String id)
	{
		m_id = id;
	}

	/**
	 * Set the keywords.
	 * 
	 * @param keywords
	 *        The new keywords set (Strings).
	 */
	public void setKeywords(Set keywords)
	{
		m_keywords = keywords;
	}

	/**
	 * Set the registered configuration.
	 * 
	 * @param config
	 *        The new registered configuration Properties.
	 */
	public void setRegisteredConfig(Properties config)
	{
		m_registeredConfig = config;
		if (m_registeredConfig == null)
		{
			m_registeredConfig = new Properties();
		}
	}

	/**
	 * Set the title.
	 * 
	 * @param title
	 *        The title to set.
	 */
	public void setTitle(String title)
	{
		m_title = title;
	}
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/kernel/tool/src/java/org/sakaiproject/util/Tool.java,v 1.8 2005/05/26 20:19:27 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
