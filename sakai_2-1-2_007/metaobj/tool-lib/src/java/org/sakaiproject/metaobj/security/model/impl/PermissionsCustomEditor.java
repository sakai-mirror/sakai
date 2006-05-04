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
package org.sakaiproject.metaobj.security.model.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.metaobj.security.model.Permission;
import org.sakaiproject.metaobj.shared.mgt.AgentManager;
import org.sakaiproject.metaobj.shared.model.Agent;
import org.sakaiproject.metaobj.utils.mvc.intf.TypedPropertyEditor;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class PermissionsCustomEditor extends PropertyEditorSupport implements TypedPropertyEditor {
   protected final transient Log logger = LogFactory.getLog(getClass());

   private AgentManager agentManager = null;

   public void setAsText(String text) throws IllegalArgumentException {
      if (text == null || text.length() == 0) {
         setValue(new ArrayList());
      }
      else {
         String[] items = text.split(",");
         List permissions = new ArrayList();
         for (int i = 0; i < items.length; i++) {
            if (items[i].length() == 0) {
               continue;
            }
            String[] values = items[i].split("~");
            String role = values[0];
            String function = values[1];
            Agent agent = getAgentManager().getWorksiteRole(role);
            permissions.add(new Permission(agent, function));
         }
         setValue(permissions);
      }
   }

   public String getAsText() {
      if (getValue() == null) {
         return null;
      }

      StringBuffer buffer = new StringBuffer();
      for (Iterator i = ((Collection) getValue()).iterator(); i.hasNext();) {
         buffer.append(i.next().toString());
         if (i.hasNext()) {
            buffer.append(",");
         }
      }
      return buffer.toString();
   }

   public Class getType() {
      return List.class;
   }

   public AgentManager getAgentManager() {
      return agentManager;
   }

   public void setAgentManager(AgentManager agentManager) {
      this.agentManager = agentManager;
   }

}
