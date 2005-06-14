/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/legacy/content/BaseContentService.java,v 1.10 2005/06/05 23:19:50 ggolden.umich.edu Exp $
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
package org.sakaiproject.component.legacy.content;

// import
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.apache.xerces.impl.dv.util.Base64;
import org.sakaiproject.component.legacy.notification.SiteEmailNotificationContent;
import org.sakaiproject.exception.EmptyException;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.InconsistentException;
import org.sakaiproject.exception.OverQuotaException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.service.framework.config.ServerConfigurationService;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.memory.Cache;
import org.sakaiproject.service.framework.memory.CacheRefresher;
import org.sakaiproject.service.framework.memory.MemoryService;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.framework.session.SessionStateBindingListener;
import org.sakaiproject.service.legacy.archive.ArchiveService;
import org.sakaiproject.service.legacy.content.ContentCollection;
import org.sakaiproject.service.legacy.content.ContentCollectionEdit;
import org.sakaiproject.service.legacy.content.ContentHostingService;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.content.ContentResourceEdit;
import org.sakaiproject.service.legacy.content.cover.ContentTypeImageService;
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.event.cover.EventTrackingService;
import org.sakaiproject.service.legacy.id.cover.IdService;
import org.sakaiproject.service.legacy.notification.NotificationEdit;
import org.sakaiproject.service.legacy.notification.NotificationService;
import org.sakaiproject.service.legacy.realm.cover.RealmService;
import org.sakaiproject.service.legacy.resource.Edit;
import org.sakaiproject.service.legacy.resource.Reference;
import org.sakaiproject.service.legacy.resource.ReferenceVector;
import org.sakaiproject.service.legacy.resource.Resource;
import org.sakaiproject.service.legacy.resource.ResourceProperties;
import org.sakaiproject.service.legacy.resource.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.security.cover.SecurityService;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.time.cover.TimeService;
import org.sakaiproject.util.java.Blob;
import org.sakaiproject.util.java.StringUtil;
import org.sakaiproject.util.Validator;
import org.sakaiproject.util.xml.Xml;
import org.sakaiproject.util.resource.BaseResourcePropertiesEdit;
import org.sakaiproject.util.storage.StorageUser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
* <p>BaseContentService is an abstract base implementation of the CHEF ContentHostingService as a
* Turbine service.</p>
* <p>Synchronization is done at the collection / resource level - multiple resources / collections can be updated at
* once, but each can be update by only one user at a time. Note: the Storage implementation should make sure that
* the same object is returned for any one resource / collection, so that we can synchronize on these objects.</p>
* todo: deal with already removed resource/collection. %%% -ggolden
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
* @see org.chefproject.service.generic.ContentHostingService
* @see ContentCollection
* @see ResourceProperties
* @see ContentResource
*/
public abstract class BaseContentService implements ContentHostingService, CacheRefresher
{
	/** The collection id for the attachments collection */
	protected static final String ATTACHMENTS_COLLECTION = "/attachment/";

	/** The initial portion of a relative access point URL. */
	protected String m_relativeAccessPoint = null;

	/** A Storage object for persistent storage. */
	protected Storage m_storage = null;

	/** A Cache for this service - ContentResource and ContentCollection keyed by reference. */
	protected Cache m_cache = null;

	/** The quota for content resource body bytes (in Kbytes) for any hierarchy in the /user/ or /group/ areas,
		or 0 if quotas are not enforced. */
	protected long m_siteQuota = 0;

	/** Collection id for the user sites. */
	public static final String COLLECTION_USER = "/user/";

	/** Collection id for the non-user sites. */
	public static final String COLLECTION_SITE = "/group/";

	/** Optional path to external file system file store for body binary. */
	protected String m_bodyPath = null;

	/** Optional set of folders just within the m_bodyPath to distribute files among. */
	protected String[] m_bodyVolumes = null;

	/*******************************************************************************
	* Constructors, Dependencies and their setter methods
	*******************************************************************************/

	/** Dependency: logging service */
	protected Logger m_logger = null;

	/**
	 * Dependency: logging service.
	 * @param service The logging service.
	 */
	public void setLogger(Logger service)
	{
		m_logger = service;
	}

	/** Dependency: MemoryService. */
	protected MemoryService m_memoryService = null;

	/**
	 * Dependency: MemoryService.
	 * @param service The MemoryService.
	 */
	public void setMemoryService(MemoryService service)
	{
		m_memoryService = service;
	}

	/** Dependency: NotificationService. */
	protected NotificationService m_notificationService = null;

	/**
	 * Dependency: NotificationService.
	 * @param service The NotificationService.
	 */
	public void setNotificationService(NotificationService service)
	{
		m_notificationService = service;
	}

	/** Dependency: ServerConfigurationService. */
	protected ServerConfigurationService m_serverConfigurationService = null;

	/**
	 * Dependency: ServerConfigurationService.
	 * @param service The ServerConfigurationService.
	 */
	public void setServerConfigurationService(ServerConfigurationService service)
	{
		m_serverConfigurationService = service;
	}

	/**
	 * Set the site quota.
	 * @param quota The site quota (as a string).
	 */
	public void setSiteQuota(String quota)
	{
		try
		{
			m_siteQuota = Long.parseLong(quota);
		}
		catch (Throwable t)
		{
		}
	}

	/** Configuration: cache, or not. */
	protected boolean m_caching = false;

	/**
	 * Configuration: set the locks-in-db
	 * @param path The storage path.
	 */
	public void setCaching(String value)
	{
		try
		{
			m_caching = new Boolean(value).booleanValue();
		}
		catch (Throwable t)
		{
		}
	}

	/**
	 * Configuration: set the external file system path for body storage
	 * If set, the resource binary database table will not be used.
	 * @param value The complete path to the root of the external file system storage area for resource body bytes.
	 */
	public void setBodyPath(String value)
	{
		m_bodyPath = value;
	}

	/**
	 * Configuration: set the external file system volume folders (folder just within the bodyPath) as
	 * a comma separated list of folder names.  If set, files will be distributed over these folders.
	 * @param value The comma separated list of folder names within body path to distribute files among.
	 */
	public void setBodyVolumes(String value)
	{
		try
		{
			m_bodyVolumes = StringUtil.split(value,",");
		}
		catch (Throwable t)
		{
		}
	}

	/*******************************************************************************
	* Init and Destroy
	*******************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		try
		{
			m_relativeAccessPoint = REFERENCE_ROOT;

			// construct a storage helper and read
			m_storage = newStorage();
			m_storage.open();

			// make the cache
			if (m_caching)
			{
				m_cache = m_memoryService.newCache(this, getAccessPoint(true));
			}

			// register a transient notification for resources
			NotificationEdit edit = m_notificationService.addTransientNotification();

			// set functions
			edit.setFunction(EVENT_RESOURCE_ADD);
			edit.addFunction(EVENT_RESOURCE_WRITE);

			// set the filter to any site related resource
			edit.setResourceFilter(getAccessPoint(true) + Resource.SEPARATOR + "group");
			// %%% is this the best we can do? -ggolden

			// set the action
			edit.setAction(new SiteEmailNotificationContent());

			StringBuffer buf = new StringBuffer();
			if (m_bodyVolumes != null)
			{
				for (int i = 0; i < m_bodyVolumes.length; i++)
				{
					buf.append(m_bodyVolumes[i]);
					buf.append(", ");
				}
			}
			m_logger.info(this +".init(): site quota: " + m_siteQuota + " body path: " + m_bodyPath + " volumes: " + buf.toString());
		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}

		// register as a resource service
		m_serverConfigurationService.registerResourceService(this);

	} // init

	/**
	* Returns to uninitialized state.
	*/
	public void destroy()
	{
		m_storage.close();
		m_storage = null;

		if ((m_caching) && (m_cache != null))
		{
			m_cache.destroy();
			m_cache = null;
		}

		m_logger.info(this +".destroy()");

	}

	/*******************************************************************************
	* StorageUser implementation - for collections
	*******************************************************************************/

	/**
	* Storage user for collections - in the resource side, not container
	*/
	protected class CollectionStorageUser implements StorageUser
	{
		public Resource newContainer(String ref)
		{
			return null;
		}
		public Resource newContainer(Element element)
		{
			return null;
		}
		public Resource newContainer(Resource other)
		{
			return null;
		}

		public Resource newResource(Resource container, String id, Object[] others)
		{
			return new BaseCollectionEdit(id);
		}

		public Resource newResource(Resource container, Element element)
		{
			return new BaseCollectionEdit(element);
		}

		public Resource newResource(Resource container, Resource other)
		{
			return new BaseCollectionEdit((ContentCollection) other);
		}

		public Edit newContainerEdit(String ref)
		{
			return null;
		}
		public Edit newContainerEdit(Element element)
		{
			return null;
		}
		public Edit newContainerEdit(Resource other)
		{
			return null;
		}

		public Edit newResourceEdit(Resource container, String id, Object[] others)
		{
			BaseCollectionEdit rv = new BaseCollectionEdit(id);
			rv.activate();
			return rv;
		}

		public Edit newResourceEdit(Resource container, Element element)
		{
			BaseCollectionEdit rv = new BaseCollectionEdit(element);
			rv.activate();
			return rv;
		}

		public Edit newResourceEdit(Resource container, Resource other)
		{
			BaseCollectionEdit rv = new BaseCollectionEdit((ContentCollection) other);
			rv.activate();
			return rv;
		}

		/**
		* Collect the fields that need to be stored outside the XML (for the resource).
		* @return An array of field values to store in the record outside the XML (for the resource).
		*/
		public Object[] storageFields(Resource r)
		{
			Object[] rv = new Object[1];
			rv[0] = StringUtil.referencePath(((ContentCollection) r).getId());

			return rv;
		}

		/**
		 * Check if this resource is in draft mode.
		 * @param r The resource.
		 * @return true if the resource is in draft mode, false if not.
		 */
		public boolean isDraft(Resource r)
		{
			return false;
		}

		/**
		 * Access the resource owner user id.
		 * @param r The resource.
		 * @return The resource owner user id.
		 */
		public String getOwnerId(Resource r)
		{
			return null;
		}

		/**
		 * Access the resource date.
		 * @param r The resource.
		 * @return The resource date.
		 */
		public Time getDate(Resource r)
		{
			return null;
		}

	} // class CollectionStorageUser

	/**
	* Storage user for resources - in the resource side, not container
	*/
	protected class ResourceStorageUser implements StorageUser
	{
		public Resource newContainer(String ref)
		{
			return null;
		}
		public Resource newContainer(Element element)
		{
			return null;
		}
		public Resource newContainer(Resource other)
		{
			return null;
		}

		public Resource newResource(Resource container, String id, Object[] others)
		{
			return new BaseResourceEdit(id);
		}

		public Resource newResource(Resource container, Element element)
		{
			return new BaseResourceEdit(element);
		}

		public Resource newResource(Resource container, Resource other)
		{
			return new BaseResourceEdit((ContentResource) other);
		}

		public Edit newContainerEdit(String ref)
		{
			return null;
		}
		public Edit newContainerEdit(Element element)
		{
			return null;
		}
		public Edit newContainerEdit(Resource other)
		{
			return null;
		}

		public Edit newResourceEdit(Resource container, String id, Object[] others)
		{
			BaseResourceEdit rv = new BaseResourceEdit(id);
			rv.activate();
			return rv;
		}

		public Edit newResourceEdit(Resource container, Element element)
		{
			BaseResourceEdit rv = new BaseResourceEdit(element);
			rv.activate();
			return rv;
		}

		public Edit newResourceEdit(Resource container, Resource other)
		{
			BaseResourceEdit rv = new BaseResourceEdit((ContentResource) other);
			rv.activate();
			return rv;
		}

		/**
		* Collect the fields that need to be stored outside the XML (for the resource).
		* @return An array of field values to store in the record outside the XML (for the resource).
		*/
		public Object[] storageFields(Resource r)
		{
			// include the file path field if we are doing body in the file system
			if (m_bodyPath != null)
			{
				Object[] rv = new Object[2];
				rv[0] = StringUtil.referencePath(((ContentResource) r).getId());
				rv[1] = StringUtil.trimToZero(((BaseResourceEdit) r).m_filePath);
				return rv;
			}

			// otherwise don't include the file path field			
			else
			{
				Object[] rv = new Object[1];
				rv[0] = StringUtil.referencePath(((ContentResource) r).getId());
				return rv;
			}
		}

		/**
		 * Check if this resource is in draft mode.
		 * @param r The resource.
		 * @return true if the resource is in draft mode, false if not.
		 */
		public boolean isDraft(Resource r)
		{
			return false;
		}

		/**
		 * Access the resource owner user id.
		 * @param r The resource.
		 * @return The resource owner user id.
		 */
		public String getOwnerId(Resource r)
		{
			return null;
		}

		/**
		 * Access the resource date.
		 * @param r The resource.
		 * @return The resource date.
		 */
		public Time getDate(Resource r)
		{
			return null;
		}

	} // class ResourceStorageUser

	/*******************************************************************************
	* ContentHostingService implementation
	*******************************************************************************/

	/**
	* Construct a Storage object.
	* @return The new storage object.
	*/
	protected abstract Storage newStorage();

	/**
	* Access the partial URL that forms the root of resource URLs.
	* @param relative if true, form within the access path only (i.e. starting with /content)
	* @return the partial URL that forms the root of resource URLs.
	*/
	protected String getAccessPoint(boolean relative)
	{
		return (relative ? "" : m_serverConfigurationService.getAccessUrl()) + m_relativeAccessPoint;

	} // getAccessPoint

	/**
	 * If the id is for a resource in a dropbox, and we are checking for read, change the check to a dropbox check, which is to check for write.
	 * You have full or no access to a dropbox, there is no just read access.
	 * @param lock The lock we are checking.
	 * @param id The resource id.
	 * @return The lock to check.
	 */
	protected String convertLockIfDropbox(String lock, String id)
	{
		if (id.startsWith("/group-user") && EVENT_RESOURCE_READ.equals(lock))
		{
			// only for /group-user/SIDEID/USERID/ refs.
			String[] parts = StringUtil.split(id,"/");
			if (parts.length >= 4)
			{
				return EVENT_RESOURCE_WRITE;
			}
		}

		return lock;
	}

