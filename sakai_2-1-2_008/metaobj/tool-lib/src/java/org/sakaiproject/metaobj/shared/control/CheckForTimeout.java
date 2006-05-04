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
package org.sakaiproject.metaobj.shared.control;

import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class CheckForTimeout implements HandlerInterceptor {
   private AuthenticationManager authenticationManager;
   private String timeoutUrl;
   private List ignoreList;

   public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
      if (getIgnoreList().contains(request.getServletPath())) {
         return true;
      }
      Agent agent = getAuthenticationManager().getAgent();
      boolean timeOut = (agent == null || agent.getId() == null || agent.getId().getValue().length() == 0);
      if (timeOut) {
         response.sendRedirect(getTimeoutUrl());
      }
      return !timeOut;
   }


   public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
   }

   public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
   }

   public AuthenticationManager getAuthenticationManager() {
      return authenticationManager;
   }

   public void setAuthenticationManager(AuthenticationManager authenticationManager) {
      this.authenticationManager = authenticationManager;
   }

   public String getTimeoutUrl() {
      return timeoutUrl;
   }

   public void setTimeoutUrl(String timeoutUrl) {
      this.timeoutUrl = timeoutUrl;
   }

   public List getIgnoreList() {
      return ignoreList;
   }

   public void setIgnoreList(List ignoreList) {
      this.ignoreList = ignoreList;
   }
}
