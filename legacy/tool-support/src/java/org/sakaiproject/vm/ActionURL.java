/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.sakaiproject.vm;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
* <p>PortletActionURL provides a URL with settable and re-settable parameters based on a portlet window's
* ActionURL base URL.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public class ActionURL
{
	/** The parameter for portlet window id (pid). */
	public final static String PARAM_PID = "pid";

	/** The parameter for site. */
	public final static String PARAM_SITE = "site";

	/** The parameter for page. */
	public final static String PARAM_PAGE = "page";

	/** The parameter for paneld. */
	public final static String PARAM_PANEL = "panel";

	/** The base url to the portlet. */
	protected String m_base = null;

	/** parameters. */
	protected Map m_parameters = new Hashtable();

	/** The portlet window id, if any. */
	protected String m_pid = null;

	/** The panel, if any. */
	protected String m_panel = null;

	/** The site, if any. */
	protected String m_site = null;

	/** The site pge, if any. */
	protected String m_page = null;

	/**
	 * Construct with a base URL to the portlet, no parameters
	 * @param base The base URL
	 */
	public ActionURL(String base)
	{
		m_base = base;
	}

	/**
	 * "Reset" the URL by clearing the parameters.
	 * @return this.
	 */
	public ActionURL reset()
	{
		m_parameters = new Hashtable();
		return this;
	}

	/**
	 * Set or replace (or remove if value is null) a parameter
	 * @param name The parameter name.
	 * @param value The parameter value.
	 * @return this.
	 */
	public ActionURL setParameter(String name, String value)
	{
		if (value == null)
		{
			m_parameters.remove(name);
		}

		else
		{
			m_parameters.put(name, value);
		}

		return this;
	}

	/**
	 * Set or reset the pid.
	 * @param pid The portlet window id.
	 */
	public ActionURL setPid(String pid)
	{
		m_pid = pid;
		return this;
	}

	/**
	 * Set or reset the site.
	 * @param site The site id.
	 */
	public ActionURL setSite(String site)
	{
		m_site = site;
		return this;
	}

	/**
	 * Set or reset the page.
	 * @param page The page id.
	 */
	public ActionURL setPage(String page)
	{
		m_page = page;
		return this;
	}

	/**
	 * Set or reset the panel.
	 * @param panel The panel id.
	 */
	public ActionURL setPanel(String panel)
	{
		m_panel = panel;
		return this;
	}

	/**
	 * Reneder the URL with parameters
	 * @return The URL.
	 */
	public String toString()
	{
		String rv = m_base;
		char c = '?';
		if (m_parameters.size() > 0)
		{
			for (Iterator iEntries = m_parameters.entrySet().iterator(); iEntries.hasNext();)
			{
				Map.Entry entry = (Map.Entry) iEntries.next();
				rv = rv + c + entry.getKey() + "=" + entry.getValue();
				c = '&';
			}
		}

		// add the pid if defined and not overridden
		if ((m_pid != null) && (!m_parameters.containsKey(PARAM_PID)))
		{
			rv = rv + c + PARAM_PID + "=" + m_pid;
			c = '&';
		}

		// add the site if defined and not overridden
		if ((m_site != null) && (!m_parameters.containsKey(PARAM_SITE)))
		{
			rv = rv + c + PARAM_SITE + "=" + m_site;
			c = '&';
		}

		// add the page if defined and not overridden
		if ((m_page != null) && (!m_parameters.containsKey(PARAM_PAGE)))
		{
			rv = rv + c + PARAM_PAGE + "=" + m_page;
			c = '&';
		}

		// add the panel if defined and not overridden
		if ((m_panel != null) && (!m_parameters.containsKey(PARAM_PANEL)))
		{
			rv = rv + c + PARAM_PANEL + "=" + m_panel;
			c = '&';
		}

		reset();
		return rv;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj)
	{
		boolean equals = false;

		if ((obj != null) && (obj instanceof ActionURL))
		{
			equals = ((ActionURL) obj).toString().equals(toString());
		}

		return equals;
	}
}



