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

package org.sakaiproject.tool.assessment.data.dao.questionpool;

import org.navigoproject.business.entity.ItemTemplate;
import org.navigoproject.business.entity.assessment.model.Answer;
import org.navigoproject.business.entity.assessment.model.ItemImpl;
import org.navigoproject.business.entity.assessment.model.ItemIteratorImpl;
import org.navigoproject.business.entity.assessment.model.ItemPropertiesImpl;
import org.navigoproject.business.entity.assessment.model.ItemTemplateImpl;
import org.navigoproject.business.entity.assessment.model.ItemTemplateIteratorImpl;
import org.navigoproject.business.entity.assessment.model.ItemTemplatePropertiesImpl;
import org.navigoproject.business.entity.assessment.model.MediaData;
import org.navigoproject.data.GenericConnectionManager;
import org.navigoproject.osid.OsidManagerFactory;
import org.navigoproject.osid.assessment.impl.AssessmentManagerImpl;
import org.navigoproject.data.IdHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import org.apache.log4j.Logger;

import osid.assessment.Item;
import osid.assessment.AssessmentManager;

import osid.shared.Id;
import osid.shared.SharedManager;

/**
 * DOCUMENTATION PENDING
 *
 * @author $author$
 * @version $Id: ItemAccessObject.java,v 1.1 2004/10/04 17:55:51 daisyf.stanford.edu Exp $
 */
public class ItemAccessObject
{
  GenericConnectionManager gcm = null;
  static Logger LOG = Logger.getLogger(ItemAccessObject.class.getName());
  static final int SECTION = 0;
  static final int POOL = 1;

