/**********************************************************************************
 * $URL$
 * $Id$
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
package org.sakaiproject.component.legacy.event;

// imports
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.sakaiproject.service.framework.config.ServerConfigurationService;
import org.sakaiproject.service.framework.sql.SqlReader;
import org.sakaiproject.service.framework.sql.SqlService;
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.notification.NotificationService;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.time.TimeService;
import org.sakaiproject.util.java.StringUtil;

/**
 * <p>
 * ClusterEventTracking is the implmentation for the EventTracking service for use in a clustered multi-app server configuration. Events are backed in the cluster database, and this database is polled to read and process locally events posted by the other
 * cluster members.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class ClusterEventTracking extends BaseEventTrackingService implements Runnable
{
	/** String used to identify this service in the logs */
	protected static String m_logId = "EventTracking: ";

	/** The db event checker thread. */
	protected Thread m_thread = null;

	/** The thread quit flag. */
	protected boolean m_threadStop = false;

	/** Last event code read from the db */
	protected long m_lastEventSeq = 0;

	/** Queue of events to write if we are batching. */
	protected Collection m_eventQueue = null;

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Constructors, Dependencies and their setter methods
	 *********************************************************************************************************************************************************************************************************************************************************/

	/** Unless false, check the db for events from the other cluster servers. */
	protected boolean m_checkDb = true;

	/**
	 * Configuration: set the check-db.
	 * 
	 * @param value
	 *        The check-db value.
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

	/** If true, batch events for bulk write. */
	protected boolean m_batchWrite = true;

	/**
	 * Configuration: set the batch writing flag.
	 * 
	 * @param value
	 *        The batch writing value.
	 */
	public void setBatchWrite(String value)
	{
		try
		{
			m_batchWrite = new Boolean(value).booleanValue();
		}
		catch (Exception any)
		{
		}
	}

	/** Dependency: SqlService. */
	protected SqlService m_sqlService = null;

	/**
	 * Dependency: SqlService.
	 * 
	 * @param value
	 *        The SqlService.
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
		m_autoDdl = new Boolean(value).booleanValue();
	}

	/** How long to wait between checks for new events from the db. */
	protected long m_period = 1000L * 5L;

	/**
	 * Set the # seconds to wait between db checks for new events.
	 * 
	 * @param time
	 *        The # seconds to wait between db checks for new events.
	 */
	public void setPeriod(String time)
	{
		m_period = Integer.parseInt(time) * 1000L;
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Init and Destroy
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		try
		{
			// if we are auto-creating our schema, check and create
			if (m_autoDdl)
			{
				m_sqlService.ddl(this.getClass().getClassLoader(), "sakai_event");
			}

			super.init();

			if (m_batchWrite)
			{
				m_eventQueue = new Vector();
			}

			// find the latest event in the db, we will start processing events after this
			if (m_checkDb)
			{
				initLastEvent();

				// startup the event checking
				start();
			}

			m_logger.info(this + ".init() - period: " + m_period / 1000 + " batch: " + m_batchWrite);
		}
		catch (Throwable t)
		{
			m_logger.warn(this + ".init(): ", t);
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

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Event post / flow
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * Cause this new event to get to wherever it has to go for persistence, etc.
	 * 
	 * @param event
	 *        The new event to post.
	 */
	protected void postEvent(Event event)
	{
		// mark the event time
		((BaseEvent) event).m_time = m_timeService.newTime();

		// notify locally generated events immediately -
		// they will not be process again when read back from the database
		notifyObservers(event, true);

		// batch the event if we are batching
		if (m_batchWrite)
		{
			synchronized (m_eventQueue)
			{
				m_eventQueue.add(event);
			}
		}

		// if not batching, write out the individual event
		else
		{
			writeEvent(event, null);
		}

		if (m_logger.isDebugEnabled()) m_logger.debug(m_logId + event);
	}

	/**
	 * Write a single event to the db
	 * 
	 * @param event
	 *        The event to write.
	 */
	protected void writeEvent(Event event, Connection conn)
	{
		// get the SQL statement
		String statement = insertStatement();

		// collect the fields
		Object fields[] = new Object[5];
		bindValues(event, fields);

		// process the insert
		boolean ok = m_sqlService.dbWrite(conn, statement, fields);
		if (!ok)
		{
			m_logger.warn(this + ".writeEvent(): dbWrite failed: session: " + fields[3] + " event: " + event.toString());
		}
	}

	/**
	 * Write a batch of events to the db
	 * 
	 * @param events
	 *        The collection of event to write.
	 */
	protected void writeBatchEvents(Collection events)
	{
		// get a connection
		Connection conn = null;
		boolean wasCommit = true;
		try
		{
			conn = m_sqlService.borrowConnection();
			wasCommit = conn.getAutoCommit();
			if (wasCommit)
			{
				conn.setAutoCommit(false);
			}

			// Note: investigate batch writing via the jdbc driver: make sure we can still use prepared statements (check out host arrays, too) -ggolden

			// common preparation for each insert
			String statement = insertStatement();
			Object fields[] = new Object[5];

			// write all events
			for (Iterator i = events.iterator(); i.hasNext();)
			{
				Event event = (Event) i.next();
				bindValues(event, fields);

				// process the insert
				boolean ok = m_sqlService.dbWrite(conn, statement, fields);
				if (!ok)
				{
					m_logger.warn(this + ".writeBatchEvents(): dbWrite failed: session: " + fields[3] + " event: "
							+ event.toString());
				}
			}

			// commit
			conn.commit();
		}
		catch (Throwable e)
		{
			if (conn != null)
			{
				try
				{
					conn.rollback();
				}
				catch (Exception ee)
				{
					m_logger.warn(this + ".writeBatchEvents, while rolling back: " + ee);
				}
			}
			m_logger.warn(this + ".writeBatchEvents: " + e);
		}
		finally
		{
			if (conn != null)
			{
				try
				{
					if (conn.getAutoCommit() != wasCommit)
					{
						conn.setAutoCommit(wasCommit);
					}
				}
				catch (Exception e)
				{
					m_logger.warn(this + ".writeBatchEvents, while setting auto commit: " + e);
				}
				m_sqlService.returnConnection(conn);
			}
		}
	}

	/**
	 * Form the proper event insert statement for the database technology.
	 * 
	 * @return The SQL insert statement for writing an event.
	 */
	protected String insertStatement()
	{
		String statement;
		if ("oracle".equals(m_sqlService.getVendor()))
		{
			statement = "insert into SAKAI_EVENT" + " (EVENT_ID,EVENT_DATE,EVENT,REF,SESSION_ID,EVENT_CODE)" + " values ("
			// form the id based on the sequence
					+ " SAKAI_EVENT_SEQ.NEXTVAL,"
					// date
					+ " ?,"
					// event
					+ " ?,"
					// reference
					+ " ?,"
					// session id
					+ " ?,"
					// code
					+ " ?"

					+ " )";
		}
		else if ("mysql".equals(m_sqlService.getVendor()))
		{
			// leave out the EVENT_ID as it will be automatically generated on the server
			statement = "insert into SAKAI_EVENT" + " (EVENT_DATE,EVENT,REF,SESSION_ID,EVENT_CODE)" + " values ("
			// date
					+ " ?,"
					// event
					+ " ?,"
					// reference
					+ " ?,"
					// session id
					+ " ?,"
					// code
					+ " ?"

					+ " )";
		}
		else
		// if ("hsqldb".equals(m_sqlService.getVendor()))
		{
			statement = "insert into SAKAI_EVENT" + " (EVENT_ID,EVENT_DATE,EVENT,REF,SESSION_ID,EVENT_CODE)" + " values ("
			// form the id based on the sequence
					+ " NEXT VALUE FOR SAKAI_EVENT_SEQ,"
					// date
					+ " ?,"
					// event
					+ " ?,"
					// reference
					+ " ?,"
					// session id
					+ " ?,"
					// code
					+ " ?"

					+ " )";
		}

		return statement;
	}

	/**
	 * Bind the event values into an array of fields for inserting.
	 * 
	 * @param event
	 *        The event to write.
	 * @param fields
	 *        The object[] to hold bind variables.
	 */
	protected void bindValues(Event event, Object[] fields)
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
			reportId = "~" + m_serverConfigurationService.getServerId() + "~" + event.getUserId();
		}

		fields[0] = ((BaseEvent) event).m_time;
		fields[1] = event.getEvent();
		fields[2] = event.getResource();
		fields[3] = reportId;
		fields[4] = (event.getModify() ? "m" : "a");
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Runnable
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * Start the clean and report thread.
	 */
	protected void start()
	{
		m_threadStop = false;

		m_thread = new Thread(this, getClass().getName());
		m_thread.start();
	}

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
	}

	/**
	 * Run the event checking thread.
	 */
	public void run()
	{
		// loop till told to stop
		while ((!m_threadStop) && (!Thread.currentThread().isInterrupted()))
		{
			final String serverInstance = m_serverConfigurationService.getServerIdInstance();
			final String serverId = m_serverConfigurationService.getServerId();

			try
			{
				// write any batched events
				Collection myEvents = new Vector();
				if (m_batchWrite)
				{
					synchronized (m_eventQueue)
					{
						if (m_eventQueue.size() > 0)
						{
							myEvents.addAll(m_eventQueue);
							m_eventQueue.clear();
						}
					}

					if (myEvents.size() > 0)
					{
						writeBatchEvents(myEvents);
					}
				}

				String statement;
				// check the db for new events
				// Note: the events may not all have sessions, so to get them we need an outer join.
				// TODO: switch to a "view" read once that's established, for now, a join -ggolden
				if ("oracle".equals(m_sqlService.getVendor()))
				{
					// this now has Oracle specific hint to improve performance with large tables -ggolden
					statement = "select /*+ FIRST_ROWS */ EVENT_ID,EVENT_DATE,EVENT,REF,SAKAI_EVENT.SESSION_ID,EVENT_CODE,SESSION_SERVER"
							+ " from SAKAI_EVENT,SAKAI_SESSION"
							+ " where (SAKAI_EVENT.SESSION_ID = SAKAI_SESSION.SESSION_ID(+)) and (EVENT_ID > ?)";
				}
				else
				// non-Oracle, without Oracle hint
				{
					statement = "select EVENT_ID,EVENT_DATE,EVENT,REF,SAKAI_EVENT.SESSION_ID,EVENT_CODE,SESSION_SERVER"
							+ " from SAKAI_EVENT,SAKAI_SESSION"
							+ " where (SAKAI_EVENT.SESSION_ID = SAKAI_SESSION.SESSION_ID) and (EVENT_ID > ?)";
				}

				// send in the last seq number parameter
				Object[] fields = new Object[1];
				fields[0] = new Long(m_lastEventSeq);

				List events = m_sqlService.dbRead(statement, fields, new SqlReader()
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
							if (id > m_lastEventSeq)
							{
								m_lastEventSeq = id;
							}

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
						catch (SQLException ignore)
						{
							return null;
						}
					}
				});

				// for each new event found, notify observers
				for (int i = 0; i < events.size(); i++)
				{
					Event event = (Event) events.get(i);
					notifyObservers(event, false);
				}
			}
			catch (Throwable e)
			{
				m_logger.warn(this + ": exception: ", e);
			}

			// take a small nap
			try
			{
				Thread.sleep(m_period);
			}
			catch (Exception ignore)
			{
			}

		} // while
	}

	/**
	 * Check the db for the largest event seq number, and set this as the one after which we will next get event.
	 */
	protected void initLastEvent()
	{
		String statement = "select MAX(EVENT_ID) from SAKAI_EVENT";

		m_sqlService.dbRead(statement, null, new SqlReader()
		{
			public Object readSqlResultRecord(ResultSet result)
			{
				try
				{
					// read the one long value into our last event seq number
					m_lastEventSeq = result.getLong(1);
				}
				catch (SQLException ignore)
				{
				}
				return null;
			}
		});

		if (m_logger.isDebugEnabled()) m_logger.debug(this + " Starting (after) Event #: " + m_lastEventSeq);
	}
}
