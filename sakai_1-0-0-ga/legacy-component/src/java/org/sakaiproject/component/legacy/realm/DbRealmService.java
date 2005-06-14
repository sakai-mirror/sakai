/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/realm/DbRealmService.java,v 1.8 2004/09/24 19:44:09 ggolden.umich.edu Exp $
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
import java.util.List;

import org.sakaiproject.service.legacy.realm.Realm;
import org.sakaiproject.service.legacy.realm.RealmEdit;
import org.sakaiproject.service.framework.sql.SqlService;
import org.sakaiproject.util.storage.BaseDbSingleStorage;
import org.sakaiproject.util.storage.StorageUser;

/**
* <p>DbRealmService is an extension of the BaseRealmService with database storage.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.8 $
*/
public class DbRealmService
	extends BaseRealmService
{
	/** Table name for realms. */
	protected String m_tableName = "CHEF_REALM";

	/** If true, we do our locks in the remote database, otherwise we do them here. */
	protected boolean m_locksInDb = true;

	/*******************************************************************************
	* Constructors, Dependencies and their setter methods
	*******************************************************************************/

	/** Dependency: SqlService */
	protected SqlService m_sqlService = null;

	/**
	 * Dependency: SqlService.
	 * @param service The SqlService.
	 */
	public void setSqlService(SqlService service)
	{
		m_sqlService = service;
	}

	/**
	 * Configuration: set the table name
	 * @param path The table name.
	 */
	public void setTableName(String name)
	{
		m_tableName = name;
	}

	/**
	 * Configuration: set the locks-in-db
	 * @param value The locks-in-db value.
	 */
	public void setLocksInDb(String value)
	{
		m_locksInDb = new Boolean(value).booleanValue();
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

			m_logger.info(this +".init(): table: " + m_tableName + " locks-in-db: " + m_locksInDb);
			
			// TODO: (stop it!) sorry, but we need the realms pre-loaded
			completeCache();
		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}
	}
	
	/*******************************************************************************
	* BaseRealmService extensions
	*******************************************************************************/

	/**
	* Construct a Storage object.
	* @return The new storage object.
	*/
	protected Storage newStorage()
	{
		return new DbStorage(this);

	}   // newStorage

	/*******************************************************************************
	* Storage implementation
	*******************************************************************************/

	/**
	* Covers for the BaseXmlFileStorage, providing Realm and RealmEdit parameters
	*/
	protected class DbStorage
		extends BaseDbSingleStorage
		implements Storage
	{
		/**
		* Construct.
		* @param realm The StorageUser class to call back for creation of Resource and Edit objects.
		*/
		public DbStorage(StorageUser user)
		{
			super(m_tableName, "REALM_ID", null, m_locksInDb, "realm", user, m_sqlService);
	
		}	// DbStorage

		public boolean check(String id) { return super.checkResource(id); }

		public Realm get(String id) { return (Realm) super.getResource(id); }

		public List getAll() { return super.getAllResources(); }

		public RealmEdit put(String id) { return (RealmEdit) super.putResource(id, null); }

		public RealmEdit edit(String id) { return (RealmEdit) super.editResource(id); }

		public void commit(RealmEdit edit) { super.commitResource(edit); }

		public void cancel(RealmEdit edit) { super.cancelResource(edit); }

		public void remove(RealmEdit edit) { super.removeResource(edit); }

	}   // DbStorage

}   // DbRealmService

/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/realm/DbRealmService.java,v 1.8 2004/09/24 19:44:09 ggolden.umich.edu Exp $
*
**********************************************************************************/
