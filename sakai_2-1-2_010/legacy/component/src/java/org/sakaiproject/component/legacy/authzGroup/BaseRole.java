/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/trunk/sakai/legacy/component/src/java/org/sakaiproject/component/legacy/authzGroup/BaseAuthzGroupService.java $
 * $Id: BaseAuthzGroupService.java 2454 2005-10-09 23:49:14Z ggolden@umich.edu $
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

// package
package org.sakaiproject.component.legacy.authzGroup;

// imports
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.service.legacy.authzGroup.AuthzGroup;
import org.sakaiproject.service.legacy.authzGroup.Role;
import org.sakaiproject.util.java.StringUtil;
import org.sakaiproject.util.xml.Xml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>
 * BaseRole is an implementation of the AuthzGroup API Role.
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public class BaseRole implements Role
{
	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(BaseRole.class);

	/** A fixed class serian number. */
	private static final long serialVersionUID = 1L;

	/** The role id. */
	protected String m_id = null;

	/** The locks that make up this. */
	protected Set m_locks = null;

	/** The role description. */
	protected String m_description = null;

	/** Active flag. */
	protected boolean m_active = false;

	/**
	 * Construct.
	 * 
	 * @param id
	 *        The role id.
	 */
	public BaseRole(String id)
	{
		m_id = id;
		m_locks = new HashSet();
	}

	/**
	 * Construct as a copy
	 * 
	 * @param id
	 *        The role id.
	 * @param other
	 *        The role to copy.
	 */
	public BaseRole(String id, Role other)
	{
		m_id = id;
		m_description = ((BaseRole) other).m_description;
		m_locks = new HashSet();
		m_locks.addAll(((BaseRole) other).m_locks);
	}

	/**
	 * Construct from information in XML.
	 * 
	 * @param el
	 *        The XML DOM Element definining the role.
	 */
	public BaseRole(Element el, AuthzGroup azGroup)
	{
		m_locks = new HashSet();
		m_id = StringUtil.trimToNull(el.getAttribute("id"));

		m_description = StringUtil.trimToNull(el.getAttribute("description"));
		if (m_description == null)
		{
			m_description = StringUtil.trimToNull(Xml.decodeAttribute(el, "description-enc"));
		}

		// the children (abilities)
		NodeList children = el.getChildNodes();
		final int length = children.getLength();
		for (int i = 0; i < length; i++)
		{
			Node child = children.item(i);
			if (child.getNodeType() != Node.ELEMENT_NODE) continue;
			Element element = (Element) child;

			// look for role | lock ability
			if (element.getTagName().equals("ability"))
			{
				String roleId = StringUtil.trimToNull(element.getAttribute("role"));
				String lock = StringUtil.trimToNull(element.getAttribute("lock"));

				if (roleId != null)
				{
					M_log.warn("(el): nested role: " + m_id + " " + roleId);
				}

				m_locks.add(lock);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Element toXml(Document doc, Stack stack)
	{
		Element role = doc.createElement("role");

		if (stack.isEmpty())
		{
			doc.appendChild(role);
		}
		else
		{
			((Element) stack.peek()).appendChild(role);
		}

		stack.push(role);

		role.setAttribute("id", getId());

		// encode the description
		if (m_description != null) Xml.encodeAttribute(role, "description-enc", m_description);

		// locks
		for (Iterator a = m_locks.iterator(); a.hasNext();)
		{
			String lock = (String) a.next();

			Element element = doc.createElement("ability");
			role.appendChild(element);
			element.setAttribute("lock", lock);
		}

		stack.pop();

		return role;
	}

	/**
	 * Enable editing.
	 */
	protected void activate()
	{
		m_active = true;

	} // activate

	/**
	 * Check to see if the azGroup is still active, or has already been closed.
	 * 
	 * @return true if the azGroup is active, false if it's been closed.
	 */
	public boolean isActiveEdit()
	{
		return m_active;
	}

	/**
	 * Close the azGroup object - it cannot be used after this.
	 */
	protected void closeEdit()
	{
		m_active = false;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getId()
	{
		return m_id;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDescription()
	{
		return m_description;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isAllowed(String lock)
	{
		return m_locks.contains(lock);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set getAllowedFunctions()
	{
		Set rv = new HashSet();
		rv.addAll(m_locks);
		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDescription(String description)
	{
		m_description = StringUtil.trimToNull(description);
	}

	/**
	 * {@inheritDoc}
	 */
	public void allowFunction(String lock)
	{
		m_locks.add(lock);
	}

	/**
	 * {@inheritDoc}
	 */
	public void allowFunctions(Collection locks)
	{
		m_locks.addAll(locks);
	}

	/**
	 * {@inheritDoc}
	 */
	public void disallowFunction(String lock)
	{
		m_locks.remove(lock);
	}

	/**
	 * {@inheritDoc}
	 */
	public void disallowFunctions(Collection locks)
	{
		m_locks.removeAll(locks);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean allowsNoFunctions()
	{
		return m_locks.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	public void disallowAll()
	{
		m_locks.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(Object obj)
	{
		if (!(obj instanceof Role)) throw new ClassCastException();

		// if the object are the same, say so
		if (obj == this) return 0;

		// sort based on (unique) id
		int compare = getId().compareTo(((Role) obj).getId());

		return compare;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Role)) return false;

		return ((Role) obj).getId().equals(getId());
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode()
	{
		return getId().hashCode();
	}
}
