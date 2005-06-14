/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/resource/ResourceService.java,v 1.10 2004/11/09 14:33:19 zqian.umich.edu Exp $
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
package org.sakaiproject.service.legacy.resource;

// imports
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <p>Services which implement ResourceService declare themselves as keepers
 * of site related resources.</p>
 * <p>This interface is not a full service interface.</p>
 * 
 * @author University of Michigan, CHEF Software Development Team
 * @version $Revision: 1.10 $
 */
public interface ResourceService
{
	/**
	 * @return a short string identifying the resources kept here, good for a file name or label.
	 */
	public String getLabel();

	/**
	 * Archive the resources for the given site.
	 * @param siteId the id of the site.
	 * @param doc The document to contain the xml.
	 * @param stack The stack of elements, the top of which will be the containing element of the "service.name" element.
	 * @param archivePath The path to the folder where we are writing auxilary files.
	 * @param attachments A vector of attachments - add to this if any attachments need to be included in the archive.
	 * @return A log of status messages from the archive.
	 */
	public String archive(String siteId, Document doc, Stack stack, String archivePath, ReferenceVector attachments);

	/**
	 * Merge the resources from the archive into the given site.
	 * @param siteId The id of the site getting imported into.
	 * @param root The XML DOM tree of content to merge.
	 * @param archviePath The path to the folder where we are reading auxilary files.
	 * @param fromSite The site id from which these items were archived.
	 * @param attachmentNames A map of old attachment name (as found in the DOM) to new attachment name.
	 * @return A log of status messages from the merge.
	 */
	public String merge(String siteId, Element root, String archivePath, String fromSiteId, Map attachmentNames, HashMap userIdTrans, Set userListAllowImport);

	/**
	* import tool contents from the source context into the destination context
	* @param fromContext The source context
	* @param toContext The destination context
	* @param resourceIds when null, all resources will be imported; otherwise, only resources with those ids will be imported
	*/
	public void importResources(String fromContext, String toContext, List resourceIds);
}

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/resource/ResourceService.java,v 1.10 2004/11/09 14:33:19 zqian.umich.edu Exp $
*
**********************************************************************************/
