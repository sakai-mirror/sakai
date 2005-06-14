/**********************************************************************************
*
* $Header: /cvs/sakai/util/src/java/org/sakaiproject/util/storage/BaseDbFlatStorage.java,v 1.11 2004/10/14 22:42:31 janderse.umich.edu Exp $
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
package org.sakaiproject.util.storage;

// import
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.sakaiproject.service.framework.log.cover.Logger;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.framework.sql.SqlReader;
import org.sakaiproject.service.framework.sql.SqlService;
import org.sakaiproject.service.legacy.resource.Edit;
import org.sakaiproject.service.legacy.resource.Resource;
import org.sakaiproject.service.legacy.time.cover.TimeService;

/**
* <p>BaseDbFlatStorage is a class that stores Resources (of some type) in a database,
* provides locked access, and generally implements a services "storage" class.  The
* service's storage class can extend this to provide covers to turn Resource and
* Edit into something more type specific to the service.</p>
*
* Note: the methods here are all "id" based, with the following assumptions:
* - just the Resource Id field is enough to distinguish one Resource from another
* - a resource's reference is based on no more than the resource id
* - a resource's id cannot change.
*
* In order to handle Unicode characters properly, the SQL statements executed by this class 
* should not embed Unicode characters into the SQL statement text; rather, Unicode values
* should be inserted as fields in a PreparedStatement.  Databases handle Unicode better in fields.
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.11 $
*/
public class BaseDbFlatStorage
{
	/** Table name for resource records. */
	protected String m_resourceTableName = null;

	/** The field in the resource table that holds the resource id. */
	protected String m_resourceTableIdField = null;

	/** The field in the resource table that is used for sorting (first sort). */
	protected String m_resourceTableSortField1 = null;

	/** The field in the resource table that is used for sorting (second sort). */
	protected String m_resourceTableSortField2 = null;

	/** The full set of fields in the table to read and write. */
	protected String[] m_resourceTableFields = null;

	/** If true, we do our locks in the remote database using a separate locking table, otherwise we do them in the class. */
	protected boolean m_locksAreInTable = true;

	/** Locks (if used), keyed by reference, holding Edits. */
	protected Hashtable m_locks = null;

	/** If set, we treat reasource ids as case insensitive. */
	protected boolean m_caseInsensitive = false;

	/** Injected (by constructor) SqlService. */
	protected SqlService m_sql = null;

	/** SqlReader to use when reading the record. */
	protected SqlReader m_reader = null;

	/**
	* Construct.
	* @param resourceTableName Table name for resources.
	* @param resourceTableIdField The field in the resource table that holds the id.
	* @param resourceTableFields The complete set of fields to read / write for the resource.
	* @param locksInTable If true, we do our locks in the remote database in a locks table, otherwise we do them here.
	* @param reader A SqlReader which will produce Edits given fields read from the table.
	* @param sqlService The SqlService.
	*/
	public BaseDbFlatStorage(String resourceTableName, String resourceTableIdField,
		String[] resourceTableFields, boolean locksInTable, SqlReader reader,
		SqlService sqlService)
	{
		m_resourceTableName = resourceTableName;
		m_resourceTableIdField = resourceTableIdField;
		m_resourceTableSortField1 = resourceTableIdField;
		m_resourceTableFields = resourceTableFields;
		m_locksAreInTable = locksInTable;
		m_sql = sqlService;
		m_reader = reader;

	}	// BaseDbSingleStorage

	/**
	 * Set the sort field to be something perhaps other than the default of the id field.
	 * @param sortField1 The field name to use for sorting.
	 * @param sortField2 Optional second sort field.
	 */
	public void setSortField(String sortField1, String sortField2)
	{
		m_resourceTableSortField1 = sortField1;
		m_resourceTableSortField2 = sortField2;
	}

	/**
	* Open and be ready to read / write.
	*/
	public void open()
	{
		// setup for locks
		m_locks = new Hashtable();

	}   // open

