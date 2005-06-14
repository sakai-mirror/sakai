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

package org.navigoproject.osid.questionpool.ejb;

import org.navigoproject.business.entity.AAMTree;
import org.navigoproject.business.entity.questionpool.model.QuestionPool;
import org.navigoproject.business.entity.questionpool.model.QuestionPoolIterator;

import java.io.*;

import javax.servlet.http.*;

import java.rmi.RemoteException;

import java.util.Collection;

import javax.ejb.EJBObject;

import osid.assessment.Item;

import osid.shared.Agent;
import osid.shared.Id;

/**
 * DOCUMENTATION PENDING
 *
 * @author $author$
 * @version $Id: QuestionPoolService.java,v 1.1.1.1 2004/07/28 21:32:07 rgollub.stanford.edu Exp $
 */
public interface QuestionPoolService
  extends EJBObject
{
  /**
   * Get a list of pools from the back end.
   *
   * @return The QuestionPoolIterator.
   */
  public QuestionPoolIterator getAllPools(Agent agent)
    throws RemoteException;

  /**
   * Get a particular pool from the back end, with all questions.
   *
   * @return The QuestionPool.
   */
  public QuestionPool getPool(Id poolId, Agent agent)
    throws RemoteException;

  /**
   * Save items to a section.
   */
  public void addItemsToSection(Collection ids, Id sectionId)
    throws RemoteException;

  /**
   * Save a question to a pool.
   */
  public void addItemToPool(Id itemId, Id poolId)
    throws RemoteException;

  /**
   * Move a question to a pool.
   */
  public void moveItemToPool(Id itemId, Id sourceId, Id destId)
    throws RemoteException;

  /**
   * Move a subpool to a pool.
   */
  public void movePool(Agent agent, Id sourceId, Id destId)
    throws RemoteException;

  /**
   * Create a new pool
   */
  public QuestionPool createPool(Id ParentPoolId)
    throws RemoteException;

  /**
   * Delete a pool
   */
  public void deletePool(Id poolId,Agent agent,AAMTree tree)
    throws RemoteException;

  /**
   * Creates a new question
   */
  public Item createQuestion(
    String displayName, String description, org.navigoproject.business.entity.Item  itemXml,
    HttpServletRequest request)
    throws RemoteException;

  /**
   * Removes a Question from the question pool. This does not  *delete* the question itself
   */
  public void removeQuestionFromPool(Id questionId, Id poolId)
    throws RemoteException;

  /**
   * Copy a subpool to a pool.
   */
  public void copyPool(
    AAMTree tree, Agent agent, Id sourceId, Id destId
    )
    throws RemoteException;

  /**
   * Copy a question to a pool.
   */
  public void copyQuestion(
    Id questionId, Id destId )
    throws RemoteException;

  /**
   * Copy a question to a pool.
   */
  public void copyQuestion(
    Id questionId, Id destId, boolean duplicateCopy)
    throws RemoteException;

  /**
   * Save a question pool.
   */
  public QuestionPool savePool(QuestionPool pool)
    throws RemoteException;

  /**
   * Select N questions randomly from a pool tree
   */
  public Collection selectNrandomQuestions(
    QuestionPool pool, int n, Agent agent)
    throws RemoteException;
}
