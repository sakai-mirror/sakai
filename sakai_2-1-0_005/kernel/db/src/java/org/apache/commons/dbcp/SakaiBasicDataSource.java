/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
 *
 * Copyright (c) 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.apache.commons.dbcp;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.apache.commons.pool.impl.GenericKeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

/**
 * <p>
 * SakaiBasicDataSource extends apache common's BasicDataSource ...
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class SakaiBasicDataSource extends BasicDataSource
{
	/**
	 * Set the default transaction isolation level from a string value, based on the settings and values in java.sql.Connection
	 * 
	 * @param defaultTransactionIsolation
	 */
	public void setDefaultTransactionIsolationString(String defaultTransactionIsolation)
	{
		if (defaultTransactionIsolation == null)
		{
			setDefaultTransactionIsolation(PoolableConnectionFactory.UNKNOWN_TRANSACTIONISOLATION);
		}
		else if (defaultTransactionIsolation.equalsIgnoreCase("TRANSACTION_NONE"))
		{
			setDefaultTransactionIsolation(Connection.TRANSACTION_NONE);
		}
		else if (defaultTransactionIsolation.equalsIgnoreCase("TRANSACTION_READ_UNCOMMITTED"))
		{
			setDefaultTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
		}
		else if (defaultTransactionIsolation.equalsIgnoreCase("TRANSACTION_READ_COMMITTED"))
		{
			setDefaultTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		}
		else if (defaultTransactionIsolation.equalsIgnoreCase("TRANSACTION_REPEATABLE_READ"))
		{
			setDefaultTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
		}
		else if (defaultTransactionIsolation.equalsIgnoreCase("TRANSACTION_SERIALIZABLE"))
		{
			setDefaultTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		}
		else
		{
			setDefaultTransactionIsolation(PoolableConnectionFactory.UNKNOWN_TRANSACTIONISOLATION);			
		}
	}

	/**
	 * <p>
	 * Sakai changes: use the SakaiPoolableConnectionFactory, removed some not-visible (damn the use of private!) code.
	 * </p>
	 * <p>
	 * Create (if necessary) and return the internal data source we are using to manage our connections.
	 * </p>
	 * <p>
	 * <strong>IMPLEMENTATION NOTE</strong> - It is tempting to use the "double checked locking" idiom in an attempt to avoid synchronizing on every single call to this method. However, this idiom fails to work correctly in the face of some optimizations
	 * that are legal for a JVM to perform.
	 * </p>
	 * 
	 * @exception SQLException
	 *            if the object pool cannot be created.
	 */
	protected synchronized DataSource createDataSource() throws SQLException
	{

		// Return the pool if we have already created it
		if (dataSource != null)
		{
			return (dataSource);
		}

		// Load the JDBC driver class
		if (driverClassName != null)
		{
			try
			{
				Class.forName(driverClassName);
			}
			catch (Throwable t)
			{
				String message = "Cannot load JDBC driver class '" + driverClassName + "'";
				logWriter.println(message);
				t.printStackTrace(logWriter);
				throw new SQLNestedException(message, t);
			}
		}

		// Create a JDBC driver instance
		Driver driver = null;
		try
		{
			driver = DriverManager.getDriver(url);
		}
		catch (Throwable t)
		{
			String message = "Cannot create JDBC driver of class '" + (driverClassName != null ? driverClassName : "")
					+ "' for connect URL '" + url + "'";
			logWriter.println(message);
			t.printStackTrace(logWriter);
			throw new SQLNestedException(message, t);
		}

		// Can't test without a validationQuery
		if (validationQuery == null)
		{
			setTestOnBorrow(false);
			setTestOnReturn(false);
			setTestWhileIdle(false);
		}

		// Create an object pool to contain our active connections
// Sakai:
//		if ((abandonedConfig != null) && (abandonedConfig.getRemoveAbandoned() == true))
//		{
//			connectionPool = new AbandonedObjectPool(null, abandonedConfig);
//		}
//		else
		{
			connectionPool = new GenericObjectPool();
		}
		connectionPool.setMaxActive(maxActive);
		connectionPool.setMaxIdle(maxIdle);
		connectionPool.setMinIdle(minIdle);
		connectionPool.setMaxWait(maxWait);
		connectionPool.setTestOnBorrow(testOnBorrow);
		connectionPool.setTestOnReturn(testOnReturn);
		connectionPool.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		connectionPool.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
		connectionPool.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		connectionPool.setTestWhileIdle(testWhileIdle);

		// Set up statement pool, if desired
		GenericKeyedObjectPoolFactory statementPoolFactory = null;
		if (isPoolPreparedStatements())
		{
			statementPoolFactory = new GenericKeyedObjectPoolFactory(null, -1, // unlimited maxActive (per key)
					GenericKeyedObjectPool.WHEN_EXHAUSTED_FAIL, 0, // maxWait
					1, // maxIdle (per key)
					maxOpenPreparedStatements);
		}

		// Set up the driver connection factory we will use
		if (username != null)
		{
			connectionProperties.put("user", username);
		}
		else
		{
			// Sakai: log("DBCP DataSource configured without a 'username'");
		}

		if (password != null)
		{
			connectionProperties.put("password", password);
		}
		else
		{
			// Sakai: log("DBCP DataSource configured without a 'password'");
		}

		DriverConnectionFactory driverConnectionFactory = new DriverConnectionFactory(driver, url, connectionProperties);

		// Set up the poolable connection factory we will use
		PoolableConnectionFactory connectionFactory = null;
		try
		{
			connectionFactory = new SakaiPoolableConnectionFactory(driverConnectionFactory, connectionPool, statementPoolFactory,
					validationQuery, defaultReadOnly, defaultAutoCommit, defaultTransactionIsolation, defaultCatalog,
					/* abandonedConfig Sakai: */ null);
			if (connectionFactory == null)
			{
				throw new SQLException("Cannot create PoolableConnectionFactory");
			}
			// Sakai: validateConnectionFactory(connectionFactory);
		}
		catch (RuntimeException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new SQLNestedException("Cannot create PoolableConnectionFactory (" + e.getMessage() + ")", e);
		}

		// Create and return the pooling data source to manage the connections
		dataSource = new PoolingDataSource(connectionPool);
		((PoolingDataSource) dataSource).setAccessToUnderlyingConnectionAllowed(isAccessToUnderlyingConnectionAllowed());
		dataSource.setLogWriter(logWriter);

		try
		{
			for (int i = 0; i < initialSize; i++)
			{
				connectionPool.addObject();
			}
		}
		catch (Exception e)
		{
			throw new SQLNestedException("Error preloading the connection pool", e);
		}

		return dataSource;
	}

}