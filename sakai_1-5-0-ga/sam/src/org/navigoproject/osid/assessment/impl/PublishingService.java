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
 * Created on Mar 12, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.osid.assessment.impl;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.navigoproject.business.entity.AttemptRealizedAssessment;
import org.navigoproject.business.entity.questionpool.model.QuestionPool;
import org.navigoproject.data.PublishedAssessmentBean;
import org.navigoproject.data.QtiSettingsBean;
import org.navigoproject.osid.FunctionLib;
import org.navigoproject.osid.TypeLib;
import org.navigoproject.osid.impl.PersistenceService;
import org.navigoproject.osid.questionpool.QuestionPoolFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import osid.OsidException;
import osid.authentication.AuthenticationException;
import osid.authentication.AuthenticationManager;
import osid.authorization.Authorization;
import osid.authorization.AuthorizationException;
import osid.authorization.AuthorizationIterator;
import osid.authorization.AuthorizationManager;
import osid.authorization.Qualifier;
import osid.coursemanagement.CourseManagementException;
import osid.coursemanagement.CourseManagementManager;
import osid.coursemanagement.CourseSectionIterator;
import osid.dr.DigitalRepositoryException;
import osid.dr.DigitalRepositoryManager;
import osid.shared.Agent;
import osid.shared.AgentIterator;
import osid.shared.Id;
import osid.shared.SharedException;
import osid.shared.SharedManager;
import osid.shared.Type;

