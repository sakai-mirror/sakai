/**********************************************************************************
*
* $Header: /cvs/sakai/sqlTest/src/java/SqlTest.java,v 1.3 2004/12/16 14:45:06 ggolden.umich.edu Exp $
*
***********************************************************************************
*
* Copyright (c) 2004 The Regents of the University of Michigan, Trustees of Indiana University,
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

import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * <p>SqlTest is ...</p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.3 $
 */
public class SqlTest
{
	static String m_vendor = "";
	static String m_connect = "";
	static String m_user = "";
	static String m_pw = "";
	static String m_driver = "";

	/**
	 */
	public static void main(String[] args) throws Exception
	{
		initDriver();
		Connection conn = makeConnection();

		String sql[] =
			{
				// warm up the connection
				"select count(1) from sakai_realm",
				"select count(1) from sakai_realm",

				// latest from code w/ subselects and in not =
				"select REALM_ID from SAKAI_REALM where REALM_KEY in " + // distinct
				"(select REALM_KEY from SAKAI_REALM_RL_FN where FUNCTION_KEY in " +
				"(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = ?) " +
				"and ROLE_KEY in " +
				"(select ROLE_KEY from SAKAI_REALM_RL_GR where ACTIVE = '1' and USER_ID = ? and SAKAI_REALM_RL_GR.REALM_KEY=SAKAI_REALM_RL_FN.REALM_KEY) " +
				"union " +
				"select REALM_KEY from SAKAI_REALM_RL_FN where FUNCTION_KEY in (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = ?) and ROLE_KEY in (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth') " +
				"union " +
				"select REALM_KEY from SAKAI_REALM_RL_FN where FUNCTION_KEY in (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = ?) and ROLE_KEY in (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.anon'))",

				// latest from code w/o unions auth/anon
				"select REALM_ID from SAKAI_REALM where REALM_KEY in " + // distinct
				"(select REALM_KEY from SAKAI_REALM_RL_FN where FUNCTION_KEY in " +
				"(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = ?) " +
				"and ROLE_KEY in " +
				"(select ROLE_KEY from SAKAI_REALM_RL_GR where ACTIVE = '1' and USER_ID = ? and SAKAI_REALM_RL_GR.REALM_KEY=SAKAI_REALM_RL_FN.REALM_KEY))",

				// the auth union for above w/ lookups for function/role
				"select REALM_KEY from SAKAI_REALM_RL_FN where FUNCTION_KEY in (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = ?) and ROLE_KEY in (select ROLE_KEY from SAKAI_REALM_ROLE where ROLE_NAME = '.auth')",
				"select REALM_KEY from SAKAI_REALM_RL_FN, SAKAI_REALM_ROLE, SAKAI_REALM_FUNCTION where SAKAI_REALM_RL_FN.ROLE_KEY=SAKAI_REALM_ROLE.ROLE_KEY and SAKAI_REALM_RL_FN.FUNCTION_KEY=SAKAI_REALM_FUNCTION.FUNCTION_KEY and FUNCTION_NAME = ? and ROLE_NAME = '.auth'",
				
				// the auth union for above pre-computed func/role
				"select REALM_KEY from SAKAI_REALM_RL_FN where FUNCTION_KEY = 82 and ROLE_KEY in (16, 8)",
				"select REALM_KEY from SAKAI_REALM_RL_FN where FUNCTION_KEY = ? and ROLE_KEY in (?, ?)",

				// latest from code w/o unions auth/anon
				"select REALM_ID from SAKAI_REALM where REALM_KEY in " + 
				"(select REALM_KEY from SAKAI_REALM_RL_FN where FUNCTION_KEY in " +
				"(select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = ?) " +
				"and ROLE_KEY in " +
				"(select ROLE_KEY from SAKAI_REALM_RL_GR where ACTIVE = '1' and USER_ID = ? and SAKAI_REALM_RL_GR.REALM_KEY=SAKAI_REALM_RL_FN.REALM_KEY)" +
				"union select REALM_KEY from SAKAI_REALM_RL_FN where FUNCTION_KEY = 82 and ROLE_KEY in (16, 8))",

				// full query pre-computed -very fast!
				"select REALM_ID from SAKAI_REALM where REALM_KEY in " + 
				"(select REALM_KEY from SAKAI_REALM_RL_FN where FUNCTION_KEY = 82 " +
				"and ROLE_KEY in " +
				"(select ROLE_KEY from SAKAI_REALM_RL_GR where ACTIVE = '1' and USER_ID = ? and SAKAI_REALM_RL_GR.REALM_KEY=SAKAI_REALM_RL_FN.REALM_KEY)" +
				"union select REALM_KEY from SAKAI_REALM_RL_FN where FUNCTION_KEY = 82 and ROLE_KEY in (16, 8))",

				// latest sql from unlockRealms
				"select distinct REALM_ID from SAKAI_REALM where REALM_KEY in (" +
				"select REALM_KEY from SAKAI_REALM_RL_FN " +
				"where FUNCTION_KEY in (select FUNCTION_KEY from SAKAI_REALM_FUNCTION where FUNCTION_NAME = ?) " +
				"and ROLE_KEY in (select ROLE_KEY from SAKAI_REALM_RL_GR where ACTIVE = '1' and USER_ID = ? and SAKAI_REALM_RL_GR.REALM_KEY=SAKAI_REALM_RL_FN.REALM_KEY)" +
				")",

				// in progress w/o subselects w/o unions autn/anon
//				"select distinct REALM_ID from " +
//				"SAKAI_REALM SR, " +
//				"SAKAI_REALM_RL_FN SRRF, " +
//				"SAKAI_REALM_FUNCTION SRF, " +
//				"SAKAI_REALM_RL_GR SRRG " +
//				"where SR.REALM_KEY = SRRF.REALM_KEY " +
//				"and SR.REALM_KEY = SRRG.REALM_KEY " +
//				"and SRRF.FUNCTION_KEY = SRF.FUNCTION_KEY " +
//				"and SRRF.ROLE_KEY = SRRG.ROLE_KEY " +
//				"and SRRG.USER_ID = ? " +
//				"and SRRG.ACTIVE = '1' " +
//				"and SRF.FUNCTION_NAME = ?",

				// the union with above for anon/auth
//				"select distinct REALM_ID from " +
//				"SAKAI_REALM SR, " +
//				"SAKAI_REALM_RL_FN SRRF, " +
//				"SAKAI_REALM_FUNCTION SRF, " +
//				"SAKAI_REALM_ROLE SRR " +
//				"where SR.REALM_KEY = SRRF.REALM_KEY " +
//				"and SRRF.FUNCTION_KEY = SRF.FUNCTION_KEY " +
//				"and SRRF.ROLE_KEY = SRR.ROLE_KEY " +
//				"and SRF.FUNCTION_NAME = ? " +
//				"and SRR.ROLE_NAME in ('.anon', '.auth')",

				// the union with above for anon/auth, with anon, auth and function site.visit pre-computed
//				"select distinct REALM_ID from " +
//				"SAKAI_REALM SR, " +
//				"SAKAI_REALM_RL_FN SRRF " +
//				"where SR.REALM_KEY = SRRF.REALM_KEY " +
//				"and FUNCTION_KEY = ? " +
//				"and ROLE_KEY in (?, ?)",

				// union with pre-computed role/function
//				"select REALM_ID from " +
//				"SAKAI_REALM SR, " +
//				"SAKAI_REALM_RL_FN SRRF, " +
//				"SAKAI_REALM_RL_GR SRRG " +
//				"where SR.REALM_KEY = SRRF.REALM_KEY " +
//				"and SR.REALM_KEY = SRRG.REALM_KEY " +
//				"and SRRF.ROLE_KEY = SRRG.ROLE_KEY " +
//				"and SRRG.USER_ID = ? " +
//				"and SRRG.ACTIVE = '1' " +
//				"and SRRF.FUNCTION_KEY = ? " +
//				"union " +
//				"select REALM_ID from " +
//				"SAKAI_REALM SR, " +
//				"SAKAI_REALM_RL_FN SRRF " +
//				"where SR.REALM_KEY = SRRF.REALM_KEY " +
//				"and FUNCTION_KEY = ? " +
//				"and ROLE_KEY in (?, ?)",

				// what's min?
				"select 1 from dual"
			};

		Object[][] fields =
			{
				null,
				null,

				{"site.visit", "ggolden", "site.visit", "site.visit"},
				{"site.visit", "ggolden"},
				{"site.visit"},
				{"site.visit"},
				null,
				{new Integer(82), new Integer(16), new Integer(8)},
				{"site.visit", "ggolden"},
				{"ggolden"},
				{"site.visit", "ggolden"},
//				{"ggolden", "site.visit"},
//				{"site.visit"},
//				{new Integer(82), new Integer(16), new Integer(8)},
//				{"ggolden", new Integer(82), new Integer(82), new Integer(16), new Integer(8)},

				null
			};

		for (int i = 0; i < sql.length; i++)
		{
			List rv = dbRead(conn, sql[i], fields[i]);
			for (Iterator it = rv.iterator(); it.hasNext();)
			{
				System.out.println(it.next());
			}
		}
	}

