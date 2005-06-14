/*
 * Created on Jan 26, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oroad.stxx.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import org.w3c.dom.Document;
import org.w3c.dom.Text;

import org.jaxen.dom.DOMXPath;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

import org.navigoproject.business.entity.RecordingData;
import org.navigoproject.business.entity.ResultReporter;
import org.navigoproject.ui.web.asi.select.SelectAssessmentForm;

/**
 * @author palcasi
 * @version $Id: GradeResultsAction.java,v 1.1.1.1 2004/07/28 21:32:07 rgollub.stanford.edu Exp $
 */
public class GradeResultsAction extends Action
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(GradeResultsAction.class);
    
  public ActionForward execute(
  ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
  HttpServletResponse response)
  {

    /**
     * @todo: we need to improve the string, this is a
     * placeholder for now.  This will, however use a unique
     * timestamp.  Also we may want to have an array of recording data
     * objects for mass-grading
     */
    RecordingData recordingData = new RecordingData(
      "grader_name",
      "student_name",
      "grade course item", null, null);

    setRecordingInfoInDocument(recordingData, request);

    SelectAssessmentForm selectAssessmentForm = new SelectAssessmentForm();
    request.setAttribute("selectAssessmentForm", selectAssessmentForm);
    ActionForward actionForward = mapping.findForward("SELECT_ASSESSMENT_RESULT");

    Document resultReport = (Document)request.getSession().getAttribute("QTI_RESULT_REPORT");
    try
    {
      String[] xpaths = request.getParameterValues("xpaths");
      for (int i = 0; i < xpaths.length; i++)
      {
        String xpath = xpaths[i];
        XPath selector = new DOMXPath(xpath);
        Text scoreValue = (Text)selector.selectSingleNode(resultReport);
        String nodeValue = request.getParameter(xpath);
        String value = scoreValue.getNodeValue();
        scoreValue.setNodeValue(nodeValue);
      }
      ResultReporter.saveResults(resultReport, request.getParameter("assessmentId"));
    }
    catch (JaxenException jaxenException)
    {
      LOG.error(jaxenException);
      throw new Error(jaxenException);
    }
    request.getSession().removeAttribute("QTI_RESULT_REPORT");
    return actionForward;
  }

  /**
   * This takes a RecordingData object and puts it in the request XML.
   * You can put any Action-specific modifications in here.
   * @param recordingData RecordingData encapsulating the audio settings
   * @param request the request object
   */
  private void setRecordingInfoInDocument(RecordingData recordingData,
                                          HttpServletRequest request)
  {
    Document document = recordingData.getXMLDataModel();
    saveDocument(request, document);
  }
}
