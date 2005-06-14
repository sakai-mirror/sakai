/**********************************************************************************
*
* $Header: /cvs/sakai/module/src/java/org/sakaiproject/tool/mytool/MyTool.java,v 1.29.2.3 2005/03/21 15:19:09 lance.indiana.edu Exp $
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

package org.sakaiproject.tool.mytool;

import org.sakaiproject.jsf.ToolBean;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.portal.PortalService;
import org.sakaiproject.service.legacy.site.SiteService;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.time.cover.TimeService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.UserDirectoryService;
import org.sakaiproject.service.sample.myservice.GreetingService;

/**
 * <p>MyTool is a sample Faces based Sakai tool.</p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.29.2.3 $
 */
public class MyTool implements ToolBean
{

	/** UI Model: the date entered by the user */
	protected Time date = null;

	/** Dependency:  The logging service. */
	protected Logger logger = null;

	/** Dependency: The user directory service. */
	protected UserDirectoryService m_userDirectoryService = null;

	/** Dependency: The site service. */
    private SiteService m_siteService;

	/** Dependency: The portal service. */
    private PortalService m_portalService;
	
	/** Configuration parameter: just an example. */
	protected String param = null;

	/** Dependency: GreetingService */
	protected GreetingService service = null;

	/** UI Model: the user name entered by the user. */
	protected String userName = null;
	
	protected String m_text1 = "Enter formatted text here (1)";
	protected String m_text2 = "Enter formatted text here (2)";
	protected String m_text3 = "Enter formatted text here (3)";
	protected String m_text4 = "Enter formatted text here (4)";
	protected String m_text5 = "Enter formatted text here (5)";

	/**
	 * noarg constructor, required for managed beans.
	 */
	public MyTool()
	{
		date = TimeService.newTime();
	}

	/**
	 */
	public Time getDate()
	{
		logger.info(this + ".getDate: " + date);
		return date;
	}

	/**
	 * Access the greeting from the service.
	 * @return the greeting string.
	 */
	public String getGreeting()
	{
		return getService().getGreeting();
	}

	public User getCurrentUser()
	{
	    return m_userDirectoryService.getCurrentUser();
	}
	public String getCurrentSiteId()
	{
	    return m_portalService.getCurrentSiteId();
	}

	public boolean getCanCurrentUserModifySite()
	{
	    return m_siteService.allowUpdateSite(m_portalService.getCurrentSiteId());
	}
		
	/**
	 * Access the logging service.
	 * @return my logger component reference.
	 */
	protected Logger getLogger()
	{
		return logger;
	}

	/**
	 * Access the configuration parameter.
	 * @return my configuration parameter.
	 */
	public String getParam()
	{
		return param;
	}

	/**
	 * Access the greeting service.
	 * @return my sample service reference.
	 */
	public GreetingService getService()
	{
		return service;
	}

	/**
	 * Access the user name.
	 * @return the user name.
	 */
	public String getUserName()
	{
		return userName;
	}

	/**
	 * Process the "DoIt" action.
	 * @return the navigation outcome.
	 */
	public String processActionDoIt()
	{
		getLogger().info(this +".processActionDoIt()");

		// this becomes the next tool mode
		return "other";
	}

	/**
	 * Process the "DoItAgain" action.
	 * @return the navigation outcome.
	 */
	public String processActionDoItAgain()
	{
		getLogger().info(this +".processActionDoItAgain()");

		// this becomes the next tool mode
		return "main";
	}

	/**
	 * @param string
	 */
	public void setDate(Time time)
	{
		date = time;
		logger.info(this + ".setDate: " + date);
	}

	/**
	 * Establish my logging component dependency.
	 * @param logger the logging component.
	 */
	public void setLogger(Logger logger)
	{
		this.logger = logger;
	}

	/**
	 * Establish my configuration parameter.
	 * @param string the configuration parameter.
	 */
	public void setParam(String string)
	{
		param = string;
	}

	/**
	 * Establish my sample service component dependency.
	 * @param service the sample service component.
	 */
	public void setService(GreetingService service)
	{
		this.service = service;
	}
	
	public void setUserDirectoryService(UserDirectoryService service)
	{
	    this.m_userDirectoryService = service;
	}
	
	public void setSiteService(SiteService service)
	{
	    this.m_siteService = service;
	}
	
	public void setPortalService(PortalService service)
	{
	    this.m_portalService = service;
	}

	/**
	 * Set the user name.
	 * @param string The user name.
	 */
	public void setUserName(String string)
	{
		userName = string;
	}
	
	public void setText1(String val) { m_text1 = val; }
	public void setText2(String val) { m_text2 = val; }
	public void setText3(String val) { m_text3 = val; }
	public void setText4(String val) { m_text4 = val; }
	public void setText5(String val) { m_text5 = val; }
	
	public String getText1() { return m_text1; }
	public String getText2() { return m_text2; }
	public String getText3() { return m_text3; }
	public String getText4() { return m_text4; }
	public String getText5() { return m_text5; }

}

/**********************************************************************************
*
* $Header: /cvs/sakai/module/src/java/org/sakaiproject/tool/mytool/MyTool.java,v 1.29.2.3 2005/03/21 15:19:09 lance.indiana.edu Exp $
*
**********************************************************************************/