	static protected void initDriver() throws Exception
	{
		try
		{
			Class.forName(m_driver).newInstance();
		}
		catch (InstantiationException e)
		{
			throw new SQLException("InstantiationException for driver: " + m_driver);
		}
		catch (IllegalAccessException e)
		{
			throw new SQLException("IllegalAccessException for driver: " + m_driver);
		}
		catch (ClassNotFoundException e)
		{
			throw new SQLException("ClassNotFoundException for driver: " + m_driver);
		}

	}

	/**
	* Process a query, filling in with fields, and return the results as a List, one per record read.
	* If a reader is provided, it will be called for each record to prepare the Object placed into the List.
	* Otherwise, the first field of each record, as a String, will be placed in the list.
	* @param callerConn The db connection object to use (if not null).
	* @param sql The sql statement.
	* @param fields The array of fields for parameters.
	* @return The List of things read, one per record.
	*/
	static protected List dbRead(Connection callerConn, String sql, Object[] fields)
	{
		// for DEBUG
		long start = 0;
		long connectionTime = 0;
		int lenRead = 0;
		long stmtTime = 0;
		long resultsTime = 0;
		int count = 0;

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		ResultSetMetaData meta = null;
		List rv = new Vector();

		try
		{
			start = System.currentTimeMillis();

			conn = callerConn;
			connectionTime = System.currentTimeMillis() - start;
			start = System.currentTimeMillis();

			pstmt = conn.prepareStatement(sql);

			// put in all the fields
			prepareStatement(pstmt, fields);

			result = pstmt.executeQuery();

			stmtTime = System.currentTimeMillis() - start;
			start = System.currentTimeMillis();

			while (result.next())
			{
				count++;
				String s = result.getString(1);
				rv.add(s);
			}
		}
		catch (Exception e)
		{
			System.out.println("Sql.dbRead: sql: " + sql + debugFields(fields) + e);
		}
		finally
		{
			resultsTime = System.currentTimeMillis() - start;

			try
			{
				if (null != result) result.close();
				if (null != pstmt) pstmt.close();
			}
			catch (Exception e)
			{
				System.out.println("Sql.dbRead: sql: " + sql + debugFields(fields) + e);
			}
		}

		debug("Sql.dbRead: connection: " + connectionTime
				+ " read: " + count
				+ " sql: " + stmtTime
				+ " process: " + resultsTime
				,sql, fields);

		return rv;

	}	// dbRead

