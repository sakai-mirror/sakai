/**********************************************************************************
*
* $Header: /cvs/sakai/component/src/java/org/sakaiproject/example/AttributeListener.java,v 1.2 2004/06/22 03:10:08 ggolden Exp $
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
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

/**
* <p>.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.2 $
*/
public class AttributeListener implements HttpSessionAttributeListener
{
	/**
	 * Respond to a session attribute creation.
	 */
	public void attributeAdded(HttpSessionBindingEvent sbe)
	{
		HttpSession session = sbe.getSession();
		ServletContext context = session.getServletContext();
		context.log(
			"HttpSessionAttribute Added: "
				+ sbe.getName()
				+ " = "
				+ sbe.getValue()
				+ " : "
				+ session.getId()
				+ " : "
				+ context.getServletContextName());
	}

	/**
	 * Respond to a session attribute removal.
	 */
	public void attributeRemoved(HttpSessionBindingEvent sbe)
	{
		HttpSession session = sbe.getSession();
		ServletContext context = session.getServletContext();
		context.log(
			"HttpSessionAttribute Removed: "
				+ sbe.getName()
				+ " = "
				+ sbe.getValue()
				+ " : "
				+ session.getId()
				+ " : "
				+ context.getServletContextName());
	}

	/**
	 * Respond to a session attribute replacement.
	 */
	public void attributeReplaced(HttpSessionBindingEvent sbe)
	{
		HttpSession session = sbe.getSession();
		ServletContext context = session.getServletContext();
		context.log(
			"HttpSessionAttribute Replaced: "
				+ sbe.getName()
				+ " = "
				+ sbe.getValue()
				+ " : "
				+ session.getId()
				+ " : "
				+ context.getServletContextName());
	}

} // class AttributeListener

/**********************************************************************************
*
* $Header: /cvs/sakai/component/src/java/org/sakaiproject/example/AttributeListener.java,v 1.2 2004/06/22 03:10:08 ggolden Exp $
*
**********************************************************************************/
