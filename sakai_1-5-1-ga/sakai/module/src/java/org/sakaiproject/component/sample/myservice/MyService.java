/**********************************************************************************
*
* $Header: /cvs/sakai/module/src/java/org/sakaiproject/component/sample/myservice/MyService.java,v 1.14 2004/06/22 03:14:14 ggolden Exp $
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

package org.sakaiproject.component.sample.myservice;

import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.sample.myservice.GreetingService;

/**
* <p>MyService implements the org.sakaiproject.service.sample.myservice.GreetingService, as a service component example.</p>
*
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.14 $
*/
public class MyService implements GreetingService
{
	/*******************************************************************************
	* Dependencies and their setter methods
	*******************************************************************************/

	/** Dependency: a logger component. */
	private Logger m_logger = null;

	/**
	 * Establish my logger component dependency.
	 * @param logger the logger component.
	 */
	public void setLogger(Logger logger)
	{
		m_logger = logger;

	}

	/*******************************************************************************
	* Init and Destroy
	*******************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		m_logger.info(this +".init()");
	}

	public void destroy()
	{
		m_logger.info(this +".destroy()");
	}

	/*******************************************************************************
	* Work interface methods: org.sakaiproject.service.sample.myservice.GreetingService
	*******************************************************************************/

	/**
	 * Access the greeting string.
	 * @return The greeting string.
	 */
	public String getGreeting()
	{
		return "Hello world!  And hello to you, ";
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/module/src/java/org/sakaiproject/component/sample/myservice/MyService.java,v 1.14 2004/06/22 03:14:14 ggolden Exp $
*
**********************************************************************************/
