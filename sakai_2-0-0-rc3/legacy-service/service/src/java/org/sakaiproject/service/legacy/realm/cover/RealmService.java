/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/realm/cover/RealmService.java,v 1.1 2005/05/12 15:45:32 ggolden.umich.edu Exp $
*
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

package org.sakaiproject.service.legacy.realm.cover;

import org.sakaiproject.service.framework.component.cover.ComponentManager;

/**
* <p>RealmService is a static Cover for the {@link org.sakaiproject.service.legacy.realm.RealmService RealmService};
* see that interface for usage details.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.1 $
*/
public class RealmService
{
	/**
	 * Access the component instance: special cover only method.
	 * @return the component instance.
	 */
	public static org.sakaiproject.service.legacy.realm.RealmService getInstance()
	{
		if (ComponentManager.CACHE_SINGLETONS)
		{
			if (m_instance == null) m_instance = (org.sakaiproject.service.legacy.realm.RealmService) ComponentManager.get(org.sakaiproject.service.legacy.realm.RealmService.class);
			return m_instance;
		}
		else
		{
			return (org.sakaiproject.service.legacy.realm.RealmService) ComponentManager.get(org.sakaiproject.service.legacy.realm.RealmService.class);
		}
	}
	private static org.sakaiproject.service.legacy.realm.RealmService m_instance = null;

	public static java.lang.String SERVICE_NAME = org.sakaiproject.service.legacy.realm.RealmService.SERVICE_NAME;
	public static java.lang.String REFERENCE_ROOT = org.sakaiproject.service.legacy.realm.RealmService.REFERENCE_ROOT;
	public static java.lang.String SECURE_ADD_REALM = org.sakaiproject.service.legacy.realm.RealmService.SECURE_ADD_REALM;
	public static java.lang.String SECURE_REMOVE_REALM = org.sakaiproject.service.legacy.realm.RealmService.SECURE_REMOVE_REALM;
	public static java.lang.String SECURE_UPDATE_REALM = org.sakaiproject.service.legacy.realm.RealmService.SECURE_UPDATE_REALM;
	public static java.lang.String SECURE_UPDATE_OWN_REALM = org.sakaiproject.service.legacy.realm.RealmService.SECURE_UPDATE_OWN_REALM;
	public static java.lang.String ANON_ROLE = org.sakaiproject.service.legacy.realm.RealmService.ANON_ROLE;
	public static java.lang.String AUTH_ROLE = org.sakaiproject.service.legacy.realm.RealmService.AUTH_ROLE;

	public static java.util.List getRealms(java.lang.String param0, org.sakaiproject.javax.PagingPosition param1)
	{
		org.sakaiproject.service.legacy.realm.RealmService service = getInstance();
		if (service == null)
			return null;

		return service.getRealms(param0, param1);
	}

	public static int countRealms(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.realm.RealmService service = getInstance();
		if (service == null)
			return 0;

		return service.countRealms(param0);
	}

	public static org.sakaiproject.service.legacy.realm.Realm getRealm(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException
	{
		org.sakaiproject.service.legacy.realm.RealmService service = getInstance();
		if (service == null)
			return null;

		return service.getRealm(param0);
	}

	public static boolean allowUpdateRealm(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.realm.RealmService service = getInstance();
		if (service == null)
			return false;

		return service.allowUpdateRealm(param0);
	}

	public static org.sakaiproject.service.legacy.realm.RealmEdit editRealm(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.InUseException
	{
		org.sakaiproject.service.legacy.realm.RealmService service = getInstance();
		if (service == null)
			return null;

		return service.editRealm(param0);
	}

	public static void commitEdit(org.sakaiproject.service.legacy.realm.RealmEdit param0)
	{
		org.sakaiproject.service.legacy.realm.RealmService service = getInstance();
		if (service == null)
			return;

		service.commitEdit(param0);
	}

	public static void cancelEdit(org.sakaiproject.service.legacy.realm.RealmEdit param0)
	{
		org.sakaiproject.service.legacy.realm.RealmService service = getInstance();
		if (service == null)
			return;

		service.cancelEdit(param0);
	}

	public static boolean allowAddRealm(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.realm.RealmService service = getInstance();
		if (service == null)
			return false;

		return service.allowAddRealm(param0);
	}

	public static org.sakaiproject.service.legacy.realm.RealmEdit addRealm(java.lang.String param0) throws org.sakaiproject.exception.IdInvalidException, org.sakaiproject.exception.IdUsedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.realm.RealmService service = getInstance();
		if (service == null)
			return null;

		return service.addRealm(param0);
	}

	public static org.sakaiproject.service.legacy.realm.RealmEdit addRealm(java.lang.String param0, org.sakaiproject.service.legacy.realm.Realm param1) throws org.sakaiproject.exception.IdInvalidException, org.sakaiproject.exception.IdUsedException, org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.realm.RealmService service = getInstance();
		if (service == null)
			return null;

		return service.addRealm(param0, param1);
	}

