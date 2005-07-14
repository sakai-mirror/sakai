/*
 * The Open Source Portfolio Initiative Software is Licensed under the Educational Community License Version 1.0:
 *
 * This Educational Community License (the "License") applies to any original work of authorship
 * (the "Original Work") whose owner (the "Licensor") has placed the following notice immediately
 * following the copyright notice for the Original Work:
 *
 * Copyright (c) 2004 Trustees of Indiana University and r-smart Corporation
 *
 * This Original Work, including software, source code, documents, or other related items, is being
 * provided by the copyright holder(s) subject to the terms of the Educational Community License.
 * By obtaining, using and/or copying this Original Work, you agree that you have read, understand,
 * and will comply with the following terms and conditions of the Educational Community License:
 *
 * Permission to use, copy, modify, merge, publish, distribute, and sublicense this Original Work and
 * its documentation, with or without modification, for any purpose, and without fee or royalty to the
 * copyright holder(s) is hereby granted, provided that you include the following on ALL copies of the
 * Original Work or portions thereof, including modifications or derivatives, that you make:
 *
 * - The full text of the Educational Community License in a location viewable to users of the
 * redistributed or derivative work.
 *
 * - Any pre-existing intellectual property disclaimers, notices, or terms and conditions.
 *
 * - Notice of any changes or modifications to the Original Work, including the date the changes were made.
 *
 * - Any modifications of the Original Work must be distributed in such a manner as to avoid any confusion
 *  with the Original Work of the copyright holders.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * The name and trademarks of copyright holder(s) may NOT be used in advertising or publicity pertaining
 * to the Original or Derivative Works without specific, written prior permission. Title to copyright
 * in the Original Work and any associated documentation will at all times remain with the copyright holders.
 *
 * $URL: /opt/CVS/osp2.x/HomesTool/src/java/org/theospi/metaobj/shared/control/tag/Message.java,v 1.1 2005/06/28 13:31:53 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/28 13:31:53 $
 */
package org.sakaiproject.metaobj.shared.control.tag;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.taglibs.standard.tag.el.fmt.MessageTag;
import org.apache.taglibs.standard.tag.common.fmt.MessageSupport;
import org.apache.taglibs.standard.tag.common.core.Util;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
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
         String varValue = (String)pageContext.getAttribute(localVar, localScope);
         if (varValue.startsWith(MessageSupport.UNDEFINED_KEY) &&
            varValue.endsWith(MessageSupport.UNDEFINED_KEY) &&
            text != null) {
            varValue = (String) ExpressionEvaluatorManager.evaluate(
               "text", text, String.class, this, pageContext);
            pageContext.setAttribute(localVar, varValue, localScope);
         }
      }
      else {
         String contentValue = content.getString();
         contentValue = content.getString();
         if (contentValue.startsWith(MessageSupport.UNDEFINED_KEY) &&
            contentValue.endsWith(MessageSupport.UNDEFINED_KEY) &&
            text != null) {
            contentValue = (String) ExpressionEvaluatorManager.evaluate(
               "text", text, String.class, this, pageContext);
         }

         pageContext.popBody();

         try {
            pageContext.getOut().print(contentValue);
         } catch (IOException ex) {
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
