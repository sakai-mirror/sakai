/**********************************************************************************
*
* $Header: /cvs/sakai/module/src/java/org/sakaiproject/tool/mytool/MyTool.java,v 1.26 2004/06/22 03:14:14 ggolden Exp $
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

import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.time.cover.TimeService;
import org.sakaiproject.service.sample.myservice.GreetingService;

/**
 * <p>MyTool is a sample Faces based Sakai tool.</p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.26 $
 */
public class MyTool
{

	/** UI Model: the date entered by the user */
	protected Time date = null;

	/** Dependency:  The logging service. */
	protected Logger logger = null;

	/** Configuration parameter: just an example. */
	protected String param = null;

	/** Dependency: GreetingService */
	protected GreetingService service = null;

	/** UI Model: the user name entered by the user. */
	protected String userName = null;

	/**
	 * noarg constructor, required for managed beans.
	 */
	public MyTool()
	{
		date = TimeService.newTime();
	}

	/**
	 * @return
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

	/**
	 * Set the user name.
	 * @param string The user name.
	 */
	public void setUserName(String string)
	{
		userName = string;
	}

}

/**********************************************************************************
*
* $Header: /cvs/sakai/module/src/java/org/sakaiproject/tool/mytool/MyTool.java,v 1.26 2004/06/22 03:14:14 ggolden Exp $
*
**********************************************************************************/
