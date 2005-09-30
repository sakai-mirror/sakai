/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/tools/src/java/org/sakaiproject/tool/admin/ArchiveAction.java,v 1.4 2005/05/12 01:38:25 ggolden.umich.edu Exp $
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
import java.util.Enumeration;
import java.util.Hashtable;

import java.util.ResourceBundle;
import org.sakaiproject.cheftool.Context;
import org.sakaiproject.cheftool.JetspeedRunData;
import org.sakaiproject.cheftool.RunData;
import org.sakaiproject.cheftool.VelocityPortlet;
import org.sakaiproject.cheftool.VelocityPortletPaneledAction;
import org.sakaiproject.cheftool.menu.Menu;
import org.sakaiproject.cheftool.menu.MenuEntry;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.legacy.archive.cover.ArchiveService;
import org.sakaiproject.service.legacy.id.cover.IdService;
import org.sakaiproject.service.legacy.security.cover.SecurityService;
import org.sakaiproject.util.FileItem;
import org.sakaiproject.util.java.StringUtil;

/**
* <p>ArchiveAction is the CHEF archive tool.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public class ArchiveAction
	extends VelocityPortletPaneledAction
{
	private static final String STATE_MODE = "mode";
	private static final String BATCH_MODE = "batch";
	
	/** Resource bundle using current language locale */
    private static ResourceBundle rb = ResourceBundle.getBundle("admin");
	/**
	* build the context
	*/
    public String buildMainPanelContext(VelocityPortlet portlet, 
			Context context,
			RunData rundata,
			SessionState state)
	{
		String template = null;
		
		// check mode and dispatch
		String mode = (String) state.getAttribute(STATE_MODE);
		if (mode == null)
		{
			template = buildListPanelContext(portlet, context, rundata, state);
		}
		else if (mode.equals(BATCH_MODE))
		{
			template = buildBatchPanelContext(portlet, context, rundata, state);
		}
		
		return (String)getContext(rundata).get("template") + template;
		
	}	// buildMainPanelContext
    
    /**
	* build the context for non-batch import/export
	*/
	public String buildListPanelContext(VelocityPortlet portlet, 
										Context context,
										RunData rundata,
										SessionState state)
	{
		context.put("tlang",rb);
		
		// build the menu
		Menu bar = new Menu();
		bar.add( new MenuEntry(rb.getString("archive.button.batch"), "doToggle_State") );
		context.put(Menu.CONTEXT_MENU, bar);
		context.put (Menu.CONTEXT_ACTION, "ArchiveAction");
		
		return "";

	}	// buildListPanelContext

	/**
	* build the context for batch import/export
	*/
	public String buildBatchPanelContext(VelocityPortlet portlet, 
										Context context,
										RunData rundata,
										SessionState state)
	{
		//build the menu
		Menu bar = new Menu();
		bar.add( new MenuEntry(rb.getString("archive.button.nonbatch"), "doToggle_State") );
		context.put(Menu.CONTEXT_MENU, bar);
		context.put (Menu.CONTEXT_ACTION, "ArchiveAction");
		
		return "-batch";

	}	// buildListPanelContext
	
	/**
	 * 
	 */
	public void doToggle_State(RunData data, Context context)
	{
		String peid = ((JetspeedRunData) data).getJs_peid();
		SessionState state = ((JetspeedRunData) data).getPortletSessionState(peid);
		
		if (state.getAttribute(STATE_MODE) == null)
		{
			state.setAttribute(STATE_MODE, BATCH_MODE);
		}
		else
		{
			state.removeAttribute(STATE_MODE);
		}
		
	}	// doToggle_State
	
	/**
	* doArchive called when "eventSubmit_doArchive" is in the request parameters
	* to run the archive.
	*/
	public void doArchive(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		if (!SecurityService.isSuperUser())
		{
			addAlert(state, rb.getString("archive.limited"));
			return;
		}

		String id = data.getParameters().getString("archive-id");
		if ((id != null) && (id.trim().length() > 0))
		{
			String msg = ArchiveService.archive(id.trim());
			addAlert(state, rb.getString("archive.site") + " " + id + " " + rb.getString("archive.complete") + " " + msg);
		}
		else
		{
			addAlert(state, rb.getString("archive.please"));
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
			addAlert(state, rb.getString("archive.import"));
			return;
		}

		String id = data.getParameters().getString("import-id");
		String file = data.getParameters().getString("import-file");
		if (	(id != null) && (id.trim().length() > 0)
			&&	(file != null) && (file.trim().length() > 0))
		{
			String msg = ArchiveService.merge(file.trim(), id.trim(), null);
			addAlert(state, rb.getString("archive.import1")  + " " + file + " " + rb.getString("archive.site") + " "
					+ id + " " + rb.getString("archive.complete") + " " + msg);
		}
		else
		{
			addAlert(state, rb.getString("archive.file"));
		}

	}	// doImport
	
	/**
	* doImport called when "eventSubmit_doImport" is in the request parameters
	* to run an import.
	*/
	public void doBatch_Import(RunData data, Context context)
	{
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(((JetspeedRunData)data).getJs_peid());

		Hashtable fTable = new Hashtable();
		
		if (!SecurityService.isSuperUser())
		{
			addAlert(state, rb.getString("archive.batch.auth"));
			return;
		}
		
		//String fileName = data.getParameters().getString("import-file");
		FileItem fi = data.getParameters().getFileItem ("importFile");
		if (fi == null)
		{
			addAlert(state, rb.getString("archive.batch.missingname"));
		}
		else
		{
			// get content
			String content = fi.getString();
			
			String[] lines = content.split("\n");
			for(int i=0; i<lines.length; i++)
			{
				String lineContent = (String) lines[i];
				String[] lineContents = lineContent.split("\t");
				if (lineContents.length == 2)
				{
					fTable.put(lineContents[0], lineContents[1]);
				}
				else
				{
					addAlert(state, rb.getString("archive.batch.wrongformat"));
				}
			}
		}
		
		if (!fTable.isEmpty())
		{
			Enumeration importFileName = fTable.keys();
			int count = 1;
			while (importFileName.hasMoreElements())
			{
				String path = StringUtil.trimToNull((String) importFileName.nextElement());
				String siteCreatorName = StringUtil.trimToNull((String) fTable.get(path));
				if (path != null && siteCreatorName != null)
				{
					String nSiteId = IdService.getUniqueId();
					
					try
					{
						// merge
						addAlert(state, "\n" + rb.getString("archive.import1") + " " + count + ": " + rb.getString("archive.import2") + " "+ path + " " + rb.getString("archive.site") + " "
								+ nSiteId + " " + rb.getString("archive.importcreatorid") + " " + siteCreatorName + " " + rb.getString("archive.complete") + "\n");
						addAlert(state, ArchiveService.merge(path, nSiteId, siteCreatorName));
						
					}
					catch (Exception ignore)
					{
					}
				}
				
				count++;
			}
		}
	}	// doBatchImport

}	// ArchiveAction

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/tools/src/java/org/sakaiproject/tool/admin/ArchiveAction.java,v 1.4 2005/05/12 01:38:25 ggolden.umich.edu Exp $
*
**********************************************************************************/
