/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/site/SiteEdit.java,v 1.7 2004/11/04 00:34:13 ggolden.umich.edu Exp $
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

import org.sakaiproject.service.legacy.resource.Edit;

/**
* <p>Site is an interface for the CHEF site management module.
* It represents a Site in the CHEF interface.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.7 $
*/
public interface SiteEdit
	extends Site, Edit
{
	/**
	* Set the human readable Title of the site.
	* @param title the new title.
	*/
	public void setTitle(String title);
	
	/**
	* Set the url of an icon for the site.
	* @param url The new icon's url.
	*/
	public void setIconUrl(String url);
	
	/**
	* Set the url for information about the site.
	* @param url The new information url.
	*/
	public void setInfoUrl(String url);
	
	/**
	* Set the joinable status of the site.
	* @param joinable represents whether the site is joinable (true) or not (false).
	*/
	public void setJoinable(boolean joinable);

	/**
	* Set the joiner role for a site.
	* @param role the joiner role for a site.
	*/
	public void setJoinerRole(String role);

	/**
	* Set the short Description of the site. Used to give a short text description of the site.
	* @param description The new short description.
	*/
	public void setShortDescription(String description);

	/**
	* Set the Description of the site. Used to give a longer text description of the site.
	* @param description The new description.
	*/
	public void setDescription(String description);

	/**
	* Set the published state of this site.
	* @param status The published state of the site.
	*/
	public void setPublished(boolean published);

	/**
	* Set the skin to use for this site.
	* @param skin The skin to use for this site.
	*/
	public void setSkin(String skin);

	/**
	* Create a new site page and add it to this site.
	* @return The SitePageEdit object for the new site page.
	*/
	public SitePageEdit addPage();

	/** @return the List (SitePageEdit) of editable Site Pages. */
	public List getPageEdits();

	/**
	* Access the SitePage that has this id, if one is defined, editable, or null if not found.
	* @param id The id of the SitePage.
	* @return The SitePage that has this id, if one is defined, or null if not found.
	*/
	public SitePageEdit getPageEdit(String id);

	/**
	* Remove a site page from this site.
	* @param page The SitePage to remove.
	*/
	public void removePage(SitePageEdit page);

	/**
	* Access the ToolConfiguration that has this id, if one is defined, else return null.
	* The tool may be on any SitePage in the site.
	* The configuration may be edited.
	* @param id The id of the tool.
	* @return The ToolConfiguration that has this id, if one is defined, else return null.
	*/
	public ToolConfigurationEdit getToolEdit(String id);

	/**
	 * Generate a new set of pages and tools that have new, unique ids.
	 * Good if the site had non-unique-system-wide ids for pages and tools.
	 * The Site Id does not change.
	 */
	public void regenerateIds();

	/**
	 * Set the site type.
	 * @param type The site type.
	 */
	public void setType(String type);

	/**
	 * Set the site public view.
	 * @param pubView The site public view setting.
	 */
	public void setPubView(boolean pubView);

}	// SiteEdit

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/site/SiteEdit.java,v 1.7 2004/11/04 00:34:13 ggolden.umich.edu Exp $
*
**********************************************************************************/
