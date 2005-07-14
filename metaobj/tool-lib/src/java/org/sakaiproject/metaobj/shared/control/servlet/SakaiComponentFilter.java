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
 * $URL: /opt/CVS/osp2.x/HomesTool/src/java/org/theospi/metaobj/shared/control/servlet/SakaiComponentFilter.java,v 1.1 2005/06/28 13:31:53 chmaurer Exp $
 * $Revision: 1.1 $
 * $Date: 2005/06/28 13:31:53 $
 */
package org.sakaiproject.metaobj.shared.control.servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.service.framework.current.cover.CurrentService;
//import org.sakaiproject.util.Setup;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SakaiComponentFilter implements RequestSetupFilter {
   protected final transient Log logger = LogFactory.getLog(getClass());

   /** This request's parsed parameters */
   protected final static String ATTR_PARAMS = "sakai.wrapper.params";

   /** This request's return URL root */
   protected final static String ATTR_RETURN_URL = "sakai.wrapper.return.url";

   private MultipartResolver multipartResolver = null;

   protected MultipartResolver getMultipartResolver() {
      return multipartResolver;
   }

   public void setMultipartResolver(MultipartResolver multipartResolver) {
      this.multipartResolver = multipartResolver;
   }

   public boolean processRequest(HttpServletRequest request) {
//      if ("Title".equals(request.getParameter("panel"))) {
//         return false;
//      }
      return true;
   }

   public HttpServletRequest wrapRequest(HttpServletRequest req, HttpServletResponse response) throws Exception {
      if (getMultipartResolver().isMultipart(req)) {
         req = getMultipartResolver().resolveMultipart(req);
      }

//    TODO: fix wrapRequest method  
      logger.warn("Bad things could happen here, as this is unsupported: CurrentService.startThread(\"REQUEST\")");
     //CurrentService.startThread("REQUEST");


     //if (Setup.setup(req, (HttpServletResponse) response /*, false*/)) {
     //   return null;
     //}

      return req;
   }

   public void tearDown(HttpServletRequest req) {
      //CurrentService.clearInThread();
      logger.error("CurrentService.clearInThread is not supported");
      if (req instanceof MultipartHttpServletRequest) {
         getMultipartResolver().cleanupMultipart(
            (MultipartHttpServletRequest)req);
      }
   }


}
