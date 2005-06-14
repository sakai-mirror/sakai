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

import org.navigoproject.business.entity.AssessmentTemplate;
import org.navigoproject.business.entity.assessment.model.AssessmentPropertiesImpl;
import org.navigoproject.business.entity.assessment.model.DistributionGroup;
import org.navigoproject.business.entity.assessment.model.FeedbackModel;
import org.navigoproject.business.entity.assessment.model.ItemTemplateImpl;
import org.navigoproject.business.entity.assessment.model.ItemTemplatePropertiesImpl;
import org.navigoproject.business.entity.assessment.model.ScoringModel;
import org.navigoproject.business.entity.assessment.model.SectionIteratorImpl;
import org.navigoproject.business.entity.assessment.model.SectionTemplateImpl;
import org.navigoproject.business.entity.assessment.model.SectionTemplatePropertiesImpl;
import org.navigoproject.business.entity.assessment.model.SubmissionModel;
import org.navigoproject.business.entity.properties.AssessmentProperties;
import org.navigoproject.business.entity.properties.AssessmentTemplateProperties;
import org.navigoproject.osid.assessment.AssessmentServiceDelegate;
import org.navigoproject.osid.shared.impl.TypeImpl;
import org.navigoproject.ui.web.form.edit.AccessForm;
import org.navigoproject.ui.web.form.edit.DescriptionForm;
import org.navigoproject.ui.web.form.edit.DisplayForm;
import org.navigoproject.ui.web.form.edit.DistributionForm;
import org.navigoproject.ui.web.form.edit.EvaluationForm;
import org.navigoproject.ui.web.form.edit.FeedbackForm;
import org.navigoproject.ui.web.form.edit.NotificationForm;
import org.navigoproject.ui.web.form.edit.PartForm;
import org.navigoproject.ui.web.form.edit.QuestionForm;
import org.navigoproject.ui.web.form.edit.SubmissionsForm;

import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import osid.assessment.Assessment;
import osid.assessment.ItemIterator;
import osid.assessment.SectionIterator;

/**
 * This class holds all the methods that read in information from the UI forms
 * and put it into the session template/assessment.   They are referenced by a
 * number of Action classes, so they're held separately (and hopefully
 * readably) here.  Note that only the template forms of QuestionForm and
 * PartForm (each of which has two forms, unlike the others) are handled here.
 *
 * @author <a href="mailto:rgollub@stanford.edu">Rachel Gollub</a>
 * @author <a href="mailto:huong.t.nguyen@stanford.edu">Huong Nguyen</a>
 * @author <a href="mailto:esmiley@stanford.edu">Ed Smiley</a>
 */
