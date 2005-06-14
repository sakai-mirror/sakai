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

import org.navigoproject.business.entity.assessment.model.MediaData;
import org.navigoproject.data.GenericConnectionManager;
import org.navigoproject.osid.OsidManagerFactory;
import org.navigoproject.osid.TypeLib;
import org.navigoproject.osid.shared.impl.TypeImpl;
import org.navigoproject.osid.shared.impl.TypeIteratorImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import osid.authentication.AuthenticationManager;

import osid.shared.Agent;
import osid.shared.Id;
import osid.shared.SharedManager;
import osid.shared.Type;
import osid.shared.TypeIterator;

/**
 * DOCUMENTATION PENDING
 *
 * @author $author$
 * @version $Id: UtilAccessObject.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 */
public class UtilAccessObject
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(UtilAccessObject.class);

  /**
   * DOCUMENTATION PENDING
   *
   * @param keyword DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public static TypeIterator getAllTypes(String keyword)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getAllTypes(String " + keyword + ")");
    }
    GenericConnectionManager gcm = GenericConnectionManager.getInstance();
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    Collection types = new ArrayList();
    try
    {
      conn = gcm.getConnection();
      stmt = conn.prepareStatement(GETALLTYPES);
      stmt.setString(1, keyword);
      rs = stmt.executeQuery();
      while(rs.next())
      {
        Type type =
          new org.navigoproject.osid.shared.impl.TypeImpl(
            rs.getString("DOMAIN"), rs.getString("AUTHORITY"),
            rs.getString("KEYWORD"), rs.getString("DESCRIPTION"));
        types.add(type);
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
        LOG.error("Statement did not close.", e);
      }

      try
      {
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e); throw new Error(e);
      }
    }

    return new TypeIteratorImpl(types.iterator());
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param id DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public static Type getType(int id)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getType(int " + id + ")");
    }
    GenericConnectionManager gcm = GenericConnectionManager.getInstance();
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    Type type = null;

    try
    {
      conn = gcm.getConnection();
      stmt = conn.prepareStatement(GETTYPE);
      stmt.setInt(1, id);
      rs = stmt.executeQuery();
      if(rs.next())
      {
        type =
          new TypeImpl(
            rs.getString("DOMAIN"), rs.getString("AUTHORITY"),
            rs.getString("KEYWORD"), rs.getString("DESCRIPTION"));
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
        LOG.error("Statement did not close.", e);
      }

      try
      {
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e); throw new Error(e);
      }
    }

    return type;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param id DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public static String getIntellectualProperty(long id)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getIntellectualProperty(long " + id + ")");
    }
    GenericConnectionManager gcm = GenericConnectionManager.getInstance();
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    String property = null;

    try
    {
      conn = gcm.getConnection();
      stmt = conn.prepareStatement(GETINTELLECTUALPROPERTY);
      stmt.setLong(1, id);
      rs = stmt.executeQuery();
      if(rs.next())
      {
        property = rs.getString("PROPERTYINFO");
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
        LOG.error("Statement did not close.", e);
      }

      try
      {
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e); throw new Error(e);
      }
    }

    return property;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public static HashMap getMediaIcon()
  {
    LOG.debug("getMediaIcon()");
    GenericConnectionManager gcm = GenericConnectionManager.getInstance();
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    String iconurl = null;
    HashMap typeicon = new HashMap();

    try
    {
      conn = gcm.getConnection();
      stmt = conn.createStatement();
      rs = stmt.executeQuery(GETMEDIATYPEICON);
      while(rs.next())
      {
        typeicon.put(rs.getString("MEDIATYPE"), rs.getString("ICONURL"));
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
        LOG.error("Statement did not close.", e);
      }

      try
      {
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e); throw new Error(e);
      }
    }

    return typeicon;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param id DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public static MediaData getMediaData(int id)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getMediaData(int " + id + ")");
    }
    GenericConnectionManager gcm = GenericConnectionManager.getInstance();
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    MediaData md = new MediaData();

    try
    {
      conn = gcm.getConnection();
      stmt = conn.prepareStatement(GETMEDIA);
      stmt.setInt(1, id);
      rs = stmt.executeQuery();
      while(rs.next())
      {
        md.setId(rs.getInt("MEDIAID"));
        md.setName(rs.getString("NAME"));
        md.setAuthor(rs.getString("AUTHOR"));
        md.setTypeId(rs.getInt("MEDIATYPEID"));
        md.setDescription(rs.getString("DESCRIPTION"));
        md.setLocation(rs.getString("LOCATION"));
        md.setDateAdded(rs.getTimestamp("DATEADDED"));

        /* this will only work if the jdbc driver that you are using implemented java.sql.Blob
          java.sql.Blob blob = rs.getBlob("MEDIA");
          byte[] b = blob.getBytes((long)1,(int)blob.length());
          md.setContent(b);
        */
        /* You also can't use getBytes() 'cos when data exceed 4k, data is not necessary stored inside 
           the table but rather a locator is stored in its place instead. The locator
           will allow the DB to locate the data.
           e.g. md.setContent(rs.getBytes("MEDIA")) will return the locator and not the actual data.
           In light of these 2 reasons, we are using getBinaryStream() instead
           daisyf 06/09/04
        */ 

        java.lang.Object o = rs.getObject("MEDIA");
        if (o!=null){
	  java.io.InputStream in = rs.getBinaryStream("MEDIA");
          in.mark(0);
          int ch;
          int len=0;
          while ((ch=in.read())!=-1)
	    len++;

          byte[] b = new byte[len];
          in.reset(); 
          in.read(b,0,len);
          md.setContent(b);
          in.close();
	}
        else{
	    LOG.info("content is stored as file, not in DB");
	}
   
        md.setFileName(rs.getString("FILENAME"));
        if(rs.getString("ISLINK") != null)
        {
          md.setLink((rs.getString("ISLINK").equals("T") ? true : false));
        }

        if(rs.getString("ISHTMLINLINE") != null)
        {
          md.setLink((rs.getString("ISHTMLINLINE").equals("T") ? true : false));
        }
        else
        {
          md.setLink(false);
        }
      }

      stmt.close();

      stmt = conn.prepareStatement(GETMEDIATYPE);
      stmt.setInt(1, md.getTypeId());
      rs = stmt.executeQuery();
      while(rs.next())
      {
        md.setType(rs.getString("MEDIATYPE"));
        md.setRecorder(rs.getString("RECORDER"));
        md.setPlayer(rs.getString("PLAYER"));
        md.setIconUrl(rs.getString("ICONURL"));
      }

      stmt.close();
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
        // It was already closed -- do nothing.
      }

      try
      {
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e); throw new Error(e);
      }
    }

    return md;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param md DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public static int saveMedia(MediaData md)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("saveMedia(MediaData " + md + ")");
    }
    GenericConnectionManager gcm = GenericConnectionManager.getInstance();
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try
    {
      conn = gcm.getConnection();

      // See if we need to find or create the content type.
      if((md.getTypeId() == 0) && (md.getType() != null))
      {
        stmt = conn.prepareStatement(GETMEDIATYPEID);
        stmt.setString(1, md.getType());
        rs = stmt.executeQuery();
        if(rs.next())
        {
          md.setTypeId(rs.getInt("MEDIATYPEID"));
          stmt.close();
        }
        else
        {
          int typeid = new Integer(getMediaTypeId().toString()).intValue();
          stmt.close();
          stmt = conn.prepareStatement(INSERTMEDIATYPE);
          stmt.setInt(1, typeid);
          stmt.setString(2, md.getType());
          stmt.executeUpdate();
          stmt.close();
          md.setTypeId(typeid);
        }
      }

      int id = 0;
      if(md.getId() > 0)
      {
        stmt = conn.prepareStatement(UPDATEMEDIA);
        id = md.getId();
      }
      else
      {
        stmt = conn.prepareStatement(INSERTMEDIA);
        id = new Integer(getMediaId().toString()).intValue();
      }

      LOG.info("**save media");

      if (md.getContent()==null){
        stmt.setBytes(1, md.getContent());
      }
      else{
        byte[] b = md.getContent();
        stmt.setBinaryStream(1, new java.io.ByteArrayInputStream(b),b.length);
      }

      //stmt.setBytes(1, md.getContent()); <-- sorry, this doesn't work well -daisyf 06/09/04
      stmt.setInt(2, md.getTypeId());
      stmt.setString(3, md.getDescription());
      stmt.setString(4, md.getLocation());
      stmt.setString(5, md.getName());
      stmt.setString(6, md.getAuthor());
      stmt.setString(7, md.getFileName());
      if(md.getDateAdded() == null)
      {
        md.setDateAdded(new java.util.Date());
      }

      stmt.setTimestamp(8, new Timestamp(md.getDateAdded().getTime()));
      stmt.setString(9, (md.isLink() ? "T" : "F"));
      stmt.setInt(10, id);
      stmt.executeUpdate();
      stmt.close();

      return id;
    }
    catch(Exception e)
    {
      LOG.error(e); 
//      throw new Error(e);

      return 0;
    }
    finally
    {
      try
      {
        stmt.close();
      }
      catch(Exception e)
      {
        // It was already closed -- do nothing.
      }

      try
      {
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e); throw new Error(e);
      }
    }
  }

  /**
   * This method deletes media from the database <b>only if there are no
   * existing links to it</b>.  You need to remove all links to the media
   * before calling this method.
   *
   * @param id DOCUMENTATION PENDING
   */
  public static void deleteMedia(int id)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("deleteMedia(int " + id + ")");
    }
    GenericConnectionManager gcm = GenericConnectionManager.getInstance();
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    boolean inUse = false;

    try
    {
      conn = gcm.getConnection();

      // Check if it's part of an assessment
      stmt = conn.prepareStatement(GETAMEDIA);
      stmt.setInt(1, id);
      rs = stmt.executeQuery();
      if(rs.next())
      {
        inUse = true;
      }

      stmt.close();

      // Check if it's part of a section
      if(! inUse)
      {
        stmt = conn.prepareStatement(GETSMEDIA);
        stmt.setInt(1, id);
        rs = stmt.executeQuery();
        if(rs.next())
        {
          inUse = true;
        }

        stmt.close();
      }

      // Check if it's part of an item
      if(! inUse)
      {
        stmt = conn.prepareStatement(GETIMEDIA);
        stmt.setInt(1, id);
        rs = stmt.executeQuery();
        if(rs.next())
        {
          inUse = true;
        }

        stmt.close();
      }

      // Check if it's been used as feedback
      if(! inUse)
      {
        stmt = conn.prepareStatement(GETFMEDIA);
        stmt.setInt(1, id);
        rs = stmt.executeQuery();
        if(rs.next())
        {
          inUse = true;
        }

        stmt.close();
      }

      // Check if it's media uploaded by a student
      if(! inUse)
      {
        stmt = conn.prepareStatement(GETUMEDIA);
        stmt.setInt(1, id);
        rs = stmt.executeQuery();
        if(rs.next())
        {
          inUse = true;
        }

        stmt.close();
      }

      // If it's not linked anywhere, delete it.
      if(! inUse)
      {
        stmt = conn.prepareStatement(DELETEMEDIA);
        stmt.setInt(1, id);
        stmt.executeUpdate();
        stmt.close();
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
        // It was already closed -- do nothing.
      }

      try
      {
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e); throw new Error(e);
      }
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param sunetid DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws Error DOCUMENTATION PENDING
   */
  public synchronized static Agent getAgent(String id)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getAgent(String " + id + ")");
    }
    try
    {
      SharedManager sm = OsidManagerFactory.createSharedManager();

      return sm.getAgent(sm.getId(id));
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
      throw new Error(e);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public synchronized static Id getMediaTypeId()
  {
    LOG.debug("getMediaTypeId()");
    return UtilAccessObject.getNewId("MEDIATYPE");
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public synchronized static Id getMediaId()
  {
    LOG.debug("getMediaId()");
    return UtilAccessObject.getNewId("MEDIA");
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param table DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public static Id getNewId(String table)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getNewId(String " + table + ")");
    }
    GenericConnectionManager gcm = GenericConnectionManager.getInstance();
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try
    {
      conn = gcm.getConnection();
      stmt = conn.prepareStatement(GETNEWID);
      stmt.setString(1, table);
      rs = stmt.executeQuery();
      int id = 1;
      if(rs.next())
      {
        id = rs.getInt(1);
      }

      stmt.close();

      if(id == 1)
      {
        stmt = conn.prepareStatement(INSNEWID);
      }
      else
      {
        stmt = conn.prepareStatement(SETNEWID);
      }

      stmt.setString(1, table);
      stmt.executeUpdate();

      SharedManager sm = OsidManagerFactory.createSharedManager();

      return sm.getId(id + "");
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
        LOG.error("Statement did not close.", e);
      }

      try
      {
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e); throw new Error(e);
      }
    }
  }

  public static final String GETALLTYPES =
    "SELECT * FROM STANFORDTYPE WHERE KEYWORD = ?";
  public static final String GETTYPE =
    "SELECT * FROM STANFORDTYPE WHERE TYPEID = ?";
  public static final String GETNEWID =
    "SELECT NEXTID FROM IDLIST WHERE TABLENAME = ?";
  public static final String INSNEWID =
    "INSERT INTO IDLIST (TABLENAME, NEXTID) VALUES (?, 2)";
  public static final String SETNEWID =
    "UPDATE IDLIST SET NEXTID=NEXTID+1 WHERE TABLENAME = ?";
  public static final String GETMEDIA = "SELECT * FROM MEDIA WHERE MEDIAID=?";
  public static final String GETMEDIATYPE =
    "SELECT * FROM MEDIATYPE WHERE MEDIATYPEID = ?";
  public static final String INSERTMEDIA =
    "INSERT INTO MEDIA (MEDIA, MEDIATYPEID, DESCRIPTION, LOCATION, NAME, AUTHOR, FILENAME, DATEADDED, ISLINK, MEDIAID) VALUES (?,?,?,?,?,?,?,?,?,?)";
  public static final String UPDATEMEDIA =
    "UPDATE MEDIA SET MEDIA=?, MEDIATYPEID=?, DESCRIPTION=?, LOCATION=?, NAME=?, AUTHOR=?, FILENAME=?, DATEADDED=?, ISLINK=? WHERE MEDIAID=?";
  public static final String DELETEMEDIA =
    "DELETE FROM MEDIA WHERE MEDIAID = ?";
  public static final String GETMEDIATYPEID =
    "SELECT MEDIATYPEID FROM MEDIATYPE WHERE MEDIATYPE=?";
  public static final String GETMEDIATYPEICON =
    "SELECT MEDIATYPE,ICONURL FROM MEDIATYPE";
  public static final String INSERTMEDIATYPE =
    "INSERT INTO MEDIATYPE (MEDIATYPEID, MEDIATYPE) VALUES (?,?)";
  public static final String GETAMEDIA =
    "SELECT MEDIAID from ASSESSMENTMEDIA WHERE MEDIAID = ?";
  public static final String GETSMEDIA =
    "SELECT MEDIAID from SECTIONMEDIA WHERE MEDIAID = ?";
  public static final String GETIMEDIA =
    "SELECT MEDIAID from ITEMMEDIA WHERE MEDIAID = ?";
  public static final String GETFMEDIA =
    "SELECT FEEDBACKMEDIAID from ASSESSMENTTAKEN WHERE FEEDBACKMEDIAID = ?";
  public static final String GETUMEDIA =
    "SELECT ANSWERMEDIAID from ANSWERTAKEN WHERE ANSWERMEDIAID = ?";
  public static final String GETINTELLECTUALPROPERTY =
    "SELECT PROPERTYINFO FROM INTELLECTUALPROPERTY WHERE INTELLECTUALPROPERTYID = ?";
}
