/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/cheftool/VelocityPortletPaneledAction.java,v 1.43 2005/02/22 19:53:27 ggolden.umich.edu Exp $
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

package org.sakaiproject.cheftool;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.cheftool.menu.Menu;
import org.sakaiproject.cheftool.menu.MenuEntry;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.framework.config.ToolRegistration;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.courier.cover.CourierService;
import org.sakaiproject.service.framework.current.cover.CurrentService;
import org.sakaiproject.service.framework.log.cover.Logger;
import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.framework.session.SessionState;
import org.sakaiproject.service.legacy.site.SiteEdit;
import org.sakaiproject.service.legacy.site.ToolConfiguration;
import org.sakaiproject.service.legacy.site.ToolConfigurationEdit;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.util.ParameterParser;
import org.sakaiproject.util.StringUtil;
import org.sakaiproject.util.Validator;
import org.sakaiproject.util.observer.ObservingCourier;
import org.sakaiproject.vm.ActionURL;

/**
* <p>VelocityPortletPaneledAction ... </p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.43 $
*/
public abstract class VelocityPortletPaneledAction extends ToolServlet
{
	protected static final String BUTTON = "eventSubmit_";

	/** The currently active helper mode static class. */
	public static final String STATE_HELPER = "vppa.helper";

	protected static final String STATE_MODE = "mode";
	protected static final String STATE_OBSERVER = "obsever";
	protected static final String STATE_ACTION = "action";

	/** The panel name of the main panel - append the tool's id. */
	protected static final String LAYOUT_MAIN = "Main";

	/** The panel name of the layout panel. */
	protected static final String LAYOUT_PANEL = "Layout";

	/** The panel name of the main panel. */
	protected static final String LAYOUT_TITLE = "Title";

	protected class MyLogger
	{
		public void warn(String channel, String msg)
		{
			Logger.warn(msg);
		}

		public void warn(String channel, String msg, Throwable e)
		{
			Logger.warn(msg, e);
		}

		public void debug(String channel, String msg)
		{
			Logger.debug(msg);
		}

		public void debug(String channel, String msg, Throwable e)
		{
			Logger.debug(msg, e);
		}

		public void info(String channel, String msg)
		{
			Logger.info(msg);
		}

		public void info(String channel, String msg, Throwable e)
		{
			Logger.info(msg, e);
		}

		public void error(String channel, String msg)
		{
			Logger.error(msg);
		}

		public void error(String channel, String msg, Throwable e)
		{
			Logger.error(msg, e);
		}

		// to support: if (Log.getLogger("chef").isDebugEnabled())
		public MyLogger getLogger(String name)
		{
			return this;
		}

		public boolean isDebugEnabled()
		{
			return Logger.isDebugEnabled();
		}

		public boolean isWarnEnabled()
		{
			return Logger.isWarnEnabled();
		}

		public boolean isInfoEnabled()
		{
			return Logger.isInfoEnabled();
		}
	}

	protected MyLogger Log = new MyLogger();

	protected void initState(SessionState state, VelocityPortlet portlet, JetspeedRunData rundata)
	{
	}

	/**
	* Compute the deliver address for the current request. 
	* Compute the client window id, based on the float state
	* @param state The tool state.
	* @param toolId The tool instance id, which might be used as part of the client window id if floating.
	* @return The client window id, based on the float state.
	*/
	protected String clientWindowId(SessionState state, String toolId)
	{
		String deliveryId =
//			PortalService.getCurrentClientWindowId((state.getAttribute(STATE_FLOAT) == null) ? null : toolId);
			PortalService.getCurrentClientWindowId();

		return deliveryId;

	} // clientWindowId

	/**
	* Compute the courier update html element id for the main panel - add "." and other names for inner panels.
	* @param toolId The tool (portlet) id.
	* @return The courier update html element id for the main panel.
	*/
	public static String mainPanelUpdateId(String toolId)
	{
		return Validator.escapeJavascript(PortalService.getToolDocElementId(toolId));

	} // mainPanelUpdateId

