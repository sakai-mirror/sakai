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

/*
 * Created on Dec 23, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.author.section;

import org.navigoproject.osid.TypeLib;
import org.navigoproject.osid.assessment.impl.AssessmentManagerImpl;
import org.navigoproject.ui.web.asi.author.AuthoringHelper;
import org.navigoproject.ui.web.asi.author.item.ItemHelper;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import javax.xml.parsers.ParserConfigurationException;

import com.oroad.stxx.action.Action;

import org.apache.xerces.dom.ElementImpl;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;

import org.xml.sax.SAXException;

import osid.assessment.AssessmentException;
import osid.assessment.Item;
import osid.assessment.Section;

import osid.shared.Id;
import osid.shared.SharedException;

/**
 * @author rshastri
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SectionHelper
  extends Action
{
  /**
   *
   */
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(SectionHelper.class);

  /**
   * DOCUMENTATION PENDING
   *
   * @param sectionID DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String addNewItem(
    String sectionID, org.navigoproject.business.entity.Item itemXml)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("addItem( String " + sectionID + ")");
    }

    String ident = null;
    try
    {
      AuthoringHelper authoringHelper = new AuthoringHelper();
      Id secid = authoringHelper.getId(sectionID);
      AssessmentManagerImpl assessmentManager = new AssessmentManagerImpl();
      Section section = assessmentManager.getSection(secid);
      Item item;
      item =
        assessmentManager.createItem("Item", "QTIxml", TypeLib.DR_QTI_ITEM);
      ident = item.getId().getIdString();
      itemXml.update("item/@ident", ident);
      item.updateData(itemXml);
      section.addItem(item);
      org.navigoproject.business.entity.Section sectionXml =
        (org.navigoproject.business.entity.Section) section.getData();
    }
    catch(AssessmentException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(SharedException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(DOMException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
    }

    return ident;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param inputStream DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public org.navigoproject.business.entity.Section readXMLDocument(
    InputStream inputStream)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("readDocument(InputStream " + inputStream);
    }

    org.navigoproject.business.entity.Section sectionXml = null;
    try
    {
      AuthoringHelper authoringHelper = new AuthoringHelper();
      sectionXml =
        new org.navigoproject.business.entity.Section(
          authoringHelper.readXMLDocument(inputStream).getDocument());
    }
    catch(ParserConfigurationException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(SAXException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(IOException e)
    {
      LOG.error(e.getMessage(), e);
    }

    return sectionXml;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param sectionID DOCUMENTATION PENDING
   * @param itemIdent DOCUMENTATION PENDING
   */
  public void addExistingItemToSection(String sectionID, String itemIdent)
  {
    try
    {
      if(LOG.isDebugEnabled())
      {
        LOG.debug(
          "addExistingItemToSection(  String" + sectionID + ",String" +
          itemIdent + ")");
      }

      AssessmentManagerImpl assessmentManager = new AssessmentManagerImpl();
      AuthoringHelper authoringHelper = new AuthoringHelper();
      Id secid = authoringHelper.getId(sectionID);
      Id itemId = authoringHelper.getId(itemIdent);
      org.navigoproject.business.entity.Item itemXml = null;
      Section section;
      Item item;
      section = assessmentManager.getSection(secid);
      item = assessmentManager.getItem(itemId);
      section.addItem(item);
    }
    catch(AssessmentException e)
    {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param items DOCUMENTATION PENDING
   * @param fromSection DOCUMENTATION PENDING
   * @param toSection DOCUMENTATION PENDING
   */
  public void moveItemsBetweenSections(
    String[] items, String fromSection, String toSection)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "moveItems(  String[]" + items + ",String" + fromSection + ",String" +
        toSection + ")");
    }

    // move item from one to another section
    for(int i = 0; i < items.length; i++)
    {
      String itemIdent = items[i];
      addExistingItemToSection(toSection, itemIdent);
      removeItemFromSection(fromSection, itemIdent);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param sectionID DOCUMENTATION PENDING
   * @param itemIdent DOCUMENTATION PENDING
   */
  public void removeItemFromSection(String sectionID, String itemIdent)
  {
    try
    {
      if(LOG.isDebugEnabled())
      {
        LOG.debug(
          "removeItemFromSection(  String" + sectionID + ",String" + itemIdent +
          ")");
      }

      AuthoringHelper authoringHelper = new AuthoringHelper();
      Section section = getSection(sectionID);
      Id itemId = authoringHelper.getId(itemIdent);
      section.removeItem(itemId);
    }
    catch(AssessmentException e)
    {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param sec_str_id DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Section getSection(String sec_str_id)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getSection(String" + sec_str_id + ")");
    }

    Section section = null;
    org.navigoproject.business.entity.Section sectionXml = null;
    try
    {
      AuthoringHelper authoringHelper = new AuthoringHelper();
      AssessmentManagerImpl assessmentManager = new AssessmentManagerImpl();
      Id secid = authoringHelper.getId(sec_str_id);
      if(secid != null)
      {
        section = assessmentManager.getSection(secid);
      }
    }
    catch(AssessmentException e)
    {
      LOG.error(e.getMessage(), e);
    }

    return section;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param sectionID DOCUMENTATION PENDING
   * @param itemID DOCUMENTATION PENDING
   * @param newIndexNo DOCUMENTATION PENDING
   */
  public void insertItemInSection(
    String sectionID, String itemID, int newIndexNo)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "insertItemInSection(String " + sectionID + ", String" + itemID +
        ", int" + newIndexNo + ")");
    }

    org.navigoproject.business.entity.Section sectionXml =
      getSectionXml(sectionID);
    Section section = getSection(sectionID);
    ArrayList itemArray = getSectionItems(sectionID, true);

    if(sectionXml != null)
    {
      itemArray.add(newIndexNo, itemID);
      sectionXml.orderItemRefs(itemArray);
      try
      {
        section.updateData(sectionXml);
      }
      catch(AssessmentException e)
      {
        LOG.error(e.getMessage(), e);
      }
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param sectionID DOCUMENTATION PENDING
   * @param indexNo DOCUMENTATION PENDING
   */
  public void moveLastItemToPosition(String sectionID, int indexNo)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "moveLastItemToPosition(String " + sectionID + ", String" + indexNo +
        ")");
    }

    org.navigoproject.business.entity.Section sectionXml =
      getSectionXml(sectionID);
    Section section = getSection(sectionID);
    ArrayList itemArray = getSectionItems(sectionID, true);
    String itemID = null;
    if(
      (indexNo != 0) && (itemArray != null) && (indexNo < itemArray.size()) &&
        (sectionXml != null))
    {
      itemID = (String) itemArray.get((itemArray.size() - 1));
      itemArray.add(indexNo, itemID);
      itemArray.remove((itemArray.size() - 1));
      sectionXml.orderItemRefs(itemArray);
      try
      {
        section.updateData(sectionXml);
      }
      catch(AssessmentException e)
      {
        LOG.error(e.getMessage(), e);
      }
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param sectionID DOCUMENTATION PENDING
   * @param itemRefs DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ArrayList getSectionItems(String sectionID, boolean itemRefs)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "getSectionItems(  String" + sectionID + ",boolean" + itemRefs + ")");
    }

    ArrayList arrList = new ArrayList();
    org.navigoproject.business.entity.Section sectionXml =
      getSectionXml(sectionID);
    List list = sectionXml.selectNodes("section/itemref");

    int size = list.size();
    for(int i = 0; i < size; i++)
    {
      ElementImpl element = (ElementImpl) (list.get(i));
      String itemID = element.getAttribute("linkrefid");
      if(itemRefs)// if only references are required
      {
        if(itemID != null)
        {
          arrList.add(itemID);
        }
      }
      else // if the item needs to be exploded
      {
        ItemHelper itemHelper = new ItemHelper();

        if(itemID != null)
        {
          org.navigoproject.business.entity.Item itemXml =
            (org.navigoproject.business.entity.Item) itemHelper.getItemXml(itemID);
          List itemList = itemXml.selectNodes("item");
          if(itemList.size() > 0)
          {
            arrList.add(itemList.get(0));
          }
        }
      }
    }

    return arrList;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param sec_str_id DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public org.navigoproject.business.entity.Section getSectionXml(
    String sec_str_id)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getSection(String" + sec_str_id + ")");
    }

    Section section = getSection(sec_str_id);
    org.navigoproject.business.entity.Section sectionXml = null;
    try
    {
      sectionXml =
        (org.navigoproject.business.entity.Section) section.getData();
    }
    catch(AssessmentException e)
    {
      LOG.error(e.getMessage(), e);
    }

    return sectionXml;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param sectionID DOCUMENTATION PENDING
   * @param sectionXml DOCUMENTATION PENDING
   */
  public void sectionUpdateData(
    String sectionID, org.navigoproject.business.entity.Section sectionXml)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "sectionUpdateData(String " + sectionID +
        ", org.navigoproject.business.entity.Section" + sectionXml + ")");
    }

    AuthoringHelper authoringHelper = new AuthoringHelper();

    Section section;
    Id secId = authoringHelper.getId(sectionID);
    try
    {
      AssessmentManagerImpl assessmentManager = new AssessmentManagerImpl();
      section = assessmentManager.getSection(secId);
      section.updateData(sectionXml);
    }
    catch(AssessmentException e)
    {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * @param secIdent
   * @param request
   */
  public void setSectionDocument(
    org.navigoproject.business.entity.Section secXml, HttpServletRequest request)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "setSectionDocument(org.navigoproject.business.entity.Section secXml " +
        secXml + ", HttpServletRequest" + request + ")");
    }

    Document document = null;
    try
    {
      document = secXml.getDocument();
    }
    catch(ParserConfigurationException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(SAXException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(IOException e)
    {
      LOG.error(e.getMessage(), e);
    }

    this.saveDocument(request, document);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param sectionXml DOCUMENTATION PENDING
   * @param xpath DOCUMENTATION PENDING
   * @param value DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public org.navigoproject.business.entity.Section updateSectionXml(
    org.navigoproject.business.entity.Section sectionXml, String xpath,
    String value)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "updateSectionXml(org.navigoproject.business.entity.Item " +
        sectionXml + ", String" + xpath + ", String" + value + ")");
    }

    try
    {
      sectionXml.update(xpath, value);
    }
    catch(DOMException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
    }

    return sectionXml;
  }

  /**
   * @param sectionID
   * @param sectionIDSelected
   */
  public void moveAllItems(String fromSection, String toSection)
  {
  	ArrayList allItems = getSectionItems(fromSection, true);
  	String itemIdent = null;
  	
  	for(int i=0; i < allItems.size() ; i++ )
  	{
  	itemIdent = (String) allItems.get(i);
  	if(itemIdent != null && itemIdent.trim().length() > 0 )
		{
			addExistingItemToSection(toSection, itemIdent);
			removeItemFromSection(fromSection, itemIdent);
		}
  	}
  }
}
