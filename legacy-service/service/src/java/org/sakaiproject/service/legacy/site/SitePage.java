/**********************************************************************************
* $URL$
* $Id$
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
import java.util.ResourceBundle;

import org.sakaiproject.service.legacy.resource.Resource;

/**
* <p>A Site Page is grouping of tools in a Site.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public interface SitePage
	extends Resource
{
	/** Layout value for a single column layout. */
	public static final int LAYOUT_SINGLE_COL = 0;

	/** Layout value for a double column layout. */
	public static final int LAYOUT_DOUBLE_COL = 1;

	/** Layout value for a popup window. */
	public static final int LAYOUT_POPUP_WIN = 2;

	/** The layouts in human readable form (localized) */
   static ResourceBundle rb = ResourceBundle.getBundle("admin");
	public static final String LAYOUT_NAMES[] = {
           rb.getString("sitpag.lay_sngl"),
           rb.getString("sitpag.lay_dbl"),
           rb.getString("sitpag.lay_pop") };
	
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



