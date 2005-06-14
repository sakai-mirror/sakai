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

package org.navigoproject.data.dao;

import org.navigoproject.business.entity.assessment.model.ItemIteratorImpl;
import org.navigoproject.business.entity.questionpool.model.*;
import org.navigoproject.data.GenericConnectionManager;
import org.navigoproject.osid.OsidManagerFactory;
import org.navigoproject.osid.questionpool.impl.QuestionPoolImpl;
import org.navigoproject.osid.questionpool.impl.QuestionPoolIteratorImpl;

import java.sql.*;

import java.util.*;

import org.apache.log4j.Logger;

import osid.shared.Id;
import osid.shared.SharedManager;
import osid.shared.Type;

/**
 * DOCUMENTATION PENDING
 *
 * @author $author$
 * @version $Id: QuestionPoolAccessObject.java,v 1.4 2004/10/15 21:50:26 lydial.stanford.edu Exp $
 */
public class QuestionPoolAccessObject
{
  GenericConnectionManager gcm = null;
  private static Logger LOG =
    Logger.getLogger(QuestionPoolAccessObject.class.getName());

  /**
   * Creates a new QuestionPoolAccessObject object.
   */
  public QuestionPoolAccessObject()
  {
    gcm = GenericConnectionManager.getInstance();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param agent DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public QuestionPoolIterator getAllPools(String agent)
  {
    Connection conn = null;
    PreparedStatement stmt = null;
    PreparedStatement stmt2 = null;
    ResultSet rs = null;
    ArrayList qpList = new ArrayList();

    try
    {
      conn = gcm.getConnection();
      stmt = conn.prepareStatement(GETALLPOOLS);
      stmt2 = conn.prepareStatement(GETITEMCOUNT);
      rs = stmt.executeQuery();
      while(rs.next())
      {
        Type type =
          getAccessType(
            rs.getLong("QUESTIONPOOLID"), rs.getInt("DEFAULTACCESSTYPEID"),
            agent);
        LOG.debug(
          "Access type: " + rs.getLong("QUESTIONPOOLID") + ":" + type);
        if(! type.getDescription().equals("Access Denied"))
        {
          SharedManager sm = OsidManagerFactory.createSharedManager();
          QuestionPool qp =
            new QuestionPoolImpl(
              sm.getId(rs.getString("QUESTIONPOOLID")),
              sm.getId(rs.getString("PARENTPOOLID")));
          qp.updateDisplayName(rs.getString("TITLE"));
          qp.updateDescription(rs.getString("DESCRIPTION"));

          QuestionPoolProperties props = new QuestionPoolProperties();
          props.setOwner(UtilAccessObject.getAgent(rs.getString("OWNERID")));
          props.setLastModified(rs.getTimestamp("LASTMODIFIED"));
          props.setAccessType(type);

          // We only use questions.size() when we display the tree
          Collection dummyItems = new ArrayList();
          stmt2.setLong(1, rs.getLong("QUESTIONPOOLID"));
          ResultSet rs2 = stmt2.executeQuery();
          if(rs2.next())
          {
            for(int i = 0; i < rs2.getInt(1); i++)
            {
              dummyItems.add("dummy");
            }
          }

          ItemAccessObject iao = new ItemAccessObject();
          long poolId = Long.parseLong(rs.getString("QUESTIONPOOLID"));
          ItemIteratorImpl items =
            iao.getAllItems(poolId, ItemAccessObject.POOL);
          ArrayList allItems = new ArrayList();
          while(items.hasNext())
          {
            allItems.add(items.next());
          }

          LOG.debug("Total items: " + allItems.size());
          props.setQuestions(allItems);

          //props.setQuestions(dummyItems);
          qp.updateData(props);

          qpList.add(qp);
        }
      }
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
    finally
    {
      try
      {
        stmt.close();
      }
      catch(Exception e)
      {
        LOG.error("Statement did not close.");
      }

      try
      {
        stmt2.close();
      }
      catch(Exception e)
      {
        LOG.error("Statement did not close.");
      }

      try
      {
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e.getMessage());
        LOG.error(e); //throw new Error(e);
      }
    }
    if(qpList==null) qpList = new ArrayList();
    return new QuestionPoolIteratorImpl(qpList);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param poolid DOCUMENTATION PENDING
   * @param agent DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public QuestionPool getPool(long poolid, String agent)
  {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    QuestionPool pool = null;

    try
    {
      conn = gcm.getConnection();
      stmt = conn.prepareStatement(GETPOOL);
      stmt.setLong(1, poolid);
      rs = stmt.executeQuery();
      if(rs.next())
      {
        SharedManager sm = OsidManagerFactory.createSharedManager();
        pool =
          new QuestionPoolImpl(
            sm.getId(rs.getString("QUESTIONPOOLID")),
            sm.getId(rs.getString("PARENTPOOLID")));
        pool.updateDisplayName(rs.getString("TITLE"));
        pool.updateDescription(rs.getString("DESCRIPTION"));

        QuestionPoolProperties props = new QuestionPoolProperties();
        props.setOwner(UtilAccessObject.getAgent(rs.getString("OWNERID")));
        props.setDateCreated(rs.getTimestamp("DATECREATED"));
        props.setLastModified(rs.getTimestamp("LASTMODIFIED"));
        props.setLastModifiedBy(
          UtilAccessObject.getAgent(rs.getString("LASTMODIFIEDBY")));
        props.setAccessType(
          getAccessType(
            rs.getLong("QUESTIONPOOLID"), rs.getInt("DEFAULTACCESSTYPEID"),
            agent));
        props.setObjectives(rs.getString("OBJECTIVE"));
        props.setKeywords(rs.getString("KEYWORDS"));
        props.setRubric(rs.getString("RUBRIC"));
        props.setType(UtilAccessObject.getType(rs.getInt("TYPEID")));
        props.setIntellectualProperty(
          UtilAccessObject.getIntellectualProperty(
            rs.getInt("INTELLECTUALPROPERTYID")));
        props.setOrganizationName(rs.getString("ORGANIZATIONNAME"));
        pool.updateData(props);

        ItemAccessObject iao = new ItemAccessObject();
        ItemIteratorImpl items = iao.getAllItems(poolid, ItemAccessObject.POOL);
        ArrayList allItems = new ArrayList();
        while(items.hasNext())
        {
          allItems.add(items.next());
        }

        LOG.debug("Total items: " + allItems.size());
        props.setQuestions(allItems);
      }
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
    finally
    {
      try
      {
        stmt.close();
      }
      catch(Exception e)
      {
        LOG.error("Statement did not close.");
      }

      try
      {
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e.getMessage());
        LOG.error(e); throw new Error(e);
      }
    }

    return pool;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param ids DOCUMENTATION PENDING
   * @param sectionId DOCUMENTATION PENDING
   */
  public void addItemsToSection(Collection ids, long sectionId)
  {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try
    {
      conn = gcm.getConnection();
      stmt = conn.prepareStatement(ADDITEMTOSECTION);
      Iterator iter = ids.iterator();
      while(iter.hasNext())
      {
        Id id = (Id) iter.next();
        LOG.debug("Adding: " + id.toString() + " to " + sectionId);
        stmt.setLong(1, sectionId);
        stmt.setLong(2, Long.parseLong(id.toString()));
        stmt.executeUpdate();
      }
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
    finally
    {
      try
      {
        stmt.close();
      }
      catch(Exception e)
      {
        LOG.error("Statement did not close.");
      }

      try
      {
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e.getMessage());
        LOG.error(e); throw new Error(e);
      }
    }
  }


  /**
   * DOCUMENTATION PENDING
   *
   * @param itemId DOCUMENTATION PENDING
   * @param poolId DOCUMENTATION PENDING
   */
  public void addItemToPool(String itemId, long poolId)
  {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try
    {
      conn = gcm.getConnection();
      stmt = conn.prepareStatement(ADDITEMTOPOOL);
      stmt.setLong(1, poolId);
      stmt.setString(2, itemId);
      stmt.executeUpdate();
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
    finally
    {
      try
      {
        stmt.close();
      }
      catch(Exception e)
      {
        LOG.error("Statement did not close.");
      }

      try
      {
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e.getMessage());
        LOG.error(e); throw new Error(e);
      }
    }
  }

/**
   * DOCUMENTATION PENDING
   *
   * @param itemId DOCUMENTATION PENDING
   * @param poolId DOCUMENTATION PENDING
   */
  public void deletePool(long poolId)
  {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try
    {
      conn = gcm.getConnection();
      stmt = conn.prepareStatement(DELETEPOOL);
      stmt.setLong(1, poolId);
      stmt.executeUpdate();
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
 finally
    {
      try
      {
        stmt.close();
      }
      catch(Exception e)
      {
        LOG.error("Statement did not close.");
      }

      try
      {
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e.getMessage());
        LOG.error(e); throw new Error(e);
      }
    }
  }



  /**
   * DOCUMENTATION PENDING
   *
   * @param itemId DOCUMENTATION PENDING
   * @param poolId DOCUMENTATION PENDING
   */
  public void removeItemFromPool(String itemId, long poolId)
  {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try
    {
      conn = gcm.getConnection();
      stmt = conn.prepareStatement(REMOVEITEMFROMPOOL);
      stmt.setLong(1, poolId);
      stmt.setString(2, itemId);
      stmt.executeUpdate();
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
    finally
    {
      try
      {
        stmt.close();
      }
      catch(Exception e)
      {
        LOG.error("Statement did not close.");
      }

      try
      {
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e.getMessage());
        LOG.error(e); throw new Error(e);
      }
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param itemId DOCUMENTATION PENDING
   * @param poolId DOCUMENTATION PENDING
   */
  public void moveItemToPool(String itemId, long sourceId, long destId)
  {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try
    {
      conn = gcm.getConnection();
      stmt = conn.prepareStatement(MOVEITEMTOPOOL);
      stmt.setLong(1, destId);
      stmt.setLong(2, sourceId);
      stmt.setString(3, itemId);
      stmt.executeUpdate();
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
    finally
    {
      try
      {
        stmt.close();
      }
      catch(Exception e)
      {
        LOG.error("Statement did not close.");
      }

      try
      {
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e.getMessage());
        LOG.error(e); throw new Error(e);
      }
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pool DOCUMENTATION PENDING
   */
  public QuestionPool savePool(QuestionPool pool)
  {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    boolean insert = true;

    try
    {
      conn = gcm.getConnection();
      LOG.debug("Pool name = " + pool.getDisplayName());
      if((pool.getId() == null) || pool.getId().toString().equals("0"))
      {
        QuestionPool oldPool = pool;
        pool = new QuestionPoolImpl(getQuestionPoolId(), oldPool.getParentId());
        pool.updateDisplayName(oldPool.getDisplayName());
        pool.updateDescription(oldPool.getDescription());
        pool.updateData(oldPool.getData());
        stmt = conn.prepareStatement(INSERTPOOL);
      }
      else
      {
        stmt = conn.prepareStatement(UPDATEPOOL);
        insert = false;
      }

      QuestionPoolProperties props = (QuestionPoolProperties) pool.getData();
      int i = 0;

      if(pool.getParentId() != null)
      {
        stmt.setInt(++i, Integer.parseInt(pool.getParentId().toString()));
      }
      else
      {
        stmt.setNull(++i, Types.INTEGER);
      }

      //stmt.setString(++i, props.getOwner().getDisplayName());
      stmt.setString(++i, props.getOwner().getId().toString());
      stmt.setString(++i, props.getOrganizationName());
      if(insert)
      {
        stmt.setTimestamp(++i, new Timestamp(new java.util.Date().getTime()));
      }

      stmt.setTimestamp(++i, new Timestamp(new java.util.Date().getTime()));
      //stmt.setString(++i, props.getLastModifiedBy().getDisplayName());
      stmt.setString(++i, props.getLastModifiedBy().getId().toString());
      stmt.setInt(++i, ACCESS_DENIED); // Charles requires this for now.
      stmt.setString(++i, pool.getDisplayName());
      stmt.setString(++i, pool.getDescription());
      stmt.setString(++i, props.getObjectives());
      stmt.setString(++i, props.getKeywords());
      stmt.setString(++i, props.getRubric());
      stmt.setInt(++i, 0); // No Type ID for now.
      stmt.setInt(++i, 0); // No Intellectual Property ID for now.
      stmt.setLong(++i, Long.parseLong(pool.getId().toString()));
      stmt.executeUpdate();
      stmt.close();

      if(insert)
      {
        stmt = conn.prepareStatement(GIVEACCESS);
        stmt.setLong(1, Long.parseLong(pool.getId().toString()));
        //stmt.setString(2, props.getOwner().getDisplayName());
        stmt.setString(2, props.getOwner().getId().toString());
        stmt.setInt(3, ADMIN);
        stmt.executeUpdate();

        if(
          //! props.getOwner().getDisplayName().equals(
           //   props.getLastModifiedBy().getDisplayName()))
          ! props.getOwner().getId().toString().equals(
              props.getLastModifiedBy().getId().toString()))
        {
          stmt.setLong(1, Long.parseLong(pool.getId().toString()));
          //stmt.setString(2, props.getLastModifiedBy().getDisplayName());
          stmt.setString(2, props.getLastModifiedBy().getId().toString());
          stmt.setInt(3, ADMIN);
          stmt.executeUpdate();
        }

        stmt.close();
      }

      return pool;
    }
    catch(Exception e)
    {
      LOG.error(e); 
//      throw new Error(e);

      return pool;
    }
    finally
    {
      try
      {
        stmt.close();
      }
      catch(Exception e)
      {
        LOG.error("Statement did not close.");
      }

      try
      {
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e.getMessage());
        LOG.error(e); throw new Error(e);
      }
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param questionPoolId DOCUMENTATION PENDING
   * @param defaultId DOCUMENTATION PENDING
   * @param agent DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Type getAccessType(long questionPoolId, int defaultId, String agent)
  {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try
    {
      conn = gcm.getConnection();
      stmt = conn.prepareStatement(GETACCESS);
      stmt.setLong(1, questionPoolId);
      stmt.setString(2, agent);
      rs = stmt.executeQuery();
      if(rs.next())
      {
        return UtilAccessObject.getType(rs.getInt("ACCESSTYPEID"));
      }

      return UtilAccessObject.getType(defaultId);
    }
    catch(Exception e)
    {
      LOG.error(e); 
//      throw new Error(e);

      return null;
    }
    finally
    {
      try
      {
        stmt.close();
      }
      catch(Exception e)
      {
        LOG.error("Statement did not close.");
      }

      try
      {
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e.getMessage());
        LOG.error(e); throw new Error(e);
      }
    }
  }

/**
   * DOCUMENTATION PENDING
   *
   * @param itemId DOCUMENTATION PENDING
   * @param poolId DOCUMENTATION PENDING
   */

 public List getSubPools(long poolId)
  {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    ArrayList poolList = new ArrayList();

    try
    {
      conn = gcm.getConnection();
      stmt = conn.prepareStatement(GETSUBPOOLS);
      stmt.setLong(1, poolId);
      rs = stmt.executeQuery();
      while(rs.next()){
        poolList.add(rs.getString("QUESTIONPOOLID"));
      }
    }
    catch(Exception e)

 {
      LOG.error(e); throw new Error(e);
    }
    finally
    {
      try
      {
        stmt.close();
      }
      catch(Exception e)
      {
        LOG.error("Statement did not close.");
      }

      try
      {
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e.getMessage());
        LOG.error(e); throw new Error(e);
      }
    }
    return poolList;
  }

/**
   * DOCUMENTATION PENDING
   *
   * @param itemId DOCUMENTATION PENDING
   * @param poolId DOCUMENTATION PENDING
   */

 public boolean hasSubPools(long poolId)
  {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    boolean result = false;

    try
    {
      conn = gcm.getConnection();
      stmt = conn.prepareStatement(GETSUBPOOLS);
      stmt.setLong(1, poolId);
      rs = stmt.executeQuery();
      if(rs.next()){
        result = true;
      }
    }
    catch(Exception e)

 {
      LOG.error(e); throw new Error(e);
    }
    finally
    {
      try
      {
        stmt.close();
      }
      catch(Exception e)
      {
        LOG.error("Statement did not close.");
      }

      try
      {
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e.getMessage());
        LOG.error(e); throw new Error(e);
      }
    }
    return result;
  }




/**
   * DOCUMENTATION PENDING
   *
   * @param itemId DOCUMENTATION PENDING
   * @param poolId DOCUMENTATION PENDING
   */

 public List getPoolIdsByAgent(String  agentId)
  {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    ArrayList idList = new ArrayList();

    try
    {
    	if(gcm != null )
      {
      	conn = gcm.getConnection();
      }
      else
      {
				LOG.error("No Connection found");
				return idList;
      }
      if(conn !=null)
      {
      	stmt = conn.prepareStatement(POOLIDSBYOWNER);}
      else
      {
				LOG.error("No Connection found");
								return idList;
      }
      stmt.setString(1, agentId);
      rs = stmt.executeQuery();
      while(rs.next()){
        idList.add(rs.getString("QUESTIONPOOLID"));
      }
    }
    catch(Exception e)

 		{
      LOG.error(e); throw new Error(e);
    }
    finally
    {
      try
      {
      	if(stmt !=null)
        stmt.close();
      }
      catch(Exception e)
      {
        LOG.error("Statement did not close.");
      }

      try
      {
				if(gcm !=null)
        {gcm.freeConnection(conn);} 
        
      }
      catch(Exception e)
      {
        LOG.error(e.getMessage());
        LOG.error(e); throw new Error(e);
      }
    }
    return idList;
  }




/**
   * DOCUMENTATION PENDING
   *
   * @param itemId DOCUMENTATION PENDING
   * @param poolId DOCUMENTATION PENDING
   */

 public List getPoolIdsByItem(String itemId)
  {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    ArrayList idList = new ArrayList();

    try
    {
      conn = gcm.getConnection();
      stmt = conn.prepareStatement(POOLIDSBYITEM);
      stmt.setString(1, itemId);
      rs = stmt.executeQuery();
      while(rs.next()){
        idList.add(rs.getString("QUESTIONPOOLID"));
      }
    }
    catch(Exception e)

 {
      LOG.error(e); throw new Error(e);
    }
    finally
    {
      try
      {
        stmt.close();
      }
      catch(Exception e)
      {
        LOG.error("Statement did not close.");
      }

      try
      {
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e.getMessage());
        LOG.error(e); throw new Error(e);
      }
    }
    return idList;
  }




/**
   * DOCUMENTATION PENDING
   *
   * @param itemId DOCUMENTATION PENDING
   * @param poolId DOCUMENTATION PENDING
   */

 public List getPoolNamesByItem(String itemId)
  {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    ArrayList nameList = new ArrayList();

    try
    {
      conn = gcm.getConnection();
      stmt = conn.prepareStatement(POOLNAMESBYITEM);
      stmt.setString(1, itemId);
      rs = stmt.executeQuery();
      while(rs.next()){
        nameList.add(new String(rs.getString("TITLE")));
      }
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
    finally
    {
      try
      {
        stmt.close();
      }
      catch(Exception e)
      {
        LOG.error("Statement did not close.");
      }

      try
      {
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e.getMessage());
        LOG.error(e); throw new Error(e);
      }
    }
    return nameList;
  }


  /**
   * Create a new QuestionPool id.
   * @return A new QuestionPool Id.
   */
  public synchronized Id getQuestionPoolId()
  {
    return UtilAccessObject.getNewId("QUESTIONPOOL");
  }

  public static int ACCESS_DENIED = 30;
  public static int READ_ONLY = 31;
  public static int READ_COPY = 32;
  public static int READ_WRITE = 33;
  public static int ADMIN = 34;
  public static String GETALLPOOLS =
    "SELECT QUESTIONPOOLID, PARENTPOOLID, TITLE, DESCRIPTION, LASTMODIFIED, DEFAULTACCESSTYPEID, OWNERID FROM QUESTIONPOOL";
  public static String GETPOOL =
    "SELECT * FROM QUESTIONPOOL WHERE QUESTIONPOOLID = ?";
  public static String GETSUBPOOLS =
    "SELECT * FROM QUESTIONPOOL WHERE PARENTPOOLID = ?";
  public static String GETITEMCOUNT =
    "SELECT COUNT(*) FROM QUESTIONPOOLITEM WHERE QUESTIONPOOLID = ?";
  public static String GETQUESTIONSFORPOOL =
    "SELECT ITEM.ITEMID, TITLE, TEXT, TYPEID FROM QUESTIONPOOLITEM, ITEM WHERE QUESTIONPOOLID = ? AND QUESTIONPOOLITEM.ITEMID = ITEM.ITEMID";
  public static String ADDITEMTOSECTION =
    "INSERT INTO SECTIONITEM (SECTIONID, ITEMID) VALUES (?,?)";
  public static String POOLNAMESBYITEM =
    "SELECT DISTINCT TITLE FROM QUESTIONPOOL,QUESTIONPOOLITEM WHERE QUESTIONPOOL.QUESTIONPOOLID = QUESTIONPOOLITEM.QUESTIONPOOLID AND ITEMID = ?";
  public static String POOLIDSBYOWNER =
    "SELECT DISTINCT QUESTIONPOOLID FROM QUESTIONPOOL WHERE OWNERID = ?";
  public static String POOLIDSBYITEM =
    "SELECT DISTINCT QUESTIONPOOL.QUESTIONPOOLID FROM QUESTIONPOOL, QUESTIONPOOLITEM WHERE QUESTIONPOOL.QUESTIONPOOLID = QUESTIONPOOLITEM.QUESTIONPOOLID AND ITEMID = ?";
  public static String GETACCESS =
    "SELECT ACCESSTYPEID FROM QUESTIONPOOLACCESS WHERE QUESTIONPOOLID = ? AND AGENTID = ?";
  public static String ADDITEMTOPOOL =
    "INSERT INTO QUESTIONPOOLITEM (QUESTIONPOOLID, ITEMID) VALUES (?,?)";
  public static String REMOVEITEMFROMPOOL =
    "DELETE FROM QUESTIONPOOLITEM WHERE QUESTIONPOOLID=? AND ITEMID = ?";
  public static String DELETEPOOL =
    "DELETE FROM QUESTIONPOOL WHERE QUESTIONPOOLID=?";
  public static String MOVEITEMTOPOOL =
    "UPDATE QUESTIONPOOLITEM SET QUESTIONPOOLID = ? WHERE QUESTIONPOOLID = ? AND ITEMID = ?";
  public static String INSERTPOOL =
    "INSERT INTO QUESTIONPOOL (PARENTPOOLID, OWNERID, ORGANIZATIONNAME, DATECREATED, LASTMODIFIED, LASTMODIFIEDBY, DEFAULTACCESSTYPEID, TITLE, DESCRIPTION, OBJECTIVE, KEYWORDS, RUBRIC, TYPEID, INTELLECTUALPROPERTYID, QUESTIONPOOLID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
  public static String UPDATEPOOL =
    "UPDATE QUESTIONPOOL SET PARENTPOOLID=?, OWNERID=?, ORGANIZATIONNAME=?, LASTMODIFIED=?, LASTMODIFIEDBY=?, DEFAULTACCESSTYPEID=?, TITLE=?, DESCRIPTION=?, OBJECTIVE=?, KEYWORDS=?, RUBRIC=?, TYPEID=?, INTELLECTUALPROPERTYID=? WHERE QUESTIONPOOLID=?";
  public static String GIVEACCESS =
    "INSERT INTO QUESTIONPOOLACCESS (QUESTIONPOOLID, AGENTID, ACCESSTYPEID) VALUES (?,?,?)";
}
