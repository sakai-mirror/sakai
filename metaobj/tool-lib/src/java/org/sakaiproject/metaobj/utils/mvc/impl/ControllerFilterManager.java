/*
 * $Header: /opt/CVS/osp2.x/HomesTool/src/java/org/theospi/utils/mvc/impl/ControllerFilterManager.java,v 1.1 2005/06/28 13:31:53 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/28 13:31:53 $
 */
package org.sakaiproject.metaobj.utils.mvc.impl;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.ModelAndView;
import org.sakaiproject.metaobj.utils.mvc.intf.ControllerFilter;

import java.util.Collection;
import java.util.Map;
import java.util.Iterator;

public class ControllerFilterManager implements ApplicationContextAware {
   private Collection filters;

   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      filters = applicationContext.getBeansOfType(ControllerFilter.class, false, false).values();
   }

   public Collection getFilters() {
      return filters;
   }

   public void processFilters(Map request, Map session, Map application, ModelAndView modelAndView, String formView) {
      for (Iterator i=filters.iterator(); i.hasNext(); ){
         ControllerFilter filter =  (ControllerFilter) i.next();
         filter.doFilter(request, session, application, modelAndView, formView);
      }
   }
}
