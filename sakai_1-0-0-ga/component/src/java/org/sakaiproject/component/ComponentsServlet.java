/**********************************************************************************
*
* $Header: /cvs/sakai/component/src/java/org/sakaiproject/component/ComponentsServlet.java,v 1.27 2004/10/01 19:29:26 ggolden.umich.edu Exp $
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

package org.sakaiproject.component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.sakaiproject.service.framework.component.cover.ComponentManager;
import org.sakaiproject.service.framework.current.cover.CurrentService;
import org.sakaiproject.service.framework.log.cover.Logger;

/**
* <p>ComponentsServlet registers service components for a webapp, and provides a management
* interface to the component system from any webapp.</p>
* <p>At startup, it reads a <strong>components.xml</strong> configuration file from the webapp WEB-INF directory, and
* feeds this to the {@link org.sakaiproject.service.framework.component.ComponentManager Component Manager} to register
* a set of components from this webapp.  Componets are available system wide, not just to clients in this webapp.</p>
* <p>The servlet response lists the services registered system wide.</p>
* <p>Make sure to include a <strong>&lt;servlet&gt;</strong> definition for this servlet in the webapp's <strong>web.xml</strong>,
* with <strong>&lt;load-on-startup&gt;</strong> set.
* If you want to enable the management interface, provide a servlet mapping as well.</p>
* <p>Example:</p>
* <p><code>
* &lt;servlet&gt;<br>
* &nbsp;&nbsp;&lt;servlet-name&gt;<br>
* &nbsp;&nbsp;&nbsp;&nbsp;components<br>
* &nbsp;&nbsp;&lt;/servlet-name&gt;<br>
* &nbsp;&nbsp;&lt;servlet-class&gt;<br>
* &nbsp;&nbsp;&nbsp;&nbsp;org.sakaiproject.component.framework.componentsServlet<br>
* &nbsp;&nbsp;&lt;/servlet-class&gt;<br>
* &nbsp;&nbsp;&lt;load-on-startup&gt;1&lt;/load-on-startup&gt;<br>
* &lt;/servlet&gt;</p>
* <p> &lt;servlet-mapping&gt;<br>
* &nbsp;&nbsp;&lt;servlet-name&gt;<br>
* &nbsp;&nbsp;&nbsp;&nbsp;components<br>
* &nbsp;&nbsp;&lt;/servlet-name&gt;<br>
* &nbsp;&nbsp;&lt;url-pattern&gt;<br>
* &nbsp;&nbsp;&nbsp;&nbsp;/components/*<br>
* &nbsp;&nbsp;&lt;/url-pattern&gt;<br>
* &lt;/servlet-mapping&gt;
* </code></p>
*
* @author University of Michigan, Sakai Software Development Team
* @version $Revision: 1.27 $
*/
public class ComponentsServlet extends HttpServlet
{
	/** Session attribute holding the cross-webapp session. */
	public static final String GLOBAL_SESSION_ATTR = "org.sakaiproject.global.session";

	/** The bean factory - nested within the overall parent factory defined by the ComponentManager */
	protected org.sakaiproject.service.framework.component.ComponentManager m_components = null;

	/**
	 * Access the Servlet's information display.
	 * @return servlet information.
	 */
	public String getServletInfo()
	{
		return "Sakai Component Manager";
	}

