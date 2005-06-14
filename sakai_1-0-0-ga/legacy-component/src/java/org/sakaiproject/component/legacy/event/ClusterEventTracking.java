/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/event/ClusterEventTracking.java,v 1.15 2004/10/07 08:12:24 janderse.umich.edu Exp $
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
package org.sakaiproject.component.legacy.event;

// imports
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.sakaiproject.service.framework.config.ServerConfigurationService;
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.notification.NotificationService;
import org.sakaiproject.service.framework.sql.SqlReader;
import org.sakaiproject.service.framework.sql.SqlService;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.time.TimeService;
import org.sakaiproject.util.StringUtil;

/**
* <p>ClusterEventTracking is the implmentation for the EventTracking CHEF event tracking service
* for use in a clustered multi-app server configuration.  Events are backed in the cluster database, and
* this database is polled to read and process locally events posted by the other cluster members.</p>
* <p>Note: the database design (see chef_event.sql) imposes a 32 character limit for
* events.</p>
* <p>The sql scripts in src/sql/chef_event.sql must be run on the cluster database.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.15 $
*/
public class ClusterEventTracking
	extends BaseEventTrackingService
	implements Runnable
{
	/** String used to identify this service in the logs */
	protected static String m_logId = "EventTracking: ";

	/** How long to wait between checks for new events from the db. */
	protected static long PERIOD = 1000 * 2;

	/** The thread I run my db event checker on. */
	protected Thread m_thread = null;

	/** My thread's quit flag. */
	protected boolean m_threadStop = false;

	/** Last event code we read from the db */
	protected long m_lastEventSeq = 0;

	/** Unless false, check the db for events from the other cluster servers. */
	protected boolean m_checkDb = true;

	/*******************************************************************************
	* Constructors, Dependencies and their setter methods
	*******************************************************************************/

	/**
	 * Configuration: set the check-db.
	 * @param value The check-db value.
	 */
	public void setCheckDb(String value)
	{
		try
		{
			m_checkDb = new Boolean(value).booleanValue();
		}
		catch (Exception any)
		{
		}
	}

	/** Dependency: SqlService. */
	protected SqlService m_sqlService = null;

	/**
	 * Dependency: SqlService.
	 * @param value The SqlService.
	 */
	public void setSqlService(SqlService service)
	{
		m_sqlService = service;
	}

	/** Dependency: configuration service. */
	protected ServerConfigurationService m_serverConfigurationService = null;

	/**
	 * Dependency: configuration service
	 */
	public void setServerConfigurationService(ServerConfigurationService service)
	{
		m_serverConfigurationService = service;
	}

	/** Dependency: TimeService. */
	protected TimeService m_timeService = null;

	/**
	 * Dependency: TimeService.
	 */
	public void setTimeService(TimeService service)
	{
		m_timeService = service;
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

			// find the latest event in the db, we will start processing events after this
			if (m_checkDb)
			{
				initLastEvent();

				// startup the event checking
				start();
			}
		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}
	}

	/**
	 * Final cleanup.
	 */
	public void destroy()
	{
		// stop our thread
		stop();

		super.destroy();
	}

	/*******************************************************************************
	* Event post / flow
	*******************************************************************************/

	/**
	* Cause this new event to get to wherever it has to go for persistence, etc.
	* @param event The new event to post.
	*/
	protected void postEvent(Event event)
	{
		// session or user?
		String reportId = null;
		if (event.getSessionId() != null)
		{
			reportId = event.getSessionId();
		}
		else
		{
			// form an id based on the cluster server's id and the event user id
			reportId = "~" + m_serverConfigurationService.getServerId() + "~" +event.getUserId();
		}

		String statement;
		if ("oracle".equals(m_sqlService.getVendor()))
		{
			// send this to the database
			statement =
					"insert into CHEF_EVENT"
				+ " (EVENT_ID,EVENT_DATE,EVENT,REF,SESSION_ID,EVENT_CODE)"
				+   " values ("
				// form the id based on the sequence
				+	" CHEF_EVENT_SEQ.NEXTVAL,"
				// date
				+	" ?,"
				// event
				+	" ?,"
				// reference
				+	" ?,"
				// session id
				+	" ?,"
				// code
				+	" ?"
	
				+   " )";
		}
		else // if ("mysql".equals(m_sqlService.getVendor()))
		{
			// leave out the EVENT_ID as it will be automatically generated on the server
			statement =
					"insert into CHEF_EVENT"
				+ " (EVENT_DATE,EVENT,REF,SESSION_ID,EVENT_CODE)"
				+   " values ("
				// date
				+	" ?,"
				// event
				+	" ?,"
				// reference
				+	" ?,"
				// session id
				+	" ?,"
				// code
				+	" ?"
	
				+   " )";
		}
	

		// collect the fields
		Object fields[] = new Object[5];
		fields[0] = m_timeService.newTime();
		fields[1] = event.getEvent();
		fields[2] = event.getResource();
		fields[3] = reportId;
		fields[4] = (event.getModify() ? "m" : "a");

		// process the insert
		boolean ok = m_sqlService.dbWrite(statement, fields);
		if (!ok)
		{
			m_logger.warn(this + ".postEvent(): dbWrite failed: session: " + reportId + " event: " + event.toString());
		}

		// notify locally generated events immediately -
		// they will not be process again when read back from the database
		notifyObservers(event, true);
		
		// and log it...'
		if (m_logger.isDebugEnabled())
			m_logger.debug(m_logId + reportId + "@" + event);

	}	// postEvent

	/*******************************************************************************
	* Runnable
	*******************************************************************************/

	/**
	* Start the clean and report thread.
	*/
	protected void start()
	{
		m_threadStop = false;

		m_thread = new Thread(this, getClass().getName());
		m_thread.start();
		
	}	// start

	/**
	* Stop the clean and report thread.
	*/
	protected void stop()
	{
		if (m_thread == null) return;

		// signal the thread to stop
		m_threadStop = true;

		// wake up the thread
		m_thread.interrupt();

		m_thread = null;

	}	// stop

	/**
	* Run the event checking thread.
	*/
	public void run()
	{
		// loop till told to stop
		while (		(!m_threadStop)
				&&	(!Thread.currentThread().isInterrupted()))
		{
			final String serverInstance = m_serverConfigurationService.getServerIdInstance();
			final String serverId = m_serverConfigurationService.getServerId();

			try
			{
				String statement;
				// check the db for new events
				// Note: the events may not all have sessions, so to get them we need an outer join.
				// TODO: switch to a "view" read once that's established, for now, a join -ggolden
				if ("oracle".equals(m_sqlService.getVendor()))
				{
					// this now has Oracle specific hint to improve performance with large tables -ggolden
					statement =
					"select /*+ FIRST_ROWS */ EVENT_ID,EVENT_DATE,EVENT,REF,CHEF_EVENT.SESSION_ID,EVENT_CODE,SESSION_SERVER"
						+ " from CHEF_EVENT,CHEF_SESSION"
						+ " where (CHEF_EVENT.SESSION_ID = CHEF_SESSION.SESSION_ID(+)) and (EVENT_ID > ?)";
				}
				else
				{
					statement =
					"select /*+ FIRST_ROWS */ EVENT_ID,EVENT_DATE,EVENT,REF,CHEF_EVENT.SESSION_ID,EVENT_CODE,SESSION_SERVER"
						+ " from CHEF_EVENT,CHEF_SESSION"
						+ " where (CHEF_EVENT.SESSION_ID = CHEF_SESSION.SESSION_ID) and (EVENT_ID > ?)";				
				}
				
				// send in the last seq number parameter
				Object[] fields = new Object[1];
				fields[0] = new Long(m_lastEventSeq);

				List events = m_sqlService.dbRead(statement, fields,
					new SqlReader()
					{
						public Object readSqlResultRecord(ResultSet result)
						{
							try
							{
								// read the Event
								long id = result.getLong(1);
								Time date = m_timeService.newTime(result.getTimestamp(2, m_sqlService.getCal()).getTime());
								String function = result.getString(3);
								String ref = result.getString(4);
								String session = result.getString(5);
								String code = result.getString(6);
								String eventSessionServerId = result.getString(7);
								
								// for each one (really, for the last one), update the last event seen seq number
								m_lastEventSeq = id;

								boolean nonSessionEvent = session.startsWith("~");
								String userId = null;
								boolean skipIt = false;

								if (nonSessionEvent)
								{
									String[] parts = StringUtil.split(session, "~");
									userId = parts[2];
									
									// we skip this event if it came from our server
									skipIt = serverId.equals(parts[1]);
								}
								
								// for session events, if the event is from this server instance,
								// we have already processed it and can skip it here.
								else
								{
									skipIt = serverInstance.equals(eventSessionServerId);
								}

								if (skipIt)
								{
									return null;
								}

								BaseEvent event = new BaseEvent(id, function, ref, code.equals("m"),
										// Note: events from outside the server don't need notification info, since
										// notification is processed only on internal events -ggolden
										NotificationService.NOTI_NONE);
								if (nonSessionEvent)
								{
									event.setUserId(userId);
								}
								else
								{
									event.setSessionId(session);
								}

								return event;
							}
							catch (SQLException ignore) { return null;}
						}
					} );

				// for each new event found, notify observers
				for (int i = 0; i < events.size(); i++)
				{
					Event event = (Event) events.get(i);
					notifyObservers(event, false);
				}
			}
			catch (Throwable e) {m_logger.warn(this + ": exception: ", e);}

			// take a small nap
			try
			{
				Thread.sleep(PERIOD);
			}
			catch (Exception ignore) {}

		}	// while

	}	// run

	/**
	* Check the db for the largest event seq number, and set this as the one after which we will next get event.
	*/
	protected void initLastEvent()
	{
		String statement =
			"select MAX(EVENT_ID) from CHEF_EVENT";

		m_sqlService.dbRead(statement, null,
			new SqlReader()
			{
				public Object readSqlResultRecord(ResultSet result)
				{
					try
					{
						// read the one long value into our last event seq number
						m_lastEventSeq = result.getLong(1);
					}
					catch (SQLException ignore) {}
					return null;
				}
			} );

		if (m_logger.isDebugEnabled())
			m_logger.debug(this + " Starting (after) Event #: " + m_lastEventSeq);

	}	// initLastEvent

}	// ClusterEventTracking

/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/event/ClusterEventTracking.java,v 1.15 2004/10/07 08:12:24 janderse.umich.edu Exp $
*
**********************************************************************************/
