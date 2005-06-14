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

package org.navigoproject.business.entity;

import java.io.*;

import java.util.*;

/**
 * Use in conjunction with QTI Persistence XML to XML Transform.
 * This bean has getter and setter methods corresponding to the elements
 * generated in xml/xsl/dataTransform/result/extractGrades.xsl.
 *
 * Note that the element names must sync with the properties in this bean.
 * Multiple elements are mapped as a List.
 * Elements without that mapping will be ignored.
 *
 * Usage notes:
 * Feed realized QTI through the extractGrades.xsl transform directly into
 * this bean through org.navigoproject.util.XMLMapper.
 *
 * @version $Id: GradingExtractionModel.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 * @author <a href="mailto:esmiley@stanford.edu">Ed Smiley</a>
 */
public class GradingExtractionModel
  implements Serializable
{
  private String assessmentResultId;
  private String student;
  private String score;
  private String maxScore;
  private String studentId;
  // there may be multiple items, the nth element in each list correspond
  private List itemId;
  private List itemScore;
  private List itemMaxScore;
  private List itemCorrect;
  private List itemAnswerLabel;
  private List itemAnswerText;
  private List itemQuestionText;
  private List itemCorrectAnswerText;
  private List itemType;
  private List itemMedia;

  /**
   *  Get the assessmentResultId property
   *
   * @return assessmentResultIdproperty
   */
  public String getAssessmentResultId()
  {
    return assessmentResultId;
  }

  /**
   *  Set the assessmentResultId property
   *
   * @param passessmentResultId value to set assessmentResultId property to
   */
  public void setAssessmentResultId(String p_assessmentResultId)
  {
    assessmentResultId = p_assessmentResultId;
  }

//  /**
//   *  Get the resultDate property
//   *
//   * @return resultDateproperty
//   */
//  public String getResultDate()
//  {
//    return resultDate;
//  }
//
//  /**
//   *  Set the resultDate property
//   *
//   * @param presultDate value to set resultDate property to
//   */
//  public void setResultDate(String p_resultDate)
//  {
//    resultDate = p_resultDate;
//  }
//
//  /**
//   *  Get the resultTime property
//   *
//   * @return resultTimeproperty
//   */
//  public String getResultTime()
//  {
//    return resultTime;
//  }
//
//  /**
//   *  Set the resultTime property
//   *
//   * @param presultTime value to set resultTime property to
//   */
//  public void setResultTime(String p_resultTime)
//  {
//    resultTime = p_resultTime;
//  }

  /**
   *  Get the student property
   *
   * @return studentproperty
   */
  public String getStudent()
  {
    return student;
  }

  /**
   *  Set the student property
   *
   * @param pstudent value to set student property to
   */
  public void setStudent(String p_student)
  {
    student = p_student;
  }

  /**
   *  Get the studentId property
   *
   * @return studentIdproperty
   */
  public String getStudentId()
  {
    return studentId;
  }

  /**
   *  Set the studentId property
   *
   * @param pstudentId value to set studentId property to
   */
  public void setStudentId(String p_studentId)
  {
    studentId = p_studentId;
  }

  /**
   *  Get the score property
   *
   * @return scoreproperty
   */
  public String getScore()
  {
    return score;
  }

  /**
   *  Set the score property
   *
   * @param pscore value to set score property to
   */
  public void setScore(String p_score)
  {
    score = p_score;
  }

  /**
   *  Get the maxScore property
   *
   * @return maxScoreproperty
   */
  public String getMaxScore()
  {
    return maxScore;
  }

  /**
   *  Set the maxScore property
   *
   * @param pmaxScore value to set maxScore property to
   */
  public void setMaxScore(String p_maxScore)
  {
    maxScore = p_maxScore;
  }

  /**
   *  Get the itemId property
   *
   * @return itemIdproperty
   */
  public List getItemId()
  {
    return itemId;
  }

  /**
   *  Set the itemId property
   *
   * @param pitemId value to set itemId property to
   */
  public void setItemId(List p_itemId)
  {
    itemId = p_itemId;
  }

  /**
   *  Get the itemScore property
   *
   * @return itemScoreproperty
   */
  public List getItemScore()
  {
    return itemScore;
  }

  /**
   *  Set the itemScore property
   *
   * @param p_itemScore value to set itemScore property to
   */
  public void setItemScore(List p_itemScore)
  {
    itemScore = p_itemScore;
  }

  /**
   *  Get the itemMaxScore property
   *
   * @return itemMaxScore property
   */
  public List getItemMaxScore()
  {
    return itemMaxScore;
  }

  /**
   *  Set the itemMaxScore property
   *
   * @param p_itemMaxScore value to set itemMaxScore property to
   */
  public void setItemMaxScore(List p_itemMaxScore)
  {
    itemMaxScore = p_itemMaxScore;
  }

//  /**
//  *  Get the itemRationale property
//  *
//  * @return itemRationaleproperty
//  */
//  public List getItemRationale()
//  {
//      return itemRationale;
//  }
//
//  /**
//  *  Set the itemRationale property
//  *
//  * @param p_itemRationale value to set itemRationale property to
//  */
//  public void setItemRationale(List p_itemRationale)
//  {
//  itemRationale = p_itemRationale;
//  }
//
//
  /**
  *  Get the item Answer Label property
  *
  * @return item Answer Label property
  */
  public List getItemAnswerLabel()
  {
      return itemAnswerLabel;
  }

  /**
  *  Set the item Answer Label property
  *
  * @param p_itemAnswerLabel value to set itemAnswerLabel property to
  */
  public void setItemAnswerLabel(List p_itemAnswerLabel)
  {
  itemAnswerLabel = p_itemAnswerLabel;
  }

  /**
  *  Get the item Answer Text property
  *
  * @return itemAnswerText property
  */
  public List getItemAnswerText()
  {
      return itemAnswerText;
  }

  /**
  *  Set the item Answer Text property
  *
  * @param itemAnswerText value to set itemAnswerText property to
  */
  public void setItemAnswerText(List p_itemAnswerText)
  {
  itemAnswerText = p_itemAnswerText;
  }


  /**
  *  Get the item question text
  *
  * @return item question text property
  */
  public List getItemQuestionText()
  {
      return itemQuestionText;
  }

  /**
  *  Set the item question text
  *
  * @param p_itemQuestionText value to set item question text
  */
  public void setItemQuestionText(List p_itemQuestionText)
  {
  itemQuestionText = p_itemQuestionText;
  }


  /**
  *  Get the item Correct Answer Text property
  *
  * @return answer List
  */
  public List getItemCorrectAnswerText()
  {
      return itemCorrectAnswerText;
  }

  /**
  *  Set the item Correct Answer Text property
  *
  * @param itemCorrectAnswerText value to set itemCorrectAnswerText property to
  */
  public void setItemCorrectAnswerText(List p_itemCorrectAnswerText)
  {
  itemCorrectAnswerText = p_itemCorrectAnswerText;
  }




  /**
  *  Get the itemCorrect property
  *
  * @return itemCorrectproperty
  */
  public List getItemCorrect()
  {
      return itemCorrect;
  }

  /**
  *  Set the itemCorrect property
  *
  * @param p_itemCorrect value to set itemCorrect property to
  */
  public void setItemCorrect(List p_itemCorrect)
  {
  itemCorrect = p_itemCorrect;
  }

  /**
  *  Get the itemType property
  *
  * @return itemTypeproperty
  */
  public List getItemType()
  {
      return itemType;
  }

  /**
  *  Set the itemType property
  *
  * @param p_itemType value to set itemType property to
  */
  public void setItemType(List p_itemType)
  {
  itemType = p_itemType;
  }

  /**
  *  Get the itemMedia property
  *
  * @return item media id for file or audio upload
  */
  public List getItemMedia()
  {
      return itemMedia;
  }

  /**
  *  Set the itemMedia property
  *
  * @param p_itemMedia, the item media id for file or audio upload
  */
  public void setItemMedia(List p_itemMedia)
  {
  itemMedia = p_itemMedia;
  }

}
