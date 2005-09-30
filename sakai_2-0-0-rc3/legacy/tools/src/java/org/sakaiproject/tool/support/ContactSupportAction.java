/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/tools/src/java/org/sakaiproject/tool/support/ContactSupportAction.java,v 1.2 2005/04/18 14:23:18 ggolden.umich.edu Exp $
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
package org.sakaiproject.tool.support;

// imports
import java.util.Properties;
import java.util.ResourceBundle;

import org.sakaiproject.api.kernel.tool.Placement;
import org.sakaiproject.api.kernel.tool.cover.ToolManager;
import org.sakaiproject.cheftool.Context;
import org.sakaiproject.cheftool.JetspeedRunData;
import org.sakaiproject.cheftool.RunData;
import org.sakaiproject.cheftool.VelocityPortlet;
import org.sakaiproject.cheftool.VelocityPortletPaneledAction;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.email.cover.EmailService;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.legacy.resource.ResourceProperties;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;

/**
 * The ContactSupport tool.
 *
 * @author University of Michigan, CHEF Software Development Team
 * @version $Revision: 1.2 $
 */
public class ContactSupportAction extends VelocityPortletPaneledAction
{
	
	private static ResourceBundle rb = ResourceBundle.getBundle("support");
	
	private final static String STATE_FORM = "form";
	private final static String STATE_CONFIRM = "confirm";
	private final static String STATE_EXCEPTION = "exception";
	private final static String STATE_DOC = "doc";
	private final static String STATE_OPTION = "editingOption";
	
	private final static String ENC_CHARSETS = "UTF-8";
	private final static String GATEWAY_TITLE = "Gateway";
	
	private final static String HELP_POST_URL = "postURL";
	private final static String HELP_GUIDE_URL = "helpURL";
	private final static String HELP_EMAIL = "supportEmail";
	
	/**
	 * Build the context for normal display
	 */
	public String buildMainPanelContext(	VelocityPortlet portlet,
	Context context,
	RunData rundata,
	SessionState state)
	{
		String mode = (String) state.getAttribute(STATE_MODE);
		
		if ((mode == null)||("doc".equals(mode)))
		{
			return buildDocModeContext(portlet, context, rundata, state);
		}
		else if ("form".equals(mode))
		{
			return buildFormModeContext(portlet, context, rundata, state);
		}
		else if ("confirm".equals(mode))
		{
			return buildConfirmModeContext(portlet, context, rundata, state);
		}
		else if ("exception".equals(mode))
		{
			return buildExceptionModeContext(portlet, context, rundata, state);
		}
		/*
		else if (STATE_OPTION.equals(mode))
		{
			return buildOptionModeContext(portlet, context, rundata, state);
		}
		*/
		else
		{
			Log.warn("chef", this + ".buildMainPanelContext: invalid mode: " + mode);
			return null;
		}
		
	} // buildMainPanelContext
	
	/**
	 * build the context for the option editing mode ( in the Main panel)
	 */
	private String buildOptionModeContext(VelocityPortlet portlet, 
	Context context, 
	RunData rundata, 
	SessionState state) 
	{
		return (String)getContext(rundata).get("template") + "_option";
		
	} // buildOptionModeContext

	/**
	 * build the context for the form mode (in the Main panel).
	 */
	private String buildFormModeContext(VelocityPortlet portlet,
	Context context,
	RunData rundata,
	SessionState state)
	{
		context.put("tlang",rb);
		context.put("user", UserDirectoryService.getCurrentUser());
		
		return (String)getContext(rundata).get("template") + "_form";
		
	}   // buildFormModeContext
	
	/**
	 * build the context for the confirm mode (in the Main panel).
	 */
	private String buildConfirmModeContext(VelocityPortlet portlet,
	Context context,
	RunData rundata,
	SessionState state)
	{
		context.put("tlang",rb);
		return (String)getContext(rundata).get("template") + "_confirm";
		
	}   // buildConfirmModeContext
	
