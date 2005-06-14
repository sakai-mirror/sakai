
/*
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
 */


package org.sakaiproject.tool.assessment.business.entity.helper.section;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xerces.dom.ElementImpl;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import org.sakaiproject.tool.assessment.business.entity.asi.Section;
import org.sakaiproject.tool.assessment.business.entity.helper.AuthoringHelper;
import org.sakaiproject.tool.assessment.business.entity.helper.item.ItemHelper;

/**
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organization: Sakai Project</p>
 * @author rshastri
 * @author Ed Smiley esmiley@stanford.edu
 * @version $Id: SectionHelper.java,v 1.3 2005/01/06 01:27:48 esmiley.stanford.edu Exp $
 */

public class SectionHelper
//  extends Action
{
  /**
   *
   */
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(SectionHelper.class);

  private Document doc;

  /**
   * DOCUMENTATION PENDING
   *
   * @param sectionID DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
//  public String addNewItem(
//    String sectionID, org.navigoproject.business.entity.Item itemXml)
//  {
//    if(LOG.isDebugEnabled())
//    {
//      LOG.debug("addItem( String " + sectionID + ")");
//    }
//
//    String ident = null;
//    try
//    {
//      AuthoringHelper authoringHelper = new AuthoringHelper();
//      Id secid = authoringHelper.getId(sectionID);
//      AssessmentManagerImpl assessmentManager = new AssessmentManagerImpl();
//      Section section = assessmentManager.getSection(secid);
//      Item item;
//      item =
//        assessmentManager.createItem("Item", "QTIxml", TypeLib.DR_QTI_ITEM);
//      ident = item.getId().getIdString();
//      itemXml.update("item/@ident", ident);
//      item.updateData(itemXml);
//      section.addItem(item);
//      Section sectionXml =
//        (Section) section.getData();
//    }
//    catch(AssessmentException e)
//    {
//      LOG.error(e.getMessage(), e);
//    }
//    catch(SharedException e)
//    {
//      LOG.error(e.getMessage(), e);
//    }
//    catch(DOMException e)
//    {
//      LOG.error(e.getMessage(), e);
//    }
//    catch(Exception e)
//    {
//      LOG.error(e.getMessage(), e);
//    }
//
//    return ident;
//  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param inputStream DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Section readXMLDocument(
    InputStream inputStream)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("readDocument(InputStream " + inputStream);
    }

    Section sectionXml = null;
    try
    {
      AuthoringHelper authoringHelper = new AuthoringHelper();
      sectionXml =
        new Section(
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
//  public void addExistingItemToSection(String sectionID, String itemIdent)
//  {
//    try
//    {
//      if(LOG.isDebugEnabled())
//      {
//        LOG.debug(
//          "addExistingItemToSection(  String" + sectionID + ",String" +
//          itemIdent + ")");
//      }
//
//      AssessmentManagerImpl assessmentManager = new AssessmentManagerImpl();
//      AuthoringHelper authoringHelper = new AuthoringHelper();
//      Id secid = authoringHelper.getId(sectionID);
//      Id itemId = authoringHelper.getId(itemIdent);
//      org.navigoproject.business.entity.Item itemXml = null;
//      Section section;
//      Item item;
//      section = assessmentManager.getSection(secid);
//      item = assessmentManager.getItem(itemId);
//      section.addItem(item);
//    }
//    catch(AssessmentException e)
//    {
//      LOG.error(e.getMessage(), e);
//    }
//  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param items DOCUMENTATION PENDING
   * @param fromSection DOCUMENTATION PENDING
   * @param toSection DOCUMENTATION PENDING
   */
//  public void moveItemsBetweenSections(
//    String[] items, String fromSection, String toSection)
//  {
//    if(LOG.isDebugEnabled())
//    {
//      LOG.debug(
//        "moveItems(  String[]" + items + ",String" + fromSection + ",String" +
//        toSection + ")");
//    }
//
//    // move item from one to another section
//    for(int i = 0; i < items.length; i++)
//    {
//      String itemIdent = items[i];
//      addExistingItemToSection(toSection, itemIdent);
//      removeItemFromSection(fromSection, itemIdent);
//    }
//  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param sectionID DOCUMENTATION PENDING
   * @param itemIdent DOCUMENTATION PENDING
   */
//  public void removeItemFromSection(String sectionID, String itemIdent)
//  {
//    try
//    {
//      if(LOG.isDebugEnabled())
//      {
//        LOG.debug(
//          "removeItemFromSection(  String" + sectionID + ",String" + itemIdent +
//          ")");
//      }
//
//      AuthoringHelper authoringHelper = new AuthoringHelper();
//      Section section = getSection(sectionID);
//      /** @todo */
//
//      Id itemId = authoringHelper.getId(itemIdent);
//      section.removeItem(itemId);
//    }
//    catch(AssessmentException e)
//    {
//      LOG.error(e.getMessage(), e);
//    }
//  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param sec_str_id DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
//  public Section getSection(String sec_str_id)
//  {
//    if(LOG.isDebugEnabled())
//    {
//      LOG.debug("getSection(String" + sec_str_id + ")");
//    }
//
//    Section section = null;
//    Section sectionXml = null;
//    try
//    {
//      AuthoringHelper authoringHelper = new AuthoringHelper();
//      AssessmentManagerImpl assessmentManager = new AssessmentManagerImpl();
//      /** @ todo */
//      Id secid = authoringHelper.getId(sec_str_id);
//      if(secid != null)
//      {
//        section = assessmentManager.getSection(secid);
//      }
//    }
//      catch(AssessmentException e)
//    {
//      LOG.error(e.getMessage(), e);
//    }
//
//    return section;
//  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param sectionID DOCUMENTATION PENDING
   * @param itemID DOCUMENTATION PENDING
   * @param newIndexNo DOCUMENTATION PENDING
   */
//  public void insertItemInSection(
//    String sectionID, String itemID, int newIndexNo)
//  {
//    if(LOG.isDebugEnabled())
//    {
//      LOG.debug(
//        "insertItemInSection(String " + sectionID + ", String" + itemID +
//        ", int" + newIndexNo + ")");
//    }
//
//    Section sectionXml =
//      getSectionXml(sectionID);
//    Section section = getSection(sectionID);
//    ArrayList itemArray = getSectionItems(sectionID, true);
//
//    if(sectionXml != null)
//    {
//      itemArray.add(newIndexNo, itemID);
//      sectionXml.orderItemRefs(itemArray);
//      try
//      {
//        section.updateData(sectionXml);
//      }
//      catch(AssessmentException e)
//      {
//        LOG.error(e.getMessage(), e);
//      }
//    }
//  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param sectionID DOCUMENTATION PENDING
   * @param indexNo DOCUMENTATION PENDING
   */
//  public void moveLastItemToPosition(String sectionID, int indexNo)
//  {
//    if(LOG.isDebugEnabled())
//    {
//      LOG.debug(
//        "moveLastItemToPosition(String " + sectionID + ", String" + indexNo +
//        ")");
//    }
//
//    Section sectionXml =
//      getSectionXml(sectionID);
//    Section section = getSection(sectionID);
//    ArrayList itemArray = getSectionItems(sectionID, true);
//    String itemID = null;
//    if(
//      (indexNo != 0) && (itemArray != null) && (indexNo < itemArray.size()) &&
//        (sectionXml != null))
//    {
//      itemID = (String) itemArray.get((itemArray.size() - 1));
//      itemArray.add(indexNo, itemID);
//      itemArray.remove((itemArray.size() - 1));
//      sectionXml.orderItemRefs(itemArray);
//      try
//      {
//        section.updateData(sectionXml);
//      }
//      catch(AssessmentException e)
//      {
//        LOG.error(e.getMessage(), e);
//      }
//    }
//  }

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
    Section sectionXml =
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
/** @todo */
//        if(itemID != null)
//        {
//          org.navigoproject.business.entity.Item itemXml =
//            (org.navigoproject.business.entity.Item) itemHelper.getItemXml(itemID);
//          List itemList = itemXml.selectNodes("item");
//          if(itemList.size() > 0)
//          {
//            arrList.add(itemList.get(0));
//          }
//        }
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
  public Section getSectionXml(
    String sec_str_id)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getSection(String" + sec_str_id + ")");
    }
    /** @todo
     *
     */

