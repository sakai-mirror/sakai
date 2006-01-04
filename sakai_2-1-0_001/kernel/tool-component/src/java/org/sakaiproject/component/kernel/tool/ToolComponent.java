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

package org.sakaiproject.component.kernel.tool;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.api.kernel.thread_local.ThreadLocalManager;
import org.sakaiproject.api.kernel.tool.Placement;
import org.sakaiproject.api.kernel.tool.Tool;
import org.sakaiproject.api.kernel.tool.ToolManager;
import org.sakaiproject.util.xml.Xml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentReaderHashMap;

/**
 * <p>
 * ToolComponent is the standard implementation of the Sakai Tool API.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class ToolComponent implements ToolManager
{
	/** Our log (commons). */
	private static Log M_log = LogFactory.getLog(ToolComponent.class);

	/** Key in the ThreadLocalManager for binding our current placement. */
	protected final static String CURRENT_PLACEMENT = "org.sakaiproject.api.kernel.tool.current.placement";

	/** Key in the ThreadLocalManager for binding our current tool. */
	protected final static String CURRENT_TOOL = "org.sakaiproject.api.kernel.tool.current.tool";

	/** The registered tools. */
	protected Map m_tools = new ConcurrentReaderHashMap();

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Dependencies and their setter methods
	 *********************************************************************************************************************************************************************************************************************************************************/

	/** Dependency: the current manager. */
	protected ThreadLocalManager m_threadLocalManager = null;

	/**
	 * Dependency - set the current manager.
	 * 
	 * @param value
	 *        The current manager.
	 */
	public void setThreadLocalManager(ThreadLocalManager manager)
	{
		m_threadLocalManager = manager;
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Init and Destroy
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		M_log.info("init()");
	}

	/**
	 * Final cleanup.
	 */
	public void destroy()
	{
		M_log.info("destroy()");
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Work interface
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * @inheritDoc
	 */
	public Set findTools(Set categories, Set keywords)
	{
		Set rv = new HashSet();

		for (Iterator i = m_tools.values().iterator(); i.hasNext();)
		{
			Tool tool = (Tool) i.next();
			if (matchCriteria(categories, tool.getCategories()) && matchCriteria(keywords, tool.getKeywords()))
			{
				rv.add(tool);
			}
		}

		return rv;
	}

	/**
	 * @inheritDoc
	 */
	public Placement getCurrentPlacement()
	{
		return (Placement) m_threadLocalManager.get(CURRENT_PLACEMENT);
	}

	/**
	 * @inheritDoc
	 */
	public Tool getCurrentTool()
	{
		return (Tool) m_threadLocalManager.get(CURRENT_TOOL);
	}

	/**
	 * @inheritDoc
	 */
	public Tool getTool(String id)
	{
		return (Tool) m_tools.get(id);
	}

	/**
	 * Check the target values for a match in the criteria. If criteria is empty or null, the target is a match.
	 * 
	 * @param criteria
	 *        The set of String values that is the criteria - any one in the target is a match
	 * @param target
	 *        The set of String values to check against the criteria.
	 * @return true if the target meets the criteria, false if not.
	 */
	protected boolean matchCriteria(Set criteria, Set target)
	{
		if ((criteria == null) || (criteria.isEmpty())) return true;

		for (Iterator i = target.iterator(); i.hasNext();)
		{
			String t = (String) i.next();
			if (criteria.contains(t)) return true;
		}
		return false;
	}

	/**
	 * @inheritDoc
	 */
	public void register(Document toolXml)
	{
		Element root = toolXml.getDocumentElement();
		if (!root.getTagName().equals("registration"))
		{
			M_log.info("register: invalid root element (expecting \"registration\"): " + root.getTagName());
			return;
		}

		// read the children nodes (tools)
		NodeList rootNodes = root.getChildNodes();
		final int rootNodesLength = rootNodes.getLength();
		for (int i = 0; i < rootNodesLength; i++)
		{
			Node rootNode = rootNodes.item(i);
			if (rootNode.getNodeType() != Node.ELEMENT_NODE) continue;
			Element rootElement = (Element) rootNode;

			// for tool
			if (rootElement.getTagName().equals("tool"))
			{
				org.sakaiproject.util.Tool tool = new org.sakaiproject.util.Tool();

				tool.setId(rootElement.getAttribute("id").trim());
				tool.setTitle(rootElement.getAttribute("title").trim());
				tool.setDescription(rootElement.getAttribute("description").trim());
				
				if ("tool".equals(rootElement.getAttribute("accessSecurity")))
				{
					tool.setAccessSecurity(Tool.AccessSecurity.TOOL);
				}
				else
				{
					tool.setAccessSecurity(Tool.AccessSecurity.PORTAL);
				}

				// collect values for these collections
				Properties finalConfig = new Properties();
				Properties mutableConfig = new Properties();
				Set categories = new HashSet();
				Set keywords = new HashSet();

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
						String name = kidElement.getAttribute("name").trim();
						String value = kidElement.getAttribute("value").trim();
						String type = kidElement.getAttribute("type").trim();
						if (name.length() > 0)
						{
							if ("final".equals(type))
							{
								finalConfig.put(name, value);
							}
							else
							{
								mutableConfig.put(name, value);
							}
						}
					}

					// for category
					if (kidElement.getTagName().equals("category"))
					{
						String name = kidElement.getAttribute("name").trim();
						if (name.length() > 0)
						{
							categories.add(name);
						}
					}

					// for keyword
					if (kidElement.getTagName().equals("keyword"))
					{
						String name = kidElement.getAttribute("name").trim();
						if (name.length() > 0)
						{
							keywords.add(name);
						}
					}
				}

				// set the tool's collected values
				tool.setRegisteredConfig(finalConfig, mutableConfig);
				tool.setCategories(categories);
				tool.setKeywords(keywords);

				register(tool);
			}
		}
	}

	/**
	 * @inheritDoc
	 */
	public void register(File toolXmlFile)
	{
		String path = toolXmlFile.getAbsolutePath();
		if (!path.endsWith(".xml"))
		{
			M_log.info("register: skiping non .xml file: " + path);
			return;
		}

		M_log.info("register: file: " + path);

		Document doc = Xml.readDocument(path);
		register(doc);
	}

	/**
	 * @inheritDoc
	 */
	public void register(InputStream toolXmlStream)
	{
		Document doc = Xml.readDocumentFromStream(toolXmlStream);
		try
		{
			toolXmlStream.close();
		}
		catch (Exception e)
		{
		}

		register(doc);
	}

	/**
	 * @inheritDoc
	 */
	public void register(Tool tool)
	{
		m_tools.put(tool.getId(), tool);
	}

	/**
	 * Establish the Tool associated with the current request / thread
	 * 
	 * @param Tool
	 *        The current Tool, or null if there is none.
	 */
	protected void setCurrentPlacement(Placement placement)
	{
		m_threadLocalManager.set(CURRENT_PLACEMENT, placement);
	}

	/**
	 * Establish the Tool associated with the current request / thread
	 * 
	 * @param Tool
	 *        The current Tool, or null if there is none.
	 */
	protected void setCurrentTool(Tool tool)
	{
		m_threadLocalManager.set(CURRENT_TOOL, tool);
	}
}



