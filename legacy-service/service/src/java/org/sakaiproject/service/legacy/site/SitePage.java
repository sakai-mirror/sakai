/**********************************************************************************
 * $URL$
 * $Id$
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
package org.sakaiproject.service.legacy.site;

// imports
import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;

import org.sakaiproject.api.kernel.tool.Tool;
import org.sakaiproject.service.legacy.entity.Edit;

/**
 * <p>
 * A Site Page is grouping of tools in a Site.
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public interface SitePage extends Edit, Serializable
{
	/** Layout value for a single column layout. */
	public static final int LAYOUT_SINGLE_COL = 0;

	/** Layout value for a double column layout. */
	public static final int LAYOUT_DOUBLE_COL = 1;

	/** The layouts in human readable form (localized) */
	static ResourceBundle rb = ResourceBundle.getBundle("admin");

	public static final String LAYOUT_NAMES[] = { rb.getString("sitpag.lay_sngl"), rb.getString("sitpag.lay_dbl") };

	/** @return The human readable Title of this SitePage. */
	public String getTitle();

	/** @return the layout for this page. */
	public int getLayout();

	/** @return the layout title for this page. */
	public String getLayoutTitle();

	/** @return The List (ToolConfiguration) of tools on this page. */
	public List getTools();

	/** @return The List (ToolConfiguration) of tools on this column (0 based) of this page. */
	public List getTools(int col);

	/** @return the skin to use for this page. */
	public String getSkin();

	/** @return the site id for this page. */
	public String getSiteId();

	/** @return true if page should open in new window. */
	public boolean isPopUp();

	/**
	 * Access a tool on this page by id.
	 * 
	 * @param id
	 *        The tool id.
	 * @return The tool on this page with this id, or null if not found.
	 */
	public ToolConfiguration getTool(String id);

	/**
	 * Access the site in which this page lives.
	 * 
	 * @return the site in which this page lives.
	 */
	public Site getContainingSite();

	/**
	 * Set the display title of this page.
	 * 
	 * @param title
	 *        The new title.
	 */
	public void setTitle(String title);

	/**
	 * Set the layout for this page.
	 * 
	 * @param layout
	 *        The new layout.
	 */
	public void setLayout(int layout);

	/**
	 * Add a new tool to the page.
	 * 
	 * @return the ToolConfigurationEdit object for the new tool.
	 */
	public ToolConfiguration addTool();

	/**
	 * Add a new tool to the page, initialized to the tool registration information provided.
	 * 
	 * @param reg
	 *        The tool registration information used to initialize the tool.
	 * @return the ToolConfigurationEdit object for the new tool.
	 */
	public ToolConfiguration addTool(Tool reg);

	/**
	 * Remove a tool from this page.
	 * 
	 * @param tool
	 *        The tool to remove.
	 */
	public void removeTool(ToolConfiguration tool);

	/**
	 * Move this page one step towards the start of the order of pages in this site.
	 */
	public void moveUp();

	/**
	 * Move this page one step towards the end of the order of pages in this site.
	 */
	public void moveDown();
}

