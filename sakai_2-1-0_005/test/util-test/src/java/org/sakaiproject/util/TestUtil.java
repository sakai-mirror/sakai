/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
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

package org.sakaiproject.util;

import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;

import java.util.Set;
import java.util.Iterator;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import org.sakaiproject.api.kernel.session.Session;
import org.sakaiproject.api.kernel.session.ToolSession;
import org.sakaiproject.api.kernel.session.cover.SessionManager;

import org.sakaiproject.api.kernel.component.cover.ComponentManager;

/**
 * <p>
 * Test Utility Class
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class TestUtil
{
	public static void showSession(PrintWriter out, boolean html)
	{
		// get the current user session information
		Session s = SessionManager.getCurrentSession();
		if (s == null)
		{
			out.println("no session established");
			if (html) out.println("<br />");
		}
		else
		{
			out.println("session: " + s.getId()
				+ " user id: " + s.getUserId()
				+ " user enterprise id: " + s.getUserEid()
				+ " started: " + new Date(s.getCreationTime()).toLocaleString()
				+ " accessed: " + new Date(s.getLastAccessedTime()).toLocaleString()
				+ " inactive after: " + s.getMaxInactiveInterval());
			if (html) out.println("<br />");
		}
		
		ToolSession ts = SessionManager.getCurrentToolSession();
		if (ts == null)
		{
			out.println("no tool session established");
			if (html) out.println("<br />");
		}
		else
		{
			out.println("tool session: " + ts.getId()
				+ " started: " + new Date(ts.getCreationTime()).toLocaleString()
				+ " accessed: " + new Date(ts.getLastAccessedTime()).toLocaleString());
			if (html) out.println("<br />");
		}
	}

	public static void dumpComponents(PrintWriter out, boolean html)
	{
		try
		{
			out.println("Sakai Component Manager");

			out.println("\nComponents (all - " + ComponentManager.getInstance() + "):");
			Set ifaces = ComponentManager.getRegisteredInterfaces();
			for (Iterator iIfaces = ifaces.iterator(); iIfaces.hasNext();)
			{
				String iface = (String) iIfaces.next();
				
				// if this is a visible interface, try to get it
				try
				{
					// Class cls = getClass().getClassLoader().loadClass(iface);
					// Class cls = Thread.currentThread().getContextClassLoader().getClass();
					// Class cls = Class.forName(names[i]);
					Class cls = Class.forName(iface, true, Thread.currentThread().getContextClassLoader());

					if (cls.isInterface())
					{
						// a visible interface
						Object component = ComponentManager.get(iface);
						out.println(iface + " --> " + component);
					}
					else
					{
						// visible, but not an interface
						out.println(iface + " ** not an interface **");
					}
				}
				catch (ClassNotFoundException e)
				{
					// not visible
					out.println(iface + " ** class not found **");
				}
			}

		}
		finally
		{
		}

	}

	public static void snoop(PrintWriter out, boolean html, ServletConfig config, HttpServletRequest req)
	{
		Enumeration e = config.getInitParameterNames();
		if (e != null)
		{
			boolean first = true;
			while (e.hasMoreElements())
			{
				if (first)
				{
					out.println("<h1>Init Parameters</h1>");
					out.println("<pre>");
					first = false;
				}
				String param = (String) e.nextElement();
				out.println(" " + param + ": " + config.getInitParameter(param));
			}
			out.println("</pre>");
		}

		out.println("<h1>Request information:</h1>");
		out.println("<pre>");

		print(out, "Request method", req.getMethod());
		String requestUri = req.getRequestURI();
		print(out, "Request URI", requestUri);
		displayStringChars(out, requestUri);
		print(out, "Request protocol", req.getProtocol());
		String servletPath = req.getServletPath();
		print(out, "Servlet path", servletPath);
		displayStringChars(out, servletPath);
		String contextPath = req.getContextPath();
		print(out, "Context path", contextPath);
		displayStringChars(out, contextPath);
		String pathInfo = req.getPathInfo();
		print(out, "Path info", pathInfo);
		displayStringChars(out, pathInfo);
		print(out, "Path translated", req.getPathTranslated());
		print(out, "Query string", req.getQueryString());
		print(out, "Content length", req.getContentLength());
		print(out, "Content type", req.getContentType());
		print(out, "Server name", req.getServerName());
		print(out, "Server port", req.getServerPort());
		print(out, "Remote user", req.getRemoteUser());
		print(out, "Remote address", req.getRemoteAddr());
//		print(out, "Remote host", req.getRemoteHost());
		print(out, "Authorization scheme", req.getAuthType());

		out.println("</pre>");

		e = req.getHeaderNames();
		if (e.hasMoreElements())
		{
			out.println("<h1>Request headers:</h1>");
			out.println("<pre>");
			while (e.hasMoreElements())
			{
				String name = (String) e.nextElement();
				out.println(" " + name + ": " + req.getHeader(name));
			}
			out.println("</pre>");
		}

		e = req.getParameterNames();
		if (e.hasMoreElements())
		{
			out.println("<h1>Servlet parameters (Single Value style):</h1>");
			out.println("<pre>");
			while (e.hasMoreElements())
			{
				String name = (String) e.nextElement();
				out.println(" " + name + " = " + req.getParameter(name));
			}
			out.println("</pre>");
		}

		e = req.getParameterNames();
		if (e.hasMoreElements())
		{
			out.println("<h1>Servlet parameters (Multiple Value style):</h1>");
			out.println("<pre>");
			while (e.hasMoreElements())
			{
				String name = (String) e.nextElement();
				String vals[] = (String[]) req.getParameterValues(name);
				if (vals != null)
				{
					out.print("<b> " + name + " = </b>");
					out.println(vals[0]);
					for (int i = 1; i < vals.length; i++)
						out.println("           " + vals[i]);
				}
				out.println("<p>");
			}
			out.println("</pre>");
		}

		e = req.getAttributeNames();
		if (e.hasMoreElements())
		{
			out.println("<h1>Request attributes:</h1>");
			out.println("<pre>");
			while (e.hasMoreElements())
			{
				String name = (String) e.nextElement();
				out.println(" " + name + ": " + req.getAttribute(name));
			}
			out.println("</pre>");
		}

	}

	protected static void print(PrintWriter out, String name, String value)
	{
		out.print(" " + name + ": ");
		out.println(value == null ? "&lt;none&gt;" : value);
	}

	protected static void print(PrintWriter out, String name, int value)
	{
		out.print(" " + name + ": ");
		if (value == -1)
		{
			out.println("&lt;none&gt;");
		}
		else
		{
			out.println(value);
		}
	}
	
	protected static void displayStringChars(PrintWriter out, String str)
	{
		if (str == null)
		{
			out.print("null");
		}
		else for (int i = 0; i < str.length(); i++)
		{
			int c = (int) str.charAt(i);
			out.print(Integer.toHexString(c) + " ");
		}
		out.println();
	}
}



