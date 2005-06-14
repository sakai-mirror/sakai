/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/user/cover/UserDirectoryService.java,v 1.11 2004/10/09 23:26:04 ggolden.umich.edu Exp $
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

package org.sakaiproject.service.legacy.user.cover;

import org.sakaiproject.service.framework.component.cover.ComponentManager;

/**
* <p>UserDirectoryService is a static Cover for the {@link org.sakaiproject.service.legacy.user.UserDirectoryService UserDirectoryService};
* see that interface for usage details.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.11 $
*/
public class UserDirectoryService
{
	/**
	 * Access the component instance: special cover only method.
	 * @return the component instance.
	 */
	public static org.sakaiproject.service.legacy.user.UserDirectoryService getInstance()
	{
		if (ComponentManager.CACHE_SINGLETONS)
		{
			if (m_instance == null) m_instance = (org.sakaiproject.service.legacy.user.UserDirectoryService) ComponentManager.get(org.sakaiproject.service.legacy.user.UserDirectoryService.class);
			return m_instance;
		}
		else
		{
			return (org.sakaiproject.service.legacy.user.UserDirectoryService) ComponentManager.get(org.sakaiproject.service.legacy.user.UserDirectoryService.class);
		}
	}
	private static org.sakaiproject.service.legacy.user.UserDirectoryService m_instance = null;

	public static java.lang.String SERVICE_NAME = org.sakaiproject.service.legacy.user.UserDirectoryService.SERVICE_NAME;
	public static java.lang.String REFERENCE_ROOT = org.sakaiproject.service.legacy.user.UserDirectoryService.REFERENCE_ROOT;
	public static java.lang.String SECURE_ADD_USER = org.sakaiproject.service.legacy.user.UserDirectoryService.SECURE_ADD_USER;
	public static java.lang.String SECURE_REMOVE_USER = org.sakaiproject.service.legacy.user.UserDirectoryService.SECURE_REMOVE_USER;
	public static java.lang.String SECURE_UPDATE_USER_OWN = org.sakaiproject.service.legacy.user.UserDirectoryService.SECURE_UPDATE_USER_OWN;
	public static java.lang.String SECURE_UPDATE_USER_ANY = org.sakaiproject.service.legacy.user.UserDirectoryService.SECURE_UPDATE_USER_ANY;

