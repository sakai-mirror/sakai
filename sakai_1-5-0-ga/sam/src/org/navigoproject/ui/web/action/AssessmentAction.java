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
import org.navigoproject.business.entity.assessment.model.AccessGroup;
import org.navigoproject.business.entity.assessment.model.Answer;
import org.navigoproject.business.entity.assessment.model.AssessmentImpl;
import org.navigoproject.business.entity.assessment.model.AssessmentPropertiesImpl;
import org.navigoproject.business.entity.assessment.model.AssessmentTemplatePropertiesImpl;
import org.navigoproject.business.entity.assessment.model.FeedbackModel;
import org.navigoproject.business.entity.assessment.model.ItemImpl;
import org.navigoproject.business.entity.assessment.model.ItemPropertiesImpl;
import org.navigoproject.business.entity.assessment.model.MediaData;
import org.navigoproject.business.entity.assessment.model.SectionImpl;
import org.navigoproject.business.entity.assessment.model.SectionPropertiesImpl;
import org.navigoproject.business.entity.assessment.model.SubmissionModel;
import org.navigoproject.business.entity.properties.AssessmentProperties;
import org.navigoproject.business.entity.properties.AssessmentTemplateProperties;
import org.navigoproject.osid.OsidManagerFactory;
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
import org.navigoproject.ui.web.form.edit.PartReorderForm;
import org.navigoproject.ui.web.form.edit.QuestionForm;
import org.navigoproject.ui.web.form.edit.QuestionReorderForm;
import org.navigoproject.ui.web.form.edit.SubmissionsForm;
import org.navigoproject.ui.web.form.edit.TemplateForm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.DynaActionForm;

import osid.assessment.Assessment;
import osid.assessment.AssessmentException;
import osid.assessment.Item;
import osid.assessment.ItemIterator;
import osid.assessment.Section;
import osid.assessment.SectionIterator;

import osid.shared.Id;
import osid.shared.SharedManager;

/**
 * This class handles the actions that involve editing templates and
 * assessments. Associated actions (file upload, taking assessments, etc.) are
 * handled by other action classes. Much of the work takes place in the
 * session -- all changes are written to (or read from) the session template
 * or assessment.
 *
 * @author <a href="mailto:rgollub@stanford.edu">Rachel Gollub</a>
 * @author <a href="mailto:huong.t.nguyen@stanford.edu">Huong Nguyen</a>
 * @author <a href="mailto:esmiley@stanford.edu">Ed Smiley</a>
 * @author <a href="mailto:zqingru@stanford.edu">Qingru Zhang</a>
 * @author <a href="mailto:lydial@stanford.edu">Lydia Li</a>
 */
