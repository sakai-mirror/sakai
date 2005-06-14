/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/admin/MemoryAction.java,v 1.8 2004/09/30 20:20:29 ggolden.umich.edu Exp $
*
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

package org.sakaiproject.tool.admin;

import org.sakaiproject.cheftool.Context;
import org.sakaiproject.cheftool.JetspeedRunData;
import org.sakaiproject.cheftool.RunData;
import org.sakaiproject.cheftool.VelocityPortlet;
import org.sakaiproject.cheftool.VelocityPortletPaneledAction;
import org.sakaiproject.cheftool.menu.Menu;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.framework.memory.cover.MemoryService;
import org.sakaiproject.service.framework.session.SessionState;

/**
* <p>MemoryAction is the CHEF memory tool.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.8 $
*/
public class MemoryAction
	extends VelocityPortletPaneledAction
{
	/** 
	* build the context
	*/
	public String buildMainPanelContext(VelocityPortlet portlet, 
										Context context,
										RunData rundata,
										SessionState state)
	{
		// put $action into context for menus, forms and links
		context.put(Menu.CONTEXT_ACTION, state.getAttribute(STATE_ACTION));

		// put the current available memory into the context
		context.put("memory", Long.toString(MemoryService.getAvailableMemory()));

		// status, if there
		if (state.getAttribute("status") != null)
		{
			context.put("status", state.getAttribute("status"));
			state.removeAttribute("status");
		}

		return (String)getContext(rundata).get("template");

	}	// buildMainPanelContext

	/**
	* doNew called when "eventSubmit_doReset" is in the request parameters
	* to reset memory useage (caches)
	*/
	public void doReset(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		try
		{
			MemoryService.resetCachers();
		}
		catch (PermissionException e)
		{
			state.setAttribute("message", "You do not have permission to perform this action.");
		}

	}	// doReset

	/**
	* doNew called when "eventSubmit_doStatus" is in the request parameters
	* to reset memory useage (caches)
	*/
	public void doStatus(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		state.setAttribute("status", MemoryService.getStatus());

	}	// doReset

}	// MemoryAction

/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/admin/MemoryAction.java,v 1.8 2004/09/30 20:20:29 ggolden.umich.edu Exp $
*
**********************************************************************************/
