/*
 *                       Navigo Software License
 *
 * Copyright 2003, Trustees of Indiana University, The Regents of the University
 * of Michigan, and Stanford University, all rights reserved.
 *
 * This work, including software, documents, or other related items (the
 * "Software"), is being provided by the copyright holder(s)s subject to the
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
 * Created on Nov 7, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.author.section;



import org.navigoproject.osid.assessment.impl.AssessmentManagerImpl;
import org.navigoproject.ui.web.asi.author.AuthoringHelper;
import org.navigoproject.ui.web.asi.author.assessment.AssessmentActionForm;
import org.navigoproject.ui.web.asi.author.assessment.AssessmentHelper;
import org.navigoproject.ui.web.asi.author.questionpool.QuestionPoolHelper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oroad.stxx.action.Action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.w3c.dom.Document;

import osid.assessment.Section;
import osid.authentication.AuthenticationException;
import osid.shared.Agent;
///**************************************************
import org.navigoproject.business.entity.AAMTree;
import org.navigoproject.business.entity.questionpool.model.QuestionPoolIterator;
import org.navigoproject.business.entity.questionpool.model.QuestionPoolTreeImpl;
import org.navigoproject.osid.questionpool.impl.QuestionPoolImpl;
import org.navigoproject.osid.questionpool.impl.QuestionPoolIteratorImpl;
import org.navigoproject.osid.OsidManagerFactory;

import org.sakaiproject.tool.assessment.data.dao.questionpool.QuestionPoolProperties;
///************************************************** 
/**
 * DOCUMENT ME!
 *
 * @author rshastri To change the template for this generated type comment go
 *         to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 *         Comments
 */