	/**
	* Check security permission.
	* @param lock The lock id string.
	* @param id The resource id string, or null if no resource is involved.
	* @return true if permitted, false if not.
	*/
	protected boolean unlockCheck(String lock, String id)
	{
		lock = convertLockIfDropbox(lock, id);

		// make a reference from the resource id, if specified
		String ref = null;
		if (id != null)
		{
			ref = getReference(id);
		}

		return SecurityService.unlock(lock, ref);

	} // unlockCheck

	/**
	* Check security permission.
	* @param lock The lock id string.
	* @param id The resource id string, or null if no resource is involved.
	* @exception PermissionException Thrown if the user does not have access
	*/
	protected void unlock(String lock, String id) throws PermissionException
	{
		lock = convertLockIfDropbox(lock, id);

		// make a reference from the resource id, if specified
		String ref = null;
		if (id != null)
		{
			ref = getReference(id);
		}

		if (!SecurityService.unlock(lock, ref))
		{
			throw new PermissionException(UsageSessionService.getSessionUserId(), lock, ref);
		}

	} // unlock

	/**
	* Check security permission for all contained collections of the given collection (if any) (not the collection itself)
	* @param lock The lock id string.
	* @param resource The resource id string, or null if no resource is involved.
	* @exception PermissionException Thrown if the user does not have access
	*/
	/*protected void unlockContained(String lock, ContentCollection collection)
		throws PermissionException
	{
		if (SecurityService == null) return;
	
		Iterator it = collection.getMemberResources().iterator();
		while (it.hasNext())
		{
			Object mbr = it.next();
			if (mbr == null) continue;
	
			// for a contained collection, check recursively
			if (mbr instanceof ContentCollection)
			{
				unlockContained(lock, (ContentCollection) mbr);
			}
			
			// for resources, check
			else if (mbr instanceof ContentResource)
			{
				unlock(lock, ((ContentResource) mbr).getId());
			}
		}
	
	}	// unlockContained */

	/**
	* Create the live properties for a collection.
	* @param c The collection.
	*/
	protected void addLiveCollectionProperties(ContentCollectionEdit c)
	{
		ResourcePropertiesEdit p = c.getPropertiesEdit();
		String current = UsageSessionService.getSessionUserId();
		p.addProperty(ResourceProperties.PROP_CREATOR, current);
		p.addProperty(ResourceProperties.PROP_MODIFIED_BY, current);

		String now = TimeService.newTime().toString();
		p.addProperty(ResourceProperties.PROP_CREATION_DATE, now);
		p.addProperty(ResourceProperties.PROP_MODIFIED_DATE, now);

		p.addProperty(ResourceProperties.PROP_IS_COLLECTION, "true");

	} //  addLiveCollectionProperties

	/**
	* Create the live properties for a collection.
	* @param c The collection.
	*/
	protected void addLiveUpdateCollectionProperties(ContentCollectionEdit c)
	{
		ResourcePropertiesEdit p = c.getPropertiesEdit();
		String current = UsageSessionService.getSessionUserId();
		p.addProperty(ResourceProperties.PROP_MODIFIED_BY, current);

		String now = TimeService.newTime().toString();
		p.addProperty(ResourceProperties.PROP_MODIFIED_DATE, now);

	} //  addLiveUpdateCollectionProperties

	/**
	* Create the live properties for a resource.
	* @param r The resource.
	*/
	protected void addLiveResourceProperties(ContentResourceEdit r)
	{
		ResourcePropertiesEdit p = r.getPropertiesEdit();

		String current = UsageSessionService.getSessionUserId();
		p.addProperty(ResourceProperties.PROP_CREATOR, current);
		p.addProperty(ResourceProperties.PROP_MODIFIED_BY, current);

		String now = TimeService.newTime().toString();
		p.addProperty(ResourceProperties.PROP_CREATION_DATE, now);
		p.addProperty(ResourceProperties.PROP_MODIFIED_DATE, now);

		p.addProperty(ResourceProperties.PROP_CONTENT_LENGTH, Long.toString(r.getContentLength()));
		p.addProperty(ResourceProperties.PROP_CONTENT_TYPE, r.getContentType());

		p.addProperty(ResourceProperties.PROP_IS_COLLECTION, "false");

	} //  addLiveResourceProperties

	/**
	* Update the live properties for a resource when modified (for a resource).
	* @param r The resource.
	*/
	protected void addLiveUpdateResourceProperties(ContentResourceEdit r)
	{
		ResourcePropertiesEdit p = r.getPropertiesEdit();

		String current = UsageSessionService.getSessionUserId();
		p.addProperty(ResourceProperties.PROP_MODIFIED_BY, current);

		String now = TimeService.newTime().toString();
		p.addProperty(ResourceProperties.PROP_MODIFIED_DATE, now);

		p.addProperty(ResourceProperties.PROP_CONTENT_LENGTH, Long.toString(r.getContentLength()));
		p.addProperty(ResourceProperties.PROP_CONTENT_TYPE, r.getContentType());

	} //  addLiveUpdateResourceProperties

	/**
	* Make sure that the entire set of properties are present, adding whatever is needed,
	* replacing nothing that's there already.
	* @param r The resource.
	*/
	protected void assureResourceProperties(ContentResourceEdit r)
	{
		ResourcePropertiesEdit p = r.getPropertiesEdit();

		String current = UsageSessionService.getSessionUserId();
		String now = TimeService.newTime().toString();

		if (p.getProperty(ResourceProperties.PROP_CREATOR) == null)
		{
			p.addProperty(ResourceProperties.PROP_CREATOR, current);
		}

		if (p.getProperty(ResourceProperties.PROP_CREATION_DATE) == null)
		{
			p.addProperty(ResourceProperties.PROP_CREATION_DATE, now);
		}

		if (p.getProperty(ResourceProperties.PROP_MODIFIED_BY) == null)
		{
			p.addProperty(ResourceProperties.PROP_MODIFIED_BY, current);
		}

		if (p.getProperty(ResourceProperties.PROP_MODIFIED_DATE) == null)
		{
			p.addProperty(ResourceProperties.PROP_MODIFIED_DATE, now);
		}

		// these can just be set
		p.addProperty(ResourceProperties.PROP_CONTENT_LENGTH, Long.toString(r.getContentLength()));
		p.addProperty(ResourceProperties.PROP_CONTENT_TYPE, r.getContentType());
		p.addProperty(ResourceProperties.PROP_IS_COLLECTION, "false");

	} // assureResourceProperties

	/**
	* Add properties for a resource.
	* @param r The resource.
	* @param props The properties.
	*/
	protected void addProperties(ResourcePropertiesEdit p, ResourceProperties props)
	{
		if (props == null)
			return;

		Iterator it = props.getPropertyNames();
		while (it.hasNext())
		{
			String name = (String) it.next();

			// skip any live properties
			if (!props.isLiveProperty(name))
			{
				p.addProperty(name, props.getProperty(name));
			}
		}

	} //  addProperties

	/*******************************************************************************
	* Collections
	*******************************************************************************/

	/**
	* check permissions for addCollection().
	* @param channelId The channel id.
	* @return true if the user is allowed to addCollection(id), false if not.
	*/
	public boolean allowAddCollection(String id)
	{
		// collection must also end in the separator (we fix it)
		if (!id.endsWith(Resource.SEPARATOR))
		{
			id = id + Resource.SEPARATOR;
		}

		// check security
		return unlockCheck(EVENT_RESOURCE_ADD, id);

	} // allowAddCollection

	/**
	* Create a new collection with the given resource id.
	* @param id The id of the collection.
	* @param properties A java Properties object with the properties to add to the new collection.
	* @exception IdUsedException if the id is already in use.
	* @exception IdInvalidException if the id is invalid.
	* @exception PermissionException if the user does not have permission to add a collection, or add a member to a collection.
	* @exception InconsistentException if the containing collection does not exist.
	* @return a new ContentCollection object.
	*/
	public ContentCollection addCollection(String id, ResourceProperties properties)
		throws IdUsedException, IdInvalidException, PermissionException, InconsistentException
	{
		ContentCollectionEdit edit = addCollection(id);

		// add the provided of properties
		addProperties(edit.getPropertiesEdit(), properties);

		// commit the change
		commitCollection(edit);

		return edit;

	} // addCollection

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
		throws IdUsedException, IdInvalidException, PermissionException, InconsistentException
	{
		// check the id's validity (this may throw IdInvalidException)
		// use only the "name" portion, separated at the end
		String justName = isolateName(id);
		Validator.checkResourceId(justName);

		// collection must also end in the separator (we fix it)
		if (!id.endsWith(Resource.SEPARATOR))
		{
			id = id + Resource.SEPARATOR;
		}

		// check security
		unlock(EVENT_RESOURCE_ADD, id);

		// make sure the containing collection exists
		String container = isolateContainingId(id);
		ContentCollection containingCollection = m_storage.getCollection(container);
		if (containingCollection == null)
		{
			// make any missing collections
			generateCollections(container);

			// try again
			containingCollection = m_storage.getCollection(container);
			if (containingCollection == null)
				throw new InconsistentException(id);
		}

		// reserve the collection in storage - it will fail if the id is in use
		BaseCollectionEdit edit = (BaseCollectionEdit) m_storage.putCollection(id);
		if (edit == null)
		{
			throw new IdUsedException(id);
		}

		// add live properties
		addLiveCollectionProperties(edit);

		// track event
		edit.setEvent(EVENT_RESOURCE_ADD);

		return edit;

	} // addCollection

	/**
	* check permissions for getCollection().
	* @param id The id of the collection.
	* @return true if the user is allowed to getCollection(id), false if not.
	*/
	public boolean allowGetCollection(String id)
	{
		return unlockCheck(EVENT_RESOURCE_READ, id);

	} // allowGetCollection

	/**
	* Check access to the collection with this local resource id.
	* @param id The id of the collection.
	* @exception IdUnusedException if the id does not exist.
	* @exception TypeException if the resource exists but is not a collection.
	* @exception PermissionException if the user does not have permissions to see this collection (or read through containing collections).
	*/
	public void checkCollection(String id) throws IdUnusedException, TypeException, PermissionException
	{
		unlock(EVENT_RESOURCE_READ, id);

		ContentCollection collection = findCollection(id);
		if (collection == null)
			throw new IdUnusedException(id);

	} // checkCollection

	/**
	* Access the collection with this local resource id.
	* The collection internal members and properties are accessible from the returned Colelction object.
	* @param id The id of the collection.
	* @exception IdUnusedException if the id does not exist.
	* @exception TypeException if the resource exists but is not a collection.
	* @exception PermissionException if the user does not have permissions to see this collection (or read through containing collections).
	* @return The ContentCollection object found.
	*/
	public ContentCollection getCollection(String id) throws IdUnusedException, TypeException, PermissionException
	{
		unlock(EVENT_RESOURCE_READ, id);

		ContentCollection collection = findCollection(id);
		if (collection == null)
			throw new IdUnusedException(id);

		// track event
		// EventTrackingService.post(EventTrackingService.newEvent(EVENT_RESOURCE_READ, collection.getReference(), false));

		return collection;

	} // getCollection

	/**
	* Access a List of all the ContentResource objects in this path (and below)
	* which the current user has access.
	* @param id A collection id.
	* @return a List of the ContentResource objects.
	*/
	public List getAllResources(String id)
	{
		List rv = new Vector();

		// get the collection members
		try
		{
			ContentCollection collection = findCollection(id);
			if (collection != null)
			{
				getAllResources(collection, rv);
			}
		}
		catch (TypeException e)
		{
		}

		return rv;

	} // getAllResources

	/**
	* Access a List of all the ContentResource objects in this collection (and below)
	* which the current user has access.
	* @param collection The collection.
	* @param rv The list in which to accumulate resource objects.
	*/
	protected void getAllResources(ContentCollection collection, List rv)
	{
		List members = collection.getMemberResources();

		// process members
		for (Iterator iMbrs = members.iterator(); iMbrs.hasNext();)
		{
			Object next = iMbrs.next();

			// if resource, add it if permitted
			if (next instanceof ContentResource)
			{
				if (unlockCheck(EVENT_RESOURCE_READ, ((ContentResource) next).getId()))
				{
					rv.add(next);
				}
			}

			// if collection, again
			else
			{
				getAllResources((ContentCollection) next, rv);
			}
		}

	} // getAllResources

	/**
	* Access the collection with this local resource id.
	* Internal find does the guts of finding without security or event tracking.
	* The collection internal members and properties are accessible from the returned Colelction object.
	* @param id The id of the collection.
	* @exception TypeException if the resource exists but is not a collection.
	* @return The ContentCollection object found, or null if not.
	*/
	protected ContentCollection findCollection(String id) throws TypeException
	{
		ContentCollection collection = null;

		// if not caching
		if ((!m_caching) || (m_cache == null) || (m_cache.disabled()))
		{
			// TODO: current service caching
			collection = m_storage.getCollection(id);
		}

		else
		{
			// if we have it cached, use it (hit or miss)
			String key = getReference(id);
			if (m_cache.containsKey(key))
			{
				Object o = m_cache.get(key);
				if ((o != null) && (!(o instanceof ContentCollection)))
					throw new TypeException(id);

				collection = (ContentCollection) o;
			}

			// if not in the cache, see if we have it in our info store
			else
			{
				collection = m_storage.getCollection(id);

				// cache it (hit or miss)
				m_cache.put(key, collection);
			}
		}

		return collection;

	} // findCollection

	/**
	* check permissions for editCollection()
	* @param id The id of the collection.
	* @return true if the user is allowed to update the collection, false if not.
	*/
	public boolean allowUpdateCollection(String id)
	{
		return unlockCheck(EVENT_RESOURCE_WRITE, id);

	} // allowUpdateCollection

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
		throws IdUnusedException, TypeException, PermissionException, InUseException
	{
		String ref = getReference(id);

		// check security (throws if not permitted)
		unlock(EVENT_RESOURCE_WRITE, id);

		// check for existance
		if (!m_storage.checkCollection(id))
		{
			throw new IdUnusedException(id);
		}

		// ignore the cache - get the collection with a lock from the info store
		BaseCollectionEdit collection = (BaseCollectionEdit) m_storage.editCollection(id);
		if (collection == null)
			throw new InUseException(id);

		collection.setEvent(EVENT_RESOURCE_WRITE);

		return collection;

	} // editCollection

