/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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

import java.io.Serializable;

import org.sakaiproject.service.legacy.authzGroup.AuthzGroup;
import org.sakaiproject.service.legacy.entity.Edit;

/**
 * <p>
 * A Site Group is a way to divide up a Site into separate units, each with its own authorization group and descriptive information.
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public interface Group extends Edit, Serializable, AuthzGroup
{
	/** @return a human readable short title of this group. */
	String getTitle();

	/** @return a text describing the group. */
	String getDescription();

	/**
	 * Access the site in which this group lives.
	 * 
	 * @return the site in which this group lives.
	 */
	public Site getContainingSite();

	/**
	 * Set the human readable short title of this group.
	 * 
	 * @param title
	 *        The new title.
	 */
	void setTitle(String title);

	/**
	 * Set the text describing this group.
	 * 
	 * @param description
	 *        The new description.
	 */
	void setDescription(String description);
}