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

import org.apache.commons.pool.KeyedObjectPool;
import org.apache.commons.pool.KeyedObjectPoolFactory;
import org.apache.commons.pool.ObjectPool;

/**
 * <p>
 * SakaiBasicDataSource extends apache common's BasicDataSource ...
 * </p>
 * <p>
 * Based on apache commons dbcp version 1.2.1, apache commons pool version 1.2
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class SakaiPoolableConnectionFactory extends PoolableConnectionFactory
{
	/** Configuration: to rollback each connection when borrowed from the pool. */
	protected boolean m_rollbackOnReturn = false;

	/**
	 * Create a new <tt>PoolableConnectionFactory</tt>.
	 * 
	 * @param connFactory
	 *        the {@link ConnectionFactory} from which to obtain base {@link Connection}s
	 * @param pool
	 *        the {@link ObjectPool} in which to pool those {@link Connection}s
	 * @param stmtPoolFactory
	 *        the {@link KeyedObjectPoolFactory} to use to create {@link KeyedObjectPool}s for pooling {@link java.sql.PreparedStatement}s, or <tt>null</tt> to disable {@link java.sql.PreparedStatement} pooling
	 * @param validationQuery
	 *        a query to use to {@link #validateObject validate} {@link Connection}s. Should return at least one row. Using <tt>null</tt> turns off validation.
	 * @param defaultReadOnly
	 *        the default "read only" setting for borrowed {@link Connection}s
	 * @param defaultAutoCommit
	 *        the default "auto commit" setting for returned {@link Connection}s
	 * @param rollbackOnReturn The rollback on borrow setting
	 */
	public SakaiPoolableConnectionFactory(ConnectionFactory connFactory, ObjectPool pool, KeyedObjectPoolFactory stmtPoolFactory,
			String validationQuery, boolean defaultReadOnly, boolean defaultAutoCommit, boolean rollbackOnReturn)
	{
		super(connFactory, pool, stmtPoolFactory, validationQuery, defaultReadOnly, defaultAutoCommit);
		m_rollbackOnReturn = rollbackOnReturn;
	}

	/**
	 * Create a new <tt>PoolableConnectionFactory</tt>.
	 * 
	 * @param connFactory
	 *        the {@link ConnectionFactory} from which to obtain base {@link Connection}s
	 * @param pool
	 *        the {@link ObjectPool} in which to pool those {@link Connection}s
	 * @param stmtPoolFactory
	 *        the {@link KeyedObjectPoolFactory} to use to create {@link KeyedObjectPool}s for pooling {@link java.sql.PreparedStatement}s, or <tt>null</tt> to disable {@link java.sql.PreparedStatement} pooling
	 * @param validationQuery
	 *        a query to use to {@link #validateObject validate} {@link Connection}s. Should return at least one row. Using <tt>null</tt> turns off validation.
	 * @param defaultReadOnly
	 *        the default "read only" setting for borrowed {@link Connection}s
	 * @param defaultAutoCommit
	 *        the default "auto commit" setting for returned {@link Connection}s
	 * @param defaultTransactionIsolation
	 *        the default "Transaction Isolation" setting for returned {@link Connection}s
	 * @param rollbackOnReturn The rollback on borrow setting
	 */
	public SakaiPoolableConnectionFactory(ConnectionFactory connFactory, ObjectPool pool, KeyedObjectPoolFactory stmtPoolFactory,
			String validationQuery, boolean defaultReadOnly, boolean defaultAutoCommit, int defaultTransactionIsolation, boolean rollbackOnReturn)
	{
		super(connFactory, pool, stmtPoolFactory, validationQuery, defaultReadOnly, defaultAutoCommit, defaultTransactionIsolation);
		m_rollbackOnReturn = rollbackOnReturn;
	}

	/**
	 * Create a new <tt>PoolableConnectionFactory</tt>.
	 * 
	 * @param connFactory
	 *        the {@link ConnectionFactory} from which to obtain base {@link Connection}s
	 * @param pool
	 *        the {@link ObjectPool} in which to pool those {@link Connection}s
	 * @param stmtPoolFactory
	 *        the {@link KeyedObjectPoolFactory} to use to create {@link KeyedObjectPool}s for pooling {@link java.sql.PreparedStatement}s, or <tt>null</tt> to disable {@link java.sql.PreparedStatement} pooling
	 * @param validationQuery
	 *        a query to use to {@link #validateObject validate} {@link Connection}s. Should return at least one row. Using <tt>null</tt> turns off validation.
	 * @param defaultReadOnly
	 *        the default "read only" setting for borrowed {@link Connection}s
	 * @param defaultAutoCommit
	 *        the default "auto commit" setting for returned {@link Connection}s
	 * @param config
	 *        the AbandonedConfig if tracing SQL objects
	 * @param rollbackOnReturn The rollback on borrow setting
	 * @deprecated AbandonedConfig is now deprecated.
	 */
	public SakaiPoolableConnectionFactory(ConnectionFactory connFactory, ObjectPool pool, KeyedObjectPoolFactory stmtPoolFactory,
			String validationQuery, boolean defaultReadOnly, boolean defaultAutoCommit, AbandonedConfig config, boolean rollbackOnReturn)
	{
		super(connFactory, pool, stmtPoolFactory, validationQuery, defaultReadOnly, defaultAutoCommit, config);
		m_rollbackOnReturn = rollbackOnReturn;
	}

	/**
	 * Create a new <tt>PoolableConnectionFactory</tt>.
	 * 
	 * @param connFactory
	 *        the {@link ConnectionFactory} from which to obtain base {@link Connection}s
	 * @param pool
	 *        the {@link ObjectPool} in which to pool those {@link Connection}s
	 * @param stmtPoolFactory
	 *        the {@link KeyedObjectPoolFactory} to use to create {@link KeyedObjectPool}s for pooling {@link java.sql.PreparedStatement}s, or <tt>null</tt> to disable {@link java.sql.PreparedStatement} pooling
	 * @param validationQuery
	 *        a query to use to {@link #validateObject validate} {@link Connection}s. Should return at least one row. Using <tt>null</tt> turns off validation.
	 * @param defaultReadOnly
	 *        the default "read only" setting for borrowed {@link Connection}s
	 * @param defaultAutoCommit
	 *        the default "auto commit" setting for returned {@link Connection}s
	 * @param defaultTransactionIsolation
	 *        the default "Transaction Isolation" setting for returned {@link Connection}s
	 * @param config
	 *        the AbandonedConfig if tracing SQL objects
	 * @param rollbackOnReturn The rollback on borrow setting
	 * @deprecated AbandonedConfig is now deprecated.
	 */
	public SakaiPoolableConnectionFactory(ConnectionFactory connFactory, ObjectPool pool, KeyedObjectPoolFactory stmtPoolFactory,
			String validationQuery, boolean defaultReadOnly, boolean defaultAutoCommit, int defaultTransactionIsolation,
			AbandonedConfig config, boolean rollbackOnReturn)
	{
		super(connFactory, pool, stmtPoolFactory, validationQuery, defaultReadOnly, defaultAutoCommit, defaultTransactionIsolation,
				config);
		m_rollbackOnReturn = rollbackOnReturn;
	}

	/**
	 * Create a new <tt>PoolableConnectionFactory</tt>.
	 * 
	 * @param connFactory
	 *        the {@link ConnectionFactory} from which to obtain base {@link Connection}s
	 * @param pool
	 *        the {@link ObjectPool} in which to pool those {@link Connection}s
	 * @param stmtPoolFactory
	 *        the {@link KeyedObjectPoolFactory} to use to create {@link KeyedObjectPool}s for pooling {@link java.sql.PreparedStatement}s, or <tt>null</tt> to disable {@link java.sql.PreparedStatement} pooling
	 * @param validationQuery
	 *        a query to use to {@link #validateObject validate} {@link Connection}s. Should return at least one row. Using <tt>null</tt> turns off validation.
	 * @param defaultReadOnly
	 *        the default "read only" setting for borrowed {@link Connection}s
	 * @param defaultAutoCommit
	 *        the default "auto commit" setting for returned {@link Connection}s
	 * @param defaultTransactionIsolation
	 *        the default "Transaction Isolation" setting for returned {@link Connection}s
	 * @param defaultCatalog
	 *        the default "catalog" setting for returned {@link Connection}s
	 * @param config
	 *        the AbandonedConfig if tracing SQL objects
	 * @param rollbackOnReturn The rollback on borrow setting
	 * @deprecated AbandonedConfig is now deprecated.
	 */
	public SakaiPoolableConnectionFactory(ConnectionFactory connFactory, ObjectPool pool, KeyedObjectPoolFactory stmtPoolFactory,
			String validationQuery, boolean defaultReadOnly, boolean defaultAutoCommit, int defaultTransactionIsolation,
			String defaultCatalog, AbandonedConfig config, boolean rollbackOnReturn)
	{
		super(connFactory, pool, stmtPoolFactory, validationQuery, defaultReadOnly, defaultAutoCommit, defaultTransactionIsolation,
				defaultCatalog, config);
		m_rollbackOnReturn = rollbackOnReturn;
	}

	/**
	 * Create a new <tt>PoolableConnectionFactory</tt>.
	 * 
	 * @param connFactory
	 *        the {@link ConnectionFactory} from which to obtain base {@link Connection}s
	 * @param pool
	 *        the {@link ObjectPool} in which to pool those {@link Connection}s
	 * @param stmtPoolFactory
	 *        the {@link KeyedObjectPoolFactory} to use to create {@link KeyedObjectPool}s for pooling {@link java.sql.PreparedStatement}s, or <tt>null</tt> to disable {@link java.sql.PreparedStatement} pooling
	 * @param validationQuery
	 *        a query to use to {@link #validateObject validate} {@link Connection}s. Should return at least one row. Using <tt>null</tt> turns off validation.
	 * @param defaultReadOnly
	 *        the default "read only" setting for borrowed {@link Connection}s
	 * @param defaultAutoCommit
	 *        the default "auto commit" setting for returned {@link Connection}s
	 * @param defaultTransactionIsolation
	 *        the default "Transaction Isolation" setting for returned {@link Connection}s
	 * @param defaultCatalog
	 *        the default "catalog" setting for returned {@link Connection}s
	 * @param config
	 *        the AbandonedConfig if tracing SQL objects
	 * @param rollbackOnReturn The rollback on borrow setting
	 */
	public SakaiPoolableConnectionFactory(ConnectionFactory connFactory, ObjectPool pool, KeyedObjectPoolFactory stmtPoolFactory,
			String validationQuery, Boolean defaultReadOnly, boolean defaultAutoCommit, int defaultTransactionIsolation,
			String defaultCatalog, AbandonedConfig config, boolean rollbackOnReturn)
	{
		super(connFactory, pool, stmtPoolFactory, validationQuery, defaultReadOnly, defaultAutoCommit, defaultTransactionIsolation,
				defaultCatalog, config);
		m_rollbackOnReturn = rollbackOnReturn;
	}

	/**
	 * Sakai modification: don't set autocommit, don't rollback if so configured!
	 */
	public void passivateObject(Object obj) throws Exception
	{
		if (obj instanceof Connection)
		{
			Connection conn = (Connection) obj;
			if (m_rollbackOnReturn)
			{
				if (!conn.getAutoCommit() && !conn.isReadOnly())
				{
					 conn.rollback();
				}
			}

			conn.clearWarnings();
			// conn.setAutoCommit(true);
		}
		if (obj instanceof DelegatingConnection)
		{
			((DelegatingConnection) obj).passivate();
		}
	}

	/**
	 * Sakai modifications: set auto-commit only if it does not match the default setting
	 */
	public void activateObject(Object obj) throws Exception
	{
		if (obj instanceof DelegatingConnection)
		{
			((DelegatingConnection) obj).activate();
		}
		if (obj instanceof Connection)
		{
			Connection conn = (Connection) obj;
			if (conn.getAutoCommit() != _defaultAutoCommit)
			{
				conn.setAutoCommit(_defaultAutoCommit);
			}
			if ((_defaultTransactionIsolation != UNKNOWN_TRANSACTIONISOLATION) && (conn.getTransactionIsolation() != _defaultTransactionIsolation))
			{
				conn.setTransactionIsolation(_defaultTransactionIsolation);
			}
			if ((_defaultReadOnly != null) && (conn.isReadOnly() != _defaultReadOnly.booleanValue()))
			{
				conn.setReadOnly(_defaultReadOnly.booleanValue());
			}
			if ((_defaultCatalog != null) && (conn.getCatalog() != _defaultCatalog))
			{
				conn.setCatalog(_defaultCatalog);
			}
		}
	}
}
