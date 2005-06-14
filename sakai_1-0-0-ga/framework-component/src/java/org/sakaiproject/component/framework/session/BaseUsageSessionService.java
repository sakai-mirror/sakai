/**********************************************************************************
*
* $Header: /cvs/sakai/framework-component/src/java/org/sakaiproject/component/framework/session/BaseUsageSessionService.java,v 1.27 2004/10/01 19:29:26 ggolden.umich.edu Exp $
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.sakaiproject.service.framework.config.ServerConfigurationService;
import org.sakaiproject.service.framework.current.CurrentService;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.framework.session.SessionStateBindingListener;
import org.sakaiproject.service.framework.session.UsageSession;
import org.sakaiproject.service.framework.session.UsageSessionService;
import org.sakaiproject.service.legacy.id.IdService;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.time.TimeService;
import org.sakaiproject.util.Setup;

/**
* <p>BaseUsageSessionService implements the UsageSessionService for Sakai, all but the Storage module.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.27 $
*/
public abstract class BaseUsageSessionService implements UsageSessionService
{
	/** Storage manager for this service. */
	protected Storage m_storage = null;

	/** Store the session in the http session under this key. */
	protected final String M_sessionKey = this.getClass().getName();

	/*******************************************************************************
	* Abstractions, etc.
	*******************************************************************************/

	/**
	 * Construct storage for this service.
	 */
	protected abstract Storage newStorage();

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

	/** Dependency: CurrentService. */
	protected CurrentService m_currentService = null;

	/**
	 * Dependency: CurrentService.
	 * @param service The CurrentService.
	 */
	public void setCurrentService(CurrentService service)
	{
		m_currentService = service;
	}

	/** Dependency: IdService. */
	protected IdService m_idService = null;

	/**
	 * Dependency: IdService.
	 * @param service The IdService.
	 */
	public void setIdService(IdService service)
	{
		m_idService = service;
	}

	/** Dependency: TimeService. */
	protected TimeService m_timeService = null;

	/**
	 * Dependency: TimeService.
	 * @param service The TimeService.
	 */
	public void setTimeService(TimeService service)
	{
		m_timeService = service;
	}

	/** How long an inactive session can last before it's timed out. */
	protected int m_sessionInactiveTimeoutMs = 1800 * 1000;

	/**
	 * Configuration: set how long an inactive session can last before it's timed out.
	 */
	public void setSessionInactiveTimeoutSeconds(String value)
	{
		try
		{
			m_sessionInactiveTimeoutMs = Integer.parseInt(value) * 1000;
		}
		catch (Exception ignore)
		{
		}
	}

	/** How long a non-communicating session can last before it's timed out. */
	protected int m_sessionLostTimeoutMs = 30 * 1000;

	/**
	 * Configuration: set how long a non-communicating session can last before it's timed out.
	 */
	public void setSessionLostTimeoutSeconds(String value)
	{
		try
		{
			m_sessionLostTimeoutMs = Integer.parseInt(value) * 1000;
		}
		catch (Exception ignore)
		{
		}
	}

	/*******************************************************************************
	* Init and Destroy
	*******************************************************************************/

	public BaseUsageSessionService()
	{
		m_storage = newStorage();
	}

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		try
		{
			// open storage
			m_storage.open();

			try
			{
				m_sessionInactiveTimeoutMs =
						Integer.parseInt(m_serverConfigurationService.getString("session.inactive")) * 1000;
			}
			catch (Exception ignore)
			{
			}

			try
			{
				m_sessionLostTimeoutMs =
						Integer.parseInt(m_serverConfigurationService.getString("session.lost")) * 1000;
			}
			catch (Exception ignore)
			{
			}

			m_logger.info(this +".init(): session inactive: " + (m_sessionInactiveTimeoutMs/1000) + " session lost: " + (m_sessionLostTimeoutMs/1000));
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
		m_storage.close();

		m_logger.info(this +".destroy()");
	}