	/**
	* Compute the courier update html element id for the title panel.
	* @param toolId The tool (portlet) id.
	* @return The courier update html element id for the title panel.
	*/
	public static String titlePanelUpdateId(String toolId)
	{
		return Validator.escapeJavascript(PortalService.getToolTitleElementId(toolId));

	} // titlePanelUpdateId

	// Note: this is the "new" way - but the old code needs the "old" way
	//	/**
	//	* Add another string to the alert message.
	//	* @param state The session state.
	//	* @param message The string to add.
	//	*/
	//	protected void addAlert(SessionState state, String message)
	//	{
	//		getAlert(state).add(message);
	//	}

	/**
	* Add another string to the alert message.
	* @param state The session state.
	* @param message The string to add.
	*/
	public static void addAlert(SessionState state, String message)
	{
		String soFar = (String) state.getAttribute(STATE_MESSAGE);
		if (soFar != null)
		{
			soFar = soFar + " " + message;
		}
		else
		{
			soFar = message;
		}
		state.setAttribute(STATE_MESSAGE, soFar);

	} // addAlert

	/**
	 * Initialize for the first time the session state for this session.
	 * If overridden in a sub-class, make sure to call super.
	 * @param state The session state.
	 * @param req The current portlet request.
	 * @param res The current portlet response.
	 */
	protected void initState(SessionState state, HttpServletRequest req, HttpServletResponse res)
	{
		super.initState(state, req, res);

		// call the old initState:
		PortletConfig config = (PortletConfig) req.getAttribute(ATTR_CONFIG);
		VelocityPortlet portlet = (VelocityPortlet) req.getAttribute(ATTR_PORTLET);
		JetspeedRunData rundata = (JetspeedRunData) req.getAttribute(ATTR_RUNDATA);

		initState(state, portlet, rundata);

	} // initState

	/**
	 * Update for this request processing the session state.
	 * If overridden in a sub-class, make sure to call super.
	 * @param state The session state.
	 * @param req The current portlet request.
	 * @param res The current portlet response.
	 */
	protected void updateState(SessionState state, HttpServletRequest req, HttpServletResponse res)
	{
		super.updateState(state, req, res);

		// the old way has just initState, so...
		PortletConfig config = (PortletConfig) req.getAttribute(ATTR_CONFIG);
		VelocityPortlet portlet = (VelocityPortlet) req.getAttribute(ATTR_PORTLET);
		JetspeedRunData rundata = (JetspeedRunData) req.getAttribute(ATTR_RUNDATA);

		initState(state, portlet, rundata);

	} // updateState

