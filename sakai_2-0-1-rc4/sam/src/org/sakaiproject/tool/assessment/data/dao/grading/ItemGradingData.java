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
package org.sakaiproject.tool.assessment.data.dao.grading;

import org.sakaiproject.tool.assessment.data.ifc.grading.AssessmentGradingIfc;
import org.sakaiproject.tool.assessment.data.ifc.grading.ItemGradingIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AnswerIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemDataIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemTextIfc;
import java.util.Date;
import java.util.ArrayList;
import java.lang.Float;
import org.sakaiproject.tool.assessment.services.GradingService;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ItemGradingData
    implements ItemGradingIfc {
  private Long itemGradingId;
  private AssessmentGradingIfc assessmentGrading;
  private ItemDataIfc publishedItem;
  private ItemTextIfc publishedItemText;
  private String agentId;
  private AnswerIfc publishedAnswer;
  private String rationale;
  private String answerText;
  private Date submittedDate;
  private Float autoScore;
  private Float overrideScore;
  private String comments;
  private String gradedBy;
  private Date gradedDate;
  private Boolean review;

  public ItemGradingData() {
  }
  public Long getItemGradingId() {
    return itemGradingId;
  }
  public void setItemGradingId(Long itemGradingId) {
    this.itemGradingId = itemGradingId;
  }
  public AssessmentGradingIfc getAssessmentGrading() {
    return assessmentGrading;
  }
  public void setAssessmentGrading(AssessmentGradingIfc assessmentGrading) {
    this.assessmentGrading = assessmentGrading;
  }
  public ItemDataIfc getPublishedItem() {
    return publishedItem;
  }
  public void setPublishedItem(ItemDataIfc publishedItem) {
    this.publishedItem = publishedItem;
  }
  public ItemTextIfc getPublishedItemText() {
    return publishedItemText;
  }
  public void setPublishedItemText(ItemTextIfc publishedItemText) {
    this.publishedItemText = publishedItemText;
  }
  public String getAgentId() {
    return agentId;
  }
  public void setAgentId(String agentId) {
    this.agentId = agentId;
  }
  public AnswerIfc getPublishedAnswer() {
    return publishedAnswer;
  }
  public void setPublishedAnswer(AnswerIfc publishedAnswer) {
    this.publishedAnswer = publishedAnswer;
  }
  public String getRationale() {
    return rationale;
  }
  public void setRationale(String rationale) {
    this.rationale = rationale;
  }
  public String getAnswerText() {
    return answerText;
  }
  public void setAnswerText(String answerText) {
    this.answerText = answerText;
  }
  public Date getSubmittedDate() {
    return submittedDate;
  }
  public void setSubmittedDate(Date submittedDate) {
    this.submittedDate = submittedDate;
  }
  public Float getAutoScore() {
     // Round to the nearest 1/10th.
    if (autoScore!=null){
      float alignment = autoScore.floatValue();
      int tmp = Math.round(alignment * 10.0f);
      alignment = (float)tmp / 10.0f;
      autoScore = new Float(alignment);
    }
    return autoScore;
  }
  public void setAutoScore(Float autoScore) {
    this.autoScore = autoScore;
  }
  public Float getOverrideScore() {
    // Round to the nearest 1/10th.
    if (overrideScore!=null){
      float alignment = overrideScore.floatValue();
      int tmp = Math.round(alignment * 10.0f);
      alignment = (float)tmp / 10.0f;
      overrideScore = new Float(alignment);
    }
    return overrideScore;
  }
  public void setOverrideScore(Float overrideScore) {
    this.overrideScore = overrideScore;
  }
  public String getComments() {
    return comments;
  }
  public void setComments(String comments) {
    this.comments = comments;
  }
  public String getGradedBy() {
    return gradedBy;
  }
  public void setGradedBy(String gradedBy) {
    this.gradedBy = gradedBy;
  }
  public Date getGradedDate() {
    return gradedDate;
  }
  public void setGradedDate(Date gradedDate) {
    this.gradedDate = gradedDate;
  }

  public Boolean getReview() {
    return review;
  }

  public void setReview(Boolean newReview) {
    review = newReview;
  }

  public ArrayList getMediaArray(){
    GradingService service = new GradingService();
    return service.getMediaArray(itemGradingId.toString());
  }

}