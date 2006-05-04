package org.sakaiproject.tool.filepicker;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.api.kernel.tool.Tool;
import org.sakaiproject.api.kernel.tool.ToolException;
import org.sakaiproject.api.kernel.tool.cover.ToolManager;
import org.sakaiproject.cheftool.Context;
import org.sakaiproject.cheftool.RunData;
import org.sakaiproject.cheftool.VelocityPortlet;
import org.sakaiproject.cheftool.VelocityPortletPaneledAction;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.legacy.filepicker.FilePickerHelper;
import org.sakaiproject.service.legacy.resource.cover.EntityManager;
import org.sakaiproject.tool.content.ResourcesAction;
import org.sakaiproject.util.java.ResourceLoader;

/**
 * Created by IntelliJ IDEA.
 * User: John Ellis
 * Date: Jul 19, 2005
 * Time: 12:35:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class FilePickerAction extends VelocityPortletPaneledAction {

   /** Resource bundle using current language locale */
    private static ResourceLoader rb = new ResourceLoader("helper");

   protected void toolModeDispatch(String methodBase, String methodExt,
                                   HttpServletRequest req, HttpServletResponse res) throws ToolException {
      SessionState sstate = getState(req);

      if (ResourcesAction.MODE_ATTACHMENT_DONE.equals(sstate.getAttribute(ResourcesAction.STATE_RESOURCES_HELPER_MODE))) {
         ToolSession toolSession = SessionManager.getCurrentToolSession();

         if (sstate.getAttribute(ResourcesAction.STATE_HELPER_CANCELED_BY_USER) == null) {
            List attachments = (List) sstate.getAttribute(ResourcesAction.STATE_ATTACHMENTS);

            if (attachments != null) {
               toolSession.setAttribute(FilePickerHelper.FILE_PICKER_ATTACHMENTS,
                     EntityManager.newReferenceList(attachments));
            }
            else if (sstate.getAttribute(ResourcesAction.STATE_EDIT_ID) == null) {
               toolSession.setAttribute(FilePickerHelper.FILE_PICKER_CANCEL, "true");
            }
         }
         else {
            toolSession.setAttribute(FilePickerHelper.FILE_PICKER_CANCEL, "true");
         }

         cleanup(sstate);

         Tool tool = ToolManager.getCurrentTool();

         String url = (String) SessionManager.getCurrentToolSession().getAttribute(
               tool.getId() + Tool.HELPER_DONE_URL);
         
         SessionManager.getCurrentToolSession().removeAttribute(tool.getId() + Tool.HELPER_DONE_URL);

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

   protected void cleanup(SessionState sstate) {
      sstate.removeAttribute(ResourcesAction.STATE_MODE);
      sstate.removeAttribute(ResourcesAction.STATE_RESOURCES_HELPER_MODE);
      sstate.removeAttribute(ResourcesAction.STATE_ATTACHMENTS);
      sstate.removeAttribute(ResourcesAction.STATE_HELPER_CANCELED_BY_USER);
   }

   /**
    * Default is to use when Portal starts up
    */
   public String buildMainPanelContext(VelocityPortlet portlet,
      Context context,
      RunData rundata,
      SessionState sstate)
   {
      // if we are in edit attachments...
      String mode = (String) sstate.getAttribute(ResourcesAction.STATE_MODE);
      ToolSession toolSession = SessionManager.getCurrentToolSession();

      if (mode == null) {
         mode = initHelperAction(portlet, context, rundata, sstate, toolSession);
      }

      return ResourcesAction.buildHelperContext(portlet, context, rundata, sstate);
   }

   protected String initHelperAction(VelocityPortlet portlet, Context context, RunData rundata,
                                     SessionState sstate, ToolSession toolSession) {
      initPicker(portlet, context, rundata, sstate);
      sstate.setAttribute(ResourcesAction.STATE_MODE, ResourcesAction.MODE_HELPER);
      sstate.setAttribute(ResourcesAction.STATE_RESOURCES_HELPER_MODE, ResourcesAction.MODE_ATTACHMENT_SELECT);
      sstate.setAttribute(ResourcesAction.STATE_SHOW_ALL_SITES, Boolean.toString(true));
      
      // state attribute ResourcesAction.STATE_ATTACH_TOOL_NAME should be set with a string to indicate name of tool
      //String toolName = ToolManager.getCurrentTool().getTitle();
      //sstate.setAttribute(ResourcesAction.STATE_ATTACH_TOOL_NAME, toolName);

      if (toolSession.getAttribute(FilePickerHelper.FILE_PICKER_ATTACH_LINKS) != null) {
         sstate.setAttribute(ResourcesAction.STATE_ATTACH_LINKS,
            toolSession.getAttribute(FilePickerHelper.FILE_PICKER_ATTACH_LINKS));
         toolSession.removeAttribute(FilePickerHelper.FILE_PICKER_ATTACH_LINKS);
      }

      if (toolSession.getAttribute(FilePickerHelper.FILE_PICKER_MAX_ATTACHMENTS) != null) {
         sstate.setAttribute(ResourcesAction.STATE_ATTACH_CARDINALITY,
            toolSession.getAttribute(FilePickerHelper.FILE_PICKER_MAX_ATTACHMENTS));
      }

      return ResourcesAction.MODE_HELPER;
   }

   protected void initPicker(VelocityPortlet portlet, Context context, RunData rundata, SessionState sstate) {
      ToolSession toolSession = SessionManager.getCurrentToolSession();
      List attachments =
            (List) toolSession.getAttribute(
                  FilePickerHelper.FILE_PICKER_ATTACHMENTS);
      toolSession.removeAttribute(FilePickerHelper.FILE_PICKER_CANCEL);

      if (attachments != null) {
         sstate.setAttribute(ResourcesAction.STATE_ATTACHMENTS, EntityManager.newReferenceList(attachments));
      }

      initMessage(toolSession, sstate);

      sstate.setAttribute(ResourcesAction.STATE_RESOURCE_FILTER,
         toolSession.getAttribute(FilePickerHelper.FILE_PICKER_RESOURCE_FILTER));
   }

   protected void initMessage(ToolSession toolSession, SessionState sstate) {
      String message = (String)toolSession.getAttribute(FilePickerHelper.FILE_PICKER_TITLE_TEXT);
      toolSession.removeAttribute(FilePickerHelper.FILE_PICKER_TITLE_TEXT);
      if (message == null) {
         message = (String) rb.get(FilePickerHelper.FILE_PICKER_TITLE_TEXT);
      }
      sstate.setAttribute(ResourcesAction.STATE_ATTACH_TITLE, message);

      message = (String)toolSession.getAttribute(FilePickerHelper.FILE_PICKER_INSTRUCTION_TEXT);
      toolSession.removeAttribute(FilePickerHelper.FILE_PICKER_INSTRUCTION_TEXT);
      if (message == null) {
         message = (String) rb.get(FilePickerHelper.FILE_PICKER_INSTRUCTION_TEXT);
      }
      sstate.setAttribute(ResourcesAction.STATE_ATTACH_INSTRUCTION, message);
   }

}
