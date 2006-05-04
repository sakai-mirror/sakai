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
import java.util.ArrayList;
import java.util.Iterator;

import org.sakaiproject.tool.assessment.data.dao.grading.ItemGradingData;
import org.sakaiproject.tool.assessment.data.ifc.assessment.AnswerIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemDataIfc;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemTextIfc;
import org.sakaiproject.tool.assessment.ui.bean.util.Validator;

/**
 * <p>Copyright: Copyright (c) 2004 Sakai</p>
 * <p> </p>
 * @author Ed Smiley
 * @version $Id: ItemContentsBean.java,v 1.29 2005/06/12 23:08:04 daisyf.stanford.edu Exp $
 */

public class ItemContentsBean implements Serializable {

  private boolean review;
  private boolean unanswered;
  private ItemDataIfc itemData;
  private String gradingComment;
  private String feedback;
  private String responseId="2";
  private String responseText="";
  private String[] responseIds = null;
  private float points;
  private float maxPoints;
  private int number;
  private ArrayList itemGradingDataArray;
  private ArrayList answers;
  private String instruction;
  private String rationale;
  private ArrayList matchingArray;
  private ArrayList fibArray;
  private ArrayList selectionArray;
  private String key;
  private String sequence;
  private ArrayList shuffledAnswers;
  private ArrayList mediaArray;

  // for audio

  private Integer duration;
  private Integer triesAllowed;


  
  
  public ItemContentsBean(){
  }

  // added by daisyf on 11/22/04
  public ItemContentsBean(ItemDataIfc itemData){
    this.itemData = itemData;
    setInstruction(this.itemData.getInstruction());
    Integer sequence = this.itemData.getSequence();
    if (sequence != null)
      setNumber(sequence.intValue());
    else
      setNumber(1);
  }

  /**
   * In the case of an ordinary question, this will obtain the a set of text with
   * one element and return it; in FIB return multiple elements separated by underscores.
   * @return text of question
   */
  public String getText()
  {
    String text = "";

    if (itemData != null)
    {
      text = itemData.getText();
    }

    return text;
  }

  /**
   * This strips text of tags for the table of contents.
   */
  public String getStrippedText()
  {
    return getText().replaceAll("<.*?>","");
  }

  /**
   * String representation of the points.
   * @return String representation of the points.
   */
  public float getPoints() {
    return points;
  }

  /**
   * String representation of the points.
   * @param points String representation of the points.
   */
  public void setPoints(float points) {
    this.points = points;
  }

  /**
   * Does this need review?
   * @return true if it is marked for review
   */
  public boolean getReview() {
    if (getItemGradingDataArray().isEmpty())
      return false;
    ItemGradingData data = (ItemGradingData) getItemGradingDataArray()
      .toArray()[0];
    if (data.getReview() == null)
      return false;
    return data.getReview().booleanValue();
  }

  /**
   * Does this need review?
   * @param review if true mark for review
   */
  public void setReview(boolean preview) {
    if (getItemGradingDataArray().isEmpty())
    {
      ItemGradingData data = new ItemGradingData();
      data.setPublishedItem(itemData);
      if (itemData.getItemTextSet().size() > 0)
        data.setPublishedItemText((ItemTextIfc)
         itemData.getItemTextSet().toArray()[0]);
      ArrayList items = new ArrayList();
      items.add(data);
      setItemGradingDataArray(items); 
    }
    Iterator iter = getItemGradingDataArray().iterator();
    while (iter.hasNext())
    {
      ItemGradingData data = (ItemGradingData) iter.next();
      data.setReview(new Boolean(preview));
    }
  }

  /**
   * unanswered?
   * @return
   */
  public boolean isUnanswered() {
    if (getItemGradingDataArray().isEmpty())
      return true;
    Iterator iter = getItemGradingDataArray().iterator();
    while (iter.hasNext())
    {
      ItemGradingData data = (ItemGradingData) iter.next();
      if (getItemData().getTypeId().toString().equals("8")) // fix for bug sam-330
      {
	  if (data.getAnswerText() != null)
              return false;
      }
      else
      {
          if (data.getPublishedAnswer() != null || data.getAnswerText() != null)
              return false;
      }
    }
    return true;
  }

  /**
   * unanswered?
   * @param unanswered
   */
  public void setUnanswered(boolean unanswered) {
    this.unanswered = unanswered;
  }

  /**
   * String representation of the max points available for this question.
   * @return String representation of the max points.
   */
  public float getMaxPoints() {
    return maxPoints;
  }

  /**
   * String representation of the max points available for this question.
   * @param maxPoints String representation of the max points available
   */
  public void setMaxPoints(float maxPoints) {
    this.maxPoints = maxPoints;
  }

  /**
   * question number
   * @return
   */
  public int getNumber() {
    return number;
  }

  /**
   * question number
   * @param number
   */
  public void setNumber(int number) {
    this.number = number;
    this.itemData.setSequence(new Integer(number));
  }

  /**
   * the item data itself
   * @return
   */
  public ItemDataIfc getItemData() {
    return itemData;
  }