	/**
	 * Dispatch to a "do" method based on reflection.
	 * Override ToolServlet to support the old "build" ways.
	 * @param methodBase The base name of the method to call.
	 * @param methodExt The end name of the method to call.
	 * @param req The HttpServletRequest.
	 * @param res The HttpServletResponse
	 */
	protected void toolModeDispatch(String methodBase, String methodExt, HttpServletRequest req, HttpServletResponse res)
	{
		// the context wraps our real vm attribute set
		Context context = (Context) req.getAttribute(ATTR_CONTEXT);

		// other wrappers
		PortletConfig config = (PortletConfig) req.getAttribute(ATTR_CONFIG);
		VelocityPortlet portlet = (VelocityPortlet) req.getAttribute(ATTR_PORTLET);
		JetspeedRunData rundata = (JetspeedRunData) req.getAttribute(ATTR_RUNDATA);

		// "panel" is used to identify the specific panel in the URL
		context.put("param_panel", ActionURL.PARAM_PANEL);

		// set the "action"
		context.put("action", getState(req).getAttribute(STATE_ACTION));

		// set the "pid"
		context.put("param_pid", ActionURL.PARAM_PID);
		context.put("pid", getPid(req));

		try
		{
			// dispatch panels (Note: panel must be in the URL, not the body - this is not from the parsed params)
			String panel = ((ParameterParser) req.getAttribute(ATTR_PARAMS)).getString(ActionURL.PARAM_PANEL);

			/* TODO: float support from before...
						// special case for floating and the Main panel:
						if (LAYOUT_MAIN.equals(panel))
						{
							if (handleFloat(portlet, context, rundata, state))
								return;
						}
			*/
			// panel parameter is missing when the main portlet is called upon - use layout
			if (panel == null)
			{
				panel = LAYOUT_PANEL;
			}

			context.put("panel", panel);

			// form a method name "build" + panel name (first letter caps) + "PanelContext"
			// buildPanelContext( VelocityPortlet, Context, ControllerState, RunData )
			Class[] types = new Class[4];
			types[0] = VelocityPortlet.class;
			types[1] = Context.class;
			types[2] = RunData.class;
			types[3] = SessionState.class;
			String methodName = "build" + panel + "PanelContext";
			Method method = getClass().getMethod(methodName, types);

			Object[] args = new Object[4];
			args[0] = portlet;
			args[1] = context;
			args[2] = rundata;
			args[3] = getState(req);
			String template = (String) method.invoke(this, args);

			// if the method did something like a redirect, we don't want to try to put out any more
			if (!res.isCommitted())
			{
				if (template == null)
				{
					// pick the template for the panel - the base + "-" + panel
					template = (String) getContext(rundata).get("template") + "-" + panel;
				}

				// the vm file needs a path and an extension
				template = "/vm/" + template + ".vm";

				// setup for old style alert
				StringBuffer buf = new StringBuffer();
				String msg = (String) getState(req).getAttribute(STATE_MESSAGE);
				if (msg != null)
				{
					buf.append(msg);
					getState(req).removeAttribute(STATE_MESSAGE);
				}
				Alert alert = getAlert(req);
				if (!alert.isEmpty())
				{
					buf.append(alert.peekAlert());
					setVmReference(ALERT_ATTR, alert, req);
				}
				if (buf.length() > 0)
				{
					setVmReference("alertMessage", buf.toString(), req);
				}

				// setup for old style validator
				setVmReference("validator", m_validator, req);

				// set standard no-cache headers
				setNoCacheHeaders(res);

				// add a standard header
				includeVm("/vm/chef_header.vm", req, res);

				includeVm(template, req, res);

				// add a standard footer
				includeVm("/vm/chef_footer.vm", req, res);
			}
		}
		catch (NoSuchMethodException e)
		{
			Log.warn("chef", this +" : ", e);
		}
		catch (IllegalAccessException e)
		{
			Log.warn("chef", this +" : ", e);
		}
		catch (InvocationTargetException e)
		{
			Throwable e2 = e;
			if (e.getCause() != null) e2 = e.getCause();
			Log.warn("chef", this +" : ", e2);
		}
		catch (ServletException e)
		{
			Log.warn("chef", this +" : ", e);
		}

	} // toolModeDispatch

	/**
	 * Process a Portlet action.
	 */
	public void processAction(HttpServletRequest req, HttpServletResponse res)
	{
		// lets use the parsed params
		JetspeedRunData rundata = (JetspeedRunData) req.getAttribute(ATTR_RUNDATA);
		ParameterParser params = rundata.getParameters();

		// see if there's an action parameter, whose value has the action to use
		String action = params.get(PARAM_ACTION);

		// if that's not present, see if there's a combination name with the action encoded in the name
		if (action == null)
		{
			Iterator names = params.getNames();
			while (names.hasNext())
			{
				String name = (String) names.next();
				if (name.startsWith(BUTTON))
				{
					action = name.substring(BUTTON.length());
					break;
				}
			}
		}

		// process the action if present
		if (action != null)
		{
			// if we have an active helper, send the action there
			String helperClass = (String) getState(req).getAttribute(STATE_HELPER);
			if (helperClass != null)
			{
				helperActionDispatch("", action, req, res, helperClass);
			}
			else
			{
				actionDispatch("", action, req, res);
			}

			// redirect to the tool's registration's url, with the tool id (pid) and panel
			ToolConfiguration tool = (ToolConfiguration) req.getAttribute(ATTR_TOOL);
			ToolRegistration reg = (ToolRegistration) req.getAttribute(ATTR_REG);
			String redirect =
				reg.getUrl()
					+ "?"
					+ ActionURL.PARAM_PID
					+ "="
					+ tool.getId()
					+ "&"
					+ ActionURL.PARAM_PANEL
					+ "="
					+ ((ParameterParser) req.getAttribute(ATTR_PARAMS)).getString(ActionURL.PARAM_PANEL);
//			Logger.info(this + " ** redirect to: " + redirect);

			try
			{
				res.sendRedirect(redirect);
			}
			catch (IOException e)
			{
			}
		}
		else
		{
			Logger.debug(this +".processAction: no action");
		}

	} // processAction