	/**
	 * Initialize the servlet.
	 * @param config The servlet config.
	 * @throws ServletException
	 */
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);

		String loadedFrom = null;

		if ("true".equals(config.getInitParameter("register-componentManager")))
		{
			// register the component manager as the component manager
			ComponentManager.loadComponent(
				org.sakaiproject.service.framework.component.ComponentManager.class.getName(),
				ComponentManager.getInstance());
		}

		// create our section of the component manager hierarchy
		m_components = ComponentManager.nestComponentManager();

		boolean loaded = false;
		// check this external-to-the-webapp config file
		String exFilePath = config.getInitParameter("ex-components-path");
		if ((exFilePath != null) && (exFilePath.length() > 0))
		{
			File exFile = new File(exFilePath);
			if (exFile.exists() && exFile.canRead())
			{
				try
				{
					// if the file's there, and readable, we consider things done, even if it fails for some reason
					loaded = true;
					loadedFrom = exFilePath;
					InputStream componentDefinitionXml = new FileInputStream(exFile);
					m_components.loadXmlStream(componentDefinitionXml);
				}
				catch (Throwable e)
				{
					log(this +".init: " + e);
				}
			}
		}

		if (!loaded)
		{
			// get the components xml as a stream, if it exists - name can be set in the parameters (rel to WEB-INF/)
			String fileName = config.getInitParameter("components-file");
			if ((fileName != null) && (fileName.length() > 0))
			{
				try
				{
					loadedFrom = "WEB-INF/" + fileName;
					InputStream componentDefinitionXml = config.getServletContext().getResourceAsStream(loadedFrom);
					m_components.loadXmlStream(componentDefinitionXml);
				}
				catch (Throwable e)
				{
					log(this +".init: " + e);
				}
			}
		}

		if ("true".equals(config.getInitParameter("set-ready")))
		{
			// tell the component manager hierarchy that all is ready
			ComponentManager.setReady(true);
		}

		// check for service component preload
		String preload = config.getInitParameter("preload");
		if (preload != null)
		{
			if (preload.equals("*"))
			{
				try
				{
					CurrentService.startThread("PRELOAD");

					// load ALL services
					Set ifaces = ComponentManager.getRegisteredInterfaces();
					for (Iterator iIfaces = ifaces.iterator(); iIfaces.hasNext();)
					{
						String iface = (String) iIfaces.next();
				
						// if this is a visible interface, try to get it
						try
						{
							Class cls = Class.forName(iface, true, Thread.currentThread().getContextClassLoader());

							if (cls.isInterface())
							{
								Object component = ComponentManager.get(iface);
								Logger.info("preload: " + iface);
							}
						}
						catch (ClassNotFoundException e)
						{
						}
					}
				}

				finally
				{
					// clear out any current access bindings
					CurrentService.clearInThread();
				}
			}
		}

		log(this + ".init(): loading from: " + loadedFrom);
	}

	/**
	 * Shutdown the servlet.
	 */
	public void destroy()
	{
		log("ComponentsServlet.destroy()");
		m_components.unload();

		super.destroy();
	}

	/**
	 * Respond to requests with a management display listing registered components.
	 * @param req The servlet request.
	 * @param res The servlet response.
	 * @throws ServletException.
	 * @throws IOException.
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		try
		{
			// set the session up for current thread access
			// Note: velocity:org.sakaiproject.vm.ComponentServlet has matching code, and defines the string
			HttpSession hsession = (HttpSession) req.getAttribute(GLOBAL_SESSION_ATTR);
			if (hsession == null)
			{
				hsession = req.getSession(true);
			}

			CurrentService.setInThread(HttpSession.class.getName(), hsession);
			CurrentService.setInThread(HttpServletRequest.class.getName(), req);

			PrintWriter out = res.getWriter();

			out.println("Sakai Component Manager");

			// TODO: what to do here? report on the services loaded, status of "statusable" ones, etc.
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

			out.println("\nComponents (webapp - " + m_components + "):");
			ifaces = m_components.getLocalRegisteredInterfaces();
			for (Iterator iIfaces = ifaces.iterator(); iIfaces.hasNext();)
			{
				String iface = (String) iIfaces.next();

				// if this is a visible interface, try to get it
				try
				{
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
			// clear out any current access bindings
			CurrentService.clearInThread();
		}
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/component/src/java/org/sakaiproject/component/ComponentsServlet.java,v 1.27 2004/10/01 19:29:26 ggolden.umich.edu Exp $
*
**********************************************************************************/
