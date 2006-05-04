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

package org.sakaiproject.tool.assessment.osid.questionpool_0_6.impl;

import org.sakaiproject.tool.assessment.business.entity.questionpool.QuestionPoolException;
import org.sakaiproject.tool.assessment.business.entity.questionpool.QuestionPool;

import org.sakaiproject.tool.assessment.data.dao.questionpool.QuestionPoolProperties;

import java.io.Serializable;

import osid.OsidException;

import osid.shared.Id;
import osid.shared.Type;

/**
 * This class implements common methods for accessing a question pool.
 * A question pool is defined as a centralized repository where questions
 * are stored.  They allow one to use the same question on multiple
 * tests without duplicating data, and provide an assessment-independent
 * way to store questions.
 *
 * @author Ed Smiley <esmiley@stanford.edu>
 */
public class QuestionPoolImpl
  implements QuestionPool
{
  private String displayName;
  private String description;
  private Id id;
  private Type questionPoolType;
  private Serializable data;
  private Id parentId;
  private QuestionPool parentPool;

  /**
   * Creates a new QuestionPoolImpl object.
   */
  public QuestionPoolImpl()
  {
    // This can hold data until we create an actual object for it.
  }

  /**
   * Constructor.
   * Each question pool has a unique Id object and owns the Id of
   * its parent. See getId(), getParentId()
   *
   * @param newId the id
   * @param newParentId the id of its parent
   */
  public QuestionPoolImpl(Id newId, Id newParentId)
  {
    id = newId;
    parentId = newParentId;
  }


  /**
   *
   * @param pdisplayName the display name for the question pool
   * @throws QuestionPoolException
   */
  public void updateDisplayName(String pdisplayName)
    throws QuestionPoolException
  {
    displayName = pdisplayName;
    if (this.data instanceof QuestionPoolProperties)
      ((QuestionPoolProperties)this.data).setTitle(pdisplayName);
  }

  /**
   *
   * @param pdescription the description for the question pool
   * @throws QuestionPoolException
   */
  public void updateDescription(String pdescription)
    throws QuestionPoolException
  {
    description = pdescription;
    if (this.data instanceof QuestionPoolProperties)
      ((QuestionPoolProperties)this.data).setDescription(pdescription);
  }

  /**
   *
   * @param pdata the extra data member for the question pool
   * @throws QuestionPoolException
   */
  public void updateData(Serializable pdata)
    throws QuestionPoolException
  {
    data = pdata;
  }

  /**
   *
   * @return the display name for the question pool
   * @throws QuestionPoolException
   */
  public String getDisplayName()
    throws QuestionPoolException
  {
    return displayName;
  }

  /**
   *
   * @return the description for the question pool
   * @throws QuestionPoolException
   */
  public String getDescription()
    throws QuestionPoolException
  {
    return description;
  }

  /**
   * DOCUMENTATION PENDING
   *
   * @return DOCUMENTATION PENDING
   *
   * @throws QuestionPoolException DOCUMENTATION PENDING
   */
  public Id getId()
    throws QuestionPoolException
  {
    return id;
  }

  /**
   *
   * @return the type of pool for the question pool
   * @throws QuestionPoolException
   */
  public Type getQuestionPoolType()
    throws QuestionPoolException
  {
    return questionPoolType;
  }

  // daisyf added this method on 8/20/04 to allow QuestionPoolQueries to function, line 115 
  public void updateQuestionPoolType(Type questionPoolType)
    throws QuestionPoolException
  {
    this.questionPoolType = questionPoolType;
  }

  /**
   *
   * @return the extra data for the question pool
   * @throws QuestionPoolException
   */
  public Serializable getData()
    throws QuestionPoolException
  {
    return data;
  }

  /**
   *
   * @return the id object for the question pool
   * @throws QuestionPoolException
   */
  public Id getParentId()
    throws QuestionPoolException
  {
    return parentId;
  }

  /**
   *
   * Sets the parent id object for the question pool
   * @throws QuestionPoolException
   */
  public void setParentId(Id parentId)
    throws QuestionPoolException
  {
    this.parentId = parentId;
    try{
      if (this.data instanceof QuestionPoolProperties)
        ((QuestionPoolProperties)this.data).setParentPoolId(new Long(parentId.getIdString()));
    }
    catch(Exception e){
    }
  }


  /**
   *
   * @return the parent pool for the question pool
   * @throws QuestionPoolException
   */
  public QuestionPool getParentPool()
    throws QuestionPoolException
  {
    return parentPool;
  }

}