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
 * Created on Mar 29, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.author.item;

import org.navigoproject.business.entity.Item;
import org.navigoproject.ui.web.asi.author.AuthoringHelper;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

/**
 * @author rshastri
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class FIBHelper
{
   
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(EditItemHelper.class);

  /**
   * @param itemXml
   * @param request
   * @return
   */
  public Item updateFillInBlankData(Item itemXml, HttpServletRequest request)
  {
    EditItemHelper editItemHelper = new EditItemHelper();
    AuthoringHelper authoringHelper = new AuthoringHelper();
    String fibAns = request.getParameter("stxx/form/itemActionForm/fibAnswer");
    if((fibAns != null) && (fibAns.trim().length() > 0))
    {
      List lst = parseFillInBlank(fibAns);
      Map valueMap = null;
      Set newSet = null;
      String mattext = null;
      String respStr = null;
      String xpath = "item/presentation/flow/flow";
      String position = null;
      String[] responses = null;
      if((lst != null) && (lst.size() > 0))
      {
				
				List idsAndResponses = new ArrayList();
				List allIdents = new ArrayList();
        //1. add Mattext And Responses
        for(int i = 0; i < lst.size(); i++)
        {	
          valueMap = (Map) lst.get(i);
          if((valueMap != null) && (valueMap.size() > 0))
          {
            mattext = (String) valueMap.get("text");
            if(mattext != null)
            {
              //add mattext
              itemXml.add(xpath, "material/mattext");
              String newXpath =
                xpath + "/material[" +
                (new Integer(i + 1).toString() + "]/mattext");

              itemXml =
                editItemHelper.updateItemXml(itemXml, newXpath, mattext);
            }

            respStr = (String) valueMap.get("ans");
            if(respStr != null)
            {
		 
              //add response_str
              itemXml.add(xpath, "response_str/render_fib");
              String newXpath =
                xpath + "/response_str[" +
                (new Integer(i + 1).toString() + "]");

              itemXml.addAttribute(newXpath, "ident");
              String ident = authoringHelper.createNewID();
              itemXml =
                editItemHelper.updateItemXml(
                  itemXml, newXpath + "/@ident", ident);
              itemXml.addAttribute(newXpath, "rcardinality");
              itemXml =
                editItemHelper.updateItemXml(
                  itemXml, newXpath + "/@rcardinality", "Ordered");
              newXpath = newXpath + "/render_fib";
              itemXml.addAttribute(newXpath, "fibtype");
              itemXml =
                editItemHelper.updateItemXml(
                  itemXml, newXpath + "/@fibtype", "String");
              itemXml.addAttribute(newXpath, "prompt");
              itemXml =
                editItemHelper.updateItemXml(
                  itemXml, newXpath + "/@prompt", "Box");
              itemXml.addAttribute(newXpath, "columns");
              itemXml =
                editItemHelper.updateItemXml(
                  itemXml, newXpath + "/@columns",
                  (new Integer(respStr.length() + 5).toString()));
              itemXml.addAttribute(newXpath, "rows");
              itemXml =
                editItemHelper.updateItemXml(itemXml, newXpath + "/@rows", "1");
// 
							responses = getPossibleCorrectResponses(respStr);
							Map idsAndResponsesMap = new HashMap();
							allIdents.add(ident);
							idsAndResponsesMap.put(ident, responses);
							idsAndResponses.add(idsAndResponsesMap);
            }
          }
        }

        String points =
          request.getParameter(
            "stxx/item/resprocessing/outcomes/decvar/@minvalue");

        //2.  Add ResponseProcessing logic
        if(idsAndResponses != null)
        {
          boolean isMutuallyExclusive = false;
          if(
            (itemXml.getFieldentry("MUTUALLY_EXCLUSIVE") != null) &&
              itemXml.getFieldentry("MUTUALLY_EXCLUSIVE").equals("True"))
          {
            isMutuallyExclusive = true;
          }

          itemXml =
            addRespconditions(
              itemXml, idsAndResponses, allIdents,isMutuallyExclusive, points, request);
        }
        //3. Set the Point Value
        if((points != null) && (points.trim().length() > 0))
        {
          String maxValue =
            new Integer(
              idsAndResponses.size() * (new Integer(points).intValue())).toString();
          itemXml =
            editItemHelper.updateItemXml(
              itemXml, "item/resprocessing/outcomes/decvar/@maxvalue", maxValue);
        }
      }
    }

    return itemXml;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param itemXml DOCUMENTATION PENDING
   * @param idsAndResponses DOCUMENTATION PENDING
   * @param isMutuallyExclusive DOCUMENTATION PENDING
   * @param points DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private org.navigoproject.business.entity.Item addRespconditions(
    org.navigoproject.business.entity.Item itemXml, List idsAndResponses,List allIdents,
    boolean isMutuallyExclusive, String points, HttpServletRequest request)
  {
		if( idsAndResponses.size()>0 )
  	{
			ArrayList combinationResponses = getSimilarCorrectAnswerIDs(idsAndResponses);
			if(combinationResponses!=null && combinationResponses.size()>0)
			{
				int respConditionNo = 1;
				for(int i = 0; i < combinationResponses.size(); i++)
				{
					Map currentEntry = (Map) combinationResponses.get(i);
					Set entrySet = currentEntry.keySet();
					Iterator entrykeys = entrySet.iterator();
					while(entrykeys.hasNext())
					{
						String[] responses = (String[]) entrykeys.next();
						ArrayList idList = (ArrayList) currentEntry.get(responses);
						if(idList != null && idList.size()>0)	
						{
							if(idList.size()==1)
							{
								addRespcondition_NotMutuallyExclusiveFIB(itemXml,new Integer(respConditionNo).toString(),(String)idList.get(0),points,responses);
								respConditionNo = respConditionNo + 1;
							}
							else
							{
								for(int k=0; k<responses.length;k++)
								{
									
									addRespcondition_Mutually_Exclusive(itemXml,new Integer(respConditionNo).toString(),idList,points,responses[k]);
									respConditionNo = respConditionNo + 1;
									
								}
								
							}
						}
					}
				}
				// add respcondition for all correct answers
				itemXml		=addRespconditionCorrectFeedback(itemXml,new Integer(respConditionNo).toString() );
				respConditionNo = respConditionNo + 1;
				//add respcondition for all incorrect answers
				itemXml =addRespconditionInCorrectFeedback(itemXml,new Integer(respConditionNo).toString() );
			}
  	      
  	}
    return itemXml;
  }

  /**
   * @param idsAndResponses
   * @return
   */
	private ArrayList getSimilarCorrectAnswerIDs(List idsAndResponses)
		{
			String[] compareResponse = null;
			ArrayList finalArray = new ArrayList(); // this is list of maps which contains arrayList  correct responses and ids
			ArrayList idList = new ArrayList();
			ArrayList responseList = new ArrayList();
			Map intermediaryMap = new HashMap();
			for(int l = 0; l < idsAndResponses.size(); l++)
			{
				Map idsAndResponsesMap = (Map) idsAndResponses.get(l);
				Set set = idsAndResponsesMap.keySet();
				Iterator keys = set.iterator();
				while(keys.hasNext())
				{
					String respIdent = (String) keys.next();
					String[] responses = null;
					if((respIdent != null) && (respIdent.length() > 0))
					{
						responses = (String[]) idsAndResponsesMap.get(respIdent);
					}

					boolean newElement = true;
					for(int i = 0; i < finalArray.size(); i++)
					{
						Map currentEntry = (Map) finalArray.get(i);
						Set entrySet = currentEntry.keySet();
						Iterator entrykeys = entrySet.iterator();
						while(entrykeys.hasNext())
						{
							compareResponse = (String[]) entrykeys.next();
							if(Arrays.equals(responses,compareResponse))
							{
								idList = (ArrayList) currentEntry.get(compareResponse);
								idList.add(respIdent);
								newElement = false;
							}
						}
					}

					if((finalArray.size() == 0) || (newElement))
					{
						idList = new ArrayList();
						idList.add(respIdent);
						intermediaryMap = new HashMap();
						intermediaryMap.put(responses, idList);
						finalArray.add(intermediaryMap);
					}
				}
			}
			return finalArray;
		}

 
//TODO combine addRespcondition_MutuallyExclusiveFIB and addRespcondition_NonMutuallyExclusiveFIB in once function
  /**
   * DOCUMENTATION PENDING
   *
   * @param itemXml DOCUMENTATION PENDING
   * @param responseCondNo DOCUMENTATION PENDING
   * @param respIdent DOCUMENTATION PENDING
   * @param points DOCUMENTATION PENDING
   * @param responses DOCUMENTATION PENDING
   * @param correctFeedback DOCUMENTATION PENDING
   * @param otherRespIdents DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private org.navigoproject.business.entity.Item addRespcondition_Mutually_Exclusive(org.navigoproject.business.entity.Item itemXml, String responseCondNo,
    ArrayList respIdents, String points, String response)
  {
    EditItemHelper editItemHelper = new EditItemHelper();
    String xpath = "item/resprocessing";
    itemXml.add(xpath, "respcondition");
    String respCond =
      "item/resprocessing/respcondition[" + responseCondNo + "]";
    itemXml.addAttribute(respCond, "continue");
    itemXml =
      editItemHelper.updateItemXml(itemXml, respCond + "/@continue", "Yes");

    //************************************************************************************************************
    String or = "";
      itemXml.add(respCond, "conditionvar/or");
    or = respCond + "/conditionvar/or";

    for(int i = 0; i < respIdents.size(); i++)
    {
      int iString = i + 1;
       
      itemXml.add(or, "varequal");
      String varequal = or + "/varequal["+(i+1)+"]";
      itemXml.addAttribute(varequal, "case");
      itemXml.addAttribute(varequal, "respident");
      itemXml =
        editItemHelper.updateItemXml(itemXml, varequal + "/@case", "No");
      itemXml =
        editItemHelper.updateItemXml(
          itemXml, varequal + "/@respident", (String)respIdents.get(i));
      itemXml = editItemHelper.updateItemXml(itemXml, varequal, response);
    }

    //************************************************************************************************************
    //Add setvar 
    itemXml.add(respCond, "setvar");
    itemXml.addAttribute(respCond + "/setvar", "action");
    itemXml =
      editItemHelper.updateItemXml(
        itemXml, respCond + "/setvar/@action", "Add");
    itemXml.addAttribute(respCond + "/setvar", "varname");
    itemXml =
      editItemHelper.updateItemXml(
        itemXml, respCond + "/setvar/@varname", "SCORE");
    itemXml =
      editItemHelper.updateItemXml(itemXml, respCond + "/setvar", points); // this should be minimum value

    return itemXml;
  }

 
  
  /**
   * DOCUMENTATION PENDING
   *
   * @param input DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private List parseFillInBlank(String input)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("parseFillInBlank(  String" + input + ")");
    }

    Map tempMap = null;
    List storeParts = new ArrayList();
    if(input == null)
    {
      return storeParts;
    }

    StringTokenizer st = new StringTokenizer(input, "}");
    String tempToken = "";
    String[] splitArray = null;

    while(st.hasMoreTokens())
    {
      tempToken = st.nextToken();
      tempMap = new HashMap();

      //split out text and answer parts from token
      splitArray = tempToken.trim().split("\\{", 2);
      tempMap.put("text", splitArray[0].trim());
      if(splitArray.length > 1)
      {
      	
        tempMap.put("ans", (splitArray[1]));
      }
      else
      {
        tempMap.put("ans", null);
      }

      storeParts.add(tempMap);
    }

    return storeParts;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param inputStr DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
	private static String[] getPossibleCorrectResponses(String inputStr)
	 {
		 String patternStr = ",";
		 String[] responses = inputStr.split(patternStr);
		 for(int i=0;i< responses.length; i++)
		 {
			 responses[i]= responses[i].trim();
		 }
		 Arrays.sort(responses);

		 return responses;
	 }
	 
	private org.navigoproject.business.entity.Item addRespcondition_NotMutuallyExclusiveFIB(
			 org.navigoproject.business.entity.Item itemXml, String responseCondNo,
			 String respIdent, String points, String[] responses)
		 {
			 EditItemHelper editItemHelper = new EditItemHelper();
			 String xpath = "item/resprocessing";
			 itemXml.add(xpath, "respcondition");
			 String respCond =
				 "item/resprocessing/respcondition[" + responseCondNo + "]";
			 itemXml.addAttribute(respCond, "continue");
			 itemXml =
				 editItemHelper.updateItemXml(itemXml, respCond + "/@continue", "Yes");

			 //************************************************************************************************************
			 String or = "";

			 	 itemXml.add(respCond, "conditionvar/or");
				 or = respCond + "/conditionvar/or";
			  

			 for(int i = 0; i < responses.length; i++)
			 {
				 itemXml.add(or, "varequal");
				 int iString = i + 1;
				 String varequal = or + "/varequal[" + iString + "]";
				 itemXml.addAttribute(varequal, "case");
				 itemXml.addAttribute(varequal, "respident");
				 itemXml =
					 editItemHelper.updateItemXml(itemXml, varequal + "/@case", "No");
				 itemXml =
					 editItemHelper.updateItemXml(
						 itemXml, varequal + "/@respident", respIdent);
				 itemXml = editItemHelper.updateItemXml(itemXml, varequal, responses[i]);
			 }

			 //************************************************************************************************************
			 //Add setvar 
			 itemXml.add(respCond, "setvar");
			 itemXml.addAttribute(respCond + "/setvar", "action");
			 itemXml =
				 editItemHelper.updateItemXml(
					 itemXml, respCond + "/setvar/@action", "Add");
			 itemXml.addAttribute(respCond + "/setvar", "varname");
			 itemXml =
				 editItemHelper.updateItemXml(
					 itemXml, respCond + "/setvar/@varname", "SCORE");
			 itemXml =
				 editItemHelper.updateItemXml(itemXml, respCond + "/setvar", points); // this should be minimum value

			 return itemXml;
		 }
	 
	 
	 //-------------------------------************************** OLD CODE**********************************************-------------------------------------------- 
	 
	 	private org.navigoproject.business.entity.Item OLD_VERSION_addRespconditions(
		org.navigoproject.business.entity.Item itemXml, List idsAndResponses,List allIdents,
		boolean isMutuallyExclusive, String points, HttpServletRequest request)
	{
		int i = 1;
		for(int l = 0; l< idsAndResponses.size() ; l++)
		{
		Iterator forOtherResponses = allIdents.iterator();
		Map idsAndResponsesMap = (Map)idsAndResponses.get(l);
		Set set = idsAndResponsesMap.keySet();
		int noIdents = idsAndResponses.size() - 1;
		
		Iterator keys = set.iterator();
		// just one entry expected
	 while(keys.hasNext())
		{
			String respIdent = (String) keys.next();
			if(respIdent != null && respIdent.length() >0)
			{
      
			String[] otherResponses =     
				 OLD_VERSION_getOtherResponseIdents(respIdent, forOtherResponses, noIdents);
			String[] responses = (String[]) idsAndResponsesMap.get(respIdent);
			if(isMutuallyExclusive)
			{
				itemXml =
					OLD_VERSION_addRespcondition_MutuallyExclusiveFIB(
						itemXml, new Integer(i).toString(), respIdent, points, responses,
						true, otherResponses);
			}
			else
			{
				itemXml =
					OLD_VERSION_addRespcondition_NotMutuallyExclusiveFIB(
						itemXml, new Integer(i).toString(), respIdent, points, responses,
						true);
			}

			i++;
			if(isMutuallyExclusive)
			{
				itemXml =
				OLD_VERSION_addRespcondition_MutuallyExclusiveFIB(
						itemXml, new Integer(i).toString(), respIdent, "0", responses, false,
						otherResponses);
			}
			else
			{
				itemXml =
					OLD_VERSION_addRespcondition_NotMutuallyExclusiveFIB(
						itemXml, new Integer(i).toString(), respIdent, "0", responses, false);
			}

			i++;
		}
		}
		}
		return itemXml;
	}
	
	private org.navigoproject.business.entity.Item OLD_VERSION_addRespcondition_MutuallyExclusiveFIB(
		org.navigoproject.business.entity.Item itemXml, String responseCondNo,
		String respIdent, String points, String[] responses, boolean correctFeedback,
		String[] otherRespIdents)
	{
		EditItemHelper editItemHelper = new EditItemHelper();
		String xpath = "item/resprocessing";
		itemXml.add(xpath, "respcondition");
		String respCond =
			"item/resprocessing/respcondition[" + responseCondNo + "]";
		itemXml.addAttribute(respCond, "continue");
		itemXml =
			editItemHelper.updateItemXml(itemXml, respCond + "/@continue", "Yes");
  
		//************************************************************************************************************
		String or = "";
		String and = "";
		String orNot = "";
		//TODO check if responses.length > 0 then add these xpaths
		itemXml.add(respCond, "conditionvar/or");
		or = respCond + "/conditionvar/or";
  
		//		for(int totalIdents =-1; totalIdents <otherRespIdents.length; totalIdents++ )
		//		{
		//			if
		//		}
		for(int i = 0; i < responses.length; i++)
		{
			int iString = i + 1;
			itemXml.add(or, "and");
			and = or + "/and[" + iString + "]";
			itemXml.add(and, "varequal");
			String varequal = and + "/varequal[1]";
			itemXml.addAttribute(varequal, "case");
			itemXml.addAttribute(varequal, "respident");
			itemXml =
				editItemHelper.updateItemXml(itemXml, varequal + "/@case", "No");
			itemXml =
				editItemHelper.updateItemXml(
					itemXml, varequal + "/@respident", respIdent);
			itemXml = editItemHelper.updateItemXml(itemXml, varequal, responses[i].trim());
			if(correctFeedback)
			{
				itemXml.add(and, "not/or");
				orNot = and + "/not/or";
			}
			else
			{
				itemXml.add(and, "or");
				orNot = and + "/or";
			}
			if(otherRespIdents != null  && otherRespIdents.length> 0 )
			{
  
			for(int j = 0; j < otherRespIdents.length; j++)
			{
				int jString = j + 1;
				itemXml.add(orNot, "varequal");
				String orNot_varequal = orNot + "/varequal[" + jString + "]";
				itemXml.addAttribute(orNot_varequal, "case");
				itemXml.addAttribute(orNot_varequal, "respident");
				itemXml =
					editItemHelper.updateItemXml(
						itemXml, orNot_varequal + "/@case", "No");
				itemXml =
					editItemHelper.updateItemXml(
						itemXml, orNot_varequal + "/@respident", otherRespIdents[j]);
				itemXml =
					editItemHelper.updateItemXml(itemXml, orNot_varequal, responses[i]);
			}
			}
		}
  
		//************************************************************************************************************
		//Add setvar 
		itemXml.add(respCond, "setvar");
		itemXml.addAttribute(respCond + "/setvar", "action");
		itemXml =
			editItemHelper.updateItemXml(
				itemXml, respCond + "/setvar/@action", "Add");
		itemXml.addAttribute(respCond + "/setvar", "varname");
		itemXml =
			editItemHelper.updateItemXml(
				itemXml, respCond + "/setvar/@varname", "SCORE");
		itemXml =
			editItemHelper.updateItemXml(itemXml, respCond + "/setvar", points); // this should be minimum value
  
		//Add display feedback
		itemXml.add(respCond, "displayfeedback");
		itemXml.addAttribute(respCond + "/displayfeedback", "feedbacktype");
		itemXml =
			editItemHelper.updateItemXml(
				itemXml, respCond + "/displayfeedback/@feedbacktype", "Response");
		itemXml.addAttribute(respCond + "/displayfeedback", "linkrefid");
		if(correctFeedback)
		{
			itemXml =
				editItemHelper.updateItemXml(
					itemXml, respCond + "/displayfeedback/@linkrefid", "Correct");
		}
		else
		{
			itemXml =
				editItemHelper.updateItemXml(
					itemXml, respCond + "/displayfeedback/@linkrefid", "InCorrect");
		}
  
		return itemXml;
	}
	
//USED BY OLDER VERSION OF MULTUALLY EXCLUSIVE CODE 
 private String[] OLD_VERSION_getOtherResponseIdents(
	 String respIdent, Iterator keys, int noResponseIdents)
 {
	 if(noResponseIdents > 0)
	 {
		 String[] otherResponseIdents = new String[noResponseIdents];
		 int i = 0;
		 while(keys.hasNext())
		 {
			 String respIdentInIterator = (String) keys.next();
			 if((respIdentInIterator != null) && (respIdentInIterator != respIdent))
			 {
				 otherResponseIdents[i] = respIdentInIterator;
				 i++;
			 }
		 }
		 return otherResponseIdents;
	 }
	 else
	 {
		 return null;
	 }
 }
 
 private org.navigoproject.business.entity.Item OLD_VERSION_addRespcondition_NotMutuallyExclusiveFIB(
		 org.navigoproject.business.entity.Item itemXml, String responseCondNo,
		 String respIdent, String points, String[] responses, boolean correctFeedback)
	 {
		 EditItemHelper editItemHelper = new EditItemHelper();
		 String xpath = "item/resprocessing";
		 itemXml.add(xpath, "respcondition");
		 String respCond =
			 "item/resprocessing/respcondition[" + responseCondNo + "]";
		 itemXml.addAttribute(respCond, "continue");
		 itemXml =
			 editItemHelper.updateItemXml(itemXml, respCond + "/@continue", "Yes");

		 //************************************************************************************************************
		 String or = "";

		 if(correctFeedback)
		 {
			 itemXml.add(respCond, "conditionvar/or");
			 or = respCond + "/conditionvar/or";
		 }
		 else
		 {
			 itemXml.add(respCond, "conditionvar/not/or");
			 or = respCond + "/conditionvar/not/or";
		 }

		 for(int i = 0; i < responses.length; i++)
		 {
			 itemXml.add(or, "varequal");
			 int iString = i + 1;
			 String varequal = or + "/varequal[" + iString + "]";
			 itemXml.addAttribute(varequal, "case");
			 itemXml.addAttribute(varequal, "respident");
			 itemXml =
				 editItemHelper.updateItemXml(itemXml, varequal + "/@case", "No");
			 itemXml =
				 editItemHelper.updateItemXml(
					 itemXml, varequal + "/@respident", respIdent);
			 itemXml = editItemHelper.updateItemXml(itemXml, varequal, responses[i]);
		 }

		 //************************************************************************************************************
		 //Add setvar 
		 itemXml.add(respCond, "setvar");
		 itemXml.addAttribute(respCond + "/setvar", "action");
		 itemXml =
			 editItemHelper.updateItemXml(
				 itemXml, respCond + "/setvar/@action", "Add");
		 itemXml.addAttribute(respCond + "/setvar", "varname");
		 itemXml =
			 editItemHelper.updateItemXml(
				 itemXml, respCond + "/setvar/@varname", "SCORE");
		 itemXml =
			 editItemHelper.updateItemXml(itemXml, respCond + "/setvar", points); // this should be minimum value

		 //Add display feedback
		 itemXml.add(respCond, "displayfeedback");
		 itemXml.addAttribute(respCond + "/displayfeedback", "feedbacktype");
		 itemXml =
			 editItemHelper.updateItemXml(
				 itemXml, respCond + "/displayfeedback/@feedbacktype", "Response");
		 itemXml.addAttribute(respCond + "/displayfeedback", "linkrefid");
		 if(correctFeedback)
		 {
			 itemXml =
				 editItemHelper.updateItemXml(
					 itemXml, respCond + "/displayfeedback/@linkrefid", "Correct");
		 }
		 else
		 {
			 itemXml =
				 editItemHelper.updateItemXml(
					 itemXml, respCond + "/displayfeedback/@linkrefid", "InCorrect");
		 }

		 return itemXml;
	 }


//******************************************************Old code ends**********************************




	private org.navigoproject.business.entity.Item addRespconditionCorrectFeedback(
			 org.navigoproject.business.entity.Item itemXml, String responseCondNo )
		 {
			 EditItemHelper editItemHelper = new EditItemHelper();
			 String xpath = "item/resprocessing";
			 itemXml.add(xpath, "respcondition");
			 String respCond =
				 "item/resprocessing/respcondition[" + responseCondNo + "]";
			 itemXml.addAttribute(respCond, "continue");
			 itemXml =
				 editItemHelper.updateItemXml(itemXml, respCond + "/@continue", "Yes");

			 //************************************************************************************************************
			 String and = "";

				 itemXml.add(respCond, "conditionvar/and");
				 and = respCond + "/conditionvar/and";
				 
				 for(int i = 1; i< (new Integer(responseCondNo)).intValue(); i++)
				 {
				 List or =  itemXml.selectNodes("item/resprocessing/respcondition["+i+"]/conditionvar/or" );
				 	if(or!=null )
				 	{
				 		itemXml.addElement(and, ((Element) or.get(0)));
				 	}
				 }
				 
			    
			 //************************************************************************************************************
		 

			 //Add display feedback
			 itemXml.add(respCond, "displayfeedback");
			 itemXml.addAttribute(respCond + "/displayfeedback", "feedbacktype");
			 itemXml =
				 editItemHelper.updateItemXml(
					 itemXml, respCond + "/displayfeedback/@feedbacktype", "Response");
			 itemXml.addAttribute(respCond + "/displayfeedback", "linkrefid");
			 
				 itemXml =
					 editItemHelper.updateItemXml(
						 itemXml, respCond + "/displayfeedback/@linkrefid", "Correct");
			   return itemXml;
		 }
		 
		 
	private org.navigoproject.business.entity.Item addRespconditionInCorrectFeedback(
			 org.navigoproject.business.entity.Item itemXml, String responseCondNo )
			 {			 EditItemHelper editItemHelper = new EditItemHelper();
				String xpath = "item/resprocessing";
				itemXml.add(xpath, "respcondition");
				String respCond =
					"item/resprocessing/respcondition[" + responseCondNo + "]";
				itemXml.addAttribute(respCond, "continue");
				itemXml =
					editItemHelper.updateItemXml(itemXml, respCond + "/@continue", "No"); 
			 
								itemXml.add(respCond, "conditionvar/other");
					 
					
//			Add display feedback
						 itemXml.add(respCond, "displayfeedback");
						 itemXml.addAttribute(respCond + "/displayfeedback", "feedbacktype");
						 itemXml =
							 editItemHelper.updateItemXml(
								 itemXml, respCond + "/displayfeedback/@feedbacktype", "Response");
						 itemXml.addAttribute(respCond + "/displayfeedback", "linkrefid");
			 
							 itemXml =
								 editItemHelper.updateItemXml(
									 itemXml, respCond + "/displayfeedback/@linkrefid", "InCorrect");
			 	return itemXml;
			 }
}
