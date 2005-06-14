/**********************************************************************************
*
* $Header: /cvs/sakai/framework-component/src/java/org/sakaiproject/component/framework/cluster/SakaiClusterService.java,v 1.9 2005/01/18 22:21:17 janderse.umich.edu Exp $
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
package org.sakaiproject.component.framework.cluster;

// imports
import java.util.Iterator;
import java.util.List;

import org.sakaiproject.service.framework.cluster.ClusterService;
import org.sakaiproject.service.framework.config.ServerConfigurationService;
import org.sakaiproject.service.framework.current.cover.CurrentService;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.session.UsageSession;
import org.sakaiproject.service.framework.session.UsageSessionService;
import org.sakaiproject.service.framework.sql.SqlService;
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.event.EventTrackingService;
import org.sakaiproject.service.legacy.presence.cover.PresenceService;

/**
* <p>BaseUsageSessionService is a CHEF usage session service implemented as a Turbine Service.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.9 $
*/
public class SakaiClusterService implements ClusterService
{
	/** The maintenance. */
	protected Maintenance m_maintenance = null;

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

	/** Dependency: EventTrackingService. */
	protected EventTrackingService m_eventTrackingService = null;

	/**
	 * Dependency: EventTrackingService.
	 * @param service The EventTrackingService.
	 */
	public void setEventTrackingService(EventTrackingService service)
	{
		m_eventTrackingService = service;
	}

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

	/** Dependency: UsageSessionService. */
	protected UsageSessionService m_usageSessionService = null;

	/**
	 * Dependency: UsageSessionService.
	 * @param service The UsageSessionService.
	 */
	public void setUsageSessionService(UsageSessionService service)
	{
		m_usageSessionService = service;
	}

	/** Configuration: how often to register that we are alive with the cluster table (seconds). */
	protected long m_refresh = 60;

	/**
	 * Configuration: set the refresh value
	 * @param value The refresh value.
	 */
	public void setRefresh(String value)
	{
		try
		{
			m_refresh = Long.parseLong(value);
		}
		catch (Exception ignore) {}
	}

	/** Configuration: how long we give an app server to respond before it is considered lost (seconds). */
	protected long m_expired = 600;

