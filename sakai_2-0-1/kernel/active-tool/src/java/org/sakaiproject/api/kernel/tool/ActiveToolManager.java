/**********************************************************************************
 *
 * $Header: /cvs/sakai2/kernel/active-tool/src/java/org/sakaiproject/api/kernel/tool/ActiveToolManager.java,v 1.1 2005/03/10 03:49:40 ggolden.umich.edu Exp $
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

package org.sakaiproject.api.kernel.tool;

import java.io.File;
import java.io.InputStream;

import javax.servlet.ServletContext;

import org.w3c.dom.Document;

/**
 * <p>
 * Extension API for ToolManager that introduces Servlet API specific activity.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public interface ActiveToolManager extends ToolManager
{
	/**
	 * Add this tool to the registry.
	 * @param tool The Tool to register.
	 */
	void register(Tool tool, ServletContext config);
	
	/**
	 * Add tools in this XML DOM to the registry, using the Tool XML schema.
	 * @param toolXml The parsed XML DOM in which tools to be added to the registry are to be found.
	 */
	void register(Document toolXml, ServletContext config);
	
	/**
	 * Add tools in this file of Tool XML schema to the registry.
	 * @param toolXmlFile The file of Tool schema XML in which tools to be added to the registry are to be found.
	 */
	void register(File toolXmlFile, ServletContext config);

	/**
	 * Add tools in this stream of Tool XML schema to the registry.
	 * @param toolXmlFile The file of Tool schema XML in which tools to be added to the registry are to be found.
	 */
	void register(InputStream toolXmlStream, ServletContext config);

	/**
	 * Find a tool with this well known id in the registry.
	 * @param id The tool's well known id.
	 * @return The Tool object that has this id, or null if not found.
	 */
	ActiveTool getActiveTool(String id);
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/kernel/active-tool/src/java/org/sakaiproject/api/kernel/tool/ActiveToolManager.java,v 1.1 2005/03/10 03:49:40 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
