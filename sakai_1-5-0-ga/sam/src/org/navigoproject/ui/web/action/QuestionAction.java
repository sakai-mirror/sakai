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

package org.navigoproject.ui.web.action;

import org.navigoproject.business.entity.AAMTree;
import org.navigoproject.business.entity.assessment.model.AssessmentPropertiesImpl;
import org.navigoproject.business.entity.assessment.model.AssessmentTemplateImpl;
import org.navigoproject.business.entity.assessment.model.AssessmentTemplatePropertiesImpl;
import org.navigoproject.business.entity.assessment.model.ItemImpl;
import org.navigoproject.business.entity.assessment.model.ItemPropertiesImpl;
import org.navigoproject.business.entity.assessment.model.SectionPropertiesImpl;
import org.navigoproject.business.entity.questionpool.model.QuestionPool;
import org.navigoproject.business.entity.questionpool.model.QuestionPoolIterator;
import org.navigoproject.business.entity.questionpool.model.QuestionPoolProperties;
import org.navigoproject.business.entity.questionpool.model.QuestionPoolTreeImpl;
import org.navigoproject.data.IdHelper;
import org.navigoproject.osid.OsidManagerFactory;
import org.navigoproject.osid.assessment.AssessmentServiceDelegate;
import org.navigoproject.osid.assessment.impl.AssessmentManagerImpl;
import org.navigoproject.osid.questionpool.QuestionPoolDelegate;
import org.navigoproject.osid.questionpool.impl.QuestionPoolImpl;
import org.navigoproject.osid.questionpool.impl.QuestionPoolIteratorImpl;
import org.navigoproject.ui.web.asi.author.item.ItemActionForm;
import org.navigoproject.ui.web.asi.author.item.ItemHelper;
import org.navigoproject.ui.web.asi.author.assessment.AssessmentHelper;
import org.navigoproject.ui.web.asi.author.section.SectionHelper;
import org.navigoproject.ui.web.form.edit.QuestionForm;
import org.navigoproject.ui.web.form.questionpool.QuestionPoolBean;
import org.navigoproject.ui.web.form.questionpool.QuestionPoolForm;
import org.navigoproject.ui.web.FunctionalityDisabler;


import java.io.InputStream;
import java.io.*;

import java.util.*;

import javax.servlet.http.*;
import javax.servlet.*;

import org.apache.commons.beanutils.BeanUtils;

import org.apache.log4j.Logger;

import org.apache.struts.action.*;
import org.apache.struts.upload.FormFile;

import osid.assessment.Assessment;
import osid.assessment.AssessmentManager;
import osid.assessment.Item;
import osid.assessment.Section;
import osid.assessment.SectionIterator;

import osid.shared.Agent;
import osid.shared.Id;
import osid.shared.SharedManager;

/**
 * Handles the Question Actions.
 *
 * @author Lydia Li<lydial@stanford.edu>
 * $Id: QuestionAction.java,v 1.7 2004/10/15 21:50:26 lydial.stanford.edu Exp $
 */
