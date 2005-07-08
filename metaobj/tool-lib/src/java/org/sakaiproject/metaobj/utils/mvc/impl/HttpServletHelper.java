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
 * $Header: /opt/CVS/osp2.x/HomesTool/src/java/org/theospi/utils/mvc/impl/HttpServletHelper.java,v 1.2 2005/07/01 13:12:55 jellis Exp $
 * $Revision: 1.2 $
 * $Date: 2005/07/01 13:12:55 $
 */
/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Mar 30, 2004
 * Time: 11:18:43 AM
 * To change this template use File | Settings | File Templates.
 */
package org.sakaiproject.metaobj.utils.mvc.impl;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HttpServletHelper {
   private static HttpServletHelper ourInstance = null;

   public static HttpServletHelper getInstance() {
      if (ourInstance == null) {
         ourInstance = new HttpServletHelper();
      }

      return ourInstance;
   }

   private HttpServletHelper() {
   }


   public void reloadApplicationMap(HttpServletRequest request, Map map) {

      /*
      for (Iterator keys = map.keySet().iterator(); keys.hasNext();) {
         String key = (String) keys.next();
         if (map.get(key) == null) {
            request.getSession().getServletContext().removeAttribute(key);
         }
         else if (!map.get(key).equals(request.getSession().getServletContext().getAttribute(key))) {
            request.getSession().getServletContext().setAttribute(key,
               map.get(key));
         }
      }

      Enumeration enumer = request.getSession().getServletContext().getAttributeNames();

      while (enumer.hasMoreElements()) {
         String key = (String)enumer.nextElement();

         if (map.get(key) == null) {
            request.getSession().getServletContext().removeAttribute(key);
         }
      }
      */
   }

   public Map createApplicationMap(HttpServletRequest request) {
      Map parameters = new HashMap();
      //Enumeration enumer = request.getSession().getServletContext().getAttributeNames();

      //while (enumer.hasMoreElements()) {
      //   String key = (String) enumer.nextElement();
      //   parameters.put(key, request.getSession().getServletContext().getAttribute(key));
      //}

      return parameters;
   }

   public void reloadSessionMap(HttpServletRequest request, Map map) {

      for (Iterator keys = map.keySet().iterator(); keys.hasNext();) {
         String key = (String) keys.next();

         if (map.get(key) == null) {
            request.getSession().removeAttribute(key);
         }
         else if (!map.get(key).equals(request.getSession().getAttribute(key))) {
            request.getSession().setAttribute(key, map.get(key));
         }
      }

      Enumeration enumer = request.getSession().getAttributeNames();

      while (enumer.hasMoreElements()) {
         String key = (String)enumer.nextElement();

         if (map.get(key) == null) {
            request.getSession().removeAttribute(key);
         }
      }
   }

   public Map createSessionMap(HttpServletRequest request) {
      Map parameters = new HashMap();
      Enumeration enumer = request.getSession().getAttributeNames();

      while (enumer.hasMoreElements()) {
         String key = (String) enumer.nextElement();
         parameters.put(key, request.getSession().getAttribute(key));
      }

      return parameters;
   }

   public void reloadRequestMap(HttpServletRequest request, Map map) {

      for (Iterator keys = map.keySet().iterator(); keys.hasNext();) {
         String key = (String) keys.next();
         if (map.get(key) == null) {
            request.removeAttribute(key);
         }
         else if (!map.get(key).equals(request.getAttribute(key))) {
            request.setAttribute(key, map.get(key));
         }
      }

      Enumeration enumer = request.getAttributeNames();

      while (enumer.hasMoreElements()) {
         String key = (String)enumer.nextElement();

         if (map.get(key) == null) {
            request.removeAttribute(key);
         }
      }
   }

   public Map createRequestMap(HttpServletRequest request) {
      Map parameters = new HashMap();
      Enumeration enumer = request.getAttributeNames();

      while (enumer.hasMoreElements()) {
         String key = (String) enumer.nextElement();
         parameters.put(key, request.getAttribute(key));
      }

      enumer = request.getParameterNames();

      while (enumer.hasMoreElements()) {
         String key = (String) enumer.nextElement();
         if (request.getParameterValues(key).length > 1) {
            parameters.put(key, request.getParameterValues(key));
         } else {
            parameters.put(key, request.getParameter(key));
         }
      }

      return parameters;
   }


}
