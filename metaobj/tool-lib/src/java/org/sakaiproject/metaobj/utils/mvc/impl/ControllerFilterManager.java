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

import org.sakaiproject.metaobj.utils.mvc.intf.ControllerFilter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class ControllerFilterManager implements ApplicationContextAware {
   private Collection filters;

   public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
      filters = applicationContext.getBeansOfType(ControllerFilter.class, false, false).values();
   }

   public Collection getFilters() {
      return filters;
   }

   public void processFilters(Map request, Map session, Map application, ModelAndView modelAndView, String formView) {
      for (Iterator i = filters.iterator(); i.hasNext();) {
         ControllerFilter filter = (ControllerFilter) i.next();
         filter.doFilter(request, session, application, modelAndView, formView);
      }
   }
}
