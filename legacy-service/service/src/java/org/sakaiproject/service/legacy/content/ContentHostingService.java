/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/content/ContentHostingService.java,v 1.1 2005/05/12 15:45:35 ggolden.umich.edu Exp $
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
package org.sakaiproject.service.legacy.content;

// imports
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.InconsistentException;
import org.sakaiproject.exception.OverQuotaException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.service.legacy.resource.ReferenceVector;
import org.sakaiproject.service.legacy.resource.Resource;
import org.sakaiproject.service.legacy.resource.ResourceProperties;
import org.sakaiproject.service.legacy.resource.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.resource.ResourceService;
import org.w3c.dom.Document;

/**
* <p>ContentHostingService is the Interface for CHEF Content Hosting services.
* This service is based on WebDAV for terminology and capability, although it may be implemented using some
* other content management framework.</p>
* <p>The ContentHostingService manages shared content resources and collections.
* A resource is a file of some media type (MIME, such as image/gif, text/html, etc),
* with a resource id (a URI) and properties (name value pairs), as described in WebDAV.</p>
* <p>Resources are organized in collections.  A collection is a list of resource ids, and is itself a
* resource with a resource id.  In the spirit of WebDAV, a resource must be placed into a collection, and
* the containing collection must exist before the resource is added.</p>
* <p>Resource ids used in the API with the service are relative to the root "/" collection of the service.
* The full URL to a resource can be accessed from the service or from a resource object.</p>
* <p>Resources have any number of properties.  A property is defined by a name, which includes an XML namespace,
* and a value, which is any string.  Some properties are pre-defined (by the DAV: namespace), and are "live";
* the value is generated by the service.  Other properties are "dead", the values are maintained by the users.
* Properties from namespaces other than "DAV:" are accepted.</p>
* <p>TO DO:<ul>
* <li>add copy, move to collection and resource</li>
* <li>add lock</li>
* <li>add version control</li></ul></p>
* <p>The ContentHostingService can be asked:<ul>
* <li>access, create or delete a collection resource.</li>
* <li>access, create or delete a non-collection resource in a collection.</li>
* <li>access, add to or delete properties of a resource.</li></ul>
* See the methods in this API for details.</p>
* <p>Security is enforced and Usage Events are generated when there are these accesses:<ul>
* <li>create a resource: content.new</li>
* <li>read a resource: content.read</li>
* <li>update the contents/properties of a resource: content.revise</li>
* <li>removing a resource: content.delete</li></ul></p>
* <p>Services Used:<ul>
* <li>SecurityService</li>
* <li>EventTrackingService</li></ul></p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
* @see org.sakaiproject.service.legacy.resource.ResourceProperties
*/
public interface ContentHostingService
	extends ResourceService
{
	/** This string can be used to find the service in the service manager. */
	public static final String SERVICE_NAME = ContentHostingService.class.getName();

	/** This string starts the references to resources in this service. */
	public static final String REFERENCE_ROOT = Resource.SEPARATOR + "content";

	/** Name of the event when creating a resource. */
	public static final String EVENT_RESOURCE_ADD = "content.new";

	/** Name of the event when reading a resource. */
	public static final String EVENT_RESOURCE_READ = "content.read";

	/** Name of the event when writing a resource. */
	public static final String EVENT_RESOURCE_WRITE = "content.revise";

	/** Name of the event when removing a resource. */
	public static final String EVENT_RESOURCE_REMOVE = "content.delete";
	
	
	/**
	 * For a given id, return its UUID (creating it if it does not already exist)
	 */
	
	public String getUuid(String id);
	
	/**
	 * For a given UUID, attempt to lookup and return the corresponding id (URI)
	 */
	
	public String resolveUuid(String uuid);

	/**
	* check permissions for addCollection().
	* @param channelId The channel id.
	* @return true if the user is allowed to addCollection(id), false if not.
	*/
	public boolean allowAddCollection(String id);

	/**
	* Create a new collection with the given resource id.
	* @param id The id of the collection.
	* @param properties A ResourceProperties object with the properties to add to the new collection.
	* @exception IdUsedException if the id is already in use.
	* @exception IdInvalidException if the id is invalid.
	* @exception PermissionException if the user does not have permission to add a collection, or add a member to a collection.
	* @exception InconsistentException if the containing collection does not exist.
	* @return a new ContentCollection object.
	*/
	public ContentCollection addCollection(String id, ResourceProperties properties)
		throws IdUsedException, IdInvalidException, PermissionException, InconsistentException;

	/**
	* Create a new collection with the given resource id, locked for update.
	* Must commitCollection() to make official, or cancelCollection() when done!
	* @param id The id of the collection.
	* @exception IdUsedException if the id is already in use.
	* @exception IdInvalidException if the id is invalid.
	* @exception PermissionException if the user does not have permission to add a collection, or add a member to a collection.
	* @exception InconsistentException if the containing collection does not exist.
	* @return a new ContentCollection object.
	*/
	public ContentCollectionEdit addCollection(String id)
		throws IdUsedException, IdInvalidException, PermissionException, InconsistentException;

	/**
	* Check permissions for getCollection().
	* @param id The id of the collection.
	* @return true if the user is allowed to getCollection(id), false if not.
	*/
	public boolean allowGetCollection(String id);

	/**
	* Check access to the collection with this local resource id.
	* @param id The id of the collection.
	* @exception IdUnusedException if the id does not exist.
	* @exception TypeException if the resource exists but is not a collection.
	* @exception PermissionException if the user does not have permissions to see this collection (or read through containing collections).
	*/
	public void checkCollection(String id)
		throws IdUnusedException, TypeException, PermissionException;

	/**
	* Access the collection with this local resource id.
	* The collection internal members and properties are accessible from the returned Collection object.
	* @param id The id of the collection.
	* @exception IdUnusedException if the id does not exist.
	* @exception TypeException if the resource exists but is not a collection.
	* @exception PermissionException if the user does not have permissions to see this collection (or read through containing collections).
	* @return The ContentCollection object found.
	*/
	public ContentCollection getCollection(String id)
		throws IdUnusedException, TypeException, PermissionException;

	/**
	* Count the number of (recursive) children for a given id.
	* examples: With a nested collection structure exactly like this: /a /a/b /a/b/1 /a/b/2 
	*   getCollectionSize(/a) returns 3 (due to these three children: /a/b /a/b/1 /a/b/2)
	*   getCollectionSize(/a/b) returns 2 (due to these two children: /a/b/1 /a/b/2)
	*   getCollectionSize(/a/b/1) returns 0 (nothing falls below this collection)
	* @param id The id of the collection.
	* @exception IdUnusedException if the id does not exist.
	* @exception TypeException if the resource exists but is not a collection.
	* @exception PermissionException if the user does not have permissions to see this collection (or read through containing collections).
	* @return The number of internal members
	*/
	public int getCollectionSize(String id)
		throws IdUnusedException, TypeException, PermissionException;

	
	/**
	* Access a List of all the ContentResource objects in this path (and below)
	* which the current user has access.
	* @param id A collection id.
	* @return a List of the ContentResource objects.
	*/
	public List getAllResources(String id);

	/**
	* check permissions for editCollection()
	* @param id The id of the collection.
	* @return true if the user is allowed to update the collection, false if not.
	*/
	public boolean allowUpdateCollection(String id);

	/**
	* Access the collection with this local resource id, locked for update.
	* Must commitCollection() to make official, or cancelCollection() when done!
	* The collection internal members and properties are accessible from the returned Collection object.
	* @param id The id of the collection.
	* @exception IdUnusedException if the id does not exist.
	* @exception TypeException if the resource exists but is not a collection.
	* @exception PermissionException if the user does not have permissions to see this collection (or read through containing collections).
	* @exception InUseException if the Collection is locked by someone else.
	* @return The ContentCollection object found.
	*/
	public ContentCollectionEdit editCollection(String id)
		throws IdUnusedException, TypeException, PermissionException, InUseException;

	/**
	* check permissions for removeCollection().
	* @param id The id of the collection.
	* @return true if the user is allowed to removeCollection(id), false if not.
	*/
	public boolean allowRemoveCollection(String id);

	/**
	* Remove a collection and all members of the collection, internal or deeper.
	* @param id The id of the collection.
	* @exception IdUnusedException if the id does not exist.
	* @exception TypeException if the resource exists but is not a collection.
	* @exception PermissionException if the user does not have permissions to remove this collection, read through any containing
	* @exception InUseException if the collection or a contained member is locked by someone else.
	* collections, or remove any members of the collection.
	*/
	public void removeCollection(String id)
		throws IdUnusedException, TypeException, PermissionException, InUseException;

	/**
	* Remove just a collection.  It must be empty.
	* @param collection The collection to remove.
	* @exception TypeException if the resource exists but is not a collection.
	* @exception PermissionException if the user does not have permissions to remove this collection, read through any containing
	* @exception InconsistentException if the collection has members, so that the removal would leave things
	* in an inconsistent state.
	*/
	public void removeCollection(ContentCollectionEdit edit)
		throws TypeException, PermissionException, InconsistentException;

	/**
	* Commit the changes made, and release the lock.
	* The Object is disabled, and not to be used after this call.
	* @param edit The ContentCollectionEdit object to commit.
	*/
	public void commitCollection(ContentCollectionEdit edit);

	/**
	* Cancel the changes made object, and release the lock.
	* The Object is disabled, and not to be used after this call.
	* @param edit The ContentCollectionEdit object to commit.
	*/
	public void cancelCollection(ContentCollectionEdit edit);

/** copy %%%, move **/

	/**
	* check permissions for addResource().
	* @param id The id of the new resource.
	* @return true if the user is allowed to addResource(id), false if not.
	*/
	public boolean allowAddResource(String id);

	/**
	* Create a new resource with the given resource id.
	* @param id The id of the new resource.
	* @param type The mime type string of the resource.
	* @param content An array containing the bytes of the resource's content.
	* @param properties A ResourceProperties object with the properties to add to the new resource.
	* @param priority The notification priority for this commit.
	* @exception PermissionException if the user does not have permission to add a resource to the containing collection.
	* @exception IdUsedException if the resource id is already in use.
	* @exception IdInvalidException if the resource id is invalid.
	* @exception InconsistentException if the containing collection does not exist.
	* @exception OverQuotaException if this would result in being over quota.
	* @return a new ContentResource object.
	*/
	public ContentResource addResource(String id, String type, byte[] content, ResourceProperties properties, int priority)
		throws PermissionException, IdUsedException, IdInvalidException, InconsistentException, OverQuotaException;

	/**
	* Create a new resource with the given resource id, locked for update.
	* Must commitResource() to make official, or cancelResource() when done!
	* @param id The id of the new resource.
	* @exception PermissionException if the user does not have permission to add a resource to the containing collection.
	* @exception IdUsedException if the resource id is already in use.
	* @exception IdInvalidException if the resource id is invalid.
	* @exception InconsistentException if the containing collection does not exist.
	* @return a new ContentResource object.
	*/
	public ContentResourceEdit addResource(String id)
		throws PermissionException, IdUsedException, IdInvalidException, InconsistentException;

	/**
	* check permissions for addAttachmentResource().
	* @return true if the user is allowed to addAttachmentResource(), false if not.
	*/
	public boolean allowAddAttachmentResource();

	/**
	* Create a new resource as an attachment to some other resource in the system.
	* The new resource will be placed into a newly created collecion in the attachment
	* collection, with an auto-generated id, and given the specified resource name within this collection.
	* @param name The name of the new resource, i.e. a partial id relative to the collection where it will live.
	* @param type The mime type string of the resource.
	* @param content An array containing the bytes of the resource's content.
	* @param properties A ResourceProperties object with the properties to add to the new resource.
	* @exception IdUsedException if the resource name is already in use (not likely, as the containing collection is auto-generated!)
	* @exception IdInvalidException if the resource name is invalid.
	* @exception InconsistentException if the containing collection (or it's containing collection...) does not exist.
	* @exception PermissionException if the user does not have permission to add a collection, or add a member to a collection.
	* @exception OverQuotaException if this would result in being over quota.
	* @return a new ContentResource object.
	*/
	public ContentResource addAttachmentResource(String name, String type, byte[] content, ResourceProperties properties)
		throws IdInvalidException, InconsistentException, IdUsedException, PermissionException, OverQuotaException;

	/**
	* Create a new resource as an attachment to some other resource in the system, locked for update.
	* Must commitResource() to make official, or cancelResource() when done!
	* The new resource will be placed into a newly created collecion in the attachment
	* collection, with an auto-generated id, and given the specified resource name within this collection.
	* @param name The name of the new resource, i.e. a partial id relative to the collection where it will live.
	* @exception IdUsedException if the resource name is already in use (not likely, as the containing collection is auto-generated!)
	* @exception IdInvalidException if the resource name is invalid.
	* @exception InconsistentException if the containing collection (or it's containing collection...) does not exist.
	* @exception PermissionException if the user does not have permission to add a collection, or add a member to a collection.
	* @return a new ContentResource object.
	*/
	public ContentResourceEdit addAttachmentResource(String name)
		throws IdInvalidException, InconsistentException, IdUsedException, PermissionException;

	/**
	* check permissions for updateResource().
	* @param id The id of the new resource.
	* @return true if the user is allowed to updateResource(id), false if not.
	*/
	public boolean allowUpdateResource(String id);

	/**
	* Update the body and or content type of an existing resource with the given resource id.
	* @param id The id of the resource.
	* @param type The mime type string of the resource (if null, no change).
	* @param content An array containing the bytes of the resource's content (if null, no change).
	* @exception PermissionException if the user does not have permission to add a resource to the containing collection or write the resource.
	* @exception IdUnusedException if the resource id is not defined.
	* @exception TypeException if the resource is a collection.
	* @exception InUseException if the resource is locked by someone else.
	* @exception OverQuotaException if this would result in being over quota.
	* @return a new ContentResource object.
	*/
	public ContentResource updateResource(String id, String type, byte[] content)
		throws PermissionException, IdUnusedException, TypeException, InUseException, OverQuotaException;

	/**
	* Access the resource with this resource id, locked for update.  For non-collection resources only.
	* Must commitEdit() to make official, or cancelEdit() when done!
	* The resource content and properties are accessible from the returned Resource object.
	* @param id The id of the resource.
	* @exception PermissionException if the user does not have permissions to read the resource or read through any containing collection.
	* @exception IdUnusedException if the resource id is not found.
	* @exception TypeException if the resource is a collection.
	* @exception InUseException if the resource is locked by someone else.
	* @return the ContentResource object found.
	*/
	public ContentResourceEdit editResource(String id)
		throws PermissionException, IdUnusedException, TypeException, InUseException;

	/**
	* check permissions for getResource().
	* @param id The id of the new resource.
	* @return true if the user is allowed to getResource(id), false if not.
	*/
	public boolean allowGetResource(String id);

	/**
	* Check access to the resource with this local resource id.  For non-collection resources only.
	* @param id The id of the resource.
	* @exception PermissionException if the user does not have permissions to read the resource or read through any containing collection.
	* @exception IdUnusedException if the resource id is not found.
	* @exception TypeException if the resource is a collection.
	*/
	public void checkResource(String id)
		throws PermissionException, IdUnusedException, TypeException;

	/**
	* Access the resource with this resource id.  For non-collection resources only.
	* The resource content and properties are accessible from the returned Resource object.
	* @param id The id of the resource.
	* @exception PermissionException if the user does not have permissions to read the resource or read through any containing collection.
	* @exception IdUnusedException if the resource id is not found.
	* @exception TypeException if the resource is a collection.
	* @return the ContentResource object found.
	*/
	public ContentResource getResource(String id)
		throws PermissionException, IdUnusedException, TypeException;

	/**
	* check permissions for removeResource().
	* @param id The id of the new resource.
	* @return true if the user is allowed to removeResource(id), false if not.
	*/
	public boolean allowRemoveResource(String id);

	/**
	* Remove a resource.  For non-collection resources only.
	* @param id The id of the resource.
	* @exception PermissionException if the user does not have permissions to read a containing collection, or to remove this resource.
	* @exception IdUnusedException if the resource id is not found.
	* @exception TypeException if the resource is a collection.
	* @exception InUseException if the resource is locked by someone else.
	*/
	public void removeResource(String id)
		throws PermissionException, IdUnusedException, TypeException, InUseException;

	/**
	* Remove a resource  that is locked for update.
	* @param edit The ContentResourceEdit object to remove.
	* @exception PermissionException if the user does not have permissions to read a containing collection, or to remove this resource.
	*/
	public void removeResource(ContentResourceEdit edit)
		throws PermissionException;

	/**
	* check permissions for rename().
	* @param id The id of the existing resource.
	* @return true if the user is allowed to rename(id), false if not.
	*/
	public boolean allowRename(String id, String new_id);

	/**
	* Rename a resource or collection.  
	* @param id The id of the resource or collection.
	* @param new_id The desired id of the resource or collection.
	* @exception PermissionException if the user does not have permissions to read a containing collection, or to rename this resource.
	* @exception IdUnusedException if the resource id is not found.
	* @exception TypeException if the resource is a collection.
	* @exception InUseException if the resource is locked by someone else.
	*/
	public void rename(String id,String new_id)
		throws PermissionException, IdUnusedException, TypeException, InUseException;

	/**
	* check permissions for copy().
	* @param id The id of the new resource.
	* @param new_id The desired id of the new resource.
	* @return true if the user is allowed to copy(id,new_id), false if not.
	*/
	public boolean allowCopy(String id, String new_id);

	/**
	* Copy a resource.  
	* @param id The id of the resource.
	* @param new_id The desired id of the new resource.
	* @exception PermissionException if the user does not have permissions to read a containing collection, or to remove this resource.
	* @exception IdUnusedException if the resource id is not found.
	* @exception TypeException if the resource is a collection.
	* @exception InUseException if the resource is locked by someone else.
	*/
	public void copy(String id, String new_id)
		throws PermissionException, IdUnusedException, TypeException, InUseException,OverQuotaException;

	/**
	* Commit the changes made, and release the lock.
	* The Object is disabled, and not to be used after this call.
	* @param edit The ContentResourceEdit object to commit.
	* @exception OverQuotaException if this would result in being over quota (the edit is then cancled).
	*/
	public void commitResource(ContentResourceEdit edit)
		throws OverQuotaException;

	/**
	* Commit the changes made, and release the lock.
	* The Object is disabled, and not to be used after this call.
	* @param edit The ContentResourceEdit object to commit.
	* @param priority The notification priority of this commit.
	* @exception OverQuotaException if this would result in being over quota (the edit is then cancled).
	*/
	public void commitResource(ContentResourceEdit edit, int priority)
		throws OverQuotaException;

	/**
	* Cancel the changes made object, and release the lock.
	* The Object is disabled, and not to be used after this call.
	* @param edit The ContentResourceEdit object to commit.
	*/
	public void cancelResource(ContentResourceEdit edit);

/** %%% copy, move **/

	/**
	* check permissions for getProperties().
	* @param id The id of the new resource.
	* @return true if the user is allowed to getProperties(id), false if not.
	*/
	public boolean allowGetProperties(String id);

	/**
	* Access the properties of a resource with this resource id, either collection or resource.
	* @param id The id of the resource.
	* @exception PermissionException if the user does not have permissions to read properties on this object or read through containing collections.
	* @exception IdUnusedException if the resource id is not found.
	* @return the ResourceProperties object for this resource.
	*/
	public ResourceProperties getProperties(String id)
		throws PermissionException, IdUnusedException;

	/**
	* check permissions for addProperty().
	* @param id The id of the new resource.
	* @return true if the user is allowed to addProperty(id), false if not.
	*/
	public boolean allowAddProperty(String id);

	/**
	* Add / update a property for a collection or resource.
	* @param id The id of the resource.
	* @param name The properties name to add or update
	* @param value The new value for the property.
	* @exception PermissionException if the user does not have premissions to write properties on this object or read through containing collections.
	* @exception IdUnusedException if the resource id is not found.
	* @exception TypeException if any property requested cannot be set (it may be live).
	* @exception InUseException if the resource is locked by someone else.
	* @return the ResourceProperties object for this resource.
	*/
	public ResourceProperties addProperty(String id, String name, String value)
		throws PermissionException, IdUnusedException, TypeException, InUseException;

	/**
	* check permissions for removeProperty().
	* @param id The id of the new resource.
	* @return true if the user is allowed to removeProperty(id), false if not.
	*/
	public boolean allowRemoveProperty(String id);

	/**
	* Remove a property from a collection or resource.
	* @param id The id of the resource.
	* @param name The property name to be removed from the resource.
	* @exception PermissionException if the user does not have premissions to write properties on this object or read through containing collections.
	* @exception IdUnusedException if the resource id is not found.
	* @exception TypeException if the property named cannot be removed.
	* @exception InUseException if the resource is locked by someone else.
	* @return the ResourceProperties object for this resource.
	*/
	public ResourceProperties removeProperty(String id, String name)
		throws PermissionException, IdUnusedException, TypeException, InUseException;

	/**
	* Access an iterator (Strings) on the names of all properties used in the hosted resources and collections.
	* @return An iterator (Strings) on the names of all properties used in the hosted resources and collections
	* (may be empty).
	*/
	//%%%public Iterator getPropertyNames();

	/**
	* Access the resource URL from a resource id.
	* @param id The resource id.
	* @return The resource URL.
	*/
	public String getUrl(String id);

	/**
	* Access the internal reference from a resource id.
	* @param id The resource id.
	* @return The internal reference from a resource id.
	*/
	public String getReference(String id);

	/**
	* Access the resource id of the collection which contains this collection or resource.
	* @param id The resource id (reference, or URL) of the ContentCollection or ContentResource
	* @return the resource id (reference, or URL, depending on the id parameter) of the collection which contains this resource.
	*/
	public String getContainingCollectionId(String id);
	
	/**
	* Get the depth of the resource/collection object in the hireachy based on the given collection id
	* @param resourceId The Id of the resource/collection object to be tested 
	* @param baseCollectionId The Id of the collection as the relative root level
	* @return the integer value reflecting the relative hierarchy depth of the test resource/collection object based on the given base collection level
	*/
	public int getDepth(String resourceId, String baseCollectionId);

	/**
	* Test if this id (reference, or URL) refers to the root collection.
	* @param id The resource id (reference, or URL) of a ContentCollection
	* @return true if this is the root collection
	*/
	public boolean isRootCollection(String id);

	/**
	* Construct a stand-alone, not associated with any particular resource,
	* ResourceProperties object.
	* @return The new ResourceProperties object.
	*/
	public ResourcePropertiesEdit newResourceProperties();

	/**
	 * Return the collection id of the root collection for this site id.
	 * @param siteId The site id.
	 * @return The collection id which is the root collection for this site.
	 */
	String getSiteCollection(String siteId);

	/**
	 * Archive the specified list of resources.
	 * @param resources A list of the resources to archive.
	 * @param doc The document to contain the xml.
	 * @param stack The stack of elements, the top of which will be the containing element of the "service.name" element.
	 * @param archivePath The path to the folder where we are writing auxilary files.
	 * @return A log of status messages from the archive.
	 */
	String archiveResources(ReferenceVector resources, Document doc, Stack stack, String archivePath);
	

    /**
     * Gets all locks set on the resource with this local resource id.
     * @param id
     * @return
     */
     Collection getLocks(String id);

    /**
     *
     * Locks an object (resource or collection) with specified local resource id.
     *
     * Initially, the WebDAV concepts of expiration, exclusive vs shared, and inheritable are not supported;
     * instead, all locks are exclusive. Programmatically created locks may be designated as "system" locks,
     * in which case, users may not remove them via WebDAV lock management tools.
     *
     * Since only exclusive locks are permitted, a user can only lock a resource if it is not already locked;
     * however, multiple system locks can be put in place, because system locks imply that no user is permitted
     * to change the resource.
     *
     *
     * @param id
     * @param lockId
     * @param subject - the reason for this lock e.g. "being graded"
     * @param system - when true, it is not possible for a user to remove this lock
     *
     */
     public void lockObject(String id, String lockId, String subject, boolean system /* Date expiration , boolean exclusive, boolean inheritable*/);


    /**
     * Removes lock with given Id from object (resource or collection) specified by this local resource id.
     * Note that other locks could exist, so it does not necessarily fully unlock the object.
     *
     * @param id
     * @param lockId
     */
     public void removeLock(String id, String lockId);

    /**
     *
     * Convenience method to determine whether any locks exist for the Resource or Collection with the given
     * local resource id
     *
     * @param id
     * @return true when there are one or more locks on the given id
     */
     public boolean isLocked(String id);

    /**
     *
     * Returns true if this Collection or any of its contents has a lock.
     * It is likely much more efficient than recursively iterating
     * through all of the contained resources.
     *
     * @param id
     * @return
     */
     public boolean containsLockedNode(String id);

    /**
     * Convenience method that permanently removes any lock associated with id
     * @param id
     */
     public void removeAllLocks(String id);

}	// ContentHostingService

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/content/ContentHostingService.java,v 1.1 2005/05/12 15:45:35 ggolden.umich.edu Exp $
*
**********************************************************************************/
