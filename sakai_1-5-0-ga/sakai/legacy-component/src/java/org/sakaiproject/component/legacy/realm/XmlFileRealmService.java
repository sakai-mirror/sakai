/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/realm/XmlFileRealmService.java,v 1.11 2005/01/13 19:09:39 ggolden.umich.edu Exp $
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
package org.sakaiproject.component.legacy.realm;

// import
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.sakaiproject.javax.PagingPosition;
import org.sakaiproject.service.legacy.realm.Grant;
import org.sakaiproject.service.legacy.realm.Realm;
import org.sakaiproject.service.legacy.realm.RealmEdit;
import org.sakaiproject.service.legacy.realm.Role;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.sakaiproject.util.storage.BaseXmlFileStorage;
import org.sakaiproject.util.storage.StorageUser;

/**
* <p>XmlFileRealmService is an extension of the BaseRealmService  with a in-memory xml file backed up
* storage.  The full set of realms are read in at startup, kept in memory, and
* written out at shutdown.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.11 $
*/
public class XmlFileRealmService
	extends BaseRealmService
{
	/** A full path and file name to the storage file. */
	protected String m_storagePath = "db/realm.xml";

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
	* BaseRealmService extensions
	*******************************************************************************/

	/**
	* Construct a Storage object.
	* @return The new storage object.
	*/
	protected Storage newStorage()
	{
		return new XmlFileStorage(this);

	}   // newStorage

	/*******************************************************************************
	* Storage implementation
	*******************************************************************************/

	/**
	* Covers for the BaseXmlFileStorage, providing Realm and RealmEdit parameters
	*/
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
			super(m_storagePath, "realms", null, "realm", user);
	
		}	// XmlFileStorage

		public boolean check(String id) { return super.checkResource(null, id); }

		public Realm get(String id)
		{
			BaseRealm realm = (BaseRealm) super.getResource(null, id);

			return realm;
		}

		public RealmEdit put(String id) { return (RealmEdit) super.putResource(null, id, null); }

		public RealmEdit edit(String id)
		{
			BaseRealmEdit realm = (BaseRealmEdit) super.editResource(null, id);

			// update the db and realm with latest provider
			refreshRealm(realm);

			return realm;
		}

		public void commit(RealmEdit realm) { super.commitResource(null, realm); }

		public void cancel(RealmEdit realm) { super.cancelResource(null, realm); }

		public void remove(RealmEdit realm) { super.removeResource(null, realm); }

		/**
		 * {@inheritDoc}
		 */
		public List getRealms(String criteria, PagingPosition page)
		{
			// return list
			List rv = new Vector();

			// start with all realms
			List realms = getAllResources(null);
			for (Iterator iRealms = realms.iterator(); iRealms.hasNext();)
			{
				Realm realm = (Realm) iRealms.next();

				// check criteria, if specified
				if (criteria != null)
				{
					boolean accepted = false;

					if ((realm.getId() != null) && (realm.getId().indexOf(criteria) != -1))
					{
						accepted = true;
					}
					else if ((realm.getProviderRealmId() != null) && (realm.getProviderRealmId().indexOf(criteria) != -1))
					{
						accepted = true;
					}

					if (!accepted)
						continue;
				}

				// passed all the tests!
				rv.add(realm);
			}

			// sort if requested
			if (rv.size() > 0)
			{
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
		public int countRealms(String criteria)
		{
			return getRealms(criteria, (PagingPosition)null).size();
		}

		/**
		 * Complete the read process once the basic realm info has been read
		 * @param realm The real to complete
		 */
		public void completeGet(final BaseRealm realm)
		{
			// Nothing to do, we are always complete
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean unlock(String userId, String lock, String realmId)
		{
			Realm realm = get(realmId);
			if (realm != null)
			{
				return realm.unlock(userId, lock);
			}

			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean unlock(String userId, String lock, Collection realms)
		{
			// get the role ids for the user from the realms
			Set roles = new HashSet();
			for (Iterator i = realms.iterator(); i.hasNext();)
			{
				String realmId = (String) i.next();

				// find the realm - missing is ok
				Realm realm = get(realmId);
				if (realm == null) continue;

				Role role = realm.getUserRole(userId);
				if (role != null)
				{
					roles.add(role.getId());
				}
			}
			
			boolean auth = (userId != null) && (!UserDirectoryService.getAnonymousUser().getId().equals(userId));

			// check the realms for a roleId role that has lock
			for (Iterator i = realms.iterator(); i.hasNext();)
			{
				String realmId = (String) i.next();

				// find the realm - missing is ok
				Realm realm = get(realmId);
				if (realm == null) continue;

				// check the each granted-to-the-user role id that this realm defined to see if it has lock
				for (Iterator r = roles.iterator(); r.hasNext();)
				{
					String roleId = (String) r.next();

					Role role = realm.getRole(roleId);
					if ((role != null) && (role.contains(lock))) return true;
				}

				// check auth in this realm
				if (auth)
				{
					Role role = realm.getRole(AUTH_ROLE);
					if ((role != null) && (role.contains(lock))) return true;
				}
				
				// check anon in this realm
				Role role = realm.getRole(ANON_ROLE);
				if ((role != null) && (role.contains(lock))) return true;
			}

			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		public Set getUsers(String lock, Collection realms)
		{
			Set rv = new HashSet();

			// get all users granted in the realms
			for (Iterator i = realms.iterator(); i.hasNext();)
			{
				String realmId = (String) i.next();

				// find the realm - missing is ok
				Realm realm = get(realmId);
				if (realm == null) continue;
				
				rv.addAll(realm.getUsers());
			}

			// for each of these users, if they can not unlock lock in the grant or role realms, remove
			for (Iterator i = rv.iterator(); i.hasNext();)
			{
				String userId = (String) i.next();

				if (!unlock(userId, lock, realms))
				{
					i.remove();
				}
			}

			return rv;
		}

		/**
		 * {@inheritDoc}
		 */
		public Set unlockRealms(String userId, String lock)
		{
			// return set
			Set rv = new HashSet();

			// scan all realms
			List realms = getAllResources(null);
			for (Iterator iRealms = realms.iterator(); iRealms.hasNext();)
			{
				Realm realm = (Realm) iRealms.next();

				// if the use has a granted role that can unlock the lock in the realm, consider it
				// Note: if the realm allows the lock via anon or auth, this is not considered.
				MyGrant grant = (MyGrant) ((BaseRealm) realm).m_userGrants.get(userId);
				if ((grant != null) && (grant.active))
				{
					if (grant.role.contains(lock))
						rv.add(realm.getId());
				}
			}

			return rv;
		}

		/**
		 * {@inheritDoc}
		 */
		public Set getLocks(String roleId, Collection realms)
		{
			Set rv = new HashSet();

			for (Iterator i = realms.iterator(); i.hasNext();)
			{
				String realmId = (String) i.next();

				// find the realm
				Realm realm = get(realmId);
				if (realm == null) continue;

				// find this realm's role of this id
				Role role = realm.getRole(roleId);
				if (role == null) continue;

				// collect the locks
				rv.addAll(role.getLocks());
			}

			return rv;
		}

		/**
		 * {@inheritDoc}
		 */
		public void refreshUser(String userId, Map providerGrants)
		{
			// remove this user's provided active grants from all realms
			List realms = getAllResources(null);
			for (Iterator i = realms.iterator(); i.hasNext();)
			{
				BaseRealm realm = (BaseRealm) i.next();

				Grant grant = realm.getUserGrant(userId);
				if ((grant != null) && grant.isActive() && grant.isProvided())
				{
					realm.removeUser(userId);
				}
			}
			
			// for each realm that has a provider in the map, and does not have a grant for the user,
			// add the active provided grant with the map's role.
			for (Iterator i = realms.iterator(); i.hasNext();)
			{
				BaseRealm realm = (BaseRealm) i.next();

				// skip realms with no provider
				if (realm.getProviderRealmId() != null)
				{
					// find the role in the map for this realm - skip if we don't have one
					String role = (String) providerGrants.get(realm.getProviderRealmId());
					if (role != null)
					{
						// skip realms where the user has a non-provider (or inactive) grant
						Grant grant = realm.getUserGrant(userId);
						if (grant == null)
						{
							realm.addUserRole(userId, role, true, true);
						}
					}
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public void refreshRealm(BaseRealm realm)
		{
			if ((realm == null) || (m_provider == null)) return;

			// pull the latest userId -> role name map from the provider
			Map roles = m_provider.getUserRolesForRealm(realm.getProviderRealmId());

			// remove all grants from the realm 'provided', and active
			for (Iterator i = realm.m_userGrants.entrySet().iterator(); i.hasNext();)
			{
				Map.Entry entry = (Map.Entry) i.next();
				String userId = (String) entry.getKey();
				MyGrant grant = (MyGrant) entry.getValue();
				if (grant.provided && grant.active)
				{
					i.remove();
				}
			}

			// add in these new 'provided' grants, if one does not exist
			for (Iterator i = roles.entrySet().iterator(); i.hasNext();)
			{
				Map.Entry entry = (Map.Entry) i.next();
				String userId = (String) entry.getKey();
				String roleId = (String) entry.getValue();
				if (!realm.m_userGrants.containsKey(userId))
				{
					Role role = (Role) realm.m_roles.get(roleId);
					if (role != null)
					{
						MyGrant grant = new MyGrant(role, true, true, userId);
						realm.m_userGrants.put(userId, grant);
					}
					else
					{
						m_logger.warn(this + ".refreshRealm: provided role does not exist: " + realm.getId() + " " + roleId);
					}
				}
			}
		}

	}   // XmlFileStorage

}   // XmlFileRealmService

/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/realm/XmlFileRealmService.java,v 1.11 2005/01/13 19:09:39 ggolden.umich.edu Exp $
*
**********************************************************************************/
