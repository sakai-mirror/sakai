/**********************************************************************************
*
* $Header: /cvs/sakai/framework-component/src/java/org/sakaiproject/component/framework/session/ClusterUsageSessionService.java,v 1.16 2004/11/25 02:25:51 janderse.umich.edu Exp $
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
package org.sakaiproject.component.framework.session;

// imports
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.sakaiproject.service.framework.session.UsageSession;
import org.sakaiproject.service.framework.sql.SqlReader;
import org.sakaiproject.service.framework.sql.SqlService;
import org.sakaiproject.service.legacy.time.Time;

/**
* <p>ClusterUsageSessionService is a CHEF usage session service that is backed by an external database, so can be
* used in a Cluster.</p>
* <p>A Maintenance thread watches cluster records in the database looking for ones that have stopped being updated
* indicating a failed app server, and cleans up these, their sessions and presence.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.16 $
*/
public class ClusterUsageSessionService
	extends BaseUsageSessionService
{
	/*******************************************************************************
	* Constructors, Dependencies and their setter methods
	*******************************************************************************/

	/** Dependency: SqlService. */
	protected SqlService m_sqlService = null;

	/**
	 * Dependency: SqlService.
	 * @param service The SqlService.
	 */
	public void setSqlService(SqlService service)
	{
		m_sqlService = service;
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

			m_logger.info(this + "init");
		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init: ", t);
		}
	}

	/**
	 * Final cleanup.
	 */
	public void destroy()
	{
		super.destroy();
	}

	/*******************************************************************************
	* Abstractions, etc.
	*******************************************************************************/

	/**
	* Construct storage for this service.
	*/
	protected Storage newStorage() { return new ClusterStorage(); }

	/*******************************************************************************
	* Storage
	*******************************************************************************/

	protected class ClusterStorage
		implements Storage
	{
		/**
		* Open and be ready to read / write.
		*/
		public void open()
		{
		}	// open

		/**
		* Close.
		*/
		public void close()
		{
		}	// close

		/**
		* Take this session into storage.
		* @param session The usage session.
		*/
		public void addSession(UsageSession session)
		{
			// and store it in the db
			String statement =
					"insert into CHEF_SESSION (SESSION_ID,SESSION_SERVER,SESSION_USER,SESSION_IP,SESSION_USER_AGENT,SESSION_START,SESSION_END) values (?, ?, ?, ?, ?, ?, ?)";

			// collect the fields
			Object fields[] = new Object[7];
			fields[0] = session.getId();
			fields[1] = session.getServer();
			fields[2] = session.getUserId();
			fields[3] = session.getIpAddress();
			fields[4] = session.getUserAgent();
			fields[5] = session.getStart();
			fields[6] = session.getEnd();

			// process the insert
			boolean ok = m_sqlService.dbWrite(statement, fields);
			if (!ok)
			{
				m_logger.warn(this + ".addSession(): dbWrite failed");
			}

		}	// addSession

		/**
		* Access a session by id
		* @param id The session id.
		* @return The session object.
		*/
		public UsageSession getSession(String id)
		{
			UsageSession rv = null;

			// check the db
			String statement =
				"select SESSION_ID,SESSION_SERVER,SESSION_USER,SESSION_IP,SESSION_USER_AGENT,SESSION_START,SESSION_END from CHEF_SESSION where SESSION_ID = ?";

			// send in the last seq number parameter
			Object[] fields = new Object[1];
			fields[0] = id;

			List sessions = m_sqlService.dbRead(statement, fields,
				new SqlReader()
				{
					public Object readSqlResultRecord(ResultSet result)
					{
						try
						{
							// read the UsageSession
							String id =  result.getString(1);
							String server = result.getString(2);
							String userId = result.getString(3);
							String ip = result.getString(4);
							String agent = result.getString(5);
							Time start = m_timeService.newTime(result.getTimestamp(6, m_sqlService.getCal()).getTime());
							Time end = m_timeService.newTime(result.getTimestamp(7, m_sqlService.getCal()).getTime());
							
							UsageSession session = new BaseUsageSession(id, server, userId, ip, agent, start, end);
							return session;
						}
						catch (SQLException ignore) { return null;}
					}
				} );

			if (!sessions.isEmpty()) rv = (UsageSession) sessions.get(0);

			return rv;

		}	// getSession

		/**
		 * @inheritDoc
		 */
		public List getSessions(List ids)
		{
			// TODO: do this in a single SQL call! -ggolden
			List rv = new Vector();
			for (Iterator i = ids.iterator(); i.hasNext();)
			{
				String id = (String) i.next();
				UsageSession s = getSession(id);
				if (s != null)
				{
					rv.add(s);
				}
			}
			
			return rv;
		}

		/**
		 * Access a List of usage sessions by *arbitrary criteria*.
		 * @param criteria A string with meaning known to the particular implementation of the API running.
		 * @param fields Optional values to go with the criteria in an implementation specific way.
		 * @return The List (UsageSession) of UsageSession object for these ids.
		 */
		public List getSessions(String criteria, Object[] values)
		{
			UsageSession rv = null;

			// use criteria as the where clause
			String statement =
				"select SESSION_ID,SESSION_SERVER,SESSION_USER,SESSION_IP,SESSION_USER_AGENT,SESSION_START,SESSION_END"				+ " from CHEF_SESSION where SESSION_ID IN ( "
				+ criteria
				+ " )";

			List sessions = m_sqlService.dbRead(statement, values,
				new SqlReader()
				{
					public Object readSqlResultRecord(ResultSet result)
					{
						try
						{
							// read the UsageSession
							String id =  result.getString(1);
							String server = result.getString(2);
							String userId = result.getString(3);
							String ip = result.getString(4);
							String agent = result.getString(5);
							Time start = m_timeService.newTime(result.getTimestamp(6, m_sqlService.getCal()).getTime());
							Time end = m_timeService.newTime(result.getTimestamp(7, m_sqlService.getCal()).getTime());
							
							UsageSession session = new BaseUsageSession(id, server, userId, ip, agent, start, end);
							return session;
						}
						catch (SQLException ignore) { return null;}
					}
				} );

			return sessions;
		}

		/**
		* This session is now closed.
		* @param session The session which is closed.
		*/
		public void closeSession(UsageSession session)
		{
			// close the session on the db
			String statement =
					"update CHEF_SESSION set SESSION_END = ? where SESSION_ID = ?";
	
			// collect the fields
			Object fields[] = new Object[2];
			fields[0] = session.getEnd();
			fields[1] = session.getId();

			// process the statement
			boolean ok = m_sqlService.dbWrite(statement, fields);
			if (!ok)
			{
				m_logger.warn(this + ".closeSession(): dbWrite failed");
			}

		}	// closeSession

		/**
		 * Access a list of all open sessions.
		 * @return a List (UsageSession) of all open sessions, ordered by server, then by start (asc)
		 */
		public List getOpenSessions()
		{
			UsageSession rv = null;

			// check the db
			String statement =
				"select SESSION_ID,SESSION_SERVER,SESSION_USER,SESSION_IP,SESSION_USER_AGENT,SESSION_START,SESSION_END"
				+ " from CHEF_SESSION where SESSION_START = SESSION_END ORDER BY SESSION_SERVER ASC, SESSION_START ASC";

			List sessions = m_sqlService.dbRead(statement, null,
				new SqlReader()
				{
					public Object readSqlResultRecord(ResultSet result)
					{
						try
						{
							// read the UsageSession
							String id =  result.getString(1);
							String server = result.getString(2);
							String userId = result.getString(3);
							String ip = result.getString(4);
							String agent = result.getString(5);
							Time start = m_timeService.newTime(result.getTimestamp(6, m_sqlService.getCal()).getTime());
							Time end = m_timeService.newTime(result.getTimestamp(7, m_sqlService.getCal()).getTime());
							
							UsageSession session = new BaseUsageSession(id, server, userId, ip, agent, start, end);
							return session;
						}
						catch (SQLException ignore) { return null;}
					}
				} );

			return sessions;
		}

	}   // Storage

}   // ClusterUsageSessionService

/**********************************************************************************
*
* $Header: /cvs/sakai/framework-component/src/java/org/sakaiproject/component/framework/session/ClusterUsageSessionService.java,v 1.16 2004/11/25 02:25:51 janderse.umich.edu Exp $
*
**********************************************************************************/
