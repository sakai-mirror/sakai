/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/common-composite-component/src/java/org/sakaiproject/component/common/hibernate/DenormalizationInterceptor.java,v 1.1 2005/05/11 15:38:42 lance.indiana.edu Exp $
 *
 ***********************************************************************************
 *
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
 *
 **********************************************************************************/

package org.sakaiproject.component.common.hibernate;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentHashMap;
import net.sf.hibernate.CallbackException;
import net.sf.hibernate.Interceptor;
import net.sf.hibernate.type.Type;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.common.agent.Agent;
import org.sakaiproject.api.common.superstructure.Node;
import org.sakaiproject.component.common.agent.AgentBean;
import org.sakaiproject.component.common.superstructure.NodeImpl;

/**
 * @author <a href="mailto:lance@indiana.edu">Lance Speelmon </a>
 * @version $Id$
 */
public class DenormalizationInterceptor implements Interceptor
{
  private static final Log LOG = LogFactory
      .getLog(DenormalizationInterceptor.class);

  private DenormalizationDao denormalizationDao; // dep inj

  private static final String DEPTH = "depth";

  private final Map parentChanged = new ConcurrentHashMap();

  /**
   * Bootstrapping code...
   */
  public void init()
  {
    ; //intentionally left blank
  }

  /**
   * @see net.sf.hibernate.Interceptor#onLoad(java.lang.Object, java.io.Serializable,
   *      java.lang.Object[], java.lang.String[], net.sf.hibernate.type.Type[])
   */
  public boolean onLoad(Object entity, Serializable id, Object[] state,
      String[] propertyNames, Type[] types) throws CallbackException
  {
    // true if the state is modified in any way.
    return false;
  }

  /**
   * @see net.sf.hibernate.Interceptor#onFlushDirty(java.lang.Object, java.io.Serializable,
   *      java.lang.Object[], java.lang.Object[], java.lang.String[], net.sf.hibernate.type.Type[])
   */
  public boolean onFlushDirty(Object entity, Serializable id,
      Object[] currentState, Object[] previousState, String[] propertyNames,
      Type[] types) throws CallbackException
  {
    // true if the state is modified in any way.
    return false;
  }

