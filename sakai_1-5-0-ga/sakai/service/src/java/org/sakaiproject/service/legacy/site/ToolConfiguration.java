/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/site/ToolConfiguration.java,v 1.6 2004/11/04 00:34:13 ggolden.umich.edu Exp $
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
import org.sakaiproject.service.legacy.resource.Resource;

/**
* <p>ToolConfiguration is a specific tool defined for inclusion in a Site.  It has it's own unique id,
* the id of the CHEF tool to place, and parameters to configure this tool.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.6 $
*/
public interface ToolConfiguration
	extends Resource
{
	/** @return The id of the CHEF Tool that this configuration represents. */
	public String getToolId();

	/** @return The human readable title. */
	public String getTitle();

	/** @return the layout hints for this tool. */
	public String getLayoutHints();

	/** If the layout hints are a row,col format, return the two numbers, else return null. */
	public int[] parseLayoutHints();

	/** @return the skin to use for this tool. */
	public String getSkin();

	/** @return the page id for this tool. */
	public String getPageId();

	/** @return the site id for this tool. */
	public String getSiteId();

	/**
	* Access the SitePage in which this tool configuration lives.
	* @return the SitePage in which this tool configuration lives.
	*/
	public SitePage getContainingPage();

} // ToolConfiguration

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/site/ToolConfiguration.java,v 1.6 2004/11/04 00:34:13 ggolden.umich.edu Exp $
*
**********************************************************************************/
