/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/framework/sql/ConnectionPool.java,v 1.4 2004/06/22 03:14:44 ggolden Exp $
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
package org.sakaiproject.service.framework.sql;

//imports
import java.sql.Connection;
import java.sql.SQLException;

/**
* <p>ConnectionPool is the interface for pool objects holding Sql connections.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.4 $
*/
public interface ConnectionPool
{
	/*******************************************************************************
	* configuration
	*******************************************************************************/

	/**
	 * Access this pool's id.
	 * Note: there is no setId() - this is established at pool construction.
	 * @return the pool id.
	 */
	String getId();

	/**
	 * Access this pool's database driver class name (like oracle.jdbc.driver.OracleDriver).
	 * @return this pool's database driver class name.
	 */
	String getDriver();

	/**
	 * Access this pool's database connect string (like jdbc:oracle:thin:@monster.ora.org:1521:tough).
	 * @return this pool's database connect string.
	 */
	String getConnect();

	/**
	 * Access this pool's database user id.
	 * @return this pool's database user id.
	 */
	String getUser();

	/**
	 * Set this pool's database connection information.
	 * @param driver The driver class name (like oracle.jdbc.driver.OracleDriver).
	 * @param connect The database connect string (like jdbc:oracle:thin:@monster.ora.org:1521:tough).
	 * @param user The database user id.
	 * @param pw The database user password.
	 * @exception SQLException if there's any trouble getting the driver ot a connection.
	 */
	void setConnect(String driver, String connect, String user, String pw) throws SQLException;

	/**
	 * Access the maximum number of connections that will be held in this pool.
	 * @return The maximum number of connections that will be held in this pool.
	 */
	int getMaxSize();

	/**
	 * Set the maximum number of connections that will be held in this pool.
	 * If not positive, there will be no limit.
	 * @param size The maximum number of connections.
	 */
	void setMaxSize(int size);

	/**
	 * Access the minimum number of connections that will be held in this pool.
	 * @return The minimum number of connections that will be held in this pool.
	 */
	int getMinSize();

	/**
	 * Set the minimum number of connections that will be held in this pool.
	 * If not positive, there will be no minimum.
	 * @param size The minimum number of connections.
	 */
	void setMinSize(int size);

	/**
	 * Access the number of connections that will added whenever we need one more connection.
	 * @return The minimum number of connections that will added whenever we need one more connection.
	 */
	int getIncrement();

	/**
	 * Set the minimum number of connections that will added whenever we need one more connection.
	 * If not 1 or less, we add one at a time.
	 * @param size The number of connections.
	 */
	void setIncrement(int size);

	/**
	 * Access the maximum time (ms) to keep any connection pooled before it's reired.
	 * @return The maximum time (ms) to keep any connection pooled before it's reired.
	 */
	long getMaxLifetime();

	/**
	 * Set the maximum time (ms) to keep any connection pooled before it's reired.
	 * If not 0 or less, connections last forever or until they become invalid.
	 * @param ms The time (ms) to keep any connection pooled before it's reired.
	 */
	void setMaxLifetime(long ms);

	/**
	 * Access the default auto-commit status for connections in the pool.
	 * @return true if connections are to be auto-commit, false if not.
	 */
	boolean getAutoCommit();

	/**
	 * Set the default auto-commit status for connections in the pool.
	 * @param auto The default auto-commit setting.
	 */
	void setAutoCommit(boolean auto);

	/**
	 * Set the maximum time (in ms) that a caller will wait for a connection to be available.
	 * If we timeout, the caller will not get a connection.
	 * If
	 * @return
	 */
	long getMaxWait();

	/**
	 * Set the maximum time (in ms) that a caller will wait for a connection to be available.
	 * If we timeout, the caller will not get a connection.
	 * It negative, callers will wait forever.  If 0, callers will not ever wait.
	 * @param wait The maximum time (in ms) that a caller will wait.
	 */
	void setMaxWait(long wait);

	/*******************************************************************************
	* use
	*******************************************************************************/

	/**
	 * Throw away all connections and start over with current pool settings for the identified pool.
	 * Any connections in use remain so, and will be discared when returned.
	 */
	void clear();

	/**
	 * Access an available or newly created Connection from this pool.
	 * Will wait a while until one is available.
	 * @return The Connection object.
	 * @throws SQLException if a connection cannot be delivered.
	 */
	Connection borrowConnection() throws SQLException;

	/**
	 * Release a database connection.
	 * @param conn The connetion to release.  If null or not one of ours, ignored.
	 * @return true if the connection is ours, false if not.
	 */
	boolean returnConnection(Connection conn);

	/*******************************************************************************
	* monitoring
	*******************************************************************************/

	/**
	 * Monitor how many connections are currently active (borrowed).
	 * @return The number of Connections from this pool currently active.
	 */
	int getNumActive();

	/**
	 * Monitor how many connections are currently in the pool not active?.
	 * @return The number of Connections from this pool allocated but not active.
	 */
	int getNumIdle();

	/**
	 * Monitor what's the average time a getConnection() client has to wait.
	 * @return The time (ms) of the average wait for a connetion from the pool (over a recent time period).
	 */
	long getAvgWait();

	/**
	 * Monitor what's the average time a connection is held for use.
	 * @return The time (ms) of the average connetion used (between the get and release)
	 * from the pool (over a recent time period).
	 */
	long getAvgActive();

	/**
	 * Set the time frame (in ms) for the monitoring methods.
	 * The averages will be computed over the last time period.
	 * @param ms The time period to use for monitoring averages.
	 */
	void setMontiorPeriod(long ms);
}

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/framework/sql/ConnectionPool.java,v 1.4 2004/06/22 03:14:44 ggolden Exp $
*
**********************************************************************************/
