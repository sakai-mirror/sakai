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
 * Created on May 3, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.author.item;


import org.navigoproject.ui.web.asi.author.AuthoringHelper;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.xerces.dom.AttrImpl;

/**
 * @author rshastri
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MatchingHelper
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(MatchingHelper.class);

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
         int noSources =
          new Integer(request.getParameter("noOfLastSource")).intValue();
        int target = new Integer(responseNo).intValue();
        responseNo = new Integer(target - noSources).toString();
      
      addRespcondition(itemXml, responseNo, responseLabelIdent, request);
      addItemfeedback(itemXml, responseNo, request);
    }

    // Update the match_group  for  matching case

      updateAllSourceMatchGroup(itemXml);

    LOG.debug("added:" + itemXml.stringValue());

    String findForward = "itemAuthor";
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

  /**
   * DOCUMENTATION PENDING
   *
   * @param itemXml DOCUMENTATION PENDING
   * @param responseNo DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   */
  public void addItemfeedback(
    org.navigoproject.business.entity.Item itemXml, String responseNo,
    HttpServletRequest request)
  {
    ItemHelper itemHelper = new ItemHelper();
		EditItemHelper editItemHelper = new EditItemHelper();
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

    itemXml = editItemHelper.updateItemXml(itemXml, newPath, feedbackIdent);

    //Add placeholder for image  
    xpath = xpath + "/itemfeedback[" + responseNo + "]/flow_mat";
    itemXml.add(xpath, "material/matimage");
    xpath = xpath + "/flow_mat/material[2]/matimage";

    //Image attributes
    LOG.debug(xpath);
    itemXml.addAttribute(xpath, "uri");
    itemXml.addAttribute(xpath, "imagetype");
    xpath = xpath + "/@imagetype";
    itemXml = editItemHelper.updateItemXml(itemXml, xpath, "text/html");
  }

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
  
     if(request.getParameter("respident") != null)
    {
      itemXml =
        editItemHelper.updateItemXml(
          itemXml, respCond + "/conditionvar/varequal/@respident",
          request.getParameter("respident"));
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
    org.navigoproject.business.entity.Item itemXml)
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