  /**
   * the item data itself
   * @param itemData
   */
  public void setItemData(ItemDataIfc itemData) {
    this.itemData = itemData;
  }

  /**
   * grading comment
   * @return grading comment
   */
  public String getGradingComment() {
    if (gradingComment == null)
      return "";
    return gradingComment;
  }

  /**
   * grading comment
   * @param gradingComment grading comment
   */
  public void setGradingComment(String gradingComment) {
    this.gradingComment = gradingComment;
  }

  /**
   * item level feedback
   * @return the item level feedback
   */
  public String getFeedback() {
    return feedback;
  }

  /**
   * item level feedback
   * @param feedback the item level feedback
   */
  public void setFeedback(String feedback) {
    this.feedback = feedback;
  }

  /**
   * If this is a true-false question return true if it is true, else false.
   * If it is not a true-false question return false.
   * @return true if this is a true true-false question
   */
  public boolean getIsTrue()
  {
    if (itemData != null)
    {
      return itemData.getIsTrue().booleanValue();
    }
    return false;
  }

  public ArrayList getItemGradingDataArray()
  {
    if (itemGradingDataArray == null)
      return new ArrayList();
    return itemGradingDataArray;
  }

  public void setItemGradingDataArray(ArrayList newArray)
  {
    itemGradingDataArray = newArray;
  }

  /* These are helper methods to get data into the database */
    
  public String getResponseId()
  {
   try {
    if (selectionArray != null)
    {
      Iterator iter = selectionArray.iterator();
      while (iter.hasNext())
      {
        SelectionBean bean = (SelectionBean) iter.next();
        if (bean.getResponse())
          return bean.getAnswer().getId().toString();
      }
      return "";
    }
   } catch (Exception e) {
     // True/false
   }

   try {
    String response = "";
    // String response = responseId;  //For testing
    Iterator iter = getItemGradingDataArray().iterator();
    if (iter.hasNext())
    {
      ItemGradingData data = (ItemGradingData) iter.next();
      if (data.getPublishedAnswer() != null)
        response = data.getPublishedAnswer().getId().toString();
    }
    return response;
   } catch (Exception e) {
     e.printStackTrace();
     return responseId;
   }
  }

  public void setResponseId(String presponseId)
  {
   try {
    responseId = presponseId;
 
    if (selectionArray != null && presponseId != null && 
      !presponseId.trim().equals(""))
    {
      Iterator iter = selectionArray.iterator();
      while (iter.hasNext())
      {
        SelectionBean bean = (SelectionBean) iter.next();
        if (bean.getAnswer().getId().toString().equals(presponseId))
        {
          bean.setResponse(true);
        }
        else
        {
          bean.setResponse(false);
        }
      }
    }
    return;
   } catch (Exception e) {
     // True/false
   }

   try {
    Iterator iter = getItemGradingDataArray().iterator();
    if (!iter.hasNext() && (presponseId == null || presponseId.equals("")))
      return;
    ItemGradingData data = null;
    if (iter.hasNext())
    {
      data = (ItemGradingData) iter.next();
    }
    else
    {
      data = new ItemGradingData();
      data.setPublishedItem(itemData);
      data.setPublishedItemText((ItemTextIfc)
        itemData.getItemTextSet().toArray()[0]);
      ArrayList items = new ArrayList();
      items.add(data);
      setItemGradingDataArray(items);
    }
    iter = ((ItemTextIfc) itemData.getItemTextSet().toArray()[0])
      .getAnswerSet().iterator();
    while (iter.hasNext())
    {
      AnswerIfc answer = (AnswerIfc) iter.next();
      if (answer.getId().toString().equals(responseId)) 
      {
        data.setPublishedAnswer(answer);
        break;
      }
    }
   } catch (Exception e)  {
     e.printStackTrace();
   }
  }

  public String[] getResponseIds()
  {
   try {
    /*
    ItemTextIfc text = (ItemTextIfc) itemData.getItemTextSet().toArray()[0];
    String[] response = new String[text.getAnswerArraySorted().size()];
    for (int i=0; i<response.length; i++)
    {
      Iterator iter = getItemGradingDataArray().iterator();
      while (iter.hasNext())
      {
        ItemGradingData data = (ItemGradingData) iter.next();
        if (data.getPublishedAnswer() != null && data.getPublishedAnswer().getId().toString().equals(text.getAnswerArraySorted().toArray()[i]))
        {
          response[i] = data.getPublishedAnswer().getId().toString();
        }
      }
    } */

    String[] response = new String[getItemGradingDataArray().size()];
    Iterator iter = getItemGradingDataArray().iterator();
    int i=0;
    while (iter.hasNext())
    {
      ItemGradingData data = (ItemGradingData) iter.next();
      if (data.getPublishedAnswer() != null)
      {
        response[i++] = data.getPublishedAnswer().getId().toString();
      }
      else
        response[i++] = null;
    }
    return response;
   } catch (Exception e) {
     e.printStackTrace();
     return null;
   }
  }

