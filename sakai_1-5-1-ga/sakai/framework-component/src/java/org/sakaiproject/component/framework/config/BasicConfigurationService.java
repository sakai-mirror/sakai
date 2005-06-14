/**********************************************************************************
*
* $Header: /cvs/sakai/framework-component/src/java/org/sakaiproject/component/framework/config/BasicConfigurationService.java,v 1.42 2005/02/07 16:51:49 zqian.umich.edu Exp $
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

// package
package org.sakaiproject.component.framework.config;

// imports
import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.sakaiproject.service.framework.config.ServerConfigurationService;
import org.sakaiproject.service.framework.config.ToolRegistration;
import org.sakaiproject.service.framework.current.cover.CurrentService;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.legacy.announcement.cover.AnnouncementService;
import org.sakaiproject.service.legacy.assignment.cover.AssignmentService;
import org.sakaiproject.service.legacy.calendar.cover.CalendarService;
import org.sakaiproject.service.legacy.chat.cover.ChatService;
import org.sakaiproject.service.legacy.content.cover.ContentHostingService;
import org.sakaiproject.service.legacy.discussion.cover.DiscussionService;
import org.sakaiproject.service.legacy.email.cover.MailArchiveService;
import org.sakaiproject.service.legacy.resource.ResourceProperties;
import org.sakaiproject.service.legacy.resource.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.util.StringUtil;
import org.sakaiproject.util.Xml;
import org.sakaiproject.util.resource.BaseResourcePropertiesEdit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
* <p>BasicConfigurationService is a basic implementation of the ServerConfigurationService.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.42 $
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

	/** Properties overrides for injected settings. */
	protected String overrideProperties = null;

	/** Properties overrides for injected settings. */
	protected String additionalProperties = null;

	/** Full path to registration files. */
	protected String registrationPath = null;

	/** List of tool registrations. */
	protected List registeredTools = new Vector();

	/** List of locks (functions). */
	protected List registeredLocks = new Vector();
	
	/** The Hashtable of registered tools within categories */
	protected Hashtable registeredToolsInCategories = new Hashtable();
	
	/** The Hashtable of default registered tools within categories */
	protected Hashtable defaultRegisteredToolIdsInCategories = new Hashtable();

	/*******************************************************************************
	* Dependencies and their setter methods
	*******************************************************************************/

	/** Dependency: logging service */
	protected Logger m_logger = null;

	/**
	 * Dependency: logging service.
	 * @param service The logging service.
	 */
	public void setLogger(Logger service)
	{
		m_logger = service;
	}

	/**
	 * Configuration: set generic properties.
	 * @param props the set of generic properties.
	 */
	public void setProperties(Properties props)
	{
		loadProps = props;
	}

	/**
	 * Configuration: set the file path and name for a properties file with values
	 * to override anything else configured (except for the override properties).
	 * @param string The override properties file full path
	 */
	public void setAdditionalProperties(String string)
	{
		additionalProperties = string;
	}

	/**
	 * Configuration: set the file path and name for a second override
	 * properties file with values to override anything else configured.
	 * @param string The override properties file full path
	 */
	public void setOverrideProperties(String string)
	{
		overrideProperties = string;
	}

	/**
	 * Configuration: set the file path for registration files.
	 * @param string The file path for registration files.
	 */
	public void setRegistrationPath(String string)
	{
		registrationPath = string;
	}

	/*******************************************************************************
	* Init and Destroy
	*******************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		// if additional properties is set, use it
		if (additionalProperties != null)
		{
			try
			{
				loadProps.load(new FileInputStream(additionalProperties));
			}
			catch (Exception e)
			{
			}
		}

		// if override is set, use it
		if (overrideProperties != null)
		{
			try
			{
				loadProps.load(new FileInputStream(overrideProperties));
			}
			catch (Exception e)
			{
			}
		}
		
		// Properties are based on Hashtable and are Synchronized in their get() method.  We don't want that!
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
			m_logger.warn(this +".init(): ", t);
		}

		// collect catagory registrations to apply once all tools are registered
		Map categories = new HashMap();

		// read all the .xml files from the registrationPath to register tools and locks
		loadRegistrations(new File(registrationPath), categories);

		// add any categories found
		for (Iterator i = categories.entrySet().iterator(); i.hasNext();)
		{
			// entry.key is the tool id, entry.value is the set of categories
			Map.Entry entry = (Map.Entry) i.next();

			// find the registered tool
			BaseToolRegistration reg = (BaseToolRegistration) getToolRegistration((String)entry.getKey());
			if (reg != null)
			{
				// add each category
				for (Iterator c = ((Set) entry.getValue()).iterator(); c.hasNext();)
				{
					String category = (String) c.next();
					reg.addCategory(category);
				}
			}
		}
		
		//add ToolRegistration to corresponding categories
		//replace the current id List
		Enumeration e = registeredToolsInCategories.keys();
		while(e.hasMoreElements())
		{
			// categories
			String c = (String) e.nextElement();
			
			List toolIds = (List) registeredToolsInCategories.get(c);
			
			List toolRegistrations = new Vector();
			for (int k=0; k<toolIds.size();k++)
			{
				String tId = (String) toolIds.get(k);
				ToolRegistration tr = getToolRegistration(tId);
				if (tr != null)
				{
					toolRegistrations.add(tr);
				}
			}
			
			registeredToolsInCategories.put(c, toolRegistrations);
		}

		m_logger.info(this +".init()");
	}

	/**
	 * Final cleanup.
	 */
	public void destroy()
	{
		m_logger.info(this +".destroy()");
	}

	/*******************************************************************************
	* ServerConfigurationService implementation
	*******************************************************************************/

	/**
	* Access the unique (to the cluster) id of the server.
	* @return The unique (to the cluster) id of the server.
	*/
	public String getServerId()
	{
		return (String) properties.get("serverId");

	} // getServerId

	/**
	* Access the unique (to the cluster) instance id of the server.
	* @return The unique (to the cluster) instance id of the server.
	*/
	public String getServerInstance()
	{
		return instanceId;

	} // getServerInstance

	/**
	* Access the combined server / instance id.
	* @return The combined server / instance id.
	*/
	public String getServerIdInstance()
	{
		return serverIdInstance;

	} // getServerIdInstance

	/**
	* Access the URL to the root of the server - append any additional path to the end.
	* @return The URL to the root of the server.
	*/
	public String getServerUrl()
	{
		// try to get the value pre-computed for this request, to better match the request server naming conventions
		String rv = (String) CurrentService.getInThread("sakai.wrapper.return.url");
		if (rv == null)
		{
			rv = (String) properties.get("serverUrl");
		}
		
		return rv;

	} // getServerUrl

	/**
	* Access the server DNS name.
	* @return The server DNS name.
	*/
	public String getServerName()
	{
		return (String) properties.get("serverName");
	}

	/**
	* Access the URL back to the CHEF access service on the server - append any additional path to the end.
	* @return The URL back to the CHEF access service on the server.
	*/
	public String getAccessUrl()
	{
		return getServerUrl() + (String) properties.get("accessPath");

	} // getAccessUrl

	/**
	* Access the URL back to the CHEF portal service on the server - append any additional path to the end.
	* @return The URL back to the CHEF portal service on the server.
	*/
	
