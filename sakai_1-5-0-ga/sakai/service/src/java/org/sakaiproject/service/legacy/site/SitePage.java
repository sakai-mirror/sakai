/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/site/SitePage.java,v 1.6 2004/11/24 01:43:44 ggolden.umich.edu Exp $
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

import org.sakaiproject.service.legacy.resource.Resource;

/**
* <p>A Site Page is grouping of tools in a Site.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.6 $
*/
public interface SitePage
	extends Resource
{
	/** Layout value for a single column layout. */
	public static final int LAYOUT_SINGLE_COL = 0;

	/** Layout value for a double column layout. */
	public static final int LAYOUT_DOUBLE_COL = 1;

	/** The layouts in human readable form. */
	public static final String LAYOUT_NAMES[] = {"Single Column Layout", "Double Column Layout"};
	
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

	/**
	* Access a tool on this page by id.
	* @param id The tool id.
	* @return The tool on this page with this id, or null if not found.
	*/
	public ToolConfiguration getTool(String id);

	/**
	* Access the site in which this page lives.
	* @return the site in which this page lives.
	*/
	public Site getContainingSite();

}	// SitePage

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/site/SitePage.java,v 1.6 2004/11/24 01:43:44 ggolden.umich.edu Exp $
*
**********************************************************************************/
