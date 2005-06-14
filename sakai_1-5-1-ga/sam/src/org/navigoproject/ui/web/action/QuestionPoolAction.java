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
import org.navigoproject.ui.web.form.edit.QuestionForm;
import org.navigoproject.ui.web.form.questionpool.QuestionPoolBean;
import org.navigoproject.ui.web.form.questionpool.QuestionPoolForm;
import org.navigoproject.ui.web.asi.author.item.ItemHelper;
import org.navigoproject.ui.web.FunctionalityDisabler;

import org.navigoproject.osid.questionpool.QuestionPoolFactory;
import java.io.*;

import java.io.InputStream;

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
 * Handles the Question Pool Actions.
 *
 * @author Rachel Gollub <rgollub@stanford.edu>
 * @author Ed Smiley <esmiley@stanford.edu>
 * @author Lydia Li<lydial@stanford.edu>
 * $Id: QuestionPoolAction.java,v 1.8 2004/10/15 21:50:26 lydial.stanford.edu Exp $
 */
public class QuestionPoolAction
  extends AAMAction
{
  private AAMTree tree;
  static Logger LOG = Logger.getLogger(QuestionPoolAction.class.getName());

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

      /*** debug code ***/
      Collection objects = tree.getSortedObjects();
      Iterator iter = objects.iterator();
      while(iter.hasNext())
      {
        try
        {
          QuestionPool pool = (QuestionPool) iter.next();
          tree.setCurrentId(pool.getId());
          LOG.debug("Current pool: " + pool.getId());
          LOG.debug("Properties: " + tree.getCurrentObjectProperties());
        }
        catch(Exception e)
        {
          LOG.error(e); throw new Error(e);
        }
      }

      /*** end debug code ***/
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

    if(actionMapping.getPath().indexOf("startCopyPool") > -1)
    {
      forward = actionMapping.findForward("copyPool");
    }

    else if(actionMapping.getPath().indexOf("copyPool") > -1)
    {
      String sourceId = "";
      String destId = "";

      QuestionPoolForm form = (QuestionPoolForm) actionForm;
      String[] srcpools = (String[]) session.getAttribute("selectedpoolIds");
      String[] destpools = form.getDestPools();
      boolean rootpoolSelected = form.getRootPoolSelected();
      if(rootpoolSelected)
      {
        for(int i = 0; i < srcpools.length; i++)
        {
          sourceId = srcpools[i];
          if(sourceId != null)
          {
            try
            {
              QuestionPoolDelegate delegate = new QuestionPoolDelegate();
              SharedManager sm = OsidManagerFactory.createSharedManager();
              InputStream inputStream =
                this.servlet.getServletContext().getResourceAsStream(
                  "/xml/author/essayTemplate.xml");
              delegate.copyPool(
                tree, OsidManagerFactory.getAgent(),
                sm.getId(sourceId), sm.getId("0"));
            }
            catch(Exception e)
            {
              LOG.error(e); throw new Error(e);
            }
          }
        }
      }

      for(int i = 0; i < srcpools.length; i++)
      {
        for(int j = 0; j < destpools.length; j++)
        {
          sourceId = srcpools[i];
          destId = destpools[j];
          if((sourceId != null) && (destId != null))
          {
            try
            {
              QuestionPoolDelegate delegate = new QuestionPoolDelegate();
              SharedManager sm = OsidManagerFactory.createSharedManager();
              InputStream inputStream =
                this.servlet.getServletContext().getResourceAsStream(
                  "/xml/author/essayTemplate.xml");
              delegate.copyPool(
                tree, OsidManagerFactory.getAgent(),
                sm.getId(sourceId), sm.getId(destId));
            }
            catch(Exception e)
            {
              LOG.error(e); throw new Error(e);
            }
          }
        }
      }

      buildTree(httpServletRequest);
      session.setAttribute("subpoolTree", tree);
      forward = actionMapping.findForward("questionPools");
    }

    else if(actionMapping.getPath().indexOf("exportPool") > -1)
    {
      String sourceId = "";

      QuestionPoolForm form = (QuestionPoolForm) actionForm;
      String[] srcpools = (String[]) session.getAttribute("selectedpoolIds");

      for(int i = 0; i < srcpools.length; i++)
      {
        sourceId = srcpools[i];
        if(sourceId != null)
        {
          try
          {
            QuestionPoolDelegate delegate = new QuestionPoolDelegate();

            SharedManager sm = OsidManagerFactory.createSharedManager();

            httpServletResponse.setContentType("application/download");
            httpServletResponse.setHeader ("Content-Disposition", "attachment;filename=\"questionpool.xml\"");

            ServletOutputStream out = httpServletResponse.getOutputStream();

            out.println(delegate.exportQuestionPool(delegate.getPool(sm.getId(sourceId),OsidManagerFactory.getAgent())));
            return forward;
          }
          catch(Exception e)
          {
            LOG.error(e); throw new Error(e);
          }
        }
      }

      forward = actionMapping.findForward("questionPools");
    }

    else if(actionMapping.getPath().indexOf("removePool") > -1)
    {
      String sourceId = "";

      QuestionPoolForm form = (QuestionPoolForm) actionForm;
      String[] srcpools = (String[]) session.getAttribute("selectedpoolIds");

      for(int i = 0; i < srcpools.length; i++)
      {
        sourceId = srcpools[i];
        if(sourceId != null)
        {
          try
          {
            QuestionPoolDelegate delegate = new QuestionPoolDelegate();
            SharedManager sm = OsidManagerFactory.createSharedManager();
            delegate.deletePool(
              sm.getId(sourceId),
              OsidManagerFactory.getAgent(), tree);
          }
          catch(Exception e)
          {
            LOG.error(e); throw new Error(e);
          }
        }
      }

      buildTree(httpServletRequest);
      session.setAttribute("subpoolTree", tree);
      forward = actionMapping.findForward("questionPools");
    }

    else if(actionMapping.getPath().indexOf("movePool") > -1)
    {
      String sourceId = "";
      String destId = "";

      QuestionPoolForm form = (QuestionPoolForm) actionForm;
      String[] srcpools = (String[]) session.getAttribute("selectedpoolIds");
      destId = form.getDestPool();

      for(int i = 0; i < srcpools.length; i++)
      {
        sourceId = srcpools[i];
        if((sourceId != null) && (destId != null))
        {
          try
          {
            QuestionPoolDelegate delegate = new QuestionPoolDelegate();
            SharedManager sm = OsidManagerFactory.createSharedManager();
            delegate.movePool(
              OsidManagerFactory.getAgent(),
              sm.getId(sourceId), sm.getId(destId));
          }
          catch(Exception e)
          {
            LOG.error(e); throw new Error(e);
          }
        }
      }

      buildTree(httpServletRequest);
      session.setAttribute("subpoolTree", tree);
      forward = actionMapping.findForward("questionPools");
    }

    else if(actionMapping.getPath().indexOf("getAllPools") > -1)
    {
      doGetAllPools(session, httpServletRequest);
      forward = actionMapping.findForward("questionPools");
    }

    else if(actionMapping.getPath().indexOf("searchQuestion") > -1)
    {
      forward = actionMapping.findForward("searchQuestion");
    }
    else if(actionMapping.getPath().indexOf("startImportPool") > -1)
    {
      forward = actionMapping.findForward("importPool");
    }
    else if(actionMapping.getPath().indexOf("importPool") > -1)
    {
    try{
      // read the xml and create a pool with the appropriate parent, and save it.
      QuestionPoolForm form = (QuestionPoolForm) actionForm;
      FormFile poolFile = form.getFilename();
      InputStream poolInputStream = poolFile.getInputStream(); 
      QuestionPoolDelegate delegate = new QuestionPoolDelegate();
        QuestionPool qpool = delegate.importQuestionPool(poolInputStream);
        delegate.savePool(qpool);
        buildTree(httpServletRequest);
        session.setAttribute("subpoolTree", tree);

    }
    catch(Exception e)
      {
        LOG.error(e); throw new Error(e);
      }

      forward = actionMapping.findForward("questionPools");
    }
    else if(actionMapping.getPath().indexOf("userAdmin") > -1)
    {
      forward = actionMapping.findForward("userAdmin");
    }

    else if(actionMapping.getPath().indexOf("startCreatePool") > -1)
    {
      doStartCreatePool(session, httpServletRequest);
      forward = actionMapping.findForward("createPool");
    }

    else if(actionMapping.getPath().indexOf("editpool") > -1)
    // for saving new pool or existing pool
    {
      doCreatePool(httpServletRequest, session);
      forward = actionMapping.findForward("questionChooser");
    }

    else if(actionMapping.getPath().indexOf("startAddQuestion") > -1)
    {
      forward = actionMapping.findForward("selectQuestionType");
    }

    else if(actionMapping.getPath().indexOf("addQuestion") > -1)
    {
      try
      {
        // add a question to a pool
        //        QuestionPoolDelegate delegate = new QuestionPoolDelegate();
        //       SharedManager sm = OsidManagerFactory.createSharedManager();
        //		InputStream itemTemplateXml = this.servlet.getServletContext().getResourceAsStream("/xml/author/essayTemplate.xml");
        //	Item newItem=  delegate.createQuestion( displayName, description, itemTemplateXml,questionText)
      }
      catch(Exception e)
      {
        LOG.error(e); throw new Error(e);
      }

      forward = actionMapping.findForward("questionPools");
    }

    else if(actionMapping.getPath().indexOf("removeQuestion") > -1)
    {
      try
      {
        // add a question to a pool
        //        QuestionPoolDelegate delegate = new QuestionPoolDelegate();
        //       SharedManager sm = OsidManagerFactory.createSharedManager(); yTemplate.xml");
        //      delegate.removeQuestion( Id questionId, Id poolId);
      }
      catch(Exception e)
      {
        LOG.error(e); throw new Error(e);
      }

      forward = actionMapping.findForward("questionPools");
    }

    else if(actionMapping.getPath().indexOf("questionChooser") > -1)
    {
/*
      if(httpServletRequest.getParameter("adder") != null)
      {
        // Save the current question data before continuing.
        AssessmentAction.doAssessmentQuestionForm(
          session, ((QuestionForm) session.getAttribute("question")), true);
        try
        {
          AssessmentServiceDelegate delegate = new AssessmentServiceDelegate();
          delegate.updateAssessment(
            (Assessment) session.getAttribute("assessmentTemplate"));
        }
        catch(Exception e)
        {
          LOG.error(e); throw new Error(e);
        }
      }

*/
      doQuestionChooser(httpServletRequest, session);
      forward = actionMapping.findForward("questionPools");
    }

    else if(actionMapping.getPath().indexOf("poolquestions") > -1)
    {
      doPoolQuestions(httpServletRequest, session);
      if(
        session.getAttribute("pooluse").equals("chooser") ||
          session.getAttribute("pooluse").equals("random"))
      {
        forward = actionMapping.findForward("templateEditor");
      }

      if(session.getAttribute("pooluse").equals("adder"))
      {
        forward = actionMapping.findForward("questionEditor");
      }
    }

    return forward;
  }

  /**
   * Get a list of all existing Question Pools.
   *
   * @param session current HttpSession
   */
  public void doGetAllPools(HttpSession session, HttpServletRequest request)
  {
    try
    {
      QuestionPoolDelegate delegate = new QuestionPoolDelegate();
      QuestionPoolIterator qpi =
        delegate.getAllPools(OsidManagerFactory.getAgent());

      QuestionPoolForm form =
        (QuestionPoolForm) session.getAttribute("questionpool");
      Collection pools = new ArrayList();

      if(form == null)
      {
        form = new QuestionPoolForm();
      }

      while(qpi.hasNext())
      {
        QuestionPoolBean bean = new QuestionPoolBean();
        QuestionPool pool = (QuestionPool) qpi.next();
        bean.setName(pool.getDisplayName());
        bean.setDescription(pool.getDescription());
        bean.setParentPoolId(pool.getParentId().toString());
        pools.add(bean);
      }

      form.setPools(pools);
      session.setAttribute("questionpool", form);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param session DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   */
  public void doStartCreatePool(
    HttpSession session, HttpServletRequest request)
  {
    try
    {
      // Forward back to question chooser
      session.setAttribute("createForward", "questionChooser");
      session.setAttribute(
        "createPoolCancel", "/Navigo/questionpool/questionList.jsp");

      QuestionPoolForm form =
        (QuestionPoolForm) session.getAttribute("questionpool");
      if(form == null)
      {
        form = new QuestionPoolForm();
      }

      QuestionPoolBean pool = new QuestionPoolBean();
      int htmlIdLevel = 0;
      ArrayList parentPools = new ArrayList();

      if(request.getParameter("use") != null)
      {
        String use = request.getParameter("use");

        session.setAttribute("createpooluse", use);
        if(use.startsWith("edit"))
        {
          pool.setId(request.getParameter("id"));

          // Get all data from the database
          QuestionPoolDelegate delegate = new QuestionPoolDelegate();
          SharedManager sm = OsidManagerFactory.createSharedManager();
          QuestionPool thepool =
            delegate.getPool(
              sm.getId(request.getParameter("id")),
              OsidManagerFactory.getAgent());
          tree.setCurrentId(thepool.getId());

          Id ppoolid = thepool.getParentId();

          // reset session attribute parentpools
          session.removeAttribute("parentpools");
          session.setAttribute("parentpools", parentPools);
          while(! ppoolid.toString().equals("0"))
          {
            QuestionPool ppool =
              delegate.getPool(ppoolid, OsidManagerFactory.getAgent());
            if(ppool != null)
            {
              parentPools.add(0, ppool);
              ppoolid = ppool.getParentId();
            }

            if(parentPools != null)
            {
              session.setAttribute("parentpools", parentPools);
            }
          }

          String htmlID = tree.getCurrentObjectHTMLId();

          // pass the htmlIdLevel to the collapseRowByLevel javascript 
          String[] result = htmlID.split("-");
          htmlIdLevel = result.length + 1;

          pool.setName(thepool.getDisplayName());
          pool.setParentPoolId(thepool.getParentId().toString());
          pool.setDescription(thepool.getDescription());
          pool.setProperties((QuestionPoolProperties) thepool.getData());
          pool.setNumberOfSubpools(
            new Integer(tree.getChildList(thepool.getId()).size()).toString());

          Collection objects = tree.getSortedObjects(thepool.getId());
          session.setAttribute("sortedSubPool", objects);

        }
        else
        {
          QuestionPoolProperties props = new QuestionPoolProperties();
          props.setOwner(OsidManagerFactory.getAgent());
          pool.setProperties(props);
          if(request.getParameter("pid") != null)
          {
            pool.setParentPoolId(request.getParameter("pid"));
          }
        }
      }

      QuestionPoolProperties props = pool.getProperties();
      props.setLastModifiedBy(OsidManagerFactory.getAgent());
      pool.setProperties(props);
      form.setCurrentPool(pool);
      form.setHtmlIdLevel(htmlIdLevel);
      session.setAttribute("questionpool", form);
      session.setAttribute("currentPool", pool);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   * @param session DOCUMENTATION PENDING
   */
  public void doCreatePool(HttpServletRequest request, HttpSession session)
  {
    try
    {
      QuestionPoolForm form =
        (QuestionPoolForm) session.getAttribute("questionpool");
      if(form == null)
      {
        form = new QuestionPoolForm();
      }

      QuestionPoolBean bean = form.getCurrentPool();
      String beanid = "0";
      if(bean.getId() != null)
      {
        beanid = bean.getId();
      }

      String parentid = "0";
      if(bean.getParentPoolId() != null)
      {
        parentid = bean.getParentPoolId();
      }

      SharedManager sm = OsidManagerFactory.createSharedManager();
      QuestionPool questionpool =
        new QuestionPoolImpl(sm.getId(beanid), sm.getId(parentid));
      questionpool.updateDisplayName(bean.getName());
      questionpool.updateDescription(bean.getDescription());
      questionpool.updateData(bean.getProperties());
      QuestionPoolDelegate delegate = new QuestionPoolDelegate();
      LOG.debug("Saving pool");
      delegate.savePool(questionpool);

      // Rebuild the tree with the new pool
      buildTree(request);
      session.setAttribute("subpoolTree", tree);
      form.setCurrentPool(null);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   * @param session DOCUMENTATION PENDING
   */
  public void doQuestionChooser(
    HttpServletRequest request, HttpSession session)
  {
    try
    {
/*
      if(request.getParameter("use") != null)
      {
        String use = request.getParameter("use");
        session.setAttribute("pooluse", use);
      }

      session.setAttribute("poolrole", "manager");
      if(request.getParameter("sectionid") != null)
      {
        session.setAttribute("pool.section", request.getParameter("sectionid"));
      }

      if(request.getParameter("randomNumber") != null)
      {
        session.setAttribute(
          "pool.randomnumber", request.getParameter("randomNumber"));
      }

      if(request.getParameter("qid") != null)
      {
        session.setAttribute("pool.question", request.getParameter("qid"));
      }

*/
      QuestionPoolForm form =
        (QuestionPoolForm) session.getAttribute("questionpool");
      if(form == null)
      {
        form = new QuestionPoolForm();
      }
/*
      if(request.getParameter("pid") != null)
      {
        QuestionPoolDelegate delegate = new QuestionPoolDelegate();
        SharedManager sm = OsidManagerFactory.createSharedManager();
        QuestionPool pool =
          delegate.getPool(
            sm.getId(request.getParameter("pid")),
            OsidManagerFactory.getAgent(request));

        QuestionPoolBean bean = new QuestionPoolBean();
        bean.setName(pool.getDisplayName());
        bean.setDescription(pool.getDescription());
        bean.setId(pool.getId().toString());
        bean.setParentPoolId(pool.getParentId().toString());
        bean.setNumberOfSubpools(
          new Integer(tree.getChildList(pool.getId()).size()).toString());
        bean.setProperties((QuestionPoolProperties) pool.getData());
        form.setCurrentPool(bean);
      }
      else
      {
        form.setCurrentPool(null);
      }

*/
      buildTree(request);
      session.setAttribute("subpoolTree", tree);
      form.setCurrentPool(null);

       // get Assessment originated from Authoring,  if any,
    String assessmentID =
      (String) session.getAttribute("assessmentID4question");
    session.setAttribute("assessmentID4questionToAddPool", assessmentID);
    // need to remove the session variable in case a user doesn't follow through on import from pool
    session.removeAttribute("assessmentID4question");

    // determine if we need to display the hint message box on top of pool manager
    if(assessmentID != null)
    {
        form.setFromAuthoring("1");
        form.setAssessmentID(assessmentID);
    }
    else {

        form.setFromAuthoring("0");
    }




      session.setAttribute("questionpool", form);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   * @param session DOCUMENTATION PENDING
   */
  public void doPoolQuestions(HttpServletRequest request, HttpSession session)
  {
    try
    {
      Collection ids = new ArrayList();
      QuestionPoolForm form =
        (QuestionPoolForm) session.getAttribute("questionpool");
      if(form == null)
      {
        return; // This should never happen.
      }

      QuestionPoolBean currentPool = form.getCurrentPool();
      if(currentPool == null)
      {
        return; // This might happen if no pool is chosen.
      }

      if(session.getAttribute("pooluse").equals("adder"))
      {
        QuestionPoolDelegate delegate = new QuestionPoolDelegate();
        SharedManager sm = OsidManagerFactory.createSharedManager();
        delegate.addItemToPool(
          sm.getId((String) session.getAttribute("pool.question")),
          sm.getId(currentPool.getId()));
        session.removeAttribute("pool.question");

        return;
      }

      if(session.getAttribute("pooluse").equals("chooser"))
      {
        // Get Ids for the selected items
        Enumeration en = request.getParameterNames();
        while(en.hasMoreElements())
        {
          String name = (String) en.nextElement();
          if(name.startsWith("question"))
          {
            String id = name.substring(8); // remove "question" at the beginning
            SharedManager sm = OsidManagerFactory.createSharedManager();
            Id idimpl = sm.getId(id);
            ids.add(idimpl);
          }
        }
      }

      Assessment assessment =
        (Assessment) session.getAttribute("assessmentTemplate");
      SectionIterator sections = assessment.getSections();
      while(sections.hasNext())
      {
        Section section = (Section) sections.next();
        if(
          section.getId().toString().equals(
              (String) session.getAttribute("pool.section")))
        {
          QuestionPoolProperties properties = currentPool.getProperties();

          // If we're randomly choosing question, just record the
          // pool name and id.
          if(session.getAttribute("pooluse").equals("random"))
          {
            SectionPropertiesImpl sprops =
              (SectionPropertiesImpl) section.getData();
            SharedManager sm = OsidManagerFactory.createSharedManager();
            sprops.setRandomPoolName(currentPool.getName());
            sprops.setRandomPoolId(sm.getId(currentPool.getId()));
            if(session.getAttribute("pool.randomnumber") != null)
            {
              sprops.setRandomNumber(
                Integer.parseInt(
                  (String) session.getAttribute("pool.randomnumber")));
              session.removeAttribute("pool.randomnumber");
            }
          }

          // Otherwise, record the questions
          else if(session.getAttribute("pooluse").equals("chooser"))
          {
            Collection questions = properties.getQuestions();
            Iterator qiter = questions.iterator();
            while(qiter.hasNext())
            {
              ItemImpl item = (ItemImpl) qiter.next();
              Iterator iter = ids.iterator();
              while(iter.hasNext())
              {
                Id thisid = (Id) iter.next();
                if(thisid.toString().equals(item.getId().toString()))
                {
                  section.addItem(item);

                  break;
                }
              }
            }
          }

          break;
        }
      }

      if(session.getAttribute("pooluse").equals("chooser"))
      {
        QuestionPoolDelegate delegate = new QuestionPoolDelegate();
        SharedManager sm = OsidManagerFactory.createSharedManager();
        delegate.addItemsToSection(
          ids, sm.getId((String) session.getAttribute("pool.section")));
      }
      else if(session.getAttribute("pooluse").equals("random"))
      {
        AssessmentServiceDelegate delegate = new AssessmentServiceDelegate();
        delegate.updateAssessment(assessment);
      }

      // Update the form. 
      AssessmentPropertiesImpl props =
        (AssessmentPropertiesImpl) assessment.getData();
      AssessmentTemplateImpl template =
        (AssessmentTemplateImpl) props.getAssessmentTemplate();
      AssessmentTemplatePropertiesImpl tprops =
        (AssessmentTemplatePropertiesImpl) template.getData();
      IndexAction.doPartForm(
        session, assessment, template, props, tprops, false);

      session.removeAttribute("pool.section");
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }

  /*
     public Agent getAgent(HttpSession session)
     {
       try {
         String agentName = (String) session.getAttribute("agent_id");
         if (agentName == null)
           agentName = "guest"; // For the first release at least
         Agent agent = new AgentImpl(agentName,
           new TypeImpl("Stanford", "AAM", "agent", "sunetid"));
         return agent;
       } catch (Exception e) {
         LOG.error(e); throw new Error(e);
         return null;
       }
     }
   */
/*
  public void movePool(HttpServletRequest request, Id source, Id dest)
  {
    try
    {
      // Get the Pools
      QuestionPoolDelegate delegate = new QuestionPoolDelegate();
      QuestionPool qPoolS =
        delegate.getPool(source, OsidManagerFactory.getAgent(request));
      QuestionPool qPoolD =
        delegate.getPool(dest, OsidManagerFactory.getAgent(request));
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }

   */


  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   * @param questionId DOCUMENTATION PENDING
   * @param destId DOCUMENTATION PENDING
   */

/*
  public void copyQuestion(
    HttpServletRequest request, Id questionId, Id destId)
  {
    try
    {
      QuestionPoolDelegate delegate = new QuestionPoolDelegate();
      delegate.addItemToPool(questionId, destId);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }
*/


  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   * @param source DOCUMENTATION PENDING
   * @param dest DOCUMENTATION PENDING
   */

/*
  public void copyPool(HttpServletRequest request, Id source, Id dest)
  {
    try
    {
      // Get the Pools
      QuestionPoolDelegate delegate = new QuestionPoolDelegate();
      SharedManager sm = OsidManagerFactory.createSharedManager();

      QuestionPool qPoolS =
        delegate.getPool(source, OsidManagerFactory.getAgent(request));
      QuestionPool qPoolD =
        delegate.getPool(dest, OsidManagerFactory.getAgent(request));

      // Determine if the Pools are in the same tree
      // If so, make sure the source level is not higher(up the tree) than the dest.
      // to avoid the endless loop.
      boolean haveCommonRoot = tree.haveCommonRoot(source, dest);
      haveCommonRoot = true; // TODO fix the code when this is not true.

      if(haveCommonRoot)
      {
        // Copy to a Pool outside the root
        // Copy *this* Pool first 
        QuestionPool questionpool = new QuestionPoolImpl(sm.getId("0"), dest);
        questionpool.updateDisplayName(qPoolS.getDisplayName());
        questionpool.updateDescription(qPoolS.getDescription());
        questionpool.updateData(qPoolS.getData());
        questionpool = delegate.savePool(questionpool);

        // Get the Questions of qPoolS
        Collection questions =
          ((QuestionPoolProperties) qPoolS.getData()).getQuestions();
        Iterator iter = questions.iterator();

        // For each question ,
        while(iter.hasNext())
        {
          Item item = (Item) iter.next();

          // add that question to questionpool 
          delegate.addItemToPool(item.getId(), questionpool.getId());
        }

        // Get the SubPools of qPoolS
        Iterator citer = (tree.getChildList(source)).iterator();
        while(citer.hasNext())
        {
          QuestionPool poolC =
            delegate.getPool(
              (Id) citer.next(), OsidManagerFactory.getAgent(request));
          questionpool = new QuestionPoolImpl(sm.getId("0"), dest);
          questionpool.updateDisplayName(poolC.getDisplayName());
          questionpool.updateDescription(poolC.getDescription());
          questionpool.updateData(poolC.getData());
          questionpool = delegate.savePool(questionpool);
          buildTree(request);

          copyPool(request, poolC.getId(), questionpool.getId());
        }
      }
      else
      { // If Pools in different trees,
        // Copy to a Pool within the same root
        // Copy *this* Pool first
        QuestionPool questionpool = new QuestionPoolImpl(sm.getId("0"), dest);
        questionpool.updateDisplayName(qPoolS.getDisplayName());
        questionpool.updateDescription(qPoolS.getDescription());
        questionpool.updateData(qPoolS.getData());
        questionpool = delegate.savePool(questionpool);

        // Get the Questions of qPoolS
        Collection questions =
          ((QuestionPoolProperties) qPoolS.getData()).getQuestions();
        Iterator iter = questions.iterator();

        // For each question ,
        while(iter.hasNext())
        {
          Item item = (Item) iter.next();
          AssessmentManager assessmentManager = new AssessmentManagerImpl();
          Item newItem =
            assessmentManager.createItem(
              item.getDisplayName(), item.getDescription(), item.getItemType());

          // add that question to questionpool
          delegate.addItemToPool(newItem.getId(), questionpool.getId());
        }

        // Get the SubPools of qPoolS
        Iterator citer = (tree.getChildList(source)).iterator();
        while(citer.hasNext())
        {
          QuestionPool poolC =
            delegate.getPool(
              (Id) citer.next(), OsidManagerFactory.getAgent(request));
          questionpool = new QuestionPoolImpl(sm.getId("0"), dest);
          questionpool.updateDisplayName(poolC.getDisplayName());
          questionpool.updateDescription(poolC.getDescription());
          questionpool.updateData(poolC.getData());
          questionpool = delegate.savePool(questionpool);
          buildTree(request);

          copyPool(request, poolC.getId(), questionpool.getId());
        }
      }
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }
*/


}
