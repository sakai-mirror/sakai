/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/trunk/sakai/legacy-service/service/src/java/org/sakaiproject/exception/InconsistentException.java $
 * $Id: InconsistentException.java 632 2005-07-14 21:22:50Z janderse@umich.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
package org.sakaiproject.metaobj.utils.mvc.intf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ListScroll {
   protected final transient Log logger = LogFactory.getLog(getClass());

   public static final String STARTING_INDEX_TAG = "listScroll_startingIndex";
   public static final String ENSURE_VISIBLE_TAG = "listScroll_ensureVisibleIndex";

   private int total;
   private int perPage;
   private int startingIndex;

   public ListScroll(int perPage, int total, int startingIndex) {
      this.perPage = perPage;
      this.total = total;
      this.startingIndex = startingIndex;
   }

   public int getNextIndex() {
      int nextIndex = startingIndex + perPage;

      if (nextIndex >= total) {
         return -1;
      }

      return nextIndex;
   }

   public int getPerPage() {
      return perPage;
   }

   public void setPerPage(int perPage) {
      this.perPage = perPage;
   }

   public int getPrevIndex() {
      int prevIndex = startingIndex - perPage;

      if (prevIndex < 0) {
         return -1;
      }

      return prevIndex;
   }

   public int getStartingIndex() {
      return startingIndex;
   }

   public void setStartingIndex(int startingIndex) {
      this.startingIndex = startingIndex;
   }

   public int getTotal() {
      return total;
   }

   public void setTotal(int total) {
      this.total = total;
   }

   public int getFirstItem() {
      if (total == 0) {
         return 0;
      }
      return startingIndex + 1;
   }

   public int getLastItem() {
      int lastItem = startingIndex + perPage;

      if (lastItem > total) {
         return total;
      }
      return lastItem;
   }
}
