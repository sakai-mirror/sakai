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

package org.navigoproject.ui.web.asi.select;

import org.navigoproject.business.entity.ResultProcessor;
import org.navigoproject.business.entity.ResultReporter;
import org.navigoproject.business.entity.XmlStringBuffer;
import org.navigoproject.data.AssessmentQueries;
import org.navigoproject.data.QtiSettingsBean;
import org.navigoproject.data.RealizationBean;
import org.navigoproject.data.RepositoryManager;
import org.navigoproject.osid.impl.PersistenceService;
import org.navigoproject.ui.web.asi.delivery.XmlSelectAction;
import org.navigoproject.ui.web.asi.review.ReviewAssessmentBean;

import java.io.IOException;

import java.sql.Timestamp;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.xml.parsers.ParserConfigurationException;

import com.oroad.stxx.action.Action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.apache.xerces.dom.TextImpl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.xml.sax.SAXException;

import osid.dr.Asset;
import osid.dr.DigitalRepositoryException;

import osid.shared.Id;
import osid.shared.SharedException;

/**
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon</a>
 * @version $Id: SelectAssessmentAction.java,v 1.1.1.1 2004/07/28 21:32:07 rgollub.stanford.edu Exp $
 */
public class SelectAssessmentAction
  extends Action
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(SelectAssessmentAction.class);

  /**
   * DOCUMENTATION PENDING
   *
   * @param mapping DOCUMENTATION PENDING
   * @param actionForm DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   * @param response DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws SharedException DOCUMENTATION PENDING
   */
  public ActionForward execute(
    ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
    HttpServletResponse response)
    throws SharedException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "perform(ActionMapping " + mapping + ", " + "ActionForm " + actionForm +
        ", " + "HttpServletRequest request, " +
        "HttpServletResponse response)");
    }

    SelectAssessmentForm form = (SelectAssessmentForm) actionForm;

    Map map =
      PersistenceService.getInstance().getAssessmentQueries()
                        .getTakeAssessmentMap();

    ArrayList pubList = (ArrayList) map.get(AssessmentQueries.PUBLISHED_KEY);

    ArrayList lateList =
      (ArrayList) map.get(AssessmentQueries.LATE_HANDLING_KEY);

    ArrayList takenList =
      (ArrayList) map.get(AssessmentQueries.REVIEWABLE_ASSESSMENTS_TAKEN);

    form.setTakeableAssessments(this.getPublishedAssessments(pubList));
    form.setLateHandlingAssessments(this.getPublishedAssessments(lateList));
    form.setReviewAssessments(this.getTakenAssessments(request, takenList));

    String assessmentId = (String) request.getAttribute(XmlSelectAction.ASSESSMENT_ID_KEY);
    if (assessmentId != null)
    {
    	PublishedAssessmentBean pab = new PublishedAssessmentBean("", assessmentId, "");
    	if (form.getTakeableAssessments().contains(pab))
    	{
    		return mapping.findForward("SelectAssessment");
    	}
    	else
    	{
    		return mapping.findForward("INDEX");
    	}
    }
    
    return mapping.findForward("DisplayAssessments");
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param beans DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private ArrayList getPublishedAssessments(ArrayList beans)
  {
    ArrayList retList = new ArrayList();
    //try
    //{
      for(int i = 0; i < beans.size(); i++)
      {
        QtiSettingsBean qsBean = (QtiSettingsBean) (beans.get(i));
        String ident = qsBean.getId();
        String title = qsBean.getDisplayName();
        //RepositoryManager rmanager = new RepositoryManager();

        //org.navigoproject.data.PublishedAssessmentBean pab =
        //  PersistenceService.getInstance().getPublishedAssessmentQueries()
        //                    .findCoreIdByPublishedId(ident);

        //Id coreAssessmentId = rmanager.getId(pab.getCoreId());

        //Calendar createdDate = Calendar.getInstance();
        //Timestamp createdTime = qsBean.getCreatedDate();
        //createdDate.setTimeInMillis(createdTime.getTime());

        //Asset asset = rmanager.getAsset(coreAssessmentId, createdDate);
        //if(asset != null)
        //{
          //String title = asset.getDisplayName();

          SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy, HH:mm");
          String dueDate = sdf.format(qsBean.getEndDate());

          PublishedAssessmentBean bean =
            new PublishedAssessmentBean(title, ident, dueDate);
          retList.add(bean);
        //}
      }
    //}
    //catch(DigitalRepositoryException e)
    //{
    //  LOG.error(e.getMessage(), e);
    //}

    return retList;
  }
  
  private ArrayList getTakenAssessments(
    HttpServletRequest request, ArrayList beans)
  {
    ArrayList list = new ArrayList();
    //try
    //{
      for(int i = 0; i < beans.size(); i++)
      {
        RealizationBean rBean = (RealizationBean) beans.get(i);
        String ident = rBean.getAssessmentTakenId();
        //RepositoryManager rmanager = new RepositoryManager();
        //Asset asset = rmanager.getAsset(rmanager.getId(ident));
        //if(asset != null)
        //{
          //String title = asset.getDisplayName();
          String title = rBean.getAssessmentTitle();
          String rawScore = "n/a";
          String time = null;
          String submissionTime = null;
          long remaining = 0,
               days = 0,
               hours = 0,
               minutes = 0,
               seconds = 0;
          
          if(rBean != null)
          {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            if (rBean.getSubmissionTime() != null){
              submissionTime = sdf.format(rBean.getSubmissionTime());
              remaining =
                rBean.getSubmissionTime().getTime() -
                rBean.getBeginTime().getTime();
            }
            
            days = (remaining - (remaining % 86400000)) / 86400000;
            remaining = remaining - (days * 86400000);
            hours = (remaining - (remaining % 3600000)) / 3600000;
            remaining = remaining - (hours * 3600000);
            minutes = (remaining - (remaining % 60000)) / 60000;
            remaining = remaining - (minutes * 60000);
            seconds = (remaining - (remaining % 1000)) / 1000;
            time =
              "" + days + "days " + hours + "hrs " + minutes + "mins " + seconds +
              "secs ";           
          //}
          ReviewAssessmentBean reviewBean =
            new ReviewAssessmentBean(
              title, ident, rawScore, rawScore, time, submissionTime);
          list.add(reviewBean);
        }
      }
    //}
    //catch(DigitalRepositoryException e)
    //{
    //  LOG.error(e.getMessage(), e);
    //}

    return list;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   * @param beans DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
//  private ArrayList getTakenAssessmentsOld(
//    HttpServletRequest request, ArrayList beans)
//  {
//    ArrayList list = new ArrayList();
//    try
//    {
//      for(int i = 0; i < beans.size(); i++)
//      {
//        RealizationBean rBean = (RealizationBean) beans.get(i);
//        String ident = rBean.getAssessmentTakenId();
//        RepositoryManager rmanager = new RepositoryManager();
//        Asset asset = rmanager.getAsset(rmanager.getId(ident));
//        if(asset != null)
//        {
//          org.navigoproject.business.entity.Assessment assessmentXml =
//            new org.navigoproject.business.entity.Assessment(
//              (String) asset.getContent());
//
//          //TODO query QTI_ASSESSMENT_TAKEN table to get time, and submissionTime. 
//          //TODO get grade from processed result. 
//          ServletContext context = request.getSession().getServletContext();
//          Document assessmentDoc = assessmentXml.getDocument();
//          ResultProcessor processor = new ResultProcessor(context, assessmentDoc);
//          Document result =
//            processor.processResults(ResultReporter.getAssessmentResult(ident));
//
//          String title = assessmentXml.getTitle();
//          String time = null;
//          String submissionTime = null;
//          if(rBean != null)
//          {
//            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
//            submissionTime = sdf.format(rBean.getSubmissionTime());
//            long remaining =
//              rBean.getSubmissionTime().getTime() -
//              rBean.getBeginTime().getTime();
//            long days = (remaining - (remaining % 86400000)) / 86400000;
//            remaining = remaining - (days * 86400000);
//            long hours = (remaining - (remaining % 3600000)) / 3600000;
//            remaining = remaining - (hours * 3600000);
//            long minutes = (remaining - (remaining % 60000)) / 60000;
//            remaining = remaining - (minutes * 60000);
//            long seconds = (remaining - (remaining % 1000)) / 1000;
//            time =
//              "" + days + "days " + hours + "hrs " + minutes + "mins " + seconds +
//              "secs ";
//          }
//
//          String rawScore = getRawScore(request, assessmentXml, result, ident);
//          ReviewAssessmentBean reviewBean =
//            new ReviewAssessmentBean(
//              title, ident, rawScore, rawScore, time, submissionTime);
//          list.add(reviewBean);
//        }
//      }
//    }
//    catch(DigitalRepositoryException e)
//    {
//      LOG.error(e.getMessage(), e);
//    }
//    catch(IOException e)
//    {
//      LOG.error(e.getMessage(), e);
//    }
//    catch(SAXException e)
//    {
//      LOG.error(e.getMessage(), e);
//    }
//    catch(ParserConfigurationException e)
//    {
//      LOG.error(e.getMessage(), e);
//    }
//
//    return list;
//  }

  /**
   * @param ident
   * @return
   */
  private String getRawScore(
    HttpServletRequest request,
    org.navigoproject.business.entity.Assessment assessmentXml, Document result, String ident)
  {
    String rawScore = "";
//      Document result = ResultReporter.getAssessmentResult(ident);
//      ServletContext context = request.getSession().getServletContext();
//      ResultProcessor processor =
//        new ResultProcessor(context, assessmentXml.getDocument());
//      Document processedResult = processor.processResults(result);
      String xpath1 =
        "qti_result_report/result/assessment_result/outcomes/score/score_value";
      String xpath2 =
        "qti_result_report/result/assessment_result/outcomes/score/score_max";
      XmlStringBuffer xml = new XmlStringBuffer(result);
      rawScore =
        getSingleNodeValue(xml, xpath1) + "/" +
        getSingleNodeValue(xml, xpath2);

    return rawScore;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param xml DOCUMENTATION PENDING
   * @param xpath DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private String getSingleNodeValue(XmlStringBuffer xml, String xpath)
  {
    String value = "";
    List list = xml.selectNodes(xpath);
    if(list.size() >= 1)
    {
      Element node = (Element) list.get(0);
      Object firstChild = node.getFirstChild();
      if((firstChild != null) && firstChild instanceof TextImpl)
      {
        TextImpl textImpl = (TextImpl) firstChild;
        value = textImpl.getNodeValue();
      }
    }

    return value;
  }
}
