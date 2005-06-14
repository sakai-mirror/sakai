/**********************************************************************************
*
* $Header: /cvs/sakai/framework-component/src/java/org/sakaiproject/component/framework/sql/BasicSqlService.java,v 1.50 2005/02/17 15:47:08 ggolden.umich.edu Exp $
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
package org.sakaiproject.component.framework.sql;

// imports
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.service.framework.config.ServerConfigurationService;
import org.sakaiproject.service.framework.current.cover.CurrentService;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.framework.sql.ConnectionPool;
import org.sakaiproject.service.framework.sql.SqlReader;
import org.sakaiproject.service.framework.sql.SqlService;
import org.sakaiproject.service.legacy.time.Time;

/**
* <p>BasicSqlService ...</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.50 $
*/
public class BasicSqlService implements SqlService
{
	/** Default pool's real id. */
	protected final static String DEFAULT_POOL = "sakai";

	/** Map of pools, keyed by pool id. */
	protected Map m_pools = null;

	/** The pool that is the default pool. */
	protected ConnectionPool m_defaultPool = null;

	/** The pool that is the "slow" pool. */
	protected ConnectionPool m_slowPool = null;

	/** The driver string for the default pool. */
	protected String m_driver = null;

	/** The connect string for the default pool. */
	protected String m_connect = null;

	/** The user id string for the default pool. */
	protected String m_user = null;

	/** The pw string for the default pool. */
	protected String m_pw = null;

	/** The number of db connection to hold in the pool. */
	protected int m_maxConnections = 50;

	/** The number of db connection to hold in the slow pool. */
	protected int m_maxSlowConnections = 25;

	/** Database vendor used; possible values are oracle, mysql, hsqldb (default). */
	protected String m_vendor = "hsqldb";

	/** Should we check the db connection with a light weight SQL query before each use to validate? */
	protected boolean m_validateConnection = false;

	/** Should we do a commit after a single statement read? */
	protected boolean m_commitAfterRead = false;

	/*******************************************************************************
	* Dependencies and their setter methods
	*******************************************************************************/

	/** Dependency: logging service */
	protected Logger m_logger = null;

	/**
	 * Dependency: logging service.
	 * @param service The logging service.
	 */
	public void setLogger(Logger service)
	{
		m_logger = service;
	}

	/** Dependency: ServerConfigurationService. */
	protected ServerConfigurationService m_serverConfigurationService = null;

	/**
	 * Dependency: ServerConfigurationService.
	 * @param service The ServerConfigurationService.
	 */
	public void setServerConfigurationService(ServerConfigurationService service)
	{
		m_serverConfigurationService = service;
	}

	/**
	 * Configuration: Set the driver string for the default pool.
	 * @param value the driver string for the default pool.
	 */
	public void setDriver(String value)
	{
		m_driver = value;
	}

	/**
	 * Configuration: Set the connect string for the default pool.
	 * @param value the connect string for the default pool.
	 */
	public void setConnect(String value)
	{
		m_connect = value;
	}

	/**
	 * Configuration: Set the user id string for the default pool.
	 * @param value the user id string for the default pool.
	 */
	public void setUser(String value)
	{
		m_user = value;
	}

	/**
	 * Configuration: Set the pw string for the default pool.
	 * @param value the user pw for the default pool.
	 */
	public void setPw(String value)
	{
		m_pw = value;
	}

	/**
	 * Configuration: Set the max number of connections for the db pool.
	 * @param value the max number of connections for the db pool.
	 */
	public void setMaxConnections(String value)
	{
		try
		{
			m_maxConnections = Integer.parseInt(value);
		}
		catch (Throwable t) {}
	}

	/**
	 * Configuration: Set the max number of connections for the slow db pool.
	 * @param value the max number of connections for the slow db pool.
	 */
	public void setMaxSlowConnections(String value)
	{
		try
		{
			m_maxSlowConnections = Integer.parseInt(value);
		}
		catch (Throwable t) {}
	}

	/**
	 * Configuration: Database vendor used; possible values are oracle, mysql, hsqldb.
	 * @param value the Database vendor used.
	 */
	public void setVendor(String value)
	{
		m_vendor = (value != null) ? value.toLowerCase() : null;
	}
	
	/**
	 * @inheritDoc
	 */
	public String getVendor()
	{
		return m_vendor;
	}

	/**
	 * Configuration: should we check the db connection with a light weight SQL query before each use to validate?
	 * @param value the validateConnection value.
	 */
	public void setValidateConnection(String value)
	{
		m_validateConnection = new Boolean(value).booleanValue();
	}

	/**
	 * Configuration: should we do a commit after each single SQL read?
	 * @param value the setting (true of false) string.
	 */
	public void setCommitAfterRead(String value)
	{
		m_commitAfterRead = new Boolean(value).booleanValue();
	}

