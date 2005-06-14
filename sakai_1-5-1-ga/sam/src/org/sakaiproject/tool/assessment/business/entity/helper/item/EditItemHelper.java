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

package org.sakaiproject.tool.assessment.business.entity.helper.item;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.DOMException;

import org.sakaiproject.tool.assessment.business.entity.asi.Item;
import org.sakaiproject.tool.assessment.business.entity.constants.AuthoringConstantStrings;
import org.sakaiproject.tool.assessment.business.entity.helper.AuthoringHelper;

/**
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organization: Sakai Project</p>
 * @author rshastri
 * @author Ed Smiley esmiley@stanford.edu
 * @version $Id: EditItemHelper.java,v 1.4 2005/01/21 03:34:11 esmiley.stanford.edu Exp $
 */

public class EditItemHelper
//  extends Action
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
  public Item updateXml(
    Item itemXml,
    Map parameterMap) //HttpServletRequest request)
  {
    LOG.info("*itemXml = " + itemXml);
//    Map requestMap = request.getParameterMap();
    Set keySet = parameterMap.keySet();
    Iterator setIter = keySet.iterator();
    AuthoringHelper authoringHelper = new AuthoringHelper();
    ItemHelper itemHelper = new ItemHelper();
    EditItemHelper editItemHelper = new EditItemHelper();

    // set min value = max value to begin with
    if(
      (
          parameterMap.get(
            "stxx/item/resprocessing/outcomes/decvar/@minvalue") != null
        ) &&
        (
          ((String) parameterMap.get(
            "stxx/item/resprocessing/outcomes/decvar/@minvalue")).trim().length() > 0
        ))
    {
      if(
        new Float( (String)
            parameterMap.get(
              "stxx/item/resprocessing/outcomes/decvar/@minvalue")).floatValue() > 0)
      {
        itemXml =
          updateItemXml(
            itemXml, "item/resprocessing/outcomes/decvar/@maxvalue",
            (String) parameterMap.get(
              "stxx/item/resprocessing/outcomes/decvar/@minvalue"));
      }
    }
    while(setIter.hasNext())
    {
      String key = setIter.next().toString();
      String reqParam = (String) parameterMap.get(key);
      if(! (key == null) && ! ((key.length()) == 0) && key.startsWith("stxx"))
      {
        reqParam = (String) parameterMap.get(key);
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
//        String[] setvar = parameterMap.getValues(key);
        Set keys = parameterMap.keySet();
        int i = 0;

        String points = (String) parameterMap.get(
            "stxx/item/resprocessing/outcomes/decvar/@minvalue");
        Iterator iter = keys.iterator();

//        while(setvar.length > i)
        while (iter.hasNext())
        {
//          key = setvar[i];
          key = (String) iter.next();
//          reqParam = points;
          itemXml = updateItemXml(itemXml, key, points);
//          key = parameterMap.get(setvar[i]);
          key = (String) parameterMap.get(key);
          reqParam = "Correct";
          itemXml = updateItemXml(itemXml, key, reqParam);
          i++;
        }

        //If it is a multiple correct answer case then Max value = minvalue*no of correct answers
//        if((setvar.length > 0) && ((new Float(points).floatValue()) > 0))
        if((!keys.isEmpty()) && ((new Float(points).floatValue()) > 0))
        {
          String maxValue =
//            new Float(setvar.length * (new Float(points).floatValue())).toString();
          new Float(i * (new Float(points).floatValue())).toString();
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
      (parameterMap.get("itemType") != null) &&
        (
          parameterMap.get("itemType").equals(AuthoringConstantStrings.FIB)
        ))
    {
      FIBHelper fibHelper = new FIBHelper();
      itemXml = fibHelper.updateFillInBlankData(itemXml, parameterMap);
    }

    return itemXml;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   */
  /*
  public void saveItem(Map parameterMap) //HttpServletRequest request)
  {
    LOG.info("enter saveItem");
    AuthoringHelper authoringHelper = new AuthoringHelper();
    SectionHelper sectionHelper = new SectionHelper();
    ItemHelper itemHelper = new ItemHelper();

    String currentSection = (String) parameterMap.get("currentSection");
    String sectionID = (String) parameterMap.get("SectionIdent");
    String[] items = new String[1];
    items[0] = "";

    Assessment assessXml = null;

//    //*****************Get the itemxml from session.
//    Item itemXml =
//      (Item) request.getSession()
//                                                      .getAttribute(
//        "xmlString");
//
//    // if it is not stored in session, get it directly - when adding an audio to a question
//    // text, for some reason, itemXml retrieve from session is null (most of the time) - search me.
//    // daisyf 07/06/04
//    if (itemXml==null){
//        LOG.debug("will get itemXml directly");
    Item itemXml = null;
    String itemIdent = (String) parameterMap.get("ItemIdent");
//	itemHelper.getItemXml(itemIdent);
//    }

    String status = "new";

    //******************************************************
    //For MCMS with rationale
    String requiredRationale = (String) parameterMap.get("requiredRationale");
    if((requiredRationale != null) && (requiredRationale.trim().length() > 0))
    {
      if(requiredRationale.equals("Yes"))
      {
        addRationale(itemXml, parameterMap);
      }
      else
      {
        removeRationale(itemXml, parameterMap);
      }
    }

    //******* Check if the item is existing.
    if(
      (parameterMap.get("ItemIdent") != null) &&
        (( (String) parameterMap.get("ItemIdent")).trim().length() > 0))
    {
      status = "existing";
    }

    //*****************update the itemxml with the values provided by author*******************************
    itemXml = updateXml(itemXml, parameterMap);

    if((sectionID != null) && (sectionID.trim().length() > 0))
    {
//      String itemIdent = null;
      if((currentSection == null) || (currentSection.trim().length() < 1))
      {
        sectionID = (String) parameterMap.get("SectionIdent");
        currentSection = sectionID;
      }

      if(status == "new")
      {
        itemIdent = sectionHelper.addNewItem(sectionID, itemXml);

        //***********************if its insert item in between two existing items. *******************************
        String instNo = (String) parameterMap.get("insertPosition");
        if((instNo != null) && (instNo.trim().length() > 0))
        {
          sectionHelper.moveLastItemToPosition(
            sectionID, new Integer(instNo).intValue());
        }

        items[0] = itemIdent;
      }
      else
      {
        itemHelper.itemUpdateData((String) parameterMap.get("ItemIdent"), itemXml);
        items = (String[]) parameterMap.get("ItemIdent");
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
        (String) parameterMap.get("ItemIdent"),
        (String[]) parameterMap.get("poolIdents"));
    }
  }
*/

  /**
   * @param itemXml
   * @param request
   */
  private void removeRationale(Item itemXml, Map parameterMap) //HttpServletRequest request)
  {
    String rationalePresent = (String) parameterMap.get("rationalePresent");
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
  private void addRationale(Item itemXml, Map parameterMap) //HttpServletRequest request)
  {
    String rationalePresent = (String) parameterMap.get("rationalePresent");
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
   * @param removeNo DOCUMENTATION PENDING
   */
  public void removeResponseLabel(
    Item itemXml,
    Map parameterMap, //HttpServletRequest request)
    String removeNo)
  {
//    Item itemXml =
//      (Item) request.getSession()
//                                                      .getAttribute(
//        "xmlString");
    itemXml = updateXml(itemXml, parameterMap);
    String xpath = null;

    //Remove responselabel
    if(
      (parameterMap.get("itemType") != null) &&
        parameterMap.get("itemType").equals(
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
      (parameterMap.get("itemType") != null) &&
        parameterMap.get("itemType").equals(
          AuthoringConstantStrings.MATCHING))
    {
      String responseNo = (String) parameterMap.get("noOfLastSource");
      int respNo = new Integer(responseNo).intValue();
      int remno = new Integer(removeNo).intValue();
      if(remno > respNo)
      {
        //respcondition is associated with adding deleting targets only
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
//
//
//    request.getSession().setAttribute("xmlString", itemXml);
    LOG.debug("removed " + itemXml.stringValue());
    ItemHelper itemHelper = new ItemHelper();
//    itemHelper.setItemDocument(request, itemXml, true);
    /** @todo */
//    itemHelper.setItemDocument(parameterMap, itemXml, true);
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
    Item itemXml, String responseNo,
    String responseLabelIdent, Map parameterMap) //HttpServletRequest request)
  {
    ItemHelper itemHelper = new ItemHelper();
    String xpath = "item/resprocessing";
    itemXml.add(xpath, "respcondition/conditionvar/varequal");
    String respCond = "item/resprocessing/respcondition[" + responseNo + "]";
    itemXml.addAttribute(respCond, "continue");
    if(
      (parameterMap.get("itemType") != null) &&
        (
          parameterMap.get("itemType").equals(
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
    if(parameterMap.get("respident") != null)
    {
      itemXml =
        updateItemXml(
          itemXml, respCond + "/conditionvar/varequal/@respident",
          (String) parameterMap.get("respident"));
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
    Item itemXml, String responseNo,
    Map parameterMap) //HttpServletRequest request)
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
    /** @todo */
//    feedbackIdent = authoringHelper.createNewID();
//    if(feedbackIdent == null)
//    {
//      feedbackIdent = responseNo + "new";
//    }

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
   * @param value DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Item updateItemXml(
    Item itemXml, String xpath, String value)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "updateItemXml(Item " + itemXml +
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
