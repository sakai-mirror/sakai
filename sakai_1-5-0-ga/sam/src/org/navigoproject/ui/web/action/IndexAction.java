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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.navigoproject.business.entity.AssessmentTemplate;
import org.navigoproject.business.entity.AssessmentTemplateIterator;
import org.navigoproject.business.entity.RecordingData;
import org.navigoproject.business.entity.assessment.model.AssessmentPropertiesImpl;
import org.navigoproject.business.entity.assessment.model.AssessmentTemplatePropertiesImpl;
import org.navigoproject.business.entity.assessment.model.DistributionGroup;
import org.navigoproject.business.entity.assessment.model.FeedbackModel;
import org.navigoproject.business.entity.assessment.model.ItemImpl;
import org.navigoproject.business.entity.assessment.model.ItemTemplatePropertiesImpl;
import org.navigoproject.business.entity.assessment.model.MediaData;
import org.navigoproject.business.entity.assessment.model.ScoringModel;
import org.navigoproject.business.entity.assessment.model.SectionImpl;
import org.navigoproject.business.entity.assessment.model.SectionPropertiesImpl;
import org.navigoproject.business.entity.assessment.model.SectionTemplateImpl;
import org.navigoproject.business.entity.assessment.model.SectionTemplatePropertiesImpl;
import org.navigoproject.business.entity.assessment.model.SubmissionModel;
import org.navigoproject.business.entity.properties.AssessmentProperties;
import org.navigoproject.business.entity.properties.AssessmentTemplateProperties;
import org.navigoproject.osid.OsidManagerFactory;
import org.navigoproject.osid.assessment.AssessmentServiceDelegate;
import org.navigoproject.ui.web.asi.author.AuthoringHelper;
import org.navigoproject.ui.web.form.IndexForm;
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
import org.navigoproject.ui.web.form.edit.TemplateForm;
import osid.OsidException;
import osid.assessment.Assessment;
import osid.assessment.AssessmentException;
import osid.assessment.ItemIterator;
import osid.assessment.Section;
import osid.assessment.SectionIterator;
import osid.shared.SharedManager;

/**
 * This class handles the actions generated by the main page; creating and
 * editing a template, and creating and editing an assessment.  It also
 * includes the precompile action, which is used to precompile pages for
 * faster loading time.  It uses the "saveAll" mechanism to create forms, and
 * then load those forms into the session via the ReadForms class to populate
 * defaults.  Most of the information tracking is done via sessions, so they
 * can be read by the jsps, forms, and actions alike.
 *
 * <p>
 * Note that QuestionForm and PartForm have different uses and values,
 * depending on whether we're working on an assessment or a template. The
 * other forms are used in both.
 * </p>
 *
 * <p></p>
 *
 * @author <a href="mailto:rgollub@stanford.edu">Rachel Gollub</a>
 * @author <a href="mailto:huong.t.nguyen@stanford.edu">Huong Nguyen</a>
 * @author <a href="mailto:esmiley@stanford.edu">Ed Smiley</a>
 * @author <a href="mailto:zqingru@stanford.edu">Qingru Zhang</a>
 * @author <a href="mailto:lydial@stanford.edu">Lydia Li</a>
 */
