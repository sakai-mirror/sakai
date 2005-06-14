/*
 *                       Navigo Software License
 *
 * Copyright 2003, Trustees of Indiana University, The Regents of the University
 * of Michigan, and Stanford University, all rights reserved.
 *
 * This work, including software, documents, or other related items (the
 * "Software"), is being provided by the copyright holder(s) subject to the
 * terms of the Navigo Software License. By obtaining, using and/or copying this
 * Software, you agree that you have read, understand, and will comply with the
 * following terms and conditions of the Navigo Software License:
 *
 * Permission to use, copy, modify, and distribute this Software and its
 * documentation, with or without modification, for any purpose and without fee
 * or royalty is hereby granted, provided that you include the following on ALL
 * copies of the Software or portions thereof, including modifications or
 * derivatives, that you make:
 *
 *    The full text of the Navigo Software License in a location viewable to
 *    users of the redistributed or derivative work.
 *
 *    Any pre-existing intellectual property disclaimers, notices, or terms and
 *    conditions. If none exist, a short notice similar to the following should
 *    be used within the body of any redistributed or derivative Software:
 *    "Copyright 2003, Trustees of Indiana University, The Regents of the
 *    University of Michigan and Stanford University, all rights reserved."
 *
 *    Notice of any changes or modifications to the Navigo Software, including
 *    the date the changes were made.
 *
 *    Any modified software must be distributed in such as manner as to avoid
 *    any confusion with the original Navigo Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) and/or Indiana University,
 * The University of Michigan, Stanford University, or Navigo may NOT be used
 * in advertising or publicity pertaining to the Software without specific,
 * written prior permission. Title to copyright in the Software and any
 * associated documentation will at all times remain with the copyright holders.
 * The export of software employing encryption technology may require a specific
 * license from the United States Government. It is the responsibility of any
 * person or organization contemplating export to obtain such a license before
 * exporting this Software.
 */

package org.navigoproject.data;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.navigoproject.settings.PathInfo;

/**
 * Manager for generic (not always oracle) connections. This actually does
 * pooling, because some database connectors don't support JDBC connection
 * pooling.
 * 
 * @author Lance Speelmon (original author Rachel Gollub)
 * 
 * @version $Id: GenericConnectionManager.java,v 1.10 2004/06/01 21:38:26
 *          lancespeelmon Exp $
 */
public class GenericConnectionManager
{
  private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
      .getLogger(GenericConnectionManager.class);
  private static GenericConnectionManager _singleton;
  private static final String SETTINGS_FILE = "SAM.properties";
  //private static String JNDI_DATASOURCE_NAME = null;
  private static InitialContext ic = null;
  private static DataSource ds = null;

  /**
   * Generic Constructor
   */
  public GenericConnectionManager()
  {
  }

  /**
   * Constructor
   */
  private GenericConnectionManager(boolean createthis) throws Exception
  {
    LOG.debug("new GenericConnectionManager()");
    Properties props = PathInfo.getInstance().getSettingsProperties(
        SETTINGS_FILE);
    if (props == null)
    {
      throw new Error("Could not find file: " + SETTINGS_FILE);
    }
  /*  JNDI_DATASOURCE_NAME = props.getProperty("JndiDataSource");
    if (JNDI_DATASOURCE_NAME == null)
    {
      throw new Error("Could not find property \"JndiDataSource\" in file: "
          + SETTINGS_FILE);
    }
    LOG.info("JndiDataSource=" + JNDI_DATASOURCE_NAME);
    ic = new InitialContext();
    ds = (DataSource) ic.lookup(JNDI_DATASOURCE_NAME);
  */
  }

  /**
   * Spring injected data source.
   */
  public void setDataSource(DataSource newds) {
    ds = newds;
  }

  /**
   * Kept for historical / backawards compatibility
   */
  private synchronized static void initDB() throws Exception
  {
    if (_singleton == null)
    {
      _singleton = new GenericConnectionManager(true);
    }
  }

  /**
   * return a GenericConnectionManager instance
   */
  public static GenericConnectionManager getInstance()
  {
    if (_singleton == null)
    {
      try
      {
        initDB();
      }
      catch (Exception e)
      {
        LOG.error(e);
        throw new Error(e);
      }
    }
    return _singleton;
  }

  /**
   * Returns a connection
   */
  public Connection getConnection() throws Exception
  {
    LOG.debug("getConnection()");
    return ds.getConnection();
  }

  /**
   * Frees the connection
   * 
   * @param conn
   *          The connection to free.
   */
  public void freeConnection(Connection conn) throws SQLException
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("freeConnection(Connection " + conn + ")");
    }
    if (conn != null)
    {
      try
      {
        conn.close();
      }
      catch (SQLException e)
      {
        LOG.error(e);
        throw e;
      }
    }
    else
    {
      LOG.warn("freeConnection(Connection conn): Connection is null!");
    }
  }

  /**
   * Utility method to get rid of the connection pool and build a new one.
   */
  public synchronized static void refreshPool() throws Exception
  {
    // has no meaning with connection pool manager
    ; // intentionally left blank
  }
}

