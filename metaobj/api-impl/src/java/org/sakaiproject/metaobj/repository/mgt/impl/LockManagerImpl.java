/*
 * The Open Source Portfolio Initiative Software is Licensed under the Educational Community License Version 1.0:
 *
 * This Educational Community License (the "License") applies to any original work of authorship
 * (the "Original Work") whose owner (the "Licensor") has placed the following notice immediately
 * following the copyright notice for the Original Work:
 *
 * Copyright (c) 2004 Trustees of Indiana University and r-smart Corporation
 *
 * This Original Work, including software, source code, documents, or other related items, is being
 * provided by the copyright holder(s) subject to the terms of the Educational Community License.
 * By obtaining, using and/or copying this Original Work, you agree that you have read, understand,
 * and will comply with the following terms and conditions of the Educational Community License:
 *
 * Permission to use, copy, modify, merge, publish, distribute, and sublicense this Original Work and
 * its documentation, with or without modification, for any purpose, and without fee or royalty to the
 * copyright holder(s) is hereby granted, provided that you include the following on ALL copies of the
 * Original Work or portions thereof, including modifications or derivatives, that you make:
 *
 * - The full text of the Educational Community License in a location viewable to users of the
 * redistributed or derivative work.
 *
 * - Any pre-existing intellectual property disclaimers, notices, or terms and conditions.
 *
 * - Notice of any changes or modifications to the Original Work, including the date the changes were made.
 *
 * - Any modifications of the Original Work must be distributed in such a manner as to avoid any confusion
 *  with the Original Work of the copyright holders.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) may NOT be used in advertising or publicity pertaining
 * to the Original or Derivative Works without specific, written prior permission. Title to copyright
 * in the Original Work and any associated documentation will at all times remain with the copyright holders.
 *
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/mgt/impl/LockManagerImpl.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/29 18:36:41 $
 */
package org.sakaiproject.metaobj.repository.mgt.impl;

import org.springframework.orm.hibernate.HibernateObjectRetrievalFailureException;
import org.springframework.orm.hibernate.HibernateSystemException;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;
import org.sakaiproject.metaobj.repository.Node;
import org.sakaiproject.metaobj.repository.mgt.intf.LockManager;
import org.sakaiproject.metaobj.repository.model.Lock;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.shared.model.OspException;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * User: rpembry
 * Date: Jan 28, 2005
 * Time: 8:57:52 AM
 */
public class LockManagerImpl extends HibernateDaoSupport implements LockManager {
   protected final static org.apache.commons.logging.Log logger =
         org.apache.commons.logging.LogFactory.getLog(LockManager.class);


   public void addLock(Node node, Id qualifierId, String reason) {
      Id assetId=node.getId();
      Lock newLock=findOrCreateLock(assetId,qualifierId,false);
      newLock.setAsset(assetId);
      newLock.setQualifier(qualifierId);
      newLock.setDateAdded(now());
      newLock.setActive(true);
      newLock.setReason(reason);

      getHibernateTemplate().saveOrUpdate(newLock);

      if (node.isCollection()) {
         Iterator children=node.getChildren().iterator();
         while (children.hasNext()) {
            Node child=(Node)children.next();
            addLock(child,qualifierId,reason);
         }

      }

   }

   public void addLock(Id assetId, Id qualifierId, String reason) {
      Lock newLock=findOrCreateLock(assetId,qualifierId,false);
      newLock.setAsset(assetId);
      newLock.setQualifier(qualifierId);
      newLock.setDateAdded(now());
      newLock.setActive(true);
      newLock.setReason(reason);
      getHibernateTemplate().saveOrUpdate(newLock);
   }

   public void removeLock(Node node, Id qualifierId) {
      Id assetId=node.getId();
      Lock oldLock=findOrCreateLock(assetId,qualifierId,true);
      oldLock.setActive(false);
      oldLock.setDateRemoved(now());
      getHibernateTemplate().saveOrUpdate(oldLock);
      if (node.isCollection()) {
         Iterator children=node.getChildren().iterator();
         while (children.hasNext()) {
            Node child=(Node)children.next();
            removeLock(child,qualifierId);
         }
      }

   }

   protected Lock findOrCreateLock(Id assetId,Id qualifierId, boolean expected) {
      Lock lock=findLock(assetId,qualifierId);
      if (lock==null) {
         if (expected==true) {
            logger.warn("expected Lock not found: "+assetId+", "+qualifierId);
         }
         return new Lock();
      }

      if (expected==false) {
         logger.warn("Lock not expected, but found anyway: "+assetId+", "+qualifierId);
      }
      return lock;

   }

   protected Lock findLock(Id assetId,Id qualifierId) {
      try {
         getHibernateTemplate().setCacheQueries(true);
         return (Lock) safePopList(getHibernateTemplate().findByNamedQuery("getLock",
            new Object[]{assetId.getValue(), qualifierId.getValue()}));
      } catch (HibernateSystemException e){
         logger.debug("lock with assetId=" + assetId  + " and qualifierId= " + qualifierId + "not found: " + e.getMessage());
         return null;
      } catch (HibernateObjectRetrievalFailureException e) {
         logger.debug("lock with assetId=" + assetId  + " and qualifierId= " + qualifierId + "not found: " + e.getMessage());
         return null;
      }
   }

   protected Object safePopList(List list) {
      if (list == null) return null;
      if (list.size() == 0) return null;
      return list.get(0);
   }

   protected Date now() {
      return java.util.Calendar.getInstance().getTime();
   }


/**
 *
 * @param node - the asset to check
 * @return - a non-empty Collection of active Locks, or null
 */
   public Collection getLocks(Node node) {
      Id assetId=node.getId();
      Collection locks=null;
      if (logger.isDebugEnabled()) {
         logger.debug("getLocks("+node+")");
      }
      try {
         getHibernateTemplate().setCacheQueries(true);
         locks= getHibernateTemplate().findByNamedQuery("activeByAsset",assetId.getValue());
      } catch (HibernateObjectRetrievalFailureException e) {
         logger.error("",e);
         throw new OspException(e);
      }
      if (locks==null) return null;
      if (locks.isEmpty()) return null;
      return locks;
   }

   public Collection getLocks(Id qualifier) {
      Collection locks=null;
      try {
         getHibernateTemplate().setCacheQueries(true);
         locks= getHibernateTemplate().findByNamedQuery("getQualifierLocks",qualifier.getValue());
      } catch (HibernateObjectRetrievalFailureException e) {
         logger.error("",e);
         throw new OspException(e);
      }
      if (locks==null) return null;
      if (locks.isEmpty()) return null;
      return locks;
   }

   // TODO create a faster query (don't need all rows)
   public boolean isLocked(Node node) {
      Collection c=getLocks(node);
      if (c==null) return false;
      return true;
   }

   public boolean containsLockedNode(Node collectionNode) {
      Collection locks=null;
      try {
         getHibernateTemplate().setCacheQueries(true);
         locks= getHibernateTemplate().findByNamedQuery("getChildrenLocks",
            collectionNode.getPath() + "/%");
      } catch (HibernateObjectRetrievalFailureException e) {
         logger.error("",e);
         throw new OspException(e);
      }
      if (locks==null) return false;
      if (locks.isEmpty()) return false;
      return true;
   }

   public void removeAllLocks(Id qualifier) {
      Collection locks = getLocks(qualifier);
      if (locks != null){
         getHibernateTemplate().deleteAll(locks);
      }
   }


}
