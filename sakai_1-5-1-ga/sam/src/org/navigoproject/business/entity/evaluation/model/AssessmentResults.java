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

package org.navigoproject.business.entity.evaluation.model;

import java.io.Serializable;

import java.util.*;

import osid.assessment.Item;

/**
 * Holds the results of a taken assessment.
 *
 * @author Rachel Gollub <rgollub@stanford.edu>
 * @auther Ed Smiley <esmiley@stanford.edu>
 *
 */
public class AssessmentResults
  implements Serializable
{
  private Collection agentResults;

  /** Use serialVersionUID for interoperability. */
  private final static long serialVersionUID = -2247126957696504281L;
  private String assessmentName;
  private long assessmentId;
  private String groupName;
  private String maxScore;
  private Integer parts;
  private Integer[] partLength;
  private Item item;
  private String lateHandling;
  private String dueDate;
  private String commentEachQuestion;

  /**
   * Creates a new AssessmentResults object.
   */
  public AssessmentResults()
  {
    agentResults = new ArrayList();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Collection getAgentResults()
  {
    return agentResults;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param presults DOCUMENTATION PENDING
   */
  public void setAgentResults(Collection presults)
  {
    agentResults = presults;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param presults DOCUMENTATION PENDING
   */
  public void addAgentResults(AgentResults presults)
  {
    agentResults.add(presults);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param presults DOCUMENTATION PENDING
   */
  public void removeAgentResults(AgentResults presults)
  {
    agentResults.remove(presults);
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getAssessmentName()
  {
    return assessmentName;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pname DOCUMENTATION PENDING
   */
  public void setAssessmentName(String pname)
  {
    assessmentName = pname;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public long getAssessmentId()
  {
    return assessmentId;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pid DOCUMENTATION PENDING
   */
  public void setAssessmentId(long pid)
  {
    assessmentId = pid;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getGroupName()
  {
    return groupName;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pname DOCUMENTATION PENDING
   */
  public void setGroupName(String pname)
  {
    groupName = pname;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getMaxScore()
  {
    return maxScore;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pscore DOCUMENTATION PENDING
   */
  public void setMaxScore(String pscore)
  {
    maxScore = pscore;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Integer getParts()
  {
    return parts;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pparts DOCUMENTATION PENDING
   */
  public void setParts(Integer pparts)
  {
    parts = pparts;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Integer[] getPartLength()
  {
    return partLength;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param plength DOCUMENTATION PENDING
   */
  public void setPartLength(Integer[] plength)
  {
    partLength = plength;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Item getItem()
  {
    return item;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pitem DOCUMENTATION PENDING
   */
  public void setItem(Item pitem)
  {
    item = pitem;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getLateHandling()
  {
    return lateHandling;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param plateHandling DOCUMENTATION PENDING
   */
  public void setLateHandling(String plateHandling)
  {
    lateHandling = plateHandling;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getDueDate()
  {
    return dueDate;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param dateString DOCUMENTATION PENDING
   */
  public void setDueDate(String dateString)
  {
    dueDate = dateString;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pcommentEach DOCUMENTATION PENDING
   */
  public void setCommentEachQuestion(String pcommentEach)
  {
    commentEachQuestion = pcommentEach;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getCommentEachQuestion()
  {
    return commentEachQuestion;
  }
  ;
}
