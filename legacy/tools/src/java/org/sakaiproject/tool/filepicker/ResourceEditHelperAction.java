/**********************************************************************************
* $URL$
* $Id$
***********************************************************************************
*
* Copyright (c) 2003, 2004 The Regents of the University of Michigan, Trustees of Indiana University,
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
package org.sakaiproject.tool.filepicker;

import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.cheftool.Context;
import org.sakaiproject.cheftool.RunData;
import org.sakaiproject.cheftool.VelocityPortlet;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.legacy.filepicker.ResourceEditingHelper;
import org.sakaiproject.tool.content.ResourcesAction;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jan 12, 2006
 * Time: 10:32:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class ResourceEditHelperAction extends FilePickerAction {

   protected String initHelperAction(VelocityPortlet portlet, Context context, RunData rundata,
                                     SessionState sstate, ToolSession toolSession) {
      if (toolSession.getAttribute(ResourceEditingHelper.ATTACHMENT_ID) != null) {
         sstate.setAttribute(ResourcesAction.STATE_RESOURCES_MODE, ResourcesAction.MODE_ATTACHMENT_EDIT_ITEM);
         sstate.setAttribute(ResourcesAction.STATE_ATTACH_ITEM_ID,
               toolSession.getAttribute(ResourceEditingHelper.ATTACHMENT_ID));
      }
      else {
         // must be create
         sstate.setAttribute(ResourcesAction.STATE_RESOURCES_MODE, ResourcesAction.MODE_ATTACHMENT_NEW_ITEM);
         Object createType = toolSession.getAttribute(ResourceEditingHelper.CREATE_TYPE);

         if (ResourceEditingHelper.CREATE_TYPE_FORM.equals(createType)) {
            sstate.setAttribute(ResourcesAction.STATE_CREATE_TYPE,
                  ResourcesAction.TYPE_FORM);
            Object createSubType = toolSession.getAttribute(ResourceEditingHelper.CREATE_SUB_TYPE);
            if (createSubType != null) {
               sstate.setAttribute(ResourcesAction.STATE_STRUCTOBJ_TYPE,
                     createSubType);
            }
         }

         sstate.setAttribute(ResourcesAction.STATE_CREATE_NUMBER, new Integer(1));
         sstate.setAttribute(ResourcesAction.STATE_CREATE_COLLECTION_ID,
               toolSession.getAttribute(ResourceEditingHelper.CREATE_PARENT));
         sstate.setAttribute(ResourcesAction.STATE_ATTACH_LINKS, Boolean.TRUE.toString());
      }

      initMessage(toolSession, sstate);
      return ResourcesAction.MODE_HELPER;
   }
}
