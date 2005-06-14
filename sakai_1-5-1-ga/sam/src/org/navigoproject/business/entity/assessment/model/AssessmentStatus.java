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

import osid.shared.Type;

/**
 * This class holds a student's status on a particular assessment.
 *
 * @author <a href="mailto:rgollub@stanford.edu">Rachel Gollub</a>
 */
public class AssessmentStatus
{
  private long assessmentId;
  private String assessmentName;
  private String description;
  private Type type;
  private int number_parts;
  private int parts_done;
  private String student_score;
  private String total_score;
  private String late_handling;
  private Date dueDate;
  private Date releaseDate;
  private Date retractDate;
  private Date feedbackDate;
  private boolean isLate;
  private boolean hasBeenSubmitted;
  private boolean isMultipart;
  private boolean hasTableOfContents;
  private boolean isReleased;

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
   * @param aid DOCUMENTATION PENDING
   */
  public void setAssessmentId(long aid)
  {
    assessmentId = aid;
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
   * @param aname DOCUMENTATION PENDING
   */
  public void setAssessmentName(String aname)
  {
    assessmentName = aname;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param dn DOCUMENTATION PENDING
   */
  public void setDescription(String dn)
  {
    description = dn;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Type getType()
  {
    return type;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param tp DOCUMENTATION PENDING
   */
  public void setType(Type tp)
  {
    type = tp;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public int getNumberOfParts()
  {
    return number_parts;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param nop DOCUMENTATION PENDING
   */
  public void setNumberOfParts(int nop)
  {
    number_parts = nop;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public int getPartsDone()
  {
    return parts_done;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pd DOCUMENTATION PENDING
   */
  public void setPartsDone(int pd)
  {
    parts_done = pd;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getStudentScore()
  {
    return student_score;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param ss DOCUMENTATION PENDING
   */
  public void setStudentScore(String ss)
  {
    student_score = ss;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getTotalScore()
  {
    return total_score;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param ts DOCUMENTATION PENDING
   */
  public void setTotalScore(String ts)
  {
    total_score = ts;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getLateHandling()
  {
    return late_handling;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param lh DOCUMENTATION PENDING
   */
  public void setLateHandling(String lh)
  {
    late_handling = lh;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Date getDueDate()
  {
    return dueDate;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param dd DOCUMENTATION PENDING
   */
  public void setDueDate(Date dd)
  {
    dueDate = dd;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Date getReleaseDate()
  {
    return releaseDate;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param rd DOCUMENTATION PENDING
   */
  public void setReleaseDate(Date rd)
  {
    releaseDate = rd;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public Date getRetractDate()
  {
    return retractDate;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param rd DOCUMENTATION PENDING
   */
  public void setRetractDate(Date rd)
  {
    retractDate = rd;
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
   * @param fd DOCUMENTATION PENDING
   */
  public void setFeedbackDate(Date fd)
  {
    feedbackDate = fd;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getIsLate()
  {
    return isLate;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param il DOCUMENTATION PENDING
   */
  public void setIsLate(boolean il)
  {
    isLate = il;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getHasBeenSubmitted()
  {
    return hasBeenSubmitted;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newHasBeenSubmitted DOCUMENTATION PENDING
   */
  public void setHasBeenSubmitted(boolean newHasBeenSubmitted)
  {
    hasBeenSubmitted = newHasBeenSubmitted;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getIsMultipart()
  {
    return isMultipart;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newIsMultipart DOCUMENTATION PENDING
   */
  public void setIsMultipart(boolean newIsMultipart)
  {
    isMultipart = newIsMultipart;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getIsReleased()
  {
    return isReleased;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newIsReleased DOCUMENTATION PENDING
   */
  public void setIsReleased(boolean newIsReleased)
  {
    isReleased = newIsReleased;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public boolean getHasTableOfContents()
  {
    return hasTableOfContents;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param newHasTableOfContents DOCUMENTATION PENDING
   */
  public void setHasTableOfContents(boolean newHasTableOfContents)
  {
    hasTableOfContents = newHasTableOfContents;
  }
}