	/**
	* Close.
	*/
	public void close()
	{
		if (!m_locks.isEmpty())
		{
			Logger.warn(this + ".close(): locks remain!");
			// %%%
		}
		m_locks.clear();
		m_locks = null;

	}   // close

	/**
	* Check if a Resource by this id exists.
	* @param id The id.
	* @return true if a Resource by this id exists, false if not.
	*/
	public boolean checkResource(String id)
	{
		// just see if the record exists
		String sql =
				"select " + m_resourceTableIdField + " from " + m_resourceTableName
			+   " where ( " + m_resourceTableIdField + " = ? )";

		Object fields[] = new Object[1];
		fields[0] = caseId(id);
		List ids = m_sql.dbRead(sql, fields, null);

		return (!ids.isEmpty());

	}	// check

	/**
	* Get the Resource with this id, or null if not found.
	* @param id The id.
	* @return The Resource with this id, or null if not found.
	*/
	public Resource getResource(String id)
	{
		Resource entry = null;

		// get the user from the db
		String sql =
				"select " + fieldList(m_resourceTableFields) + " from " + m_resourceTableName
			+   " where ( " + m_resourceTableIdField + " = ? )";

		Object fields[] = new Object[1];
		fields[0] = caseId(id);
		List rv = m_sql.dbRead(sql, fields, m_reader);

		if ((rv != null) && (rv.size() > 0))
		{
			entry = (Resource) rv.get(0);
		}

		return entry;

	}   // getResource

	public List getAllResources()
	{
		// read all resources from the db
		String sql = "select " + fieldList(m_resourceTableFields) + " from " + m_resourceTableName;

		List rv = m_sql.dbRead(sql, null, m_reader);

		return rv;

	}   // getAllResources

	public int countAllResources()
	{
		List all = new Vector();

		// read all count
		String sql = "select count(1) from " + m_resourceTableName;

		List results = m_sql.dbRead(sql, null,
			new SqlReader()
			{
				public Object readSqlResultRecord(ResultSet result)
				{
					try
					{
						int count = result.getInt(1);
						return new Integer(count);
					}
					catch (SQLException ignore) { return null;}
				}
			}
		 );

		if (results.isEmpty()) return 0;

		return ((Integer) results.get(0)).intValue();
	}

	public List getAllResources(int first, int last)
	{
		String sql;
		Object[] fields = null;
		if ("oracle".equals(m_sql.getVendor()))
		{
			// use Oracle RANK function
			sql = "select " + fieldList(m_resourceTableFields) + " from"
				+	" (select " + fieldList(m_resourceTableFields)
				+	" ,RANK() OVER"
				+	" (order by " + m_resourceTableSortField1
				+	(m_resourceTableSortField2 == null ? "" : "," + m_resourceTableSortField2)
				+	") as rank"
				+	" from " + m_resourceTableName
				+	" order by " + m_resourceTableSortField1
				+	(m_resourceTableSortField2 == null ? "" : "," + m_resourceTableSortField2)
				+ " )"
				+	" where rank between ? and ?";
			fields = new Object[2];
			fields[0] = new Long(first);
			fields[1] = new Long(last);
		}
		else
		{
			// use standard SQL LIMIT clause
			sql = "select " + fieldList(m_resourceTableFields) + " from " 
				+ m_resourceTableName
				+	" order by " + m_resourceTableSortField1
				+	(m_resourceTableSortField2 == null ? "" : "," + m_resourceTableSortField2)
				+	" limit " + (last-first+1) + " offset " + (first-1);
		}		

		List rv = m_sql.dbRead(sql, fields, m_reader);

		return rv;

	}   // getAllResources

	/**
	* Get all Resources matching a SQL where clause.
	* @param where The SQL where clause with bind variables indicated (not including the preceeding "where ").
	* @param values The bind values
	* @return The list of all Resources that meet the criteria.
	*/
	public List getSelectedResources(String where, Object[] values)
	{
		// read all resources from the db with a where
		String sql = "select " + fieldList(m_resourceTableFields) + " from " + m_resourceTableName
				+ " where " + where;

		List all = m_sql.dbRead(sql, values, m_reader);

		return all;
	}

