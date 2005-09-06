/**********************************************************************************
* $URL$
* $Id$
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

package org.sakaiproject.service.framework.session;

import java.util.List;
import java.util.Map;

/**
 * <p>UsageSessionService keeps track of usage sessions.</p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public interface UsageSessionService
{
	/** Name for the event of logging in. */
	public static final String EVENT_LOGIN = "user.login";

	/** Name for the event of logging out. */
	public static final String EVENT_LOGOUT = "user.logout";
	
	/** Name for the global cookie to store the session */
	public static final String SAKAI_SESSION_COOKIE = "sakai.session";
	
	/** Name for the session key to retrieve the UsageSession */
	/** Note: This must be a constant and not based on classname - it must
	 * stay the same regardless of the name of the implementing class. */
	public static final String USAGE_SESSION_KEY = "org.sakaiproject.service.framework.session.UsageSessionService";

	/**
	 * Establish a usage session associated with the current request or thread.
	 * @param userId The user id.
	 * @param remoteAddress The IP address from the user is making a request.
	 * @param userAgent The string describing the user's browser.
	 * @return The new UsageSession.
	 */
	UsageSession startSession(String userId, String remoteAddress, String userAgent);

	/**
	 * Access the usage session associated with the current request or thread.
	 * @return The UsageSession object holding the information about this session.
	 */
	UsageSession getSession();

	/**
	 * Access the user id from the usage session associated with the current request or thread.
	 * @return The user id from the usage session associated with the current request or thread, or null if there is none.
	 */
	String getSessionUserId();

	/**
	 * Access the session id from the usage session associated with the current request or thread, or null if no session.
	 * @return The session id from the usage session associated with the current request or thread, or null if no session.
	 */
	String getSessionId();

	/**
	 * Access a SessionState object with the given key associated with the current usage session.
	 * @param key The SessionState key.
	 * @return an SessionState object with the given key.
	 */
	SessionState getSessionState(String key);

	/**
	 * Indicate recent user activity on the current usage session - user initiated or auto.
	 * Maintains the initiated activity timeout mechanism.
	 * @param auto if true, activity from an automatic event, otherwise from a user initiated event.
	 * @return The current usage session (may be just closed).
	 */
	UsageSession setSessionActive(boolean auto);

	/**
	 * Access a usage session (may be other than the current one) by id.
	 * @param id the Session id.
	 * @return The UsageSession object for this id, or null if not defined.
	 */
	UsageSession getSession(String id);

	/**
	 * Access a List of usage sessions by List of ids.
	 * @param ids the List (String) of Session ids.
	 * @return The List (UsageSession) of UsageSession object for these ids.
	 */
	List getSessions(List ids);

	/**
	 * Access a List of usage sessions by *arbitrary criteria* for te session ids.
	 * @param criteria A string with meaning known to the particular implementation of the API running.
	 * @param fields Optional values to go with the criteria in an implementation specific way.
	 * @return The List (UsageSession) of UsageSession object for these ids.
	 */
	List getSessions(String criteria, Object[] values);

	/**
	 * Access the time (seconds) we will wait for any user generated request from a session
	 * before we consider the session inactive.
	 * @return the time (seconds) used for session inactivity detection.
	 */
	int getSessionInactiveTimeout();

	/**
	 * Access the time (seconds) we will wait for hearing anyting from a session before we consider
	 * the session lost.
	 * @return the time (seconds) used for lost session detection.
	 */
	int getSessionLostTimeout();

	/**
	 * Access a list of all open sessions.
	 * @return a List (UsageSession) of all open sessions, ordered by server, then by start (asc)
	 */
	List getOpenSessions();

	/**
	 * Access a list of all open sessions, grouped by server.
	 * @return a Map (server id -> List (UsageSession)) of all open sessions, ordered by server, then by start (asc)
	 */
	Map getOpenSessionsByServer();
}



