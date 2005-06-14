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
 * $Header: /cvs/help/component/src/java/org/sakaiproject/component/help/model/AttributeAppender.java,v 1.1 2004/11/06 02:33:14 casong.indiana.edu Exp $
 * $Revision: 1.1 $
 * $Date: 2004/11/06 02:33:14 $
 */
package org.sakaiproject.component.help.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.help.model.UrlAppender;
import org.sakaiproject.service.help.Resource;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

public class AttributeAppender implements UrlAppender {
   private Map attributes;
   protected final Log logger = LogFactory.getLog(getClass());

   public URL getUrl(Resource resource, URL url) throws MalformedURLException {
      StringBuffer buffer = new StringBuffer();
      Map attributes = getAttributes();
      buffer.append(url.toString());

      if (buffer.toString().indexOf("?") == -1) {
         buffer.append("?");
      } else {
         buffer.append("&");
      }

      int count = 0;
      for (Iterator i=attributes.keySet().iterator(); i.hasNext();){
         if (count != 0){
            buffer.append("&");
         }
         String key = (String)i.next();
         buffer.append(key);
         buffer.append("=");
         try {
            buffer.append(attributes.get(URLEncoder.encode(key,"UTF-8")));
         } catch (UnsupportedEncodingException e) {
            logger.error(e);
         }
         count++;
      }
      return new URL(buffer.toString());
   }

   public Map getAttributes() {
      return attributes;
   }

   public void setAttributes(Map attributes) {
      this.attributes = attributes;
   }
}
