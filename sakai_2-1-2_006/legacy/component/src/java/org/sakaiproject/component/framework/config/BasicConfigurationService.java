/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
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
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.sakaiproject.api.kernel.component.cover.ComponentManager;
import org.sakaiproject.api.kernel.function.cover.FunctionManager;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.service.framework.config.ServerConfigurationService;
import org.sakaiproject.service.framework.current.cover.CurrentService;
import org.sakaiproject.service.framework.log.Logger;
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

	/** The map of values from the loaded properties - not synchronized at access. */
	protected Map m_properties = new HashMap();

	/** Full path to registration files. */
	protected String registrationPath = null;

	/** File name within sakai.home for the tool order file. */
	protected String toolOrderFile = null;

	/** loaded tool orders - map keyed by category of List of tool id strings. */
	protected Map m_toolOrders = new HashMap();

	/** required tools - map keyed by category of List of tool id strings. */
	protected Map m_toolsRequired = new HashMap();

	/** default tools - map keyed by category of List of tool id strings. */
	protected Map m_defaultTools = new HashMap();
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
		// load the properties, from the configuration manager's set of properties that were used to configure the components
		m_properties.putAll(ComponentManager.getConfig());

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
		return (String) m_properties.get("serverId");
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
			rv = (String) m_properties.get("serverUrl");
		}

		return rv;

	} 

	/**
	 * {@inheritDoc}
	 */
	public String getServerName()
	{
		return (String) m_properties.get("serverName");
	}

	/**
	 * {@inheritDoc}
	 */
	public String getAccessUrl()
	{
		return getServerUrl() + (String) m_properties.get("accessPath");
	}

	/**
	 * {@inheritDoc}
	 */
	public String getHelpUrl(String helpContext)
	{
		String rv = getPortalUrl() + (String) m_properties.get("helpPath") + "/main";
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
		return getServerUrl() + (String) m_properties.get("portalPath");
	}

	/**
	 * {@inheritDoc}
	 */
	public String getUserHomeUrl()
	{
		String rv = (String) m_properties.get("userHomeUrl");

		// use the portal URL with the current user's My Workspace, if not otherwise defined
		if (rv == null)
		{
			String user = SessionManager.getCurrentSessionUserId();
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
		String rv = (String) m_properties.get("gatewaySiteId");

		if (rv == null)
		{
			rv = "~anon";
		}

		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getLoggedOutUrl()
	{
		String rv = (String) m_properties.get("loggedOutUrl");
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
		String rv = StringUtil.trimToNull((String) m_properties.get(name));
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
		// this is now a kernel responsibility
		return FunctionManager.getRegisteredFunctions();
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
	public List getToolsRequired(String category)
	{
		if (category != null)
		{
			List order = (List) m_toolsRequired.get(category);
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
	public List getDefaultTools(String category)
	{
		if (category != null)
		{
			List order = (List) m_defaultTools.get(category);
			if (order != null)
			{
				return order;
			}
		}

		return new Vector();
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
						
						List required = new Vector();
						m_toolsRequired.put(name, required);
						List defaultTools = new Vector();
						m_defaultTools.put(name, defaultTools);

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
								
								String req = StringUtil.trimToNull(element.getAttribute("required"));
								if ((req != null) && (Boolean.TRUE.toString().equalsIgnoreCase(req)))
								{
									required.add(id);
								}

								String sel = StringUtil.trimToNull(element.getAttribute("selected"));
								if ((sel != null) && (Boolean.TRUE.toString().equalsIgnoreCase(sel)))
								{
									defaultTools.add(id);
								}
							}
						}
					}
				}
			}
		}
	}
}
