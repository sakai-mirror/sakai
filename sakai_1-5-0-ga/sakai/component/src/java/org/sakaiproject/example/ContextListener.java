/**********************************************************************************
*
* $Header: /cvs/sakai/component/src/java/org/sakaiproject/example/ContextListener.java,v 1.2 2004/06/22 03:10:08 ggolden Exp $
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

package org.sakaiproject.example;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
* <p>ContextListener listens for context events and reports them.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.2 $
*/
public class ContextListener implements ServletContextListener
{
	/**
	 * {@inheritDoc}
	 */
	public void contextInitialized(ServletContextEvent event)
	{
		ServletContext context = event.getServletContext();
		System.err.println("context Created: " + context.getServletContextName());
	}

	/**
	 * {@inheritDoc}
	 */
	public void contextDestroyed(ServletContextEvent event)
	{
		ServletContext context = event.getServletContext();
		System.err.println("context Destroyed: " + context.getServletContextName());	
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/component/src/java/org/sakaiproject/example/ContextListener.java,v 1.2 2004/06/22 03:10:08 ggolden Exp $
*
**********************************************************************************/
