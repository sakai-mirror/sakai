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
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/mgt/intf/LockManager.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/29 18:36:41 $
 */
package org.sakaiproject.metaobj.repository.mgt.intf;

import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.metaobj.repository.Node;

import java.util.Collection;

/**
 * User: rpembry
 * Date: Jan 27, 2005
 * Time: 3:31:41 PM
 */
public interface LockManager  {


   /**
    * Locks a given assest.
    *
    * @param node - the node to be locked (can be a collection)
    * @param qualifierId - qualifer and reason (type)
    *
    *qualifier uniquely determines what has created the lock
    * re-using a qualiferId is strongly discouraged and may not be supported in the future

    *
   */
   public void addLock(Node node, Id qualifierId, String reason);
   /**
    * convenience method for when you don't have a node, but only the asset id.  Doesn't support folders
    * only single assets.
    * @param assetId
    * @param qualifierId
    * @param reason
    */
   public void addLock(Id assetId, Id qualifierId, String reason);
   public boolean isLocked(Node node);
   public Collection getLocks(Node node);
   /**
    *
    * @param qualifier
    * @return collection of all locks associated with qualifier
    */
   public Collection getLocks(Id qualifier);
   public void removeLock(Node node, Id qualifier);
   public boolean containsLockedNode(Node collectionNode);
   /**
    * permanenetly removes any lock associated with this qualifier
    * @param qualifier
    */
   public void removeAllLocks(Id qualifier);

}
