/**********************************************************************************
 *
 * $Header: /opt/CVS/osp2.x/HomesTool/src/java/org/theospi/utils/mvc/impl/ListScrollIndexerImpl.java,v 1.1 2005/06/28 13:31:53 chmaurer Exp $
 *
 ***********************************************************************************
 * Copyright (c) 2005 the r-smart group, inc.
 **********************************************************************************/
package org.sakaiproject.metaobj.utils.mvc.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScrollIndexer;
import org.sakaiproject.metaobj.utils.mvc.intf.ListScroll;

import java.util.List;
import java.util.Map;

public class ListScrollIndexerImpl implements ListScrollIndexer {
   protected final transient Log logger = LogFactory.getLog(getClass());
   private int perPage;

   public List indexList(Map request, Map model, List sourceList) {
      int startingIndex = 0;
      int total = sourceList.size();

      String ensureVisible = (String)request.get(ListScroll.ENSURE_VISIBLE_TAG);

      if (ensureVisible != null) {
         int visibleIndex = Integer.parseInt(ensureVisible);
         int startingPage = (visibleIndex/perPage);
         startingIndex = startingPage * perPage;
      }
      else {
         String newStart = (String)request.get(ListScroll.STARTING_INDEX_TAG);

         if (newStart != null) {
            startingIndex = Integer.parseInt(newStart);
            if (startingIndex < 0) {
               startingIndex = 0;
            }
         }
      }

      if (startingIndex > total) {
         int lastPage = (int) Math.ceil(((double)total)/((double)perPage));
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