public class ReadForms
  extends AAMAction
{
  private Assessment template;
  private AssessmentProperties properties;
  private AssessmentTemplateProperties props;
  private HttpSession session;
  private boolean isTemplate;
  private static final Logger LOG = Logger.getLogger(ReadForms.class.getName());

  /**
   * This just sets the relevant variables.
   *
   * @param ptemplate DOCUMENTATION PENDING
   * @param pproperties DOCUMENTATION PENDING
   * @param pprops DOCUMENTATION PENDING
   * @param psession DOCUMENTATION PENDING
   * @param pisTemplate DOCUMENTATION PENDING
   */
  public ReadForms(
    Assessment ptemplate, AssessmentProperties pproperties,
    AssessmentTemplateProperties pprops, HttpSession psession,
    boolean pisTemplate)
  {
    template = ptemplate;
    properties = pproperties;
    props = pprops;
    session = psession;
    isTemplate = pisTemplate;
  }

  /**
   * A no-op -- this is not called as an action right now.
   *
   * @param actionMapping DOCUMENTATION PENDING
   * @param actionForm DOCUMENTATION PENDING
   * @param httpServletRequest DOCUMENTATION PENDING
   * @param httpServletResponse DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ActionForward execute(
    ActionMapping actionMapping, ActionForm actionForm,
    HttpServletRequest httpServletRequest,
    HttpServletResponse httpServletResponse)
  {
    LOG.debug("action mapping: " + actionMapping.getPath());

    return null;

    // This may do something useful later.  -rmg
  }

  /**
   * Loads the access form groups into the session assessment/template.
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void doAccessFormGroups()
    throws Exception
  {
    AccessForm form = (AccessForm) session.getAttribute("access");

    if(isTemplate)
    {
      props.setInstructorViewable(
        "groups", form.getGroups_isInstructorViewable());
      props.setInstructorEditable(
        "groups", form.getGroups_isInstructorEditable());
      props.setStudentViewable("groups", form.getGroups_isStudentViewable());
    }

    properties.setAccessGroups(form.getGroups());
  }

  /**
   * Loads the access form information into the session assessment/template.
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void doAccessForm()
    throws Exception
  {
    AccessForm form = null;
    form = (AccessForm) session.getAttribute("access");

    if(isTemplate)
    {
      props.setInstructorViewable(
        "releaseType", form.getReleaseType_isInstructorViewable());
      props.setInstructorEditable(
        "releaseType", form.getReleaseType_isInstructorEditable());
      props.setStudentViewable(
        "releaseType", form.getReleaseType_isStudentViewable());

      props.setInstructorViewable(
        "releaseDate", form.getReleaseDate_isInstructorViewable());
      props.setInstructorEditable(
        "releaseDate", form.getReleaseDate_isInstructorEditable());
      props.setStudentViewable(
        "releaseDate", form.getReleaseDate_isStudentViewable());

      props.setInstructorViewable(
        "retractType", form.getRetractType_isInstructorViewable());
      props.setInstructorEditable(
        "retractType", form.getRetractType_isInstructorEditable());
      props.setStudentViewable(
        "retractType", form.getRetractType_isStudentViewable());

      props.setInstructorViewable(
        "retractDate", form.getRetractDate_isInstructorViewable());
      props.setInstructorEditable(
        "retractDate", form.getRetractDate_isInstructorEditable());
      props.setStudentViewable(
        "retractDate", form.getRetractDate_isStudentViewable());

      props.setInstructorViewable(
        "dueDate", form.getDueDate_isInstructorViewable());
      props.setInstructorEditable(
        "dueDate", form.getDueDate_isInstructorEditable());
      props.setStudentViewable("dueDate", form.getDueDate_isStudentViewable());

      props.setInstructorViewable(
        "retryAllowed", form.getRetryAllowed_isInstructorViewable());
      props.setInstructorEditable(
        "retryAllowed", form.getRetryAllowed_isInstructorEditable());
      props.setStudentViewable(
        "retryAllowed", form.getRetryAllowed_isStudentViewable());

      props.setInstructorViewable(
        "timedAssessment", form.getTimedAssessment_isInstructorViewable());
      props.setInstructorEditable(
        "timedAssessment", form.getTimedAssessment_isInstructorEditable());
      props.setStudentViewable(
        "timedAssessment", form.getTimedAssessment_isStudentViewable());

      props.setInstructorViewable(
        "passwordRequired", form.getPasswordRequired_isInstructorViewable());
      props.setInstructorEditable(
        "passwordRequired", form.getPasswordRequired_isInstructorEditable());
      props.setStudentViewable(
        "passwordRequired", form.getPasswordRequired_isStudentViewable());

      props.setInstructorViewable(
        "ipAccessType", form.getIpAccessType_isInstructorViewable());
      props.setInstructorEditable(
        "ipAccessType", form.getIpAccessType_isInstructorEditable());
      props.setStudentViewable(
        "ipAccessType", form.getIpAccessType_isStudentViewable());
    }

    properties.setAccessGroups(form.getGroups());
  }

  /**
   * Loads the description form information into the session
   * assessment/template.
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void doDescriptionForm()
    throws Exception
  {
    boolean isSubmit = true;
    DescriptionForm form = null;
    form = (DescriptionForm) session.getAttribute("description");

    /////////////////////////////
    // Ganapathy's changes
    ////////////////////////////
    // If 'New File No Link' has been clicked..
    //        if((httpServletRequest.getParameter("NewFileNoLink.x")!=null)||(httpServletRequest.getParameter("NewFileNoLink.y")!=null))
    //    {
    //	isSubmit=false;
    //        session.setAttribute("mediaSource", "description");
    //        session.setAttribute("isLink", "false");
    //        session.setAttribute("getMedia", null);
    //        session.setAttribute("fileUpload", null);
    //
    //        forward = actionMapping.findForward("startFileUpload");
    //    }
    //
    //	// If 'New File Link' has been clicked..
    //        if((httpServletRequest.getParameter("NewFileLink.x")!=null)||(httpServletRequest.getParameter("NewFileLink.y")!=null))
    //    {
    //	isSubmit=false;
    //        session.setAttribute("mediaSource", "description");
    //        session.setAttribute("isLink", "true");
    //        session.setAttribute("getMedia", null);
    //        session.setAttribute("fileUpload", null);
    //
    //        forward = actionMapping.findForward("startFileUpload");
    //    }
    //        // If 'Edit File No Link' has been clicked..
    //        int mcCnt=-1; int mcIndex=-1;
    //        if(form.getMediaCollection() !=null)
    //                mcCnt = (form.getMediaCollection()).size() ;
    //        for(int i=0;i<mcCnt;i++)
    //                if((httpServletRequest.getParameter("EditFileNoLink"+i+".x")!=null)||(httpServletRequest.getParameter("EditFileNoLink"+i+".y")!=null))
    //                        mcIndex=i;
    //        LOG.debug("mcIndex"+mcIndex);
    //        if(mcIndex > -1){
    //	isSubmit=false;
    //        session.setAttribute("mediaSource", "description");
    //        session.setAttribute("isLink", "false");
    //        populateFileUpload(session, "description", (new Integer(mcIndex)).toString());
    //        forward = actionMapping.findForward("startFileUpload");
    //        }
    //        // If 'Edit File Link' has been clicked..
    //        int rmcCnt=-1; int rmcIndex=-1;
    //        if(form.getRelatedMediaCollection() !=null)
    //                rmcCnt = (form.getRelatedMediaCollection()).size() ;
    //        for(int i=0;i<rmcCnt;i++)
    //                if((httpServletRequest.getParameter("EditFileLink"+i+".x")!=null)||(httpServletRequest.getParameter("EditFileLink"+i+".y")!=null))
    //                        rmcIndex=i;
    //        LOG.debug("rmcIndex"+rmcIndex);
    //        if(rmcIndex > -1){
    //	isSubmit=false;
    //        session.setAttribute("mediaSource", "description");
    //        session.setAttribute("isLink", "true");
    //        populateFileUpload(session, "description", (new Integer(rmcIndex)).toString());
    //        forward = actionMapping.findForward("startFileUpload");
    //        }
    /////////////////////////////
    // Ganapathys changes end here
    //////////////////////////////
    template.updateDisplayName(form.getName());
    template.updateDescription(form.getDescription());

    if(isTemplate)
    {
      ((AssessmentTemplate) template).setTemplateName(form.getTemplateName());
      ((AssessmentTemplate) template).setComments(
        form.getTemplateDescription());
    }

    properties.setObjectives(form.getObjectives());
    properties.setKeywords(form.getKeywords());
    properties.setRubrics(form.getRubrics());
    properties.setMediaCollection(new ArrayList());
    properties.setType(
      new TypeImpl("Stanford", "AAM", "assessment", form.getAssessmentType()));
    Iterator iter = form.getMediaCollection().iterator();
    while(iter.hasNext())
    {
      properties.addMedia(iter.next());
    }

    iter = form.getRelatedMediaCollection().iterator();
    while(iter.hasNext())
    {
      properties.addMedia(iter.next());
    }

    if(isTemplate)
    {
      props.setInstructorViewable(
        "templateName", form.getTemplateName_isInstructorViewable());
      props.setInstructorViewable(
        "comments", form.getTemplateDescription_isInstructorViewable());
      props.setInstructorViewable("name", form.getName_isInstructorViewable());
      props.setInstructorViewable(
        "description", form.getDescription_isInstructorViewable());
      props.setInstructorViewable(
        "type", form.getAssessmentType_isInstructorViewable());
      props.setInstructorViewable(
        "objectives", form.getObjectives_isInstructorViewable());
      props.setInstructorViewable(
        "keywords", form.getKeywords_isInstructorViewable());
      props.setInstructorViewable(
        "rubrics", form.getRubrics_isInstructorViewable());
      props.setInstructorViewable(
        "mediaCollection", form.getMediaCollection_isInstructorViewable());
      props.setInstructorViewable(
        "relatedMediaCollection",
        form.getRelatedMediaCollection_isInstructorViewable());

      props.setInstructorEditable(
        "templateName", form.getTemplateName_isInstructorEditable());
      props.setInstructorEditable(
        "comments", form.getTemplateDescription_isInstructorEditable());
      props.setInstructorEditable("name", form.getName_isInstructorEditable());
      props.setInstructorEditable(
        "description", form.getDescription_isInstructorEditable());
      props.setInstructorEditable(
        "type", form.getAssessmentType_isInstructorEditable());
      props.setInstructorEditable(
        "objectives", form.getObjectives_isInstructorEditable());
      props.setInstructorEditable(
        "keywords", form.getKeywords_isInstructorEditable());
      props.setInstructorEditable(
        "rubrics", form.getRubrics_isInstructorEditable());
      props.setInstructorEditable(
        "mediaCollection", form.getMediaCollection_isInstructorEditable());
      props.setInstructorEditable(
        "relatedMediaCollection",
        form.getRelatedMediaCollection_isInstructorEditable());

      props.setStudentViewable(
        "templateName", form.getTemplateName_isStudentViewable());
      props.setStudentViewable(
        "comments", form.getTemplateDescription_isStudentViewable());
      props.setStudentViewable("name", form.getName_isStudentViewable());
      props.setStudentViewable(
        "description", form.getDescription_isStudentViewable());
      props.setStudentViewable(
        "type", form.getAssessmentType_isStudentViewable());
      props.setStudentViewable(
        "objectives", form.getObjectives_isStudentViewable());
      props.setStudentViewable(
        "keywords", form.getKeywords_isStudentViewable());
      props.setStudentViewable("rubrics", form.getRubrics_isStudentViewable());
      props.setStudentViewable(
        "mediaCollection", form.getMediaCollection_isStudentViewable());
      props.setStudentViewable(
        "relatedMediaCollection",
        form.getRelatedMediaCollection_isStudentViewable());
    }

    /////////////////////////
    // Ganapathys changes
    ////////////////////////
    // Checking if a "Delete Non Link File" has been clicked
    //        for(int i=0;i<mcCnt;i++)
    //                if((httpServletRequest.getParameter("DeleteFileNoLink"+i+".x")!=null)||(httpServletRequest.getParameter("DeleteFileNoLink"+i+".y")!=null))
    //                        mcIndex=i;
    //	if(mcIndex > -1){
    // try
    //      {
    //        boolean isLink = false;
    //        DescriptionForm dform = (DescriptionForm) session.getAttribute("description");
    //        int index = mcIndex;
    //        Collection media = null;
    //        if (isLink)
    //          media = dform.getRelatedMediaCollection();
    //        else
    //          media = dform.getMediaCollection();
    //        Iterator diter = media.iterator();
    //        for (int i=0; i<index; i++)
    //          diter.next();
    //        MediaData data = (MediaData) diter.next();
    //        media.remove(data);
    //        if (data.getId() > 0)
    //        {
    //          AssessmentServiceDelegate delegate = new AssessmentServiceDelegate();
    //          delegate.deleteMedia(data.getId());
    //        }
    //        if (isLink)
    //          dform.setRelatedMediaCollection(media);
    //        else
    //          dform.setMediaCollection(media);
    //      } catch (Exception e) {
    //        LOG.error(e); throw new Error(e);
    //      }
    //
    //      forward = actionMapping.findForward("description");
    //	}
    //
    ///////////////////////////
    // Ganapathy's changes end
    /////////////////////////////
  }

  /**
   * Loads the display form information into the session assessment/template.
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void doDisplayForm()
    throws Exception
  {
    DisplayForm form = null;
    form = (DisplayForm) session.getAttribute("display");

    properties.setItemAccessType(form.getItemAccessType());
    properties.setItemBookmarking(form.getItemBookMarking());
    properties.setDisplayChunking(form.getDisplayChunking());
    properties.setMultiPartAllowed(form.getMultiPartAllowed());

    if(isTemplate)
    {
      props.setInstructorViewable(
        "itemAccessType", form.getItemAccessType_isInstructorViewable());
      props.setInstructorViewable(
        "itemBookmarking", form.getItemBookMarking_isInstructorViewable());
      props.setInstructorViewable(
        "displayChunking", form.getDisplayChunking_isInstructorViewable());
      props.setInstructorViewable(
        "multiPartAllowed", form.getMultiPartAllowed_isInstructorViewable());

      props.setInstructorEditable(
        "itemAccessType", form.getItemAccessType_isInstructorEditable());
      props.setInstructorEditable(
        "itemBookmarking", form.getItemBookMarking_isInstructorEditable());
      props.setInstructorEditable(
        "displayChunking", form.getDisplayChunking_isInstructorEditable());
      props.setInstructorEditable(
        "multiPartAllowed", form.getMultiPartAllowed_isInstructorEditable());

      props.setStudentViewable(
        "itemAccessType", form.getItemAccessType_isStudentViewable());
      props.setStudentViewable(
        "itemBookmarking", form.getItemBookMarking_isStudentViewable());
      props.setStudentViewable(
        "displayChunking", form.getDisplayChunking_isStudentViewable());
      props.setStudentViewable(
        "multiPartAllowed", form.getMultiPartAllowed_isStudentViewable());
    }
  }

  /**
   * Loads the feedback form information into the session assessment/template.
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void doFeedbackForm()
    throws Exception
  {
    FeedbackForm form = null;
    form = (FeedbackForm) session.getAttribute("feedback");

    FeedbackModel model = new FeedbackModel();

    /**
     * todo: validation/template creation should be handling feedback problem!
     */
    String fbt = "2"; //defaults: no feedback

    /**
     * todo: validation/template creation should be handling feedback problem!
     */
    String ifbt = "2"; //defaults: no feedback

    /**
     * todo: validation/template creation should be handling feedback problem!
     */
    String dfbt = "2"; //defaults: no feedback

    /**
     * todo: validation/template creation should be handling feedback problem!
     */
    String pqfbt = "2"; //defaults: no feedback

    try
    {
      fbt = form.getFeedbackType();
      ifbt =
        form.getImmediateFeedbackType_q() + ":" +
        form.getImmediateFeedbackType_p() + ":" +
        form.getImmediateFeedbackType_a();
      dfbt = form.getDatedFeedbackType();
      pqfbt = form.getPerQuestionFeedbackType();
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);

      // default
    }

    model.setFeedbackType(fbt);
    model.setImmediateFeedbackType(ifbt);
    model.setDatedFeedbackType(dfbt);
    model.setPerQuestionFeedbackType(pqfbt);
    model.setFeedbackDate(form.getFeedbackReleaseDate());
    model.setScoreDate(form.getTotalScoreReleaseDate());

    ((AssessmentPropertiesImpl) properties).setFeedbackModel(model);

    if(isTemplate)
    {
      props.setInstructorViewable(
        "feedbackType", form.getFeedbackType_isInstructorViewable());

      props.setInstructorEditable(
        "feedbackType", form.getFeedbackType_isInstructorEditable());

      props.setStudentViewable(
        "feedbackType", form.getFeedbackType_isStudentViewable());
    }
  }

  /**
   * Loads the submissions form information into the session
   * assessment/template.
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void doSubmissionsForm()
    throws Exception
  {
    LOG.debug("Entering doSubmissionsForm()");
    SubmissionsForm form = null;
    form = (SubmissionsForm) session.getAttribute("submissions");

    properties.setLateHandling(form.getLateHandling());
    SubmissionModel model = (SubmissionModel) properties.getSubmissionModel();
    if(model == null)
    {
      model = new SubmissionModel();
    }

    model.setNumberSubmissions(form.getSubmissionModel());
    int numberSubs = 0;
    try
    {
      numberSubs = new Integer(form.getSubmissionNumber()).intValue();
    }
    catch(Exception e)
    {
      LOG.warn("Not a number.");
    }

    model.setSubmissionsAllowed(numberSubs);
    properties.setSubmissionModel(model);
    properties.setSubmissionsSaved(
      form.getSubmissionsSaved1() + ":" + form.getSubmissionsSaved2() + ":" +
      form.getSubmissionsSaved3());

    if(isTemplate)
    {
      props.setInstructorViewable(
        "lateHandling", form.getLateHandling_isInstructorViewable());
      props.setInstructorViewable(
        "submissionModel", form.getSubmissionModel_isInstructorViewable());
      props.setInstructorViewable(
        "submissionsSaved", form.getSubmissionsSaved_isInstructorViewable());

      props.setInstructorEditable(
        "lateHandling", form.getLateHandling_isInstructorEditable());
      props.setInstructorEditable(
        "submissionModel", form.getSubmissionModel_isInstructorEditable());
      props.setInstructorEditable(
        "submissionsSaved", form.getSubmissionsSaved_isInstructorEditable());

      props.setStudentViewable(
        "lateHandling", form.getLateHandling_isStudentViewable());
      props.setStudentViewable(
        "submissionModel", form.getSubmissionModel_isStudentViewable());
      props.setStudentViewable(
        "submissionsSaved", form.getSubmissionsSaved_isStudentViewable());
    }
  }

  /**
   * Loads the evaluation form information into the session
   * assessment/template.
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void doEvaluationForm()
    throws Exception
  {
    EvaluationForm form = null;
    form = (EvaluationForm) session.getAttribute("evaluation");

    properties.setEvaluationDistribution(
      form.getEvaluationDistribution_toInstructor() + ":" +
      form.getEvaluationDistribution_toTAs() + ":" +
      form.getEvaluationDistribution_toSectionGrader() + ":" +
      form.getEvaluationDistribution_toReviewGroup() + ":" +
      form.getEvaluationDistribution_toTestee());
    properties.setTesteeIdentity(form.getTesteeIdentity());
    properties.setEvaluationComponents(
      form.getEvaluationComponents_scores() + ":" +
      form.getEvaluationComponents_commentsForQuestions() + ":" +
      form.getEvaluationComponents_commentsForParts() + ":" +
      form.getEvaluationComponents_commentForAssess());
    properties.setAutoScoring(form.getAutoScoring());
    ScoringModel model = new ScoringModel();
    model.setScoringType(form.getScoringType());
    model.setNumericModel(form.getNumericModel());
    try
    {
      model.setDefaultQuestionValue(
        Integer.parseInt(form.getDefaultQuestionValue()));
    }
    catch(Exception e)
    {
      LOG.warn("DQV not a number.");
    }

    try
    {
      model.setFixedTotalScore(Integer.parseInt(form.getFixedTotalScore()));
    }
    catch(Exception e)
    {
      LOG.warn("FTS not a number.");
    }

    properties.setScoringModel(model);

    if(isTemplate)
    {
      props.setInstructorViewable(
        "evaluationDistribution",
        form.getEvaluationDistribution_isInstructorViewable());
      props.setInstructorViewable(
        "testeeIdentity", form.getTesteeIdentity_isInstructorViewable());
      props.setInstructorViewable(
        "evaluationComponents",
        form.getEvaluationComponents_isInstructorViewable());
      props.setInstructorViewable(
        "autoScoring", form.getAutoScoring_isInstructorViewable());
      props.setInstructorViewable(
        "scoringType", form.getScoringType_isInstructorViewable());
      props.setInstructorViewable(
        "numericModel", form.getNumericModel_isInstructorViewable());

      props.setInstructorEditable(
        "evaluationDistribution",
        form.getEvaluationDistribution_isInstructorEditable());
      props.setInstructorEditable(
        "testeeIdentity", form.getTesteeIdentity_isInstructorEditable());
      props.setInstructorEditable(
        "evaluationComponents",
        form.getEvaluationComponents_isInstructorEditable());
      props.setInstructorEditable(
        "autoScoring", form.getAutoScoring_isInstructorEditable());
      props.setInstructorEditable(
        "scoringType", form.getScoringType_isInstructorEditable());
      props.setInstructorEditable(
        "numericModel", form.getNumericModel_isInstructorEditable());

      props.setStudentViewable(
        "evaluationDistribution",
        form.getEvaluationDistribution_isStudentViewable());
      props.setStudentViewable(
        "testeeIdentity", form.getTesteeIdentity_isStudentViewable());
      props.setStudentViewable(
        "evaluationComponents", form.getEvaluationComponents_isStudentViewable());
      props.setStudentViewable(
        "autoScoring", form.getAutoScoring_isStudentViewable());
      props.setStudentViewable(
        "scoringType", form.getScoringType_isStudentViewable());
      props.setStudentViewable(
        "numericModel", form.getNumericModel_isStudentViewable());
    }
  }

  /**
   * Loads the distribution form information into the session
   * assessment/template.
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void doDistributionForm()
    throws Exception
  {
    DistributionForm form = null;
    form = (DistributionForm) session.getAttribute("distribution");

    DistributionGroup group = new DistributionGroup();
    group.setDistributionGroup("admin");
    group.addDistributionType(form.getToAdmin1());
    group.addDistributionType(form.getToAdmin2());
    group.addDistributionType(form.getToAdmin3());
    properties.addDistributionGroup(group);

    group = new DistributionGroup();
    group.setDistributionGroup("testee");
    group.addDistributionType(form.getToTestee1());
    group.addDistributionType(form.getToTestee2());
    group.addDistributionType(form.getToTestee3());
    group.addDistributionType(form.getToTestee4());
    properties.addDistributionGroup(group);

    group = new DistributionGroup();
    group.setDistributionGroup("gradebook");
    group.addDistributionType(form.getToGradebook());
    properties.addDistributionGroup(group);

    if(isTemplate)
    {
      props.setInstructorViewable(
        "toAdmin", form.getToAdmin_isInstructorViewable());
      props.setInstructorViewable(
        "toTestee", form.getToTestee_isInstructorViewable());
      props.setInstructorViewable(
        "toGradebook", form.getToGradebook_isInstructorViewable());

      props.setInstructorEditable(
        "toAdmin", form.getToAdmin_isInstructorEditable());
      props.setInstructorEditable(
        "toTestee", form.getToTestee_isInstructorEditable());
      props.setInstructorEditable(
        "toGradebook", form.getToGradebook_isInstructorEditable());

      props.setStudentViewable("toAdmin", form.getToAdmin_isStudentViewable());
      props.setStudentViewable(
        "toTestee", form.getToTestee_isStudentViewable());
      props.setStudentViewable(
        "toGradebook", form.getToGradebook_isStudentViewable());
    }
  }

  /**
   * Loads the notification form information into the session
   * assessment/template.
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void doNotificationForm()
    throws Exception
  {
    NotificationForm form = null;
    form = (NotificationForm) session.getAttribute("notification");

    properties.setTesteeNotification(
      form.getTesteeNotification1() + ":" + form.getTesteeNotification2() +
      ":" + form.getTesteeNotification3() + ":" +
      form.getTesteeNotification4());
    properties.setInstructorNotification(
      form.getInstructorNotification1() + ":" +
      form.getInstructorNotification2() + ":" +
      form.getInstructorNotification3() + ":" +
      form.getInstructorNotification4() + ":" +
      form.getInstructorNotification5());

    if(isTemplate)
    {
      props.setInstructorViewable(
        "testeeNotification", form.getTesteeNotification_isInstructorViewable());
      props.setInstructorViewable(
        "instructorNotification",
        form.getInstructorNotification_isInstructorViewable());

      props.setInstructorEditable(
        "testeeNotification", form.getTesteeNotification_isInstructorEditable());
      props.setInstructorEditable(
        "instructorNotification",
        form.getInstructorNotification_isInstructorEditable());

      props.setStudentViewable(
        "testeeNotification", form.getTesteeNotification_isStudentViewable());
      props.setStudentViewable(
        "instructorNotification",
        form.getInstructorNotification_isStudentViewable());
    }
  }

  /**
   * Loads the part form information into the session template.
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void doPartForm()
    throws Exception
  {
    PartForm form = null;
    form = (PartForm) session.getAttribute("part");

    SectionTemplateImpl stemplate = null;
    SectionIteratorImpl sii = (SectionIteratorImpl) template.getSections();
    if(sii.hasNext())
    {
      stemplate = (SectionTemplateImpl) sii.next();
    }
    else
    {
      AssessmentServiceDelegate delegate = new AssessmentServiceDelegate();
      stemplate =
        (SectionTemplateImpl) delegate.createSectionTemplate(
          form.getName(), form.getDescription(), null);
      template.addSection(stemplate);
    }

    SectionTemplatePropertiesImpl sprops =
      (SectionTemplatePropertiesImpl) stemplate.getData();
    if(sprops == null)
    {
      sprops = new SectionTemplatePropertiesImpl();
    }

    stemplate.updateDisplayName(form.getName());
    stemplate.updateDescription(form.getDescription());
    sprops.setKeywords(form.getKeywords());
    sprops.setSectionType(
      new TypeImpl("Stanford", "AAM", "assessment", form.getType()));
    sprops.setObjectives(form.getObjectives());
    sprops.setRubrics(form.getRubrics());
    sprops.setItemOrder(form.getQuestionOrder());
    sprops.setMediaCollection(new ArrayList());
    Iterator iter = form.getMediaCollection().iterator();
    while(iter.hasNext())
    {
      sprops.addMedia(iter.next());
    }

    iter = form.getRelatedMediaCollection().iterator();
    while(iter.hasNext())
    {
      sprops.addMedia(iter.next());
    }

    sprops.setInstructorViewable("name", form.getName_isInstructorViewable());
    sprops.setInstructorViewable("type", form.getType_isInstructorViewable());
    sprops.setInstructorViewable(
      "description", form.getDescription_isInstructorEditable());
    sprops.setInstructorViewable(
      "keywords", form.getKeywords_isInstructorViewable());
    sprops.setInstructorViewable(
      "objectives", form.getObjectives_isInstructorEditable());
    sprops.setInstructorViewable(
      "rubrics", form.getRubrics_isInstructorViewable());
    sprops.setInstructorViewable(
      "questionOrder", form.getQuestionOrder_isInstructorViewable());
    sprops.setInstructorViewable(
      "mediaCollection", form.getMediaCollection_isInstructorViewable());
    sprops.setInstructorViewable(
      "relatedMediaCollection",
      form.getRelatedMediaCollection_isInstructorViewable());

    sprops.setInstructorEditable("name", form.getName_isInstructorEditable());
    sprops.setInstructorEditable("type", form.getType_isInstructorEditable());
    sprops.setInstructorEditable(
      "description", form.getDescription_isInstructorEditable());
    sprops.setInstructorEditable(
      "keywords", form.getKeywords_isInstructorEditable());
    sprops.setInstructorEditable(
      "objectives", form.getObjectives_isInstructorEditable());
    sprops.setInstructorEditable(
      "rubrics", form.getRubrics_isInstructorEditable());
    sprops.setInstructorEditable(
      "questionOrder", form.getQuestionOrder_isInstructorEditable());
    sprops.setInstructorEditable(
      "mediaCollection", form.getMediaCollection_isInstructorEditable());
    sprops.setInstructorEditable(
      "relatedMediaCollection",
      form.getRelatedMediaCollection_isInstructorEditable());

    sprops.setStudentViewable("name", form.getName_isStudentViewable());
    sprops.setStudentViewable("type", form.getType_isStudentViewable());
    sprops.setStudentViewable(
      "description", form.getDescription_isStudentViewable());
    sprops.setStudentViewable("keywords", form.getKeywords_isStudentViewable());
    sprops.setStudentViewable(
      "objectives", form.getObjectives_isStudentViewable());
    sprops.setStudentViewable("rubrics", form.getRubrics_isStudentViewable());
    sprops.setStudentViewable(
      "questionOrder", form.getQuestionOrder_isStudentViewable());
    sprops.setStudentViewable(
      "mediaCollection", form.getMediaCollection_isStudentViewable());
    sprops.setStudentViewable(
      "relatedMediaCollection",
      form.getRelatedMediaCollection_isStudentViewable());

    stemplate.updateData(sprops);
  }

  /**
   * Loads the access form information into the session template.
   *
   * @throws Exception DOCUMENTATION PENDING
   */
  public void doQuestionForm()
    throws Exception
  {
    QuestionForm form = null;
    form = (QuestionForm) session.getAttribute("question");

    // Find the section this item belongs to
    AssessmentServiceDelegate delegate = new AssessmentServiceDelegate();
    SectionIterator siter = template.getSections();
    SectionTemplateImpl stemplate = null;
    SectionTemplatePropertiesImpl sprops = null;
    if(! siter.hasNext())
    {
      // If template has no sections, create one.
      stemplate =
        (SectionTemplateImpl) delegate.createSectionTemplate(
          "Default Section", "Default Section", null);
      template.addSection(stemplate);
    }
    else
    {
      stemplate = (SectionTemplateImpl) siter.next();
    }

    if(stemplate.getData() != null)
    {
      sprops = (SectionTemplatePropertiesImpl) stemplate.getData();
    }
    else
    {
      sprops = new SectionTemplatePropertiesImpl();
    }

    // Find the item if it already exists
    ItemIterator iiter = stemplate.getItems();
    ItemTemplateImpl itemplate = null;
    ItemTemplatePropertiesImpl iprops = null;
    if(! iiter.hasNext())
    {
      itemplate =
        (ItemTemplateImpl) delegate.createItemTemplate(
          form.getName(), "Description", null);
      iprops = new ItemTemplatePropertiesImpl();
    }
    else
    {
      itemplate = (ItemTemplateImpl) iiter.next();
      iprops = (ItemTemplatePropertiesImpl) itemplate.getData();
    }

    iprops.setText(form.getText());
    iprops.setHint(form.getHint());
    iprops.setValue(form.getValue());
    iprops.setObjectives(form.getObjectives());
    iprops.setKeywords(form.getKeywords());
    iprops.setRubrics(form.getRubrics());
    iprops.setFeedback(form.getFeedback());
    iprops.setPageBreak(form.getPageBreak());
    iprops.setMediaCollection(new ArrayList());
    Iterator iter = form.getMediaCollection().iterator();
    while(iter.hasNext())
    {
      iprops.addMedia(iter.next());
    }

    iter = form.getRelatedMediaCollection().iterator();
    while(iter.hasNext())
    {
      iprops.addMedia(iter.next());
    }

    iprops.setInstructorViewable("name", form.getName_isInstructorViewable());
    iprops.setInstructorViewable("text", form.getText_isInstructorViewable());
    iprops.setInstructorViewable("hint", form.getHint_isInstructorViewable());
    iprops.setInstructorViewable("value", form.getValue_isInstructorViewable());
    iprops.setInstructorViewable(
      "objectives", form.getObjectives_isInstructorViewable());
    iprops.setInstructorViewable(
      "keywords", form.getKeywords_isInstructorViewable());
    iprops.setInstructorViewable(
      "rubrics", form.getRubrics_isInstructorViewable());
    iprops.setInstructorViewable(
      "feedback", form.getFeedback_isInstructorViewable());
    iprops.setInstructorViewable(
      "pagebreak", form.getPageBreak_isInstructorViewable());

    ///////////////////////////
    // Huong's changes
    ///////////////////////////
    iprops.setInstructorViewable(
      "afeedback", form.getAfeedback_isInstructorViewable());
    iprops.setInstructorEditable(
      "afeedback", form.getAfeedback_isInstructorEditable());
    iprops.setStudentViewable(
      "afeedback", form.getAfeedback_isStudentViewable());
    iprops.setInstructorEditable("name", form.getName_isInstructorEditable());
    iprops.setInstructorEditable("text", form.getText_isInstructorEditable());
    iprops.setInstructorEditable("hint", form.getHint_isInstructorEditable());
    iprops.setInstructorEditable("value", form.getValue_isInstructorEditable());
    iprops.setInstructorEditable(
      "objectives", form.getObjectives_isInstructorEditable());
    iprops.setInstructorEditable(
      "keywords", form.getKeywords_isInstructorEditable());
    iprops.setInstructorEditable(
      "rubrics", form.getRubrics_isInstructorEditable());
    iprops.setInstructorEditable(
      "feedback", form.getFeedback_isInstructorEditable());
    iprops.setInstructorEditable(
      "pagebreak", form.getPageBreak_isInstructorEditable());
    iprops.setStudentViewable("name", form.getName_isStudentViewable());
    iprops.setStudentViewable("text", form.getText_isStudentViewable());
    iprops.setStudentViewable("hint", form.getHint_isStudentViewable());
    iprops.setStudentViewable("value", form.getValue_isStudentViewable());
    iprops.setStudentViewable(
      "objectives", form.getObjectives_isStudentViewable());
    iprops.setStudentViewable("keywords", form.getKeywords_isStudentViewable());
    iprops.setStudentViewable("rubrics", form.getRubrics_isStudentViewable());
    iprops.setStudentViewable("feedback", form.getFeedback_isStudentViewable());
    iprops.setStudentViewable(
      "pagebreak", form.getPageBreak_isStudentViewable());
    itemplate.updateData(iprops);
    sprops.setItemList(new ArrayList());
    stemplate.addItem(itemplate);
  }
}
