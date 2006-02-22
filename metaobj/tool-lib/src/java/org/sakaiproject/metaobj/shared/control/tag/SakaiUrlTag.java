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
package org.sakaiproject.metaobj.shared.control.tag;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.taglibs.standard.tag.el.core.UrlTag;
import org.sakaiproject.api.kernel.component.cover.ComponentManager;
import org.sakaiproject.api.kernel.tool.Tool;
import org.sakaiproject.metaobj.shared.mgt.PortalParamManager;
import org.sakaiproject.metaobj.shared.model.OspException;
import org.sakaiproject.util.web.Web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class SakaiUrlTag extends UrlTag {
   protected final transient Log logger = LogFactory.getLog(getClass());

   protected boolean includeParams = true;
   protected boolean includeQuestion = true;

   public int doStartTag() throws JspException {
      int returned = super.doStartTag();

      if (includeParams) {
         Map params = getPortalParamManager().getParams(pageContext.getRequest());

         if (params.size() == 0) {
            addParameter("1", "1");
         }

         for (Iterator i = params.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            try {
               addParameter((String) entry.getKey(), (String) entry.getValue());
            }
            catch (RuntimeException exp) {
               logger.error("", exp);
               throw exp;
            }
         }
      }

      return returned;
   }

   public int doEndTag() throws JspException {
      HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();


      req.setAttribute(Tool.NATIVE_URL, null); //signal to WrappedRequest that we want the Sakai managed

      String toolContext = req.getContextPath();
      String toolPath = req.getPathInfo();

      req.setAttribute(Tool.NATIVE_URL, Tool.NATIVE_URL);


      if (context == null && value.startsWith("/")) {
      }
      else {
         if (!value.startsWith(".")) { //excluding dots allows relative links such as ../../sakai-legacy/images/pdf.gif
            //value="/member/"+value;
            value = "/" + value;
         }
      }

      context = toolContext;

      logger.debug("tag value=" + value +
            " context=" + context + " web util val=" + Web.returnUrl(req, value));

      if (!includeQuestion) {
         BodyContent content = pageContext.pushBody();

         int returnVal = super.doEndTag();

         String contentValue = content.getString();
         int question = contentValue.indexOf('?');
         if (question != -1) {
            contentValue = contentValue.replace('?', '&');
         }

         logger.debug("tag before: " + contentValue);
         contentValue = ((HttpServletResponse)
               pageContext.getResponse()).encodeURL(contentValue);
         logger.debug("tag after: " + contentValue);

         pageContext.popBody();

         try {
            pageContext.getOut().print(contentValue);
         }
         catch (IOException ex) {
            throw new OspException("", ex);
         }

         return returnVal;
      }
      else {
         return super.doEndTag();
      }

   }

   public void setIncludeParams(boolean includeParams) {
      this.includeParams = includeParams;
   }

   public void setIncludeQuestion(boolean includeQuestion) {
      this.includeQuestion = includeQuestion;
   }

   public void release() {
      context = null;
      includeParams = true;
      includeQuestion = true;
      super.release();
   }

   protected PortalParamManager getPortalParamManager() {
      return (PortalParamManager)
            ComponentManager.getInstance().get(PortalParamManager.class.getName());
   }

}
