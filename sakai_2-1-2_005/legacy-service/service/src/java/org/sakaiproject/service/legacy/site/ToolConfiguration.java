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
package org.sakaiproject.service.legacy.site;

// imports
import java.io.Serializable;

import org.sakaiproject.api.kernel.tool.Placement;

/**
 * <p>
 * ToolConfiguration is a the placement of a tool on a site page; a placement with layout information.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public interface ToolConfiguration extends Placement, Serializable
{
	/**
	 * @return the layout hints for this tool.
	 */
	public String getLayoutHints();

	/**
	 * Set the layout hints.
	 * 
	 * @param hints
	 *        The layout hints.
	 */
	public void setLayoutHints(String hints);

	/**
	 * If the layout hints are a row,col format, return the two numbers, else return null.
	 */
	public int[] parseLayoutHints();

	/**
	 * @return the skin to use for this tool.
	 */
	public String getSkin();

	/**
	 * @return the page id for this tool.
	 */
	public String getPageId();

	/**
	 * @return the site id for this tool.
	 */
	public String getSiteId();

	/**
	 * Access the SitePage in which this tool configuration lives.
	 * 
	 * @return the SitePage in which this tool configuration lives.
	 */
	public SitePage getContainingPage();

	/**
	 * Move this tool one step towards the start of the order of tools in this page.
	 */
	public void moveUp();

	/**
	 * Move this tool one step towards the end of the order of tools in this page.
	 */
	public void moveDown();
	
	/**
	 * Access the tool's order in the page.
	 * @return the tool's order in the page.
	 */
	public int getPageOrder();
}



