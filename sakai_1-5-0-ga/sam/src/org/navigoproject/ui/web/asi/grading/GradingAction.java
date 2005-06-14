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
 * Created on Mar 25, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.grading;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.navigoproject.business.entity.AssessmentReport;

import org.navigoproject.data.RepositoryManager;

import org.navigoproject.osid.OsidManagerFactory;

import org.navigoproject.service.evaluation.EvaluationServiceDelegate;
import org.navigoproject.service.evaluation.EvaluationServiceException;

import org.navigoproject.ui.web.asi.delivery.DeliveryBaseAction;
import org.navigoproject.ui.web.asi.delivery.XmlDeliveryBean;
import org.navigoproject.ui.web.asi.delivery.XmlDeliveryForm;
import org.navigoproject.ui.web.asi.delivery.XmlSelectAction;

import osid.OsidException;

import osid.shared.SharedManager;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author palcasi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class GradingAction extends DeliveryBaseAction
{
    private static final String ASSESSMENT_REPORT_KEY = "ASSESSMENT_REPORT";
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GradingAction.class);

    protected ActionForward autoSaveAction(ActionMapping mapping,
        XmlDeliveryBean deliveryBean, XmlDeliveryForm deliveryForm,
        HttpServletRequest request)
    {
        // TODO Auto-generated method stub
        return null;
    }

    protected ActionForward autoSaveInputAction(ActionMapping mapping,
        XmlDeliveryBean deliveryBean, XmlDeliveryForm deliveryForm,
        HttpServletRequest request)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param mapping
     * @param deliveryForm
     * @param deliveryBean
     * @param request
     * @return
     */
    protected ActionForward previewAssessmentAction(ActionMapping mapping,
        XmlDeliveryForm deliveryForm, XmlDeliveryBean deliveryBean,
        HttpServletRequest request)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.navigoproject.ui.web.asi.delivery.DeliveryBaseAction#beginTestAction(org.apache.struts.action.ActionMapping, org.navigoproject.ui.web.asi.delivery.XmlDeliveryForm, org.navigoproject.ui.web.asi.delivery.XmlDeliveryBean, javax.servlet.http.HttpServletRequest)
     */
    protected ActionForward beginTestAction(ActionMapping mapping,
        XmlDeliveryForm deliveryForm, XmlDeliveryBean deliveryBean,
        HttpServletRequest request)
    {
        this.saveInputs(request);
        return super.nextQuestionAction(mapping, deliveryBean, deliveryForm,
            request);
    }

    /* (non-Javadoc)
     * @see org.navigoproject.ui.web.asi.delivery.DeliveryBaseAction#cancelAction(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest, org.navigoproject.ui.web.asi.delivery.XmlDeliveryForm, org.navigoproject.ui.web.asi.delivery.XmlDeliveryBean)
     */
    protected ActionForward cancelAction(ActionMapping mapping,
        HttpServletRequest request, XmlDeliveryForm deliveryForm,
        XmlDeliveryBean deliveryBean)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.navigoproject.ui.web.asi.delivery.DeliveryBaseAction#cancelReviewAction(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest, org.navigoproject.ui.web.asi.delivery.XmlDeliveryForm, org.navigoproject.ui.web.asi.delivery.XmlDeliveryBean)
     */
    protected ActionForward cancelReviewAction(ActionMapping mapping,
        HttpServletRequest request, XmlDeliveryForm deliveryForm,
        XmlDeliveryBean deliveryBean)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.navigoproject.ui.web.asi.delivery.DeliveryBaseAction#reviewAllAction(org.apache.struts.action.ActionMapping, org.navigoproject.ui.web.asi.delivery.XmlDeliveryForm, org.navigoproject.ui.web.asi.delivery.XmlDeliveryBean, javax.servlet.http.HttpServletRequest)
     */
    protected ActionForward reviewAllAction(ActionMapping mapping,
        XmlDeliveryForm deliveryForm, XmlDeliveryBean deliveryBean,
        HttpServletRequest request)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.navigoproject.ui.web.asi.delivery.DeliveryBaseAction#saveInputs(javax.servlet.http.HttpServletRequest)
     */
    protected void saveInputs(HttpServletRequest request)
    {
        if (LOG.isDebugEnabled())
        {
            LOG.debug("saveInputs(HttpServletRequest " + request + ")");
        }

        AssessmentReport assessmentReport = (AssessmentReport) request.getSession()
                                                                      .getAttribute(ASSESSMENT_REPORT_KEY);
        XmlDeliveryBean deliveryBean = (XmlDeliveryBean) request.getSession()
                                                                .getAttribute(XmlSelectAction.ASSESSMENT_SESSION_KEY);
        RepositoryManager rm = new RepositoryManager(request);
        SharedManager sm = null;

        //      try
        //      {
        //        sm = OsidManagerFactory.createSharedManager();
        //      }
        //      catch (OsidException e1)
        //      {
        //        // TODO Auto-generated catch block
        //        LOG.debug(e1);
        //        throw new Error(e1);
        //      }
        try
        {
            EvaluationServiceDelegate evalDelegate = new EvaluationServiceDelegate();
            String[] assessmentIds = request.getParameterValues("assessmentIds");

            if (assessmentIds != null)
            {
                for (int i = 0; i < assessmentIds.length; i++)
                {
                    String assessmentId = assessmentIds[i];
                    String score = request.getParameter(assessmentId +
                            ".score");
                    String scoreAdjust = "0";
                    String publishedId = request.getParameter(assessmentId +
                            ".publishedId");

                    //save assessment comments
                    String comments = request.getParameter(assessmentId +
                            ".comments");
                    assessmentReport.setAssessmentComments(comments);

                    //rm.updateAssessmentResult(sm.getId(assessmentId), score, comments);
                    evalDelegate.changeAssessmentGrade(score, scoreAdjust,
                        comments, publishedId, assessmentId);
                }
            }

            String[] sectionIds = request.getParameterValues("sectionIds");

            if (sectionIds != null)
            {
                for (int i = 0; i < sectionIds.length; i++)
                {
                    String sectionId = sectionIds[i];

                    //save section comments
                    String comments = request.getParameter(sectionId +
                            ".comments");
                    assessmentReport.setSectionComments(sectionId, comments);
                }
            }

            String[] itemIds = request.getParameterValues("itemIds");

            if (itemIds != null)
            {
                for (int i = 0; i < itemIds.length; i++)
                {
                    String itemId = itemIds[i];
                    String assessmentResultId = request.getParameter(itemId +
                            ".assessmentResultId");

                    //save item comments
                    String comments = request.getParameter(itemId +
                            ".comments");
                    assessmentReport.setItemComments(itemId, comments);

                    //save item score
                    String score = request.getParameter(itemId + ".score");
                    assessmentReport.setItemScore(itemId, score);

                    //rm.updateItemResult(sm.getId(assessmentResultId), sm.getId(itemId), score, comments);
                    evalDelegate.changeItemGrade(score, comments, itemId,
                        assessmentResultId);
                }
            }

            deliveryBean.setQtiResultReport(assessmentReport.getDocument());
        }
        catch (EvaluationServiceException e)
        {
            LOG.error(e);
            throw new Error(e);
        }
        catch (OsidException e)
        {
            LOG.error(e);
            throw new Error(e);
        }
        catch (IOException e)
        {
            LOG.error(e);
            throw new Error(e);
        }
    }

    /* (non-Javadoc)
     * @see org.navigoproject.ui.web.asi.delivery.DeliveryBaseAction#submitAction(org.apache.struts.action.ActionMapping, org.navigoproject.ui.web.asi.delivery.XmlDeliveryForm, org.navigoproject.ui.web.asi.delivery.XmlDeliveryBean, javax.servlet.http.HttpServletRequest)
     */
    protected ActionForward submitAction(ActionMapping mapping,
        XmlDeliveryForm deliveryForm, XmlDeliveryBean deliveryBean,
        HttpServletRequest request)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
    * @param mapping
    * @param deliveryBean
    * @param deliveryForm
    * @param request
    * @return
    */
    protected ActionForward saveAndExitAction(ActionMapping mapping,
        XmlDeliveryBean deliveryBean, XmlDeliveryForm deliveryForm,
        HttpServletRequest request)
    {
        if (LOG.isDebugEnabled())
        {
            LOG.debug("saveAndExitAction(ActionMapping " + mapping + ", " +
                "XmlDeliveryForm " + deliveryForm + ", " + "XmlDeliveryBean " +
                deliveryBean + ", " + "HttpServletRequest request");
        }

        this.saveInputs(request);
        
        String id = request.getParameter("id");
        ActionForward actionForward = this.findForward(mapping, request,
          deliveryForm, deliveryBean, "TotalScores");
        
        request.getSession().removeAttribute(XmlSelectAction.ASSESSMENT_SESSION_KEY);
        request.getSession().removeAttribute("ASSESSMENT_REPORT");
        deliveryBean.release();

        return actionForward;
    }
}