	/**
	 * Dispatch to a "processAction" method based on reflection.
	 * @param methodBase The base name of the method to call.
	 * @param methodExt The end name of the method to call.
	 * @param req The HttpServletRequest.
	 * @param res The HttpServletResponse
	 * @throws PortletExcption, IOException, just like the "do" methods.
	 */
	protected void actionDispatch(String methodBase, String methodExt, HttpServletRequest req, HttpServletResponse res)
	{
		String methodName = null;
		try
		{
			// the method signature
			Class[] signature = new Class[2];
			signature[0] = RunData.class;
			signature[1] = Context.class;

			// the method name
			methodName = methodBase + methodExt;

			// find a method of this class with this name and signature
			Method method = getClass().getMethod(methodName, signature);

			// parameters - the context for a "do" should not be used, so we will send in null
			Object[] args = new Object[2];
			args[0] = (JetspeedRunData) req.getAttribute(ATTR_RUNDATA);
			args[1] = null;

			// make the call
			method.invoke(this, args);
		}
		catch (NoSuchMethodException e)
		{
			// try for a single parameter call
			try
			{
				// the method signature
				Class[] signature = new Class[1];
				signature[0] = RunData.class;

				// the method name
				methodName = methodBase + methodExt;

				// find a method of this class with this name and signature
				Method method = getClass().getMethod(methodName, signature);

				// parameters - the context for a "do" should not be used, so we will send in null
				Object[] args = new Object[1];
				args[0] = (JetspeedRunData) req.getAttribute(ATTR_RUNDATA);

				// make the call
				method.invoke(this, args);
			}
			catch (NoSuchMethodException e2)
			{
				getServletContext().log("Exception calling method " + methodName + " " + e2);
			}
			catch (IllegalAccessException e2)
			{
				getServletContext().log("Exception calling method " + methodName + " " + e2);
			}
			catch (InvocationTargetException e2)
			{
				String xtra = "";
				if (e2.getCause() != null) xtra = " (Caused by " + e2.getCause() + ")";
				getServletContext().log("Exception calling method " + methodName + " " + e2 + xtra);
				
			}
		}
		catch (IllegalAccessException e)
		{
			getServletContext().log("Exception calling method " + methodName + " " + e);
		}
		catch (InvocationTargetException e)
		{
			String xtra = "";
			if (e.getCause() != null) xtra = " (Caused by " + e.getCause() + ")";
			getServletContext().log("Exception calling method " + methodName + " " + e + xtra);
		}

	} // actionDispatch

