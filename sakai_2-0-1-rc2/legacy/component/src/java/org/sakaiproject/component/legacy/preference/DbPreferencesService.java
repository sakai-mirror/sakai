/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/legacy/preference/DbPreferencesService.java,v 1.1 2005/04/06 02:42:37 ggolden.umich.edu Exp $
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
package org.sakaiproject.component.legacy.preference;

// import
import org.sakaiproject.service.legacy.preference.Preferences;
import org.sakaiproject.service.legacy.preference.PreferencesEdit;
import org.sakaiproject.service.framework.sql.SqlService;
import org.sakaiproject.util.storage.BaseDbSingleStorage;
import org.sakaiproject.util.storage.StorageUser;

/**
* <p>DbPreferencesService is an extension of the BasePreferencesService with database storage.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public class DbPreferencesService
	extends BasePreferencesService
{
	/** Table name for realms. */
	protected String m_tableName = "SAKAI_PREFERENCES";

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

	/** Configuration: to run the ddl on init or not. */
	protected boolean m_autoDdl = false;

	/**
	 * Configuration: to run the ddl on init or not.
	 * 
	 * @param value
	 *        the auto ddl value.
	 */
	public void setAutoDdl(String value)
	{
		m_autoDdl = new Boolean(value).booleanValue();
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
			// if we are auto-creating our schema, check and create
			if (m_autoDdl)
			{
				m_sqlService.ddl(this.getClass().getClassLoader(), "sakai_preferences");
			}

			super.init();

			m_logger.info(this +".init(): table: " + m_tableName + " locks-in-db: " + m_locksInDb);
		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}
	}
	
	/*******************************************************************************
	* BasePreferencesService extensions
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
	* Covers for the BaseXmlFileStorage, providing Preferences and PreferencesEdit parameters
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
			super(m_tableName, "PREFERENCES_ID", null, m_locksInDb, "preferences", user, m_sqlService);
	
		}	// DbStorage

		public boolean check(String id) { return super.checkResource(id); }

		public Preferences get(String id) { return (Preferences) super.getResource(id); }

		// public List getAll() { return super.getAllResources(); }

		public PreferencesEdit put(String id) { return (PreferencesEdit) super.putResource(id, null); }

		public PreferencesEdit edit(String id) { return (PreferencesEdit) super.editResource(id); }

		public void commit(PreferencesEdit edit) { super.commitResource(edit); }

		public void cancel(PreferencesEdit edit) { super.cancelResource(edit); }

		public void remove(PreferencesEdit edit) { super.removeResource(edit); }

	}   // DbStorage

}   // DbPreferencesService

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/legacy/preference/DbPreferencesService.java,v 1.1 2005/04/06 02:42:37 ggolden.umich.edu Exp $
*
**********************************************************************************/