//	HttpServletRequest req = (HttpServletRequest) m_currentService.getInThread(HttpServletRequest.class.getName());

	public String getPortalUrl()
	{
	    HttpServletRequest req = (HttpServletRequest) CurrentService.getInThread(HttpServletRequest.class.getName());
	    
	    String rv = getServerUrl();
	    String displayPrefix = (String) req.getAttribute("displayPrefix");
	    
	    if (displayPrefix != null) {
	        rv += displayPrefix;
	    }
	    	else {
	    	    rv += (String) properties.get("portalPath");
	    	}
		return rv;
	} // getPortalUrl
	
	/**
	 * {@inheritDoc}
	 */
	public String getPortalTunnelUrl()
	{
		return getServerUrl() + (String) properties.get("portalTunnelPath");
	}

	/**
	 * {@inheritDoc}
	 */
	public String getUserHomeUrl()
	{
		String rv = (String) properties.get("userHomeUrl");

		// use the portal URL with the current user's myWorkspace, if not otherwise defined
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
	* Access the URL to use as a redirect when the user needs to login.
	* @return The URL to use as a redirect when the user needs to login.
	*/
	public String getLoginUrl()
	{
		return (String) properties.get("loginUrl");
	}

	/**
	* Access the URL to use as a redirect when the user has logged out.
	* @return The URL to use as a redirect when the user has logged out.
	*/
	public String getLoggedOutUrl()
	{
		String rv = (String) properties.get("loggedOutUrl");

		// use the portal URL if there's no logout defined
		if (rv == null)
			rv = getPortalUrl();

		return rv;

	} // getLoggedOutUrl

	/**
	* Access some named configuration value as a string.
	* @param name The configuration value name.
	* @return The configuration value with this name, or "" if not found.
	*/
	public String getString(String name)
	{
		return getString(name, "");

	} // getString

	/**
	* Access some named configuration value as a string.
	* @param name The configuration value name.
	* @param dflt The value to return if not found.
	* @return The configuration value with this name, or the default value if not found.
	*/
	public String getString(String name, String dflt)
	{
		String rv = StringUtil.trimToNull((String) properties.get(name));
		if (rv == null) rv = dflt;

		return rv;

	} // getString

	/**
	* Access some named configuration values as an array of strings.
	* The name is the base name.
	* name + ".count" must be defined to be a positive integer - how many are defined.
	* name + "." + i (1..count) must be defined to be the values.
	* @param name The configuration value name base.
	* @return The configuration value with this name, or the null if not found.
	*/
	public String[] getStrings(String name)
	{
		// get the count
		int count = getInt(name+".count", 0);
		if (count > 0)
		{
			String[] rv = new String[count];
			for (int i = 1; i <= count; i++)
			{
				rv[i-1] = getString(name + "." + i,"");
			}
			return rv;
		}
		
		return null;

	} // getStrings

	/**
	* Access some named configuration value as an int.
	* @param name The configuration value name.
	* @param dflt The value to return if not found.
	* @return The configuration value with this name, or the default value if not found.
	*/
	public int getInt(String name, int dflt)
	{
		String value = getString(name);

		if (value.length() == 0)
			return dflt;

		return Integer.parseInt(value);

	} // getInt

	/**
	* Access some named configuration value as a boolean.
	* @param name The configuration value name.
	* @param dflt The value to return if not found.
	* @return The configuration value with this name, or the default value if not found.
	*/
	public boolean getBoolean(String name, boolean dflt)
	{
		String value = getString(name);

		if (value.length() == 0)
			return dflt;

		return Boolean.valueOf(value).booleanValue();

	} // getBoolean

	/**
	* Access a particular tool registration.
	* @param id The tool registration id.
	* @return The ToolRegistration with this id, or null if not found.
	*/
	public ToolRegistration getToolRegistration(String id)
	{
		List tools = getToolRegistrations();
		for (Iterator iTools = tools.iterator(); iTools.hasNext();)
		{
			ToolRegistration tool = (ToolRegistration) iTools.next();
			if (id.equals(tool.getId()))
				return tool;
		}

		return null;

	} // getToolRegistration

	/**
	* Access the list of feature tools available on this server.
	* @return List (ToolRegistration) of available tools.
	*/
	public List getToolRegistrations()
	{
		List rv = new Vector();
		rv.addAll(registeredTools);

		return rv;

	} // getTools
	
    /**
	* Access the list of feature tools available on this server registered in this category.
	* @param category The category name.
	* @return List (ToolRegistration) of available tools in this category.
	*/
    public List getToolRegistrations(String category)
	{
		List rv = new Vector();
		rv.addAll((List) registeredToolsInCategories.get(category));

		return rv;

	} // getToolIdsInCategory	

    /**
	* Access the list of default feature tools available on this server registered in this category.
	* @param category The category name.
	* @return List (String) of available default tool ids in this category.
	*/
    public List getDefaultToolIds(String category)
	{
		List rv = new Vector();
		if (defaultRegisteredToolIdsInCategories.containsKey(category))
		{
			rv.addAll((List) defaultRegisteredToolIdsInCategories.get(category));
		}
		return rv;

	} // getDefaultToolIdRegistrations
    
	/**
	* Access the list of security locks in use on this server.
	* @return List (String) of security locks in use on this server.
	*/
	public List getLocks()
	{
		List rv = new Vector();
		rv.addAll(registeredLocks);

		return rv;

	} // getLocks

	/**
	* Access the list of services which keep site related resources.
	* @return List (ResourceService) of services which keep site related resources.
	*/
	public List getResourceServices()
	{
		// %%% Make this either configured or have the services register... -ggolden
		List rv = new Vector();

		rv.add(AnnouncementService.getInstance());
		rv.add(AssignmentService.getInstance());
		rv.add(CalendarService.getInstance());
		rv.add(ChatService.getInstance());
		rv.add(ContentHostingService.getInstance());
		rv.add(DiscussionService.getInstance());
		rv.add(MailArchiveService.getInstance());

		return rv;

	} // getResourceServices

	/**
	 * Load root as a registration file, or if it's a directory, all the files within.
	 * @param root The file or directory to load.
	 */
	protected void loadRegistrations(File root, Map categories)
	{
		if (root.isDirectory())
		{
			File files[] = root.listFiles();
			if (files != null)
			{
				for (int i = 0; i < files.length; i++)
				{
					loadRegistrations(files[i], categories);
				}
			}
		}
		else
		{
			try
			{
				loadRegistrationFile(root, categories);
			}
			catch (Throwable t)
			{
				m_logger.warn(this + ".loadRegistrations: file: " + root.getAbsolutePath() + " : " + t);
			}
		}
	}

	/**
	 * Load this single file as a registration file, loading tools and locks.
	 * @param file The file to load
	 */
	protected void loadRegistrationFile(File file, Map categories)
	{
		String path = file.getAbsolutePath();
		if (!path.endsWith(".xml"))
		{
			m_logger.info(this +".loadRegistrationFile: skiping file: " + path);
			return;
		} 

		m_logger.info(this +".loadRegistrationFile: " + path);

		Document doc = Xml.readDocument(path);
		Element root = doc.getDocumentElement();
		if (!root.getTagName().equals("registration"))
		{
			m_logger.info(
				this +".loadRegistrationFile: invalid root element (expecting \"registration\"): " + root.getTagName());
			return;
		}

		// read the children nodes
		NodeList rootNodes = root.getChildNodes();
		final int rootNodesLength = rootNodes.getLength();
		for (int i = 0; i < rootNodesLength; i++)
		{
			Node rootNode = rootNodes.item(i);
			if (rootNode.getNodeType() != Node.ELEMENT_NODE) continue;
			Element rootElement = (Element)rootNode;

			// for tool
			if (rootElement.getTagName().equals("tool"))
			{
				String id = StringUtil.trimToNull(rootElement.getAttribute("id"));
				String title = StringUtil.trimToNull(rootElement.getAttribute("title"));
				String description = StringUtil.trimToNull(rootElement.getAttribute("description"));
				String url = StringUtil.trimToNull(rootElement.getAttribute("url"));
				String helpUrl = StringUtil.trimToNull(rootElement.getAttribute("helpUrl"));
				BaseToolRegistration reg = new BaseToolRegistration(id, title, description, url, helpUrl);

				NodeList kids = rootElement.getChildNodes();
				final int kidsLength = kids.getLength();
				for (int k = 0; k < kidsLength; k++)
				{
					Node kidNode = kids.item(k);
					if (kidNode.getNodeType() != Node.ELEMENT_NODE) continue;
					Element kidElement = (Element) kidNode;
					
					// for configuration
					if (kidElement.getTagName().equals("configuration"))
					{
						String name = StringUtil.trimToNull(kidElement.getAttribute("name"));
						String value = StringUtil.trimToZero(kidElement.getAttribute("value"));
						if (name != null)
						{
							reg.m_defaultConfiguration.addProperty(name, value);
						}
					}
				}

				registeredTools.add(reg);
			}
			
			else if (rootElement.getTagName().equals("function"))
			{
				String name = StringUtil.trimToNull(rootElement.getAttribute("name"));
				if (name != null)
				{
					registeredLocks.add(name);
				}
			}
			
			else if (rootElement.getTagName().equals("category"))
			{
				String name = StringUtil.trimToNull(rootElement.getAttribute("name"));
				
				Vector toolsInCategory = new Vector();

				NodeList kids = rootElement.getChildNodes();
				final int kidsLength = kids.getLength();
				for (int k = 0; k < kidsLength; k++)
				{
					Node kidNode = kids.item(k);
					if (kidNode.getNodeType() != Node.ELEMENT_NODE) continue;
					Element kidElement = (Element) kidNode;
					
					// for configuration
					if (kidElement.getTagName().equals("tool"))
					{
						String id = StringUtil.trimToNull(kidElement.getAttribute("id"));
						if (id != null)
						{
							// this tool (id) needs to have this category (name)
							// tool may not yet exist
							Set toolCategories = (Set) categories.get(id);
							if (toolCategories == null)
							{
								toolCategories = new HashSet();
								categories.put(id, toolCategories);
							}
							toolCategories.add(name);
						}
						
						// add into the tool id list under the category
						// not ToolRegistration this time, since tool may not exist yet.
						toolsInCategory.add(id);
					}					
				}
				
				// registered tools in category
				registeredToolsInCategories.put(name, toolsInCategory);
			}
			
			else if (rootElement.getTagName().equals("default-tool"))
			{
				// default tools
				String name = StringUtil.trimToNull(rootElement.getAttribute("name"));
				
				Vector defaultToolsInCategory = new Vector();

				NodeList kids = rootElement.getChildNodes();
				final int kidsLength = kids.getLength();
				for (int k = 0; k < kidsLength; k++)
				{
					Node kidNode = kids.item(k);
					if (kidNode.getNodeType() != Node.ELEMENT_NODE) continue;
					Element kidElement = (Element) kidNode;
					
					// for configuration
					if (kidElement.getTagName().equals("tool"))
					{
						String id = StringUtil.trimToNull(kidElement.getAttribute("id"));
						if (id != null)
						{
							// this tool (id) needs to have this category (name)
							// tool may not yet exist
							Set toolCategories = (Set) categories.get(id);
							if (toolCategories == null)
							{
								toolCategories = new HashSet();
								categories.put(id, toolCategories);
							}
							toolCategories.add(name);
						}
						
						// add into the tool id list under the category
						// not ToolRegistration this time, since tool may not exist yet.
						defaultToolsInCategory.add(id);
					}					
				}
				
				// registered tools in category
				defaultRegisteredToolIdsInCategories.put(name, defaultToolsInCategory);
			}
		}
	}

	/*******************************************************************************
	* ToolRegistration implementation
	*******************************************************************************/

	public class BaseToolRegistration implements ToolRegistration
	{
		/** The tool id. */
		protected String m_id = null;

		/** The tool Url. */
		protected String m_url = null;
		
		/** The help registration xml file Url. */
		protected String m_helpUrl = null;

		/** The properties. */
		protected ResourcePropertiesEdit m_properties = null;

		/** The properties storing the tool's default configuration. */
		protected ResourcePropertiesEdit m_defaultConfiguration = null;

		/**
		* Construct.
		* @param id The user id.
		*/
		public BaseToolRegistration(String id, String title, String description, String url, String helpUrl)
		{
			// set the id
			m_id = id;

			// set the url
			m_url = url;
			
			// set the helpUrl
			m_helpUrl = helpUrl;

			// setup both meta and default configuration properties
			m_properties = new BaseResourcePropertiesEdit();
			m_defaultConfiguration = new BaseResourcePropertiesEdit();

			// set title and description properties
			m_properties.addProperty(ResourceProperties.PROP_TOOL_TITLE, title);
			m_properties.addProperty(ResourceProperties.PROP_TOOL_DESCRIPTION, description);

			// set one category
			m_properties.addPropertyToList(ResourceProperties.PROP_TOOL_CATEGORY, "CHEF");
			m_properties.addPropertyToList(ResourceProperties.PROP_TOOL_CATEGORY, "Sakai");

		} // BaseToolRegistration

		/** Set another category. */
		public void addCategory(String category)
		{
			m_properties.addPropertyToList(ResourceProperties.PROP_TOOL_CATEGORY, category);

		} // addCategory

		/**
		* Access the user id.
		* @return The user id string.
		*/
		public String getId()
		{
			return m_id;

		} // getId

		/**
		* Access the URL which can be used to access the resource.
		* @return The URL which can be used to access the resource.
		*/
		public String getUrl()
		{
			// add the portal tunnel, if set
		    String rv = m_url;
		    
			String tunnel = getPortalTunnelUrl();
			if (tunnel != null)
			{
				rv = tunnel + m_url;
			}

			return rv;
		} // getUrl
		
		public String getHelpUrl()
		{
			// add the portal tunnel, if set
			String tunnel = getPortalTunnelUrl();
			if (tunnel != null & m_helpUrl != null)
			{
				return tunnel + m_helpUrl;
			}

			return m_helpUrl;  
		}

		/**
		* Access the internal reference which can be used to access the resource from within the system.
		* @return The the internal reference which can be used to access the resource from within the system.
		*/
		public String getReference()
		{
			return m_id; // %%%

		} // getReference

		/**
		* Access the resources's properties.
		* @return The resources's properties.
		*/
		public ResourceProperties getProperties()
		{
			return m_properties;

		} // getProperties

		/**
		* Serialize the resource into XML, adding an element to the doc under the top of the stack element.
		* @param doc The DOM doc to contain the XML (or null for a string return).
		* @param stack The DOM elements, the top of which is the containing element of the new "resource" element.
		* @return The newly added element.
		*/
		public Element toXml(Document doc, Stack stack)
		{
			return null; // %%%

		} // toXml

		/** @return The human readable Title of the tool. */
		public String getTitle()
		{
			return m_properties.getProperty(ResourceProperties.PROP_TOOL_TITLE);

		} // getTitle

		/** @return A longer text Description of the tool. */
		public String getDescription()
		{
			return m_properties.getProperty(ResourceProperties.PROP_TOOL_DESCRIPTION);

		} // getDescription

		/** @return A list of categories (String) for this tool.*/
		public List getCategories()
		{
			return m_properties.getPropertyList(ResourceProperties.PROP_TOOL_CATEGORY);

		} // getCategories

		/**
		* Access the set of configuration values that are the defaults for this tool.
		* @return The ResourceProperties that hold the set of configuration values that are the defaults for this tool.
		*/
		public ResourceProperties getDefaultConfiguration()
		{
			return m_defaultConfiguration;

		} // getDefaultConfiguration

		/**
		* Compare this object with the specified object for order.
		* @return A negative integer, zero, or a positive integer as this object is
		* less than, equal to, or greater than the specified object.
		*/
		public int compareTo(Object obj)
		{
			if (!(obj instanceof ToolRegistration))
				throw new ClassCastException();

			// if the object are the same, say so
			if (obj == this)
				return 0;

			// sort based on title
			int compare = getTitle().compareTo(((ToolRegistration) obj).getTitle());

			return compare;

		} // compareTo

	} // BaseToolRegistration

} // BasicConfigurationService

/**********************************************************************************
*
* $Header: /cvs/sakai/framework-component/src/java/org/sakaiproject/component/framework/config/BasicConfigurationService.java,v 1.42 2005/02/07 16:51:49 zqian.umich.edu Exp $
*
**********************************************************************************/