	public static boolean allowRemoveRealm(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.realm.RealmService service = getInstance();
		if (service == null)
			return false;

		return service.allowRemoveRealm(param0);
	}

	public static void removeRealm(org.sakaiproject.service.legacy.realm.RealmEdit param0) throws org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.realm.RealmService service = getInstance();
		if (service == null)
			return;

		service.removeRealm(param0);
	}

	public static void removeRealm(java.lang.String param0) throws org.sakaiproject.exception.PermissionException
	{
		org.sakaiproject.service.legacy.realm.RealmService service = getInstance();
		if (service == null)
			return;

		service.removeRealm(param0);
	}

	public static java.lang.String realmReference(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.realm.RealmService service = getInstance();
		if (service == null)
			return null;

		return service.realmReference(param0);
	}

	public static void joinSite(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.InUseException
	{
		org.sakaiproject.service.legacy.realm.RealmService service = getInstance();
		if (service == null)
			return;

		service.joinSite(param0);
	}

	public static void unjoinSite(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException, org.sakaiproject.exception.PermissionException, org.sakaiproject.exception.InUseException
	{
		org.sakaiproject.service.legacy.realm.RealmService service = getInstance();
		if (service == null)
			return;

		service.unjoinSite(param0);
	}
	
	public static boolean allowUnjoinSite(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.realm.RealmService service = getInstance();
		if (service == null)
			return false;

		return service.allowUnjoinSite(param0);
	}

	public static java.util.Set getUsers(java.lang.String param0, java.util.Collection param1)
	{
		org.sakaiproject.service.legacy.realm.RealmService service = getInstance();
		if (service == null)
			return null;

		return service.getUsers(param0, param1);
	}

	public static java.util.Set unlockRealms(java.lang.String param0, java.lang.String param1)
	{
		org.sakaiproject.service.legacy.realm.RealmService service = getInstance();
		if (service == null)
			return null;

		return service.unlockRealms(param0, param1);
	}

	public static java.util.Set getLocks(java.lang.String param0, java.util.Collection param1)
	{
		org.sakaiproject.service.legacy.realm.RealmService service = getInstance();
		if (service == null)
			return null;

		return service.getLocks(param0, param1);
	}

	public static void refreshUser(java.lang.String param0)
	{
		org.sakaiproject.service.legacy.realm.RealmService service = getInstance();
		if (service == null)
			return;

		service.refreshUser(param0);
	}

	public static boolean unlock(java.lang.String param0, java.lang.String param1, java.util.Collection param2)
	{
		org.sakaiproject.service.legacy.realm.RealmService service = getInstance();
		if (service == null)
			return false;

		return service.unlock(param0, param1, param2);
	}

	public static boolean unlock(java.lang.String param0, java.lang.String param1, java.lang.String param2)
	{
		org.sakaiproject.service.legacy.realm.RealmService service = getInstance();
		if (service == null)
			return false;

		return service.unlock(param0, param1, param2);
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/realm/cover/RealmService.java,v 1.1 2005/05/12 15:45:32 ggolden.umich.edu Exp $
*
**********************************************************************************/
