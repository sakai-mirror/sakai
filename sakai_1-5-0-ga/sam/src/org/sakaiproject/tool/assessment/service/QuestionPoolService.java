
/*
* Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
*                  Board of Trustees of the Leland Stanford, Jr., University, and The MIT Corporation
*
* Licensed under the Educational Community License Version 1.0 (the "License");
* By obtaining, using and/or copying this Original Work, you agree that you have read,
* understand, and will comply with the terms and conditions of the Educational Community License.
* You may obtain a copy of the License at:
*
*      http://cvs.sakaiproject.org/licenses/license_1_0.html
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
* INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
* AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
* DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/


package org.sakaiproject.tool.assessment.service;

import org.sakaiproject.tool.assessment.business.entity.AAMTree;
import org.sakaiproject.tool.assessment.business.entity.questionpool.QuestionPool;
import org.sakaiproject.tool.assessment.business.entity.questionpool.QuestionPoolIterator;

import org.sakaiproject.tool.assessment.data.dao.questionpool.QuestionPoolItem;
import org.navigoproject.data.GenericConnectionManager;
import org.sakaiproject.tool.assessment.osid.questionpool_0_6.QuestionPoolFactory;
import org.sakaiproject.tool.assessment.osid.questionpool_0_6.impl.QuestionPoolIteratorImpl;
import org.navigoproject.osid.impl.PersistenceService;
import org.sakaiproject.tool.assessment.data.dao.questionpool.QuestionPoolProperties;

import java.io.*;
import javax.servlet.http.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import osid.assessment.Item;

import osid.shared.Agent;
import osid.shared.Id;

/**
 * The QuestionPoolService calls the service locator to reach the
 * manager on the back end.
 * @author Rachel Gollub <rgollub@stanford.edu>
 */
public class QuestionPoolService
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(QuestionPoolService.class);

  private QuestionPoolFactory questionPoolService = null;

  /**
   * Creates a new QuestionPoolService object.
   */
  public QuestionPoolService()
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
//TODO must call the Service.
      results =
        (QuestionPoolIteratorImpl) PersistenceService.getInstance().getQuestionPoolQueries().getAllPools(agent.getId().getIdString());
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
//TODO must call the Service.
    System.out.println("***** poolId in delegate="+poolId.getIdString());
      pool =
        PersistenceService.getInstance().getQuestionPoolQueries().getPool(
          new Long(poolId.getIdString()), agent.getId().getIdString());
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }

    return pool;
  }

  public QuestionPool getPool(Long poolId, Agent agent)
  {
    QuestionPool pool = null;
    try
    {
//TODO must call the Service.
      pool =
        PersistenceService.getInstance().getQuestionPoolQueries().getPool(
          poolId, agent.getId().getIdString());
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }

    return pool;
  }

  /**
   * Get a list of pools that have a specific Agent
   */
  public List getPoolIdsByItem(Id itemId)
  {
    List idList = null;
    try
    {
//TODO must call the Service.
      idList =
        PersistenceService.getInstance().getQuestionPoolQueries().getPoolIdsByItem(
          itemId.getIdString());

    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }

    return idList;
  }

  public List getPoolIdsByAgent(Agent agent)
  {
    List idList = null;
    try
    {
//TODO must call the Service.
      idList =
        PersistenceService.getInstance().getQuestionPoolQueries().getPoolIdsByAgent(
          agent.getId().getIdString());
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
//TODO must call the Service.
      Long pId = new Long(poolId.getIdString());
      poolList = 
	  PersistenceService.getInstance().getQuestionPoolQueries().getSubPools(pId);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }

    return poolList;
  }

  public int getSubPoolSize(Id poolId)
  {
    int poolSize = 0;
    try
    {
      Long pId = new Long(poolId.getIdString());
      poolSize = 
	  PersistenceService.getInstance().getQuestionPoolQueries().getSubPoolSize(pId);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
    return poolSize;
  }

/**
   * Checks to see if a pool has subpools
   */
  public boolean hasSubPools(Id poolId)
  {
    boolean result = false;
    try
    {
//TODO must call the Service.
      result =
        PersistenceService.getInstance().getQuestionPoolQueries().hasSubPools(new Long(poolId.getIdString()));
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
//TODO must call the Service.
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
      PersistenceService.getInstance().getQuestionPoolQueries().addItemToPool(
        new QuestionPoolItem(
          new Long(poolId.getIdString()),
          itemId.getIdString()
        )
      );
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
      PersistenceService.getInstance().getQuestionPoolQueries().moveItemToPool(
          itemId.getIdString(),
          new Long(sourceId.getIdString()),
          new Long(destId.getIdString())
      );
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
      PersistenceService.getInstance().getQuestionPoolQueries().movePool(
        agent.getId().getIdString(), new Long(sourceId.getIdString()), new Long(destId.getIdString()));
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
//TODO must call the Service.
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
  public void deletePool(Id poolId, Agent agent, AAMTree tree)
  {
    try
    {
      QuestionPool qp = PersistenceService.getInstance().getQuestionPoolQueries().getPool(
        new Long(poolId.getIdString()), agent.getId().getIdString());

      // you are not allowed to delete pool if u are not the owner
      if (!((QuestionPoolProperties)qp.getData()).getOwner().equals(agent))
        throw new Error(new Exception());
      PersistenceService.getInstance().getQuestionPoolQueries().deletePool(
        poolId, agent, tree); // wonder if I can rsolve this here
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
//TODO must call the Service.
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
   * removes a Question from the question pool. This does not  *delete* the question itself
   */
  public void removeQuestionFromPool(Id questionId, Id poolId)
  {
    try
    {
      PersistenceService.getInstance().getQuestionPoolQueries().removeItemFromPool(
        questionId.getIdString(), new Long(poolId.getIdString()));
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
      questionPoolService.copyPool( tree, agent, sourceId, destId );
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
//TODO must call the Service.
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
//TODO must call the Service.
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
      return PersistenceService.getInstance().getQuestionPoolQueries().savePool(pool);
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
