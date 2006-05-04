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
package org.sakaiproject.metaobj.shared.control.servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SakaiComponentFilter implements RequestSetupFilter {
   protected final transient Log logger = LogFactory.getLog(getClass());

   /**
    * This request's parsed parameters
    */
   protected final static String ATTR_PARAMS = "sakai.wrapper.params";

   /**
    * This request's return URL root
    */
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
         getMultipartResolver().cleanupMultipart((MultipartHttpServletRequest) req);
      }
   }


}