	/*******************************************************************************
	* UsageSessionService implementation
	*******************************************************************************/

	/**
	 * @inheritdoc
	 */
	public UsageSession startSession(String userId, String remoteAddress, String userAgent)
	{
		// do we have an http session "current"?
		HttpSession hsession = (HttpSession) m_currentService.getInThread(HttpSession.class.getName());
		if (hsession != null)
		{
			// do we already have a session in the hsession?
			try
			{
				UsageSession session = (UsageSession) hsession.getAttribute(M_sessionKey);
				if (session != null)
				{
					m_logger.warn(this + ".addSession: already have a session");
					return session;
				}
			
				// create the session and bind it to the http session
				session = new BaseUsageSession(m_serverConfigurationService.getServerIdInstance(), userId, remoteAddress, userAgent);
				hsession.setAttribute(M_sessionKey, session);

				// store
				m_storage.addSession(session);

				return session;
			}
			catch (IllegalStateException e)
			{
				// session is invalidated already
				return null;
			}
		}

		// if we are running with no hsession, bind the user id only (not a full session) to the current service (we can replace what's there)
		m_currentService.setInThread(M_sessionKey, userId);
		return null;
	}

	/**
	 * {@inheritdoc}
	 */
	public UsageSession closeSession()
	{
		// do we have an http session "current"?
		HttpSession hsession = (HttpSession) m_currentService.getInThread(HttpSession.class.getName());
		if (hsession != null)
		{
			// do we have a session in the hsession?
			try
			{
				BaseUsageSession session = (BaseUsageSession) hsession.getAttribute(M_sessionKey);
				if (session == null)
				{
					m_logger.warn(this + ".closeSession: no session.");
					return null;
				}

				// close the session
				session.close();

				return session;
			}
			catch (IllegalStateException e)
			{
				// session is invalidated already
				return null;
			}
		}
		
		// if we are running with no hsession, unbind the user id from the current service
		m_currentService.setInThread(M_sessionKey, null);
		return null;
	}

	/**
	 * {@inheritdoc}
	 */
	public UsageSession getSession()
	{
		UsageSession rv = null;

		// do we have an http session "current"?
		HttpSession hsession = (HttpSession) m_currentService.getInThread(HttpSession.class.getName());
		if (hsession != null)
		{
			try
			{
				// do we have a session in the hsession?
				rv = (BaseUsageSession) hsession.getAttribute(M_sessionKey);
			}
			catch (IllegalStateException e)
			{
				return null;
			}
		}
		
		return rv;
	}

	/**
	 * @inheritdoc
	 */
	public String getSessionUserId()
	{
		String rv = null;

		// do we have an http session "current"?
		HttpSession hsession = (HttpSession) m_currentService.getInThread(HttpSession.class.getName());
		if (hsession != null)
		{
			try
			{
				// do we have a session in the hsession?
				UsageSession session = (UsageSession) hsession.getAttribute(M_sessionKey);
				if (session != null)
				{
					// get the session's user id
					rv = session.getUserId();
				}
			}
			catch (IllegalStateException e)
			{
				return null;
			}			
		}

		// if no user id yet, try a current service user id
		if (rv == null)
		{
			rv = (String) m_currentService.getInThread(M_sessionKey);
		}

		// may be null, which indicates that there's no user id available
		return rv;
	}

	/**
	 * @inheritdoc
	 */
	public String getSessionId()
	{
		String rv = null;

		// do we have an http session "current"?
		HttpSession hsession = (HttpSession) m_currentService.getInThread(HttpSession.class.getName());
		if (hsession != null)
		{
			try
			{
				// do we have a session in the hsession?
				UsageSession session = (UsageSession) hsession.getAttribute(M_sessionKey);
				if (session != null)
				{
					// get the session's user id
					rv = session.getId();
				}
			}
			catch (IllegalStateException e)
			{
				return null;
			}
		}

		// may be null, which indicates that there's no session
		return rv;
	}

