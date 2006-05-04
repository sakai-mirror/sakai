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

package org.sakaiproject.service.framework.memory;

import org.sakaiproject.service.legacy.site.Group;
import org.sakaiproject.service.legacy.site.SitePage;
import org.sakaiproject.service.legacy.site.ToolConfiguration;

/**
 * <p>
 * SiteCache is a cache that holds Site objects, and gives access to the cached site's ToolConfigurations by their id.
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public interface SiteCache extends Cache
{
	/**
	 * Access the tool that is part of a cached site, by tool Id.
	 * 
	 * @param toolId
	 *        The tool's id.
	 * @return The ToolConfiguration that has this id, from a cached site.
	 */
	ToolConfiguration getTool(String toolId);

	/**
	 * Access the page that is part of a cached site, by page Id.
	 * 
	 * @param pageId
	 *        The page's id.
	 * @return The SitePage that has this id, from a cached site.
	 */
	SitePage getPage(String pageId);

	/**
	 * Access the group that is part of a cached site, by group Id.
	 * 
	 * @param id
	 *        The group id.
	 * @return The Group that has this id, from a cached site.
	 */
	Group getGroup(String id);
}