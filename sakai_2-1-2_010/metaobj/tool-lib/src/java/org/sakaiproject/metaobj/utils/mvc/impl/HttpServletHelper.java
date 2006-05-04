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
         String key = (String) enumer.nextElement();

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
         String key = (String) enumer.nextElement();

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
         }
         else {
            parameters.put(key, request.getParameter(key));
         }
      }

      return parameters;
   }


}
