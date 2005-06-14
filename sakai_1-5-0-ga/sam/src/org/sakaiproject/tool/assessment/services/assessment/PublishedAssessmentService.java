/*
 * Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
 *                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
 *
     * Licensed under the Educational Community License Version 1.0 (the "License");
 * By obtaining, using and/or copying this Original Work, you agree that you have read,
 * understand, and will comply with the terms and conditions of the Educational Community License.
 * You may obtain a copy of the License at:
 *
 *      http://cvs.sakaiproject.org/licenses/license_1_0.html
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.sakaiproject.tool.assessment.services.assessment;

import org.sakaiproject.tool.assessment.facade.PublishedAssessmentFacade;
import org.sakaiproject.tool.assessment.facade.AssessmentGradingFacade;
import org.sakaiproject.tool.assessment.data.ifc.assessment.
    AssessmentAccessControlIfc;
import org.sakaiproject.tool.assessment.data.dao.assessment.
    AssessmentAccessControl;
    import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedItemData;
    import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedMetaData;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedItemText;
import org.sakaiproject.tool.assessment.data.dao.grading.AssessmentGradingData;
import org.sakaiproject.tool.assessment.facade.AssessmentFacade;

import org.navigoproject.osid.impl.PersistenceService;
//import org.sakaiproject.tool.assessment.services.shared.PersistenceService;
import java.io.*;
import javax.servlet.http.*;
import osid.shared.SharedException;
import osid.authorization.AuthorizationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

/**
 * The QuestionPoolService calls the service locator to reach the
 * manager on the back end.
 * @author Rachel Gollub <rgollub@stanford.edu>
 */
public class PublishedAssessmentService {
  private static final org.apache.log4j.Logger LOG =
      org.apache.log4j.Logger.getLogger(PublishedAssessmentService.class);

  /**
   * Creates a new QuestionPoolService object.
   */
  public PublishedAssessmentService() {
  }

  /**
    private HashMap getTotalSubmissionPerAssessment(String agentId) {
      List l = PersistenceService.getInstance().
          getPublishedAssessmentFacadeQueries().
          getNumberOfSubmissionsOfAllAssessmentsByAgent(agentId);
      HashMap h = new HashMap();
      for (int i = 0; i < l.size(); i++) {
        AssessmentGradingData d = (AssessmentGradingData) l.get(i);
       h.put(d.getPublishedAssessmentId(), new Integer(d.getTotalSubmitted()));
        System.out.println("****LOOG pubId = " + d.getPublishedAssessmentId()+
                           "count ="+ d.getTotalSubmitted());
      }
      return h;
    }
   */

  /**
   * rachelgollub: So takeable is that you have *not* reached the number of
   * submissions and you're either before the due date or (you're after the due
   * date, you haven't submitted yet, and late handling is enabled).
   * - quoted from IM on 1/31/05
   * Marc said some of teh assessment do not have any due date, e.g. survey
   */
  public ArrayList getBasicInfoOfAllPublishedAssessments(String agentId, String orderBy,
                                             boolean ascending) {

    // 2. get all takeable assessment available
    return PersistenceService.getInstance().
        getPublishedAssessmentFacadeQueries().
        getBasicInfoOfAllPublishedAssessments(orderBy, ascending,
                                             PublishedAssessmentFacade.
                                             ACTIVE_STATUS);
  }

