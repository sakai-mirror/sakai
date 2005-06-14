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

import org.apache.xerces.dom.AttrImpl;

import org.sakaiproject.tool.assessment.business.entity.asi.Item;

/**
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organization: Sakai Project</p>
 * @author rshastri
 * @author Ed Smiley esmiley@stanford.edu
 * @version $Id: MatchingHelper.java,v 1.2 2005/01/06 01:07:57 esmiley.stanford.edu Exp $
 */

public class MatchingHelper
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(MatchingHelper.class);

//  /**
//   * DOCUMENTATION PENDING
//   *
//   * @param request DOCUMENTATION PENDING
//   * @param responseNo DOCUMENTATION PENDING
//   * @param itemXml DOCUMENTATION PENDING
//   * @param isInsert DOCUMENTATION PENDING
//   * @param shouldAddResponseCondition DOCUMENTATION PENDING
//   */
//  public void addResponseLabel(
//    Map parameterMap, //HttpServletRequest request,
//    String responseNo,
//    Item itemXml, boolean isInsert,
//    boolean shouldAddResponseCondition)
//  {
//    AuthoringHelper authoringHelper = new AuthoringHelper();
//    String xpath = (String) parameterMap.get("parent");
//    String responseLabelIdent = null;
//    responseLabelIdent = authoringHelper.createNewID();
//    if(responseLabelIdent == null)
//    {
//      responseLabelIdent = responseNo + "new";
//    }
//
//    //add ResponseLabelEntries
//    addResponseLabelEntry(
//      itemXml, xpath, isInsert, responseNo, responseLabelIdent, parameterMap);
//
//    //Add response conditions except when Target in matching is added
//    if(shouldAddResponseCondition == true)
//    {
//         int noSources =
//          new Integer((String) parameterMap.get("noOfLastSource")).intValue();
//        int target = new Integer(responseNo).intValue();
//        responseNo = new Integer(target - noSources).toString();
//
//      addRespcondition(itemXml, responseNo, responseLabelIdent, parameterMap);
//      addItemfeedback(itemXml, responseNo, parameterMap);
//    }
//
//    // Update the match_group  for  matching case
//
//      updateAllSourceMatchGroup(itemXml);
//
//    LOG.debug("added:" + itemXml.stringValue());
//
//    String findForward = "itemAuthor";
//  }

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
    Item itemXml, String xpath,
    boolean isInsert, String responseNo, String responseLabelIdent,
    Map parameterMap) //HttpServletRequest request)
  {
    ItemHelper itemHelper = new ItemHelper();
		EditItemHelper editItemHelper = new EditItemHelper();
    if(isInsert)
    {
      String nextNode = "response_label[" + responseNo + "]";
      itemXml.insertElement(nextNode, xpath, "response_label");
      itemXml.add(
        xpath + "/response_label[" + responseNo + "]", "material/mattext");

        itemXml.addAttribute(
          xpath + "/response_label[" + responseNo + "]", "match_max");
        itemXml.addAttribute(
          xpath + "/response_label[" + responseNo + "]", "match_group");
        itemXml =
				editItemHelper.updateItemXml(
            itemXml,
            xpath + "/response_label[" + responseNo + "]" + "/@match_max", "1");

    }
    else
    {
      itemXml.add(xpath, "response_label/material/mattext");
    }

    String newPath = xpath + "/response_label[" + responseNo + "]";
    itemXml.addAttribute(newPath, "ident");
    newPath = xpath + "/response_label[" + responseNo + "]/@ident";
    itemXml = editItemHelper.updateItemXml(itemXml, newPath, responseLabelIdent);

    //Add placeholder for image
    xpath = xpath + "/response_label[" + responseNo + "]";
    itemXml.add(xpath, "material/matimage");
    xpath = xpath + "/material[2]/matimage";

    //Image attributes
    LOG.debug(xpath);
    itemXml.addAttribute(xpath, "uri");
    itemXml.addAttribute(xpath, "imagetype");
    xpath = xpath + "/@imagetype";
    itemXml = editItemHelper.updateItemXml(itemXml, xpath, "text/html");
  }