	/**
	* check permissions for removeCollection().
	* Note: for just this collection, not the members on down.
	* @param id The id of the collection.
	* @return true if the user is allowed to removeCollection(id), false if not.
	*/
	public boolean allowRemoveCollection(String id)
	{
		return unlockCheck(EVENT_RESOURCE_REMOVE, id);

	} // allowRemoveCollection

	/**
	* Remove just a collection.  It must be empty.
	* @param collection The collection to remove.
	* @exception TypeException if the resource exists but is not a collection.
	* @exception PermissionException if the user does not have permissions to remove this collection, read through any containing
	* @exception InconsistentException if the collection has members, so that the removal would leave things
	* in an inconsistent state.
	*/
	public void removeCollection(ContentCollectionEdit edit)
		throws TypeException, PermissionException, InconsistentException
	{
		// check for closed edit
		if (!edit.isActiveEdit())
		{
			try
			{
				throw new Exception();
			}
			catch (Exception e)
			{
				m_logger.warn(this +".removeCollection(): closed ContentCollectionEdit", e);
			}
			return;
		}

		// check security (throws if not permitted)
		unlock(EVENT_RESOURCE_REMOVE, edit.getId());

		// check for members
		List members = edit.getMemberResources();
		if (!members.isEmpty())
			throw new InconsistentException(edit.getId());

		// complete the edit
		m_storage.removeCollection(edit);

		// track it (no notification)
		EventTrackingService.post(
			EventTrackingService.newEvent(EVENT_RESOURCE_REMOVE, edit.getReference(), true, NotificationService.NOTI_NONE));

		// close the edit object
		 ((BaseCollectionEdit) edit).closeEdit();

		((BaseCollectionEdit) edit).setRemoved();

		// remove any realm defined for this resource
		try
		{
			RealmService.removeRealm(RealmService.editRealm(edit.getReference()));
		}
		catch (InUseException e)
		{
			m_logger.warn(this +".removeCollection: removing realm for : " + edit.getReference() + " : " + e);
		}
		catch (PermissionException e)
		{
			m_logger.warn(this +".removeCollection: removing realm for : " + edit.getReference() + " : " + e);
		}
		catch (IdUnusedException ignore)
		{
		}

	} // removeCollection

	/**
	* Remove a collection and all members of the collection, internal or deeper.
	* @param id The id of the collection.
	* @exception IdUnusedException if the id does not exist.
	* @exception TypeException if the resource exists but is not a collection.
	* @exception PermissionException if the user does not have permissions to remove this collection, read through any containing
	* @exception InUseException if the collection or a contained member is locked by someone else.
	* collections, or remove any members of the collection.
	*/
	public void removeCollection(String id) throws IdUnusedException, TypeException, PermissionException, InUseException
	{
		// check security for remove
		unlock(EVENT_RESOURCE_REMOVE, id);

		// find the collection
		ContentCollection thisCollection = findCollection(id);
		if (thisCollection == null)
			throw new IdUnusedException(id);

		// check security: can we remove members (if any)
		// Note: this will also be done in clear(), except some might get deleted before one is not allowed.
		//unlockContained(EVENT_RESOURCE_REMOVE, thisCollection);

		// get an edit
		ContentCollectionEdit edit = editCollection(id);

		// clear of all members (recursive)
		// Note: may fail if something's in use or not permitted.  May result in a partial clear.
		try
		{
			((BaseCollectionEdit) edit).clear();

			// remove
			removeCollection(edit);
		}
		catch (InconsistentException e)
		{
			m_logger.warn(this +".removeCollection():", e);
		}
		finally
		{
			// if we don't get the remove done, we need to cancel here
			if (((BaseCollectionEdit) edit).isActiveEdit())
			{
				cancelCollection(edit);
			}
		}

	} // removeCollection

	/**
	* Commit the changes made, and release the lock.
	* The Object is disabled, and not to be used after this call.
	* @param edit The ContentCollectionEdit object to commit.
	*/
	public void commitCollection(ContentCollectionEdit edit)
	{
		// check for closed edit
		if (!edit.isActiveEdit())
		{
			try
			{
				throw new Exception();
			}
			catch (Exception e)
			{
				m_logger.warn(this +".commitCollection(): closed ContentCollectionEdit", e);
			}
			return;
		}

		// update the properties for update
		addLiveUpdateCollectionProperties(edit);

		// complete the edit
		m_storage.commitCollection(edit);

		// track it (no notification)
		EventTrackingService.post(
			EventTrackingService.newEvent(
				((BaseCollectionEdit) edit).getEvent(),
				edit.getReference(),
				true,
				NotificationService.NOTI_NONE));

		// close the edit object
		 ((BaseCollectionEdit) edit).closeEdit();

	} // commitCollection

	/**
	* Cancel the changes made object, and release the lock.
	* The Object is disabled, and not to be used after this call.
	* @param edit The ContentCollectionEdit object to commit.
	*/
	public void cancelCollection(ContentCollectionEdit edit)
	{
		// check for closed edit
		if (!edit.isActiveEdit())
		{
			try
			{
				throw new Exception();
			}
			catch (Exception e)
			{
				m_logger.warn(this +".cancelCollection(): closed ContentCollectionEdit", e);
			}
			return;
		}

		// release the edit lock
		m_storage.cancelCollection(edit);
		
		// if the edit is newly created during an add collection process, remove it from the storage
		if (((BaseCollectionEdit) edit).getEvent().equals(EVENT_RESOURCE_ADD))
		{
			m_storage.removeCollection(edit);
		}

		// close the edit object
		 ((BaseCollectionEdit) edit).closeEdit();

	} // cancelCollection

	/*******************************************************************************
	* Resources
	*******************************************************************************/

	/**
	* check permissions for addResource().
	* @param id The id of the new resource.
	* @return true if the user is allowed to addResource(id), false if not.
	*/
	public boolean allowAddResource(String id)
	{
		// resource must also NOT end with a separator characters (we fix it)
		if (id.endsWith(Resource.SEPARATOR))
		{
			id = id.substring(0, id.length() - 1);
		}

		// check security
		return unlockCheck(EVENT_RESOURCE_ADD, id);

	} // allowAddResource

	/**
	* Create a new resource with the given resource id.
	* @param id The id of the new resource.
	* @param type The mime type string of the resource.
	* @param content An array containing the bytes of the resource's content.
	* @param properties A java Properties object with the properties to add to the new resource.
	* @param priority The notification priority for this commit.
	* @exception PermissionException if the user does not have permission to add a resource to the containing collection.
	* @exception IdUsedException if the resource id is already in use.
	* @exception IdInvalidException if the resource id is invalid.
	* @exception InconsistentException if the containing collection does not exist.
	* @exception OverQuotaException if this would result in being over quota.
	* @return a new ContentResource object.
	*/
	public ContentResource addResource(String id, String type, byte[] content, ResourceProperties properties, int priority)
		throws PermissionException, IdUsedException, IdInvalidException, InconsistentException, OverQuotaException
	{
		id = (String) ((Hashtable) fixTypeAndId(id, type)).get("id");
		ContentResourceEdit edit = addResource(id);
		edit.setContentType(type);
		edit.setContent(content);
		addProperties(edit.getPropertiesEdit(), properties);

		// commit the change
		commitResource(edit, priority);

		return edit;

	} // addResource

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
		throws PermissionException, IdUsedException, IdInvalidException, InconsistentException
	{
		// check the id's validity (this may throw IdInvalidException)
		// use only the "name" portion, separated at the end
		String justName = isolateName(id);
		Validator.checkResourceId(justName);

		// resource must also NOT end with a separator characters (we fix it)
		if (id.endsWith(Resource.SEPARATOR))
		{
			id = id.substring(0, id.length() - 1);
		}

		// check security
		unlock(EVENT_RESOURCE_ADD, id);

		// make sure the containing collection exists
		String container = isolateContainingId(id);
		ContentCollection containingCollection = m_storage.getCollection(container);
		if (containingCollection == null)
		{
			// make any missing collections
			generateCollections(container);

			// try again
			containingCollection = m_storage.getCollection(container);
			if (containingCollection == null)
				throw new InconsistentException(id);
		}

		// reserve the resource in storage - it will fail if the id is in use
		BaseResourceEdit edit = (BaseResourceEdit) m_storage.putResource(id);
		if (edit == null)
		{
			throw new IdUsedException(id);
		}

		// add live properties
		addLiveResourceProperties(edit);

		// track event
		edit.setEvent(EVENT_RESOURCE_ADD);

		return edit;

	} // addResource

	/**
	* check permissions for addAttachmentResource().
	* @return true if the user is allowed to addAttachmentResource(), false if not.
	*/
	public boolean allowAddAttachmentResource()
	{
		return unlockCheck(EVENT_RESOURCE_ADD, ATTACHMENTS_COLLECTION);

	} // allowAddAttachmentResource

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
		throws IdInvalidException, InconsistentException, IdUsedException, PermissionException, OverQuotaException
	{
		// make sure the name is valid
		Validator.checkResourceId(name);

		// resource must also NOT end with a separator characters (we fix it)
		if (name.endsWith(Resource.SEPARATOR))
		{
			name = name.substring(0, name.length() - 1);
		}

		// form a name based on the attachments collection, a unique folder id, and the given name
		String collection = ATTACHMENTS_COLLECTION + IdService.getUniqueId() + Resource.SEPARATOR;
		String id = collection + name;

		// add this collection
		ContentCollectionEdit edit = addCollection(collection);
		edit.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, name);
		commitCollection(edit);

