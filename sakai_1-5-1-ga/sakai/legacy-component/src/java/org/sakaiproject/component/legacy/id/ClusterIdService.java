/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/id/ClusterIdService.java,v 1.9 2005/01/18 22:23:49 janderse.umich.edu Exp $
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
package org.sakaiproject.component.legacy.id;

// imports
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.sakaiproject.service.legacy.id.IdService;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.sql.SqlService;

/**
* <p>ClusterIdService implements the IdService with sequence in the cluster database.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.9 $
*/
public class ClusterIdService implements IdService
{
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

	/** Dependency: sql service */
	protected SqlService m_sqlService= null;

	/**
	 * Dependency: sql service.
	 * @param service The sql service.
	 */
	public void setSqlService(SqlService service)
	{
		m_sqlService = service;
	}

	/*******************************************************************************
	* Init and Destroy
	*******************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		m_logger.info(this + ".init()");
	}

	/**
	 * Final cleanup.
	 */
	public void destroy()
	{
		m_logger.info(this + ".destroy()");
	}

	/*******************************************************************************
	* IdService implementation
	*******************************************************************************/

	/**
	* Allocate a unique Id.
	* @return A unique Id.
	*/
	public synchronized String getUniqueId()
	{
		String nextId = null;
		if ("oracle".equals(m_sqlService.getVendor()))
		{
			// get the next seq. number from the database
			String statement = "select CHEF_ID_SEQ.NEXTVAL from dual";
			List result = m_sqlService.dbRead(statement);
			nextId = (String) result.get(0);
		}
		else if ("mysql".equals(m_sqlService.getVendor()))
		{
			// see http://dev.mysql.com/doc/mysql/en/Information_functions.html
			// which describes how to simulate sequence numbers using MySQL
			// this is multi-server safe and thread-safe.
			Connection conn = null;
			try
			{
				conn = m_sqlService.borrowConnection();
				String statement = "UPDATE CHEF_ID_SEQ SET NEXTVAL=LAST_INSERT_ID(NEXTVAL+1)";
				m_sqlService.dbWrite(conn, statement, null);
				statement = "SELECT LAST_INSERT_ID()";
				List result = m_sqlService.dbRead(conn, statement, null, null);
				nextId = (String) result.get(0);
			}
			catch (SQLException e)
			{
			}
			finally
			{
				
				if (conn != null) m_sqlService.returnConnection(conn);
			}
		}
		else //if ("hsqldb".equals(m_sqlService.getVendor()))
		{
			// get the next seq. number from the database
		    String statement = "CALL NEXT VALUE FOR CHEF_ID_SEQ";    
		    List result = m_sqlService.dbRead(statement);
		    nextId = (String) result.get(0);
		}
		
		String rv = Long.toString(System.currentTimeMillis()) + "-" + nextId;
		return rv;

	}	// getUniqueId		

}	// ClusterIdService

/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/id/ClusterIdService.java,v 1.9 2005/01/18 22:23:49 janderse.umich.edu Exp $
*
**********************************************************************************/
