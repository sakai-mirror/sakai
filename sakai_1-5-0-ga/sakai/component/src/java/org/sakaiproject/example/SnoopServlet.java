package org.sakaiproject.example;

/*
 * @(#)SnoopServlet.java        1.20 97/11/17
 * 
 * Copyright (c) 1995-1997 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.0
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
/**
* Snoop servlet. This servlet simply echos back the request line and
* headers that were sent by the client, plus any HTTPS information
* which is accessible.
*
* @version     1.20
* @author      Various
*/
public class SnoopServlet extends HttpServlet
{
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		long start = System.currentTimeMillis();
		PrintWriter out;

		res.setContentType("text/html; charset=UTF-8");
		out = res.getWriter();

		out.println("<html>");
		out.println("<head><title>Sakai Snoop Servlet</title></head>");
		out.println("<body>");

		Enumeration e = getServletConfig().getInitParameterNames();
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
				out.println(" " + param + ": " + getInitParameter(param));
			}
			out.println("</pre>");
		}

		out.println("<h1>Request information:</h1>");
		out.println("<pre>");

		print(out, "Request method", req.getMethod());
		print(out, "Request URI", req.getRequestURI());
		print(out, "Request protocol", req.getProtocol());
		print(out, "Servlet path", req.getServletPath());
		print(out, "Path info", req.getPathInfo());
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

		// session
		out.println("<h1>Session</h1>");
		HttpSession session = req.getSession(false);
		if (session == null)
		{
			out.println("Session does not yet exist.<br />");
			session = req.getSession(true);
		}
		out.println("object: " + session + "<br />");
		out.println("id: " + session.getId() + "<br />");
		String value =
			(String) session.getAttribute("org.chefproject.SnoopServlet:test");
		out.println("test attribute: " + value + "<br />");
		session.setAttribute(
			"org.chefproject.SnoopServlet:test",
			getServletContext().getRealPath("text.txt"));

		//		// jndi
		//		out.println("<h1>JNDI</h1>");
		//		try
		//		{
		//			Context ctx = new InitialContext();
		//			out.println("InitialContext: " + ctx + "<br />");
		//
		//			if (ctx != null)
		//			{
		//				Context ctx2 = (Context) ctx.lookup("java:comp/env");
		//				out.println("java:comp/env context: " + ctx2 + "<br />");
		//
		//				if (ctx2 != null)
		//				{
		////					Object obj = ctx2.lookup("simpleValue");
		////					out.println("simpleVale: " + obj);
		//
		//					ctx2.bind("snoop","servlet");
		//
		//					Enumeration names = ctx2.list("");
		//					while (names.hasMoreElements())
		//					{
		//						NameClassPair pair =
		//							(NameClassPair) names.nextElement();
		//						out.println(
		//							"name: "
		//								+ pair.getName()
		//								+ " class: "
		//								+ pair.getClassName()
		//								+ "<br />");
		//					}
		//				}
		//			}
		//		}
		//		catch (NamingException e1)
		//		{
		//			out.println("Exception: " + e1.toString());
		//		}

		// cross context dispatch
		try
		{
			String dcontext = req.getParameter("x");
			String dpath = "/snoop";
			if (dcontext != null)
			{
				// get a dispatcher to the services context
				ServletContext servicesContext = getServletContext().getContext(dcontext);
				if (servicesContext != null)
				{
					RequestDispatcher dispatcher = servicesContext.getRequestDispatcher(dpath);
					if (dispatcher != null)
					{
						out.println("<p>* * * Including cross context dispatch * * *<br />");
						dispatcher.include(req, res);
						out.println("<br />* * *");
					}
					else
					{
						out.println("dispatch: cannot find dispatcher for " + dpath + "<br />");
					}
				}
				else
				{
					out.println("dispatch: cannot find context: " + dcontext + "<br />");
				}
			}
		}
		catch (Throwable t)
		{
			out.println("dispatch exception: " + t + "<br />");
		}

		// bulk - bulk=kbytes response size bulk up
		try
		{
			long bulk = Long.parseLong(req.getParameter("bulk"));
			out.println("<p>Bulk: " + bulk + "Kbytes </br>");
			StringBuffer bulkStr = new StringBuffer(1024);

			// fill the buf with 1024 characters
			for (int i = 0; i < 16; i++)
			{
				for (int j = 0; j < 59; j++)
				{
					bulkStr.append((char) ((int) ' ' + j));
				}
				bulkStr.append("<br>\n");
			}

			// bulk up
			for (int i = 0; i < bulk; i++)
			{
				out.println(bulkStr);
			}

			out.println("</p>");
		}
		catch (Exception ex)
		{
		}

		// delay - delay=ms busy time
		try
		{
			long delay = Long.parseLong(req.getParameter("delay"));
			out.println("<p>Delay: " + delay + "</p>");
			for (long i = 0; i < delay; i++)
			{
				Thread.sleep(1);
			}
		}
		catch (Exception ex)
		{
		}

		// sleep - sleep=ms sleep time
		try
		{
			long sleep = Long.parseLong(req.getParameter("sleep"));
			out.println("<p>Sleep: " + sleep + "</p>");
			Thread.sleep(sleep);
		}
		catch (Exception ex)
		{
		}

		long elapsed = System.currentTimeMillis() - start;
		out.println("<p>Time: " + elapsed + " ms</p>");
		out.println("</body></html>");
	}

	private void print(PrintWriter out, String name, String value)
	{
		out.print(" " + name + ": ");
		out.println(value == null ? "&lt;none&gt;" : value);
	}

	private void print(PrintWriter out, String name, int value)
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
}