	/**
	 * Dispatch to a "processAction" method based on reflection in a helper class.
	 * @param methodBase The base name of the method to call.
	 * @param methodExt The end name of the method to call.
	 * @param req The HttpServletRequest.
	 * @param res The HttpServletResponse
	 * @throws PortletExcption, IOException, just like the "do" methods.
	 */
	protected void helperActionDispatch(
		String methodBase,
		String methodExt,
		HttpServletRequest req,
		HttpServletResponse res,
		String className)
	{
		String methodName = null;
		try
		{
			// the method signature
			Class[] signature = new Class[1];
			signature[0] = RunData.class;

			// the method name
			methodName = methodBase + methodExt;

			Class cls = Class.forName(className);

			// find a method of this class with this name and signature
			Method method = cls.getMethod(methodName, signature);

			// parameters - the context for a "do" should not be used, so we will send in null
			Object[] args = new Object[1];
			args[0] = (JetspeedRunData) req.getAttribute(ATTR_RUNDATA);

			// make the call
			method.invoke(this, args);
		}
		catch (ClassNotFoundException e)
		{
			getServletContext().log("Exception helper class not found " + e);
		}
		catch (NoSuchMethodException e)
		{
			getServletContext().log("Exception calling method " + methodName + " " + e);
		}
		catch (IllegalAccessException e)
		{
			getServletContext().log("Exception calling method " + methodName + " " + e);
		}
		catch (InvocationTargetException e)
		{
			String xtra = "";
			if (e.getCause() != null) xtra = " (Caused by " + e.getCause() + ")";
			getServletContext().log("Exception calling method " + methodName + " " + e + xtra);
		}

	} // helperActionDispatch

	/**
	 * This is used to get "template" from the map, the default template registered for the tool
	 * in chef_tools.xreg.
	 */
	protected Map getContext(RunData data)
	{
		// get template from the servlet config
		String template = getServletConfig().getInitParameter("template");
		Map rv = new HashMap();
		rv.put("template", template);

		return rv;
	}

	// OPTIONS SUPPORT
	public static final String STATE_OBSERVER2 = "obsever2";
	public static final String STATE_PRESENCE_OBSERVER = "presence_observer";
	public static final String STATE_FLOAT = "float";
	public static final String STATE_SITE_EDIT = "site";
	public static final String STATE_TOOL_EDIT = "tool";
	public static final String STATE_MESSAGE = "message";

	/** Standard modes. */
	public static final String MODE_OPTIONS = "options";

	/** 
	* Handle a request to set options.
	*/
	public void doOptions(RunData runData, Context context)
	{
		String toolId = PortalService.getCurrentToolId();
		String siteId = PortalService.getCurrentSiteId();
		SessionState state = ((JetspeedRunData) runData).getPortletSessionState(toolId);

		String msg = null;

		try
		{
			// get a lock on the site
			SiteEdit site = SiteService.editSite(siteId);

			// get this tool's configuration
			ToolConfigurationEdit tool = site.getToolEdit(toolId);

			// put in state
			state.setAttribute(STATE_SITE_EDIT, site);
			state.setAttribute(STATE_TOOL_EDIT, tool);

			// go into options mode
			state.setAttribute(STATE_MODE, MODE_OPTIONS);

			// disable auto-updates while editing
			disableObservers(state);

			// if we're not in the main panel for this tool, schedule an update of the main panel
			String currentPanelId = runData.getParameters().getString(ActionURL.PARAM_PANEL);
			if (!LAYOUT_MAIN.equals(currentPanelId))
			{
				String address = clientWindowId(state, toolId);
				String mainPanelId = mainPanelUpdateId(toolId);
				CourierService.deliver(address, mainPanelId);
			}
			return;
		}
		catch (IdUnusedException ignore)
		{
			msg = "due to a system error.";
		}
		catch (PermissionException e)
		{
			msg = "you do not have permission to set options for this Worksite.";
		}
		catch (InUseException e)
		{
			msg =
				"you are already configuring this Worksite from another tool, or someone else is configuring this Worksite.";
		}

		// we didn't get a lock on the site, so tell the user and don't change mode
		state.setAttribute(STATE_MESSAGE, "Options are unavailable right now: " + msg);

	} // doOptions

	/**
	* Complete the options process with a save.
	*/
	protected void saveOptions()
	{
		SessionState state = getState((HttpServletRequest) CurrentService.getInThread(HttpServletRequest.class.getName()));
		SiteEdit site = (SiteEdit) state.getAttribute(STATE_SITE_EDIT);
		if (site != null)
		{
			SiteService.commitEdit(site);
		}

		state.removeAttribute(STATE_SITE_EDIT);
		state.removeAttribute(STATE_TOOL_EDIT);

	} // saveOptions

