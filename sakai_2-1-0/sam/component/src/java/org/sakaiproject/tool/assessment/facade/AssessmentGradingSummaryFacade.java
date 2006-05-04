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
package org.sakaiproject.tool.assessment.facade;

import java.util.Date;

import org.sakaiproject.tool.assessment.data.ifc.assessment.PublishedAssessmentIfc;
import org.sakaiproject.tool.assessment.data.ifc.grading.AssessmentGradingIfc;
import org.sakaiproject.tool.assessment.data.ifc.grading.AssessmentGradingSummaryIfc;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class AssessmentGradingSummaryFacade
    implements AssessmentGradingSummaryIfc
{
  private Long assessmentGradingSummaryId;
  private PublishedAssessmentIfc publishedAssessment;
  private String agentId;
  private Integer totalSubmitted;
  private Integer totalSubmittedForGrade;
  private AssessmentGradingIfc lastSubmittedAssessmentGrading;
  private Date lastSubmittedDate;
  private Boolean lastSubmittedAssessmentIsLate;
  private Float sumOf_autoScoreForGrade;
  private Float average_autoScoreForGrade;
  private Float highest_autoScoreForGrade;
  private Float lowest_autoScoreForGrade;
  private Float last_autoScoreForGrade;
  private Float sumOf_overrideScoreForGrade;
  private Float average_overrideScoreForGrade;
  private Float highest_overrideScoreForGrade;
  private Float lowest_overrideScoreForGrade;
  private Float last_overrideScoreForGrade;
  private Integer scoringType;
  private AssessmentGradingIfc acceptedAssessmentGrading;
  private Boolean acceptedAssessmentIsLate;
  private Float finalAssessmentScore;
  private Boolean feedToGradeBook;

  public AssessmentGradingSummaryFacade() {
  }
  public Long getAssessmentGradingSummaryId() {
    return assessmentGradingSummaryId;
  }
  public void setAssessmentGradingSummaryId(Long assessmentGradingSummaryId) {
    this.assessmentGradingSummaryId = assessmentGradingSummaryId;
  }
  public PublishedAssessmentIfc getPublishedAssessment() {
    return publishedAssessment;
  }
  public void setPublishedAssessment(PublishedAssessmentIfc publishedAssessment) {
    this.publishedAssessment = publishedAssessment;
  }
  public String getAgentId() {
    return agentId;
  }
  public void setAgentId(String agentId) {
    this.agentId = agentId;
  }
  public Integer getTotalSubmitted() {
    return totalSubmitted;
  }
  public void setTotalSubmitted(Integer totalSubmitted) {
    this.totalSubmitted = totalSubmitted;
  }
  public Integer getTotalSubmittedForGrade() {
    return totalSubmittedForGrade;
  }
  public void setTotalSubmittedForGrade(Integer totalSubmittedForGrade) {
    this.totalSubmittedForGrade = totalSubmittedForGrade;
  }
  public AssessmentGradingIfc getLastSubmittedAssessmentGrading() {
    return lastSubmittedAssessmentGrading;
  }
  public void setLastSubmittedAssessmentGrading(AssessmentGradingIfc lastSubmittedAssessmentGrading) {
    this.lastSubmittedAssessmentGrading = lastSubmittedAssessmentGrading;
  }
  public Date getLastSubmittedDate() {
    return lastSubmittedDate;
  }
  public void setLastSubmittedDate(Date lastSubmittedDate) {
    this.lastSubmittedDate = lastSubmittedDate;
  }
  public Boolean getLastSubmittedAssessmentIsLate() {
    return lastSubmittedAssessmentIsLate;
  }
  public void setLastSubmittedAssessmentIsLate(Boolean lastSubmittedAssessmentIsLate) {
    this.lastSubmittedAssessmentIsLate = lastSubmittedAssessmentIsLate;
  }
  public Float getSumOf_autoScoreForGrade() {
    return sumOf_autoScoreForGrade;
  }
  public void setSumOf_autoScoreForGrade(Float sumOf_autoScoreForGrade) {
    this.sumOf_autoScoreForGrade = sumOf_autoScoreForGrade;
  }
  public Float getAverage_autoScoreForGrade() {
    return average_autoScoreForGrade;
  }
  public void setAverage_autoScoreForGrade(Float average_autoScoreForGrade) {
    this.average_autoScoreForGrade = average_autoScoreForGrade;
  }
  public Float getHighest_autoScoreForGrade() {
    return highest_autoScoreForGrade;
  }
  public void setHighest_autoScoreForGrade(Float highest_autoScoreForGrade) {
    this.highest_autoScoreForGrade = highest_autoScoreForGrade;
  }
  public Float getLowest_autoScoreForGrade() {
    return lowest_autoScoreForGrade;
  }
  public void setLowest_autoScoreForGrade(Float lowest_autoScoreForGrade) {
    this.lowest_autoScoreForGrade = lowest_autoScoreForGrade;
  }
  public Float getLast_autoScoreForGrade() {
    return last_autoScoreForGrade;
  }
  public void setLast_autoScoreForGrade(Float last_autoScoreForGrade) {
    this.last_autoScoreForGrade = last_autoScoreForGrade;
  }
  public Float getSumOf_overrideScoreForGrade() {
    return sumOf_overrideScoreForGrade;
  }
  public void setSumOf_overrideScoreForGrade(Float sumOf_overrideScoreForGrade) {
    this.sumOf_overrideScoreForGrade = sumOf_overrideScoreForGrade;
  }
  public Float getAverage_overrideScoreForGrade() {
    return average_overrideScoreForGrade;
  }
  public void setAverage_overrideScoreForGrade(Float average_overrideScoreForGrade) {
    this.average_overrideScoreForGrade = average_overrideScoreForGrade;
  }
  public Float getHighest_overrideScoreForGrade() {
    return highest_overrideScoreForGrade;
  }
  public void setHighest_overrideScoreForGrade(Float highest_overrideScoreForGrade) {
    this.highest_overrideScoreForGrade = highest_overrideScoreForGrade;
  }
  public Float getLowest_overrideScoreForGrade() {
    return lowest_overrideScoreForGrade;
  }
  public void setLowest_overrideScoreForGrade(Float lowest_overrideScoreForGrade) {
    this.lowest_overrideScoreForGrade = lowest_overrideScoreForGrade;
  }
  public Float getLast_overrideScoreForGrade() {
    return last_overrideScoreForGrade;
  }
  public void setLast_overrideScoreForGrade(Float last_overrideScoreForGrade) {
    this.last_overrideScoreForGrade = last_overrideScoreForGrade;
  }
  public Integer getScoringType() {
    return scoringType;
  }
  public void setScoringType(Integer scoringType) {
    this.scoringType = scoringType;
  }

  public AssessmentGradingIfc getAcceptedAssessmentGrading() {
    return acceptedAssessmentGrading;
  }
  public void setAcceptedAssessmentGrading(AssessmentGradingIfc acceptedAssessmentGrading) {
    this.acceptedAssessmentGrading = acceptedAssessmentGrading;
  }
  public Boolean getAcceptedAssessmentIsLate() {
    return acceptedAssessmentIsLate;
  }
  public void setAcceptedAssessmentIsLate(Boolean acceptedAssessmentIsLate) {
    this.acceptedAssessmentIsLate = acceptedAssessmentIsLate;
  }
  public Float getFinalAssessmentScore() {
    return finalAssessmentScore;
  }
  public void setFinalAssessmentScore(Float finalAssessmentScore) {
    this.finalAssessmentScore = finalAssessmentScore;
  }
  public Boolean getFeedToGradeBook() {
    return feedToGradeBook;
  }
  public void setFeedToGradeBook(Boolean feedToGradeBook) {
    this.feedToGradeBook = feedToGradeBook;
  }

}