	/**
	 * build the context for the doc mode (in the Main panel).
	 */
	private String buildDocModeContext(VelocityPortlet portlet,
	Context context,
	RunData rundata,
	SessionState state)
	{
		/*
		if (SecurityService.isSuperUser())
		{
		Menu bar = new Menu(portlet, rundata, "ContactSupportAction");
		bar.add(new MenuEntry("Options",null, true,	MenuItem.CHECKED_NA,"doOptions"));
		context.put(Menu.CONTEXT_MENU, bar);
		context.put(Menu.CONTEXT_ACTION, "ContactSupportAction");
		}
		
		String toolId = PortalService.getCurrentToolId();
		String siteId = PortalService.getCurrentSiteId();

		try
		{
			// get a lock on the site
			Site site = SiteService.getSite(siteId);

			// get this tool's configuration
			ToolConfiguration tool = site.getTool(toolId);
				
			String url = tool.getProperties().getProperty(HELP_GUIDE_URL);
			if (url != null) url = url.trim();
			if ((url != null) && (url.length() > 0))
			{
				// if it's missing the transport, add http://
				if (url.indexOf("://") == -1) url = "http://" + url;
				context.put("newHelpGuideUrl", url);
			}
		}
		catch (IdUnusedException ignore)
		{
		}
		*/
		context.put("tlang",rb);
		String helpEmail = ServerConfigurationService.getString("support.email",null);
		if (helpEmail != null)
		{
			// if support email has been specified in configuration file, show the Contact toolbar
			context.put("showContact", Boolean.TRUE);
		}
		else
		{
			context.put("showContact", Boolean.FALSE);
		}
		
		return (String)getContext(rundata).get("template") + "_doc";
		
	}   // buildDocModeContext
	
	/**
	 * build the context for the exception mode (in the Main panel).
	 */
	private String buildExceptionModeContext(VelocityPortlet portlet,
	Context context,
	RunData rundata,
	SessionState state)
	{
		context.put("tlang",rb);
		return (String)getContext(rundata).get("template") + "_exception";
		
	}   // buildExceptionModeContext
	
	/**
	 * Handle a send comments/questions request.
	 **/
	public void doSend(RunData runData, Context context)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState state = ((JetspeedRunData)runData).getPortletSessionState(peid);
		
		String userName = "";
		userName = runData.getParameters().getString("username");
		String displayName = "";
		displayName = runData.getParameters().getString("displayName");
		String comments = "";
		comments = runData.getParameters().getString("comments");
		String email = "";
		email = runData.getParameters().getString("email");
		
		/*		
		String helpEmail = new String(); 
		
		String toolId = PortalService.getCurrentToolId();
		String siteId = PortalService.getCurrentSiteId();

		try
		{
			Site site = SiteService.getSite(siteId);
			ToolConfiguration tool = site.getTool(toolId);
		
			helpEmail = tool.getProperties().getProperty(HELP_EMAIL);
	
			if (helpEmail != null) helpEmail.trim();
		}
		catch (IdUnusedException ignore)
		{
		}
		*/
		
		String content = "";
		
		String UNAVAILABLE = "~unavailable~";
		
		String IP = UNAVAILABLE;
		String agent = UNAVAILABLE;
		String sessionId = UNAVAILABLE;
		String worksiteURL = UNAVAILABLE;
		String worksiteTitle = UNAVAILABLE;
		String worksiteCreatorUsername = UNAVAILABLE;
		String worksiteCreatorEmail = UNAVAILABLE;
		
		// collect all the data
		try
		{
			if (UsageSessionService.getSession() != null)
			{
				IP = UsageSessionService.getSession().getIpAddress();
				//agent = UsageSessionService.getCurrentSession().getUserAgent();
				//sessionId = UsageSessionService.getCurrentSession().getId();
			}
			else if (runData.getRequest() != null)
			{
				IP = runData.getRequest().getRemoteAddr();
				//agent = runData.getRequest().getHeader("user-agent");
				//sessionId = UNAVAILABLE;
			}// if-else if
			
			Site worksite = SiteService.getSite(PortalService.getCurrentSiteId());
			if (worksite != null)
			{					
				worksiteURL = worksite.getUrl();
				worksiteTitle = worksite.getTitle();
				
				try
				{						
					String creatorId = worksite.getProperties().getProperty(ResourceProperties.PROP_CREATOR);
					if (creatorId != null)
					{
						User creator = UserDirectoryService.getUser(creatorId);
						
						worksiteCreatorUsername = creatorId;
						worksiteCreatorEmail = creator.getEmail();
					}
				}
				catch (IdUnusedException error)
				{
					Log.debug("chef", "id ununused exception" + error);
				} // try-catch
			} // if (worksite != null)
		}
		catch (IdUnusedException e)
		{
			Log.debug("chef", "id ununused exception" + e);
		} // try-catch : collect data
		
			
		content = "\n\n"
		+ rb.getString ("sennam") + " " + displayName + "\n"
		+ rb.getString ("senuse") + " " + userName + "\n\n"
		+ rb.getString("comque") + " \n"
		+ comments + "\n\n"
		+ rb.getString("senrep") + " " + email + "\n\n"
		+ rb.getString("sitrep")+  " " + worksiteTitle + "\n"
		+ rb.getString("sitid") + " " + PortalService.getCurrentSiteId() + "\n"
		+ rb.getString("siturl") + " " + worksiteURL + "\n\n";
		
