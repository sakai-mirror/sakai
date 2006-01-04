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
import java.util.Stack;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.kernel.tool.Tool;
import org.sakaiproject.service.legacy.entity.ResourceProperties;
import org.sakaiproject.service.legacy.entity.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.id.cover.IdService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SitePage;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.util.java.StringUtil;
import org.sakaiproject.util.resource.BaseResourceProperties;
import org.sakaiproject.util.resource.BaseResourcePropertiesEdit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>
 * BaseSitePage is an implementation of the Site API SitePage.
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public class BaseSitePage implements SitePage, Identifiable
{
	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(BaseSitePage.class);

	/** A fixed class serian number. */
	private static final long serialVersionUID = 1L;

	/** The title. */
	protected String m_title = null;

	/** The layout. */
	protected int m_layout = LAYOUT_SINGLE_COL;

	/** The site id. */
	protected String m_id = null;

	/** The properties. */
	protected ResourcePropertiesEdit m_properties = null;

	/** the list of tool configurations for this SitePage */
	protected ResourceVector m_tools = null;

	/** false while the page's tools have not yet been read in. */
	protected boolean m_toolsLazy = false;

	/** Active flag. */
	protected boolean m_active = false;

	/** The site I belong to. */
	protected Site m_site = null;

	/** The site id I belong to, in case I have no m_site. */
	protected String m_siteId = null;

	/** The site skin, in case I have no m_site. */
	protected String m_skin = null;

	/**
	 * Construct. Auto-generate the id.
	 * 
	 * @param site
	 *        The site in which this page lives.
	 */
	protected BaseSitePage(Site site)
	{
		m_site = site;
		m_id = IdService.getUniqueId();
		m_properties = new BaseResourcePropertiesEdit();
		m_tools = new ResourceVector();
	}

	/**
	 * ReConstruct
	 * 
	 * @param site
	 *        The site in which this page lives.
	 * @param id
	 *        The page id.
	 * @param title
	 *        The page title.
	 * @param layout
	 *        The layout as a string ("0" or not currently supported).
	 */
	protected BaseSitePage(Site site, String id, String title, String layout)
	{
		m_site = site;
		m_id = id;

		m_properties = new BaseResourcePropertiesEdit();
		((BaseResourcePropertiesEdit) m_properties).setLazy(true);

		m_tools = new ResourceVector();
		m_toolsLazy = true;

		m_title = title;

		if (layout.equals(String.valueOf(LAYOUT_SINGLE_COL)))
			m_layout = LAYOUT_SINGLE_COL;
		else if (layout.equals(String.valueOf(LAYOUT_DOUBLE_COL))) m_layout = LAYOUT_DOUBLE_COL;
	}

	/**
	 * ReConstruct - if we don't have a site to follow up to get to certain site info.
	 * 
	 * @param site
	 *        The site in which this page lives.
	 * @param id
	 *        The page id.
	 * @param title
	 *        The page title.
	 * @param layout
	 *        The layout as a string ("0" or not currently supported).
	 */
	protected BaseSitePage(String pageId, String title, String layout, String siteId, String skin)
	{
		m_site = null;
		m_id = pageId;

		m_properties = new BaseResourcePropertiesEdit();
		((BaseResourcePropertiesEdit) m_properties).setLazy(true);

		m_tools = new ResourceVector();
		m_toolsLazy = true;

		m_title = title;

		if (layout.equals(String.valueOf(LAYOUT_SINGLE_COL)))
			m_layout = LAYOUT_SINGLE_COL;
		else if (layout.equals(String.valueOf(LAYOUT_DOUBLE_COL))) m_layout = LAYOUT_DOUBLE_COL;

		m_siteId = siteId;
		m_skin = skin;
	}

	/**
	 * Construct as a copy of another.
	 * 
	 * @param other
	 *        The other to copy.
	 * @param site
	 *        The site in which this page lives.
	 * @param exact
	 *        If true, we copy ids - else we generate new ones for page and tools.
	 */
	protected BaseSitePage(SitePage other, Site site, boolean exact)
	{
		BaseSitePage bOther = (BaseSitePage) other;

		m_site = site;

		if (exact)
		{
			m_id = bOther.m_id;
		}
		else
		{
			m_id = IdService.getUniqueId();
		}
		m_title = bOther.m_title;
		m_layout = bOther.m_layout;

		m_properties = new BaseResourcePropertiesEdit();
		ResourceProperties pOther = other.getProperties();
		Iterator l = pOther.getPropertyNames();
		while (l.hasNext())
		{
			String pOtherName = (String) l.next();
			// TODO: why this replaceAll? When is the site id in a page property? if exact, it's a big waste... - ggolden
			m_properties.addProperty(pOtherName, pOther.getProperty(pOtherName).replaceAll(bOther.getSiteId(), getSiteId()));
		}
		((BaseResourcePropertiesEdit) m_properties).setLazy(((BaseResourceProperties) other.getProperties()).isLazy());

		// deep copy the tools
		m_tools = new ResourceVector();
		for (Iterator iTools = bOther.getTools().iterator(); iTools.hasNext();)
		{
			BaseToolConfiguration tool = (BaseToolConfiguration) iTools.next();
			m_tools.add(new BaseToolConfiguration(tool, this, exact));
		}
		m_toolsLazy = ((BaseSitePage) other).m_toolsLazy;

		m_siteId = bOther.m_siteId;
		m_skin = bOther.m_skin;
	}

	/**
	 * Construct from XML element.
	 * 
	 * @param el
	 *        The XML element.
	 * @param site
	 *        The site in which this page lives.
	 */
	protected BaseSitePage(Element el, Site site)
	{
		m_site = site;

		// setup for properties
		m_properties = new BaseResourcePropertiesEdit();

		// setup for page list
		m_tools = new ResourceVector();

		m_id = el.getAttribute("id");
		m_title = StringUtil.trimToNull(el.getAttribute("title"));
		try
		{
			m_layout = Integer.parseInt(StringUtil.trimToNull(el.getAttribute("layout")));
		}
		catch (Exception e)
		{
		}

		// the children (properties and page list)
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

			// look for the tool list
			else if (element.getTagName().equals("tools"))
			{
				NodeList toolsNodes = element.getChildNodes();
				for (int t = 0; t < toolsNodes.getLength(); t++)
				{
					Node toolNode = toolsNodes.item(t);
					if (toolNode.getNodeType() != Node.ELEMENT_NODE) continue;
					Element toolEl = (Element) toolNode;
					if (!toolEl.getTagName().equals("tool")) continue;

					BaseToolConfiguration tool = new BaseToolConfiguration(toolEl, this);
					m_tools.add(tool);
				}
			}
		}
	}

	/**
	 * @inheritDoc
	 */
	public String getTitle()
	{
		return m_title;
	}

	/**
	 * @inheritDoc
	 */
	public int getLayout()
	{
		return m_layout;
	}

	/**
	 * @inheritDoc
	 */
	public String getSkin()
	{
		if (m_site != null)
		{
			return ((BaseSiteService) (SiteService.getInstance())).adjustSkin(m_site.getSkin(), m_site.isPublished());
		}

		return m_skin;
	}

	/**
	 * @inheritDoc
	 */
	public String getSiteId()
	{
		if (m_site != null)
		{
			return m_site.getId();
		}

		return m_siteId;
	}

	/**
	 * @inheritDoc
	 */
	public boolean isPopUp()
	{
		// TODO:
		return false;
	}

	/**
	 * @inheritDoc
	 */
	public String getLayoutTitle()
	{
		return LAYOUT_NAMES[m_layout];
	}

	/**
	 * @inheritDoc
	 */
	public List getTools()
	{
		if (m_toolsLazy)
		{
			((BaseSiteService) (SiteService.getInstance())).m_storage.readPageTools(this, m_tools);
			m_toolsLazy = false;
		}

		// TODO: need to sort by layout hint
		return m_tools;
	}

	/**
	 * @inheritDoc
	 */
	public Collection getTools(String[] toolIds)
	{
		List rv = new Vector();
		if ((toolIds == null) || (toolIds.length == 0)) return rv;

		for (Iterator iTools = getTools().iterator(); iTools.hasNext();)
		{
			ToolConfiguration tc = (ToolConfiguration) iTools.next();
			Tool tool = tc.getTool();
			if ((tool != null) && (tool.getId() != null))
			{
				for (int i = 0; i < toolIds.length; i++)
				{
					if (tool.getId().equals(toolIds[i]))
					{
						rv.add(tc);
					}
				}
			}
		}

		return rv;
	}

	/**
	 * @inheritDoc
	 */
	public List getTools(int col)
	{
		// TODO: need to sort by layout hint
		List rv = new Vector();
		for (Iterator iTools = getTools().iterator(); iTools.hasNext();)
		{
			ToolConfiguration tc = (ToolConfiguration) iTools.next();
			// row, col
			int[] layout = tc.parseLayoutHints();
			if (layout != null)
			{
				if (layout[1] == col)
				{
					rv.add(tc);
				}
			}
			// else consider it part of the 0 column
			else if (col == 0)
			{
				rv.add(tc);
			}
		}
		return rv;
	}

	/**
	 * @inheritDoc
	 */
	public ToolConfiguration getTool(String id)
	{
		return (ToolConfiguration) ((ResourceVector) getTools()).getById(id);
	}

	/**
	 * @inheritDoc
	 */
	public void setTitle(String title)
	{
		m_title = StringUtil.trimToNull(title);
	}

	/**
	 * @inheritDoc
	 */
	public void setLayout(int layout)
	{
		if ((layout == LAYOUT_SINGLE_COL) || (layout == LAYOUT_DOUBLE_COL))
		{
			m_layout = layout;
		}
		else
			M_log.warn("setLayout(): set to invalid value: " + layout);
	}

	/**
	 * @inheritDoc
	 */
	public ToolConfiguration addTool()
	{
		BaseToolConfiguration tool = new BaseToolConfiguration(this);
		((ResourceVector) getTools()).add(tool);

		return tool;
	}

	/**
	 * @inheritDoc
	 */
	public ToolConfiguration addTool(Tool reg)
	{
		BaseToolConfiguration tool = new BaseToolConfiguration(reg, this);
		((ResourceVector) getTools()).add(tool);

		return tool;
	}

	/**
	 * @inheritDoc
	 */
	public void removeTool(ToolConfiguration tool)
	{
		((ResourceVector) getTools()).remove(tool);
	}

	/**
	 * @inheritDoc
	 */
	public void moveUp()
	{
		if (m_site == null) return;
		((ResourceVector) m_site.getPages()).moveUp(this);
	}

	/**
	 * @inheritDoc
	 */
	public void moveDown()
	{
		if (m_site == null) return;
		((ResourceVector) m_site.getPages()).moveDown(this);
	}

	/**
	 * @inheritDoc
	 */
	public ResourcePropertiesEdit getPropertiesEdit()
	{
		if (((BaseResourceProperties) m_properties).isLazy())
		{
			((BaseSiteService) (SiteService.getInstance())).m_storage.readPageProperties(this, m_properties);
			((BaseResourcePropertiesEdit) m_properties).setLazy(false);
		}

		return m_properties;
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
	public String getUrl()
	{
		String rv = null;
		if (m_site == null)
		{
			rv = ((BaseSiteService) (SiteService.getInstance())).m_serverConfigurationService.getPortalUrl()
					+ ((BaseSiteService) (SiteService.getInstance())).sitePageReference(m_siteId, m_id);
		}

		rv = ((BaseSiteService) (SiteService.getInstance())).m_serverConfigurationService.getPortalUrl()
				+ ((BaseSiteService) (SiteService.getInstance())).sitePageReference(m_site.getId(), m_id);

		return rv;
	}

	/**
	 * @inheritDoc
	 */
	public String getReference()
	{
		if (m_site == null)
		{
			return ((BaseSiteService) (SiteService.getInstance())).sitePageReference(m_siteId, m_id);
		}

		return ((BaseSiteService) (SiteService.getInstance())).sitePageReference(m_site.getId(), m_id);
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
	public Site getContainingSite()
	{
		return m_site;
	}

	/**
	 * @inheritDoc
	 */
	public ResourceProperties getProperties()
	{
		if (((BaseResourceProperties) m_properties).isLazy())
		{
			((BaseSiteService) (SiteService.getInstance())).m_storage.readPageProperties(this, m_properties);
			((BaseResourcePropertiesEdit) m_properties).setLazy(false);
		}

		return m_properties;
	}

	/**
	 * @inheritDoc
	 */
	public Element toXml(Document doc, Stack stack)
	{
		Element page = doc.createElement("page");
		((Element) stack.peek()).appendChild(page);

		page.setAttribute("id", getId());
		if (m_title != null) page.setAttribute("title", m_title);
		page.setAttribute("layout", Integer.toString(m_layout));

		// properties
		stack.push(page);
		getProperties().toXml(doc, stack);
		stack.pop();

		// tools
		Element list = doc.createElement("tools");
		page.appendChild(list);
		stack.push(list);
		for (Iterator iTools = getTools().iterator(); iTools.hasNext();)
		{
			BaseToolConfiguration tool = (BaseToolConfiguration) iTools.next();
			tool.toXml(doc, stack);
		}
		stack.pop();

		return page;
	}
}