	/**
	* Cancel the options process.
	*/
	protected void cancelOptions()
	{
		SessionState state = getState((HttpServletRequest) CurrentService.getInThread(HttpServletRequest.class.getName()));
		SiteEdit site = (SiteEdit) state.getAttribute(STATE_SITE_EDIT);
		if (site != null)
		{
			SiteService.cancelEdit(site);
		}

		state.removeAttribute(STATE_SITE_EDIT);
		state.removeAttribute(STATE_TOOL_EDIT);

	} // cancelOptions

	/**
	* Add the options to the menu bar, if allowed.
	* @param bar The menu bar to add to,
	* @param ref The resource reference to base the security decision upon.
	*/
	protected void addOptionsMenu(Menu bar, JetspeedRunData data) // %%% don't need data -ggolden
	{
		if (SiteService.allowUpdateSite(PortalService.getCurrentSiteId()))
		{
			bar.add(new MenuEntry("Options...", "doOptions"));
		}

	} // addOptionsMenu

	/**
	* Disable any observers registered in state in STATE_OBSERVER or STATE_OBSERVER2
	* @param state The session state.
	*/
	public static void disableObservers(SessionState state)
	{
		ObservingCourier observer = (ObservingCourier) state.getAttribute(STATE_OBSERVER);
		if (observer != null)
		{
			observer.disable();
		}

		observer = (ObservingCourier) state.getAttribute(STATE_OBSERVER2);
		if (observer != null)
		{
			observer.disable();
		}

	} // disableObservers

	/**
	* Enable any observers registered in state in STATE_OBSERVER or STATE_OBSERVER2
	* @param state The session state.
	*/
	public static void enableObservers(SessionState state)
	{
		ObservingCourier observer = (ObservingCourier) state.getAttribute(STATE_OBSERVER);
		if (observer != null)
		{
			observer.enable();
		}

		observer = (ObservingCourier) state.getAttribute(STATE_OBSERVER2);
		if (observer != null)
		{
			observer.enable();
		}

	} // enableObservers

	/**
	* Handle the "reset tool" option from the Title bar.
	*/
	public void doReset(RunData runData, Context context)
	{
		// access the portlet element id to find "our" state (i.e. the state for this portlet)
		String peid = ((JetspeedRunData) runData).getJs_peid();
		SessionState state = ((JetspeedRunData) runData).getPortletSessionState(peid);

		// preserve floating, if we are
//		boolean floating = state.getAttribute(STATE_FLOAT) != null;

		// clear this state
		resetTool(state);

		String windowToolId = null;

//		// restore the floating
//		if (floating)
//		{
//			state.setAttribute(STATE_FLOAT, "float");
//
//			// we need to tell the window with this tool id (i.e. the floating one) what's going on
//			windowToolId = peid;
//		}

		// make sure the Main panel is updated
		String main = VelocityPortletPaneledAction.mainPanelUpdateId(peid);
		CourierService.deliver(PortalService.getCurrentClientWindowId(windowToolId), main);

	}	// doReset

	/**
	* Reset the tool (state) to "home" conditions.
	* Default here is to clear everything from state.
	* @param state The tool's session state.
	*/
	protected void resetTool(SessionState state)
	{
		state.clear();

	}	// resetTool

	/**
	* Say if the tool will require the "reset tool" button on the title bar.
	* @return true to have the "reset tool" button, false if not.
	* The default is to have it.
	*/
	protected boolean requireResetToolLink()
	{
		return true;

	}	// requireResetToolLink