  /**
   * @see net.sf.hibernate.Interceptor#onSave(java.lang.Object, java.io.Serializable,
   *      java.lang.Object[], java.lang.String[], net.sf.hibernate.type.Type[])
   */
  public boolean onSave(Object entity, Serializable id, Object[] state,
      String[] propertyNames, Type[] types) throws CallbackException
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("onSave(Object " + entity + ", Serializable " + id
          + ", Object[] " + state + ", String[] " + propertyNames + ", Type[] "
          + types + ")");
    }
    boolean stateModified = false;

    if (entity instanceof Node)
    {
      LOG.debug("// entity instanceof Node");
      if (entity instanceof NodeImpl)
      {
        LOG.debug("// entity instanceof NodeImpl");
        final NodeImpl node = (NodeImpl) entity;

        LOG.debug("// determine Node\'s tree depth...");
        final int pos = (Arrays.asList(propertyNames)).indexOf(DEPTH);
        if (pos == -1)
          throw new IllegalStateException(
              "A Node should have a depth property!");

        Node child = node;
        int treeDepth = 0;
        while (child.getParent() != null)
        {
          treeDepth++;
          child = child.getParent();
        }
        state[pos] = new Integer(treeDepth);
        stateModified = true;

        if (node.getId() != null)
        {
          LOG.debug("// NodeImpl has existing persistent state");

          // detect moved Node and if changed mark for denorm cleanup
          final Node newParent = node.getParent();
          LOG.debug("// get existing parent for Node");
          final Node oldParent = (denormalizationDao.getNode(node.getId()))
              .getParent();
          // if current parent != persistent parent
          if (LOG.isDebugEnabled())
          {
            LOG.debug("newParent=" + newParent + "; oldParent =" + oldParent);
          }
          if ((oldParent == null && newParent != null)
              || ((oldParent != null) && !(oldParent.equals(newParent))))
          {
            LOG.debug("// the parent has been changed; mark as changed in Map");
            /*
             * Find transitive children and mark for reset denorm data. 
             * The postFlush() method will check this marker.
             */
            parentChanged.put(node.getId(), new Boolean(true));
          }
        }
      }
      else
      {
        throw new UnsupportedOperationException(
            "Only well known Node implementations are supported at this time.");
      }
    }
    LOG.debug("return stateModified;");
    return stateModified;
  }

  /**
   * @see net.sf.hibernate.Interceptor#onDelete(java.lang.Object, java.io.Serializable,
   *      java.lang.Object[], java.lang.String[], net.sf.hibernate.type.Type[])
   */
  public void onDelete(Object entity, Serializable id, Object[] state,
      String[] propertyNames, Type[] types) throws CallbackException
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("onDelete(Object " + entity + ", Serializable " + id
          + ", Object[] " + state + ", String[] " + propertyNames + ", Type[] "
          + types + ")");
    }
    if (entity instanceof Agent)
    {
      LOG.debug("// entity instanceof Agent");
      if (entity instanceof AgentBean)
      {
        LOG.debug("// entity instanceof AgentBean");
        Integer rowsDeleted = denormalizationDao
            .scrubGroupParentMemberMap((AgentBean) entity);
        if (LOG.isDebugEnabled())
        {
          LOG.debug("Deleted " + rowsDeleted + " denormalized rows for Agent: "
              + entity);
        }
      }
      else
      {
        throw new UnsupportedOperationException(
            "Only well known Agent implementations are supported at this time.");
      }
    }
    if (entity instanceof Node)
    {
      LOG.debug("// entity instanceof Node");
      if (entity instanceof NodeImpl)
      {
        LOG.debug("// entity instanceof NodeImpl");
        Integer d = denormalizationDao
            .scrubNodeParentChildMap((NodeImpl) entity);
        if (LOG.isDebugEnabled())
        {
          LOG.debug("Deleted " + d + "rows of denormalized data for Node: "
              + entity);
        }
      }
      else
      {
        throw new UnsupportedOperationException(
            "Only well known Node implementations are supported at this time.");
      }
    }
  }

  /**
   * @see net.sf.hibernate.Interceptor#preFlush(java.util.Iterator)
   */
  public void preFlush(Iterator entities) throws CallbackException
  {
    ; // intentionally left blank
  }

  /**
   * @see net.sf.hibernate.Interceptor#postFlush(java.util.Iterator)
   */
  public void postFlush(Iterator entities) throws CallbackException
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("postFlush(Iterator " + entities + ")");
    }
    while (entities.hasNext())
    {
      Object entity = entities.next();
      if (LOG.isDebugEnabled())
      {
        LOG.debug("Object entity = " + entity);
      }
      if (entity instanceof Agent)
      { // denormalize transitive Group memberships for Agent
        LOG.debug("// entity instanceof Agent");
        if (entity instanceof AgentBean)
        {
          LOG.debug("// entity instanceof AgentBean");
          denormalizationDao.denormalizeAgent((AgentBean) entity);
        }
        else
        {
          throw new UnsupportedOperationException(
              "Only well known Agent implementations are supported at this time.");
        }
      }
      if (entity instanceof Node)
      { // denormalize transitive parent relationships for Node
        LOG.debug("// entity instanceof Node");
        if (entity instanceof NodeImpl)
        {
          LOG.debug("// entity instanceof NodeImpl");
          NodeImpl node = (NodeImpl) entity;
          denormalizationDao.denormalizeNode(node);

          // has the parent been changed?
          Boolean b = (Boolean) parentChanged.get(node.getId());
          if (LOG.isDebugEnabled())
          {
            LOG.debug("// has the parent been changed? = " + b);
          }
          if (b != null && b.booleanValue())
          {
            LOG.debug("// parent has been changed");
            Iterator transitiveChildren = denormalizationDao
                .getTransitiveChildren(node);
            //TODO this will need to be implemented async for a large tree
            while (transitiveChildren.hasNext())
            {
              NodeImpl child = (NodeImpl) transitiveChildren.next();
              denormalizationDao.denormalizeNode(child);
            }
            LOG.debug("// reset state in Map");
            parentChanged.put(node.getId(), null);
          }
        }
        else
        {
          throw new UnsupportedOperationException(
              "Only well known Node implementations are supported at this time.");
        }
      }
    }
    LOG.debug("return;");
    return;
  }

  /**
   * @see net.sf.hibernate.Interceptor#isUnsaved(java.lang.Object)
   */
  public Boolean isUnsaved(Object entity)
  {
    /*
     * null - Hibernate uses the unsaved-value mapping to determine if the object is unsaved
     */
    return null;
  }

  /**
   * @see net.sf.hibernate.Interceptor#findDirty(java.lang.Object, java.io.Serializable,
   *      java.lang.Object[], java.lang.Object[], java.lang.String[], net.sf.hibernate.type.Type[])
   */
  public int[] findDirty(Object entity, Serializable id, Object[] currentState,
      Object[] previousState, String[] propertyNames, Type[] types)
  {
    /*
     * null - use Hibernate's default dirty-checking algorithm
     */
    return null;
  }

  /**
   * @see net.sf.hibernate.Interceptor#instantiate(java.lang.Class, java.io.Serializable)
   */
  public Object instantiate(Class clazz, Serializable id)
      throws CallbackException
  {
    /*
     * null to choose default behaviour
     */
    return null;
  }

  /**
   * Dependency injection.
   * 
   * @param denormalizationDao
   *          The denormalizationDao to set.
   */
  public void setDenormalizationDao(DenormalizationDao denormalizationDao)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("setDenormalizationDao(DenormalizationDao "
          + denormalizationDao + ")");
    }

    this.denormalizationDao = denormalizationDao;
  }

}

/**********************************************************************************
 *
 * $Header: /cvs/sakai2/common/common-composite-component/src/java/org/sakaiproject/component/common/hibernate/DenormalizationInterceptor.java,v 1.1 2005/05/11 15:38:42 lance.indiana.edu Exp $
 *
 **********************************************************************************/
