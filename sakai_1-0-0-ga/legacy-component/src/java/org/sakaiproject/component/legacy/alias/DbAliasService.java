/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/alias/DbAliasService.java,v 1.14 2004/10/14 22:22:53 janderse.umich.edu Exp $
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
package org.sakaiproject.component.legacy.alias;

// import
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.framework.sql.SqlReader;
import org.sakaiproject.service.framework.sql.SqlService;
import org.sakaiproject.service.legacy.alias.Alias;
import org.sakaiproject.service.legacy.alias.AliasEdit;
import org.sakaiproject.service.legacy.resource.ResourceProperties;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.time.cover.TimeService;
import org.sakaiproject.util.StringUtil;
import org.sakaiproject.util.Xml;
import org.sakaiproject.util.storage.BaseDbFlatStorage;
import org.sakaiproject.util.storage.BaseDbSingleStorage;
import org.sakaiproject.util.storage.StorageUser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
* <p>DbAliasService is an extension of the BaseAliasService with a database storage.
* Fields are fully relational.  Full properties are not yet supported - core ones are.
* Code to find and convert records from before, from the XML based CHEF_ALIAS table is included.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.14 $
*/
public class DbAliasService
	extends BaseAliasService
{
	/** Table name for users. */
	protected String m_tableName = "SAKAI_ALIAS";

	/** ID field. */
	protected String m_idFieldName = "ALIAS_ID";

	/** All fields. */
	protected String[] m_fieldNames = {"ALIAS_ID","TARGET","CREATEDBY", "MODIFIEDBY", "CREATEDON", "MODIFIEDON"};

	/** If true, we do our locks in the remote database, otherwise we do them here. */
	protected boolean m_useExternalLocks = true;

	/** Set if we are to run the from-old conversion. */
	protected boolean m_convertOld = false;

	/**
	 * Configuration: run the from-old conversion.
	 * @param value The conversion desired value.
	 */
	public void setConvertOld(String value)
	{
		m_convertOld = new Boolean(value).booleanValue();
	}

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
	 * Configuration: set the external locks value.
	 * @param value The external locks value.
	 */
	public void setExternalLocks(String value)
	{
		m_useExternalLocks = new Boolean(value).booleanValue();
	}

	/** Configuration: check the old table, too. */
	protected boolean m_checkOld = false;

	/**
	 * Configuration: set the locks-in-db
	 * @param value The locks-in-db value.
	 */
	public void setCheckOld(String value)
	{
		m_checkOld = new Boolean(value).booleanValue();
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

			m_logger.info(this +".init(): table: " + m_tableName + " external locks: " + m_useExternalLocks
						+ " checkOld: " + m_checkOld);
			
			// convert?
			if (m_convertOld)
			{
				m_convertOld = false;
				convertOld();
			}

			// do a count which might find no old records so we can ignore old!
			if (m_checkOld)
			{
				m_storage.count();
			}
		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}
	}
	
	/*******************************************************************************
	* BaseAliasService extensions
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
	* Covers for the BaseXmlFileStorage, providing User and AliasEdit parameters
	*/
	protected class DbStorage
		extends BaseDbFlatStorage
		implements Storage, SqlReader
	{
		/** A prior version's storage model. */
		protected Storage m_oldStorage = null;

		/**
		* Construct.
		* @param user The StorageUser class to call back for creation of Resource and Edit objects.
		*/
		public DbStorage(StorageUser user)
		{
			super(m_tableName, m_idFieldName, m_fieldNames, m_useExternalLocks, null, m_sqlService);
			m_reader = this;
			setCaseInsensitivity(true);
			
			// setup for old-new stradling
			if (m_checkOld)
			{
				m_oldStorage = new DbStorageOld(user);
			}

		}	// DbStorage

		public boolean check(String id)
		{
			boolean rv = super.checkResource(id);
			
			// if not, check old
			if (m_checkOld && (!rv))
			{
				rv = m_oldStorage.check(id);
			}

			return rv;
		}

		public AliasEdit get(String id)
		{
			AliasEdit rv = (AliasEdit) super.getResource(id);

			// if not, check old
			if (m_checkOld && (rv == null))
			{
				rv = m_oldStorage.get(id);
			}
			return rv;
		}

		public List getAll()
		{
			// if we have to be concerned with old stuff, we cannot let the db do the range selection
			if (m_checkOld)
			{
				List all = super.getAllResources();
			
				// add in any additional defined in old
				Set merge = new HashSet();
				merge.addAll(all);

				// add those in the old not already (id based equals) in all
				List more = m_oldStorage.getAll();
				merge.addAll(more);

				all.clear();
				all.addAll(merge);

				return all;
			}
			
			// let the db do range selection
			List all = super.getAllResources();
			return all;
		}

		public List getAll(int first, int last)
		{
			// if we have to be concerned with old stuff, we cannot let the db do the range selection
			if (m_checkOld)
			{
				List all = super.getAllResources();
			
				// add in any additional defined in old
				Set merge = new HashSet();
				merge.addAll(all);

				// add those in the old not already (id based equals) in all
				List more = m_oldStorage.getAll();
				merge.addAll(more);

				all.clear();
				all.addAll(merge);

				Collections.sort(all);

				// subset by position
				if (first < 1) first = 1;
				if (last >= all.size()) last = all.size();
		
				all = all.subList(first-1, last);
				return all;
			}
			
			// let the db do range selection
			List all = super.getAllResources(first, last);
			return all;
		}

		public int count()
		{
			// if we have to be concerned with old stuff, we cannot let the db do all the counting
			if (m_checkOld)
			{
				int count = super.countAllResources();
				count += m_oldStorage.count();

				return count;
			}

			return super.countAllResources();
		}

		public List getAll(String target)
		{
			Object[] fields = new Object[1];
			fields[0] = target;
			
			// if we have to be concerned with old stuff, we cannot let the db do the range selection
			if (m_checkOld)
			{			
				List all = super.getSelectedResources("TARGET = ?", fields);

				// add in any additional defined in old
				Set merge = new HashSet();
				merge.addAll(all);

				// add those in the old not already (id based equals) in all
				List more = m_oldStorage.getAll(target);
				merge.addAll(more);

				all.clear();
				all.addAll(merge);

				return all;
			}
			
			List all = super.getSelectedResources("TARGET = ?", fields);
			return all;
		}

		public List getAll(String target, int first, int last)
		{
			Object[] fields = new Object[1];
			fields[0] = target;
			
			// if we have to be concerned with old stuff, we cannot let the db do the range selection
			if (m_checkOld)
			{			
				List all = super.getSelectedResources("TARGET = ?", fields);

				// add in any additional defined in old
				Set merge = new HashSet();
				merge.addAll(all);

				// add those in the old not already (id based equals) in all
				List more = m_oldStorage.getAll(target);
				merge.addAll(more);

				all.clear();
				all.addAll(merge);

				Collections.sort(all);

				// subset by position
				if (first < 1) first = 1;
				if (last >= all.size()) last = all.size();
		
				all = all.subList(first-1, last);
				return all;
			}
			
			List all = super.getSelectedResources("TARGET = ?", fields, first, last);
			return all;
		}

		public AliasEdit put(String id)
		{
			// check for already exists (new or old)
			if (check(id)) return null;

			BaseAliasEdit rv = (BaseAliasEdit) super.putResource(id, fields(id, null, false));
			if (rv != null) rv.activate();
			return rv;
		}

		public AliasEdit edit(String id)
		{
			BaseAliasEdit rv = (BaseAliasEdit) super.editResource(id);
			
			// if not found, try from the old (convert to the new)
			if (m_checkOld && (rv == null))
			{
				// this locks the old table/record
				rv = (BaseAliasEdit) m_oldStorage.edit(id);
				if (rv != null)
				{
					// create the record in new, also locking it into an edit
					rv = (BaseAliasEdit) super.putResource(id, fields(id, rv, false));
					
					// delete the old record
					m_oldStorage.remove(rv);
				}
			}

			if (rv != null) rv.activate();
			return rv;
		}

		public void commit(AliasEdit edit) { super.commitResource(edit, fields(edit.getId(), edit, true)); }

		public void cancel(AliasEdit edit) { super.cancelResource(edit); }

		public void remove(AliasEdit edit) { super.removeResource(edit); }

		public List search(String criteria, int first, int last)
		{
			// if we have to be concerned with old stuff, we cannot let the db do the search
			if (m_checkOld)
			{			
				List all = getAll();
				List rv = new Vector();

				for (Iterator i = all.iterator(); i.hasNext();)
				{
					Alias a = (Alias) i.next();
					if (	StringUtil.containsIgnoreCase(a.getId(),criteria)
						||	StringUtil.containsIgnoreCase(a.getTarget(),criteria))
					{
						rv.add(a);
					}
				}

				Collections.sort(rv);

				// subset by position
				if (first < 1) first = 1;
				if (last >= rv.size()) last = rv.size();
		
				rv = rv.subList(first-1, last);

				return rv;
			}

			Object[] fields = new Object[2];
			fields[0] = "%" + criteria + "%";
			fields[1] = fields[0];
			List all = super.getSelectedResources("UPPER(ALIAS_ID) LIKE UPPER(?) OR UPPER(TARGET) LIKE UPPER(?)", fields, first, last);

			return all;
		}

		public int countSearch(String criteria)
		{
			// if we have to be concerned with old stuff, we cannot let the db do the search and count
			if (m_checkOld)
			{			
				List all = getAll();
				List rv = new Vector();

				for (Iterator i = all.iterator(); i.hasNext();)
				{
					Alias a = (Alias) i.next();
					if (	StringUtil.containsIgnoreCase(a.getId(),criteria)
						||	StringUtil.containsIgnoreCase(a.getTarget(),criteria))
					{
						rv.add(a);
					}
				}

				return rv.size();
			}

			Object[] fields = new Object[2];
			fields[0] = "%" + criteria + "%";
			fields[1] = fields[0];
			int rv = super.countSelectedResources("UPPER(ALIAS_ID) LIKE UPPER(?) OR UPPER(TARGET) LIKE UPPER(?)", fields);

			return rv;
		}

		/**
		 * Get the fields for the database from the edit for this id, and the id again at the end if needed
		 * @param id The resource id
		 * @param edit The edit (may be null in a new)
		 * @param idAgain If true, include the id field again at the end, else don't.
		 * @return The fields for the database.
		 */
		protected Object[] fields(String id, AliasEdit edit, boolean idAgain)
		{
			Object[] rv = new Object[idAgain ? 7 : 6];
			rv[0] = caseId(id);
			if (idAgain)
			{
				rv[6] = rv[0];
			}

			if (edit == null)
			{
				String current = UsageSessionService.getSessionUserId();
				if (current == null) current = "";

				Time now = TimeService.newTime();
				rv[1] = "";
				rv[2] = current;
				rv[3] = current;
				rv[4] = now;
				rv[5] = now;
			}

			else
			{
				rv[1] = edit.getTarget();
				ResourceProperties props = edit.getProperties();
				rv[2] = props.getProperty(ResourceProperties.PROP_CREATOR);
				rv[3] = props.getProperty(ResourceProperties.PROP_MODIFIED_BY);
				try
				{
					rv[4] = props.getTimeProperty(ResourceProperties.PROP_CREATION_DATE);
				}
				catch (Exception any)
				{
					rv[4] = TimeService.newTime();
				}
				try
				{
					rv[5] = props.getTimeProperty(ResourceProperties.PROP_MODIFIED_DATE);
				}
				catch (Exception any)
				{
					rv[5] = TimeService.newTime();
				}
			}

			return rv;
		}

		/**
		 * Read from the result one set of fields to create a Resource.
		 * @param result The Sql query result.
		 * @return The Resource object.
		 */
		public Object readSqlResultRecord(ResultSet result)
		{
			try
			{
				String id = result.getString(1);
				String target = result.getString(2);
				String createdBy = result.getString(3);
				String modifiedBy = result.getString(4);
				Time createdOn = TimeService.newTime(
						result.getTimestamp(5, m_sqlService.getCal()).getTime());
				Time modifiedOn = TimeService.newTime(
						result.getTimestamp(6, m_sqlService.getCal()).getTime());

				// create the Resource from these fields
				return new BaseAliasEdit(id, target, createdBy, createdOn, modifiedBy, modifiedOn);
			}
			catch (SQLException ignore) { return null;}
		}

	}   // DbStorage

	/**
	* This is how to access the old chef_alias table (CTools through 2.0.7)
	*/
	protected class DbStorageOld
		extends BaseDbSingleStorage
		implements Storage
	{
		/**
		* Construct.
		* @param user The StorageUser class to call back for creation of Resource and Edit objects.
		*/
		public DbStorageOld(StorageUser user)
		{
			super("CHEF_ALIAS", "ALIAS_ID", null, false, "alias", user, m_sqlService);
			setCaseInsensitivity(true);
	
		}	// DbStorage

		public boolean check(String id) { return super.checkResource(id); }

		public AliasEdit get(String id) { return (AliasEdit) super.getResource(id); }

		public List getAll(int first, int last) { return super.getAllResources(first, last); }

		public List getAll() { return super.getAllResources(); }

		public int count()
		{
			int rv = super.countAllResources();
			
			// if we find no more records in the old table, we can start ignoring it...
			// Note: this means once they go away they cannot come back (old versions cannot run in the cluster
			// and write to the old cluster table). -ggolden
			if (rv == 0)
			{
				m_checkOld = false;
				m_logger.info(this + " ** starting to ignore old");
			}
			return rv;
		}

		public AliasEdit put(String id) { return (AliasEdit) super.putResource(id, null); }

		public AliasEdit edit(String id) { return (AliasEdit) super.editResource(id); }

		public void commit(AliasEdit edit) { super.commitResource(edit); }

		public void cancel(AliasEdit edit) { super.cancelResource(edit); }

		public void remove(AliasEdit edit) { super.removeResource(edit); }

		public List getAll(String target)
		{
			List all = super.getAllResources();

			// pick out from all those that are for this target
			List found = new Vector();
			for (Iterator iAll = all.iterator(); iAll.hasNext();)
			{
				BaseAliasEdit a = (BaseAliasEdit) iAll.next();
				if (a.getTarget().equals(target)) found.add(a);
			}

			return found;
		}

		public List getAll(String target, int first, int last)
		{
			List all = super.getAllResources();

			// pick out from all those that are for this target
			List found = new Vector();
			for (Iterator iAll = all.iterator(); iAll.hasNext();)
			{
				BaseAliasEdit a = (BaseAliasEdit) iAll.next();
				if (a.getTarget().equals(target)) found.add(a);
			}

			// sort for position check
			Collections.sort(found);

			// subset by position
			if (first < 1) first = 1;
			if (last >= found.size()) last = found.size();
			
			found = found.subList(first-1, last);

			return found;
		}

		/**
		* Search for aliases with id or target matching criteria, in range.
		* @param criteria The search criteria.
		* @param first The first record position to return.
		* @param last The last record position to return.
		* @return The List (BaseAliasEdit) of all alias.
		*/
		public List search(String criteria, int first, int last)
		{
			List all = super.getAllResources();

			List rv = new Vector();
			for (Iterator i = all.iterator(); i.hasNext();)
			{
				Alias a = (Alias) i.next();
				if (	StringUtil.containsIgnoreCase(a.getId(),criteria)
					||	StringUtil.containsIgnoreCase(a.getTarget(),criteria))
				{
					rv.add(a);
				}
			}
			
			Collections.sort(rv);

			// subset by position
			if (first < 1) first = 1;
			if (last >= rv.size()) last = rv.size();
			
			rv = rv.subList(first-1, last);

			return rv;
		}

		/**
		* Count all the aliases with id or target matching criteria.
		* @param criteria The search criteria.
		* @return The count of all aliases with id or target matching criteria.
		*/
		public int countSearch(String criteria)
		{
			List all = super.getAllResources();

			Vector rv = new Vector();
			for (Iterator i = all.iterator(); i.hasNext();)
			{
				Alias a = (Alias) i.next();
				if (	StringUtil.containsIgnoreCase(a.getId(),criteria)
					||	StringUtil.containsIgnoreCase(a.getTarget(),criteria))
				{
					rv.add(a);
				}
			}
			
			return rv.size();
		}
	}

	/**
	 * Create a new table record for all old table records found, and delete the old.
	 */
	protected void convertOld()
	{
		m_logger.info(this + ".convertOld");

		try
		{
			// get a connection
			final Connection connection = m_sqlService.borrowConnection();
			boolean wasCommit = connection.getAutoCommit();
			connection.setAutoCommit(false);

			// read all alias ids
			String sql = "select ALIAS_ID, XML from CHEF_ALIAS";
			m_sqlService.dbRead(connection, sql, null,
				new SqlReader()
				{
					private int count = 0;
					public Object readSqlResultRecord(ResultSet result)
					{
						try
						{
							// create the Resource from the db xml
							String id = result.getString(1);
							String xml = result.getString(2);
							
							// read the xml
							Document doc =  Xml.readDocumentFromString(xml);

							// verify the root element
							Element root = doc.getDocumentElement();
							if (!root.getTagName().equals("alias"))
							{
								m_logger.warn(this + ".convertOld: XML root element not alias: " + root.getTagName());
								return null;
							}
							AliasEdit a = new BaseAliasEdit(root);

							// pick up the fields
							Object[] fields = ((DbStorage) m_storage).fields(id, a, false);
							
							// insert the record
							boolean ok = ((DbStorage) m_storage).insertResource(id, fields, connection);
							if (!ok)
							{
								m_logger.warn(this + ".convertOld: failed to insert: " + id);
							}

							// delete the old record
							String statement = "delete from CHEF_ALIAS where ALIAS_ID = ?";
							fields = new Object[1];
							fields[0] = id;
							ok = m_sqlService.dbWrite(connection, statement, fields);
							if (!ok)
							{
								m_logger.warn(this + ".convertOld: failed to delete: " + id);
							}

							// m_logger.info(" ** alias converted: " + id);

							return null;
						}
						catch (Throwable ignore) { return null;}
					}
				} );

			connection.commit();
			connection.setAutoCommit(wasCommit);
			m_sqlService.returnConnection(connection);
		}
		catch (Throwable t)
		{
			m_logger.warn(this + ".convertOld: failed: " + t);		
		}

		m_logger.info(this + ".convertOld: done");
	}

}

/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/alias/DbAliasService.java,v 1.14 2004/10/14 22:22:53 janderse.umich.edu Exp $
*
**********************************************************************************/
