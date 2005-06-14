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
import org.navigoproject.osid.questionpool.QuestionPoolFactory;

import java.io.*;
import javax.servlet.http.*;

import java.rmi.RemoteException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import osid.assessment.Item;

import osid.shared.Agent;
import osid.shared.Id;

/**
 * DOCUMENTATION PENDING
 *
 * @author $author$
 * @version $Id: QuestionPoolServiceBean.java,v 1.1.1.1 2004/07/28 21:32:07 rgollub.stanford.edu Exp $
 */
public class QuestionPoolServiceBean
  implements SessionBean
{
  private static final org.apache.log4j.Logger LOG =
    org.apache.log4j.Logger.getLogger(QuestionPoolServiceBean.class);
    
  private QuestionPoolFactory qpFactory = QuestionPoolFactory.getInstance();

  /**
   * Creates a new QuestionPoolServiceBean object.
   */
  public QuestionPoolServiceBean()
  {
    super();
  }

  /**
   * Called by the container to create a session bean instance. Its parameters typically
   * contain the information the client uses to customize the bean instance for its use.
   * It requires a matching pair in the bean class and its home interface.
   */
  public void ejbCreate()
    throws RemoteException
  {
  }

  /**
   * Gets a list of all pools.
   *
   * @return The QuestionPoolIterator.
   */
  public QuestionPoolIterator getAllPools(Agent agent)
    throws RemoteException
  {
    QuestionPoolIterator pools = null;
    try
    {
      pools = qpFactory.getAllPools(agent);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }

    return pools;
  }

  /**
   * Gets a particular pool from the back end, with all questions.
   *
   * @return The QuestionPool.
   */
  public QuestionPool getPool(Id poolId, Agent agent)
    throws RemoteException
  {
    QuestionPool pool = null;
    try
    {
      pool = qpFactory.getPool(poolId, agent);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }

    return pool;
  }

  /**
   * Save items to a section.
   */
  public void addItemsToSection(Collection ids, Id sectionId)
    throws RemoteException
  {
    try
    {
      qpFactory.addItemsToSection(ids, sectionId);
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
    throws RemoteException
  {
    try
    {
      qpFactory.addItemToPool(itemId, poolId);
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
    throws RemoteException
  {
    try
    {
      qpFactory.moveItemToPool(itemId, sourceId, destId);
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
    throws RemoteException
  {
    try
    {
      qpFactory.movePool(agent, sourceId, destId);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }

  /**
   * create a new pool
   */
  public QuestionPool createPool(Id parentPoolId)
  {
    try
    {
      return qpFactory.createPool(parentPoolId);
    }
    catch(Exception e)
    {
      LOG.error(e); 
//      throw new Error(e);

      return null;
    }
  }

  /**
   * delete a pool
   */
  public void deletePool(Id poolId,Agent agent,AAMTree tree)
  {
    try
    {
      qpFactory.deletePool(poolId,agent,tree);
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
      return qpFactory.createQuestion(
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
      qpFactory.removeQuestionFromPool(questionId, poolId);
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
    AAMTree tree, Agent agent, Id sourceId, Id destId)
    throws RemoteException
  {
    try
    {
      qpFactory.copyPool(tree, agent, sourceId, destId);
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
      qpFactory.copyQuestion(questionId, destId );
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
      qpFactory.copyQuestion(questionId, destId, duplicateCopy);
    }
    catch(Exception e)
    {
      LOG.error(e); throw new Error(e);
    }
  }

  /**
   * Save a question pool.
   */
  public QuestionPool savePool(QuestionPool pool)
    throws RemoteException
  {
    try
    {
      return qpFactory.savePool(pool);
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
    throws RemoteException
  {
    try
    {
      return qpFactory.selectNrandomQuestions(pool, n, agent);
    }
    catch(Exception e)
    {
      LOG.error(e); 
//      throw new Error(e);

      return new ArrayList();
    }
  }

  /**
   * A container invokes this method before it ends the life of the session object. This
   * happens as a result of a client's invoking a remove operation, or when a container
   * decides to terminate the session object after a timeout. This method is called with
   * no transaction context.
   */
  public void ejbRemove()
  {
  }

  /**
   * The activate method is called when the instance is activated from its 'passive' state.
   * The instance should acquire any resource that it has released earlier in the ejbPassivate()
   * method. This method is called with no transaction context.
   */
  public void ejbActivate()
  {
  }

  /**
   * The passivate method is called before the instance enters the 'passive' state. The
   * instance should release any resources that it can re-acquire later in the ejbActivate()
   * method. After the passivate method completes, the instance must be in a state that
   * allows the container to use the Java Serialization protocol to externalize and store
   * away the instance's state. This method is called with no transaction context.
   */
  public void ejbPassivate()
  {
  }

  /**
   * Set the associated session context. The container calls this method after the instance
   * creation. The enterprise Bean instance should store the reference to the context
   * object in an instance variable. This method is called with no transaction context.
   */
  public void setSessionContext(SessionContext sc)
  {
  }
}
