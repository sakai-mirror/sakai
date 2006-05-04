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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.service.legacy.authzGroup.AuthzGroup;
import org.sakaiproject.service.legacy.authzGroup.Member;
import org.sakaiproject.service.legacy.authzGroup.Role;
import org.sakaiproject.service.legacy.authzGroup.cover.AuthzGroupService;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.entity.ResourceProperties;
import org.sakaiproject.service.legacy.entity.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.time.cover.TimeService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.sakaiproject.util.java.StringUtil;
import org.sakaiproject.util.resource.BaseResourceProperties;
import org.sakaiproject.util.resource.BaseResourcePropertiesEdit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>
 * BaseAuthzGroup is an implementation of the AuthGroup API AuthzGroup.
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public class BaseAuthzGroup implements AuthzGroup
{
	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(BaseAuthzGroup.class);

	/** A fixed class serian number. */
	private static final long serialVersionUID = 1L;

	/** The internal 'db' key. */
	protected Integer m_key = null;

	/** The azGroup id. */
	protected String m_id = null;

	/** The properties. */
	protected ResourcePropertiesEdit m_properties = null;

	/** Map of userId to Member */
	protected Map m_userGrants = null;

	/** Map of Role id to a Role defined in this AuthzGroup. */
	protected Map m_roles = null;

	/** The external azGroup id, or null if not defined. */
	protected String m_providerRealmId = null;

	/** The role to use for maintain users. */
	protected String m_maintainRole = null;

	/** The created user id. */
	protected String m_createdUserId = null;

	/** The last modified user id. */
	protected String m_lastModifiedUserId = null;

	/** The time created. */
	protected Time m_createdTime = null;

	/** The time last modified. */
	protected Time m_lastModifiedTime = null;

	/** Set while the azGroup is not fully loaded from the storage. */
	protected boolean m_lazy = false;

	/** The event code for this azGroup. */
	protected String m_event = null;

	/** Active flag. */
	protected boolean m_active = false;

	/** True if created by the "new" call rather than "add" - it has not yet been stored. */
	protected boolean m_isNew = false;

	/**
	 * Construct.
	 * 
	 * @param id
	 *        The azGroup id.
	 */
	public BaseAuthzGroup(String id)
	{
		m_id = id;

		// setup for properties
		ResourcePropertiesEdit props = new BaseResourcePropertiesEdit();
		m_properties = props;

		m_userGrants = new HashMap();
		m_roles = new HashMap();

		// if the id is not null (a new azGroup, rather than a reconstruction)
		// add the automatic (live) properties
		if (m_id != null) ((BaseAuthzGroupService) (AuthzGroupService.getInstance())).addLiveProperties(this);
	}

	/**
	 * Construct from another AuthzGroup object.
	 * 
	 * @param azGroup
	 *        The azGroup object to use for values.
	 */
	public BaseAuthzGroup(AuthzGroup azGroup)
	{
		setAll(azGroup);
	}

	/**
	 * (Re)Construct from parts.
	 * 
	 * @param dbid
	 *        The database id.
	 * @param id
	 *        The azGroup id.
	 * @param providerId
	 *        The provider id.
	 * @param maintainRole
	 *        The maintain role id.
	 * @param createdBy
	 *        The user created by id.
	 * @param createdOn
	 *        The time created.
	 * @param modifiedBy
	 *        The user modified by id.
	 * @param modifiedOn
	 *        The time modified.
	 */
	public BaseAuthzGroup(Integer dbid, String id, String providerId, String maintainRole, String createdBy, Time createdOn,
			String modifiedBy, Time modifiedOn)
	{
		// setup for properties
		ResourcePropertiesEdit props = new BaseResourcePropertiesEdit();
		m_properties = props;

		m_userGrants = new HashMap();
		m_roles = new HashMap();

		m_key = dbid;
		m_id = id;
		m_providerRealmId = StringUtil.trimToNull(providerId);
		m_maintainRole = StringUtil.trimToNull(maintainRole);

		m_createdUserId = createdBy;
		m_lastModifiedUserId = modifiedBy;
		m_createdTime = createdOn;
		m_lastModifiedTime = modifiedOn;

		// setup for properties, but mark them lazy since we have not yet established them from data
		((BaseResourcePropertiesEdit) m_properties).setLazy(true);

		m_lazy = true;
	}

	/**
	 * Construct from information in XML.
	 * 
	 * @param el
	 *        The XML DOM Element definining the azGroup.
	 */
	public BaseAuthzGroup(Element el)
	{
		m_userGrants = new HashMap();
		m_roles = new HashMap();

		// setup for properties
		m_properties = new BaseResourcePropertiesEdit();

		m_id = StringUtil.trimToNull(el.getAttribute("id"));
		m_providerRealmId = StringUtil.trimToNull(el.getAttribute("provider-id"));
		m_maintainRole = StringUtil.trimToNull(el.getAttribute("maintain-role"));

		m_createdUserId = StringUtil.trimToNull(el.getAttribute("created-id"));
		m_lastModifiedUserId = StringUtil.trimToNull(el.getAttribute("modified-id"));

		String time = StringUtil.trimToNull(el.getAttribute("created-time"));
		if (time != null)
		{
			m_createdTime = TimeService.newTimeGmt(time);
		}

		time = StringUtil.trimToNull(el.getAttribute("modified-time"));
		if (time != null)
		{
			m_lastModifiedTime = TimeService.newTimeGmt(time);
		}

		// process the children (properties, grants, abilities, roles)
		NodeList children = el.getChildNodes();
		final int length = children.getLength();
		for (int i = 0; i < length; i++)
		{
			Node child = children.item(i);
			if (child.getNodeType() != Node.ELEMENT_NODE) continue;
			Element element = (Element) child;

			// look for properties
			if (element.getTagName().equals("properties"))
			{
				// re-create properties
				m_properties = new BaseResourcePropertiesEdit(element);
			}

			// look for a role
			else if (element.getTagName().equals("role"))
			{
				BaseRole role = new BaseRole(element, this);
				m_roles.put(role.getId(), role);
			}

			// process a grant
			else if (element.getTagName().equals("grant"))
			{
				String userId = StringUtil.trimToNullLower(element.getAttribute("user"));
				String roleId = StringUtil.trimToNull(element.getAttribute("role"));
				String active = StringUtil.trimToNull(element.getAttribute("active"));
				String provided = StringUtil.trimToNull(element.getAttribute("provided"));

				// record this user - role grant - just use the first one
				BaseRole role = (BaseRole) m_roles.get(roleId);
				if (role != null)
				{
					// if already granted, update to point to the role with the most permissions
					BaseMember grant = (BaseMember) m_userGrants.get(userId);
					if (grant != null)
					{
						if (role.m_locks.size() > ((BaseRole) grant.role).m_locks.size())
						{
							M_log.warn("(el): additional lesser user grant ignored: " + m_id + " " + userId + " "
									+ grant.role.getId() + " keeping: " + roleId);
							grant.role = role;
						}
						else
						{
							M_log.warn("(el): additional lesser user grant ignored: " + m_id + " " + userId + " " + roleId
									+ " keeping: " + grant.role.getId());
						}
					}
					else
					{
						grant = new BaseMember(role, Boolean.valueOf(active).booleanValue(), Boolean.valueOf(provided)
								.booleanValue(), userId);
						m_userGrants.put(userId, grant);
					}
				}
				else
				{
					M_log.warn("(el): role null: " + roleId);
				}
			}

			// look for user - [ Role | lock ] ability (the old way, pre 1.23)
			else if (element.getTagName().equals("ability"))
			{
				String userId = StringUtil.trimToNullLower(element.getAttribute("user"));
				String roleId = StringUtil.trimToNull(element.getAttribute("role"));
				String lock = StringUtil.trimToNull(element.getAttribute("lock"));
				String anon = StringUtil.trimToNull(element.getAttribute("anon"));
				String auth = StringUtil.trimToNull(element.getAttribute("auth"));

				// old way anon was stored
				// add the lock to the anon role definition
				if (anon != null)
				{
					if (roleId != null)
					{
						// the old pubview was done this way, we handle it so no need for warning
						if (!("pubview".equals(roleId)))
						{
							M_log.warn("(el) role for anon: " + m_id + " " + roleId);
						}
					}

					if (lock != null)
					{
						BaseRole role = (BaseRole) m_roles.get(AuthzGroupService.ANON_ROLE);
						if (role == null)
						{
							role = new BaseRole(AuthzGroupService.ANON_ROLE);
							m_roles.put(AuthzGroupService.ANON_ROLE, role);
						}
						role.allowFunction(lock);
					}
				}

				// old way auth was stored
				// add the lock to the auth role definition
				else if (auth != null)
				{
					if (roleId != null)
					{
						// the old pubview was done this way, we handle it so no need for warning
						if (!("pubview".equals(roleId)))
						{
							M_log.warn("(el) role for auth: " + m_id + " " + roleId);
						}
					}

					if (lock != null)
					{
						BaseRole role = (BaseRole) m_roles.get(AuthzGroupService.AUTH_ROLE);
						if (role == null)
						{
							role = new BaseRole(AuthzGroupService.AUTH_ROLE);
							m_roles.put(AuthzGroupService.AUTH_ROLE, role);
						}
						role.allowFunction(lock);
					}
				}

				else if (userId != null)
				{
					BaseRole role = (BaseRole) m_roles.get(roleId);
					if (role != null)
					{
						// if already granted, update to point to the role with the most permissions
						BaseMember grant = (BaseMember) m_userGrants.get(userId);
						if (grant != null)
						{
							if (role.m_locks.size() > ((BaseRole) grant.role).m_locks.size())
							{
								M_log.warn("(el): additional lesser user grant ignored: " + m_id + " " + userId + " "
										+ grant.role.getId() + " keeping: " + roleId);
								grant.role = role;
							}
							else
							{
								M_log.warn("(el): additional lesser user grant ignored: " + m_id + " " + userId + " " + roleId
										+ " keeping: " + grant.role.getId());
							}
						}
						else
						{
							grant = new BaseMember(role, true, false, userId);
							m_userGrants.put(userId, grant);
						}
					}
					else
					{
						M_log.warn("(el): role null: " + roleId);
					}
				}
			}
		}

		// pull out some properties into fields to convert old (pre 1.23) versions
		if (m_createdUserId == null)
		{
			m_createdUserId = m_properties.getProperty("CHEF:creator");
		}
		if (m_lastModifiedUserId == null)
		{
			m_lastModifiedUserId = m_properties.getProperty("CHEF:modifiedby");
		}
		if (m_createdTime == null)
		{
			try
			{
				m_createdTime = m_properties.getTimeProperty("DAV:creationdate");
			}
			catch (Exception ignore)
			{
			}
		}
		if (m_lastModifiedTime == null)
		{
			try
			{
				m_lastModifiedTime = m_properties.getTimeProperty("DAV:getlastmodified");
			}
			catch (Exception ignore)
			{
			}
		}
		m_properties.removeProperty("CHEF:creator");
		m_properties.removeProperty("CHEF:modifiedby");
		m_properties.removeProperty("DAV:creationdate");
		m_properties.removeProperty("DAV:getlastmodified");

		// make sure we have our times
		if ((m_createdTime == null) && (m_lastModifiedTime != null))
		{
			m_createdTime = (Time) m_lastModifiedTime.clone();
		}

		if (m_createdTime == null)
		{
			m_createdTime = TimeService.newTime();
		}

		if (m_lastModifiedTime == null)
		{
			m_lastModifiedTime = (Time) m_createdTime.clone();
		}

		// and our users
		if ((m_createdUserId == null) && (m_lastModifiedUserId != null))
		{
			m_createdUserId = m_lastModifiedUserId;
		}

		if (m_createdUserId == null)
		{
			m_createdUserId = "admin";
		}

		if (m_lastModifiedUserId == null)
		{
			m_lastModifiedUserId = m_createdUserId;
		}

		// recognize old (ContentHosting) pubview realms where anon/auth were granted "pubview" role
		// roles can not be nested anymore - remove the pubview role and put the one "content.read" lock into .anon
		if (m_roles.get("pubview") != null)
		{
			m_roles.remove("pubview");

			BaseRole role = (BaseRole) m_roles.get(AuthzGroupService.ANON_ROLE);
			if (role == null)
			{
				role = new BaseRole(AuthzGroupService.ANON_ROLE);
				m_roles.put(AuthzGroupService.ANON_ROLE, role);
			}
			role.allowFunction("content.read");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDescription()
	{
		// the special ones
		if (getId().startsWith("!site.template"))
		{
			return "Site AuthzGroup Template";
		}

		else if (getId().equals("!site.user"))
		{
			return "My Workspace AuthzGroup Template";
		}

		else if (getId().startsWith("!user.template"))
		{
			return "User AuthzGroup Template";
		}

		else if (getId().equals("!site.helper"))
		{
			return "Site Helper Patch AuthzGroup";
		}

		else if (getId().startsWith("!"))
		{
			return "Special AuthzGroup";
		}

		// the rest are references to some resource
		try
		{
			Reference ref = ((BaseAuthzGroupService) (AuthzGroupService.getInstance())).m_entityManager.newReference(getId());
			return ref.getDescription();
		}
		catch (Throwable ignore)
		{
		}

		return "unknown";
	}

	/**
	 * Take all values from this object.
	 * 
	 * @param azGroup
	 *        The AuthzGroup to take values from.
	 */
	protected void setAll(AuthzGroup azGroup)
	{
		if (((BaseAuthzGroup) azGroup).m_lazy)
			((BaseAuthzGroupService) (AuthzGroupService.getInstance())).m_storage.completeGet(((BaseAuthzGroup) azGroup));

		m_key = ((BaseAuthzGroup) azGroup).m_key;
		m_id = ((BaseAuthzGroup) azGroup).m_id;
		m_providerRealmId = ((BaseAuthzGroup) azGroup).m_providerRealmId;
		m_maintainRole = ((BaseAuthzGroup) azGroup).m_maintainRole;

		m_createdUserId = ((BaseAuthzGroup) azGroup).m_createdUserId;
		m_lastModifiedUserId = ((BaseAuthzGroup) azGroup).m_lastModifiedUserId;
		if (((BaseAuthzGroup) azGroup).m_createdTime != null)
			m_createdTime = (Time) ((BaseAuthzGroup) azGroup).m_createdTime.clone();
		if (((BaseAuthzGroup) azGroup).m_lastModifiedTime != null)
			m_lastModifiedTime = (Time) ((BaseAuthzGroup) azGroup).m_lastModifiedTime.clone();

		// make a deep copy of the roles as new Role objects
		m_roles = new HashMap();
		for (Iterator it = ((BaseAuthzGroup) azGroup).m_roles.entrySet().iterator(); it.hasNext();)
		{
			Map.Entry entry = (Map.Entry) it.next();
			BaseRole role = (BaseRole) entry.getValue();
			String id = (String) entry.getKey();

			m_roles.put(id, new BaseRole(id, role));
		}

		// make a deep copy (w/ new Member objects pointing to my own roles) of the user - role grants
		m_userGrants = new HashMap();
		for (Iterator it = ((BaseAuthzGroup) azGroup).m_userGrants.entrySet().iterator(); it.hasNext();)
		{
			Map.Entry entry = (Map.Entry) it.next();
			BaseMember grant = (BaseMember) entry.getValue();
			String id = (String) entry.getKey();

			m_userGrants
					.put(id, new BaseMember((Role) m_roles.get(grant.role.getId()), grant.active, grant.provided, grant.userId));
		}

		m_properties = new BaseResourcePropertiesEdit();
		m_properties.addAll(azGroup.getProperties());
		((BaseResourcePropertiesEdit) m_properties).setLazy(((BaseResourceProperties) azGroup.getProperties()).isLazy());

		m_lazy = false;
	}

	/**
	 * {@inheritDoc}
	 */
	public Element toXml(Document doc, Stack stack)
	{
		if (m_lazy) ((BaseAuthzGroupService) (AuthzGroupService.getInstance())).m_storage.completeGet(this);

		Element azGroup = doc.createElement("azGroup");

		if (stack.isEmpty())
		{
			doc.appendChild(azGroup);
		}
		else
		{
			((Element) stack.peek()).appendChild(azGroup);
		}

		stack.push(azGroup);

		azGroup.setAttribute("id", getId());
		if (m_providerRealmId != null)
		{
			azGroup.setAttribute("provider-id", m_providerRealmId);
		}
		if (m_maintainRole != null)
		{
			azGroup.setAttribute("maintain-role", m_maintainRole);
		}

		azGroup.setAttribute("created-id", m_createdUserId);
		azGroup.setAttribute("modified-id", m_lastModifiedUserId);
		azGroup.setAttribute("created-time", m_createdTime.toString());
		azGroup.setAttribute("modified-time", m_lastModifiedTime.toString());

		// properties
		getProperties().toXml(doc, stack);

		// roles (write before grants!)
		for (Iterator i = m_roles.values().iterator(); i.hasNext();)
		{
			BaseRole role = (BaseRole) i.next();
			role.toXml(doc, stack);
		}

		// user - role grants
		for (Iterator i = m_userGrants.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry entry = (Map.Entry) i.next();
			BaseMember grant = (BaseMember) entry.getValue();
			String user = (String) entry.getKey();

			Element element = doc.createElement("grant");
			azGroup.appendChild(element);
			element.setAttribute("user", user);
			element.setAttribute("role", grant.role.getId());
			element.setAttribute("active", Boolean.valueOf(grant.active).toString());
			element.setAttribute("provided", Boolean.valueOf(grant.provided).toString());
		}

		stack.pop();

		return azGroup;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getId()
	{
		if (m_id == null) return "";
		return m_id;
	}

	/**
	 * {@inheritDoc}
	 */
	public Integer getKey()
	{
		return m_key;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getUrl()
	{
		return ((BaseAuthzGroupService) (AuthzGroupService.getInstance())).getAccessPoint(false) + m_id;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getReference()
	{
		return ((BaseAuthzGroupService) (AuthzGroupService.getInstance())).authzGroupReference(m_id);
	}

	/**
	 * {@inheritDoc}
	 */
	public ResourceProperties getProperties()
	{
		if (m_lazy) ((BaseAuthzGroupService) (AuthzGroupService.getInstance())).m_storage.completeGet(this);

		return m_properties;
	}

	/**
	 * {@inheritDoc}
	 */
	public User getCreatedBy()
	{
		try
		{
			return UserDirectoryService.getUser(m_createdUserId);
		}
		catch (Exception e)
		{
			return UserDirectoryService.getAnonymousUser();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public User getModifiedBy()
	{
		try
		{
			return UserDirectoryService.getUser(m_lastModifiedUserId);
		}
		catch (Exception e)
		{
			return UserDirectoryService.getAnonymousUser();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Time getCreatedTime()
	{
		return m_createdTime;
	}

	/**
	 * {@inheritDoc}
	 */
	public Time getModifiedTime()
	{
		return m_lastModifiedTime;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isAllowed(String user, String lock)
	{
		if (m_lazy) ((BaseAuthzGroupService) (AuthzGroupService.getInstance())).m_storage.completeGet(this);

		// consider a role granted
		BaseMember grant = (BaseMember) m_userGrants.get(user);
		if ((grant != null) && (grant.active))
		{
			if (grant.role.isAllowed(lock)) return true;
		}

		// consider auth role
		if (!UserDirectoryService.getAnonymousUser().getId().equals(user))
		{
			Role auth = (Role) m_roles.get(AuthzGroupService.AUTH_ROLE);
			if (auth != null)
			{
				if (auth.isAllowed(lock)) return true;
			}
		}

		// consider anon role
		Role anon = (Role) m_roles.get(AuthzGroupService.ANON_ROLE);
		if (anon != null)
		{
			if (anon.isAllowed(lock)) return true;
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasRole(String user, String role)
	{
		if (m_lazy) ((BaseAuthzGroupService) (AuthzGroupService.getInstance())).m_storage.completeGet(this);

		BaseMember grant = (BaseMember) m_userGrants.get(user);
		if ((grant != null) && (grant.active) && (grant.role.getId().equals(role))) return true;

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set getUsers()
	{
		if (m_lazy) ((BaseAuthzGroupService) (AuthzGroupService.getInstance())).m_storage.completeGet(this);

		Set rv = new HashSet();
		for (Iterator it = m_userGrants.entrySet().iterator(); it.hasNext();)
		{
			Map.Entry entry = (Map.Entry) it.next();
			String user = (String) entry.getKey();
			Member grant = (Member) entry.getValue();
			if (grant.isActive())
			{
				rv.add(user);
			}
		}

		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set getMembers()
	{
		// Note: this is the only way to see non-active grants

		if (m_lazy) ((BaseAuthzGroupService) (AuthzGroupService.getInstance())).m_storage.completeGet(this);

		Set rv = new HashSet();
		for (Iterator it = m_userGrants.entrySet().iterator(); it.hasNext();)
		{
			Map.Entry entry = (Map.Entry) it.next();
			Member grant = (Member) entry.getValue();
			rv.add(grant);
		}

		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set getUsersIsAllowed(String lock)
	{
		if (m_lazy) ((BaseAuthzGroupService) (AuthzGroupService.getInstance())).m_storage.completeGet(this);

		Set rv = new HashSet();
		for (Iterator it = m_userGrants.entrySet().iterator(); it.hasNext();)
		{
			Map.Entry entry = (Map.Entry) it.next();
			String user = (String) entry.getKey();
			BaseMember grant = (BaseMember) entry.getValue();
			if (grant.active && grant.role.isAllowed(lock))
			{
				rv.add(user);
			}
		}

		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set getUsersHasRole(String role)
	{
		if (m_lazy) ((BaseAuthzGroupService) (AuthzGroupService.getInstance())).m_storage.completeGet(this);

		Set rv = new HashSet();
		for (Iterator it = m_userGrants.entrySet().iterator(); it.hasNext();)
		{
			Map.Entry entry = (Map.Entry) it.next();
			String user = (String) entry.getKey();
			BaseMember grant = (BaseMember) entry.getValue();
			if (grant.active && grant.role.getId().equals(role))
			{
				rv.add(user);
			}
		}

		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public Role getUserRole(String user)
	{
		if (m_lazy) ((BaseAuthzGroupService) (AuthzGroupService.getInstance())).m_storage.completeGet(this);

		BaseMember grant = (BaseMember) m_userGrants.get(user);
		if ((grant != null) && (grant.active)) return grant.role;

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Member getMember(String user)
	{
		if (m_lazy) ((BaseAuthzGroupService) (AuthzGroupService.getInstance())).m_storage.completeGet(this);

		BaseMember grant = (BaseMember) m_userGrants.get(user);
		return grant;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set getRoles()
	{
		if (m_lazy) ((BaseAuthzGroupService) (AuthzGroupService.getInstance())).m_storage.completeGet(this);

		return new HashSet(m_roles.values());
	}

	public Set getRolesIsAllowed(String function)
	{
		if (m_lazy) ((BaseAuthzGroupService) (AuthzGroupService.getInstance())).m_storage.completeGet(this);

		Set rv = new HashSet();
		for (Iterator i = m_roles.values().iterator(); i.hasNext();)
		{
			Role r = (Role) i.next();
			if (r.isAllowed(function))
			{
				rv.add(r.getId());
			}
		}

		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public Role getRole(String id)
	{
		if (m_lazy) ((BaseAuthzGroupService) (AuthzGroupService.getInstance())).m_storage.completeGet(this);

		return (Role) m_roles.get(id);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getProviderGroupId()
	{
		return m_providerRealmId;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isEmpty()
	{
		if (m_lazy) ((BaseAuthzGroupService) (AuthzGroupService.getInstance())).m_storage.completeGet(this);

		// no roles, no grants to users, nothing in anon or auth
		if (m_roles.isEmpty() && m_userGrants.isEmpty())
		{
			return true;
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getMaintainRole()
	{
		if (m_maintainRole == null)
		{
			return "maintain";
		}

		return m_maintainRole;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj)
	{
		if (!(obj instanceof AuthzGroup)) return false;
		return ((AuthzGroup) obj).getId().equals(getId());
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode()
	{
		return getId().hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(Object obj)
	{
		if (!(obj instanceof AuthzGroup)) throw new ClassCastException();

		// if the object are the same, say so
		if (obj == this) return 0;

		// sort based on id
		int compare = getId().compareTo(((AuthzGroup) obj).getId());

		return compare;
	}

	/**
	 * {@inheritDoc}
	 */
	public void addMember(String user, String roleId, boolean active, boolean provided)
	{
		Role role = (Role) m_roles.get(roleId);
		if (role == null)
		{
			M_log.warn(".addUserRole: role undefined: " + roleId);
			return;
		}

		BaseMember grant = (BaseMember) m_userGrants.get(user);
		if (grant == null)
		{
			grant = new BaseMember(role, active, provided, user);
			m_userGrants.put(user, grant);
		}
		else
		{
			grant.role = role;
			grant.active = active;
			grant.provided = provided;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeMember(String user)
	{
		if (m_lazy) ((BaseAuthzGroupService) (AuthzGroupService.getInstance())).m_storage.completeGet(this);

		m_userGrants.remove(user);
	}

	/**
	 * Take all values from this object.
	 * 
	 * @param azGroup
	 *        The AuthzGroup object to take values from.
	 */
	protected void set(AuthzGroup azGroup)
	{
		setAll(azGroup);
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeMembers()
	{
		if (m_lazy) ((BaseAuthzGroupService) (AuthzGroupService.getInstance())).m_storage.completeGet(this);

		m_userGrants.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	public Role addRole(String id) throws IdUsedException
	{
		if (m_lazy) ((BaseAuthzGroupService) (AuthzGroupService.getInstance())).m_storage.completeGet(this);

		Role role = (Role) m_roles.get(id);
		if (role != null) throw new IdUsedException(id);

		role = new BaseRole(id);
		m_roles.put(role.getId(), role);

		return role;
	}

	/**
	 * {@inheritDoc}
	 */
	public Role addRole(String id, Role other) throws IdUsedException
	{
		if (m_lazy) ((BaseAuthzGroupService) (AuthzGroupService.getInstance())).m_storage.completeGet(this);

		Role role = (Role) m_roles.get(id);
		if (role != null) throw new IdUsedException(id);

		role = new BaseRole(id, other);
		m_roles.put(role.getId(), role);

		return role;
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeRole(String roleId)
	{
		if (m_lazy) ((BaseAuthzGroupService) (AuthzGroupService.getInstance())).m_storage.completeGet(this);

		Role r = (Role) m_roles.get(roleId);
		if (r != null)
		{
			m_roles.remove(roleId);

			// remove the role from any appearance in m_userGrants
			for (Iterator it = m_userGrants.entrySet().iterator(); it.hasNext();)
			{
				Map.Entry entry = (Map.Entry) it.next();
				BaseMember grant = (BaseMember) entry.getValue();
				String id = (String) entry.getKey();

				if (grant.role.equals(r))
				{
					it.remove();
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeRoles()
	{
		if (m_lazy) ((BaseAuthzGroupService) (AuthzGroupService.getInstance())).m_storage.completeGet(this);

		// clear roles and grants (since grants grant roles)
		m_roles.clear();
		m_userGrants.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	public void setProviderGroupId(String id)
	{
		m_providerRealmId = StringUtil.trimToNull(id);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setMaintainRole(String role)
	{
		m_maintainRole = StringUtil.trimToNull(role);
	}

	/**
	 * Access the event code for this azGroup.
	 * 
	 * @return The event code for this azGroup.
	 */
	protected String getEvent()
	{
		return m_event;
	}

	/**
	 * Set the event code for this azGroup.
	 * 
	 * @param event
	 *        The event code for this azGroup.
	 */
	protected void setEvent(String event)
	{
		m_event = event;
	}

	/**
	 * {@inheritDoc}
	 */
	public ResourcePropertiesEdit getPropertiesEdit()
	{
		if (m_lazy) ((BaseAuthzGroupService) (AuthzGroupService.getInstance())).m_storage.completeGet(this);

		return m_properties;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean keepIntersection(AuthzGroup other)
	{
		if (other == null) return false;
		
		boolean rv = false;
		
		// get un-lazy
		if (m_lazy) ((BaseAuthzGroupService) (AuthzGroupService.getInstance())).m_storage.completeGet(this);

		// for each member
		for (Iterator it = m_userGrants.entrySet().iterator(); it.hasNext();)
		{
			Map.Entry entry = (Map.Entry) it.next();
			Member grant = (Member) entry.getValue();
			
			Member otherMember = other.getMember(grant.getUserId());
			
			// remove our member if the other has no member
			if (otherMember == null)
			{
				it.remove();
				rv = true;
			}

			// make sure we are just as active as other
			else
			{
				if (grant.isActive() != otherMember.isActive())
				{
					grant.setActive(otherMember.isActive());
					rv = true;
				}
			}
		}
		
		return rv;
	}

	/**
	 * Enable editing.
	 */
	protected void activate()
	{
		m_active = true;
	}

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
}