public class AssessmentAction
  extends AAMAction
{
  private static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(AssessmentAction.class.getName());

  /**
   * This is the standard execute() method; it handles action requests involved
   * in editing templates and assessments.
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
    LOG.debug("execute()");
    ActionForward forward = null;
    HttpSession session = httpServletRequest.getSession(true);

    LOG.debug("action mapping: " + actionMapping.getPath());

    Assessment template =
      (Assessment) session.getAttribute("assessmentTemplate");
    AssessmentProperties properties = null;

    boolean isTemplate = false;
    if(template instanceof AssessmentTemplate)
    {
      isTemplate = true;
    }

    if(template != null)
    {
      try
      {
        properties = (AssessmentProperties) template.getData();
      }
      catch(AssessmentException ae)
      {
        LOG.error(ae);
        forward =
          forwardToFailure(
            "AssessmentAction: Failed to load Assessment Properties.",
            actionMapping, httpServletRequest);
      }
    }

    AssessmentTemplateProperties props = null;

    // Set a separate variable so we don't have to cast it each time.
    if(properties instanceof AssessmentTemplateProperties)
    {
      props = (AssessmentTemplatePropertiesImpl) properties;
    }

    // This starts the assessment part editor.
    if(actionMapping.getPath().indexOf("startPartEditor") > -1)
    {
      if(doStartPartEditor(session, httpServletRequest, template))
      {
        forward = null; // Will be set later
      }
      else
      {
        forward =
          forwardToFailure(
            "AssessmentAction: Failed to Start Part Editor.", actionMapping,
            httpServletRequest);
      }
    }

    // Save a template
    if(actionMapping.getPath().indexOf("editDescription") > -1)
    {
      try
      {
        if(
          doEditDescription(
              (TemplateForm) actionForm, (AssessmentTemplate) template, props))
        {
          forward = null; // Will be set later
        }
        else
        {
          forward =
            forwardToFailure(
              "AssessmentAction: Failed to save Template.", actionMapping,
              httpServletRequest);
        }
      }
      catch(Exception e)
      {
        LOG.error(e);
        throw new Error(e);
      }
    }

    // If it's a part reorder, upload the part list
    else if(actionMapping.getPath().indexOf("startReorderPart") > -1)
    {
      try
      {
        doStartReorderPart(session, httpServletRequest);
      }

      catch(Exception e)
      {
        LOG.error(e);
        throw new Error(e);
      }

      forward = actionMapping.findForward("reorder");
    }

    // Do the actual part reorder
    else if(actionMapping.getPath().indexOf("partReorder") > -1)
    {
      forward =
        actionMapping.findForward(
          doPartReorder(session, httpServletRequest, template));
    }

    else if(actionMapping.getPath().indexOf("cancelPartReorder") > -1)
    {
      try
      {
        PartForm pf = (PartForm) session.getAttribute("part");
        pf.setPartList(pf.getOriginal_partList());
      }
      catch(Exception e)
      {
        LOG.error(e);
        throw new Error(e);
      }

      forward = actionMapping.findForward("templateEditor");
    }

    // If it's a question reorder, upload the question list
    else if(actionMapping.getPath().indexOf("startQuestionReorder") > -1)
    {
      try
      {
        doStartReorderQuestion(session, httpServletRequest, template);
      }

      catch(Exception e)
      {
        LOG.error(e);
        throw new Error(e);
      }

      forward = actionMapping.findForward("reorder");
    }

    // Do the actual question reorder
    else if(actionMapping.getPath().indexOf("questionReorder") > -1)
    {
      forward =
        actionMapping.findForward(
          doQuestionReorder(session, httpServletRequest, template));
    }

    else if(actionMapping.getPath().indexOf("cancelQuestionReorder") > -1)
    {
      try
      {
        SectionIterator sii = template.getSections();
        Section section = null;

        //Find the right section
        while(sii.hasNext())
        {
          section = (Section) sii.next();
          if(section.getId() == session.getAttribute("section_id"))
          {
            break;
          }
        }

        ((SectionPropertiesImpl) section.getData()).setItemList(
          ((SectionPropertiesImpl) section.getData()).getOriginal_ItemList());
      }
      catch(Exception e)
      {
        ;
      }

      forward = actionMapping.findForward("templateEditor");
    }

    // This populates the confirmdelete page for deleting a part
    else if(actionMapping.getPath().indexOf("deletePart") > -1)
    {
      session.setAttribute(
        "delete.message",
        "Delete this part will delete all of its subsections( questions, media, etc..). Are you sure you want to delete it?");
      session.setAttribute("id", httpServletRequest.getParameter("id"));
      session.setAttribute("assessmentTemplate", template);
      session.setAttribute("Source", "part");
      forward = actionMapping.findForward("confirmDelete");
    }

    //This deletes a part or question
    else if(actionMapping.getPath().indexOf("bdeleteAction") > -1)
    {
      String source = (String) session.getAttribute("Source");
      if(source.equals("part"))
      {
        if(doDeletePart(session))
        {
          forward = actionMapping.findForward("templateEditor");
        }
        else
        {
          forward =
            forwardToFailure(
              "AssessmentAction: Failed to delete part.", actionMapping,
              httpServletRequest);
        }
      }
      else if(source.equals("question"))
      {
        if(doQuestionDeleting(session))
        {
          forward = actionMapping.findForward("templateEditor");
        }
        else
        {
          forward =
            forwardToFailure(
              "AssessmentAction: Failed to delete question.", actionMapping,
              httpServletRequest);
        }
      }
    }

    //This populates the confrim delete page for deleting a question
    else if(actionMapping.getPath().indexOf("deleteQuestion") > -1)
    {
      session.setAttribute(
        "delete.message",
        "This will delete this question will all of its subsections. Are you sure you want to delete it?");
      session.setAttribute("Source", "question");
      session.setAttribute("id", httpServletRequest.getParameter("id"));
      session.setAttribute(
        "sectionid", httpServletRequest.getParameter("sectionid"));
      session.setAttribute("moreid", httpServletRequest.getParameter("moreid"));
      session.setAttribute("assessmentTemplate", template);
      forward = actionMapping.findForward("confirmDelete");
    }

    // This starts the question editors
    else if(
      (actionMapping.getPath().indexOf("startQuestionEditor") > -1) ||
        (actionMapping.getPath().indexOf("startQuestionMetadata") > -1))
    {
      if(
        doQuestionEditing(session, httpServletRequest, template, actionMapping))
      {
        // A different forward for the question editor
        if(actionMapping.getPath().indexOf("startQuestionEditor") > -1)
        {
          forward = actionMapping.findForward("questionEditor");
        }
        else
        {
          forward = null; // Will be set later
        }
      }
      else
      {
        forward =
          forwardToFailure(
            "AssessmentAction: Failed to Start question editor.", actionMapping,
            httpServletRequest);
      }
    }

    // This starts the access groups page.
    else if(actionMapping.getPath().indexOf("startAccess") > -1)
    {
      if(doStartAccess(session, httpServletRequest, properties))
      {
        forward = null; // Will be set later
      }
      else
      {
        forward =
          forwardToFailure(
            "AssessmentAction: Failed to Start Access.", actionMapping,
            httpServletRequest);
      }
    }

    // Initialize the form reader

    /* ReadForms reader =
       new ReadForms(template, properties, props, session, isTemplate);
       try
       {
         if(
           actionForm instanceof AccessForm &&
             (actionMapping.getPath().indexOf("editGroups") > -1))
         {
           session.setAttribute("access", (AccessForm) actionForm);
           reader.doAccessFormGroups();
         }
         else if(
           actionForm instanceof AccessForm &&
             (actionMapping.getPath().indexOf("editAccess") > -1))
         {
           session.setAttribute("access", actionForm);
           reader.doAccessForm();
         }
         else if(actionForm instanceof DescriptionForm)
         {
           session.setAttribute("description", (DescriptionForm) actionForm);
           session.setAttribute("editorState", "edit");
           reader.doDescriptionForm();
         }
         else if(actionForm instanceof DisplayForm)
         {
           session.setAttribute("display", (DisplayForm) actionForm);
           reader.doDisplayForm();
         }
         else if(actionForm instanceof FeedbackForm)
         {
           session.setAttribute("feedback", actionForm);
           reader.doFeedbackForm();
         }
         else if(actionForm instanceof SubmissionsForm)
         {
           session.setAttribute("submissions", actionForm);
           reader.doSubmissionsForm();
         }
         else if(actionForm instanceof EvaluationForm)
         {
           session.setAttribute("evaluation", actionForm);
           reader.doEvaluationForm();
         }
         else if(actionForm instanceof DistributionForm)
         {
           session.setAttribute("distribution", actionForm);
           reader.doDistributionForm();
         }
         else if(actionForm instanceof NotificationForm)
         {
           session.setAttribute("notification", actionForm);
           reader.doNotificationForm();
         }
         else if(
           (
               actionForm instanceof PartForm &&
               (actionMapping.getPath().indexOf("startPartEditor") == -1)
             ) && (actionMapping.getPath().indexOf("deletePart") == -1) &&
             isTemplate)
         {
           session.setAttribute("part", actionForm);
           reader.doPartForm();
         }
         else if(
           actionForm instanceof PartForm &&
             (actionMapping.getPath().indexOf("startPartEditor") == -1) &&
             (actionMapping.getPath().indexOf("deletePart") == -1) &&
             session.getAttribute("editorRole").equals("assessmentEditor"))
         {
           doAssessmentPartForm(session, actionForm);
         }
         else if(
           actionForm instanceof QuestionForm &&
             (actionMapping.getPath().indexOf("startQuestionEditor") == -1) &&
             (actionMapping.getPath().indexOf("editQuestionEditor") == -1) &&
             (actionMapping.getPath().indexOf("startQuestionMetadata") == -1) &&
             (actionMapping.getPath().indexOf("editQuestionMetadata") == -1) &&
             (actionMapping.getPath().indexOf("deleteQuestion") == -1))
         {
           session.setAttribute("question", actionForm);
           reader.doQuestionForm();
         }
         else if(
           actionForm instanceof QuestionForm &&
             (
               (actionMapping.getPath().indexOf("editQuestionEditor") != -1) ||
               (actionMapping.getPath().indexOf("editQuestionMetadata") != -1)
             ))
         {
           doAssessmentQuestionForm(session, actionForm, true);
           if(httpServletRequest.getParameter("id") != null)
           {
             doQuestionEditing(
               session, httpServletRequest, template, actionMapping);
             forward = actionMapping.findForward("metadata");
           }
         }
       }
       catch(Exception e)
       {
         LOG.error(e); throw new Error(e);
         forward =
           forwardToFailure(
             "AssessmentAction: " + e.getMessage(), actionMapping,
             httpServletRequest);
       }
     */
    try
    {
      if(
        (actionMapping.getPath().indexOf("startQuestionEditor") == -1) &&
          (actionMapping.getPath().indexOf("startQuestionMetadata") == -1) &&
          (actionMapping.getPath().indexOf("deleteQuestion") == -1) &&
          (actionMapping.getPath().indexOf("startPartEditor") == -1) &&
          (actionMapping.getPath().indexOf("startAccess") == -1))
      {
        try
        {
          AssessmentServiceDelegate delegate = new AssessmentServiceDelegate();
          if(isTemplate)
          {
            delegate.updateAssessmentTemplate((AssessmentTemplate) template);
          }
          else
          {
            delegate.updateAssessment(template);
          }
        }
        catch(Exception e)
        {
          LOG.error(e);
          throw new Error(e);
        }
      }

      if(actionMapping.getPath().indexOf("startQuestionMetadata") != -1)
      {
        forward = actionMapping.findForward("questionMetadata");
      }

      if(actionMapping.getPath().indexOf("editQuestionMetadata") != -1)
      {
        forward = actionMapping.findForward("questionEditor");
      }

      if(actionMapping.getPath().indexOf("startPartEditor") != -1)
      {
        forward = actionMapping.findForward("part");
      }

      if(actionMapping.getPath().indexOf("startAccess") != -1)
      {
        forward = actionMapping.findForward("access");
      }
    }

    catch(Exception e)
    {
      LOG.error(e);
      forward =
        forwardToFailure(
          "AssessmentAction: " + e.getMessage(), actionMapping,
          httpServletRequest);
    }

    if(forward == null)
    { // Everything's worked so far
      // put the template object into the http session
      session.setAttribute("assessmentTemplate", template);

      // this page is only a place holder
      // Forward to frontdoor
      forward = actionMapping.findForward("TEMPLATEEDITOR");

      /* solving Bug#385 problem, must save question content
       * before "dispatch" to file upload and other action
       * -daisyf
       */
      LOG.debug(
        "editQuestionEditor=" +
        actionMapping.getPath().indexOf("editQuestionEditor"));
      if(actionMapping.getPath().indexOf("editQuestionEditor") > -1)
      {
        QuestionForm questionForm = (QuestionForm) actionForm;
        if(session.getAttribute("question") == null)
        {
          session.setAttribute("question", questionForm);
        }

        LOG.debug("***question id=" + questionForm.getId());
        String forwardAction = questionForm.getForwardAction();
        LOG.debug("forwardAction=" + forwardAction);
        if(
          (forwardAction != null) &&
            forwardAction.equals("startQuestionMetadata"))
        {
          session.setAttribute("temp.question.id", questionForm.getId() + "");
          doQuestionEditing(
            session, httpServletRequest, template, actionMapping);
          forward = actionMapping.findForward("startQuestionMetadata");
        }
        else if(
          (forwardAction != null) &&
            forwardAction.equals("startQuestionEditor"))
        {
          session.setAttribute(
            "temp.question.moreid", questionForm.getId() + "");
          doQuestionEditing(
            session, httpServletRequest, template, actionMapping);
          forward = actionMapping.findForward("startQuestionEditor");
        }
        else if(
          (forwardAction != null) &&
            forwardAction.equals("startFileUploadQuestion"))
        {
          forward = actionMapping.findForward("startFileUploadQuestion");
          session.setAttribute("isLink", "false");
          session.setAttribute("mediaSource", "question");
          session.setAttribute("ignoreIndex", "true");
        }
      }
    }

    return forward;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param session DOCUMENTATION PENDING
   * @param actionForm DOCUMENTATION PENDING
   */
  public static void doAssessmentPartForm(
    HttpSession session, ActionForm actionForm)
  {
    PartForm form = (PartForm) actionForm;
    SectionImpl section = (SectionImpl) session.getAttribute("getPart");
    if(section == null)
    {
      return;
    }

    try
    {
      section.updateDisplayName(form.getName());
      section.updateDescription(form.getDescription());
      SectionPropertiesImpl sprops = (SectionPropertiesImpl) section.getData();
      if(sprops == null)
      {
        sprops = new SectionPropertiesImpl();
      }

      if(form.getType() != null)
      {
        sprops.setSectionType(
          new TypeImpl("Stanford", "AAM", "assessment", form.getType()));
      }

      sprops.setObjectives(form.getObjectives());
      sprops.setKeywords(form.getKeywords());
      sprops.setRubrics(form.getRubrics());
      sprops.setItemOrder(form.getQuestionOrder());

      section.updateData(sprops);

      // Add to session and form
      if(session.getAttribute("section.add") != null)
      {
        ((AssessmentImpl) session.getAttribute("assessmentTemplate")).addSection(
          section);
        Collection parts = form.getPartList();
        parts.add(section);
        form.setPartList(parts);
        session.removeAttribute("section.add");
      }
    }
    catch(Exception e)
    {
      LOG.error(e);
      throw new Error(e);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param session DOCUMENTATION PENDING
   * @param actionForm DOCUMENTATION PENDING
   */
  public static void doAssessmentQuestionForm(
    HttpSession session, ActionForm actionForm)
  {
    doAssessmentQuestionForm(session, actionForm, false);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param session DOCUMENTATION PENDING
   * @param actionForm DOCUMENTATION PENDING
   * @param saveToSection DOCUMENTATION PENDING
   */
  public static void doAssessmentQuestionForm(
    HttpSession session, ActionForm actionForm, boolean saveToSection)
  {
    QuestionForm form = (QuestionForm) actionForm;
    ItemImpl item = (ItemImpl) session.getAttribute("getQuestion");

    try
    {
      ItemPropertiesImpl iprops = (ItemPropertiesImpl) item.getData();

      item.updateDisplayName(form.getName());
      iprops.setText(form.getText());
      iprops.setHint(form.getHint());
      iprops.setValue(form.getValue());
      iprops.setObjectives(form.getObjectives());
      iprops.setKeywords(form.getKeywords());
      iprops.setRubrics(form.getRubrics());
      iprops.setFeedback(form.getFeedback());
      iprops.setPageBreak(form.getPageBreak());
      Collection answers = form.getAnswers();
      if(answers == null)
      {
        answers = new ArrayList();
      }

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

      // remove null answers if multiple choice
      Collection realanswers = new ArrayList();
      Collection deleteanswers = new ArrayList();
      Iterator it = answers.iterator();
      while(it.hasNext())
      {
        Answer answer = (Answer) it.next();
        LOG.debug(
          "Saving answer = " + answer.getText() + ", isCorrect = " +
          answer.getIsCorrect());
        if((answer.getText() != null) && ! answer.getText().trim().equals(""))
        {
          realanswers.add(answer);
        }
        else
        {
          if(item.getItemType().toString().startsWith("Multi"))
          {
            deleteanswers.add(answer);
          }
          else if(item.getItemType().toString().startsWith("True"))
          {
            answer.setText((answer.getIsCorrect() ? "True" : "False"));
            realanswers.add(answer);
          }
        }
      }

      // Delete excess answers.
      if(! deleteanswers.isEmpty())
      {
        AssessmentServiceDelegate delegate = new AssessmentServiceDelegate();
        delegate.deleteAnswers(deleteanswers);
      }

      iprops.setAnswers(realanswers);

      item.updateData(iprops);

      if(saveToSection && (session.getAttribute("question.section") != null))
      {
        LOG.debug("Saving to section.");
        ((SectionImpl) session.getAttribute("question.section")).addItem(item);
        session.removeAttribute("question.section");
      }
    }
    catch(Exception e)
    {
      LOG.error(e);
      throw new Error(e);
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param question DOCUMENTATION PENDING
   * @param session DOCUMENTATION PENDING
   */
  public void populateQuestion(Item question, HttpSession session)
  {
    QuestionForm qf = (QuestionForm) session.getAttribute("question");
    if(qf == null)
    {
      qf = new QuestionForm();
    }

    try
    {
      qf.setId(new Integer(question.getId().toString()).intValue());
      qf.setName(question.getDisplayName());
      if(question.getItemType() != null)
      {
        qf.setItemType(question.getItemType().toString());
      }

      ItemPropertiesImpl iprops = (ItemPropertiesImpl) question.getData();
      if(iprops != null)
      {
        qf.setText(iprops.getText());
        qf.setHint(iprops.getHint());
        qf.setValue(iprops.getValue());
        qf.setObjectives(iprops.getObjectives());
        qf.setKeywords(iprops.getKeywords());
        qf.setRubrics(iprops.getRubrics());
        qf.setFeedback(iprops.getFeedback());
        if(
          ((AssessmentPropertiesImpl) ((AssessmentImpl) session.getAttribute(
              "assessmentTemplate")).getData()).getDisplayChunking().equals(
              "2"))
        {
          qf.setOfferPageBreak(true);
        }
        else
        {
          qf.setOfferPageBreak(false);
        }

        qf.setPageBreak(iprops.getPageBreak());

        // Load media
        Iterator iter = iprops.getMediaCollection().iterator();
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

        qf.setMediaCollection(media);
        qf.setRelatedMediaCollection(relatedMedia);

        // Get answers
        qf.setAnswers(iprops.getAnswers());

        if(iprops.getAnswers().isEmpty())
        {
          Collection answers = new ArrayList();
          if(
            question.getItemType().toString().startsWith("Multi") ||
              question.getItemType().toString().startsWith("True"))
          {
            // Add correct number of answers
            AssessmentServiceDelegate delegate =
              new AssessmentServiceDelegate();
            int questionNo = 1;
            if(question.getItemType().toString().startsWith("Multip"))
            {
              questionNo = 4;
            }

            for(int i = 0; i < questionNo; i++)
            {
              answers.add(delegate.createAnswer());
            }

            ;
          }

          qf.setAnswers(answers);
        }
      }
    }
    catch(Exception e)
    {
      LOG.error(e);
      throw new Error(e);
    }

    session.setAttribute("question", qf);
    session.setAttribute("getQuestion", question);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param section DOCUMENTATION PENDING
   * @param session DOCUMENTATION PENDING
   * @param isNew DOCUMENTATION PENDING
   */
  public void populatePart(Section section, HttpSession session, boolean isNew)
  {
    PartForm pf = (PartForm) session.getAttribute("part");
    if(pf == null)
    {
      pf = new PartForm();
    }

    try
    {
      pf.setName(section.getDisplayName());
      if(section.getSectionType() != null)
      {
        pf.setType(section.getSectionType().getDescription());
      }

      pf.setDescription(section.getDescription());

      SectionPropertiesImpl sprops = (SectionPropertiesImpl) section.getData();
      if(sprops == null)
      {
        sprops = new SectionPropertiesImpl();
      }

      pf.setKeywords(sprops.getKeywords());
      pf.setObjectives(sprops.getObjectives());
      pf.setRubrics(sprops.getRubrics());
      pf.setQuestionOrder(sprops.getItemOrder());
    }
    catch(Exception e)
    {
      LOG.error(e);
      throw new Error(e);
    }

    session.setAttribute("part", pf);
    session.setAttribute("getPart", section);
    session.setAttribute("isNew", (isNew ? "true" : "false"));
  }

  /**
   * This method handles the preliminaries for the part editor.
   *
   * @param session DOCUMENTATION PENDING
   * @param httpServletRequest DOCUMENTATION PENDING
   * @param template DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean doStartPartEditor(
    HttpSession session, HttpServletRequest httpServletRequest,
    Assessment template)
  {
    Section section = null;
    boolean isNew = false;

    // If there is no existing part
    if(httpServletRequest.getParameter("id") == null)
    {
      try
      {
        isNew = true;

        // Populate the part form with the template values.
        LOG.debug("template");
        AssessmentTemplate mytemplate =
          ((AssessmentProperties) template.getData()).getAssessmentTemplate();
        SectionIterator si = mytemplate.getSections();
        Section sectionTemplate = si.next();
        populatePart(sectionTemplate, session, false);

        LOG.debug("section");

        // create new section
        AssessmentServiceDelegate delegate = new AssessmentServiceDelegate();
        section =
          delegate.createSection(
            "New Part", sectionTemplate.getDescription(), null);
        SectionPropertiesImpl myprops = new SectionPropertiesImpl();
        LOG.debug("media");

        // Pass media through (this isn't done in populatePart)
        SectionPropertiesImpl templateprops =
          (SectionPropertiesImpl) sectionTemplate.getData();
        myprops.setMediaCollection(
          new ArrayList(templateprops.getMediaCollection()));
        LOG.debug("save");

        // Save props to the section
        section.updateData(myprops);
        session.setAttribute("section.add", "true");
      }
      catch(Exception e)
      {
        LOG.error(e);
//        throw new Error(e);

        return false;
      }
    }

    // If we're editing an existing section
    else
    {
      try
      {
        SectionIterator iter = template.getSections();
        while(iter.hasNext())
        {
          section = (Section) iter.next();
          if(
            section.getId().toString().equals(
                httpServletRequest.getParameter("id")))
          {
            break;
          }
        }
      }
      catch(Exception e)
      {
        LOG.error(e);
//        throw new Error(e);

        return false;
      }
    }

    populatePart(section, session, isNew);

    return true;
  }

  /**
   *
   */
  public boolean doEditDescription(
    TemplateForm form, AssessmentTemplate template,
    AssessmentTemplateProperties props)
    throws Exception
  {
    LOG.debug("AA: Name: " + form.getTemplateName());
    template.setTemplateName(form.getTemplateName());
    template.setTemplateAuthor(form.getTemplateAuthor());
    template.setComments(form.getTemplateDescription());
    props.setItemAccessType(form.getItemAccessType());
    props.setDisplayChunking(form.getDisplayChunking());
    props.setQuestionNumbering(form.getQuestionNumbering());
    SubmissionModel model = null;
    if(props.getSubmissionModel() == null)
    {
      model = new SubmissionModel();
    }
    else
    {
      model = (SubmissionModel) props.getSubmissionModel();
    }

    model.setNumberSubmissions(form.getSubmissionModel());
    try
    {
      model.setSubmissionsAllowed(Integer.parseInt(form.getSubmissionNumber()));
    }
    catch(Exception e)
    {
      model.setSubmissionsAllowed(1);
    }

    props.setSubmissionModel(model);
    props.setLateHandling(form.getLateHandling());
    props.setAutoSave(form.getAutoSave());

    // Feedback
    // construct a colon delimited string for feedback Components check boxes  
    StringBuffer sComponents = new StringBuffer();
    sComponents.append(
      form.getFeedbackComponent_QuestionText().booleanValue() ? "T" : "F");
    sComponents.append(":");
    sComponents.append(
      form.getFeedbackComponent_StudentResp().booleanValue() ? "T" : "F");
    sComponents.append(":");
    sComponents.append(
      form.getFeedbackComponent_CorrectResp().booleanValue() ? "T" : "F");
    sComponents.append(":");
    sComponents.append(
      form.getFeedbackComponent_StudentScore().booleanValue() ? "T" : "F");
    sComponents.append(":");
    sComponents.append(
      form.getFeedbackComponent_QuestionLevel().booleanValue() ? "T" : "F");
    sComponents.append(":");
    sComponents.append(
      form.getFeedbackComponent_SelectionLevel().booleanValue() ? "T" : "F");
    sComponents.append(":");
    sComponents.append(
      form.getFeedbackComponent_GraderComments().booleanValue() ? "T" : "F");
    sComponents.append(":");
    sComponents.append(
      form.getFeedbackComponent_Statistics().booleanValue() ? "T" : "F");

    props.setFeedbackComponents(sComponents.toString());
    props.setFeedbackType(form.getFeedbackType());

    // Grading
    props.setAnonymousGrading(form.getAnonymousGrading().booleanValue());
    props.setToGradebook(form.getToGradebook());
    props.setRecordedScore(form.getRecordedScore());

    Iterator iter = form.getValueMap().keySet().iterator();
    while(iter.hasNext())
    {
      String key = (String) iter.next();
      props.setInstructorEditable(
        key, (form.getValue(key).toString().equals("on") ? true : false));
    }

    template.updateData(props);

    /*
       private String templateAuthor;
       private String templateDescription;
       private String itemAccessType;
       private String displayChunking;
       private String submissionModel;
       private String submissionNumber;
       private String lateHandling;
       private String feedbackType;
       private final Map feedbackTypes = new HashMap();
       private boolean anonymousGrading;
       private String toGradebook;
       private String recordedScore;
           props.setInstructorEditable(
             "templateName_isInstructorEditable",
             ((Boolean) form.get("templateName_isInstructorEditable")).booleanValue());
           props.setInstructorEditable(
             "assessmentName_isInstructorEditable",
             ((Boolean) form.get("assessmentName_isInstructorEditable")).booleanValue());
           props.setInstructorEditable(
             "assessmentAuthor_isInstructorEditable",
             ((Boolean) form.get("assessmentAuthor_isInstructorEditable")).booleanValue());
           props.setInstructorEditable(
             "descriptionName_isInstructorEditable",
             ((Boolean) form.get("description_isInstructorEditable")).booleanValue());
           props.setInstructorEditable(
             "dueDate_isInstructorEditable",
             ((Boolean) form.get("dueDate_isInstructorEditable")).booleanValue());
           props.setInstructorEditable(
             "releaseDate_isInstructorEditable",
             ((Boolean) form.get("releaseDate_isInstructorEditable")).booleanValue());
           props.setInstructorEditable(
             "anonymousRelease_isInstructorEditable",
             ((Boolean) form.get("anonymousRelease_isInstructorEditable")).booleanValue());
           props.setInstructorEditable(
             "authenticatedRelease_isInstructorEditable",
             ((Boolean) form.get("authenticatedRelease_isInstructorEditable")).booleanValue());
           props.setInstructorEditable(
             "fullClassRelease_isInstructorEditable",
             ((Boolean) form.get("fullClassRelease_isInstructorEditable")).booleanValue());
           props.setInstructorEditable(
             "ipAccessType_isInstructorEditable",
             ((Boolean) form.get("ipAccessType_isInstructorEditable")).booleanValue());
           props.setInstructorEditable(
             "passwordRequired_isInstructorEditable",
             ((Boolean) form.get("passwordRequired_isInstructorEditable")).booleanValue());
           props.setInstructorEditable(
             "timedAssessment_isInstructorEditable",
             ((Boolean) form.get("timedAssessment_isInstructorEditable")).booleanValue());
           props.setInstructorEditable(
             "itemAccessType_isInstructorEditable",
             ((Boolean) form.get("itemAccessType_isInstructorEditable")).booleanValue());
           props.setInstructorEditable(
             "displayChunking_isInstructorEditable",
             ((Boolean) form.get("displayChunking_isInstructorEditable")).booleanValue());
           props.setInstructorEditable(
             "submissionModel_isInstructorEditable",
             ((Boolean) form.get("submissionModel_isInstructorEditable")).booleanValue());
           props.setInstructorEditable(
             "lateHandling_isInstructorEditable",
             ((Boolean) form.get("lateHandling_isInstructorEditable")).booleanValue());
           props.setInstructorEditable(
             "submissionMessage_isInstructorEditable",
             ((Boolean) form.get("submissionMessage_isInstructorEditable")).booleanValue());
           props.setInstructorEditable(
             "submissionFinalURL_isInstructorEditable",
             ((Boolean) form.get("submissionFinalURL_isInstructorEditable")).booleanValue());
           props.setInstructorEditable(
             "feedbackType_isInstructorEditable",
             ((Boolean) form.get("feedbackType_isInstructorEditable")).booleanValue());
           props.setInstructorEditable(
             "immediateFeedback_isInstructorEditable",
             ((Boolean) form.get("immediateFeedback_isInstructorEditable")).booleanValue());
           props.setInstructorEditable(
             "testeeIdentity_isInstructorEditable",
             ((Boolean) form.get("testeeIdentity_isInstructorEditable")).booleanValue());
           props.setInstructorEditable(
             "toGradebook_isInstructorEditable",
             ((Boolean) form.get("toGradebook_isInstructorEditable")).booleanValue());
           props.setInstructorEditable(
             "recordedScore_isInstructorEditable",
             ((Boolean) form.get("recordedScore_isInstructorEditable")).booleanValue());
           props.setInstructorEditable(
             "metadataAssess_isInstructorEditable",
             ((Boolean) form.get("metadataAssess_isInstructorEditable")).booleanValue());
           props.setInstructorEditable(
             "metadataParts_isInstructorEditable",
             ((Boolean) form.get("metadataParts_isInstructorEditable")).booleanValue());
           props.setInstructorEditable(
             "metadataQuestions_isInstructorEditable",
             ((Boolean) form.get("metadataQuestions_isInstructorEditable")).booleanValue());
     */
    return true;
  }

  /**
   * This deletes a Part and all of the subsections (questions, media, etc..)
   *
   * @param session DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean doDeletePart(HttpSession session)
  {
    try
    {
      if(LOG.isDebugEnabled())
      {
        LOG.debug("doDeletePart(HttpSession " + session + ")");
      }

      AssessmentServiceDelegate delegate = new AssessmentServiceDelegate();
      SharedManager sm = OsidManagerFactory.createSharedManager();
      Id id = sm.getId((String) session.getAttribute("id"));

      //      Id id = new IdImpl(Long.parseLong((String) session.getAttribute("id")));
      // Remove it from the database
      delegate.deleteSection(id);
      Assessment template =
        (Assessment) session.getAttribute("assessmentTemplate");

      // Remove it from the session
      template.removeSection(id);

      // Remove it from the form
      PartForm pf = (PartForm) session.getAttribute("part");
      SectionIterator si = template.getSections();
      Collection sections = new ArrayList();
      while(si.hasNext())
      {
        Section section = (Section) si.next();
        sections.add(section);
      }

      pf.setPartList(sections);
      session.setAttribute("part", pf);

      return true;
    }
    catch(Exception e)
    {
      LOG.error(e);
//      throw new Error(e);

      return false;
    }
  }

  /**
   * This method handles the preliminaries for editing a question, or creating
   * a question.  The code is much the same for all three, so they're all in
   * one method.
   *
   * @param session DOCUMENTATION PENDING
   * @param httpServletRequest DOCUMENTATION PENDING
   * @param template DOCUMENTATION PENDING
   * @param actionMapping DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean doQuestionEditing(
    HttpSession session, HttpServletRequest httpServletRequest,
    Assessment template, ActionMapping actionMapping)
  {
    LOG.debug("doQuestionEditing()");
    try
    {
      Item item = null;
      SectionIterator iter = template.getSections();
      while(iter.hasNext())
      {
        Section section = (Section) iter.next();

        // If this is a new question
        if(httpServletRequest.getParameter("sectionid") != null)
        {
          LOG.debug("New question.");
          if(
            section.getId().toString().equals(
                httpServletRequest.getParameter("sectionid")))
          {
            // First reset and prefill with template data.
            session.setAttribute("question", null);
            AssessmentPropertiesImpl aprops =
              (AssessmentPropertiesImpl) template.getData();
            Assessment tplate = aprops.getAssessmentTemplate();
            AssessmentTemplatePropertiesImpl tprops =
              (AssessmentTemplatePropertiesImpl) tplate.getData();
            IndexAction.doQuestionForm(session, tplate, aprops, tprops);

            String typeid = httpServletRequest.getParameter("typeid");

            AssessmentServiceDelegate delegate =
              new AssessmentServiceDelegate();
            item =
              delegate.createItem(
                "New Question", "Description",
                new TypeImpl("Stanford", "AAM", "item", typeid));

            // Don't need these anymore -- it's done by constructor.
            //ItemPropertiesImpl myprops = new ItemPropertiesImpl();
            //item.updateData(myprops);
            // Save the section to put it in
            session.setAttribute("question.section", section);

            // New question
            populateQuestion(item, session);

            // Set the question number
            QuestionForm form = (QuestionForm) session.getAttribute("question");
            ItemIterator iiter = section.getItems();
            int num = 1;
            while(iiter.hasNext())
            {
              num++;
              iiter.next();
            }

            form.setNumber(new Integer(num).toString());

            break;
          }
        }

        // If this is an existing question
        else
        {
          ItemIterator iiter = section.getItems();
          String id = httpServletRequest.getParameter("id");
          LOG.debug(
            "Looking at question " + httpServletRequest.getParameter("id"));
          if(id == null)
          {
            id = httpServletRequest.getParameter("moreid");
          }

          // in questionEditor.jsp, question is saved before
          // "dispatching" to other action like add another
          // answer to a MC question, id is stored as attribute
          // during the transit - daisyf (refer to bug #385)
          if(id == null)
          {
            id = (String) session.getAttribute("temp.question.id");
          }

          if(id == null)
          {
            id = (String) session.getAttribute("temp.question.moreid");
          }

          session.removeAttribute("temp.question.id");
          session.removeAttribute("question.section");

          int num = 1;
          while(iiter.hasNext())
          {
            item = iiter.next();
            LOG.debug("Checking if " + item.getId() + " = " + id);
            if(item.getId().toString().equals(id))
            {
              //Editing a question
              populateQuestion(item, session);

              QuestionForm form =
                (QuestionForm) session.getAttribute("question");

              // Set number
              form.setNumber(new Integer(num).toString());

              // need to check session too, in case it is parse
              // through there, bug#385 - daisyf
              if(
                (httpServletRequest.getParameter("moreid") != null) ||
                  (session.getAttribute("temp.question.moreid") != null))
              {
                session.removeAttribute("temp.question.moreid");

                // Set answers
                Collection answers = form.getAnswers();

                AssessmentServiceDelegate delegate =
                  new AssessmentServiceDelegate();
                answers.add(delegate.createAnswer());
                answers.add(delegate.createAnswer());
                form.setAnswers(answers);
                session.setAttribute("question", form);
              }

              break;
            }

            num++;
          }
        }
      }

      return true;
    }
    catch(Exception e)
    {
      LOG.error(e);
//      throw new Error(e);

      return false;
    }
  }

  /**
   * This method handles the preliminaries for deleting a question.
   *
   * @param session DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean doQuestionDeleting(HttpSession session)
  {
    try
    {
      Item item = null;

      Assessment template =
        (Assessment) session.getAttribute("assessmentTemplate");
      SectionIterator iter = template.getSections();
      while(iter.hasNext())
      {
        Section section = (Section) iter.next();

        // If this is an existing question
        if(session.getAttribute("sectionid") == null)
        {
          ItemIterator iiter = section.getItems();
          String id = (String) session.getAttribute("id");
          if(id == null)
          {
            id = (String) session.getAttribute("moreid");
          }

          // in questionEditor.jsp, question is saved before
          // "dispatching" to other action like add another
          // answer to a MC question, id is stored as attribute
          // during the transit - daisyf (refer to bug #385)
          if(id == null)
          {
            id = (String) session.getAttribute("temp.question.id");
          }

          if(id == null)
          {
            id = (String) session.getAttribute("temp.question.moreid");
          }

          session.removeAttribute("temp.question.id");
          while(iiter.hasNext())
          {
            item = iiter.next();
            if(item.getId().toString().equals(id))
            {
              AssessmentServiceDelegate delegate =
                new AssessmentServiceDelegate();
              ItemPropertiesImpl iprops = (ItemPropertiesImpl) item.getData();
              delegate.deleteAnswers(iprops.getAnswers());
              delegate.deleteItem(item.getId());
              section.removeItem(item.getId());

              break;
            }
          }
        }
      }

      return true;
    }
    catch(Exception e)
    {
      LOG.error(e);
//      throw new Error(e);

      return false;
    }
  }

  /**
   * This method sets the preliminary information for access group editing.
   *
   * @param session DOCUMENTATION PENDING
   * @param httpServletRequest DOCUMENTATION PENDING
   * @param properties DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean doStartAccess(
    HttpSession session, HttpServletRequest httpServletRequest,
    AssessmentProperties properties)
  {
    AccessGroup group = null;
    if(properties == null)
    {
      LOG.error("CRISIS: properties = null");
      properties = new AssessmentPropertiesImpl();
    }

    if(properties.getAccessGroups() == null)
    {
      LOG.warn("No access groups found.");
      properties.setAccessGroups(new ArrayList());
    }

    Object[] groups = properties.getAccessGroups().toArray();
    if(groups.length == 0)
    {
      if(
        (session.getAttribute("access") == null) ||
          (((AccessForm) session.getAttribute("access")).getGroups() == null))
      {
        // Reset defaults.  We should never get here.
        Collection groups2 = new ArrayList();
        AccessGroup group2 = new AccessGroup();
        group2.setName("Full Class");
        group2.setIsActive(true);

        // set form defaults for full class
        group2.setReleaseType("0");
        group2.setRetractType("0");
        group2.setRetryAllowed(true);
        group2.setDueDateModel("1");
        groups2.add(group2);

        AccessForm af = new AccessForm();
        af.setGroups(groups2);
        session.setAttribute("access", af);

        LOG.warn("Recreated default groups!  Bad news.");
      }

      groups =
        ((AccessForm) session.getAttribute("access")).getGroups().toArray();
    }

    for(int i = 0; i < groups.length; i++)
    {
      group = (AccessGroup) groups[i];
      String groupName = httpServletRequest.getParameter("name");
      if(groupName == null)
      {
        groupName = "Full Class";
      }

      if(groupName.equals(group.getName()))
      {
        session.setAttribute("getGroup", new Integer(i).toString());
        session.setAttribute("groupName", groupName);

        break;
      }
    }

    return true;
  }

  // ***********************
  // Now the reorder methods
  // ***********************

  /**
   * The start methods just prepare the reorder windows as appropriate for the
   * calling screen.  This one is for Parts.
   *
   * @param session DOCUMENTATION PENDING
   * @param httpServletRequest DOCUMENTATION PENDING
   */
  public void doStartReorderPart(
    HttpSession session, HttpServletRequest httpServletRequest)
  {
    PartReorderForm form = new PartReorderForm();
    PartForm pf = (PartForm) session.getAttribute("part");
    pf.setOriginal_partList(new ArrayList(pf.getPartList()));
    session.setAttribute("part", pf);

    form.setList(pf.getPartList());
    session.setAttribute("partReorder", form);
  }

  // This reorders the part list and sticks it back in the form.
  public String doPartReorder(
    HttpSession session, HttpServletRequest request, Assessment template)
  {
    PartReorderForm form =
      (PartReorderForm) session.getAttribute("partReorder");

    // This is only used if it's a reorder, not a submit
    if(request.getParameter("isMySubmit").equals("false"))
    {
      // Call the method to reset the list, but don't save it
      // anywhere.
      form.getReversedList();

      // Now go back to the reorder page.
      return "partReorder";
    }

    // If we really meant to save it, move on.
    try
    {
      SectionImpl[] sections;

      PartForm pf = (PartForm) session.getAttribute("part");
      ArrayList newPartList = (ArrayList) pf.getPartList();

      sections = new SectionImpl[newPartList.size()];

      for(int i = 0; i < newPartList.size(); i++)
      {
        sections[i] = (SectionImpl) newPartList.get(i);
      }

      template.orderSections(sections);
    }
    catch(Exception e)
    {
      LOG.error(e);
      throw new Error(e);
    }

    return "templateEditor";
  }

  /**
   * The start methods just prepare the reorder windows as appropriate for the
   * calling screen.  This one is for Questions.
   *
   * @param session DOCUMENTATION PENDING
   * @param httpServletRequest DOCUMENTATION PENDING
   * @param template DOCUMENTATION PENDING
   */
  public void doStartReorderQuestion(
    HttpSession session, HttpServletRequest httpServletRequest,
    Assessment template)
  {
    try
    {
      QuestionReorderForm form = new QuestionReorderForm();
      SectionIterator sii = template.getSections();
      Section section = null;

      //Find the right section
      while(sii.hasNext())
      {
        section = (Section) sii.next();
        if(
          section.getId().toString().equals(
              httpServletRequest.getParameter("id")))
        {
          break;
        }
      }

      session.setAttribute("section_id", section.getId());
      ((SectionPropertiesImpl) section.getData()).setOriginal_ItemList(
        new ArrayList(
          ((SectionPropertiesImpl) section.getData()).getItemList()));
      form.setList(((SectionPropertiesImpl) section.getData()).getItemList());
      session.setAttribute("questionReorder", form);
    }
    catch(Exception e)
    {
      LOG.error(e);
      throw new Error(e);
    }
  }

  // This reorders the question list and sticks it back in the form.
  public String doQuestionReorder(
    HttpSession session, HttpServletRequest request, Assessment template)
  {
    QuestionReorderForm form =
      (QuestionReorderForm) session.getAttribute("questionReorder");

    // This is only used if it's a reorder, not a submit
    if(request.getParameter("isMySubmit").equals("false"))
    {
      // Call the method to reset the list, but don't save it
      // anywhere.
      form.getReversedList();

      // Now go back to the reorder page.
      return "questionReorder";
    }

    // If we really meant to save it, move on.
    try
    {
      ItemImpl[] items;
      SectionIterator sii = template.getSections();
      Section section = null;

      //Find the right section
      while(sii.hasNext())
      {
        section = (Section) sii.next();
        if(section.getId() == session.getAttribute("section_id"))
        {
          break;
        }
      }

      ArrayList newPartList =
        (ArrayList) ((SectionPropertiesImpl) section.getData()).getItemList();

      items = new ItemImpl[newPartList.size()];

      for(int i = 0; i < newPartList.size(); i++)
      {
        items[i] = (ItemImpl) newPartList.get(i);
      }

      section.orderItems(items);
    }
    catch(Exception e)
    {
      LOG.error(e);
      throw new Error(e);
    }

    return "templateEditor";
  }
}
