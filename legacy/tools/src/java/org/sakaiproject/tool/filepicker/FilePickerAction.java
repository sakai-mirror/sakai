package org.sakaiproject.tool.filepicker;

import org.sakaiproject.cheftool.VelocityPortletPaneledAction;
import org.sakaiproject.cheftool.VelocityPortlet;
import org.sakaiproject.cheftool.Context;
import org.sakaiproject.cheftool.RunData;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.legacy.resource.ReferenceVector;
import org.sakaiproject.service.legacy.filepicker.FilePickerHelper;
import org.sakaiproject.tool.helper.AttachmentAction;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.api.kernel.tool.Tool;
import org.sakaiproject.api.kernel.tool.cover.ToolManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jul 19, 2005
 * Time: 12:35:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class FilePickerAction extends VelocityPortletPaneledAction {

   protected void toolModeDispatch(String methodBase, String methodExt,
                                   HttpServletRequest req, HttpServletResponse res) {
      SessionState sstate = getState(req);

      if (AttachmentAction.MODE_DONE.equals(sstate.getAttribute(AttachmentAction.STATE_MODE))) {
         ToolSession toolSession = SessionManager.getCurrentToolSession();
         ReferenceVector attachments = (ReferenceVector) sstate.getAttribute(AttachmentAction.STATE_ATTACHMENTS);

         if (attachments != null) {
            toolSession.setAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS,
                  new ReferenceVector(attachments));
         }
         else {
            toolSession.setAttribute(FilePickerHelper.FILE_PICKER_CANCEL, "true");
         }

         // clean up
         sstate.removeAttribute(AttachmentAction.STATE_MODE);
         sstate.removeAttribute(AttachmentAction.STATE_ATTACHMENTS);

         Tool tool = ToolManager.getCurrentTool();

         String url = (String) SessionManager.getCurrentToolSession().getAttribute(
               tool.getId() + Tool.HELPER_DONE_URL);

         try {
            res.sendRedirect(url);
         }
         catch (IOException e) {
            Log.warn("chef", this + " : ", e);
         }
         return;
      }
      else {
         super.toolModeDispatch(methodBase, methodExt, req, res);
      }
   } // toolModeDispatch

   /**
    * Default is to use when Portal starts up
    */
   public String buildMainPanelContext(VelocityPortlet portlet,
      Context context,
      RunData rundata,
      SessionState sstate)
   {
      // if we are in edit attachments...
      String mode = (String) sstate.getAttribute(AttachmentAction.STATE_MODE);

      if (mode == null) {
         initPicker(portlet, context, rundata, sstate);
         sstate.setAttribute(AttachmentAction.STATE_MODE, AttachmentAction.MODE_MAIN);
         mode = AttachmentAction.MODE_MAIN;
      }

      return AttachmentAction.buildHelperContext(portlet, context, rundata, sstate);
   }

   protected void initPicker(VelocityPortlet portlet, Context context, RunData rundata, SessionState sstate) {
      ToolSession toolSession = SessionManager.getCurrentToolSession();
      ReferenceVector attachments =
            (ReferenceVector) toolSession.getAttribute(
                  FilePickerHelper.FILE_PICKER_ATTACHMENTS);
      toolSession.removeAttribute(FilePickerHelper.FILE_PICKER_CANCEL);

      if (attachments != null) {
         sstate.setAttribute(AttachmentAction.STATE_ATTACHMENTS, new ReferenceVector(attachments));
      }
   }

}
