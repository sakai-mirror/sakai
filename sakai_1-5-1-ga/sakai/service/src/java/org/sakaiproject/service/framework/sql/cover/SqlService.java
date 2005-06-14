/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/framework/sql/cover/SqlService.java,v 1.11 2004/12/08 21:19:54 ggolden.umich.edu Exp $
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

package org.sakaiproject.service.framework.sql.cover;

import org.sakaiproject.service.framework.component.cover.ComponentManager;

/**
* <p>SqlService is a static Cover for the {@link org.sakaiproject.service.framework.sql.SqlService SqlService};
* see that interface for usage details.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.11 $
*/
public class SqlService
{
	/**
	 * Access the component instance: special cover only method.
	 * @return the component instance.
	 */
	public static org.sakaiproject.service.framework.sql.SqlService getInstance()
	{
		if (ComponentManager.CACHE_SINGLETONS)
		{
			if (m_instance == null) m_instance = (org.sakaiproject.service.framework.sql.SqlService) ComponentManager.get(org.sakaiproject.service.framework.sql.SqlService.class);
			return m_instance;
		}
		else
		{
			return (org.sakaiproject.service.framework.sql.SqlService) ComponentManager.get(org.sakaiproject.service.framework.sql.SqlService.class);
		}
	}
	private static org.sakaiproject.service.framework.sql.SqlService m_instance = null;

	public static java.lang.String DEFAULT = org.sakaiproject.service.framework.sql.SqlService.DEFAULT;

	public static java.sql.Connection borrowConnection() throws java.sql.SQLException
	{
		org.sakaiproject.service.framework.sql.SqlService service = getInstance();
		if (service == null)
			return null;

		return service.borrowConnection();
	}

	public static void returnConnection(java.sql.Connection param0)
	{
		org.sakaiproject.service.framework.sql.SqlService service = getInstance();
		if (service == null)
			return;

		service.returnConnection(param0);
	}

	public static org.sakaiproject.service.framework.sql.ConnectionPool newPool(java.lang.String param0) throws org.sakaiproject.exception.IdUsedException
	{
		org.sakaiproject.service.framework.sql.SqlService service = getInstance();
		if (service == null)
			return null;

		return service.newPool(param0);
	}

