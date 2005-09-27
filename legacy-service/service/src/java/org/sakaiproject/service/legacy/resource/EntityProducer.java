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
package org.sakaiproject.service.legacy.resource;

// imports
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <p>
 * Services which implement EntityProducer declare themselves as producers of Sakai entities.
 * </p>
 */
public interface EntityProducer
{
	/**
	 * @return a short string identifying the resources kept here, good for a file name or label.
	 */
	String getLabel();

	/**
	 * @return true if the serice wants to be part of archive / merge, false if not.
	 */
	boolean willArchiveMerge();

	/**
	 * @return true if the serice wants to be part of import, false if not.
	 */
	boolean willImport();

	/**
	 * Archive the resources for the given site.
	 * 
	 * @param siteId
	 *        the id of the site.
	 * @param doc
	 *        The document to contain the xml.
	 * @param stack
	 *        The stack of elements, the top of which will be the containing element of the "service.name" element.
	 * @param archivePath
	 *        The path to the folder where we are writing auxilary files.
	 * @param attachments
	 *        A list of attachments - add to this if any attachments need to be included in the archive.
	 * @return A log of status messages from the archive.
	 */
	String archive(String siteId, Document doc, Stack stack, String archivePath, List attachments);

	/**
	 * Merge the resources from the archive into the given site.
	 * 
	 * @param siteId
	 *        The id of the site getting imported into.
	 * @param root
	 *        The XML DOM tree of content to merge.
	 * @param archviePath
	 *        The path to the folder where we are reading auxilary files.
	 * @param fromSite
	 *        The site id from which these items were archived.
	 * @param attachmentNames
	 *        A map of old attachment name (as found in the DOM) to new attachment name.
	 * @return A log of status messages from the merge.
	 */
	String merge(String siteId, Element root, String archivePath, String fromSiteId, Map attachmentNames,
			Map userIdTrans, Set userListAllowImport);

	/**
	 * import Entites from the source context into the destination context
	 * 
	 * @param fromContext
	 *        The source context
	 * @param toContext
	 *        The destination context
	 * @param ids
	 *        when null, all entities will be imported; otherwise, only entities with those ids will be imported
	 */
	void importEntities(String fromContext, String toContext, List ids);
	
	/**
	 * If the service recognizes the reference as its own, parse it and fill in the Reference
	 * @param reference The reference string to examine.
	 * @param ref The Reference object to set with the results of the parse from a recognized reference.
	 * @return true if the reference belonged to the service, false if not.
	 */
	boolean parseEntityReference(String reference, Reference ref);
	
	/**
	 * Create an entity description for the entity referenced - the entity will belong to the service.
	 * @param ref The entity reference.
	 * @return The entity description, or null if one cannot be made.
	 */
	String getEntityDescription(Reference ref);
	
	/**
	 * Access the resource properties for the referenced entity - the entity will belong to the service.
	 * @param ref The entity reference.
	 * @return The ResourceProperties object for the entity, or null if it has none.
	 */
	ResourceProperties getEntityResourceProperties(Reference ref);
	
	/**
	 * Access the referenced Entity - the entity will belong to the service.
	 * @param ref The entity reference.
	 * @return The Entity, or null if not found.
	 */
	Entity getEntity(Reference ref);

	/**
	 * Access a URL for the referenced entity - the entity will belong to the service.
	 * @param ref The entity reference.
	 * @return The entity's URL, or null if it does not have one.
	 */
	String getEntityUrl(Reference ref);
	
	/**
	 * Access a collection of realm ids for security on the for the referenced entity - the entity will belong to the service.
	 * @param ref The entity reference.
	 * @return The entity's collection of security realm ids, or null if this cannot be done.
	 */
	Collection getEntityRealms(Reference ref);
}
