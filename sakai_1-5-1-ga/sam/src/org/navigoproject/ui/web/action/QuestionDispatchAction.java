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
import org.navigoproject.business.entity.Item;
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
import org.navigoproject.ui.web.asi.author.assessment.AssessmentHelper;
import org.navigoproject.ui.web.asi.author.item.ItemHelper;
import org.navigoproject.ui.web.asi.author.section.SectionHelper;
import org.navigoproject.ui.web.form.edit.QuestionForm;
import org.navigoproject.ui.web.form.questionpool.QuestionPoolBean;
import org.navigoproject.ui.web.form.questionpool.QuestionPoolForm;
import org.navigoproject.ui.web.asi.delivery.XmlDeliveryService;

import org.w3c.dom.Document;
import java.util.*;

import javax.servlet.http.*;

import org.apache.commons.beanutils.BeanUtils;

import org.apache.log4j.Logger;

import org.apache.struts.action.*;
import org.apache.struts.actions.LookupDispatchAction;

import osid.dr.DigitalRepositoryException;

import osid.assessment.Assessment;
import osid.assessment.AssessmentManager;

//import osid.assessment.Item;
import osid.assessment.Section;
import osid.assessment.SectionIterator;

import osid.dr.Asset;
import osid.shared.Agent;
import osid.shared.Id;
import osid.shared.SharedManager;
import osid.shared.SharedException;

/**
 * Handles the Question Dispatch Actions.
 * for forms with multiple submit buttons
 *
 * @author Lydia Li <lydial@stanford.edu>
 * $Id: QuestionDispatchAction.java,v 1.7 2004/10/15 21:50:26 lydial.stanford.edu Exp $
 */
public class QuestionDispatchAction
  extends LookupDispatchAction
{
  private AAMTree tree;
  static Logger LOG =
    Logger.getLogger(QuestionDispatchAction.class.getName());

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  protected Map getKeyMethodMap()
  {
    Map map = new HashMap();
    map.put("button.addToAssessment", "addToAssessment");
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
  public void setSelectedQuestions(
    ActionForm actionForm, HttpServletRequest request)
  {
    HttpSession session = request.getSession(true);
    QuestionPoolForm form = (QuestionPoolForm) actionForm;

    String[] spools = form.getSelectedQuestions();
    Collection qpools = new ArrayList();

    try
    {
      ItemHelper helper = new ItemHelper();
      for(int x = 0; x < spools.length; x++)
      {
        Item qitem = helper.getItemXml(spools[x]);
        qpools.add(qitem);
      }
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }

    session.setAttribute("selectedItems", qpools);
    session.setAttribute("selectedItemIds", spools);
  }

  /**
   * handles copy Question(s) action
   */
  public ActionForward copy(
    ActionMapping actionMapping, ActionForm actionForm,
    HttpServletRequest request, HttpServletResponse response)
  {
    ActionForward forward = null;

    setSelectedQuestions(actionForm, request);
    forward = actionMapping.findForward("copyQuestion");

    return forward;
  }

  /**
   * handles move Question (s)  action
   */
  public ActionForward move(
    ActionMapping actionMapping, ActionForm actionForm,
    HttpServletRequest request, HttpServletResponse response)
  {
    ActionForward forward = null;

    setSelectedQuestions(actionForm, request);
    forward = actionMapping.findForward("moveQuestion");

    return forward;
  }

  /**
   * handles Remove Question(s) action
   */
  public ActionForward addToAssessment(
    ActionMapping actionMapping, ActionForm actionForm,
    HttpServletRequest request, HttpServletResponse response)
  {

    ActionForward forward = null;
    HttpSession session = request.getSession(true);
    setSelectedQuestions(actionForm, request);
    QuestionPoolForm form = (QuestionPoolForm) actionForm;

    
    Map map = new HashMap();
    Agent user =null;

    try
    {
      user = OsidManagerFactory.getAgent();
      //  user = OsidManagerFactory.getAgent(request);   //  this is for the older version of OsidManagerFactory
      map = getAssessments(request, user.getId().getIdString());
    }
    catch(Exception e1)
    {
        LOG.error(e1.getMessage(), e1);
    }

	// get Assessment originated from Authoring,  if any, 
    String assessmentID =
      (String) session.getAttribute("assessmentID4questionToAddPool");
    if(assessmentID != null)
    {

	form.setAssessmentID(assessmentID);
        session.setAttribute("fromAuthoring", "1");
        session.removeAttribute("assessmentID4questionToAddPool");
        forward = actionMapping.findForward("addToAssessmentFromAuthoring");
    }
    else {
    session.setAttribute("allAssets", map);
    forward = actionMapping.findForward("addToAssessment");
    }
    return forward;

/* qingru's code
    ActionForward forward = null;
    HttpSession session = request.getSession(true);
    AssessmentHelper assessmentHelper = new AssessmentHelper();
    SectionHelper sectionHelper = new SectionHelper();

    String assessmentID =
      (String) session.getAttribute("assessmentID4question");

    if(assessmentID == null)
    {
      setSelectedQuestions(actionForm, request);
      forward = actionMapping.findForward("removeQuestion");
    }
    else
    {
      setSelectedQuestions(actionForm, request);
      String[] selectedItemId =
        (String[]) session.getAttribute("selectedItemIds");
      for(int i = 0; i < selectedItemId.length; i++)
      {
        assessmentHelper.insertItemInAssessment(
          assessmentID, assessmentHelper.getLastSectionRef(assessmentID), selectedItemId[i]);
      }

      session.removeAttribute("selectedItemIds");
      session.removeAttribute("selectedItems");
      session.removeAttribute("assessmentId4question");

      org.navigoproject.business.entity.Assessment newxmlString =
        assessmentHelper.getComposedAssessment(assessmentID, true);

      assessmentHelper.setAssessmentDocument(assessmentID, request);

      forward = actionMapping.findForward("showCompleteAssessment");
    }

    return forward;


*/

  }

  /**
   * handles Remove Question(s) action
   */
  public ActionForward remove(
    ActionMapping actionMapping, ActionForm actionForm,
    HttpServletRequest request, HttpServletResponse response)
  {
    ActionForward forward = null;

    setSelectedQuestions(actionForm, request);
    forward = actionMapping.findForward("removeQuestion");

    return forward;
  }

  /**
   * handles Export Question(s) action
   */
  public ActionForward export(
    ActionMapping actionMapping, ActionForm actionForm,
    HttpServletRequest request, HttpServletResponse response)
  {
    ActionForward forward = null;

    setSelectedQuestions(actionForm, request);
    forward = actionMapping.findForward("exportQuestion");

    return forward;
  }


   public Map getAssessments(HttpServletRequest request, String username)
    throws DigitalRepositoryException
  {
    LOG.debug("getAssessments(String " + username + ")");
    Map map = new HashMap();
    HttpSession session = request.getSession(true);
    ArrayList assets = (new XmlDeliveryService()).getAssets(username);
   // ArrayList assetIds =  new ArrayList();
   // ArrayList assetNames =  new ArrayList();
    int size = assets.size();
    for(int i = 0; i < size; i++)
    {
      Asset asset = (Asset) assets.get(i);
      String assetName = asset.getDisplayName();
      String assetId = asset.getId().toString();
      map.put(assetId, assetName);
    //  assetIds.add(assetId);
     // assetNames.add(assetName);
    }
	return map;

  }

}
