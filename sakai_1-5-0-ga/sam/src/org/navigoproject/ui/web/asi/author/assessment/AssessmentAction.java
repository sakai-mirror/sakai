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
 * Created on Aug 4, 2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.author.assessment;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oroad.stxx.action.Action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.xerces.dom.CharacterDataImpl;
import org.jaxen.JaxenException;
import org.navigoproject.business.entity.Iso8601DateFormat;
import org.navigoproject.business.entity.Iso8601TimeInterval;
import org.navigoproject.business.entity.RecordingData;
import org.navigoproject.business.entity.properties.AssessmentAuthorHelper;
import org.navigoproject.ui.web.FunctionalityDisabler;
import org.navigoproject.ui.web.asi.author.AuthoringHelper;
import org.navigoproject.ui.web.asi.author.section.SectionHelper;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * DOCUMENTATION PENDING
 * 
 * @author rshastri
 * @author Lance Speelmon (question disabling logic)
 * @version $Id: AssessmentAction.java,v 1.70 2004/06/15 20:21:32 lancespeelmon
 *          Exp $
 */
public class AssessmentAction extends Action
{
  private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
      .getLogger(AssessmentAction.class);

  public ActionForward execute(ActionMapping mapping, ActionForm actionForm,
      HttpServletRequest request, HttpServletResponse response)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("perform(ActionMapping " + mapping + ", " + "ActionForm "
          + actionForm + ", " + "HttpServletRequest" + request
          + "HttpServletResponse" + response + ")");
    }

    // disable specific functionality
    FunctionalityDisabler.populateDisableVariables(request);

    //CourseManagementManager courseManager;
    //courseManager.getCourseSection();
    AuthoringHelper authoringHelper = new AuthoringHelper();
    AssessmentActionForm assessmentActionForm = (AssessmentActionForm) actionForm;
    String action = request.getParameter("action");
    if (action == null)
    {
      action = (String)request.getAttribute("action");
    }
    Document document;

    /**
     * @todo: we need both an agent id string and name and assessment and item
     *        to mak ethis unique, this is a placeholder for now. This will,
     *        however use a unique timestamp. Also we may want to move this
     *        invocation to be used only when needed.
     */
    RecordingData recordingData = new RecordingData(authoringHelper
        .getRemoteUserName(request),
        authoringHelper.getRemoteUserName(request), "course settings", null,
        null);

    setRecordingInfoInDocument(recordingData, request);

    String filePath = "/xml/author/";
    AssessmentHelper assessmentHelper = new AssessmentHelper();
    SectionHelper sectionHelper = new SectionHelper();

    if ((action == null) || (action.equals("''")))
    {
      return mapping.findForward("getAssessments");
    }

    else
    {
      document = null;
      if ((action.equals("Import")))
      {
        return mapping.findForward("upload");
      }

      if ((action.equals("Pool Manager")))
      {
        return mapping.findForward("questionChooser");
      }

      if ((action.equals("Templates")))
      {
        return mapping.findForward("TEMPLATEEDITOR");
      }
      if ((action.equals("Responses")))
      {
        return mapping.findForward("totalScores");
      }

      if (action.equals("Delete") || action.equals("Remove"))
      {
        assessmentActionForm.setAssessmentID(request
            .getParameter("assessmentID"));
        return mapping.findForward("delete");
      }

      if (action.equals("Export"))
      {
        return mapping.findForward("EXPORT");
      }

      if (action.equals("Create"))
      {
        //Create new assessment
        String title = request.getParameter("title");
        //title = "\u0F00" + "\u0F01" + "\u0F02"; 

        if ((title == null) || (title.trim().length() < 1))
        {
          LOG.error("Unable to create assessment without a title");
          assessmentActionForm
              .setErrorMsg("UNABLE TO CREATE AN ASSESSMENT WITHOUT  A TITLE");
          return mapping.findForward("getAssessments");
        }
        InputStream inputStream = servlet.getServletContext()
            .getResourceAsStream(filePath + "assessmentTemplate.xml");
        org.navigoproject.business.entity.Assessment assessXml = assessmentHelper
            .readXMLDocument(inputStream);
        // set the creator
        assessXml.setFieldentry("CREATOR", authoringHelper
            .getRemoteUserName(request));

        // creating a assessment using template if template id is provided for
        if (request.getParameter("selectTemplate") != null
            && request.getParameter("selectTemplate").length() > 0)
        {
          AssessmentAuthorHelper templateHelper = new AssessmentAuthorHelper();
          assessXml = templateHelper.getAssessmentMetadata(request
              .getParameter("selectTemplate"), assessXml);
          if (assessXml == null)
          {
            LOG.error("Unable to read template ");
            assessmentActionForm
                .setErrorMsg("UNABLE TO READ ASESSMENT TEMPLATE");
            return mapping.findForward("getAssessments");
          }
        }

        String assessIdent = assessmentHelper.createNewAssessment(title,
            assessXml);
        inputStream = servlet.getServletContext().getResourceAsStream(
            filePath + "sectionTemplate.xml");

        if ((assessIdent == null) || (assessIdent.trim().length() < 1))
        {
          LOG.error("Unable to create assessment. ");
          assessmentActionForm.setErrorMsg("UNABLE TO CREATE ASSESSMENT");
          return mapping.findForward("getAssessments");
        }
        else
        {

          //Default section created for user .
          String sectionIdent = assessmentHelper.createNewSection(assessIdent,
              "Default", inputStream);

          if ((sectionIdent == null) || (sectionIdent.trim().length() < 1))
          {
            LOG.error("Unable to create default section for the assessment. "
                + "The Assessment is created but no default section is: ");

            //	assessmentHelper.clearAssessmentContents(assessIdent);
            AssessmentActionForm form = (AssessmentActionForm) actionForm;
            form
                .setErrorMsg("UNABLE TO CREATE DEFAULT SECTION FOR THE  ASSESSMENT");
            return mapping.findForward("getAssessments");
          }
          else
          {
            assessXml = assessmentHelper.getAssessmentXml(assessIdent);
            assessmentHelper.setAssessmentDocument(assessIdent, request);
            //				*****update Last Modified Date*******************
            assessmentHelper.updateLastModifiedBy(assessIdent);
            return mapping.findForward("showCompleteAssessment");
          }
        }
      }

      if (action.equals("Questions") || action.equals("Test Questions")
          || action.equals("Preview Assessment"))
      {
        String assessmentID = request.getParameter("assessmentID");
        if(assessmentID == null)
        {
          assessmentID = (String)request.getAttribute("assessmentID");
        }

        if (assessmentID != null)
        {
          org.navigoproject.business.entity.Assessment newxmlString = assessmentHelper
              .getComposedAssessment(assessmentID, true);
          if (newxmlString.getIdent() == null
              || newxmlString.getIdent().trim().length() < 1)
          {
            assessmentActionForm.setErrorMsg("Unable to find assessment");
            return mapping.findForward("getAssessments");
          }
          assessmentHelper.setAssessmentDocument(assessmentID, request);
          return mapping.findForward("showCompleteAssessment");
        }
      }

      // add a section
      if (action.equals("Add"))
      {
        return mapping.findForward("sectionAction");
      }

      if (action.equals("Settings") || action.equals("Test Settings"))
      {
        String assessmentID = request.getParameter("assessmentID");
        org.navigoproject.business.entity.Assessment assessXml = null;
        if (assessmentID != null)
        {
          assessXml = assessmentHelper.getAssessmentXml(assessmentID);
        }

        if (assessXml != null)
        {

          assessXml = populatePublishandFeedbackDates(assessXml,
              assessmentActionForm);
          assessXml = populateDurationBeans(assessXml, assessmentActionForm);
          assessmentActionForm.setUsername(authoringHelper
              .getRemoteUserName(request));
          // Lists for Publishing
          assessmentActionForm.setGroupList(authoringHelper
              .getCourseGroup(request));
          assessmentActionForm.setCourseList(authoringHelper
              .getCourseSections(request));
          assessmentActionForm
              .setAssessmentReleasedTo_SelectedList(assessmentHelper
                  .getAssessmentReleasedTo(assessXml, true));
          // end populate lists for publishing
          request.getSession().setAttribute("xmlString", assessXml);

          assessmentHelper.setAssessmentDocument(assessmentID, request);

          String previousPage = request.getParameter("previousPage");
          if((previousPage != null) && (previousPage.equals("showCompleteAssessment")))
          {
            request.setAttribute("previousPage", "showCompleteAssessment");
          }
          
          return mapping.findForward("assessmentSettings");
        }
        else
        {
          LOG.debug("Error retriving the assessment settings");
          assessmentActionForm
              .setErrorMsg("ERROR RETRIVING ASSESSMENT SETTINGS");

          return mapping.findForward("getAssessments");
        }
      }
    }

    //end else
    return mapping.findForward("getAssessments");
  }

  /**
   * This takes a RecordingData object and puts it in the request XML. You can
   * put any Action-specific modifications in here.
   * 
   * @param recordingData
   *          RecordingData encapsulating the audio settings
   * @param request
   *          the request object
   */
  private void setRecordingInfoInDocument(RecordingData recordingData,
      HttpServletRequest request)
  {
    Document document = recordingData.getXMLDataModel();
    saveDocument(request, document);
  }

  private org.navigoproject.business.entity.Assessment populatePublishandFeedbackDates(
      org.navigoproject.business.entity.Assessment assessXml,
      AssessmentActionForm actionForm)
  {
    if (LOG.isDebugEnabled())
    {
      LOG
          .debug("populatePublishDates(org.navigoproject.business.entity.Assessment "
              + assessXml + ", " + "AssessmentActionForm " + actionForm + " )");
    }

    int year;
    int month;
    int date;
    String day;
    int hour;
    int minute = 0;
    try
    {
      String startdateText = assessXml.getFieldentry("START_DATE");
      String enddateText = assessXml.getFieldentry("END_DATE");
      String retractdateText = assessXml.getFieldentry("RETRACT_DATE");
      String feedback_deliveryText = assessXml
          .getFieldentry("FEEDBACK_DELIVERY_DATE");

      actionForm.setPublish_start_hours(0);
      actionForm.setPublish_start_minutes(0);
      actionForm.setPublish_start_day("");
      Calendar startDate = Calendar.getInstance();
      if ((startdateText != null) && (startdateText.length() > 0)
          && (!startdateText.equals("0000-00-00T00:00:00")))
      {
        Iso8601DateFormat startdateISO = new Iso8601DateFormat();
        startDate = startdateISO.parse(startdateText.trim());
      }

      year = startDate.get(Calendar.YEAR);
      month = startDate.get(Calendar.MONTH) + 1;
      date = startDate.get(Calendar.DATE);
      day = month + "/" + date + "/" + year;
      actionForm.setPublish_start_day(day);
      hour = startDate.get(Calendar.HOUR_OF_DAY);
      minute = startDate.get(Calendar.MINUTE);

      if (hour != 0)
      {
        actionForm.setPublish_start_hours(hour);
      }

      if (minute != 0)
      {
        actionForm.setPublish_start_minutes(minute);
      }

      // End Date Calculations
      GregorianCalendar endDate = new GregorianCalendar();
      endDate.add(GregorianCalendar.YEAR, 1);

      actionForm.setPublish_end_day("");
      actionForm.setPublish_end_hours(0);
      actionForm.setPublish_end_minutes(0);
      if ((enddateText != null) && (enddateText.length() > 0)
          && (!enddateText.equals("0000-00-00T00:00:00")))
      {
        Iso8601DateFormat enddateISO = new Iso8601DateFormat();
        endDate = (GregorianCalendar) enddateISO.parse(enddateText.trim());
      }

      year = endDate.get(Calendar.YEAR);
      month = endDate.get(Calendar.MONTH) + 1;
      date = endDate.get(Calendar.DATE);
      day = month + "/" + date + "/" + year;
      actionForm.setPublish_end_day(day);
      hour = endDate.get(Calendar.HOUR_OF_DAY);
      minute = endDate.get(Calendar.MINUTE);
      if (hour != 0)
      {
        actionForm.setPublish_end_hours(hour);
      }

      if (minute != 0)
      {
        actionForm.setPublish_end_minutes(minute);
      }

      //retract Date
      GregorianCalendar retractDate = new GregorianCalendar();

      actionForm.setPublish_retract_day("");
      actionForm.setPublish_retract_hours(0);
      actionForm.setPublish_retract_minutes(0);
      if ((retractdateText != null) && (retractdateText.length() > 0)
          && (!retractdateText.equals("0000-00-00T00:00:00")))
      {
        Iso8601DateFormat retractdateISO = new Iso8601DateFormat();
        retractDate = (GregorianCalendar) retractdateISO.parse(retractdateText
            .trim());

        year = retractDate.get(Calendar.YEAR);
        month = retractDate.get(Calendar.MONTH) + 1;
        date = retractDate.get(Calendar.DATE);
        day = month + "/" + date + "/" + year;
        actionForm.setPublish_retract_day(day);
        hour = retractDate.get(Calendar.HOUR_OF_DAY);
        minute = retractDate.get(Calendar.MINUTE);
        if (hour != 0)
        {
          actionForm.setPublish_retract_hours(hour);
        }

        if (minute != 0)
        {
          actionForm.setPublish_retract_minutes(minute);
        }
      }

      // feedback //FEEDBACK_DELIVERY_DATE feedback_delivery
      GregorianCalendar feedback_deliveryDate = new GregorianCalendar();

      actionForm.setFeedback_delivery_day("");
      actionForm.setFeedback_delivery_hours(0);
      actionForm.setFeedback_delivery_minutes(0);
      if ((feedback_deliveryText != null)
          && (feedback_deliveryText.length() > 0)
          && (!feedback_deliveryText.equals("0000-00-00T00:00:00")))
      {
        Iso8601DateFormat feedback_deliveryISO = new Iso8601DateFormat();
        feedback_deliveryDate = (GregorianCalendar) feedback_deliveryISO
            .parse(feedback_deliveryText.trim());

        year = feedback_deliveryDate.get(Calendar.YEAR);
        month = feedback_deliveryDate.get(Calendar.MONTH) + 1;
        date = feedback_deliveryDate.get(Calendar.DATE);
        day = month + "/" + date + "/" + year;
        actionForm.setFeedback_delivery_day(day);
        hour = feedback_deliveryDate.get(Calendar.HOUR_OF_DAY);
        minute = feedback_deliveryDate.get(Calendar.MINUTE);
        if (hour != 0)
        {
          actionForm.setFeedback_delivery_hours(hour);
        }

        if (minute != 0)
        {
          actionForm.setFeedback_delivery_minutes(minute);
        }
      }
    }

    catch (DOMException ex)
    {
      LOG.error(ex.getMessage(), ex);
    }

    catch (Exception ex)
    {
      LOG.error(ex.getMessage(), ex);
    }

    return assessXml;
  }

  private org.navigoproject.business.entity.Assessment populateDurationBeans(
      org.navigoproject.business.entity.Assessment assessXml,
      AssessmentActionForm actionForm)
  {
    if (LOG.isDebugEnabled())
    {
      LOG
          .debug("populateDurationBeans(org.navigoproject.business.entity.Assessment "
              + assessXml + ", " + "AssessmentActionForm " + actionForm + " )");
    }

    List durationList;
    try
    {
      durationList = assessXml
          .selectNodes("questestinterop/assessment/duration");
      if (durationList.size() > 0)
      {
        Element duration = (Element) durationList.get(0);
        CharacterDataImpl durationText = (CharacterDataImpl) duration
            .getFirstChild();

        Integer getTime = null;
        if ((durationText != null) && (durationText.getNodeValue() != null)
            && (durationText.getNodeValue().trim().length() > 0))
        {
          Iso8601TimeInterval durationISO = new Iso8601TimeInterval(
              durationText.getNodeValue().trim());
          durationISO.createString();
          assessXml.update("questestinterop/assessment/duration", durationISO
              .toString());

          //begin Populate Beans
          getTime = durationISO.getHours();
          if (getTime != null)
          {
            actionForm.setHours(getTime.intValue());
          }
          else
          {
            actionForm.setHours(0);
          }

          getTime = durationISO.getMinutes();
          if (getTime != null)
          {
            actionForm.setMinutes(getTime.intValue());
          }
          else
          {
            actionForm.setMinutes(0);
          }

          getTime = durationISO.getWeeks();
          if (getTime != null)
          {
            actionForm.setWeeks(getTime.intValue());
          }
          else
          {
            actionForm.setWeeks(0);
          }

          getTime = durationISO.getDays();
          if (getTime != null)
          {
            actionForm.setDays(getTime.intValue());
          }
          else
          {
            actionForm.setDays(0);
          }

        }
      }
    }

    catch (JaxenException ex)
    {
      LOG.error(ex.getMessage(), ex);
    }

    catch (DOMException ex)
    {
      LOG.error(ex.getMessage(), ex);
    }

    catch (IOException ex)
    {
      LOG.error(ex.getMessage(), ex);
    }

    catch (Exception ex)
    {
      LOG.error(ex.getMessage(), ex);
    }

    return assessXml;
  }
}