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

import org.navigoproject.AuthoringConstantStrings;
import org.navigoproject.business.entity.Item;
import org.navigoproject.ui.web.asi.author.AuthoringHelper;
import org.navigoproject.ui.web.asi.author.questionpool.QuestionPoolHelper;
import org.navigoproject.ui.web.asi.author.section.SectionHelper;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.oroad.stxx.action.Action;

import org.w3c.dom.DOMException;

/**
 * DOCUMENT ME!
 *
 * @author rshastri To change the template for this generated type comment go
 *         to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 *         Comments
 */
public class EditItemHelper
  extends Action
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(EditItemHelper.class);

  /**
   * DOCUMENTATION PENDING
   *
   * @param itemXml DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public org.navigoproject.business.entity.Item updateXml(
    org.navigoproject.business.entity.Item itemXml, HttpServletRequest request)
  {
    LOG.info("*itemXml = " + itemXml);
    Map requestMap = request.getParameterMap();
    Set keySet = requestMap.keySet();
    Iterator setIter = keySet.iterator();
    AuthoringHelper authoringHelper = new AuthoringHelper();
    ItemHelper itemHelper = new ItemHelper();
    EditItemHelper editItemHelper = new EditItemHelper();

    // set min value = max value to begin with
    if(
      (
          request.getParameter(
            "stxx/item/resprocessing/outcomes/decvar/@minvalue") != null
        ) &&
        (
          request.getParameter(
            "stxx/item/resprocessing/outcomes/decvar/@minvalue").trim().length() > 0
        ))
    {
      if(
        new Float(
            request.getParameter(
              "stxx/item/resprocessing/outcomes/decvar/@minvalue")).floatValue() > 0)
      {
        itemXml =
          updateItemXml(
            itemXml, "item/resprocessing/outcomes/decvar/@maxvalue",
            request.getParameter(
              "stxx/item/resprocessing/outcomes/decvar/@minvalue"));
      }
    }
    while(setIter.hasNext())
    {
      String key = setIter.next().toString();
      String reqParam = request.getParameter(key);
      if(! (key == null) && ! ((key.length()) == 0) && key.startsWith("stxx"))
      {
        reqParam = request.getParameter(key);
        key = key.substring(5);
        if(reqParam == null)
        {
          reqParam = "''";
        }

        itemXml = updateItemXml(itemXml, key, reqParam);
      }

      // FOR LID QUESTIONS
      if((key != null) && key.equals("correctChoice"))
      {
        // First Set all the other Choices to incorrect
        List noOfrespcondition =
          itemXml.selectNodes("count(/item/resprocessing/respcondition)");
        Double nos = null;
        int numberVal;
        if((noOfrespcondition != null) && (noOfrespcondition.size() > 0))
        {
          nos = (Double) noOfrespcondition.get(0);
          numberVal = nos.intValue();
          for(int i = 1; i < (numberVal + 1); i++)
          {
            String xpath = "item/resprocessing/respcondition[" + i +
              "]/setvar";

            itemXml = updateItemXml(itemXml, xpath, "0");
            xpath =
              "item/resprocessing/respcondition[" + i +
              "]/displayfeedback/@linkrefid";
            itemXml = updateItemXml(itemXml, xpath, "InCorrect");
          }
        }

        //updating setvar :
        String[] setvar = request.getParameterValues(key);
        int i = 0;

        String points =
          request.getParameter(
            "stxx/item/resprocessing/outcomes/decvar/@minvalue");

        while(setvar.length > i)
        {
          key = setvar[i];
          reqParam = points;
          itemXml = updateItemXml(itemXml, key, reqParam);
          key = request.getParameter(setvar[i]);
          reqParam = "Correct";
          itemXml = updateItemXml(itemXml, key, reqParam);
          i++;
        }

        //If it is a multiple correct answer case then Max value = minvalue*no of correct answers
        if((setvar.length > 0) && ((new Float(points).floatValue()) > 0))
        {
          String maxValue =
            new Float(setvar.length * (new Float(points).floatValue())).toString();
          itemXml =
            updateItemXml(
              itemXml, "item/resprocessing/outcomes/decvar/@maxvalue", maxValue);
        }
      }
    }

    // end of while
    //Special Case FIB 
    //--------------------
    if(
      (request.getParameter("itemType") != null) &&
        (
          request.getParameter("itemType").equals(AuthoringConstantStrings.FIB)
        ))
    {
      FIBHelper fibHelper = new FIBHelper();
      itemXml = fibHelper.updateFillInBlankData(itemXml, request);
    }

    return itemXml;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   */
  public void saveItem(HttpServletRequest request)
  {
    LOG.info("enter saveItem");
    AuthoringHelper authoringHelper = new AuthoringHelper();
    SectionHelper sectionHelper = new SectionHelper();
    ItemHelper itemHelper = new ItemHelper();

    String currentSection = request.getParameter("currentSection");
    String sectionID = request.getParameter("SectionIdent");
    String[] items = new String[1];
    items[0] = "";

    org.navigoproject.business.entity.Assessment assessXml = null;

    //*****************Get the itemxml from session.
    org.navigoproject.business.entity.Item itemXml =
      (org.navigoproject.business.entity.Item) request.getSession()
                                                      .getAttribute(
        "xmlString");

    // if it is not stored in session, get it directly - when adding an audio to a question
    // text, for some reason, itemXml retrieve from session is null (most of the time) - search me.
    // daisyf 07/06/04
    if (itemXml==null){
        LOG.debug("will get itemXml directly");
        String itemIdent = request.getParameter("ItemIdent");
	itemHelper.getItemXml(itemIdent);
    }

    String status = "new";

    //******************************************************
    //For MCMS with rationale
    String requiredRationale = request.getParameter("requiredRationale");
    if((requiredRationale != null) && (requiredRationale.trim().length() > 0))
    {
      if(requiredRationale.equals("Yes"))
      {
        addRationale(itemXml, request);
      }
      else
      {
        removeRationale(itemXml, request);
      }
    }

    //******* Check if the item is existing.
    if(
      (request.getParameter("ItemIdent") != null) &&
        (request.getParameter("ItemIdent").trim().length() > 0))
    {
      status = "existing";
    }

    //*****************update the itemxml with the values provided by author*******************************
    itemXml = updateXml(itemXml, request);

    if((sectionID != null) && (sectionID.trim().length() > 0))
    {
      String itemIdent = null;
      if((currentSection == null) || (currentSection.trim().length() < 1))
      {
        sectionID = request.getParameter("SectionIdent");
        currentSection = sectionID;
      }

      if(status == "new")
      {
        itemIdent = sectionHelper.addNewItem(sectionID, itemXml);

        //***********************if its insert item in between two existing items. *******************************
        String instNo = request.getParameter("insertPosition");
        if((instNo != null) && (instNo.trim().length() > 0))
        {
          sectionHelper.moveLastItemToPosition(
            sectionID, new Integer(instNo).intValue());
        }

        items[0] = itemIdent;
      }
      else
      {
        itemHelper.itemUpdateData(request.getParameter("ItemIdent"), itemXml);
        items = request.getParameterValues("ItemIdent");
      }

      //**************Move Item from existing section to another************************************
      if(
        (sectionID != null) && (currentSection != null) &&
          (currentSection.trim().length() > 0) &&
          (sectionID.trim().length() > 0) &&
          (! sectionID.equals(currentSection)))
      {
        if(
          (sectionID != null) && (items != null) && (items[0] != "") &&
            (currentSection != null) && (! currentSection.equals(sectionID)))
        {
          sectionHelper.moveItemsBetweenSections(
            items, currentSection, sectionID);
        }
        else
        {
          LOG.error("item move did not take place");
        }
      }

      //***********************Assign to pools***********************************************
      QuestionPoolHelper questionPoolHelper = new QuestionPoolHelper();
      questionPoolHelper.addToPools(
        request.getParameter("ItemIdent"),
        request.getParameterValues("poolIdents"));
    }
  }

  /**
   * @param itemXml
   * @param request
   */
  private void removeRationale(Item itemXml, HttpServletRequest request)
  {
    String rationalePresent = request.getParameter("rationalePresent");
    if(
      (rationalePresent != null) && (rationalePresent.trim().length() > 0) &&
        rationalePresent.equals("Yes"))
    {
      itemXml.removeElement("item/presentation/flow/response_str");
    }
  }

  /**
   * @param itemXml
   * @param request
   */
  private void addRationale(Item itemXml, HttpServletRequest request)
  {
    String rationalePresent = request.getParameter("rationalePresent");
    if(
      (rationalePresent != null) && (rationalePresent.trim().length() > 0) &&
        rationalePresent.equals("No"))
    {
      itemXml.add("item/presentation/flow", "response_str");

      itemXml.addAttribute("item/presentation/flow/response_str", "ident");
      itemXml.addAttribute(
        "item/presentation/flow/response_str", "rcardinality");
      itemXml.addAttribute("item/presentation/flow/response_str", "rtiming");

      itemXml.add("item/presentation/flow/response_str", "render_fib");

      itemXml.addAttribute(
        "item/presentation/flow/response_str/render_fib", "columns");
      itemXml.addAttribute(
        "item/presentation/flow/response_str/render_fib", "fibtype");
      itemXml.addAttribute(
        "item/presentation/flow/response_str/render_fib", "prompt");
      itemXml.addAttribute(
        "item/presentation/flow/response_str/render_fib", "rows");

      itemXml.add(
        "item/presentation/flow/response_str/render_fib", "response_label");
      itemXml.addAttribute(
        "item/presentation/flow/response_str/render_fib", "ident");

      itemXml =
        updateItemXml(
          itemXml, "item/presentation/flow/response_str/@ident", "MCRationale");
      itemXml =
        updateItemXml(
          itemXml, "item/presentation/flow/response_str/@rcardinality", "Single");
      itemXml =
        updateItemXml(
          itemXml, "item/presentation/flow/response_str/@rtiming", "No");

      itemXml =
        updateItemXml(
          itemXml, "item/presentation/flow/response_str/render_fib/@columns",
          "80");
      itemXml =
        updateItemXml(
          itemXml, "item/presentation/flow/response_str/render_fib/@fibtype",
          "String");
      itemXml =
        updateItemXml(
          itemXml, "item/presentation/flow/response_str/render_fib/@prompt",
          "Box");
      itemXml =
        updateItemXml(
          itemXml, "item/presentation/flow/response_str/render_fib/@rows", "6");

      itemXml =
        updateItemXml(
          itemXml,
          "item/presentation/flow/response_str/render_fib/response_label/@ident",
          "A");
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   * @param responseNo DOCUMENTATION PENDING
   * @param itemXml DOCUMENTATION PENDING
   * @param isInsert DOCUMENTATION PENDING
   * @param shouldAddResponseCondition DOCUMENTATION PENDING
   */
  public void addResponseLabel(
    HttpServletRequest request, String responseNo,
    org.navigoproject.business.entity.Item itemXml, boolean isInsert,
    boolean shouldAddResponseCondition)
  {
    AuthoringHelper authoringHelper = new AuthoringHelper();

    String xpath = request.getParameter("parent");
    String responseLabelIdent = null;
    responseLabelIdent = authoringHelper.createNewID();
    if(responseLabelIdent == null)
    {
      responseLabelIdent = responseNo + "new";
    }

    //add ResponseLabelEntries
    addResponseLabelEntry(
      itemXml, xpath, isInsert, responseNo, responseLabelIdent, request);

    //Add response conditions except when Target in matching is added 
    if(shouldAddResponseCondition == true)
    {
      //      if(
      //        (request.getParameter("itemType") != null) &&
      //          request.getParameter("itemType").equals(
      //            AuthoringConstantStrings.MATCHING))
      //      {
      //        int noSources =
      //          new Integer(request.getParameter("noOfLastSource")).intValue();
      //        int target = new Integer(responseNo).intValue();
      //        responseNo = new Integer(target - noSources).toString();
      //      }
      addItemfeedback(itemXml, responseNo, request);
      addRespcondition(itemXml, responseNo, responseLabelIdent, request);
    }

    //    // Update the match_group  for  matching case
    //    if(
    //      (request.getParameter("itemType") != null) &&
    //        request.getParameter("itemType").equals(
    //          AuthoringConstantStrings.MATCHING))
    //    {
    //      updateAllSourceMatchGroup(itemXml);
    //    }
    String findForward = "itemAuthor";
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   * @param removeNo DOCUMENTATION PENDING
   */
  public void removeResponseLabel(HttpServletRequest request, String removeNo)
  {
    org.navigoproject.business.entity.Item itemXml =
      (org.navigoproject.business.entity.Item) request.getSession()
                                                      .getAttribute(
        "xmlString");
    itemXml = updateXml(itemXml, request);
    String xpath = null;

    //Remove responselabel
    if(
      (request.getParameter("itemType") != null) &&
        request.getParameter("itemType").equals(
          AuthoringConstantStrings.MATCHING))
    {
      xpath =
        "item/presentation/flow/response_grp/render_choice[1]/response_label[" +
        removeNo + "]";
    }
    else
    {
      xpath =
        "item/presentation/flow/response_lid/render_choice[1]/response_label[" +
        removeNo + "]";
    }

    if(xpath != null)
    {
      itemXml.removeElement(xpath);
    }

    boolean deleteRespcondition = true;
    if(
      (request.getParameter("itemType") != null) &&
        request.getParameter("itemType").equals(
          AuthoringConstantStrings.MATCHING))
    {
      String responseNo = request.getParameter("noOfLastSource");
      int respNo = new Integer(responseNo).intValue();
      int remno = new Integer(removeNo).intValue();
      if(remno > respNo)
      {
        //respcondition is associted with adding deleting targets only
        deleteRespcondition = true;
        removeNo = new Integer(remno - respNo).toString(); // to calculate corresponding respcondition
      }
      else
      {
        deleteRespcondition = false;
      }
    }
    else
    {
      deleteRespcondition = true;
    }

    //Remove responsecondition
    if(deleteRespcondition == true)
    {
      xpath = "item/resprocessing/respcondition[" + removeNo + "]";
      if(xpath != null)
      {
        itemXml.removeElement(xpath);
      }

      xpath = "item/itemfeedback[" + removeNo + "]";
      if(xpath != null)
      {
        itemXml.removeElement(xpath);
      }

      //      if(
      //        (request.getParameter("itemType") != null) &&
      //          request.getParameter("itemType").equals(
      //            AuthoringConstantStrings.MATCHING))
      //      {
      //        updateAllSourceMatchGroup(itemXml);
      //      }
    }


    request.getSession().setAttribute("xmlString", itemXml);
    LOG.debug("removed " + itemXml.stringValue());
    ItemHelper itemHelper = new ItemHelper();
    itemHelper.setItemDocument(request, itemXml, true);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param itemXml DOCUMENTATION PENDING
   */
  /**
   * DOCUMENTATION PENDING
   *
   * @param itemXml DOCUMENTATION PENDING
   * @param responseNo DOCUMENTATION PENDING
   * @param responseLabelIdent DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   */
  public void addRespcondition(
    org.navigoproject.business.entity.Item itemXml, String responseNo,
    String responseLabelIdent, HttpServletRequest request)
  {
    ItemHelper itemHelper = new ItemHelper();
    String xpath = "item/resprocessing";
    itemXml.add(xpath, "respcondition/conditionvar/varequal");
    String respCond = "item/resprocessing/respcondition[" + responseNo + "]";
    itemXml.addAttribute(respCond, "continue");
    if(
      (request.getParameter("itemType") != null) &&
        (
          request.getParameter("itemType").equals(
            AuthoringConstantStrings.MCMC)
        ))
    {
      itemXml = updateItemXml(itemXml, respCond + "/@continue", "Yes");
    }
    else
    {
      itemXml = updateItemXml(itemXml, respCond + "/@continue", "No");
    }

    itemXml.addAttribute(respCond + "/conditionvar/varequal", "case");
    itemXml =
      updateItemXml(itemXml, respCond + "/conditionvar/varequal/@case", "Yes");

    itemXml.addAttribute(respCond + "/conditionvar/varequal", "respident");

    //    if(
    //      (request.getParameter("itemType") != null) &&
    //        request.getParameter("itemType").equals(
    //          AuthoringConstantStrings.MATCHING))
    //    {
    //      // or FIB this will be applied . 
    //      itemXml.addAttribute(respCond + "/conditionvar/varequal", "index");
    //      itemXml =
    //        updateItemXml(
    //          itemXml, respCond + "/conditionvar/varequal/@index", responseNo);
    //    }
    if(request.getParameter("respident") != null)
    {
      itemXml =
        updateItemXml(
          itemXml, respCond + "/conditionvar/varequal/@respident",
          request.getParameter("respident"));
    }

    itemXml =
      updateItemXml(
        itemXml, respCond + "/conditionvar/varequal", responseLabelIdent);

    //Add setvar 
    itemXml.add(respCond, "setvar");
    itemXml.addAttribute(respCond + "/setvar", "action");
    itemXml = updateItemXml(itemXml, respCond + "/setvar/@action", "Add");
    itemXml.addAttribute(respCond + "/setvar", "varname");
    itemXml = updateItemXml(itemXml, respCond + "/setvar/@varname", "SCORE");
    itemXml = updateItemXml(itemXml, respCond + "/setvar", "0");
    itemXml = updateItemXml(itemXml, respCond + "/setvar", "0");

    //Add display feedback Question Level Feedback
    itemXml.add(respCond, "displayfeedback");
    itemXml.addAttribute(respCond + "/displayfeedback", "feedbacktype");
    itemXml =
      updateItemXml(
        itemXml, respCond + "/displayfeedback/@feedbacktype", "Response");
    itemXml.addAttribute(respCond + "/displayfeedback", "linkrefid");
    itemXml =
      updateItemXml(
        itemXml, respCond + "/displayfeedback/@linkrefid", "InCorrect");

    //Add display feedback for Selection Level Feedback
    itemXml.add(respCond, "displayfeedback");
    itemXml.addAttribute(respCond + "/displayfeedback[2]", "feedbacktype");
    itemXml =
      updateItemXml(
        itemXml, respCond + "/displayfeedback[2]/@feedbacktype", "Response");
    itemXml.addAttribute(respCond + "/displayfeedback[2]", "linkrefid");

    String itemFeedbk = "item/itemfeedback[" + responseNo + "]/@ident";
    itemFeedbk = itemXml.selectSingleValue(itemFeedbk, "attribute");
    itemXml =
      updateItemXml(
        itemXml, respCond + "/displayfeedback[2]/@linkrefid", itemFeedbk);
  }

  /**
   * DOCUMENT ME!
   *
   * @param itemXml
   * @param responseNo
   * @param request
   */
  public void addItemfeedback(
    org.navigoproject.business.entity.Item itemXml, String responseNo,
    HttpServletRequest request)
  {
    ItemHelper itemHelper = new ItemHelper();
    String xpath = "item";
    AuthoringHelper authoringHelper = new AuthoringHelper();

    String nextNode = "itemfeedback[" + responseNo + "]";
    itemXml.insertElement(nextNode, xpath, "itemfeedback");
    itemXml.add(
      xpath + "/itemfeedback[" + responseNo + "]", "flow_mat/material/mattext");

    String newPath = xpath + "/itemfeedback[" + responseNo + "]";
    itemXml.addAttribute(newPath, "ident");
    newPath = xpath + "/itemfeedback[" + responseNo + "]/@ident";
    String feedbackIdent = null;
    feedbackIdent = authoringHelper.createNewID();
    if(feedbackIdent == null)
    {
      feedbackIdent = responseNo + "new";
    }

    itemXml = updateItemXml(itemXml, newPath, feedbackIdent);

    //Add placeholder for image  
    xpath = xpath + "/itemfeedback[" + responseNo + "]/flow_mat";
    itemXml.add(xpath, "material/matimage");
    xpath = xpath + "/flow_mat/material[2]/matimage";

    //Image attributes
    itemXml.addAttribute(xpath, "uri");
    itemXml.addAttribute(xpath, "imagetype");
    xpath = xpath + "/@imagetype";
    itemXml = updateItemXml(itemXml, xpath, "text/html");
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param itemXml DOCUMENTATION PENDING
   * @param xpath DOCUMENTATION PENDING
   * @param isInsert DOCUMENTATION PENDING
   * @param responseNo DOCUMENTATION PENDING
   * @param responseLabelIdent DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   */
  public void addResponseLabelEntry(
    org.navigoproject.business.entity.Item itemXml, String xpath,
    boolean isInsert, String responseNo, String responseLabelIdent,
    HttpServletRequest request)
  {
    ItemHelper itemHelper = new ItemHelper();
    if(isInsert)
    {
      String nextNode = "response_label[" + responseNo + "]";
      itemXml.insertElement(nextNode, xpath, "response_label");
      itemXml.add(
        xpath + "/response_label[" + responseNo + "]", "material/mattext");

      //
      //      if(
      //        (request.getParameter("itemType") != null) &&
      //          request.getParameter("itemType").equals(
      //            AuthoringConstantStrings.MATCHING))
      //      {
      //        itemXml.addAttribute(
      //          xpath + "/response_label[" + responseNo + "]", "match_max");
      //        itemXml.addAttribute(
      //          xpath + "/response_label[" + responseNo + "]", "match_group");
      //        itemXml =
      //          updateItemXml(
      //            itemXml,
      //            xpath + "/response_label[" + responseNo + "]" + "/@match_max", "1");
      //      }
    }
    else
    {
      itemXml.add(xpath, "response_label/material/mattext");
    }

    String newPath = xpath + "/response_label[" + responseNo + "]";
    itemXml.addAttribute(newPath, "ident");
    newPath = xpath + "/response_label[" + responseNo + "]/@ident";
    itemXml = updateItemXml(itemXml, newPath, responseLabelIdent);

    //Add placeholder for image  
    xpath = xpath + "/response_label[" + responseNo + "]";
    itemXml.add(xpath, "material/matimage");
    xpath = xpath + "/material[2]/matimage";

    //Image attributes
    itemXml.addAttribute(xpath, "uri");
    itemXml.addAttribute(xpath, "imagetype");
    xpath = xpath + "/@imagetype";
    itemXml = updateItemXml(itemXml, xpath, "text/html");
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param itemXml DOCUMENTATION PENDING
   * @param xpath DOCUMENTATION PENDING
   * @param value DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public org.navigoproject.business.entity.Item updateItemXml(
    org.navigoproject.business.entity.Item itemXml, String xpath, String value)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "updateItemXml(org.navigoproject.business.entity.Item " + itemXml +
        ", String" + xpath + ", String" + value + ")");
    }

    try
    {
      itemXml.update(xpath, value);
    }
    catch(DOMException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
    }

    return itemXml;
  }
}
