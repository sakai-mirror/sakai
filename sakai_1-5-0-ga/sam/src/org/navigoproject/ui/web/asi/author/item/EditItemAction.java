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
 * Created on Oct 30, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.author.item;

import org.navigoproject.AuthoringConstantStrings;
import org.navigoproject.business.entity.AAMTree;
import org.navigoproject.business.entity.questionpool.model.QuestionPool;
import org.navigoproject.business.entity.questionpool.model.QuestionPoolProperties;
import org.navigoproject.business.entity.questionpool.model.QuestionPoolTreeImpl;
import org.navigoproject.osid.OsidManagerFactory;
import org.navigoproject.osid.questionpool.QuestionPoolDelegate;
import org.navigoproject.ui.web.FunctionalityDisabler;
import org.navigoproject.ui.web.asi.author.AuthoringHelper;
import org.navigoproject.ui.web.asi.author.assessment.AssessmentHelper;
import org.navigoproject.ui.web.asi.author.section.SectionHelper;
import org.navigoproject.ui.web.form.questionpool.QuestionPoolBean;
import org.navigoproject.ui.web.form.questionpool.QuestionPoolForm;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oroad.stxx.action.Action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import osid.assessment.Item;

import osid.shared.Agent;
import osid.shared.Id;
import osid.shared.SharedManager;

/**
 * DOCUMENT ME!
 *
 * @author rshastri To change the template for this generated type comment go
 *         to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 *         Comments
 */