  public void setResponseIds(String[] presponseIds)
  {
   try {
    ArrayList newItems = new ArrayList();
    responseIds = presponseIds;
    if (getItemGradingDataArray().isEmpty() && 
      (presponseIds == null || presponseIds.length == 0))
      return;
    for (int i=0; i< presponseIds.length; i++)
    {
      ItemGradingData data = null;
      Iterator iter = getItemGradingDataArray().iterator();
      while (iter.hasNext())
      {
        ItemGradingData temp = (ItemGradingData) iter.next();
        if (temp.getPublishedAnswer() != null &&
           temp.getPublishedAnswer().getId().toString().equals(presponseIds[i]))
          data = temp;
      }
      if (data == null)
      {
        data = new ItemGradingData();
        data.setPublishedItem(itemData);
        ItemTextIfc text = (ItemTextIfc) itemData.getItemTextSet().toArray()[0];
        data.setPublishedItemText(text);
        Iterator iter2 = text.getAnswerSet().iterator();
        while (iter2.hasNext())
        {
          AnswerIfc answer = (AnswerIfc) iter2.next();
          if (answer.getId().toString().equals(presponseIds[i]))
            data.setPublishedAnswer(answer);
        }
      }
      newItems.add(data);
    }
    setItemGradingDataArray(newItems);
   } catch (Exception e)  {
     e.printStackTrace();
   }
  }

  public String getResponseText()
  {
   try {
    String response = responseText;
    Iterator iter = getItemGradingDataArray().iterator();
    if (iter.hasNext())
    {
      ItemGradingData data = (ItemGradingData) iter.next();
      response = data.getAnswerText();
    }
    return response;
   } catch (Exception e) {
     e.printStackTrace();
     return responseText;
   }
  }

  public void setResponseText(String presponseId)
  {
   try {
    responseText = presponseId;
    Iterator iter = getItemGradingDataArray().iterator();
    if (!iter.hasNext() && (presponseId == null || presponseId.equals("")))
      return;
    ItemGradingData data = null;
    if (iter.hasNext())
    {
      data = (ItemGradingData) iter.next();
    }
    else
    {
      data = new ItemGradingData();
      data.setPublishedItem(itemData);
      data.setPublishedItemText((ItemTextIfc)
        itemData.getItemTextSet().toArray()[0]);
      ArrayList items = new ArrayList();
      items.add(data);
      setItemGradingDataArray(items);
    }
     data.setAnswerText(presponseId);
   } catch (Exception e)  {
     e.printStackTrace();
   }
  }

  public ArrayList getMatchingArray()
  {
    return matchingArray;
  }

  public void setMatchingArray(ArrayList newArray)
  {
    matchingArray = newArray;
  }

  public ArrayList getFibArray()
  {
    return fibArray;
  }

  public void setFibArray(ArrayList newArray)
  {
    fibArray = newArray;
  }

  public ArrayList getSelectionArray()
  {
    return selectionArray;
  }

  public void setSelectionArray(ArrayList newArray)
  {
    selectionArray = newArray;
  }

  public ArrayList getAnswers()
  {
    return answers;
  }

  public void setAnswers(ArrayList list)
  {
    answers = list;
  }

  //  added by Daisy
  public void setInstruction(String instruction){
    this.instruction = instruction;
  }

  public String getInstruction(){
    return this.instruction;
  }

  public void setRationale(String newRationale){
    Iterator iter = getItemGradingDataArray().iterator();
    if (iter.hasNext())
    {
      ItemGradingData data = (ItemGradingData) iter.next();
      data.setRationale(newRationale);
    }
  }

  public String getRationale(){
    Iterator iter = getItemGradingDataArray().iterator();
    if (iter.hasNext())
    {
      ItemGradingData data = (ItemGradingData) iter.next();
      rationale = data.getRationale();
    }
    return Validator.check(rationale, "");
  }

  public String getKey() {
    return key;
  }
 
  public void setKey(String newKey) {
    key = newKey;
  }

  public String getSequence()
  {
    return sequence;
  }

  public void setSequence(String newSequence)
  {
    sequence = newSequence;
  }

  public ArrayList getShuffledAnswers()
  {
    return shuffledAnswers;
  }

  public void setShuffledAnswers(ArrayList newAnswers)
  {
    shuffledAnswers = newAnswers;
  }

  public Integer getTriesAllowed()
  {
    return triesAllowed;
  }

  public void setTriesAllowed(Integer param)
  {
    triesAllowed= param;
  }

  public Integer getDuration()
  {
    return duration;
  }

  public void setDuration(Integer param)
  {
    duration = param;
  }

  public ArrayList getMediaArray()
  {
    ArrayList mediaArray = new ArrayList();
    ItemGradingData itemGradingData=null;
    try {
      Iterator iter = getItemGradingDataArray().iterator();
      if (iter.hasNext())
      {
        itemGradingData = (ItemGradingData) iter.next();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (itemGradingData != null) 
      mediaArray = itemGradingData.getMediaArray();
    return mediaArray;
  }



}