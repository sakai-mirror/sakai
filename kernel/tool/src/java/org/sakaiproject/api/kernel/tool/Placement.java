/**********************************************************************************
 *
 * $Header: /cvs/sakai2/kernel/tool/src/java/org/sakaiproject/api/kernel/tool/Placement.java,v 1.3 2005/04/25 20:09:53 ggolden.umich.edu Exp $
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

import java.util.Properties;

/**
 * <p>
 * Tool Placement models a particular tool places in a particular place within a Sakai navigation or portal location.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public interface Placement
{
	/**
	 * Access the configuration properties, combined from placement and registration, for the tool placement. Placement values override registration. Access is read only.
	 * 
	 * @return The read-only combined configuration properties for the tool.
	 */
	Properties getConfig();

	/**
	 * Access the placement context.
	 * 
	 * @return The context associated with this tool placement.
	 */
	String getContext();

	/**
	 * Get the tool placement id.
	 * 
	 * @return The tool placement id.
	 */
	String getId();

	/**
	 * Access the configuration properties for this tool placement - not including those from the tool registration.
	 * 
	 * @return The configuration properties for this tool placement - not including those from the tool registration.
	 */
	Properties getPlacementConfig();

	/**
	 * Access the tool placement title.
	 * 
	 * @return The tool placement title.
	 */
	String getTitle();

	/**
	 * Access the tool placeed with this placement.
	 * 
	 * @return The tool placed with this placement.
	 */
	Tool getTool();

	/**
	 * Set the title for this tool placement. Non-null values override the tool registration title.
	 * 
	 * @param title
	 *        The tool placement title.
	 */
	void setTitle(String title);

	/**
	 * Set the tool for this tool placement.
	 * 
	 * @param tool
	 *        The tool.
	 */
	void setTool(Tool tool);
	
	/**
	 * Save any changes to the placement.
	 */
	void save();
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/kernel/tool/src/java/org/sakaiproject/api/kernel/tool/Placement.java,v 1.3 2005/04/25 20:09:53 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
