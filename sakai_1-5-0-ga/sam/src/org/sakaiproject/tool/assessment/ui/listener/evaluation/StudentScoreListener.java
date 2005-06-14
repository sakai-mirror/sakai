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

package org.sakaiproject.tool.assessment.ui.listener.evaluation;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;

import osid.assessment.Assessment;

import org.apache.log4j.Logger;
import org.apache.commons.beanutils.BeanUtils;

import org.navigoproject.osid.impl.PersistenceService;

import org.sakaiproject.tool.assessment.business.entity.RecordingData;
import org.sakaiproject.tool.assessment.data.dao.grading.AssessmentGradingData;
import org.sakaiproject.tool.assessment.data.dao.grading.AssessmentGradingSummaryData;
import org.sakaiproject.tool.assessment.facade.AgentFacade;
import org.sakaiproject.tool.assessment.services.GradingService;
import org.sakaiproject.tool.assessment.ui.bean.delivery.DeliveryBean;
import org.sakaiproject.tool.assessment.ui.bean.evaluation.AgentResults;
import org.sakaiproject.tool.assessment.ui.bean.evaluation.StudentScoresBean;
import org.sakaiproject.tool.assessment.ui.listener.delivery.DeliveryActionListener;
import org.sakaiproject.tool.assessment.ui.listener.evaluation.util.EvaluationListenerUtil;
import org.sakaiproject.tool.assessment.ui.listener.util.ContextUtil;
import org.sakaiproject.tool.assessment.util.BeanSort;

// For testing
import java.util.*;
import org.sakaiproject.tool.assessment.data.ifc.assessment.*;
import org.sakaiproject.tool.assessment.data.dao.grading.*;
import org.sakaiproject.tool.assessment.services.assessment.*;
import org.sakaiproject.tool.assessment.facade.*;
// end testing

/**
 * <p>
 * This handles the selection of the Student Score page.
 *  </p>
 * <p>Description: Action Listener for Evaluation Student Score page</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organization: Sakai Project</p>
 * @author Rachel Gollub
 * @version $Id: StudentScoreListener.java,v 1.3 2005/01/28 06:42:06 rgollub.stanford.edu Exp $
 */

public class StudentScoreListener
  implements ActionListener
{
  static Logger LOG = Logger.getLogger(StudentScoreListener.class.getName());
  private static EvaluationListenerUtil util;
  private static BeanSort bs;
  private static ContextUtil cu;

  /**
   * Standard process action method.
   * @param ae ActionEvent
   * @throws AbortProcessingException
   */
  public void processAction(ActionEvent ae) throws
    AbortProcessingException
  {
    LOG.info("StudentScore LISTENER.");
    StudentScoresBean bean = (StudentScoresBean) cu.lookupBean("studentScores");

    // we probably want to change the poster to be consistent
    String publishedId = cu.lookupParam("publishedIdd");

    LOG.info("Calling studentScores.");
    if (!studentScores(publishedId, bean, false))
    {
      //throw new RuntimeException("failed to call studentScores.");
    }

  }

  /**
   * This will populate the StudentScoresBean with the data associated with the
   * particular versioned assessment based on the publishedId.
   *
   * @param publishedId String
   * @param bean StudentScoresBean
   * @return boolean
   */
  public boolean studentScores(
    String publishedId, StudentScoresBean bean, boolean isValueChange)
  {
    LOG.debug("studentScores()");
    try
    {
      bean.setStudentName(cu.lookupParam("studentName"));
      bean.setPublishedId(publishedId);
      bean.setStudentId(cu.lookupParam("studentid"));
      bean.setAssessmentGradingId(cu.lookupParam("gradingData"));
      bean.setItemId(cu.lookupParam("itemId"));

      DeliveryActionListener listener = new DeliveryActionListener();
      listener.processAction(null);

      DeliveryBean dbean = (DeliveryBean) cu.lookupBean("delivery");
      bean.setComments(dbean.getAssessmentGrading().getComments());

      return true;
    } catch (Exception e) { 
      e.printStackTrace();
      return false;
    }
  }
}