//    Section section = getSection(sec_str_id);
    Section sectionXml = null;
//    Section sectionXml = null;
//    try
//    {
//      sectionXml =
//        (Section) section.getData();
//    }
//    catch(AssessmentException e)
//    {
//      LOG.error(e.getMessage(), e);
//    }

    return sectionXml;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param sectionID DOCUMENTATION PENDING
   * @param sectionXml DOCUMENTATION PENDING
   */
  public void sectionUpdateData(
    String sectionID, Section sectionXml)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "sectionUpdateData(String " + sectionID +
        ", Section" + sectionXml + ")");
    }

    AuthoringHelper authoringHelper = new AuthoringHelper();

    /** @todo */
//    Section section;
//    Id secId = authoringHelper.getId(sectionID);
    try
    {
//      AssessmentManagerImpl assessmentManager = new AssessmentManagerImpl();
//      section = assessmentManager.getSection(secId);
//      section.updateData(sectionXml);
    }
    catch(Exception e)
//      catch(AssessmentException e)
    {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * @param secIdent
   * @param request
   */
  public void setSectionDocument(
    Section secXml, HttpServletRequest request)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "setSectionDocument(Section secXml " +
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

    doc = document;
//      this.saveDocument(request, document);
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
  public Section updateSectionXml(
    Section sectionXml, String xpath,
    String value)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "updateSectionXml(Item " +
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
      /** @todo */
//			addExistingItemToSection(toSection, itemIdent);
//			removeItemFromSection(fromSection, itemIdent);
		}
  	}
  }
}
