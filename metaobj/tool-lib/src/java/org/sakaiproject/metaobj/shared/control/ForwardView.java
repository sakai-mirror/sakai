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
package org.sakaiproject.metaobj.shared.control;

import org.springframework.web.servlet.view.AbstractUrlBasedView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: May 18, 2004
 * Time: 3:12:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class ForwardView extends AbstractUrlBasedView {

   private String action = "";

   /**
    * Subclasses must implement this method to render the view.
    * <p>The first take will be preparing the request: This may include setting
    * the model elements as request attributes, e.g. in the case of a JSP view.
    *
    * @param model    combined output Map, with dynamic values taking precedence
    *                 over static attributes
    * @param request  current HTTP request
    * @param response current HTTP response
    * @throws Exception if rendering failed
    */
   protected void renderMergedOutputModel(Map model, HttpServletRequest request,
                                          HttpServletResponse response) throws Exception {
      request.setAttribute("action", getUrl());

      for (Iterator i = model.entrySet().iterator(); i.hasNext();) {
         Map.Entry entry = (Map.Entry) i.next();
         request.setAttribute((String) entry.getKey(), entry.getValue());
      }

      request.getRequestDispatcher(getUrl()).forward(request, response);
   }

   public String getAction() {
      return action;
   }

   public void setAction(String action) {
      this.action = action;
   }
}
