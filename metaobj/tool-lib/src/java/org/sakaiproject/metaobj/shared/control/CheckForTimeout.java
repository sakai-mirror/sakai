package org.sakaiproject.metaobj.shared.control;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.sakaiproject.metaobj.security.AuthenticationManager;
import org.sakaiproject.metaobj.shared.model.Agent;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class CheckForTimeout implements HandlerInterceptor {
   private AuthenticationManager authenticationManager;
   private String timeoutUrl;
   private List ignoreList;

   public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
      if (getIgnoreList().contains(request.getServletPath())){
         return true;
      }
      Agent agent = getAuthenticationManager().getAgent();
      boolean timeOut = (agent == null  || agent.getId() == null || agent.getId().getValue().length() == 0);
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
