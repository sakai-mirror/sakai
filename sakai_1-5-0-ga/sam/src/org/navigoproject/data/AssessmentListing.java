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


/**
 *
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * @author <a href="mailto:rpembry@indiana.edu">Randall P. Embry</a>
 * @version $Id: AssessmentListing.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 */
package org.navigoproject.data;

import java.util.Calendar;

import osid.shared.Id;

/**
 *
 * <p>Copyright: Copyright 2003 Trustees of Indiana University</p>
 * @author <a href="mailto:rpembry@indiana.edu">Randall P. Embry</a>
 * @version $Id: AssessmentListing.java,v 1.1.1.1 2004/07/28 21:32:06 rgollub.stanford.edu Exp $
 */
public class AssessmentListing
{
  private Id assessmentAssetId;
  private String displayName;
  private String description;
  private Calendar effectiveDate;
  private Calendar expirationDate;

  /**
   * Creates a new AssessmentListing object.
   *
   * @param id DOCUMENTATION PENDING
   * @param displayName DOCUMENTATION PENDING
   * @param description DOCUMENTATION PENDING
   * @param effectiveDate DOCUMENTATION PENDING
   * @param expirationDate DOCUMENTATION PENDING
   */
  public AssessmentListing(
    Id id, String displayName, String description, Calendar effectiveDate,
    Calendar expirationDate)
  {
    this.assessmentAssetId = id;
    this.displayName = displayName;
    this.description = description;
    this.effectiveDate = effectiveDate;
    this.expirationDate = expirationDate;
  }

  /**
   * @return
   */
  public String getDescription()
  {
    return description;
  }

  /**
   * @return
   */
  public String getDisplayName()
  {
    return displayName;
  }

  /**
   * @return
   */
  public Calendar getEffectiveDate()
  {
    return effectiveDate;
  }

  /**
   * @return
   */
  public Calendar getExpirationDate()
  {
    return expirationDate;
  }

  /**
   * @param string
   */
  public void setDescription(String string)
  {
    description = string;
  }

  /**
   * @param string
   */
  public void setDisplayName(String string)
  {
    displayName = string;
  }

  /**
   * @param calendar
   */
  public void setEffectiveDate(Calendar calendar)
  {
    effectiveDate = calendar;
  }

  /**
   * @param calendar
   */
  public void setExpirationDate(Calendar calendar)
  {
    expirationDate = calendar;
  }

  /**
   * @return
   */
  public Id getAssessmentAssetId()
  {
    return assessmentAssetId;
  }

  /**
   * @param id
   */
  public void setAssessmentAssetId(Id id)
  {
    assessmentAssetId = id;
  }
}
