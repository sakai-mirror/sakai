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
import java.util.List;

import org.sakaiproject.service.legacy.resource.Edit;

/**
 * <p>
 * SiteEdit is a mutable Site object.
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public interface SiteEdit extends Site, Edit
{
	/**
	 * Set the human readable Title of the site.
	 * 
	 * @param title
	 *        the new title.
	 */
	void setTitle(String title);

	/**
	 * Set the url of an icon for the site.
	 * 
	 * @param url
	 *        The new icon's url.
	 */
	void setIconUrl(String url);

	/**
	 * Set the url for information about the site.
	 * 
	 * @param url
	 *        The new information url.
	 */
	void setInfoUrl(String url);

	/**
	 * Set the joinable status of the site.
	 * 
	 * @param joinable
	 *        represents whether the site is joinable (true) or not (false).
	 */
	void setJoinable(boolean joinable);

	/**
	 * Set the joiner role for a site.
	 * 
	 * @param role
	 *        the joiner role for a site.
	 */
	void setJoinerRole(String role);

	/**
	 * Set the short Description of the site. Used to give a short text description of the site.
	 * 
	 * @param description
	 *        The new short description.
	 */
	void setShortDescription(String description);

	/**
	 * Set the Description of the site. Used to give a longer text description of the site.
	 * 
	 * @param description
	 *        The new description.
	 */
	void setDescription(String description);

	/**
	 * Set the published state of this site.
	 * 
	 * @param status
	 *        The published state of the site.
	 */
	void setPublished(boolean published);

	/**
	 * Set the skin to use for this site.
	 * 
	 * @param skin
	 *        The skin to use for this site.
	 */
	void setSkin(String skin);

	/**
	 * Create a new site page and add it to this site.
	 * 
	 * @return The SitePageEdit object for the new site page.
	 */
	SitePageEdit addPage();

	/** @return the List (SitePageEdit) of editable Site Pages. */
	List getPageEdits();

	/**
	 * Access the SitePage that has this id, if one is defined, editable, or null if not found.
	 * 
	 * @param id
	 *        The id of the SitePage.
	 * @return The SitePage that has this id, if one is defined, or null if not found.
	 */
	SitePageEdit getPageEdit(String id);

	/**
	 * Remove a site page from this site.
	 * 
	 * @param page
	 *        The SitePage to remove.
	 */
	void removePage(SitePageEdit page);

	/**
	 * Generate a new set of pages and tools that have new, unique ids. Good if the site had non-unique-system-wide ids for pages and tools. The Site Id does not change.
	 */
	void regenerateIds();

	/**
	 * Set the site type.
	 * 
	 * @param type
	 *        The site type.
	 */
	void setType(String type);

	/**
	 * Set the site view.
	 * 
	 * @param pubView
	 *        The site view setting.
	 */
	void setPubView(boolean pubView);

	/**
	 * Add a new section. The Id is generated, the rest of the fields can be set using calls to the Section object returned.
	 */
	Section addSection();

	/**
	 * Remove this section from the sections for this site.
	 * 
	 * @param section
	 *        The section to remove.
	 */
	void removeSection(Section section);
}
