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


package org.sakaiproject.tool.assessment.ui.bean.delivery;

import java.io.Serializable;
import org.sakaiproject.tool.assessment.data.ifc.assessment.SectionDataIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemDataIfc;
import java.util.Iterator;
import java.util.Set;
import java.util.ArrayList;
import javax.faces.model.SelectItem;

/**
 * <p> </p>
 * <p> </p>
 * <p>Copyright: Copyright (c) 2004 Sakai</p>
 * <p> </p>
 * @author Ed Smiley
 * @version $Id: SectionContentsBean.java,v 1.13 2005/02/16 06:59:19 rgollub.stanford.edu Exp $
 */

public class SectionContentsBean implements Serializable {
  private String text;
  private java.util.ArrayList itemContents;
  private String sectionId;
  private String number;
  private float maxPoints;
  private float points;
  private int questions;
  private int numbering;
  private String numParts;
  private String description;
  private int unansweredQuestions;// ItemContentsBeans
  private ArrayList questionNumbers = new ArrayList();

  public SectionContentsBean(){
  }
  /**
   * Part description.
   * @return Part description.
   */

  public String getText() {
    return text;
  }

  /**
   * Part description.
   * @param text Part description.
   */
  public void setText(String text) {
    this.text = text;
  }

  /**
   *   Points earned thus far for part.
   *
   * @return the points
   */
    public float getPoints() {
    return points;
  }

  /**
   * Points earned thus far for part.
   * @param points
   */
  public void setPoints(float points) {
    this.points = points;
  }

  /**
   * Number unanswered.
   * @return total unanswered.
   */
  public int getUnansweredQuestions() {
    Iterator i = itemContents.iterator();
    int num = 0;
    while (i.hasNext())
    {
      ItemContentsBean next = (ItemContentsBean) i.next();
      if (next.isUnanswered())
        num++;
    }
    return num;
  }

  /**
   * Number unanswered.
   * @param unansweredQuestions
   */
  public void setUnansweredQuestions(int unansweredQuestions) {
    this.unansweredQuestions = unansweredQuestions;
  }

  /**
   * Total points the part is worth.
   * @return max total points for part
   */
  public float getMaxPoints() {
    return maxPoints;
  }

  /**
   * Total points the part is worth.
   * @param maxPoints points the part is worth.
   */
  public void setMaxPoints(float maxPoints) {
    this.maxPoints = maxPoints;
  }

  /**
   * Total number of questions.
   * @return total number of questions
   */
  public int getQuestions() {
    return questions;
  }

  /**
   * Total number of questions.
   * @param questions number of questions
   */
  public void setQuestions(int questions) {
    this.questions = questions;
  }

  /**
   * Total number of questions to list, based on numbering scheme
   * @return total number of questions
   */
  public int getNumbering() {
    return numbering;
  }

  /**
   * Total number of questions to list, based on numbering scheme
   * @param questions number of questions
   */
  public void setNumbering(int newNumbering) {
    numbering = newNumbering;
  }

  /**
   * Contents of part.
   * @return item contents of part.
   */
  public java.util.ArrayList getItemContents() {
    return itemContents;
  }

  /**
   * Contents of part.
   * @param itemContents item contents of part.
   */
  public void setItemContents(java.util.ArrayList itemContents) {
    this.itemContents = itemContents;
  }

  /**
   * Get the size of the contents
   */
  public String getItemContentsSize() {
    if (itemContents == null)
      return "0";
    return new Integer(itemContents.size()).toString();
  }

  /**
   * Set the size of the contents
   */
  public void setItemContentsSize(String dummy) {
    // noop
  }

  /**
   * Display part number.
   * @return display numbering
   */
  public String getNumber() {
    return number;
  }

  /**
   * Display part number.
   * @param number display numbering
   */
  public void setNumber(String number) {
    this.number = number;
  }

  // added by daisyf on 11/22/04
  private String title;
  private String sequence;

  public String getTitle() {
    return this.title;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  public ArrayList getQuestionNumbers() {
    return questionNumbers;
  }
  public void setQuestionNumbers() {
    this.questionNumbers = new ArrayList();
    for (int i=1; i<=this.itemContents.size(); i++){
      this.questionNumbers.add(new SelectItem(new Integer(i)));
    }
  }

  public SectionContentsBean(SectionDataIfc section){
   try {
    this.itemContents = new ArrayList();
    setSectionId(section.getSectionId().toString());
    setTitle(section.getTitle());
    //System.out.println("** section title ="+this.title);
    Integer sequence = section.getSequence();
    if (sequence != null)
      setNumber(sequence.toString());
    else
      setNumber("1");
    setNumber(section.getSequence().toString());
    // do teh rest later
    Set itemSet = section.getItemSet();
    if (itemSet != null){
      setQuestions(itemSet.size());
      Iterator i = itemSet.iterator();
      while (i.hasNext()){
        ItemDataIfc item = (ItemDataIfc)i.next();
        //System.out.println("** item ="+item);
        ItemContentsBean itemBean = new ItemContentsBean(item);
        this.itemContents.add(itemBean);
      }
    }
    // set questionNumbers now
    setQuestionNumbers();
   } catch (Exception e) {
     e.printStackTrace();
   }
  }

  public String getSectionId() {
    return sectionId;
  }

  /**
   * Part description.
   * @param text Part description.
   */
  public void setSectionId(String sectionId) {
    this.sectionId = sectionId;
  }

  public String getNumParts() {
    return numParts;
  }

  public void setNumParts(String newNum) {
    numParts = newNum;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String newDesc) {
    description = newDesc;
  }
}