	/**
	 * @inheritdoc
	 */
	public SessionState getSessionState(String key)
	{
		// do we have an http session "current"?
		HttpSession hsession = (HttpSession) m_currentService.getInThread(HttpSession.class.getName());
		if (hsession != null)
		{
			try
			{
				// the key we use
				String hKey = M_sessionKey + "." + key;

				// do we have a state already bound with this key in the hsession?
				SessionState state = (SessionState) hsession.getAttribute(hKey);

				// if not, make one and bind it
				if (state == null)
				{
					state = new BaseSessionState(key);
					hsession.setAttribute(hKey, state);
				}
			
				return state;
			}
			catch (IllegalStateException e)
			{
				return null;
			}
		}
				
		return null;
	}

	/**
	 * @inheritdoc
	 */
	public UsageSession setSessionActive(boolean auto)
	{
		BaseUsageSession session = (BaseUsageSession) getSession();
		if (session == null) return null;
		
		if (session.isClosed()) return session;

		if (auto)
		{
			// do not mark the current session as having user activity
			// but close it if it's timed out from no user activity
			if (session.isInactive())
			{
				session.close();
			}
		}
		else
		{
			// mark the current session as having user activity
			session.setActivity();
		}

		return session;
	}

	/**
	 * @inheritdoc
	 */
	public UsageSession getSession(String id)
	{		
		UsageSession rv = m_storage.getSession(id);
		
		return rv;
	}

	/**
	 * @inheritdoc
	 */
	public List getSessions(List ids)
	{
		List rv = m_storage.getSessions(ids);

		return rv;
	}

	/**
	 * @inheritdoc
	 */
	public List getSessions(String criteria, Object[] values)
	{
		List rv = m_storage.getSessions(criteria, values);
		
		return rv;
	}

	/**
	 * @inheritdoc
	 */
	public int getSessionInactiveTimeout()
	{
		return m_sessionInactiveTimeoutMs / 1000;
	}

	/**
	 * @inheritdoc
	 */
	public int getSessionLostTimeout()
	{
		return m_sessionLostTimeoutMs / 1000;
	}

	/**
	 * @inheritdoc
	 */
	public List getOpenSessions()
	{
		return m_storage.getOpenSessions();
	}

	/**
	 * @inheritdoc
	 */
	public Map getOpenSessionsByServer()
	{
		List all = m_storage.getOpenSessions();
		
		Map byServer = new TreeMap();

		List current = null;
		String key = null;
		
		for (Iterator i = all.iterator(); i.hasNext();)
		{
			UsageSession s = (UsageSession) i.next();
			
			// to start, or when the server changes, create a new inner list and add to the map
			if ((key == null) || (!key.equals(s.getServer())))
			{
				key = s.getServer();
				current = new Vector();
				byServer.put(key, current);
			}

			current.add(s);
		}
		
		return byServer;
	}

	/*******************************************************************************
	* Storage
	*******************************************************************************/

	protected interface Storage
	{
		/**
		 * Open.
		 */
		void open();

		/**
		 * Close.
		 */
		void close();

		/**
		 * Take this session into storage.
		 * @param session The usage session.
		 */
		void addSession(UsageSession session);

		/**
		 * Access a session by id
		 * @param id The session id.
		 * @return The session object.
		 */
		UsageSession getSession(String id);

		/**
		 * Access a bunch of sessions by the List id session ids.
		 * @param ids The session id List.
		 * @return The List (UsageSession) of session objects for these ids.
		 */
		List getSessions(List ids);

		/**
		 * Access a List of usage sessions by *arbitrary criteria*.
		 * @param criteria A string with meaning known to the particular implementation of the API running.
		 * @param fields Optional values to go with the criteria in an implementation specific way.
		 * @return The List (UsageSession) of UsageSession object for these ids.
		 */
		List getSessions(String criteria, Object[] values);

