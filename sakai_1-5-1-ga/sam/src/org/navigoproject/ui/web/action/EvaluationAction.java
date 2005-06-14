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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.navigoproject.business.entity.BeanSort;
import org.navigoproject.business.entity.Item;
import org.navigoproject.business.entity.RecordingData;
import org.navigoproject.business.entity.assessment.model.MediaData;
import org.navigoproject.business.entity.evaluation.model.AgentResults;
import org.navigoproject.data.QtiSettingsBean;
import org.navigoproject.data.RepositoryManager;
import org.navigoproject.osid.impl.PersistenceService;
import org.navigoproject.service.evaluation.EvaluationServiceDelegate;
import org.navigoproject.service.evaluation.EvaluationServiceException;
import org.navigoproject.ui.web.asi.author.AuthoringHelper;
import org.navigoproject.ui.web.asi.author.assessment.AssessmentHelper;
import org.navigoproject.ui.web.asi.author.item.ItemHelper;
import org.navigoproject.ui.web.asi.author.section.SectionHelper;
import org.navigoproject.ui.web.form.evaluation.HistogramQuestionScoresForm;
import org.navigoproject.ui.web.form.evaluation.HistogramScoresForm;
import org.navigoproject.ui.web.form.evaluation.QuestionScoresForm;
import org.navigoproject.ui.web.form.evaluation.TotalScoresForm;
import osid.assessment.Assessment;
import osid.shared.Id;


import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.navigoproject.business.entity.assessment.model.MediaData;
import org.navigoproject.business.entity.evaluation.model.AgentResults;
import org.navigoproject.business.entity.evaluation.model.MediaZipInfoModel;
import org.navigoproject.business.entity.evaluation.model.util.MediaDataZipArchive;
import org.navigoproject.data.dao.UtilAccessObject;


/**
 * Handles the Evaluation actions for grading and analysis.
 *
 * @author Rachel Gollub <rgollub@stanford.edu>
 * @author Ed Smiley <esmiley@stanford.edu>
 * @author Qingru Zhang <zqingru@stanford.edu>
 * @author Huong Nguyen <hquinn@stanford.edu>
 * @version $Id: EvaluationAction.java,v 1.2 2004/08/03 18:11:16 lydial.stanford.edu Exp $
 *
 * @see TotalScoresForm, QuestionScoresForm,HistogramScoresForm,HistogramQuestionScoresForm
 *
 */
