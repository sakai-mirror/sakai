/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
*
**********************************************************************************/

package org.sakaiproject.tool.assessment.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.tool.assessment.data.model.Tree;
import org.sakaiproject.tool.assessment.data.dao.questionpool.QuestionPoolItemData;
import org.sakaiproject.tool.assessment.facade.QuestionPoolFacade;
import org.sakaiproject.tool.assessment.facade.QuestionPoolIteratorFacade;

//import osid.assessment.Item;


/**
 * The QuestionPoolService calls the service locator to reach the
 * manager on the back end.
 * @author Rachel Gollub <rgollub@stanford.edu>
 */
public class QuestionPoolService
{
    private static Log log = LogFactory.getLog(QuestionPoolService.class);

  /**
   * Creates a new QuestionPoolService object.
   */
  public QuestionPoolService()
  {
  }

  /**
   * Get all pools from the back end.
   */
  public QuestionPoolIteratorFacade getAllPools(String agentId)
  {
    QuestionPoolIteratorFacade results = null;
      results =
        (QuestionPoolIteratorFacade) PersistenceService.getInstance().
           getQuestionPoolFacadeQueries().getAllPools(agentId);
    return results;
  }


  /**
   * Get basic info for pools(just id and  title)  for displaying in pulldown .
   */
  public ArrayList getBasicInfoOfAllPools(String agentId)
  {
    ArrayList results = null;
      results = PersistenceService.getInstance().
           getQuestionPoolFacadeQueries().getBasicInfoOfAllPools(agentId);
    return results;
  }


  /**
   * Get a particular pool from the backend, with all questions.
   */
  public QuestionPoolFacade getPool(Long poolId, String agentId)
  {
    QuestionPoolFacade pool = null;
    try
    {
      pool =
        PersistenceService.getInstance().getQuestionPoolFacadeQueries().
          getPool(poolId, agentId);
    }
    catch(Exception e)
    {
      log.error(e); throw new Error(e);
    }

    return pool;
  }

  /**
   * Get a list of pools that have a specific Agent
   */
  public List getPoolIdsByItem(String itemId)
  {
    List idList = null;
    try
    {
      idList =
        PersistenceService.getInstance().getQuestionPoolFacadeQueries().
          getPoolIdsByItem(itemId);
    }
    catch(Exception e)
    {
      log.error(e); throw new Error(e);
    }

    return idList;
  }


  public boolean hasItem(String itemId, Long poolId)
  {
        List poollist = null;
	boolean found = false;
    try
    {
	poollist= getPoolIdsByItem(itemId);
	if (poollist!=null) {
	found = poollist.contains(poolId);
	}
	else {
	found = false;
	}
    }
    catch(Exception e)
    {
     e.printStackTrace();
    }
	return found;

  }

  /**
   * Get pool id's by agent.
   */
  public List getPoolIdsByAgent(String agentId)
  {
    List idList = null;
    try
    {
      idList =
        PersistenceService.getInstance().getQuestionPoolFacadeQueries().
          getPoolIdsByAgent(agentId);
    }
    catch(Exception e)
    {
      log.error(e); throw new Error(e);
    }

    return idList;
  }

  /**
   * Get a list of pools that have a specific parent
   */
  public List getSubPools(Long poolId)
  {
    List poolList = null;
    try
    {
      poolList =
	  PersistenceService.getInstance().getQuestionPoolFacadeQueries().
            getSubPools(poolId);
    }
    catch(Exception e)
    {
      log.error(e); throw new Error(e);
    }

    return poolList;
  }

  /**
   * Get the size of a subpool.
   */
  public int getSubPoolSize(Long poolId)
  {
    int poolSize = 0;
    try
    {
      poolSize =
	  PersistenceService.getInstance().getQuestionPoolFacadeQueries().
            getSubPoolSize(poolId);
    }
    catch(Exception e)
    {
      log.error(e); throw new Error(e);
    }
    return poolSize;
  }

  /**
   * Checks to see if a pool has subpools
   */
  public boolean hasSubPools(Long poolId)
  {
    boolean result = false;
    try
    {
      result =
        PersistenceService.getInstance().getQuestionPoolFacadeQueries().
          hasSubPools(poolId);
    }
    catch(Exception e)
    {
      log.error(e); throw new Error(e);
    }

    return result;
  }

