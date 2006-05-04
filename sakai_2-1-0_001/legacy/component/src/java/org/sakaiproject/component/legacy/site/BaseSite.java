/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/trunk/sakai/legacy/component/src/java/org/sakaiproject/component/legacy/site/BaseSiteService.java $
 * $Id: BaseSiteService.java 2523 2005-10-12 04:03:48Z ggolden@umich.edu $
 **********************************************************************************
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

package org.sakaiproject.component.legacy.site;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.service.legacy.authzGroup.AuthzGroup;
import org.sakaiproject.service.legacy.authzGroup.Member;
import org.sakaiproject.service.legacy.authzGroup.Role;
import org.sakaiproject.service.legacy.authzGroup.cover.AuthzGroupService;
import org.sakaiproject.service.legacy.entity.Entity;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.entity.ResourceProperties;
import org.sakaiproject.service.legacy.entity.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.site.Group;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SitePage;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.time.cover.TimeService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.sakaiproject.util.java.StringUtil;
import org.sakaiproject.util.resource.BaseResourceProperties;
import org.sakaiproject.util.resource.BaseResourcePropertiesEdit;
import org.sakaiproject.util.xml.Xml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>
 * BaseSite is a base implementation of the Site API Site.
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public class BaseSite implements Site
{
	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(BaseSite.class);

	/** A fixed class serian number. */
	private static final long serialVersionUID = 1L;

	/** The event code for this edit. */
	protected String m_event = null;

	/** Active flag. */
	protected boolean m_active = false;

	/** List of groups deleted in this edit pass. */
	protected Collection m_deletedGroups = new Vector();

	/** The site id. */
	protected String m_id = null;

	/** The site title. */
	protected String m_title = null;

	/** The site short description. */
	protected String m_shortDescription = null;

	/** The site description. */
	protected String m_description = null;

	/** The name of the role given to users who join a joinable site. */
	protected String m_joinerRole = null;

	/** Is this site joinable. */
	protected boolean m_joinable = false;

	/** Published or not. */
	protected boolean m_published = false;

	/** The icon url. */
	protected String m_icon = null;

	/** The site info url. */
	protected String m_info = null;

	/** The properties. */
	protected ResourcePropertiesEdit m_properties = null;

	/** The list of site pages for this site. */
	protected ResourceVector m_pages = null;

	/** Set true while the pages have not yet been read in for a site. */
	protected boolean m_pagesLazy = false;

	/** The skin to use for this site. */
	protected String m_skin = null;

	/** The pubView flag. */
	protected boolean m_pubView = false;

	/** The site type. */
	protected String m_type = null;

	/** The created user id. */
	protected String m_createdUserId = null;

	/** The last modified user id. */
	protected String m_lastModifiedUserId = null;

	/** The time created. */
	protected Time m_createdTime = null;

	/** The time last modified. */
	protected Time m_lastModifiedTime = null;

	/** The list of site groups for this site. */
	protected ResourceVector m_groups = null;

	/** Set true while the groups have not yet been read in for a site. */
	protected boolean m_groupsLazy = false;

	/** The azg from the AuthzGroupService that is my AuthzGroup impl. */
	protected AuthzGroup m_azg = null;

	/** Set to true if we have changed our azg, so it need to be written back on save. */
	protected boolean m_azgChanged = false;

	/**
	 * Construct.
	 * 
	 * @param id
	 *        The site id.
	 */
	public BaseSite(String id)
	{
		m_id = id;

		// setup for properties
		m_properties = new BaseResourcePropertiesEdit();

		// set up the page list
		m_pages = new ResourceVector();

		// set up the groups collection
		m_groups = new ResourceVector();

		// if the id is not null (a new site, rather than a reconstruction)
		// add the automatic (live) properties
		if (m_id != null) ((BaseSiteService) (SiteService.getInstance())).addLiveProperties(this);
	}

	/**
	 * Construct from another Site, exact.
	 * 
	 * @param site
	 *        The other site to copy values from.
	 * @param exact
	 *        If true, we copy ids - else we generate new ones for site, page and tools.
	 */
	public BaseSite(Site other)
	{
		BaseSite bOther = (BaseSite) other;
		set(bOther, true);
	}

	/**
	 * Construct from another Site.
	 * 
	 * @param site
	 *        The other site to copy values from.
	 * @param exact
	 *        If true, we copy ids - else we generate new ones for site, page and tools.
	 */
	public BaseSite(Site other, boolean exact)
	{
		BaseSite bOther = (BaseSite) other;
		set(bOther, exact);
	}

	/**
	 * Construct from an existing definition, in xml.
	 * 
	 * @param el
	 *        The message in XML in a DOM element.
	 */
	public BaseSite(Element el)
	{
		// setup for properties
		m_properties = new BaseResourcePropertiesEdit();

		// setup for page list
		m_pages = new ResourceVector();

		// setup for the groups list
		m_groups = new ResourceVector();

		m_id = el.getAttribute("id");
		m_title = StringUtil.trimToNull(el.getAttribute("title"));

		// description might be encripted
		m_description = StringUtil.trimToNull(el.getAttribute("description"));
		if (m_description == null)
		{
			m_description = StringUtil.trimToNull(Xml.decodeAttribute(el, "description-enc"));
		}

		// short description might be encripted
		m_shortDescription = StringUtil.trimToNull(el.getAttribute("short-description"));
		if (m_shortDescription == null)
		{
			m_shortDescription = StringUtil.trimToNull(Xml.decodeAttribute(el, "short-description-enc"));
		}

		m_joinable = Boolean.valueOf(el.getAttribute("joinable")).booleanValue();
		m_joinerRole = StringUtil.trimToNull(el.getAttribute("joiner-role"));

		String published = StringUtil.trimToNull(el.getAttribute("published"));
		if (published == null)
		{
			// read the old "status" (this file 1.42 and before) 1-un 2-pub
			published = StringUtil.trimToNull(el.getAttribute("status"));
			if (published != null)
			{
				published = Boolean.valueOf("2".equals(published)).toString();
			}
		}

		m_published = Boolean.valueOf(published).booleanValue();

		m_icon = StringUtil.trimToNull(el.getAttribute("icon"));
		m_info = StringUtil.trimToNull(el.getAttribute("info"));
		m_skin = StringUtil.trimToNull(el.getAttribute("skin"));

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

		// get pubView setting - but old versions (pre 1.42 of this file) won't have it and will have a property instead
		String pubViewValue = StringUtil.trimToNull(el.getAttribute("pubView"));

		// get the type - but old versions (pre 1.42 of this file) won't have it and will have a property instead
		String typeValue = StringUtil.trimToNull(el.getAttribute("type"));

		// the children (properties and page list)
		NodeList children = el.getChildNodes();
		for (int i = 0; i < children.getLength(); i++)
		{
			Node child = children.item(i);
			if (child.getNodeType() != Node.ELEMENT_NODE) continue;
			Element element = (Element) child;

			// look for properties
			if (element.getTagName().equals("properties"))
			{
				// re-create properties
				m_properties = new BaseResourcePropertiesEdit(element);

				// look for pubview (pre 1.42 of this file) in properties
				if (pubViewValue == null)
				{
					pubViewValue = m_properties.getProperty("CTNG:site-include");
					if (pubViewValue == null)
					{
						pubViewValue = m_properties.getProperty("site-include");
					}
				}
				m_properties.removeProperty("CTNG:site-include");
				m_properties.removeProperty("site-include");

				// look for type (pre 1.42 of this file) in properties (two possibilities)
				if (typeValue == null)
				{
					typeValue = m_properties.getProperty("SAKAI:site-type");
					if (typeValue == null)
					{
						typeValue = m_properties.getProperty("CTNG:site-type");
					}
				}
				m_properties.removeProperty("SAKAI:site-type");
				m_properties.removeProperty("CTNG:site-type");

				// look for short description (pre 1.42 of this file) in properties
				if (m_shortDescription == null)
				{
					m_shortDescription = m_properties.getProperty("CTNG:short-description");

					if (m_shortDescription == null)
					{
						m_shortDescription = m_properties.getProperty("short-description");
					}
				}
				m_properties.removeProperty("CTNG:short-description");
				m_properties.removeProperty("short-description");

				// pull out some properties into fields to convert old (pre 1.42) versions
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
			}

			// look for the page list
			else if (element.getTagName().equals("pages"))
			{
				NodeList pagesNodes = element.getChildNodes();
				for (int p = 0; p < pagesNodes.getLength(); p++)
				{
					Node pageNode = pagesNodes.item(p);
					if (pageNode.getNodeType() != Node.ELEMENT_NODE) continue;
					Element pageEl = (Element) pageNode;
					if (!pageEl.getTagName().equals("page")) continue;

					BaseSitePage page = new BaseSitePage(pageEl, this);
					m_pages.add(page);
				}

				// TODO: else if ( "groups")
			}
		}

		// set the pubview, now it's found in either the attribute or the properties
		if (pubViewValue != null)
		{
			m_pubView = Boolean.valueOf(pubViewValue).booleanValue();
		}
		else
		{
			m_pubView = false;
		}

		// set the type, now it's found in either the attribute or the properties
		m_type = typeValue;
	}

	/**
	 * ReConstruct.
	 * 
	 * @param id
	 * @param title
	 * @param type
	 * @param shortDesc
	 * @param description
	 * @param iconUrl
	 * @param infoUrl
	 * @param skin
	 * @param published
	 * @param joinable
	 * @param pubView
	 * @param joinRole
	 * @param isSpecial
	 * @param isUser
	 * @param createdBy
	 * @param createdOn
	 * @param modifiedBy
	 * @param modifiedOn
	 */
	public BaseSite(String id, String title, String type, String shortDesc, String description, String iconUrl, String infoUrl,
			String skin, boolean published, boolean joinable, boolean pubView, String joinRole, boolean isSpecial, boolean isUser,
			String createdBy, Time createdOn, String modifiedBy, Time modifiedOn)
	{
		// setup for properties
		m_properties = new BaseResourcePropertiesEdit();

		// set up the page list
		m_pages = new ResourceVector();

		// set up the groups collection
		m_groups = new ResourceVector();

		m_id = id;
		m_title = title;
		m_type = type;
		m_shortDescription = shortDesc;
		m_description = description;
		m_icon = iconUrl;
		m_info = infoUrl;
		m_skin = skin;
		m_published = published;
		m_joinable = joinable;
		m_pubView = pubView;
		m_joinerRole = joinRole;
		// TODO: isSpecial
		// TODO: isUser
		m_createdUserId = createdBy;
		m_lastModifiedUserId = modifiedBy;
		m_createdTime = createdOn;
		m_lastModifiedTime = modifiedOn;

		// setup for properties, but mark them lazy since we have not yet established them from data
		((BaseResourcePropertiesEdit) m_properties).setLazy(true);

		m_pagesLazy = true;
		m_groupsLazy = true;
	}

	/**
	 * Set me to be a deep copy of other (all but my id.)
	 * 
	 * @param bOther
	 *        the other to copy.
	 * @param exact
	 *        If true, we copy ids - else we generate new ones for site, page and tools.
	 */
	protected void set(BaseSite other, boolean exact)
	{
		// if exact, set the id, else assume the id was already set
		if (exact)
		{
			m_id = other.m_id;
		}

		m_title = other.m_title;
		m_shortDescription = other.m_shortDescription;
		m_description = other.m_description;
		m_joinable = other.m_joinable;
		m_joinerRole = other.m_joinerRole;
		m_published = other.m_published;
		m_icon = other.m_icon;
		m_info = other.m_info;
		m_skin = other.m_skin;
		m_type = other.m_type;
		m_pubView = other.m_pubView;
		if (exact)
		{
			m_createdUserId = other.m_createdUserId;
		}
		else
		{
			m_createdUserId = UserDirectoryService.getCurrentUser().getId();
		}
		m_lastModifiedUserId = other.m_lastModifiedUserId;
		if (other.m_createdTime != null) m_createdTime = (Time) other.m_createdTime.clone();
		if (other.m_lastModifiedTime != null) m_lastModifiedTime = (Time) other.m_lastModifiedTime.clone();

		m_properties = new BaseResourcePropertiesEdit();
		ResourceProperties pOther = other.getProperties();
		Iterator l = pOther.getPropertyNames();
		while (l.hasNext())
		{
			String pOtherName = (String) l.next();
			m_properties.addProperty(pOtherName, pOther.getProperty(pOtherName).replaceAll(other.getId(), getId()));
		}
		((BaseResourcePropertiesEdit) m_properties).setLazy(((BaseResourceProperties) other.getProperties()).isLazy());

		// deep copy the pages
		m_pages = new ResourceVector();
		for (Iterator iPages = other.getPages().iterator(); iPages.hasNext();)
		{
			BaseSitePage page = (BaseSitePage) iPages.next();
			m_pages.add(new BaseSitePage(page, this, exact));
		}
		m_pagesLazy = other.m_pagesLazy;

		// deep copy the groups
		m_groups = new ResourceVector();
		for (Iterator iGroups = other.getGroups().iterator(); iGroups.hasNext();)
		{
			Group group = (Group) iGroups.next();
			m_groups.add(new BaseGroup(group, this, exact));
		}
		m_groupsLazy = other.m_groupsLazy;
	}

	/**
	 * @inheritDoc
	 */
	public String getId()
	{
		if (m_id == null) return "";
		return m_id;
	}

	/**
	 * @inheritDoc
	 */
	public String getUrl()
	{
		return ((BaseSiteService) (SiteService.getInstance())).m_serverConfigurationService.getPortalUrl() + "/site/" + m_id;
	}

	/**
	 * @inheritDoc
	 */
	public String getReference()
	{
		return ((BaseSiteService) (SiteService.getInstance())).siteReference(m_id);
	}

	/**
	 * @inheritDoc
	 */
	public ResourceProperties getProperties()
	{
		// if lazy, resolve
		if (((BaseResourceProperties) m_properties).isLazy())
		{
			((BaseSiteService) (SiteService.getInstance())).m_storage.readSiteProperties(this, m_properties);
			((BaseResourcePropertiesEdit) m_properties).setLazy(false);
		}

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
	 * @inheritDoc
	 */
	public String getTitle()
	{
		// if set here, use the setting
		if (m_title != null) return m_title;

		// if not otherwise set, use the id
		return getId();
	}

	/**
	 * @inheritDoc
	 */
	public String getShortDescription()
	{
		return m_shortDescription;
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
	public boolean isJoinable()
	{
		return m_joinable;
	}

	/**
	 * @inheritDoc
	 */
	public String getJoinerRole()
	{
		return m_joinerRole;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isPublished()
	{
		return m_published;
	}

	/**
	 * @inheritDoc
	 */
	public String getSkin()
	{
		return m_skin;
	}

	/**
	 * @inheritDoc
	 */
	public String getIconUrl()
	{
		return m_icon;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getIconUrlFull()
	{
		return ((BaseSiteService) (SiteService.getInstance())).convertReferenceUrl(m_icon);
	}

	/**
	 * @inheritDoc
	 */
	public String getInfoUrl()
	{
		return m_info;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getInfoUrlFull()
	{
		if (m_info == null) return null;

		return ((BaseSiteService) (SiteService.getInstance())).convertReferenceUrl(m_info);
	}

	/**
	 * {@inheritDoc}
	 */
	public List getPages()
	{
		if (m_pagesLazy)
		{
			((BaseSiteService) (SiteService.getInstance())).m_storage.readSitePages(this, m_pages);
			m_pagesLazy = false;
		}

		return m_pages;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection getGroups()
	{
		if (m_groupsLazy)
		{
			((BaseSiteService) (SiteService.getInstance())).m_storage.readSiteGroups(this, m_groups);
			m_groupsLazy = false;
		}

		return m_groups;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasGroups()
	{
		Collection groups = getGroups();
		return !groups.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	public void loadAll()
	{
		// first, pages
		getPages();

		// next, tools from all pages, all at once
		((BaseSiteService) (SiteService.getInstance())).m_storage.readSiteTools(this);

		// get groups, all at once
		getGroups();

		// now all properties
		((BaseSiteService) (SiteService.getInstance())).m_storage.readAllSiteProperties(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public List getOrderedPages()
	{
		List order = ((BaseSiteService) (SiteService.getInstance())).m_serverConfigurationService.getToolOrder(getType());
		if (order.isEmpty()) return getPages();

		// get a copy we can modify without changing the site!
		List pages = new Vector(getPages());

		// find any pages that include the tool type for each tool in the ordering, move them into the newOrder and remove from the old
		List newOrder = new Vector();

		// for each entry in the order
		for (Iterator i = order.iterator(); i.hasNext();)
		{
			String toolId = (String) i.next();

			// find any pages that have this tool
			for (Iterator p = pages.iterator(); p.hasNext();)
			{
				SitePage page = (SitePage) p.next();
				List tools = page.getTools();
				for (Iterator t = tools.iterator(); t.hasNext();)
				{
					ToolConfiguration tool = (ToolConfiguration) t.next();
					if (tool.getTool().getId().equals(toolId))
					{
						// this page has this tool, so move it from the pages to the newOrder
						newOrder.add(page);
						p.remove();
						break;
					}
				}
			}
		}

		// add any remaining
		newOrder.addAll(pages);

		return newOrder;
	}

	/**
	 * {@inheritDoc}
	 */
	public SitePage getPage(String id)
	{
		return (SitePage) ((ResourceVector) getPages()).getById(id);
	}

	/**
	 * {@inheritDoc}
	 */
	public ToolConfiguration getTool(String id)
	{
		// search the pages
		for (Iterator iPages = getPages().iterator(); iPages.hasNext();)
		{
			SitePage page = (SitePage) iPages.next();
			ToolConfiguration tool = page.getTool(id);

			if (tool != null) return tool;
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection getTools(String[] toolIds)
	{
		List rv = new Vector();
		if ((toolIds == null) || (toolIds.length == 0)) return rv;

		// search the pages
		for (Iterator iPages = getPages().iterator(); iPages.hasNext();)
		{
			SitePage page = (SitePage) iPages.next();
			rv.addAll(page.getTools(toolIds));
		}

		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public Group getGroup(String id)
	{
		if (id == null) return null;

		// if this is a reference, starting with a "/", parse it, make sure it's a group, in this site, and pull the id
		if (id.startsWith(Entity.SEPARATOR))
		{
			Reference ref = ((BaseSiteService) (SiteService.getInstance())).m_entityManager.newReference(id);
			if ((SiteService.SERVICE_NAME.equals(ref.getType())) && (SiteService.GROUP_SUBTYPE.equals(ref.getSubType()))
					&& (m_id.equals(ref.getContainer())))
			{
				return (Group) ((ResourceVector) getGroups()).getById(ref.getId());
			}

			return null;
		}

		return (Group) ((ResourceVector) getGroups()).getById(id);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getType()
	{
		return m_type;
	}

	/**
	 * @inheritDoc
	 */
	public boolean isType(Object type)
	{
		if (type == null) return true;

		String myType = getType();

		if (type instanceof String[])
		{
			for (int i = 0; i < ((String[]) type).length; i++)
			{
				String test = ((String[]) type)[i];
				if ((test != null) && (test.equals(myType)))
				{
					return true;
				}
			}
		}

		else if (type instanceof Collection)
		{
			return ((Collection) type).contains(myType);
		}

		else if (type instanceof String)
		{
			return type.equals(myType);
		}

		return false;
	}

	/**
	 * @inheritDoc
	 */
	public boolean equals(Object obj)
	{
		if (obj instanceof Site)
		{
			return ((Site) obj).getId().equals(getId());
		}

		// compare to strings as id
		if (obj instanceof String)
		{
			return ((String) obj).equals(getId());
		}

		return false;
	}

	/**
	 * @inheritDoc
	 */
	public int hashCode()
	{
		return getId().hashCode();
	}

	/**
	 * @inheritDoc
	 */
	public int compareTo(Object obj)
	{
		if (!(obj instanceof Site)) throw new ClassCastException();

		// if the object are the same, say so
		if (obj == this) return 0;

		// start the compare by comparing their sort names
		int compare = getTitle().compareTo(((Site) obj).getTitle());

		// if these are the same
		if (compare == 0)
		{
			// sort based on (unique) id
			compare = getId().compareTo(((Site) obj).getId());
		}

		return compare;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isPubView()
	{
		return m_pubView;
	}

	/**
	 * {@inheritDoc}
	 */
	public Element toXml(Document doc, Stack stack)
	{
		Element site = doc.createElement("site");
		if (stack.isEmpty())
		{
			doc.appendChild(site);
		}
		else
		{
			((Element) stack.peek()).appendChild(site);
		}

		site.setAttribute("id", getId());
		if (m_title != null) site.setAttribute("title", m_title);

		// encode the short description
		if (m_shortDescription != null) Xml.encodeAttribute(site, "short-description-enc", m_shortDescription);

		// encode the description
		if (m_description != null) Xml.encodeAttribute(site, "description-enc", m_description);

		site.setAttribute("joinable", new Boolean(m_joinable).toString());
		if (m_joinerRole != null) site.setAttribute("joiner-role", m_joinerRole);
		site.setAttribute("published", Boolean.valueOf(m_published).toString());
		if (m_icon != null) site.setAttribute("icon", m_icon);
		if (m_info != null) site.setAttribute("info", m_info);
		if (m_skin != null) site.setAttribute("skin", m_skin);
		site.setAttribute("pubView", Boolean.valueOf(m_pubView).toString());
		site.setAttribute("type", m_type);

		site.setAttribute("created-id", m_createdUserId);
		site.setAttribute("modified-id", m_lastModifiedUserId);
		site.setAttribute("created-time", m_createdTime.toString());
		site.setAttribute("modified-time", m_lastModifiedTime.toString());

		// properties
		stack.push(site);
		getProperties().toXml(doc, stack);
		stack.pop();

		// site pages
		Element list = doc.createElement("pages");
		site.appendChild(list);
		stack.push(list);
		for (Iterator iPages = getPages().iterator(); iPages.hasNext();)
		{
			BaseSitePage page = (BaseSitePage) iPages.next();
			page.toXml(doc, stack);
		}
		stack.pop();

		// TODO: site groups

		return site;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setTitle(String title)
	{
		m_title = StringUtil.trimToNull(title);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setShortDescription(String shortDescripion)
	{
		m_shortDescription = StringUtil.trimToNull(shortDescripion);
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
	public void setJoinable(boolean joinable)
	{
		m_joinable = joinable;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setJoinerRole(String role)
	{
		m_joinerRole = role;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setPublished(boolean published)
	{
		m_published = published;

	}

	/**
	 * {@inheritDoc}
	 */
	public void setSkin(String skin)
	{
		m_skin = skin;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setIconUrl(String url)
	{
		m_icon = StringUtil.trimToNull(url);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setInfoUrl(String url)
	{
		m_info = StringUtil.trimToNull(url);
	}

	/**
	 * {@inheritDoc}
	 */
	public SitePage addPage()
	{
		BaseSitePage page = new BaseSitePage(this);
		getPages().add(page);

		return page;
	}

	/**
	 * @inheritDoc
	 */
	public void removePage(SitePage page)
	{
		getPages().remove(page);
	}

	/**
	 * Access the event code for this edit.
	 * 
	 * @return The event code for this edit.
	 */
	protected String getEvent()
	{
		return m_event;
	}

	/**
	 * Set the event code for this edit.
	 * 
	 * @param event
	 *        The event code for this edit.
	 */
	protected void setEvent(String event)
	{
		m_event = event;
	}

	/**
	 * @inheritDoc
	 */
	public ResourcePropertiesEdit getPropertiesEdit()
	{
		// if lazy, resolve
		if (((BaseResourceProperties) m_properties).isLazy())
		{
			((BaseSiteService) (SiteService.getInstance())).m_storage.readSiteProperties(this, m_properties);
			((BaseResourcePropertiesEdit) m_properties).setLazy(false);
		}

		return m_properties;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setType(String type)
	{
		m_type = type;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setPubView(boolean pubView)
	{
		m_pubView = pubView;

	}

	/**
	 * Enable editing.
	 */
	protected void activate()
	{
		m_active = true;
	}

	/**
	 * @inheritDoc
	 */
	public boolean isActiveEdit()
	{
		return m_active;
	}

	/**
	 * Close the edit object - it cannot be used after this.
	 */
	protected void closeEdit()
	{
		m_active = false;
	}

	/**
	 * @inheritDoc
	 */
	public void regenerateIds()
	{
		// deep copy the pages
		ResourceVector newPages = new ResourceVector();
		for (Iterator iPages = getPages().iterator(); iPages.hasNext();)
		{
			BaseSitePage page = (BaseSitePage) iPages.next();
			newPages.add(new BaseSitePage(page, this, false));
		}

		m_pages = newPages;
	}

	/**
	 * {@inheritDoc}
	 */
	public Group addGroup()
	{
		Group rv = new BaseGroup(this);
		m_groups.add(rv);

		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeGroup(Group group)
	{
		// remove it
		m_groups.remove(group);

		// track so we can clean up related on commit
		m_deletedGroups.add(group);
	}

	/**
	 * Access (find if needed) the azg from the AuthzGroupService that implements my grouping.
	 * @return My azg.
	 */
	protected AuthzGroup getAzg()
	{
		if (m_azg == null)
		{
			try
			{
				m_azg = AuthzGroupService.getAuthzGroup(getReference());
			}
			catch (IdUnusedException e)
			{
				try
				{
					// create the site's azg, but don't store it yet (that happens if save is called)
	
					// try the site created-by user for the maintain role in the site
					String userId = getCreatedBy().getId();
					if (userId != null)
					{
						// make sure it's valid
						try
						{
							UserDirectoryService.getUser(userId);
						}
						catch (IdUnusedException e1)
						{
							userId = null;
						}
					}
					
					// use the current user if needed
					if (userId == null)
					{
						User user = UserDirectoryService.getCurrentUser();
						userId = user.getId();
					}

					// find the template for the new azg
					String groupAzgTemplate = ((BaseSiteService) (SiteService.getInstance())).siteAzgTemplate(this);
					AuthzGroup template = null;
					try
					{
						template = AuthzGroupService.getAuthzGroup(groupAzgTemplate);
					}
					catch (Exception e1)
					{
						try
						{
							// if the template is not defined, try the fall back template
							template = AuthzGroupService.getAuthzGroup("!site.template");
						}
						catch (Exception e2)
						{
						}
					}

					m_azg = AuthzGroupService.newAuthzGroup(getReference(), template, userId);
					m_azgChanged = true;
				}
				catch (Throwable t)
				{
					M_log.warn("getAzg: " + t);
				}
			}
		}
		
		return m_azg;
	}

	public void addMember(String userId, String roleId, boolean active, boolean provided)
	{
		m_azgChanged = true;
		getAzg().addMember(userId, roleId, active, provided);
	}

	public Role addRole(String id) throws IdUsedException
	{
		m_azgChanged = true;
		return getAzg().addRole(id);
	}

	public Role addRole(String id, Role other) throws IdUsedException
	{
		m_azgChanged = true;
		return getAzg().addRole(id, other);
	}

	public String getMaintainRole()
	{
		return getAzg().getMaintainRole();
	}

	public Member getMember(String userId)
	{
		return getAzg().getMember(userId);
	}

	public Set getMembers()
	{
		return getAzg().getMembers();
	}

	public String getProviderGroupId()
	{
		return getAzg().getProviderGroupId();
	}

	public Role getRole(String id)
	{
		return getAzg().getRole(id);
	}

	public Set getRoles()
	{
		return getAzg().getRoles();
	}

	public Set getRolesIsAllowed(String function)
	{
		return getAzg().getRolesIsAllowed(function);
	}

	public Role getUserRole(String userId)
	{
		return getAzg().getUserRole(userId);
	}

	public Set getUsers()
	{
		return getAzg().getUsers();
	}

	public Set getUsersHasRole(String role)
	{
		return getAzg().getUsersHasRole(role);
	}

	public Set getUsersIsAllowed(String function)
	{
		return getAzg().getUsersIsAllowed(function);
	}

	public boolean hasRole(String userId, String role)
	{
		return getAzg().hasRole(userId, role);
	}

	public boolean isAllowed(String userId, String function)
	{
		return getAzg().isAllowed(userId, function);
	}

	public boolean isEmpty()
	{
		return getAzg().isEmpty();
	}

	public void removeMember(String userId)
	{
		m_azgChanged = true;
		getAzg().removeMember(userId);
	}

	public void removeMembers()
	{
		m_azgChanged = true;
		getAzg().removeMembers();
	}

	public void removeRole(String role)
	{
		m_azgChanged = true;
		getAzg().removeRole(role);
	}

	public void removeRoles()
	{
		m_azgChanged = true;
		getAzg().removeRoles();
	}

	public void setMaintainRole(String role)
	{
		m_azgChanged = true;
		getAzg().setMaintainRole(role);
	}

	public void setProviderGroupId(String id)
	{
		m_azgChanged = true;
		getAzg().setProviderGroupId(id);
	}
}