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

import org.navigoproject.business.entity.ItemTemplateIterator;
import org.navigoproject.business.entity.SectionTemplate;
import org.navigoproject.business.entity.assessment.model.ItemImpl;
import org.navigoproject.business.entity.assessment.model.MediaData;
import org.navigoproject.business.entity.assessment.model.SectionImpl;
import org.navigoproject.business.entity.assessment.model.SectionIteratorImpl;
import org.navigoproject.business.entity.assessment.model.SectionPropertiesImpl;
import org.navigoproject.business.entity.assessment.model.SectionTemplateImpl;
import org.navigoproject.business.entity.assessment.model.SectionTemplateIteratorImpl;
import org.navigoproject.business.entity.assessment.model.SectionTemplatePropertiesImpl;
import org.navigoproject.data.GenericConnectionManager;
import org.navigoproject.osid.OsidManagerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import osid.assessment.ItemIterator;
import osid.assessment.Section;

import osid.shared.Id;
import osid.shared.SharedManager;

/**
 * DOCUMENTATION PENDING
 *
 * @author $author$
 * @version $Id: SectionAccessObject.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 */
public class SectionAccessObject
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(SectionAccessObject.class);
    
  GenericConnectionManager gcm = null;

  /**
   * Creates a new SectionAccessObject object.
   */
  public SectionAccessObject()
  {
    gcm = GenericConnectionManager.getInstance();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentID DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public SectionIteratorImpl getAllSections(long assessmentID)
  {
    Connection conn = null;
    PreparedStatement stmt = null;
    PreparedStatement substmt = null;
    ResultSet rs = null;
    ResultSet rs2 = null;
    Collection sections = new ArrayList();
    try
    {
      conn = gcm.getConnection();
      stmt = conn.prepareStatement(GETSECTIONIDS);
      substmt = conn.prepareStatement(GETSECTION);
      stmt.setLong(1, assessmentID);
      rs = stmt.executeQuery();
      SharedManager sm = OsidManagerFactory.createSharedManager();
      while(rs.next())
      {
        substmt.setLong(1, rs.getLong("SECTIONID"));
        rs2 = substmt.executeQuery();
        while(rs2.next())
        {
          SectionImpl section =
            new SectionImpl(sm.getId(rs2.getString("SECTIONID")));
          section.updateDisplayName(rs2.getString("TITLE"));
          section.updateDescription(rs2.getString("DESCRIPTION"));
          SectionPropertiesImpl props = new SectionPropertiesImpl();
          section.updateData(getSectionProperties(props, rs2));
          ItemAccessObject iao = new ItemAccessObject();
          ItemIterator iti = iao.getAllItems(rs2.getLong("SECTIONID"));
          while(iti.hasNext())
          {
            section.addItem(iti.next());
          }

          sections.add(section);
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
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e); throw new Error(e);
      }
    }

    return new SectionIteratorImpl(sections);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public SectionTemplateIteratorImpl getAllTemplates(long assessmentId)
  {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    Collection templates = new ArrayList();
    try
    {
      conn = gcm.getConnection();
      stmt = conn.prepareStatement(GETALLTEMPLATES);
      stmt.setLong(1, assessmentId);
      rs = stmt.executeQuery();
      SharedManager sm = OsidManagerFactory.createSharedManager();
      while(rs.next())
      {
        SectionTemplateImpl template =
          new SectionTemplateImpl(sm.getId(rs.getString("SECTIONID")));
        template.updateDisplayName(rs.getString("TITLE"));
        template.updateDescription(rs.getString("DESCRIPTION"));
        SectionTemplatePropertiesImpl props =
          new SectionTemplatePropertiesImpl();
        template.updateData(getSectionProperties(props, rs));
        ItemAccessObject iao = new ItemAccessObject();
        ItemTemplateIterator iti = iao.getAllTemplates(assessmentId);
        while(iti.hasNext())
        {
          template.addItem(iti.next());
        }

        templates.add(template);
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
        LOG.error(e); throw new Error(e);
      }
    }

    return new SectionTemplateIteratorImpl(templates);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param section DOCUMENTATION PENDING
   * @param assessmentId DOCUMENTATION PENDING
   * @param position DOCUMENTATION PENDING
   */
  public void saveSection(Section section, long assessmentId, int position)
  {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try
    {
      boolean isTemplate = false;
      if(section instanceof SectionTemplate)
      {
        isTemplate = true;
      }

      conn = gcm.getConnection();
      long id = new Integer(((Id) section.getId()).getIdString()).intValue();
      stmt = conn.prepareStatement(ISUPDATETEMPLATE);
      stmt.setLong(1, id);
      rs = stmt.executeQuery();
      boolean isUpdate = false;
      if(rs.next())
      {
        isUpdate = true;
      }

      stmt.close();

      int typeid = 0;
      if(section.getSectionType() != null)
      {
        stmt = conn.prepareStatement(GETTYPEID);
        stmt.setString(1, section.getSectionType().getDescription());
        stmt.setString(2, "assessment");
        rs = stmt.executeQuery();
        if(rs.next())
        {
          typeid = rs.getInt(1);
        }
      }

      int i = 1;
      if(isUpdate)
      {
        if(isTemplate)
        {
          stmt = conn.prepareStatement(UPDATETEMPLATE);
        }
        else
        {
          stmt = conn.prepareStatement(UPDATEASSESSMENTSECTION);
          stmt.setLong(1, position);
          stmt.setLong(2, assessmentId);
          stmt.setLong(3, id);
          stmt.executeUpdate();
          stmt.close();

          stmt = conn.prepareStatement(UPDATESECTION);
        }
      }
      else
      {
        if(isTemplate)
        {
          stmt = conn.prepareStatement(INSERTTEMPLATE);
        }
        else
        {
          stmt = conn.prepareStatement(INSERTASSESSMENTSECTION);
          stmt.setLong(1, assessmentId);
          stmt.setLong(2, id);
          stmt.setLong(3, position);
          stmt.executeUpdate();
          stmt.close();

          stmt = conn.prepareStatement(INSERTSECTION);
        }
      }

      stmt.setString(i++, section.getDisplayName());
      stmt.setString(i++, section.getDescription());
      stmt.setInt(i++, typeid);
      SectionPropertiesImpl props = (SectionPropertiesImpl) section.getData();
      if(props == null)
      {
        if(isTemplate)
        {
          props = new SectionTemplatePropertiesImpl();
        }
        else
        {
          props = new SectionPropertiesImpl();
        }
      }

      stmt.setString(i++, props.getObjectives());
      stmt.setString(i++, props.getKeywords());
      stmt.setString(i++, props.getRubrics());
      stmt.setString(i++, props.getItemOrder());
      if(! isTemplate)
      {
        if(props.getRandomPoolId() == null)
        {
          stmt.setNull(i++, Types.INTEGER);
        }
        else
        {
          stmt.setLong(i++, Long.parseLong(props.getRandomPoolId().toString()));
        }

        stmt.setInt(i++, props.getRandomNumber());
      }

      stmt.setLong(i++, assessmentId);

      if(! isUpdate)
      {
        if(isTemplate)
        {
          stmt.setString(i++, "T");
        }
        else
        {
          stmt.setString(i++, "F");
        }
      }

      stmt.setLong(i++, id);
      stmt.executeUpdate();
      stmt.close();

      // Get the old ones
      Collection oldMedia = new ArrayList();
      stmt = conn.prepareStatement(GETMEDIA);
      stmt.setLong(1, id);
      rs = stmt.executeQuery();
      while(rs.next())
      {
        oldMedia.add(new Integer(rs.getInt("MEDIAID")));
      }

      stmt.close();

      // Delete links to all of them
      stmt = conn.prepareStatement(DELETEMEDIA);
      stmt.setLong(1, id);
      stmt.executeUpdate();
      stmt.close();

      if(
        (props.getMediaCollection() != null) &&
          (props.getMediaCollection().size() > 0))
      {
        // Save the new ones
        Iterator iter = props.getMediaCollection().iterator();
        int[] ids = new int[props.getMediaCollection().size()];
        int ii = 0;
        while(iter.hasNext())
        {
          ids[ii++] = UtilAccessObject.saveMedia((MediaData) iter.next());
        }

        // Insert links to the new ones
        stmt = conn.prepareStatement(INSERTMEDIA);
        for(ii = 0; ii < ids.length; ii++)
        {
          stmt.setLong(1, id);
          stmt.setInt(2, ids[ii]);
          stmt.setInt(3, ii);
          stmt.executeUpdate();
        }

        stmt.close();
      }

      // Now that we've linked them all, delete the ones that aren't
      // linked.
      Iterator iter = oldMedia.iterator();
      while(iter.hasNext())
      {
        // This will only delete media that have no links
        UtilAccessObject.deleteMedia(((Integer) iter.next()).intValue());
      }

      if(isTemplate)
      {
        SectionTemplatePropertiesImpl tprops =
          (SectionTemplatePropertiesImpl) props;
        stmt = conn.prepareStatement(DELETEPROPS);
        stmt.setLong(1, id);
        stmt.executeUpdate();
        stmt.close();

        ArrayList fields = new ArrayList();
        for(
          Enumeration e = tprops.getInstructorEditableMap().keys();
            e.hasMoreElements();)
        {
          fields.add((String) e.nextElement());
        }

        for(
          Enumeration e = tprops.getInstructorViewableMap().keys();
            e.hasMoreElements();)
        {
          String field = (String) e.nextElement();
          if(! fields.contains(field))
          {
            fields.add(field);
          }
        }

        for(
          Enumeration e = tprops.getStudentViewableMap().keys();
            e.hasMoreElements();)
        {
          String field = (String) e.nextElement();
          if(! fields.contains(field))
          {
            fields.add(field);
          }
        }

        stmt = conn.prepareStatement(INSERTPROPS);
        iter = fields.iterator();
        while(iter.hasNext())
        {
          String field = (String) iter.next();
          stmt.setLong(1, id);
          stmt.setString(2, field);
          stmt.setString(3, (tprops.isInstructorEditable(field) ? "T" : "F"));
          stmt.setString(4, (tprops.isInstructorViewable(field) ? "T" : "F"));
          stmt.setString(5, (tprops.isStudentViewable(field) ? "T" : "F"));
          stmt.executeUpdate();
        }

        stmt.close();
      }

      // Now save the items.
      ItemAccessObject iao = new ItemAccessObject();
      ItemIterator ii = section.getItems();
      int j = 0;
      while(ii.hasNext())
      {
        if(isTemplate)
        {
          iao.saveItem((ItemImpl) ii.next(), assessmentId, ++j);
        }
        else
        {
          iao.saveItem((ItemImpl) ii.next(), id, ++j);
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
   * @param sectionId DOCUMENTATION PENDING
   */
  public void deleteSection(long sectionId)
  {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try
    {
      conn = gcm.getConnection();

      LOG.debug("Deleting section " + sectionId);

      // Delete the link to the assessment
      stmt = conn.prepareStatement(DELETEASSESSMENTSECTION);
      stmt.setLong(1, sectionId);
      stmt.executeUpdate();
      stmt.close();

      // Delete the actual section
      stmt = conn.prepareStatement(DELETESECTION);
      stmt.setLong(1, sectionId);
      stmt.executeUpdate();
      stmt.close();

      // Delete all items in this section
      ItemAccessObject iao = new ItemAccessObject();
      stmt = conn.prepareStatement(GETITEMIDS);
      stmt.setLong(1, sectionId);
      rs = stmt.executeQuery();
      while(rs.next())
      {
        iao.deleteItem(rs.getInt("ITEMID"));
      }

      stmt.close();

      // Delete media
      stmt = conn.prepareStatement(DELETEMEDIA);
      stmt.setLong(1, sectionId);
      stmt.executeUpdate();
      stmt.close();

      stmt = conn.prepareStatement(GETMEDIA);
      stmt.setLong(1, sectionId);
      rs = stmt.executeQuery();
      while(rs.next())
      {
        UtilAccessObject.deleteMedia(rs.getInt("MEDIAID"));
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
        LOG.error("Statement did not close.");
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
   * @param props DOCUMENTATION PENDING
   * @param rs DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public SectionPropertiesImpl getSectionProperties(
    SectionPropertiesImpl props, ResultSet rs)
  {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs2 = null;
    try
    {
      props.setObjectives(rs.getString("OBJECTIVE"));
      props.setKeywords(rs.getString("KEYWORDS"));
      props.setRubrics(rs.getString("RUBRICS"));
      props.setItemOrder(rs.getString("QUESTIONORDER"));
      props.setSectionType(UtilAccessObject.getType(rs.getInt("TYPEID")));
      LOG.debug("RANDOMPOOLID: " + rs.getString("RANDOMPOOLID"));
      if(rs.getString("RANDOMPOOLID") != null)
      {
        SharedManager sm = OsidManagerFactory.createSharedManager();
        props.setRandomPoolId(sm.getId(rs.getString("RANDOMPOOLID")));
      }

      props.setRandomNumber(rs.getInt("RANDOMNUMBER"));
      conn = gcm.getConnection();

      if(rs.getLong("RANDOMPOOLID") > 0)
      {
        stmt = conn.prepareStatement(GETPOOLNAME);
        stmt.setLong(1, rs.getLong("RANDOMPOOLID"));
        rs2 = stmt.executeQuery();
        if(rs2.next())
        {
          props.setRandomPoolName(rs2.getString("TITLE"));
        }

        stmt.close();
      }

      stmt = conn.prepareStatement(GETMEDIA);
      stmt.setInt(1, rs.getInt("SECTIONID"));
      rs2 = stmt.executeQuery();
      while(rs2.next())
      {
        props.addMedia(UtilAccessObject.getMediaData(rs2.getInt("MEDIAID")));
      }

      stmt.close();

      if(props instanceof SectionTemplatePropertiesImpl)
      {
        SectionTemplatePropertiesImpl tprops =
          (SectionTemplatePropertiesImpl) props;
        stmt = conn.prepareStatement(GETPROPS);
        stmt.setInt(1, rs.getInt("SECTIONID"));
        rs2 = stmt.executeQuery();
        while(rs2.next())
        {
          String field = rs2.getString("FIELD");
          if(rs2.getString("ISINSTRUCTORVIEWABLE").equals("T"))
          {
            tprops.setInstructorViewable(field, true);
          }
          else
          {
            tprops.setInstructorViewable(field, false);
          }

          if(rs2.getString("ISINSTRUCTOREDITABLE").equals("T"))
          {
            tprops.setInstructorEditable(field, true);
          }
          else
          {
            tprops.setInstructorEditable(field, false);
          }

          if(rs2.getString("ISSTUDENTVIEWABLE").equals("T"))
          {
            tprops.setStudentViewable(field, true);
          }
          else
          {
            tprops.setStudentViewable(field, false);
          }
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
        gcm.freeConnection(conn);
      }
      catch(Exception e)
      {
        LOG.error(e); throw new Error(e);
      }
    }

    return props;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public synchronized Id getSectionId()
  {
    return UtilAccessObject.getNewId("SECTION");
  }

  public static final String GETSECTIONIDS =
    "SELECT SECTIONID FROM ASSESSMENTSECTION WHERE ASSESSMENTID = ? ORDER BY POSITION";
  public static final String GETSECTION =
    "SELECT * FROM SECTION WHERE SECTIONID = ?";
  public static final String GETALLSECTIONS =
    "SELECT * FROM SECTION WHERE ISTEMPLATE = 'F' AND ASSESSMENTID = ?";
  public static final String GETALLTEMPLATES =
    "SELECT * FROM SECTION WHERE ISTEMPLATE = 'T' AND ASSESSMENTTEMPLATEID = ?";
  public static final String ISUPDATETEMPLATE =
    "SELECT SECTIONID FROM SECTION WHERE SECTIONID = ?";
  public static final String GETTYPEID =
    "SELECT TYPEID FROM STANFORDTYPE WHERE DESCRIPTION=? AND KEYWORD=?";
  public static final String INSERTTEMPLATE =
    "INSERT INTO SECTION (TITLE, DESCRIPTION, TYPEID, OBJECTIVE, KEYWORDS, RUBRICS, QUESTIONORDER, ASSESSMENTTEMPLATEID, ISTEMPLATE, SECTIONID) VALUES (?,?,?,?,?,?,?,?,?,?)";
  public static final String UPDATETEMPLATE =
    "UPDATE SECTION SET TITLE=?, DESCRIPTION=?, TYPEID=?, OBJECTIVE=?, KEYWORDS=?, RUBRICS=?, QUESTIONORDER=?, ASSESSMENTTEMPLATEID=? WHERE SECTIONID = ?";
  public static final String INSERTSECTION =
    "INSERT INTO SECTION (TITLE, DESCRIPTION, TYPEID, OBJECTIVE, KEYWORDS, RUBRICS, QUESTIONORDER, RANDOMPOOLID, RANDOMNUMBER, TEMPLATEID, ISTEMPLATE, SECTIONID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
  public static final String INSERTASSESSMENTSECTION =
    "INSERT INTO ASSESSMENTSECTION (ASSESSMENTID, SECTIONID, POSITION) VALUES (?,?,?)";
  public static final String UPDATEASSESSMENTSECTION =
    "UPDATE ASSESSMENTSECTION SET POSITION = ? WHERE ASSESSMENTID =? AND SECTIONID=?";
  public static final String UPDATESECTION =
    "UPDATE SECTION SET TITLE=?, DESCRIPTION=?, TYPEID=?, OBJECTIVE=?, KEYWORDS=?, RUBRICS=?, QUESTIONORDER=?, RANDOMPOOLID=?, RANDOMNUMBER=?, TEMPLATEID=? WHERE SECTIONID = ?";
  public static final String DELETEASSESSMENTSECTION =
    "DELETE FROM ASSESSMENTSECTION WHERE SECTIONID = ?";
  public static final String DELETESECTION =
    "DELETE FROM SECTION WHERE SECTIONID = ?";
  public static final String DELETEPROPS =
    "DELETE FROM SECTIONTEMPLATE WHERE SECTIONID = ?";
  public static final String INSERTPROPS =
    "INSERT INTO SECTIONTEMPLATE (SECTIONID, FIELD, ISINSTRUCTOREDITABLE, ISINSTRUCTORVIEWABLE, ISSTUDENTVIEWABLE) VALUES (?,?,?,?,?)";
  public static final String GETPROPS =
    "SELECT * FROM SECTIONTEMPLATE WHERE SECTIONID = ?";
  public static final String GETMEDIA =
    "SELECT MEDIAID FROM SECTIONMEDIA WHERE SECTIONID = ? ORDER BY POSITION";
  public static final String DELETEMEDIA =
    "DELETE FROM SECTIONMEDIA WHERE SECTIONID = ?";
  public static final String INSERTMEDIA =
    "INSERT INTO SECTIONMEDIA (SECTIONID, MEDIAID, POSITION) VALUES(?,?,?)";
  public static final String GETITEMIDS =
    "SELECT ITEMID FROM SECTIONITEM WHERE SECTIONID = ?";
  public static final String GETPOOLNAME =
    "SELECT TITLE FROM QUESTIONPOOL WHERE QUESTIONPOOLID = ?";
}
