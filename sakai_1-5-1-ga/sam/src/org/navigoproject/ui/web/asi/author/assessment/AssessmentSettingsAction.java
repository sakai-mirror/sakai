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

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.navigoproject.osid.assessment.impl.AssessmentManagerImpl;
import org.navigoproject.osid.assessment.impl.PublishingRequest;
import org.navigoproject.osid.assessment.impl.PublishingService;
import org.navigoproject.ui.web.asi.author.AuthoringHelper;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;

import osid.assessment.Assessment;
import osid.assessment.AssessmentException;
import osid.shared.Id;
import osid.shared.SharedException;
import osid.shared.SharedManager;

import com.oroad.stxx.action.Action;

/**
 * DOCUMENTATION PENDING
 *
 * @author $author$
 * @version $Id: AssessmentSettingsAction.java,v 1.2 2004/08/03 18:18:23 lydial.stanford.edu Exp $
 */
public class AssessmentSettingsAction
  extends Action
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(AssessmentSettingsAction.class);

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

    try
    {
      AuthoringHelper authoringHelper = new AuthoringHelper();
      AssessmentHelper assessmentHelper = new AssessmentHelper();
      AssessmentActionForm assessmentActionForm =
        (AssessmentActionForm) actionForm;
      AssessmentManagerImpl assessmentManager = new AssessmentManagerImpl();
      String action = request.getParameter("action");
      org.navigoproject.business.entity.Assessment assessXml = null;
      Document document;

      if(
        (action == null) || (action.equals("''")) || (action.equals("       OK       ")) ||
          (action.equals("Cancel")))
      {
        if(action.equals("Cancel"))
        {
          String assessmentID = request.getParameter("assessmentID");
          String previousPage = request.getParameter("previousPage");
          if((previousPage != null) && (previousPage.equals("showCompleteAssessment")))
          {
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
          return mapping.findForward("getAssessments");
        }
          
        return mapping.findForward("getAssessments");
      }
      else
      {
        document = null;
        if(action.equals("Save and Publish Assessment") || action.equals("Save Settings"))
        {
          if(
            (
                request.getParameter("stxx/questestinterop/assessment/@title") == null
              ) ||
              (
                request.getParameter("stxx/questestinterop/assessment/@title")
                         .trim().length() < 1
              ))
          {
            assessmentActionForm.setErrorMsg(
              "UNABLE TO SAVE ASSESSMENT SETTINGS WITHOUT A TITLE");

            return mapping.findForward("getAssessments");
          }

          assessXml = saveSettings(request);

          // if it is not timed assessment then auto submit can not be true.
//          if(
//            (assessXml.getFieldentry("CONSIDER_DURATION") != null) &&
//              assessXml.getFieldentry("CONSIDER_DURATION").equals("False"))
//          {
//            assessXml.setFieldentry("AUTO_SUBMIT", "False");
//          }

          String assessmentID = request.getParameter("assessmentID");
          Assessment assess = assessmentHelper.getAssessment(assessmentID);
          String title =
            request.getParameter("stxx/questestinterop/assessment/@title");
          assess.updateDisplayName(title);
          assess.updateData(assessXml);

          if(action.equals("Save and Publish Assessment"))
          {
 
            String start_string = assessXml.getFieldentry("START_DATE").replaceFirst("T", " ");
            Timestamp start_date = Timestamp.valueOf(start_string);
                
            String end_string = assessXml.getFieldentry("END_DATE").replaceFirst("T", " ");  
            Timestamp end_date = Timestamp.valueOf(end_string);
                
            String retract_string = assessXml.getFieldentry("RETRACT_DATE").replaceFirst("T", " ");
            Timestamp retract_date = Timestamp.valueOf(retract_string);
              
            String feedback_string = assessXml.getFieldentry("FEEDBACK_DELIVERY_DATE").replaceFirst("T", " "); 
            Timestamp feedback_date = Timestamp.valueOf(feedback_string);
                
           
            Calendar startDate = null;
            if (start_string.length() >= 10 && start_string.substring(0,10).equals("0000-00-00")){
              // Ensure if date was not set in ui then nulls are placed in db
              //startDate = null;
            }
            else{
              startDate = Calendar.getInstance();
              startDate.setTimeInMillis(start_date.getTime());  
            }
            
            Calendar endDate = null;
            if (end_string.length() >= 10 && end_string.substring(0,10).equals("0000-00-00")){
              // Ensure if date was not set in ui then nulls are placed in db
              //endDate = null;
            }
            else{
              endDate = Calendar.getInstance();
              endDate.setTimeInMillis(end_date.getTime());  
            }         
            
            Calendar retractDate = null;
            if (retract_string.length() >= 10 && retract_string.substring(0,10).equals("0000-00-00")){
              // Ensure if date was not set in ui then nulls are placed in db
              //retractDate = null;
            }
            else{
              retractDate = Calendar.getInstance();
              retractDate.setTimeInMillis(retract_date.getTime());  
            }            
            
            Calendar feedbackDate = null;
            if (feedback_string.length() >= 10 && feedback_string.substring(0,10).equals("0000-00-00")){
              // Ensure if date was not set in ui then nulls are placed in db
              //feedbackDate = null;
            }
            else{
              feedbackDate = Calendar.getInstance();
              feedbackDate.setTimeInMillis(feedback_date.getTime());  
            }                                               

            
            //*****************New Publishing Code***********************************************
            PublishingService publishingService =
              PublishingService.getInstance();
            PublishingRequest publishingRequest = new PublishingRequest();
            ArrayList oldReleasedTo =
              assessmentHelper.getAssessmentReleasedTo(assessXml, false);
            ArrayList releasedTo = new ArrayList();
            if(oldReleasedTo != null)
            {
              Iterator iter = oldReleasedTo.iterator();
              while(iter.hasNext())
              {
                String id = (String) iter.next();
                if((id != null) && id.equals("Anonymous"))
                {
                  id = publishingService.getAnonymousId().getIdString();
                }

                if((id != null) && id.equals("Authenticated users"))
                {
                  id =
                    publishingService.getAuthenticatedUsersId().getIdString();
                }

                releasedTo.add(id);
              }
            }

            ArrayList confirmList = new ArrayList();
            SharedManager sharedManager =
                org.navigoproject.osid.OsidManagerFactory.createSharedManager();
            if(oldReleasedTo != null)
            {
              Iterator iter = oldReleasedTo.iterator();
              while(iter.hasNext())
              {
                String id = (String) iter.next();
                if((id != null) && id.equals("Anonymous"))
                {
                  confirmList.add(id);
                }
                else if((id != null) && id.equals("Authenticated users"))
                {
                  confirmList.add(id);
                }
                else
                {
                  String agentName = sharedManager.getAgent(sharedManager.getId(id)).getDisplayName();
                  confirmList.add(agentName);
                }
              }
            }

            
            publishingRequest.setAssessmentDisplayName(title);
            publishingRequest.setAgentIdStrings(releasedTo);
            publishingRequest.setAssessmentDescription(assess.getDescription());
            publishingRequest.setCoreAssessmentIdString(
              assess.getId().getIdString());
            publishingRequest.setAuthorId(
              authoringHelper.getRemoteUserID(request).getIdString());

            //TODO to be implemented
            //						if(assessXml.getFieldentry("CONSIDER_START_DATE").equals("True"))
            //						{
            publishingRequest.setBeginDate(startDate);

            //						}
            //						if(assessXml.getFieldentry("CONSIDER_END_DATE").equals("True"))
            //						{
            publishingRequest.setDueDate(endDate);
            publishingRequest.setRetractDate(retractDate);

            //						}
            publishingRequest.setFeedbackDate(retractDate);
            publishingRequest.setFeedbackType(
              assessXml.getFieldentry("FEEDBACK_DELIVERY"));
            if(assessXml.getFieldentry("CONSIDER_RETRACT_DATE").equals("True"))
            {
              publishingRequest.setRetractDate(feedbackDate);
            }

            publishingRequest.setMaxAttempts(
              Integer.valueOf(assessXml.getFieldentry("MAX_ATTEMPTS")));
            publishingRequest.setCreatedDate(Calendar.getInstance());
            publishingRequest.setAutoSubmit(
              assessXml.getFieldentry("AUTO_SUBMIT"));
            publishingRequest.setTestDisabled("False");
            publishingRequest.setIpRestrictions(assessXml.getFieldentry("ALLOW_IP"));
            publishingRequest.setUsernameRestriction(assessXml.getFieldentry("USERID"));
            publishingRequest.setPasswordRestriction(assessXml.getFieldentry("PASSWORD"));
            Character lateHandling = new Character('F');
            if("True".equals(assessXml.getFieldentry("LATE_HANDLING")))
            {
              lateHandling = new Character('T');
            }
            publishingRequest.setLateHandling(lateHandling);

            Character autoSave= new Character('F');
            if("True".equals(assessXml.getFieldentry("AUTO_SAVE")))
            {
              autoSave = new Character('T');
            }
            publishingRequest.setAutoSave(autoSave);


            Id publishedId =
              publishingService.createPublishedAssessment(publishingRequest);
            if(publishedId == null)
            {
              LOG.error("Could not publish assessment");
              assessmentActionForm.setErrorMsg("UNABLE TO PUBLISH ASSESSMENT");

              return mapping.findForward("getAssessments");
            }

            //*********************End new publishing code****************************************
            assessmentHelper.setAssessmentDocument(assessmentID, request);

            //**********Set publishing confirmation settings*********************
            if(startDate != null)
            {
              assessmentActionForm.setConfirmation_start_date(
                start_date.toString());
            }
            else
            {
              assessmentActionForm.setConfirmation_start_date("");
            }

            if(endDate != null)
            {
              assessmentActionForm.setConfirmation_end_date(
                end_date.toString());
            }
            else
            {
              assessmentActionForm.setConfirmation_end_date("");
            }

            if(retractDate != null)
            {
              assessmentActionForm.setConfirmation_retract_date(
                retract_date.toString());
            }
            else
            {
              assessmentActionForm.setConfirmation_retract_date("");
            }

            assessmentActionForm.setConfirmation_max_attemps(
              assessXml.getFieldentry("MAX_ATTEMPTS"));
            assessmentActionForm.setConfirmation_released_to(confirmList);

            assessmentActionForm.setConfirmation_publishedId(
              publishedId.getIdString());

            //*****update Last Modified Date*******************
            assessmentHelper.updateLastModifiedBy(assessmentID);

            String uriPrefix = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

            request.setAttribute("URI_PREFIX",uriPrefix);
            request.setAttribute("PUBLISHED_ID",publishedId.getIdString());

            return mapping.findForward("publishConfirm");
          }

          if(action.equals("Save Settings"))
          {
            //					*****update Last Modified Date*******************
            assessmentHelper.updateLastModifiedBy(assessmentID);
            
            String previousPage = request.getParameter("previousPage");
            if((previousPage != null) && (previousPage.equals("showCompleteAssessment")))
            {
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

            return mapping.findForward("getAssessments");
          }
        }
      }
    }
    catch(SharedException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(AssessmentException e1)
    {
      LOG.error(e1.getMessage(), e1);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
    }

    return mapping.findForward("getAssessments");
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param request DOCUMENTATION PENDING
   * @param authorActionForm DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private org.navigoproject.business.entity.Assessment saveSettings(
    HttpServletRequest request)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("saveSettings(HttpServletRequest " + request + ")");
    }

    AuthoringHelper authoringHelper = new AuthoringHelper();
    org.navigoproject.business.entity.Assessment assessXml =
      (org.navigoproject.business.entity.Assessment) request.getSession()
                                                            .getAttribute(
        "xmlString");
    Map requestMap = request.getParameterMap();
    Set keySet = requestMap.keySet();
    Iterator setIter = keySet.iterator();
    String reqParam = null;
    String key = null;

    while(setIter.hasNext())
    {
      key = setIter.next().toString();

      if(
        ! (key == null) && ! ((key.length()) == 0) &&
          key.startsWith("stxx/questestinterop"))
      {
        reqParam = request.getParameter(key);
        if((reqParam != null) && (! key.endsWith("fieldlabel")))
        {
          key = key.substring(5);
          assessXml = updateXmlString(assessXml, key, reqParam);
        }
      }

      // end if starting with stxx
    }

    //end while
    // for meta data represented in check box .. True/ False only
    ArrayList fieldlabel = new ArrayList();
    fieldlabel.add(0, "CONSIDER_USERID");
    fieldlabel.add(1, "CONSIDER_ALLOW_IP");
    fieldlabel.add(2, "AUTO_SUBMIT");
    fieldlabel.add(3, "CONSIDER_DURATION");
    fieldlabel.add(4, "CONSIDER_START_DATE");
    fieldlabel.add(5, "CONSIDER_END_DATE");
    fieldlabel.add(6, "CONSIDER_RETRACT_DATE");
    fieldlabel.add(7, "FEEDBACK_SHOW_QUESTION");
    fieldlabel.add(8, "FEEDBACK_SHOW_RESPONSE");
    fieldlabel.add(9, "FEEDBACK_SHOW_CORRECT_RESPONSE");
    fieldlabel.add(10, "FEEDBACK_SHOW_STUDENT_SCORE");
    fieldlabel.add(11, "FEEDBACK_SHOW_ITEM_LEVEL");
    fieldlabel.add(12, "FEEDBACK_SHOW_SELECTION_LEVEL");
    fieldlabel.add(13, "FEEDBACK_SHOW_GRADER_COMMENT");
    fieldlabel.add(14, "FEEDBACK_SHOW_STATS");
    fieldlabel.add(15, "ANONYMOUS_GRADING");
    fieldlabel.add(16, "COLLECT_ITEM_METADATA");
    fieldlabel.add(17, "COLLECT_SECTION_METADATA");

    for(int i = 0; i < fieldlabel.size(); i++)
    {
      String fl = (String) fieldlabel.get(i);
      String fieldentry =
        "questestinterop/assessment/qtimetadata/qtimetadatafield[" +
        request.getParameter(fl) + "]/fieldentry";
      reqParam = request.getParameter(fieldentry);
      if((reqParam != null) && reqParam.equals("on"))
      {
        assessXml = updateXmlString(assessXml, fieldentry, "True");
      }
      else
      {
        assessXml = updateXmlString(assessXml, fieldentry, "False");
      }
    }

    //End Metadata represented in check box
    // Max Attempts
    String maxAttemptValue = "";
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

    if((maxAttemptValue != null) && (maxAttemptValue.trim().length() > 0))
    {
      assessXml.setFieldentry("MAX_ATTEMPTS", maxAttemptValue);
    }

    if(
      (request.getParameter("feedbackswitch") != null) &&
        request.getParameter("feedbackswitch").equals("on"))
    {
      assessXml =
        updateXmlString(
          assessXml,
          "questestinterop/assessment/assessmentcontrol/@feedbackswitch", "Yes");
    }

    else
    {
      assessXml =
        updateXmlString(
          assessXml,
          "questestinterop/assessment/assessmentcontrol/@feedbackswitch", "No");
    }

    // Duration  & publish Dates specific.
    String duration = returnISO8601Duration(request);

    String startDate = returnISO8601Date(request, "start");
    String endDate = returnISO8601Date(request, "end");
    String retractDate = returnISO8601Date(request, "retract");
    String feedback_deliveryDate =
      returnISO8601Date(request, "feedback_delivery");
    assessXml.setFieldentry("START_DATE", startDate);
    assessXml.setFieldentry("END_DATE", endDate);
    assessXml.setFieldentry("RETRACT_DATE", retractDate);
    assessXml.setFieldentry("FEEDBACK_DELIVERY_DATE", feedback_deliveryDate);
    if((duration != null) && (duration.length() > 0))
    {
      assessXml =
        updateXmlString(
          assessXml, "questestinterop/assessment/duration", duration);
    }

    return assessXml;
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
      }

      hour = request.getParameter("start_hours");
      minute = request.getParameter("start_minutes");
    }

    // end date
    if((TypeofDate != null) && TypeofDate.equals("end"))
    {
      day = request.getParameter("publish_end_day");
      
      if(day == null)
      {
        day = request.getParameter("publish_start_day");
      }
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
      }

      hour = request.getParameter("end_hours");
      minute = request.getParameter("end_minutes");
      
      if(hour == null)
      {
        hour = request.getParameter("start_hours");
      }
      if(minute == null)
      {
        minute = request.getParameter("start_minutes");
      }
    }

    // retract Date 
    if((TypeofDate != null) && TypeofDate.equals("retract"))
    {
      day = request.getParameter("publish_retract_day");

      if(day == null)
      {
        day = request.getParameter("publish_start_day");
      }
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

      if(hour == null)
      {
        hour = request.getParameter("start_hours");
      }
      if(minute == null)
      {
        minute = request.getParameter("start_minutes");
      }
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
      }

      hour = request.getParameter("feedback_delivery_hours");
      minute = request.getParameter("feedback_delivery_minutes");
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
   * @param request DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private String returnISO8601Duration(HttpServletRequest request)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("returnISO8601Duration(HttpServletRequest" + request + ")");
    }

    String duration = "P";

    if(
      (request.getParameter("weeks") != null) &&
        (request.getParameter("weeks").trim().length() > 0))
    {
      duration = duration + request.getParameter("weeks") + "W";
    }

    if(
      (request.getParameter("days") != null) &&
        (request.getParameter("days").trim().length() > 0))
    {
      duration = duration + request.getParameter("days") + "D";
    }

    if(
      (request.getParameter("hours") != null) &&
        (request.getParameter("hours").trim().length() > 0))
    {
      duration = duration + "T" + request.getParameter("hours") + "H";
    }
    else
    {
      duration = duration + "T";
    }

    if(
      (request.getParameter("minutes") != null) &&
        (request.getParameter("minutes").trim().length() > 0))
    {
      duration = duration + request.getParameter("minutes") + "M";
    }

    //End Calculating duration
    return duration;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param xmlString DOCUMENTATION PENDING
   * @param key DOCUMENTATION PENDING
   * @param reqParam DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private org.navigoproject.business.entity.Assessment updateXmlString(
    org.navigoproject.business.entity.Assessment assessXML, String key,
    String reqParam)
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug(
        "updateXmlString(org.navigoproject.business.entity.Assessment" +
        assessXML + ", String" + key + ", String" + reqParam + ")");
    }

    try
    {
      assessXML =
        new org.navigoproject.business.entity.Assessment(
          (assessXML.update(key, reqParam)).getDocument());
    }
    catch(DOMException e)
    {
      LOG.error(e.getMessage(), e);
    }
    catch(Exception e)
    {
      LOG.error(e.getMessage(), e);
    }

    return assessXML;
  }
}
