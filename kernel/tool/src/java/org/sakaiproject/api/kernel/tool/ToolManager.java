/**********************************************************************************
 *
 * $Header: /cvs/sakai2/kernel/tool/src/java/org/sakaiproject/api/kernel/tool/ToolManager.java,v 1.3 2005/04/18 14:23:33 ggolden.umich.edu Exp $
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
import java.util.Set;

import org.w3c.dom.Document;

/**
 * <p>
 * ToolManager holds registration of Tools available in this Sakai installation.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public interface ToolManager
{
	/**
	 * Add this tool to the registry.
	 * @param tool The Tool to register.
	 */
	void register(Tool tool);
	
	/**
	 * Add tools in this XML DOM to the registry, using the Tool XML schema.
	 * @param toolXml The parsed XML DOM in which tools to be added to the registry are to be found.
	 */
	void register(Document toolXml);
	
	/**
	 * Add tools in this file of Tool XML schema to the registry.
	 * @param toolXmlFile The file of Tool schema XML in which tools to be added to the registry are to be found.
	 */
	void register(File toolXmlFile);

	/**
	 * Add tools in this stream of Tool XML schema to the registry.
	 * @param toolXmlFile The file of Tool schema XML in which tools to be added to the registry are to be found.
	 */
	void register(InputStream toolXmlStream);

	/**
	 * Find a tool with this well known id in the registry.
	 * @param id The tool's well known id.
	 * @return The Tool object that has this id, or null if not found.
	 */
	Tool getTool(String id);

	/**
	 * Find a set of tools that meet the critieria.
	 * A tool must have a category in the categories criteria (unless it is empty or null) to be returned.
	 * A tool must have a keyword in the keywords criteria (unless it is empty or null) to be returned.
	 * If both categories and keywords criteria are specified, the tool must meet both criteria to be returned.
	 * If neither criteria are specified, all registered tools are returned.
	 * @param categories A Set (String) of category values; if null or empty no category criteria is specified.
	 * @param keywords A Set (String) of keyword values; if null or empty no keyword criteria is specified.
	 * @return A Set (Tool) of Tool objects that meet the criteria, or an empty set if none found.
	 */
	Set findTools(Set categories, Set keywords);

	/**
	 * Access the Tool associated with the current request / thread
	 * @return The current Tool, or null if there is none.
	 */
	Tool getCurrentTool();
	
	/**
	 * Access the Tool Placement associated with the current request / thread
	 * @return The current Tool Placement, or null if there is none.
	 */
	Placement getCurrentPlacement();

	// TODO: unregister...
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/kernel/tool/src/java/org/sakaiproject/api/kernel/tool/ToolManager.java,v 1.3 2005/04/18 14:23:33 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
