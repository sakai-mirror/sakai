/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/legacy/presence/ClusterPresenceService.java,v 1.1 2005/04/06 02:42:37 ggolden.umich.edu Exp $
*
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
package org.sakaiproject.component.legacy.presence;

// imports
import java.util.List;

import org.sakaiproject.service.framework.sql.SqlService;

/**
* <p>ClusterPresenceService extends the BasePresenceService with a Storage model that keeps track of presence
* for a cluster of CHEF app servers, backed by a shared DB table.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.1 $
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
			// if we are auto-creating our schema, check and create
			if (m_autoDdl)
			{
				m_sqlService.ddl(this.getClass().getClassLoader(), "sakai_presence");
			}

			super.init();
		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}
	}

	/*******************************************************************************
	* Storage
	*******************************************************************************/

	protected class ClusterStorage
		implements Storage
	{
		/**
		 * {@inheritDoc}
		 */
		public void setPresence(String sessionId, String locationId)
		{
			// send this to the database
			String statement =
					"insert into SAKAI_PRESENCE (SESSION_ID,LOCATION_ID) values ( ?, ?)";

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
		 * {@inheritDoc}
		 */
		public void removePresence(String sessionId, String locationId)
		{
			// form the SQL delete statement
			String statement =
					"delete from SAKAI_PRESENCE"
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
		 * {@inheritDoc}
		 */
		public List getSessions(String locationId)
		{
			// TODO: Note: this assumes
			//		1) the UsageSessionService has a db component selected.
			//		2) the presence table and the session table are in the same db.

			// form a SQL query to select session ids for this location
			String statement =
					"select SESSION_ID from SAKAI_PRESENCE where LOCATION_ID = ?";

			// send in the locationId
			Object[] fields = new Object[1];
			fields[0] = locationId;

			// get these from usage session
			List sessions = m_usageSessionService.getSessions(statement, fields);

			return sessions;

		}	// getSessions

		/**
		 * {@inheritDoc}
		 */
		public List getLocations()
		{
			// form the SQL query
			String statement =
					"select DISTINCT LOCATION_ID from SAKAI_PRESENCE";

			List locs = m_sqlService.dbRead(statement);
			
			return locs;

		}	// getLocations

	}	// ClusterStorage

}	// ClusterPresenceService

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/legacy/presence/ClusterPresenceService.java,v 1.1 2005/04/06 02:42:37 ggolden.umich.edu Exp $
*
**********************************************************************************/