//package org.navigoproject.data;
//
//import org.navigoproject.settings.PathInfo;
//
//import java.io.*;
//
//import java.sql.*;
//
//import java.util.*;
//
///**
// * Manager for generic (not always oracle) connections.  This actually
// * does pooling, because some database connectors don't support JDBC
// * connection pooling.
// *
// * @author Rachel Gollub
// *
// * @version $Id: GenericConnectionManager.java,v 1.3 2004/09/24 05:37:11 rgollub.stanford.edu Exp $
// */
//public class GenericConnectionManager
//{
//  private static final org.apache.log4j.Logger LOG =
//    org.apache.log4j.Logger.getLogger(GenericConnectionManager.class);
//  private static GenericConnectionManager _singleton;
//  private static String propFile = null;
//  private static String server;
//  private static int minConns;
//  private static int maxConns;
//
//  // I'm using Vector because ArrayList isn't synchronized
//  private static Vector freeConns = new Vector();
//  private static Vector inUseConns = new Vector();
//
//  /**
//   * Constructor
//   * Initialize a GenericConnectionManager with the property file
//   *
//   * @param propFile This properties file should contain four values:<ul>
//   * <li> dbDriver : The driver class
//   * <li> dbServer : The string or url to use to create a connection
//   * <li> minConns : The minimum number of connections to hold open
//   * <li> maxConns : The maximum number of connections to hold open
//   * </ul>
//   */
//  private GenericConnectionManager(String propFile)
//    throws Exception
//  {
//    LOG.debug("Initializing the Database");
//    Properties p = new Properties();
//    p.load(new FileInputStream(propFile));
//
//    String driver = (String) p.get("dbDriver");
//    server = (String) p.get("dbServer");
//    minConns = Integer.parseInt((String) p.get("minConns"));
//    maxConns = Integer.parseInt((String) p.get("maxConns"));
//
//    Class.forName(driver).newInstance();
//    PoolInitializer pi = new PoolInitializer(server, freeConns, minConns);
//    pi.start();
//  }
//
//  /**
//   * Initialize database with the properties in the the propFile
//   *
//   * @param propFile This properties file should contain four values:<ul>
//   * <li> dbDriver : The driver class
//   * <li> dbServer : The string or url to use to create a connection
//   * <li> minConns : The minimum number of connections to hold open
//   * <li> maxConns : The maximum number of connections to hold open
//   * </ul>
//   */
//  public synchronized static void initDB(String propFile)
//    throws Exception
//  {
//    GenericConnectionManager.propFile = propFile; //Save it for the refresh
//
//    if(_singleton == null)
//    {
//      _singleton = new GenericConnectionManager(propFile);
//    }
//  }
//
//  /**
//   * return a GenericConnectionManager instance
//   */
//  public static GenericConnectionManager getInstance()
//  {
//    if(_singleton == null)
//    {
//      //throw new NullPointerException("GenericConnectionManager not initialized properly");
//      try
//      {
//        LOG.error("Lost DB instance.");
//        String fileName =
//          PathInfo.getInstance().getPathToSecurity() + "/AAMoracle.properties";
//        initDB(fileName);
//      }
//      catch(Exception e)
//      {
//        //TODO this exception should not be swallowed? - LDS
//        LOG.error(e);
//
//         throw new Error(e);
//      }
//    }
//
//    return _singleton;
//  }
//
//  /**
//   * Returns a connection
//   */
//  public Connection getConnection()
//    throws Exception
//  {
//    int i = 0;
//    while(i++ < 3)
//    { // Try three times for a connection
//      if(! freeConns.isEmpty())
//      {
//        Connection conn = (Connection) freeConns.elementAt(0);
//        freeConns.removeElementAt(0);
//        inUseConns.addElement(conn);
//
//        return conn;
//      }
//
//      if(inUseConns.size() < maxConns)
//      {
//        Connection conn = DriverManager.getConnection(server);
//        inUseConns.addElement(conn);
//
//        return conn;
//      }
//
//      Thread.sleep(1000); // Wait a second for a free connection
//    }
//
//    throw new Exception("No free connections available.");
//  }
//
//  /**
//   * Frees the connection
//   *
//   * @param conn The connection to free.
//   */
//  public void freeConnection(Connection conn)
//    throws SQLException
//  {
//    inUseConns.remove(conn);
//    freeConns.addElement(conn);
//  }
//
//  /**
//   * Utility method to get rid of the connection pool and build a new
//   * one.
//   */
//  public synchronized static void refreshPool()
//    throws Exception
//  {
//    if(propFile == null)
//    {
//      throw new NullPointerException("propFile is null");
//    }
//
//    _singleton = null;
//    initDB(propFile);
//  }
//}
//
//
///**
// * A private class to initialize the pool.  This is done in a separate
// * thread, since the number of connections could be huge, and we don't
// * want to hold up everything else while hundreds of connections are
// * created.
// */
//class PoolInitializer
//  extends Thread
//{
//  private static final org.apache.log4j.Logger LOG =
//    org.apache.log4j.Logger.getLogger(PoolInitializer.class);
//  Vector in = null;
//  int connections = 0;
//  String url = null;
//
//  /**
//   * Creates a new PoolInitializer object.
//   *
//   * @param server DOCUMENTATION PENDING
//   * @param freeConns DOCUMENTATION PENDING
//   * @param minConns DOCUMENTATION PENDING
//   */
//  public PoolInitializer(String server, Vector freeConns, int minConns)
//  {
//    url = server;
//    in = freeConns;
//    connections = minConns;
//  }
//
//  /**
//   * DOCUMENTATION PENDING
//   */
//  public void run()
//  {
//    try
//    {
//      for(int i = 0; i < connections; i++)
//      {
//        in.addElement(DriverManager.getConnection(url));
//      }
//    }
//    catch(Exception e)
//    {
//      LOG.error(e);
//      throw new Error(e);
//    }
//  }
//}
