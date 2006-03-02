/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2004-2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
*
**********************************************************************************/

package org.sakaiproject.tool.assessment.ui.listener.evaluation;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.tool.assessment.ui.bean.delivery.DeliveryBean;
import org.sakaiproject.tool.assessment.ui.bean.evaluation.StudentScoresBean;
import org.sakaiproject.tool.assessment.ui.listener.delivery.DeliveryActionListener;
import org.sakaiproject.tool.assessment.data.dao.grading.AssessmentGradingData;
import org.sakaiproject.tool.assessment.services.GradingService;
import org.sakaiproject.tool.assessment.ui.listener.evaluation.util.EvaluationListenerUtil;
import org.sakaiproject.tool.assessment.ui.listener.util.ContextUtil;
import org.sakaiproject.tool.assessment.util.BeanSort;

/**
 * <p>
 * This handles the selection of the Student Score page.
 *  </p>
 * <p>Description: Action Listener for Evaluation Student Score page</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Organization: Sakai Project</p>
 * @author Rachel Gollub
 * @version $Id$
 */

public class StudentScoreListener
  implements ActionListener
{
  private static Log log = LogFactory.getLog(StudentScoreListener.class);
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
    log.info("StudentScore LISTENER.");
    StudentScoresBean bean = (StudentScoresBean) cu.lookupBean("studentScores");

    // we probably want to change the poster to be consistent
    String publishedId = cu.lookupParam("publishedIdd");

    log.info("Calling studentScores.");
    if (!studentScores(publishedId, bean, false))
    {
      throw new RuntimeException("failed to call studentScores.");
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
    log.debug("studentScores()");
    try
    {
      bean.setStudentName(cu.lookupParam("studentName"));
      bean.setPublishedId(publishedId);
      bean.setStudentId(cu.lookupParam("studentid"));
      bean.setAssessmentGradingId(cu.lookupParam("gradingData"));
      bean.setItemId(cu.lookupParam("itemId"));

      DeliveryBean dbean = (DeliveryBean) cu.lookupBean("delivery");
      dbean.setActionString("gradeAssessment");
      //dbean.setForGrading(true);

      GradingService service = new GradingService();
      AssessmentGradingData adata= (AssessmentGradingData) service.load(bean.getAssessmentGradingId());

      DeliveryActionListener listener = new DeliveryActionListener();
      listener.processAction(null);

      if (adata.getComments() != null)
          bean.setComments(adata.getComments());

      //dbean.setForGrading(false);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
}