public class EvaluationAction
  extends AAMAction
{
  private static final Logger LOG = Logger.getLogger(EvaluationAction.class);
  private static BeanSort bs;
  private static String role = "";

  /**
   * This is the standard execute method.  It handles the submit methods for
   * the grading pages.
   */
  public ActionForward execute(
    ActionMapping actionMapping, ActionForm actionForm,
    HttpServletRequest httpServletRequest,
    HttpServletResponse httpServletResponse)
  {
    ActionForward forward = null;
    HttpSession session = httpServletRequest.getSession(true);

    boolean autosave = false;
    if (org.navigoproject.settings.ApplicationSettings.isEnableAutoSaveForGrading()){
       autosave = true;
    }

    if(actionMapping.getPath().indexOf("editTotalResults") > -1)
    {
      LOG.info("posting total scores");
      doSaveTotalScores(httpServletRequest);
      forward = actionMapping.findForward("totalScores");
      if(httpServletRequest.getParameter("submitexit") != null)
      {
        LOG.info("submit and exit");
        forward = actionMapping.findForward("AUTHOR");
      }
    }

    if(actionMapping.getPath().indexOf("editQuestionResults") > -1)
    {
      LOG.info("posting question scores");
      doSaveQuestionScores(httpServletRequest);
      forward = actionMapping.findForward("questionScores");
      if(httpServletRequest.getParameter("submitexit") != null)
      {
        LOG.info("submit and exit");
        forward = actionMapping.findForward("AUTHOR");
      }
    }

    if(
      (actionMapping.getPath().indexOf("totalScores") > -1) ||
        (actionMapping.getPath().indexOf("anonymous") > -1) ||
        (actionMapping.getPath().indexOf("submissionStatus") > -1))
    {
      boolean success = false;

      success =
        doTotalScores(
          session, httpServletRequest, (TotalScoresForm) actionForm);

      if(! success)
      {
        forward =
          forwardToFailure(
            "EvaluationAction: Failed to view Total Scores.", actionMapping,
            httpServletRequest);
      }
      else
      {
        if(actionMapping.getPath().indexOf("totalScores") > -1)
        {
          //test if anonymous
if (autosave){
      forward = actionMapping.findForward("gradingAutoSave");
}
else {
      forward = actionMapping.findForward("totalScores");
}

        }

        if(actionMapping.getPath().indexOf("anonymous") > -1)
        {
          forward = actionMapping.findForward("anonymous");
        }

        if(actionMapping.getPath().indexOf("submissionStatus") > -1)
        {
          forward = actionMapping.findForward("submissionStatus");
        }
      }

      //      setTotalScoresValue((TotalScoresForm) actionForm, 100);
    }

    if(actionMapping.getPath().indexOf("questionScores") > -1)
    {
      boolean success =
        doQuestionScores(
          session, httpServletRequest, (QuestionScoresForm) actionForm);

      if(success)
      {
        forward = actionMapping.findForward("questionScores");
      }
      else
      {
        forward =
          forwardToFailure(
            "EvaluationAction: Failed to view Question Scores.", actionMapping,
            httpServletRequest);
      }

      //      setQuestionScoresValue((QuestionScoresForm) actionForm, 100);
    }

    if(actionMapping.getPath().indexOf("histogramScores") > -1)
    {
      boolean success = true;
      LOG.debug("Histogram Scores passing");

      forward = actionMapping.findForward("histogramScores");

      if(httpServletRequest.getParameter("roleSelection") != null)
      {
        role = httpServletRequest.getParameter("roleSelection");
        double[] roleScore;
        double[] qroleScore;
        String assessmentId = null;

        try
        {
          LOG.debug(
            "NOT TEST DATA AND ROLE SELECTION AND READY FOR DOHISTOGRAM");
          doHistogram(
            session, httpServletRequest, (HistogramScoresForm) actionForm);
        }
        catch(Exception ex)
        {
          LOG.error("Histogram error. ", ex);
        }
      }
      else //no role selection
      {
        try
        {
          LOG.debug(
            "NOT TEST DATA AND NOT ROLE SELECTION AND READY FOR DOHISTOGRAM");
          doHistogram(
            session, httpServletRequest, (HistogramScoresForm) actionForm);
        }
        catch(Exception ex)
        {
          LOG.error(ex);
        }
      }

      //setHistogramQuestionForm(histogramQuestionForm,mcQuestion,correct,"Multiple Choice");
      //--setHistogramQuestionForm(histogramQuestionForm,tfQuestion,tfCorrect, "True/False");
    }

    //END OF HISTOGRAMSCORES

    if(actionMapping.getPath().indexOf("exportZip") > -1)
    {
      boolean success =
        doExportZip(
          session, httpServletRequest, httpServletResponse,
          (QuestionScoresForm) actionForm);

      if(!success)
      {
        forward =
          forwardToFailure(
            "EvaluationAction: Failed to download zip file.", actionMapping,
            httpServletRequest);
      }

    }
    LOG.info("action mapping: " + actionMapping.getPath());

    return forward;
  }

  //end execute

  /**
   * DOCUMENTATION PENDING
   *
   * @param answers DOCUMENTATION PENDING
   * @param correct DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public void doHistogram(
    HttpSession session, HttpServletRequest request, HistogramScoresForm form)
    throws EvaluationServiceException
  {
    String assessmentId = null;
    String assessmentName = null;
    Assessment myAssessment = null;
    try
    {
      AssessmentHelper assessmentHelper = new AssessmentHelper();
      String id = request.getParameter("id");
      String publishedID = request.getParameter("publishedID");
      session.setAttribute("publishedID", publishedID);

      // find creation date of published assessment so we can get versioned asset
      QtiSettingsBean qsb = PersistenceService.getInstance().getQtiQueries().returnQtiSettingsBean(publishedID);
      Calendar cal = Calendar.getInstance();
      if (qsb == null){
        throw new Exception("QtiSettingsBean should not be null here!");
      }
      else{
        Timestamp ts = qsb.getCreatedDate();
        if (ts == null){
          throw new Exception("Created date should not be null here!");
        }
        else{
          cal.setTimeInMillis(ts.getTime());
        }
      }

      if(id != null)
      {
        myAssessment = assessmentHelper.getAssessmentByDate(id, cal);
        assessmentId = myAssessment.getId().toString();
        assessmentName = myAssessment.getDisplayName();
        LOG.debug("ASSESSMENT ID= " + assessmentId);
        LOG.debug("ASSESSMENT NAME= " + assessmentName);

        EvaluationServiceDelegate evalDelegate =
          new EvaluationServiceDelegate();

        //SUBMISSION SELECTION
        if(request.getParameter("allSubmissions") != null)
        {
          if(request.getParameter("allSubmissions").equals("true"))
          {
            form.setAllSubmissions(true);
          }
          else
          {
            form.setAllSubmissions(false);
          }
        }

        Map assessmentMap = null;

        // call service to get Map of statistics information
        if(form.getAllSubmissions() == true)
        {
          assessmentMap =
            evalDelegate.getAssessmentStatisticsMap(assessmentId, false);
        }
        else
        {
          assessmentMap =
            evalDelegate.getAssessmentStatisticsMap(assessmentId, true);
        }

        // test to see if it gets back empty map
        if(assessmentMap.isEmpty())
        {
          form.setNumResponses(0);
        }

        try
        {
          BeanUtils.populate(form, assessmentMap);

          // quartiles don't seem to be working, workaround
          form.setQ1((String) assessmentMap.get("q1"));
          form.setQ2((String) assessmentMap.get("q2"));
          form.setQ3((String) assessmentMap.get("q3"));
          form.setQ4((String) assessmentMap.get("q4"));
          form.setTotalScore(castingNum(getTotalPoints(assessmentId, cal)));

          ///////////////////////////////////////////////////////////
          // START DEBUGGING
          LOG.debug("TESTING ASSESSMENT MAP");
          LOG.debug("assessmentMap: =>");
          LOG.debug(assessmentMap);
          LOG.debug("--------------------------------------------");
          LOG.debug("TESTING TOTALS HISTOGRAM FORM");
          LOG.debug(
            "HistogramScoresForm Form: =>\n" + "form.getMean()=" +
            form.getMean() + "\n" + "form.getColumnHeight()[0] (first elem)=" +
            form.getColumnHeight()[0] + "\n" + "form.getInterval()=" +
            form.getInterval() + "\n" + "form.getLowerQuartile()=" +
            form.getLowerQuartile() + "\n" + "form.getMaxScore()=" +
            form.getMaxScore() + "\n" + "form.getMean()=" + form.getMean() +
            "\n" + "form.getMedian()=" + form.getMedian() + "\n" +
            "form.getNumResponses()=" + form.getNumResponses() + "\n" +
            "form.getNumStudentCollection()=" + form.getNumStudentCollection() +
            "\n" + "form.getQ1()=" + form.getQ1() + "\n" + "form.getQ2()=" +
            form.getQ2() + "\n" + "form.getQ3()=" + form.getQ3() + "\n" +
            "form.getQ4()=" + form.getQ4());
          LOG.debug("--------------------------------------------");

          // END DEBUGGING CODE
          ///////////////////////////////////////////////////////////
        }
        catch(Exception e)
        {
          LOG.debug("unable to populate form" + e);
        }

        form.setAssessmentName(assessmentName);
        form.setAssessmentId(assessmentId);

        // end submissionSelection
        ArrayList sections = assessmentHelper.getSectionRefsByDate(assessmentId, cal);

        // create array of HistogramQuestionScoresForms
        int numQuestions = 0;
        LOG.debug("sections size : " + sections.size());

        //To find out how many questions in total.
        for(int p = 0; p < sections.size(); p++)
        {
          System.out.print("before sectionID of p " + p);

          //  Element a= (Element) sections.get(p);
          String sectionID = (String) sections.get(p);
          LOG.debug("section  ID [" + p + "]:" + sectionID);
          if(sectionID == null)
          {
            continue; // skip
          }

          SectionHelper sectionHelper = new SectionHelper();
          ArrayList itemList = sectionHelper.getSectionItems(sectionID, false);
          LOG.debug("Item's size : " + itemList.size());

          for(int i = 0; i < itemList.size(); i++)
          {
            LOG.debug("NUMQUESTIONS p=" + p + ",i=" + i);
            numQuestions++;
          }
        }

        LOG.debug("TOTAL QUESTIONS :" + numQuestions);

        HistogramQuestionScoresForm[] qArray =
          new HistogramQuestionScoresForm[numQuestions];

        //  HistogramQuestionScoresForm qForm=new HistogramQuestionScoresForm();
        numQuestions = 0;

        for(int p = 0; p < sections.size(); p++)
        {
          System.out.print("before sectionID of p " + p);

          //  Element a= (Element) sections.get(p);
          String sectionID = (String) sections.get(p);
          LOG.debug("section  ID [" + p + "]:" + sectionID);
          if(sectionID == null)
          {
            continue; // skip
          }

          SectionHelper sectionHelper = new SectionHelper();
          ArrayList itemList = sectionHelper.getSectionItems(sectionID, true);
          LOG.debug("Item's size : " + itemList.size());

          for(int i = 0; i < itemList.size(); i++)
          {
            HistogramQuestionScoresForm qForm =
              new HistogramQuestionScoresForm();
            qArray[i] = qForm;
            qForm.setQuestionNumber("" + (i + 1));
            qForm.setPartNumber("" + (p + 1));
            LOG.debug("NUMQUESTIONS p=" + p + ",i=" + i);
            numQuestions++;

            // calculate question information, such as question type
            String itemId = itemList.get(i).toString();
            ItemHelper itemHelper = new ItemHelper();
            Item item = itemHelper.getItemXml(itemId);
            qForm.setQuestionText(item.getItemText());
            qForm.setQuestionType(item.getItemType());
            try
            {
              // call service to get Map of statistics information
              Map itemMap = null;
              if(form.getAllSubmissions() == true)
              {
                itemMap =
                  evalDelegate.getItemStatisticsMap(
                    assessmentId, itemId, false);
              }
              else
              {
                itemMap =
                  evalDelegate.getItemStatisticsMap(assessmentId, itemId, true);
              }

              BeanUtils.populate(qForm, itemMap);

              // quartiles don't seem to be working, workaround
              qForm.setQ1((String) itemMap.get("q1"));
              qForm.setQ2((String) itemMap.get("q2"));
              qForm.setQ3((String) itemMap.get("q3"));
              qForm.setQ4((String) itemMap.get("q4"));
              String corranswer =
                evalDelegate.getCorrectAnswer(assessmentId, itemId);
              qForm.setCorrectAnswer(corranswer);

              ///////////////////////////////////////////////////////////
              // START DEBUGGING
              LOG.debug("TESTING ITEM MAP");
              LOG.debug(
                "statistics map for item id: " + itemId + "assessment id: " +
                assessmentId);
              LOG.debug(itemMap);
              LOG.debug("--------------------------------------------");
              LOG.debug("TESTING QUESTION HISTOGRAM FORM");
              LOG.debug(
                "HistogramQuestionScoresForm: =>" + "qForm.getMean()=" +
                qForm.getMean() + "\n" + "qForm.getColumnHeight()=" +
                qForm.getColumnHeight() + "\n" + "qForm.getInterval()=" +
                qForm.getInterval() + "\n" + "qForm.getLowerQuartile()=" +
                qForm.getLowerQuartile() + "\n" + "qForm.getMaxScore()=" +
                qForm.getMaxScore() + "\n" + "qForm.getMedian()=" +
                qForm.getMedian() + "\n" + "qForm.getNumResponses()=" +
                qForm.getNumResponses() + "\n" +
                "qForm.getNumStudentCollection()=" +
                qForm.getNumStudentCollection() + "\n" + "qForm.getQ1()=" +
                qForm.getQ1() + "\n" + "qForm.getQ2()=" + qForm.getQ2() + "\n" +
                "qForm.getQ3()=" + qForm.getQ3() + "\n" + "qForm.getQ4()=" +
                qForm.getQ4());
              LOG.debug("--------------------------------------------");

              // END DEBUGGING CODE
              ///////////////////////////////////////////////////////////
            }
            catch(Exception e)
            {
              LOG.debug("unable to populate form" + e);
            }
          }
        }

        form.setHistogramQuestions(qArray);
      }
      else
      {
        throw new EvaluationServiceException(
          "EvaluationAction: Failed to view Histograms for unknown assessment.");
      }
    }
    catch(Exception e)
    {
      throw new EvaluationServiceException(
        "EvaluationAction: Failed to process Histograms for assessment " +
        assessmentId + ".  " + e);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param session DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   * @param form DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean doTotalScores(
    HttpSession session, HttpServletRequest request, TotalScoresForm form)
  {
    LOG.debug("doTotalScores()");
    boolean anonymousGrading;
    try
    {
      AssessmentHelper assessmentHelper = new AssessmentHelper();
      String id = request.getParameter("id");
      String publishedID = request.getParameter("publishedID");
      session.setAttribute("publishedID", publishedID);

      // find creation date of published assessment so we can get versioned asset
      QtiSettingsBean qsb = PersistenceService.getInstance().getQtiQueries().returnQtiSettingsBean(publishedID);
      Calendar cal = Calendar.getInstance();
      if (qsb == null){
        throw new Exception("QtiSettingsBean should not be null here!");
      }
      else{
        Timestamp ts = qsb.getCreatedDate();
        if (ts == null){
          throw new Exception("Created date should not be null here!");
        }
        else{
          cal.setTimeInMillis(ts.getTime());
        }
      }

      if(id != null)
      {
        //Assessment myAssessment = assessmentHelper.getAssessment(id);
        // get assessment by date
        Assessment myAssessment = assessmentHelper.getAssessmentByDate(id, cal);
        if (myAssessment == null){
          throw new Exception("assessment should not be null here");
        }

        String assessmentId = myAssessment.getId().toString();
        String assessmentName = myAssessment.getDisplayName();
        Boolean assessmentAnonymous =
          new Boolean(
            assessmentHelper.getFieldentryByDate(assessmentId, cal,  "ANONYMOUS_GRADING"));
        anonymousGrading = assessmentAnonymous.booleanValue();
        form.setAssessmentName(assessmentName);
        form.setAssessmentId(assessmentId);
        form.setAnonymous(anonymousGrading);
        form.setLateHandling(
          assessmentHelper.getFieldentryByDate(assessmentId, cal, "LATE_HANDLING"));
        form.setDueDate(
          assessmentHelper.getFieldentryByDate(assessmentId, cal, "DUE_DATE"));
        form.setMaxScore(castingNum(getTotalPoints(assessmentId, cal)));
        LOG.info("assessment id " + assessmentId);
      }
      else
      {
        // we need to know at least what assessment we are on
        return false;
      }

      if(request.getParameter("roleSelection") != null)
      {
        form.setRoleSelection(request.getParameter("roleSelection"));
      }

      if(request.getParameter("sortType") != null)
      {
        form.setSortType(request.getParameter("sortType"));
      }
      else
      {
        if(anonymousGrading)
        {
          form.setSortType("totalScore");
        }
        else
        {
          form.setSortType("lastName");
        }
      }

      if(request.getParameter("allSubmissions") != null)
      {
        if(request.getParameter("allSubmissions").equals("true"))
        {
          form.setAllSubmissions(true);
        }
        else
        {
          form.setAllSubmissions(false);
        }
      }
      // recordingData encapsulates the information needed for recording.
      // set recording agent, agent id,
      // set course_assignment_context value
      // set max tries (0=unlimited), and 30 seconds max length
      String courseContext = form.getAssessmentName() + " total ";
      AuthoringHelper authoringHelper = new AuthoringHelper();
      RecordingData recordingData =
          new RecordingData(
          authoringHelper.getRemoteUserID(request).toString(),
          authoringHelper.getRemoteUserName(request),
          courseContext, "0", "30");
      // set this value in the request for sound recorder
      session.setAttribute("recordingData", recordingData);

      // computation of agent data
      EvaluationServiceDelegate evalDelegate = new EvaluationServiceDelegate();
      Collection agents = new ArrayList();

      // if we are getting all submissions then we call the service delegate
      // and set the last submission flag to false, otherwise we call it
      // setting the last flag to true
      if(form.getAllSubmissions() == true)
      {
        agents =
          evalDelegate.getAgentTotalResults(form.getAssessmentId(), false);
      }
      else
      {
        agents =
          evalDelegate.getAgentTotalResults(form.getAssessmentId(), true);
      }

      bs = new BeanSort(agents, form.getSortType());
      if(
        (form.getSortType()).equals("totalScore") ||
          (form.getSortType()).equals("adjustmentTotalScore") ||
          (form.getSortType()).equals("finalScore"))
      {
        bs.toNumericSort();
      }

      bs.sort();
      form.setAgents(agents);
    }

    catch(Exception e)
    {
      LOG.error(e);

      return false;
    }

    return true;
  }

  /**
   * Assemble the data for the grading page for individual questions.
   * @param session current HttpSession
   * @param id the id of the assessment
   */
  public boolean doQuestionScores(
    HttpSession session, HttpServletRequest request, QuestionScoresForm form)
  {
    boolean anonymousGrading;
    String itemId = "";
    String assessmentId = "";
    EvaluationServiceDelegate evalDelegate;
    ArrayList sectionList = new ArrayList();
    ArrayList itemListList = new ArrayList();
    try
    {
      evalDelegate = new EvaluationServiceDelegate();
      AssessmentHelper assessmentHelper = new AssessmentHelper();
      String id = request.getParameter("id");
      String publishedID = request.getParameter("publishedID");
      session.setAttribute("publishedID", publishedID);

      // find creation date of published assessment so we can get versioned asset
      QtiSettingsBean qsb = PersistenceService.getInstance().getQtiQueries().returnQtiSettingsBean(publishedID);
      Calendar cal = Calendar.getInstance();
      if (qsb == null){
        throw new Exception("QtiSettingsBean should not be null here!");
      }
      else{
        Timestamp ts = qsb.getCreatedDate();
        if (ts == null){
          throw new Exception("Created date should not be null here!");
        }
        else{
          cal.setTimeInMillis(ts.getTime());
        }
      }

      if(id != null)
      {
        Assessment myAssessment = assessmentHelper.getAssessmentByDate(id, cal);
        assessmentId = myAssessment.getId().toString();
        String assessmentName = myAssessment.getDisplayName();
        Boolean assessmentAnonymous =
          new Boolean(
            assessmentHelper.getFieldentryByDate(assessmentId, cal, "ANONYMOUS_GRADING"));
        anonymousGrading = assessmentAnonymous.booleanValue();
        sectionList = assessmentHelper.getSectionRefsByDate(assessmentId, cal);
        form.setAssessmentName(assessmentName);
        form.setAssessmentId(assessmentId);
        form.setAnonymous(anonymousGrading);
        form.setLateHandling(
          assessmentHelper.getFieldentryByDate(assessmentId, cal, "LATE_HANDLING"));
        form.setCommentEachQuestion(
          assessmentHelper.getFieldentryByDate(
            assessmentId, cal, "FEEDBACK_SHOW_GRADER_COMMENT"));
        form.setDueDate(
          assessmentHelper.getFieldentryByDate(assessmentId, cal, "DUE_DATE"));
        form.setMaxScore(castingNum(getTotalPoints(assessmentId, cal)));

        LOG.info("assessment id " + assessmentId);
      }
      else
      {
        // we need to know at least what assessment we are on
        return false;
      }

      //we can start out on part 1, question 1
      if(request.getParameter("questionNumber") != null)
      {
        form.setQuestionNumber(request.getParameter("questionNumber"));
      }

      if(request.getParameter("partNumber") != null)
      {
        form.setPartNumber(request.getParameter("partNumber"));
      }

      if(request.getParameter("roleSelection") != null)
      {
        form.setRoleSelection(request.getParameter("roleSelection"));
      }

      if(request.getParameter("sortType") != null)
      {
        form.setSortType(request.getParameter("sortType"));
      }
      else
      {
        if(anonymousGrading)
        {
          form.setSortType("assessmentResultID");
        }
        else
        {
          form.setSortType("lastName");
        }
      }

      if(request.getParameter("allSubmissions") != null)
      {
        if(request.getParameter("allSubmissions").equals("true"))
        {
          form.setAllSubmissions(true);
        }
        else
        {
          form.setAllSubmissions(false);
        }
      }
      // recordingData encapsulates the information needed for recording.
      // set recording agent, agent id,
      // set course_assignment_context value
      // set max tries (0=unlimited), and 30 seconds max length
      String courseContext = form.getAssessmentName() + " score " +
                             "_q" + form.getQuestionNumber() +
                             "_p" + form.getPartNumber();
      AuthoringHelper authoringHelper = new AuthoringHelper();
      RecordingData recordingData =
          new RecordingData(
          authoringHelper.getRemoteUserID(request).toString(),
          authoringHelper.getRemoteUserName(request),
          courseContext, "0", "30");
      // set this value in the request for sound recorder
      session.setAttribute("recordingData", recordingData);

      // Computation for parts and items
      // count of parts
      int parts = sectionList.size();
      LOG.info("parts=" + parts);

      // list of the count of question items for each part
      ArrayList questionList = new ArrayList();

      SectionHelper sectionHelper = new SectionHelper();

      // we loop through each part and each question within each part
      for(int p = 0; p < parts; p++)
      {
        String sectionId = (String) sectionList.get(p);
        LOG.info("getSectionItems(  String" + sectionId + ", boolean true )");

        ArrayList items = sectionHelper.getSectionItems(sectionId, true);
        int questionCount = items.size();
        LOG.info("questions for " + p + ": " + questionCount);
        LOG.info("item " + items.get(0));

        // add in the count of questions in this part
        questionList.add(new Integer(questionCount));

        if(! form.getPartNumber().equals("" + (p + 1)))
        {
          continue; // our item is not here, look in the next one
        }

        form.setPartText(sectionHelper.getSection(sectionId).getDisplayName());

        LOG.info("processing part: " + form.getPartText());

        // look up item in part
        for(int q = 0; q < questionCount; q++)
        {
          if(
            form.getQuestionNumber().equals("" + (q + 1)) &&
              form.getPartNumber().equals("" + (p + 1)))
          {
            LOG.info("itemId=" + items.get(q));
            itemId = items.get(q).toString();
            ItemHelper itemHelper = new ItemHelper();
            org.navigoproject.business.entity.Item item =
              itemHelper.getItemXml(itemId);
            form.setItem(item);
            form.setQuestionText(item.getItemText());
            form.setQuestionType(item.getItemType());
            form.setQuestionAnswer(getAnswers(item));
            form.setQuestionPoint(castingNum(getMaxPoints(item)));

            break; // found item, so done with these items
          }
        }
      }

      Integer[] questions = new Integer[questionList.size()];
      for(int q = 0; q < questions.length; q++)
      {
        questions[q] = (Integer) questionList.get(q);
      }

      form.setParts(new Integer(parts));
      form.setQuestions(questions);

      Collection agents = new ArrayList();

      //      LOG.info("getAllSubmissions=" + form.getAllSubmissions());
      // if we are getting all submissions then we call the service delegate
      // and set the last submission flag to false, otherwise we call it
      // setting the last flag to true
      if(("" + form.getAllSubmissions()).equals("true"))
      {
        //        LOG.info("getAllSubmissions=" + form.getAllSubmissions());
        agents =
          evalDelegate.getAgentItemResults(
            form.getAssessmentId(), itemId, false);
      }
      else
      {
        //        LOG.info("getAllSubmissions=" + form.getAllSubmissions());
        agents =
          evalDelegate.getAgentItemResults(
            form.getAssessmentId(), itemId, true);
      }

      bs = new BeanSort(agents, form.getSortType());

      if((form.getSortType()).equals("totalScore"))
      {
        bs.toNumericSort();
      }

      bs.sort();
      form.setAgents(agents);
      form.setTotalPeople(new Integer(form.getAgents().size()).toString());

      return true;
    }
    catch(Exception e)
    {
      LOG.error(e);

      return false;
    }
  }

  /**
   * Assemble the data in a zip file for an individual question.
   * @param session current HttpSession
   * @param request current HttpRequest
   */

  public boolean doExportZip(
      HttpSession session, HttpServletRequest request,
      HttpServletResponse response, QuestionScoresForm form)
  {
    // get information about the question
    boolean anonymousGrading;
    String itemId = "";
    String assessmentId = "";
    EvaluationServiceDelegate evalDelegate;
    try
    {
      evalDelegate = new EvaluationServiceDelegate();
      AssessmentHelper assessmentHelper = new AssessmentHelper();
      String id = request.getParameter("id");
      String publishedID = request.getParameter("publishedID");
      session.setAttribute("publishedID", publishedID);

      // find creation date of published assessment so we can get versioned asset
      QtiSettingsBean qsb = PersistenceService.getInstance().getQtiQueries().returnQtiSettingsBean(publishedID);
      Calendar cal = Calendar.getInstance();
      if (qsb == null){
        throw new Exception("QtiSettingsBean should not be null here!");
      }
      else{
        Timestamp ts = qsb.getCreatedDate();
        if (ts == null){
          throw new Exception("Created date should not be null here!");
        }
        else{
          cal.setTimeInMillis(ts.getTime());
        }
      }

      if(id != null)
      {
        Assessment myAssessment = assessmentHelper.getAssessmentByDate(id, cal);
        assessmentId = myAssessment.getId().toString();
        String assessmentName = myAssessment.getDisplayName();
        LOG.info("assessment id " + assessmentId);
      }
      else
      {
        // we need to know at least what assessment we are on
        return false;
      }

      // get the agent results
      Collection agents = new ArrayList();
      if(("" + form.getAllSubmissions()).equals("true"))
      {
        LOG.info("getAllSubmissions=" + form.getAllSubmissions());
        agents =
          evalDelegate.getAgentItemResults(
            form.getAssessmentId(), itemId, false);
      }
      else
      {
        LOG.info("getAllSubmissions=" + form.getAllSubmissions());
        agents =
          evalDelegate.getAgentItemResults(
            form.getAssessmentId(), itemId, true);
      }
      // zip them up
      LOG.info("creating zip file");
      showZipFile(agents, publishedID, form, response);
      return true;
    }
    catch(Exception e)
    {
      LOG.error(e);

      return false;
    }
  }

  /**
   * Helper method.
   * Displays zip file for download from agenr results
   *
     @param agentResults
   * @param publishedID
   * @param form
   * @param res
   * @throws IOException
   */

  private void showZipFile(Collection agentResults, String publishedID,
                           QuestionScoresForm form, HttpServletResponse res)
  throws IOException
  {
    // set up zip file
    MediaZipInfoModel mzim = new MediaZipInfoModel();
    HashMap agentMap = MediaDataZipArchive.createAgentMap(agentResults);
    mzim.setAgents(agentMap);
    mzim.setAssessmentName(form.getAssessmentName());
    mzim.setCourseName("course_assessment");
    mzim.setPart(form.getPartNumber());
    mzim.setQuestion(form.getQuestionNumber());
    mzim.setPublishedAssessmentId("62fa62fac74747c48d9e8827283");
    String zipFileName = "/tmp/" + Math.random() + ".zip";
    try
    {
      mzim.setOutputStream(new FileOutputStream(zipFileName));
    }
    catch (Exception ex)
    {
      LOG.error("Unable to create zip file output stream: " + ex);
      throw new RuntimeException(ex);
    }

    MediaDataZipArchive archive = new MediaDataZipArchive(mzim);
    archive.create(mzim.getAgents());

    String saveName = null;

    if (zipFileName != null)
    {
      File fileToShow = new File(zipFileName);
      saveName = fileToShow.getName();
    }

    // output zip file
    String fileType = "application/x-zip-compressed";
    res.setHeader(
        "Content-Disposition",
        fileType + "; filename=\"" + saveName + "\";");

    res.setContentType(fileType);

    ServletOutputStream outputStream = res.getOutputStream();
    FileInputStream fileStream = null;

    try
    {
      fileStream = new FileInputStream(zipFileName);
    }
    catch (FileNotFoundException e)
    {
      LOG.warn("File " + zipFileName + " not found:" + e.getMessage());
    }

    int i = 0;
    int count = 0;
    BufferedInputStream buf_inputStream = new BufferedInputStream(
        fileStream);
    BufferedOutputStream buf_outputStream =
        new BufferedOutputStream(outputStream);
    if (buf_inputStream != null)
    {
      while ( (i = buf_inputStream.read()) != -1)
      {
        buf_outputStream.write(i);
        count++;
      }
    }

    res.setContentLength(count);

    res.flushBuffer();

    buf_inputStream.close();

    buf_outputStream.close();

    try
    {
      File file = new File(zipFileName);
      if (file.delete())
        LOG.info("removed zip file");
    }
    catch (Exception e)
    {
      LOG.warn("cannot remove zip file " + zipFileName);
    }

  }

  /**
   * Persist the results from the ActionForm in the total page.
   * @param session current HttpSession
   */
  public void doSaveTotalScores(HttpServletRequest request)
  {
    HttpSession session = request.getSession();
    try
    {
      EvaluationServiceDelegate evalDelegate = new EvaluationServiceDelegate();
      RepositoryManager rManager = new RepositoryManager(request);
      Collection agents =
        ((TotalScoresForm) session.getAttribute("totalScores")).getAgents();
      Iterator iter = agents.iterator();
      while(iter.hasNext())
      {
        AgentResults agentResults = (AgentResults) iter.next();
        LOG.debug(
          "SAVE: \ntotal score=" + agentResults.getTotalScore() +
          "\nadjustment=" + agentResults.getAdjustmentTotalScore() +
          "\ncomments=" + agentResults.getTotalScoreComments() +
          "\nassessment id=" + agentResults.getAssessmentID() +
          "\nassessment result id=" + agentResults.getAssessmentResultID());

      // this service persists to relational records
      evalDelegate.changeAssessmentGrade(
        agentResults.getTotalScore(), agentResults.getAdjustmentTotalScore(),
        agentResults.getTotalScoreComments(), agentResults.getAssessmentID(),
        agentResults.getAssessmentResultID());

      // now, this service changes the XML to corespond
      Id aId = rManager.getId(agentResults.getAssessmentResultID());
      rManager.updateAssessmentResult(aId, agentResults.getTotalScore(),
        agentResults.getTotalScoreComments());
      }

      LOG.info("Saved total scores.");
    }
    catch(Exception e)
    {
      LOG.error(e);
      throw new Error(e);
    }
  }

  /**
   * Persist the results from the ActionForm in the individual question page.
   * @param session current HttpSession
   */
  public void doSaveQuestionScores(HttpServletRequest request)
  {
    HttpSession session = request.getSession();
    try
    {
      EvaluationServiceDelegate evalDelegate = new EvaluationServiceDelegate();
      RepositoryManager rManager = new RepositoryManager(request);
      Collection agents =
        ((QuestionScoresForm) session.getAttribute("questionScores")).getAgents();
      Iterator iter = agents.iterator();
      while(iter.hasNext())
      {
        AgentResults agentResults = (AgentResults) iter.next();

        // this service persists to relational records
        evalDelegate.changeItemGrade(
          agentResults.getTotalScore(), agentResults.getComments(),
          agentResults.getItemID(), agentResults.getAssessmentResultID());

        // now, this service changes the XML to corespond
        Id aId = rManager.getId(agentResults.getAssessmentResultID());
        Id iId = rManager.getId(agentResults.getItemID());
        rManager.updateItemResult(aId, iId,
          agentResults.getTotalScore(), agentResults.getTotalScoreComments());

        LOG.debug(
          "SAVE: \ntotal score=" + agentResults.getTotalScore() +
          "\ncomments=" + agentResults.getComments() + "\nitem id=" +
          agentResults.getItemID() + "\nassessment result id=" +
          agentResults.getAssessmentResultID());
      }

      LOG.info("Saved question scores.");
    }
    catch(Exception e)
    {
      LOG.error(e);
      throw new Error(e);
    }
  }

  /**
   * Get the answer text from the Item XML
   *
   * @todo convert to returning list of answers and correct answer removing UI
   * @param itemXml  the Item XML
   *
   * @return a String containing the answers
   */
  private String getAnswers(org.navigoproject.business.entity.Item itemXml)
  {
    String checkmark = "<image src=\"../images/checkmark.gif\">";
    String spacer =
      "<image src=\"../images/spacer.gif\" " + "width=\"14\" height=\"11\" />";

    String baseXPath =
      "item/presentation/flow/response_lid/render_choice/response_label";
    String respXPath = "/material/mattext";
    String respVarXPath = "/@ident";

    String respVarProcBaseXPath = "item/resprocessing/respcondition";
    String respVarCorrectXPath = "/conditionvar/varequal";
    String respVarFeedbackXPath = "/displayfeedback/@linkrefid";

    int respSize = 0;
    int correctRespSize = 0;
    String answersText = "";

    // calculate correct element's number
    String correctAnswerVar = "";

    List corr = itemXml.selectNodes(respVarProcBaseXPath);

    if((corr != null) && (corr.size() > 0))
    {
      correctRespSize = corr.size();
    }

    // find the number of the correct answer
    String sep = "";
    for(int i = 1; i <= correctRespSize; i++)
    {
      String index = ("[" + i) + "]";

      String possibleValue =
        itemXml.selectSingleValue(
          respVarProcBaseXPath + index + respVarFeedbackXPath, "attribute");

      LOG.info("possibleValue=" + possibleValue);
      if("Correct".equals(possibleValue))
      {
        correctAnswerVar += (
          sep +
          itemXml.selectSingleValue(
            respVarProcBaseXPath + index + respVarCorrectXPath, "element")
        );
        LOG.debug("correct!: " + i);
        sep = "|";
      }
    }

    LOG.info("correctAnswerVar=" + correctAnswerVar);

    // now get each response and compose String of answers
    boolean showVars = true;

    List resp = itemXml.selectNodes(baseXPath);

    if((resp != null) && (resp.size() > 0))
    {
      respSize = resp.size();
    }

    for(int i = 1; i <= respSize; i++)
    {
      String index = ("[" + i) + "]";
      String answerVar =
        itemXml.selectSingleValue(
          baseXPath + index + respVarXPath, "attribute");
      String answerItem =
        itemXml.selectSingleValue(baseXPath + index + respXPath, "element");

      boolean right = false;

      if(answerRight(correctAnswerVar, answerVar))
      {
        LOG.info("answerVar correct: " + answerVar);
        right = true;
      }

      if(answerItem == null)
      {
        answerItem = "";
      }

      if(answerItem.equals("True"))
      {
        showVars = false;
      }

      answersText += "<br />\n";

      if(right)
      {
        answersText += checkmark;
      }
      else
      {
        answersText += spacer;
      }

      answersText += (
        "<input disabled=\"true\" type=\"radio\" name=\"ANSWER\" value=\"" +
        answerVar + "\">"
      );
      if(showVars)
      {
        answersText += (answerVar + ".  ");
      }

      answersText += answerItem;
    }

    answersText += "<br />";

    //    LOG.info("answers=  " + answersText);
    return answersText;
  }

  /**
   * Utility
   * @param correctAnswerVar
   * @param answerVar
   * @return true if correct
   */
  private boolean answerRight(String correctAnswerVar, String answerVar)
  {
    StringTokenizer st = new StringTokenizer(correctAnswerVar, "|");
    while(st.hasMoreElements())
    {
      String correct = st.nextToken();
      if(correct.equals(answerVar))
      {
        return true; // match
      }
    }

    return false;
  }

  /**
   * Get the total points for the assessment
   *
   * @param the assessment id
   *
   * @return the points
   */
  private double getTotalPoints(String assessmentId, Calendar cal)
  {
    double points = 0;
    ArrayList sectionList = new ArrayList();
    ArrayList itemList = new ArrayList();
    AssessmentHelper assessmentHelper = new AssessmentHelper();
    sectionList = assessmentHelper.getSectionRefsByDate(assessmentId, cal);

    // we loop through each part and each question within each part
    for(int p = 0; p < sectionList.size(); p++)
    {
      String sectionId = (String) sectionList.get(p);
      SectionHelper sectionHelper = new SectionHelper();
      LOG.info("getSectionItems(  String" + sectionId + ", boolean true )");

      ArrayList items = sectionHelper.getSectionItems(sectionId, true);
      int questionCount = items.size();
      LOG.info("questions for " + p + ": " + questionCount);
      LOG.info("item " + items.get(0));

      // look up item in part
      for(int q = 0; q < questionCount; q++)
      {
        String itemId = items.get(q).toString();
        ItemHelper itemHelper = new ItemHelper();
        org.navigoproject.business.entity.Item item =
          itemHelper.getItemXml(itemId);
        double max = getMaxPoints(item);
        double maxVal = 0;
        try
        {
          maxVal = max;
        }
        catch(Exception ex)
        {
          // don't throw exception, just skip scores that can't be doubles
          // we are not supporting non-numeric socres at this time
          // this is for forward compatibility
        }

        points += maxVal;
      }
    }

    return points;
  }

  /**
   * Get the points text from the Item XML
   *
   * @param itemXml  the Item XML
   *
   * @return a String containing the points
   */
  private double getMaxPoints(org.navigoproject.business.entity.Item itemXml)
  {
    String baseXPath = "item/resprocessing/outcomes/decvar";
    double answerPoints = 0;
    int respSize = 0;

    List resp = itemXml.selectNodes(baseXPath);
    if((resp != null) && (resp.size() > 0))
    {
      respSize = resp.size();
    }
    else
    {
      return 0;
    }

    for(int i = 1; i <= respSize; i++)
    {
      String index = ("[" + i) + "]";
      String max =
        itemXml.selectSingleValue(
          baseXPath + index + "/@maxvalue", "attribute");
      double dmax = 0;
      try
      {
        dmax = Double.parseDouble(max);
      }
      catch(Exception ex)
      {
        // don't throw exception, just skip scores that can't be doubles
      }

      answerPoints += dmax;
    }

    return answerPoints;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param n a double value
   *
   * @return a string value
   */
  private String castingNum(double n)
  {
    if(Math.ceil(n) == Math.floor(n))
    {
      return ("" + (int) n);
    }
    else
    {
      return "" + n;
    }
  }
}
