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
 * Created on May 19, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.author.assessment.published;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.navigoproject.osid.assessment.impl.PublishingRequest;
import org.navigoproject.osid.assessment.impl.PublishingService;
import org.navigoproject.ui.web.asi.author.AuthoringHelper;
import org.navigoproject.ui.web.asi.author.assessment.AssessmentHelper;
import org.w3c.dom.Document;

import osid.OsidException;
import osid.shared.SharedException;

import com.oroad.stxx.action.Action;

/**
 * @author rshastri
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PublishedAssessmentAction
  extends Action
{
  /**
   *
   */
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(PublishedAssessmentAction.class);

  /**
   * DOCUMENTATION PENDING
   *
   * @param mapping DOCUMENTATION PENDING
   * @param actionForm DOCUMENTATION PENDING
   * @param request DOCUMENTATION PENDING
   * @param response DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public ActionForward execute(
    ActionMapping mapping, ActionForm actionForm, HttpServletRequest request,
    HttpServletResponse response)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "perform(ActionMapping " + mapping + ", " + "ActionForm " + actionForm +
        ", " + "HttpServletRequest" + request + "HttpServletResponse" +
        response + ")");
    }

    AuthoringHelper authoringHelper = new AuthoringHelper();
    AssessmentHelper assessmentHelper = new AssessmentHelper();
    PublishedAssessmentActionForm publishedAssessmentActionForm =
      (PublishedAssessmentActionForm) actionForm;
    PublishingRequest pb = new PublishingRequest();
    ArrayList publishedto = new ArrayList();
    PublishingService publishingService = PublishingService.getInstance();
    String action = request.getParameter("action");
    Document document;
    if((action == null) || (action.equals("''")))
    {
      return mapping.findForward("authoringMain");
    }

    else
    {
      document = null;

      if(action.equals("Settings"))
      {
        String assessmentID = request.getParameter("assessmentID");
        String publishedID = request.getParameter("publishedID");
        org.navigoproject.business.entity.Assessment assessXml = null;
        if((publishedID != null) && (publishedID.trim().length() > 0))
        {
          pb =
            publishingService.getPublishedAssessmentSettings(
              authoringHelper.getId(publishedID));
        }
        else
        {
          LOG.debug("Error retriving the core assessment");
          request.setAttribute("Error", "Error");
          request.setAttribute(
            "ErrorID", "Error retriving the published assessment:No ID found");

          return mapping.findForward("authoringMain");
        }

        if((assessmentID != null) && (assessmentID.trim().length() > 0))
        {
         // assessXml = assessmentHelper.getAssessmentXml(assessmentID);
				 assessXml = assessmentHelper.getDatedAssessmentXml(assessmentID, pb.getCreatedDate());
        }
        else
        {
          LOG.debug("Error retriving the core assessment");
          request.setAttribute("Error", "Error");
          request.setAttribute(
            "ErrorID", "Error retriving the core assessment: No ID found");

          return mapping.findForward("authoringMain");
        }

        if((assessXml != null) && (pb != null))
        {
          //LOG.debug(assessXml.stringValue());
          populatePublishandFeedbackDates(
            pb.getBeginDate(), pb.getDueDate(), pb.getRetractDate(),
            pb.getFeedbackDate(), publishedAssessmentActionForm);
          publishedAssessmentActionForm.setUsername(
            authoringHelper.getRemoteUserName(request));

          // Lists for Publishing  
          publishedAssessmentActionForm.setGroupList(
            authoringHelper.getCourseGroup(request));
          publishedAssessmentActionForm.setCourseList(
            authoringHelper.getCourseSections(request));
          Iterator locations = pb.getAgentIdStrings();
          if(locations != null)
          {
            while(locations.hasNext())
            {
							String id = (String) locations.next();
							if(id!=null)
							{
			
								if(id.equals("Anonymous"))
								{
									publishedto.add("Anonymous" + "+"+"Anonymous");
								}
								else if(id.equals("Authenticated users"))
								{
									publishedto.add("Authenticated users" + "+"+"Authenticated users");
								}
								else
								{
									String nameOfID = authoringHelper.getIdName(id);
									publishedto.add(id+"+"+nameOfID);	
								}
			 				}		
            
            }
          }

          publishedAssessmentActionForm.setAssessmentReleasedTo_SelectedList(
            publishedto);
          publishedAssessmentActionForm.setIsActive(
            request.getParameter("isActive"));
          publishedAssessmentActionForm.setMaxAttempts(
            pb.getMaxAttempts().toString());
          publishedAssessmentActionForm.setPublishedID(publishedID);

          // end populate lists for publishing
          request.getSession().setAttribute("xmlString", assessXml);
          assessmentHelper.setAssessmentDocumentByDate(assessmentID, pb.getCreatedDate(), request);

          //for showing URL in settings for published assessment.
          String uriPrefix = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
          request.setAttribute("URI_PREFIX",uriPrefix);
          request.setAttribute("PUBLISHED_ID", publishedID);
          
          String publishedName = request.getParameter("publishedName");
          if((publishedName != null) &&(!publishedName.equals("")))
          {
            publishedAssessmentActionForm.setPublishedName(publishedName);
          }


          return mapping.findForward("settings");
        }
        else
        {
          LOG.debug("Error retriving the assessment settings");
          request.setAttribute("Error", "Error");
          request.setAttribute(
            "ErrorID", "Error retriving the published assessment");

          return mapping.findForward("authoringMain");
        }
      }

      //Save Changes
      if(action.equals("Change Settings"))
      {
        String publishedID = request.getParameter("publishedID");
        if((publishedID != null) && (publishedID.trim().length() > 0))
        {
          pb =
            publishingService.getPublishedAssessmentSettings(
              authoringHelper.getId(publishedID));
        }
        else
        {
          LOG.debug("Error retriving the core assessment");
          request.setAttribute("Error", "Error");
          request.setAttribute(
            "ErrorID", "Error retriving the published assessment:No ID found");

          return mapping.findForward("authoringMain");
        }

        if((pb != null))
        {
          Timestamp start_date =
            Timestamp.valueOf(
              returnISO8601Date(request, "start").replaceFirst("T", " "));
          Timestamp end_date =
            Timestamp.valueOf(
              returnISO8601Date(request, "end").replaceFirst("T", " "));
          Timestamp retract_date =
            Timestamp.valueOf(
              returnISO8601Date(request, "retract").replaceFirst("T", " "));
          Timestamp feedback_date =
            Timestamp.valueOf(
              returnISO8601Date(request, "feedback_delivery").replaceFirst(
                "T", " "));
          Calendar startDate = Calendar.getInstance();
          startDate.setTimeInMillis(start_date.getTime());
          Calendar endDate = Calendar.getInstance();
          endDate.setTimeInMillis(end_date.getTime());
          Calendar retractDate = Calendar.getInstance();
          if(retract_date.getTime() > 0)
          {
            retractDate.setTimeInMillis(retract_date.getTime());
          }
          else
          {
            retractDate = null;
          }

          Calendar feedbackDate = Calendar.getInstance();
          if(feedback_date.getTime() > 0)
          {
            feedbackDate.setTimeInMillis(feedback_date.getTime());
          }
          else
          {
            feedbackDate = null;
          }

          //collect max Attempts
          String maxAttemptValue = null;
          if(
            ! (
                (request.getParameter("unlimitedAttempts") != null) &&
                request.getParameter("unlimitedAttempts").equals("'Yes'")
              ))
          {
            maxAttemptValue = request.getParameter("maxAttemps");
          }
          else
          {
            maxAttemptValue = "0";
          }

          Integer max = null;
          if(
            (maxAttemptValue != null) && (maxAttemptValue.trim().length() > 0) &&
              ((new Integer(maxAttemptValue)) != null))
          {
            max = new Integer(maxAttemptValue);
          }

          //collect Assigned to 
          ArrayList oldReleasedTo =
            authoringHelper.changeDelimitedStringtoArray(
              request.getParameter("assignedTo"), ",");
          ArrayList releasedTo = new ArrayList();
          if(oldReleasedTo != null && oldReleasedTo.size()>0)
          {
            Iterator iter = oldReleasedTo.iterator();
            while(iter.hasNext())
            {
              String id = (String) iter.next();
              try
              {
                if((id != null) && id.equals("Anonymous"))
                {
                  id = publishingService.getAnonymousId().getIdString();
                }

                if((id != null) && id.equals("Authenticated users"))
                {
                  id =
                    publishingService.getAuthenticatedUsersId().getIdString();
                }
              }
              catch(SharedException e)
              {
                LOG.error(e.getMessage(), e);
              }

              releasedTo.add(id);
            }
          }

          //modify assessment
          pb.setBeginDate(startDate);
          pb.setDueDate(endDate);
          pb.setRetractDate(retractDate);
          pb.setFeedbackDate(feedbackDate);
          pb.setMaxAttempts(max);
          pb.setAgentIdStrings(releasedTo);
          try
          {
            publishingService.modifyPublishedAssessment(
              authoringHelper.getId(publishedID), pb);
          }
          catch(OsidException e)
          {
            LOG.error("Unable to modify published assessment");
            request.setAttribute("Error", "Error");
            request.setAttribute(
              "ErrorID",
              "Error retriving the published assessment: Modify failed");

            return mapping.findForward("authoringMain");
          }
        }

        else
        {
          LOG.debug("Error retriving the published assessment");
          request.setAttribute("Error", "Error");
          request.setAttribute(
            "ErrorID", "Error retriving the published assessment: Modify failed");

          return mapping.findForward("authoringMain");
        }
      }

      if(action.equals("Revoke Published Assessment"))
      {
        String publishedID = request.getParameter("publishedID");
        if((publishedID != null) && (publishedID.trim().length() > 0))
        {
          publishedAssessmentActionForm.setPublishedID(publishedID);

          return mapping.findForward("revokeConfirmation");
        }
        else
        {
          LOG.debug("Error retriving the published assessment");
          request.setAttribute("Error", "Error");
          request.setAttribute(
            "ErrorID",
            "Error retriving the published assessment : Could not revoke assessment");

          return mapping.findForward("authoringMain");
        }
      }

      if(action.equals("Revoke"))
      {
        String publishedID = request.getParameter("publishedID");
        if((publishedID != null) && (publishedID.trim().length() > 0))
        {
          publishingService.revokePublishedAssessment(
            authoringHelper.getId(publishedID));
        }
        else
        {
          LOG.debug("Error retriving the published assessment");
          request.setAttribute("Error", "Error");
          request.setAttribute(
            "ErrorID",
            "Error retriving the published assessment : Could not revoke assessment");

          return mapping.findForward("authoringMain");
        }
      }
    }

    // else if action is  not null 
    return mapping.findForward("authoringMain");
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   * @param TypeofDate DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private String returnISO8601Date(
    HttpServletRequest request, String TypeofDate)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "returnISO8601Date(HttpServletRequest " + request + ",String " +
        TypeofDate + ")");
    }

    int month = 0;
    int date = 0;
    int year = 0;
    String day = "";

    String hour = "00";
    String minute = "00";
    String ISOdate = "";
    if((TypeofDate != null) && TypeofDate.equals("start"))
    {
      day = request.getParameter("publish_start_day");
      if((day != null) && (day.length() > 0))
      {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date startDate = null;
        try
        {
          startDate = formatter.parse(day);
        }
        catch(ParseException e)
        {
          LOG.error(e.getMessage(), e);
        }

        Calendar sDate = new GregorianCalendar();
        sDate.setTime(startDate);
        year = sDate.get(Calendar.YEAR);
        month = sDate.get(Calendar.MONTH) + 1;
        date = sDate.get(Calendar.DATE);
        hour = request.getParameter("start_hours");
        minute = request.getParameter("start_minutes");
      }
    }

    // end date
    if((TypeofDate != null) && TypeofDate.equals("end"))
    {
      day = request.getParameter("publish_end_day");
      if((day != null) && (day.length() > 0))
      {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date endDate = null;
        try
        {
          endDate = formatter.parse(day);
        }
        catch(ParseException e)
        {
          LOG.error(e.getMessage(), e);
        }

        Calendar eDate = new GregorianCalendar();
        eDate.setTime(endDate);
        year = eDate.get(Calendar.YEAR);
        month = eDate.get(Calendar.MONTH) + 1;
        date = eDate.get(Calendar.DATE);
        hour = request.getParameter("end_hours");
        minute = request.getParameter("end_minutes");
      }
    }

    // retract Date 
    if((TypeofDate != null) && TypeofDate.equals("retract"))
    {
      day = request.getParameter("publish_retract_day");
      if((day != null) && (day.length() > 0))
      {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date retractDate = null;
        try
        {
          retractDate = formatter.parse(day);
        }
        catch(ParseException e)
        {
          LOG.error(e.getMessage(), e);
        }

        Calendar eDate = new GregorianCalendar();
        eDate.setTime(retractDate);
        year = eDate.get(Calendar.YEAR);
        month = eDate.get(Calendar.MONTH) + 1;
        date = eDate.get(Calendar.DATE);
      }

      hour = request.getParameter("retract_hours");
      minute = request.getParameter("retract_minutes");
    }

    //FEEDBACK_DELIVERY_DATE feedback_delivery dates
    if((TypeofDate != null) && TypeofDate.equals("feedback_delivery"))
    {
      day = request.getParameter("feedback_delivery_day");
      if((day != null) && (day.length() > 0))
      {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date feedback_deliveryDate = null;
        try
        {
          feedback_deliveryDate = formatter.parse(day);
        }
        catch(ParseException e)
        {
          LOG.error(e.getMessage(), e);
        }

        Calendar eDate = new GregorianCalendar();
        eDate.setTime(feedback_deliveryDate);
        year = eDate.get(Calendar.YEAR);
        month = eDate.get(Calendar.MONTH) + 1;
        date = eDate.get(Calendar.DATE);
        hour = request.getParameter("feedback_delivery_hours");
        minute = request.getParameter("feedback_delivery_minutes");
      }
    }

    // common peice 	 
    String strYear = "0000";
    String strMonth = "00";
    String strDate = "00";

    if((String.valueOf(year)).length() == 4)
    {
      strYear = "" + year;
    }

    if((String.valueOf(month)).length() == 1)
    {
      strMonth = "0" + month;
    }

    if((String.valueOf(month)).length() == 2)
    {
      strMonth = "" + month;
    }

    if((String.valueOf(date)).length() == 1)
    {
      strDate = "0" + date;
    }

    if((String.valueOf(date)).length() == 2)
    {
      strDate = "" + date;
    }

    if(hour.equals("0"))
    {
      hour = "00";
    }

    if(hour.trim().length() == 1)
    {
      hour = "0" + hour;
    }

    if(minute.equals("0"))
    {
      minute = "00";
    }

    if(minute.trim().length() == 1)
    {
      minute = "0" + minute;
    }

    ISOdate =
      ISOdate + strYear + "-" + strMonth + "-" + strDate + "" + "T" + hour +
      ":" + minute + ":00";

    return ISOdate;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param startDate DOCUMENTATION PENDING
   * @param endDate DOCUMENTATION PENDING
   * @param retractDate DOCUMENTATION PENDING
   * @param feedbackDeliveryDate DOCUMENTATION PENDING
   * @param actionForm DOCUMENTATION PENDING
   */
  public void populatePublishandFeedbackDates(
    Calendar startDate, Calendar endDate, Calendar retractDate,
    Calendar feedbackDeliveryDate, PublishedAssessmentActionForm actionForm)
  {
    int year;
    int month;
    int date;
    String day;
    int hour;
    int minute = 0;

    String startdateText = "";
    String enddateText = "";
    String retractdateText = "";
    String feedback_deliveryText = "";

    actionForm.setPublish_start_hours(0);
    actionForm.setPublish_start_minutes(0);
    actionForm.setPublish_start_day("");

    if(startDate != null)
    {
      year = startDate.get(Calendar.YEAR);
      month = startDate.get(Calendar.MONTH) + 1;
      date = startDate.get(Calendar.DATE);
      day = month + "/" + date + "/" + year;
      actionForm.setPublish_start_day(day);
      hour = startDate.get(Calendar.HOUR_OF_DAY);
      minute = startDate.get(Calendar.MINUTE);

      if(hour != 0)
      {
        actionForm.setPublish_start_hours(hour);
      }

      if(minute != 0)
      {
        actionForm.setPublish_start_minutes(minute);
      }
    }

    // End Date Calculations
    //	endDate.add(GregorianCalendar.YEAR, 1);
    actionForm.setPublish_end_day("");
    actionForm.setPublish_end_hours(0);
    actionForm.setPublish_end_minutes(0);

    if(endDate != null)
    {
      year = endDate.get(Calendar.YEAR);
      month = endDate.get(Calendar.MONTH) + 1;
      date = endDate.get(Calendar.DATE);
      day = month + "/" + date + "/" + year;
      actionForm.setPublish_end_day(day);
      hour = endDate.get(Calendar.HOUR_OF_DAY);
      minute = endDate.get(Calendar.MINUTE);
      if(hour != 0)
      {
        actionForm.setPublish_end_hours(hour);
      }

      if(minute != 0)
      {
        actionForm.setPublish_end_minutes(minute);
      }
    }

    //retract Date
    actionForm.setPublish_retract_day("");
    actionForm.setPublish_retract_hours(0);
    actionForm.setPublish_retract_minutes(0);
    if(retractDate != null)
    {
      year = retractDate.get(Calendar.YEAR);
      month = retractDate.get(Calendar.MONTH) + 1;
      date = retractDate.get(Calendar.DATE);
      day = month + "/" + date + "/" + year;
      actionForm.setPublish_retract_day(day);
      hour = retractDate.get(Calendar.HOUR_OF_DAY);
      minute = retractDate.get(Calendar.MINUTE);
      if(hour != 0)
      {
        actionForm.setPublish_retract_hours(hour);
      }

      if(minute != 0)
      {
        actionForm.setPublish_retract_minutes(minute);
      }
    }

    // feedback //FEEDBACK_DELIVERY_DATE feedback_delivery
    actionForm.setFeedback_delivery_day("");
    actionForm.setFeedback_delivery_hours(0);
    actionForm.setFeedback_delivery_minutes(0);

    if(feedbackDeliveryDate != null)
    {
      year = feedbackDeliveryDate.get(Calendar.YEAR);
      month = feedbackDeliveryDate.get(Calendar.MONTH) + 1;
      date = feedbackDeliveryDate.get(Calendar.DATE);
      day = month + "/" + date + "/" + year;
      actionForm.setFeedback_delivery_day(day);
      hour = feedbackDeliveryDate.get(Calendar.HOUR_OF_DAY);
      minute = feedbackDeliveryDate.get(Calendar.MINUTE);
      if(hour != 0)
      {
        actionForm.setFeedback_delivery_hours(hour);
      }

      if(minute != 0)
      {
        actionForm.setFeedback_delivery_minutes(minute);
      }
    }
  }
}
