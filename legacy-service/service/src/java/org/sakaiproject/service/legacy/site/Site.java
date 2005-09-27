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

//imports
import java.util.List;

import org.sakaiproject.service.legacy.resource.Entity;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.user.User;

/**
* <p>Site is the object that knows the information, tools and layouts for a CHEF Site.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public interface Site
	extends Entity, Comparable
{
	// TODO: move these to Resource
	
	/**
	 * @return the user who created this.
	 */
	public User getCreatedBy();

	/**
	 * @return the user who last modified this.
	 */
	public User getModifiedBy();

	/**
	 * @return the time created.
	 */
	public Time getCreatedTime();

	/**
	 * @return the time last modified.
	 */
	public Time getModifiedTime();
	
	// TODO:

	/** @return The human readable Title of the site. */
	public String getTitle();

	/** @return A short text Description of the site. */
	public String getShortDescription();

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

	/** @return the skin to use for this site. */
	public String getSkin();

	/** @return the List (SitePage) of Site Pages. */
	public List getPages();

	/**
	 * Make sure pages and tools are loaded, not lazy
	 */
	public void loadPagesTools();

	/**
	 * Return the pages ordered by the tool order constraint for this site's type (as tool category).
	 * @return The pages ordered by the tool order constraint for this site's type (as tool category).
	 */
	public List getOrderedPages();

	/** @return true if the site is published, false if not. */
	public boolean isPublished();

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

	/**
	 * Check if the site is marked for public viewing.
	 * @return True if the site is marked for public viewing, false if not
	 */
	public boolean isPubView();	

}	// Site