		/**
		 * This session is now closed.
		 * @param session The session which is closed.
		 */
		void closeSession(UsageSession session);
		
		/**
		 * Access a list of all open sessions.
		 * @return a List (UsageSession) of all open sessions, ordered by server, then by start (asc)
		 */
		List getOpenSessions();
	}

	/*******************************************************************************
	* UsageSession
	*******************************************************************************/

	protected class BaseUsageSession implements UsageSession, HttpSessionBindingListener
	{
		/** The user id for this session. */
		protected String m_user = null;

		/** The unique id for this session. */
		protected String m_id = null;

		/** The server which is hosting the session. */
		protected String m_server = null;

		/** The IP Address from which this session originated. */
		protected String m_ip = null;

		/** The User Agent string describing the browser used in this session. */
		protected String m_userAgent = null;

		/** The BrowserID string describing the browser used in this session. */
		protected String m_browserId = null;

		/** The time the session was started */
		protected Time m_start = null;

		/** The time the session was closed. */
		protected Time m_end = null;

		/** The time to close the session (move forward with each activity setting). */
		protected long m_timeToClose = 0;

		/**
		 * Construct.
		 * @param server The server id which is hosting the session.
		 * @param user The user id for this session.
		 * @param address The IP Address from which this session originated.
		 * @param agent The User Agent string describing the browser used in this session.
		 */
		public BaseUsageSession(String server, String user, String address, String agent)
		{
			m_id = m_idService.getUniqueId();
			m_server = server;
			m_user = user;
			m_ip = address;
			m_userAgent = agent;
			m_start = m_timeService.newTime();
			m_end = m_start;
			setBrowserId(agent);
			m_timeToClose = System.currentTimeMillis() + m_sessionInactiveTimeoutMs;
		}

		/**
		 * Construct fully.
		 * @param id The session id.
		 * @param server The server id which is hosting the session.
		 * @param user The user id for this session.
		 * @param address The IP Address from which this session originated.
		 * @param agent The User Agent string describing the browser used in this session.
		 */
		public BaseUsageSession(String id, String server, String user, String address, String agent, Time start, Time end)
		{
			m_id = id;
			m_server = server;
			m_user = user;
			m_ip = address;
			m_userAgent = agent;
			m_start = start;
			m_end = end;
			setBrowserId(agent);
			m_timeToClose = System.currentTimeMillis() + m_sessionInactiveTimeoutMs;
		}

		/**
		 * Set the browser id for this session, decoded from the user agent string.
		 * @param agent The user agent string.
		 */
		protected void setBrowserId(String agent)
		{
			if (agent == null)
			{
				m_browserId = UNKNOWN;
			}

			// test whether agent is UserAgent value for a known browser.
			// should we also check version number?
			else if (agent.indexOf("Netscape") >= 0 && agent.indexOf("Mac") >= 0)
			{
				m_browserId = MAC_NN;
			}
			else if (agent.indexOf("Netscape") >= 0 && agent.indexOf("Windows") >= 0)
			{
				m_browserId = WIN_NN;
			}
			else if (agent.indexOf("MSIE") >= 0 && agent.indexOf("Mac") >= 0)
			{
				m_browserId = MAC_IE;
			}
			else if (agent.indexOf("MSIE") >= 0 && agent.indexOf("Windows") >= 0)
			{
				m_browserId = WIN_IE;
			}
			else if (agent.indexOf("Camino") >= 0 && agent.indexOf("Macintosh") >= 0)
			{
				m_browserId = MAC_CM;
			}
			else if (agent.startsWith("Mozilla") && agent.indexOf("Windows") >= 0)
			{
				m_browserId = WIN_MZ;
			}
			else if (agent.startsWith("Mozilla") && agent.indexOf("Macintosh") >= 0)
			{
				m_browserId = MAC_MZ;
			}
			else
			{
				m_browserId = UNKNOWN;
			}
		}

