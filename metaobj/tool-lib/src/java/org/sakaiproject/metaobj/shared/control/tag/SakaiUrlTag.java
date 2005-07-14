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
 * $URL: /opt/CVS/osp2.x/HomesTool/src/java/org/theospi/metaobj/shared/control/tag/SakaiUrlTag.java,v 1.2 2005/06/30 17:34:21 chmaurer Exp $
 * $Revision: 1.2 $
 * $Date: 2005/06/30 17:34:21 $
 */
package org.sakaiproject.metaobj.shared.control.tag;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.taglibs.standard.tag.el.core.UrlTag;
import org.sakaiproject.api.kernel.component.cover.ComponentManager;
import org.sakaiproject.api.kernel.tool.Tool;
import org.sakaiproject.util.web.Web;
import org.sakaiproject.metaobj.shared.mgt.PortalParamManager;
import org.sakaiproject.metaobj.shared.model.OspException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import java.util.Map;
import java.util.Iterator;
import java.io.IOException;

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

         for (Iterator i=params.entrySet().iterator();i.hasNext();) {
            Map.Entry entry = (Map.Entry)i.next();
            try {
               addParameter((String)entry.getKey(), (String)entry.getValue());
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
      HttpServletRequest req=(HttpServletRequest)pageContext.getRequest();


      req.setAttribute(Tool.NATIVE_URL,null); //signal to WrappedRequest that we want the Sakai managed

      String toolContext=req.getContextPath();
      String toolPath=req.getPathInfo();

      req.setAttribute(Tool.NATIVE_URL, Tool.NATIVE_URL);


      if (context == null && value.startsWith("/")) {
      } else {
          if (!value.startsWith(".")) { //excluding dots allows relative links such as ../../sakai-legacy/images/pdf.gif
            //value="/member/"+value;
            value="/"+value;
          }
      }

       context=toolContext;
       
       logger.debug("tag value="+value+
             " context="+context+" web util val="+ Web.returnUrl(req,value));

      if (!includeQuestion) {
         BodyContent content = pageContext.pushBody();

         int returnVal = super.doEndTag();

         String contentValue = content.getString();
         int question = contentValue.indexOf('?');
         if (question != -1) {
            contentValue = contentValue.replace('?', '&');
         }

          logger.debug("tag before: "+contentValue);
          contentValue=((HttpServletResponse)
                  pageContext.getResponse()).encodeURL(contentValue);
          logger.debug("tag after: "+contentValue);

         pageContext.popBody();

         try {
            pageContext.getOut().print(contentValue);
         } catch (IOException ex) {
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
