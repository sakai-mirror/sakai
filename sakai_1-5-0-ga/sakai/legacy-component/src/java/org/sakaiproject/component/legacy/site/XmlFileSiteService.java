/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/site/XmlFileSiteService.java,v 1.15 2004/12/20 04:40:27 ggolden.umich.edu Exp $
*
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

// package
package org.sakaiproject.component.legacy.site;

// import
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.javax.PagingPosition;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.legacy.realm.cover.RealmService;
import org.sakaiproject.service.legacy.resource.ResourceProperties;
import org.sakaiproject.service.legacy.resource.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SiteEdit;
import org.sakaiproject.service.legacy.site.SitePage;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.util.storage.BaseXmlFileStorage;
import org.sakaiproject.util.storage.StorageUser;

/**
* <p>XmlFileSiteService is an extension of the BaseSiteService  with a in-memory xml file backed up
* storage.  The full set of sites are read in at startup, kept in memory, and
* written out at shutdown.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.15 $
* @see org.chefproject.service.component.BaseSiteService
*/
public class XmlFileSiteService
	extends BaseSiteService
{
	/** A full path and file name to the storage file. */
	protected String m_storagePath = "db/site.xml";

	/*******************************************************************************
	* Constructors, Dependencies and their setter methods
	*******************************************************************************/

	/**
	 * Configuration: set the storage path
	 * @param path The storage path.
	 */
	public void setStoragePath(String path)
	{
		m_storagePath = path;
	}

	/*******************************************************************************
	* Init and Destroy
	*******************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		try
		{
			super.init();

			m_logger.info(this +".init(): storage path: " + m_storagePath);

		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}

	} // init

	/*******************************************************************************
	* BaseSiteService extensions
	*******************************************************************************/

	/**
	* Construct a Storage object.
	* @return The new storage object.
	*/
	protected Storage newStorage()
	{
		return new XmlFileStorage(this);

	}	// newStorage

	/*******************************************************************************
	* Storage implementation
	*******************************************************************************/

	protected class XmlFileStorage
		extends BaseXmlFileStorage
		implements Storage
	{
		/**
		* Construct.
		* @param user The StorageUser class to call back for creation of Resource and Edit objects.
		*/
		public XmlFileStorage(StorageUser user)
		{
			super(m_storagePath, "sites", null, "site", user);

		}	// XmlFileStorage

		public boolean check(String id) { return super.checkResource(null, id); }

		public Site get(String id) { return (Site) super.getResource(null, id); }

		public List getAll() { return super.getAllResources(null); }

		public SiteEdit put(String id) { return (SiteEdit) super.putResource(null, id, null); }

		public SiteEdit edit(String id) { return (SiteEdit) super.editResource(null, id); }

		public void commit(SiteEdit edit) { super.commitResource(null, edit); }

		public void cancel(SiteEdit edit) { super.cancelResource(null, edit); }

		public void remove(SiteEdit edit) { super.removeResource(null, edit); }

		public int count()
		{
			return super.countAllResources(null);
		}

		/**
		 * {@inheritDoc}
		 */
		public List getSites(SelectionType type, Object ofType, String criteria, Map propertyCriteria, SortType sort, PagingPosition page)
		{
			// return list
			List rv = new Vector();

			// collect this user's update, unpublished visit and visit realms to use instead of unlockCheck
			// which will also consider anon and auth grants which we don't want here
			String userId = UsageSessionService.getSessionUserId();
			Set updRealms = null;
			Set unpRealms = null;
			Set visitRealms = null;
			if ((type == SelectionType.ACCESS) || (type == SelectionType.JOINABLE))
			{
				unpRealms = RealmService.unlockRealms(userId, SITE_VISIT_UNPUBLISHED);
				visitRealms = RealmService.unlockRealms(userId, SITE_VISIT);
			}
			else if (type == SelectionType.UPDATE)
			{
				updRealms = RealmService.unlockRealms(userId, SECURE_UPDATE_SITE);
			}

			// start with all sites
			List sites = getAll();
			for (Iterator iSites = sites.iterator(); iSites.hasNext();)
			{
				Site site = (Site) iSites.next();

				// reject myWorkspace (user) sites
				if (type.isIgnoreUser() && isUserSite(site.getId()))
					continue;

				// reject special sites
				if (type.isIgnoreSpecial() && isSpecialSite(site.getId()))
					continue;

				// reject unpublished sites
				if (type.isIgnoreUnpublished() && (!site.isPublished()))
					continue;

				// reject unwanted site types
				if ((ofType != null) && (!site.isType(ofType)))
				{
					continue;
				}

				// reject non-joinable sites
				if ((type == SelectionType.JOINABLE) && (!site.isJoinable()))
				{
					continue;
				}

				boolean selected = true;

				// check for current user having update permission to site
				if (type == SelectionType.UPDATE)
				{
//					if (!unlockCheck(SECURE_UPDATE_SITE, site.getReference()))
					if (!updRealms.contains(site.getReference()))
						continue;
				}

				// check for current user having read permission to site
				else if ((type == SelectionType.ACCESS) || (type == SelectionType.JOINABLE))
				{
					boolean access = false;

					if (site.isPublished())
					{
//						if (unlockCheck(SITE_VISIT, site.getReference()))
						if (visitRealms.contains(site.getReference()))
							access = true;
					}
					else
					{
//						if (unlockCheck(SITE_VISIT_UNPUBLISHED, site.getReference()))
						if (unpRealms.contains(site.getReference()))
							access = true;
					}
					
					// for ACCESS, skip if the user does not have access
					if ((type == SelectionType.ACCESS) && !access)
						continue;

					// for JOINABLE, skip if the user DOES have access already
					else if ((type == SelectionType.JOINABLE) && access)
						continue;
				}

				// check for pub view status
				else if (type == SelectionType.PUBVIEW)
				{
					if (!site.isPubView())
						continue;
				}

				// check criteria, if specified
				if (criteria != null)
				{
					boolean accepted = false;

					if ((site.getTitle() != null) && (site.getTitle().indexOf(criteria) != -1))
					{
						accepted = true;
					}
					else if ((site.getShortDescription() != null) && (site.getShortDescription().indexOf(criteria) != -1))
					{
						accepted = true;
					}
					else if ((site.getDescription() != null) && (site.getDescription().indexOf(criteria) != -1))
					{
						accepted = true;
					}
					else if ((site.getId() != null) && (site.getId().indexOf(criteria) != -1))
					{
						accepted = true;
					}
					else if ((site.getSkin() != null) && (site.getSkin().indexOf(criteria) != -1))
					{
						accepted = true;
					}

					if (!accepted)
						continue;
				}
				
				// check propertyCriteria if specified
				if ((propertyCriteria != null) && (propertyCriteria.size() > 0))
				{
					boolean accepted = true;
					ResourceProperties props = site.getProperties();
					
					// for each given property criteria
					for (Iterator i = propertyCriteria.entrySet().iterator(); i.hasNext();)
					{
						Map.Entry entry = (Map.Entry) i.next();
						String matchValue = (String) entry.getValue();
						boolean thisAccepted = false;

						List values = props.getPropertyList((String) entry.getKey());
						if ((values == null) || (values.size() == 0))
						{
							accepted = false;
							break;
						}

						// see if any value of this property matches
						for (Iterator v = values.iterator(); v.hasNext();)
						{
							String propValue = (String) v.next();
							if (propValue.indexOf(matchValue) != -1)
							{
								// we have satisfied this one
								thisAccepted = true;
								break;
							}
						}

						// if we didn't meet this one, we are done
						if (!thisAccepted)
						{
							accepted = false;
							break;
						}
					}

					if (!accepted)
						continue;
				}

				// passed all the tests!
				rv.add(site);
			}

			// sort if requested
			if ((sort != null) && (sort != SortType.NONE) && (rv.size() > 0))
			{
				// TODO: sort by the sort spec
				Collections.sort(rv);
			}

			// paging
			if (page != null)
			{
				// adjust to the size of the set found
				page.validate(rv.size());
				rv = rv.subList(page.getFirst()-1, page.getLast());
			}

			return rv;
		}

		/**
		 * {@inheritDoc}
		 */
		public List getSiteTypes()
		{
			Set rvSet = new HashSet();

			// process all the sites
			List sites = getAll();
			for (Iterator iSites = sites.iterator(); iSites.hasNext();)
			{
				Site site = (Site) iSites.next();

				if (site.getType() != null)
				{
					rvSet.add(site.getType());
				}				
			}
			
			List rv = new Vector();
			rv.addAll(rvSet);
			
			Collections.sort(rv);
			
			return rv;
		}

		/**
		 * {@inheritDoc}
		 */
		public int countSites(SelectionType type, Object ofType, String criteria, Map propertyCriteria)
		{
			return getSites(type, ofType, criteria, propertyCriteria, SortType.NONE, null).size();
		}

		/**
		* Access the ToolConfiguration that has this id, if one is defined, else return null.
		* The tool may be on any SitePage in the site.
		* @param id The id of the tool.
		* @return The ToolConfiguration that has this id, if one is defined, else return null.
		*/
		public ToolConfiguration findTool(String id)
		{
			List sites = getAll();
			for (Iterator iSites = sites.iterator(); iSites.hasNext();)
			{
				Site site = (Site) iSites.next();
				ToolConfiguration tool = site.getTool(id);
			
				if ( tool != null )
				{
					return tool;
				}
			}

			return null;

		} // findTool

		/**
		 * {@inheritDoc}
		 */
		public SitePage findPage(String id)
		{
			List sites = getAll();
			for (Iterator iSites = sites.iterator(); iSites.hasNext();)
			{
				Site site = (Site) iSites.next();
				SitePage page = site.getPage(id);
			
				if (page != null)
				{
					return page;
				}
			}

			return null;
		}

		/**
		 * Read site properties from storage into the site's properties.
		 * @param edit The user to read properties for.
		 */
		public void readSiteProperties(Site site, ResourcePropertiesEdit props)
		{
			// they are already here!
		}

		/**
		 * Read site properties from storage into the site's properties.
		 * @param page The page for which properties are desired.
		 */
		public void readPageProperties(SitePage page, ResourcePropertiesEdit props)
		{
			// they are already here!
		}

		/**
		 * Read site properties from storage into the site's properties.
		 * @param tool The tool for which properties are desired.
		 */
		public void readToolProperties(ToolConfiguration tool, ResourcePropertiesEdit props)
		{
			// they are already here!
		}

		/**
		 * Read site pages from storage into the site's pages.
		 * @param site The site for which pages are desired.
		 */
		public void readSitePages(Site site, ResourceVector pages)
		{
			// they are already here!
		}

		/**
		 * Read site page tools from storage into the page's tools.
		 * @param page The page for which tools are desired.
		 */
		public void readPageTools(SitePage page, ResourceVector tools)
		{
			// they are already here!
		}

		/**
		 * Return the skin for this site
		 * @param siteId The site id.
		 * @return the skin for this site.
		 */
		public String getSiteSkin(String siteId)
		{
			try
			{
				Site site = getSite(siteId);
				return adjustSkin(site.getSkin(), site.isPublished());
			}
			catch (IdUnusedException e)
			{
				return adjustSkin(null, true);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public void setSiteSecurity(String siteId, Set updateUsers, Set visitUnpUsers, Set visitUsers)
		{
			// Note: no internal security needs for this component (all security is handled by the security service).
		}

		/**
		 * {@inheritDoc}
		 */
		public void setUserSecurity(String userId, Set updateSites, Set visitUnpSites, Set visitSites)
		{
			// Note: no internal security needs for this component (all security is handled by the security service).
		}

	}	// XmlFileStorage

}	// XmlFileSiteService

/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/site/XmlFileSiteService.java,v 1.15 2004/12/20 04:40:27 ggolden.umich.edu Exp $
*
**********************************************************************************/