	/**
	* Count all Resources matching a SQL where clause.
	* @param where The SQL where clause with bind variables indicated (not including the preceeding "where ").
	* @param values The bind values
	* @return The count of all Resources that meet the criteria.
	*/
	public int countSelectedResources(String where, Object[] values)
	{
		// read all resources from the db with a where
		String sql = "select count(1) from " + m_resourceTableName
				+ " where " + where;

		List results = m_sql.dbRead(sql, values,
			new SqlReader()
			{
				public Object readSqlResultRecord(ResultSet result)
				{
					try
					{
						int count = result.getInt(1);
						return new Integer(count);
					}
					catch (SQLException ignore) { return null;}
				}
			}
		 );
	
		if (results.isEmpty()) return 0;
	
		return ((Integer) results.get(0)).intValue();
	}

	/**
	* Get all Resources matching a SQL where clause.
	* @param where The SQL where clause with bind variables indicated (not including the preceeding "where ".
	* @param values The bind values
	* @return The list of all Resources that meet the criteria.
	*/
	public List getSelectedResources(String where, Object[] values, int first, int last)
	{

		Object[] fields;
		String sql;
		
		
		if ("oracle".equals(m_sql.getVendor()))
		{
			if (values != null)
			{
				fields = new Object[2 + values.length];
				System.arraycopy(values, 0, fields, 0, values.length);
			}
			else
			{
				fields = new Object[2];
			}

			// use Oracle RANK function
			sql = "select " + fieldList(m_resourceTableFields) + " from"
				+	" (select " + fieldList(m_resourceTableFields)
				+	" ,RANK() OVER"
				+	" (order by " + m_resourceTableSortField1
				+	(m_resourceTableSortField2 == null ? "" : "," + m_resourceTableSortField2)
				+	") as rank"
				+	" from " + m_resourceTableName
				+	" where " + where
				+	" order by " + m_resourceTableSortField1
				+	(m_resourceTableSortField2 == null ? "" : "," + m_resourceTableSortField2)
				+	" )"
				+	" where rank between ? and ?";
				fields[fields.length-2] = new Long(first);
				fields[fields.length-1] = new Long(last);
		}
		else
		{
			fields = values;
			// use standard SQL LIMIT clause
			sql = "select " + fieldList(m_resourceTableFields) + " from " 
				+ m_resourceTableName
				+	" where " + where
				+	" order by " + m_resourceTableSortField1
				+	(m_resourceTableSortField2 == null ? "" : "," + m_resourceTableSortField2)
				+	" limit " + (last-first+1) + " offset " + (first-1);
		}

		List rv = m_sql.dbRead(sql, fields, m_reader);

		return rv;
	}

	/**
	* Add a new Resource with this id.
	* @param id The id.
	* @param fields The fields to write.
	* @return The locked Resource object with this id, or null if the id is in use.
	*/
	public Edit putResource(String id, Object[] fields)
	{
		// process the insert
		boolean ok = insertResource(id, fields, null);

		// if this failed, assume a key conflict (i.e. id in use)
		if (!ok) return null;

		// now get a lock on the record for edit
		Edit edit = editResource(id);
		if (edit == null)
		{
			Logger.warn(this + ".putResource(): didn't get a lock!");
			return null;
		}
		
		return edit;

	}   // putResource

	/**
	* Add a new Resource with this id - no edit is returned, no lock is held.
	* @param id The id.
	* @param fields The fields to write.
	* @return True if successful, false if not.
	*/
	public boolean insertResource(String id, Object[] fields, Connection conn)
	{
		String statement =
				"insert into " + m_resourceTableName
			+	"( " + fieldList(m_resourceTableFields) + " )"
			+   " values ( "
			+	valuesParams(m_resourceTableFields)
			+   " )";

		// process the insert
		boolean ok = m_sql.dbWrite(conn, statement, fields);
		return ok;

	}   // putResource

