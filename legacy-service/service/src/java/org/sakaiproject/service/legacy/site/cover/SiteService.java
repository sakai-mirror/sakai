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

package org.sakaiproject.service.legacy.site.cover;

import org.sakaiproject.service.framework.component.cover.ComponentManager;

/**
* <p>SiteService is a static Cover for the {@link org.sakaiproject.service.legacy.site.SiteService SiteService};
* see that interface for usage details.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
*/
public class SiteService
{
	/**
	 * Access the component instance: special cover only method.
	 * @return the component instance.
	 */
	public static org.sakaiproject.service.legacy.site.SiteService getInstance()
	{
		if (ComponentManager.CACHE_SINGLETONS)
		{
			if (m_instance == null) m_instance = (org.sakaiproject.service.legacy.site.SiteService) ComponentManager.get(org.sakaiproject.service.legacy.site.SiteService.class);
			return m_instance;
		}
		else
		{
			return (org.sakaiproject.service.legacy.site.SiteService) ComponentManager.get(org.sakaiproject.service.legacy.site.SiteService.class);
		}
	}
	private static org.sakaiproject.service.legacy.site.SiteService m_instance = null;

	public static java.lang.String SERVICE_NAME = org.sakaiproject.service.legacy.site.SiteService.SERVICE_NAME;
	public static java.lang.String REFERENCE_ROOT = org.sakaiproject.service.legacy.site.SiteService.REFERENCE_ROOT;
	public static java.lang.String SITE_VISIT = org.sakaiproject.service.legacy.site.SiteService.SITE_VISIT;
	public static java.lang.String SITE_VISIT_UNPUBLISHED = org.sakaiproject.service.legacy.site.SiteService.SITE_VISIT_UNPUBLISHED;
	public static java.lang.String SECURE_ADD_SITE = org.sakaiproject.service.legacy.site.SiteService.SECURE_ADD_SITE;
	public static java.lang.String SECURE_ADD_USER_SITE = org.sakaiproject.service.legacy.site.SiteService.SECURE_ADD_USER_SITE;
	public static java.lang.String SECURE_REMOVE_SITE = org.sakaiproject.service.legacy.site.SiteService.SECURE_REMOVE_SITE;
	public static java.lang.String SECURE_UPDATE_SITE = org.sakaiproject.service.legacy.site.SiteService.SECURE_UPDATE_SITE;
	public static java.lang.String SECURE_VIEW_ROSTER = org.sakaiproject.service.legacy.site.SiteService.SECURE_VIEW_ROSTER;
	public static java.lang.String MAIN_CONTAINER = org.sakaiproject.service.legacy.site.SiteService.MAIN_CONTAINER;
	public static java.lang.String SITE_TEMPLATE = org.sakaiproject.service.legacy.site.SiteService.SITE_TEMPLATE;
	public static java.lang.String USER_SITE_TEMPLATE = org.sakaiproject.service.legacy.site.SiteService.USER_SITE_TEMPLATE;
	public static java.lang.String SITE_ERROR = org.sakaiproject.service.legacy.site.SiteService.SITE_ERROR;
	public static java.lang.String URL_ERROR = org.sakaiproject.service.legacy.site.SiteService.URL_ERROR;

	public static java.lang.String SITE_SUBTYPE = org.sakaiproject.service.legacy.site.SiteService.SITE_SUBTYPE;
	public static java.lang.String SECTION_SUBTYPE = org.sakaiproject.service.legacy.site.SiteService.SECTION_SUBTYPE;
	public static java.lang.String PAGE_SUBTYPE = org.sakaiproject.service.legacy.site.SiteService.PAGE_SUBTYPE;
	public static java.lang.String TOOL_SUBTYPE = org.sakaiproject.service.legacy.site.SiteService.TOOL_SUBTYPE;

	public static boolean allowAccessSite(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return false;

		return service.allowAccessSite(param0);
	}

	public static org.sakaiproject.service.legacy.site.Site getSite(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return null;

		return service.getSite(param0);
	}

	public static org.sakaiproject.service.legacy.site.Site getSiteVisit(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return null;

		return service.getSiteVisit(param0);
	}

	public static boolean allowUpdateSite(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return false;

		return service.allowUpdateSite(param0);
	}

	public static void save(org.sakaiproject.service.legacy.site.Site param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return;

		service.save(param0);
	}

	public static void saveSiteInfo(java.lang.String param0, java.lang.String param1, java.lang.String param2) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return;

