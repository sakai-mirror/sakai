/**********************************************************************************
 *
 * $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/framework/config/BasicConfigurationService.java,v 1.14 2005/05/26 01:26:48 ggolden.umich.edu Exp $
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

// package
package org.sakaiproject.component.framework.config;

// imports
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import org.sakaiproject.service.framework.config.ServerConfigurationService;
import org.sakaiproject.service.framework.current.cover.CurrentService;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.legacy.resource.ResourceService;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.util.RequestFilter;
import org.sakaiproject.util.java.StringUtil;
import org.sakaiproject.util.xml.Xml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>
 * BasicConfigurationService is a basic implementation of the ServerConfigurationService.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class BasicConfigurationService implements ServerConfigurationService
{
	/** The instance id for this app server. */
	protected String instanceId = null;

	/** This is computed, joining the configured serverId and the set instanceId. */
	protected String serverIdInstance = null;

	/** Properties as loaded. */
	protected Properties loadProps = new Properties();

	/** The map of values from the loaded properties - not synchronized at access. */
	protected Map properties = new HashMap();

	/** Full path to registration files. */
	protected String registrationPath = null;

	/** File name within sakai.home for the tool order file. */
	protected String toolOrderFile = null;

	/** List of locks (functions). */
	protected List registeredLocks = new Vector();

	/** Set of resource services. */
	protected Set m_resourceServices = new HashSet();

	/** loaded tool orders - map keyed by category of List of tool id strings. */
	protected Map m_toolOrders = new HashMap();

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Dependencies and their setter methods
	 *********************************************************************************************************************************************************************************************************************************************************/

	/** Dependency: logging service */
	protected Logger m_logger = null;

	/**
	 * Dependency: logging service.
	 * 
	 * @param service
	 *        The logging service.
	 */
	public void setLogger(Logger service)
	{
		m_logger = service;
	}

	/**
	 * Configuration: set generic properties.
	 * 
	 * @param props
	 *        the set of generic properties.
	 */
	public void setProperties(Properties props)
	{
		loadProps = props;
	}

	/**
	 * Configuration: set the file path for registration files.
	 * 
	 * @param string
	 *        The file path for registration files.
	 */
	public void setRegistrationPath(String string)
	{
		registrationPath = string;
	}

	/**
	 * Configuration: set the file name within sakai.home for tool order file.
	 * 
	 * @param string
	 *        The file name for tool order file.
	 */
	public void setToolOrderFile(String string)
	{
		toolOrderFile = string;
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Init and Destroy
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		// load the extra properties, sakai then local then security
		try
		{
			loadProps.load(new FileInputStream(getSakaiHomePath() + "sakai.properties"));
		}
		catch (Exception e)
		{
		}

		try
		{
			loadProps.load(new FileInputStream(getSakaiHomePath() + "local.properties"));
		}
		catch (Exception e)
		{
		}

		try
		{
			loadProps.load(new FileInputStream(System.getProperty("sakai.security") + "security.properties"));
		}
		catch (Exception e)
		{
		}

		// Properties are based on Hashtable and are Synchronized in their get() method. We don't want that!
		properties.putAll(loadProps);
		loadProps = null;

		try
		{
			// set a unique instance id for this server run
			// Note: to reduce startup dependency, just use the current time, NOT the id service.
			instanceId = Long.toString(System.currentTimeMillis());

			serverIdInstance = getServerId() + "-" + instanceId;
		}
		catch (Throwable t)
		{
			m_logger.warn(this + ".init(): ", t);
		}

		// treat this as a resource to read from the class loader, a single file
		// not: loadRegistrations(new File(registrationPath));
		if (registrationPath != null)
		{
			InputStream in = getClass().getClassLoader().getResourceAsStream(registrationPath);
			if (in != null)
			{
				loadRegistrationStream(in);
			}
			else
			{
				m_logger.warn(this + ".init(): can't access resource: " + registrationPath);
			}
		}
		
		// load in the tool order, if specified, from the sakai home area
		if (toolOrderFile != null)
		{
			File f = new File(getSakaiHomePath() + toolOrderFile);
			if (f.exists())
			{
				try
				{
					loadToolOrder(new FileInputStream(f));
				}
				catch (Throwable t)
				{
					m_logger.warn(this + ".init(): trouble loading tool order from : " + getSakaiHomePath() + toolOrderFile + " : " + t.toString());
				}
			}
		}

		m_logger.info(this + ".init()");
	}

	/**
	 * Final cleanup.
	 */
	public void destroy()
	{
		m_logger.info(this + ".destroy()");
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * ServerConfigurationService implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * {@inheritDoc}
	 */
	public String getServerId()
	{
		return (String) properties.get("serverId");
	}

	/**
	 * {@inheritDoc}
	 */
	public String getServerInstance()
	{
		return instanceId;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getServerIdInstance()
	{
		return serverIdInstance;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getServerUrl()
	{
		// try to get the value pre-computed for this request, to better match the request server naming conventions
		String rv = (String) CurrentService.getInThread(RequestFilter.CURRENT_SERVER_URL);
		if (rv == null)
		{
			rv = (String) properties.get("serverUrl");
		}

		return rv;

	} 

	/**
	 * {@inheritDoc}
	 */
	public String getServerName()
	{
		return (String) properties.get("serverName");
	}

	/**
	 * {@inheritDoc}
	 */
	public String getAccessUrl()
	{
		return getServerUrl() + (String) properties.get("accessPath");
	}

	/**
	 * {@inheritDoc}
	 */
	public String getHelpUrl(String helpContext)
	{
		String rv = getPortalUrl() + (String) properties.get("helpPath") + "/main";
		if (helpContext != null)
		{
			rv += "?help=" + helpContext;
		}
		
		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getPortalUrl()
	{
		return getServerUrl() + (String) properties.get("portalPath");
	}

	/**
	 * {@inheritDoc}
	 */
	public String getUserHomeUrl()
	{
		String rv = (String) properties.get("userHomeUrl");

		// use the portal URL with the current user's My Workspace, if not otherwise defined
		if (rv == null)
		{
			String user = UsageSessionService.getSessionUserId();
			boolean loggedIn = (user != null);
			String siteId = null;
			if (loggedIn)
			{
				siteId = SiteService.getUserSiteId(user);
			}
			else
			{
				siteId = getGatewaySiteId();
			}

			rv = getPortalUrl() + "/site/" + siteId;
			//			rv = getPortalUrl() + "?site=" + siteId;
		}

		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getGatewaySiteId()
	{
		String rv = (String) properties.get("gatewaySiteId");

		if (rv == null)
		{
			rv = "~anon";
		}

		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getLoginUrl()
	{
		String rv = (String) properties.get("loginUrl");
		if (rv != null)
		{
			// if not a full URL, add the server to the front
			if (rv.startsWith("/"))
			{
				rv = getServerUrl() + rv;
			}
		}

		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getLoggedOutUrl()
	{
		String rv = (String) properties.get("loggedOutUrl");
		if (rv != null)
		{
			// if not a full URL, add the server to the front
			if (rv.startsWith("/"))
			{
				rv = getServerUrl() + rv;
			}
		}

		// use the portal URL if there's no logout defined
		else
		{
			rv = getPortalUrl();
		}

		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getSakaiHomePath()
	{
		String rv = System.getProperty("sakai.home");
		if (rv == null)
		{
			rv = "/usr/local/sakai/";
		}
		if (!rv.endsWith("/")) rv = rv + "/";
		
		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getString(String name)
	{
		return getString(name, "");
	}

	/**
	 * {@inheritDoc}
	 */
	public String getString(String name, String dflt)
	{
		String rv = StringUtil.trimToNull((String) properties.get(name));
		if (rv == null) rv = dflt;

		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public String[] getStrings(String name)
	{
		// get the count
		int count = getInt(name + ".count", 0);
		if (count > 0)
		{
			String[] rv = new String[count];
			for (int i = 1; i <= count; i++)
			{
				rv[i - 1] = getString(name + "." + i, "");
			}
			return rv;
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getInt(String name, int dflt)
	{
		String value = getString(name);

		if (value.length() == 0) return dflt;

		return Integer.parseInt(value);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean getBoolean(String name, boolean dflt)
	{
		String value = getString(name);

		if (value.length() == 0) return dflt;

		return Boolean.valueOf(value).booleanValue();
	}

	/**
	 * {@inheritDoc}
	 */
	public List getLocks()
	{
		List rv = new Vector();
		rv.addAll(registeredLocks);

		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public List getToolOrder(String category)
	{
		if (category != null)
		{
			List order = (List) m_toolOrders.get(category);
			if (order != null)
			{
				return order;
			}
		}

		return new Vector();
	}

	/**
	 * {@inheritDoc}
	 */
	public void registerResourceService(ResourceService service)
	{
		m_resourceServices.add(service);
	}

	/**
	 * {@inheritDoc}
	 */
	public List getResourceServices()
	{
		List rv = new Vector();
		rv.addAll(m_resourceServices);

		return rv;
	}

	/**
	 * Load root as a registration file, or if it's a directory, all the files within.
	 * 
	 * @param root
	 *        The file or directory to load.
	 */
	protected void loadRegistrations(File root)
	{
		if (root.isDirectory())
		{
			File files[] = root.listFiles();
			if (files != null)
			{
				for (int i = 0; i < files.length; i++)
				{
					loadRegistrations(files[i]);
				}
			}
		}
		else
		{
			try
			{
				loadRegistrationFile(root);
			}
			catch (Throwable t)
			{
				m_logger.warn(this + ".loadRegistrations: file: " + root.getAbsolutePath() + " : " + t);
			}
		}
	}

	/**
	 * Load this single file as a registration file, loading tools and locks.
	 * 
	 * @param file
	 *        The file to load
	 */
	protected void loadRegistrationFile(File file)
	{
		String path = file.getAbsolutePath();
		if (!path.endsWith(".xml"))
		{
			m_logger.info(this + ".loadRegistrationFile: skiping file: " + path);
			return;
		}

		try
		{
			InputStream in = new FileInputStream(path);

			m_logger.info(this + ".loadRegistrationFile: " + path);
			loadRegistrationStream(in);
		}
		catch (FileNotFoundException e)
		{
			m_logger.info(this + ".loadRegistrationFile: can't read file: " + path);
		}
	}

	/**
	 * Load this single file as a registration file, loading tools and locks.
	 * 
	 * @param in
	 *        The Stream to load
	 */
	protected void loadRegistrationStream(InputStream in)
	{
		Map categories = new HashMap();

		Document doc = Xml.readDocumentFromStream(in);
		Element root = doc.getDocumentElement();
		if (!root.getTagName().equals("registration"))
		{
			m_logger.info(this + ".loadRegistrationFile: invalid root element (expecting \"registration\"): " + root.getTagName());
			return;
		}

		// read the children nodes
		NodeList rootNodes = root.getChildNodes();
		final int rootNodesLength = rootNodes.getLength();
		for (int i = 0; i < rootNodesLength; i++)
		{
			Node rootNode = rootNodes.item(i);
			if (rootNode.getNodeType() != Node.ELEMENT_NODE) continue;
			Element rootElement = (Element) rootNode;

			// ignore "tool" and "category" entries - these are done now with the tool manager
			if (rootElement.getTagName().equals("function"))
			{
				String name = StringUtil.trimToNull(rootElement.getAttribute("name"));
				if (name != null)
				{
					registeredLocks.add(name);
				}
			}
		}
	}

	/**
	 * Load this single file as a registration file, loading tools and locks.
	 * 
	 * @param in
	 *        The Stream to load
	 */
	protected void loadToolOrder(InputStream in)
	{
		Map categories = new HashMap();

		Document doc = Xml.readDocumentFromStream(in);
		Element root = doc.getDocumentElement();
		if (!root.getTagName().equals("toolOrder"))
		{
			m_logger.info(this + ".loadToolOrder: invalid root element (expecting \"toolOrder\"): " + root.getTagName());
			return;
		}

		// read the children nodes
		NodeList rootNodes = root.getChildNodes();
		final int rootNodesLength = rootNodes.getLength();
		for (int i = 0; i < rootNodesLength; i++)
		{
			Node rootNode = rootNodes.item(i);
			if (rootNode.getNodeType() != Node.ELEMENT_NODE) continue;
			Element rootElement = (Element) rootNode;

			// look for "category" elements
			if (rootElement.getTagName().equals("category"))
			{
				String name = StringUtil.trimToNull(rootElement.getAttribute("name"));
				if (name != null)
				{
					// form a list for this category
					List order = (List) m_toolOrders.get(name);
					if (order == null)
					{
						order = new Vector();
						m_toolOrders.put(name, order);

						// get the kids
						NodeList nodes = rootElement.getChildNodes();
						final int nodesLength = nodes.getLength();
						for (int c = 0; c < nodesLength; c++)
						{
							Node node = nodes.item(c);
							if (node.getNodeType() != Node.ELEMENT_NODE) continue;
							Element element = (Element) node;
							
							if (element.getTagName().equals("tool"))
							{
								String id = StringUtil.trimToNull(element.getAttribute("id"));
								if (id != null)
								{
									order.add(id);
								}
							}
						}
					}
				}
			}
		}
	}
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/framework/config/BasicConfigurationService.java,v 1.14 2005/05/26 01:26:48 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