	/**
	* Get a lock on the Resource with this id, or null if a lock cannot be gotten.
	* @param id The user id.
	* @return The locked Resource with this id, or null if this records cannot be locked.
	*/
	public Edit editResource(String id)
	{
		Edit edit = null;

		// if the locks are in a separate table in the db
		if (m_locksAreInTable)
		{
			// read the record - fail if not there
			Resource entry = getResource(id);
			if (entry == null) return null;

			// write a lock to the lock table - if we can do it, we get the lock
			String statement =
					"insert into SAKAI_LOCKS"
				+	" (TABLE_NAME,RECORD_ID,LOCK_TIME,USAGE_SESSION_ID)"
				+	" values (?, ?, ?, ?)";

			// we need session id and user id
			String sessionId = UsageSessionService.getSessionId();
			if (sessionId == null)
			{
				sessionId = "";
			}

			// collect the fields
			Object fields[] = new Object[4];
			fields[0] = m_resourceTableName;
			fields[1] = internalRecordId(caseId(id));
			fields[2] = TimeService.newTime();
			fields[3] = sessionId;

			// add the lock - if fails, someone else has the lock
			boolean ok = m_sql.dbWrite(statement, fields);
			if (!ok)
			{
				return null;
			}
			
			// we got the lock! - make the edit from the Resource
			edit = (Edit) entry;
		}

		// otherwise, get the lock locally
		else
		{
			// get the entry, and check for existence
			Resource entry = getResource(id);
			if (entry == null) return null;

			// we only sync this getting - someone may release a lock out of sync
			synchronized (m_locks)
			{
				// if already locked
				if (m_locks.containsKey(entry.getReference())) return null;
				
				// make the edit from the Resource
				edit = (Edit) entry;

				// store the edit in the locks by reference
				m_locks.put(entry.getReference(), edit);
			}
		}

		return edit;

	}	// editResource

	/**
	* Commit the changes and release the lock.
	* @param edit The Edit to commit.
	* @param fields The set of fields to write to the db, plus the id field as it is to be written again at the end.
	*/
	public void commitResource(Edit edit, Object fields[])
	{
		String statement =
				"update " + m_resourceTableName
			+	" set "
			+ 	updateSet(m_resourceTableFields)
			+   " where ( " + m_resourceTableIdField + " = ? )";

		if (m_locksAreInTable)
		{
			// process the update
			m_sql.dbWrite(statement, fields);

			// remove the lock
			statement =
					"delete from SAKAI_LOCKS where TABLE_NAME = ? and RECORD_ID = ?";

			// collect the fields
			Object lockFields[] = new Object[2];
			lockFields[0] = m_resourceTableName;
			lockFields[1] = internalRecordId(caseId(edit.getId()));
			boolean ok = m_sql.dbWrite(statement, lockFields);
			if (!ok)
			{
				Logger.warn(this + ".commit: missing lock for table: " + lockFields[0] + " key: " + lockFields[1]);
			}
		}

		else
		{
			// just process the update
			m_sql.dbWrite(statement, fields);

			// remove the lock
			m_locks.remove(edit.getReference());
		}

	}	// commitResource

	/**
	* Cancel the changes and release the lock.
	* @param user The Edit to cancel.
	*/
	public void cancelResource(Edit edit)
	{
		if (m_locksAreInTable)
		{
			// remove the lock
			String statement =
					"delete from SAKAI_LOCKS where TABLE_NAME = ? and RECORD_ID = ?";

			// collect the fields
			Object lockFields[] = new Object[2];
			lockFields[0] = m_resourceTableName;
			lockFields[1] = internalRecordId(caseId(edit.getId()));
			boolean ok = m_sql.dbWrite(statement, lockFields);
			if (!ok)
			{
				Logger.warn(this + ".cancel: missing lock for table: " + lockFields[0] + " key: " + lockFields[1]);
			}
		}

		else
		{
			// release the lock
			m_locks.remove(edit.getReference());
		}

	}	// cancelResource

