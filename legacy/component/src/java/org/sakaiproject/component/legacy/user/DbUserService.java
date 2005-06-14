/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/legacy/user/DbUserService.java,v 1.2 2005/05/12 01:38:28 ggolden.umich.edu Exp $
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

// package
package org.sakaiproject.component.legacy.user;

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
import org.sakaiproject.service.legacy.resource.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.time.cover.TimeService;
import org.sakaiproject.service.legacy.user.UserEdit;
import org.sakaiproject.util.java.StringUtil;
import org.sakaiproject.util.xml.Xml;
import org.sakaiproject.util.storage.BaseDbFlatStorage;
import org.sakaiproject.util.storage.BaseDbSingleStorage;
import org.sakaiproject.util.storage.StorageUser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
* <p>DbCachedUserService is an extension of the BaseUserService with a database storage backed up
* by an in-memory cache.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
*/
public class DbUserService
	extends BaseUserDirectoryService
{
	/** Table name for users. */
	protected String m_tableName = "SAKAI_USER";

	/** Table name for properties. */
	protected String m_propTableName = "SAKAI_USER_PROPERTY";

	/** ID field. */
	protected String m_idFieldName = "USER_ID";

	/** SORT field 1. */
	protected String m_sortField1 = "LAST_NAME";

	/** SORT field 2. */
	protected String m_sortField2 = "FIRST_NAME";

	/** All fields. */
	protected String[] m_fieldNames = {"USER_ID","EMAIL","FIRST_NAME","LAST_NAME","TYPE","PW","CREATEDBY", "MODIFIEDBY", "CREATEDON", "MODIFIEDON"};

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

	/** If true, we do our locks in the remote database, otherwise we do them here. */
	protected boolean m_useExternalLocks = true;

	/**
	 * Configuration: set the external locks value.
	 * @param value The external locks value.
	 */
	public void setExternalLocks(String value)
	{
		m_useExternalLocks = new Boolean(value).booleanValue();
	}

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
				m_sqlService.ddl(this.getClass().getClassLoader(), "sakai_user");
			}

			super.init();
			
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

			m_logger.info(this +".init(): table: " + m_tableName + " external locks: " + m_useExternalLocks
						+ " checkOld: " + m_checkOld);
			
		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}
	}
	
	/*******************************************************************************
	* BaseUserService extensions
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
	* Covers for the BaseXmlFileStorage, providing User and UserEdit parameters
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
			super(m_tableName, m_idFieldName, m_fieldNames, m_propTableName, m_useExternalLocks, null, m_sqlService);
			setSortField(m_sortField1, m_sortField2);

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

		public UserEdit get(String id)
		{
			UserEdit rv = (UserEdit) super.getResource(id);

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

		public UserEdit put(String id)
		{
			// check for already exists (new or old)
			if (check(id)) return null;

			BaseUserEdit rv = (BaseUserEdit) super.putResource(id, fields(id, null, false));
			if (rv != null) rv.activate();
			return rv;
		}

		public UserEdit edit(String id)
		{
			BaseUserEdit rv = (BaseUserEdit) super.editResource(id);
			
			// if not found, try from the old (convert to the new)
			if (m_checkOld && (rv == null))
			{
				// this locks the old table/record
				rv = (BaseUserEdit) m_oldStorage.edit(id);
				if (rv != null)
				{
					// create the record in new, also locking it into an edit
					rv = (BaseUserEdit) super.putResource(id, fields(id, rv, false));
					
					// delete the old record
					m_oldStorage.remove(rv);
				}
			}

			if (rv != null) rv.activate();
			return rv;
		}

		public void commit(UserEdit edit) { super.commitResource(edit, fields(edit.getId(), edit, true),
					edit.getProperties()); }

		public void cancel(UserEdit edit) { super.cancelResource(edit); }

		public void remove(UserEdit edit) { super.removeResource(edit); }

		public List search(String criteria, int first, int last)
		{
			// if we have to be concerned with old stuff, we cannot let the db do the search
			if (m_checkOld)
			{			
				List all = getAll();
				List rv = new Vector();

				for (Iterator i = all.iterator(); i.hasNext();)
				{
					BaseUserEdit u = (BaseUserEdit) i.next();
					if (u.selectedBy(criteria))
					{
						rv.add(u);
					}
				}

				Collections.sort(rv);

				// subset by position
				if (first < 1) first = 1;
				if (last >= rv.size()) last = rv.size();
		
				rv = rv.subList(first-1, last);

				return rv;
			}

			Object[] fields = new Object[4];
			fields[0] = "%" + criteria + "%";
			fields[1] = fields[0];
			fields[2] = fields[0];
			fields[3] = fields[0];
			List rv = super.getSelectedResources("UPPER(USER_ID) LIKE UPPER(?) OR UPPER(EMAIL) LIKE UPPER(?) OR UPPER(FIRST_NAME) LIKE UPPER(?) OR UPPER(LAST_NAME) LIKE UPPER(?)", fields);

			return rv;
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
					BaseUserEdit u = (BaseUserEdit) i.next();
					if (u.selectedBy(criteria))
					{
						rv.add(u);
					}
				}

				return rv.size();
			}

			Object[] fields = new Object[4];
			fields[0] = "%" + criteria + "%";
			fields[1] = fields[0];
			fields[2] = fields[0];
			fields[3] = fields[0];
			int rv = super.countSelectedResources("UPPER(USER_ID) LIKE UPPER(?) OR UPPER(EMAIL) LIKE UPPER(?) OR UPPER(FIRST_NAME) LIKE UPPER(?) OR UPPER(LAST_NAME) LIKE UPPER(?)", fields);

			return rv;
		}

		/**
		 * {@inheritDoc}
		 */
		public UserEdit findUserByEmail(String email)
		{
			UserEdit edit = null;

			// check old if needed
			if (m_checkOld)
			{
				edit = m_oldStorage.findUserByEmail(email);
			}
			
			if (edit == null)
			{
				// search for it
				Object[] fields = new Object[1];
				fields[0] = email;
				List rv = super.getSelectedResources("UPPER(EMAIL) = UPPER(?)", fields);
				if ((rv != null) && (rv.size() > 0))
				{
					if (rv.size() > 1)
					{
						m_logger.info("Email with multiple users: " + email);
					}

					edit = (UserEdit) rv.get(0);
				}
			}

			return edit;
		}

		/**
		 * Read properties from storage into the edit's properties.
		 * @param edit The user to read properties for.
		 */
		public void readProperties(UserEdit edit, ResourcePropertiesEdit props)
		{
			super.readProperties(edit, props);
		}

		/**
		 * Get the fields for the database from the edit for this id, and the id again at the end if needed
		 * @param id The resource id
		 * @param edit The edit (may be null in a new)
		 * @param idAgain If true, include the id field again at the end, else don't.
		 * @return The fields for the database.
		 */
		protected Object[] fields(String id, UserEdit edit, boolean idAgain)
		{
			Object[] rv = new Object[idAgain ? 11 : 10];
			rv[0] = caseId(id);
			if (idAgain)
			{
				rv[10] = rv[0];
			}

			if (edit == null)
			{
				String attribUser = UsageSessionService.getSessionUserId();
				
				// if no current user, since we are working up a new user record, use the user id as creator...
				if ((attribUser == null) || (attribUser.length() == 0)) attribUser = (String) rv[0];

				Time now = TimeService.newTime();
				rv[1] = "";
				rv[2] = "";
				rv[3] = "";
				rv[4] = "";
				rv[5] = "";
				rv[6] = attribUser;
				rv[7] = attribUser;
				rv[8] = now;
				rv[9] = now;
			}

			else
			{
				rv[1] = StringUtil.trimToZero(edit.getEmail());
				rv[2] = StringUtil.trimToZero(edit.getFirstName());
				rv[3] = StringUtil.trimToZero(edit.getLastName());
				rv[4] = StringUtil.trimToZero(edit.getType());
				rv[5] = StringUtil.trimToZero(((BaseUserEdit) edit).m_pw);

				// for creator and modified by, if null, make it the id
				rv[6] = StringUtil.trimToNull(((BaseUserEdit) edit).m_createdUserId);
				if (rv[6] == null)
				{
					rv[6] = rv[0];
				}
				rv[7] = StringUtil.trimToNull(((BaseUserEdit) edit).m_lastModifiedUserId);
				if (rv[7] == null)
				{
					rv[7] = rv[0];
				}

				rv[8] = edit.getCreatedTime();
				rv[9] = edit.getModifiedTime();
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
				String email = result.getString(2);
				String firstName = result.getString(3);
				String lastName = result.getString(4);
				String type = result.getString(5);
				String pw = result.getString(6);
				String createdBy = result.getString(7);
				String modifiedBy = result.getString(8);
				Time createdOn = TimeService.newTime(
						result.getTimestamp(9, m_sqlService.getCal()).getTime());
				Time modifiedOn = TimeService.newTime(
						result.getTimestamp(10, m_sqlService.getCal()).getTime());

				// create the Resource from these fields
				return new BaseUserEdit(id, email, firstName, lastName, type, pw, createdBy, createdOn, modifiedBy, modifiedOn);
			}
			catch (SQLException e)
			{
				m_logger.warn(this + ".readSqlResultRecord: " + e);
				return null;
			}
		}

	}   // DbStorage

	/**
	* Covers for the BaseXmlFileStorage, providing User and UserEdit parameters
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
			super("CHEF_USER", "USER_ID", null, false, "user", user, m_sqlService);
		}

		public boolean check(String id) { return super.checkResource(id); }

		public UserEdit get(String id) { return (UserEdit) super.getResource(id); }

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

		public List getAll(int first, int last)
		{
			List all = super.getAllResources();

			// sort for position check
			Collections.sort(all);

			// subset by position
			if (first < 1) first = 1;
			if (last >= all.size()) last = all.size();
			
			all = all.subList(first-1, last);

			return all;
		}

		public List search(String criteria, int first, int last)
		{
			List all = super.getAllResources();

			List rv = new Vector();
			for (Iterator i = all.iterator(); i.hasNext();)
			{
				BaseUserEdit u = (BaseUserEdit) i.next();
				if (u.selectedBy(criteria))
				{
					rv.add(u);
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
		 * {@inheritDoc}
		 */
		public int countSearch(String criteria)
		{
			List all = super.getAllResources();

			Vector rv = new Vector();
			for (Iterator i = all.iterator(); i.hasNext();)
			{
				BaseUserEdit u = (BaseUserEdit) i.next();
				if (u.selectedBy(criteria))
				{
					rv.add(u);
				}
			}
			
			return rv.size();
		}

		public UserEdit put(String id) { return (UserEdit) super.putResource(id, null); }

		public UserEdit edit(String id) { return (UserEdit) super.editResource(id); }

		public void commit(UserEdit edit) { super.commitResource(edit); }

		public void cancel(UserEdit edit) { super.cancelResource(edit); }

		public void remove(UserEdit edit) { super.removeResource(edit); }

		/**
		 * {@inheritDoc}
		 */
		public UserEdit findUserByEmail(String email)
		{
			// check internal users
			List users = getUsers();
			for (Iterator iUsers = users.iterator(); iUsers.hasNext();)
			{
				UserEdit user = (UserEdit) iUsers.next();
				if (user.getEmail().equalsIgnoreCase(email))
				{
					return user;
				}
			}
			
			return null;
		}

		/**
		 * Read properties from storage into the edit's properties.
		 * @param edit The user to read properties for.
		 */
		public void readProperties(UserEdit edit, ResourcePropertiesEdit props)
		{
			m_logger.warn(this + ".readProperties: should not be called.");
		}

	}   // DbCachedStorage

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

			// read all user ids
			String sql = "select USER_ID, XML from CHEF_USER";
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
							if (!root.getTagName().equals("user"))
							{
								m_logger.warn(this + ".convertOld: XML root element not user: " + root.getTagName());
								return null;
							}
							UserEdit e = new BaseUserEdit(root);

							// pick up the fields
							Object[] fields = ((DbStorage) m_storage).fields(id, e, false);
							
							// insert the record
							boolean ok = ((DbStorage) m_storage).insertResource(id, fields, connection);
							if (!ok)
							{
								// warn, and don't delete the old!
								m_logger.warn(this + ".convertOld: failed to insert: " + id);
								return null;
							}

							// delete the old record
							String statement = "delete from CHEF_USER where USER_ID = ?";
							fields = new Object[1];
							fields[0] = id;
							ok = m_sqlService.dbWrite(connection, statement, fields);
							if (!ok)
							{
								m_logger.warn(this + ".convertOld: failed to delete: " + id);
							}

							// m_logger.info(" ** user converted: " + id);

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

}   // DbCachedUserService

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/legacy/user/DbUserService.java,v 1.2 2005/05/12 01:38:28 ggolden.umich.edu Exp $
*
**********************************************************************************/
