/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2005, 2006 The Sakai Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *      http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.tool.su;

// sakai classes
import org.sakaiproject.api.kernel.session.Session;
import org.sakaiproject.api.kernel.session.SessionManager;
import org.sakaiproject.service.legacy.security.SecurityService;
import org.sakaiproject.service.legacy.user.UserDirectoryService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.util.java.ResourceLoader;

import org.sakaiproject.service.legacy.authzGroup.AuthzGroupService;
import org.sakaiproject.service.legacy.authzGroup.GroupProvider;

// java classes
import javax.faces.context.FacesContext;
import javax.faces.application.FacesMessage;

// apache common logging classes (probably change to sakai logging)
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author zach.thomas@txstate.edu
 */
public class SuTool
{

	private static final long serialVersionUID = 1L;

	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(SuTool.class);

	/** the GroupProvider will be dependency-injected from the information in faces-config.xml */
	private GroupProvider groupProvider;

	public void setGroupProvider(GroupProvider groupProvider)
	{
		this.groupProvider = groupProvider;
	}

	// Service instance variables
	private AuthzGroupService M_authzGroupService = org.sakaiproject.service.legacy.authzGroup.cover.AuthzGroupService
			.getInstance();

	private UserDirectoryService M_uds = org.sakaiproject.service.legacy.user.cover.UserDirectoryService.getInstance();

	private SecurityService M_security = org.sakaiproject.service.legacy.security.cover.SecurityService.getInstance();

	private SessionManager M_session = org.sakaiproject.api.kernel.session.cover.SessionManager.getInstance();

	// getters for these vars
	private String username;

	private String validatedUserId;

	private User userinfo;

	private boolean allowed = false;

	// internal only vars
	private String message = "";

	private boolean confirm = false;

	ResourceLoader msgs = new ResourceLoader("org.sakaiproject.tool.su.bundle.Messages");

	// base constructor
	public SuTool()
	{
	}

	/**
	 * Functions
	 */
	public String su()
	{

		Session sakaiSession = M_session.getCurrentSession();
		FacesContext fc = FacesContext.getCurrentInstance();
		userinfo = null;
		message = "";

		if (!getAllowed())
		{
			confirm = false;
			return "unauthorized";
		}

		try
		{
			// try with the user id
			userinfo = M_uds.getUser(username.trim());
			validatedUserId = userinfo.getId();
		}
		catch (IdUnusedException e)
		{
			try
			{
				// try with the user eid
				userinfo = M_uds.getUserByEid(username.trim());
				validatedUserId = userinfo.getId();
			}
			catch (IdUnusedException ee)
			{
				message = msgs.getString("no_such_user") + ": " + username;
				fc.addMessage("su", new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message + ":" + ee));
				M_log.warn("[SuTool] Exception: " + message);
				confirm = false;
				return "error";
			}
		}

		if (!confirm)
		{
			message = msgs.getString("displaying_info_for") + ": " + validatedUserId;
			fc.addMessage("su", new FacesMessage(FacesMessage.SEVERITY_INFO, message, message + ":" + userinfo.getDisplayName()));
			return "unconfirmed";
		}

		// set the session user from the value supplied in the form
		message = "Username " + sakaiSession.getUserId() + " becoming " + validatedUserId;
		M_log.info("[SuTool] " + message);
		fc.addMessage("su", new FacesMessage(FacesMessage.SEVERITY_INFO, message, message + ": Currently="
				+ userinfo.getDisplayName()));
		sakaiSession.setUserId(validatedUserId);
		sakaiSession.setUserEid(validatedUserId);

		// refesh the user's realms, so any recent changes to their site membership will take effect
		M_authzGroupService.refreshUser(validatedUserId);

		return "redirect";
	}

	// simple way to support 2 buttons that do almost the same thing
	public String confirm()
	{
		confirm = true;
		return su();
	}

	/**
	 * Specialized Getters
	 */
	public boolean getAllowed()
	{
		Session sakaiSession = M_session.getCurrentSession();
		FacesContext fc = FacesContext.getCurrentInstance();

		if (!M_security.isSuperUser())
		{
			message = "Unauthorized user attempted access: " + sakaiSession.getUserId();
			M_log.error("[SuTool] Fatal Error: " + message);
			fc.addMessage("allowed", new FacesMessage(FacesMessage.SEVERITY_FATAL, message, message + ":" + " unauthorized"));
			allowed = false;
		}
		else
		{
			allowed = true;
		}

		return allowed;
	}

	/**
	 * Basic Getters and setters
	 */
	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public User getUserinfo()
	{
		return userinfo;
	}

	public void setUserinfo(User userinfo)
	{
		this.userinfo = userinfo;
	}

}
