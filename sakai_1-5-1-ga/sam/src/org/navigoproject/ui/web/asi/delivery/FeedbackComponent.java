/*
 * Created on Mar 19, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.delivery;

/**
 * @author casong
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class FeedbackComponent
{
  /**
   * FEEDBACK_SHOW_CORRECT_RESPONSE
     FEEDBACK_SHOW_STUDENT_SCORE
     FEEDBACK_SHOW_ITEM_LEVEL
     FEEDBACK_SHOW_SELECTION_LEVEL
     FEEDBACK_SHOW_GRADER_COMMENT
     FEEDBACK_SHOW_STATS
     FEEDBACK_SHOW_QUESTION
     FEEDBACK_SHOW_RESPONSE
   **/
  public String showCorrectResponse;
  public String showStudentScore;
  public String showItemLevel;
  public String showSelectionLevel;
  public String showGraderComment;
  public String showStats;
  public String showQuestion;
  public String showResponse;
  
  public String getShowCorrectResponse()
  {
    return this.showCorrectResponse;
  }
  
  public String getShowStudentScore(){
    return this.showStudentScore;
  }
  
  public String getShowItemLevel(){
    return this.showItemLevel;
  }
  
  public String getShowSelectionLevel(){
    return this.showSelectionLevel;
  }
  
  public String getShowGraderComment(){
    return this.showGraderComment;
  }
  
  public String getShowStats(){
    return this.showStats;
  }
  
  public String getShowQuestion(){
    return this.showQuestion;
  }
  
  public String getShowResponse(){
    return this.showResponse;
  }
  
}