  public ArrayList getAllReviewableAssessments(String agentId, String orderBy,
                                               boolean ascending) {

    // 1. get total no. of submission per assessment by the given agent
    HashMap h = getTotalSubmissionPerAssessment(agentId);

    ArrayList assessmentList = PersistenceService.getInstance().
        getPublishedAssessmentFacadeQueries().
        getAllReviewableAssessments(orderBy, ascending);
    LOG.info("Qingru***: for reviewable assessmentList.size() in publishedassessmentservice is " +
             assessmentList.size());
    ArrayList reviewableAssessmentList = new ArrayList();
    for (int i = 0; i < assessmentList.size(); i++) {
      AssessmentGradingFacade f = (AssessmentGradingFacade) assessmentList.get(
          i);

      Integer NumberOfSubmissions = (Integer) h.get(
          f.getPublishedAssessment().getPublishedAssessmentId());
      if (NumberOfSubmissions == null) {
        NumberOfSubmissions = new Integer(0);
      }
      LOG.info("Qingru***:Numberofsubmissions==" + NumberOfSubmissions);
      try {
        if (!PersistenceService.getInstance().getAuthzQueriesFacade().isAuthorized(null, "VIEW_ASSESSMENT",
                                              f.getPublishedAssessment().
                                              getPublishedAssessmentId().
                                              toString())) {
          break;
        }
      }
      catch (Exception e1) {
        LOG.fatal("Wrapping Error around unhandled Exception: "
                  + e1.getMessage());
        throw new Error(e1.getMessage());
      }
      /*
                  AssessmentAccessControlIfc control = (AssessmentAccessControlIfc)f.getPublishedAssessment().ge\
       tAssessmentAccessControl();
                  if (control !=null)
                  {
        if (control.getUnlimitedSubmissions() != null && control.getSubmissionsAllowed() != null)
        {
          if (control.getUnlimitedSubmissions().booleanValue()== false && control.getSubmissionsAllowed()== Numb\
       erOfSubmissions)
          {
                          reviewableAssessmentList.add(f);
                        }
                      }
        else if (f.getIsLate() != null && f.getForGrade() != null)
        {
                        if (f.getIsLate().booleanValue() == true && AssessmentAccessControlIfc.NOT_ACCEPT_LATE_S\
       UBMISSION.equals(control.getLateHandling())
          && f.getForGrade().booleanValue() == true )
                 {
                    reviewableAssessmentList.add(f);
                 }
                      }
                  }
       */
      // for testing only
      reviewableAssessmentList.add(f);
    }
    return reviewableAssessmentList;
  }

  public ArrayList getAllActivePublishedAssessments(String orderBy) {
    return getAllPublishedAssessments(orderBy,
                                      PublishedAssessmentFacade.ACTIVE_STATUS);
  }

  public ArrayList getAllActivePublishedAssessments(
      int pageSize, int pageNumber, String orderBy) {
    return getAllPublishedAssessments(
        pageSize, pageNumber, orderBy, PublishedAssessmentFacade.ACTIVE_STATUS);
  }

  public ArrayList getAllInActivePublishedAssessments(String orderBy) {
    return getAllPublishedAssessments(orderBy,
                                      PublishedAssessmentFacade.INACTIVE_STATUS);
  }

  public ArrayList getAllInActivePublishedAssessments(
      int pageSize, int pageNumber, String orderBy) {
    return getAllPublishedAssessments(
        pageSize, pageNumber, orderBy,
        PublishedAssessmentFacade.INACTIVE_STATUS);
  }

  public ArrayList getAllPublishedAssessments(String orderBy, Integer status) {
    return PersistenceService.getInstance().getPublishedAssessmentFacadeQueries().
        getAllPublishedAssessments(orderBy, status); // signalling all & no paging
  }

  public ArrayList getAllPublishedAssessments(
      int pageSize, int pageNumber, String orderBy, Integer status) {
    try {
      if (pageSize > 0 && pageNumber > 0) {
        return PersistenceService.getInstance().
            getPublishedAssessmentFacadeQueries().
            getAllPublishedAssessments(pageSize, pageNumber, orderBy, status);
      }
      else {
        return PersistenceService.getInstance().
            getPublishedAssessmentFacadeQueries().
            getAllPublishedAssessments(orderBy, status);
      }
    }
    catch (Exception e) {
      LOG.error(e);
      throw new Error(e);
    }
  }

  public PublishedAssessmentFacade getPublishedAssessment(String assessmentId) {
    try {
      return PersistenceService.getInstance().
          getPublishedAssessmentFacadeQueries().
          getPublishedAssessment(new Long(assessmentId));
    }
    catch (Exception e) {
      LOG.error(e);
      throw new Error(e);
    }
  }