	public static org.sakaiproject.service.legacy.user.User getUser(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException
	{
		org.sakaiproject.service.legacy.user.UserDirectoryService service = getInstance();
		if (service == null)
			return null;

		return service.getUser(param0);
	}

	public static java.util.List getUsers(java.util.List param0)
	{
		org.sakaiproject.service.legacy.user.UserDirectoryService service = getInstance();
		if (service == null)
			return null;

		return service.getUsers(param0);
	}

	public static java.util.List getUsers()
	{
		org.sakaiproject.service.legacy.user.UserDirectoryService service = getInstance();
		if (service == null)
			return null;

		return service.getUsers();
	}

	public static java.util.List getUsers(int param0, int param1)
	{
		org.sakaiproject.service.legacy.user.UserDirectoryService service = getInstance();
		if (service == null)
			return null;

		return service.getUsers(param0, param1);
	}

	public static org.sakaiproject.service.legacy.user.User getCurrentUser()
	{
		org.sakaiproject.service.legacy.user.UserDirectoryService service = getInstance();
		if (service == null)
			return null;

		return service.getCurrentUser();
	}

	public static org.sakaiproject.service.legacy.user.User findUserByEmail(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException
	{
		org.sakaiproject.service.legacy.user.UserDirectoryService service = getInstance();
		if (service == null)
			return null;

		return service.findUserByEmail(param0);
	}

	public static boolean allowUpdateUser(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.user.UserDirectoryService service = getInstance();
		if (service == null)
			return false;

		return service.allowUpdateUser(param0);
	}

	public static org.sakaiproject.service.legacy.user.UserEdit editUser(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.InUseException
	{
		org.sakaiproject.service.legacy.user.UserDirectoryService service = getInstance();
		if (service == null)
			return null;

		return service.editUser(param0);
	}

	public static void commitEdit(org.sakaiproject.service.legacy.user.UserEdit param0)
	{
		org.sakaiproject.service.legacy.user.UserDirectoryService service = getInstance();
		if (service == null)
			return;

		service.commitEdit(param0);
	}

	public static void cancelEdit(org.sakaiproject.service.legacy.user.UserEdit param0)
	{
		org.sakaiproject.service.legacy.user.UserDirectoryService service = getInstance();
		if (service == null)
			return;

		service.cancelEdit(param0);
	}

	public static org.sakaiproject.service.legacy.user.User getAnonymousUser()
	{
		org.sakaiproject.service.legacy.user.UserDirectoryService service = getInstance();
		if (service == null)
			return null;

		return service.getAnonymousUser();
	}

	public static int countUsers()
	{
		org.sakaiproject.service.legacy.user.UserDirectoryService service = getInstance();
		if (service == null)
			return 0;

		return service.countUsers();
	}

	public static java.util.List searchUsers(java.lang.String param0, int param1, int param2)
	{
		org.sakaiproject.service.legacy.user.UserDirectoryService service = getInstance();
		if (service == null)
			return null;

		return service.searchUsers(param0, param1, param2);
	}

	public static int countSearchUsers(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.user.UserDirectoryService service = getInstance();
		if (service == null)
			return 0;

		return service.countSearchUsers(param0);
	}

	public static boolean allowAddUser(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.user.UserDirectoryService service = getInstance();
		if (service == null)
			return false;

		return service.allowAddUser(param0);
	}

	public static org.sakaiproject.service.legacy.user.UserEdit addUser(java.lang.String param0) throws org.sakaiproject.exception.IdInvalidException, org.sakaiproject.exception.IdUsedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.user.UserDirectoryService service = getInstance();
		if (service == null)
			return null;

		return service.addUser(param0);
	}

	public static org.sakaiproject.service.legacy.user.User addUser(java.lang.String param0, java.lang.String param1, java.lang.String param2, java.lang.String param3, java.lang.String param4, java.lang.String param5, org.sakaiproject.service.legacy.resource.ResourceProperties param6) throws org.sakaiproject.exception.IdInvalidException, org.sakaiproject.exception.IdUsedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.user.UserDirectoryService service = getInstance();
		if (service == null)
			return null;

		return service.addUser(param0, param1, param2, param3, param4, param5, param6);
	}

	public static org.sakaiproject.service.legacy.user.UserEdit mergeUser(org.w3c.dom.Element param0) throws org.sakaiproject.exception.IdInvalidException, org.sakaiproject.exception.IdUsedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.user.UserDirectoryService service = getInstance();
		if (service == null)
			return null;

		return service.mergeUser(param0);
	}

	public static boolean allowRemoveUser(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.user.UserDirectoryService service = getInstance();
		if (service == null)
			return false;

		return service.allowRemoveUser(param0);
	}

	public static void removeUser(org.sakaiproject.service.legacy.user.UserEdit param0) throws org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.user.UserDirectoryService service = getInstance();
		if (service == null)
			return;

		service.removeUser(param0);
	}

	public static org.sakaiproject.service.legacy.user.User authenticate(java.lang.String param0, java.lang.String param1)
	{
		org.sakaiproject.service.legacy.user.UserDirectoryService service = getInstance();
		if (service == null)
			return null;

		return service.authenticate(param0, param1);
	}

	public static void destroyAuthentication()
	{
		org.sakaiproject.service.legacy.user.UserDirectoryService service = getInstance();
		if (service == null)
			return;

		service.destroyAuthentication();
	}

	public static java.lang.String userReference(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.user.UserDirectoryService service = getInstance();
		if (service == null)
			return null;

		return service.userReference(param0);
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/user/cover/UserDirectoryService.java,v 1.11 2004/10/09 23:26:04 ggolden.umich.edu Exp $
*
**********************************************************************************/