public class EditItemAction
  extends Action
{
  /**
   * DOCUMENTATION PENDING
   */
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(EditItemAction.class);

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

    String action = request.getParameter("action");
    AuthoringHelper authoringHelper = new AuthoringHelper();
    AssessmentHelper assessmentHelper = new AssessmentHelper();
    SectionHelper sectionHelper = new SectionHelper();
    ItemHelper itemHelper = new ItemHelper();
    EditItemHelper editItemHelper = new EditItemHelper();

    // set target to save question to
    String target = "assessment";
    if(request.getSession().getAttribute("itemTarget") != null)
    {
      target = (String) request.getSession().getAttribute("itemTarget");
    }

    itemForm.setTarget(target);

    String RemoveNo = null;
    for(int i = 1; i < 101; i++)
    {
      String no = "Remove" + i;
      String isRem = request.getParameter(no);
      if((isRem != null) && isRem.equals("Remove"))
      {
        RemoveNo = new Integer(i).toString();
        action = "Remove";

        break;
      }
    }

    //this is for matching.
    if(
      (request.getParameter("AddT") != null) &&
        request.getParameter("AddT").equals("Add"))
    {
      action = "Insert";
    }

    if(
      (request.getParameter("insert") != null) &&
        request.getParameter("insert").equals("Insert"))
    {
      action = "Add";
    }

    if(action != null)
    {
      if(action.equals("Remove"))
      {
        editItemHelper.removeResponseLabel(request, RemoveNo);
        itemHelper.setActionForm(request, actionForm);

        return mapping.findForward("itemAuthor");
      }

      if(action.equals("Cancel") || action.equals("Preview"))
      {
				if (target.equals("questionpool")){
				
				
				
				          return mapping.findForward("editPool");
				}
        LOG.debug("Cancel");
        String assessmentID = request.getParameter("assessmentID");

       
        if(assessmentID != null)
        {
          assessmentHelper.setAssessmentDocument(assessmentID, request);
        }
        else
        {
					request.setAttribute("Error","Error");
					request.setAttribute("ErrorID","Unable to retrieve assessment");
				
          return mapping.findForward("authoringMain");
        }
				
        return mapping.findForward("showCompleteAssessment");
      }
      
      if(action.equals("Back to Terms"))
      {
        org.navigoproject.business.entity.Item itemXml =
          (org.navigoproject.business.entity.Item) request.getSession()
                                                          .getAttribute(
            "xmlString");
        itemHelper.setActionForm(request, actionForm);
        itemHelper.setItemDocument(request, itemXml, false);

        return mapping.findForward("itemAuthor");
      }

      // Add more responses?
      if(action.equals("Add") || action.equals("Insert"))
      {
        String ansNos = null;
        String responseNo = null;
        boolean isInsert = false;
        boolean shouldAddResponseCondition = true;
        int respNo;
        if(action.equals("Add"))
        {
          ansNos = request.getParameter("answerNumbers");
          responseNo = request.getParameter("responseNo");
          respNo = new Integer(responseNo).intValue();
          if(
            (request.getParameter("itemType") != null) &&
              request.getParameter("itemType").equals(
                AuthoringConstantStrings.MATCHING))
          {
            shouldAddResponseCondition = true; // this is target and a respcondition must be added
          }
        }
        else
        {
          shouldAddResponseCondition = false;
          ansNos = request.getParameter("termNumbers");
          responseNo = request.getParameter("noOfLastSource");
          respNo = new Integer(responseNo).intValue() + 1; // as we'll need one more than last source
          isInsert = true;
        }

        org.navigoproject.business.entity.Item itemXml =
          (org.navigoproject.business.entity.Item) request.getSession()
                                                          .getAttribute(
            "xmlString");
        itemXml = editItemHelper.updateXml(itemXml, request);
        // IF it is  matching
				if((request.getParameter("itemType") != null) &&
					( request.getParameter("itemType").equals(AuthoringConstantStrings.MATCHING)
					))
					{
						MatchingHelper matchingHelper = new MatchingHelper();
						matchingHelper.addResponseLabel(request, Integer.toString(respNo), itemXml, isInsert,
						shouldAddResponseCondition);
						itemXml = editItemHelper.updateXml(itemXml, request);
					}      
			else 
       {
        if((ansNos != null) && (ansNos.trim().length() > 0))
        {
          int noAns = new Integer(ansNos).intValue();

          for(int i = 0; i < noAns; i++)
          {
            editItemHelper.addResponseLabel(
              request, Integer.toString(respNo), itemXml, isInsert,
              shouldAddResponseCondition);
            respNo = respNo + 1;
          }
        }
        else
        {
          editItemHelper.addResponseLabel(
            request, Integer.toString(respNo), itemXml, isInsert,
            shouldAddResponseCondition);
        }
      }
        request.getSession().setAttribute("xmlString", itemXml);

        itemHelper.setActionForm(request, actionForm);
        itemHelper.setItemDocument(request, itemXml, true);

        return mapping.findForward("itemAuthor");
      }

      if(action.equals("Save"))
      {
        if(target.equals("questionpool")) // Add to question pool 
        {
          QuestionPoolBean currentPool =
            (QuestionPoolBean) request.getSession().getAttribute("currentPool");
          if(currentPool != null)
          {
            try
            {
              // add a question to a pool
              QuestionPoolDelegate delegate = new QuestionPoolDelegate();
              SharedManager sm = OsidManagerFactory.createSharedManager();
              String displayName = "Item";
              String description = "QTIxml";
              String questionText =
                request.getParameter(
                  "stxx/item/presentation/flow/material/mattext");

          //For Survey Type question
          //******************************************************
          String scaleName = request.getParameter("scaleName");
          if(
            (scaleName != null) ||
              (
                (request.getParameter("itemType") != null) &&
                (
                  request.getParameter("itemType").equals(
                    AuthoringConstantStrings.FIB)
                )
              ))
          {
            org.navigoproject.business.entity.Item itemXml = null;
            InputStream inputStream = null;
            String fileName = null;
            String filePath = "/xml/author/";
            if((scaleName != null) && (scaleName.trim().length() > 0))
            {
              filePath = "/xml/author/survey/";
              fileName = filePath + scaleName + ".xml";
            }


           //******************************************************
            //For FIB redo the response_labels over
            String isFIB = request.getParameter("itemType");
            if((isFIB != null) && (isFIB.equals(AuthoringConstantStrings.FIB)))
            {
              fileName = filePath + "fibTemplate.xml";
            }
           inputStream =
              this.servlet.getServletContext().getResourceAsStream(fileName);
            itemXml = itemHelper.readXMLDocument(inputStream);

            if(
              (request.getParameter("ItemIdent") != null) &&
                (request.getParameter("ItemIdent").trim().length() > 0))
            {
              itemXml =
                editItemHelper.updateItemXml(
                  itemXml, "item/@ident", request.getParameter("ItemIdent"));
            }

            request.getSession().setAttribute("xmlString", itemXml);
          }



              org.navigoproject.business.entity.Item itemXml =
                (org.navigoproject.business.entity.Item) request.getSession()
                                                                .getAttribute(
                  "xmlString");

              Item newItem = null;
              boolean existing = false;
              if(
                (request.getParameter("ItemIdent") != null) &&
                  (request.getParameter("ItemIdent").trim().length() > 0))
              {
                existing = true;
              }
              else
              {
                existing = false;
              }

              newItem =
                delegate.createQuestion(
                  displayName, description, itemXml, request);
              if(existing)
              {
                // do nothing
              }
              else
              {
                delegate.addItemToPool(
                  newItem.getId(), sm.getId(currentPool.getId()));
              }

              QuestionPool thepool =
                delegate.getPool(
                  sm.getId(currentPool.getId()),
                  OsidManagerFactory.getAgent());
              QuestionPoolProperties prop =
                (QuestionPoolProperties) thepool.getData();
              currentPool.setProperties(prop);

              request.getSession().setAttribute("currentPool", currentPool);
            }
            catch(Exception e)
            {
              LOG.error(e); throw new Error(e);
            }
          }

         request.getSession().removeAttribute("xmlString");
          return mapping.findForward("questionEditor");
        }
        else // Add to assessment 
        {
          LOG.debug("Save");

          //For Survey Type question
          //******************************************************
          String scaleName = request.getParameter("scaleName");
          if(
            (scaleName != null) ||
              (
                (request.getParameter("itemType") != null) &&
                (
                  request.getParameter("itemType").equals(
                    AuthoringConstantStrings.FIB)
                )
              ))
          {
            org.navigoproject.business.entity.Item itemXml = null;
            InputStream inputStream = null;
            String fileName = null;
            String filePath = "/xml/author/";
            if((scaleName != null) && (scaleName.trim().length() > 0))
            {
              filePath = "/xml/author/survey/";
              fileName = filePath + scaleName + ".xml";
            }

            //******************************************************
            //For FIB redo the response_labels over
            String isFIB = request.getParameter("itemType");
            if((isFIB != null) && (isFIB.equals(AuthoringConstantStrings.FIB)))
            {
              fileName = filePath + "fibTemplate.xml";
            }

            inputStream =
              this.servlet.getServletContext().getResourceAsStream(fileName);
            itemXml = itemHelper.readXMLDocument(inputStream);

            if(
              (request.getParameter("ItemIdent") != null) &&
                (request.getParameter("ItemIdent").trim().length() > 0))
            {
              itemXml =
                editItemHelper.updateItemXml(
                  itemXml, "item/@ident", request.getParameter("ItemIdent"));
            }

            request.getSession().setAttribute("xmlString", itemXml);
          }

          //******************************************************
          editItemHelper.saveItem(request);

          //******************************************************
          String assessmentID = request.getParameter("assessmentID");
          assessmentHelper.setAssessmentDocument(assessmentID, request);
//				*****update Last Modified Date*******************
					assessmentHelper.updateLastModifiedBy(assessmentID);
          request.getSession().removeAttribute("xmlString");
 	        return mapping.findForward("showCompleteAssessment");
        }
      }

      // DELETE continued matching
      if(action.equals("Save Items and Continue"))
      {
        org.navigoproject.business.entity.Item itemXml =
          (org.navigoproject.business.entity.Item) request.getSession()
                                                          .getAttribute(
            "xmlString");
        itemXml = editItemHelper.updateXml(itemXml, request);
        itemHelper.setActionForm(request, actionForm);

        if(itemXml != null)
        {
          itemHelper.setItemDocument(request, itemXml, false);
        }
        else
        {
          LOG.error("Could not retrive itemXML from memory");
					request.setAttribute("Error","Error");
					request.setAttribute("ErrorID","Unable to get question");
				
          return mapping.findForward("authoringMain");
        }

        return mapping.findForward("matchContinued");
      }

      // DELETE END 
    }
		request.setAttribute("Error","Error");
		request.setAttribute("ErrorID","No action found");
				
    return mapping.findForward("authoringMain");
  }
}
