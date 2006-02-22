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
package org.sakaiproject.metaobj.utils.mvc.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScroll;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScrollIndexer;

import java.util.List;
import java.util.Map;

public class ListScrollIndexerImpl implements ListScrollIndexer {
   protected final transient Log logger = LogFactory.getLog(getClass());
   private int perPage;

   public List indexList(Map request, Map model, List sourceList) {
      int startingIndex = 0;
      int total = sourceList.size();

      String ensureVisible = (String) request.get(ListScroll.ENSURE_VISIBLE_TAG);

      if (ensureVisible != null) {
         int visibleIndex = Integer.parseInt(ensureVisible);
         int startingPage = (visibleIndex / perPage);
         startingIndex = startingPage * perPage;
      }
      else {
         String newStart = (String) request.get(ListScroll.STARTING_INDEX_TAG);

         if (newStart != null) {
            startingIndex = Integer.parseInt(newStart);
            if (startingIndex < 0) {
               startingIndex = 0;
            }
         }
      }

      if (startingIndex > total) {
         int lastPage = (int) Math.ceil(((double) total) / ((double) perPage));
         lastPage--;
         startingIndex = lastPage * perPage;
      }

      int endingIndex = startingIndex + perPage;

      if (endingIndex > sourceList.size()) {
         endingIndex = sourceList.size();
      }

      model.put("listScroll", new ListScroll(perPage, sourceList.size(), startingIndex));

      return sourceList.subList(startingIndex, endingIndex);
   }

   public int getPerPage() {
      return perPage;
   }

   public void setPerPage(int perPage) {
      this.perPage = perPage;
   }
}
