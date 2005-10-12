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

import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.service.legacy.entity.ResourceProperties;
import org.sakaiproject.service.legacy.entity.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.id.cover.IdService;
import org.sakaiproject.service.legacy.site.Section;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.util.resource.BaseResourceProperties;
import org.sakaiproject.util.resource.BaseResourcePropertiesEdit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <p>
 * BaseSection is an implementation of the Site API Section.
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public class BaseSection implements Section, Identifiable
{
	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(BaseSection.class);

	/** A fixed class serian number. */
	private static final long serialVersionUID = 1L;

	/** The title. */
	protected String m_title = null;

	/** The description. */
	protected String m_description = null;

	/** The site id. */
	protected String m_id = null;

	/** The properties. */
	protected ResourcePropertiesEdit m_properties = null;

	/** The site I belong to. */
	protected Site m_site = null;

	/** The site id I belong to, in case I have no m_site. */
	protected String m_siteId = null;

	/**
	 * Construct. Auto-generate the id.
	 * 
	 * @param site
	 *        The site in which this page lives.
	 */
	protected BaseSection(Site site)
	{
		m_site = site;
		m_id = IdService.getUniqueId();
		m_properties = new BaseResourcePropertiesEdit();
	}

	protected BaseSection(String id, String title, String description, Site site)
	{
		m_id = id;
		m_title = title;
		m_description = description;
		m_site = site;
		m_properties = new BaseResourcePropertiesEdit();
	}

	protected BaseSection(String id, String title, String description, String siteId)
	{
		m_id = id;
		m_title = title;
		m_description = description;
		m_siteId = siteId;
		m_properties = new BaseResourcePropertiesEdit();
	}

	/**
	 * Construct as a copy of another.
	 * 
	 * @param other
	 *        The other to copy.
	 * @param site
	 *        The site in which this section lives.
	 * @param exact
	 *        If true, we copy id - else we generate a new one.
	 */
	protected BaseSection(Section other, Site site, boolean exact)
	{
		BaseSection bOther = (BaseSection) other;

		m_site = (Site) site;
		m_siteId = bOther.m_siteId;

		if (exact)
		{
			m_id = bOther.m_id;
		}
		else
		{
			m_id = IdService.getUniqueId();
		}

		m_title = bOther.m_title;
		m_description = bOther.m_description;

		m_properties = new BaseResourcePropertiesEdit();
		m_properties.addAll(other.getProperties());
		((BaseResourcePropertiesEdit) m_properties).setLazy(((BaseResourceProperties) other.getProperties()).isLazy());
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
	public String getDescription()
	{
		return m_description;
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
	public void setTitle(String title)
	{
		m_title = title;
	}

	/**
	 * @inheritDoc
	 */
	public void setDescription(String description)
	{
		m_description = description;
	}

	/**
	 * @inheritDoc
	 */
	public String getUrl()
	{
		return null;
	}

	/**
	 * @inheritDoc
	 */
	public String getReference()
	{
		if (m_site != null)
		{
			return ((BaseSiteService) (SiteService.getInstance())).siteSectionReference(m_site.getId(), getId());
		}

		return ((BaseSiteService) (SiteService.getInstance())).siteSectionReference(m_siteId, getId());
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
	public ResourceProperties getProperties()
	{
		return m_properties;
	}

	/**
	 * @inheritDoc
	 */
	public Element toXml(Document doc, Stack stack)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @inheritDoc
	 */
	public boolean isActiveEdit()
	{
		return true;
	}

	/**
	 * @inheritDoc
	 */
	public ResourcePropertiesEdit getPropertiesEdit()
	{
		return m_properties;
	}

	/**
	 * @inheritDoc
	 */
	public String toString()
	{
		return m_title + " (" + m_id + ")";
	}
}