	public static org.sakaiproject.service.framework.sql.ConnectionPool getPool(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException
	{
		org.sakaiproject.service.framework.sql.SqlService service = getInstance();
		if (service == null)
			return null;

		return service.getPool(param0);
	}

	public static java.util.List getPools()
	{
		org.sakaiproject.service.framework.sql.SqlService service = getInstance();
		if (service == null)
			return null;

		return service.getPools();
	}

	public static void deletePool(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException
	{
		org.sakaiproject.service.framework.sql.SqlService service = getInstance();
		if (service == null)
			return;

		service.deletePool(param0);
	}

	public static void setDefaultPool(java.lang.String param0) throws org.sakaiproject.exception.IdUnusedException
	{
		org.sakaiproject.service.framework.sql.SqlService service = getInstance();
		if (service == null)
			return;

		service.setDefaultPool(param0);
	}

	public static java.util.List dbRead(java.lang.String param0)
	{
		org.sakaiproject.service.framework.sql.SqlService service = getInstance();
		if (service == null)
			return null;

		return service.dbRead(param0);
	}

	public static java.util.List dbRead(java.lang.String param0, java.lang.Object[] param1, org.sakaiproject.service.framework.sql.SqlReader param2)
	{
		org.sakaiproject.service.framework.sql.SqlService service = getInstance();
		if (service == null)
			return null;

		return service.dbRead(param0, param1, param2);
	}

	public static java.util.List dbRead(java.sql.Connection param0, java.lang.String param1, java.lang.Object[] param2, org.sakaiproject.service.framework.sql.SqlReader param3)
	{
		org.sakaiproject.service.framework.sql.SqlService service = getInstance();
		if (service == null)
			return null;

		return service.dbRead(param0, param1, param2, param3);
	}

	public static void dbReadBinary(java.lang.String param0, java.lang.Object[] param1, byte[] param2)
	{
		org.sakaiproject.service.framework.sql.SqlService service = getInstance();
		if (service == null)
			return;

		service.dbReadBinary(param0, param1, param2);
	}

	public static void dbReadBinary(java.sql.Connection param0, java.lang.String param1, java.lang.Object[] param2, byte[] param3)
	{
		org.sakaiproject.service.framework.sql.SqlService service = getInstance();
		if (service == null)
			return;

		service.dbReadBinary(param0, param1, param2, param3);
	}

	public static java.io.InputStream dbReadBinary(java.lang.String param0, java.lang.Object[] param1, boolean param2) throws org.sakaiproject.exception.ServerOverloadException
	{
		org.sakaiproject.service.framework.sql.SqlService service = getInstance();
		if (service == null)
			return null;

		return service.dbReadBinary(param0, param1, param2);
	}

	public static boolean dbWrite(java.lang.String param0)
	{
		org.sakaiproject.service.framework.sql.SqlService service = getInstance();
		if (service == null)
			return false;

		return service.dbWrite(param0);
	}

	public static boolean dbWrite(java.lang.String param0, java.lang.String param1)
	{
		org.sakaiproject.service.framework.sql.SqlService service = getInstance();
		if (service == null)
			return false;

		return service.dbWrite(param0, param1);
	}

	public static boolean dbWrite(java.lang.String param0, java.lang.Object[] param1)
	{
		org.sakaiproject.service.framework.sql.SqlService service = getInstance();
		if (service == null)
			return false;

		return service.dbWrite(param0, param1);
	}

	public static boolean dbWrite(java.sql.Connection param0, java.lang.String param1, java.lang.Object[] param2)
	{
		org.sakaiproject.service.framework.sql.SqlService service = getInstance();
		if (service == null)
			return false;

		return service.dbWrite(param0, param1, param2);
	}

	public static boolean dbWrite(java.lang.String param0, java.lang.Object[] param1, java.lang.String param2)
	{
		org.sakaiproject.service.framework.sql.SqlService service = getInstance();
		if (service == null)
			return false;

		return service.dbWrite(param0, param1, param2);
	}

	public static boolean dbWriteBinary(java.lang.String param0, java.lang.Object[] param1, byte[] param2, int param3, int param4)
	{
		org.sakaiproject.service.framework.sql.SqlService service = getInstance();
		if (service == null)
			return false;

		return service.dbWriteBinary(param0, param1, param2, param3, param4);
	}

	public static boolean dbWriteFailQuiet(java.sql.Connection param0, java.lang.String param1, java.lang.Object[] param2)
	{
		org.sakaiproject.service.framework.sql.SqlService service = getInstance();
		if (service == null)
			return false;

		return service.dbWriteFailQuiet(param0, param1, param2);
	}

	public static void dbReadBlobAndUpdate(java.lang.String param0, byte[] param1)
	{
		org.sakaiproject.service.framework.sql.SqlService service = getInstance();
		if (service == null)
			return;

		service.dbReadBlobAndUpdate(param0, param1);
	}

	public static java.sql.Connection dbReadLock(java.lang.String param0, java.lang.StringBuffer param1)
	{
		org.sakaiproject.service.framework.sql.SqlService service = getInstance();
		if (service == null)
			return null;

		return service.dbReadLock(param0, param1);
	}

	public static void dbUpdateCommit(java.lang.String param0, java.lang.Object[] param1, java.lang.String param2, java.sql.Connection param3)
	{
		org.sakaiproject.service.framework.sql.SqlService service = getInstance();
		if (service == null)
			return;

		service.dbUpdateCommit(param0, param1, param2, param3);
	}

	public static void dbCancel(java.sql.Connection param0)
	{
		org.sakaiproject.service.framework.sql.SqlService service = getInstance();
		if (service == null)
			return;

		service.dbCancel(param0);
	}

	public static java.util.GregorianCalendar getCal()
	{
		org.sakaiproject.service.framework.sql.SqlService service = getInstance();
		if (service == null)
			return null;

		return service.getCal();
	}

	public static java.lang.String getVendor()
	{
		org.sakaiproject.service.framework.sql.SqlService service = getInstance();
		if (service == null)
			return null;

		return service.getVendor();
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/framework/sql/cover/SqlService.java,v 1.11 2004/12/08 21:19:54 ggolden.umich.edu Exp $
*
**********************************************************************************/
