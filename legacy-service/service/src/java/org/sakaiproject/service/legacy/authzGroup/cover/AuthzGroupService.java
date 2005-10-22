/**********************************************************************************
* $URL$
* $Id$
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

package org.sakaiproject.service.legacy.authzGroup.cover;

import org.sakaiproject.service.framework.component.cover.ComponentManager;

/**
* <p>AuthzGroupService is a static Cover for the {@link org.sakaiproject.service.legacy.authzGroup.AuthzGroupService AuthzGroupService};
* see that interface for usage details.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
*/
public class AuthzGroupService
{
	/**
	 * Access the component instance: special cover only method.
	 * @return the component instance.
	 */
	public static org.sakaiproject.service.legacy.authzGroup.AuthzGroupService getInstance()
	{
		if (ComponentManager.CACHE_SINGLETONS)
		{
			if (m_instance == null) m_instance = (org.sakaiproject.service.legacy.authzGroup.AuthzGroupService) ComponentManager.get(org.sakaiproject.service.legacy.authzGroup.AuthzGroupService.class);
			return m_instance;
		}
		else
		{
			return (org.sakaiproject.service.legacy.authzGroup.AuthzGroupService) ComponentManager.get(org.sakaiproject.service.legacy.authzGroup.AuthzGroupService.class);
		}
	}
	private static org.sakaiproject.service.legacy.authzGroup.AuthzGroupService m_instance = null;

	public static java.lang.String SERVICE_NAME = org.sakaiproject.service.legacy.authzGroup.AuthzGroupService.SERVICE_NAME;
	public static java.lang.String REFERENCE_ROOT = org.sakaiproject.service.legacy.authzGroup.AuthzGroupService.REFERENCE_ROOT;
	public static java.lang.String SECURE_ADD_AUTHZ_GROUP = org.sakaiproject.service.legacy.authzGroup.AuthzGroupService.SECURE_ADD_AUTHZ_GROUP;
	public static java.lang.String SECURE_REMOVE_AUTHZ_GROUP = org.sakaiproject.service.legacy.authzGroup.AuthzGroupService.SECURE_REMOVE_AUTHZ_GROUP;
	public static java.lang.String SECURE_UPDATE_AUTHZ_GROUP = org.sakaiproject.service.legacy.authzGroup.AuthzGroupService.SECURE_UPDATE_AUTHZ_GROUP;
	public static java.lang.String SECURE_UPDATE_OWN_AUTHZ_GROUP = org.sakaiproject.service.legacy.authzGroup.AuthzGroupService.SECURE_UPDATE_OWN_AUTHZ_GROUP;
	public static java.lang.String ANON_ROLE = org.sakaiproject.service.legacy.authzGroup.AuthzGroupService.ANON_ROLE;
	public static java.lang.String AUTH_ROLE = org.sakaiproject.service.legacy.authzGroup.AuthzGroupService.AUTH_ROLE;

	public static java.util.List getAuthzGroups(java.lang.String param0, org.sakaiproject.javax.PagingPosition param1)
	{
		org.sakaiproject.service.legacy.authzGroup.AuthzGroupService service = getInstance();
		if (service == null)
			return null;

		return service.getAuthzGroups(param0, param1);
	}

	public static int countAuthzGroups(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.authzGroup.AuthzGroupService service = getInstance();
		if (service == null)
			return 0;

		return service.countAuthzGroups(param0);
	}

	public static org.sakaiproject.service.legacy.authzGroup.AuthzGroup getAuthzGroup(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException
	{
		org.sakaiproject.service.legacy.authzGroup.AuthzGroupService service = getInstance();
		if (service == null)
			return null;

		return service.getAuthzGroup(param0);
	}

	public static boolean allowUpdate(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.authzGroup.AuthzGroupService service = getInstance();
		if (service == null)
			return false;

		return service.allowUpdate(param0);
	}

	public static void save(org.sakaiproject.service.legacy.authzGroup.AuthzGroup param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.authzGroup.AuthzGroupService service = getInstance();
		if (service == null)
			return;

		service.save(param0);
	}

	public static void savePostSecurity(org.sakaiproject.service.legacy.authzGroup.AuthzGroup param0) throws org.sakaiproject.exception.IdUnusedException
	{
		org.sakaiproject.service.legacy.authzGroup.AuthzGroupService service = getInstance();
		if (service == null)
			return;

		service.savePostSecurity(param0);
	}

	public static boolean allowAdd(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.authzGroup.AuthzGroupService service = getInstance();
		if (service == null)
			return false;

		return service.allowAdd(param0);
	}

	public static org.sakaiproject.service.legacy.authzGroup.AuthzGroup addAuthzGroup(java.lang.String param0) throws org.sakaiproject.exception.IdInvalidException, org.sakaiproject.exception.IdUsedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.authzGroup.AuthzGroupService service = getInstance();
		if (service == null)
			return null;

		return service.addAuthzGroup(param0);
	}

