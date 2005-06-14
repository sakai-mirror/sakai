/**********************************************************************************
*
* $Header: /cvs/sakai/james/src/java/org/sakaiproject/james/JamesServlet.java,v 1.7 2004/09/21 18:29:32 ggolden.umich.edu Exp $
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

package org.sakaiproject.james;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.sakaiproject.service.framework.component.cover.ComponentManager;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.vm.ComponentServlet;

/**
* <p>JamesServlet starts James.
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.7 $
*/
public class JamesServlet extends ComponentServlet
{
	/** config variable and system property for the james / phoenix home. */
	private final static String PHOENIX_HOME = "phoenix.home";

	/** The james / phoenix home value. */
	protected String m_phoenixHome = null;

	/** The JamesRunner (Thread). */
	protected JamesRunner m_runner = null;

	/** init thread - so we don't wait in the actual init() call */
	public class ServletInit extends Thread
	{
		/** The servlet config. */
		protected ServletConfig m_config = null;

		/**
		 * construct and start the init activity
		 */
		public ServletInit(ServletConfig config)
		{
			m_config = config;
			start();
		}

		/**
		 * run the init
		 */
		public void run()
		{
			// wait till component manager is "ready"
			// this is important because we are running at startup, and the services may not be
			// completely defined before this point.
			while (!ComponentManager.isReady())
			{
				log("JamesServlet: waiting on component manager ready...");
				try
				{
					Thread.sleep(1000);
				}
				catch (Exception e)
				{
				}
			}

			// get config info
			String host = ServerConfigurationService.getServerName();
			String dns1 = ServerConfigurationService.getString("dns.1");
			String dns2 = ServerConfigurationService.getString("dns.2");
			String smtpPort = ServerConfigurationService.getString("smtp.port");
			boolean enabled = ServerConfigurationService.getBoolean("email.enabled", false);

			log("JamesServlet.init(): host: " + host + " enabled: " + enabled + " dns1: " + dns1 + " dns2: " + dns2 + " smtp.port: " + smtpPort);

			// if not enabled, don't start james
			if (!enabled)
			{
				return;
			}

			// set the home for james / phoenix, as configured
			String homeRelative = m_config.getInitParameter(PHOENIX_HOME);
			if (homeRelative == null)
			{
				// or pointing to the webapps root if not configured
				homeRelative = "";
			}

			// expand to real path
			m_phoenixHome = getServletContext().getRealPath(homeRelative);

			customizeConfig(host, dns1, dns2, smtpPort);

			// start the James thread
			m_runner = new JamesRunner();
		}

		protected void customizeConfig(String host, String dns1, String dns2, String smtpPort)
		{
			try
			{
				// read the config file
				File f = new File(m_phoenixHome + "/apps/james/SAR-INF/config.xml");
				BufferedReader input = new BufferedReader(new FileReader(f));
				StringBuffer contents = new StringBuffer();
				String line = null;
				while ((line = input.readLine()) != null)
				{
					contents.append(line);
					contents.append(System.getProperty("line.separator"));
				}
				input.close();

				// TODO: modify [HOST] and [DNS]
				int pos = -1;
				while ((pos = contents.indexOf("[HOST]")) != -1)
				{
					contents.replace(pos, pos + 6, host);
				}

				pos = contents.indexOf("<server>[DNS1]</server>");
				if (pos != -1)
				{
					if ((dns1 != null) && (dns1.length() > 0))
					{
						contents.replace(pos, pos + 23, "<server>" + dns1 + "</server>");
					}
					else
					{
						contents.replace(pos, pos + 23, "");
					}
				}

				pos = contents.indexOf("<server>[DNS2]</server>");
				if (pos != -1)
				{
					if ((dns2 != null) && (dns2.length() > 0))
					{
						contents.replace(pos, pos + 23, "<server>" + dns2 + "</server>");
					}
					else
					{
						contents.replace(pos, pos + 23, "");
					}
				}

				pos = contents.indexOf("<port>[SMTP.PORT]</port>");
				if (pos != -1)
				{
					if ((smtpPort == null) || (smtpPort.length() == 0))
					{
						smtpPort = "25";
					}
					contents.replace(pos, pos + 24, "<port>" + smtpPort + "</port>");
				}

				// write it back
				Writer output = null;
				try
				{
					output = new BufferedWriter(new FileWriter(f));
					output.write(contents.toString());
				}
				finally
				{
					if (output != null)
						output.close();
				}
			}
			catch (Exception e)
			{
			}
		}
	}

	/** James thread */
	public class JamesRunner extends Thread
	{
		/**
		 * construct and start the init activity
		 */
		public JamesRunner()
		{
			start();
		}

		/**
		 * Run the James thread.
		 */
		public void run()
		{
			System.setProperty(PHOENIX_HOME, m_phoenixHome);

			log(this +".run: starting James service");

			// start James / Avalon running in this VM.
			try
			{
				// set the phoenix log in our log area
				String[] args = new String[2];
				args[0] = "-l";
				args[1] = "/usr/local/sakai/logs/phoenix.log";

				int exitCode = PhoenixLauncherMain.startup(args, new HashMap(), true);
				log(this +".run: James service stopped: " + exitCode);
			}
			catch (Exception e)
			{
				log(this +".run: exception:", e);
			}
		}
	}

	/**
	 * Access the Servlet's information display.
	 * @return servlet information.
	 */
	public String getServletInfo()
	{
		return "Sakai James Servlet";
	}

	/**
	 * Initialize the servlet.
	 * @param config The servlet config.
	 * @throws ServletException
	 */
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);

		// start the separate init thread
		new ServletInit(config);
	}

	/**
	 * Shutdown the servlet.
	 */
	public void destroy()
	{
		log("JamesServlet.destroy()");

		if (m_runner == null)
			return;

		PhoenixLauncherMain.shutdown();

		m_runner = null;

		super.destroy();
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/james/src/java/org/sakaiproject/james/JamesServlet.java,v 1.7 2004/09/21 18:29:32 ggolden.umich.edu Exp $
*
**********************************************************************************/
