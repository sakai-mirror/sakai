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
package org.sakaiproject.service.legacy.archive;

// imports

/**
* <p>GenericArchiveService takes care of exporting and importing resources.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public interface ArchiveService
{
	/** This string can be used to find the service in the service manager. */
	public static final String SERVICE_NAME = ArchiveService.class.getName();

	/** A tag for the input system. */
	public static final String FROM_CT = "CT";
	public static final String FROM_WT = "WT";
	public static final String FROM_SAKAI = "Sakai 1.0";
	
	// the list of boolean tells if the imported item needs to be set as draft
	public static final boolean SAKAI_msg_draft_import = true;
	public static final boolean SAKAI_assign_draft_import = true;
	public static final boolean SAKAI_rsc_draft_import = false;
	public static final boolean SAKAI_schedule_draft_import = true;
	
	/**
	* Create an archive for the resources of a site.
	* @param siteId The id of the site to archive.
	* @return A log of messages from the archive.
	*/
	public String archive(String siteId);

	/**
	  * Read in an archived set of resources, and merge the entries into the specified site, and set site creator name
	  * @param archiveUrl The archive xml file Url.
	  * @param siteId The id of the site to merge the content into.
	  * @param siteCreatorId the site creator Id
	  * @return A log of messages from the merge.
	  */
	public String merge(String archiveUrl, String siteId, String siteCreatorId);

}	// GenericArchiveService



