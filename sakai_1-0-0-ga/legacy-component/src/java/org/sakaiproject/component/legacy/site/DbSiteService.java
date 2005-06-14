/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/site/DbSiteService.java,v 1.8 2004/09/24 19:44:09 ggolden.umich.edu Exp $
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
import java.util.List;

import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.SiteEdit;
import org.sakaiproject.service.framework.sql.SqlService;
import org.sakaiproject.util.storage.BaseDbSingleStorage;
import org.sakaiproject.util.storage.StorageUser;

/**
* <p>DbSiteService is an extension of the BaseSiteService with a database storage.</p>
* 
* @author gniversity of Michigan, CHEF Software Development Team
* @version $Revision: 1.8 $
*/
public class DbSiteService
	extends BaseSiteService
{
	/** Table name for sites. */
	protected String m_tableName = "CHEF_SITE";

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

			// TODO: (stop it!) sorry, but we need the sites pre-loaded
			getSites();

		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}
	}
	
	/*******************************************************************************
	* BaseSiteService extensions
	*******************************************************************************/

	/**
	* Construct a Storage object.
	* @return The new storage object.
	*/
	protected Storage newStorage()
	{
		return new DbStorage(this);

	}	// newStorage

	/*******************************************************************************
	* Storage implementation
	*******************************************************************************/

	protected class DbStorage
		extends BaseDbSingleStorage
		implements Storage
	{
		/**
		* Construct.
		* @param user The StorageUser class to call back for creation of Resource and Edit objects.
		*/
		public DbStorage(StorageUser user)
		{
			super(m_tableName, "SITE_ID", null, m_locksInDb, "site", user, m_sqlService);
	
		}	// DbStorage

		public boolean check(String id) { return super.checkResource(id); }

		public Site get(String id) { return (Site) super.getResource(id); }

		public List getAll() { return super.getAllResources(); }

		public SiteEdit put(String id) { return (SiteEdit) super.putResource(id, null); }

		public SiteEdit edit(String id) { return (SiteEdit) super.editResource(id); }

		public void commit(SiteEdit edit) { super.commitResource(edit); }

		public void cancel(SiteEdit edit) { super.cancelResource(edit); }

		public void remove(SiteEdit edit) { super.removeResource(edit); }

	}	// DbStorage

}	// DbSiteService

/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/site/DbSiteService.java,v 1.8 2004/09/24 19:44:09 ggolden.umich.edu Exp $
*
**********************************************************************************/
