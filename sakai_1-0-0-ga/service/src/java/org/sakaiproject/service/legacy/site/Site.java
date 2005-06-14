/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/site/Site.java,v 1.7 2004/08/11 20:21:16 ggolden.umich.edu Exp $
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

//imports
import java.util.List;

import org.sakaiproject.service.legacy.resource.Resource;

/**
* <p>Site is the object that knows the information, tools and layouts for a CHEF Site.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.7 $
*/
public interface Site
	extends Resource, Comparable
{
	/** Status value for a site defined, but not generally open to users yet. */
	public static final int SITE_STATUS_UNPUBLISHED = 1;

	/** Status value for a site defined and open for business. */
	public static final int SITE_STATUS_PUBLISHED = 2;

	/** @return The human readable Title of the site. */
	public String getTitle();

	/** @return A longer text Description of the site. */
	public String getDescription();

	/** @return The Site's icon URL. */
	public String getIconUrl();

	/** @return The Site's icon URL as a full URL. */
	public String getIconUrlFull();

	/** @return The Site's info display URL. */
	public String getInfoUrl();
	
	/** @return The Site's info display URL as a full URL. */
	public String getInfoUrlFull();

	/** @return true if this Site can be joined by anyone, false if not. */
	public boolean isJoinable();

	/** @return the role name given to users who join a joinable site. */
	public String getJoinerRole();

	/** @return the status of the site. */
	public int getStatus();

	/** @return the skin to use for this site. */
	public String getSkin();

	/** @return the List (SitePage) of Site Pages. */
	public List getPages();

	/**
	* Access the SitePage that has this id, if one is defined, else return null.
	* @param id The id of the SitePage.
	* @return The SitePage that has this id, if one is defined, else return null.
	*/
	public SitePage getPage(String id);

	/**
	* Access the ToolConfiguration that has this id, if one is defined, else return null.
	* The tool may be on any SitePage in the site.
	* @param id The id of the tool.
	* @return The ToolConfiguration that has this id, if one is defined, else return null.
	*/
	public ToolConfiguration getTool(String id);

	/**
	 * Access the site type.
	 * @return The site type.
	 */
	public String getType();

	/**
	 * Test if the site is of this type.
	 * It is if the param is null.
	 * @param type A String type to match, or a String[], List or Set of Strings, any of which can match.
	 * @return true if the site is of the type(s) specified, false if not.
	 */
	public boolean isType(Object type);

}	// Site

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/site/Site.java,v 1.7 2004/08/11 20:21:16 ggolden.umich.edu Exp $
*
**********************************************************************************/
