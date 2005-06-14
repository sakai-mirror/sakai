/**********************************************************************************
 *
 * $Header: /cvs/sakai2/legacy/james/src/java/org/sakaiproject/james/JamesServlet.java,v 1.4 2005/06/02 03:52:56 ggolden.umich.edu Exp $
 *
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.util.java.StringUtil;

/**
 * <p>
 * JamesServlet starts James.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class JamesServlet extends HttpServlet
{
	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(JamesServlet.class);

	/** config variable and system property for the james / phoenix home. */
	private final static String PHOENIX_HOME = "phoenix.home";

	/** The james / phoenix home value. */
	protected String m_phoenixHome = null;

	/** The JamesRunner (Thread). */
	protected JamesRunner m_runner = null;

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

			M_log.info("run: starting James service");

			// start James / Avalon running in this VM.
			try
			{
				// set the phoenix log in our log area (logs in sakai.home)
				String sakaiHome = System.getProperty("sakai.home");
				String[] args = new String[2];
				args[0] = "-l";
				args[1] = sakaiHome + "logs/phoenix.log";

				int exitCode = PhoenixLauncherMain.startup(args, new HashMap(), true);
				M_log.info("run: James service stopped: " + exitCode);
			}
			catch (Throwable e)
			{
				M_log.warn("run: exception:", e);
			}
		}
	}

	/**
	 * Access the Servlet's information display.
	 * 
	 * @return servlet information.
	 */
	public String getServletInfo()
	{
		return "Sakai James Servlet";
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

		startJames(config);
	}

	/**
	 * Shutdown the servlet.
	 */
	public void destroy()
	{
		M_log.info("destroy()");

		if (m_runner == null) return;

		PhoenixLauncherMain.shutdown();

		m_runner = null;

		super.destroy();
	}

	protected void startJames(ServletConfig config)
	{
		// get config info
		String host = ServerConfigurationService.getServerName();
		String dns1 = StringUtil.trimToNull(ServerConfigurationService.getString("smtp.dns.1"));
		String dns2 = StringUtil.trimToNull(ServerConfigurationService.getString("smtp.dns.2"));
		String smtpPort = StringUtil.trimToNull(ServerConfigurationService.getString("smtp.port"));
		boolean enabled = ServerConfigurationService.getBoolean("smtp.enabled", false);

		M_log.info("init(): host: " + host + " enabled: " + enabled + " dns1: " + dns1 + " dns2: " + dns2 + " smtp.port: "
				+ smtpPort);

		// if not enabled, don't start james
		if (!enabled)
		{
			return;
		}

		// set the home for james / phoenix, as configured
		String homeRelative = config.getInitParameter(PHOENIX_HOME);
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

			// modify [HOST], [DNS1], [DNS2] and [SMTP.PORT]
			int pos = -1;
			String target = "[HOST]";
			while ((pos = contents.indexOf(target)) != -1)
			{
				contents.replace(pos, pos + target.length(), host);
			}

			target = "<server>[DNS1]</server>";
			pos = contents.indexOf(target);
			if (pos != -1)
			{
				if ((dns1 != null) && (dns1.length() > 0))
				{
					contents.replace(pos, pos + target.length(), "<server>" + dns1 + "</server>");
				}
				else
				{
					contents.replace(pos, pos + target.length(), "");
				}
			}

			target = "<server>[DNS2]</server>";
			pos = contents.indexOf(target);
			if (pos != -1)
			{
				if ((dns2 != null) && (dns2.length() > 0))
				{
					contents.replace(pos, pos + target.length(), "<server>" + dns2 + "</server>");
				}
				else
				{
					contents.replace(pos, pos + target.length(), "");
				}
			}

			target = "<port>[SMTP.PORT]</port>";
			pos = contents.indexOf(target);
			if (pos != -1)
			{
				if ((smtpPort == null) || (smtpPort.length() == 0))
				{
					smtpPort = "25";
				}
				contents.replace(pos, pos + target.length(), "<port>" + smtpPort + "</port>");
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
				if (output != null) output.close();
			}
		}
		catch (Exception e)
		{
		}

		try
		{
			// read the environment file
			File f = new File(m_phoenixHome + "/apps/james/SAR-INF/environment.xml");
			BufferedReader input = new BufferedReader(new FileReader(f));
			StringBuffer contents = new StringBuffer();
			String line = null;
			while ((line = input.readLine()) != null)
			{
				contents.append(line);
				contents.append(System.getProperty("line.separator"));
			}
			input.close();

			// modify [SAKAI.HOME]
			int pos = -1;
			String target = "[SAKAI.HOME]";
			while ((pos = contents.indexOf(target)) != -1)
			{
				contents.replace(pos, pos + target.length(), System.getProperty("sakai.home"));
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
				if (output != null) output.close();
			}
		}
		catch (Exception e)
		{
		}
	}
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/legacy/james/src/java/org/sakaiproject/james/JamesServlet.java,v 1.4 2005/06/02 03:52:56 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