		/**
		 * Close the session.
		 */
		protected void close()
		{
			if (!isClosed())
			{
				m_end = m_timeService.newTime();
				m_storage.closeSession(this);
			}
		}

		/**
		 * @inheritdoc
		 */
		public boolean isClosed()
		{
			return (!(m_end.equals(m_start)));

		} // isClosed

		/**
		 * @inheritdoc
		 */
		public String getUserId()
		{
			return m_user;
		}

		/**
		 * @inheritdoc
		 */
		public String getId()
		{
			return m_id;
		}

		/**
		 * @inheritdoc
		 */
		public String getServer()
		{
			return m_server;
		}

		/**
		 * @inheritdoc
		 */
		public String getIpAddress()
		{
			return m_ip;
		}

		/**
		 * @inheritdoc
		 */
		public String getUserAgent()
		{
			return m_userAgent;
		}

		/**
		 * @inheritdoc
		 */
		public String getBrowserId()
		{
			return m_browserId;
		}

		/**
		 * @inheritdoc
		 */
		public Time getStart()
		{
			return m_timeService.newTime(m_start.getTime());

		} // getStart

		/**
		 * @inheritdoc
		 */
		public Time getEnd()
		{
			return m_timeService.newTime(m_end.getTime());

		} // getEnd

		/**
		 * There's new user activity now.
		 */
		protected void setActivity()
		{
			m_timeToClose = System.currentTimeMillis() + m_sessionInactiveTimeoutMs;
		}

		/**
		 * Has this session gone inactive?
		 * @return True if the session has seen no activity in the last timeout period, false if it's still active.
		 */
		protected boolean isInactive()
		{
			return m_timeToClose < System.currentTimeMillis();
		}

		/**
		 * @inheritdoc
		 */
		public void valueBound(HttpSessionBindingEvent hsbe)
		{
		}

		/**
		 * @inheritdoc
		 */
		public void valueUnbound(HttpSessionBindingEvent hsbe)
		{
			try
			{
				m_currentService.startThread("SESSION UNBIND");
	
				// if we didn't close this already, generate the event (with this session) and close
				if (!isClosed())
				{
					Setup.logoutEvent(this);
					close();
				}
			}
			finally
			{
				// clear out any current access bindings
				m_currentService.clearInThread();
			}
		}

		/**
		 * @inheritdoc
		 */
		public int compareTo(Object obj)
		{
			if (!(obj instanceof UsageSession))
				throw new ClassCastException();

			// if the object are the same, say so
			if (obj == this)
				return 0;

			// start the compare by comparing their users
			int compare = getUserId().compareTo(((UsageSession) obj).getUserId());

			// if these are the same
			if (compare == 0)
			{
				// sort based on (unique) id
				compare = getId().compareTo(((UsageSession) obj).getId());
			}

			return compare;
		}

		/**
		 * @inheritdoc
		 */
		public String toString()
		{
			return "["
				+ ((m_id == null) ? "" : m_id)
				+ " | "
				+ ((m_server == null) ? "" : m_server)
				+ " | "
				+ ((m_user == null) ? "" : m_user)
				+ " | "
				+ ((m_ip == null) ? "" : m_ip)
				+ " | "
				+ ((m_userAgent == null) ? "" : m_userAgent)
				+ " | "
				+ m_start.toStringGmtFull()
				+ " ]";
		}
	}


	/*******************************************************************************
	* SessionState
	*******************************************************************************/

	/**
	 * Store the Map for the state, and listen for HttpSessionBinding events
	 */
	protected class BaseSessionState implements SessionState, HttpSessionBindingListener
	{
		/** Map of attributes. */
		protected Map m_map = null;

		/** The key to this session state attribute set. */
		protected String m_key = null;

		/**
		 * Construct.
		 * @param key The state key.
		 * @param map The map to hold.
		 */
		public BaseSessionState(String key)
		{
			m_key = key;
			m_map = new HashMap();
		}