/**
 * @author ajpoland
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PublishingService
{
  private static org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(PublishingService.class);
  private SharedManager sm;
  private AuthorizationManager azm;
  private AuthenticationManager anm;
  private DigitalRepositoryManager dm;
  private CourseManagementManager cmm;
  private Agent currentUserAgent;
  public static Qualifier PUBLISHED_ASSESSMENT;
  public static Id publishedAssessmentRootQualifier;

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public static PublishingService getInstance()
  {
    return PersistenceService.getInstance().getPublishingService();
  }

  /**
   * Creates a new PublishingService object.
   *
   * @param request DOCUMENTATION PENDING
   */
  public PublishingService()
  {
//    OsidOwner myOwner = OsidManagerFactory.getOsidOwner();
//    try
//    {
//      azm = OsidManagerFactory.createAuthorizationManager(myOwner);
//      anm = OsidManagerFactory.createAuthenticationManager(myOwner);
//      dm = OsidManagerFactory.createDigitalRepositoryManager(myOwner);
//      cmm =
//        (CourseManagementManager) OsidManagerFactory.createCourseManagementManager(
//          myOwner);
//      sm = OsidManagerFactory.createSharedManager(myOwner);
//
//      //currentUserAgent = sm.getAgent(anm.getUserId(TypeLib.AUTHN_WEB_BASIC));
//    }
//    catch(OsidException e)
//    {
//      LOG.fatal("PublishingService: Unable to create necessary OSID managers");
//      throw new Error(e.getMessage());
//    }

  }

  /* (non-Javadoc)
   * @see org.navigoproject.osid.assessment.PublishingService#getCourseSections(osid.shared.Id)
   */
  public CourseSectionIterator getCourseSectionList(Id authorId)
  {
    LOG.debug("getCourseSectionList(" + authorId + ")");

    if(authorId == null)
    {
      LOG.debug("getCourseSectionList: Null authorId");

      return null;
    }

    CourseSectionIterator courseSections = null;

    try
    {
      courseSections = getCourseManagementManager().getCourseSections(authorId);
    }
    catch(CourseManagementException e)
    {
      LOG.debug("Error getting course list for agent: " + authorId);
    }

    return courseSections;
  }

  /* (non-Javadoc)
   * @see org.navigoproject.osid.assessment.PublishingService#getGroupList(osid.shared.Id)
   */
  public AgentIterator getCourseGroupList()
  {
    LOG.debug("getCourseGroupList()");

    try
    {
      return getSharedManager().getGroupsByType(TypeLib.FILTER_COURSE_AUTHOR);
    }
    catch(OsidException e)
    {
      LOG.debug("Error getting course groups for current author");

      return null;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param publishingRequest DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws AuthorizationException DOCUMENTATION PENDING
   * @throws AssessmentException DOCUMENTATION PENDING
   * @throws DigitalRepositoryException DOCUMENTATION PENDING
   * @throws SharedException DOCUMENTATION PENDING
   */
  public Id createPublishedAssessment(PublishingRequest publishingRequest)
    throws SharedException, OsidException
  {
    return createPublishedAssessment(getSharedManager().createId(), publishingRequest);
  }

  /* (non-Javadoc)
   * @see org.navigoproject.osid.assessment.PublishingService#createAuthorization(osid.assessment.Assessment)
   */
  private Id createPublishedAssessment(
    Id publishedAssessmentId, PublishingRequest publishingRequest)
    throws OsidException
  {
    if(LOG.isDebugEnabled())
    {
      LOG.debug("createPublishedAssessment(PublishingRequest)");
    }

    LOG.debug(publishingRequest);

    if(publishingRequest == null)
    {
      throw new OsidException("publishingRequest is null");
    }

    if(publishingRequest.getCoreAssessmentIdString() == null)
    {
      throw new OsidException("can't authorize null Assessment");
    }

    // Check to make sure all ids are valid
    ListIterator agentIds = publishingRequest.getAgentIdStrings();

    while(agentIds.hasNext())
    {
      String testAgentIdString = (String) agentIds.next();

      Agent testagent = getSharedManager().getAgent(getSharedManager().getId(testAgentIdString));

      if(! testagent.getId().getIdString().equals(testAgentIdString))
      {
        throw new OsidException(
          "agentIdString [" + testAgentIdString + "] is invalid");
      }
    }

    // Make sure author id is valid
    Agent testAuthorAgent =
      getSharedManager().getAgent(getSharedManager().getId(publishingRequest.getAuthorId()));

    if(
      ! testAuthorAgent.getId().getIdString().equals(
          publishingRequest.getAuthorId()))
    {
      throw new OsidException(
        "agentIdString [" + publishingRequest.getAuthorId() + "] is invalid");
    }

    LOG.debug("publishingRequest: " + publishingRequest.toString());
    LOG.debug(
      "Assessment to be published ID: " +
      publishingRequest.getCoreAssessmentIdString());
      
		try
		{			
			publishedAssessmentRootQualifier = getSharedManager().getId("ACL_CREATE");

      if (PUBLISHED_ASSESSMENT == null){      
			  PUBLISHED_ASSESSMENT =
				  getAuthorizationManager().createRootQualifier(
					  publishedAssessmentRootQualifier,
					  "Published Assessment Root Qualifier", "",
					  TypeLib.QUALIFIER_PUBLISHED_ASSESSMENT, null);
      }
		}
		catch(AuthorizationException e)
		{
			LOG.fatal("Unable to handle AuthorizationException");
			throw new Error(e.getMessage());
		}
		catch(SharedException e)
		{
			LOG.fatal("Unable to handle SharedException when creating Functions");
			throw new Error(e.getMessage());
		}
             
    Qualifier qualifier =
      getAuthorizationManager().createQualifier(
        publishedAssessmentId, publishingRequest.getAssessmentDisplayName(),
        publishingRequest.getAssessmentDescription(),
        TypeLib.QUALIFIER_PUBLISHED_ASSESSMENT, PUBLISHED_ASSESSMENT.getId());

    //LOG.debug("Created Qualifier Id is: " + qualifier.getId());
    //  - Create *Grade* authorization for assessment author
    //      GRADE_ASSESSMENT Authorization - no specific start or end date
    Authorization grade_Authorization = null;
    try
    {
      grade_Authorization =
			  getAuthorizationManager().createDatedAuthorization(
          getSharedManager().getId(publishingRequest.getAuthorId()),
          FunctionLib.GRADE_ASSESSMENT.getId(), qualifier.getId(),
          publishingRequest.getBeginDate(), publishingRequest.getDueDate());
    }
    catch(AuthorizationException e1)
    {
      // Please leave this stacktrace in; there's no other effective way
      // to figure out why this isn't working. --rmg
      e1.printStackTrace();
      LOG.debug(
        "Error getting ID from sharedmanager: " +
        publishingRequest.getAuthorId());
      throw new OsidException("Error getting ID from sharedmanager " + e1);
    }
    catch(SharedException e1)
    {
      LOG.debug(
        "Error getting ID from sharedmanager: " +
        publishingRequest.getAuthorId());
      throw new OsidException("Error getting ID from sharedmanager");
    }

    LOG.debug(
      "GRADE_ASSESSMENT authorization: " + grade_Authorization.toString());

    // Debug code: if it's not being published to anyone, there's a bug, so
    // publish it to the author, at least.
    ArrayList alist = new ArrayList();
    alist.add(publishingRequest.getAuthorId());
    // End debug code

    agentIds = publishingRequest.getAgentIdStrings();
    if (!agentIds.hasNext())
      agentIds = alist.listIterator();

    //  Loop through agents list, authorize each one
    // - For each agent create four different authorizations
    //   * Take
    //   * View
    //   * View Feedback
    //   * Available
    while(agentIds.hasNext())
    {
      String agentIdString = (String) agentIds.next();

      Id agentId = null;
      try
      {
        agentId = getSharedManager().getId(agentIdString);

        LOG.debug("Authorizing for agent Id:" + agentId);

        // TAKE_ASSESSMENT Authorization
        Authorization take_Authorization =
				  getAuthorizationManager().createDatedAuthorization(
            agentId, FunctionLib.TAKE_ASSESSMENT.getId(), qualifier.getId(),
            publishingRequest.getBeginDate(), publishingRequest.getDueDate());

        LOG.debug(
          "TAKE_ASSESSMENT authorization: " + take_Authorization.toString());

        //  VIEW_ASSESSMENT Authorization
        Authorization view_Authorization =
				  getAuthorizationManager().createDatedAuthorization(
            agentId, FunctionLib.VIEW_ASSESSMENT.getId(), qualifier.getId(),
            publishingRequest.getBeginDate(), publishingRequest.getRetractDate());

        LOG.debug(
          "VIEW_ASSESSMENT authorization: " + view_Authorization.toString());

        //  VIEW_ASSESSMENT_FEEDBACK Authorization
        Authorization feedback_Authorization =
				  getAuthorizationManager().createDatedAuthorization(
            agentId, FunctionLib.VIEW_ASSESSMENT_FEEDBACK.getId(),
            qualifier.getId(), publishingRequest.getFeedbackDate(),
            publishingRequest.getRetractDate());

        LOG.debug(
          "VIEW_ASSESSMENT_FEEDBACK authorization: " +
          feedback_Authorization.toString());

        //  AVAILABLE_ASSESSMENT Authorization
        Authorization available_Authorization =
				  getAuthorizationManager().createDatedAuthorization(
            agentId, FunctionLib.AVAILABLE_ASSESSMENT.getId(), qualifier.getId(),
            publishingRequest.getBeginDate(), publishingRequest.getRetractDate());

        LOG.debug(
          "AVAILABLE_ASSESSMENT_FEEDBACK authorization: " +
          feedback_Authorization.toString());
      }
      catch(SharedException e)
      {
        LOG.fatal(
          "Error getting agent: " + agentIdString + ": " + e.getMessage());

        throw new OsidException("Error getting agent from sharedmanager");
      }
    }

    
   
   

    Timestamp dueDate = null;
    Timestamp beginDate = null;
    Timestamp createdDate = null;
    Timestamp feedbackDate = null;
    Timestamp retractDate = null;

    if(publishingRequest.getDueDate() != null)
    {
      dueDate = new Timestamp(publishingRequest.getDueDate().getTimeInMillis());
    }

    if(publishingRequest.getBeginDate() != null)
    {
      beginDate =
        new Timestamp(publishingRequest.getBeginDate().getTimeInMillis());
    }

    if(publishingRequest.getCreatedDate() != null)
    {
      createdDate =
        new Timestamp(publishingRequest.getCreatedDate().getTimeInMillis());
    }

    if(publishingRequest.getFeedbackDate() != null)
    {
      feedbackDate =
        new Timestamp(publishingRequest.getFeedbackDate().getTimeInMillis());
    }

    if(publishingRequest.getRetractDate() != null)
    {
      retractDate =
        new Timestamp(publishingRequest.getRetractDate().getTimeInMillis());
    }

    QtiSettingsBean qsb =
      new QtiSettingsBean(
        publishedAssessmentId.toString(), publishingRequest.getAssessmentDisplayName(),
        publishingRequest.getMaxAttempts(),
        publishingRequest.getAutoSubmit(), publishingRequest.getAutoSave(), publishingRequest.getTestDisabled(),        
        beginDate, dueDate, createdDate, feedbackDate, retractDate,
        publishingRequest.getIpRestrictions(),
        publishingRequest.getUsernameRestriction(),
        publishingRequest.getPasswordRestriction(),
        publishingRequest.getFeedbackType(), publishingRequest.getLateHandling());

    PersistenceService.getInstance().getQtiQueries().persistSettingsBean(qsb);

    LOG.debug(
      "QtiSettingsBean - usernameRestriction: " + qsb.getUsernameRestriction());
    LOG.debug(
      "QtiSettingsBean - passwordRestriction: " + qsb.getPasswordRestriction());
    
    PublishedAssessmentBean pab =
         new PublishedAssessmentBean(
           publishedAssessmentId.toString(),
           publishingRequest.getCoreAssessmentIdString());
    
    
   try
    {
      LOG.debug("Persisting PublishedAssessmentBean: "+pab);
      PersistenceService.getInstance().getPublishedAssessmentQueries()
                        .persistPublishedAssessment(pab);

      return publishedAssessmentId;
    }
    catch(Exception e)
    {
      LOG.debug(
        "Error occured while attempting to persist publishedId: " +
        e.getMessage());

      throw new OsidException(
        "Error occured while attempting to persist publishedId");
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param publishedAssessmentId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public PublishingRequest getPublishedAssessmentSettings(
    Id publishedAssessmentId)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("getPublishedAssessmentSettings(Id " + publishedAssessmentId
          + ")");
    }
    PublishingRequest prequest = new PublishingRequest();

    try
    {
      PublishedAssessmentBean pab =
        (PublishedAssessmentBean) PersistenceService.getInstance()
                                                    .getPublishedAssessmentQueries()
                                                    .getByPublishedId(
          publishedAssessmentId.getIdString());

      QtiSettingsBean qsb =
        (QtiSettingsBean) PersistenceService.getInstance().getQtiQueries()
                                            .returnQtiSettingsBean(
          publishedAssessmentId.getIdString());

      Qualifier qualifier = getAuthorizationManager().getQualifier(publishedAssessmentId);

      if(pab == null)
      {
        throw new Error("Failed to retrieve PublishedAssessmentBean; Id was " +
          publishedAssessmentId.getIdString());
      }

      if(qsb == null)
      {
        throw new Error("Failed to retrieve QtiSettingsBean");
      }

      prequest.setCoreAssessmentIdString(pab.getCoreId());

      Calendar beginDate = null;
      Calendar createdDate = null;
      Calendar dueDate = null;
      Calendar feedbackDate = null;
      Calendar retractDate = null;

      if(qsb.getStartDate() != null)
      {
        beginDate = Calendar.getInstance();
        beginDate.setTime(new Date(qsb.getStartDate().getTime()));
      }

      if(qsb.getCreatedDate() != null)
      {
        createdDate = Calendar.getInstance();
        createdDate.setTime(new Date(qsb.getCreatedDate().getTime()));
      }

      if(qsb.getEndDate() != null)
      {
        dueDate = Calendar.getInstance();

        dueDate.setTime(new Date(qsb.getEndDate().getTime()));
      }

      if(qsb.getFeedbackDate() != null)
      {
        feedbackDate = Calendar.getInstance();
        feedbackDate.setTime(new Date(qsb.getFeedbackDate().getTime()));
      }

      if(qsb.getRetractDate() != null)
      {
        retractDate = Calendar.getInstance();
        retractDate.setTime(new Date(qsb.getRetractDate().getTime()));
      }

      prequest.setBeginDate(beginDate);
      prequest.setCreatedDate(createdDate);
      prequest.setDueDate(dueDate);
      prequest.setFeedbackDate(feedbackDate);
      prequest.setRetractDate(retractDate);

      prequest.setIpRestrictions(qsb.getIpRestrictions());
      prequest.setUsernameRestriction(qsb.getUsernameRestriction());
      prequest.setPasswordRestriction(qsb.getPasswordRestriction());

      prequest.setAutoSubmit(qsb.getAutoSubmit());
      prequest.setAutoSave(qsb.getAutoSave());
      prequest.setMaxAttempts(qsb.getMaxAttempts());
      prequest.setTestDisabled(qsb.getTestDisabled());

      prequest.setAssessmentDisplayName(qualifier.getDisplayName());
      prequest.setAssessmentDescription(qualifier.getDescription());

      // Pull TAKE authorizations
      ArrayList agentIdStrings = new ArrayList();

      AuthorizationIterator TAKE_auths =
			  getAuthorizationManager().getExplicitUserAZs(
          FunctionLib.TAKE_ASSESSMENT.getId(), qualifier.getId(), false);

      while(TAKE_auths.hasNext())
      {
        Authorization TAKE_auth = (Authorization) TAKE_auths.next();

        agentIdStrings.add(TAKE_auth.getAgent().getId().toString());
      }

      TAKE_auths =
			  getAuthorizationManager().getExplicitUserAZs(
          FunctionLib.TAKE_ASSESSMENT.getId(), qualifier.getId(), true);

      while(TAKE_auths.hasNext())
      {
        Authorization TAKE_auth = (Authorization) TAKE_auths.next();

        agentIdStrings.add(TAKE_auth.getAgent().getId().toString());
      }

      prequest.setAgentIdStrings(agentIdStrings);

      //Pull GRADE authorization
      AuthorizationIterator GRADE_auths =
			  getAuthorizationManager().getExplicitUserAZs(
          FunctionLib.GRADE_ASSESSMENT.getId(), qualifier.getId(), true);

      if(GRADE_auths.hasNext())
      {
        Authorization GRADE_auth = (Authorization) GRADE_auths.next();

        prequest.setAuthorId(GRADE_auth.getAgent().getId().toString());
      }

      return prequest;
    }
    catch(SharedException e)
    {
      LOG.debug(
        "Error getting publishing request for publishedAssessmentId=" +
        publishedAssessmentId);

      return null;
    }
    catch(AuthorizationException e)
    {
      LOG.debug(
        "Error getting publishing request for publishedAssessmentId=" +
        publishedAssessmentId);

      return null;
    }
    catch (Exception e)
    {
      LOG.debug("Unspecified error: " + e);
      return null;
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param agent DOCUMENTATION PENDING
   * @param pa DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public void revokePublishedAssessment(Id publishedAssessmentId)
  {
    LOG.debug("revokePublishedAssessment(Id=" + publishedAssessmentId);

    try
    {
      Qualifier qualifier = getAuthorizationManager().getQualifier(publishedAssessmentId);

      LOG.debug("qualifier id=" + qualifier.getId());

      AuthorizationIterator TAKE_auths =
			  getAuthorizationManager().getExplicitUserAZs(
          FunctionLib.TAKE_ASSESSMENT.getId(), qualifier.getId(), true);

      if(! TAKE_auths.hasNext())
      {
        LOG.debug("TAKE_auths is empty");
      }

      while(TAKE_auths.hasNext())
      {
        Authorization TAKE_auth = (Authorization) TAKE_auths.next();

        StringBuffer deleteId =
          new StringBuffer(TAKE_auth.getAgent().getId().toString());
        deleteId.append(":");
        deleteId.append(TAKE_auth.getFunction().getId().toString());
        deleteId.append(":");
        deleteId.append(TAKE_auth.getQualifier().getId().toString());

        LOG.debug("Trying to delete TAKE_auth:" + deleteId);

				getAuthorizationManager().deleteAuthorization(getSharedManager().getId(deleteId.toString()));
      }

      AuthorizationIterator GRADE_auths =
			  getAuthorizationManager().getExplicitUserAZs(
          FunctionLib.GRADE_ASSESSMENT.getId(), qualifier.getId(), true);

      if(! GRADE_auths.hasNext())
      {
        LOG.debug("GRADE_auths is empty");
      }

      while(GRADE_auths.hasNext())
      {
        Authorization GRADE_auth = (Authorization) GRADE_auths.next();

        StringBuffer deleteId =
          new StringBuffer(GRADE_auth.getAgent().getId().toString());
        deleteId.append(":");
        deleteId.append(GRADE_auth.getFunction().getId().toString());
        deleteId.append(":");
        deleteId.append(GRADE_auth.getQualifier().getId().toString());

        LOG.debug("Trying to delete GRADE_auth:" + deleteId);

				getAuthorizationManager().deleteAuthorization(getSharedManager().getId(deleteId.toString()));
      }

      AuthorizationIterator VIEW_auths =
			  getAuthorizationManager().getExplicitUserAZs(
          FunctionLib.VIEW_ASSESSMENT.getId(), qualifier.getId(), true);

      while(VIEW_auths.hasNext())
      {
        Authorization VIEW_auth = (Authorization) VIEW_auths.next();

        StringBuffer deleteId =
          new StringBuffer(VIEW_auth.getAgent().getId().toString());
        deleteId.append(":");
        deleteId.append(VIEW_auth.getFunction().getId().toString());
        deleteId.append(":");
        deleteId.append(VIEW_auth.getQualifier().getId().toString());

				getAuthorizationManager().deleteAuthorization(getSharedManager().getId(deleteId.toString()));
      }

      AuthorizationIterator FEEDBACK_auths =
			  getAuthorizationManager().getExplicitUserAZs(
          FunctionLib.VIEW_ASSESSMENT_FEEDBACK.getId(), qualifier.getId(), true);

      while(FEEDBACK_auths.hasNext())
      {
        Authorization FEEDBACK_auth = (Authorization) FEEDBACK_auths.next();

        StringBuffer deleteId =
          new StringBuffer(FEEDBACK_auth.getAgent().getId().toString());
        deleteId.append(":");
        deleteId.append(FEEDBACK_auth.getFunction().getId().toString());
        deleteId.append(":");
        deleteId.append(FEEDBACK_auth.getQualifier().getId().toString());

        getAuthorizationManager().deleteAuthorization(getSharedManager().getId(deleteId.toString()));
      }

      AuthorizationIterator AVAIL_auths =
			  getAuthorizationManager().getExplicitUserAZs(
          FunctionLib.AVAILABLE_ASSESSMENT.getId(), qualifier.getId(), true);

      while(AVAIL_auths.hasNext())
      {
        Authorization AVAIL_auth = (Authorization) AVAIL_auths.next();

        StringBuffer deleteId =
          new StringBuffer(AVAIL_auth.getAgent().getId().toString());
        deleteId.append(":");
        deleteId.append(AVAIL_auth.getFunction().getId().toString());
        deleteId.append(":");
        deleteId.append(AVAIL_auth.getQualifier().getId().toString());

				getAuthorizationManager().deleteAuthorization(getSharedManager().getId(deleteId.toString()));
      }

      TAKE_auths =
			  getAuthorizationManager().getExplicitUserAZs(
          FunctionLib.TAKE_ASSESSMENT.getId(), qualifier.getId(), false);

      if(! TAKE_auths.hasNext())
      {
        LOG.debug("TAKE_auths is empty");
      }

      while(TAKE_auths.hasNext())
      {
        Authorization TAKE_auth = (Authorization) TAKE_auths.next();

        StringBuffer deleteId =
          new StringBuffer(TAKE_auth.getAgent().getId().toString());
        deleteId.append(":");
        deleteId.append(TAKE_auth.getFunction().getId().toString());
        deleteId.append(":");
        deleteId.append(TAKE_auth.getQualifier().getId().toString());

        LOG.debug("Trying to delete TAKE_auth:" + deleteId);

				getAuthorizationManager().deleteAuthorization(getSharedManager().getId(deleteId.toString()));
      }

      GRADE_auths =
			  getAuthorizationManager().getExplicitUserAZs(
          FunctionLib.GRADE_ASSESSMENT.getId(), qualifier.getId(), false);

      if(! GRADE_auths.hasNext())
      {
        LOG.debug("GRADE_auths is empty");
      }

      while(GRADE_auths.hasNext())
      {
        Authorization GRADE_auth = (Authorization) GRADE_auths.next();

        StringBuffer deleteId =
          new StringBuffer(GRADE_auth.getAgent().getId().toString());
        deleteId.append(":");
        deleteId.append(GRADE_auth.getFunction().getId().toString());
        deleteId.append(":");
        deleteId.append(GRADE_auth.getQualifier().getId().toString());

        LOG.debug("Trying to delete GRADE_auth:" + deleteId);

				getAuthorizationManager().deleteAuthorization(getSharedManager().getId(deleteId.toString()));
      }

      VIEW_auths =
			  getAuthorizationManager().getExplicitUserAZs(
          FunctionLib.VIEW_ASSESSMENT.getId(), qualifier.getId(), false);

      while(VIEW_auths.hasNext())
      {
        Authorization VIEW_auth = (Authorization) VIEW_auths.next();

        StringBuffer deleteId =
          new StringBuffer(VIEW_auth.getAgent().getId().toString());
        deleteId.append(":");
        deleteId.append(VIEW_auth.getFunction().getId().toString());
        deleteId.append(":");
        deleteId.append(VIEW_auth.getQualifier().getId().toString());

				getAuthorizationManager().deleteAuthorization(getSharedManager().getId(deleteId.toString()));
      }

      FEEDBACK_auths =
			  getAuthorizationManager().getExplicitUserAZs(
          FunctionLib.VIEW_ASSESSMENT_FEEDBACK.getId(), qualifier.getId(), false);

      while(FEEDBACK_auths.hasNext())
      {
        Authorization FEEDBACK_auth = (Authorization) FEEDBACK_auths.next();

        StringBuffer deleteId =
          new StringBuffer(FEEDBACK_auth.getAgent().getId().toString());
        deleteId.append(":");
        deleteId.append(FEEDBACK_auth.getFunction().getId().toString());
        deleteId.append(":");
        deleteId.append(FEEDBACK_auth.getQualifier().getId().toString());

				getAuthorizationManager().deleteAuthorization(getSharedManager().getId(deleteId.toString()));
      }

      AVAIL_auths =
			  getAuthorizationManager().getExplicitUserAZs(
          FunctionLib.AVAILABLE_ASSESSMENT.getId(), qualifier.getId(), false);

      while(AVAIL_auths.hasNext())
      {
        Authorization AVAIL_auth = (Authorization) AVAIL_auths.next();

        StringBuffer deleteId =
          new StringBuffer(AVAIL_auth.getAgent().getId().toString());
        deleteId.append(":");
        deleteId.append(AVAIL_auth.getFunction().getId().toString());
        deleteId.append(":");
        deleteId.append(AVAIL_auth.getQualifier().getId().toString());

				getAuthorizationManager().deleteAuthorization(getSharedManager().getId(deleteId.toString()));
      }

      PersistenceService.getInstance().getPublishedAssessmentQueries()
                        .deleteByPublishedId(
        publishedAssessmentId.getIdString());

      QtiSettingsBean qsb =
        PersistenceService.getInstance().getQtiQueries().returnQtiSettingsBean(
          publishedAssessmentId.getIdString());

      if(qsb != null)
      {
        PersistenceService.getInstance().getQtiQueries().deleteSettingsBean(
          qsb.getId());
      }
    }
    catch(AuthorizationException e)
    {
      LOG.debug(
        "Caught authorization exception while attempting to revokePublishedAssessment");
    }
    catch(SharedException e)
    {
      LOG.debug(
        "Caught shared exception while attempting to revokePublishedAssessment");
    }
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param publishedAssessmentId DOCUMENTATION PENDING
   * @param prequest DOCUMENTATION PENDING
   *
   * @throws AuthorizationException DOCUMENTATION PENDING
   * @throws AssessmentException DOCUMENTATION PENDING
   * @throws DigitalRepositoryException DOCUMENTATION PENDING
   */
  public void modifyPublishedAssessment(
    Id publishedAssessmentId, PublishingRequest prequest)
    throws OsidException
  {
    revokePublishedAssessment(publishedAssessmentId);

    createPublishedAssessment(publishedAssessmentId, prequest);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param agentId DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Iterator getPublishedAssessments(Id agentId, Type filterType)
  {
    ArrayList publishedIds = new ArrayList();

    try
    {
      LOG.debug("getPublishedAssessments(" + agentId.getIdString());
    }
    catch(SharedException e3)
    {
      LOG.debug("getPublishedAssessments(Invalid ID?)");
    }

    AuthorizationIterator authorizations = null;

    ArrayList publishedAssessments = new ArrayList();

    try
    {
      if(
        filterType.equals(TypeLib.FILTER_ACTIVE) ||
          filterType.equals(TypeLib.FILTER_ALL))
      {
        authorizations =
				  getAuthorizationManager().getExplicitAZs(
            agentId, FunctionLib.GRADE_ASSESSMENT.getId(), null, true);

        while(authorizations.hasNext())
        {
          Authorization authorization = authorizations.next();

          LOG.debug("authorization: " + authorization);

          //          PublishedAssessmentBean pab =
          //            PersistenceService.getInstance().getPublishedAssessmentQueries()
          //                              .getByPublishedId(
          //              authorization.getQualifier().getId().toString());
          //
          //          LOG.debug("qualifier: " + authorization.getQualifier().toString());
          //          LOG.debug("coreId: " + pab.getCoreId());
          publishedIds.add(authorization.getQualifier().getId().getIdString());
        }
      }

      if(
        filterType.equals(TypeLib.FILTER_EXPIRED) ||
          filterType.equals(TypeLib.FILTER_ALL))
      {
        authorizations =
				  getAuthorizationManager().getExplicitAZs(
            agentId, FunctionLib.GRADE_ASSESSMENT.getId(), null, false);

        while(authorizations.hasNext())
        {
          Authorization authorization = authorizations.next();

          LOG.debug("authorization: " + authorization);
          LOG.debug("qualifier: " + authorization.getQualifier().toString());

          publishedIds.add(authorization.getQualifier().getId().getIdString());
        }
      }
    }

    catch(AuthorizationException e1)
    {
      LOG.debug(
        "getAssessmentsPublishedForAgent - Error examining authorization");

      return publishedIds.iterator();
    }
    catch(SharedException e)
    {
      LOG.debug("SharedException occured");

      return publishedIds.iterator();
    }

    return publishedIds.iterator();
  }

  /* (non-Javadoc)
   * @see org.navigoproject.osid.assessment.PublishingService#createAnonymousAuthorization(osid.dr.Asset, java.util.Calendar, java.util.Calendar)
   */
  public Id getAnonymousId()
  {
    Agent anonymousAgent = null;
    try
    {
      anonymousAgent = getSharedManager().getAgent(getSharedManager().getId("Anonymous:"));

      return anonymousAgent.getId();
    }
    catch(SharedException e)
    {
      LOG.debug(
        "createAnonymousAuthorization: Unable to create anonymous agent");
    }

    return null;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Id getAuthenticatedUsersId()
  {
    Agent authUsersAgent = null;
    try
    {
      authUsersAgent = getSharedManager().getAgent(getSharedManager().getId("AuthenticatedUsers:"));

      return authUsersAgent.getId();
    }
    catch(SharedException e)
    {
      LOG.debug(
        "createAuthenticatedUsersAuthorization: Unable to create authenticated users agent");
    }

    return null;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param document DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  private boolean attemptRealization(Document document)
  {
    try
    {
      Map poolNumMap =
        new AttemptRealizedAssessment(document).getPoolNumberMap(
          new GregorianCalendar());

      Iterator keySetIter = poolNumMap.keySet().iterator();

      while(keySetIter.hasNext())
      {
        String poolId = (String) keySetIter.next();

        String number = (String) poolNumMap.get(poolId);

        QuestionPool pool =
          QuestionPoolFactory.getInstance().getPool(
            getSharedManager().getId(number), currentUserAgent);
      }
    }
    catch(ParserConfigurationException e)
    {
      LOG.error(e);
      throw new Error(e);
    }
    catch(SAXException e)
    {
      LOG.error(e);
      throw new Error(e);
    }
    catch(IOException e)
    {
      LOG.error(e);
      throw new Error(e);
    }
    catch(SharedException e)
    {
      LOG.error(e);
      throw new Error(e);
    }

    return false;
  }  
  
	/**
	 * DOCUMENTATION PENDING
	 *
	 * @return DOCUMENTATION PENDING
	 *
	 * @throws DigitalRepositoryException DOCUMENTATION PENDING
	 */
	private DigitalRepositoryManager getDigitalRepositoryManager()
		throws DigitalRepositoryException
	{
		if(dm == null)
		{
			try
			{
				dm =
					org.navigoproject.osid.OsidManagerFactory.createDigitalRepositoryManager();
			}
			catch(OsidException e)
			{
				LOG.warn("Unexpected OsidException: " + e.getMessage());
				throw new DigitalRepositoryException(e.getMessage());
			}
		}

		return dm;
	}
  
	private SharedManager getSharedManager()
		throws SharedException
	{
		if(sm == null)
		{
			try
			{
				sm =
					org.navigoproject.osid.OsidManagerFactory.createSharedManager();
			}
			catch(OsidException e)
			{
				LOG.warn("Unexpected OsidException: " + e.getMessage());
				throw new SharedException(e.getMessage());
			}
		}

		return sm;
	}  
	
	private AuthorizationManager getAuthorizationManager()
		throws AuthorizationException
	{
		if(azm == null)
		{
			try
			{
				azm =
					org.navigoproject.osid.OsidManagerFactory.createAuthorizationManager();
			}
			catch(OsidException e)
			{
				LOG.warn("Unexpected OsidException: " + e.getMessage());
				throw new AuthorizationException(e.getMessage());
			}
		}

		return azm;
	}	
  
  
	private AuthenticationManager getAuthenticationManager()
		throws AuthenticationException
	{
		if(anm == null)
		{
			try
			{
				anm =
					org.navigoproject.osid.OsidManagerFactory.createAuthenticationManager();
			}
			catch(OsidException e)
			{
				LOG.warn("Unexpected OsidException: " + e.getMessage());
				throw new AuthenticationException(e.getMessage());
			}
		}

		return anm;
	}	
  
	private CourseManagementManager getCourseManagementManager()
		throws CourseManagementException
	{
		if(cmm == null)
		{
			try
			{
				cmm =
					org.navigoproject.osid.OsidManagerFactory.createCourseManagementManager();
			}
			catch(OsidException e)
			{
				LOG.warn("Unexpected OsidException: " + e.getMessage());
				throw new CourseManagementException(e.getMessage());
			}
		}

		return cmm;
	}	
}