	/**
	 * Add some standard references to the vm context.
	 * @param request The render request.
	 * @param response The render response.
	 */
	protected void setVmStdRef(HttpServletRequest request, HttpServletResponse response)
	{
		// set these first so they block any super's settings

		// pick the site's skin or skin.default, with the skin.root
		String skinRoot = ServerConfigurationService.getString("skin.root", "/sakai-portal/css/");
		String skin = ServerConfigurationService.getString("skin.default", "sakai_core.css");
		ToolConfiguration tool = (ToolConfiguration) request.getAttribute(ATTR_TOOL);
		if (tool != null)
		{
			String siteSkin = StringUtil.trimToNull(tool.getSkin());
			if (siteSkin != null)
			{
				skin = siteSkin;
			}
		}

		int pos = skin.indexOf(".css");

		if (pos != skin.length()-4)
		{
			skin += ".css";
		}

		setVmReference("sakai_skin", skinRoot + skin, request);

		// pick up all the super ones
		super.setVmStdRef(request, response);

		// add a "$config" which accesses the config service
		setVmReference("config", ServerConfigurationService.getInstance(), request);

		// add the pid
		setVmReference("pid", getPid(request), request);
	}

	/** A Context bound into the request attributes. */
	protected final static String ATTR_CONTEXT = "sakai.wrapper.context";

	/** A PortletConfig bound into the request attributes. */
	protected final static String ATTR_CONFIG = "sakai.wrapper.config";

	/** A VelocityPortlet bound into the request attributes. */
	protected final static String ATTR_PORTLET = "sakai.wrapper.portlet";

	/** A JetspeedRunData bound into the request attributes. */
	protected final static String ATTR_RUNDATA = "sakai.wrapper.rundata";

	/** This tool's ToolConfiguration (in page in site). */
	protected final static String ATTR_TOOL = "sakai.wrapper.tool";

	/** This tool's ToolRegistration. */
	protected final static String ATTR_REG = "sakai.wrapper.reg";

	/**
	 * Respond to a request by dispatching to a portlet like "do" method based on the portlet mode and tool mode
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
		throws ServletException
	{
		// set in VmServlet
		ParameterParser params = (ParameterParser)req.getAttribute(ATTR_PARAMS);

		// we will need some covers... Note: parameters are parsed (i.e. files are read) here
		Context context = new Context(this, req);
		ToolConfiguration tool = SiteService.findTool(getPid(req));
		ToolRegistration reg = ServerConfigurationService.getToolRegistration(tool.getToolId());
		PortletConfig config = new PortletConfig(getServletConfig(), tool, reg);
		VelocityPortlet portlet = new VelocityPortlet(getPid(req), config);
		JetspeedRunData rundata = new JetspeedRunData(req, getState(req), getPid(req), params);

		req.setAttribute(ATTR_CONTEXT, context);
		req.setAttribute(ATTR_CONFIG, config);
		req.setAttribute(ATTR_PORTLET, portlet);
		req.setAttribute(ATTR_RUNDATA, rundata);
		req.setAttribute(ATTR_TOOL, tool);
		req.setAttribute(ATTR_REG, reg);

		super.doGet(req, res);
	}

	/** 
	 * Build the context for the Layout panel.  The layout is aggregated into the main portal pane page.
	 * This builds the standard layout.  Override to have a custom layout.
	 * @return (optional) template name for this panel
	 */
	public String buildLayoutPanelContext(VelocityPortlet portlet, Context context, RunData rundata, SessionState state)
	{
		context.put("title", portlet.getPortletConfig().getTitle());
		context.put("pid", portlet.getID());

		return "chef_Layout";
	}

	/** 
	 * Build the context for the Title panel.
	 * This builds the standard layout.  Override to have a custom layout.
	 * @return (optional) template name for this panel
	 */
	public String buildTitlePanelContext(VelocityPortlet portlet, Context context, RunData rundata, SessionState state)
	{
		context.put("title", portlet.getPortletConfig().getTitle());

		if (requireResetToolLink())
		{
			context.put("includeReset", "true");
		}

		return "chef_Title";
	}

} // class VelocityPortletPaneledAction

/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/cheftool/VelocityPortletPaneledAction.java,v 1.43 2005/02/22 19:53:27 ggolden.umich.edu Exp $
*
**********************************************************************************/