		/**
		 * {@inheritdoc}
		 */
		public Object getAttribute(String name)
		{
			synchronized (m_map)
			{
				return m_map.get(name);
			}
		}

		/**
		 * {@inheritdoc}
		 */
		public void setAttribute(String name, Object value)
		{
			Object old = null;
			synchronized (m_map)
			{
				old = m_map.get(name);

				if (value == null)
				{
					m_map.remove(name);
				}
			
				else
				{
					m_map.put(name, value);
				}
			}

			if (old != null)
			{
				unBindAttributeValue(name, old);
			}
			
			if (value != null)
			{
				bindAttributeValue(name, value);
			}
		}

		/**
		 * {@inheritdoc}
		 */
		public void removeAttribute(String name)
		{
			Object old = null;
			
			synchronized (m_map)
			{
				old = m_map.get(name);
				m_map.remove(name);
			}

			if (old != null)
			{
				unBindAttributeValue(name, old);
			}
		}

		/**
		 * {@inheritdoc}
		 */
		public void clear()
		{
			Map old = new HashMap();

			synchronized (m_map)
			{
				old.putAll(m_map);

				m_map.clear();
			}

			for (Iterator i = old.entrySet().iterator(); i.hasNext();)
			{
				Map.Entry entry = (Map.Entry) i.next();
				Object value = entry.getValue();
				String name = (String)entry.getKey();
				unBindAttributeValue(name, value);
			}
		}

		/**
		 * Access a List of all names of attributes stored in the SessionState.
		 * @return A List of all names of attribute stored in the SessionState.
		 */
		public List getAttributeNames()
		{
			synchronized (m_map)
			{
				List rv = new Vector();
				rv.addAll(m_map.keySet());
				
				return rv;
			}
		}

		/**
		 * We don't care about when we are bound...
		 */
		public void valueBound(HttpSessionBindingEvent hsbe)
		{
		}

		/**
		 * When we are unbound, unbind our state's (map's) attributes
		 */
		public void valueUnbound(HttpSessionBindingEvent hsbe)
		{
			try
			{
				m_currentService.startThread("STATE UNBIND");

				// notify all attribute and clear the state
				clear();
			}
			finally
			{
				// clear out any current access bindings
				m_currentService.clearInThread();
			}
		}

		/**
		 * If the object is a SessionStateBindingListener, unbind it
		 * @param attributeName The attribute name.
		 * @param attribute The attribute object
		 */
		protected void unBindAttributeValue(String attributeName, Object attribute)
		{
			// if this object wants session binding notification
			if ((attribute != null) && (attribute instanceof SessionStateBindingListener))
			{
				try
				{
					((SessionStateBindingListener) attribute).valueUnbound(m_key, attributeName);
					m_logger.debug("unbinding: " + attribute + " name: " + attributeName + " state: " + m_key);
				}
				catch (Throwable e)
				{
					m_logger.warn("BaseStateService.unBindAttributeValue: unbinding exception: ", e);
				}
			}
		}

		/**
		 * If the object is a SessionStateBindingListener, bind it
		 * @param attributeName The attribute name.
		 * @param attribute The attribute object
		 */
		protected void bindAttributeValue(String attributeName, Object attribute)
		{
			// if this object wants session binding notification
			if ((attribute != null) && (attribute instanceof SessionStateBindingListener))
			{
				try
				{
					((SessionStateBindingListener) attribute).valueBound(m_key, attributeName);
				}
				catch (Throwable e)
				{
					m_logger.warn("BaseStateService.bindAttributeValue: unbinding exception: ", e);
				}
			}
		}
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/framework-component/src/java/org/sakaiproject/component/framework/session/BaseUsageSessionService.java,v 1.27 2004/10/01 19:29:26 ggolden.umich.edu Exp $
*
**********************************************************************************/
