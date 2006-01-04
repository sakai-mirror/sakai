/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2003-2005 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.sakaiproject.tool.assessment.ui.bean.delivery;

/**
 * @author casong
 * @author esmiley@stanford.edu
 * $Id$
 */
public class FeedbackComponent
{
  private boolean showCorrectResponse;
  private boolean showGraderComment;
  private boolean showItemLevel;
  private boolean showQuestion;
  private boolean showResponse;
  private boolean showSelectionLevel;
  private boolean showStats;
  private boolean showImmediate;
  private boolean showStudentScore;
  private boolean showStudentQuestionScore;
  private boolean showDateFeedback;
  private boolean showNoFeedback;

  /**
   *
   * @return true if it is correct
   */
  public boolean getShowCorrectResponse()
  {
    return showCorrectResponse;
  }

  /**
   *
   * @return
   */
  public boolean getShowStudentScore(){
    return this.showStudentScore;
  }

 /**
   *
   * @return
   */
  public boolean getShowStudentQuestionScore(){
    return this.showStudentQuestionScore;
  }

  /**
   * If feedback is displayed on the item level.
   * @return if feedback is displayed on the item level.
   */
  public boolean getShowItemLevel(){
    return this.showItemLevel;
  }

  /**
   * If feedback is displayed on the selection level.
   * @return if feedback is displayed on the selection level.
   */
  public boolean getShowSelectionLevel(){
    return this.showSelectionLevel;
  }

  /**
   * If feedback is displayed on the comment level.
   * @return
   */
  public boolean getShowGraderComment(){
    return this.showGraderComment;
  }

  /**
   * If statistics are shown to student.
   * @return
   */
  public boolean getShowStats(){
    return this.showStats;
  }

  /**
   * If informaton  is displayed on the question.
   * @return if information is displayed on the question.
   */
  public boolean getShowQuestion(){
    return this.showQuestion;
  }

  /**
   * If response is displayed.
   * @return if response is displayed
   */
  public boolean getShowResponse(){
    return this.showResponse;
  }

  /**
   * If feedback is displayed on the immediate level.
   * @return true if feedback is displayed on immediately.
   */
  public boolean getShowImmediate() {
    return showImmediate;
  }

  /**
   * If feedback is displayed on the immediate level.
   * @param showImmediate if feedback is displayed on immediately.
   */
  public void setShowImmediate(boolean showImmediate) {
    this.showImmediate = showImmediate;
  }

  /**
   * If feedback is displayed on date.
   * @return if feedback is displayed on the date level.
   */
  public boolean getShowDateFeedback() {
    return showDateFeedback;
  }

  /**
   * If feedback is displayed on the date level.
   * @param showDateFeedback if feedback is displayed on the date level.
   */
  public void setShowDateFeedback(boolean showDateFeedback) {
    this.showDateFeedback = showDateFeedback;
  }

  /**
   * If feedback is displayed on NO level.
   * @return true if NO feedback is displayed.
   */
  public boolean getShowNoFeedback() {
    return showNoFeedback;
  }

  /**
   * If feedback is displayed on NO level.
   * @param showNoFeedback NO feedback is displayed?
   */
  public void setShowNoFeedback(boolean showNoFeedback) {
    this.showNoFeedback = showNoFeedback;
  }
  /**
   * If correct answer is displayed.
   * @param showCorrectResponse If correct answer is displayed.
   */
  public void setShowCorrectResponse(boolean showCorrectResponse)
  {
    this.showCorrectResponse = showCorrectResponse;
  }

  /**
   * Show comments from grader?
   * @param showGraderComment
   */
  public void setShowGraderComment(boolean showGraderComment)
  {
    this.showGraderComment = showGraderComment;
  }

  /**
   * If feedback is displayed on the item level.
   * @param showItemLevel
   */
  public void setShowItemLevel(boolean showItemLevel)
  {
    this.showItemLevel = showItemLevel;
  }

  /**
   * Show question?
   * @param showQuestion
   */
  public void setShowQuestion(boolean showQuestion)
  {
    this.showQuestion = showQuestion;
  }
  /**
   * Show response?
   * @param showResponse
   */
  public void setShowResponse(boolean showResponse)
  {
    this.showResponse = showResponse;
  }
  /**
   * If feedback is displayed on the selection level.
   * @param showSelectionLevel
   */
  public void setShowSelectionLevel(boolean showSelectionLevel)
  {
    this.showSelectionLevel = showSelectionLevel;
  }

  /**
   * Show statistics?
   * @param showStats
   */
  public void setShowStats(boolean showStats)
  {
    this.showStats = showStats;
  }

  /**
   * Show score?
   * @param showStudentScore
   */
  public void setShowStudentScore(boolean showStudentScore)
  {
    this.showStudentScore = showStudentScore;
  }

  /**
   * Show Question score?
   * @param showStudentQuestionScore
   */
  public void setShowStudentQuestionScore(boolean showStudentQuestionScore)
  {
    this.showStudentQuestionScore = showStudentQuestionScore;
  }

}
