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

package org.navigoproject.business.entity.assessment.model;

import java.util.Date;

/**
 * This keeps track of feedback information for a given access group.
 *
 * @author Rachel Gollub
 *
 * @see AccessGroup
 */
public class FeedbackModel
{
  private String feedbackType;
  private String immediateFeedbackType;
  private String datedFeedbackType;
  private String perQuestionFeedbackType;
  private Date feedbackDate;
  private Date scoreDate;

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getFeedbackType()
  {
    return feedbackType;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pfeedbackType DOCUMENTATION PENDING
   */
  public void setFeedbackType(String pfeedbackType)
  {
    feedbackType = pfeedbackType;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getImmediateFeedbackType()
  {
    return immediateFeedbackType;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pimmediateFeedbackType DOCUMENTATION PENDING
   */
  public void setImmediateFeedbackType(String pimmediateFeedbackType)
  {
    immediateFeedbackType = pimmediateFeedbackType;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getDatedFeedbackType()
  {
    return datedFeedbackType;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pdatedFeedbackType DOCUMENTATION PENDING
   */
  public void setDatedFeedbackType(String pdatedFeedbackType)
  {
    datedFeedbackType = pdatedFeedbackType;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getPerQuestionFeedbackType()
  {
    return perQuestionFeedbackType;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pfeedbackType DOCUMENTATION PENDING
   */
  public void setPerQuestionFeedbackType(String pfeedbackType)
  {
    perQuestionFeedbackType = pfeedbackType;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Date getFeedbackDate()
  {
    return feedbackDate;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pfeedbackDate DOCUMENTATION PENDING
   */
  public void setFeedbackDate(Date pfeedbackDate)
  {
    feedbackDate = pfeedbackDate;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Date getScoreDate()
  {
    return scoreDate;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pscoreDate DOCUMENTATION PENDING
   */
  public void setScoreDate(Date pscoreDate)
  {
    scoreDate = pscoreDate;
  }
}
