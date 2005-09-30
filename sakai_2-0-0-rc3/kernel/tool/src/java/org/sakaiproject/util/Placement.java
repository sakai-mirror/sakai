/**********************************************************************************
 *
 * $Header: /cvs/sakai2/kernel/tool/src/java/org/sakaiproject/util/Placement.java,v 1.4 2005/05/06 04:34:28 ggolden.umich.edu Exp $
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

import java.util.Properties;

import org.sakaiproject.api.kernel.tool.Tool;

/**
 * <p>
 * Placement is a utility class that implements the Placement interface.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.4 $
 */
public class Placement implements org.sakaiproject.api.kernel.tool.Placement
{

	/** Placement configuration Properties (mutable). */
	protected Properties m_config = new Properties();

	/** The placement context. */
	protected String m_context = null;

	/** The well known identifier string. */
	protected String m_id = null;

	/** The title string. */
	protected String m_title = null;

	/** Tool placed. */
	protected Tool m_tool = null;

	/**
	 * Construct
	 */
	public Placement()
	{
	}

	/**
	 * Construct.
	 * 
	 * @param id
	 *        The placement id.
	 * @param tool
	 *        The tool to place.
	 * @param config
	 *        The particular placement config Properties to use.
	 * @param context
	 *        The particular placement context to use.
	 * @param title
	 *        The tool placement title.
	 */
	public Placement(String id, Tool tool, Properties config, String context, String title)
	{
		m_id = id;
		m_tool = tool;
		if (config != null)
		{
			m_config = config;
		}
		m_context = context;
		m_title = title;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Placement))
		{
			return false;
		}

		return ((Placement) obj).getId().equals(getId());
	}

	/**
	 * {@inheritDoc}
	 */
	public Properties getConfig()
	{
		// the placement config overrides registered config
		Properties p = new Properties();

		// put the registered ones in, and do it first so that the placement can override
		if (m_tool != null)
		{
			p.putAll(m_tool.getRegisteredConfig());
		}

		// put the placement in
		p.putAll(getPlacementConfig());

		return p;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getContext()
	{
		return m_context;
	}

	/**
	 * @inheritDoc
	 */
	public String getId()
	{
		return m_id;
	}

	/**
	 * {@inheritDoc}
	 */
	public Properties getPlacementConfig()
	{
		return m_config;
	}

	/**
	 * @inheritDoc
	 */
	public String getTitle()
	{
		String rv = null;
		if (m_title != null)
		{
			rv = m_title;
		}
		else if (m_tool != null)
		{
			rv = m_tool.getTitle();
		}

		return rv;
	}

	/**
	 * @inheritDoc
	 */
	public Tool getTool()
	{
		return m_tool;
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode()
	{
		return getId().hashCode();
	}

	/**
	 * Set the context.
	 * 
	 * @param context
	 *        The context to set.
	 */
	public void setContext(String context)
	{
		m_context = context;
	}

	/**
	 * Set the id.
	 * 
	 * @param id
	 *        The id to set.
	 */
	public void setId(String id)
	{
		m_id = id;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setTitle(String title)
	{
		m_title = title;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setTool(Tool tool)
	{
		m_tool = tool;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void save()
	{
	}
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/kernel/tool/src/java/org/sakaiproject/util/Placement.java,v 1.4 2005/05/06 04:34:28 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
