/**********************************************************************************
 *
 * $Header: /cvs/sakai2/test/comp-test-app1/src/java/org/sakaiproject/tool/test/CompTest1Tool.java,v 1.11 2005/05/17 18:49:36 csev.umich.edu Exp $
 *
 ***********************************************************************************
 *
 * Copyright (c) 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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

package org.sakaiproject.tool.test;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.api.kernel.component.cover.ComponentManager;
import org.sakaiproject.api.test.components.Api1;
import org.sakaiproject.api.test.components.Api2;
import org.sakaiproject.api.test.components.Api3;
import org.sakaiproject.api.test.components.Api4;
import org.sakaiproject.api.test.components.Api5;
import org.sakaiproject.util.TestUtil;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * <p>
 * Sakai test servlet.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class CompTest1Tool extends HttpServlet
{
	/**
	 * Access the Servlet's information display.
	 * 
	 * @return servlet information.
	 */
	public String getServletInfo()
	{
		return "Sakai Component Test Servlet in app1";
	}

	/**
	 * Initialize the servlet.
	 * 
	 * @param config
	 *        The servlet config.
	 * @throws ServletException
	 */
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
		log(this + ".init()");
	}

	/**
	 * Shutdown the servlet.
	 */
	public void destroy()
	{
		log(this + ".destroy()");

		super.destroy();
	}

	/**
	 * Respond to requests.
	 * 
	 * @param req
	 *        The servlet request.
	 * @param res
	 *        The servlet response.
	 * @throws ServletException.
	 * @throws IOException.
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException
	{
		PrintWriter out = res.getWriter();

		out.println(getServletInfo());
		
		TestUtil.showSession(out, false);

		// discover a bean from the Spring webapp context, the Spring way
		//		(WebApplicationContext) getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		if (wac != null)
		{
			Api1 managerFromSpring = (Api1) wac.getBean(Api1.class
					.getName());
			if (managerFromSpring == null)
			{
				out.println("missing component from Spring's local AC: "
						+ Api1.class.getName());
			}
			else
			{
				String value = managerFromSpring.method();
				out.println("Api1 (from Spring): " + value);
			}
		}
		else
		{
			out.println("missing Spring's WAC from servlet context: "
							+ WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		}

		// discover the shared component via component manager
		Api1 manager = (Api1) ComponentManager.get(Api1.class);
		if (manager == null)
		{
			out.println("missing component: " + Api1.class.getName());
		}
		else
		{
			String value = manager.method();
			out.println("Api1: " + value);
		}

		// get a local component via Spring
		if (wac != null)
		{
			Api2 manager2FromSpring = (Api2) wac.getBean(Api2.class
					.getName());
			if (manager2FromSpring == null)
			{
				out.println("missing component from Spring's local AC: "
						+ Api2.class.getName());
			}
			else
			{
				String value = manager2FromSpring.method();
				out.println("Api2 (from Spring): " + value);
			}
		}

		// check other managers
		Api3 manager3 = (Api3) ComponentManager.get(Api3.class);
		if (manager3 == null)
		{
			out.println("missing component: " + Api3.class.getName());
		}
		else
		{
			String value = manager3.method();
			out.println("Api3: " + value);
		}

		// check other managers
		Api4 manager4 = (Api4) ComponentManager.get(Api4.class);
		if (manager4 == null)
		{
			out.println("missing component: " + Api4.class.getName());
		}
		else
		{
			String value = manager4.method();
			out.println("Api4: " + value);
		}

		// check other managers
		Api5 manager5 = (Api5) ComponentManager.get(Api5.class);
		if (manager5 == null)
		{
			out.println("missing component: " + Api5.class.getName());
		}
		else
		{
			String value = manager5.method();
			out.println("Api5: " + value);
		}

		// discover the shared component via component manager
		org.sakaiproject.api.test.components.Api6 manager6 = (org.sakaiproject.api.test.components.Api6) ComponentManager.get(org.sakaiproject.api.test.components.Api6.class);
		if (manager6 == null)
		{
			out.println("missing component: " + org.sakaiproject.api.test.components.Api6.class.getName());
		}
		else
		{
			String value = manager6.method();
			out.println("Api6: " + value);
		}

		// discover and call the shared component via its cover
		try 
		{
			String value = org.sakaiproject.api.test.components.cover.Api6.method();
			out.println("Api6 via cover: " + value);
		}
		catch (Throwable t)
		{
			out.println("Failure calling Api6 cover: " + t.getMessage() );
		}
		
		// Print out the registered components
		TestUtil.dumpComponents(out, false);
	}
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/test/comp-test-app1/src/java/org/sakaiproject/tool/test/CompTest1Tool.java,v 1.11 2005/05/17 18:49:36 csev.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
