/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/presence/ClusterPresenceService.java,v 1.10 2004/09/30 20:21:42 ggolden.umich.edu Exp $
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
import java.util.List;

import org.sakaiproject.service.framework.sql.SqlService;

/**
* <p>ClusterPresenceService extends the BasePresenceService with a Storage model that keeps track of presence
* for a cluster of CHEF app servers, backed by a shared DB table.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.10 $
*/
public class ClusterPresenceService
	extends BasePresenceService
{
	/**
	* Allocate a new storage object.
	* @return A new storage object.
	*/
	protected Storage newStorage()
	{
		return new ClusterStorage();

	}	// newStorage

	/** Dependency: SqlService */
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
	* Storage
	*******************************************************************************/

	protected class ClusterStorage
		implements Storage
	{
		/**
		 * {@inheritdoc}
		 */
		public void setPresence(String sessionId, String locationId)
		{
			// send this to the database
			String statement =
					"insert into CHEF_PRESENCE (SESSION_ID,LOCATION_ID) values ( ?, ?)";

			// collect the fields
			Object fields[] = new Object[2];
			fields[0] = sessionId;
			fields[1] = locationId;

			// process the insert
			boolean ok = m_sqlService.dbWrite(statement, fields);
			if (!ok)
			{
				m_logger.warn(this + ".setPresence(): dbWrite failed");
			}

		}	// setPresence

		/**
		 * {@inheritdoc}
		 */
		public void removePresence(String sessionId, String locationId)
		{
			// form the SQL delete statement
			String statement =
					"delete from CHEF_PRESENCE"
					+   " where ( SESSION_ID = ? and LOCATION_ID = ?)";

			// setup the fields
			Object[] fields = new Object[1];
			fields[0] = sessionId;

			// process the remove
			boolean ok = m_sqlService.dbWrite(statement, fields, locationId);
			if (!ok)
			{
				m_logger.warn(this + ".removePresence(): dbWrite failed");
			}

		}	// removePresence

		/**
		 * {@inheritdoc}
		 */
		public List getSessions(String locationId)
		{
			// TODO: Note: this assumes
			//		1) the UsageSessionService has a db component selected.
			//		2) the presence table and the session table are in the same db.

			// form a SQL query to select session ids for this location
			String statement =
					"select SESSION_ID from CHEF_PRESENCE where LOCATION_ID = ?";

			// send in the locationId
			Object[] fields = new Object[1];
			fields[0] = locationId;

			// get these from usage session
			List sessions = m_usageSessionService.getSessions(statement, fields);

			return sessions;

		}	// getSessions

		/**
		 * {@inheritdoc}
		 */
		public List getLocations()
		{
			// form the SQL query
			String statement =
					"select DISTINCT LOCATION_ID from CHEF_PRESENCE";

			List locs = m_sqlService.dbRead(statement);
			
			return locs;

		}	// getLocations

	}	// ClusterStorage

}	// ClusterPresenceService

/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/presence/ClusterPresenceService.java,v 1.10 2004/09/30 20:21:42 ggolden.umich.edu Exp $
*
**********************************************************************************/
