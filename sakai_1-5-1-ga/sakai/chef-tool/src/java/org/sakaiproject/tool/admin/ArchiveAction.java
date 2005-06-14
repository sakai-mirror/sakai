/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/admin/ArchiveAction.java,v 1.8 2004/09/30 20:19:37 ggolden.umich.edu Exp $
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

// package
package org.sakaiproject.tool.admin;

// imports
import org.sakaiproject.cheftool.Context;
import org.sakaiproject.cheftool.JetspeedRunData;
import org.sakaiproject.cheftool.RunData;
import org.sakaiproject.cheftool.VelocityPortlet;
import org.sakaiproject.cheftool.VelocityPortletPaneledAction;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.legacy.archive.cover.ArchiveService;
import org.sakaiproject.service.legacy.security.cover.SecurityService;

/**
* <p>ArchiveAction is the CHEF archive tool.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.8 $
*/
public class ArchiveAction
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
		return (String)getContext(rundata).get("template");

	}	// buildMainPanelContext

	/**
	* doArchive called when "eventSubmit_doArchive" is in the request parameters
	* to run the archive.
	*/
	public void doArchive(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		if (!SecurityService.isSuperUser())
		{
			addAlert(state, "archive is limited to administrators.\n");
			return;
		}

		String id = data.getParameters().getString("archive-id");
		if ((id != null) && (id.trim().length() > 0))
		{
			String msg = ArchiveService.archive(id.trim());
			addAlert(state, "archive of site " + id + " complete.\n" + msg);
		}
		else
		{
			addAlert(state, "Please specify a site to archive.");
		}

	}	// doArchive

	/**
	* doImport called when "eventSubmit_doImport" is in the request parameters
	* to run an import.
	*/
	public void doImport(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		if (!SecurityService.isSuperUser())
		{
			addAlert(state, "import is limited to administrators.\n");
			return;
		}

		String id = data.getParameters().getString("import-id");
		String file = data.getParameters().getString("import-file");
		if (	(id != null) && (id.trim().length() > 0)
			&&	(file != null) && (file.trim().length() > 0))
		{
			String msg = ArchiveService.merge(file.trim(), id.trim());
			addAlert(state, "import from file " + file + " to site "
					+ id + " complete.\n" + msg);
		}
		else
		{
			addAlert(state, "Please specify a file name and a site id for import.");
		}

	}	// doImport

}	// ArchiveAction

/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/admin/ArchiveAction.java,v 1.8 2004/09/30 20:19:37 ggolden.umich.edu Exp $
*
**********************************************************************************/