public class SectionAction
  extends Action
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(SectionAction.class);

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
		String findForward = "sectionAuthor";
		Document document;
		String assessmentID = request.getParameter("assessmentID");
		String fileName = null;
		String status = "new";
		org.navigoproject.business.entity.Section sectionXml = null;
		AssessmentManagerImpl assessmentManager = new AssessmentManagerImpl();
		AuthoringHelper 	authoringHelper = new 	AuthoringHelper();
		AssessmentHelper assessmentHelper = new AssessmentHelper();
		SectionHelper sectionHelper = new SectionHelper();


		SectionActionForm sectionForm = (SectionActionForm) actionForm;
		String filePath = "/xml/author/";
		if(request.getParameter("removeConfirm") != null && (request.getParameter("removeConfirm").equals("Remove")))
		{ 
			String sectionIdent = request.getParameter("SectionIdent");
			if((sectionIdent == null) || (sectionIdent.equals("")))
			{
		      setAssessment(request);
		      return mapping.findForward("showCompleteAssessment");
			}
			assessmentID = request.getParameter("assessmentID");
		 	sectionForm.setAssessmentID(assessmentID);
			sectionForm.setSectionIdent(sectionIdent);
			sectionForm.setNoOfItems(new Integer(sectionHelper.getSectionItems(request.getParameter("SectionIdent"),true).size()).toString());
			if((assessmentID != null) && (assessmentID.trim().length() > 0))
				{
				 	ArrayList arr = assessmentHelper.getSections(assessmentID);
					sectionForm.setAssessmentSectionIdents(arr);
				}
			return mapping.findForward("delete");		
		}		
		

    String action = request.getParameter("action");
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
      if(
        (request.getParameter("SectionIdent") != null) &&
          (request.getParameter("SectionIdent").length() > 0))
      {
        status = "existing";
      }
			if(action.equals("Add"))
			{
				InputStream inputStream =
				this.servlet.getServletContext().getResourceAsStream(
					filePath + "sectionTemplate.xml");
				QuestionPoolHelper questionPoolHelper = new  QuestionPoolHelper();
			sectionXml =sectionHelper.readXMLDocument(inputStream);
			assessmentID = request.getParameter("assessmentID");
			//TODO common in Add and Modify
			sectionForm.setAssessTitle(request.getParameter("assessTitle"));
			sectionForm.setAssessmentID(assessmentID);
			sectionForm.setShowMetadata(assessmentHelper.getFieldentry(assessmentID,"COLLECT_SECTION_METADATA"));
			sectionForm.setPoolsAvailable(questionPoolHelper.getPoolsPlusName(request));
			request.getSession().setAttribute("xmlString", sectionXml);
			sectionHelper.setSectionDocument(sectionXml, request);
			return mapping.findForward("sectionAuthor");
			}
      if(action.equals("Save"))
      {
        sectionXml =
          (org.navigoproject.business.entity.Section) request.getSession()
                                                             .getAttribute(
            "xmlString");
        if(sectionXml != null)
        {
          sectionXml = updateXml(sectionXml, request);

          //common code begin
          //          assessmentIDinSession =
          //            (String) request.getSession().getAttribute("ASSESSMENT_ID");
          Section section;
          if(assessmentID != null)
          {
            String sectionIdent = null;

            if(status == "new")
            { // create a new section
              sectionIdent =
                assessmentHelper.createSection(
                  assessmentID, request.getParameter("stxx/section/@title"),
                  sectionXml);

              //Error creating a section
              if(sectionIdent == null)
              {
                LOG.error("Error Creating a new Section");

                return mapping.findForward("authoringMain");
              }
            }
            else
            {
              String sectionid = request.getParameter("SectionIdent");
              if(sectionid != null)
              {
                sectionHelper.sectionUpdateData(sectionid, sectionXml);
              }
              else
              {
                LOG.error(
                  "Trying to retrive an existing Section with out passing appropriate section Ident");
								request.setAttribute("Error","Error");
														request.setAttribute("ErrorID","Unable to find section");
                return mapping.findForward("authoringMain");
              }
            }

            // add Remove items from the section begin
            /*    String[] addItems =
               request.getParameterValues("defaultItemSelected");
               String[] removeItems =
                 request.getParameterValues("currentItemSelected");
               String currentSection = request.getParameter("SectionIdent");
                String defaultSection =    (String) request.getSession().getAttribute("SECTION_ID");
               if(status == "new")
               {
                 currentSection = sectionIdent;
               }
               if(
                 (addItems != null) && (addItems.length > 0) &&
                   (defaultSection != null) && (currentSection != null) &&
                   (currentSection.trim().length() > 0))
               {
                 sectionHelper.moveItemsBetweenSections(
                   addItems, defaultSection, currentSection);
               }
               if(
                 (removeItems != null) && (removeItems.length > 0) &&
                   (request.getSession().getAttribute("SECTION_ID") != null) &&
                   (currentSection != null) &&
                   (currentSection.trim().length() > 0))
               {
                 sectionHelper.moveItemsBetweenSections(
                   removeItems, currentSection, defaultSection);
               }
             */
            // add Remove items from the section end
            assessmentHelper.setAssessmentDocument(assessmentID, request);
//					*****update Last Modified Date*******************
						assessmentHelper.updateLastModifiedBy(assessmentID);
            findForward = "showCompleteAssessment";
          }
        }
        else
        {
          LOG.error(
            "No sectionXml retrieved from session. Can not update the section.");
					request.setAttribute("Error","Error");
											request.setAttribute("ErrorID","Unable to find section");
          return mapping.findForward("authoringMain");
        }
      }
    }
    // move all items from one section to another
    
		if(action.equals("Move"))
		{
			String sectionID = request.getParameter("SectionIdent");
			String sectionIDSelected = request.getParameter("SectionIdentSelected");
			sectionHelper.moveAllItems(sectionID, sectionIDSelected);
			action = "Remove";
		}
 		
    // Removes section
    if(action.equals("Remove"))
    {
      assessmentID = request.getParameter("assessmentID");

      //      assessmentID =
      //        (String) request.getSession().getAttribute("ASSESSMENT_ID");
      String sectionid = request.getParameter("SectionIdent");
      if((assessmentID.trim().length() > 0) && (sectionid.trim().length() > 0))
      {
        assessmentHelper.removeSection(sectionid, assessmentID);
      }
      else
      {
        LOG.error(
          "Trying to remove an existing Section with out passing appropriate section Ident");
				request.setAttribute("Error","Error");
										request.setAttribute("ErrorID","Unable to find section to be removed");
				return mapping.findForward("authoringMain");						
      }

      setAssessment(request);
//		*****update Last Modified Date*******************
								assessmentHelper.updateLastModifiedBy(assessmentID);
      findForward = "showCompleteAssessment";
    }

    if(action.equals("Modify"))
    {
      String secIdent = request.getParameter("SectionIdent");
      if((secIdent != null) && (secIdent.trim().length() > 0))
      {
        org.navigoproject.business.entity.Section secXml =
          sectionHelper.getSectionXml(secIdent);
        QuestionPoolHelper questionPoolHelper = new  QuestionPoolHelper();
        request.getSession().setAttribute("xmlString", secXml);
        sectionForm.setAssessTitle(request.getParameter("assessTitle"));
        sectionForm.setAssessmentID(assessmentID);
        sectionForm.setShowMetadata(assessmentHelper.getFieldentry(assessmentID, "COLLECT_SECTION_METADATA"));
        sectionForm.setPoolsAvailable(questionPoolHelper.getPoolsPlusName(request));
			  sectionHelper.setSectionDocument(secXml, request);
      }

      findForward = "sectionAuthor";

      return mapping.findForward(findForward);
    }

    // REORDER 
    if(action.equals("Reorder"))
    {
  	  assessmentID = request.getParameter("assessmentID");
      String sectionID = request.getParameter("SectionIdent");

      String requiredPosition = request.getParameter("reorder");
      String totalSectionsAssess = request.getParameter("totalSections");
      if(
        (requiredPosition != null) && (totalSectionsAssess != null) &&
          (
            (new Integer(totalSectionsAssess).intValue()) >= (
              new Integer(requiredPosition).intValue()
            )
          ))
      {
        // delete the section from the give position
        assessmentHelper.removeSection(sectionID, assessmentID);

        // add section at new position
        if(
          (new Integer(totalSectionsAssess).intValue()) == (
              new Integer(requiredPosition).intValue()
            ))
        {
        	 assessmentHelper.appendSection(assessmentID, sectionID);
         
        }
        else
        {
					assessmentHelper.insertSection(
										 assessmentID, requiredPosition, sectionID);
        }
      }

      if(assessmentID != null)
      {
        assessmentHelper.setAssessmentDocument(assessmentID, request);
      }
      else
      {
        LOG.error("Pull composed assessment : Null assessmentID passed ");
				request.setAttribute("Error","Error");
										request.setAttribute("ErrorID","Unable to find assessment");
        return mapping.findForward("authoringMain");
      }
//		*****update Last Modified Date*******************
			assessmentHelper.updateLastModifiedBy(assessmentID);
      return mapping.findForward("showCompleteAssessment");
    }

    //END REORDER
    if(action.equals("Cancel"))
    {
      setAssessment(request);
      findForward = "showCompleteAssessment";
    }

    return mapping.findForward(findForward);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   */
  private void setAssessment(HttpServletRequest request)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("setAssessment(HttpServletRequest " + request + " )");
    }

    AssessmentHelper assessmentHelper = new AssessmentHelper();

    //    String assessmentIDinSession =
    //      (String) request.getSession().getAttribute("ASSESSMENT_ID");
    String assessmentID = request.getParameter("assessmentID");
    if(assessmentID != null)
    {
      assessmentHelper.setAssessmentDocument(assessmentID, request);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param sectionXml DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private org.navigoproject.business.entity.Section updateXml(
    org.navigoproject.business.entity.Section sectionXml,
    HttpServletRequest request)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "updateXml(org.navigoproject.business.entity.Section" + sectionXml +
        "HttpServletRequest " + request + " )");
    }

    SectionHelper sectionHelper = new SectionHelper();
    Map requestMap = request.getParameterMap();
    Set keySet = requestMap.keySet();
    Iterator setIter = keySet.iterator();

    while(setIter.hasNext())
    {
      String key = setIter.next().toString();
      if(! (key == null) && ! ((key.length()) == 0) && key.startsWith("stxx"))
      {
        String reqParam = request.getParameter(key);
        key = key.substring(5);
        if(reqParam == null)
        {
          reqParam = "''";
        }

        sectionXml = sectionHelper.updateSectionXml(sectionXml, key, reqParam);
      }
    }

    if(
      (request.getParameter("order_type") != null) &&
        request.getParameter("order_type").equals("on"))
    {
      sectionXml =
        sectionHelper.updateSectionXml(
          sectionXml, "section/selection_ordering/order/@order_type", "Random");
    }

    else
    {
      sectionXml =
        sectionHelper.updateSectionXml(
          sectionXml, "section/selection_ordering/order/@order_type",
          "Sequential");
    }

    return sectionXml;
  }
}
