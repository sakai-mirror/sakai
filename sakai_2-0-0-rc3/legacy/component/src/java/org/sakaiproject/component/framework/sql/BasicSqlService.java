/**********************************************************************************
 *
 * $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/framework/sql/BasicSqlService.java,v 1.8 2005/06/05 02:25:34 ggolden.umich.edu Exp $
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
package org.sakaiproject.component.framework.sql;

// imports
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.service.framework.current.cover.CurrentService;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.framework.sql.SqlReader;
import org.sakaiproject.service.framework.sql.SqlService;
import org.sakaiproject.service.legacy.time.Time;

/**
 * <p>
 * BasicSqlService implements the SqlService to support (mostly Legacy) Sakai code.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.8 $
 */
public class BasicSqlService implements SqlService
{
	private static final Log LOG = LogFactory.getLog(BasicSqlService.class);

	private static final Log SWC_LOG = LogFactory.getLog(StreamWithConnection.class);

	/** The "shared", "common" database connection pool */
	protected DataSource defaultDataSource;

	/** The "slow" connection pool for file uploads/downloads */
	protected DataSource longDataSource;

	/** Should we do a commit after a single statement read? */
	protected boolean m_commitAfterRead = false;

	/**
	 * Configuration: should we do a commit after each single SQL read?
	 * 
	 * @param value
	 *        the setting (true of false) string.
	 */
	public void setCommitAfterRead(String value)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("setCommitAfterRead(String " + value + ")");
		}

		m_commitAfterRead = new Boolean(value).booleanValue();
	}

	/** Database vendor used; possible values are oracle, mysql, hsqldb (default). */
	protected String m_vendor = "hsqldb";

	/**
	 * Configuration: Database vendor used; possible values are oracle, mysql, hsqldb.
	 * 
	 * @param value
	 *        the Database vendor used.
	 */
	public void setVendor(String value)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("setVendor(String " + value + ")");
		}

		m_vendor = (value != null) ? value.toLowerCase() : null;
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Dependencies and their setter methods
	 *********************************************************************************************************************************************************************************************************************************************************/

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
		if (LOG.isDebugEnabled())
		{
			LOG.debug("setAutoDdl(String " + value + ")");
		}

		m_autoDdl = new Boolean(value).booleanValue();
	}

	/**
	 * @inheritDoc
	 */
	public String getVendor()
	{
		return m_vendor;
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Init and Destroy
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		LOG.info(this + ".init()");

		// if we are auto-creating our schema, check and create
		if (m_autoDdl)
		{
			ddl(getClass().getClassLoader(), "sakai_locks");
		}

		LOG.info(this + ".init() completed successfully");
	}

	/**
	 * Final cleanup.
	 */
	public void destroy()
	{
		LOG.info(this + ".destroy()");
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Work interface methods: org.sakaiproject.sql.SqlService
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * {@inheritDoc}
	 */
	public Connection borrowConnection() throws SQLException
	{
		LOG.debug("borrowConnection()");

		if (defaultDataSource != null)
		{
			return defaultDataSource.getConnection();
		}
		else
		{
			throw new SQLException("no default pool.");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void returnConnection(Connection conn)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("returnConnection(Connection " + conn + ")");
		}

		if (conn != null)
		{
			try
			{
				conn.close();
			}
			catch (SQLException e)
			{
				throw new Error(e);
			}
		}
	}

	/** Used to work with dates in GMT in the db. */
	protected final GregorianCalendar m_cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));

	/**
	 * {@inheritDoc}
	 */
	public GregorianCalendar getCal()
	{
		return m_cal;
	}

	/**
	 * Read a single field from the db, from multiple records, returned as string[], one per record.
	 * 
	 * @param sql
	 *        The sql statement.
	 * @return The List of Strings of single fields of the record found, or empty if none found.
	 */
	public List dbRead(String sql)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("dbRead(String " + sql + ")");
		}

		return dbRead(sql, null, null);
	} // dbRead

	/**
	 * Process a query, filling in with fields, and return the results as a List, one per record read. If a reader is provided, it will be called for each record to prepare the Object placed into the List. Otherwise, the first field of each record, as a
	 * String, will be placed in the list.
	 * 
	 * @param sql
	 *        The sql statement.
	 * @param fields
	 *        The array of fields for parameters.
	 * @param reader
	 *        The reader object to read each record.
	 * @return The List of things read, one per record.
	 */
	public List dbRead(String sql, Object[] fields, SqlReader reader)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("dbRead(String " + sql + ", Object[] " + fields + ", SqlReader " + reader + ")");
		}

		return dbRead(null, sql, fields, reader);
	}

	/**
	 * Process a query, filling in with fields, and return the results as a List, one per record read. If a reader is provided, it will be called for each record to prepare the Object placed into the List. Otherwise, the first field of each record, as a
	 * String, will be placed in the list.
	 * 
	 * @param callerConn
	 *        The db connection object to use (if not null).
	 * @param sql
	 *        The sql statement.
	 * @param fields
	 *        The array of fields for parameters.
	 * @param reader
	 *        The reader object to read each record.
	 * @return The List of things read, one per record.
	 */
	public List dbRead(Connection callerConn, String sql, Object[] fields, SqlReader reader)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("dbRead(Connection " + callerConn + ", String " + sql + ", Object[] " + fields + ", SqlReader " + reader
					+ ")");
		}

		// for DEBUG
		long start = 0;
		long connectionTime = 0;
		int lenRead = 0;
		long stmtTime = 0;
		long resultsTime = 0;
		int count = 0;

		if (LOG.isDebugEnabled())
		{
			String userId = UsageSessionService.getSessionId();
			StringBuffer buf = new StringBuffer();
			if (fields != null)
			{
				buf.append(fields[0]);
				for (int i = 1; i < fields.length; i++)
				{
					buf.append(", ");
					buf.append(fields[i]);
				}
			}
			LOG.debug("Sql.dbRead: " + userId + "\n" + sql + "\n" + buf);
		}

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		ResultSetMetaData meta = null;
		List rv = new Vector();

		try
		{
			if (org.sakaiproject.service.framework.current.CurrentService.DEBUG) start = System.currentTimeMillis();

			// borrow a new connection if we are not provided with one to use
			if (callerConn != null)
			{
				conn = callerConn;
			}
			else
			{
				conn = borrowConnection();
			}
			if (org.sakaiproject.service.framework.current.CurrentService.DEBUG)
				connectionTime = System.currentTimeMillis() - start;
			if (org.sakaiproject.service.framework.current.CurrentService.DEBUG) start = System.currentTimeMillis();

			pstmt = conn.prepareStatement(sql);

			// put in all the fields
			prepareStatement(pstmt, fields);

			result = pstmt.executeQuery();

			if (org.sakaiproject.service.framework.current.CurrentService.DEBUG) stmtTime = System.currentTimeMillis() - start;
			if (org.sakaiproject.service.framework.current.CurrentService.DEBUG) start = System.currentTimeMillis();

			while (result.next())
			{
				if (org.sakaiproject.service.framework.current.CurrentService.DEBUG) count++;

				try
				{
					// without a reader, we read the first String from each record
					if (reader == null)
					{
						String s = result.getString(1);
						if (s != null) rv.add(s);
					}
					else
					{
						Object obj = reader.readSqlResultRecord(result);
						if (obj != null) rv.add(obj);
					}
				}
				catch (Throwable t)
				{
					LOG.warn("Sql.dbRead: unable to read a result from sql: " + sql + debugFields(fields) + " row: "
							+ result.getRow());
				}
			}
		}
		catch (Exception e)
		{
			LOG.warn("Sql.dbRead: sql: " + sql + debugFields(fields), e);
		}
		finally
		{
			if (org.sakaiproject.service.framework.current.CurrentService.DEBUG) resultsTime = System.currentTimeMillis() - start;

			try
			{
				if (null != result) result.close();
				if (null != pstmt) pstmt.close();

				// return the connection only if we have borrowed a new one for this call
				if (callerConn == null)
				{
					if (null != conn)
					{
						// if we commit on read
						if (m_commitAfterRead)
						{
							conn.commit();
						}

						returnConnection(conn);
					}
				}
			}
			catch (Exception e)
			{
				LOG.warn("Sql.dbRead: sql: " + sql + debugFields(fields), e);
			}
		}

		if (org.sakaiproject.service.framework.current.CurrentService.DEBUG)
			debug("Sql.dbRead: connection: " + connectionTime + " read: " + count + " sql: " + stmtTime + " process: "
					+ resultsTime, sql, fields);

		return rv;

	} // dbRead

	/**
	 * Read a single field from the db, from multiple record - concatenating the binary values into value.
	 * 
	 * @param sql
	 *        The sql statement.
	 * @param fields
	 *        The array of fields for parameters.
	 * @param value
	 *        The array of bytes to fill with the value read from the db.
	 */
	public void dbReadBinary(String sql, Object[] fields, byte[] value)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("dbReadBinary(String " + sql + ", Object[] " + fields + ", byte[] " + value + ")");
		}

		dbReadBinary(null, sql, fields, value);
	}

	/**
	 * Read a single field from the db, from multiple record - concatenating the binary values into value.
	 * 
	 * @param callerConn
	 *        The optional db connection object to use.
	 * @param sql
	 *        The sql statement.
	 * @param fields
	 *        The array of fields for parameters.
	 * @param value
	 *        The array of bytes to fill with the value read from the db.
	 */
	public void dbReadBinary(Connection callerConn, String sql, Object[] fields, byte[] value)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("dbReadBinary(Connection " + callerConn + ", String " + sql + ", Object[] " + fields + ", byte[] " + value
					+ ")");
		}

		// for DEBUG
		long start = 0;
		long connectionTime = 0;
		int lenRead = 0;

		if (LOG.isDebugEnabled())
		{
			String userId = UsageSessionService.getSessionId();
			LOG.debug("Sql.dbReadBinary(): " + userId + "\n" + sql);
		}

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		ResultSetMetaData meta = null;

		try
		{
			if (org.sakaiproject.service.framework.current.CurrentService.DEBUG) start = System.currentTimeMillis();
			if (callerConn != null)
			{
				conn = callerConn;
			}
			else
			{
				conn = borrowConnection();
			}
			if (org.sakaiproject.service.framework.current.CurrentService.DEBUG)
				connectionTime = System.currentTimeMillis() - start;
			if (org.sakaiproject.service.framework.current.CurrentService.DEBUG) start = System.currentTimeMillis();

			pstmt = conn.prepareStatement(sql);

			// put in all the fields
			prepareStatement(pstmt, fields);

			result = pstmt.executeQuery();

			int index = 0;
			while (result.next() && (index < value.length))
			{
				InputStream stream = result.getBinaryStream(1);
				int len = stream.read(value, index, value.length - index);
				stream.close();
				index += len;
				if (org.sakaiproject.service.framework.current.CurrentService.DEBUG) lenRead += len;
			}
		}
		catch (Exception e)
		{
			LOG.warn("Sql.dbReadBinary(): " + e);
		}
		finally
		{
			try
			{
				if (null != result) result.close();
				if (null != pstmt) pstmt.close();

				// return the connection only if we have borrowed a new one for this call
				if (callerConn == null)
				{
					if (null != conn)
					{
						// if we commit on read
						if (m_commitAfterRead)
						{
							conn.commit();
						}

						returnConnection(conn);
					}
				}
			}
			catch (Exception e)
			{
				LOG.warn("Sql.dbReadBinary(): " + e);
			}
		}

		if (org.sakaiproject.service.framework.current.CurrentService.DEBUG)
			debug("sql read binary: len: " + lenRead + "  connection: " + connectionTime + "  statement: "
					+ (System.currentTimeMillis() - start), sql, fields);

	} // dbReadBinary

	/**
	 * Read a single field / record from the db, returning a stream on the result record / field. The stream holds the conection open - so it must be closed or finalized quickly!
	 * 
	 * @param sql
	 *        The sql statement.
	 * @param fields
	 *        The array of fields for parameters.
	 * @param big
	 *        If true, the read is expected to be potentially large.
	 * @throws ServerOverloadException
	 *         if the read cannot complete due to lack of a free connection (if wait is false)
	 */
	public InputStream dbReadBinary(String sql, Object[] fields, boolean big) throws ServerOverloadException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("dbReadBinary(String " + sql + ", Object[] " + fields + ", boolean " + big + ")");
		}

		InputStream rv = null;

		// for DEBUG
		long start = 0;
		long connectionTime = 0;
		int lenRead = 0;

		if (LOG.isDebugEnabled())
		{
			String userId = UsageSessionService.getSessionId();
			LOG.debug("Sql.dbReadBinary(): " + userId + "\n" + sql);
		}

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		ResultSetMetaData meta = null;

		try
		{
			if (org.sakaiproject.service.framework.current.CurrentService.DEBUG) start = System.currentTimeMillis();
			if (!big)
			{
				conn = borrowConnection();
			}
			else
			{
				// get a connection if it's available, else throw
				conn = borrowConnection();
				if (conn == null)
				{
					throw new ServerOverloadException(null);
				}
			}
			if (org.sakaiproject.service.framework.current.CurrentService.DEBUG)
				connectionTime = System.currentTimeMillis() - start;
			if (org.sakaiproject.service.framework.current.CurrentService.DEBUG) start = System.currentTimeMillis();

			pstmt = conn.prepareStatement(sql);

			// put in all the fields
			prepareStatement(pstmt, fields);

			result = pstmt.executeQuery();

			if (result.next())
			{
				InputStream stream = result.getBinaryStream(1);
				rv = new StreamWithConnection(stream, result, pstmt, conn);
			}
		}
		catch (ServerOverloadException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			LOG.warn("Sql.dbReadBinary(): " + e);
		}
		finally
		{
			// ONLY if we didn't make the rv - else let the rv hold these OPEN!
			if (rv == null)
			{
				try
				{
					if (null != result) result.close();
					if (null != pstmt) pstmt.close();
					if (null != conn)
					{
						// if we commit on read
						if (m_commitAfterRead)
						{
							conn.commit();
						}

						// return to the proper pool!
						if (big)
						{
							returnConnection(conn);
						}
						else
						{
							returnConnection(conn);
						}
					}
				}
				catch (Exception e)
				{
					LOG.warn("Sql.dbReadBinary(): " + e);
				}
			}
		}

		if (org.sakaiproject.service.framework.current.CurrentService.DEBUG)
			debug("sql read binary: len: " + lenRead + "  connection: " + connectionTime + "  statement: "
					+ (System.currentTimeMillis() - start), sql, fields);

		return rv;

	} // dbReadBinary

	/**
	 * Execute the "write" sql - no response.
	 * 
	 * @param sql
	 *        The sql statement.
	 * @return true if successful, false if not.
	 */
	public boolean dbWrite(String sql)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("dbWrite(String " + sql + ")");
		}

		return dbWrite(sql, null, null, null, false);

	} // dbWrite

	/**
	 * Execute the "write" sql - no response. a long field is set to "?" - fill it in with var
	 * 
	 * @param sql
	 *        The sql statement.
	 * @param var
	 *        The value to bind to the first parameter in the sql statement.
	 * @return true if successful, false if not.
	 */
	public boolean dbWrite(String sql, String var)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("dbWrite(String " + sql + ", String " + var + ")");
		}

		return dbWrite(sql, null, var, null, false);

	} // dbWrite

	/**
	 * Execute the "write" sql - no response. a long binary field is set to "?" - fill it in with var
	 * 
	 * @param sql
	 *        The sql statement.
	 * @param fields
	 *        The array of fields for parameters.
	 * @param var
	 *        The value to bind to the last parameter in the sql statement.
	 * @param offset
	 *        The start within the var to write
	 * @param len
	 *        The number of bytes of var, starting with index, to write
	 * @return true if successful, false if not.
	 */
	public boolean dbWriteBinary(String sql, Object[] fields, byte[] var, int offset, int len)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("dbWriteBinary(String " + sql + ", Object[] " + fields + ", byte[] " + var + ", int " + offset + ", int "
					+ len + ")");
		}

		// for DEBUG
		long start = 0;
		long connectionTime = 0;

		if (LOG.isDebugEnabled())
		{
			String userId = UsageSessionService.getSessionId();
			LOG.debug("Sql.dbWriteBinary(): " + userId + "\n" + sql + "  size:" + var.length);
		}

		Connection conn = null;
		PreparedStatement pstmt = null;
		boolean autoCommit = false;
		boolean resetAutoCommit = false;

		// stream from the var
		InputStream varStream = new ByteArrayInputStream(var, offset, len);

		boolean success = false;

		try
		{
			if (org.sakaiproject.service.framework.current.CurrentService.DEBUG) start = System.currentTimeMillis();
			conn = borrowConnection();
			if (org.sakaiproject.service.framework.current.CurrentService.DEBUG)
				connectionTime = System.currentTimeMillis() - start;

			// make sure we do not have auto commit - will change and reset if needed
			autoCommit = conn.getAutoCommit();
			if (autoCommit)
			{
				conn.setAutoCommit(false);
				resetAutoCommit = true;
			}

			if (org.sakaiproject.service.framework.current.CurrentService.DEBUG) start = System.currentTimeMillis();
			pstmt = conn.prepareStatement(sql);

			// put in all the fields
			int pos = prepareStatement(pstmt, fields);

			// last, put in the binary
			pstmt.setBinaryStream(pos, varStream, len);

			int result = pstmt.executeUpdate();

			// commit and indicate success
			conn.commit();
			success = true;
		}
		catch (SQLException e)
		{
			// this is likely due to a key constraint problem...
			return false;
		}
		catch (Exception e)
		{
			LOG.warn("Sql.dbWriteBinary(): " + e);
			return false;
		}
		finally
		{
			try
			{
				if (null != pstmt) pstmt.close();
				varStream.close();
				if (null != conn)
				{
					// rollback on failure
					if (!success)
					{
						conn.rollback();
					}

					// if we changed the auto commit, reset here
					if (resetAutoCommit)
					{
						conn.setAutoCommit(autoCommit);
					}
					returnConnection(conn);
				}
			}
			catch (Exception e)
			{
				LOG.warn("Sql.dbWriteBinary(): " + e);
			}
		}

		if (org.sakaiproject.service.framework.current.CurrentService.DEBUG)
			debug("sql write binary: len: " + len + "  connection: " + connectionTime + "  statement: "
					+ (System.currentTimeMillis() - start), sql, fields);

		return true;

	} // dbWriteBinary

	/**
	 * Execute the "write" sql - no response, using a set of fields from an array plus one more as params.
	 * 
	 * @param sql
	 *        The sql statement.
	 * @param fields
	 *        The array of fields for parameters.
	 * @return true if successful, false if not.
	 */
	public boolean dbWrite(String sql, Object[] fields)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("dbWrite(String " + sql + ", Object[] " + fields + ")");
		}

		return dbWrite(sql, fields, null, null, false);

	} // dbWrite

	/**
	 * Execute the "write" sql - no response, using a set of fields from an array and a given connection.
	 * 
	 * @param connection
	 *        The connection to use.
	 * @param sql
	 *        The sql statement.
	 * @param fields
	 *        The array of fields for parameters.
	 * @return true if successful, false if not.
	 */
	public boolean dbWrite(Connection connection, String sql, Object[] fields)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("dbWrite(Connection " + connection + ", String " + sql + ", Object[] " + fields + ")");
		}

		return dbWrite(sql, fields, null, connection, false);
	}

	/**
	 * Execute the "write" sql - no response, using a set of fields from an array and a given connection logging no errors on failure.
	 * 
	 * @param connection
	 *        The connection to use.
	 * @param sql
	 *        The sql statement.
	 * @param fields
	 *        The array of fields for parameters.
	 * @return true if successful, false if not.
	 */
	public boolean dbWriteFailQuiet(Connection connection, String sql, Object[] fields)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("dbWriteFailQuiet(Connection " + connection + ", String " + sql + ", Object[] " + fields + ")");
		}

		return dbWrite(sql, fields, null, connection, true);
	}

	/**
	 * Execute the "write" sql - no response, using a set of fields from an array plus one more as params.
	 * 
	 * @param sql
	 *        The sql statement.
	 * @param fields
	 *        The array of fields for parameters.
	 * @param lastField
	 *        The value to bind to the last parameter in the sql statement.
	 * @return true if successful, false if not.
	 */
	public boolean dbWrite(String sql, Object[] fields, String lastField)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("dbWrite(String " + sql + ", Object[] " + fields + ", String " + lastField + ")");
		}

		return dbWrite(sql, fields, lastField, null, false);
	}

	/**
	 * Execute the "write" sql - no response, using a set of fields from an array plus one more as params and connection.
	 * 
	 * @param sql
	 *        The sql statement.
	 * @param fields
	 *        The array of fields for parameters.
	 * @param lastField
	 *        The value to bind to the last parameter in the sql statement.
	 * @param callerConnection
	 *        The connection to use.
	 * @param failQuiet
	 *        If true, don't log errors from statement failure
	 * @return true if successful, false if not.
	 */
	protected boolean dbWrite(String sql, Object[] fields, String lastField, Connection callerConnection, boolean failQuiet)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("dbWrite(String " + sql + ", Object[] " + fields + ", String " + lastField + ", Connection "
					+ callerConnection + ", boolean " + failQuiet + ")");
		}

		// for DEBUG
		long start = 0;
		long connectionTime = 0;

		if (LOG.isDebugEnabled())
		{
			String userId = UsageSessionService.getSessionId();
			StringBuffer buf = new StringBuffer();
			if (fields != null)
			{
				buf.append(fields[0]);
				for (int i = 1; i < fields.length; i++)
				{
					buf.append(", ");
					buf.append(fields[i]);
				}
				if (lastField != null)
				{
					buf.append(", ");
					buf.append(lastField);
				}
			}
			else if (lastField != null)
			{
				buf.append(lastField);
			}
			LOG.debug("Sql.dbWrite(): " + userId + "\n" + sql + "\n" + buf);
		}

		Connection conn = null;
		PreparedStatement pstmt = null;
		boolean autoCommit = false;
		boolean resetAutoCommit = false;

		boolean success = false;

		try
		{
			if (callerConnection != null)
			{
				conn = callerConnection;
			}
			else
			{
				if (org.sakaiproject.service.framework.current.CurrentService.DEBUG) start = System.currentTimeMillis();
				conn = borrowConnection();
				if (org.sakaiproject.service.framework.current.CurrentService.DEBUG)
					connectionTime = System.currentTimeMillis() - start;

				// make sure we have do not have auto commit - will change and reset if needed
				autoCommit = conn.getAutoCommit();
				if (autoCommit)
				{
					conn.setAutoCommit(false);
					resetAutoCommit = true;
				}
			}

			if (org.sakaiproject.service.framework.current.CurrentService.DEBUG) start = System.currentTimeMillis();
			pstmt = conn.prepareStatement(sql);

			// put in all the fields
			int pos = prepareStatement(pstmt, fields);

			// last, put in the string value
			if (lastField != null)
			{
				pstmt.setCharacterStream(pos, new StringReader(lastField), lastField.length());
				pos++;
			}

			int result = pstmt.executeUpdate();

			// commit unless we are in a transaction (provided with a connection)
			if (callerConnection == null)
			{
				conn.commit();
			}

			// indicate success
			success = true;
		}
		catch (SQLException e)
		{
			// this is likely due to a key constraint problem...
			if ((!failQuiet) || (org.sakaiproject.service.framework.current.CurrentService.DEBUG))
			{
				LOG.warn("Sql.dbWrite(): sql: " + sql + " binds: " + debugFields(fields) + " " + e);
			}
			return false;
		}
		catch (Exception e)
		{
			LOG.warn("Sql.dbWrite(): " + e);
			return false;
		}
		finally
		{
			try
			{
				if (null != pstmt) pstmt.close();
				if ((null != conn) && (callerConnection == null))
				{
					// rollback on failure
					if (!success)
					{
						conn.rollback();
					}

					// if we changed the auto commit, reset here
					if (resetAutoCommit)
					{
						conn.setAutoCommit(autoCommit);
					}
					returnConnection(conn);
				}
			}
			catch (Exception e)
			{
				LOG.warn("Sql.dbWrite(): " + e);
			}
		}

		if (org.sakaiproject.service.framework.current.CurrentService.DEBUG)
			debug("Sql.dbWrite(): len: " + ((lastField != null) ? "" + lastField.length() : "null") + "  connection: "
					+ connectionTime + "  statement: " + (System.currentTimeMillis() - start), sql, fields);

		return true;

	} // dbWrite

	/**
	 * Read a single field BLOB from the db from one record, and update it's bytes with content.
	 * 
	 * @param sql
	 *        The sql statement to select the BLOB.
	 * @param content
	 *        The new bytes for the BLOB.
	 */
	public void dbReadBlobAndUpdate(String sql, byte[] content)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("dbReadBlobAndUpdate(String " + sql + ", byte[] " + content + ")");
		}

		if (!"oracle".equals(getVendor()))
		{
			throw new UnsupportedOperationException("BasicSqlService.dbReadBlobAndUpdate() only works with an Oracle DB");
		}

		// for DEBUG
		long start = 0;
		long connectionTime = 0;
		int lenRead = 0;

		if (LOG.isDebugEnabled())
		{
			String userId = UsageSessionService.getSessionId();
			LOG.debug("Sql.dbReadBlobAndUpdate(): " + userId + "\n" + sql);
		}

		Connection conn = null;
		Statement stmt = null;
		ResultSet result = null;
		ResultSetMetaData meta = null;
		Object blob = null;
		OutputStream os = null;

		try
		{
			if (org.sakaiproject.service.framework.current.CurrentService.DEBUG) start = System.currentTimeMillis();
			conn = borrowConnection();
			if (org.sakaiproject.service.framework.current.CurrentService.DEBUG)
				connectionTime = System.currentTimeMillis() - start;
			if (org.sakaiproject.service.framework.current.CurrentService.DEBUG) start = System.currentTimeMillis();
			stmt = conn.createStatement();
			result = stmt.executeQuery(sql);
			if (result.next())
			{
				blob = result.getBlob(1);
			}
			if (blob != null)
			{
				// %%% not supported? b.truncate(0);
				// int len = b.setBytes(0, content);
				try
				{
					// Use reflection to remove compile time dependency on oracle driver
					Class[] paramsClasses = new Class[0];
					Method getBinaryOutputStreamMethod = blob.getClass().getMethod("getBinaryOutputStream", paramsClasses);
					Object[] params = new Object[0];
					os = (OutputStream) getBinaryOutputStreamMethod.invoke(blob, params);
					os.write(content);
					os.close();
				}
				catch (NoSuchMethodException ex)
				{
					LOG.warn("Oracle driver error: " + ex);
				}
				catch (IllegalAccessException ex)
				{
					LOG.warn("Oracle driver error: " + ex);
				}
				catch (InvocationTargetException ex)
				{
					LOG.warn("Oracle driver error: " + ex);
				}
			}
		}
		catch (Exception e)
		{
			LOG.warn("Sql.dbReadBlobAndUpdate(): " + e);
		}
		finally
		{
			try
			{
				if (null != result) result.close();
				if (null != stmt) stmt.close();
				if (null != conn)
				{
					// if we commit on read
					if (m_commitAfterRead)
					{
						conn.commit();
					}

					returnConnection(conn);
				}
			}
			catch (Exception e)
			{
				LOG.warn("Sql.dbRead(): " + e);
			}
		}

		if (org.sakaiproject.service.framework.current.CurrentService.DEBUG)
			debug("sql dbReadBlobAndUpdate: len: " + lenRead + "  connection: " + connectionTime + "  statement: "
					+ (System.currentTimeMillis() - start), sql, null);

	} // dbReadBlobAndUpdate

	/**
	 * Read a single field from the db, from a single record, return the value found, and lock for update.
	 * 
	 * @param sql
	 *        The sql statement.
	 * @param field
	 *        A StringBuffer that will be filled with the field.
	 * @return The Connection holding the lock.
	 */
	public Connection dbReadLock(String sql, StringBuffer field)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("dbReadLock(String " + sql + ", StringBuffer " + field + ")");
		}

		Connection conn = null;
		Statement stmt = null;
		ResultSet result = null;
		boolean autoCommit = false;
		boolean resetAutoCommit = false;
		boolean closeConn = false;

		try
		{
			// get a new conncetion
			conn = borrowConnection();

			// adjust to turn off auto commit - we need a transaction
			autoCommit = conn.getAutoCommit();
			if (autoCommit)
			{
				conn.setAutoCommit(false);
				resetAutoCommit = true;
			}

			if (LOG.isDebugEnabled()) LOG.debug("Sql.dbReadLock():\n" + sql);

			// create a statement and execute
			stmt = conn.createStatement();
			result = stmt.executeQuery(sql);

			// if we have a result record
			if (result.next())
			{
				// get the result and pack into the return buffer
				String rv = result.getString(1);
				if ((field != null) && (rv != null)) field.append(rv);
			}

			// otherwise we fail
			else
			{
				closeConn = true;
			}
		}

		// this is likely the error when the record is otherwise locked - we fail
		catch (SQLException e)
		{
			// Note: ORA-00054 gives an e.getErrorCode() of 54, if anyone cares...
			// LOG.warn("Sql.dbUpdateLock(): " + e.getErrorCode() + " - " + e);
			closeConn = true;
		}

		catch (Exception e)
		{
			LOG.warn("Sql.dbReadLock(): " + e);
			closeConn = true;
		}

		finally
		{
			try
			{
				// close the result and statement
				if (null != result) result.close();
				if (null != stmt) stmt.close();

				// if we are failing, restore and release the connectoin
				if ((closeConn) && (conn != null))
				{
					// just in case we got a lock
					conn.rollback();
					if (resetAutoCommit) conn.setAutoCommit(autoCommit);
					returnConnection(conn);
					conn = null;
				}
			}
			catch (Exception e)
			{
				LOG.warn("Sql.dbReadLock(): " + e);
			}
		}

		return conn;

	} // dbReadLock

	/**
	 * Commit the update that was locked on this connection.
	 * 
	 * @param sql
	 *        The sql statement.
	 * @param fields
	 *        The array of fields for parameters.
	 * @param var
	 *        The value to bind to the last parameter in the sql statement.
	 * @param conn
	 *        The database connection on which the lock was gained.
	 */
	public void dbUpdateCommit(String sql, Object[] fields, String var, Connection conn)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("dbUpdateCommit(String " + sql + ", Object[] " + fields + ", String " + var + ", Connection " + conn + ")");
		}

		PreparedStatement pstmt = null;

		try
		{
			if (LOG.isDebugEnabled()) LOG.debug("Sql.dbUpdateCommit():\n" + sql);

			pstmt = conn.prepareStatement(sql);

			// put in all the fields
			int pos = prepareStatement(pstmt, fields);

			// prepare the update statement and fill with the last variable (if any)
			if (var != null)
			{
				pstmt.setCharacterStream(pos, new StringReader(var), var.length());
				pos++;
			}

			// run the SQL statement
			int result = pstmt.executeUpdate();
			pstmt.close();
			pstmt = null;

			// commit
			conn.commit();
		}
		catch (Exception e)
		{
			LOG.warn("Sql.dbUpdateCommit(): " + e);
		}
		finally
		{
			try
			{
				// close the statemenet and restore / release the connection
				if (null != pstmt) pstmt.close();
				if (null != conn)
				{
					// we don't really know what this should be, but we assume the default is not
					conn.setAutoCommit(false);
					returnConnection(conn);
				}
			}
			catch (Exception e)
			{
				LOG.warn("Sql.dbUpdateCommit(): " + e);
			}
		}

	} // dbUpdateCommit

	/**
	 * Cancel the update that was locked on this connection.
	 * 
	 * @param conn
	 *        The database connection on which the lock was gained.
	 */
	public void dbCancel(Connection conn)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("dbCancel(Connection " + conn + ")");
		}

		try
		{
			// cancel any changes, release any locks
			conn.rollback();

			// we don't really know what this should be, but we assume the default is not
			conn.setAutoCommit(false);
			returnConnection(conn);
		}
		catch (Exception e)
		{
			LOG.warn("Sql.dbCancel(): " + e);
		}

	} // dbCancel

	/**
	 * {@inheritDoc}
	 */
	public void ddl(ClassLoader loader, String resource)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("ddl(ClassLoader " + loader + ", String " + resource + ")");
		}

		// add the vender string path, and extension
		resource = m_vendor + '/' + resource + ".sql";

		// find the resource from the loader
		InputStream in = loader.getResourceAsStream(resource);
		if (in == null)
		{
			LOG.warn("Sql.ddl: missing resource: " + resource);
			return;
		}

		try
		{
			BufferedReader r = new BufferedReader(new InputStreamReader(in));
			try
			{
				// read the first line, skipping any '--' comment lines
				boolean firstLine = true;
				StringBuffer buf = new StringBuffer();
				for (String line = r.readLine(); line != null; line = r.readLine())
				{
					line = line.trim();
					if (line.startsWith("--")) continue;
					if (line.length() == 0) continue;

					// add the line to the buffer
					buf.append(' ');
					buf.append(line);

					// process if the line ends with a ';'
					boolean process = line.endsWith(";");

					if (!process) continue;

					// remove trailing ';'
					buf.setLength(buf.length() - 1);

					// run the first line as the test - if it fails, we are done
					if (firstLine)
					{
						firstLine = false;
						if (!dbWriteFailQuiet(null, buf.toString(), null))
						{
							return;
						}
					}

					// run other lines, until done - any one can fail (we will report it)
					else
					{
						dbWrite(null, buf.toString(), null);
					}

					// clear the buffer for next
					buf.setLength(0);
				}
			}
			catch (IOException any)
			{
				LOG.warn("Sql.ddl: resource: " + resource + " : " + any);
			}
			finally
			{
				try
				{
					r.close();
				}
				catch (IOException any)
				{
					LOG.warn("Sql.ddl: resource: " + resource + " : " + any);
				}
			}
		}
		finally
		{
			try
			{
				in.close();
			}
			catch (IOException any)
			{
				LOG.warn("Sql.ddl: resource: " + resource + " : " + any);
			}
		}
	}

	/**
	 * Prepare a prepared statement with fields.
	 * 
	 * @param pstmt
	 *        The prepared statement to fill in.
	 * @param fields
	 *        The Object array of values to fill in.
	 * @return the next pos that was not filled in.
	 */
	protected int prepareStatement(PreparedStatement pstmt, Object[] fields) throws SQLException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("prepareStatement(PreparedStatement " + pstmt + ", Object[] " + fields + ")");
		}

		// put in all the fields
		int pos = 1;
		if ((fields != null) && (fields.length > 0))
		{
			for (int i = 0; i < fields.length; i++)
			{
				if (fields[i] == null || (fields[i] instanceof String && ((String) fields[i]).length() == 0))
				{
					// treat a Java null as an SQL null,
					// and ALSO treat a zero-length Java string as an SQL null
					// This makes sure that Oracle vs MySQL use the same value
					// for null.
					pstmt.setObject(pos, null);
					pos++;
				}
				else if (fields[i] instanceof Time)
				{
					Time t = (Time) fields[i];
					if ("hsqldb".equals(getVendor()))
					{
						pstmt.setTimestamp(pos, new Timestamp(t.getTime()), null);
					}
					else
					{
						pstmt.setTimestamp(pos, new Timestamp(t.getTime()), m_cal);
					}
					pos++;
				}
				else if (fields[i] instanceof Long)
				{
					long l = ((Long) fields[i]).longValue();
					pstmt.setLong(pos, l);
					pos++;
				}
				// %%% support any other types specially?
				else
				{
					String value = fields[i].toString();
					pstmt.setCharacterStream(pos, new StringReader(value), value.length());
					pos++;
				}
			}
		}

		return pos;

	} // prepareStatement

	/**
	 * Append a message about this SQL statement to the DEBUG string in progress, if any
	 * 
	 * @param str
	 *        The SQL statement.
	 * @param fields
	 *        The bind fields.
	 */
	protected void debug(String str, String sql, Object[] fields)
	{
		// no error will mess us up!
		try
		{
			StringBuffer buf = (StringBuffer) CurrentService.getInThread("DEBUG");
			if (buf == null) return;

			// skip some chatter
			// /if (str.indexOf("SAKAI_CLUSTER") != -1) return;
			// if (str.indexOf("CHEF_EVENT") != -1) return;
			// if (str.indexOf("CHEF_SESSION") != -1) return;
			// if (str.indexOf("CHEF_PRESENCE") != -1) return;
			// if (str.indexOf("dual") != -1) return;

			buf.append("\n\t");
			buf.append(str);
			buf.append(" binds: ");
			buf.append(debugFields(fields));
			buf.append("\n");
			buf.append(sql);

			// LOG.info(buf.toString());
		}
		catch (Throwable ignore)
		{
			LOG.debug("Ignored Exception: " + ignore.getMessage(), ignore);
			; // ignore exceptions
		};
	}

	protected String debugFields(Object[] fields)
	{
		StringBuffer buf = new StringBuffer();
		if (fields != null)
		{
			for (int i = 0; i < fields.length; i++)
			{
				if (fields[i] != null)
				{
					buf.append(" ");
					buf.append(fields[i].toString());
				}
				else
				{
					buf.append(" null");
				}
			}
		}
		return buf.toString();
	}

	/**
	 * <p>
	 * StreamWithConnection is a cover over a stream that comes from a statmenet result in a connection, holding all these until closed.
	 * </p>
	 */
	public class StreamWithConnection extends InputStream
	{
		protected Connection m_conn = null;

		protected PreparedStatement m_pstmt = null;

		protected ResultSet m_result = null;

		protected InputStream m_stream;

		public StreamWithConnection(InputStream stream, ResultSet result, PreparedStatement pstmt, Connection conn)
		{
			if (SWC_LOG.isDebugEnabled())
			{
				SWC_LOG.debug("new StreamWithConnection(InputStream " + stream + ", ResultSet " + result + ", PreparedStatement "
						+ pstmt + ", Connection " + conn + ")");
			}

			m_conn = conn;
			m_result = result;
			m_pstmt = pstmt;
			m_stream = stream;
		}

		public void close() throws IOException
		{
			SWC_LOG.trace("close()");

			if (m_stream != null) m_stream.close();
			m_stream = null;

			try
			{
				if (null != m_result)
				{
					m_result.close();
				}
				m_result = null;
			}
			catch (SQLException any)
			{
			}

			try
			{
				if (null != m_pstmt)
				{
					m_pstmt.close();
				}
				m_pstmt = null;
			}
			catch (SQLException any)
			{
			}

			if (null != m_conn)
			{
				returnConnection(m_conn);
				m_conn = null;
			}
		}

		protected void finalize()
		{
			SWC_LOG.debug("finalize()");

			try
			{
				close();
			}
			catch (IOException any)
			{
				LOG.error(any.getMessage(), any);
			}
		}

		public int read() throws IOException
		{
			SWC_LOG.trace("read()");

			return m_stream.read();
		}

		public int read(byte b[]) throws IOException
		{
			if (SWC_LOG.isDebugEnabled())
			{
				SWC_LOG.debug("read(byte " + b + ")");
			}

			return m_stream.read(b);
		}

		public int read(byte b[], int off, int len) throws IOException
		{
			if (SWC_LOG.isDebugEnabled())
			{
				SWC_LOG.debug("read(byte " + b + ", int " + off + ", int " + len + ")");
			}

			return m_stream.read(b, off, len);
		}

		public long skip(long n) throws IOException
		{
			if (SWC_LOG.isDebugEnabled())
			{
				SWC_LOG.debug("skip(long " + n + ")");
			}

			return m_stream.skip(n);
		}

		public int available() throws IOException
		{
			SWC_LOG.trace("available()");

			return m_stream.available();
		}

		public synchronized void mark(int readlimit)
		{
			if (SWC_LOG.isDebugEnabled())
			{
				SWC_LOG.debug("mark(int " + readlimit + ")");
			}

			m_stream.mark(readlimit);
		}

		public synchronized void reset() throws IOException
		{
			SWC_LOG.trace("reset()");

			m_stream.reset();
		}

		public boolean markSupported()
		{
			SWC_LOG.trace("markSupported()");

			return m_stream.markSupported();
		}
	}

	/**
	 * @param defaultDataSource
	 *        The defaultDataSource to set.
	 */
	public void setDefaultDataSource(DataSource defaultDataSource)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("setDefaultDataSource(DataSource " + defaultDataSource + ")");
		}

		this.defaultDataSource = defaultDataSource;
	}

	/**
	 * @param slowDataSource
	 *        The slowDataSource to set.
	 */
	public void setLongDataSource(DataSource slowDataSource)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("setLongDataSource(DataSource " + slowDataSource + ")");
		}

		this.longDataSource = slowDataSource;
	}
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/framework/sql/BasicSqlService.java,v 1.8 2005/06/05 02:25:34 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
