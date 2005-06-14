/**********************************************************************************
*
* $Header: /cvs/sakai/framework-component/src/java/org/sakaiproject/component/framework/session/BasicUsageSessionService.java,v 1.8 2004/11/25 02:25:51 janderse.umich.edu Exp $
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.sakaiproject.service.framework.session.UsageSession;
import org.sakaiproject.service.legacy.time.Time;

/**
* <p>BasicUsageSessionService is a CHEF usage session service that is stand-alone and an authority of ALL
* possible sessions.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.8 $
*/
public class BasicUsageSessionService
	extends BaseUsageSessionService
{	
	/*******************************************************************************
	* Abstractions, etc.
	*******************************************************************************/

	/**
	* Construct storage for this service.
	*/
	protected Storage newStorage() { return new BasicStorage(); }

	/*******************************************************************************
	* Storage
	*******************************************************************************/

	protected class BasicStorage
		implements Storage
	{
		/** The sessions, keyed by id. */
		protected Hashtable m_sessions = new Hashtable();

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
			m_sessions.clear();
		}	// close

		/**
		* Take this session into storage.
		* @param session The usage session.
		*/
		public void addSession(UsageSession session)
		{
			m_sessions.put(session.getId(), session);

		}	// addSession

		/**
		* Access a session by id
		* @param id The session id.
		* @return The session object.
		*/
		public UsageSession getSession(String id)
		{
			return (UsageSession) m_sessions.get(id);

		}	// getSession

		/**
		 * @inheritDoc
		 */
		public List getSessions(List ids)
		{
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
			// bad news! we don't support this!
			m_logger.warn(this + ".getSessions(criteria, values) called, not supported");
			return null;
		}

		/**
		* This session is now closed.
		* @param session The session which is closed.
		*/
		public void closeSession(UsageSession session)
		{
			// remove it
			m_sessions.remove(session.getId());
		}

		/**
		 * Access a list of all open sessions.
		 * @return a List (UsageSession) of all open sessions, ordered by server, then by start (asc)
		 */
		public List getOpenSessions()
		{
			// get them all - we only hold onto open sessions
			List rv = new Vector();
			rv.addAll(m_sessions.values());

			// just need to sort by start...
			Collections.sort(rv,
					new Comparator()
					{
						public int compare(Object o1, Object o2)
						{
							// if the same object
							if (o1 == o2) return 0;
		
							// assume they are UsageSessions
							UsageSession u1 = (UsageSession) o1;
							UsageSession u2 = (UsageSession) o2;
		
							// get each one's start
							Time t1 = u1.getStart();
							Time t2 = u2.getStart();
		
							// compare based on date
							int compare = t1.compareTo(t2);
		
							return compare;
						}
					}
				);

			return rv;
		}

	}   // Storage

}   // BaseUsageSessionService

/**********************************************************************************
*
* $Header: /cvs/sakai/framework-component/src/java/org/sakaiproject/component/framework/session/BasicUsageSessionService.java,v 1.8 2004/11/25 02:25:51 janderse.umich.edu Exp $
*
**********************************************************************************/