	public static org.sakaiproject.service.legacy.authzGroup.AuthzGroup addAuthzGroup(java.lang.String param0, org.sakaiproject.service.legacy.authzGroup.AuthzGroup param1, java.lang.String param2) throws org.sakaiproject.exception.IdInvalidException, org.sakaiproject.exception.IdUsedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.authzGroup.AuthzGroupService service = getInstance();
		if (service == null)
			return null;

		return service.addAuthzGroup(param0, param1, param2);
	}

	public static org.sakaiproject.service.legacy.authzGroup.AuthzGroup newAuthzGroup(java.lang.String param0, org.sakaiproject.service.legacy.authzGroup.AuthzGroup param1, java.lang.String param2) throws org.sakaiproject.exception.IdUsedException
	{
		org.sakaiproject.service.legacy.authzGroup.AuthzGroupService service = getInstance();
		if (service == null)
			return null;

		return service.newAuthzGroup(param0, param1, param2);
	}

	public static boolean allowRemove(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.authzGroup.AuthzGroupService service = getInstance();
		if (service == null)
			return false;

		return service.allowRemove(param0);
	}

	public static void removeAuthzGroup(org.sakaiproject.service.legacy.authzGroup.AuthzGroup param0) throws org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.authzGroup.AuthzGroupService service = getInstance();
		if (service == null)
			return;

		service.removeAuthzGroup(param0);
	}

	public static void removeAuthzGroup(java.lang.String param0) throws org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.authzGroup.AuthzGroupService service = getInstance();
		if (service == null)
			return;

		service.removeAuthzGroup(param0);
	}

	public static java.lang.String authzGroupReference(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.authzGroup.AuthzGroupService service = getInstance();
		if (service == null)
			return null;

		return service.authzGroupReference(param0);
	}

	public static void joinGroup(java.lang.String param0, java.lang.String param1) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.authzGroup.AuthzGroupService service = getInstance();
		if (service == null)
			return;

		service.joinGroup(param0, param1);
	}

	public static void unjoinGroup(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.authzGroup.AuthzGroupService service = getInstance();
		if (service == null)
			return;

		service.unjoinGroup(param0);
	}
	
	public static boolean allowJoinGroup(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.authzGroup.AuthzGroupService service = getInstance();
		if (service == null)
			return false;

		return service.allowJoinGroup(param0);
	}

	public static boolean allowUnjoinGroup(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.authzGroup.AuthzGroupService service = getInstance();
		if (service == null)
			return false;

		return service.allowUnjoinGroup(param0);
	}

	public static java.util.Set getUsersIsAllowed(java.lang.String param0, java.util.Collection param1)
	{
		org.sakaiproject.service.legacy.authzGroup.AuthzGroupService service = getInstance();
		if (service == null)
			return null;

		return service.getUsersIsAllowed(param0, param1);
	}

	public static java.util.Set getAuthzGroupsIsAllowed(java.lang.String param0, java.lang.String param1, java.util.Collection param2)
	{
		org.sakaiproject.service.legacy.authzGroup.AuthzGroupService service = getInstance();
		if (service == null)
			return null;

		return service.getAuthzGroupsIsAllowed(param0, param1, param2);
	}

	public static java.util.Set getAllowedFunctions(java.lang.String param0, java.util.Collection param1)
	{
		org.sakaiproject.service.legacy.authzGroup.AuthzGroupService service = getInstance();
		if (service == null)
			return null;

		return service.getAllowedFunctions(param0, param1);
	}

	public static void refreshUser(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.authzGroup.AuthzGroupService service = getInstance();
		if (service == null)
			return;

		service.refreshUser(param0);
	}

	public static boolean isAllowed(java.lang.String param0, java.lang.String param1, java.util.Collection param2)
	{
		org.sakaiproject.service.legacy.authzGroup.AuthzGroupService service = getInstance();
		if (service == null)
			return false;

		return service.isAllowed(param0, param1, param2);
	}

	public static boolean isAllowed(java.lang.String param0, java.lang.String param1, java.lang.String param2)
	{
		org.sakaiproject.service.legacy.authzGroup.AuthzGroupService service = getInstance();
		if (service == null)
			return false;

		return service.isAllowed(param0, param1, param2);
	}

	public static java.lang.String getUserRole(java.lang.String param0, java.lang.String param1)
	{
		org.sakaiproject.service.legacy.authzGroup.AuthzGroupService service = getInstance();
		if (service == null)
			return null;

		return service.getUserRole(param0, param1);
	}

	public static java.util.Map getUsersRole(java.util.Collection param0, java.lang.String param1)
	{
		org.sakaiproject.service.legacy.authzGroup.AuthzGroupService service = getInstance();
		if (service == null)
			return null;

		return service.getUsersRole(param0, param1);
	}
}
