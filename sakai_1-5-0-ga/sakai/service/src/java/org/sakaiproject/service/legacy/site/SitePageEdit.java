/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/site/SitePageEdit.java,v 1.4 2004/06/22 03:14:51 ggolden Exp $
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
package org.sakaiproject.service.legacy.site;

// imports
import java.util.List;

import org.sakaiproject.service.framework.config.ToolRegistration;
import org.sakaiproject.service.legacy.resource.Edit;

/**
* <p>A SitePageEdit is an editable SitePage.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.4 $
*/
public interface SitePageEdit
	extends SitePage, Edit
{
	/** 
	* Set the display title of this page.
	* @param title The new title.
	*/
	public void setTitle(String title);

	/**
	* Set the layout for this page.
	* @param layout The new layout.
	*/
	public void setLayout(int layout);

	/**
	* Add a new tool to the page.
	* @return the ToolConfigurationEdit object for the new tool.
	*/
	public ToolConfigurationEdit addTool();

	/**
	* Add a new tool to the page, initialized to the tool registration information provided.
	* @param ref The tool registration information used to initialize the tool.
	* @return the ToolConfigurationEdit object for the new tool.
	*/
	public ToolConfigurationEdit addTool(ToolRegistration reg);

	/** @return The List (ToolConfigurationEdit) of tools on this page, editable. */
	public List getToolEdits();

	/**
	* Access a tool on this page by id, editable.
	* @param id The tool id.
	* @return The tool on this page with this id, or null if not found.
	*/
	public ToolConfigurationEdit getToolEdit(String id);

	/**
	* Remove a tool from this page.
	* @param tool The tool to remove.
	*/
	public void removeTool(ToolConfigurationEdit tool);

	/**
	* Move this page one step towards the start of the order of pages in this site.
	*/
	public void moveUp();

	/**
	* Move this page one step towards the end of the order of pages in this site.
	*/
	public void moveDown();

}	// SitePageEdit

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/site/SitePageEdit.java,v 1.4 2004/06/22 03:14:51 ggolden Exp $
*
**********************************************************************************/