	static protected Connection makeConnection() throws Exception
	{
		Connection conn = DriverManager.getConnection(m_connect, m_user, m_pw);

		// set the connection session to use GMT for dates
		long time = 0;
		time = System.currentTimeMillis();

		// form vendor specific ultra-cheap queries
		String sql = null;
		if ("oracle".equals(m_vendor))
		{
			sql = "alter session set TIME_ZONE='+00:00'";
		}
		else if ("mysql".equals(m_vendor))
		{
			// set the connection time zone to GMT
			sql = "SET @time_zone = \"+0:00\"";
		}
		// no other db vendor currently supported
		else
		{
			return conn;
		}

		// fire off the query, and if it fails, we will invalidate the connection
		PreparedStatement pstmt = null;
		ResultSet result = null;
		try
		{
			pstmt = conn.prepareStatement(sql);
			result = pstmt.executeQuery();
		}
		catch (Throwable any)
		{
			System.out.println("Sql.makeObject: " + sql + " : " + any);
		}
		finally
		{
			try
			{
				if (null != result) result.close();
				if (null != pstmt) pstmt.close();
			}
			catch (Throwable any)
			{
				System.out.println("Sql.makeObject: " + sql + " : " + any);
			}
		}

		debug("Sql.makeObject:  time: " + (System.currentTimeMillis()-time), sql, null);

		return conn;
	}

