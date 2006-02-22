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
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import org.apache.taglibs.standard.tag.common.core.Util;
import org.apache.taglibs.standard.tag.common.fmt.MessageSupport;
import org.apache.taglibs.standard.tag.el.fmt.MessageTag;
import org.sakaiproject.metaobj.shared.model.OspException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import java.io.IOException;

public class Message extends MessageTag {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private String text = null;
   private String localVar = null;
   private int localScope = PageContext.PAGE_SCOPE;  // 'scope' attribute


   // Releases any resources we may have (or inherit)
   public void release() {
      localScope = PageContext.PAGE_SCOPE;
      text = null;
      localVar = null;
      super.release();
   }

   public void setScope(String scope) {
      super.setScope(scope);
      localScope = Util.getScope(scope);
   }

   public void setVar(String var) {
      super.setVar(var);
      localVar = var;
   }

   public int doEndTag() throws JspException {

      BodyContent content = null;

      if (localVar == null) {
         content = pageContext.pushBody();
      }

      int returnVal = super.doEndTag();

      if (localVar != null) {
         String varValue = (String) pageContext.getAttribute(localVar, localScope);
         if (varValue.startsWith(MessageSupport.UNDEFINED_KEY) &&
               varValue.endsWith(MessageSupport.UNDEFINED_KEY) &&
               text != null) {
            varValue = (String) ExpressionEvaluatorManager.evaluate("text", text, String.class, this, pageContext);
            pageContext.setAttribute(localVar, varValue, localScope);
         }
      }
      else {
         String contentValue = content.getString();
         contentValue = content.getString();
         if (contentValue.startsWith(MessageSupport.UNDEFINED_KEY) &&
               contentValue.endsWith(MessageSupport.UNDEFINED_KEY) &&
               text != null) {
            contentValue = (String) ExpressionEvaluatorManager.evaluate("text", text, String.class, this, pageContext);
         }

         pageContext.popBody();

         try {
            pageContext.getOut().print(contentValue);
         }
         catch (IOException ex) {
            throw new OspException("", ex);
         }
      }

      return returnVal;
   }


   public String getText() {
      return text;
   }

   public void setText(String text) {
      this.text = text;
   }
}
