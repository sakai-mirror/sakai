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

/*
 * Created on May 10, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.navigoproject.ui.web.asi.review;


/**
 * @author casong
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ReviewAssessmentBean
{
  private String title;
  private String ident;
  private String grade;
  private String rawScore;
  private String time;
  private String submittedTime;
  
  public ReviewAssessmentBean(String title, String ident, String grade, String rawScore, String time, String submittedTime)
  {
    this.title = title;
    this.ident = ident;
    this.grade = grade;
    this.rawScore = rawScore;
    this.time = time;
    this.submittedTime = submittedTime;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getTitle()
  {
    return this.title;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param title DOCUMENTATION PENDING
   */
  public void setTitle(String title)
  {
    this.title = title;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getGrade()
  {
    return this.grade;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param grade DOCUMENTATION PENDING
   */
  public void setGrade(String grade)
  {
    this.grade = grade;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getRawScore()
  {
    return this.rawScore;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param rawScore DOCUMENTATION PENDING
   */
  public void setRawScore(String rawScore)
  {
    this.rawScore = rawScore;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getTime()
  {
    return this.time;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param time DOCUMENTATION PENDING
   */
  public void setTime(String time)
  {
    this.time = time;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   */
  public String getSubmittedTime()
  {
    return this.submittedTime;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param submittedTime DOCUMENTATION PENDING
   */
  public void setSubmittedTime(String submittedTime)
  {
    this.submittedTime = submittedTime;
  }
  /**
   * @return
   */
  public String getIdent()
  {
    return ident;
  }

  /**
   * @param string
   */
  public void setIdent(String string)
  {
    ident = string;
  }

}