  /**
   * Get all items sorted by orderby
   */
  public ArrayList getAllItemsSorted(Long poolId, String orderBy)
  {
    ArrayList results = null;
    try {
      if ("text".equals(orderBy)) {
        results = (ArrayList) PersistenceService.getInstance().
           getQuestionPoolFacadeQueries().getAllItemFacadesOrderByItemText(poolId, "instruction");
      }
      else if ("keyword".equals(orderBy)) {
        results = (ArrayList) PersistenceService.getInstance().
           getQuestionPoolFacadeQueries().getAllItemFacadesOrderByItemType(poolId, orderBy);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return results;
  }


  /**
   * Get all scores for a published assessment from the back end.
   */
  public ArrayList getAllItems(Long poolId)
  {
    ArrayList results = null;
    try {
      results =
        (ArrayList) PersistenceService.getInstance().
           getQuestionPoolFacadeQueries().getAllItemFacades(poolId);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return results;
  }


  /**
   * Save a question to a pool.
   */
  public void addItemToPool(String itemId, Long poolId)
  {
    try
    {
      PersistenceService.getInstance().getQuestionPoolFacadeQueries().
        addItemToPool(new QuestionPoolItemData(poolId, itemId));
    }
    catch(Exception e)
    {
      log.error(e); throw new Error(e);
    }
  }

  /**
   * Move a question to a pool.
   */
  public void moveItemToPool(String itemId, Long sourceId, Long destId)
  {
    try
    {
      PersistenceService.getInstance().getQuestionPoolFacadeQueries().
        moveItemToPool(itemId, sourceId, destId);
    }
    catch(Exception e)
    {
      log.error(e); throw new Error(e);
    }
  }

  /**
   * Is a pool a descendant of the other?
   */
  public boolean isDescendantOf(Long poolA,Long poolB, String agentId)
  {
    try{
      Long tempPoolId = poolA;
      while((tempPoolId !=null)&&(tempPoolId.toString().compareTo("0")>0)){
        QuestionPoolFacade tempPool = getPool(tempPoolId, agentId);
        if(tempPool.getParentPoolId().toString().compareTo(poolB.toString())==0) return true;
        tempPoolId = tempPool.getParentPoolId();
      }
      return false;

    }catch(Exception e){
      e.printStackTrace();
      log.error(e);
      return false;
    }
  }


  /**
   * Move a subpool to a pool.
   */
  public void movePool(String agentId, Long sourceId, Long destId)
  {
    try
    {
      if (!isDescendantOf(destId, sourceId, agentId)) {
        if (!sourceId.equals(destId)) {

        PersistenceService.getInstance().getQuestionPoolFacadeQueries().
          movePool(agentId, sourceId, destId);
        }
        else {
          log.warn("Illegal Move: Can not move a pool to itself." );
        }
      }
      else {
        log.warn("Illegal Move: Can not move a pool to its descendant." );
      }
    }
    catch(Exception e)
    {
      log.error(e); throw new Error(e);
    }

  }

  /**
   * Delete a pool
   */
  public void deletePool(Long poolId, String agentId, Tree tree)
  {
    try
    {
      QuestionPoolFacade qp = PersistenceService.getInstance().
        getQuestionPoolFacadeQueries().getPool(poolId, agentId);

      // you are not allowed to delete pool if you are not the owner
      if (!qp.getOwnerId().equals(agentId))
        throw new Error(new Exception());
      PersistenceService.getInstance().getQuestionPoolFacadeQueries().
        deletePool(poolId, agentId, tree);
    }
    catch(Exception e)
    {
      log.error(e); throw new Error(e);
    }
  }

  /**
   * removes a Question from the question pool. This does not  *delete* the question itself
   */
  public void removeQuestionFromPool(String questionId, Long poolId)
  {
    try
    {
      PersistenceService.getInstance().getQuestionPoolFacadeQueries().
        removeItemFromPool(questionId, poolId);
    }
    catch(Exception e)
    {
      log.error(e); throw new Error(e);
    }
  }

  /**
   * Copy a subpool to a pool.
   */
  public void copyPool(Tree tree, String agentId, Long sourceId,
    Long destId)
  {
    try
    {
      if (!isDescendantOf(destId, sourceId, agentId)) {
        PersistenceService.getInstance().getQuestionPoolFacadeQueries().copyPool
        (tree, agentId, sourceId, destId);
      }
      else {
        log.warn("Illegal Copy: Can not copy a pool to its descendant!" );
      }

    }
    catch(Exception e)
    {
      log.error(e); throw new Error(e);
    }
  }

  /**
   * Copy a question to a pool
   */
  public void copyQuestion(
    osid.shared.Id questionId, osid.shared.Id destId )
  {
    try
    {
//TODO must call the Service.
	//questionPoolService.copyQuestion(questionId, destId );
    }
    catch(Exception e)
    {
      log.error(e); throw new Error(e);
    }
  }

  /**
   * Copy a question to a pool
   */
  public void copyQuestion(
    osid.shared.Id questionId, osid.shared.Id destId, boolean duplicateCopy)
  {
    try
    {
//TODO must call the Service.
	//questionPoolService.copyQuestion(questionId, destId ,duplicateCopy);
    }
    catch(Exception e)
    {
      log.error(e); throw new Error(e);
    }
  }


  /*
   * Exports a Question as an Xml file
   */
  public String exportQuestion(osid.shared.Id questionId)
  {
    try{
	return "";//questionPoolService.exportQuestion(questionId);
    }catch(Exception e){
      log.debug("Exception in exportQuestion");
      log.error(e);
      return null;
    }
  }

  /**
   * Save a question pool.
   */
  public QuestionPoolFacade savePool(QuestionPoolFacade pool)
  {
    try
    {
      return PersistenceService.getInstance().getQuestionPoolFacadeQueries().savePool(pool);
    }
    catch(Exception e)
    {
      log.error(e);
//      throw new Error(e);

      return pool;
    }
  }

  public HashMap getQuestionPoolItemMap(){
    return PersistenceService.getInstance().getQuestionPoolFacadeQueries().
        getQuestionPoolItemMap();
  }

}
