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

import org.navigoproject.osid.OsidManagerFactory;

import java.io.Serializable;

import osid.assessment.Item;

import osid.shared.Id;
import osid.shared.SharedManager;
import osid.shared.Type;

//import org.navigoproject.business.entity.ItemTemplate;
//import org.navigoproject.business.entity.Objective;
//import org.navigoproject.business.entity.Rubric;

/**
 * An item is essentially a question.  The important information is kept in the
 * data (ItemPropertiesImpl).
 *
 * @author Rachel Gollub
 * @author Qingru Zhang
 */
public class ItemImpl
  implements Item
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(ItemImpl.class);

  // OKI data
  private String name;
  private String description;
  private Serializable data = new ItemPropertiesImpl();
  private Id id;

  /**
   * Creates a new ItemImpl object.
   */
  public ItemImpl()
  {
    try
    {
      SharedManager sm = OsidManagerFactory.createSharedManager();
      id = sm.createId();
    }
    catch(Exception e)
    {
      // TODO How to handle this exception? LDS
      LOG.error(e.getMessage(), e);
      throw new Error(e);
    }
  }

  /**
   * Creates a new ItemImpl object.
   *
   * @param pid DOCUMENTATION PENDING
   */
  public ItemImpl(Id pid)
  {
    id = pid;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pname DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   */
  public void updateDisplayName(String pname)
    throws osid.assessment.AssessmentException
  {
    name = pname;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pdescription DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   */
  public void updateDescription(String pdescription)
    throws osid.assessment.AssessmentException
  {
    description = pdescription;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @param pdata DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   */
  public void updateData(java.io.Serializable pdata)
    throws osid.assessment.AssessmentException
  {
    data = pdata;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   */
  public String getDisplayName()
    throws osid.assessment.AssessmentException
  {
    return name;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   */
  public String getDescription()
    throws osid.assessment.AssessmentException
  {
    return description;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   */
  public Type getItemType()
    throws osid.assessment.AssessmentException
  {
    return ((ItemPropertiesImpl) data).getItemType();
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   */
  public Id getId()
    throws osid.assessment.AssessmentException
  {
    return id;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws osid.assessment.AssessmentException DOCUMENTATION PENDING
   */
  public Serializable getData()
    throws osid.assessment.AssessmentException
  {
    return data;
  }
}
