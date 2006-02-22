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
import org.sakaiproject.api.kernel.component.cover.ComponentManager;
import org.sakaiproject.metaobj.shared.mgt.PortalParamManager;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;

public class FormTag extends BodyTagSupport {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private final static String paramFormat =
         "<input type=\"hidden\" name=\"{0}\" value=\"{1}\" />";

   public int doStartTag() throws JspException {
      // add hidden inputs here
      Map newParams = getPortalParamManager().getParams(this.pageContext.getRequest());

      for (Iterator i = newParams.entrySet().iterator(); i.hasNext();) {
         Map.Entry entry = (Map.Entry) i.next();
         try {
            pageContext.getOut().write(MessageFormat.format(paramFormat,
                  new Object[]{entry.getKey(), entry.getValue()}));
         }
         catch (IOException e) {
            logger.error("", e);
            throw new JspException(e);
         }
      }

      return EVAL_BODY_INCLUDE;
   }

   protected PortalParamManager getPortalParamManager() {
      return (PortalParamManager)
            ComponentManager.getInstance().get(PortalParamManager.class.getName());
   }
}