	/*******************************************************************************
	* Init and Destroy
	*******************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		// pick up settings from config, if present
		m_driver = m_serverConfigurationService.getString("sql.driver", m_driver);
		m_connect = m_serverConfigurationService.getString("sql.connect", m_connect);
		m_user = m_serverConfigurationService.getString("sql.user", m_user);
		m_pw = m_serverConfigurationService.getString("sql.pw", m_pw);
		m_vendor = m_serverConfigurationService.getString("sql.vendor", m_vendor);
		m_validateConnection = m_serverConfigurationService.getBoolean("sql.validateConnection", m_validateConnection);

		try
		{
			m_maxConnections =
					Integer.parseInt(m_serverConfigurationService.getString("sql.maxConnections",
																	Integer.toString(m_maxConnections)));
		}
		catch (Throwable ignore){}

		try
		{
			m_maxSlowConnections =
					Integer.parseInt(m_serverConfigurationService.getString("sql.maxSlowConnections",
																	Integer.toString(m_maxSlowConnections)));
		}
		catch (Throwable ignore){}
		
		
		// test that the database connection is configured correctly
		// and output error messages if the database connection is not
		// configured correctly.
		Throwable driverException = null;
		try
		{
		    Class driverClass = Class.forName(m_driver);
		}
		catch (ClassNotFoundException e)
		{
		    driverException = e;
		    m_logger.error("Database driver class not found!");
		}

		if (m_driver.indexOf("hsqldb") >= 0 && m_vendor.indexOf("hsqldb") < 0)
		{
		    m_vendor = "hsqldb";
		    driverException = new Exception("WARNING: sql.vendor and sql.driver do not match in the Sakai configuration file!");
		    driverException.fillInStackTrace();
		}		
		
		if (m_driver.indexOf("mysql") >= 0 && m_vendor.indexOf("mysql") < 0)
		{
		    m_vendor = "mysql";
		    driverException = new Exception("WARNING: sql.vendor and sql.driver do not match in the Sakai configuration file!");
		    driverException.fillInStackTrace();
		}

		if (m_driver.indexOf("oracle") >= 0 && m_vendor.indexOf("oracle") < 0)
		{
		    m_vendor = "oracle";
		    driverException = new Exception("WARNING: sql.vendor and sql.driver do not match in the Sakai configuration file!");
		    driverException.fillInStackTrace();
		}

		try
		{
			// create a map to hold pools
			m_pools = new HashMap();

			// create a default pool
			ConnectionPool pool = newPool(DEFAULT_POOL);
			pool.setConnect(m_driver, m_connect, m_user, m_pw);
			pool.setMaxSize(m_maxConnections);
			pool.setMinSize(m_maxConnections);
			setDefaultPool(DEFAULT_POOL);

			// create the slow pool - if full, the client will wait only 1 second for a connection before failing
			m_slowPool = newPool("SLOW");
			m_slowPool.setConnect(m_driver, m_connect, m_user, m_pw);
			m_slowPool.setMaxSize(m_maxSlowConnections);
			m_slowPool.setMinSize(m_maxSlowConnections);
			m_slowPool.setMaxWait(1000);

			m_logger.info(this +".init(): " + m_driver + " " + m_connect + " " + m_user + " maxConnections: " + m_maxConnections + " slowPoolConnections: " + m_maxSlowConnections);
		}
		catch (Throwable t)
		{
		    m_logger.error("Unexpected exception when initializing database connection!");
			m_logger.warn(this +".init(): exception: " + t);
			driverException = t;
		}
		
		if (driverException != null)
		{
		    m_logger.error("There was an exception while initializeing the database connection.\n"
		    	+ "Please check the Sakai configuration files, and also check that the driver jar\n"
		    	+ "file has been copied to tomcat/common/lib.\n"
		    	+ "Current database configuration:\n"
		    	+ "sql.vendor=" + m_serverConfigurationService.getString("sql.vendor") + "\n"
		    	+ "sql.driver=" + m_serverConfigurationService.getString("sql.driver") + "\n"
		    	+ "sql.connect=" + m_serverConfigurationService.getString("sql.connect") + "\n"
		    	+ "sql.user=" + m_serverConfigurationService.getString("sql.user") + "\n"
		    	+ "sql.pw=(hidden)\n"
		    );
		    driverException.printStackTrace();
		}
	}

	/**
	 * Final cleanup.
	 */
	public void destroy()
	{
		m_logger.info(this +".destroy()");
	}

	/*******************************************************************************
	* Work interface methods: org.sakaiproject.sql.SqlService
	*******************************************************************************/

