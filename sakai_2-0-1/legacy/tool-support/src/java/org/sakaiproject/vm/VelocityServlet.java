/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/tool-support/src/java/org/sakaiproject/vm/VelocityServlet.java,v 1.2 2005/04/14 02:17:51 ggolden.umich.edu Exp $
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

package org.sakaiproject.vm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
* <p>Responds with the expansion of a Velocity Template.
* The template and context references are specified in the request.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public class VelocityServlet
	extends org.apache.velocity.servlet.VelocityServlet
{

	/**
	 *   Called by the VelocityServlet
	 *   init().  We want to set a set of properties
	 *   so that templates will be found in the webapp
	 *   root.  This makes this easier to work with as 
	 *   an example, so a new user doesn't have to worry
	 *   about config issues when first figuring things
	 *   out
	 */
	protected Properties loadConfiguration(ServletConfig config)
		throws IOException, FileNotFoundException
	{
		// load the properties as configured in the servlet init params
		Properties p = super.loadConfiguration(config);

		/*
		 *  first, we set the template path for the
		 *  FileResourceLoader to the root of the 
		 *  webapp.  This probably won't work under
		 *  in a WAR under WebLogic, but should 
		 *  under tomcat :)
		 */

		String path = config.getServletContext().getRealPath("/");

		if (path == null)
		{
			System.out.println(
				" SampleServlet.loadConfiguration() : unable to "
					+ "get the current webapp root.  Using '/'. Please fix.");

			path = "/";
		}

		p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, path);

		/**
		 *  and the same for the log file
		 */
		
		p.setProperty("runtime.log", path + p.getProperty("runtime.log"));

		return p;
	}

	/**
	 *  <p>
	 *  main routine to handle a request.  Called by
	 *  VelocityServlet, your responsibility as programmer
	 *  is to simply return a valid Template
	 *  </p>
	 *
	 *  @param ctx a Velocity Context object to be filled with
	 *             data.  Will be used for rendering this 
	 *             template
	 *  @return Template to be used for request
	 */
	public Template handleRequest(
		HttpServletRequest request,
		HttpServletResponse response,
		Context ctx)
	{
		// Note: Velocity doesn't like dots in the context names, so we change them to '_'

		// load the context with attributes
		Enumeration e = request.getAttributeNames();
		while (e.hasMoreElements())
		{
			String name = (String) e.nextElement();
			String vName = escapeVmName(name);
			Object value = request.getAttribute(name);

			ctx.put(vName, value);
//			log("--> context (attribute): " + vName + " = " + value);
		}

		// if the javax.servlet.include.servlet_path attribute exists, use this value as the template
		String templatePath =
			(String) request.getAttribute("javax.servlet.include.servlet_path");

		// if not there, try our special include
		if (templatePath == null)
		{
			templatePath = (String) request.getAttribute("sakai.vm.path");
		}

		// if not there, use the servletpath
		if (templatePath == null)
		{
			templatePath = request.getServletPath();
		}

		Template template = null;
		try
		{
//			log("--> template path: " + templatePath);
			template = getTemplate(templatePath);
		}
		catch (ParseErrorException ex)
		{
			log("Exception reading vm template: " + templatePath + " " + ex);
		}
		catch (ResourceNotFoundException ex)
		{
			log("Exception reading vm template: " + templatePath + " " + ex);
		}
		catch (Exception ex)
		{
			log("Exception reading vm template: " + templatePath + " " + ex);
		}

		return template;

	} // handleRequest

	/**
	 * Change any characters that Velocity doesn't like in the name to '_' to make a valid Velocity name
	 * @param name The name to convert.
	 * @return The name converted to a valid Velocity name.
	 */
	protected String escapeVmName(String name)
	{
		char[] chars = name.toCharArray();

		// make sure first character is valid (alpha)
		if (!Character.isLetter(chars[0]))
		{
			chars[0] = 'X';
		}

		// replace any other invalid characters
		for (int i = 1; i < chars.length; i++)
		{
			char c = chars[i];
			if (!(Character.isLetterOrDigit(c) || (c == '_') || (c == '-')))
			{
				chars[i] = '_';
			}
		}

		return new String(chars);

	} // escapeVmName

} // class VelocityServlet

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/tool-support/src/java/org/sakaiproject/vm/VelocityServlet.java,v 1.2 2005/04/14 02:17:51 ggolden.umich.edu Exp $
*
**********************************************************************************/
