/**********************************************************************************
 *
 * $Header: /cvs/sakai2/kernel/session-component/src/java/org/sakaiproject/component/kernel/session/SessionComponent.java,v 1.20 2005/05/30 03:39:09 ggolden.umich.edu Exp $
 *
 ***********************************************************************************
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

package org.sakaiproject.component.kernel.session;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.kernel.id.IdManager;
import org.sakaiproject.api.kernel.session.ContextSession;
import org.sakaiproject.api.kernel.session.Session;
import org.sakaiproject.api.kernel.session.SessionBindingEvent;
import org.sakaiproject.api.kernel.session.SessionBindingListener;
import org.sakaiproject.api.kernel.session.SessionManager;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.api.kernel.thread_local.ThreadLocalManager;
import org.sakaiproject.util.java.IteratorEnumeration;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentReaderHashMap;

/**
 * <p>
 * Standard implementation of the Sakai SessionManager.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class SessionComponent implements SessionManager
{
	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(SessionComponent.class);

	/** The sessions - keyed by session id. */
	protected Map m_sessions = new ConcurrentReaderHashMap();

	/** The maintenance. */
	protected Maintenance m_maintenance = null;

	/** Key in the ThreadLocalManager for binding our current session. */
	protected final static String CURRENT_SESSION = "org.sakaiproject.api.kernel.session.current";

	/** Key in the ThreadLocalManager for binding our current tool session. */
	protected final static String CURRENT_TOOL_SESSION = "org.sakaiproject.api.kernel.session.current.tool";

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Dependencies and their setter methods
	 *********************************************************************************************************************************************************************************************************************************************************/

	/** Dependency: the current manager. */
	protected ThreadLocalManager m_threadLocalManager = null;

	/**
	 * Dependency - set the current manager.
	 * 
	 * @param value
	 *        The current manager.
	 */
	public void setThreadLocalManager(ThreadLocalManager manager)
	{
		m_threadLocalManager = manager;
	}

	/** Dependency: the id manager. */
	protected IdManager m_idManager = null;

	/**
	 * Dependency - set the id manager.
	 * 
	 * @param value
	 *        The id manager.
	 */
	public void setIdManager(IdManager manager)
	{
		m_idManager = manager;
	}

	/** Configuration: default inactive period for sessions (seconds). */
	protected int m_defaultInactiveInterval = 30 * 60;

	/**
	 * Configuration - set the default inactive period for sessions.
	 * 
	 * @param value
	 *        The default inactive period for sessions.
	 */
	public void setInactiveInterval(String value)
	{
		try
		{
			m_defaultInactiveInterval = Integer.parseInt(value);
		}
		catch (Throwable t)
		{
			System.out.println(t);
		}
	}

	/** Configuration: how often to check for inactive sessions (seconds). */
	protected int m_checkEvery = 60;

	/**
	 * Configuration: set how often to check for inactive sessions (seconds).
	 * 
	 * @param value
	 *        The how often to check for inactive sessions (seconds) value.
	 */
	public void setCheckEvery(String value)
	{
		try
		{
			m_checkEvery = Integer.parseInt(value);
		}
		catch (Throwable t)
		{
			System.out.println(t);
		}
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Init and Destroy
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		// start the maintenance thread
		if (m_checkEvery > 0)
		{
			m_maintenance = new Maintenance();
			m_maintenance.start();
		}

		M_log.info("init(): interval: " + m_defaultInactiveInterval + " refresh: " + m_checkEvery);
	}

	/**
	 * Final cleanup.
	 */
	public void destroy()
	{
		if (m_maintenance != null)
		{
			m_maintenance.stop();
			m_maintenance = null;
		}

		M_log.info("destroy()");
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Work interface methods: SessionManager
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * @inheritDoc
	 */
	public Session getSession(String sessionId)
	{
		MySession s = (MySession) m_sessions.get(sessionId);

		return s;
	}

	/**
	 * @inheritDoc
	 */
	public Session startSession()
	{
		// create a new session
		Session s = new MySession();

		// remember it by id
		Session old = (Session) m_sessions.put(s.getId(), s);

		// check for id conflict
		if (old != null)
		{
			M_log.warn("startSession: duplication id: " + s.getId());
		}

		return s;
	}

	/**
	 * @inheritDoc
	 */
	public Session startSession(String id)
	{
		// create a new session
		Session s = new MySession(id);

		// remember it by id
		Session old = (Session) m_sessions.put(s.getId(), s);

		// check for id conflict
		if (old != null)
		{
			M_log.warn("startSession(id): duplication id: " + s.getId());
		}

		return s;
	}

	/**
	 * @inheritDoc
	 */
	public Session getCurrentSession()
	{
		return (Session) m_threadLocalManager.get(CURRENT_SESSION);
	}

	/**
	 * @inheritDoc
	 */
	public ToolSession getCurrentToolSession()
	{
		return (ToolSession) m_threadLocalManager.get(CURRENT_TOOL_SESSION);
	}

	/**
	 * @inheritDoc
	 */
	public void setCurrentSession(Session s)
	{
		m_threadLocalManager.set(CURRENT_SESSION, s);
	}

	/**
	 * @inheritDoc
	 */
	public void setCurrentToolSession(ToolSession s)
	{
		m_threadLocalManager.set(CURRENT_TOOL_SESSION, s);
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Entity: Session Also is an HttpSession
	 *********************************************************************************************************************************************************************************************************************************************************/

	public class MySession implements Session, HttpSession
	{
		/** Hold attributes in a Map. TODO: ConcurrentHashMap may be better for multiple writers */
		protected Map m_attributes = new ConcurrentReaderHashMap();

		/** Hold toolSessions in a Map, by placement id. TODO: ConcurrentHashMap may be better for multiple writers */
		protected Map m_toolSessions = new ConcurrentReaderHashMap();

		/** Hold context toolSessions in a Map, by context (webapp) id. TODO: ConcurrentHashMap may be better for multiple writers */
		protected Map m_contextSessions = new ConcurrentReaderHashMap();

		/** The creation time of the session. */
		protected long m_created = 0;

		/** The session id. */
		protected String m_id = null;

		/** Time last accessed (via getSession()). */
		protected long m_accessed = 0;

		/** Seconds of inactive time before being automatically invalidated - 0 turns off this feature. */
		protected int m_inactiveInterval = m_defaultInactiveInterval;

		/** The user id for this session. */
		protected String m_userId = null;

		/** The user enterprise id for this session. */
		protected String m_userEid = null;

		/** True while the session is valid. */
		protected boolean m_valid = true;

		public MySession()
		{
			m_id = m_idManager.createUuid();
			m_created = System.currentTimeMillis();
			m_accessed = m_created;
		}

		public MySession(String id)
		{
			m_id = id;
			m_created = System.currentTimeMillis();
			m_accessed = m_created;
		}

		/**
		 * @inheritDoc
		 */
		public Object getAttribute(String name)
		{
			return m_attributes.get(name);
		}

		/**
		 * @inheritDoc
		 */
		public Enumeration getAttributeNames()
		{
			return new IteratorEnumeration(m_attributes.keySet().iterator());
		}

		/**
		 * @inheritDoc
		 */
		public long getCreationTime()
		{
			return m_created;
		}

		/**
		 * @inheritDoc
		 */
		public String getId()
		{
			return m_id;
		}

		/**
		 * @inheritDoc
		 */
		public long getLastAccessedTime()
		{
			return m_accessed;
		}

		/**
		 * @inheritDoc
		 */
		public int getMaxInactiveInterval()
		{
			return m_inactiveInterval;
		}

		/**
		 * @inheritDoc
		 */
		public String getUserEid()
		{
			return m_userEid;
		}

		/**
		 * @inheritDoc
		 */
		public String getUserId()
		{
			return m_userId;
		}

		/**
		 * @inheritDoc
		 */
		public void invalidate()
		{
			m_valid = false;

			// move the attributes and tool sessions to local maps in a synchronized block so the unbinding happens only on one thread
			Map unbindMap = null;
			Map toolMap = null;
			Map contextMap = null;
			synchronized (this)
			{
				unbindMap = new HashMap(m_attributes);
				m_attributes.clear();

				toolMap = new HashMap(m_toolSessions);
				m_toolSessions.clear();

				contextMap = new HashMap(m_contextSessions);
				m_contextSessions.clear();

				// let it not be found
				m_sessions.remove(getId());
			}

			// clear each tool session
			for (Iterator i = toolMap.entrySet().iterator(); i.hasNext();)
			{
				Map.Entry e = (Map.Entry) i.next();
				ToolSession t = (ToolSession) e.getValue();
				t.clearAttributes();
			}

			// clear each context session
			for (Iterator i = contextMap.entrySet().iterator(); i.hasNext();)
			{
				Map.Entry e = (Map.Entry) i.next();
				ToolSession t = (ToolSession) e.getValue();
				t.clearAttributes();
			}

			// send unbind events
			for (Iterator i = unbindMap.entrySet().iterator(); i.hasNext();)
			{
				Map.Entry e = (Map.Entry) i.next();
				String name = (String) e.getKey();
				Object value = e.getValue();
				unBind(name, value);
			}

			// if this is the current session, remove it
			if (this.equals(getCurrentSession()))
			{
				setCurrentSession(null);
			}
		}

		/**
		 * @inheritDoc
		 */
		public void setActive()
		{
			m_accessed = System.currentTimeMillis();
		}

		/**
		 * @inheritDoc
		 */
		public void removeAttribute(String name)
		{
			// remove
			Object value = m_attributes.remove(name);

			// unbind event
			unBind(name, value);
		}

		/**
		 * @inheritDoc
		 */
		public void setAttribute(String name, Object value)
		{
			// add
			Object old = m_attributes.put(name, value);

			// bind event
			bind(name, value);

			// unbind event if old exiss
			if (old != null)
			{
				unBind(name, old);
			}
		}

		/**
		 * @inheritDoc
		 */
		public void setMaxInactiveInterval(int interval)
		{
			m_inactiveInterval = interval;
		}

		/**
		 * @inheritDoc
		 */
		public void setUserEid(String eid)
		{
			m_userEid = eid;
		}

		/**
		 * @inheritDoc
		 */
		public void setUserId(String uid)
		{
			m_userId = uid;
		}

		/**
		 * @inheritDoc
		 */
		public ToolSession getToolSession(String placementId)
		{
			ToolSession t = (ToolSession) m_toolSessions.get(placementId);
			if (t == null)
			{
				t = new MyLittleSession(this, placementId);
				m_toolSessions.put(placementId, t);
			}

			// mark it as accessed
			((MyLittleSession) t).setAccessed();

			return t;
		}

		/**
		 * @inheritDoc
		 */
		public ContextSession getContextSession(String contextId)
		{
			ContextSession t = (ContextSession) m_contextSessions.get(contextId);
			if (t == null)
			{
				t = new MyLittleSession(this, contextId);
				m_contextSessions.put(contextId, t);
			}

			// mark it as accessed
			((MyLittleSession) t).setAccessed();

			return t;
		}

		/**
		 * Check if the session has become inactive
		 * 
		 * @return true if the session is capable of becoming inactive and has done so, false if not.
		 */
		protected boolean isInactive()
		{
			return ((m_inactiveInterval > 0) && (System.currentTimeMillis() > (m_accessed + (m_inactiveInterval * 1000))));
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean equals(Object obj)
		{
			if (!(obj instanceof Session))
			{
				return false;
			}

			return ((Session) obj).getId().equals(getId());
		}

		/**
		 * {@inheritDoc}
		 */
		public int hashCode()
		{
			return getId().hashCode();
		}

		/**
		 * {@inheritDoc}
		 */
		public ServletContext getServletContext()
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		public HttpSessionContext getSessionContext()
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		public Object getValue(String arg0)
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		public String[] getValueNames()
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		public void putValue(String arg0, Object arg1)
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		public void removeValue(String arg0)
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isNew()
		{
			return false;
		}

		/**
		 * Unbind the value if it's a SessionBindingListener. Also does the HTTP unbinding if it's a HttpSessionBindingListener.
		 * 
		 * @param name
		 *        The attribute name bound.
		 * @param value
		 *        The bond value.
		 */
		protected void unBind(String name, Object value)
		{
			if (value instanceof SessionBindingListener)
			{
				SessionBindingEvent event = new MySessionBindingEvent(name, this, value);
				((SessionBindingListener) value).valueUnbound(event);
			}

			// also unbind any objects that are regular HttpSessionBindingListeners
			if (value instanceof HttpSessionBindingListener)
			{
				HttpSessionBindingEvent event = new HttpSessionBindingEvent(this, name, value);
				((HttpSessionBindingListener) value).valueUnbound(event);
			}
		}

		/**
		 * Bind the value if it's a SessionBindingListener. Also does the HTTP binding if it's a HttpSessionBindingListener.
		 * 
		 * @param name
		 *        The attribute name bound.
		 * @param value
		 *        The bond value.
		 */
		protected void bind(String name, Object value)
		{
			if (value instanceof SessionBindingListener)
			{
				SessionBindingEvent event = new MySessionBindingEvent(name, this, value);
				((SessionBindingListener) value).valueBound(event);
			}

			// also bind any objects that are regular HttpSessionBindingListeners
			if (value instanceof HttpSessionBindingListener)
			{
				HttpSessionBindingEvent event = new HttpSessionBindingEvent(this, name, value);
				((HttpSessionBindingListener) value).valueBound(event);
			}
		}
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Entity: SessionBindingEvent
	 *********************************************************************************************************************************************************************************************************************************************************/

	public class MySessionBindingEvent implements SessionBindingEvent
	{
		/** The attribute name. */
		protected String m_name = null;

		/** The session. */
		protected Session m_session = null;

		/** The value. */
		protected Object m_value = null;

		/**
		 * Construct.
		 * 
		 * @param name
		 *        The name.
		 * @param session
		 *        The session.
		 * @param value
		 *        The value.
		 */
		MySessionBindingEvent(String name, Session session, Object value)
		{
			m_name = name;
			m_session = session;
			m_value = value;
		}

		/**
		 * @inheritDoc
		 */
		public String getName()
		{
			return m_name;
		}

		/**
		 * @inheritDoc
		 */
		public Session getSession()
		{
			return m_session;
		}

		/**
		 * @inheritDoc
		 */
		public Object getValue()
		{
			return m_value;
		}
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Entity: ToolSession, ContextSession (and even HttpSession)
	 *********************************************************************************************************************************************************************************************************************************************************/

	public class MyLittleSession implements ToolSession, ContextSession, HttpSession
	{
		/** Hold attributes in a Map. TODO: ConcurrentHashMap may be better for multiple writers */
		protected Map m_attributes = new ConcurrentReaderHashMap();

		/** The creation time of the session. */
		protected long m_created = 0;

		/** The session id. */
		protected String m_id = null;

		/** The tool placement / context id. */
		protected String m_littleId = null;

		/** The sakai session in which I live. */
		protected Session m_session = null;

		/** Time last accessed (via getSession()). */
		protected long m_accessed = 0;

		public MyLittleSession(Session s, String id)
		{
			m_id = m_idManager.createUuid();
			m_created = System.currentTimeMillis();
			m_accessed = m_created;
			m_littleId = id;
			m_session = s;
		}

		/**
		 * @inheritDoc
		 */
		public Object getAttribute(String name)
		{
			return m_attributes.get(name);
		}

		/**
		 * @inheritDoc
		 */
		public Enumeration getAttributeNames()
		{
			return new IteratorEnumeration(m_attributes.keySet().iterator());
		}

		/**
		 * @inheritDoc
		 */
		public long getCreationTime()
		{
			return m_created;
		}

		/**
		 * @inheritDoc
		 */
		public String getId()
		{
			return m_id;
		}

		/**
		 * @inheritDoc
		 */
		public long getLastAccessedTime()
		{
			return m_accessed;
		}

		/**
		 * @inheritDoc
		 */
		public String getPlacementId()
		{
			return m_littleId;
		}

		/**
		 * @inheritDoc
		 */
		public String getContextId()
		{
			return m_littleId;
		}

		/**
		 * @inheritDoc
		 */
		public void clearAttributes()
		{
			// move the attributes to a local map in a synchronized block so the unbinding happens only on one thread
			Map unbindMap = null;
			synchronized (this)
			{
				unbindMap = new HashMap(m_attributes);
				m_attributes.clear();
			}

			// send unbind events
			for (Iterator i = unbindMap.entrySet().iterator(); i.hasNext();)
			{
				Map.Entry e = (Map.Entry) i.next();
				String name = (String) e.getKey();
				Object value = e.getValue();
				unBind(name, value);
			}
		}

		/**
		 * Mark the session as just accessed.
		 */
		protected void setAccessed()
		{
			m_accessed = System.currentTimeMillis();
		}

		/**
		 * @inheritDoc
		 */
		public void removeAttribute(String name)
		{
			// remove
			Object value = m_attributes.remove(name);

			// unbind event
			unBind(name, value);
		}

		/**
		 * @inheritDoc
		 */
		public void setAttribute(String name, Object value)
		{
			// treat a set to null as a remove
			if (value == null)
			{
				removeAttribute(name);
			}

			else
			{
				// add
				Object old = m_attributes.put(name, value);
	
				// bind event
				bind(name, value);
	
				// unbind event if old exiss
				if (old != null)
				{
					unBind(name, old);
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean equals(Object obj)
		{
			if (!(obj instanceof ToolSession))
			{
				return false;
			}

			return ((ToolSession) obj).getId().equals(getId());
		}

		/**
		 * {@inheritDoc}
		 */
		public int hashCode()
		{
			return getId().hashCode();
		}

		/**
		 * Unbind the value if it's a SessionBindingListener. Also does the HTTP unbinding if it's a HttpSessionBindingListener.
		 * 
		 * @param name
		 *        The attribute name bound.
		 * @param value
		 *        The bond value.
		 */
		protected void unBind(String name, Object value)
		{
			if (value instanceof SessionBindingListener)
			{
				SessionBindingEvent event = new MySessionBindingEvent(name, null, value);
				((SessionBindingListener) value).valueUnbound(event);
			}

			// also unbind any objects that are regular HttpSessionBindingListeners
			if (value instanceof HttpSessionBindingListener)
			{
				HttpSessionBindingEvent event = new HttpSessionBindingEvent(this, name, value);
				((HttpSessionBindingListener) value).valueUnbound(event);
			}
		}

		/**
		 * Bind the value if it's a SessionBindingListener. Also does the HTTP binding if it's a HttpSessionBindingListener.
		 * 
		 * @param name
		 *        The attribute name bound.
		 * @param value
		 *        The bond value.
		 */
		protected void bind(String name, Object value)
		{
			if (value instanceof SessionBindingListener)
			{
				SessionBindingEvent event = new MySessionBindingEvent(name, m_session, value);
				((SessionBindingListener) value).valueBound(event);
			}
			
			if (value instanceof HttpSessionBindingListener)
			{
				HttpSessionBindingEvent event = new HttpSessionBindingEvent(this, name, value);
				((HttpSessionBindingListener) value).valueBound(event);
			}
		}

		/**
		 * @inheritDoc
		 */
		public String getUserEid()
		{
			return m_session.getUserEid();
		}

		/**
		 * @inheritDoc
		 */
		public String getUserId()
		{
			return m_session.getUserId();
		}

		/**
		 * @inheritDoc
		 */
		public ServletContext getServletContext()
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * @inheritDoc
		 */
		public void setMaxInactiveInterval(int arg0)
		{
		// TODO: just ignore this ?
		}

		/**
		 * @inheritDoc
		 */
		public int getMaxInactiveInterval()
		{
			return m_session.getMaxInactiveInterval();
		}

		/**
		 * @inheritDoc
		 */
		public HttpSessionContext getSessionContext()
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * @inheritDoc
		 */
		public Object getValue(String arg0)
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * @inheritDoc
		 */
		public String[] getValueNames()
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * @inheritDoc
		 */
		public void putValue(String arg0, Object arg1)
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * @inheritDoc
		 */
		public void removeValue(String arg0)
		{
			throw new UnsupportedOperationException();
		}

		/**
		 * @inheritDoc
		 */
		public void invalidate()
		{
			clearAttributes();
			// TODO: cause to go away?
		}

		/**
		 * @inheritDoc
		 */
		public boolean isNew()
		{
			return false;
		}
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Maintenance
	 *********************************************************************************************************************************************************************************************************************************************************/

	protected class Maintenance implements Runnable
	{
		/** My thread running my timeout checker. */
		protected Thread m_maintenanceChecker = null;

		/** Signal to the timeout checker to stop. */
		protected boolean m_maintenanceCheckerStop = false;

		/**
		 * Construct.
		 */
		public Maintenance()
		{}

		/**
		 * Start the maintenance thread.
		 */
		public void start()
		{
			if (m_maintenanceChecker != null) return;

			m_maintenanceChecker = new Thread(this, "Sakai.SessionComponent.Maintenance");
			m_maintenanceCheckerStop = false;
			m_maintenanceChecker.start();
		}

		/**
		 * Stop the maintenance thread.
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
				catch (InterruptedException ignore)
				{}
				m_maintenanceChecker = null;
			}
		}

		/**
		 * Run the maintenance thread. Every m_checkEvery seconds, check for expired sessions.
		 */
		public void run()
		{
			while (!m_maintenanceCheckerStop)
			{
				try
				{
					for (Iterator i = m_sessions.values().iterator(); i.hasNext();)
					{
						MySession s = (MySession) i.next();
						if (M_log.isDebugEnabled()) M_log.debug("checking session " + s.getId());
						if (s.isInactive())
						{
							if (M_log.isDebugEnabled()) M_log.debug("invalidating session " + s.getId());
							s.invalidate();
						}
					}
				}
				catch (Throwable e)
				{
					M_log.warn("run(): exception: " + e);
				}
				finally
				{}

				// cycle every REFRESH seconds
				if (!m_maintenanceCheckerStop)
				{
					try
					{
						Thread.sleep(m_checkEvery * 1000L);
					}
					catch (Exception ignore)
					{}
				}
			}
		}
	}
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/kernel/session-component/src/java/org/sakaiproject/component/kernel/session/SessionComponent.java,v 1.20 2005/05/30 03:39:09 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