public class QuestionAction
  extends AAMAction
{
  static Logger LOG = Logger.getLogger(QuestionAction.class.getName());

  private AAMTree tree;
// This just builds the tree.
  public void buildTree(HttpServletRequest request)
  {
    try
    {
      QuestionPoolDelegate delegate = new QuestionPoolDelegate();
      tree =
        new QuestionPoolTreeImpl(
          (QuestionPoolIteratorImpl) delegate.getAllPools(
            OsidManagerFactory.getAgent()));

    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }

  /**
   * This is the standard execute method.  It handles the submit methods for
   * the question pool actions.
   */
  public ActionForward execute(
    ActionMapping actionMapping, ActionForm actionForm,
    HttpServletRequest httpServletRequest,
    HttpServletResponse httpServletResponse)
  {
    ActionForward forward = null;
    HttpSession session = httpServletRequest.getSession(true);

    // disable specific functionality
    FunctionalityDisabler.populateDisableVariables(httpServletRequest);

    if(tree == null)
    {
      buildTree(httpServletRequest);
    }

    if(session.getAttribute("subpoolTree") == null)
    {
      session.setAttribute("subpoolTree", tree);
    }

    LOG.info("action mapping: " + actionMapping.getPath());

    if(actionMapping.getPath().indexOf("copyQuestion") > -1)
    {
      String sourceId = "";
      String destId = "";

      QuestionPoolForm form = (QuestionPoolForm) actionForm;
      String[] srcitems = (String[]) session.getAttribute("selectedItemIds");
      String[] destpools = form.getDestPools();

      for(int i = 0; i < srcitems.length; i++)
      {
        for(int j = 0; j < destpools.length; j++)
        {
          sourceId = srcitems[i];
          destId = destpools[j];
          if((sourceId != null) && (destId != null))
          {
            try
            {
              QuestionPoolDelegate delegate = new QuestionPoolDelegate();
              SharedManager sm = OsidManagerFactory.createSharedManager();

              //  InputStream inputStream =
              this.servlet.getServletContext().getResourceAsStream(
                "/xml/author/essayTemplate.xml");

              delegate.copyQuestion(sm.getId(sourceId), sm.getId(destId));
            }
            catch(Exception e)
            {
              LOG.error(e); throw new Error(e);
            }
          }
        }
      }

         // update the pool editor page
         QuestionPoolBean currentPool =
            (QuestionPoolBean) httpServletRequest.getSession().getAttribute("currentPool");
	 if(currentPool != null)
          {
            try
            {
              QuestionPoolDelegate delegate = new QuestionPoolDelegate();
              SharedManager sm = OsidManagerFactory.createSharedManager();
              QuestionPool thepool =
                delegate.getPool(
                  sm.getId(currentPool.getId()),
                  OsidManagerFactory.getAgent());
              QuestionPoolProperties prop =
                (QuestionPoolProperties) thepool.getData();
              currentPool.setProperties(prop);

              httpServletRequest.getSession().setAttribute("currentPool", currentPool);
            }
            catch(Exception e)
            {
              LOG.error(e); throw new Error(e);
            }
          }

  

      buildTree(httpServletRequest);
      session.setAttribute("subpoolTree", tree);

      forward = actionMapping.findForward("questionEditor");
    }

    else if(actionMapping.getPath().indexOf("exportQuestion") > -1)
    {
      String sourceId = "";

      QuestionPoolForm form = (QuestionPoolForm) actionForm;
      String[] srcitems = (String[]) session.getAttribute("selectedItemIds");

      for(int i = 0; i < srcitems.length; i++)
      {
        sourceId = srcitems[i];
        if(sourceId != null)
        {
          try
          {
            QuestionPoolDelegate delegate = new QuestionPoolDelegate();

            SharedManager sm = OsidManagerFactory.createSharedManager();

            httpServletResponse.setContentType("application/download");
            httpServletResponse.setHeader ("Content-Disposition", "attachment;filename=\"question.xml\"");

            ServletOutputStream out = httpServletResponse.getOutputStream();

            out.println(delegate.exportQuestion(sm.getId(sourceId)));
            return forward;
          }
          catch(Exception e)
          {
            LOG.error(e); throw new Error(e);
          }
        }



      }

      forward = actionMapping.findForward("questionEditor");
    }

    else if(actionMapping.getPath().indexOf("removeQuestion") > -1)
    {
      String sourceId = "";

      QuestionPoolForm form = (QuestionPoolForm) actionForm;
      String[] srcitems = (String[]) session.getAttribute("selectedItemIds");
      QuestionPoolBean currentPool =
        (QuestionPoolBean) session.getAttribute("currentPool");
      if(currentPool != null)
      {
        for(int i = 0; i < srcitems.length; i++)
        {
          sourceId = srcitems[i];
          if(sourceId != null)
          {
            try
            {
              QuestionPoolDelegate delegate = new QuestionPoolDelegate();
              SharedManager sm = OsidManagerFactory.createSharedManager();
              delegate.removeQuestionFromPool(
                sm.getId(sourceId), sm.getId(currentPool.getId()));

         // update the pool editor page
             QuestionPool thepool =
                delegate.getPool(
                  sm.getId(currentPool.getId()),
                  OsidManagerFactory.getAgent());
              QuestionPoolProperties prop =
                (QuestionPoolProperties) thepool.getData();
              currentPool.setProperties(prop);

              httpServletRequest.getSession().setAttribute("currentPool", currentPool);

            }
            catch(Exception e)
            {
              LOG.error(e); throw new Error(e);
            }
          }
        }
      }
       // currentPool!=null;
      buildTree(httpServletRequest);
      session.setAttribute("subpoolTree", tree);

      forward = actionMapping.findForward("questionEditor");
    }

    else if(actionMapping.getPath().indexOf("moveQuestion") > -1)
    {
      String sourceId = "";
      String destId = "";

      QuestionPoolForm form = (QuestionPoolForm) actionForm;
      String[] srcitems = (String[]) session.getAttribute("selectedItemIds");
      destId = form.getDestPool();
      QuestionPoolBean currentPool =
        (QuestionPoolBean) session.getAttribute("currentPool");
      if(currentPool != null)
      {
        for(int i = 0; i < srcitems.length; i++)
        {
          sourceId = srcitems[i];
          if((sourceId != null) && (destId != null))
          {
            try
            {
              QuestionPoolDelegate delegate = new QuestionPoolDelegate();
              SharedManager sm = OsidManagerFactory.createSharedManager();
              delegate.moveItemToPool(
                sm.getId(sourceId), sm.getId(currentPool.getId()),
                sm.getId(destId));
            }
            catch(Exception e)
            {
              LOG.error(e); throw new Error(e);
            }
          }
        }
      }
       // current!=null;
      buildTree(httpServletRequest);
      session.setAttribute("subpoolTree", tree);

         // update the pool editor page
         if(currentPool != null)
          {
            try
            {
              QuestionPoolDelegate delegate = new QuestionPoolDelegate();
              SharedManager sm = OsidManagerFactory.createSharedManager();
              QuestionPool thepool =
                delegate.getPool(
                  sm.getId(currentPool.getId()),
                  OsidManagerFactory.getAgent());
              QuestionPoolProperties prop =
                (QuestionPoolProperties) thepool.getData();
              currentPool.setProperties(prop);

              httpServletRequest.getSession().setAttribute("currentPool", currentPool);
            }
            catch(Exception e)
            {
              LOG.error(e); throw new Error(e);
            }
          }


      forward = actionMapping.findForward("questionEditor");
    }

    else if(actionMapping.getPath().indexOf("addToAssessment") > -1)
    {

    QuestionPoolForm form = (QuestionPoolForm) actionForm;
    AssessmentHelper assessmentHelper = new AssessmentHelper();
    SectionHelper sectionHelper = new SectionHelper();

    String assessmentID =
      (String) form.getAssessmentID();

    if(assessmentID!= null)
    {
      String[] selectedItemId =
        (String[]) session.getAttribute("selectedItemIds");
      String secRef = assessmentHelper.getLastSectionRef(assessmentID);
      if (session.getAttribute("sectionID4question") !=null) {
	secRef = (String) session.getAttribute("sectionID4question");
      } 
      for(int i = 0; i < selectedItemId.length; i++)
      {
        sectionHelper.addExistingItemToSection(secRef, selectedItemId[i]); 
      }

      session.removeAttribute("selectedItemIds");
      session.removeAttribute("selectedItems");
      session.removeAttribute("assessmentsMap");
      session.removeAttribute("sectionID4question");



      org.navigoproject.business.entity.Assessment newxmlString =
        assessmentHelper.getComposedAssessment(assessmentID, true);

      assessmentHelper.setAssessmentDocument(assessmentID, httpServletRequest);

      String source = (String) session.getAttribute("fromAuthoring");
      if ((source !=null) && (source.equals("1"))) {

      forward = actionMapping.findForward("showCompleteAssessment");

/*
	forward = new ActionForward();
 
      ActionForward tempf = actionMapping.findForward("showCompleteAssessment");
      forward.setPath(tempf.getPath() + "?assessmentID=" + assessmentID + "&action=Questions");
*/

      }
      else  {

      	forward = actionMapping.findForward("questionEditor");

      }
      session.removeAttribute("fromAuthoring");

    }

    }

    else if(actionMapping.getPath().indexOf("searchQuestion") > -1)
    {
      forward = actionMapping.findForward("searchQuestion");
    }
    else if(actionMapping.getPath().indexOf("startImportQuestion") > -1)
    {
      forward = actionMapping.findForward("importQuestion");
    }

    else if(actionMapping.getPath().indexOf("startAddQuestion") > -1)
    {
      forward = actionMapping.findForward("selectQuestionType");
    }

    else if(actionMapping.getPath().indexOf("importQuestion") > -1)
    {

      //	String itemType = form.getItemType();	
      QuestionPoolBean currentPool =
        (QuestionPoolBean) session.getAttribute("currentPool");
      if(currentPool != null)
      {
        try
        {
          // add a question to a pool
      QuestionPoolForm form = (QuestionPoolForm) actionForm;
      FormFile questionFile = form.getFilename();
      InputStream questionInputStream = questionFile.getInputStream();

          QuestionPoolDelegate delegate = new QuestionPoolDelegate();
          SharedManager sm = OsidManagerFactory.createSharedManager();
          Item newItem = delegate.importQuestion(questionInputStream);

          delegate.addItemToPool(
            newItem.getId(), sm.getId(currentPool.getId()));
          QuestionPool thepool =
            delegate.getPool(
              sm.getId(currentPool.getId()),
              OsidManagerFactory.getAgent());  //  this is for old osidmanagerFactory
          QuestionPoolProperties prop =
            (QuestionPoolProperties) thepool.getData();
          currentPool.setProperties(prop);

          session.setAttribute("currentPool", currentPool);
        }
        catch(Exception e)
        {
          LOG.error(e); throw new Error(e);
        }
      }

      forward = actionMapping.findForward("createPool");
    }
    else if(actionMapping.getPath().indexOf("addQuestion") > -1)
    {
      ItemActionForm form = (ItemActionForm) actionForm;

      //	String itemType = form.getItemType();	
      QuestionPoolBean currentPool =
        (QuestionPoolBean) session.getAttribute("currentPool");
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
            httpServletRequest.getParameter(
              "stxx/item/presentation/flow/material/mattext");
          org.navigoproject.business.entity.Item itemXml =
            (org.navigoproject.business.entity.Item) session.getAttribute(
              "xmlString");
          Item newItem =
            delegate.createQuestion(
              displayName, description, itemXml, httpServletRequest);

          delegate.addItemToPool(
            newItem.getId(), sm.getId(currentPool.getId()));
          QuestionPool thepool =
            delegate.getPool(
              sm.getId(currentPool.getId()),
              OsidManagerFactory.getAgent());  //  this is for old osidmanagerFactory
          QuestionPoolProperties prop =
            (QuestionPoolProperties) thepool.getData();
          currentPool.setProperties(prop);

          session.setAttribute("currentPool", currentPool);
        }
        catch(Exception e)
        {
          LOG.error(e); throw new Error(e);
        }
      }

      buildTree(httpServletRequest);
      session.setAttribute("subpoolTree", tree);

      forward = actionMapping.findForward("questionPools");
    }

    return forward;
  }
}
