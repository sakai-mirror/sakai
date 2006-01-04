/**********************************************************************************
* $URL$
* $Id$
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
package org.sakaiproject.service.framework.sql;

//imports
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.List;

import org.sakaiproject.exception.ServerOverloadException;

/**
* <p>SqlService provides access to pooled Connections to Sql databases.</p>
* <p>The Connection objects managed by this service are standard java.sql.Connection objects.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
*/
public interface SqlService
{
	// TODO: we don't need this now -ggolden
	/** The poolId for which can be used to get at the default pool. */
	static final String DEFAULT = "default";

	/**
	 * Access an available or newly created Connection from the default pool.
	 * Will wait a while until one is available.
	 * @return The Connection object.
	 * @throws SQLException if a connection cannot be delivered.
	 */
	Connection borrowConnection() throws SQLException;

	/**
	 * Release a database connection.
	 * @param conn The connetion to release.  If null or not one of ours, ignored.
	 */
	void returnConnection(Connection conn);

	/*******************************************************************************
	* Sql operations
	*******************************************************************************/

	/**
	 * Read a single field from the db, from multiple records, returned as string[], one per record.
	 * @param sql The sql statement.
	 * @return The List of Strings of single fields of the record found, or empty if none found.
	 */
	List dbRead(String sql);

	/**
	 * Process a query, filling in with fields, and return the results as a List, one per record read.
	 * If a reader is provided, it will be called for each record to prepare the Object placed into the List.
	 * Otherwise, the first field of each record, as a String, will be placed in the list.
	 * @param sql The sql statement.
	 * @param fields The array of fields for parameters.
	 * @param reader The reader object to read each record.
	 * @return The List of things read, one per record.
	 */
	List dbRead(String sql, Object[] fields, SqlReader reader);

	/**
	 * Process a query, filling in with fields, and return the results as a List, one per record read.
	 * If a reader is provided, it will be called for each record to prepare the Object placed into the List.
	 * Otherwise, the first field of each record, as a String, will be placed in the list.
	 * @param conn The db connection object to use.
	 * @param sql The sql statement.
	 * @param fields The array of fields for parameters.
	 * @param reader The reader object to read each record.
	 * @return The List of things read, one per record.
	 */
	List dbRead(Connection conn, String sql, Object[] fields, SqlReader reader);
	
	/**
	 * Read a single field from the db, from multiple record - concatenating the binary values into value.
	 * @param sql The sql statement.
	 * @param fields The array of fields for parameters.
	 * @param value The array of bytes to fill with the value read from the db.
	 */
	void dbReadBinary(String sql, Object[] fields, byte[] value);
	
	/**
	 * Read a single field from the db, from multiple record - concatenating the binary values into value.
	 * @param conn The optional db connection object to use.
	 * @param sql The sql statement.
	 * @param fields The array of fields for parameters.
	 * @param value The array of bytes to fill with the value read from the db.
	 */
	void dbReadBinary(Connection conn, String sql, Object[] fields, byte[] value);

	/**
	 * Read a single field / record from the db, returning a stream on the result record / field.
	 * The stream holds the conection open - so it must be closed or finalized quickly!
	 * @param sql The sql statement.
	 * @param fields The array of fields for parameters.
	 * @param big If true, the read is expected to be potentially large.
	 * @throws ServerOverloadException if the read cannot complete due to lack of a free connection (if wait is false)
	 */
	InputStream dbReadBinary(String sql, Object[] fields, boolean big) throws ServerOverloadException;

	/**
	 * Execute the "write" sql - no response.
	 * @param sql The sql statement.
	 * @return true if successful, false if not.
	 */
	boolean dbWrite(String sql);

	/**
	 * Execute the "write" sql - no response.
	 * a long field is set to "?" - fill it in with var
	 * @param sql The sql statement.
	 * @param var The value to bind to the first parameter in the sql statement.
	 * @return true if successful, false if not.
	 */
	boolean dbWrite(String sql, String var);

	/**
	 * Execute the "write" sql - no response.
	 * a long binary field is set to "?" - fill it in with var
	 * @param sql The sql statement.
	 * @param fields The array of fields for parameters.
	 * @param var The value to bind to the last parameter in the sql statement.
	 * @param offset The start within the var to write
	 * @param len The number of bytes of var, starting with index, to write
	 * @return true if successful, false if not.
	 */
	boolean dbWriteBinary(String sql, Object[] fields, byte[] var, int offset, int len);
	
	/**
	 * Execute the "write" sql - no response, using a set of fields from an array.
	 * @param sql The sql statement.
	 * @param fields The array of fields for parameters.
	 * @return true if successful, false if not.
	 */
	boolean dbWrite(String sql, Object[] fields);

	/**
	 * Execute the "write" sql - no response, using a set of fields from an array and a given connection.
	 * @param connection The connection to use.
	 * @param sql The sql statement.
	 * @param fields The array of fields for parameters.
	 * @return true if successful, false if not.
	 */
	boolean dbWrite(Connection connection, String sql, Object[] fields);

	/**
	 * Execute the "write" sql - no response, using a set of fields from an array and a given connection
	 * logging no errors on failure.
	 * @param connection The connection to use.
	 * @param sql The sql statement.
	 * @param fields The array of fields for parameters.
	 * @return true if successful, false if not.
	 */
	boolean dbWriteFailQuiet(Connection connection, String sql, Object[] fields);

	/**
	 * Execute the "write" sql - no response, using a set of fields from an array plus one more as params.
	 * @param sql The sql statement.
	 * @param fields The array of fields for parameters.
	 * @param lastField The value to bind to the last parameter in the sql statement.
	 * @return true if successful, false if not.
	 */
	boolean dbWrite(String sql, Object[] fields, String lastField);
	
	/**
	 * Read a single field BLOB from the db from one record, and update it's bytes
	 * with content.
	 * @param sql The sql statement to select the BLOB.
	 * @param content The new bytes for the BLOB.
	 */
	void dbReadBlobAndUpdate(String sql, byte[] content);

	/**
	 * Read a single field from the db, from a single record, return the value found, and lock for update. 
	 * @param sql The sql statement.
	 * @param field A StringBuffer that will be filled with the field.
	 * @return The Connection holding the lock.
	 */
	Connection dbReadLock(String sql, StringBuffer field);
	
	/**
	 * Commit the update that was locked on this connection.
	 * @param sql The sql statement.
	 * @param fields The array of fields for parameters.
	 * @param var The value to bind to the last parameter in the sql statement.
	 * @param conn The database connection on which the lock was gained.
	 */
	void dbUpdateCommit(String sql, Object[] fields, String var, Connection conn);
	
	/**
	 * Cancel the update that was locked on this connection.
	 * @param conn The database connection on which the lock was gained.
	 */
	void dbCancel(Connection conn);

	/**
	 * Access the calendar used in processing Time objects for Sql.
	 * @return The calendar used in processing Time objects for Sql.
	 */
	GregorianCalendar getCal();
	
	/** 
	 * @return a string indicating the database vendor - "oracle" or "mysql" or "hsqldb".
	 */
	String getVendor();

	/**
	 * Load and run the named file using the given class loader, as a ddl check / create.
	 * The first non-comment ('--') line will be run, and if successfull, all other non-comment lines will be run.
	 * SQL statements must be on a single line, and may have ';' terminators.
	 * @param loader The ClassLoader used to load the resource.
	 * @param resource The path name to the resource - vender string and .sql will be added
	 */
	void ddl(ClassLoader loader, String resource);
}