		// and add the resource
		return addResource(id, type, content, properties, NotificationService.NOTI_NONE);

	} // addAttachmentResource

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
		throws IdInvalidException, InconsistentException, IdUsedException, PermissionException
	{
		// make sure the name is valid
		Validator.checkResourceId(name);

		// resource must also NOT end with a separator characters (we fix it)
		if (name.endsWith(Resource.SEPARATOR))
		{
			name = name.substring(0, name.length() - 1);
		}

		// form a name based on the attachments collection, a unique folder id, and the given name
		String collection = ATTACHMENTS_COLLECTION + IdService.getUniqueId() + Resource.SEPARATOR;
		String id = collection + name;

		// add this collection
		ContentCollectionEdit edit = addCollection(collection);
		edit.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, name);
		commitCollection(edit);

		return addResource(id);

	} // addAttachmentResource

	/**
	* check permissions for updateResource().
	* @param id The id of the new resource.
	* @return true if the user is allowed to updateResource(id), false if not.
	*/
	public boolean allowUpdateResource(String id)
	{
		return unlockCheck(EVENT_RESOURCE_WRITE, id);

	} // allowUpdateResource

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
		throws PermissionException, IdUnusedException, TypeException, InUseException, OverQuotaException
	{
		// find a resource that is this resource
		ContentResourceEdit edit = editResource(id);

		edit.setContentType(type);
		edit.setContent(content);

		// commit the change
		commitResource(edit);

		return edit;

	} // updateResource

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
		throws PermissionException, IdUnusedException, TypeException, InUseException
	{
		String ref = getReference(id);

		// check security (throws if not permitted)
		unlock(EVENT_RESOURCE_WRITE, id);

		// check for existance
		if (!m_storage.checkResource(id))
		{
			throw new IdUnusedException(id);
		}

		// ignore the cache - get the collection with a lock from the info store
		BaseResourceEdit resource = (BaseResourceEdit) m_storage.editResource(id);
		if (resource == null)
			throw new InUseException(id);

		resource.setEvent(EVENT_RESOURCE_WRITE);

		return resource;

	} // editResource

	/**
	* check permissions for getResource().
	* @param id The id of the new resource.
	* @return true if the user is allowed to getResource(id), false if not.
	*/
	public boolean allowGetResource(String id)
	{
		return unlockCheck(EVENT_RESOURCE_READ, id);

	} // allowGetResource

	/**
	* Check access to the resource with this local resource id.  For non-collection resources only.
	* @param id The id of the resource.
	* @exception PermissionException if the user does not have permissions to read the resource or read through any containing collection.
	* @exception IdUnusedException if the resource id is not found.
	* @exception TypeException if the resource is a collection.
	*/
	public void checkResource(String id) throws PermissionException, IdUnusedException, TypeException
	{
		// check security
		unlock(EVENT_RESOURCE_READ, id);

		ContentResource resource = findResource(id);
		if (resource == null)
			throw new IdUnusedException(id);

	} // checkResource

	/**
	* Access the resource with this resource id.  For non-collection resources only.
	* The resource content and properties are accessible from the returned Resource object.
	* @param id The resource id.
	* @exception PermissionException if the user does not have permissions to read the resource or read through any containing collection.
	* @exception IdUnusedException if the resource id is not found.
	* @exception TypeException if the resource is a collection.
	* @return the ContentResource object found.
	*/
	public ContentResource getResource(String id) throws PermissionException, IdUnusedException, TypeException
	{
		// check security
		unlock(EVENT_RESOURCE_READ, id);

		ContentResource resource = findResource(id);
		if (resource == null)
			throw new IdUnusedException(id);

		// track event
		// EventTrackingService.post(EventTrackingService.newEvent(EVENT_RESOURCE_READ, resource.getReference(), false));

		return resource;

	} // getResource

	/**
	* Access the resource with this resource id.  For non-collection resources only.
	* Internal find that doesn't do security or event tracking
	* The resource content and properties are accessible from the returned Resource object.
	* @param id The resource id.
	* @exception TypeException if the resource is a collection.
	* @return the ContentResource object found, or null if there's a problem.
	*/
	protected ContentResource findResource(String id) throws TypeException
	{
		ContentResource resource = null;

		// if not caching
		if ((!m_caching) || (m_cache == null) || (m_cache.disabled()))
		{
			// TODO: current service caching
			resource = m_storage.getResource(id);
		}

		else
		{
			// if we have it cached, use it (hit or miss)
			String key = getReference(id);
			if (m_cache.containsKey(key))
			{
				Object o = m_cache.get(key);
				if ((o != null) && (!(o instanceof ContentResource)))
					throw new TypeException(id);

				resource = (ContentResource) o;
			}

			// if not in the cache, see if we have it in our info store
			else
			{
				resource = m_storage.getResource(id);

				// cache it (hit or miss)
				m_cache.put(key, resource);
			}
		}

		return resource;

	} // findResource

	/**
	* check permissions for removeResource().
	* @param id The id of the new resource.
	* @return true if the user is allowed to removeResource(id), false if not.
	*/
	public boolean allowRemoveResource(String id)
	{
		// check security
		return unlockCheck(EVENT_RESOURCE_REMOVE, id);

	} // allowRemoveResource

	/**
	* Remove a resource.  For non-collection resources only.
	* @param id The resource id.
	* @exception PermissionException if the user does not have permissions to read a containing collection, or to remove this resource.
	* @exception IdUnusedException if the resource id is not found.
	* @exception TypeException if the resource is a collection.
	* @exception InUseException if the resource is locked by someone else.
	*/
	public void removeResource(String id) throws PermissionException, IdUnusedException, TypeException, InUseException
	{
		BaseResourceEdit edit = (BaseResourceEdit) editResource(id);
		removeResource(edit);

	} // removeResource

	/**
	* Remove a resource  that is locked for update.
	* @param edit The ContentResourceEdit object to remove.
	* @exception PermissionException if the user does not have permissions to read a containing collection, or to remove this resource.
	*/
	public void removeResource(ContentResourceEdit edit) throws PermissionException
	{
		// check for closed edit
		if (!edit.isActiveEdit())
		{
			try
			{
				throw new Exception();
			}
			catch (Exception e)
			{
				m_logger.warn(this +".removeResource(): closed ContentResourceEdit", e);
			}
			return;
		}

		// check security (throws if not permitted)
		unlock(EVENT_RESOURCE_REMOVE, edit.getId());

		// complete the edit
		m_storage.removeResource(edit);

		// track it (no notification)
		EventTrackingService.post(
			EventTrackingService.newEvent(EVENT_RESOURCE_REMOVE, edit.getReference(), true, NotificationService.NOTI_NONE));

		// close the edit object
		 ((BaseResourceEdit) edit).closeEdit();

		((BaseResourceEdit) edit).setRemoved();

		// remove any realm defined for this resource
		try
		{
			RealmService.removeRealm(RealmService.editRealm(edit.getReference()));
		}
		catch (InUseException e)
		{
			m_logger.warn(this +".removeResource: removing realm for : " + edit.getReference() + " : " + e);
		}
		catch (PermissionException e)
		{
			m_logger.warn(this +".removeResource: removing realm for : " + edit.getReference() + " : " + e);
		}
		catch (IdUnusedException ignore)
		{
		}

	} // removeResource

	/**
	* check permissions for rename().
	* Note: for just this collection, not the members on down.
	* @param id The id of the collection.
	* @return true if the user is allowed to rename(id), false if not.
	*/
	public boolean allowRename(String id, String new_id)
	{
		m_logger.warn(this +".allowRename(" + id + ") - Rename not implemented");
		return false;

		// return unlockCheck(EVENT_RESOURCE_ADD, new_id) &&
		//        unlockCheck(EVENT_RESOURCE_REMOVE, id);

	} // allowRename

	/**
	* Rename a collection or resource.  
	* @param id The id of the collection.
	* @param new_id The desired id of the collection.
	* @exception IdUnusedException if the id does not exist.
	* @exception TypeException if the resource exists but is not a collection or resource.
	* @exception PermissionException if the user does not have permissions to rename 
	* @exception InUseException if the id or a contained member is locked by someone else.
	* collections, or remove any members of the collection.
	*/
	public void rename(String id, String new_id)
		throws IdUnusedException, TypeException, PermissionException, InUseException
	{
		// Note - this could be implemented in this base class using a copy and a delete
		// and then overridden in those derived classes which can support
		// a direct rename operation.

		m_logger.warn(this +".rename(" + id + "," + new_id + ") - Rename not implemented");
		throw new TypeException("BaseContentService.rename() Not Implemented ");

	} // rename

	/**
	* check permissions for copy().
	* @param id The id of the new resource.
	* @param new_id The desired id of the new resource.
	* @return true if the user is allowed to copy(id,new_id), false if not.
	*/
	public boolean allowCopy(String id, String new_id)
	{
		return unlockCheck(EVENT_RESOURCE_ADD, new_id) && unlockCheck(EVENT_RESOURCE_READ, id);
	}

	/**
	* Copy a resource or collection
	* @param id The id of the resource.
	* @param new_id The desired id of the new resource.
	* @exception PermissionException if the user does not have permissions to read a containing collection, or to remove this resource.
	* @exception IdUnusedException if the resource id is not found.
	* @exception TypeException if the resource is a collection.
	* @exception InUseException if the resource is locked by someone else.
	*/
	public void copy(String id, String new_id)
		throws PermissionException, IdUnusedException, TypeException, InUseException, OverQuotaException
	{
		boolean isCollection = false;
		boolean isRootCollection = false;
		ContentResource thisResource = null;

		if (m_logger.isDebugEnabled())
			m_logger.debug(this +".copy(" + id + "," + new_id + ")");

		// find the collection
		ContentCollection thisCollection = findCollection(id);
		if (thisCollection != null)
		{
			isCollection = true;
			if (isRootCollection(id))
			{
				throw new PermissionException(UsageSessionService.getSessionUserId(), null, null);
			}
		}
		else
		{
			thisResource = findResource(id);
		}

		if (thisResource == null && thisCollection == null)
		{
			throw new IdUnusedException(id);
		}

		if (isCollection)
		{
			copyCollection(thisCollection, new_id);
		}
		else
		{
			copyResource(thisResource, new_id);
		}

	}

	/**
	 * getResourceNameCHEF - Needs to become a method of resource
	 *    returns the internal name for a resource.
	 *    @@Glenn - review this @@
	 */

	public String getResourceNameCHEF(Resource mbr)
	{
		String idx = mbr.getId();
		String resourceName = isolateName(idx);
		ResourceProperties props = mbr.getProperties();
		if (resourceName == null)
			resourceName = props.getProperty(ResourceProperties.PROP_DISPLAY_NAME);

		return resourceName;
	}

	/**
	 * Get a duplicate copy of resource properties
	 * This copies everything except for the DISPLAYNAME - DISPLAYNAME is only 
	 * copied if it is different than the file name as derived from the id (path)
	 *
	 * Note to Chuck - should the add operations check for empty Display and 
	 * set it to the file name rather than putting all the code all over the place.
	 */
	private ResourcePropertiesEdit duplicateResourceProperties(ResourceProperties properties, String id)
	{
		ResourcePropertiesEdit resourceProperties = newResourceProperties();

		if (properties == null)
			return resourceProperties;

		//  If there is a distinct display name, we keep it
		//  If the display name is the "file name" we pitch it and let the name change
		String displayName = properties.getProperty(ResourceProperties.PROP_DISPLAY_NAME);
		String resourceName = isolateName(id);
		if (displayName == null)
			displayName = resourceName;
		if (displayName.length() == 0)
			displayName = resourceName;

		// loop throuh the properties 
		Iterator propertyNames = properties.getPropertyNames();
		while (propertyNames.hasNext())
		{
			String propertyName = (String) propertyNames.next();
			if (!properties.isLiveProperty(propertyName))
			{
				if (propertyName.equals(ResourceProperties.PROP_DISPLAY_NAME))
				{
					if (!displayName.equals(resourceName))
					{
						resourceProperties.addProperty(propertyName, displayName);
					}
				}
				else
				{
					resourceProperties.addProperty(propertyName, properties.getProperty(propertyName));
				} // if-else
			} // if
		} // while
		return resourceProperties;

	} // duplicateResourceProperties

	/**
	* Copy a resource.  
	* @param thisResource The resource to be copied
	* @param new_id The desired id of the new resource.
	* @exception PermissionException if the user does not have permissions to read a containing collection, or to remove this resource.
	* @exception IdUnusedException if the resource id is not found.
	* @exception TypeException if the resource is a collection.
	* @exception InUseException if the resource is locked by someone else.
	*/
	public void copyResource(ContentResource resource, String new_id)
		throws PermissionException, IdUnusedException, TypeException, InUseException, OverQuotaException
	{
		ResourceProperties properties = resource.getProperties();
		ResourcePropertiesEdit newProps = duplicateResourceProperties(properties, resource.getId());

		String displayName = newProps.getProperty(ResourceProperties.PROP_DISPLAY_NAME);
		String fileName = isolateName(new_id);

		if (displayName == null && fileName != null)
		{
			newProps.addProperty(ResourceProperties.PROP_DISPLAY_NAME, fileName);
		}

		if (m_logger.isDebugEnabled())
			m_logger.debug(this +".copyResource displayname=" + displayName + " fileName=" + fileName);

		// copy the resource to the new location
		try
		{
			ContentResource newResource =
				addResource(
					new_id,
					resource.getContentType(),
					resource.getContent(),
					newProps,
					NotificationService.NOTI_OPTIONAL);
			if (m_logger.isDebugEnabled())
				m_logger.debug(this +".copyResource successful");
		}
		catch (InconsistentException e)
		{
			throw new TypeException(new_id);
		}
		catch (IdInvalidException e)
		{
			throw new TypeException(new_id);
		}
		catch (IdUsedException e)
		{
			// Could come up with a naming convention to add versions here
			throw new PermissionException(UsageSessionService.getSessionUserId(), null, null);
		}
	} // copyResource

	/**
	* Copy a collection.  
	* @param thisCollection The collection to be copied
	* @param new_id The desired id of the new collection.
	* @exception PermissionException if the user does not have permissions to perform the operations
	* @exception IdUnusedException if the collection id is not found.
	* @exception TypeException if the resource is not a collection.
	* @exception InUseException if the resource is locked by someone else.
	*/
	public void copyCollection(ContentCollection thisCollection, String new_id)
		throws PermissionException, IdUnusedException, TypeException, InUseException, OverQuotaException
	{
		List members = thisCollection.getMemberResources();

		if (m_logger.isDebugEnabled())
			m_logger.debug(this +".copyCollection size=" + members.size());

		if (members.size() > 0)
			throw new PermissionException(UsageSessionService.getSessionUserId(), null, null);

		String name = isolateName(new_id);

		ResourceProperties properties = thisCollection.getProperties();
		ResourcePropertiesEdit newProps = duplicateResourceProperties(properties, thisCollection.getId());
		newProps.addProperty(ResourceProperties.PROP_DISPLAY_NAME, name);

		if (m_logger.isDebugEnabled())
			m_logger.debug(this +".copyCollection adding colletion=" + new_id + " name=" + name);

		try
		{
			ContentCollection newCollection = addCollection(new_id, newProps);
			if (m_logger.isDebugEnabled())
				m_logger.debug(this +".copyCollection successful");
		}
		catch (InconsistentException e)
		{
			throw new TypeException(new_id);
		}
		catch (IdInvalidException e)
		{
			throw new TypeException(new_id);
		}
		catch (IdUsedException e) // Why is this the case??
		{
			throw new PermissionException(UsageSessionService.getSessionUserId(), null, null);
		}

	} // copyCollection

	/**
	* Commit the changes made, and release the lock.
	* The Object is disabled, and not to be used after this call.
	* @param edit The ContentResourceEdit object to commit.
	* @exception OverQuotaException if this would result in being over quota (the edit is then cancled).
	*/
	public void commitResource(ContentResourceEdit edit) throws OverQuotaException
	{
		commitResource(edit, NotificationService.NOTI_OPTIONAL);

	} // commitResource

	/**
	* Commit the changes made, and release the lock.
	* The Object is disabled, and not to be used after this call.
	* @param edit The ContentResourceEdit object to commit.
	* @param priority The notification priority of this commit.
	* @exception OverQuotaException if this would result in being over quota (the edit is then cancled).
	*/
	public void commitResource(ContentResourceEdit edit, int priority) throws OverQuotaException
	{
		// check for closed edit
		if (!edit.isActiveEdit())
		{
			try
			{
				throw new Exception();
			}
			catch (Exception e)
			{
				m_logger.warn(this +".commitResource(): closed ContentResourceEdit", e);
			}
			return;
		}

		// check for over quota.
		if (overQuota(edit))	
		{
			cancelResource(edit);
			throw new OverQuotaException(edit.getReference());
		}

		commitResourceEdit(edit, priority);

	} // commitResource

	/**
	* Commit the changes made, and release the lock - no quota check.
	* The Object is disabled, and not to be used after this call.
	* @param edit The ContentResourceEdit object to commit.
	* @param priority The notification priority of this commit.
	*/
	protected void commitResourceEdit(ContentResourceEdit edit, int priority)
	{
		// check for closed edit
		if (!edit.isActiveEdit())
		{
			try
			{
				throw new Exception();
			}
			catch (Exception e)
			{
				m_logger.warn(this +".commitResourceEdit(): closed ContentResourceEdit", e);
			}
			return;
		}

		// update the properties for update
		addLiveUpdateResourceProperties(edit);

		// complete the edit
		m_storage.commitResource(edit);

		// track it
		EventTrackingService.post(
			EventTrackingService.newEvent(((BaseResourceEdit) edit).getEvent(), edit.getReference(), true, priority));

		// close the edit object
		 ((BaseResourceEdit) edit).closeEdit();

	} // commitResourceEdit

	/**
	* Cancel the changes made object, and release the lock.
	* The Object is disabled, and not to be used after this call.
	* @param edit The ContentResourceEdit object to commit.
	*/
	public void cancelResource(ContentResourceEdit edit)
	{
		// check for closed edit
		if (!edit.isActiveEdit())
		{
			try
			{
				throw new Exception();
			}
			catch (Exception e)
			{
				m_logger.warn(this +".cancelResource(): closed ContentResourceEdit", e);
			}
			return;
		}

		// release the edit lock
		m_storage.cancelResource(edit);

		// if the edit is newly created during an add resource process, remove it from the storage
		if (((BaseResourceEdit) edit).getEvent().equals(EVENT_RESOURCE_ADD))
		{
			m_storage.removeResource(edit);
		}

		// close the edit object
		 ((BaseResourceEdit) edit).closeEdit();

	} // cancelResource

	/**
	* check permissions for getProperties().
	* @param id The id of the new resource.
	* @return true if the user is allowed to getProperties(id), false if not.
	*/
	public boolean allowGetProperties(String id)
	{
		return unlockCheck(EVENT_RESOURCE_READ, id);

	} // allowGetProperties

	/**
	* Access the properties of a resource with this resource id, either collection or resource.
	* @param id The resource id.
	* @exception PermissionException if the user does not have permissions to read properties on this object or read through containing collections.
	* @exception IdUnusedException if the resource id is not found.
	* @return the ResourceProperties object for this resource.
	*/
	public ResourceProperties getProperties(String id) throws PermissionException, IdUnusedException
	{
		unlock(EVENT_RESOURCE_READ, id);

		boolean collectionHint = id.endsWith(Resource.SEPARATOR);

		Resource o = null;

		try
		{
			if (collectionHint)
			{
				o = findCollection(id);
			}
			else
			{
				o = findResource(id);
			}
		}
		catch (TypeException ignore)
		{
		}

		// unlikely, but...
		if (o == null)
			throw new IdUnusedException(id);

		// track event - removed for clarity of the event log -ggolden
		//EventTrackingService.post(EventTrackingService.newEvent(EVENT_PROPERTIES_READ, getReference(id)));

		return o.getProperties();

	} // getProperties

	/**
	* check permissions for addProperty().
	* @param id The id of the new resource.
	* @return true if the user is allowed to addProperty(id), false if not.
	*/
	public boolean allowAddProperty(String id)
	{
		return unlockCheck(EVENT_RESOURCE_WRITE, id);

	} // allowAddProperty

	/**
	* Add / update a property for a resource, either collection or resource.
	* @param id The resource id.
	* @param name The properties name to add or update
	* @param value The new value for the property.
	* @exception PermissionException if the user does not have premissions to write properties on this object or read through containing collections.
	* @exception IdUnusedException if the resource id is not found.
	* @exception TypeException if any property requested cannot be set (it may be live).
	* @exception InUseException if the resource is locked by someone else.
	* @return the ResourceProperties object for this resource.
	*/
	public ResourceProperties addProperty(String id, String name, String value)
		throws PermissionException, IdUnusedException, TypeException, InUseException
	{
		unlock(EVENT_RESOURCE_WRITE, id);

		boolean collectionHint = id.endsWith(Resource.SEPARATOR);
		Edit o = null;
		if (collectionHint)
		{
			o = editCollection(id);
		}
		else
		{
			o = editResource(id);
		}

		// unlikely, but...
		if (o == null)
			throw new IdUnusedException(id);

		// get the properties
		ResourcePropertiesEdit props = o.getPropertiesEdit();

		// check for TypeException updating live properties
		if (props.isLiveProperty(name))
			throw new TypeException(name);

		// add the property
		props.addProperty(name, value);

		// commit the change
		if (o instanceof ContentResourceEdit)
		{
			commitResourceEdit((ContentResourceEdit) o, NotificationService.NOTI_NONE);
		}
		if (o instanceof ContentCollectionEdit)
		{
			commitCollection((ContentCollectionEdit) o);
		}

		return props;

	} // addProperty

	/**
	* check permissions for removeProperty().
	* @param id The id of the new resource.
	* @return true if the user is allowed to removeProperty(id), false if not.
	*/
	public boolean allowRemoveProperty(String id)
	{
		return unlockCheck(EVENT_RESOURCE_WRITE, id);

	} // allowRemoveProperty

	/**
	* Remove a property from a resource, either collection or resource.
	* @param id The resource id.
	* @param name The property name to be removed from the resource.
	* @exception PermissionException if the user does not have premissions to write properties on this object or read through containing collections.
	* @exception IdUnusedException if the resource id is not found.
	* @exception TypeException if the property named cannot be removed.
	* @exception InUseException if the resource is locked by someone else.
	* @return the ResourceProperties object for this resource.
	*/
	public ResourceProperties removeProperty(String id, String name)
		throws PermissionException, IdUnusedException, TypeException, InUseException
	{
		unlock(EVENT_RESOURCE_WRITE, id);

		boolean collectionHint = id.endsWith(Resource.SEPARATOR);
		Edit o = null;
		if (collectionHint)
		{
			o = editCollection(id);
		}
		else
		{
			o = editResource(id);
		}

		// unlikely, but...
		if (o == null)
			throw new IdUnusedException(id);

		// get the properties
		ResourcePropertiesEdit props = o.getPropertiesEdit();

		// check for TypeException updating live properties
		if (props.isLiveProperty(name))
			throw new TypeException(name);

		// remove the property
		props.removeProperty(name);

		// commit the change
		if (o instanceof ContentResourceEdit)
		{
			commitResourceEdit((ContentResourceEdit) o, NotificationService.NOTI_NONE);
		}
		if (o instanceof ContentCollectionEdit)
		{
			commitCollection((ContentCollectionEdit) o);
		}

		return props;

	} // removeProperty

	/**
	* Access the resource URL from a resource id.
	* @param id The resource id.
	* @return The resource URL.
	*/
	public String getUrl(String id)
	{
		// escape just the is part, not the access point
		return getAccessPoint(false) + Validator.escapeUrl(id);

	} // getUrl

	/**
	* Access the internal reference from a resource id.
	* @param id The resource id.
	* @return The internal reference from a resource id.
	*/
	public String getReference(String id)
	{
		return getAccessPoint(true) + id;

	} // getReference

	/**
	* Access the resource id of the collection which contains this collection or resource.
	* @param id The resource id (reference, or URL) of the ContentCollection or ContentResource
	* @return the resource id (reference, or URL, depending on the id parameter) of the collection which contains this resource.
	*/
	public String getContainingCollectionId(String id)
	{
		return isolateContainingId(id);

	} // getContainingCollectionId

	/**
	* Get the depth of the resource/collection object in the hireachy based on the given collection id
	* @param resourceId The Id of the resource/collection object to be tested 
	* @param baseCollectionId The Id of the collection as the relative root level
	* @return the integer value reflecting the relative hierarchy depth of the test resource/collection object based on the given base collection level
	*/
	public int getDepth(String resourceId, String baseCollectionId)
	{
		if (resourceId.indexOf(baseCollectionId) == -1)
		{
			// the resource object is not a member of base collection 
			return -1;
		}
		else
		{
			int i = 1;
			// the resource object is a member of base collection
			String s = resourceId.substring(baseCollectionId.length());
			while (s.indexOf(Resource.SEPARATOR) != -1)
			{
				if (s.indexOf(Resource.SEPARATOR) != (s.length() - 1))
				{
					// the resource seperator character is not the last character
					i++;
					s = s.substring(s.indexOf(Resource.SEPARATOR) + 1);
				}
				else
				{
					s = "";
				}
			}
			return i;
		}

	} // getDepth

	/**
	* Test if this id (reference, or URL) refers to the root collection.
	* @param id The resource id (reference, or URL) of a ContentCollection
	* @return true if this is the root collection
	*/
	public boolean isRootCollection(String id)
	{
		// test for the root local id
		if (id.equals(Resource.SEPARATOR))
			return true;

		// test for the root reference
		if (id.equals(getReference(Resource.SEPARATOR)))
			return true;

		// test for the root URL
		if (id.equals(getUrl(Resource.SEPARATOR)))
			return true;

		return false;

	} // isRootCollection

	/**
	* Construct a stand-alone, not associated with any particular resource,
	* ResourceProperties object.
	* @return The new ResourceProperties object.
	*/
	public ResourcePropertiesEdit newResourceProperties()
	{
		return new BaseResourcePropertiesEdit();

	} // newResourceProperties

	/*******************************************************************************
	* ResourceService implementation
	*******************************************************************************/

	/**
	* @return a short string identifying the resources kept here, good for a file name or label.
	*/
	public String getLabel()
	{
		return "content";
	}

	/**
	 * {@inheritDoc}
	 */
	public String archive(String siteId, Document doc, Stack stack, String archivePath, ReferenceVector attachments)
	{
		// prepare the buffer for the results log
		StringBuffer results = new StringBuffer();

		// start with an element with our very own name
		Element element = doc.createElement(ContentHostingService.SERVICE_NAME);
		((Element) stack.peek()).appendChild(element);
		stack.push(element);

		// the root collection for the site
		String siteCollectionId = getSiteCollection(siteId);

		try
		{
			// get the collection for the site
			ContentCollection collection = getCollection(siteCollectionId);

			archiveCollection(collection, doc, stack, archivePath, siteCollectionId, results);
		}
		catch (Exception any)
		{
			results.append("Error archiving collection from site: " + siteId + " " + any.toString() + "\n");
		}

		stack.pop();

		return results.toString();

	} // archive

	/**
	 * {@inheritDoc}
	 */
	public String archiveResources(ReferenceVector attachments, Document doc, Stack stack, String archivePath)
	{
		// prepare the buffer for the results log
		StringBuffer results = new StringBuffer();

		// start with an element with our very own name
		Element element = doc.createElement(ContentHostingService.SERVICE_NAME);
		((Element) stack.peek()).appendChild(element);
		stack.push(element);

		for (Iterator i = attachments.iterator(); i.hasNext();)
		{
			Reference ref = (Reference) i.next();
			try
			{
				ContentResource resource = (ContentResource) ref.getResource();

				if (resource != null)
				{
					results.append(
						archiveResource(resource, doc, stack, archivePath, null));
				}
			}
			catch (Exception any)
			{
				results.append("Error archiving resource: " + ref + " " + any.toString() + "\n");
				m_logger.warn(this +".archveResources: exception archiving resource: " + ref + ": ", any);
			}
		}

		stack.pop();

		return results.toString();
	}
	
	/**
	* Replace the WT user id with the new qualified id
	* @param el The XML element holding the perproties
	* @param useIdTrans The HashMap to track old WT id to new CTools id
	 */
	protected void WTUserIdTrans(Element el, HashMap userIdTrans)
	{
		NodeList children4 = el.getChildNodes();
		int length4 = children4.getLength();
		for(int i4 = 0; i4 < length4; i4++)
		{
			Node child4 = children4.item(i4);
			if (child4.getNodeType() == Node.ELEMENT_NODE)
			{
				Element element4 = (Element)child4;
				if (element4.getTagName().equals("property"))
				{
					String creatorId = "";
					String modifierId = "";
					if (element4.hasAttribute("CHEF:creator"))
					{
						if ("BASE64".equalsIgnoreCase(element4.getAttribute("enc")))
						{
							creatorId = Xml.decodeAttribute(element4, "CHEF:creator");
						}
						else
						{
							creatorId = element4.getAttribute("CHEF:creator");
						}
						String newCreatorId = (String)userIdTrans.get(creatorId);
						if (newCreatorId != null)
						{
							Xml.encodeAttribute(element4, "CHEF:creator", newCreatorId);
							element4.setAttribute("enc","BASE64");
						}															
					}
					else if (element4.hasAttribute("CHEF:modifiedby"))
					{
						if ("BASE64".equalsIgnoreCase(element4.getAttribute("enc")))
						{
							modifierId = Xml.decodeAttribute(element4, "CHEF:modifiedby");
						}
						else
						{
							modifierId = element4.getAttribute("CHEF:modifiedby");
						}
						String newModifierId = (String)userIdTrans.get(modifierId);
						if (newModifierId != null)
						{
							Xml.encodeAttribute(element4, "CHEF:creator", newModifierId);
							element4.setAttribute("enc","BASE64");
						}
					}
				}
			}
		}
		
	} // WTUserIdTrans
	

	/**
	* Merge the resources from the archive into the given site.
	* @param siteId The id of the site getting imported into.
	* @param root The XML DOM tree of content to merge.
	* @param archviePath The path to the folder where we are reading auxilary files.
	* @return A log of status messages from the archive.
	*/
	public String merge(String siteId, Element root, String archivePath, String mergeId, Map attachmentNames, HashMap userIdTrans, Set userListAllowImport)
	{
		// get the system name: FROM_WT, FROM_CT, FROM_SAKAI
		String source = "";
		// root: <service> node			
		Node parent = root.getParentNode(); // parent: <archive> node containing "system"
		if (parent.getNodeType() == Node.ELEMENT_NODE)
		{
			Element parentEl = (Element)parent;
			source = parentEl.getAttribute("system");
		}
		
		// prepare the buffer for the results log
		StringBuffer results = new StringBuffer();

		try
		{
			NodeList children = root.getChildNodes();
			final int length = children.getLength();
			for (int i = 0; i < length; i++)
			{
				Node child = children.item(i);
				if (child.getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element element = (Element) child;

				// for "collection" kids
				if (element.getTagName().equals("collection"))
				{
					// replace the WT userid when needed
					if (!userIdTrans.isEmpty())
					{
						// replace the WT user id with new user id
						WTUserIdTrans(element, userIdTrans);
					}
					
					// from the relative id form a full id for the target site,
					// updating the xml element
					String relId = StringUtil.trimToNull(element.getAttribute("rel-id"));
					if (relId == null)
					{
						// Note: the site's root collection will have a "" rel-id, which will be null.
						continue;
					}
					String id = getSiteCollection(siteId) + relId;
					element.setAttribute("id", id);

					// collection: add if missing, else merge in
					ContentCollection c = mergeCollection(element);
					if (c == null)
					{
						results.append("collection: " + id + " already exists and was not replaced.\n");
					}
					else
					{
						results.append("collection: " + id + " imported.\n");
					}
				}

				// for "resource" kids
				else if (element.getTagName().equals("resource"))
				{
					// a flag showing if continuing merging this resource
					boolean goAhead = true; 
					
					// check if the person who last modified this source has the right role
					// if not, set the goAhead flag to be false when fromSakai or fromCT
					if (source.equalsIgnoreCase(ArchiveService.FROM_SAKAI)
					|| source.equalsIgnoreCase(ArchiveService.FROM_CT))
					{
						NodeList children2 = element.getChildNodes();
						int length2 = children2.getLength();
						for (int i2 = 0; i2 < length2; i2++)
						{
							Node child2 = children2.item(i2);
							if (child2.getNodeType() == Node.ELEMENT_NODE)
							{
								Element element2 = (Element)child2;

								// get the "channel" child
								if (element2.getTagName().equals("properties"))
								{
									NodeList children3 = element2.getChildNodes();
									final int length3 = children3.getLength();
									for(int i3 = 0; i3 < length3; i3++)
									{
										Node child3 = children3.item(i3);
										if (child3.getNodeType() == Node.ELEMENT_NODE)
										{
											Element element3 = (Element)child3;
		
											// for "message" children
											if (element3.getTagName().equals("property"))
											{	
												if (element3.getAttribute("name").equalsIgnoreCase("CHEF:modifiedby"))
												{
													if ("BASE64".equalsIgnoreCase(element3.getAttribute("enc")))
													{
														String creatorId = Xml.decodeAttribute(element3, "value");
														if (!userListAllowImport.contains(creatorId)) goAhead = false;
													}
													else
													{
														String creatorId = element3.getAttribute("value");
														if (!userListAllowImport.contains(creatorId)) goAhead = false;
													}
												}
											}
										}
									}
								}
							}
						}
					} // the end to if fromSakai or fromCT
					
					if (goAhead)
					{
						// replace the WT userid when needed
						if (!userIdTrans.isEmpty())
						{
							// replace the WT user id with new user id
							WTUserIdTrans(element, userIdTrans);
						}
						
						// from the relative id form a full id for the target site,
						// updating the xml element
						String id = StringUtil.trimToNull(element.getAttribute("id"));
						String relId = StringUtil.trimToNull(element.getAttribute("rel-id"));
						
						// escape the invalid characters
						id = Validator.escapeQuestionMark(id);
						relId = Validator.escapeQuestionMark(relId);
						
						// if it's attachment, assign a new attachment folder
						if (id.startsWith(ATTACHMENTS_COLLECTION))
						{
							String oldRef = getReference(id);
							
							// take the name from after /attachment/whatever/
							id = ATTACHMENTS_COLLECTION + IdService.getUniqueId() +
									id.substring(id.indexOf('/', ATTACHMENTS_COLLECTION.length()));
	
							// record the rename
							attachmentNames.put(oldRef, id);
						}
	
						// otherwise move it into the site
						else
						{
							if (relId == null)
							{
								m_logger.warn(this +".mergeContent(): no rel-id attribute in resource");
								continue;
							}
	
							id = getSiteCollection(siteId) + relId;
						}
	
						element.setAttribute("id", id);
	
						ContentResource r = null;
	
						// if the body-location attribute points at another file for the body, get this
						String bodyLocation = StringUtil.trimToNull(element.getAttribute("body-location"));
						if (bodyLocation != null)
						{
							// the file name is relative to the archive file
							String bodyPath = StringUtil.fullReference(archivePath, bodyLocation);
	
							// get a stream from the file
							FileInputStream in = new FileInputStream(bodyPath);
	
							// read the bytes 
							Blob body = new Blob();
							body.read(in);
	
							// 	resource: add if missing
							r = mergeResource(element, body.getBytes());
						}
	
						else
						{
							// 	resource: add if missing
							r = mergeResource(element);
						}
	
						if (r == null)
						{
							results.append("resource: " + id + " already exists and was not replaced.\n");
						}
						else
						{
							results.append("resource: " + id + " imported.\n");
						}
					}
				}
			}
		}
		catch (Exception any)
		{
			results.append("import interrputed: " + any.toString() + "\n");
			m_logger.warn(this +".mergeContent(): exception: ", any);
		}

		return results.toString();

	} // merge

	/**
	* import tool(s) contents from the source context into the destination context
	* @param fromContext The source context
	* @param toContext The destination context
	* @param resourceIds when null, all resources will be imported; otherwise, only resources with those ids will be imported
	*/
	public void importResources(String fromContext, String toContext, List resourceIds)
	{
		// default to import all resources
		boolean toBeImported = true;
		
		// get the list of all resources for importing
		try
		{
			// get the root collection
			ContentCollection oCollection = getCollection(fromContext);
			
			// Get the collection members from the 'new' collection
			List oResources = oCollection.getMemberResources ();
			for (int i=0; i < oResources.size(); i++)
			{
				// get the original resource
				Resource oResource = (Resource) oResources.get(i);
				String oId = oResource.getId();
				
				if (resourceIds != null && resourceIds.size() > 0)
				{
					// only import those with ids inside the list
					toBeImported = false;
					for (int j = 0; j < resourceIds.size() && !toBeImported; j++)
					{
						if (((String) resourceIds.get(j)).equals(oId))
						{
							toBeImported = true;
						}
					}
				}
				
				if (toBeImported)
				{
					String oId2 = oResource.getId();
					String nId = "";
					
					int ind = oId2.indexOf(fromContext);
					if (ind != -1)
					{
						String str1 = "";
						String str2 = "";
						if (ind != 0)
						{
							// the substring before the fromContext string
							str1 = oId2.substring(0, ind);
						}
						if (!((ind + fromContext.length()) > oId2.length()))
						{
							// the substring after the fromContext string
							str2 = oId2.substring(ind + fromContext.length(), oId2.length());
						}
						// get the new resource id; fromContext is replaced with toContext					
						nId = str1 + toContext + str2;
					}
					
					ResourceProperties oProperties = oResource.getProperties();
					boolean isCollection = false;
					try
					{
						isCollection = oProperties.getBooleanProperty(ResourceProperties.PROP_IS_COLLECTION);
					}
					catch (EmptyException e) {}
					catch (TypeException e) {}
					
					if (isCollection)
					{
						// add collection
						try
						{
							ContentCollectionEdit edit = addCollection(nId);
							//import properties
							ResourcePropertiesEdit p = edit.getPropertiesEdit();
							p.clear();
							p.addAll(oProperties);
							// complete the edit
							m_storage.commitCollection(edit);
						}
						catch (IdUsedException e) {}
						catch (IdInvalidException e) {}
						catch (PermissionException e) {}
						catch (InconsistentException e) {}
						
						importResources(oResource.getId(), nId, resourceIds);
					}
					else
					{
						try
						{
							// add resource
							ContentResourceEdit edit = addResource(nId);
							edit.setContentType(((ContentResource) oResource).getContentType());
							edit.setContent(((ContentResource) oResource).getContent());
							//import properties
							ResourcePropertiesEdit p = edit.getPropertiesEdit();
							p.clear();
							p.addAll(oProperties);
							// complete the edit
							m_storage.commitResource(edit);
						}
						catch (PermissionException e) {}
						catch (IdUsedException e) {}
						catch (IdInvalidException e) {}
						catch (InconsistentException e) {}
					}	// if
				}	// if
			}	// for
		}
		catch (IdUnusedException e){}
		catch (TypeException e){}
		catch (PermissionException e){}
		
	}	// importResources
	
	/*******************************************************************************
	* etc
	*******************************************************************************/

	/**
	* Archive the collection, then the members of the collection - recursively for collection members.
	* @param collection The collection whose members are to be archived.
	* @param doc The document to contain the xml.
	* @param stack The stack of elements, the top of which will be the containing element of the "collection" or "resource" element.
	* @param storagePath The path to the folder where we are writing files.
	* @param siteCollectionId The resource id of the site collection.
	* @param results A log of messages from the archive.
	*/
	protected void archiveCollection(
		ContentCollection collection,
		Document doc,
		Stack stack,
		String storagePath,
		String siteCollectionId,
		StringBuffer results)
	{
		// first the collection
		Element el = collection.toXml(doc, stack);

		// store the relative file id in the xml
		el.setAttribute("rel-id", collection.getId().substring(siteCollectionId.length()));

		results.append("archiving collection: " + collection.getId() + "\n");

		// now each member
		List members = collection.getMemberResources();
		if ((members == null) || (members.size() == 0))
			return;
		for (int i = 0; i < members.size(); i++)
		{
			Object member = members.get(i);
			if (member instanceof ContentCollection)
			{
				archiveCollection((ContentCollection) member, doc, stack, storagePath, siteCollectionId, results);
			}
			else if (member instanceof ContentResource)
			{
				results.append(
					archiveResource((ContentResource) member, doc, stack, storagePath, siteCollectionId));
			}
		}

	} // archiveCollection

	/**
	 * Archive a singe resource
	 * @param resource The content resource to archive
	 * @param doc The XML document.
	 * @param stack The stack of elements.
	 * @param storagePath The path to the folder where we are writing files.
	 * @param siteCollectionId The resource id of the site collection (optional).
	 * @return A log of messages from the archive.
	 */
	protected String archiveResource(ContentResource resource, Document doc, Stack stack, String storagePath, String siteCollectionId)
	{
		// get the content bytes
		byte[] content = resource.getContent();

		// form the xml
		Element el = resource.toXml(doc, stack);
 
		// remove the content from the xml
		el.removeAttribute("body");

		// write the content to a file
		String fileName = IdService.getUniqueId();
		Blob b = new Blob();
		b.append(content);
		try
		{
			FileOutputStream out = new FileOutputStream(storagePath + fileName);
			b.write(out);
			out.close();
		}
		catch (Exception e)
		{
			m_logger.warn(this +".archiveResource(): while writing body for: " + resource.getId() + " : " + e);
		}

		// store the file name in the xml
		el.setAttribute("body-location", fileName);

		// store the relative file id in the xml
		if (siteCollectionId != null)
		{
			el.setAttribute("rel-id", resource.getId().substring(siteCollectionId.length()));
		}

		return "archiving resource: " + resource.getId() + " body in file: " + fileName + "\n";
	}

	/**
	* Merge in a collection from an XML DOM definition.
	* Take whole if not defined already.  Ignore if already here.
	* @param element The XML DOM element containing the collection definition.
	* @exception PermissionException if the user does not have permission to add a collection.
	* @exception InconsistentException if the containing collection does not exist.
	* @exception IdInvalidException if the id is not valid.
	* @return a new ContentCollection object, or null if it was not created.
	*/
	protected ContentCollection mergeCollection(Element element)
		throws PermissionException, InconsistentException, IdInvalidException
	{
		// read the collection object
		BaseCollectionEdit collectionFromXml = new BaseCollectionEdit(element);
		String id = collectionFromXml.getId();

		// add it
		BaseCollectionEdit edit = null;
		try
		{
			edit = (BaseCollectionEdit) addCollection(id);
		}
		catch (IdUsedException e)
		{
			// ignore if it exists
			return null;
		}

		// transfer from the XML read object to the edit
		edit.set(collectionFromXml);

		// setup the event
		edit.setEvent(EVENT_RESOURCE_ADD);

		// commit the change
		commitCollection(edit);

		return edit;

	} // mergeCollection

	/**
	* Merge in a resource from an XML DOM definition.
	* Ignore if already defined.  Take whole if not.
	* @param element The XML DOM element containing the collection definition.
	* @exception PermissionException if the user does not have permission to add a resource.
	* @exception InconsistentException if the containing collection does not exist.
	* @exception IdInvalidException if the id is not valid.
	* @exception OverQuotaException if this would result in being over quota.
	* @return a new ContentResource object, or null if it was not created.
	*/
	protected ContentResource mergeResource(Element element)
		throws PermissionException, InconsistentException, IdInvalidException, OverQuotaException
	{
		return mergeResource(element, null);

	} // mergeResource

	/**
	* Merge in a resource from an XML DOM definition and a body bytes array.
	* Ignore if already defined.  Take whole if not.
	* @param element The XML DOM element containing the collection definition.
	* @param body The body bytes.
	* @exception PermissionException if the user does not have permission to add a resource.
	* @exception InconsistentException if the containing collection does not exist.
	* @exception IdInvalidException if the id is not valid.
	* @exception OverQuotaException if this would result in being over quota.
	* @return a new ContentResource object, or null if it was not created.
	*/
	protected ContentResource mergeResource(Element element, byte[] body)
		throws PermissionException, InconsistentException, IdInvalidException, OverQuotaException
	{
		// make the resource object
		BaseResourceEdit resourceFromXml = new BaseResourceEdit(element);
		String id = resourceFromXml.getId();

		// get it added
		BaseResourceEdit edit = null;
		try
		{
			edit = (BaseResourceEdit) addResource(id);
		}
		catch (IdUsedException e)
		{
			// ignore the add if it exists already
			return null;
		}

		// transfer the items of interest (content type, properties) from the XML read object to the edit.
		edit.setContentType(resourceFromXml.getContentType());
		ResourcePropertiesEdit p = edit.getPropertiesEdit();
		p.clear();
		p.addAll(resourceFromXml.getProperties());

		// if body is provided, use it
		if (body != null)
		{
			edit.setContent(body);
		}

		// setup the event
		edit.setEvent(EVENT_RESOURCE_ADD);

		// commit the change - Note: we do properties differently
		assureResourceProperties(edit);

		// check for over quota.
		if (overQuota(edit))
		{
			throw new OverQuotaException(edit.getReference());
		}

		// complete the edit
		m_storage.commitResource(edit);

		// track it
		EventTrackingService.post(
			EventTrackingService.newEvent(((BaseResourceEdit) edit).getEvent(), edit.getReference(), true, NotificationService.NOTI_NONE));

		// close the edit object
		 ((BaseResourceEdit) edit).closeEdit();

		return edit;

	} // mergeResource

	/**
	* Find the containing collection id of a given resource id.
	* @param id The resource id.
	* @return the containing collection id.
	*/
	protected String isolateContainingId(String id)
	{
		// take up to including the last resource path separator, not counting one at the very end if there
		return id.substring(0, id.lastIndexOf('/', id.length() - 2) + 1);

	} // isolateContainingId

	/**
	* Find the resource name of a given resource id.
	* @param id The resource id.
	* @return the resource name.
	*/
	protected String isolateName(String id)
	{
		if (id == null)
			return null;
		if (id.length() == 0)
			return null;

		// take after the last resource path separator, not counting one at the very end if there
		boolean lastIsSeparator = id.charAt(id.length() - 1) == '/';
		return id.substring(id.lastIndexOf('/', id.length() - 2) + 1, (lastIsSeparator ? id.length() - 1 : id.length()));

	} // isolateName

	/**
	* Check the fixed type and id infomation:
	* The same or better content type based on the known type for this id's extension, if any.
	* The same or added extension id based on the know MIME type, if any
	* Only if the type is the unknown type already.
	* @param id The resource id with possible file extension to check.
	* @param type The content type.
	* @return the best guess content type based on this resource's id and resource id with extension based on this resource's MIME type.
	*/
	protected Hashtable fixTypeAndId(String id, String type)
	{
		// the Hashtable holds the id and mime type
		Hashtable extType = new Hashtable();
		extType.put("id", id);
		if (type == null)
			type = "";
		extType.put("type", type);
		String extension = Validator.getFileExtension(id);

		if (extension.length() != 0)
		{
			// if there's a file extension and a blank, null or unknown(application/binary) mime type, 
			//fix the mime type by doing a lookup based on the extension
			if (((type == null) || (type.length() == 0) || (ContentTypeImageService.isUnknownType(type))))
			{
				extType.put("type", ContentTypeImageService.getContentType(extension));
			}
		}
		else
		{
			// if there is no file extension, but a non-null, non-blank mime type, do a lookup based on the mime type and add an extension
			// if there is no extension, find one according to the MIME type and add it.
			if ((type != null) && (!type.equals("")) && (!ContentTypeImageService.isUnknownType(type)))
			{
				extension = ContentTypeImageService.getContentTypeExtension(type);
				if (extension.length() > 0)
				{
					id = id + "." + extension;
					extType.put("id", id);
				}
			}
			else
			{
				// if mime type is null or mime type is empty or mime and there is no extension
				if ((type == null) || (type.equals("")))
				{
					extType.put("type", "application/binary");
				}
				id = id + ".bin";
				extType.put("id", id);
			}
		}

		return extType;

	} // fixTypeAndId

	/**
	* Test if this resource edit would place the account" over quota.
	* @param edit The proposed resource edit.
	* @return true if this change would palce the "account" over quota, false if not.
	*/
	protected boolean overQuota(ContentResourceEdit edit)
	{
		// Note: This implementation is hard coded to just check for a quota in the "/user/" 
		//       or "/group/" area. -ggolden

		// Note: this does NOT count attachments (/attachments/*) nor dropbox (/group-user/<site id>/<user id>/*) -ggolden

		// quick exits if we are not doing site quotas
		//if (m_siteQuota == 0)
		//	return false;

		long quota = 0;
		
		// use this quota unless we have one more specific 		
		quota = m_siteQuota;

		// some quick exits, if we are not doing user quota, or if this is not a user or group resource
		// %%% These constants should be from somewhere else -ggolden
		if (!((edit.getId().startsWith("/user/")) || (edit.getId().startsWith("/group/"))))
			return false;

		// expect null, "user" | "group", user/groupid, rest...
		String[] parts = StringUtil.split(edit.getId(), Resource.SEPARATOR);
		if (parts.length <= 2)
			return false;

		// get this collection
		String id = Resource.SEPARATOR + parts[1] + Resource.SEPARATOR + parts[2] + Resource.SEPARATOR;
		ContentCollection collection = null;
		try
		{
			collection = findCollection(id);
		}
		catch (TypeException ignore)
		{
		}

		if (collection == null)
			return false;

		// see if this collection has a quota property
		try
		{
			long siteSpecific = collection.getProperties().getLongProperty(ResourceProperties.PROP_COLLECTION_BODY_QUOTA);
			if (siteSpecific == 0)
				return false;

			quota = siteSpecific;
		}
		catch (EmptyException ignore)
		{
			// don't log or anything, this just means that this site doesn't have this quota property.
		}
		catch (Exception ignore)
		{
			m_logger.warn(this +".overQuota: reading quota property of : " + collection.getId() + " : " + ignore);			
		}

		if (quota == 0)
		{
			return false;
		}
		
		// get the content size of all resources in this hierarchy
		long size = ((BaseCollectionEdit) collection).getBodySizeK();

		// find the resource being edited
		ContentResource inThere = null;
		try
		{
			inThere = findResource(edit.getId());
		}
		catch (TypeException ignore)
		{
		}

		if (inThere != null)
		{
			// reduce the size by the existing size
			size -= bytes2k(inThere.getContentLength());
		}

		// add in the new size
		size += bytes2k(edit.getContentLength());

		return (size >= quota);

	} // overQuota

	/**
	* Convert bytes to Kbytes, rounding up, and counting even 0 bytes as 1 k.
	* @param bytes The size in bytes.
	* @return The size in Kbytes, rounded up.
	*/
	protected long bytes2k(long bytes)
	{
		return ((bytes - 1) / 1024) + 1;

	} // bytes2k

	/**
	* Attempt to create any collections needed so that the parameter collection exists.
	* @param target The collection that we want to exist.
	*/
	protected void generateCollections(String target)
	{
		try
		{
			// check each collection from the root
			String[] parts = StringUtil.split(target, "/");
			String id = "/";

			for (int i = 1; i < parts.length; i++)
			{
				// grow the id to the next collection
				id = id + parts[i] + "/";

				// does it exist?
				ContentCollection collection = findCollection(id);

				// if not, can we make it
				if (collection == null)
				{
					ContentCollectionEdit edit = addCollection(id);
					edit.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, parts[i]);
					commitCollection(edit);
				}
			}
		}
		// if we cannot, give up
		catch (Exception any)
		{
		}

	} // generateCollections

	/**
	 * {@inheritDoc}
	 */
	public String getSiteCollection(String siteId)
	{
		String rv = null;

		if (SiteService.isUserSite(siteId))
		{
			rv = COLLECTION_USER + SiteService.getSiteUserId(siteId) + "/";
		}

		else if (!SiteService.isSpecialSite(siteId))
		{
			rv = COLLECTION_SITE + siteId + "/";
		}
		
		else
		{
			// ???
			rv = "/";
		}
		
		return rv;
	}

	/*******************************************************************************
	* ContentCollection implementation
	*******************************************************************************/

	public class BaseCollectionEdit implements ContentCollectionEdit, SessionStateBindingListener
	{
		/** Store the resource id */
		protected String m_id = null;

		/** The properties. */
		protected ResourcePropertiesEdit m_properties = null;

		/** The event code for this edit. */
		protected String m_event = null;

		/** Active flag. */
		protected boolean m_active = false;

		/** When true, the collection has been removed. */
		protected boolean m_isRemoved = false;

		/**
		* Construct with an id.
		* @param id The unique channel id.
		*/
		public BaseCollectionEdit(String id)
		{
			// set the id
			m_id = id;

			// setup for properties
			m_properties = new BaseResourcePropertiesEdit();

		} // BaseCollectionEdit

		/**
		* Construct as a copy of another.
		* @param other The other to copy.
		*/
		public BaseCollectionEdit(ContentCollection other)
		{
			set(other);

		} // BaseCollectionEdit

		/**
		* Construct from info in XML in a DOM element.
		* @param el The XML DOM element.
		*/
		public BaseCollectionEdit(Element el)
		{
			// setup for properties
			m_properties = new BaseResourcePropertiesEdit();

			m_id = el.getAttribute("id");

			// the children (properties)
			NodeList children = el.getChildNodes();
			final int length = children.getLength();
			for (int i = 0; i < length; i++)
			{
				Node child = children.item(i);
				if (child.getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element element = (Element) child;

				// look for properties
				if (element.getTagName().equals("properties"))
				{
					// re-create properties
					m_properties = new BaseResourcePropertiesEdit(element);
				}
			}

		} // BaseCollectionEdit

		/**
		* Take all values from this object.
		* @param user The other object to take values from.
		*/
		protected void set(ContentCollection other)
		{
			// set the id
			m_id = other.getId();

			// setup for properties
			m_properties = new BaseResourcePropertiesEdit();
			m_properties.addAll(other.getProperties());

		} // set

		/**
		* Clean up.
		*/
		protected void finalize()
		{
			// catch the case where an edit was made but never resolved
			if (m_active)
			{
				cancelCollection(this);
			}

		} // finalize

		/**
		* Access the URL which can be used to access the resource.
		* @return The URL which can be used to access the resource.
		*/
		public String getUrl()
		{
			return getAccessPoint(false) + m_id;

		} // getUrl

		/**
		* Access the internal reference which can be used to access the resource from within the system.
		* @return The the internal reference which can be used to access the resource from within the system.
		*/
		public String getReference()
		{
			return getAccessPoint(true) + m_id;

		} // getReference

		/**
		* Access the id of the resource.
		* @return The id.
		*/
		public String getId()
		{
			return m_id;

		} // getId

		/**
		* Access a List of the collection's internal members, each a resource id string.
		* @return a List of the collection's internal members, each a resource id string
		* (may be empty).
		*/
		public List getMembers()
		{
			// get the objects
			List memberResources = getMemberResources();

			// form the list of just ids
			List mbrs = new Vector();
			for (int i = 0; i < memberResources.size(); i++)
			{
				Resource res = (Resource) memberResources.get(i);
				if (res != null)
				{
					mbrs.add(res.getId());
				}
			}

			if (mbrs.size() == 0)
				return mbrs;

			// sort? %%%
			// Collections.sort(mbrs);

			return mbrs;

		} // getMembers

		/**
		* Access the size of all the resource body bytes within this collection in Kbytes.
		* @return The size of all the resource body bytes within this collection in Kbytes.
		*/
		public long getBodySizeK()
		{
			long size = 0;

			// get the member objects
			List members = getMemberResources();

			// for each member
			for (Iterator it = members.iterator(); it.hasNext();)
			{
				Object obj = it.next();
				if (obj == null)
					continue;

				// if a resource, add the body size
				if (obj instanceof ContentResource)
				{
					size += bytes2k(((ContentResource) obj).getContentLength());
				}

				// if a collection, count it's size
				else
				{
					size += ((BaseCollectionEdit) obj).getBodySizeK();
				}
			}

			//if (m_logger.isDebugEnabled())
			//	m_logger.debug(this + ".getBodySizeK(): collection: " + getId() + " size: " + size);

			return size;

		} // getBodySizeK

		/**
		* Access a List of the collections' internal members as full ContentResource or
		* ContentCollection objects.
		* @return a List of the full objects of the members of the collection.
		*/
		public List getMemberResources()
		{
			List mbrs = new Vector();

			// if not caching
			if ((!m_caching) || (m_cache == null) || (m_cache.disabled()))
			{
				// TODO: current service caching
				mbrs = m_storage.getCollections(this);
				mbrs.addAll(m_storage.getResources(this));
			}

			else
			{
				// if the cache is complete for this collection, use it
				if (m_cache.isComplete(getReference()))
				{
					// get just this collection's members
					mbrs = m_cache.getAll(getReference());
				}

				// otherwise get all the members from storage
				else
				{
					// Note: while we are getting from storage, storage might change.  These can be processed
					// after we get the storage entries, and put them in the cache, and mark the cache complete.
					// -ggolden
					synchronized (m_cache)
					{
						// if we were waiting and it's now complete...
						if (m_cache.isComplete(getReference()))
						{
							// get just this collection's members
							mbrs = m_cache.getAll(getReference());
						}
						else
						{
							// save up any events to the cache until we get past this load
							m_cache.holdEvents();

							// read from storage - resources and collections, but just those
							// whose path is this's path (i.e. just mine!)
							mbrs = m_storage.getCollections(this);
							mbrs.addAll(m_storage.getResources(this));

							// update the cache, and mark it complete
							for (int i = 0; i < mbrs.size(); i++)
							{
								Resource mbr = (Resource) mbrs.get(i);
								m_cache.put(mbr.getReference(), mbr);
							}

							m_cache.setComplete(getReference());

							// now we are complete, process any cached events
							m_cache.processEvents();
						}
					}
				}
			}

			if (mbrs.size() == 0)
				return mbrs;

			// sort %%%
			// Collections.sort(mbrs);

			return mbrs;

		} // getMemberResources

		/**
		* Access the collection's properties.
		* @return The collection's properties.
		*/
		public ResourceProperties getProperties()
		{
			return m_properties;

		} // getProperties

		/**
		* Set the collection as removed.
		*/
		protected void setRemoved()
		{
			m_isRemoved = true;

		} // setRemoved

		/**
		* Clear all the members of the collection, all the way down.
		* Security has already been checked!
		*/
		protected void clear()
			throws IdUnusedException, PermissionException, InconsistentException, TypeException, InUseException
		{
			// get this collection's members
			List mbrs = getMemberResources();
			for (int i = 0; i < mbrs.size(); i++)
			{
				Object mbr = mbrs.get(i);
				if (mbr == null)
					continue;

				// for a contained collection, clear its members first - if any are in use, the show's over
				if (mbr instanceof ContentCollection)
				{
					((BaseCollectionEdit) mbr).clear();
				}

				// now remove this member
				if (mbr instanceof ContentCollection)
				{
					// if this is not allowed or in use, we throw and the show's over.
					removeCollection(((ContentCollection) mbr).getId());
				}
				else if (mbr instanceof ContentResource)
				{
					// if this is not allowed or in use, we throw and the show's over.
					removeResource(((ContentResource) mbr).getId());
				}
			}

		} // clear

		/**
		* Serialize the resource into XML, adding an element to the doc under the top of the stack element.
		* @param doc The DOM doc to contain the XML (or null for a string return).
		* @param stack The DOM elements, the top of which is the containing element of the new "resource" element.
		* @return The newly added element.
		*/
		public Element toXml(Document doc, Stack stack)
		{
			Element collection = doc.createElement("collection");

			if (stack.isEmpty())
			{
				doc.appendChild(collection);
			}
			else
			{
				((Element) stack.peek()).appendChild(collection);
			}

			stack.push(collection);

			collection.setAttribute("id", m_id);

			// properties
			m_properties.toXml(doc, stack);

			stack.pop();

			return collection;

		} // toXml

		/**
		* Access the event code for this edit.
		* @return The event code for this edit.
		*/
		protected String getEvent()
		{
			return m_event;
		}

		/**
		* Set the event code for this edit.
		* @param event The event code for this edit.
		*/
		protected void setEvent(String event)
		{
			m_event = event;
		}

		/**
		* Access the resource's properties for modification
		* @return The resource's properties.
		*/
		public ResourcePropertiesEdit getPropertiesEdit()
		{
			return m_properties;

		} // getPropertiesEdit

		/**
		* Enable editing.
		*/
		protected void activate()
		{
			m_active = true;

		} // activate

		/**
		* Check to see if the edit is still active, or has already been closed.
		* @return true if the edit is active, false if it's been closed.
		*/
		public boolean isActiveEdit()
		{
			return m_active;

		} // isActiveEdit

		/**
		* Close the edit object - it cannot be used after this.
		*/
		protected void closeEdit()
		{
			m_active = false;

		} // closeEdit

		/*******************************************************************************
		* SessionStateBindingListener implementation
		*******************************************************************************/

		/**
		* Accept notification that this object has been bound as a SessionState attribute.
		* @param sessionStateKey The id of the session state which holds the attribute.
		* @param attributeName The id of the attribute to which this object is now the value.
		*/
		public void valueBound(String sessionStateKey, String attributeName)
		{
		}

		/**
		* Accept notification that this object has been removed from a SessionState attribute.
		* @param sessionStateKey The id of the session state which held the attribute.
		* @param attributeName The id of the attribute to which this object was the value.
		*/
		public void valueUnbound(String sessionStateKey, String attributeName)
		{
			if (m_logger.isDebugEnabled())
				m_logger.debug(this +".valueUnbound()");

			// catch the case where an edit was made but never resolved
			if (m_active)
			{
				cancelCollection(this);
			}

		} // valueUnbound

	} // class BaseCollection

	/*******************************************************************************
	* ContentResource implementation
	*******************************************************************************/

	public class BaseResourceEdit implements ContentResourceEdit, SessionStateBindingListener
	{
		/** The resource id. */
		protected String m_id = null;

		/** The content type. */
		protected String m_contentType = null;

		/** The body. May be missing - not yet read (null)*/
		protected byte[] m_body = null;

		/** The content length of the body, consult only if the body is missing (null) */
		protected int m_contentLength = 0;

		/** The properties. */
		protected ResourcePropertiesEdit m_properties = null;

		/** The event code for this edit. */
		protected String m_event = null;

		/** Active flag. */
		protected boolean m_active = false;

		/** When true, the collection has been removed. */
		protected boolean m_isRemoved = false;

		/** When true, someone changed the body content with setContent() */
		protected boolean m_bodyUpdated = false;

		/** The file system path, post root, for file system stored body binary. */
		protected String m_filePath = null;

		/**
		* Construct.
		* @param id The local resource id.
		*/
		public BaseResourceEdit(String id)
		{
			m_id = id;

			// setup for properties
			m_properties = new BaseResourcePropertiesEdit();

			// allocate a file path if needed
			if (m_bodyPath != null)
			{
				setFilePath(TimeService.newTime());
			}

		} // BaseResourceEdit

		/**
		* Construct as a copy of another
		* @param other The other to copy.
		*/
		public BaseResourceEdit(ContentResource other)
		{
			set(other);

		} // BaseResourceEdit

		/**
		 * Set the file path for this resource
		 * @param time The time on which to based the path.
		 */
		protected void setFilePath(Time time)
		{
			// compute file path: use now in ms mod m_bodyVolumes.length to pick a volume from m_bodyVolumes (if defined)
			// add /yyyy/DDD/HH year / day of year / hour / and a unique id for the final file name.
			// Don't include the body path.
			String volume = "/";
			if ((m_bodyVolumes != null) && (m_bodyVolumes.length > 0))
			{
				volume += m_bodyVolumes[(int) (Math.abs(time.getTime()) % ((long) m_bodyVolumes.length))];
				volume += "/";
			}

			m_filePath = volume + time.toStringFilePath() + IdService.getUniqueId();
		}

		/**
		* Take all values from this object.
		* @param user The other object to take values from.
		*/
		protected void set(ContentResource other)
		{
			m_id = other.getId();
			m_contentType = other.getContentType();
			m_contentLength = other.getContentLength();

			// if there's a body in the other, reference it, else leave this one null
			// Note: this treats the body byte array as immutable, so to update it one
			//	*must* call setContent() not just getContent and mess with the bytes. -ggolden
			byte[] content = ((BaseResourceEdit) other).m_body;
			if (content != null)
			{
				m_contentLength = content.length;
				m_body = content;
			}

			m_filePath = ((BaseResourceEdit) other).m_filePath;

			// setup for properties
			m_properties = new BaseResourcePropertiesEdit();
			m_properties.addAll(other.getProperties());

		} // set

		/**
		* Construct from information in XML in a DOM element.
		* @param el The XML DOM element.
		*/
		public BaseResourceEdit(Element el)
		{
			m_properties = new BaseResourcePropertiesEdit();

			m_id = el.getAttribute("id");
			setContentType(StringUtil.trimToNull(el.getAttribute("content-type")));
			m_contentLength = 0;
			try
			{
				m_contentLength = Integer.parseInt(el.getAttribute("content-length"));
			}
			catch (Exception ignore)
			{
			}

			String enc = StringUtil.trimToNull(el.getAttribute("body"));
			if (enc != null)
			{
				byte[] decoded = Base64.decode(enc);
				m_body = new byte[m_contentLength];
				System.arraycopy(decoded, 0, m_body, 0, m_contentLength);
			}
			
			m_filePath = StringUtil.trimToNull(el.getAttribute("filePath"));

			// the children (properties)
			NodeList children = el.getChildNodes();
			final int length = children.getLength();
			for (int i = 0; i < length; i++)
			{
				Node child = children.item(i);
				if (child.getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element element = (Element) child;

				// look for properties
				if (element.getTagName().equals("properties"))
				{
					// re-create properties
					m_properties = new BaseResourcePropertiesEdit(element);
				}
			}

		} // BaseResourceEdit

		/**
		* Clean up.
		*/
		protected void finalize()
		{
			// catch the case where an edit was made but never resolved
			if (m_active)
			{
				cancelResource(this);
			}

		} // finalize

		/**
		* Access the URL which can be used to access the resource.
		* @return The URL which can be used to access the resource.
		*/
		public String getUrl()
		{
			return getAccessPoint(false) + m_id;

		} // getUrl

		/**
		* Access the internal reference which can be used to access the resource from within the system.
		* @return The the internal reference which can be used to access the resource from within the system.
		*/
		public String getReference()
		{
			return getAccessPoint(true) + m_id;

		} // getReference

		/**
		* Access the id of the resource.
		* @return The id.
		*/
		public String getId()
		{
			return m_id;

		} // getId

		/**
		* Access the content byte length.
		* @return The content byte length.
		*/
		public int getContentLength()
		{
			// if we have a body, use it's length
			if (m_body != null)
				return m_body.length;

			// otherwise, use the content length
			return m_contentLength;

		} // getContentLength

		/**
		* Access the resource MIME type.
		* @return The resource MIME type.
		*/
		public String getContentType()
		{
			return ((m_contentType == null) ? "" : m_contentType);

		} // getContentType

		/**
		* Access the content bytes of the resource.
		* @return An array containing the bytes of the resource's content.
		*/
		public byte[] getContent()
		{
			// return the body bytes
			byte[] rv = m_body;

			if ((rv == null) && (m_contentLength > 0))
			{
				// TODO: we do not store the body with the object, so as not to cache the body bytes -ggolden
				rv = m_storage.getResourceBody(this);
				// m_body = rv;
			}

			return rv;

		} // getContent

		/**
		 * Access the content as a stream.
		 * Please close the stream when done as it may be holding valuable system resources.
		 * @return an InputStream through which the bytes of the resource can be read.
		 */
		public InputStream streamContent() throws ServerOverloadException
		{
			InputStream rv = null;

			if (m_body != null)
			{
				rv = new ByteArrayInputStream(m_body);
			}
			else
			{
				rv = m_storage.streamResourceBody(this);
			}

			return rv;
		}

		/**
		* Access the resource's properties.
		* @return The resource's properties.
		*/
		public ResourceProperties getProperties()
		{
			return m_properties;

		} // getProperties

		/**
		* Set the resource as removed.
		*/
		protected void setRemoved()
		{
			m_isRemoved = true;

		} // setRemoved

		/**
		* Set the content byte length.
		* @param length The content byte length.
		*/
		public void setContentLength(int length)
		{
			m_contentLength = length;

		} // setContentLength

		/**
		* Set the resource MIME type.
		* @param type The resource MIME type.
		*/
		public void setContentType(String type)
		{
			type = (String) ((Hashtable) fixTypeAndId(getId(), type)).get("type");
			m_contentType = type;

		} // setContentType

		/**
		* Set the resource content.
		* @param content An array containing the bytes of the resource's content.
		*/
		public void setContent(byte[] content)
		{
			if (content == null)
			{
				try
				{
					throw new Exception();
				}
				catch (Exception e)
				{
					m_logger.warn(this +".setContent(): null content", e);
				}
				return;
			}

			// only if different
			if (StringUtil.different(content, m_body))
			{
				// take the new body and length
				m_body = content;
				m_contentLength = m_body.length;

				// mark me as having a changed body
				m_bodyUpdated = true;
			}

		} // setContent

		/**
		* Serialize the resource into XML, adding an element to the doc under the top of the stack element.
		* @param doc The DOM doc to contain the XML (or null for a string return).
		* @param stack The DOM elements, the top of which is the containing element of the new "resource" element.
		* @return The newly added element.
		*/
		public Element toXml(Document doc, Stack stack)
		{
			Element resource = doc.createElement("resource");

			if (stack.isEmpty())
			{
				doc.appendChild(resource);
			}
			else
			{
				((Element) stack.peek()).appendChild(resource);
			}

			stack.push(resource);

			resource.setAttribute("id", m_id);
			resource.setAttribute("content-type", m_contentType);

			// body  may not be loaded; if not use m_contentLength
			int contentLength = m_contentLength;
			if (m_body != null)
				contentLength = m_body.length;
			resource.setAttribute("content-length", Integer.toString(contentLength));

			if (m_filePath != null) resource.setAttribute("filePath", m_filePath);

			// if there's no body bytes (len = 0?) m_body will still be null, so just skip it
			if (m_body != null)
			{
				String enc = Base64.encode(m_body);
				resource.setAttribute("body", enc);
			}

			// properties
			m_properties.toXml(doc, stack);

			stack.pop();

			return resource;

		} // toXml

		/**
		* Access the event code for this edit.
		* @return The event code for this edit.
		*/
		protected String getEvent()
		{
			return m_event;
		}

		/**
		* Set the event code for this edit.
		* @param event The event code for this edit.
		*/
		protected void setEvent(String event)
		{
			m_event = event;
		}

		/**
		* Access the resource's properties for modification
		* @return The resource's properties.
		*/
		public ResourcePropertiesEdit getPropertiesEdit()
		{
			return m_properties;

		} // getPropertiesEdit

		/**
		* Enable editing.
		*/
		protected void activate()
		{
			m_active = true;

		} // activate

		/**
		* Check to see if the edit is still active, or has already been closed.
		* @return true if the edit is active, false if it's been closed.
		*/
		public boolean isActiveEdit()
		{
			return m_active;

		} // isActiveEdit

		/**
		* Close the edit object - it cannot be used after this.
		*/
		protected void closeEdit()
		{
			m_active = false;

		} // closeEdit

		/*******************************************************************************
		* SessionStateBindingListener implementation
		*******************************************************************************/

		/**
		* Accept notification that this object has been bound as a SessionState attribute.
		* @param sessionStateKey The id of the session state which holds the attribute.
		* @param attributeName The id of the attribute to which this object is now the value.
		*/
		public void valueBound(String sessionStateKey, String attributeName)
		{
		}

		/**
		* Accept notification that this object has been removed from a SessionState attribute.
		* @param sessionStateKey The id of the session state which held the attribute.
		* @param attributeName The id of the attribute to which this object was the value.
		*/
		public void valueUnbound(String sessionStateKey, String attributeName)
		{
			if (m_logger.isDebugEnabled())
				m_logger.debug(this +".valueUnbound()");

			// catch the case where an edit was made but never resolved
			if (m_active)
			{
				cancelResource(this);
			}

		} // valueUnbound

	} // BaseResource

	/*******************************************************************************
	* Storage implementation
	*******************************************************************************/

	protected interface Storage
	{
		/**
		* Open and be ready to read / write.
		*/
		public void open();

		/**
		* Close.
		*/
		public void close();

		/**
		* Return the identified collection, or null if not found.
		*/
		public ContentCollection getCollection(String id);

		/**
		* Return true if the identified collection exists.
		*/
		public boolean checkCollection(String id);

		/**
		* Get a list of all getCollections within a collection.
		*/
		public List getCollections(ContentCollection collection);

		/**
		* Keep a new collection.
		*/
		public ContentCollectionEdit putCollection(String collectionId);

		/**
		* Get a collection locked for update
		*/
		public ContentCollectionEdit editCollection(String collectionId);

		/**
		* Commit a collection edit.
		*/
		public void commitCollection(ContentCollectionEdit edit);

		/**
		* Cancel a collection edit.
		*/
		public void cancelCollection(ContentCollectionEdit edit);

		/**
		* Forget about a collection.
		*/
		public void removeCollection(ContentCollectionEdit collection);

		/**
		* Return the identified resource, or null if not found.
		*/
		public ContentResource getResource(String id);

		/**
		* Return true if the identified resource exists.
		*/
		public boolean checkResource(String id);

		/**
		* Get a list of all resources within a collection.
		*/
		public List getResources(ContentCollection collection);

		/**
		* Keep a new resource.
		*/
		public ContentResourceEdit putResource(String resourceId);

		/**
		* Get a resource locked for update
		*/
		public ContentResourceEdit editResource(String resourceId);

		/**
		* Commit a resource edit.
		*/
		public void commitResource(ContentResourceEdit edit);

		/**
		* Cancel a resource edit.
		*/
		public void cancelResource(ContentResourceEdit edit);

		/**
		* Forget about a resource.
		*/
		public void removeResource(ContentResourceEdit resource);

		/**
		* Read the resource's body.
		*/
		public byte[] getResourceBody(ContentResource resource);

		/**
		 * Stream the resource's body.
		 */
		public InputStream streamResourceBody(ContentResource resource)  throws ServerOverloadException;

	} // Storage

	/*******************************************************************************
	* CacheRefresher implementation (no container)
	*******************************************************************************/

	/**
	* Get a new value for this key whose value has already expired in the cache.
	* @param key The key whose value has expired and needs to be refreshed.
	* @param oldValue The old exipred value of the key.
	* @param event The event which triggered this refresh.
	* @return a new value for use in the cache for this key; if null, the entry will be removed.
	*/
	public Object refresh(Object key, Object oldValue, Event event)
	{
		Object rv = null;

		// key is a reference
		Reference ref = new Reference((String) key);
		String id = ref.getId();

		if (m_logger.isDebugEnabled())
			m_logger.debug(this +".refresh(): key " + key + " id : " + ref.getId());

		// get from storage only (not cache!)
		boolean collectionHint = id.endsWith(Resource.SEPARATOR);
		if (collectionHint)
		{
			rv = m_storage.getCollection(id);
		}
		else
		{
			rv = m_storage.getResource(id);
		}

		return rv;

	} // refresh

} // BaseContentService

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/legacy/content/BaseContentService.java,v 1.10 2005/06/05 23:19:50 ggolden.umich.edu Exp $
*
**********************************************************************************/
