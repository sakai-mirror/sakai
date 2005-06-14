/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/web/IFrameAction.java,v 1.15.2.1 2005/03/21 18:44:34 lance.indiana.edu Exp $
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
package org.sakaiproject.tool.web;

// imports
import org.sakaiproject.cheftool.Context;
import org.sakaiproject.cheftool.JetspeedRunData;
import org.sakaiproject.cheftool.PortletConfig;
import org.sakaiproject.cheftool.RunData;
import org.sakaiproject.cheftool.VelocityPortlet;
import org.sakaiproject.cheftool.VelocityPortletPaneledAction;
import org.sakaiproject.cheftool.menu.Menu;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.courier.cover.CourierService;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.legacy.resource.Reference;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.ToolConfigurationEdit;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.util.StringUtil;

/**
* <p>IFrameAction is the CHEF tool to place any web content in an IFrame on the page.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.15.2.1 $
*/
public class IFrameAction
	extends VelocityPortletPaneledAction
{
	/** The source URL, in state, config and context. */
	private final static String SOURCE = "source";

	/** The height, in state, config and context. */
	private final static String HEIGHT = "height";

	/** The special attribute, in state, config and context. */
	private final static String SPECIAL = "special";

	/** The title, in state and context. */
	private final static String TITLE = "title";

	/** The flag name and value in state to indicate an update to the portlet is needed. */
	private final static String UPDATE = "update";
	
	/** Whether to pass through the PID to the URL displayed in the IFRAME.  This
	 * enables integration in that the application in the IFRAME will know what
	 * site and tool it is part of.
	 */
	private final static String PASS_PID = "passthroughPID";

	/**
	* Populate the state object, if needed - override to do something!
	*/
	protected void initState(SessionState state, VelocityPortlet portlet, JetspeedRunData rundata)
	{
		super.initState(state, portlet, rundata);

		PortletConfig config = portlet.getPortletConfig();
		
		state.setAttribute(PASS_PID, config.getInitParameter(PASS_PID));

		String special = StringUtil.trimToNull(config.getInitParameter(SPECIAL));

		if (special == null)
		{
			// this was how the ChefWebPagePortlet used to work.  We handle those now.
			if ("true".equals(config.getInitParameter("site")))
			{
				special = "site";
			}
			else if ("true".equals(config.getInitParameter("workspace")))
			{
				special = "workspace";
			}
			else if ("true".equals(config.getInitParameter("worksite")))
			{
				special = "worksite";
			}
		}

		if (special == null) special = "";

		state.setAttribute(SPECIAL, special);

		String url = StringUtil.trimToNull(config.getInitParameter(SOURCE));
		if (url == null)
		{
			// this was how the ChefWebPagePortlet used to work.  We handle those now.
			url = StringUtil.trimToNull(config.getInitParameter("url"));
		}

		// store the raw as-configured source
		state.setAttribute("source-config", url);

		// compute working source
		url = sourceUrl(special, url, state, rundata);
		state.setAttribute(SOURCE, url);

		if (state.getAttribute(HEIGHT) == null)
		{
			state.setAttribute(HEIGHT, config.getInitParameter(HEIGHT));
		}

		if (state.getAttribute(TITLE) == null)
		{
			state.setAttribute(TITLE, portlet.getPortletConfig().getTitle());
		}

	}   // initState

	/** 
	* build the context
	*/
	public String buildMainPanelContext(VelocityPortlet portlet, 
										Context context,
										RunData rundata,
										SessionState state)
	{
		if (MODE_OPTIONS.equals(state.getAttribute(STATE_MODE)))
		{
			return buildOptionsPanelContext(portlet, context, rundata, state);
		}

		// if we have a source url, use it
		String sourceUrl = StringUtil.trimToNull((String) state.getAttribute(SOURCE));
		if (sourceUrl != null)
		{
			context.put(SOURCE,(String) state.getAttribute(SOURCE));
			context.put(HEIGHT, state.getAttribute(HEIGHT));
		}

		// otherwise provide the site's description text
		else
		{		
			//set the url to the site of this request's config'ed url
			String siteId = PortalService.getCurrentSiteId();
			try
			{
				Site s = SiteService.getSite(siteId);
			
				String description = s.getDescription();
				if (description != null)
				{
					context.put("text", description);
				}
			}
			catch (Exception any) {}
		}

		// put $action into context for menus, forms and links
		context.put(Menu.CONTEXT_ACTION, state.getAttribute(STATE_ACTION));

		// add options if allowed
		Menu bar = new Menu(portlet, rundata, "IFrameAction");
		addOptionsMenu(bar, (JetspeedRunData) rundata);
		// if we there's anything in the menu, use it
		if (!bar.getItems().isEmpty())
		{
			context.put(Menu.CONTEXT_MENU, bar);
		}

		return (String)getContext(rundata).get("template");

	}	// buildMainPanelContext

	/**
	*  Setup for options.
	**/
	public String buildOptionsPanelContext( VelocityPortlet portlet,
											Context context,
											RunData data,
											SessionState state) 
	{
		context.put(SOURCE, state.getAttribute("source-config"));
		context.put(HEIGHT, state.getAttribute(HEIGHT));
		context.put(TITLE, state.getAttribute(TITLE));
		context.put(SPECIAL, state.getAttribute(SPECIAL));

		context.put(Menu.CONTEXT_ACTION, "IFrameAction");

		context.put("doUpdate", BUTTON + "doConfigure_update");
		context.put("doCancel", BUTTON + "doCancel");

		// pick the "-customize" template based on the standard template name
		String template = (String)getContext(data).get("template");
		return template + "-customize";

	}	// buildOptionsPanelContext

	/**
	* Handle the configure context's update button
	*/
	public void doConfigure_update(RunData data, Context context)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(peid);

		// special
		String special = data.getParameters().getString(SPECIAL);
		if (!special.equals(state.getAttribute(SPECIAL)))
		{
			// update the source in state
			state.setAttribute(SPECIAL, special);
		}

		// source - only if special's not set
		String source = "";
		if (((String) state.getAttribute(SPECIAL)).length() == 0)
		{
			// mark for update w/o a test against state's current value - state may be showing
			// a special source value (worksite, site, workspace).
			source = data.getParameters().getString(SOURCE);

			// if it's missing the transport, add http://
			source = source.trim();
			if ((source.length() > 0) && (!source.startsWith("/")) && (source.indexOf("://") == -1))
			{
				source = "http://" + source;
			}
		}

		// update configed source
		state.setAttribute("source-config", source);

		// update the working source
		source = sourceUrl(special, source, state, (JetspeedRunData) data);
		state.setAttribute(SOURCE, source);

		// height
		String height = data.getParameters().getString(HEIGHT);
		if (!height.equals(state.getAttribute(HEIGHT)))
		{
			// update the source in state
			state.setAttribute(HEIGHT, height);
		}

		// title
		String title = data.getParameters().getString(TITLE);
		if (!title.equals(state.getAttribute(TITLE)))
		{
			// update the source in state
			state.setAttribute(TITLE, title);
		}

		// update the tool config
		ToolConfigurationEdit tool = (ToolConfigurationEdit) state.getAttribute(STATE_TOOL_EDIT);
		tool.getPropertiesEdit().addProperty(HEIGHT, (String)state.getAttribute(HEIGHT));
		tool.getPropertiesEdit().addProperty(SPECIAL, (String)state.getAttribute(SPECIAL));
		tool.getPropertiesEdit().addProperty(SOURCE, (String)state.getAttribute("source-config"));
		tool.setTitle((String)state.getAttribute(TITLE));

		// commit the change
		saveOptions();

		// we are done with customization... back to the main mode
		state.removeAttribute(STATE_MODE);

		// deliver an update to the title panel (to show the new channel name)
		String deliveryId = PortalService.getCurrentClientWindowId(null);
		String titleId = titlePanelUpdateId(peid);
		CourierService.deliver(deliveryId, titleId);

	}   // doConfigure_update

	/**
	* doCancel called for form input tags type="submit" named="eventSubmit_doCancel"
	* cancel the options process
	*/
	public void doCancel(RunData data, Context context)
	{
		// access the portlet element id to find our state
		String peid = ((JetspeedRunData)data).getJs_peid();
		SessionState state = ((JetspeedRunData)data).getPortletSessionState(peid);

		// cancel the options
		cancelOptions();

		// we are done with customization... back to the main mode
		state.removeAttribute(STATE_MODE);

	}   // doCancel

	/**
	* Return the url unchanged, unless it's a reference, then return the reference url
	*/
	private String convertReferenceUrl(String url)
	{
		// make a reference
		Reference ref = new Reference(url);

		// if it didn't recognize this, return it unchanged
		if (ref.getType() == null) return url;

		// return the reference's url
		return ref.getUrl();

	}	// convertReferenceUrl

	/**
	* Make a source URL based on the special setting, the url already set to "url" or "source", ...
	*/
	private String sourceUrl(String special, String url, SessionState state, JetspeedRunData rundata)
	{		
		// if already set, ignore special
		if ((url == null) || (url.trim().length() == 0))
		{	
			// if marked for "site", use the site intro from the properties
			if ("site".equals(special))
			{
				// set the url to the site config'ed url
				url = ServerConfigurationService.getString("server.info.url","");
			}
			
			// if marked for "workspace", use the "user" site intro from the properties
			else if ("workspace".equals(special))
			{
				// set the url to the site config'ed url
				url = ServerConfigurationService.getString("myworkspace.info.url","");
			}
	
			// if marked for "worksite", use the setting from the site's definition
			else if ("worksite".equals(special))
			{
				// set the url to the site of this request's config'ed url
				String siteId = PortalService.getCurrentSiteId();
				try
				{
					// use the site's info url, if any, else the site description will be used
					Site s = SiteService.getSite(siteId);
					url = StringUtil.trimToZero(s.getInfoUrlFull());
				}
				catch (Exception any) {}
			}
			
			// otherwise, if it's not set, set it to a sample page in public
			else
			{
				url = "/content/public/webcontent_instructions.html"; // %%% probably should configure this -ggolden
			}
		}

		// accept a partial reference url (i.e. "/content/group/chef/test.gif"), convert to full url
		if (url != null)
		{
			url = url.trim();
			url = convertReferenceUrl(url);
		}
		
		// pass the PID through on the URL, IF configured to do so
		if ("true".equals(state.getAttribute(PASS_PID)))
		{
		    if (url.indexOf("?") < 0)
		    {
		        url = url + "?";
		    }
		    else
		    {
		        url = url + "&";
		    }
		    
		    url = url + "pid=" + PortalService.getCurrentToolId();  
		}

		return url;

	}	// sourceUrl

	/**
	* Say if the tool will require the "reset tool" button on the title bar.
	* @return true to have the "reset tool" button, false if not.
	* The default is to have it.
	*/
	protected boolean requireResetToolLink()
	{
		return false;

	}	// requireResetToolLink

}	// IFrameAction

/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/web/IFrameAction.java,v 1.15.2.1 2005/03/21 18:44:34 lance.indiana.edu Exp $
*
**********************************************************************************/