	/**
	* Remove this (locked) Resource.
	* @param user The Edit to remove.
	*/
	public void removeResource(Edit edit)
	{
		// form the SQL delete statement
		String statement =
				"delete from " + m_resourceTableName
				+   " where ( " + m_resourceTableIdField + " = ? )";

		Object fields[] = new Object[1];
		fields[0] = caseId(edit.getId());

		if (m_locksAreInTable)
		{
			// process the delete statement
			m_sql.dbWrite(statement, fields);

			// remove the lock
			statement =
					"delete from SAKAI_LOCKS where TABLE_NAME = ? and RECORD_ID = ?";

			// collect the fields
			Object lockFields[] = new Object[2];
			lockFields[0] = m_resourceTableName;
			lockFields[1] = internalRecordId(caseId(edit.getId()));
			boolean ok = m_sql.dbWrite(statement, lockFields);
			if (!ok)
			{
				Logger.warn(this + ".remove: missing lock for table: " + lockFields[0] + " key: " + lockFields[1]);
			}
		}

		else
		{
			// process the delete statement
			m_sql.dbWrite(statement, fields);

			// release the lock
			m_locks.remove(edit.getReference());
		}

	}   // removeResource

	/**
	* Form a string of n question marks with commas, for sql value statements, one for each
	* item in the values array, or an empty string if null.
	* @param values The values to be inserted into the sql statement.
	* @return A sql statement fragment for the values part of an insert, one for each value in the array.
	*/
	protected String valuesParams(String[] fields)
	{
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < fields.length - 1; i++)
		{
			buf.append(" ?,");
		}
		
		// for the last field
		buf.append(" ? ");

		return buf.toString();

	}	// valuesParams

	/**
	* Form a string of n name=?, for sql update set statements, one for each
	* item in the values array, or an empty string if null.
	* @param values The values to be inserted into the sql statement.
	* @return A sql statement fragment for the values part of an insert, one for each value in the array.
	*/
	protected String updateSet(String[] fields)
	{
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < fields.length-1; i++)
		{
			buf.append(fields[i] + " = ?,");
		}
		buf.append(fields[fields.length-1] + "= ?");

		return buf.toString();

	}	// updateSet

	/**
	* Form a string of field, field, field - one for each item in the fields array.
	* @param fields The field names.
	* @return A string of field, field, field - one for each item in the fields array.
	*/
	protected String fieldList(String[] fields)
	{
		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < fields.length-1; i++)
		{
			buf.append(fields[i] + ",");
		}

		buf.append(fields[fields.length-1]);

		return buf.toString();

	}	// fieldList

	/**
	* Fix the case of resource ids to support case insensitive ids if enabled
	* @param The id to fix.
	* @return The id, case modified as needed.
	*/
	protected String caseId(String id)
	{
		if (m_caseInsensitive)
		{
			return id.toLowerCase();
		}

		return id;

	}	// caseId

	/**
	* Enable / disable case insensitive ids.
	* @param setting true to set case insensitivity, false to set case sensitivity.
	*/
	protected void setCaseInsensitivity(boolean setting)
	{
		m_caseInsensitive = setting;

	}	// setCaseInsensitivity

	/**
	 * Return a record ID to use internally in the database.
	 * This is needed for databases (MySQL) that have limits on key lengths.  
	 * The hash code ensures that the record ID will be unique, 
	 * even if the DB only considers a prefix of a very long record ID.
	 * @param recordId
	 * @return The record ID to use internally in the database
	 */
	private String internalRecordId(String recordId)
	{
		if ("oracle".equals(m_sql.getVendor()))
		{
			return recordId;
		}
		else //if ("mysql".equals(m_sql.getVendor()))
		{
			if (recordId == null) recordId = "null";
			return recordId.hashCode() + " - " + recordId;
		}
	}
	
}   // BaseDbSingleStorage

/**********************************************************************************
*
* $Header: /cvs/sakai/util/src/java/org/sakaiproject/util/storage/BaseDbFlatStorage.java,v 1.11 2004/10/14 22:42:31 janderse.umich.edu Exp $
*
**********************************************************************************/