		service.saveSiteInfo(param0, param1, param2);
	}

	public static boolean allowAddSite(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return false;

		return service.allowAddSite(param0);
	}

	public static org.sakaiproject.service.legacy.site.Site addSite(java.lang.String param0, java.lang.String param1) throws org.sakaiproject.exception.IdInvalidException, org.sakaiproject.exception.IdUsedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return null;

		return service.addSite(param0, param1);
	}

	public static org.sakaiproject.service.legacy.site.Site addSite(java.lang.String param0, org.sakaiproject.service.legacy.site.Site param1) throws org.sakaiproject.exception.IdInvalidException, org.sakaiproject.exception.IdUsedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return null;

		return service.addSite(param0, param1);
	}
	
	public static boolean allowRemoveSite(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return false;

		return service.allowRemoveSite(param0);
	}

	public static void removeSite(org.sakaiproject.service.legacy.site.Site param0) throws org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return;

		service.removeSite(param0);
	}

	public static java.lang.String siteReference(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return null;

		return service.siteReference(param0);
	}

	public static java.lang.String sitePageReference(java.lang.String param0, java.lang.String param1)
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return null;

		return service.sitePageReference(param0, param1);
	}

	public static java.lang.String siteToolReference(java.lang.String param0, java.lang.String param1)
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return null;

		return service.siteToolReference(param0, param1);
	}

	public static java.lang.String siteSectionReference(java.lang.String param0, java.lang.String param1)
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return null;

		return service.siteSectionReference(param0, param1);
	}

	public static boolean isUserSite(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return false;

		return service.isUserSite(param0);
	}

	public static java.lang.String getSiteUserId(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return null;

		return service.getSiteUserId(param0);
	}

	public static java.lang.String getUserSiteId(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return null;

		return service.getUserSiteId(param0);
	}

	public static boolean isSpecialSite(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return false;

		return service.isSpecialSite(param0);
	}

	public static java.lang.String getSiteSpecialId(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return null;

		return service.getSiteSpecialId(param0);
	}

	public static java.lang.String getSpecialSiteId(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return null;

		return service.getSpecialSiteId(param0);
	}

	public static java.lang.String getSiteDisplay(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return null;

		return service.getSiteDisplay(param0);
	}

	public static org.sakaiproject.service.legacy.site.ToolConfiguration findTool(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return null;

		return service.findTool(param0);
	}

	public static org.sakaiproject.service.legacy.site.SitePage findPage(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return null;

		return service.findPage(param0);
	}

	public static boolean allowViewRoster(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return false;

		return service.allowViewRoster(param0);
	}

	public static void unjoin(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.InUseException
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return;

		service.unjoin(param0);
	}
	
	public static boolean allowUnjoinSite(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return false;

		return service.allowUnjoinSite(param0);
	}

	public static java.lang.String getSiteSkin(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return null;

		return service.getSiteSkin(param0);
	}

	public static java.util.List getSiteTypes()
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return null;

		return service.getSiteTypes();
	}

	public static java.util.List getSites(org.sakaiproject.service.legacy.site.SiteService.SelectionType param0, java.lang.Object param1, java.lang.String param2, java.util.Map param3, org.sakaiproject.service.legacy.site.SiteService.SortType param4, org.sakaiproject.javax.PagingPosition param5)
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return null;

		return service.getSites(param0, param1, param2, param3, param4, param5);
	}

	public static int countSites(org.sakaiproject.service.legacy.site.SiteService.SelectionType param0, java.lang.Object param1, java.lang.String param2, java.util.Map param3)
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return 0;

		return service.countSites(param0, param1, param2, param3);
	}

	public static void setSiteSecurity(java.lang.String param0, java.util.Set param1, java.util.Set param2, java.util.Set param3)
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return;

		service.setSiteSecurity(param0, param1, param2, param3);
	}

	public static void setUserSecurity(java.lang.String param0, java.util.Set param1, java.util.Set param2, java.util.Set param3)
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return;

		service.setUserSecurity(param0, param1, param2, param3);
	}

	public static void join(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.InUseException
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return;

		service.join(param0);
	}
	
	public static java.lang.String merge(java.lang.String param0, org.w3c.dom.Element param1, java.lang.String param2 )
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return null;
		
		return service.merge(param0, param1, param2);
	}

	public static org.sakaiproject.service.legacy.site.Section findSection(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.site.SiteService service = getInstance();
		if (service == null)
			return null;
		
		return service.findSection(param0);
	}
}
