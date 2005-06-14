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

import java.util.*;

import javax.servlet.http.*;

import org.apache.commons.beanutils.BeanUtils;

import org.apache.log4j.Logger;

import org.apache.struts.action.*;
import org.apache.struts.actions.LookupDispatchAction;

import osid.assessment.Assessment;
import osid.assessment.AssessmentManager;
import osid.assessment.Item;
import osid.assessment.Section;
import osid.assessment.SectionIterator;

import osid.shared.Agent;
import osid.shared.Id;
import osid.shared.SharedManager;

/**
 * Handles the Question Pool Dispatch Actions.
 * for forms with multiple submit buttons
 *
 * @author Lydia Li <lydial@stanford.edu>
 * $Id: QuestionPoolDispatchAction.java,v 1.7 2004/10/15 21:50:26 lydial.stanford.edu Exp $
 */
public class QuestionPoolDispatchAction
  extends LookupDispatchAction
{
  private AAMTree tree;
  private static final Logger LOG =
    Logger.getLogger(QuestionPoolDispatchAction.class.getName());

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  protected Map getKeyMethodMap()
  {
    Map map = new HashMap();
    map.put("button.copy", "copy");
    map.put("button.move", "move");
    map.put("button.remove", "remove");
    map.put("button.export", "export");

    return map;
  }

  /**
   * get selected pools based on multibox values, and save to session.
   *
   * @return DOCUMENTATION PENDING
   */
  public void setSelectedPools(
    ActionForm actionForm, HttpServletRequest request)
  {
    HttpSession session = request.getSession(true);

    QuestionPoolForm form = (QuestionPoolForm) actionForm;

    String[] spools = form.getSelectedPools();
    Collection qpools = new ArrayList();
    try
    {
      QuestionPoolDelegate delegate = new QuestionPoolDelegate();
      SharedManager sm = OsidManagerFactory.createSharedManager();

      for(int x = 0; x < spools.length; x++)
      {
        QuestionPool qPool =
          delegate.getPool(
            (sm.getId(spools[x])), OsidManagerFactory.getAgent());
        qpools.add(qPool);
      }

      Iterator qiter = qpools.iterator();

      while(qiter.hasNext())
      {
        QuestionPool item = (QuestionPool) qiter.next();
      }
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }

    session.setAttribute("selectedpools", qpools);
    session.setAttribute("selectedpoolIds", spools);
  }

  /**
   * handles copy pool(s) action
   */
  public ActionForward copy(
    ActionMapping actionMapping, ActionForm actionForm,
    HttpServletRequest request, HttpServletResponse response)
  {
    ActionForward forward = null;
    setSelectedPools(actionForm, request);
    forward = actionMapping.findForward("copyPool");

    return forward;
  }

  /**
   * handles move pool(s)  action
   */
  public ActionForward move(
    ActionMapping actionMapping, ActionForm actionForm,
    HttpServletRequest request, HttpServletResponse response)
  {
    ActionForward forward = null;
    setSelectedPools(actionForm, request);
    forward = actionMapping.findForward("movePool");

    return forward;
  }

  /**
   * handles Remove Pool(s) action
   */
  public ActionForward remove(
    ActionMapping actionMapping, ActionForm actionForm,
    HttpServletRequest request, HttpServletResponse response)
  {
    ActionForward forward = null;
    setSelectedPools(actionForm, request);
    forward = actionMapping.findForward("removePool");

    return forward;
  }

  /**
   * handles Export Pool(s) action
   */
  public ActionForward export(
    ActionMapping actionMapping, ActionForm actionForm,
    HttpServletRequest request, HttpServletResponse response)
  {
    ActionForward forward = null;
    setSelectedPools(actionForm, request);
    forward = actionMapping.findForward("exportPool");

    return forward;
  }
}