//  /**
//   * DOCUMENTATION PENDING
//   *
//   * @param itemXml DOCUMENTATION PENDING
//   * @param responseNo DOCUMENTATION PENDING
//   * @param request DOCUMENTATION PENDING
//   */
//  public void addItemfeedback(
//    Item itemXml, String responseNo,
//    Map parameterMap) //HttpServletRequest request)
//  {
//    ItemHelper itemHelper = new ItemHelper();
//		EditItemHelper editItemHelper = new EditItemHelper();
//    String xpath = "item";
//    AuthoringHelper authoringHelper = new AuthoringHelper();
//
//    String nextNode = "itemfeedback[" + responseNo + "]";
//    itemXml.insertElement(nextNode, xpath, "itemfeedback");
//    itemXml.add(
//      xpath + "/itemfeedback[" + responseNo + "]", "flow_mat/material/mattext");
//
//    String newPath = xpath + "/itemfeedback[" + responseNo + "]";
//    itemXml.addAttribute(newPath, "ident");
//    newPath = xpath + "/itemfeedback[" + responseNo + "]/@ident";
//    String feedbackIdent = null;
//    feedbackIdent = authoringHelper.createNewID();
//    if(feedbackIdent == null)
//    {
//      feedbackIdent = responseNo + "new";
//    }
//
//    itemXml = editItemHelper.updateItemXml(itemXml, newPath, feedbackIdent);
//
//    //Add placeholder for image
//    xpath = xpath + "/itemfeedback[" + responseNo + "]/flow_mat";
//    itemXml.add(xpath, "material/matimage");
//    xpath = xpath + "/flow_mat/material[2]/matimage";
//
//    //Image attributes
//    LOG.debug(xpath);
//    itemXml.addAttribute(xpath, "uri");
//    itemXml.addAttribute(xpath, "imagetype");
//    xpath = xpath + "/@imagetype";
//    itemXml = editItemHelper.updateItemXml(itemXml, xpath, "text/html");
//  }

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
    String responseLabelIdent, Map parameterMap ) //HttpServletRequest request)
  {
    ItemHelper itemHelper = new ItemHelper();
		EditItemHelper editItemHelper = new EditItemHelper();
    String xpath = "item/resprocessing";
    itemXml.add(xpath, "respcondition/conditionvar/varequal");
    String respCond = "item/resprocessing/respcondition[" + responseNo + "]";
    itemXml.addAttribute(respCond, "continue");

      itemXml = editItemHelper.updateItemXml(itemXml, respCond + "/@continue", "No");


    itemXml.addAttribute(respCond + "/conditionvar/varequal", "case");
    itemXml =
      editItemHelper.updateItemXml(itemXml, respCond + "/conditionvar/varequal/@case", "Yes");

    itemXml.addAttribute(respCond + "/conditionvar/varequal", "respident");

      itemXml.addAttribute(respCond + "/conditionvar/varequal", "index");
      itemXml =
        editItemHelper.updateItemXml(
          itemXml, respCond + "/conditionvar/varequal/@index", responseNo);

     if(parameterMap.get("respident") != null)
    {
      itemXml =
        editItemHelper.updateItemXml(
          itemXml, respCond + "/conditionvar/varequal/@respident",
          (String) parameterMap.get("respident"));
    }

    itemXml =
      editItemHelper.updateItemXml(
        itemXml, respCond + "/conditionvar/varequal", responseLabelIdent);

    //Add setvar
    itemXml.add(respCond, "setvar");
    itemXml.addAttribute(respCond + "/setvar", "action");
    itemXml = editItemHelper.updateItemXml(itemXml, respCond + "/setvar/@action", "Add");
    itemXml.addAttribute(respCond + "/setvar", "varname");
    itemXml = editItemHelper.updateItemXml(itemXml, respCond + "/setvar/@varname", "SCORE");
    itemXml = editItemHelper.updateItemXml(itemXml, respCond + "/setvar", "0");
    itemXml = editItemHelper.updateItemXml(itemXml, respCond + "/setvar", "0");

    //Add display feedback
    itemXml.add(respCond, "displayfeedback");
    itemXml.addAttribute(respCond + "/displayfeedback", "feedbacktype");
    itemXml =
      editItemHelper.updateItemXml(
        itemXml, respCond + "/displayfeedback/@feedbacktype", "Response");
    itemXml.addAttribute(respCond + "/displayfeedback", "linkrefid");
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param itemXml DOCUMENTATION PENDING
   */
  public void updateAllSourceMatchGroup(
    Item itemXml)
  {
    ItemHelper itemHelper = new ItemHelper();
		EditItemHelper editItemHelper = new EditItemHelper();
    List targetIdents =
      itemXml.selectNodes(
        "item/presentation/flow/response_grp/render_choice/response_label[not(@match_group)]/@ident");
    if(targetIdents.size() > 0)
    {
      Iterator iter = targetIdents.iterator();
      AttrImpl ident = null;
      String match_group = null;
      while(iter.hasNext())
      {
        ident = (AttrImpl) iter.next();
        if(match_group == null)
        {
          match_group = ident.getValue();
        }
        else
        {
          match_group = match_group + "," + ident.getValue();
        }
      }

      if(match_group != null)
      {
        int noOfSources =
          (itemXml.selectNodes(
            "item/presentation/flow/response_grp/render_choice/response_label[(@match_group)]")).size();
        for(int i = 1; i <= noOfSources; i++)
        {
          String xpath =
            "item/presentation/flow/response_grp/render_choice/response_label[" +
            i + "]/@match_group";
          itemXml = editItemHelper.updateItemXml(itemXml, xpath, match_group);
        }
      }
    }
  }
}
