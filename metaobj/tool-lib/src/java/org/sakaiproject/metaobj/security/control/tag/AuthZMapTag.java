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
package org.sakaiproject.metaobj.security.control.tag;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import org.sakaiproject.api.kernel.component.cover.ComponentManager;
import org.sakaiproject.metaobj.security.AuthorizationFacade;
import org.sakaiproject.metaobj.security.model.AuthZMap;
import org.sakaiproject.metaobj.shared.mgt.IdManager;
import org.sakaiproject.metaobj.shared.model.Id;
import org.sakaiproject.service.framework.portal.cover.PortalService;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import java.util.Map;

public class AuthZMapTag extends TagSupport {

   private Id qualifier;
   private String qualifierExpression;
   private boolean useSite;
   private String prefix;
   private String var;
   private int scope;

   protected final transient Log logger = LogFactory.getLog(getClass());

   public AuthZMapTag() {
      init();
   }

   /**
    * Default processing of the start tag, returning SKIP_BODY.
    *
    * @return SKIP_BODY
    * @throws javax.servlet.jsp.JspException if an error occurs while processing this tag
    * @see javax.servlet.jsp.tagext.Tag#doStartTag()
    */

   public int doStartTag() throws JspException {
      Map authz = new AuthZMap(getAuthzFacade(), getPrefix(), evaluateQualifier());

      pageContext.setAttribute(getVar(), authz, getScope());

      return super.doStartTag();
   }

   /**
    * Release state.
    *
    * @see javax.servlet.jsp.tagext.Tag#release()
    */

   public void release() {
      super.release();
      init();
   }

   protected AuthorizationFacade getAuthzFacade() {
      return (AuthorizationFacade) ComponentManager.getInstance().get("org.sakaiproject.metaobj.security.AuthorizationFacade");
   }

   public String getPrefix() {
      return prefix;
   }

   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }

   public Id evaluateQualifier() throws JspException {
      if (isUseSite()) {
         qualifier = getIdManager().getId(PortalService.getCurrentSiteId());
      }
      else if (qualifierExpression == null) {
         qualifier = getIdManager().getId(PortalService.getCurrentToolId());
      }
      else {
         qualifier = (Id) ExpressionEvaluatorManager.evaluate("qualifier", qualifierExpression,
               Id.class, this, pageContext);
      }
      return qualifier;
   }

   public void setQualifier(String qualifierExpression) {
      this.qualifierExpression = qualifierExpression;
   }

   public int getScope() {
      return scope;
   }

   public String getVar() {
      return var;
   }

   public void setVar(String var) {
      this.var = var;
   }

   public void setScope(String scope) {
      if (scope.equalsIgnoreCase("page")) {
         this.scope = PageContext.PAGE_SCOPE;
      }
      else if (scope.equalsIgnoreCase("request")) {
         this.scope = PageContext.REQUEST_SCOPE;
      }
      else if (scope.equalsIgnoreCase("session")) {
         this.scope = PageContext.SESSION_SCOPE;
      }
      else if (scope.equalsIgnoreCase("application")) {
         this.scope = PageContext.APPLICATION_SCOPE;
      }

      // TODO: Add error handling?  Needs direction from spec.
   }

   // initializes internal state
   protected void init() {
      var = "can";
      scope = PageContext.PAGE_SCOPE;
      setUseSite(false);
   }

   protected IdManager getIdManager() {
      return (IdManager) ComponentManager.getInstance().get("idManager");
   }

   public boolean isUseSite() {
      return useSite;
   }

   public void setUseSite(boolean useSite) {
      this.useSite = useSite;
   }

}