  public PublishedAssessmentFacade publishAssessment(AssessmentFacade
      assessment) {
    try {
      return PersistenceService.getInstance().
          getPublishedAssessmentFacadeQueries().
          publishAssessment(assessment);
    }
    catch (Exception e) {
      LOG.error(e);
      throw new Error(e);
    }
  }

  public void saveAssessment(PublishedAssessmentFacade assessment) {
    PersistenceService.getInstance().getPublishedAssessmentFacadeQueries().
        saveOrUpdate(assessment);
  }

  public void removeAssessment(String assessmentId) {
    PersistenceService.getInstance().getPublishedAssessmentFacadeQueries().
        removeAssessment(new Long(assessmentId));
  }

  public ArrayList getBasicInfoOfAllActivePublishedAssessments(String orderBy) {
    return PersistenceService.getInstance().getPublishedAssessmentFacadeQueries().
        getBasicInfoOfAllActivePublishedAssessments(orderBy); // signalling all & no paging
  }

  public ArrayList getBasicInfoOfAllInActivePublishedAssessments(String orderBy) {
    return PersistenceService.getInstance().getPublishedAssessmentFacadeQueries().
        getBasicInfoOfAllInActivePublishedAssessments(orderBy); // signalling all & no paging
  }

  public PublishedAssessmentFacade getSettingsOfPublishedAssessment(String
      assessmentId) {
    try {
      return PersistenceService.getInstance().
          getPublishedAssessmentFacadeQueries().
          getSettingsOfPublishedAssessment(new Long(assessmentId));
    }
    catch (Exception e) {
      LOG.error(e);
      throw new Error(e);
    }
  }

  public PublishedItemData loadPublishedItem(String itemId) {
    try {
      return PersistenceService.getInstance().
          getPublishedAssessmentFacadeQueries().
          loadPublishedItem(new Long(itemId));
    }
    catch (Exception e) {
      LOG.error(e);
      throw new Error(e);
    }
  }

  public PublishedItemText loadPublishedItemText(String itemTextId) {
    try {
      return PersistenceService.getInstance().
          getPublishedAssessmentFacadeQueries().
          loadPublishedItemText(new Long(itemTextId));
    }
    catch (Exception e) {
      LOG.error(e);
      throw new Error(e);
    }
  }

  /**
   * return an array list of the last AssessmentGradingFacade per assessment that
   * a user has submitted for grade.
   * @param agentId
   * @param orderBy
   * @param ascending
   * @return
   */
  public ArrayList getBasicInfoOfLastSubmittedAssessments(String agentId,
      String orderBy, boolean ascending) {
    return PersistenceService.getInstance().
        getPublishedAssessmentFacadeQueries().
         getBasicInfoOfLastSubmittedAssessments(agentId, orderBy, ascending);
  }

  /** total submitted for grade
   * returns HashMap (Long publishedAssessmentId, Integer totalSubmittedForGrade);
   */
  public HashMap getTotalSubmissionPerAssessment(String agentId) {
    return PersistenceService.getInstance().
        getPublishedAssessmentFacadeQueries().
        getTotalSubmissionPerAssessment(agentId);
  }

  public Integer getTotalSubmission(String agentId, String publishedAssessmentId) {
    return PersistenceService.getInstance().
        getPublishedAssessmentFacadeQueries().
        getTotalSubmission(agentId, new Long(publishedAssessmentId));
  }

  public PublishedAssessmentFacade getPublishedAssessmentIdByAlias(String alias) {
    return PersistenceService.getInstance().
        getPublishedAssessmentFacadeQueries().
        getPublishedAssessmentIdByAlias(alias);
  }

  public void saveOrUpdateMetaData(PublishedMetaData meta) {
   PersistenceService.getInstance().getPublishedAssessmentFacadeQueries().
        saveOrUpdateMetaData(meta);
  }

  public HashMap getFeedbackHash(){
    return PersistenceService.getInstance().getPublishedAssessmentFacadeQueries().
         getFeedbackHash();
  }
}
