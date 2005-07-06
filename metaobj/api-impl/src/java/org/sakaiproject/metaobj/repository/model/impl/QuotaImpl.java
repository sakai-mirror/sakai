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
 * $Header: /opt/CVS/osp2.x/homesComponent/src/java/org/theospi/metaobj/repository/model/impl/QuotaImpl.java,v 1.1 2005/06/29 18:36:41 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/29 18:36:41 $
 */
/*
 * Created on May 11, 2004
 *
 */
package org.sakaiproject.metaobj.repository.model.impl;

import org.sakaiproject.metaobj.repository.model.Quota;
import org.sakaiproject.metaobj.shared.model.Id;


/**
 * @author rpembry
 */
public class QuotaImpl implements Quota {
   Id rowId;
   Id userId;
   Long quota;
   Long used;
   Integer fileCount;

   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   public String toString() {
      return this.userId + "," + this.quota + ", " + this.used + ", " + this.fileCount;
   }

   /**
    * @return Returns the quota.
    */
   public Long getQuota() {
      return quota;
   }

   /**
    * @param quota The quota to set.
    */
   public void setQuota(Long quota) {
      this.quota = quota;
   }

   /**
    * @return Returns the used.
    */
   public Long getUsed() {
      return used;
   }

   /**
    * @param used The used to set.
    */
   public void setUsed(Long used) {
      this.used = used;
   }

   public long getFree() {
      long free = getQuota().longValue() - getUsed().longValue();
      if (free < 0) return 0;
      return free;
   }

   public Integer getFileCount() {
      return this.fileCount;
   }

   /**
    * @param fileCount The fileCount to set.
    */
   public void setFileCount(Integer fileCount) {
      this.fileCount = fileCount;
   }

   /**
    * @return Returns the rowId.
    */
   public Id getRowId() {
      return rowId;
   }

   /**
    * @param rowId The rowId to set.
    */
   public void setRowId(Id rowId) {
      this.rowId = rowId;
   }

   /**
    * @return Returns the userId.
    */
   public Id getUserId() {
      return userId;
   }

   /**
    * @param userId The userId to set.
    */
   public void setUserId(Id userId) {
      this.userId = userId;
   }

}