	/**
	 * {@inheritDoc}
	 */
	public Connection borrowConnection() throws SQLException
	{
		if (m_defaultPool != null)
		{
			return m_defaultPool.borrowConnection();
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
		if (m_defaultPool != null)
		{
			m_defaultPool.returnConnection(conn);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public ConnectionPool newPool(String poolId) throws IdUsedException
	{
		try
		{
			getPool(poolId);
			throw new IdUsedException(poolId);
		}
		catch (IdUnusedException e)
		{
		}

		// create a new pool object
		MyConnectionPool pool = new MyConnectionPool(poolId);

		// save it by id
		synchronized (m_pools)
		{
			m_pools.put(pool.getId(), pool);
		}

		return pool;
	}

	/**
	 * {@inheritDoc}
	 */
	public ConnectionPool getPool(String poolId) throws IdUnusedException
	{
		// handle default
		if (DEFAULT.equals(poolId))
		{
			if (m_defaultPool != null)
			{
				return m_defaultPool;
			}
			else
			{
				throw new IdUnusedException(poolId);
			}
		}

		// handle non-default names
		ConnectionPool rv = null;
		synchronized (m_pools)
		{
			rv = (ConnectionPool) m_pools.get(poolId);
		}

		if (rv == null)
		{
			throw new IdUnusedException(poolId);
		}

		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public List getPools()
	{
		List rv = new Vector();

		synchronized (m_pools)
		{
			for (Iterator iEntries = m_pools.entrySet().iterator(); iEntries.hasNext();)
			{
				Map.Entry entry = (Map.Entry) iEntries.next();
				rv.add(entry.getValue());
			}
		}

		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public void deletePool(String poolId) throws IdUnusedException
	{
		// get to throw the exception if missing
		ConnectionPool pool = getPool(poolId);

		// clear the pool
		pool.clear();

		// forget about it
		synchronized (m_pools)
		{
			m_pools.remove(poolId);
		}

		// if this was the default pool, unset default		
		if (m_defaultPool != null)
		{
			if (m_defaultPool.getId().equals(poolId))
			{
				m_defaultPool = null;
			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public void setDefaultPool(String poolId) throws IdUnusedException
	{
		// get to throw the exception if missing
		ConnectionPool pool = getPool(poolId);

		m_defaultPool = pool;
	}

	/** Name of the database pool to use for requests. */
	protected final String POOL_NAME = "default";

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
	* @param sql The sql statement.
	* @return The List of Strings of single fields of the record found, or empty if none found.
	*/
	public List dbRead(String sql)
	{
		return dbRead(sql, null, null);

	}	// dbRead

	/**
	* Process a query, filling in with fields, and return the results as a List, one per record read.
	* If a reader is provided, it will be called for each record to prepare the Object placed into the List.
	* Otherwise, the first field of each record, as a String, will be placed in the list.
	* @param sql The sql statement.
	* @param fields The array of fields for parameters.
	* @param reader The reader object to read each record.
	* @return The List of things read, one per record.
	*/
	public List dbRead(String sql, Object[] fields, SqlReader reader)
	{
		return dbRead(null, sql, fields, reader);
	}

	/**
	* Process a query, filling in with fields, and return the results as a List, one per record read.
	* If a reader is provided, it will be called for each record to prepare the Object placed into the List.
	* Otherwise, the first field of each record, as a String, will be placed in the list.
	* @param callerConn The db connection object to use (if not null).
	* @param sql The sql statement.
	* @param fields The array of fields for parameters.
	* @param reader The reader object to read each record.
	* @return The List of things read, one per record.
	*/
	public List dbRead(Connection callerConn, String sql, Object[] fields, SqlReader reader)
	{
		// for DEBUG
		long start = 0;
		long connectionTime = 0;
		int lenRead = 0;
		long stmtTime = 0;
		long resultsTime = 0;
		int count = 0;

		if (m_logger.isDebugEnabled())
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
			m_logger.debug("Sql.dbRead: " + userId + "\n" + sql + "\n" + buf);
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
			if (org.sakaiproject.service.framework.current.CurrentService.DEBUG) connectionTime = System.currentTimeMillis() - start;
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
					m_logger.warn("Sql.dbRead: unable to read a result from sql: " + sql + debugFields(fields) + " row: " + result.getRow());
				}
			}
		}
		catch (Exception e)
		{
			m_logger.warn("Sql.dbRead: sql: " + sql + debugFields(fields), e);
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
				m_logger.warn("Sql.dbRead: sql: " + sql + debugFields(fields), e);
			}
		}

		if (org.sakaiproject.service.framework.current.CurrentService.DEBUG)
			debug("Sql.dbRead: connection: " + connectionTime
					+ " read: " + count
					+ " sql: " + stmtTime
					+ " process: " + resultsTime
					,sql, fields);

		return rv;

	}	// dbRead

	/**
	* Read a single field from the db, from multiple record - concatenating the binary values into value.
	* @param sql The sql statement.
	* @param fields The array of fields for parameters.
	* @param value The array of bytes to fill with the value read from the db.
	*/
	public void dbReadBinary(String sql, Object[] fields, byte[] value)
	{
		dbReadBinary(null, sql, fields, value);
	}

	/**
	* Read a single field from the db, from multiple record - concatenating the binary values into value.
	* @param callerConn The optional db connection object to use.
	* @param sql The sql statement.
	* @param fields The array of fields for parameters.
	* @param value The array of bytes to fill with the value read from the db.
	*/
	public void dbReadBinary(Connection callerConn, String sql, Object[] fields, byte[] value)
	{
		// for DEBUG
		long start = 0;
		long connectionTime = 0;
		int lenRead = 0;

		if (m_logger.isDebugEnabled())
		{
			String userId = UsageSessionService.getSessionId();
			m_logger.debug("Sql.dbReadBinary(): " + userId + "\n" + sql);
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
			if (org.sakaiproject.service.framework.current.CurrentService.DEBUG) connectionTime = System.currentTimeMillis() - start;
			if (org.sakaiproject.service.framework.current.CurrentService.DEBUG) start = System.currentTimeMillis();

			pstmt = conn.prepareStatement(sql);

			// put in all the fields
			prepareStatement(pstmt, fields);

			result = pstmt.executeQuery();

			int index = 0;
			while (result.next() && (index < value.length))
			{
				InputStream stream = result.getBinaryStream(1);
				int len = stream.read(value, index, value.length-index);
				stream.close();
				index += len;
				if (org.sakaiproject.service.framework.current.CurrentService.DEBUG) lenRead += len;
			}
		}
		catch (Exception e)
		{
			m_logger.warn("Sql.dbReadBinary(): " + e);
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
				m_logger.warn("Sql.dbReadBinary(): " + e);
			}
		}

		if (org.sakaiproject.service.framework.current.CurrentService.DEBUG)
			debug("sql read binary: len: "  + lenRead + "  connection: " + connectionTime + "  statement: " + (System.currentTimeMillis()-start), sql, fields);

	}	// dbReadBinary 

	/**
	 * Read a single field / record from the db, returning a stream on the result record / field.
	 * The stream holds the conection open - so it must be closed or finalized quickly!
	 * @param sql The sql statement.
	 * @param fields The array of fields for parameters.
	 * @param big If true, the read is expected to be potentially large.
	 * @throws ServerOverloadException if the read cannot complete due to lack of a free connection (if wait is false)
	 */
	public InputStream dbReadBinary(String sql, Object[] fields, boolean big) throws ServerOverloadException
	{
		InputStream rv = null;

		// for DEBUG
		long start = 0;
		long connectionTime = 0;
		int lenRead = 0;

		if (m_logger.isDebugEnabled())
		{
			String userId = UsageSessionService.getSessionId();
			m_logger.debug("Sql.dbReadBinary(): " + userId + "\n" + sql);
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
				conn = m_slowPool.borrowConnection();
				if (conn == null)
				{
					throw new ServerOverloadException(null);
				}
			}
			if (org.sakaiproject.service.framework.current.CurrentService.DEBUG) connectionTime = System.currentTimeMillis() - start;
			if (org.sakaiproject.service.framework.current.CurrentService.DEBUG) start = System.currentTimeMillis();

			pstmt = conn.prepareStatement(sql);

			// put in all the fields
			prepareStatement(pstmt, fields);

			result = pstmt.executeQuery();

			if (result.next())
			{
				InputStream stream = result.getBinaryStream(1);
				rv = new StreamWithConnection(stream, result, pstmt, conn, (big ? m_slowPool : m_defaultPool));
			}
		}
		catch (ServerOverloadException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			m_logger.warn("Sql.dbReadBinary(): " + e);
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
							m_slowPool.returnConnection(conn);
						}
						else
						{
							m_defaultPool.returnConnection(conn);
						}
					}
				}
				catch (Exception e)
				{
					m_logger.warn("Sql.dbReadBinary(): " + e);
				}
			}
		}

		if (org.sakaiproject.service.framework.current.CurrentService.DEBUG)
			debug("sql read binary: len: "  + lenRead + "  connection: " + connectionTime + "  statement: " + (System.currentTimeMillis()-start), sql, fields);

		return rv;

	}	// dbReadBinary 

	/**
	* Execute the "write" sql - no response.
	* @param sql The sql statement.
	* @return true if successful, false if not.
	*/
	public boolean dbWrite(String sql)
	{
		return dbWrite(sql, null, null, null, false);

	}	// dbWrite

	/**
	* Execute the "write" sql - no response.
	* a long field is set to "?" - fill it in with var
	* @param sql The sql statement.
	* @param var The value to bind to the first parameter in the sql statement.
	* @return true if successful, false if not.
	*/
	public boolean dbWrite(String sql, String var)
	{
		return dbWrite(sql, null, var, null, false);

	}	// dbWrite

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
	public boolean dbWriteBinary(String sql, Object[] fields, byte[] var, int offset, int len)
	{
		// for DEBUG
		long start = 0;
		long connectionTime = 0;

		if (m_logger.isDebugEnabled())
		{
			String userId = UsageSessionService.getSessionId();
			m_logger.debug("Sql.dbWriteBinary(): " + userId + "\n" + sql + "  size:" + var.length);
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
			if (org.sakaiproject.service.framework.current.CurrentService.DEBUG) connectionTime = System.currentTimeMillis()-start;
			
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
			m_logger.warn("Sql.dbWriteBinary(): " + e);
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
				m_logger.warn("Sql.dbWriteBinary(): " + e);
			}
		}
		
		if (org.sakaiproject.service.framework.current.CurrentService.DEBUG)
			debug("sql write binary: len: "  + len + "  connection: " + connectionTime + "  statement: " + (System.currentTimeMillis()-start), sql, fields);

		return true;

	}	// dbWriteBinary

	/**
	* Execute the "write" sql - no response, using a set of fields from an array plus one more as params.
	* @param sql The sql statement.
	* @param fields The array of fields for parameters.
	* @return true if successful, false if not.
	*/
	public boolean dbWrite(String sql, Object[] fields)
	{
		return dbWrite(sql, fields, null, null, false);

	}	// dbWrite

	/**
	 * Execute the "write" sql - no response, using a set of fields from an array and a given connection.
	 * @param connection The connection to use.
	 * @param sql The sql statement.
	 * @param fields The array of fields for parameters.
	 * @return true if successful, false if not.
	 */
	public boolean dbWrite(Connection connection, String sql, Object[] fields)
	{
		return dbWrite(sql, fields, null, connection, false);
	}

	/**
	 * Execute the "write" sql - no response, using a set of fields from an array and a given connection
	 * logging no errors on failure.
	 * @param connection The connection to use.
	 * @param sql The sql statement.
	 * @param fields The array of fields for parameters.
	 * @return true if successful, false if not.
	 */
	public boolean dbWriteFailQuiet(Connection connection, String sql, Object[] fields)
	{
		return dbWrite(sql, fields, null, connection, true);
	}

	/**
	* Execute the "write" sql - no response, using a set of fields from an array plus one more as params.
	* @param sql The sql statement.
	* @param fields The array of fields for parameters.
	* @param lastField The value to bind to the last parameter in the sql statement.
	* @return true if successful, false if not.
	*/
	public boolean dbWrite(String sql, Object[] fields, String lastField)
	{
		return dbWrite(sql, fields, lastField, null, false);
	}

	/**
	* Execute the "write" sql - no response, using a set of fields from an array plus one more as params and connection.
	* @param sql The sql statement.
	* @param fields The array of fields for parameters.
	* @param lastField The value to bind to the last parameter in the sql statement.
	* @param callerConnection The connection to use.
	* @param failQuiet If true, don't log errors from statement failure
	* @return true if successful, false if not.
	*/
	protected boolean dbWrite(String sql, Object[] fields, String lastField, Connection callerConnection, boolean failQuiet)
	{
		// for DEBUG
		long start = 0;
		long connectionTime = 0;

		if (m_logger.isDebugEnabled())
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
			m_logger.debug("Sql.dbWrite(): " + userId + "\n" + sql + "\n" + buf);
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
				if (org.sakaiproject.service.framework.current.CurrentService.DEBUG) connectionTime = System.currentTimeMillis() - start;

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
				m_logger.warn("Sql.dbWrite(): sql: " + sql + " binds: " + debugFields(fields) + " " + e);
			}
			return false;
		}
		catch (Exception e)
		{
			m_logger.warn("Sql.dbWrite(): " + e);
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
				m_logger.warn("Sql.dbWrite(): " + e);
			}
		}

		if (org.sakaiproject.service.framework.current.CurrentService.DEBUG)
			debug("Sql.dbWrite(): len: "  + ((lastField != null) ? ""+lastField.length() : "null") + "  connection: " + connectionTime + "  statement: " + (System.currentTimeMillis()-start), sql, fields);

		return true;

	}	// dbWrite

	/**
	* Read a single field BLOB from the db from one record, and update it's bytes
	* with content.
	* @param sql The sql statement to select the BLOB.
	* @param content The new bytes for the BLOB.
	*/
	public void dbReadBlobAndUpdate(String sql, byte[] content)
	{
		if (!"oracle".equals(getVendor()))
		{
			throw new UnsupportedOperationException("BasicSqlService.dbReadBlobAndUpdate() only works with an Oracle DB");
		}
		
		// for DEBUG
		long start = 0;
		long connectionTime = 0;
		int lenRead = 0;

		if (m_logger.isDebugEnabled())
		{
			String userId = UsageSessionService.getSessionId();
			m_logger.debug("Sql.dbReadBlobAndUpdate(): " + userId + "\n" + sql);
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
			if (org.sakaiproject.service.framework.current.CurrentService.DEBUG) connectionTime = System.currentTimeMillis() - start;
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
					Method getBinaryOutputStreamMethod = blob.getClass().getMethod
								("getBinaryOutputStream", paramsClasses);
					Object[] params = new Object[0];
					os = (OutputStream) getBinaryOutputStreamMethod.invoke(blob, params);
					os.write(content);
					os.close();
				}
				catch (NoSuchMethodException ex)
				{
					m_logger.warn("Oracle driver error: " + ex);
				}
				catch (IllegalAccessException ex)
				{
					m_logger.warn("Oracle driver error: " + ex);
				}
				catch (InvocationTargetException ex)
				{
					m_logger.warn("Oracle driver error: " + ex);
				}
			}
		}
		catch (Exception e)
		{
			m_logger.warn("Sql.dbReadBlobAndUpdate(): " + e);
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
				m_logger.warn("Sql.dbRead(): " + e);
			}
		}

		if (org.sakaiproject.service.framework.current.CurrentService.DEBUG)
			debug("sql dbReadBlobAndUpdate: len: "  + lenRead + "  connection: "
						+ connectionTime + "  statement: " + (System.currentTimeMillis()-start), sql, null);

	}	// dbReadBlobAndUpdate

	/**
	* Read a single field from the db, from a single record, return the value found, and lock for update. 
	* @param sql The sql statement.
	* @param field A StringBuffer that will be filled with the field.
	* @return The Connection holding the lock.
	*/
	public Connection dbReadLock(String sql, StringBuffer field)
	{
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

			if (m_logger.isDebugEnabled())
				m_logger.debug("Sql.dbReadLock():\n" + sql);

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
			//m_logger.warn("Sql.dbUpdateLock(): " + e.getErrorCode() + " - " + e);
			closeConn = true;
		}

		catch (Exception e)
		{
			m_logger.warn("Sql.dbReadLock(): " + e);
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
				m_logger.warn("Sql.dbReadLock(): " + e);
			}
		}

		return conn;

	}	// dbReadLock

	/**
	* Commit the update that was locked on this connection.
	* @param sql The sql statement.
	* @param fields The array of fields for parameters.
	* @param var The value to bind to the last parameter in the sql statement.
	* @param conn The database connection on which the lock was gained.
	*/
	public void dbUpdateCommit(String sql, Object[] fields, String var, Connection conn)
	{
		PreparedStatement pstmt = null;

		try
		{
			if (m_logger.isDebugEnabled())
				m_logger.debug("Sql.dbUpdateCommit():\n" + sql);

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
			m_logger.warn("Sql.dbUpdateCommit(): " + e);
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
				m_logger.warn("Sql.dbUpdateCommit(): " + e);
			}
		}

	}	// dbUpdateCommit

	/**
	* Cancel the update that was locked on this connection.
	* @param conn The database connection on which the lock was gained.
	*/
	public void dbCancel(Connection conn)
	{
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
			m_logger.warn("Sql.dbCancel(): " + e);
		}

	}	// dbCancel

	/**
	* Prepare a prepared statement with fields.
	* @param pstmt The prepared statement to fill in.
	* @param fields The Object array of values to fill in.
	* @return the next pos that was not filled in.
	*/
	protected int prepareStatement(PreparedStatement pstmt, Object[] fields)
		throws SQLException
	{
		// put in all the fields
		int pos = 1;
		if ((fields != null) && (fields.length > 0))
		{
			for (int i = 0; i < fields.length; i++)
			{	
				if (fields[i] == null || (fields[i] instanceof String && ((String)fields[i]).length() == 0))
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
					pstmt.setTimestamp(pos, new Timestamp(t.getTime()), m_cal);
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

	}	// prepareStatement

	/**
	 * Append a message about this SQL statement to the DEBUG string in progress, if any
	 * @param str The SQL statement.
	 * @param fields The bind fields.
	 */
	protected void debug(String str, String sql, Object[] fields)
	{
		// no error will mess us up!
		try
		{
			StringBuffer buf = (StringBuffer) CurrentService.getInThread("DEBUG");
			if (buf == null) return;

			// skip some chatter
			///if (str.indexOf("SAKAI_CLUSTER") != -1) return;
			//if (str.indexOf("CHEF_EVENT") != -1) return;
			//if (str.indexOf("CHEF_SESSION") != -1) return;
			//if (str.indexOf("CHEF_PRESENCE") != -1) return;
			//if (str.indexOf("dual") != -1) return;

			buf.append("\n\t");
			buf.append(str);
			buf.append(" binds: ");
			buf.append(debugFields(fields));
			buf.append("\n");
			buf.append(sql);

//			m_logger.info(buf.toString());
		}
		catch (Throwable ignore) {};
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

	/*******************************************************************************
	* ConnectionPool
	*******************************************************************************/

	protected class MyConnectionPool implements ConnectionPool
	{
		/** The pool id. */
		protected String m_id = null;

		/** The database driver class name. */
		protected String m_driver = null;

		/** The database connect string. */
		protected String m_connect = null;

		/** The database user id. */
		protected String m_user = null;

		/** The database user password. */
		protected String m_pw = null;

		/** The default auto-commit to off for newly borrowed connections. */
		protected boolean m_autoCommit = false;

		/** The pool. */
		protected GenericObjectPool m_pool = null;

		/**
		 * Construct
		 * @param id The pool id.
		 */
		public MyConnectionPool(String id)
		{
			m_id = id;

			// create the generic object pool
			m_pool = new GenericObjectPool(new MyPoolableObjectFactory());
			m_pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
			m_pool.setTestOnBorrow(true);
			m_pool.setTestOnReturn(false);
			m_pool.setTestWhileIdle(false);
		}

		/**
		 * {@inheritDoc}
		 */
		public String getId()
		{
			return m_id;
		}

		/**
		 * {@inheritDoc}
		 */
		public String getDriver()
		{
			return m_driver;
		}

		/**
		 * {@inheritDoc}
		 */
		public String getConnect()
		{
			return m_connect;
		}

		/**
		 * {@inheritDoc}
		 */
		public String getUser()
		{
			return m_user;
		}

		/**
		 * {@inheritDoc}
		 */
		public void setConnect(String driver, String connect, String user, String pw) throws SQLException
		{
			// load the driver
			m_driver = driver;
			try
			{
				Class.forName(m_driver).newInstance();
			}
			catch (InstantiationException e)
			{
				throw new SQLException("InstantiationException for driver: " + driver);
			}
			catch (IllegalAccessException e)
			{
				throw new SQLException("IllegalAccessException for driver: " + driver);
			}
			catch (ClassNotFoundException e)
			{
				throw new SQLException("ClassNotFoundException for driver: " + driver);
			}

			m_connect = connect;
			m_user = user;
			m_pw = pw;

			// TODO: now that we are re-set, deal with pooled connections
			m_pool.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		public int getMaxSize()
		{
			return m_pool.getMaxActive();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setMaxSize(int size)
		{
			m_pool.setMaxActive(size);
		}

		/**
		 * {@inheritDoc}
		 */
		public int getMinSize()
		{
			return m_pool.getMaxIdle();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setMinSize(int size)
		{
			m_pool.setMaxIdle(size);
		}

		/**
		 * {@inheritDoc}
		 */
		public int getIncrement()
		{
			return 0;
		}

		/**
		 * {@inheritDoc}
		 */
		public void setIncrement(int size)
		{
		}

		/**
		 * {@inheritDoc}
		 */
		public long getMaxLifetime()
		{
			return 0;
		}

		/**
		 * {@inheritDoc}
		 */
		public void setMaxLifetime(long ms)
		{
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean getAutoCommit()
		{
			return m_autoCommit;
		}

		/**
		 * {@inheritDoc}
		 */
		public void setAutoCommit(boolean auto)
		{
			m_autoCommit = auto;
		}

		/**
		 * {@inheritDoc}
		 */
		public long getMaxWait()
		{
			return m_pool.getMaxWait();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setMaxWait(long wait)
		{
			m_pool.setMaxWait(wait);
		}

		/**
		 * {@inheritDoc}
		 */
		public void clear()
		{
			m_pool.clear();
		}

		/**
		 * {@inheritDoc}
		 */
		public Connection borrowConnection() throws SQLException
		{
			try
			{
//				if (m_pool.getMaxActive() == m_pool.getNumActive())
//				{
//					m_logger.info("SQL: pool full- waiting");
//				}

				return (Connection) m_pool.borrowObject();
			}
			catch (Exception e)
			{
				throw new SQLException("unable to borrow: " + e.toString());
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean returnConnection(Connection conn)
		{
			try
			{
				m_pool.returnObject(conn);
			}
			catch (Exception e)
			{
			}
			return true; // TODO
		}

		/**
		 * {@inheritDoc}
		 */
		public int getNumActive()
		{
			return m_pool.getNumActive();
		}

		/**
		 * {@inheritDoc}
		 */
		public int getNumIdle()
		{
			// TODO Auto-generated method stub
			return m_pool.getNumIdle();
		}

		/**
		 * {@inheritDoc}
		 */
		public long getAvgWait()
		{
			// TODO Auto-generated method stub
			return 0;
		}

		/**
		 * {@inheritDoc}
		 */
		public long getAvgActive()
		{
			// TODO Auto-generated method stub
			return 0;
		}

		/**
		 * {@inheritDoc}
		 */
		public void setMontiorPeriod(long ms)
		{
			// TODO Auto-generated method stub
		}

		/*******************************************************************************
		* PoolableObjectFactory
		*******************************************************************************/

		/**
		 * Factory for the actual connections.
		 */
		private class MyPoolableObjectFactory implements PoolableObjectFactory
		{
			/**
			 * {@inheritDoc}
			 */
			public Object makeObject() throws Exception
			{
				Connection conn = DriverManager.getConnection(m_connect, m_user, m_pw);

				// set the connection session to use GMT for dates
				long time = 0;
				if (org.sakaiproject.service.framework.current.CurrentService.DEBUG) time = System.currentTimeMillis();

				// form vendor specific queries to set the time zone
				String sql = null;
				if ("oracle".equals(m_vendor))
				{
					sql = "alter session set TIME_ZONE='+00:00'";
				}
				else if ("mysql".equals(m_vendor))
				{
					// set the connection time zone to GMT
					sql = "SET @time_zone = \"+0:00\"";
				}
				else //if ("hsqldb".equals(m_vendor))
				{
				    // HSQLDB doesn't allow setting the time zone
					return conn;
				}

				// fire off the query, and if it fails, we will invalidate the connection
				PreparedStatement pstmt = null;
				ResultSet result = null;
				try
				{
					pstmt = conn.prepareStatement(sql);
					result = pstmt.executeQuery();
				}
				catch (Throwable any)
				{
					m_logger.warn("Sql.makeObject: " + sql + " : " + any);
				}
				finally
				{
					try
					{
						if (null != result) result.close();
						if (null != pstmt) pstmt.close();
					}
					catch (Throwable any)
					{
						m_logger.warn("Sql.makeObject: " + sql + " : " + any);
					}
				}

				if (org.sakaiproject.service.framework.current.CurrentService.DEBUG)
					debug("Sql.makeObject:  time: " + (System.currentTimeMillis()-time), sql, null);

				return conn;
			}

			/**
			 * {@inheritDoc}
			 */
			public void destroyObject(Object obj) throws Exception
			{
			}

			/**
			 * {@inheritDoc}
			 */
			public boolean validateObject(Object obj)
			{
				boolean rv = true;
				
				if (m_validateConnection)
				{
					long time = 0;
					if (org.sakaiproject.service.framework.current.CurrentService.DEBUG) time = System.currentTimeMillis();

					// form vendor specific query to set the time zone
					String sql = null;
					if ("oracle".equals(m_vendor))
					{
						sql = "select current_timestamp from dual";
					}
					else if ("mysql".equals(m_vendor))
					{
						sql = "select current_timestamp()";
					}
					else //if ("hsqldb".equals(m_vendor))
					{
						// HSQLDB doesn't allow setting the time zone
						return rv;
					}

					// fire off the query, and if it fails, we will invalidate the connection
					PreparedStatement pstmt = null;
					ResultSet result = null;
					try
					{
						pstmt = ((Connection) obj).prepareStatement(sql);
						result = pstmt.executeQuery();
					}
					catch (Throwable any)
					{
						rv = false;
					}
					finally
					{
						try
						{
							if (null != result) result.close();
							if (null != pstmt) pstmt.close();
						}
						catch (Throwable any)
						{
							rv = false;
						}
					}

					if (org.sakaiproject.service.framework.current.CurrentService.DEBUG)
						debug("Sql.validateObject:  time: " + (System.currentTimeMillis()-time) + " : ", sql, null);
				}

				return rv;
			}

			/**
			 * {@inheritDoc}
			 */
			public void activateObject(Object obj) throws Exception
			{
				// set the default auto-commit mode if it needs setting
				boolean autoCommit = ((Connection) obj).getAutoCommit();
				if (autoCommit != m_autoCommit)
				{
					((Connection) obj).setAutoCommit(m_autoCommit);
				}

			} // activateObject

			/**
			 * {@inheritDoc}
			 */
			public void passivateObject(Object obj) throws Exception
			{
			}
		}
	}

	/**
	 * <p>StreamWithConnection is a cover over a stream that comes from a statmenet result in a connection,
	 * holding all these until closed.</p>
	 */
	public class StreamWithConnection
		extends InputStream
	{
		protected Connection m_conn = null;
		protected PreparedStatement m_pstmt = null;
		protected ResultSet m_result = null;
		protected InputStream m_stream;
		protected ConnectionPool m_pool = null;

		public StreamWithConnection(InputStream stream, ResultSet result, PreparedStatement pstmt, Connection conn, ConnectionPool pool)
		{
			m_conn = conn;
			m_result = result;
			m_pstmt = pstmt;
			m_stream = stream;
			m_pool = pool;
		}

		public void close() throws IOException
		{
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
				m_pool.returnConnection(m_conn);
				m_conn = null;
			}

			m_pool = null;
		}

		protected void finalize()
		{
			try
			{
				close();
			}
			catch (IOException any)
			{
			}
		}

		public int read() throws IOException { return m_stream.read(); }
		public int read(byte b[]) throws IOException { return m_stream.read(b); }
		public int read(byte b[], int off, int len) throws IOException { return m_stream.read(b, off, len); }
		public long skip(long n) throws IOException { return m_stream.skip(n); }
		public int available() throws IOException  { return m_stream.available(); }
		public synchronized void mark(int readlimit) { m_stream.mark(readlimit); }
		public synchronized void reset() throws IOException { m_stream.reset(); }
		public boolean markSupported()  { return m_stream.markSupported(); }
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/framework-component/src/java/org/sakaiproject/component/framework/sql/BasicSqlService.java,v 1.50 2005/02/17 15:47:08 ggolden.umich.edu Exp $
*
**********************************************************************************/
