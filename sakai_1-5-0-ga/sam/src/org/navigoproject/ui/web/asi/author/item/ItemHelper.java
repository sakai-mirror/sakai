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
 * Created on Dec 22, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.author.item;

import org.navigoproject.osid.assessment.impl.AssessmentManagerImpl;
import org.navigoproject.ui.web.asi.author.AuthoringHelper;
import org.navigoproject.ui.web.asi.author.assessment.AssessmentHelper;
import org.navigoproject.ui.web.asi.author.questionpool.QuestionPoolHelper;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import javax.xml.parsers.ParserConfigurationException;

import com.oroad.stxx.action.Action;

import org.apache.struts.action.ActionForm;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;

import org.xml.sax.SAXException;

import osid.assessment.AssessmentException;
import osid.assessment.Item;

import osid.dr.DigitalRepositoryException;

import osid.shared.Id;

/**
 * DOCUMENT ME!
 *
 * @author rshastri To change the template for this generated type comment go
 *         to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 *         Comments
 */
public class ItemHelper
  extends Action
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(ItemHelper.class);

  /**
   *
   */
  public ItemHelper()
  {
    super();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   * @param actionForm DOCUMENTATION PENDING
   */
  public void setActionForm(HttpServletRequest request, ActionForm actionForm)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "setItemDocument( HttpServletRequest " + request + ", ActionForm" +
        actionForm + ")");
    }

    QuestionPoolHelper questionPoolHelper = new QuestionPoolHelper();
    ItemActionForm itemForm = (ItemActionForm) actionForm;
    String sectionID = request.getParameter("SectionIdent");
    itemForm.setAssessTitle(request.getParameter("assessTitle"));
    itemForm.setItemNo(request.getParameter("itemNo"));
    String currentSection = request.getParameter("currentSection");
    itemForm.setCurrentSection(sectionID);
    itemForm.setInsertPosition(request.getParameter("insertPosition"));
    itemForm.setSectionIdent(sectionID);
    String assessID = request.getParameter("assessmentID");
    itemForm.setAssessmentID(assessID);
    itemForm.setPoolsAvailable(questionPoolHelper.getPoolsPlusName(request));
    itemForm.setPoolsSelected(
      questionPoolHelper.getPoolsByItem(request.getParameter("ItemIdent")));
    if((assessID != null) && (assessID.trim().length() > 0))
    {
      AssessmentHelper assessmentHelper = new AssessmentHelper();
      ArrayList arr = assessmentHelper.getSections(assessID);
      itemForm.setAssessmentSectionIdents(arr);
      String collectItemMetadata =
        assessmentHelper.getFieldentry(assessID, "COLLECT_ITEM_METADATA");
      itemForm.setShowMetadata(collectItemMetadata);
      String showOverAllFeedback =
        assessmentHelper.getFieldentry(assessID, "FEEDBACK_DELIVERY");
      itemForm.setShowOverallFeedback(showOverAllFeedback);
      String showQuestionLevelFeedback =
        assessmentHelper.getFieldentry(assessID, "FEEDBACK_SHOW_ITEM_LEVEL");
      itemForm.setShowQuestionLevelFeedback(showQuestionLevelFeedback);
      String showSelectionLevelFeedback =
        assessmentHelper.getFieldentry(
          assessID, "FEEDBACK_SHOW_SELECTION_LEVEL");
      itemForm.setShowSelectionLevelFeedback(showSelectionLevelFeedback);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   * @param itemXml DOCUMENTATION PENDING
   * @param isUpdate DOCUMENTATION PENDING
   */
  public void setItemDocument(
    HttpServletRequest request, org.navigoproject.business.entity.Item itemXml,
    boolean isUpdate)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "setItemDocument( HttpServletRequest " + request +
        ", org.navigoproject.business.entity.Item" + itemXml + ", boolean " +
        isUpdate + ")");
    }

    Document document;
    try
    {
      if(isUpdate)
      {
        itemXml.update();
      }

      document = itemXml.getDocument();
      this.saveDocument(request, document);
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
    catch(DigitalRepositoryException e)
    {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param inputStream DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public org.navigoproject.business.entity.Item readXMLDocument(
    InputStream inputStream)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("readDocument(InputStream " + inputStream);
    }

    org.navigoproject.business.entity.Item itemXml = null;

    try
    {
      AuthoringHelper authoringHelper = new AuthoringHelper();
      itemXml =
        new org.navigoproject.business.entity.Item(
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

    return itemXml;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   * @param itemIdent DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public org.navigoproject.business.entity.Item getItemXml(String itemIdent)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getItemXml( String" + itemIdent + ")");
    }

    org.navigoproject.business.entity.Item itemXml = null;
    try
    {
      Item item = getItem(itemIdent);
      itemXml = (org.navigoproject.business.entity.Item) item.getData();
    }
    catch(AssessmentException e)
    {
      LOG.error(e.getMessage(), e);
    }

    return itemXml;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param itemIdent DOCUMENTATION PENDING
   * @param itemXml DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   */
  public void itemUpdateData(
    String itemIdent, org.navigoproject.business.entity.Item itemXml)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "itemUpdateData(String " + itemIdent +
        "org.navigoproject.business.entity.Item" + itemXml + ")");
    }

    AuthoringHelper authoringHelper = new AuthoringHelper();
    Id itemId = authoringHelper.getId(itemIdent);

    if(itemId != null)
    {
      Item item;
      try
      {
        AssessmentManagerImpl assessmentManager = new AssessmentManagerImpl();
        item = assessmentManager.getItem(itemId);
        item.updateData(itemXml);
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
   * @param itemID DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Item getItem(String itemID)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("getItem(String " + itemID + ")");
    }

    AuthoringHelper authoringHelper = new AuthoringHelper();
    Id itemIdent = authoringHelper.getId(itemID);
    Item item = null;
    try
    {
      AssessmentManagerImpl assessmentManager = new AssessmentManagerImpl();
      item = assessmentManager.getItem(itemIdent);
    }
    catch(AssessmentException e)
    {
      LOG.error(e.getMessage(), e);
    }

    return item;
  }
}