public class IndexAction
  extends AAMAction
{
  boolean isTemplate = true;
  static Logger LOG = Logger.getLogger(IndexAction.class.getName());

  /**
   * This is the standard execute method.  It does redirects for precompiling
   * jsps, and handles the four submit methods for the main page.  If new
   * forms are required, it uses the ReadForms class to populate those forms
   * with the defaults, and then load the information back into the session
   * template.
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
    ActionForward forward = null;
    HttpSession session = httpServletRequest.getSession(true);
    boolean saveAll = false; // This is just used to populate the

    LOG.debug("IA: Path: " + actionMapping.getPath());

    if(actionMapping.getPath().indexOf("precompile") > -1)
    {
      session.setAttribute("editorRole", "templateEditor");
      session.setAttribute("editorState", "newTemplate");
      session.setAttribute("getGroup", "0");
      if(httpServletRequest.getParameter("page") != null)
      {
        forward =
          actionMapping.findForward(httpServletRequest.getParameter("page"));
      }
    }

    else if(actionMapping.getPath().indexOf("AAMLogin") > -1)
    {
      forward = actionMapping.findForward("index");

      return forward;
    }

    // If this is a new template, get rid of all previous information
    else if(actionMapping.getPath().indexOf("newTemplate") > -1)
    {
      String name = ((TemplateForm) actionForm).getTemplateName();
      if(name.trim().equals(""))
      {
        forward = actionMapping.findForward("noTitle");
      }
      else
      {
        session = invalidateSession(session, httpServletRequest);

        if(! doNewTemplate(session, httpServletRequest, name))
        {
          forward =
            forwardToFailure(
              "IndexAction: Failed to create new Template.", actionMapping,
              httpServletRequest);
        }

        //saveAll = true;
        isTemplate = true;

        // This is necessary because all the defaults are set by the
        // jsp, and now that we require template name first, we don't
        // save things properly.
        session.setAttribute("needsSave", "true");
      }
    }

    // This just allows you to edit a template.
    else if(actionMapping.getPath().indexOf("editTemplate") > -1)
    {
      if(! doEditTemplate(session, httpServletRequest))
      {
        forward =
          forwardToFailure(
            "IndexAction: Failed to Edit Template.", actionMapping,
            httpServletRequest);
      }

      isTemplate = true;
    }

    // This allows you to create a new assessment based on an
    // existing template.  It is called submitIndex because of
    // some legacy code.
    else if(actionMapping.getPath().indexOf("submitIndex") > -1)
    {
      String id = "0";
      isTemplate = false;
      if(actionForm instanceof IndexForm)
      {
        id = ((IndexForm) actionForm).getAssessmentTypeChoice();
      }

      if(! doSubmitIndex(session, httpServletRequest, id))
      {
        forward =
          forwardToFailure(
            "IndexAction: Failed Submit.", actionMapping, httpServletRequest);
      }

      saveAll = true;

      // This is necessary because all the defaults are set by the
      // jsp, and now that we require assessment name first, we don't
      // save things properly.
      session.setAttribute("needsSave", "true");
    }

    // This just allows you to edit an existing assessment.
    else if(actionMapping.getPath().indexOf("editAssessment") > -1)
    {
      isTemplate = false;
      if(! doEditAssessment(session, httpServletRequest))
      {
        forward =
          forwardToFailure(
            "IndexAction: Failed to edit Assessment.", actionMapping,
            httpServletRequest);
      }
    }

    // This confirms a delete of a template or assessment
    else if(actionMapping.getPath().indexOf("confirmDelete") > -1)
    {
      if(doConfirmDelete(session, httpServletRequest))
      {
        forward = actionMapping.findForward("confirmDelete");
      }
      else
      {
        forward =
          forwardToFailure(
            "IndexAction: Failed to confirm delete.", actionMapping,
            httpServletRequest);
      }
    }

    // This deletes a template or assessment
    else if(actionMapping.getPath().indexOf("deleteAction") > -1)
    {
      // This deletes a template
      //if(session.getAttribute("delete.isTemplate").equals("true"))
      //{
      if(doDeleteTemplate(session))
      {
        forward = actionMapping.findForward("index");
      }
      else
      {
        forward =
          forwardToFailure(
            "IndexAction: Failed to delete Template.", actionMapping,
            httpServletRequest);
      }

      // }

      /*
         else
         // This deletes an assessment
         {
           if(doDeleteAssessment(session))
           {
             forward = actionMapping.findForward("index");
           }
           else
           {
             forward =
               forwardToFailure(
                 "IndexAction: Failed to delete Assessment.", actionMapping,
                 httpServletRequest);
           }
         }
       */
    }

    // This picks up the template we've created or selected from
    // the session, where the above methods stored it.
    session = httpServletRequest.getSession(true);
    Assessment template =
      (Assessment) session.getAttribute("assessmentTemplate");
    AssessmentProperties properties = null;

    // This whole complicated thing just sets the properties correctly.
    if(template != null)
    {
      try
      {
        properties = (AssessmentProperties) template.getData();
      }
      catch(AssessmentException ae)
      {
        LOG.error(ae.getMessage(), ae);
        forward =
          forwardToFailure(
            "IndexAction: " + ae.getMessage(), actionMapping, httpServletRequest);
      }
    }
    else // Only used if new assessment template
    {
      properties = new AssessmentTemplatePropertiesImpl();

      // add courseId and agentId here - daisyf 10/21/03
      // courseId & agentId should be retrieved from user session
      Long courseId = new Long("0");
      String agentId = "";
      if(session.getAttribute("course_id") != null)
      {
        courseId = new Long((String) session.getAttribute("course_id"));
      }

      if(session.getAttribute("agent_id") != null)
      {
        agentId = (String) session.getAttribute("agent_id");
      }

      ((AssessmentTemplatePropertiesImpl) properties).setCourseId(courseId);
      ((AssessmentTemplatePropertiesImpl) properties).setAgentId(agentId);

      try
      {
        AssessmentServiceDelegate delegate = new AssessmentServiceDelegate();
        template =
          delegate.createAssessmentTemplate(
            "New template", null, null,
            (AssessmentTemplateProperties) properties);
      }
      catch(AssessmentException e)
      {
        LOG.error(e.getMessage(), e);
        forward =
          forwardToFailure(
            "IndexAction: " + e.getMessage(), actionMapping, httpServletRequest);
      }
      catch(OsidException e)
      {
        LOG.error(e.getMessage(), e);
        forward =
          forwardToFailure(
            "IndexAction: " + e.getMessage(), actionMapping, httpServletRequest);
      }
    }

    AssessmentTemplateProperties props = null;

    // Set a separate variable so we don't have to cast it each time.
    if(properties instanceof AssessmentTemplateProperties)
    {
      props = (AssessmentTemplatePropertiesImpl) properties;
    }

    // Done setting properties correctly.
    // Now, if we need to set defaults, call ReadForms to set them
    // (since we've already created the forms with their default
    // values.
    if(saveAll)
    {
      try
      {
        ReadForms forms =
          new ReadForms(template, properties, props, session, isTemplate);
        forms.doAccessFormGroups();
        forms.doAccessForm();
        forms.doDescriptionForm();
        forms.doDisplayForm();
        forms.doDistributionForm();
        forms.doEvaluationForm();
        forms.doFeedbackForm();
        forms.doNotificationForm();
        forms.doSubmissionsForm();

        if(isTemplate)
        {
          LOG.debug("This is a template -- adding sections.");
          forms.doPartForm();
          forms.doQuestionForm();
        }
      }
      catch(Exception e)
      {
        LOG.error(e.getMessage(), e);
        forward =
          forwardToFailure(
            "IndexAction: " + e.getMessage(), actionMapping, httpServletRequest);
      }
    }

    // Now save the properties information to the template, and
    // we're done.
    try
    {
      template.updateData(properties);
    }
    catch(AssessmentException e)
    {
      LOG.error(e.getMessage(), e);
      forward =
        forwardToFailure(
          "IndexAction: " + e.getMessage(), actionMapping, httpServletRequest);
    }

    if(forward == null)
    { // Everything's worked so far
      // put the template object into the http session
      session.setAttribute("assessmentTemplate", template);

      //PartForm pf = (PartForm) session.getAttribute("part");
      //LOG.debug("Part list: " + pf.getPartList().size());
      // Forward to the default page
      forward = actionMapping.findForward("templateEditor");
    }

    return forward;
  }

  /**
   * This creates the default groups for a new template, and then creates the
   * necessary forms.  It then uses ReadForms to read the defaults from the
   * forms and save them to the session template.
   *
   * @param session DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean doNewTemplate(HttpSession session,
                               HttpServletRequest httpServletRequest,
                               String name)
  {
    try
    {
      LOG.debug("IA: Doing new template");

      /*
         // Create access categories
         Collection groups = new ArrayList();
         AccessGroup group = new AccessGroup();
         group.setName("Full Class");
         group.setIsActive(true);
         // set form defaults for full class
         group.setReleaseType("0");
         group.setRetractType("0");
         group.setRetryAllowed(true);
         group.setDueDateModel("1");
         groups.add(group);
         / * These are out for beta2
            group = new AccessGroup();
            group.setName("Sections");
            group.setIsActive(true);
            groups.add(group);
            group = new AccessGroup();
            group.setName("Groups");
            group.setIsActive(true);
            groups.add(group);
       * /
           AccessForm af = new AccessForm();
           af.setGroups(groups);
           // Populate forms with defaults.
           session.setAttribute("access", af);
           session.setAttribute("description", new DescriptionForm());
           session.setAttribute("display", new DisplayForm());
           session.setAttribute("distribution", new DistributionForm());
           session.setAttribute("evaluation", new EvaluationForm());
           session.setAttribute("feedback", new FeedbackForm());
           session.setAttribute("submissions", new SubmissionsForm());
           session.setAttribute("notification", new NotificationForm());
           session.setAttribute("part", new PartForm());
           session.setAttribute("question", new QuestionForm());
           session.setAttribute("editorRole", "templateEditor");
           session.setAttribute("editorState", "newTemplate");
       */
      TemplateForm form = new TemplateForm();
      form.setTemplateName(name);
      session.setAttribute("templateEditorForm", form);
      LOG.debug("IA: Access:" + name);

      // recordingData encapsulates the information needed for recording.
      // set recording agent, agent id,
      // set course_assignment_context value
      // set max tries (0=unlimited), and 30 seconds max length
      String contextInfo = "template description audio";
      AuthoringHelper authoringHelper = new AuthoringHelper();
      RecordingData recordingData =
          new RecordingData(
          authoringHelper.getRemoteUserID(httpServletRequest).toString(),
          authoringHelper.getRemoteUserName(httpServletRequest),
          contextInfo, "0", "30");
      // set this value in the session for sound recorder
      session.setAttribute("recordingData", recordingData);


      return true;
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);

      return false;
    }
  }

  /**
   * This finds the correct template to edit, and loads it into the session for
   * editing.
   *
   * @param session DOCUMENTATION PENDING
   * @param httpServletRequest DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean doEditTemplate(
    HttpSession session, HttpServletRequest httpServletRequest)
  {
    try
    {
      LOG.debug(
        "Editing template " + httpServletRequest.getParameter("id"));
      AssessmentServiceDelegate delegate = new AssessmentServiceDelegate();
      AssessmentTemplateIterator iter =
        delegate.getAssessmentTemplate(
          Long.parseLong(httpServletRequest.getParameter("id")));
      AssessmentTemplate myTemplate = (AssessmentTemplate) iter.next();
      session = invalidateSession(session, httpServletRequest);
      session.setAttribute("editorRole", "templateEditor");
      session.setAttribute("editorState", "edit");
      isTemplate = true;

      //populateSession(myTemplate, session);
      populateTemplate((AssessmentTemplate) myTemplate, session);
      session.setAttribute("assessmentTemplate", myTemplate);

      // recordingData encapsulates the information needed for recording.
      // set recording agent, agent id,
      // set course_assignment_context value
      // set max tries (0=unlimited), and 30 seconds max length
      String contextInfo = "template description audio";
      AuthoringHelper authoringHelper = new AuthoringHelper();
      RecordingData recordingData =
          new RecordingData(
          authoringHelper.getRemoteUserID(httpServletRequest).toString(),
          authoringHelper.getRemoteUserName(httpServletRequest),
          contextInfo, "0", "30");
      // set this value in the session for sound recorder
      session.setAttribute("recordingData", recordingData);

      return true;
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);

      return false;
    }
  }

  /**
   * This creates a new assessment based on an existing template. The template
   * values are used to populate the session, so the assessment gets those
   * values as the defaults.
   *
   * @param session DOCUMENTATION PENDING
   * @param httpServletRequest DOCUMENTATION PENDING
   * @param id DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean doSubmitIndex(
    HttpSession session, HttpServletRequest httpServletRequest, String id)
  {
    try
    {
      AssessmentServiceDelegate delegate = new AssessmentServiceDelegate();
      AssessmentTemplateIterator iter =
        delegate.getAssessmentTemplate(Long.parseLong(id));
      AssessmentTemplate myTemplate = (AssessmentTemplate) iter.next();
      session = invalidateSession(session, httpServletRequest);
      session.setAttribute("editorRole", "assessmentEditor");
      session.setAttribute("editorState", "newAssessment");

      // Here we populate the session with the template information,
      // and then create a new Assessment with the template.
      populateSession(myTemplate, session);
      Assessment myAssessment = null;
      delegate = new AssessmentServiceDelegate();
      myAssessment = delegate.createAssessment("New assessment", null, null);
      AssessmentPropertiesImpl myProps = new AssessmentPropertiesImpl();
      myProps.setAssessmentTemplate(myTemplate);

      // add courseId and agentId here - daisyf 10/21/03
      // courseId & agentId should be retrieved from user session
      Long courseId = new Long("0");
      String agentId = "";
      if(session.getAttribute("course_id") != null)
      {
        courseId = new Long((String) session.getAttribute("course_id"));
      }

      if(session.getAttribute("agent_id") != null)
      {
        agentId = (String) session.getAttribute("agent_id");
      }

      myProps.setCourseId(courseId);
      myProps.setAgentId(agentId);
      myAssessment.updateData(myProps);

      // Create an automatic first part
      Section section = delegate.createSection("New Part", "", null);
      SectionPropertiesImpl myprops = new SectionPropertiesImpl();

      // Add media
      SectionIterator siter = myTemplate.getSections();
      Section tsection = (Section) siter.next();
      myprops.setMediaCollection(
        ((SectionPropertiesImpl) tsection.getData()).getMediaCollection());
      section.updateData(myprops);

      // Update the rest of the fields
      session.setAttribute("getPart", section);
      AssessmentAction.doAssessmentPartForm(
        session, (ActionForm) session.getAttribute("part"));
      section.updateDisplayName("New Part");

      // Add to assessment
      myAssessment.addSection(section);
      doPartForm(session, myAssessment, myTemplate, null, null, false);
      session.setAttribute("assessmentTemplate", myAssessment);

      return true;
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);

      return false;
    }
  }

  /**
   * This finds an existing assessment and its associated template, and loads
   * them into the session.  The session is populated first with the template
   * values, and then some of the values are overwritten with configured
   * assessment values.
   *
   * @param session DOCUMENTATION PENDING
   * @param httpServletRequest DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean doEditAssessment(
    HttpSession session, HttpServletRequest httpServletRequest)
  {
    try
    {
      AssessmentServiceDelegate delegate = new AssessmentServiceDelegate();
      Assessment myAssessment =
        delegate.getAssessment(
          Long.parseLong(httpServletRequest.getParameter("id")));
      session = invalidateSession(session, httpServletRequest);
      session.setAttribute("editorRole", "assessmentEditor");
      session.setAttribute("editorState", "edit");

      // Populate with template information first, then overwrite
      // where appropriate with assessment info.
      try
      {
        populateSession(
          ((AssessmentProperties) myAssessment.getData()).getAssessmentTemplate(),
          session);
      }
      catch(Exception e)
      {
        LOG.warn("Failed to populate with " + e.getMessage());
      }

      populateSession(myAssessment, session);
      session.setAttribute("assessmentTemplate", myAssessment);

      return true;
    }
    catch(Exception e)
    {
      LOG.error(e);

      return false;
    }
  }

  /**
   * This fills in template information from the database.
   */
  public void populateTemplate(
    AssessmentTemplate assessment, HttpSession session)
  {
    AssessmentTemplateProperties props = null;
    try
    {
      props = (AssessmentTemplateProperties) assessment.getData();
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }

    TemplateForm tf = (TemplateForm) session.getAttribute("templateEditorForm");

    if(tf == null)
    {
      tf = new TemplateForm();
    }

    try
    {
      tf.setTemplateName(assessment.getTemplateName());
      tf.setTemplateAuthor(assessment.getTemplateAuthor());
      tf.setTemplateDescription(assessment.getComments());
      tf.setItemAccessType(props.getItemAccessType());
      tf.setDisplayChunking(props.getDisplayChunking());
      tf.setQuestionNumbering(props.getQuestionNumbering());
      if(props.getSubmissionModel() != null)
      {
        tf.setSubmissionModel(
          ((SubmissionModel) props.getSubmissionModel()).getNumberSubmissions());
        tf.setSubmissionNumber(
          new Integer(
            ((SubmissionModel) props.getSubmissionModel()).getSubmissionsAllowed()).toString());
      }

      tf.setLateHandling(props.getLateHandling());
      tf.setAutoSave(props.getAutoSave());

      for(
        Enumeration e =
          ((AssessmentTemplatePropertiesImpl) props).getInstructorEditableMap()
           .keys(); e.hasMoreElements();)
      {
        String key = (String) e.nextElement();
        tf.setValue(key, new Boolean(props.isInstructorEditable(key)));
      }

      // Feedback
      tf.setFeedbackType(props.getFeedbackType());
      if(props.getFeedbackComponents() != null)
      {
        StringTokenizer st =
          new StringTokenizer(props.getFeedbackComponents(), ":");
        tf.setFeedbackComponent_QuestionText(
          (st.nextToken().equals("T")) ? Boolean.TRUE : Boolean.FALSE);
        tf.setFeedbackComponent_StudentResp(
          (st.nextToken().equals("T")) ? Boolean.TRUE : Boolean.FALSE);
        tf.setFeedbackComponent_CorrectResp(
          (st.nextToken().equals("T")) ? Boolean.TRUE : Boolean.FALSE);
        tf.setFeedbackComponent_StudentScore(
          (st.nextToken().equals("T")) ? Boolean.TRUE : Boolean.FALSE);
        tf.setFeedbackComponent_QuestionLevel(
          (st.nextToken().equals("T")) ? Boolean.TRUE : Boolean.FALSE);
        tf.setFeedbackComponent_SelectionLevel(
          (st.nextToken().equals("T")) ? Boolean.TRUE : Boolean.FALSE);
        tf.setFeedbackComponent_GraderComments(
          (st.nextToken().equals("T")) ? Boolean.TRUE : Boolean.FALSE);
        tf.setFeedbackComponent_Statistics(
          (st.nextToken().equals("T")) ? Boolean.TRUE : Boolean.FALSE);
      }

      // Grading
      tf.setAnonymousGrading(new Boolean(props.getAnonymousGrading()));
      tf.setToGradebook(props.getToGradebook());
      tf.setRecordedScore(props.getRecordedScore());

      session.setAttribute("templateEditorForm", tf);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }

  /**
   * This takes the information from the template/assessment, and loads into
   * the session forms for display and editing, to be stored later (when any
   * changes are made) by AssessmentAction.
   *
   * @param assessment DOCUMENTATION PENDING
   * @param session DOCUMENTATION PENDING
   */
  public void populateSession(Assessment assessment, HttpSession session)
  {
    AssessmentTemplate template = null;
    AssessmentProperties props = null;
    AssessmentTemplateProperties tprops = null;

    // Set the variables for this method.
    try
    {
      props = (AssessmentProperties) assessment.getData();
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }

    if(assessment instanceof AssessmentTemplate)
    {
      template = (AssessmentTemplate) assessment;
      tprops = (AssessmentTemplateProperties) props;
    }
    else
    {
      try
      {
        template = props.getAssessmentTemplate();
        if(template != null)
        {
          tprops = (AssessmentTemplatePropertiesImpl) template.getData();
        }
      }
      catch(Exception e)
      {
        LOG.error(e); throw new Error(e);
      }
    }

    // Now load each of the forms with the correct information.
    doDescriptionForm(session, assessment, template, props, tprops);
    doAccessForm(session, template, props, tprops);
    doDisplayForm(session, template, props, tprops);
    doNotificationForm(session, template, props, tprops);
    doFeedbackForm(session, template, props, tprops);
    doSubmissionsForm(session, template, props, tprops);
    doEvaluationForm(session, template, props, tprops);
    doDistributionForm(session, template, props, tprops);
    doPartForm(session, assessment, template, props, tprops, isTemplate);
    doQuestionForm(session, template, props, tprops);
  }

  /**
   * This loads template/assessment values into the description form.
   *
   * @param session DOCUMENTATION PENDING
   * @param assessment DOCUMENTATION PENDING
   * @param template DOCUMENTATION PENDING
   * @param props DOCUMENTATION PENDING
   * @param tprops DOCUMENTATION PENDING
   */
  public void doDescriptionForm(
    HttpSession session, Assessment assessment, AssessmentTemplate template,
    AssessmentProperties props, AssessmentTemplateProperties tprops)
  {
    DescriptionForm df = (DescriptionForm) session.getAttribute("description");
    if(df == null)
    {
      df = new DescriptionForm();
    }

    try
    {
      df.setName(assessment.getDisplayName());
      df.setDescription(assessment.getDescription());
      df.setTemplateName(template.getTemplateName());
      df.setTemplateDescription(template.getComments());
      if(assessment.getAssessmentType() != null)
      {
        df.setAssessmentType(assessment.getAssessmentType().toString());
      }

      df.setObjectives(props.getObjectives());
      df.setKeywords(props.getKeywords());
      df.setRubrics(props.getRubrics());
      Iterator iter = props.getMediaCollection().iterator();
      Collection media = new ArrayList();
      Collection relatedMedia = new ArrayList();

      while(iter.hasNext())
      {
        MediaData md = (MediaData) iter.next();
        if(md.isLink())
        {
          relatedMedia.add(md);
        }
        else
        {
          media.add(md);
        }

        AssessmentMediaAction.mediaMap.put(md.getMapId(), md);
      }

      df.setMediaCollection(media);
      df.setRelatedMediaCollection(relatedMedia);

      df.setTemplateName_isInstructorViewable(
        tprops.isInstructorViewable("templateName"));
      df.setTemplateDescription_isInstructorViewable(
        tprops.isInstructorViewable("comments"));
      df.setName_isInstructorViewable(tprops.isInstructorViewable("name"));
      df.setDescription_isInstructorViewable(
        tprops.isInstructorViewable("description"));
      df.setAssessmentType_isInstructorViewable(
        tprops.isInstructorViewable("type"));
      df.setObjectives_isInstructorViewable(
        tprops.isInstructorViewable("objectives"));
      df.setKeywords_isInstructorViewable(
        tprops.isInstructorViewable("keywords"));
      df.setRubrics_isInstructorViewable(
        tprops.isInstructorViewable("rubrics"));
      df.setMediaCollection_isInstructorViewable(
        tprops.isInstructorViewable("mediaCollection"));
      df.setRelatedMediaCollection_isInstructorViewable(
        tprops.isInstructorViewable("relatedMediaCollection"));

      df.setTemplateName_isInstructorEditable(
        tprops.isInstructorEditable("templateName"));
      df.setTemplateDescription_isInstructorEditable(
        tprops.isInstructorEditable("comments"));
      df.setName_isInstructorEditable(tprops.isInstructorEditable("name"));
      df.setDescription_isInstructorEditable(
        tprops.isInstructorEditable("description"));
      df.setAssessmentType_isInstructorEditable(
        tprops.isInstructorEditable("type"));
      df.setObjectives_isInstructorEditable(
        tprops.isInstructorEditable("objectives"));
      df.setKeywords_isInstructorEditable(
        tprops.isInstructorEditable("keywords"));
      df.setRubrics_isInstructorEditable(
        tprops.isInstructorEditable("rubrics"));
      df.setMediaCollection_isInstructorEditable(
        tprops.isInstructorEditable("mediaCollection"));
      df.setRelatedMediaCollection_isInstructorEditable(
        tprops.isInstructorEditable("relatedMediaCollection"));

      df.setTemplateName_isStudentViewable(
        tprops.isStudentViewable("templateName"));
      df.setTemplateDescription_isStudentViewable(
        tprops.isStudentViewable("comments"));
      df.setName_isStudentViewable(tprops.isStudentViewable("name"));
      df.setDescription_isStudentViewable(
        tprops.isStudentViewable("description"));
      df.setAssessmentType_isStudentViewable(tprops.isStudentViewable("type"));
      df.setObjectives_isStudentViewable(
        tprops.isStudentViewable("objectives"));
      df.setKeywords_isStudentViewable(tprops.isStudentViewable("keywords"));
      df.setRubrics_isStudentViewable(tprops.isStudentViewable("rubrics"));
      df.setMediaCollection_isStudentViewable(
        tprops.isStudentViewable("mediaCollection"));
      df.setRelatedMediaCollection_isStudentViewable(
        tprops.isStudentViewable("relatedMediaCollection"));

      session.setAttribute("description", df);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * This loads the assessment/template values into the access form.
   *
   * @param session DOCUMENTATION PENDING
   * @param template DOCUMENTATION PENDING
   * @param props DOCUMENTATION PENDING
   * @param tprops DOCUMENTATION PENDING
   */
  public void doAccessForm(
    HttpSession session, Assessment template, AssessmentProperties props,
    AssessmentTemplateProperties tprops)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "doAccessForm(HttpSession " + session + ", Assessment " + template +
        ", AssessmentProperties " + props + ", AssessmentTemplateProperties " +
        tprops + ")");
    }

    AccessForm af = (AccessForm) session.getAttribute("access");
    if(af == null)
    {
      af = new AccessForm();
    }

    try
    {
      // This is complicated.  The groups should be gotten from
      // Coursework, and set as active as appropiate.  I.e., if
      // the group isn't in getGroups(), it should be created
      // but inactive.
      if(! props.getAccessGroups().isEmpty())
      {
        af.setGroups(props.getAccessGroups());
      }
      else
      {
        props.setAccessGroups(af.getGroups());
      }

      // 1st we make sure that our tests are performed on real objects not null
      if(props.getAccessGroups() == null)
      {
        props.setAccessGroups(new ArrayList());
      }

      if(af.getGroups() == null)
      {
        af.setGroups(new ArrayList());
      }

      af.setGroups_isInstructorViewable(tprops.isInstructorViewable("groups"));
      af.setReleaseType_isInstructorViewable(
        tprops.isInstructorViewable("releaseType"));
      af.setReleaseDate_isInstructorViewable(
        tprops.isInstructorViewable("releaseDate"));
      af.setRetractType_isInstructorViewable(
        tprops.isInstructorViewable("retractType"));
      af.setRetractDate_isInstructorViewable(
        tprops.isInstructorViewable("retractDate"));
      af.setDueDate_isInstructorViewable(
        tprops.isInstructorViewable("dueDate"));
      af.setRetryAllowed_isInstructorViewable(
        tprops.isInstructorViewable("retryAllowed"));
      af.setTimedAssessment_isInstructorViewable(
        tprops.isInstructorViewable("timedAssessment"));
      af.setPasswordRequired_isInstructorViewable(
        tprops.isInstructorViewable("passwordRequired"));
      af.setIpAccessType_isInstructorViewable(
        tprops.isInstructorViewable("ipAccessType"));

      af.setGroups_isInstructorEditable(tprops.isInstructorEditable("groups"));
      af.setReleaseType_isInstructorEditable(
        tprops.isInstructorEditable("releaseType"));
      af.setReleaseDate_isInstructorEditable(
        tprops.isInstructorEditable("releaseDate"));
      af.setRetractType_isInstructorEditable(
        tprops.isInstructorEditable("retractType"));
      af.setRetractDate_isInstructorEditable(
        tprops.isInstructorEditable("retractDate"));
      af.setDueDate_isInstructorEditable(
        tprops.isInstructorEditable("dueDate"));
      af.setRetryAllowed_isInstructorEditable(
        tprops.isInstructorEditable("retryAllowed"));
      af.setTimedAssessment_isInstructorEditable(
        tprops.isInstructorEditable("timedAssessment"));
      af.setPasswordRequired_isInstructorEditable(
        tprops.isInstructorEditable("passwordRequired"));
      af.setIpAccessType_isInstructorEditable(
        tprops.isInstructorEditable("ipAccessType"));

      af.setGroups_isStudentViewable(tprops.isStudentViewable("groups"));
      af.setReleaseType_isStudentViewable(
        tprops.isStudentViewable("releaseType"));
      af.setReleaseDate_isStudentViewable(
        tprops.isStudentViewable("releaseDate"));
      af.setRetractType_isStudentViewable(
        tprops.isStudentViewable("retractType"));
      af.setRetractDate_isStudentViewable(
        tprops.isStudentViewable("retractDate"));
      af.setDueDate_isStudentViewable(tprops.isStudentViewable("dueDate"));
      af.setRetryAllowed_isStudentViewable(
        tprops.isStudentViewable("retryAllowed"));
      af.setTimedAssessment_isStudentViewable(
        tprops.isStudentViewable("timedAssessment"));
      af.setPasswordRequired_isStudentViewable(
        tprops.isStudentViewable("passwordRequired"));
      af.setIpAccessType_isStudentViewable(
        tprops.isStudentViewable("ipAccessType"));

      session.setAttribute("access", af);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * This loads the assessment/template values into the display form.
   *
   * @param session DOCUMENTATION PENDING
   * @param template DOCUMENTATION PENDING
   * @param props DOCUMENTATION PENDING
   * @param tprops DOCUMENTATION PENDING
   */
  public void doDisplayForm(
    HttpSession session, Assessment template, AssessmentProperties props,
    AssessmentTemplateProperties tprops)
  {
    DisplayForm disf = (DisplayForm) session.getAttribute("display");
    if(disf == null)
    {
      disf = new DisplayForm();
    }

    try
    {
      disf.setItemAccessType(props.getItemAccessType());
      disf.setItemBookMarking(props.getItemBookmarking());
      disf.setDisplayChunking(props.getDisplayChunking());
      disf.setMultiPartAllowed(props.getMultiPartAllowed());

      disf.setItemAccessType_isInstructorViewable(
        tprops.isInstructorViewable("itemAccessType"));
      disf.setItemBookMarking_isInstructorViewable(
        tprops.isInstructorViewable("itemBookmarking"));
      disf.setDisplayChunking_isInstructorViewable(
        tprops.isInstructorViewable("displayChunking"));
      disf.setMultiPartAllowed_isInstructorViewable(
        tprops.isInstructorViewable("multiPartAllowed"));

      disf.setItemAccessType_isInstructorEditable(
        tprops.isInstructorEditable("itemAccessType"));
      disf.setItemBookMarking_isInstructorEditable(
        tprops.isInstructorEditable("itemBookmarking"));
      disf.setDisplayChunking_isInstructorEditable(
        tprops.isInstructorEditable("displayChunking"));
      disf.setMultiPartAllowed_isInstructorEditable(
        tprops.isInstructorEditable("multiPartAllowed"));

      disf.setItemAccessType_isStudentViewable(
        tprops.isStudentViewable("itemAccessType"));
      disf.setItemBookMarking_isStudentViewable(
        tprops.isStudentViewable("itemBookmarking"));
      disf.setDisplayChunking_isStudentViewable(
        tprops.isStudentViewable("displayChunking"));
      disf.setMultiPartAllowed_isStudentViewable(
        tprops.isStudentViewable("multiPartAllowed"));

      session.setAttribute("display", disf);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * This loads the assessment/template values into the notification form.
   *
   * @param session DOCUMENTATION PENDING
   * @param template DOCUMENTATION PENDING
   * @param props DOCUMENTATION PENDING
   * @param tprops DOCUMENTATION PENDING
   */
  public void doNotificationForm(
    HttpSession session, Assessment template, AssessmentProperties props,
    AssessmentTemplateProperties tprops)
  {
    NotificationForm nf =
      (NotificationForm) session.getAttribute("notification");
    if(nf == null)
    {
      nf = new NotificationForm();
    }

    try
    {
      if(props.getTesteeNotification() != null)
      {
        StringTokenizer st =
          new StringTokenizer(props.getTesteeNotification(), ":");
        nf.setTesteeNotification1(st.nextToken());
        nf.setTesteeNotification2(st.nextToken());
        nf.setTesteeNotification3(st.nextToken());
        nf.setTesteeNotification4(st.nextToken());
      }

      if(props.getInstructorNotification() != null)
      {
        StringTokenizer st =
          new StringTokenizer(props.getInstructorNotification(), ":");
        nf.setInstructorNotification1(st.nextToken());
        nf.setInstructorNotification2(st.nextToken());
        nf.setInstructorNotification3(st.nextToken());
        nf.setInstructorNotification4(st.nextToken());
      }

      nf.setTesteeNotification_isInstructorViewable(
        tprops.isInstructorViewable("testeeNotification"));
      nf.setInstructorNotification_isInstructorViewable(
        tprops.isInstructorViewable("instructorNotification"));

      nf.setTesteeNotification_isInstructorEditable(
        tprops.isInstructorEditable("testeeNotification"));
      nf.setInstructorNotification_isInstructorEditable(
        tprops.isInstructorEditable("instructorNotification"));

      nf.setTesteeNotification_isStudentViewable(
        tprops.isStudentViewable("testeeNotification"));
      nf.setInstructorNotification_isStudentViewable(
        tprops.isStudentViewable("instructorNotification"));

      session.setAttribute("notification", nf);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * This loads the assessment/template values into the feedback form.
   *
   * @param session DOCUMENTATION PENDING
   * @param template DOCUMENTATION PENDING
   * @param props DOCUMENTATION PENDING
   * @param tprops DOCUMENTATION PENDING
   */
  public void doFeedbackForm(
    HttpSession session, Assessment template, AssessmentProperties props,
    AssessmentTemplateProperties tprops)
  {
    FeedbackForm ff = (FeedbackForm) session.getAttribute("feedback");
    if(ff == null)
    {
      ff = new FeedbackForm();
    }

    try
    {
      FeedbackModel model =
        (FeedbackModel) ((AssessmentPropertiesImpl) props).getFeedbackModel();
      if(model != null)
      {
        ff.setFeedbackType(model.getFeedbackType());

        //Huong's changes
        if(model.getImmediateFeedbackType() != null)
        {
          //old - ff.setImmediateFeedbackType(model.getImmediateFeedbackType());
          StringTokenizer st =
            new StringTokenizer(model.getImmediateFeedbackType(), ":");
          if(st.hasMoreTokens())
          {
            ff.setImmediateFeedbackType_q(st.nextToken());
          }

          if(st.hasMoreTokens())
          {
            ff.setImmediateFeedbackType_p(st.nextToken());
          }

          if(st.hasMoreTokens())
          {
            ff.setImmediateFeedbackType_a(st.nextToken());
          }
        }

        //end of Huong changes
        ff.setDatedFeedbackType(model.getDatedFeedbackType());
        ff.setPerQuestionFeedbackType(model.getPerQuestionFeedbackType());

        ff.setFeedbackReleaseDate(model.getFeedbackDate());
        ff.setTotalScoreReleaseDate(model.getScoreDate());

        // Date info goes here
        ff.setFeedbackType_isInstructorViewable(
          tprops.isInstructorViewable("feedbackType"));

        ff.setFeedbackType_isInstructorEditable(
          tprops.isInstructorEditable("feedbackType"));

        ff.setFeedbackType_isStudentViewable(
          tprops.isStudentViewable("feedbackType"));
      }

      session.setAttribute("feedback", ff);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * This loads the assessment/template values into the submissions form.
   *
   * @param session DOCUMENTATION PENDING
   * @param template DOCUMENTATION PENDING
   * @param props DOCUMENTATION PENDING
   * @param tprops DOCUMENTATION PENDING
   */
  public void doSubmissionsForm(
    HttpSession session, Assessment template, AssessmentProperties props,
    AssessmentTemplateProperties tprops)
  {
    SubmissionsForm sf = (SubmissionsForm) session.getAttribute("submissions");
    if(sf == null)
    {
      sf = new SubmissionsForm();
    }

    try
    {
      sf.setLateHandling(props.getLateHandling());
      SubmissionModel model = (SubmissionModel) props.getSubmissionModel();
      if(model != null)
      {
        sf.setSubmissionModel(model.getNumberSubmissions());
        sf.setSubmissionNumber(
          new Integer(model.getSubmissionsAllowed()).toString());
      }

      if(props.getSubmissionsSaved() != null)
      {
        StringTokenizer st =
          new StringTokenizer(props.getSubmissionsSaved(), ":");
        sf.setSubmissionsSaved1(st.nextToken());
        sf.setSubmissionsSaved2(st.nextToken());
        sf.setSubmissionsSaved3(st.nextToken());
      }

      sf.setLateHandling_isInstructorViewable(
        tprops.isInstructorViewable("lateHandling"));
      sf.setSubmissionModel_isInstructorViewable(
        tprops.isInstructorViewable("submissionModel"));
      sf.setSubmissionsSaved_isInstructorViewable(
        tprops.isInstructorViewable("submissionsSaved"));

      sf.setLateHandling_isInstructorEditable(
        tprops.isInstructorEditable("lateHandling"));
      sf.setSubmissionModel_isInstructorEditable(
        tprops.isInstructorEditable("submissionModel"));
      sf.setSubmissionsSaved_isInstructorEditable(
        tprops.isInstructorEditable("submissionsSaved"));

      sf.setLateHandling_isStudentViewable(
        tprops.isStudentViewable("lateHandling"));
      sf.setSubmissionModel_isStudentViewable(
        tprops.isStudentViewable("submissionModel"));
      sf.setSubmissionsSaved_isStudentViewable(
        tprops.isStudentViewable("submissionsSaved"));

      session.setAttribute("submissions", sf);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * This loads the assessment/template values into the evaluation form.
   *
   * @param session DOCUMENTATION PENDING
   * @param template DOCUMENTATION PENDING
   * @param props DOCUMENTATION PENDING
   * @param tprops DOCUMENTATION PENDING
   */
  public void doEvaluationForm(
    HttpSession session, Assessment template, AssessmentProperties props,
    AssessmentTemplateProperties tprops)
  {
    EvaluationForm ef = (EvaluationForm) session.getAttribute("evaluation");
    if(ef == null)
    {
      ef = new EvaluationForm();
    }

    try
    {
      if(props.getEvaluationDistribution() != null)
      {
        StringTokenizer st =
          new StringTokenizer(props.getEvaluationDistribution(), ":");
        ef.setEvaluationDistribution_toInstructor(st.nextToken());
        ef.setEvaluationDistribution_toTAs(st.nextToken());
        ef.setEvaluationDistribution_toSectionGrader(st.nextToken());
        ef.setEvaluationDistribution_toReviewGroup(st.nextToken());
        if(st.hasMoreTokens()) // backfill for adding TAs
        {
          ef.setEvaluationDistribution_toTestee(st.nextToken());
        }
      }

      ef.setTesteeIdentity(props.getTesteeIdentity());
      if(props.getEvaluationComponents() != null)
      {
        StringTokenizer st =
          new StringTokenizer(props.getEvaluationComponents(), ":");
        ef.setEvaluationComponents_scores(st.nextToken());
        ef.setEvaluationComponents_commentsForQuestions(st.nextToken());
        ef.setEvaluationComponents_commentsForParts(st.nextToken());
        ef.setEvaluationComponents_commentForAssess(st.nextToken());
      }

      ef.setAutoScoring(props.getAutoScoring());
      ScoringModel model = (ScoringModel) props.getScoringModel();
      if(model != null)
      {
        ef.setScoringType(model.getScoringType());
        ef.setNumericModel(model.getNumericModel());
        ef.setDefaultQuestionValue(
          new Integer(model.getDefaultQuestionValue()).toString());
        ef.setFixedTotalScore(
          new Integer(model.getFixedTotalScore()).toString());
      }

      ef.setEvaluationDistribution_isInstructorViewable(
        tprops.isInstructorViewable("evaluationDistribution"));
      ef.setTesteeIdentity_isInstructorViewable(
        tprops.isInstructorViewable("testeeIdentity"));
      ef.setEvaluationComponents_isInstructorViewable(
        tprops.isInstructorViewable("evaluationComponents"));
      ef.setAutoScoring_isInstructorViewable(
        tprops.isInstructorViewable("autoScoring"));
      ef.setScoringType_isInstructorViewable(
        tprops.isInstructorViewable("scoringType"));
      ef.setNumericModel_isInstructorViewable(
        tprops.isInstructorViewable("numericModel"));

      ef.setEvaluationDistribution_isInstructorEditable(
        tprops.isInstructorEditable("evaluationDistribution"));
      ef.setTesteeIdentity_isInstructorEditable(
        tprops.isInstructorEditable("testeeIdentity"));
      ef.setEvaluationComponents_isInstructorEditable(
        tprops.isInstructorEditable("evaluationComponents"));
      ef.setAutoScoring_isInstructorEditable(
        tprops.isInstructorEditable("autoScoring"));
      ef.setScoringType_isInstructorEditable(
        tprops.isInstructorEditable("scoringType"));
      ef.setNumericModel_isInstructorEditable(
        tprops.isInstructorEditable("numericModel"));

      ef.setEvaluationDistribution_isStudentViewable(
        tprops.isStudentViewable("evaluationDistribution"));
      ef.setTesteeIdentity_isStudentViewable(
        tprops.isStudentViewable("testeeIdentity"));
      ef.setEvaluationComponents_isStudentViewable(
        tprops.isStudentViewable("evaluationComponents"));
      ef.setAutoScoring_isStudentViewable(
        tprops.isStudentViewable("autoScoring"));
      ef.setScoringType_isStudentViewable(
        tprops.isStudentViewable("scoringType"));
      ef.setNumericModel_isStudentViewable(
        tprops.isStudentViewable("numericModel"));

      session.setAttribute("evaluation", ef);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * This loads the assessment/template values into the distribution form.
   *
   * @param session DOCUMENTATION PENDING
   * @param template DOCUMENTATION PENDING
   * @param props DOCUMENTATION PENDING
   * @param tprops DOCUMENTATION PENDING
   */
  public void doDistributionForm(
    HttpSession session, Assessment template, AssessmentProperties props,
    AssessmentTemplateProperties tprops)
  {
    DistributionForm distf =
      (DistributionForm) session.getAttribute("distribution");
    if(distf == null)
    {
      distf = new DistributionForm();
    }

    try
    {
      Iterator iter = props.getDistributionGroups().iterator();
      while(iter.hasNext())
      {
        DistributionGroup group = (DistributionGroup) iter.next();
        String name = group.getDistributionGroup();
        Object[] list = group.getDistributionTypes().toArray();
        if(name.equals("admin"))
        {
          if(list.length > 0)
          {
            distf.setToAdmin1(list[0].toString());
          }

          if(list.length > 1)
          {
            distf.setToAdmin2(list[1].toString());
          }

          if(list.length > 2)
          {
            distf.setToAdmin3(list[2].toString());
          }
        }
        else if(name.equals("testee"))
        {
          if(list.length > 0)
          {
            distf.setToTestee1(list[0].toString());
          }

          if(list.length > 1)
          {
            distf.setToTestee2(list[1].toString());
          }

          if(list.length > 2)
          {
            distf.setToTestee3(list[2].toString());
          }

          if(list.length > 3)
          {
            distf.setToTestee4(list[3].toString());
          }
        }
        else if(name.equals("gradebook"))
        {
          if(list.length > 0)
          {
            distf.setToGradebook(list[0].toString());
          }
        }
      }

      distf.setToAdmin_isInstructorViewable(
        tprops.isInstructorViewable("toAdmin"));
      distf.setToTestee_isInstructorViewable(
        tprops.isInstructorViewable("toTestee"));
      distf.setToGradebook_isInstructorViewable(
        tprops.isInstructorViewable("toGradebook"));

      distf.setToAdmin_isInstructorEditable(
        tprops.isInstructorEditable("toAdmin"));
      distf.setToTestee_isInstructorEditable(
        tprops.isInstructorEditable("toTestee"));
      distf.setToGradebook_isInstructorEditable(
        tprops.isInstructorEditable("toGradebook"));

      distf.setToAdmin_isStudentViewable(tprops.isStudentViewable("toAdmin"));
      distf.setToTestee_isStudentViewable(tprops.isStudentViewable("toTestee"));
      distf.setToGradebook_isStudentViewable(
        tprops.isStudentViewable("toGradebook"));

      session.setAttribute("distribution", distf);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * This loads the template or assessment values into the part form.
   *
   * @param session DOCUMENTATION PENDING
   * @param assessment DOCUMENTATION PENDING
   * @param template DOCUMENTATION PENDING
   * @param props DOCUMENTATION PENDING
   * @param tprops DOCUMENTATION PENDING
   * @param isTemplate DOCUMENTATION PENDING
   */
  public static void doPartForm(
    HttpSession session, Assessment assessment, AssessmentTemplate template,
    AssessmentProperties props, AssessmentTemplateProperties tprops,
    boolean isTemplate)
  {
    PartForm pf = (PartForm) session.getAttribute("part");
    if(pf == null)
    {
      pf = new PartForm();
    }

    try
    {
      SectionIterator iter = template.getSections();
      if(iter.hasNext())
      {
        SectionTemplateImpl stemplate = (SectionTemplateImpl) iter.next();
        SectionTemplatePropertiesImpl sprops =
          (SectionTemplatePropertiesImpl) stemplate.getData();
        if(sprops != null)
        {
          pf.setName(stemplate.getDisplayName());
          if(stemplate.getSectionType() != null)
          {
            pf.setType(stemplate.getSectionType().toString());
          }

          pf.setDescription(stemplate.getDescription());
          pf.setKeywords(sprops.getKeywords());
          pf.setObjectives(sprops.getObjectives());
          pf.setRubrics(sprops.getRubrics());
          pf.setQuestionOrder(sprops.getItemOrder());
          Iterator piter = sprops.getMediaCollection().iterator();
          Collection media = new ArrayList();
          Collection relatedMedia = new ArrayList();
          while(piter.hasNext())
          {
            MediaData md = (MediaData) piter.next();
            if(md.isLink())
            {
              relatedMedia.add(md);
            }
            else
            {
              media.add(md);
            }

            AssessmentMediaAction.mediaMap.put(md.getMapId(), md);
          }

          pf.setMediaCollection(media);
          pf.setRelatedMediaCollection(relatedMedia);

          pf.setName_isInstructorViewable(sprops.isInstructorViewable("name"));
          pf.setType_isInstructorViewable(sprops.isInstructorViewable("type"));
          pf.setDescription_isInstructorEditable(
            sprops.isInstructorViewable("description"));
          pf.setKeywords_isInstructorViewable(
            sprops.isInstructorViewable("keywords"));
          pf.setObjectives_isInstructorEditable(
            sprops.isInstructorViewable("objectives"));
          pf.setRubrics_isInstructorViewable(
            sprops.isInstructorViewable("rubrics"));
          pf.setQuestionOrder_isInstructorViewable(
            sprops.isInstructorViewable("questionOrder"));
          pf.setMediaCollection_isInstructorViewable(
            sprops.isInstructorViewable("mediaCollection"));
          pf.setRelatedMediaCollection_isInstructorViewable(
            sprops.isInstructorViewable("relatedMediaCollection"));

          pf.setName_isInstructorEditable(sprops.isInstructorEditable("name"));
          pf.setType_isInstructorEditable(sprops.isInstructorEditable("type"));
          pf.setDescription_isInstructorEditable(
            sprops.isInstructorEditable("description"));
          pf.setKeywords_isInstructorEditable(
            sprops.isInstructorEditable("keywords"));
          pf.setObjectives_isInstructorEditable(
            sprops.isInstructorEditable("objectives"));
          pf.setRubrics_isInstructorEditable(
            sprops.isInstructorEditable("rubrics"));
          pf.setQuestionOrder_isInstructorEditable(
            sprops.isInstructorEditable("questionOrder"));
          pf.setMediaCollection_isInstructorEditable(
            sprops.isInstructorEditable("mediaCollection"));
          pf.setRelatedMediaCollection_isInstructorEditable(
            sprops.isInstructorEditable("relatedMediaCollection"));

          pf.setName_isStudentViewable(sprops.isStudentViewable("name"));
          pf.setType_isStudentViewable(sprops.isStudentViewable("type"));
          pf.setDescription_isStudentViewable(
            sprops.isStudentViewable("description"));
          pf.setKeywords_isStudentViewable(
            sprops.isStudentViewable("keywords"));
          pf.setObjectives_isStudentViewable(
            sprops.isStudentViewable("objectives"));
          pf.setRubrics_isStudentViewable(sprops.isStudentViewable("rubrics"));
          pf.setQuestionOrder_isStudentViewable(
            sprops.isStudentViewable("questionOrder"));
          pf.setMediaCollection_isStudentViewable(
            sprops.isStudentViewable("mediaCollection"));
          pf.setRelatedMediaCollection_isStudentViewable(
            sprops.isStudentViewable("relatedMediaCollection"));
        }
      }

      // Don't do this if we're creating or editing a template
      if(! isTemplate && ! (assessment instanceof AssessmentTemplate))
      {
        LOG.warn("This is assessment only code.");
        SectionIterator si = assessment.getSections();
        Collection sections = new ArrayList();
        while(si.hasNext())
        {
          Section section = (Section) si.next();
          sections.add(section);
        }

        pf.setPartList(sections);
      }

      session.setAttribute("part", pf);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * This loads the template values into the question form.
   *
   * @param session DOCUMENTATION PENDING
   * @param template DOCUMENTATION PENDING
   * @param props DOCUMENTATION PENDING
   * @param tprops DOCUMENTATION PENDING
   */
  public static void doQuestionForm(
    HttpSession session, Assessment template, AssessmentProperties props,
    AssessmentTemplateProperties tprops)
  {
    QuestionForm qf = (QuestionForm) session.getAttribute("question");
    if(qf == null)
    {
      qf = new QuestionForm();
    }

    try
    {
      SectionIterator iter = template.getSections();
      if(iter.hasNext())
      {
        SectionImpl si = (SectionImpl) iter.next();
        ItemIterator iiter = si.getItems();
        if(iiter.hasNext())
        {
          ItemImpl ii = (ItemImpl) iiter.next();
          qf.setId(new Integer(ii.getId().toString()).intValue());
          ItemTemplatePropertiesImpl iprops =
            (ItemTemplatePropertiesImpl) ii.getData();
          if(iprops != null)
          {
            qf.setText(iprops.getText());
            qf.setHint(iprops.getHint());
            qf.setValue(iprops.getValue());
            qf.setObjectives(iprops.getObjectives());
            qf.setKeywords(iprops.getKeywords());
            qf.setRubrics(iprops.getRubrics());
            qf.setFeedback(iprops.getFeedback());
            qf.setPageBreak(iprops.getPageBreak());
            qf.setAnswers(iprops.getAnswers());

            qf.setName_isInstructorViewable(
              iprops.isInstructorViewable("name"));
            qf.setText_isInstructorViewable(
              iprops.isInstructorViewable("text"));
            qf.setHint_isInstructorViewable(
              iprops.isInstructorViewable("hint"));
            qf.setValue_isInstructorViewable(
              iprops.isInstructorViewable("value"));
            qf.setObjectives_isInstructorViewable(
              iprops.isInstructorViewable("objectives"));
            qf.setKeywords_isInstructorViewable(
              iprops.isInstructorViewable("keywords"));
            qf.setRubrics_isInstructorViewable(
              iprops.isInstructorViewable("rubrics"));
            qf.setFeedback_isInstructorViewable(
              iprops.isInstructorViewable("feedback"));
            qf.setAfeedback_isInstructorViewable(
              iprops.isInstructorViewable("afeedback"));
            qf.setPageBreak_isInstructorViewable(
              iprops.isInstructorViewable("pagebreak"));

            qf.setName_isInstructorEditable(
              iprops.isInstructorEditable("name"));
            qf.setText_isInstructorEditable(
              iprops.isInstructorEditable("text"));
            qf.setHint_isInstructorEditable(
              iprops.isInstructorEditable("hint"));
            qf.setValue_isInstructorEditable(
              iprops.isInstructorEditable("value"));
            qf.setObjectives_isInstructorEditable(
              iprops.isInstructorEditable("objectives"));
            qf.setKeywords_isInstructorEditable(
              iprops.isInstructorEditable("keywords"));
            qf.setRubrics_isInstructorEditable(
              iprops.isInstructorEditable("rubrics"));
            qf.setFeedback_isInstructorEditable(
              iprops.isInstructorEditable("feedback"));
            qf.setAfeedback_isInstructorEditable(
              iprops.isInstructorEditable("afeedback"));
            qf.setPageBreak_isInstructorEditable(
              iprops.isInstructorEditable("pagebreak"));

            qf.setName_isStudentViewable(iprops.isStudentViewable("name"));
            qf.setText_isStudentViewable(iprops.isStudentViewable("text"));
            qf.setHint_isStudentViewable(iprops.isStudentViewable("hint"));
            qf.setValue_isStudentViewable(iprops.isStudentViewable("value"));
            qf.setObjectives_isStudentViewable(
              iprops.isStudentViewable("objectives"));
            qf.setKeywords_isStudentViewable(
              iprops.isStudentViewable("keywords"));
            qf.setRubrics_isStudentViewable(
              iprops.isStudentViewable("rubrics"));
            qf.setFeedback_isStudentViewable(
              iprops.isStudentViewable("feedback"));
            qf.setAfeedback_isStudentViewable(
              iprops.isStudentViewable("afeedback"));
            qf.setPageBreak_isStudentViewable(
              iprops.isStudentViewable("pagebreak"));
          }
        }
      }

      session.setAttribute("question", qf);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
    }
  }

  /**
   * This puts relevant information into the session, and checks to see if the
   * requested delete is possible, putting the results into the session as
   * well.
   *
   * @param session DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean doConfirmDelete(
    HttpSession session, HttpServletRequest request)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "doConfirmDelete(HttpSession " + session + ", HttpServletRequest " +
        request + ")");
    }

    try
    {
      String whatisit = "template";
      if(request.getParameter("templateid") != null)
      {
        session.setAttribute("delete.id", request.getParameter("templateid"));
        session.setAttribute("delete.isTemplate", "true");
      }
      else
      {
        session.setAttribute("delete.id", request.getParameter("assessmentid"));
        session.setAttribute("delete.isTemplate", "false");
        whatisit = "assessment";
      }

      AssessmentServiceDelegate delegate = new AssessmentServiceDelegate();
      int result =
        delegate.checkDelete(
          Long.parseLong((String) session.getAttribute("delete.id")));
      switch(result)
      {
        case 0:
          session.setAttribute(
            "delete.message",
            "Are you sure you want to delete this " + whatisit + "?");

          break;

        case 1:
          session.setAttribute(
            "delete.message",
            "This template has associated assessments, though they have not " +
            "been published; are you sure you want to delete it?  All " +
            "assessments will be deleted as well.");

          break;

        case 2:
          session.setAttribute(
            "delete.message",
            "This template has associated assessments which have been " +
            "published; are you sure you want to delete it?  All " +
            "assessments and results will be deleted as well.");

          break;

        case 3:
          session.setAttribute(
            "delete.message",
            "This assessment has been published; if you delete it all " +
            "associated results will also be deleted.  Are you sure " +
            "you want to delete it?");

          break;

        case 4:
          return false;
      }

      return true;
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);

      return false;
    }
  }

  /**
   * This deletes a template with all its associated parts, items, etc..
   *
   * @param session DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean doDeleteTemplate(HttpSession session)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("doDeleteTemplate(HttpSession " + session + ")");
    }

    try
    {
      AssessmentServiceDelegate delegate = new AssessmentServiceDelegate();
      SharedManager sm = OsidManagerFactory.createSharedManager();
      delegate.deleteAssessmentTemplate(
        sm.getId((String) session.getAttribute("delete.id")));

      return true;
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);

      return false;
    }
  }

  /**
   * This deletes an assessment with all its associated parts, items, etc..
   *
   * @param session DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean doDeleteAssessment(HttpSession session)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("doDeleteAssessment(HttpSession " + session + ")");
    }

    try
    {
      AssessmentServiceDelegate delegate = new AssessmentServiceDelegate();
      SharedManager sm = OsidManagerFactory.createSharedManager();
      delegate.deleteAssessment(
        sm.getId((String) session.getAttribute("delete.id")));

      return true;
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);

      return false;
    }
  }

  // Moved to AAMaction base class
  //  /**
  //   * This invalidates a session without erasing the metadata from
  //   * coursework.
  //   */
  //  private HttpSession invalidateSession(HttpSession session,
  //   HttpServletRequest request)
  //  {
  //    String agent_id = (String) session.getAttribute("agent_id");
  //    String agent_name = (String) session.getAttribute("agent_name");
  //    String course_id = (String) session.getAttribute("course_id");
  //    String course_name = (String) session.getAttribute("course_name");
  //    session.invalidate();
  //    session = request.getSession(true);
  //    session.setAttribute("agent_id", agent_id);
  //    session.setAttribute("agent_name", agent_name);
  //    session.setAttribute("course_id", course_id);
  //    session.setAttribute("course_name", course_name);
  //
  //    return session;
  //  }
}
