/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/presence/StandalonePresenceService.java,v 1.5 2004/09/30 20:20:58 ggolden.umich.edu Exp $
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
package org.sakaiproject.component.legacy.presence;

// imports
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
* <p>StandalonePresenceService extends the BasePresenceService with a Storage model that keeps track of presence
* for a single, stand-alone app server running CHEF, by keeping presence stored in memory.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.5 $
*/
public class StandalonePresenceService extends BasePresenceService
{
	/**
	* Allocate a new storage object.
	* @return A new storage object.
	*/
	protected Storage newStorage()
	{
		return new StandaloneStorage();

	} // newStorage

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
		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}

	} // init

	/*******************************************************************************
	* Storage
	*******************************************************************************/

	protected class StandaloneStorage implements Storage
	{
		/** Map of locationId to (Set of sessionIds). */
		protected Map m_locations = new HashMap();

		/**
		 * {@inheritdoc}
		 */
		public void setPresence(String sessionId, String locationId)
		{
			// make sure locationId is being tracked
			Set sessions = (Set) m_locations.get(locationId);
			if (sessions == null)
			{
				synchronized (m_locations)
				{
					sessions = (Set) m_locations.get(locationId);
					if (sessions == null)
					{
						sessions = new HashSet();
						m_locations.put(locationId, sessions);
					}
				}
			}

			// make sure that this sessionId is in there for this locationId
			synchronized (sessions)
			{
				if (!sessions.contains(sessionId))
				{
					sessions.add(sessionId);
				}
			}

		} // setPresence

		/**
		 * {@inheritdoc}
		 */
		public void removePresence(String sessionId, String locationId)
		{
			// get the collection of sessions for this location
			Set sessions = (Set) m_locations.get(locationId);
			if (sessions == null) return;

			// remove this one
			synchronized (sessions)
			{
				if (sessions.contains(sessionId))
				{
					sessions.remove(sessionId);
				}
			}

			// if there are no sessions left at the location, remove the location
			synchronized (m_locations)
			{
				if (sessions.isEmpty())
				{
					m_locations.remove(locationId);
				}
			}

		} // removePresence

		/**
		 * {@inheritdoc}
		 */
		public List getSessions(String locationId)
		{
			List sessionIdsList = new Vector();

			// get the collection of sessions for this location
			Set sessionIdsSet = (Set) m_locations.get(locationId);
			if (sessionIdsSet == null)
				return sessionIdsList;

			sessionIdsList.addAll(sessionIdsSet);

			// get the UsageSession objects for these ids
			List sessions = m_usageSessionService.getSessions(sessionIdsList);

			return sessions;

		} // getSessions

		public List getLocations()
		{
			List rv = new Vector();
			rv.addAll(m_locations.keySet());

			return rv;

		} // getLocations

	} // StandaloneStorage

} // StandalonePresenceService

/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/presence/StandalonePresenceService.java,v 1.5 2004/09/30 20:20:58 ggolden.umich.edu Exp $
*
**********************************************************************************/