	/**
	 * Configuration: set the expired value
	 * @param value The expired value.
	 */
	public void setExpired(String value)
	{
		try
		{
			m_expired = Long.parseLong(value);
		}
		catch (Exception ignore) {}
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
			// start the maintenance thread
			m_maintenance = new Maintenance();
			m_maintenance.start();

			m_logger.info(this + "init: refresh: " + m_refresh + " expired: " + m_expired);
		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}
	}

	/**
	* Returns to uninitialized state.
	*/
	public void destroy()
	{
		m_maintenance.stop();
		m_maintenance = null;

		m_logger.info(this +".destroy()");
	}

	/*******************************************************************************
	* ClusterService implementation
	*******************************************************************************/

	public List getServers()
	{
		// get all expired open app servers not me
		String statement =
			"select SERVER_ID from SAKAI_CLUSTER order by SERVER_ID asc";
					
		List servers = m_sqlService.dbRead(statement);
		
		return servers;
	}

	/*******************************************************************************
	* Maintenance
	*******************************************************************************/

	protected class Maintenance
		implements Runnable
	{
		/** My thread running my timeout checker. */
		protected Thread m_maintenanceChecker = null;
		
		/** Signal to the timeout checker to stop. */
		protected boolean m_maintenanceCheckerStop = false;

		/**
		 * Construct.
		 */
		public Maintenance()
		{
		}

		/**
		 * Start the maintenance thread, registering this app server in the cluster table.
		 */
		public void start()
		{
			if (m_maintenanceChecker != null) return;

			// register in the cluster table
			String statement =
					"insert into SAKAI_CLUSTER (SERVER_ID,UPDATE_TIME) values (?, "+sqlTimestamp()+")";
			Object fields[] = new Object[1];
			fields[0] = m_serverConfigurationService.getServerIdInstance();
			boolean ok = m_sqlService.dbWrite(statement, fields);
			if (!ok)
			{
				m_logger.warn(this + ".start(): dbWrite failed");
			}

			m_maintenanceChecker = new Thread(this, "SakaiClusterService.Maintenance");
			m_maintenanceCheckerStop = false;
			m_maintenanceChecker.start();
		}

		/**
		 * Stop the maintenance thread, removing this app server's registration from the cluster table.
		 */
		public void stop()
		{
			if (m_maintenanceChecker != null)
			{
				m_maintenanceCheckerStop = true;
				m_maintenanceChecker.interrupt();
				try
				{
					// wait for it to die
					m_maintenanceChecker.join();
				}
				catch (InterruptedException ignore) {}
				m_maintenanceChecker = null;
			}

			// close our entry from the database - delete the record
			String statement =
					"delete from SAKAI_CLUSTER where SERVER_ID = ?";
			Object fields[] = new Object[1];
			fields[0] = m_serverConfigurationService.getServerIdInstance();
			boolean ok = m_sqlService.dbWrite(statement, fields);
			if (!ok)
			{
				m_logger.warn(this + ".stop(): dbWrite failed: " + statement);
			}
		}

		/**
		 * Run the maintenance thread.
		 * Every REFRESH seconds, re-register this app server as alive in the cluster.
		 * Then check for any cluster entries that are more than EXPIRED seconds old, indicating a failed
		 * app server, and remove that record, that server's sessions, and presence, generating appropriate
		 * session and presence events so the other app servers know what's going on.
		 */
		public void run()
		{
			if (m_logger.isDebugEnabled())
				m_logger.debug(this + ".run()");

			while (!m_maintenanceCheckerStop)
			{
				try
				{
					CurrentService.startThread("CLUSTER");

					final String serverIdInstance = m_serverConfigurationService.getServerIdInstance();

					if (m_logger.isDebugEnabled())
						m_logger.debug(this + ".checking...");

					// if we have been closed, reopen!
					String statement =
							"select SERVER_ID from SAKAI_CLUSTER where SERVER_ID = ?";
					Object[] fields = new Object[1];
					fields[0] = serverIdInstance;
					List results = m_sqlService.dbRead(statement, fields, null);
					if (results.isEmpty())
					{
						m_logger.warn(this + ".run(): server has been closed in cluster table, reopened: " + serverIdInstance);

						statement =
								"insert into SAKAI_CLUSTER (SERVER_ID,UPDATE_TIME) values (?, "+sqlTimestamp()+")";
						fields[0] = serverIdInstance;
						boolean ok = m_sqlService.dbWrite(statement, fields);
						if (!ok)
						{
							m_logger.warn(this + ".start(): dbWrite failed");
						}
					}
					
					// update our alive and well status
					else
					{
						// register that this app server is alive and well
						statement =
								"update SAKAI_CLUSTER set UPDATE_TIME = "+sqlTimestamp()+" where SERVER_ID = ?";
						fields[0] = serverIdInstance;
						boolean ok = m_sqlService.dbWrite(statement, fields);
						if (!ok)
						{
							m_logger.warn(this + ".run(): dbWrite failed: " + statement);
						}
					}

					// get all expired open app servers not me
					if ("oracle".equals(m_sqlService.getVendor()))
					{
						statement = "select SERVER_ID from SAKAI_CLUSTER where SERVER_ID != ? and UPDATE_TIME < (CURRENT_TIMESTAMP - "
						+ ((float)m_expired / (float)(60*60*24))
						+ " )";
					}
					else if ("mysql".equals(m_sqlService.getVendor()))
					{
						statement =
						"select SERVER_ID from SAKAI_CLUSTER where SERVER_ID != ? and UPDATE_TIME < CURRENT_TIMESTAMP() - INTERVAL "
						+ m_expired + " SECOND";
					}
					else //if ("hsqldb".equals(m_sqlService.getVendor()))
					{
						statement = "select SERVER_ID from SAKAI_CLUSTER where SERVER_ID != ? and DATEDIFF('ss', UPDATE_TIME, CURRENT_TIMESTAMP) >= "+m_expired;				    
					}
					// setup the fields to skip reading me!
					fields[0] = serverIdInstance;
					
					List instances = m_sqlService.dbRead(statement, fields, null);

					// close any severs found to be expired
					for (Iterator iInstances = instances.iterator(); iInstances.hasNext();)
					{
						String serverId = (String) iInstances.next();

						// close the server - delete the record
						statement =
								"delete from SAKAI_CLUSTER where SERVER_ID = ?";
						fields[0] = serverId;
						boolean ok = m_sqlService.dbWrite(statement, fields);
						if (!ok)
						{
							m_logger.warn(this + ".run(): dbWrite failed: " + statement);
						}

						m_logger.warn(this + ".run(): ghost-busting server: " + serverId + " from : " + serverIdInstance);
					}

					// find all the session ids of sessions that are open but are from closed servers
					statement =
								"select SESSION_ID from CHEF_SESSION where SESSION_START = SESSION_END "
							+	"and SESSION_SERVER not in "
							+	"(select SERVER_ID from SAKAI_CLUSTER)";
					List sessions = m_sqlService.dbRead(statement);

					// process each session to close it and lose it's presence
					for (Iterator iSessions = sessions.iterator(); iSessions.hasNext();)
					{
						String sessionId = (String) iSessions.next();
						
						// get all the presence for this session
						statement =
								"select LOCATION_ID from CHEF_PRESENCE where SESSION_ID = ?";
						fields[0] = sessionId;		
						List presence = m_sqlService.dbRead(statement, fields, null);
						
						// remove all the presence for this session
						statement =
								"delete from CHEF_PRESENCE where SESSION_ID = ?";			
						boolean ok = m_sqlService.dbWrite(statement, fields);
						if (!ok)
						{
							m_logger.warn(this + ".run(): dbWrite failed: " + statement);
						}

						// get the session
						UsageSession session = m_usageSessionService.getSession(sessionId);

						// send presence end events for these
						for (Iterator iPresence = presence.iterator(); iPresence.hasNext();)
						{
							String locationId = (String) iPresence.next();

							Event event = m_eventTrackingService.newEvent(PresenceService.EVENT_ABSENCE,
									PresenceService.presenceReference(locationId), true);
							m_eventTrackingService.post(event, session);
						}

						// a session closed event (logout)
						Event event = m_eventTrackingService.newEvent(UsageSessionService.EVENT_LOGOUT, null, true);
						m_eventTrackingService.post(event, session);

						// close this session on the db
						statement =
								"update CHEF_SESSION set SESSION_END = "+sqlTimestamp()+" where SESSION_ID = ?";
						fields[0] = sessionId;		
						ok = m_sqlService.dbWrite(statement, fields);
						if (!ok)
						{
							m_logger.warn(this + ".run(): dbWrite failed: " + statement);
						}
						
						// remove any locks from the session
						statement =
								"delete from SAKAI_LOCKS where USAGE_SESSION_ID = ?";
						fields[0] = sessionId;		
						ok = m_sqlService.dbWrite(statement, fields);
						if (!ok)
						{
							m_logger.warn(this + ".run(): dbWrite failed: " + statement);
						}
					}					
				}
				catch (Throwable e) {m_logger.warn(this + ": exception: ", e);}
				finally
				{
					// clear out any current access bindings
					CurrentService.clearInThread();
				}

				// cycle every REFRESH seconds
				if (!m_maintenanceCheckerStop)
				{
					try
					{
						Thread.sleep(m_refresh * 1000L);
					}
					catch (Exception ignore) {}
				}
			}

			if (m_logger.isDebugEnabled())
				m_logger.debug(this + ": done");	
		}
	}
	
	/** Return the vendor-specific SQL for the current timestamp */
	private String sqlTimestamp()
	{
		if ("mysql".equals(m_sqlService.getVendor()))
		{
			return "CURRENT_TIMESTAMP()";
		}
		else // oracle, hsqldb
		{
			return "CURRENT_TIMESTAMP";
		}
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/framework-component/src/java/org/sakaiproject/component/framework/cluster/SakaiClusterService.java,v 1.9 2005/01/18 22:21:17 janderse.umich.edu Exp $
*
**********************************************************************************/