	/**
	 * Append a message about this SQL statement to the DEBUG string in progress, if any
	 * @param str The SQL statement.
	 * @param fields The bind fields.
	 */
	static protected void debug(String str, String sql, Object[] fields)
	{
		// no error will mess us up!
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append("\n\t");
			buf.append(str);
			buf.append(" binds: ");
			buf.append(debugFields(fields));
			buf.append("\n");
			buf.append(sql);

			System.out.println(buf);
		}
		catch (Throwable ignore) {};
	}

	static protected String debugFields(Object[] fields)
	{
		StringBuffer buf = new StringBuffer();
		if (fields != null)
		{
			for (int i = 0; i < fields.length; i++)
			{
				if (fields[i] != null)
				{
					buf.append(" ");
					buf.append(fields[i].toString());
				}
				else
				{
					buf.append(" null");
				}
			}
		}
		return buf.toString();
	}

	/**
	* Prepare a prepared statement with fields.
	* @param pstmt The prepared statement to fill in.
	* @param fields The Object array of values to fill in.
	* @return the next pos that was not filled in.
	*/
	static protected int prepareStatement(PreparedStatement pstmt, Object[] fields)
		throws SQLException
	{
		// put in all the fields
		int pos = 1;
		if ((fields != null) && (fields.length > 0))
		{
			for (int i = 0; i < fields.length; i++)
			{	
				if (fields[i] == null || (fields[i] instanceof String && ((String)fields[i]).length() == 0))
				{
					// treat a Java null as an SQL null, 
					// and ALSO treat a zero-length Java string as an SQL null
					// This makes sure that Oracle vs MySQL use the same value
					// for null.
					pstmt.setObject(pos, null);
					pos++;
				}
//				else if (fields[i] instanceof Time)
//				{
//					Time t = (Time) fields[i];
//					pstmt.setTimestamp(pos, new Timestamp(t.getTime()), m_cal);
//					pos++;
//				}
				else if (fields[i] instanceof Long)
				{
					long l = ((Long) fields[i]).longValue();
					pstmt.setLong(pos, l);
					pos++;
				}
				// %%% support any other types specially?
				else
				{
					String value = fields[i].toString();
					pstmt.setCharacterStream(pos, new StringReader(value), value.length());
					pos++;
				}
			}
		}

		return pos;

	}	// prepareStatement

}

/**********************************************************************************
*
* $Header: /cvs/sakai/sqlTest/src/java/SqlTest.java,v 1.3 2004/12/16 14:45:06 ggolden.umich.edu Exp $
*
**********************************************************************************/
