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

package org.navigoproject.osid.questionpool;

import org.navigoproject.business.entity.AAMTree;
import org.navigoproject.business.entity.questionpool.model.QuestionPool;
import org.navigoproject.business.entity.questionpool.model.QuestionPoolIterator;
import org.navigoproject.data.GenericConnectionManager;

import java.io.*;
import javax.servlet.http.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import osid.assessment.Item;

import osid.shared.Agent;
import osid.shared.Id;

/**
 * The QuestionPoolDelegate calls the service locator to reach the
 * manager on the back end.
 * @author Rachel Gollub <rgollub@stanford.edu>
 */
public class QuestionPoolDelegate
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(QuestionPoolDelegate.class);
    
  private QuestionPoolFactory questionPoolService = null;

  /**
   * Creates a new QuestionPoolDelegate object.
   */
  public QuestionPoolDelegate()
  {
    try
    {
      questionPoolService = new QuestionPoolFactory();
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }

    try
    {
      // XXX Properties file!
      GenericConnectionManager.getInstance();
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }

  /**
   * Get all pools from the back end.
   */
  public QuestionPoolIterator getAllPools(Agent agent)
  {
    QuestionPoolIterator results = null;
    try
    {
      results = questionPoolService.getAllPools(agent);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }

    return results;
  }

  /**
   * Get a particular pool from the backend, with all questions.
   */
  public QuestionPool getPool(Id poolId, Agent agent)
  {
    QuestionPool pool = null;
    try
    {
      pool = questionPoolService.getPool(poolId, agent);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }

    return pool;
  }

 /**
   * Get a list of pools that share a specific Item
   */
  public List getPoolNamesByItem(Id itemId)
  {
    List nameList = null;
    try
    {
      nameList = questionPoolService.getPoolNamesByItem(itemId);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }

    return nameList;
  }

 /**
   * Get a list of pools that share a specific Item
   */
  public List getPoolIdsByItem(Id itemId)
  {
    List idList = null;
    try
    {
      idList = questionPoolService.getPoolIdsByItem(itemId);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }

    return idList;
  }

/**
   * Get a list of pools that have a specific Agent
   */
  public List getPoolIdsByAgent(Agent agent)
  {
    List idList = null;
    try
    {
      idList = questionPoolService.getPoolIdsByAgent(agent);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }

    return idList;
  }

/**
   * Get a list of pools that have a specific parent
   */
  public List getSubPools(Id poolId)
  {
    List poolList = null;
    try
    {
      poolList = questionPoolService.getSubPools(poolId);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }

    return poolList;
  }

/**
   * Checks to see if a pool has subpools
   */
  public boolean hasSubPools(Id poolId)
  {
    boolean result = false;
    try
    {
      result = questionPoolService.hasSubPools(poolId);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }

    return result;
  }

  /**
   * Save items to a section.
   */
  public void addItemsToSection(Collection ids, Id sectionId)
  {
    try
    {
      questionPoolService.addItemsToSection(ids, sectionId);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }

  /**
   * Save a question to a pool.
   */
  public void addItemToPool(Id itemId, Id poolId)
  {
    try
    {
      questionPoolService.addItemToPool(itemId, poolId);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }

  /**
   * Move a question to a pool.
   */
  public void moveItemToPool(Id itemId, Id sourceId, Id destId)
  {
    try
    {
      questionPoolService.moveItemToPool(itemId, sourceId, destId);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }

  /**
   * Move a subpool to a pool.
   */
  public void movePool(Agent agent, Id sourceId, Id destId)
  {
    try
    {
      questionPoolService.movePool(agent, sourceId, destId);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }

  /**
   * Create a new pool
   */
  public QuestionPool createPool(Id parentPoolId)
  {
    try
    {
      return questionPoolService.createPool(parentPoolId);
    }
    catch(Exception e)
    {
      LOG.error(e); 
//      throw new Error(e);

      return null;
    }
  }

  /**
   *
   */
  public void deletePool(Id poolId,Agent agent,AAMTree tree)
  {
    try
    {
      questionPoolService.deletePool(poolId,agent,tree);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }

  /**
   * Creates a new question
   */
  public Item createQuestion(
    String displayName, String description, org.navigoproject.business.entity.Item  itemXml,
    HttpServletRequest request)
  {
    try
    {
      return questionPoolService.createQuestion(
        displayName, description, itemXml, request);
    }
    catch(Exception e)
    {
      LOG.error(e); 
//      throw new Error(e);

      return null;
    }
  }

  /**
   * emoves a Question from the question pool. This does not  *delete* the question itself
   */
  public void removeQuestionFromPool(Id questionId, Id poolId)
  {
    try
    {
	questionPoolService.removeQuestionFromPool(questionId,poolId);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }

  /**
   * Copy a subpool to a pool.
   */
  public void copyPool(
    AAMTree tree, Agent agent, Id sourceId, Id destId
    )
  {
    try
    {
      questionPoolService.copyPool(
        tree, agent, sourceId, destId );
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }

  /**
   * Copy a question to a pool
   */
  public void copyQuestion(
    Id questionId, Id destId )
  {
    try
    {
      questionPoolService.copyQuestion(questionId, destId );
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }

  /**
   * Copy a question to a pool
   */
  public void copyQuestion(
    Id questionId, Id destId, boolean duplicateCopy)
  {
    try
    {
      questionPoolService.copyQuestion(questionId, destId ,duplicateCopy);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }


  /*
   * Imports a Question from an Xml file
   */
  public Item importQuestion(InputStream inputStream)
  {
    try{
      return questionPoolService.importQuestion(inputStream);
    }catch(Exception e){
      LOG.debug("Exception in importQuestion");
      LOG.error(e);
      return null;
    }
  }

  /*
   * Exports a Question as an Xml file
   */
  public String exportQuestion(Id questionId)
  {
    try{
      return questionPoolService.exportQuestion(questionId);
    }catch(Exception e){
      LOG.debug("Exception in exportQuestion");
      LOG.error(e);
      return null;
    }
  }

  /*
   * Imports a Question Pool
   */
  public QuestionPool importQuestionPool(InputStream inputStream)
  {
    try
    {
      return questionPoolService.importQuestionPool(inputStream);
    }catch(Exception e){
      LOG.debug("Exception in importQuestionPool");
      LOG.error(e);
      return null;

    }
  }

  /*
   * Export Question Pool to Xml file
   */
  public String exportQuestionPool(QuestionPool pool)
  {
    try{
      return questionPoolService.exportQuestionPool(pool);
    }catch(Exception e){
      LOG.debug("Exception in exportQuestionPool");
      LOG.error(e);
      return null;
    }

  }



  /**
   * Save a question pool.
   */
  public QuestionPool savePool(QuestionPool pool)
  {
    try
    {
      return questionPoolService.savePool(pool);
    }
    catch(Exception e)
    {
      LOG.error(e); 
//      throw new Error(e);

      return pool;
    }
  }

  /**
   * Select N questions randomly from a pool tree
   */
  public Collection selectNrandomQuestions(
    QuestionPool pool, int n, Agent agent)
  {
    try
    {
      return questionPoolService.selectNrandomQuestions(pool, n,  agent);
    }
    catch(Exception e)
    {
      LOG.error(e); 
//      throw new Error(e);

      return new ArrayList();
    }
  }
}