		String helpEmail = ServerConfigurationService.getString("support.email",null);
		
		if (helpEmail == null)
		{
			addAlert(state, rb.getString("supema"));
		}
		else
		{
			EmailService.send(email, helpEmail, rb.getString("comque2"), content, helpEmail, email, null);
			// set state to be "confirm"
			state.setAttribute(STATE_MODE, STATE_CONFIRM);
		}
		
	}	// doSend
	
	/**
	 * Handle a new comments/questions request.
	 **/
	public void doNew(RunData runData, Context context)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState state = ((JetspeedRunData)runData).getPortletSessionState(peid);
		
		// set state to be "confirm"
		state.setAttribute(STATE_MODE, STATE_FORM);
		
	}	// doNew
	
	/**
	 * Handle a "going back to help documents" request.
	 **/
	public void doDoc(RunData runData, Context context)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState state = ((JetspeedRunData)runData).getPortletSessionState(peid);
		
		// set state to be "confirm"
		state.setAttribute(STATE_MODE, STATE_DOC);
		
	}	// doDoc
	
	/**
	 * Populate the state object, if needed.
	 */
	protected void initState(SessionState state, VelocityPortlet portlet, JetspeedRunData rundata)
	{
		super.initState(state, portlet, rundata);
		
		/*
		if (state.getAttribute(.SUPPORT_EMAIL) == null)
		{
		String postURL = ServerConfigurationService.getString("support.url","");
		String helpGuideURL = ServerConfigurationService.getString("help.url", "");
		
		state.setAttribute(SUPPORT_POST_URL, postURL);
		state.setAttribute(HELP_GUIDE_URL, helpGuideURL);
		*/
	}   // initState
	
	
	/**
	 * Handle an "editing option" request
	 */
	public void doOptions(RunData runData, Context context)
	{
		super.doOptions(runData, context);
		
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState state = ((JetspeedRunData)runData).getPortletSessionState(peid);
	
		// set state to be "editingOption"
		state.setAttribute(STATE_MODE, STATE_OPTION);
			
	} // doOption
	
	
	/**
	 * Handle an "Save" request in editing-option page
	 */
	public void doSave(RunData runData, Context context)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState state = ((JetspeedRunData)runData).getPortletSessionState(peid);
			
		String newHelpGuideUrl = runData.getParameters().getString("newHelpGuideUrl");
		String newHelpEmail = runData.getParameters().getString("newHelpEmail");
			
		// update the tool config
		Placement placement = ToolManager.getCurrentPlacement();
		Properties propEdit = placement.getPlacementConfig();
		
		if ((newHelpGuideUrl != null) && (!newHelpGuideUrl.equals("")))
		{
			propEdit.setProperty(HELP_GUIDE_URL, newHelpGuideUrl);
				
		}
			
		if ((newHelpEmail != null) && (!newHelpEmail.equals("")))
		{
			propEdit.setProperty(HELP_EMAIL, newHelpEmail);
		}
		
		
		// set state to be "doc"
		state.setAttribute(STATE_MODE, STATE_DOC);
			 
		super.saveOptions();
	
	} // doSave
	
	
	/**
	 * Handle a "Cancel" request in editing-option page
	 */
	public void doCancel(RunData runData, Context context)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)runData).getJs_peid();
		SessionState state = ((JetspeedRunData)runData).getPortletSessionState(peid);
			
		// set state to be "confirm"
		state.setAttribute(STATE_MODE, STATE_DOC);
		
	 } // doCancel
	
	
}   // ContactSupportAction

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/tools/src/java/org/sakaiproject/tool/support/ContactSupportAction.java,v 1.2 2005/04/18 14:23:18 ggolden.umich.edu Exp $
*
**********************************************************************************/