  /**
   * Creates a new ItemAccessObject object.
   */
  public ItemAccessObject()
  {
    gcm = GenericConnectionManager.getInstance();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param sectionId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ItemIteratorImpl getAllItems(long sectionId)
  {
    return getAllItems(sectionId, SECTION);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param id DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ItemImpl getItem(long id)
  {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try
    {
      conn = gcm.getConnection();
      stmt = conn.prepareStatement(GETITEM);
      stmt.setLong(1, id);
      rs = stmt.executeQuery();
      ItemImpl item = null;
      SharedManager sm = OsidManagerFactory.createSharedManager();
      while(rs.next())
      {
        item = new ItemImpl(sm.getId(rs.getString("ITEMID")));
        item.updateDisplayName(rs.getString("TITLE"));
        item.updateDescription(rs.getString("DESCRIPTION"));
        ItemPropertiesImpl props = new ItemPropertiesImpl();
        item.updateData(getItemProperties(props, rs));
      }

      return item;
    }
    catch(Exception e)
    {
      LOG.error(e); 
//      throw new Error(e);
    }
    finally
    {
      try
      {
        rs.close();
      }
      catch(Exception e)
      {
        LOG.error("Resultset did not close.");
      }

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
//        LOG.error("Failed to Free Connection.");
      }
    }
    return null;
  }


  /**
   * DOCUMENTATION PENDING
   *
   * @param id DOCUMENTATION PENDING
   * @param source DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ItemIteratorImpl getAllItems(long id, int source)
  {
    Connection conn = null;
    PreparedStatement stmt = null;
    PreparedStatement substmt = null;
    ResultSet rs = null;
    ResultSet rs2 = null;
    Collection items = new ArrayList();
    try
    {
      conn = gcm.getConnection();
      LOG.debug(
        "Section = " + SECTION + ", Pool = " + POOL + ", source = " + source);
      if(source == SECTION)
      {
        stmt = conn.prepareStatement(GETITEMIDS);
      }
      else if(source == POOL)
      {
        stmt = conn.prepareStatement(GETPOOLIDS);
      }

      substmt = conn.prepareStatement(GETITEM);
      stmt.setLong(1, id);
      LOG.debug("ID = " + id);
      rs = stmt.executeQuery();
      SharedManager sm = OsidManagerFactory.createSharedManager();
      while(rs.next())
      {
        LOG.debug("Got here.");
        if(source == POOL){
        AssessmentManager assessmentManager = new AssessmentManagerImpl();
        Item item = assessmentManager.getItem(IdHelper.stringToId(rs.getString("ITEMID")));
        items.add(item);

        }else{
        substmt.setLong(1, rs.getLong("ITEMID"));
        rs2 = substmt.executeQuery();
        while(rs2.next())
        {
          LOG.debug("Found item: " + rs2.getString("TEXT"));
          ItemImpl item = new ItemImpl(sm.getId(rs2.getString("ITEMID")));
          item.updateDisplayName(rs2.getString("TITLE"));
          item.updateDescription(rs2.getString("DESCRIPTION"));
          ItemPropertiesImpl props = new ItemPropertiesImpl();
          item.updateData(getItemProperties(props, rs2));
          items.add(item);
        }
        } //End If not POOL
      }
    }
    catch(Exception e)
    {
      LOG.error(e); // Do *NOT* throw an exception, this is not a fatal error.
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

    return new ItemIteratorImpl(items);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param assessmentId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ItemTemplateIteratorImpl getAllTemplates(long assessmentId)
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
        ItemTemplateImpl template =
          new ItemTemplateImpl(sm.getId(rs.getString("ITEMID")));
        ItemTemplatePropertiesImpl props = new ItemTemplatePropertiesImpl();
        template.updateData(getItemProperties(props, rs));
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

    return new ItemTemplateIteratorImpl(templates);
  }

  /**
   * Parent id is an assessment id for a template, and a section id for an
   * item.
   *
   * @param item DOCUMENTATION PENDING
   * @param parentId DOCUMENTATION PENDING
   * @param position DOCUMENTATION PENDING
   */
  public void saveItem(Item item, long parentId, int position)
  {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try
    {
      boolean isTemplate = false;
      if(item instanceof ItemTemplate)
      {
        isTemplate = true;
      }

      conn = gcm.getConnection();
      long id = new Long(((Id) item.getId()).getIdString()).longValue();
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
      if(item.getItemType() != null)
      {
        stmt = conn.prepareStatement(GETTYPEID);
        stmt.setString(1, item.getItemType().getDescription());
        stmt.setString(2, "item");
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
          stmt = conn.prepareStatement(UPDATESECTIONITEM);
          stmt.setLong(1, position);
          stmt.setLong(2, parentId);
          stmt.setLong(3, id);
          stmt.executeUpdate();
          stmt.close();

          stmt = conn.prepareStatement(UPDATEITEM);
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
          stmt = conn.prepareStatement(INSERTSECTIONITEM);
          stmt.setLong(1, parentId);
          stmt.setLong(2, id);
          stmt.setLong(3, position);
          stmt.executeUpdate();
          stmt.close();

          stmt = conn.prepareStatement(INSERTITEM);
        }
      }

      stmt.setString(i++, item.getDisplayName());
      ItemPropertiesImpl props = (ItemPropertiesImpl) item.getData();
      stmt.setString(i++, props.getText());
      stmt.setString(i++, item.getDescription());
      stmt.setInt(i++, typeid);
      stmt.setString(i++, props.getObjectives());
      stmt.setString(i++, props.getKeywords());
      stmt.setString(i++, props.getRubrics());
      stmt.setString(i++, props.getValue());
      stmt.setString(i++, props.getHint());
      stmt.setString(i++, props.getFeedback());
      stmt.setString(i++, (props.getPageBreak() ? "T" : "F"));
      stmt.setLong(i++, parentId);

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

      // Delete the links to all of them
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
          MediaData data = (MediaData) iter.next();
          ids[ii++] = UtilAccessObject.saveMedia(data);
        }

        // Create links to the new ones 
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
        ItemTemplatePropertiesImpl tprops = (ItemTemplatePropertiesImpl) props;
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
      else
      {
        saveAnswers(props.getAnswers(), id, conn);
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
   * @param answers DOCUMENTATION PENDING
   * @param itemId DOCUMENTATION PENDING
   * @param conn DOCUMENTATION PENDING
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void saveAnswers(Collection answers, long itemId, Connection conn)
    throws Exception
  {
    PreparedStatement stmt = null;
    ResultSet rs = null;
    int i = 0;

    Iterator iter = answers.iterator();
    while(iter.hasNext())
    {
      Answer answer = (Answer) iter.next();
      if(answer.getText() != null)
      {
        long id = answer.getId();
        if(id == 0)
        {
          id = new Long(getAnswerId().toString()).longValue();
        }

        stmt = conn.prepareStatement(ISINSERTANSWER);
        stmt.setLong(1, id);
        rs = stmt.executeQuery();
        boolean isUpdate = false;
        if(rs.next())
        {
          isUpdate = true;
        }

        stmt.close();

        if(isUpdate)
        {
          stmt = conn.prepareStatement(UPDATEITEMANSWER);
          stmt.setLong(1, ++i);
          stmt.setLong(2, itemId);
          stmt.setLong(3, id);
          stmt.executeUpdate();
          stmt.close();

          stmt = conn.prepareStatement(UPDATEANSWER);
        }
        else
        {
          stmt = conn.prepareStatement(INSERTITEMANSWER);
          stmt.setLong(1, itemId);
          stmt.setLong(2, id);
          stmt.setLong(3, ++i);
          stmt.executeUpdate();
          stmt.close();

          stmt = conn.prepareStatement(INSERTANSWER);
        }

        stmt.setString(1, answer.getText());
        stmt.setString(2, (answer.getIsCorrect() ? "T" : "F"));
        stmt.setString(3, answer.getValue());
        stmt.setString(4, answer.getFeedback());
        stmt.setLong(5, id);
        stmt.executeUpdate();
        stmt.close();
      }
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param itemId DOCUMENTATION PENDING
   */
  public void deleteItem(long itemId)
  {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;

    try
    {
      conn = gcm.getConnection();

      // Delete links to section
      stmt = conn.prepareStatement(DELETESECTIONITEM);
      stmt.setLong(1, itemId);
      stmt.executeUpdate();
      stmt.close();

      // Stop here if the item is in another assessment or a question pool.
      if(! checkItem(itemId))
      {
        return;
      }

      // Delete the actual item
      stmt = conn.prepareStatement(DELETEITEM);
      stmt.setLong(1, itemId);
      stmt.executeUpdate();
      stmt.close();

      // Delete all answers to item
      Collection answers = new ArrayList();
      stmt = conn.prepareStatement(GETANSWERIDS);
      stmt.setLong(1, itemId);
      rs = stmt.executeQuery();
      while(rs.next())
      {
        Answer answer = new Answer();
        answer.setId(rs.getInt("ANSWERID"));
        answers.add(answer);
      }

      stmt.close();
      deleteAnswers(answers);

      // Delete media 
      stmt = conn.prepareStatement(DELETEMEDIA);
      stmt.setLong(1, itemId);
      stmt.executeUpdate();
      stmt.close();

      stmt = conn.prepareStatement(GETMEDIA);
      stmt.setLong(1, itemId);
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
   * @param answers DOCUMENTATION PENDING
   */
  public void deleteAnswers(Collection answers)
  {
    Connection conn = null;
    PreparedStatement stmt = null;
    PreparedStatement substmt = null;
    try
    {
      conn = gcm.getConnection();

      stmt = conn.prepareStatement(DELETEITEMANSWER);
      substmt = conn.prepareStatement(DELETEANSWER);

      Iterator iter = answers.iterator();
      while(iter.hasNext())
      {
        Answer answer = (Answer) iter.next();
        stmt.setLong(1, answer.getId());
        stmt.executeUpdate();

        substmt.setLong(1, answer.getId());
        substmt.executeUpdate();
      }

      stmt.close();
      substmt.close();
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
        substmt.close();
      }
      catch(Exception e)
      {
        LOG.error("Substmt did not close.");
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
  public ItemPropertiesImpl getItemProperties(
    ItemPropertiesImpl props, ResultSet rs)
  {
    Connection conn = null;
    PreparedStatement stmt = null;
    PreparedStatement substmt = null;
    ResultSet rs2 = null;
    ResultSet rs3 = null;
    try
    {
      props.setText(rs.getString("TEXT"));
      props.setObjectives(rs.getString("OBJECTIVE"));
      props.setKeywords(rs.getString("KEYWORDS"));
      props.setRubrics(rs.getString("RUBRICS"));
      props.setValue(rs.getString("VALUE"));
      props.setHint(rs.getString("HINT"));
      props.setFeedback(rs.getString("FEEDBACK"));
      if(rs.getString("ADDPAGEBREAK") != null)
      {
        props.setPageBreak(
          (rs.getString("ADDPAGEBREAK").equals("T") ? true : false));
      }

      props.setItemType(UtilAccessObject.getType(rs.getInt("TYPEID")));

      conn = gcm.getConnection();

      stmt = conn.prepareStatement(GETMEDIA);
      stmt.setLong(1, rs.getLong("ITEMID"));
      rs2 = stmt.executeQuery();
      while(rs2.next())
      {
        props.addMedia(UtilAccessObject.getMediaData(rs2.getInt("MEDIAID")));
      }

      stmt.close();

      stmt = conn.prepareStatement(GETANSWERIDS);
      stmt.setLong(1, rs.getLong("ITEMID"));
      substmt = conn.prepareStatement(GETANSWER);
      Collection answers = new ArrayList();
      rs2 = stmt.executeQuery();
      while(rs2.next())
      {
        substmt.setLong(1, rs2.getLong("ANSWERID"));
        rs3 = substmt.executeQuery();
        while(rs3.next())
        {
          Answer answer = new Answer();
          answer.setId(rs3.getLong("ANSWERID"));
          answer.setText(rs3.getString("TEXT"));
          answer.setIsCorrect(
            (rs3.getString("ISCORRECT").equals("T") ? true : false));
          answer.setValue(rs3.getString("VALUE"));
          answer.setFeedback(rs3.getString("FEEDBACK"));
          answers.add(answer);
        }
      }

      substmt.close();
      stmt.close();
      props.setAnswers(answers);

      if(props instanceof ItemTemplatePropertiesImpl)
      {
        ItemTemplatePropertiesImpl tprops = (ItemTemplatePropertiesImpl) props;
        stmt = conn.prepareStatement(GETPROPS);
        stmt.setInt(1, rs.getInt("ITEMID"));
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
   * This checks to see if an item can be deleted.  Always check before
   * deleting an item!!!
   *
   * @param itemId DOCUMENTATION PENDING
   *
   * @return True if it can be deleted, false if it is being used by something
   *         else.
   */
  public boolean checkItem(long itemId)
  {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try
    {
      conn = gcm.getConnection();
      stmt = conn.prepareStatement(ITEMINQP);
      stmt.setLong(1, itemId);
      rs = stmt.executeQuery();
      if(rs.next())
      {
        return false;
      }

      stmt.close();
      stmt = conn.prepareStatement(ITEMINS);
      stmt.setLong(1, itemId);
      rs = stmt.executeQuery();
      if(rs.next())
      {
        return false;
      }

      return true;
    }
    catch(Exception e)
    {
      LOG.error(e);

      return false;
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
   * @return DOCUMENTATION PENDING
   */
  public synchronized Id getItemId()
  {
    return UtilAccessObject.getNewId("ITEM");
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public synchronized Id getAnswerId()
  {
    return UtilAccessObject.getNewId("ANSWER");
  }

  public static final String GETITEMIDS =
    "SELECT ITEMID FROM SECTIONITEM WHERE SECTIONID = ? ORDER BY POSITION";
  public static final String GETPOOLIDS =
    "SELECT ITEMID FROM QUESTIONPOOLITEM WHERE QUESTIONPOOLID = ?";
  public static final String GETITEM = "SELECT * FROM ITEM WHERE ITEMID = ?";
  public static final String GETALLTEMPLATES =
    "SELECT * FROM ITEM WHERE ISTEMPLATE = 'T' AND ASSESSMENTTEMPLATEID = ?";
  public static final String ISUPDATETEMPLATE =
    "SELECT ITEMID FROM ITEM WHERE ITEMID = ?";
  public static final String GETTYPEID =
    "SELECT TYPEID FROM STANFORDTYPE WHERE DESCRIPTION=? AND KEYWORD=?";
  public static final String INSERTSECTIONITEM =
    "INSERT INTO SECTIONITEM (SECTIONID, ITEMID, POSITION) VALUES (?,?,?)";
  public static final String INSERTTEMPLATE =
    "INSERT INTO ITEM (TITLE, TEXT, DESCRIPTION, TYPEID, OBJECTIVE, KEYWORDS, RUBRICS, VALUE, HINT, FEEDBACK, ADDPAGEBREAK, ASSESSMENTTEMPLATEID, ISTEMPLATE, ITEMID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
  public static final String UPDATETEMPLATE =
    "UPDATE ITEM SET TITLE=?, TEXT=?, DESCRIPTION=?, TYPEID=?, OBJECTIVE=?, KEYWORDS=?, RUBRICS=?, VALUE=?, HINT=?, FEEDBACK=?, ADDPAGEBREAK=?, ASSESSMENTTEMPLATEID=? WHERE ITEMID = ?";
  public static final String INSERTITEM =
    "INSERT INTO ITEM (TITLE, TEXT, DESCRIPTION, TYPEID, OBJECTIVE, KEYWORDS, RUBRICS, VALUE, HINT, FEEDBACK, ADDPAGEBREAK, TEMPLATEID, ISTEMPLATE, ITEMID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
  public static final String UPDATEITEM =
    "UPDATE ITEM SET TITLE=?, TEXT=?, DESCRIPTION=?, TYPEID=?, OBJECTIVE=?, KEYWORDS=?, RUBRICS=?, VALUE=?, HINT=?, FEEDBACK=?, ADDPAGEBREAK=?, TEMPLATEID=? WHERE ITEMID = ?";
  public static final String UPDATESECTIONITEM =
    "UPDATE SECTIONITEM SET POSITION = ? WHERE SECTIONID = ? AND ITEMID = ?";
  public static final String DELETESECTIONITEM =
    "DELETE FROM SECTIONITEM WHERE ITEMID = ?";
  public static final String DELETEITEM = "DELETE FROM ITEM WHERE ITEMID = ?";
  public static final String GETANSWERIDS =
    "SELECT ANSWERID FROM ITEMANSWER WHERE ITEMID = ? ORDER BY POSITION";
  public static final String GETANSWER =
    "SELECT * FROM ANSWER WHERE ANSWERID = ?";
  public static final String INSERTITEMANSWER =
    "INSERT INTO ITEMANSWER (ITEMID, ANSWERID, POSITION) VALUES (?,?,?)";
  public static final String UPDATEITEMANSWER =
    "UPDATE ITEMANSWER SET POSITION = ? WHERE ITEMID = ? AND ANSWERID = ?";
  public static final String ISINSERTANSWER =
    "SELECT ANSWERID FROM ANSWER WHERE ANSWERID = ?";
  public static final String DELETEITEMANSWER =
    "DELETE FROM ITEMANSWER WHERE ANSWERID = ?";
  public static final String DELETEANSWER =
    "DELETE FROM ANSWER WHERE ANSWERID = ?";
  public static final String INSERTANSWER =
    "INSERT INTO ANSWER (TEXT, ISCORRECT, VALUE, FEEDBACK, ANSWERID) VALUES (?,?,?,?,?)";
  public static final String UPDATEANSWER =
    "UPDATE ANSWER SET TEXT=?, ISCORRECT=?, VALUE=?, FEEDBACK=? WHERE ANSWERID = ?";
  public static final String DELETEPROPS =
    "DELETE FROM ITEMTEMPLATE WHERE ITEMID = ?";
  public static final String INSERTPROPS =
    "INSERT INTO ITEMTEMPLATE (ITEMID, FIELD, ISINSTRUCTOREDITABLE, ISINSTRUCTORVIEWABLE, ISSTUDENTVIEWABLE) VALUES (?,?,?,?,?)";
  public static final String GETPROPS =
    "SELECT * FROM ITEMTEMPLATE WHERE ITEMID = ?";
  public static final String GETMEDIA =
    "SELECT MEDIAID FROM ITEMMEDIA WHERE ITEMID = ? ORDER BY POSITION";
  public static final String DELETEMEDIA =
    "DELETE FROM ITEMMEDIA WHERE ITEMID = ?";
  public static final String INSERTMEDIA =
    "INSERT INTO ITEMMEDIA (ITEMID, MEDIAID, POSITION) VALUES(?,?,?)";
  public static final String ITEMINQP =
    "SELECT ITEMID FROM QUESTIONPOOLITEM WHERE ITEMID = ?";
  public static final String ITEMINS =
    "SELECT ITEMID FROM SECTIONITEM WHERE ITEMID = ?";
}
