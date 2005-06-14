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
 * Created on Aug 4, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.author.item;

import org.navigoproject.business.entity.RecordingData;
import org.navigoproject.osid.OsidManagerFactory;
import org.navigoproject.osid.questionpool.QuestionPoolDelegate;
import org.navigoproject.ui.web.FunctionalityDisabler;
import org.navigoproject.ui.web.asi.author.AuthoringHelper;
import org.navigoproject.ui.web.asi.author.assessment.AssessmentAction;
import org.navigoproject.ui.web.asi.author.assessment.AssessmentHelper;
import org.navigoproject.ui.web.asi.author.section.SectionHelper;
import org.navigoproject.ui.web.form.questionpool.QuestionPoolBean;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oroad.stxx.action.Action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.apache.xerces.dom.CharacterDataImpl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import osid.shared.SharedManager;

/**
 * DOCUMENTATION PENDING
 *
 * @author $author$
 * @version $Id: ItemAction.java,v 1.3 2004/10/15 21:50:27 lydial.stanford.edu Exp $
 */
public class ItemAction
  extends Action
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(AssessmentAction.class);

  /**
   * DOCUMENTATION PENDING
   *
   * @param mapping DOCUMENTATION PENDING
   * @param actionForm DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   * @param response DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ActionForward execute(
    ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
    HttpServletResponse response)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "perform(ActionMapping " + mapping + ", " + "ActionForm " + actionForm +
        ", " + "HttpServletRequest" + request + "HttpServletResponse" +
        response + ")");
    }

    // disable specific functionality
    FunctionalityDisabler.populateDisableVariables(request);

    ItemActionForm itemForm = (ItemActionForm) actionForm;
    AuthoringHelper authoringHelper = new AuthoringHelper();
    AssessmentHelper assessmentHelper = new AssessmentHelper();
    SectionHelper sectionHelper = new SectionHelper();
    ItemHelper itemHelper = new ItemHelper();
    Document document;

    String courseContext="assessment question";
    if (request.getParameter("assessmentID")!=null)
    {
      String aid = request.getParameter("assessmentID");
      osid.assessment.Assessment assessment =
          assessmentHelper.getAssessment(aid);
      try{
        courseContext = assessment.getDisplayName();
      }
      catch (osid.assessment.AssessmentException ae)
      {
        // use default value
      }
    }

    RecordingData recordingData =
      new RecordingData(
        authoringHelper.getRemoteUserID(request).toString(),
        authoringHelper.getRemoteUserName(request),
        courseContext, null, null);// the null values give default
    setRecordingInfoInDocument(recordingData, request);
    String action = request.getParameter("action");
    String fileName = null;
    String itemAuthor = null;
    org.navigoproject.business.entity.Item itemXml = null;
    String filePath = "/xml/author/";

    // set target to save question to
    String target = "assessment";
    if(request.getParameter("target") != null)
    {
      target = request.getParameter("target");
    }

    itemForm.setTarget(target);
    request.getSession().setAttribute("itemTarget", target);

    if(
      (request.getParameter("removeConfirm") != null) &&
        (request.getParameter("removeConfirm").equals("Remove")))
    {
      itemForm = (ItemActionForm) actionForm;
      String itemIdent = request.getParameter("ItemIdent");
      String sectionIdent = request.getParameter("SectionIdent");
      String assessmentID = request.getParameter("assessmentID");
      String ItemType = request.getParameter("itemType");
      itemForm.setItemIdent(itemIdent);
      itemForm.setSectionIdent(sectionIdent);
      itemForm.setAssessmentID(assessmentID);
      itemForm.setItemType(ItemType);

      return mapping.findForward("delete");
    }

    if((request.getParameter("myQuestions") != null) &&
      (request.getParameter("myQuestions").equals("My Questions")))
    {
      action = "My Questions";
    }


    if(
      (request.getParameter("removeCancel") != null) &&
        (request.getParameter("removeCancel").equals("Cancel")))
    {
      String itemIdent = request.getParameter("ItemIdent");
      if((itemIdent != null) && (itemIdent.trim().length() > 0))
      {
        action = "Modify";
      }
      else
      {
        action = request.getParameter("itemType");
      }
    }

    if(! ((action != null) && (action.trim().length() > 0)))
    {
      action = request.getParameter("reorder");
      if((action != null) && (action.trim().length() > 0))
      {
        action = "Reorder";
      }
    }

    if(action != null)
    {
      if(action.equals("Insert New Question"))
      {
        String itemType = request.getParameter("itemType");
        action = itemType;
      }

      if(action.equals("Import from Question Pool"))
      {
        String assessmentIdent = request.getParameter("assessmentID");
        request.getSession(true).setAttribute(
          "assessmentID4question", assessmentIdent);

        String sectionIdent = request.getParameter("SectionIdent");
        request.getSession(true).setAttribute(
          "sectionID4question", sectionIdent);

        LOG.debug(
          "assessmentId in session is" +
          (String) request.getSession(true).getAttribute(
            "assessmentID4question"));

        return mapping.findForward("questionChooser");
      }

      if(
        action.equals("True False") || (action.equals("Multiple Choice")) ||
          (action.equals("Multiple Correct Answer")) ||
          (action.equals("Fill In the Blank")) || (action.equals("Essay")) ||
          (action.equals("Matching")) ||
          (action.equals("Multiple Choice Survey")) ||
          (action.equals("File Upload")) || (action.equals("Audio Recording")))
      {
        // set item type
        itemForm.setItemType(action);
        fileName = filePath + "trueFalseTemplate.xml"; // default file
        if(action.equals("True False"))
        {
          fileName = filePath + "trueFalseTemplate.xml";
        }

        if(action.equals("Multiple Correct Answer"))
        {
          fileName = filePath + "mcMCTemplate.xml";
        }

        if(action.equals("Multiple Choice"))
        {
          fileName = filePath + "mcSCTemplate.xml";
        }

        if(action.equals("Fill In the Blank"))
        {
          fileName = filePath + "fibTemplate.xml";
          itemForm.setFibAnswer("");
        }

        if(action.equals("Essay"))
        {
          fileName = filePath + "essayTemplate.xml";
        }

        if(action.equals("Matching"))
        {
          fileName = filePath + "matchTemplate.xml";
        }

        if(action.equals("Multiple Choice Survey"))
        {
          fileName = filePath + "mcSurveyTemplate.xml";
        }

        if(action.equals("File Upload"))
        {
          fileName = filePath + "fileUploadTemplate.xml";
        }

        if(action.equals("Audio Recording"))
        {
          fileName = filePath + "audioRecordingTemplate.xml";
        }

        InputStream inputStream =
          this.servlet.getServletContext().getResourceAsStream(fileName);
        itemXml = itemHelper.readXMLDocument(inputStream);
        itemHelper.setActionForm(request, actionForm);
        request.getSession().setAttribute("xmlString", itemXml);
        request.getSession().setAttribute("xmlInputStream", inputStream); // this is for question pool createQuestion() method
        itemHelper.setItemDocument(request, itemXml, false);

        String currentSecitonID = request.getParameter("currentSecitonID");
        if((currentSecitonID != null) && (!currentSecitonID.equals("")))
        {
            itemForm.setSectionIdent(currentSecitonID);
        }
        
        return mapping.findForward("itemAuthor");
      }

      if(action.equals("Cancel"))
      {
        String assessmentID = request.getParameter("assessmentID");
        if(assessmentID != null)
        {
          assessmentHelper.setAssessmentDocument(assessmentID, request);
        }
        else
        {
          LOG.error("Invalid assessmentID passed ");

          return mapping.findForward("authoringMain");
        }

        return mapping.findForward("showCompleteAssessment");
      }

      if(action.equals("My Questions"))
      {
        String assessmentID = request.getParameter("assessmentID");
        if(assessmentID != null)
        {
          assessmentHelper.setAssessmentDocument(assessmentID, request);
        }
        else
        {
          LOG.error("Invalid assessmentID passed ");

          return mapping.findForward("authoringMain");
        }

        return mapping.findForward("showCompleteAssessment");
      }

      if(action.equals("Reorder"))
      {
        String assessmentID = request.getParameter("assessmentID");
        String itemIdent = request.getParameter("ItemIdent");
        String sectionID = request.getParameter("SectionIdent");

        String requiredPosition = request.getParameter("reorder");
        String totalItemsAssess = request.getParameter("totalItems");
        String itemPositionAssess = request.getParameter("itemNo");

        if(
          (requiredPosition != null) && (totalItemsAssess != null) &&
            (
              (new Integer(totalItemsAssess).intValue()) >= (
                new Integer(requiredPosition).intValue()
              )
            ))
        {
          // delete the item from the give position
          sectionHelper.removeItemFromSection(sectionID, itemIdent);

          // add the item at new position
          // also check if its a section move and spare processing time
          if(
            (requiredPosition != null) && (totalItemsAssess != null) &&
              totalItemsAssess.equals(requiredPosition))
          {
            //if its the last place it has to be moved
            String secID = assessmentHelper.getLastSectionRef(assessmentID);
            sectionHelper.addExistingItemToSection(secID, itemIdent);
          }

          if(
            (requiredPosition != null) && (totalItemsAssess != null) &&
              (! (totalItemsAssess.equals(requiredPosition))))
          {
            // if an insert
            assessmentHelper.insertItemInAssessment(
              assessmentID, requiredPosition, itemIdent);
          }
        }

        if(assessmentID != null)
        {
          assessmentHelper.setAssessmentDocument(assessmentID, request);
        }
        else
        {
          LOG.error("Invalid assessmentID passed  ");
					request.setAttribute("Error","Error");
					request.setAttribute("ErrorID","Unable to find assessment");
				
          return mapping.findForward("authoringMain");
        }
//			*****update Last Modified Date*******************
				assessmentHelper.updateLastModifiedBy(assessmentID);
        return mapping.findForward("showCompleteAssessment");
      }

      if(action.equals("Modify"))
      {
        String itemIdent = request.getParameter("ItemIdent");
        if(itemIdent != null)
        {
          itemXml = itemHelper.getItemXml(itemIdent);
          if(
            (request.getParameter("itemType") != null) &&
              request.getParameter("itemType").equals("Fill In the Blank"))
          {
            String fibAns = rebuildFibAns(itemXml);
LOG.info("lydiatest fibAns : " + fibAns);
            itemForm.setFibAnswer(fibAns);
          }

          request.getSession().setAttribute("xmlString", itemXml);
          itemHelper.setActionForm(request, actionForm);
          itemHelper.setItemDocument(request, itemXml, false);

          return mapping.findForward("itemAuthor");
        }
        else
        {
          LOG.error("Item Modify: Null ItemID passed ");
					request.setAttribute("Error","Error");
					request.setAttribute("ErrorID","Unable to modify assessment");
				
          return mapping.findForward("authoringMain");
        }
      }

      if(action.equals("Preview"))
      {
        ///
        String itemIdent = request.getParameter("ItemIdent");
        if(itemIdent != null)
        {
          itemXml = itemHelper.getItemXml(itemIdent);

          request.getSession().setAttribute("xmlString", itemXml);
          itemHelper.setActionForm(request, actionForm);
          itemHelper.setItemDocument(request, itemXml, false);
          return mapping.findForward("itemPreview");
        }

        ///
      }

      if(action.equals("Remove"))
      {
        if(target.equals("questionpool"))
        {
          QuestionPoolBean currentPool =
            (QuestionPoolBean) request.getSession().getAttribute("currentPool");
          if(currentPool != null)
          {
            try
            {
              String itemIdent = request.getParameter("ItemIdent");
              QuestionPoolDelegate delegate = new QuestionPoolDelegate();
              SharedManager sm = OsidManagerFactory.createSharedManager();
              delegate.removeQuestionFromPool(
                sm.getId(itemIdent), sm.getId(currentPool.getId()));
            }
            catch(Exception e)
            {
              LOG.error(e); throw new Error(e);
            }
          }

          return mapping.findForward("questionChooser");
        }
        else
        { // for assessment
          String sectionID = request.getParameter("SectionIdent");
          String itemIdent = request.getParameter("ItemIdent");

          if(
            (sectionID != null) && (sectionID.trim().length() > 0) &&
              (itemIdent != null) && (itemIdent.trim().length() > 0))
          {
            sectionHelper.removeItemFromSection(sectionID, itemIdent);
          }

          String assessmentID = request.getParameter("assessmentID");
          if(assessmentID != null)
          {
            assessmentHelper.setAssessmentDocument(assessmentID, request);
//					*****update Last Modified Date*******************
						assessmentHelper.updateLastModifiedBy(assessmentID);
          }
          else
          {
            LOG.error("Invalid assessmentID passed ");
						request.setAttribute("Error","Error");
						request.setAttribute("ErrorID","Unable to find assessment");
				
            return mapping.findForward("authoringMain");
          }

          return mapping.findForward("showCompleteAssessment");
        }
      }
    }
		request.setAttribute("Error","Error");
		request.setAttribute("ErrorID","No action found");
    return mapping.findForward("authoringMain");
  }

  /**
   * DOCUMENT ME!
   *
   * @param itemXml
   *
   * @return
   */
  private String rebuildFibAns(org.navigoproject.business.entity.Item itemXml)
  {
   // String mExclusive = itemXml.getFieldentry("MUTUALLY_EXCLUSIVE");
    int matSize = 0;
    int respSize = 0;
    String respIdent="";
    String varequal = "";
		String tempVarequal = "";
    String fibAnswer = "";
    String mattext = "";
    int incrementNo = -1;
		String responseIdent = "";
    int correctRespCondition = 1;

    List mat = itemXml.selectNodes("item/presentation/flow/flow/material");
    List resp = itemXml.selectNodes("item/presentation/flow/flow/response_str");

    if((mat != null) && (mat.size() > 0))
    {
      matSize = mat.size();
    }

    if((resp != null) && (resp.size() > 0))
    {
      respSize = resp.size();
    }

    int largerSize = matSize;
    if(respSize > matSize)
    {
      largerSize = respSize;
    }

    for(int i = 1; i <= largerSize; i++)
    {
      incrementNo++;
      mattext =
        itemXml.selectSingleValue(
          "item/presentation/flow/flow/material[" + i + "]/mattext", "element");
      if((mattext != null) && ! mattext.equals("") && ! mattext.equals("null"))
      {
        fibAnswer = fibAnswer + mattext;
      }

      correctRespCondition = incrementNo + i;
      varequal = "";
      respIdent = itemXml.selectSingleValue(
			"item/presentation/flow/flow/response_str[" + i + "]/@ident", "attribute");
			if(respIdent!=null && respIdent.trim().length()>0)
			{
		  List respCondition =
									itemXml.selectNodes("item/resprocessing/respcondition");
				if(respCondition != null && respCondition.size()>2)
				{
					for(int j=1; j < (respCondition.size() -1); j++)
					{
						List varequalList =	itemXml.selectNodes("item/resprocessing/respcondition["+j+"]/conditionvar/or/varequal");
						 if(varequalList != null && varequalList.size()>0)
						 
								for(int p=1; p < (varequalList.size()+1); p++)
							{
							
								responseIdent =	itemXml.selectSingleValue("item/resprocessing/respcondition["+j+"]/conditionvar/or/varequal["+p+"]/@respident","attribute");
								tempVarequal = itemXml.selectSingleValue("item/resprocessing/respcondition["+j+"]/conditionvar/or/varequal["+p+"]","element");
									if(respIdent.equals(responseIdent))
									{
										varequal = varequal + ", "+tempVarequal;
									}
							}
						//Element element = (Element) respCondition.get(j);
					}
					if((varequal != null) && ! varequal.equals("") &&
											! varequal.equals("null"))
						{
							fibAnswer = fibAnswer + "{" + varequal + "}";
						}
									
				}
				 
				 
			}
		// last two response conditions are for feedback, they should be ignored.
			




//OLD CODE     
// if((mExclusive != null) && mExclusive.equals("False"))
//      {
//        List allVars =
//          itemXml.selectNodes(
//            "item/resprocessing/respcondition[" + correctRespCondition +
//            "]/conditionvar/or/varequal");
//
//        if(allVars != null)
//        {
//          for(int j = 0; j < allVars.size(); j++)
//          {
//            Element element = (Element) allVars.get(j);
//            CharacterDataImpl tempNode =
//              (CharacterDataImpl) element.getFirstChild();
//            String temp = "";
//            if(
//              (tempNode != null) && (tempNode.getNodeValue() != null) &&
//                (tempNode.getNodeValue().trim().length() > 0))
//            {
//              temp = tempNode.getNodeValue();
//              varequal = varequal + temp;
//              if(j != (allVars.size() - 1))
//              {
//                varequal = varequal + ", ";
//              }
//            }
//          }
//        }
//
//        if(
//          (varequal != null) && ! varequal.equals("") &&
//            ! varequal.equals("null"))
//        {
//          fibAnswer = fibAnswer + "{" + varequal + "}";
//        }
//      }
//      else
//      {
//        List allAnds =
//          itemXml.selectNodes(
//            "item/resprocessing/respcondition[" + correctRespCondition +
//            "]/conditionvar/or/and");
//
//        if(allAnds != null)
//        {
//          for(int j = 0; j < allAnds.size(); j++)
//          {
//            int jString = j + 1;
//            String temp =
//              itemXml.selectSingleValue(
//                "item/resprocessing/respcondition[" + correctRespCondition +
//                "]/conditionvar/or/and[" + jString + "]/varequal[1]", "element");
//            varequal = varequal + temp;
//            if(j != (allAnds.size() - 1))
//            {
//              varequal = varequal + ", ";
//            }
//          }
//        }
//
//        if(
//          (varequal != null) && ! varequal.equals("") &&
//            ! varequal.equals("null"))
//        {
//          fibAnswer = fibAnswer + "{" + varequal + "}";
//        }
//      }
    }

    return fibAnswer;
  }

  /**
   * This takes a RecordingData object and puts it in the request XML.
   * You can put any Action-specific modifications in here.
   * @param recordingData RecordingData encapsulating the audio settings
   * @param request the request object
   */
  private void setRecordingInfoInDocument(
    RecordingData recordingData, HttpServletRequest request)
  {
    Document document = recordingData.getXMLDataModel();
    saveDocument(request, document);
  }
}
