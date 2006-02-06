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
package org.sakaiproject.component.legacy.content;

// import
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.xerces.impl.dv.util.Base64;
import org.sakaiproject.api.kernel.function.cover.FunctionManager;
import org.sakaiproject.api.kernel.session.SessionBindingEvent;
import org.sakaiproject.api.kernel.session.SessionBindingListener;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.api.kernel.tool.cover.ToolManager;
import org.sakaiproject.component.legacy.notification.SiteEmailNotificationContent;
import org.sakaiproject.exception.CopyrightException;
import org.sakaiproject.exception.EmptyException;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdLengthException;
import org.sakaiproject.exception.IdUniquenessException;
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
import org.sakaiproject.service.legacy.archive.ArchiveService;
import org.sakaiproject.service.legacy.authzGroup.AuthzGroup;
import org.sakaiproject.service.legacy.authzGroup.Role;
import org.sakaiproject.service.legacy.authzGroup.cover.AuthzGroupService;
import org.sakaiproject.service.legacy.content.ContentCollection;
import org.sakaiproject.service.legacy.content.ContentCollectionEdit;
import org.sakaiproject.service.legacy.content.ContentHostingService;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.content.ContentResourceEdit;
import org.sakaiproject.service.legacy.content.cover.ContentTypeImageService;
import org.sakaiproject.service.legacy.entity.Edit;
import org.sakaiproject.service.legacy.entity.Entity;
import org.sakaiproject.service.legacy.entity.EntityManager;
import org.sakaiproject.service.legacy.entity.EntityProducer;
import org.sakaiproject.service.legacy.entity.HttpAccess;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.entity.ResourceProperties;
import org.sakaiproject.service.legacy.entity.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.event.cover.EventTrackingService;
import org.sakaiproject.service.legacy.id.cover.IdService;
import org.sakaiproject.service.legacy.notification.NotificationEdit;
import org.sakaiproject.service.legacy.notification.NotificationService;
import org.sakaiproject.service.legacy.security.cover.SecurityService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.time.cover.TimeService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.sakaiproject.util.Validator;
import org.sakaiproject.util.java.Blob;
import org.sakaiproject.util.java.StringUtil;
import org.sakaiproject.util.resource.BaseResourcePropertiesEdit;
import org.sakaiproject.util.storage.StorageUser;
import org.sakaiproject.util.xml.Xml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

// dopost

import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileItem;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Enumeration;
import org.sakaiproject.service.framework.log.cover.Log;
import org.sakaiproject.service.legacy.time.TimeBreakdown;

// dodir

import java.util.Comparator;
import java.io.PrintWriter;
import org.sakaiproject.util.ContentHostingComparator;
import java.util.Collections;

/**
 * <p>
 * BaseContentService is an abstract base implementation of the Sakai ContentHostingService.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public abstract class BaseContentService implements ContentHostingService, CacheRefresher
{
	/** The collection id for the attachments collection */
	protected static final String ATTACHMENTS_COLLECTION = "/attachment/";

	/** Number of times to attempt to find a unique resource id when copying or moving a resource */
	protected static final int MAXIMUM_ATTEMPTS_FOR_UNIQUENESS = 100;
	
	/** Maximum number of characters in a valid resource-id */
	protected static final int MAXIMUM_RESOURCE_ID_LENGTH = 255;
	
	/** The initial portion of a relative access point URL. */
	protected String m_relativeAccessPoint = null;

	/** A Storage object for persistent storage. */
	protected Storage m_storage = null;

	/** A Cache for this service - ContentResource and ContentCollection keyed by reference. */
	protected Cache m_cache = null;

	/**
	 * The quota for content resource body bytes (in Kbytes) for any hierarchy in the /user/ or /group/ areas, or 0 if quotas are not enforced.
	 */
	protected long m_siteQuota = 0;

	/** Collection id for the user sites. */
	public static final String COLLECTION_USER = "/user/";

	/** Collection id for the non-user sites. */
	public static final String COLLECTION_SITE = "/group/";

	/** Optional path to external file system file store for body binary. */
	protected String m_bodyPath = null;

	/** Optional set of folders just within the m_bodyPath to distribute files among. */
	protected String[] m_bodyVolumes = null;

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Constructors, Dependencies and their setter methods
	 *********************************************************************************************************************************************************************************************************************************************************/

	/** Dependency: logging service */
	protected Logger m_logger = null;

	/**
	 * Dependency: logging service.
	 * 
	 * @param service
	 *        The logging service.
	 */
	public void setLogger(Logger service)
	{
		m_logger = service;
	}

	/** Dependency: MemoryService. */
	protected MemoryService m_memoryService = null;

	/**
	 * Dependency: MemoryService.
	 * 
	 * @param service
	 *        The MemoryService.
	 */
	public void setMemoryService(MemoryService service)
	{
		m_memoryService = service;
	}

	/** Dependency: NotificationService. */
	protected NotificationService m_notificationService = null;

	/**
	 * Dependency: NotificationService.
	 * 
	 * @param service
	 *        The NotificationService.
	 */
	public void setNotificationService(NotificationService service)
	{
		m_notificationService = service;
	}

	/** Dependency: ServerConfigurationService. */
	protected ServerConfigurationService m_serverConfigurationService = null;

	/**
	 * Dependency: ServerConfigurationService.
	 * 
	 * @param service
	 *        The ServerConfigurationService.
	 */
	public void setServerConfigurationService(ServerConfigurationService service)
	{
		m_serverConfigurationService = service;
	}

	/**
	 * Set the site quota.
	 * 
	 * @param quota
	 *        The site quota (as a string).
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
	 * 
	 * @param path
	 *        The storage path.
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
	 * Configuration: set the external file system path for body storage If set, the resource binary database table will not be used.
	 * 
	 * @param value
	 *        The complete path to the root of the external file system storage area for resource body bytes.
	 */
	public void setBodyPath(String value)
	{
		m_bodyPath = value;
	}

	/**
	 * Configuration: set the external file system volume folders (folder just within the bodyPath) as a comma separated list of folder names. If set, files will be distributed over these folders.
	 * 
	 * @param value
	 *        The comma separated list of folder names within body path to distribute files among.
	 */
	public void setBodyVolumes(String value)
	{
		try
		{
			m_bodyVolumes = StringUtil.split(value, ",");
		}
		catch (Throwable t)
		{
		}
	}

	/** Dependency: EntityManager. */
	protected EntityManager m_entityManager = null;

	/**
	 * Dependency: EntityManager.
	 * 
	 * @param service
	 *        The EntityManager.
	 */
	public void setEntityManager(EntityManager service)
	{
		m_entityManager = service;
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Init and Destroy
	 *********************************************************************************************************************************************************************************************************************************************************/

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
			edit.setResourceFilter(getAccessPoint(true) + Entity.SEPARATOR + "group");
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

			// register as an entity producer
			m_entityManager.registerEntityProducer(this);

			// register functions
			FunctionManager.registerFunction(EVENT_RESOURCE_ADD);
			FunctionManager.registerFunction(EVENT_RESOURCE_READ);
			FunctionManager.registerFunction(EVENT_RESOURCE_WRITE);
			FunctionManager.registerFunction(EVENT_RESOURCE_REMOVE);
			FunctionManager.registerFunction(EVENT_DROPBOX_OWN);
			FunctionManager.registerFunction(EVENT_DROPBOX_MAINTAIN);

			m_logger.info(this + ".init(): site quota: " + m_siteQuota + " body path: " + m_bodyPath + " volumes: "
					+ buf.toString());
		}
		catch (Throwable t)
		{
			m_logger.warn(this + ".init(): ", t);
		}

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

		m_logger.info(this + ".destroy()");

	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * StorageUser implementation - for collections
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * Storage user for collections - in the resource side, not container
	 */
	protected class CollectionStorageUser implements StorageUser
	{
		public Entity newContainer(String ref)
		{
			return null;
		}

		public Entity newContainer(Element element)
		{
			return null;
		}

		public Entity newContainer(Entity other)
		{
			return null;
		}

		public Entity newResource(Entity container, String id, Object[] others)
		{
			return new BaseCollectionEdit(id);
		}

		public Entity newResource(Entity container, Element element)
		{
			return new BaseCollectionEdit(element);
		}

		public Entity newResource(Entity container, Entity other)
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

		public Edit newContainerEdit(Entity other)
		{
			return null;
		}

		public Edit newResourceEdit(Entity container, String id, Object[] others)
		{
			BaseCollectionEdit rv = new BaseCollectionEdit(id);
			rv.activate();
			return rv;
		}

		public Edit newResourceEdit(Entity container, Element element)
		{
			BaseCollectionEdit rv = new BaseCollectionEdit(element);
			rv.activate();
			return rv;
		}

		public Edit newResourceEdit(Entity container, Entity other)
		{
			BaseCollectionEdit rv = new BaseCollectionEdit((ContentCollection) other);
			rv.activate();
			return rv;
		}

		/**
		 * Collect the fields that need to be stored outside the XML (for the resource).
		 * 
		 * @return An array of field values to store in the record outside the XML (for the resource).
		 */
		public Object[] storageFields(Entity r)
		{
			Object[] rv = new Object[1];
			rv[0] = StringUtil.referencePath(((ContentCollection) r).getId());

			return rv;
		}

		/**
		 * Check if this resource is in draft mode.
		 * 
		 * @param r
		 *        The resource.
		 * @return true if the resource is in draft mode, false if not.
		 */
		public boolean isDraft(Entity r)
		{
			return false;
		}

		/**
		 * Access the resource owner user id.
		 * 
		 * @param r
		 *        The resource.
		 * @return The resource owner user id.
		 */
		public String getOwnerId(Entity r)
		{
			return null;
		}

		/**
		 * Access the resource date.
		 * 
		 * @param r
		 *        The resource.
		 * @return The resource date.
		 */
		public Time getDate(Entity r)
		{
			return null;
		}

	} // class CollectionStorageUser

	/**
	 * Storage user for resources - in the resource side, not container
	 */
	protected class ResourceStorageUser implements StorageUser
	{
		public Entity newContainer(String ref)
		{
			return null;
		}

		public Entity newContainer(Element element)
		{
			return null;
		}

		public Entity newContainer(Entity other)
		{
			return null;
		}

		public Entity newResource(Entity container, String id, Object[] others)
		{
			return new BaseResourceEdit(id);
		}

		public Entity newResource(Entity container, Element element)
		{
			return new BaseResourceEdit(element);
		}

		public Entity newResource(Entity container, Entity other)
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

		public Edit newContainerEdit(Entity other)
		{
			return null;
		}

		public Edit newResourceEdit(Entity container, String id, Object[] others)
		{
			BaseResourceEdit rv = new BaseResourceEdit(id);
			rv.activate();
			return rv;
		}

		public Edit newResourceEdit(Entity container, Element element)
		{
			BaseResourceEdit rv = new BaseResourceEdit(element);
			rv.activate();
			return rv;
		}

		public Edit newResourceEdit(Entity container, Entity other)
		{
			BaseResourceEdit rv = new BaseResourceEdit((ContentResource) other);
			rv.activate();
			return rv;
		}

		/**
		 * Collect the fields that need to be stored outside the XML (for the resource).
		 * 
		 * @return An array of field values to store in the record outside the XML (for the resource).
		 */
		public Object[] storageFields(Entity r)
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
		 * 
		 * @param r
		 *        The resource.
		 * @return true if the resource is in draft mode, false if not.
		 */
		public boolean isDraft(Entity r)
		{
			return false;
		}

		/**
		 * Access the resource owner user id.
		 * 
		 * @param r
		 *        The resource.
		 * @return The resource owner user id.
		 */
		public String getOwnerId(Entity r)
		{
			return null;
		}

		/**
		 * Access the resource date.
		 * 
		 * @param r
		 *        The resource.
		 * @return The resource date.
		 */
		public Time getDate(Entity r)
		{
			return null;
		}

	} // class ResourceStorageUser

	/**********************************************************************************************************************************************************************************************************************************************************
	 * ContentHostingService implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * Construct a Storage object.
	 * 
	 * @return The new storage object.
	 */
	protected abstract Storage newStorage();

   /**
    *
    * @param id id of the resource to set the UUID for
    * @param uuid the new UUID of the resource
    */   
   protected abstract void setUuidInternal(String id, String uuid);

	/**
	 * Access the partial URL that forms the root of resource URLs.
	 * 
	 * @param relative
	 *        if true, form within the access path only (i.e. starting with /content)
	 * @return the partial URL that forms the root of resource URLs.
	 */
	protected String getAccessPoint(boolean relative)
	{
		return (relative ? "" : m_serverConfigurationService.getAccessUrl()) + m_relativeAccessPoint;

	} // getAccessPoint

	/**
	 * If the id is for a resource in a dropbox, change the function to a dropbox check, which is to check for write.<br />
	 * You have full or no access to a dropbox.
	 * 
	 * @param lock
	 *        The lock we are checking.
	 * @param id
	 *        The resource id.
	 * @return The lock to check.
	 */
	protected String convertLockIfDropbox(String lock, String id)
	{
		// if this resource is a dropbox, you need dropbox maintain permission
		if (id.startsWith("/group-user"))
		{
			// only for /group-user/SITEID/USERID/ refs.
			String[] parts = StringUtil.split(id, "/");
			if (parts.length >= 4)
			{
				return EVENT_DROPBOX_MAINTAIN;
			}
		}

		return lock;
	}

	/**
	 * Check security permission.
	 * 
	 * @param lock
	 *        The lock id string.
	 * @param id
	 *        The resource id string, or null if no resource is involved.
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
	 * Throws a PermissionException if the resource with the given Id is explicitly locked
	 * 
	 * @param id
	 * @throws PermissionException
	 */
	protected void checkExplicitLock(String id) throws PermissionException
	{
		String uuid = this.getUuid(id);

		if (uuid != null && this.isLocked(uuid))
		{
			// TODO: WebDAV locks need to be more sophisticated than this
			throw new PermissionException("remove", id);
		}
	}

	/**
	 * Check security permission.
	 * 
	 * @param lock
	 *        The lock id string.
	 * @param id
	 *        The resource id string, or null if no resource is involved.
	 * @exception PermissionException
	 *            Thrown if the user does not have access
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
			throw new PermissionException(lock, ref);
		}

	} // unlock

	/**
	 * Check security permission for all contained collections of the given collection (if any) (not the collection itself)
	 * 
	 * @param lock
	 *        The lock id string.
	 * @param resource
	 *        The resource id string, or null if no resource is involved.
	 * @exception PermissionException
	 *            Thrown if the user does not have access
	 */
	/*
	 * protected void unlockContained(String lock, ContentCollection collection) throws PermissionException { if (SecurityService == null) return; Iterator it = collection.getMemberResources().iterator(); while (it.hasNext()) { Object mbr = it.next(); if
	 * (mbr == null) continue; // for a contained collection, check recursively if (mbr instanceof ContentCollection) { unlockContained(lock, (ContentCollection) mbr); } // for resources, check else if (mbr instanceof ContentResource) { unlock(lock,
	 * ((ContentResource) mbr).getId()); } } } // unlockContained
	 */

	/**
	 * Create the live properties for a collection.
	 * 
	 * @param c
	 *        The collection.
	 */
	protected void addLiveCollectionProperties(ContentCollectionEdit c)
	{
		ResourcePropertiesEdit p = c.getPropertiesEdit();
		String current = SessionManager.getCurrentSessionUserId();
		p.addProperty(ResourceProperties.PROP_CREATOR, current);
		p.addProperty(ResourceProperties.PROP_MODIFIED_BY, current);

		String now = TimeService.newTime().toString();
		p.addProperty(ResourceProperties.PROP_CREATION_DATE, now);
		p.addProperty(ResourceProperties.PROP_MODIFIED_DATE, now);

		p.addProperty(ResourceProperties.PROP_IS_COLLECTION, "true");

	} // addLiveCollectionProperties

	/**
	 * Create the live properties for a collection.
	 * 
	 * @param c
	 *        The collection.
	 */
	protected void addLiveUpdateCollectionProperties(ContentCollectionEdit c)
	{
		ResourcePropertiesEdit p = c.getPropertiesEdit();
		String current = SessionManager.getCurrentSessionUserId();
		p.addProperty(ResourceProperties.PROP_MODIFIED_BY, current);

		String now = TimeService.newTime().toString();
		p.addProperty(ResourceProperties.PROP_MODIFIED_DATE, now);

	} // addLiveUpdateCollectionProperties

	/**
	 * Create the live properties for a resource.
	 * 
	 * @param r
	 *        The resource.
	 */
	protected void addLiveResourceProperties(ContentResourceEdit r)
	{
		ResourcePropertiesEdit p = r.getPropertiesEdit();

		String current = SessionManager.getCurrentSessionUserId();
		p.addProperty(ResourceProperties.PROP_CREATOR, current);
		p.addProperty(ResourceProperties.PROP_MODIFIED_BY, current);

		String now = TimeService.newTime().toString();
		p.addProperty(ResourceProperties.PROP_CREATION_DATE, now);
		p.addProperty(ResourceProperties.PROP_MODIFIED_DATE, now);

		p.addProperty(ResourceProperties.PROP_CONTENT_LENGTH, Long.toString(r.getContentLength()));
		p.addProperty(ResourceProperties.PROP_CONTENT_TYPE, r.getContentType());

		p.addProperty(ResourceProperties.PROP_IS_COLLECTION, "false");

	} // addLiveResourceProperties

	/**
	 * Update the live properties for a resource when modified (for a resource).
	 * 
	 * @param r
	 *        The resource.
	 */
	protected void addLiveUpdateResourceProperties(ContentResourceEdit r)
	{
		ResourcePropertiesEdit p = r.getPropertiesEdit();

		String current = SessionManager.getCurrentSessionUserId();
		p.addProperty(ResourceProperties.PROP_MODIFIED_BY, current);

		String now = TimeService.newTime().toString();
		p.addProperty(ResourceProperties.PROP_MODIFIED_DATE, now);

		p.addProperty(ResourceProperties.PROP_CONTENT_LENGTH, Long.toString(r.getContentLength()));
		p.addProperty(ResourceProperties.PROP_CONTENT_TYPE, r.getContentType());

	} // addLiveUpdateResourceProperties

	/**
	 * Make sure that the entire set of properties are present, adding whatever is needed, replacing nothing that's there already.
	 * 
	 * @param r
	 *        The resource.
	 */
	protected void assureResourceProperties(ContentResourceEdit r)
	{
		ResourcePropertiesEdit p = r.getPropertiesEdit();

		String current = SessionManager.getCurrentSessionUserId();
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
	 * 
	 * @param r
	 *        The resource.
	 * @param props
	 *        The properties.
	 */
	protected void addProperties(ResourcePropertiesEdit p, ResourceProperties props)
	{
		if (props == null) return;

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

	} // addProperties

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Collections
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * check permissions for addCollection().
	 * 
	 * @param channelId
	 *        The channel id.
	 * @return true if the user is allowed to addCollection(id), false if not.
	 */
	public boolean allowAddCollection(String id)
	{
		// collection must also end in the separator (we fix it)
		if (!id.endsWith(Entity.SEPARATOR))
		{
			id = id + Entity.SEPARATOR;
		}

		// check security
		return unlockCheck(EVENT_RESOURCE_ADD, id);

	} // allowAddCollection

	/**
	 * Create a new collection with the given resource id.
	 * 
	 * @param id
	 *        The id of the collection.
	 * @param properties
	 *        A java Properties object with the properties to add to the new collection.
	 * @exception IdUsedException
	 *            if the id is already in use.
	 * @exception IdInvalidException
	 *            if the id is invalid.
	 * @exception PermissionException
	 *            if the user does not have permission to add a collection, or add a member to a collection.
	 * @exception InconsistentException
	 *            if the containing collection does not exist.
	 * @return a new ContentCollection object.
	 */
	public ContentCollection addCollection(String id, ResourceProperties properties) throws IdUsedException, IdInvalidException,
			PermissionException, InconsistentException
	{
		ContentCollectionEdit edit = addCollection(id);

		// add the provided of properties
		addProperties(edit.getPropertiesEdit(), properties);

		// commit the change
		commitCollection(edit);

		return edit;

	} // addCollection

	/**
	 * Create a new collection with the given resource id, locked for update. Must commitCollection() to make official, or cancelCollection() when done!
	 * 
	 * @param id
	 *        The id of the collection.
	 * @exception IdUsedException
	 *            if the id is already in use.
	 * @exception IdInvalidException
	 *            if the id is invalid.
	 * @exception PermissionException
	 *            if the user does not have permission to add a collection, or add a member to a collection.
	 * @exception InconsistentException
	 *            if the containing collection does not exist.
	 * @return a new ContentCollection object.
	 */
	public ContentCollectionEdit addCollection(String id) throws IdUsedException, IdInvalidException, PermissionException,
			InconsistentException
	{
		// check the id's validity (this may throw IdInvalidException)
		// use only the "name" portion, separated at the end
		String justName = isolateName(id);
		Validator.checkResourceId(justName);

		// collection must also end in the separator (we fix it)
		if (!id.endsWith(Entity.SEPARATOR))
		{
			id = id + Entity.SEPARATOR;
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
			if (containingCollection == null) throw new InconsistentException(id);
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
	 * 
	 * @param id
	 *        The id of the collection.
	 * @return true if the user is allowed to getCollection(id), false if not.
	 */
	public boolean allowGetCollection(String id)
	{
		return unlockCheck(EVENT_RESOURCE_READ, id);

	} // allowGetCollection

	/**
	 * Check access to the collection with this local resource id.
	 * 
	 * @param id
	 *        The id of the collection.
	 * @exception IdUnusedException
	 *            if the id does not exist.
	 * @exception TypeException
	 *            if the resource exists but is not a collection.
	 * @exception PermissionException
	 *            if the user does not have permissions to see this collection (or read through containing collections).
	 */
	public void checkCollection(String id) throws IdUnusedException, TypeException, PermissionException
	{
		unlock(EVENT_RESOURCE_READ, id);

		ContentCollection collection = findCollection(id);
		if (collection == null) throw new IdUnusedException(id);

	} // checkCollection

	/**
	 * Access the collection with this local resource id. The collection internal members and properties are accessible from the returned Colelction object.
	 * 
	 * @param id
	 *        The id of the collection.
	 * @exception IdUnusedException
	 *            if the id does not exist.
	 * @exception TypeException
	 *            if the resource exists but is not a collection.
	 * @exception PermissionException
	 *            if the user does not have permissions to see this collection (or read through containing collections).
	 * @return The ContentCollection object found.
	 */
	public ContentCollection getCollection(String id) throws IdUnusedException, TypeException, PermissionException
	{
		unlock(EVENT_RESOURCE_READ, id);

		ContentCollection collection = findCollection(id);
		if (collection == null) throw new IdUnusedException(id);

		// track event
		// EventTrackingService.post(EventTrackingService.newEvent(EVENT_RESOURCE_READ, collection.getReference(), false));

		return collection;

	} // getCollection

	/**
	 * Access a List of all the ContentResource objects in this path (and below) which the current user has access.
	 * 
	 * @param id
	 *        A collection id.
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
	 * Access a List of all the ContentResource objects in this collection (and below) which the current user has access.
	 * 
	 * @param collection
	 *        The collection.
	 * @param rv
	 *        The list in which to accumulate resource objects.
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
	 * Access the collection with this local resource id. Internal find does the guts of finding without security or event tracking. The collection internal members and properties are accessible from the returned Colelction object.
	 * 
	 * @param id
	 *        The id of the collection.
	 * @exception TypeException
	 *            if the resource exists but is not a collection.
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
				if ((o != null) && (!(o instanceof ContentCollection))) throw new TypeException(id);

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
	 * 
	 * @param id
	 *        The id of the collection.
	 * @return true if the user is allowed to update the collection, false if not.
	 */
	public boolean allowUpdateCollection(String id)
	{
		if (isLocked(getUuid(id)))
		{
			return false;
		}
		return unlockCheck(EVENT_RESOURCE_WRITE, id);

	} // allowUpdateCollection

	/**
	 * Access the collection with this local resource id, locked for update. Must commitCollection() to make official, or cancelCollection() when done! The collection internal members and properties are accessible from the returned Collection object.
	 * 
	 * @param id
	 *        The id of the collection.
	 * @exception IdUnusedException
	 *            if the id does not exist.
	 * @exception TypeException
	 *            if the resource exists but is not a collection.
	 * @exception PermissionException
	 *            if the user does not have permissions to see this collection (or read through containing collections).
	 * @exception InUseException
	 *            if the Collection is locked by someone else.
	 * @return The ContentCollection object found.
	 */
	public ContentCollectionEdit editCollection(String id) throws IdUnusedException, TypeException, PermissionException,
			InUseException
	{
		String ref = getReference(id);

		if (isLocked(getUuid(id)))
		{
			throw new PermissionException(id, ref);
		}

		// check security (throws if not permitted)
		unlock(EVENT_RESOURCE_WRITE, id);

		// check for existance
		if (!m_storage.checkCollection(id))
		{
			throw new IdUnusedException(id);
		}

		// ignore the cache - get the collection with a lock from the info store
		BaseCollectionEdit collection = (BaseCollectionEdit) m_storage.editCollection(id);
		if (collection == null) throw new InUseException(id);

		collection.setEvent(EVENT_RESOURCE_WRITE);

		return collection;

	} // editCollection

	/**
	 * check permissions for removeCollection(). Note: for just this collection, not the members on down.
	 * 
	 * @param id
	 *        The id of the collection.
	 * @return true if the user is allowed to removeCollection(id), false if not.
	 */
	public boolean allowRemoveCollection(String id)
	{
		return unlockCheck(EVENT_RESOURCE_REMOVE, id);

	} // allowRemoveCollection

	/**
	 * Remove just a collection. It must be empty.
	 * 
	 * @param collection
	 *        The collection to remove.
	 * @exception TypeException
	 *            if the resource exists but is not a collection.
	 * @exception PermissionException
	 *            if the user does not have permissions to remove this collection, read through any containing
	 * @exception InconsistentException
	 *            if the collection has members, so that the removal would leave things in an inconsistent state.
	 * @exception ServerOverloadException
	 *            if the server is configured to write the resource body to the filesystem and an attempt to access the resource body 
	 *            of any collection member fails.
	 */
	public void removeCollection(ContentCollectionEdit edit) throws TypeException, PermissionException, InconsistentException, ServerOverloadException
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
				m_logger.warn(this + ".removeCollection(): closed ContentCollectionEdit", e);
			}
			return;
		}

		// check security (throws if not permitted)
		unlock(EVENT_RESOURCE_REMOVE, edit.getId());

		// check for members
		List members = edit.getMemberResources();
		if (!members.isEmpty()) throw new InconsistentException(edit.getId());

		// complete the edit
		m_storage.removeCollection(edit);

		// track it (no notification)
		EventTrackingService.post(EventTrackingService.newEvent(EVENT_RESOURCE_REMOVE, edit.getReference(), true,
				NotificationService.NOTI_NONE));

		// close the edit object
		((BaseCollectionEdit) edit).closeEdit();

		((BaseCollectionEdit) edit).setRemoved();

		// remove any realm defined for this resource
		try
		{
			AuthzGroupService.removeAuthzGroup(AuthzGroupService.getAuthzGroup(edit.getReference()));
		}
		catch (PermissionException e)
		{
			m_logger.warn(this + ".removeCollection: removing realm for : " + edit.getReference() + " : " + e);
		}
		catch (IdUnusedException ignore)
		{
		}

	} // removeCollection

	/**
	 * Remove a collection and all members of the collection, internal or deeper.
	 * 
	 * @param id
	 *        The id of the collection.
	 * @exception IdUnusedException
	 *            if the id does not exist.
	 * @exception TypeException
	 *            if the resource exists but is not a collection.
	 * @exception PermissionException
	 *            if the user does not have permissions to remove this collection, read through any containing
	 * @exception InUseException
	 *            if the collection or a contained member is locked by someone else. collections, or remove any members of the collection.
	 * @exception ServerOverloadException
	 *            if the server is configured to write the resource body to the filesystem and an attempt to access the resource body of any collection member fails.
	 */
	public void removeCollection(String id) throws IdUnusedException, TypeException, PermissionException, InUseException, ServerOverloadException
	{
		// check security for remove
		unlock(EVENT_RESOURCE_REMOVE, id);

		// find the collection
		ContentCollection thisCollection = findCollection(id);
		if (thisCollection == null) throw new IdUnusedException(id);

		// check security: can we remove members (if any)
		// Note: this will also be done in clear(), except some might get deleted before one is not allowed.
		// unlockContained(EVENT_RESOURCE_REMOVE, thisCollection);

		// get an edit
		ContentCollectionEdit edit = editCollection(id);

		// clear of all members (recursive)
		// Note: may fail if something's in use or not permitted. May result in a partial clear.
		try
		{
			((BaseCollectionEdit) edit).clear();

			// remove
			removeCollection(edit);
		}
		catch (InconsistentException e)
		{
			m_logger.warn(this + ".removeCollection():", e);
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
	 * Commit the changes made, and release the lock. The Object is disabled, and not to be used after this call.
	 * 
	 * @param edit
	 *        The ContentCollectionEdit object to commit.
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
				m_logger.warn(this + ".commitCollection(): closed ContentCollectionEdit", e);
			}
			return;
		}

		// update the properties for update
		addLiveUpdateCollectionProperties(edit);

		// complete the edit
		m_storage.commitCollection(edit);

		// track it (no notification)
		EventTrackingService.post(EventTrackingService.newEvent(((BaseCollectionEdit) edit).getEvent(), edit.getReference(), true,
				NotificationService.NOTI_NONE));

		// close the edit object
		((BaseCollectionEdit) edit).closeEdit();

	} // commitCollection

	/**
	 * Cancel the changes made object, and release the lock. The Object is disabled, and not to be used after this call.
	 * 
	 * @param edit
	 *        The ContentCollectionEdit object to commit.
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
				m_logger.warn(this + ".cancelCollection(): closed ContentCollectionEdit", e);
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

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Resources
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * check permissions for addResource().
	 * 
	 * @param id
	 *        The id of the new resource.
	 * @return true if the user is allowed to addResource(id), false if not.
	 */
	public boolean allowAddResource(String id)
	{
		// resource must also NOT end with a separator characters (we fix it)
		if (id.endsWith(Entity.SEPARATOR))
		{
			id = id.substring(0, id.length() - 1);
		}

		// check security
		return unlockCheck(EVENT_RESOURCE_ADD, id);

	} // allowAddResource

	/**
	 * Create a new resource with the given resource id.
	 * 
	 * @param id
	 *        The id of the new resource.
	 * @param type
	 *        The mime type string of the resource.
	 * @param content
	 *        An array containing the bytes of the resource's content.
	 * @param properties
	 *        A java Properties object with the properties to add to the new resource.
	 * @param priority
	 *        The notification priority for this commit.
	 * @exception PermissionException
	 *            if the user does not have permission to add a resource to the containing collection.
	 * @exception IdUsedException
	 *            if the resource id is already in use.
	 * @exception IdInvalidException
	 *            if the resource id is invalid.
	 * @exception InconsistentException
	 *            if the containing collection does not exist.
	 * @exception OverQuotaException
	 *            if this would result in being over quota.
	 * @exception ServerOverloadException
	 *            if the server is configured to write the resource body to the filesystem and the save fails.
	 * @return a new ContentResource object.
	 */
	public ContentResource addResource(String id, String type, byte[] content, ResourceProperties properties, int priority)
			throws PermissionException, IdUsedException, IdInvalidException, InconsistentException, OverQuotaException, ServerOverloadException
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
	 * Create a new resource with the given resource name used as a resource id within the
	 * specified collection or (if that id is already in use) with a resource id based on
	 * a variation on the name to achieve a unique id, provided a unique id can be found 
	 * before a limit is reached on the number of attempts to achieve uniqueness.
	 * 
	 * @param name
	 *        The name of the new resource (such as a filename).
	 * @param collectionId
	 *        The id of the collection to which the resource should be added.
	 * @param limit
	 *        The maximum number of attempts at finding a unique id based on the given name.
	 * @param type
	 *        The mime type string of the resource.
	 * @param content
	 *        An array containing the bytes of the resource's content.
	 * @param properties
	 *        A ResourceProperties object with the properties to add to the new resource.
	 * @param priority
	 *        The notification priority for this commit.
	 * @exception PermissionException
	 *            if the user does not have permission to add a resource to the containing collection.
	 * @exception IdUniquenessException
	 *            if a unique resource id cannot be found before the limit on the number of attempts is reached.
	 * @exception IdLengthException
	 *            if the resource id exceeds the maximum number of characters for a valid resource id.
	 * @exception IdInvalidException
	 *            if the resource id is invalid.
	 * @exception InconsistentException
	 *            if the containing collection does not exist.
	 * @exception OverQuotaException
	 *            if this would result in being over quota.
	 * @exception ServerOverloadException
	 *            if the server is configured to write the resource body to the filesystem and the save fails.
	 * @return a new ContentResource object.
	 */
	public ContentResource addResource(String name, String collectionId, int limit, String type, byte[] content, ResourceProperties properties, int priority)
			throws PermissionException, IdUniquenessException, IdLengthException, IdInvalidException, InconsistentException, OverQuotaException,
			ServerOverloadException
	{
		try
		{
			collectionId = collectionId.trim();
			name = Validator.escapeResourceName(name.trim());
			checkCollection(collectionId);
		}
		catch(IdUnusedException e)
		{
			throw new InconsistentException(collectionId);
		}
		catch(TypeException e)
		{
			throw new InconsistentException(collectionId);
		}
		
		String id = collectionId + name;
		id = (String) ((Hashtable) fixTypeAndId(id, type)).get("id");
		if(id.length() > MAXIMUM_RESOURCE_ID_LENGTH)
		{
			throw new IdLengthException(id);
		}

		ContentResourceEdit edit = null;
		
		try
		{
			edit = addResource(id);
			edit.setContentType(type);
			edit.setContent(content);
			addProperties(edit.getPropertiesEdit(), properties);
			// commit the change
			commitResource(edit, priority);
		}
		catch(IdUsedException e)
		{
			try
			{
				checkResource(id);
			}
			catch(IdUnusedException inner_e)
			{
				// TODO: What does this condition actually represent?  What exception should be thrown?
				throw new IdUniquenessException(id);
			}
			catch(TypeException inner_e)
			{
				throw new InconsistentException(id);
			}
			
			SortedSet siblings = new TreeSet();
			try
			{
				ContentCollection collection = findCollection(collectionId);
				siblings.addAll(collection.getMembers());
			}
			catch(TypeException inner_e)
			{
				throw new InconsistentException(collectionId);
			}
			
			int index = name.lastIndexOf(".");
			String base = name;
			String ext = "";
			if(index > 0 && ! "Url".equalsIgnoreCase(type))
			{
				base = name.substring(0, index);
				ext = name.substring(index);
			}
			boolean trying = true;
			int attempts = 1;
			while(trying) 	// see end of loop for condition that enforces attempts <= limit)
			{
				String new_id = collectionId + base + "-" + attempts + ext;
				if(new_id.length() > MAXIMUM_RESOURCE_ID_LENGTH)
				{
					throw new IdLengthException(new_id);
				}
				if(! siblings.contains(new_id))
				{
					try
					{
						edit = addResource(new_id);
						edit.setContentType(type);
						edit.setContent(content);
						addProperties(edit.getPropertiesEdit(), properties);
						// commit the change
						commitResource(edit, priority);
						
						trying = false;
					}
					catch(IdUsedException ignore)
					{
						// try again
					}
				}
				attempts++;
				if(attempts > limit)
				{
					throw new IdUniquenessException(new_id);
				}
			}
		}
		return edit;
		
	}



	/**
	 * Create a new resource with the given resource id, locked for update. Must commitResource() to make official, or cancelResource() when done!
	 * 
	 * @param id
	 *        The id of the new resource.
	 * @exception PermissionException
	 *            if the user does not have permission to add a resource to the containing collection.
	 * @exception IdUsedException
	 *            if the resource id is already in use.
	 * @exception IdInvalidException
	 *            if the resource id is invalid.
	 * @exception InconsistentException
	 *            if the containing collection does not exist.
	 * @return a new ContentResource object.
	 */
	public ContentResourceEdit addResource(String id) throws PermissionException, IdUsedException, IdInvalidException,
			InconsistentException
	{
		// check the id's validity (this may throw IdInvalidException)
		// use only the "name" portion, separated at the end
		String justName = isolateName(id);
		Validator.checkResourceId(justName);
		// resource must also NOT end with a separator characters (we fix it)
		if (id.endsWith(Entity.SEPARATOR))
		{
			id = id.substring(0, id.length() - 1);
		}

		// check security
		checkExplicitLock(id);
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
			if (containingCollection == null) throw new InconsistentException(id);
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
	 * 
	 * @return true if the user is allowed to addAttachmentResource(), false if not.
	 */
	public boolean allowAddAttachmentResource()
	{
		return unlockCheck(EVENT_RESOURCE_ADD, ATTACHMENTS_COLLECTION);

	} // allowAddAttachmentResource
	
	/**
	 * Check whether a resource id or collection id references an entity in the attachments collection. 
	 * This method makes no guarantees that a resource actually exists with this id.
	 * @param id	Assumed to be a valid resource id or collection id.
	 * @return	true if the id (assuming it is a valid id for an existing resource or collection) 
	 * references an entity in the hidden attachments area created through one of this class's 
	 * addAttachmentResource methods. 
	 */
	public boolean isAttachmentResource(String id)
	{
		// TODO: Should we check whether this is a valid resource id?
		return id.startsWith(ATTACHMENTS_COLLECTION);
	}

	/**
	 * Create a new resource as an attachment to some other resource in the system. The new resource will be placed into a newly created collecion in the attachment collection, with an auto-generated id, and given the specified resource name within this
	 * collection.
	 * 
	 * @param name
	 *        The name of the new resource, i.e. a partial id relative to the collection where it will live.
	 * @param type
	 *        The mime type string of the resource.
	 * @param content
	 *        An array containing the bytes of the resource's content.
	 * @param properties
	 *        A ResourceProperties object with the properties to add to the new resource.
	 * @exception IdUsedException
	 *            if the resource name is already in use (not likely, as the containing collection is auto-generated!)
	 * @exception IdInvalidException
	 *            if the resource name is invalid.
	 * @exception InconsistentException
	 *            if the containing collection (or it's containing collection...) does not exist.
	 * @exception PermissionException
	 *            if the user does not have permission to add a collection, or add a member to a collection.
	 * @exception OverQuotaException
	 *            if this would result in being over quota.
	 * @exception ServerOverloadException
	 *            if the server is configured to write the resource body to the filesystem and the save fails.
	 * @return a new ContentResource object.
	 */
	public ContentResource addAttachmentResource(String name, String type, byte[] content, ResourceProperties properties)
			throws IdInvalidException, InconsistentException, IdUsedException, PermissionException, OverQuotaException, ServerOverloadException
	{
		// make sure the name is valid
		Validator.checkResourceId(name);

		// resource must also NOT end with a separator characters (we fix it)
		if (name.endsWith(Entity.SEPARATOR))
		{
			name = name.substring(0, name.length() - 1);
		}

		// form a name based on the attachments collection, a unique folder id, and the given name
		String collection = ATTACHMENTS_COLLECTION + IdService.getUniqueId() + Entity.SEPARATOR;
		String id = collection + name;

		// add this collection
		ContentCollectionEdit edit = addCollection(collection);
		edit.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, name);
		commitCollection(edit);

		// and add the resource
		return addResource(id, type, content, properties, NotificationService.NOTI_NONE);

	} // addAttachmentResource

	/**
	 * Create a new resource as an attachment to some other resource in the system. The new resource will be placed into a newly created collecion in the attachment collection, with an auto-generated id, and given the specified resource name within this
	 * collection.
	 * 
	 * @param name
	 *        The name of the new resource, i.e. a partial id relative to the collection where it will live.
	 * @param site
	 *        The string identifier for the site where the attachment is being added.
	 * @param tool
	 *        The display-name for the tool through which the attachment is being added within the site's attachments collection.
	 * @param type
	 *        The mime type string of the resource.
	 * @param content
	 *        An array containing the bytes of the resource's content.
	 * @param properties
	 *        A ResourceProperties object with the properties to add to the new resource.
	 * @exception IdUsedException
	 *            if the resource name is already in use (not likely, as the containing collection is auto-generated!)
	 * @exception IdInvalidException
	 *            if the resource name is invalid.
	 * @exception InconsistentException
	 *            if the containing collection (or it's containing collection...) does not exist.
	 * @exception PermissionException
	 *            if the user does not have permission to add a collection, or add a member to a collection.
	 * @exception OverQuotaException
	 *            if this would result in being over quota.
	 * @exception ServerOverloadException
	 *            if the server is configured to write the resource body to the filesystem and the save fails.
	 * @return a new ContentResource object.
	 */
	public ContentResource addAttachmentResource(String name, String site, String tool, String type, byte[] content, ResourceProperties properties)
			throws IdInvalidException, InconsistentException, IdUsedException, PermissionException, OverQuotaException, ServerOverloadException
	{
		// ignore site if it is not valid
		if(site == null || site.trim().equals(""))
		{
			return addAttachmentResource(name, type, content, properties);
		}
		site = site.trim();
		String siteId = Validator.escapeResourceName(site);
		
		// if tool is not valid, use "_anon_"
		if(tool == null || tool.trim().equals(""))
		{
			tool = "_anon_";
		}
		tool = tool.trim();
		String toolId = Validator.escapeResourceName(tool);
		
		// make sure the name is valid
		Validator.checkResourceId(name);

		// resource must also NOT end with a separator characters (we fix it)
		if (name.endsWith(Entity.SEPARATOR))
		{
			name = name.substring(0, name.length() - 1);
		}
		
		String siteCollection = ATTACHMENTS_COLLECTION + siteId + Entity.SEPARATOR;
		try
		{
			checkCollection(siteCollection);
		}
		catch(Exception e)
		{
			// add this collection
			ContentCollectionEdit siteEdit = addCollection(siteCollection);
			try
			{
				String siteTitle = SiteService.getSite(site).getTitle();
				siteEdit.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, siteTitle);
			}
			catch(Exception e1)
			{
				siteEdit.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, site);
			}
			commitCollection(siteEdit);
		}

		String toolCollection = siteCollection + toolId + Entity.SEPARATOR;
		try
		{
			checkCollection(toolCollection);
		}
		catch(Exception e)
		{
			// add this collection
			ContentCollectionEdit toolEdit = addCollection(toolCollection);
			toolEdit.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, tool);
			commitCollection(toolEdit);
		}

		// form a name based on the attachments collection, a unique folder id, and the given name
		String collection = toolCollection + IdService.getUniqueId() + Entity.SEPARATOR;
		String id = collection + name;

		// add this collection
		ContentCollectionEdit edit = addCollection(collection);
		edit.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, name);
		commitCollection(edit);

		// and add the resource
		return addResource(id, type, content, properties, NotificationService.NOTI_NONE);

	} // addAttachmentResource

	/**
	 * Create a new resource as an attachment to some other resource in the system, locked for update. Must commitResource() to make official, or cancelResource() when done! The new resource will be placed into a newly created collecion in the attachment
	 * collection, with an auto-generated id, and given the specified resource name within this collection.
	 * 
	 * @param name
	 *        The name of the new resource, i.e. a partial id relative to the collection where it will live.
	 * @exception IdUsedException
	 *            if the resource name is already in use (not likely, as the containing collection is auto-generated!)
	 * @exception IdInvalidException
	 *            if the resource name is invalid.
	 * @exception InconsistentException
	 *            if the containing collection (or it's containing collection...) does not exist.
	 * @exception PermissionException
	 *            if the user does not have permission to add a collection, or add a member to a collection.
	 * @return a new ContentResource object.
	 */
	public ContentResourceEdit addAttachmentResource(String name) throws IdInvalidException, InconsistentException,
			IdUsedException, PermissionException
	{
		// make sure the name is valid
		Validator.checkResourceId(name);

		// resource must also NOT end with a separator characters (we fix it)
		if (name.endsWith(Entity.SEPARATOR))
		{
			name = name.substring(0, name.length() - 1);
		}

		// form a name based on the attachments collection, a unique folder id, and the given name
		String collection = ATTACHMENTS_COLLECTION + IdService.getUniqueId() + Entity.SEPARATOR;
		String id = collection + name;

		// add this collection
		ContentCollectionEdit edit = addCollection(collection);
		edit.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, name);
		commitCollection(edit);

		return addResource(id);

	} // addAttachmentResource

	/**
	 * check permissions for updateResource().
	 * 
	 * @param id
	 *        The id of the new resource.
	 * @return true if the user is allowed to updateResource(id), false if not.
	 */
	public boolean allowUpdateResource(String id)
	{
		return unlockCheck(EVENT_RESOURCE_WRITE, id);

	} // allowUpdateResource

	/**
	 * Update the body and or content type of an existing resource with the given resource id.
	 * 
	 * @param id
	 *        The id of the resource.
	 * @param type
	 *        The mime type string of the resource (if null, no change).
	 * @param content
	 *        An array containing the bytes of the resource's content (if null, no change).
	 * @exception PermissionException
	 *            if the user does not have permission to add a resource to the containing collection or write the resource.
	 * @exception IdUnusedException
	 *            if the resource id is not defined.
	 * @exception TypeException
	 *            if the resource is a collection.
	 * @exception InUseException
	 *            if the resource is locked by someone else.
	 * @exception OverQuotaException
	 *            if this would result in being over quota.
	 * @exception ServerOverloadException
	 *            if the server is configured to write the resource body to the filesystem and the save fails.
	 * @return a new ContentResource object.
	 */
	public ContentResource updateResource(String id, String type, byte[] content) throws PermissionException, IdUnusedException,
			TypeException, InUseException, OverQuotaException, ServerOverloadException
	{
		// find a resource that is this resource
		ContentResourceEdit edit = editResource(id);

		edit.setContentType(type);
		edit.setContent(content);

		// commit the change
		commitResource(edit, NotificationService.NOTI_NONE);

		return edit;

	} // updateResource

	/**
	 * Access the resource with this resource id, locked for update. For non-collection resources only. Must commitEdit() to make official, or cancelEdit() when done! The resource content and properties are accessible from the returned Resource object.
	 * 
	 * @param id
	 *        The id of the resource.
	 * @exception PermissionException
	 *            if the user does not have permissions to read the resource or read through any containing collection.
	 * @exception IdUnusedException
	 *            if the resource id is not found.
	 * @exception TypeException
	 *            if the resource is a collection.
	 * @exception InUseException
	 *            if the resource is locked by someone else.
	 * @return the ContentResource object found.
	 */
	public ContentResourceEdit editResource(String id) throws PermissionException, IdUnusedException, TypeException, InUseException
	{
		String ref = getReference(id);

		// check security (throws if not permitted)
		checkExplicitLock(id);
		unlock(EVENT_RESOURCE_WRITE, id);

		// check for existance
		if (!m_storage.checkResource(id))
		{
			throw new IdUnusedException(id);
		}

		// ignore the cache - get the collection with a lock from the info store
		BaseResourceEdit resource = (BaseResourceEdit) m_storage.editResource(id);
		if (resource == null) throw new InUseException(id);

		resource.setEvent(EVENT_RESOURCE_WRITE);

		return resource;

	} // editResource

	/**
	 * check permissions for getResource().
	 * 
	 * @param id
	 *        The id of the new resource.
	 * @return true if the user is allowed to getResource(id), false if not.
	 */
	public boolean allowGetResource(String id)
	{
		return unlockCheck(EVENT_RESOURCE_READ, id);

	} // allowGetResource

	/**
	 * Check access to the resource with this local resource id. For non-collection resources only.
	 * 
	 * @param id
	 *        The id of the resource.
	 * @exception PermissionException
	 *            if the user does not have permissions to read the resource or read through any containing collection.
	 * @exception IdUnusedException
	 *            if the resource id is not found.
	 * @exception TypeException
	 *            if the resource is a collection.
	 */
	public void checkResource(String id) throws PermissionException, IdUnusedException, TypeException
	{
		// check security
		unlock(EVENT_RESOURCE_READ, id);

		ContentResource resource = findResource(id);
		if (resource == null) throw new IdUnusedException(id);

	} // checkResource

	/**
	 * Access the resource with this resource id. For non-collection resources only. The resource content and properties are accessible from the returned Resource object.
	 * 
	 * @param id
	 *        The resource id.
	 * @exception PermissionException
	 *            if the user does not have permissions to read the resource or read through any containing collection.
	 * @exception IdUnusedException
	 *            if the resource id is not found.
	 * @exception TypeException
	 *            if the resource is a collection.
	 * @return the ContentResource object found.
	 */
	public ContentResource getResource(String id) throws PermissionException, IdUnusedException, TypeException
	{
		// check security
		unlock(EVENT_RESOURCE_READ, id);

		ContentResource resource = findResource(id);
		if (resource == null) throw new IdUnusedException(id);

		// track event
		// EventTrackingService.post(EventTrackingService.newEvent(EVENT_RESOURCE_READ, resource.getReference(), false));

		return resource;

	} // getResource

	/**
	 * Access the resource with this resource id. For non-collection resources only. Internal find that doesn't do security or event tracking The resource content and properties are accessible from the returned Resource object.
	 * 
	 * @param id
	 *        The resource id.
	 * @exception TypeException
	 *            if the resource is a collection.
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
				if ((o != null) && (!(o instanceof ContentResource))) throw new TypeException(id);

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
	 * 
	 * @param id
	 *        The id of the new resource.
	 * @return true if the user is allowed to removeResource(id), false if not.
	 */
	public boolean allowRemoveResource(String id)
	{
		// check security
		return unlockCheck(EVENT_RESOURCE_REMOVE, id);

	} // allowRemoveResource

	/**
	 * Remove a resource. For non-collection resources only.
	 * 
	 * @param id
	 *        The resource id.
	 * @exception PermissionException
	 *            if the user does not have permissions to read a containing collection, or to remove this resource.
	 * @exception IdUnusedException
	 *            if the resource id is not found.
	 * @exception TypeException
	 *            if the resource is a collection.
	 * @exception InUseException
	 *            if the resource is locked by someone else.
	 */
	public void removeResource(String id) throws PermissionException, IdUnusedException, TypeException, InUseException
	{
		BaseResourceEdit edit = (BaseResourceEdit) editResource(id);
		removeResource(edit);

	} // removeResource

	/**
	 * Remove a resource that is locked for update.
	 * 
	 * @param edit
	 *        The ContentResourceEdit object to remove.
	 * @exception PermissionException
	 *            if the user does not have permissions to read a containing collection, or to remove this resource.
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
				m_logger.warn(this + ".removeResource(): closed ContentResourceEdit", e);
			}
			return;
		}

		String id = edit.getId();

		// check security (throws if not permitted)
		checkExplicitLock(id);
		unlock(EVENT_RESOURCE_REMOVE, id);

		// htripath -store the metadata information into a delete table
		// assumed uuid is not null as checkExplicitLock(id) throws exception when null
		String uuid = this.getUuid(id);
		String userId = SessionManager.getCurrentSessionUserId().trim();
		addResourceToDeleteTable(edit, uuid, userId);

		// complete the edit
		m_storage.removeResource(edit);

		// track it (no notification)
		EventTrackingService.post(EventTrackingService.newEvent(EVENT_RESOURCE_REMOVE, edit.getReference(), true,
				NotificationService.NOTI_NONE));

		// close the edit object
		((BaseResourceEdit) edit).closeEdit();

		((BaseResourceEdit) edit).setRemoved();

		// remove any realm defined for this resource
		try
		{
			AuthzGroupService.removeAuthzGroup(AuthzGroupService.getAuthzGroup(edit.getReference()));
		}
		catch (PermissionException e)
		{
			m_logger.warn(this + ".removeResource: removing realm for : " + edit.getReference() + " : " + e);
		}
		catch (IdUnusedException ignore)
		{
		}

	} // removeResource

	/** 
	 * Store the resource in a separate delete table 
	 * @param edit
	 * @param uuid
	 * @param userId
	 * @exception PermissionException 
	 * @exception ServerOverloadException
	 * 			 if server is configured to save resource body in filesystem and attempt to read from filesystem fails.
	 */
	public void addResourceToDeleteTable(ContentResourceEdit edit, String uuid, String userId) throws PermissionException
	{
		String id = edit.getId();
		String content_type = edit.getContentType();
		byte[] content = null;
		try 
		{
			content = edit.getContent();
		} 
		catch (ServerOverloadException e) 
		{
			String this_method = this + ".addResourceToDeleteTable()";
			m_logger.warn("\n\n" + this_method + "\n" + this_method + ": Unable to access file in server filesystem\n" + this_method + ": May be orphaned file: " + id + "\n" + this_method + "\n\n");
		}
		ResourceProperties properties = edit.getProperties();
		
		ContentResource newResource = addDeleteResource(id, content_type, content, properties, uuid, userId, NotificationService.NOTI_OPTIONAL);
	}

	public ContentResource addDeleteResource(String id, String type, byte[] content, ResourceProperties properties, String uuid,
			String userId, int priority) throws PermissionException
	{
		id = (String) ((Hashtable) fixTypeAndId(id, type)).get("id");
		// resource must also NOT end with a separator characters (fix it)
		if (id.endsWith(Entity.SEPARATOR))
		{
			id = id.substring(0, id.length() - 1);
		}
		// check security-unlock to add record
		unlock(EVENT_RESOURCE_ADD, id);

		// reserve the resource in storage - it will fail if the id is in use
		BaseResourceEdit edit = (BaseResourceEdit) m_storage.putDeleteResource(id, uuid, userId);
		// add live properties-do we need this? - done to have uniformity with main table
		if (edit != null)
		{
			addLiveResourceProperties(edit);
		}
		// track event - do we need this? no harm to keep track
		edit.setEvent(EVENT_RESOURCE_ADD);

		edit.setContentType(type);
		if(content != null)
		{
			edit.setContent(content);
		}
		addProperties(edit.getPropertiesEdit(), properties);

		// complete the edit - update xml which contains properties xml and store the file content
		m_storage.commitDeleteResource(edit, uuid);

		// close the edit object
		((BaseResourceEdit) edit).closeEdit();

		return edit;

	} // addDeleteResource

	/**
	 * check permissions for rename(). Note: for just this collection, not the members on down.
	 * 
	 * @param id
	 *        The id of the collection.
	 * @return true if the user is allowed to rename(id), false if not.
	 */
	public boolean allowRename(String id, String new_id)
	{
		m_logger.warn(this + ".allowRename(" + id + ") - Rename not implemented");
		return false;

		// return unlockCheck(EVENT_RESOURCE_ADD, new_id) &&
		// unlockCheck(EVENT_RESOURCE_REMOVE, id);

	} // allowRename

	/**
	 * Rename a collection or resource.
	 * 
	 * @param id
	 *        The id of the collection.
	 * @param new_id
	 *        The desired id of the collection.
	 * @return The full id of the resource after the rename is completed.
	 * @exception IdUnusedException
	 *            if the id does not exist.
	 * @exception TypeException
	 *            if the resource exists but is not a collection or resource.
	 * @exception PermissionException
	 *            if the user does not have permissions to rename
	 * @exception InUseException
	 *            if the id or a contained member is locked by someone else. collections, or remove any members of the collection.
	 * @exception IdUsedException
	 *            if copied item is a collection and the new id is already in use
	 *            or if the copied item is not a collection and a unique id cannot be found
	 *            in some arbitrary number of attempts (@see MAXIMUM_ATTEMPTS_FOR_UNIQUENESS).
	 * @exception ServerOverloadException
	 *            if the server is configured to write the resource body to the filesystem and the save fails.
	 */
	public String rename(String id, String new_id) throws IdUnusedException, TypeException, PermissionException,
			InUseException, OverQuotaException, InconsistentException, IdUsedException, ServerOverloadException
	{
		// Note - this could be implemented in this base class using a copy and a delete
		// and then overridden in those derived classes which can support
		// a direct rename operation.
		
		// check security for create new resource
		unlock(EVENT_RESOURCE_REMOVE, id);
		
		// check security for create new resource
		unlock(EVENT_RESOURCE_READ, id);
		
		// check security for remove
		unlock(EVENT_RESOURCE_ADD, new_id);
		
		boolean isCollection = false;
		boolean isRootCollection = false;
		ContentResourceEdit thisResource = null;
		ContentCollectionEdit thisCollection = null;

		if (m_logger.isDebugEnabled()) m_logger.debug(this + ".copy(" + id + "," + new_id + ")");

		if(m_storage.checkCollection(id))
		{
			isCollection = true;
			// find the collection
			thisCollection = editCollection(id);
			if(isRootCollection(id))
			{
				cancelCollection(thisCollection);
				throw new PermissionException(UsageSessionService.getSessionUserId(), null, null);
			}
		}
		else
		{
			thisResource = editResource(id);
		}

		if(thisResource == null && thisCollection == null)
		{
			throw new IdUnusedException(id);
		}

		if(isCollection)
		{
			new_id = copyCollection(thisCollection, new_id);
			removeCollection(thisCollection);
		}
		else
		{
			new_id = copyResource(thisResource, new_id);
			removeResource(thisResource);
		}
		return new_id;

	} // rename

	/**
	 * check permissions for copy().
	 * 
	 * @param id
	 *        The id of the new resource.
	 * @param new_id
	 *        The desired id of the new resource.
	 * @return true if the user is allowed to copy(id,new_id), false if not.
	 */
	public boolean allowCopy(String id, String new_id)
	{
		return unlockCheck(EVENT_RESOURCE_ADD, new_id) && unlockCheck(EVENT_RESOURCE_READ, id);
	}
	
	/**
	 * Copy a collection or resource from one location to another.  Creates a new collection with an id 
	 * similar to new_folder_id and recursively copies all nested collections and resources within thisCollection
	 * to the new collection. 
	 * 
	 * @param id
	 *        The id of the resource.
	 * @param folder_id
	 *        The id of the folder in which the copy should be created.
	 * @return The full id of the new copy of the resource.
	 * @exception PermissionException
	 *            if the user does not have permissions to read a containing collection, or to remove this resource.
	 * @exception IdUnusedException
	 *            if the resource id is not found.
	 * @exception TypeException
	 *            if the resource is a collection.
	 * @exception InUseException
	 *            if the resource is locked by someone else.
	 * @exception IdLengthException
	 *            if the new id of the copied item (or any nested item) is longer than the maximum length of an id.
	 * @exception InconsistentException
	 *            if the destination folder (folder_id) is contained within the source folder (id).
	 * @exception IdUsedException
	 *            if a unique resource id cannot be found after some arbitrary number of attempts (@see MAXIMUM_ATTEMPTS_FOR_UNIQUENESS).
	 * @exception ServerOverloadException
	 *            if the server is configured to write the resource body to the filesystem and the save fails.
	 */
	public String copyIntoFolder(String id, String folder_id) 
		throws PermissionException, IdUnusedException, TypeException, InUseException, IdLengthException, IdUniquenessException,
		OverQuotaException, InconsistentException, IdUsedException, ServerOverloadException
	{
		if(folder_id.startsWith(id))
		{
			throw new InconsistentException(id + " is contained within " + folder_id);
		}
		String new_id = newName(id, folder_id);
		if(new_id.length() >= MAXIMUM_RESOURCE_ID_LENGTH)
		{
			throw new IdLengthException(new_id);
		}
		
		// Should use copyIntoFolder if possible
		boolean isCollection = false;
		boolean isRootCollection = false;
		ContentResource thisResource = null;

		if (m_logger.isDebugEnabled()) m_logger.debug(this + ".copy(" + id + "," + new_id + ")");

		// find the collection
		ContentCollection thisCollection = findCollection(id);
		if (thisCollection != null)
		{
			isCollection = true;
			if (isRootCollection(id))
			{
				throw new PermissionException(null, null);
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
			new_id = deepcopyCollection(thisCollection, new_id);
		}
		else
		{
			new_id = copyResource(thisResource, new_id);
		}
		return new_id;
	}

	/**
	 * Calculate a candidate for a resource id for a resource being copied/moved into a new folder.
	 * @param id
	 * @param folder_id
	 * @exception PermissionException
	 *            if the user does not have permissions to read the properties for the existing resource.
	 * @exception IdUnusedException
	 *            if the resource id is not found.
	 */
	protected String newName(String id, String folder_id)
		throws PermissionException, IdUnusedException
	{
		String filename = isolateName(id);
		if(filename == null || filename.length() == 0)
		{
			ResourceProperties props = getProperties(id);
			filename = props.getProperty(ResourceProperties.PROP_DISPLAY_NAME);
		}
		if(! folder_id.endsWith(Entity.SEPARATOR))
		{
			folder_id += Entity.SEPARATOR;
		}
			
		return folder_id + filename;
	}
	
	/**
	 * Move a resource or collection to a (different) folder. This may be accomplished by renaming the resource or 
	 * by recursively renaming the collection and all enclosed members (no matter how deep) to effectively change
	 * their locations. Alternatively, it may be accomplished by copying the resource and recursively copying 
	 * collections from their existing collection to the new collection and ultimately deleting the original
	 * resource(s) and/or collections(s).
	 * 
	 * @param id
	 *        The id of the resource or collection to be moved.
	 * @param folder_id
	 *        The id of the folder to which the resource should be moved.
	 * @return The full id of the resource after the move is completed.
	 * @exception PermissionException
	 *            if the user does not have permissions to read a containing collection, or to remove this resource.
	 * @exception IdUnusedException
	 *            if the resource id is not found.
	 * @exception TypeException
	 *            if the resource is a collection.
	 * @exception InUseException
	 *            if the resource is locked by someone else.
	 * @exception InconsistentException
	 *            if the containing collection does not exist.
	 * @exception InconsistentException
	 *            if the destination folder (folder_id) is contained within the source folder (id).
	 * @exception IdUsedException
	 *            if a unique resource id cannot be found after some arbitrary number of attempts (@see MAXIMUM_ATTEMPTS_FOR_UNIQUENESS).
	 * @exception ServerOverloadException
	 *            if the server is configured to write the resource body to the filesystem and the save fails.
	 */
	public String moveIntoFolder(String id, String folder_id) 
		throws PermissionException, IdUnusedException, TypeException, InUseException, 
		OverQuotaException, IdUsedException, InconsistentException, ServerOverloadException
	{
		if(folder_id.startsWith(id))
		{
			throw new InconsistentException(id + " is contained within " + folder_id);
		}
		String new_id = newName(id, folder_id);
		
		// check security for delete existing resource
		unlock(EVENT_RESOURCE_REMOVE, id);
		
		// check security for read existing resource
		unlock(EVENT_RESOURCE_READ, id);
		
		// check security for add new resource
		unlock(EVENT_RESOURCE_ADD, new_id);
		
		boolean isCollection = false;
		boolean isRootCollection = false;
		ContentResourceEdit thisResource = null;
		ContentCollectionEdit thisCollection = null;

		if (m_logger.isDebugEnabled()) m_logger.debug(this + ".moveIntoFolder(" + id + "," + new_id + ")");

		if(m_storage.checkCollection(id))
		{
			isCollection = true;
			// find the collection
			thisCollection = editCollection(id);
			if(isRootCollection(id))
			{
				cancelCollection(thisCollection);
				throw new PermissionException(UsageSessionService.getSessionUserId(), null, null);
			}
		}
		else
		{
			thisResource = editResource(id);
		}

		if(thisResource == null && thisCollection == null)
		{
			throw new IdUnusedException(id);
		}

		if(isCollection)
		{
			new_id = moveCollection(thisCollection, new_id);
		}
		else
		{
			new_id = moveResource(thisResource, new_id);
		}
		return new_id;

	} // moveIntoFolder

	/**
	 * Move a collection to a new folder.  Moves the existing collection or creates a new collection with an id 
	 * similar to the new_folder_id (in which case the original collection is removed) and recursively moves all 
	 * nested collections and resources within thisCollection to the new collection.  When finished, thisCollection 
	 * no longer exists, but the collection identified by the return value has the same structure and all of the 
	 * members the original had (or copies of them). 
	 * 
	 * @param thisCollection
	 *        The collection to be copied
	 * @param new_folder_id
	 *        The desired id of the collection after it is moved.
	 * @return The full id of the moved collection.
	 * @exception PermissionException
	 *            if the user does not have permissions to perform the operations
	 * @exception IdUnusedException
	 *            if the collection id is not found.
	 * @exception TypeException
	 *            if the resource is not a collection.
	 * @exception InUseException
	 *            if the collection is locked by someone else.
	 * @exception IdUsedException
	 *            if a unique resource id cannot be found after some arbitrary number of attempts to find
	 *            a unique variation of the new_id (@see MAXIMUM_ATTEMPTS_FOR_UNIQUENESS).
	 * @exception ServerOverloadException
	 * 			 if the server is configured to save content bodies in the server's filesystem and an error occurs
	 *            trying to access the filesystem.
	 */
	protected String moveCollection(ContentCollectionEdit thisCollection, String new_folder_id) 
		throws PermissionException, IdUnusedException, TypeException, InUseException, OverQuotaException, IdUsedException, ServerOverloadException
	{
		String name = isolateName(new_folder_id);

		ResourceProperties properties = thisCollection.getProperties();
		ResourcePropertiesEdit newProps = duplicateResourceProperties(properties, thisCollection.getId());
		newProps.addProperty(ResourceProperties.PROP_DISPLAY_NAME, name);

		if (m_logger.isDebugEnabled()) m_logger.debug(this + ".copyCollection adding colletion=" + new_folder_id + " name=" + name);
		
		String base_id = new_folder_id + "-";
		boolean still_trying = true;
		int attempt = 0;
		try
		{
			while(still_trying && attempt < MAXIMUM_ATTEMPTS_FOR_UNIQUENESS)
			{
				try
				{
					ContentCollection newCollection = addCollection(new_folder_id, newProps);
				
					if (m_logger.isDebugEnabled()) m_logger.debug(this + ".moveCollection successful");
					still_trying = false;
				}
				catch (IdUsedException e)
				{
					try
					{
						ContentCollection test_for_exists = getCollection(new_folder_id);
					}
					catch(Exception ee)
					{
						throw e;
					}
					attempt++;
					if(attempt >= MAXIMUM_ATTEMPTS_FOR_UNIQUENESS)
					{
						throw e;
					}
					new_folder_id = base_id + attempt;
					newProps.addProperty(ResourceProperties.PROP_DISPLAY_NAME, name + "-" + attempt);
				}
			}
			
			List members = thisCollection.getMembers();
			
			if (m_logger.isDebugEnabled()) m_logger.debug(this + ".moveCollection size=" + members.size());

			Iterator memberIt = members.iterator();
			while(memberIt.hasNext())
			{
				String member_id = (String) memberIt.next();
				moveIntoFolder(member_id, new_folder_id);
			}
			
			removeCollection(thisCollection);
		}
		catch (InconsistentException e)
		{
			throw new TypeException(new_folder_id);
		}
		catch (IdInvalidException e)
		{
			throw new TypeException(new_folder_id);
		}
		
		return new_folder_id;
		
	}	// moveCollection

	/**
	 * Move a resource to a new folder.  Either creates a new resource with an id similar to the new_folder_id and 
	 * and removes the original resource, or renames the resource with an id similar to the new id, which effectively
	 * moves the resource to a new location. 
	 * 
	 * @param thisResource
	 *        The resource to be copied
	 * @param new_id
	 *        The desired id of the resource after it is moved.
	 * @return The full id of the moved resource (which may be a variation on the new_id to ensure uniqueness 
	 *         within the new folder.
	 * @exception PermissionException
	 *            if the user does not have permissions to perform the operations
	 * @exception IdUnusedException
	 *            if the resource id is not found.
	 * @exception TypeException
	 *            if the resource is a collection.
	 * @exception InUseException
	 *            if the resource is locked by someone else.
	 * @exception IdUsedException
	 *            if a unique resource id cannot be found after some arbitrary number of attempts to find
	 *            a unique variation of the new_id (@see MAXIMUM_ATTEMPTS_FOR_UNIQUENESS).
	 * @exception ServerOverloadException
	 * 			 if the server is configured to save content bodies in the server's filesystem and an error occurs
	 *            trying to access the filesystem.
	 */
	protected String moveResource(ContentResourceEdit thisResource, String new_id) 
		throws PermissionException, IdUnusedException, TypeException, InUseException, OverQuotaException, IdUsedException, ServerOverloadException
	{
		ResourceProperties properties = thisResource.getProperties();
		ResourcePropertiesEdit newProps = duplicateResourceProperties(properties, thisResource.getId());

		String displayName = newProps.getProperty(ResourceProperties.PROP_DISPLAY_NAME);
		String fileName = isolateName(new_id);
		String folderId = isolateContainingId(new_id);

		if (displayName == null && fileName != null)
		{
			newProps.addProperty(ResourceProperties.PROP_DISPLAY_NAME, fileName);
			displayName = fileName;
		}

		if (m_logger.isDebugEnabled()) m_logger.debug(this + ".moveResource displayname=" + displayName + " fileName=" + fileName);
		
		String basename = fileName;
		String extension = "";
		int index = fileName.lastIndexOf(".");
		if(index >= 0)
		{
			basename = fileName.substring(0, index);
			extension = fileName.substring(index);
		}
		
		boolean still_trying = true;
		int attempt = 0;

		while(still_trying && attempt < MAXIMUM_ATTEMPTS_FOR_UNIQUENESS)
		{
			// copy the resource to the new location
			try
			{
				ContentResource newResource = addResource(new_id, thisResource.getContentType(), thisResource.getContent(), newProps,
						NotificationService.NOTI_NONE);

				if (m_logger.isDebugEnabled()) m_logger.debug(this + ".moveResource successful");
				still_trying = false;
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
				try
				{
					ContentResource test_for_exists = getResource(new_id);
				}
				catch(Exception ee)
				{
					throw e;
				}
				if(attempt >= MAXIMUM_ATTEMPTS_FOR_UNIQUENESS)
				{
					throw e;
				}
				attempt++;
				new_id = folderId + basename + "-" + attempt + extension;
				newProps.addProperty(ResourceProperties.PROP_DISPLAY_NAME, displayName + "-" + attempt);
			}
		}

      String oldUuid = getUuid(thisResource.getId());
      setUuidInternal(new_id, oldUuid);
		removeResource(thisResource);
				
		return new_id;
		
	}	// moveResource

	/**
	 * Copy a resource or collection.  
	 * 
	 * @param id
	 *        The id of the resource.
	 * @param new_id
	 *        The desired id of the new resource.
	 * @exception PermissionException
	 *            if the user does not have permissions to read a containing collection, or to remove this resource.
	 * @exception IdUnusedException
	 *            if the resource id is not found.
	 * @exception TypeException
	 *            if the resource is a collection.
	 * @exception InUseException
	 *            if the resource is locked by someone else.
	 * @exception IdUsedException
	 *            if copied item is a collection and the new id is already in use
	 *            or if the copied item is not a collection and a unique id cannot be found
	 *            in some arbitrary number of attempts (@see MAXIMUM_ATTEMPTS_FOR_UNIQUENESS).
	 * @exception ServerOverloadException
	 *            if the server is configured to write the resource body to the filesystem and the save fails.
	 * @see copyIntoFolder(String, String) method (preferred method for invocation from a tool).
	 */
	public String copy(String id, String new_id) 
		throws PermissionException, IdUnusedException, TypeException, InUseException, OverQuotaException, IdUsedException, ServerOverloadException
	{
		// Should use copyIntoFolder if possible
		boolean isCollection = false;
		boolean isRootCollection = false;
		ContentResource thisResource = null;

		if (m_logger.isDebugEnabled()) m_logger.debug(this + ".copy(" + id + "," + new_id + ")");

		// find the collection
		ContentCollection thisCollection = findCollection(id);
		if (thisCollection != null)
		{
			isCollection = true;
			if (isRootCollection(id))
			{
				throw new PermissionException(null, null);
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
			new_id = copyCollection(thisCollection, new_id);
		}
		else
		{
			new_id = copyResource(thisResource, new_id);
		}
		return new_id;

	}

	/**
	 * getResourceNameCHEF - Needs to become a method of resource returns the internal name for a resource.
	 * 
	 * @@Glenn - review this @@
	 */

	public String getResourceNameCHEF(Entity mbr)
	{
		String idx = mbr.getId();
		String resourceName = isolateName(idx);
		ResourceProperties props = mbr.getProperties();
		if (resourceName == null) resourceName = props.getProperty(ResourceProperties.PROP_DISPLAY_NAME);

		return resourceName;
	}

	/**
	 * Get a duplicate copy of resource properties This copies everything except for the DISPLAYNAME - DISPLAYNAME is only copied if it is different than the file name as derived from the id (path) Note to Chuck - should the add operations check for empty
	 * Display and set it to the file name rather than putting all the code all over the place.
	 */
	private ResourcePropertiesEdit duplicateResourceProperties(ResourceProperties properties, String id)
	{
		ResourcePropertiesEdit resourceProperties = newResourceProperties();

		if (properties == null) return resourceProperties;

		// If there is a distinct display name, we keep it
		// If the display name is the "file name" we pitch it and let the name change
		String displayName = properties.getProperty(ResourceProperties.PROP_DISPLAY_NAME);
		String resourceName = isolateName(id);
		if (displayName == null) displayName = resourceName;
		if (displayName.length() == 0) displayName = resourceName;

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
	 * 
	 * @param thisResource
	 *        The resource to be copied
	 * @param new_id
	 *        The desired id of the new resource.
	 * @return The full id of the new copy of the resource.
	 * @exception PermissionException
	 *            if the user does not have permissions to read a containing collection, or to remove this resource.
	 * @exception IdUnusedException
	 *            if the resource id is not found.
	 * @exception TypeException
	 *            if the resource is a collection.
	 * @exception InUseException
	 *            if the resource is locked by someone else.
	 * @exception OverQuotaException 
	 * 			 if copying the resource would exceed the quota.
	 * @exception IdUsedException
	 * 			 if a unique id cannot be found in some arbitrary number of attempts (@see MAXIMUM_ATTEMPTS_FOR_UNIQUENESS).
	 * @exception ServerOverloadException
	 *            if the server is configured to write the resource body to the filesystem and the save fails.
	 */
	public String copyResource(ContentResource resource, String new_id) throws PermissionException, IdUnusedException, TypeException,
			InUseException, OverQuotaException, IdUsedException, ServerOverloadException
	{
		ResourceProperties properties = resource.getProperties();
		ResourcePropertiesEdit newProps = duplicateResourceProperties(properties, resource.getId());

		String displayName = newProps.getProperty(ResourceProperties.PROP_DISPLAY_NAME);
		String fileName = isolateName(new_id);
		String folderId = isolateContainingId(new_id);

		if (displayName == null && fileName != null)
		{
			newProps.addProperty(ResourceProperties.PROP_DISPLAY_NAME, fileName);
			displayName = fileName;
		}

		if (m_logger.isDebugEnabled()) m_logger.debug(this + ".copyResource displayname=" + displayName + " fileName=" + fileName);
		
		String basename = fileName;
		String extension = "";
		int index = fileName.lastIndexOf(".");
		if(index >= 0)
		{
			basename = fileName.substring(0, index);
			extension = fileName.substring(index);
		}
		
		boolean still_trying = true;
		int attempt = 0;

		while(still_trying && attempt < MAXIMUM_ATTEMPTS_FOR_UNIQUENESS)
		{
			// copy the resource to the new location
			try
			{
				ContentResource newResource = addResource(new_id, resource.getContentType(), resource.getContent(), newProps,
						NotificationService.NOTI_NONE);
				if (m_logger.isDebugEnabled()) m_logger.debug(this + ".copyResource successful");
				still_trying = false;
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
				try
				{
					ContentResource test_for_exists = getResource(new_id);
				}
				catch(Exception ee)
				{
					throw e;
				}
				if(attempt >= MAXIMUM_ATTEMPTS_FOR_UNIQUENESS)
				{
					throw e;
				}
				attempt++;
				new_id = folderId + basename + "-" + attempt + extension;
				newProps.addProperty(ResourceProperties.PROP_DISPLAY_NAME, displayName + " (" + attempt + ")");

				// Could come up with a naming convention to add versions here
				//throw new PermissionException(UsageSessionService.getSessionUserId(), null, null);
			}
		}
		return new_id;
		
	} // copyResource

	/**
	 * Copy a collection.
	 * 
	 * @param thisCollection
	 *        The collection to be copied
	 * @param new_id
	 *        The desired id of the new collection.
	 * @return The full id of the new copy of the resource.
	 * @exception PermissionException
	 *            if the user does not have permissions to perform the operations
	 * @exception IdUnusedException
	 *            if the collection id is not found.
	 * @exception TypeException
	 *            if the resource is not a collection.
	 * @exception InUseException
	 *            if the resource is locked by someone else.
	 * @exception IdUsedException
	 *            if the new collection id is already in use.
	 */
	public String copyCollection(ContentCollection thisCollection, String new_id) throws PermissionException, IdUnusedException,
			TypeException, InUseException, OverQuotaException, IdUsedException
	{
		List members = thisCollection.getMemberResources();

		if (m_logger.isDebugEnabled()) m_logger.debug(this + ".copyCollection size=" + members.size());

		if (members.size() > 0)
		{
			// recurse to copy everything in the folder?
			throw new PermissionException(null, null);
		}

		String name = isolateName(new_id);

		ResourceProperties properties = thisCollection.getProperties();
		ResourcePropertiesEdit newProps = duplicateResourceProperties(properties, thisCollection.getId());
		newProps.addProperty(ResourceProperties.PROP_DISPLAY_NAME, name);

		if (m_logger.isDebugEnabled()) m_logger.debug(this + ".copyCollection adding colletion=" + new_id + " name=" + name);

		try
		{
			ContentCollection newCollection = addCollection(new_id, newProps);
			if (m_logger.isDebugEnabled()) m_logger.debug(this + ".copyCollection successful");
		}
		catch (InconsistentException e)
		{
			throw new TypeException(new_id);
		}
		catch (IdInvalidException e)
		{
			throw new TypeException(new_id);
		}
		/*
		catch (IdUsedException e) // Why is this the case??
		{
			throw new PermissionException(null, null);
		}
		*/
		return new_id;

	} // copyCollection

	/**
	 * Make a deep copy of a collection.  Creates a new collection with an id similar to new_folder_id and recursively 
	 * copies all nested collections and resources within thisCollection to the new collection. 
	 * 
	 * @param thisCollection
	 *        The collection to be copied
	 * @param new_folder_id
	 *        The desired id of the new collection.
	 * @return The full id of the copied collection (which may be a slight variation on the desired id to ensure uniqueness).
	 * @exception PermissionException
	 *            if the user does not have permissions to perform the operations
	 * @exception IdUnusedException
	 *            if the collection id is not found. ???
	 * @exception TypeException
	 *            if the resource is not a collection.
	 * @exception InUseException
	 *            if the collection is locked by someone else.
	 * @exception IdUsedException
	 *            if a unique id cannot be found for the new collection after some arbitrary number of attempts to find
	 *            a unique variation of the new_folder_id (@see MAXIMUM_ATTEMPTS_FOR_UNIQUENESS).
	 * @exception ServerOverloadException
	 * 			 if the server is configured to save content bodies in the server's filesystem and an error occurs
	 *            trying to access the filesystem.
	 */
	protected String deepcopyCollection(ContentCollection thisCollection, String new_folder_id) 
		throws PermissionException, IdUnusedException, TypeException, InUseException, IdLengthException, IdUniquenessException, OverQuotaException, IdUsedException, ServerOverloadException
	{
		String name = isolateName(new_folder_id);

		ResourceProperties properties = thisCollection.getProperties();
		ResourcePropertiesEdit newProps = duplicateResourceProperties(properties, thisCollection.getId());
		newProps.addProperty(ResourceProperties.PROP_DISPLAY_NAME, name);

		if (m_logger.isDebugEnabled()) m_logger.debug(this + ".copyCollection adding colletion=" + new_folder_id + " name=" + name);
		
		String base_id = new_folder_id + "-";
		boolean still_trying = true;
		int attempt = 0;
		ContentCollection newCollection = null;
		try
		{
			try
			{
				newCollection = addCollection(new_folder_id, newProps);
			
				if (m_logger.isDebugEnabled()) m_logger.debug(this + ".moveCollection successful");
				still_trying = false;
			}
			catch (IdUsedException e)
			{
				try
				{
					checkCollection(new_folder_id);
				}
				catch(Exception ee)
				{
					throw new IdUniquenessException(new_folder_id);
				}
			}
			String containerId = this.isolateContainingId(new_folder_id);
			ContentCollection containingCollection = findCollection(containerId);
			SortedSet siblings = new TreeSet();
			siblings.addAll(containingCollection.getMembers());
			
			while(still_trying)
			{
				attempt++;
				if(attempt >= MAXIMUM_ATTEMPTS_FOR_UNIQUENESS)
				{
					throw new IdUniquenessException(new_folder_id);
				}
				new_folder_id = base_id + attempt;
				if(! siblings.contains(new_folder_id))
				{
					newProps.addProperty(ResourceProperties.PROP_DISPLAY_NAME, name + "-" + attempt);
					try
					{
						newCollection = addCollection(new_folder_id, newProps);
						still_trying = false;
					}
					catch(IdUsedException inner_e)
					{
						// try again
					}
				}
			}
			
			List members = thisCollection.getMembers();
			
			if (m_logger.isDebugEnabled()) m_logger.debug(this + ".moveCollection size=" + members.size());

			Iterator memberIt = members.iterator();
			while(memberIt.hasNext())
			{
				String member_id = (String) memberIt.next();
				copyIntoFolder(member_id, new_folder_id);
			}
			
		}
		catch (InconsistentException e)
		{
			throw new TypeException(new_folder_id);
		}
		catch (IdInvalidException e)
		{
			throw new TypeException(new_folder_id);
		}
		
		return new_folder_id;
		
	}	// deepcopyCollection

	/**
	 * Commit the changes made, and release the lock. The Object is disabled, and not to be used after this call.
	 * 
	 * @param edit
	 *        The ContentResourceEdit object to commit.
	 * @exception OverQuotaException
	 *            if this would result in being over quota (the edit is then cancled).
	 * @exception ServerOverloadException
	 *            if the server is configured to write the resource body to the filesystem and the save fails.
	 */
	public void commitResource(ContentResourceEdit edit) throws OverQuotaException, ServerOverloadException 
	{
		commitResource(edit, NotificationService.NOTI_OPTIONAL);

	} // commitResource

	/**
	 * Commit the changes made, and release the lock. The Object is disabled, and not to be used after this call.
	 * 
	 * @param edit
	 *        The ContentResourceEdit object to commit.
	 * @param priority
	 *        The notification priority of this commit.
	 * @exception OverQuotaException
	 *            if this would result in being over quota (the edit is then cancled).
	 * @exception ServerOverloadException
	 *            if the server is configured to write the resource body to the filesystem and the save fails.
	 */
	public void commitResource(ContentResourceEdit edit, int priority) throws OverQuotaException, ServerOverloadException
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
				m_logger.warn(this + ".commitResource(): closed ContentResourceEdit", e);
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
	 * Commit the changes made, and release the lock - no quota check. The Object is disabled, and not to be used after this call.
	 * 
	 * @param edit
	 *        The ContentResourceEdit object to commit.
	 * @param priority
	 *        The notification priority of this commit.
	 */
	protected void commitResourceEdit(ContentResourceEdit edit, int priority) throws ServerOverloadException
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
				m_logger.warn(this + ".commitResourceEdit(): closed ContentResourceEdit", e);
			}
			return;
		}

		// update the properties for update
		addLiveUpdateResourceProperties(edit);

		// complete the edit
		m_storage.commitResource(edit);

		// track it
		EventTrackingService.post(EventTrackingService.newEvent(((BaseResourceEdit) edit).getEvent(), edit.getReference(), true,
				priority));

		// close the edit object
		((BaseResourceEdit) edit).closeEdit();

	} // commitResourceEdit

	/**
	 * Cancel the changes made object, and release the lock. The Object is disabled, and not to be used after this call.
	 * 
	 * @param edit
	 *        The ContentResourceEdit object to commit.
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
				m_logger.warn(this + ".cancelResource(): closed ContentResourceEdit", e);
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
	 * 
	 * @param id
	 *        The id of the new resource.
	 * @return true if the user is allowed to getProperties(id), false if not.
	 */
	public boolean allowGetProperties(String id)
	{
		return unlockCheck(EVENT_RESOURCE_READ, id);

	} // allowGetProperties

	/**
	 * Access the properties of a resource with this resource id, either collection or resource.
	 * 
	 * @param id
	 *        The resource id.
	 * @exception PermissionException
	 *            if the user does not have permissions to read properties on this object or read through containing collections.
	 * @exception IdUnusedException
	 *            if the resource id is not found.
	 * @return the ResourceProperties object for this resource.
	 */
	public ResourceProperties getProperties(String id) throws PermissionException, IdUnusedException
	{
		unlock(EVENT_RESOURCE_READ, id);

		boolean collectionHint = id.endsWith(Entity.SEPARATOR);

		Entity o = null;

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
		if (o == null) throw new IdUnusedException(id);

		// track event - removed for clarity of the event log -ggolden
		// EventTrackingService.post(EventTrackingService.newEvent(EVENT_PROPERTIES_READ, getReference(id)));

		return o.getProperties();

	} // getProperties

	/**
	 * check permissions for addProperty().
	 * 
	 * @param id
	 *        The id of the new resource.
	 * @return true if the user is allowed to addProperty(id), false if not.
	 */
	public boolean allowAddProperty(String id)
	{
		return unlockCheck(EVENT_RESOURCE_WRITE, id);

	} // allowAddProperty

	/**
	 * Add / update a property for a resource, either collection or resource.
	 * 
	 * @param id
	 *        The resource id.
	 * @param name
	 *        The properties name to add or update
	 * @param value
	 *        The new value for the property.
	 * @exception PermissionException
	 *            if the user does not have premissions to write properties on this object or read through containing collections.
	 * @exception IdUnusedException
	 *            if the resource id is not found.
	 * @exception TypeException
	 *            if any property requested cannot be set (it may be live).
	 * @exception InUseException
	 *            if the resource is locked by someone else.
	 * @exception ServerOverloadException
	 *            if the server is configured to write the resource body to the filesystem and the save fails.
	 * @return the ResourceProperties object for this resource.
	 */
	public ResourceProperties addProperty(String id, String name, String value) throws PermissionException, IdUnusedException,
			TypeException, InUseException, ServerOverloadException
	{
		checkExplicitLock(id);
		unlock(EVENT_RESOURCE_WRITE, id);

		boolean collectionHint = id.endsWith(Entity.SEPARATOR);
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
		if (o == null) throw new IdUnusedException(id);

		// get the properties
		ResourcePropertiesEdit props = o.getPropertiesEdit();

		// check for TypeException updating live properties
		if (props.isLiveProperty(name)) throw new TypeException(name);

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
	 * 
	 * @param id
	 *        The id of the new resource.
	 * @return true if the user is allowed to removeProperty(id), false if not.
	 */
	public boolean allowRemoveProperty(String id)
	{
		return unlockCheck(EVENT_RESOURCE_WRITE, id);

	} // allowRemoveProperty

	/**
	 * Remove a property from a resource, either collection or resource.
	 * 
	 * @param id
	 *        The resource id.
	 * @param name
	 *        The property name to be removed from the resource.
	 * @exception PermissionException
	 *            if the user does not have premissions to write properties on this object or read through containing collections.
	 * @exception IdUnusedException
	 *            if the resource id is not found.
	 * @exception TypeException
	 *            if the property named cannot be removed.
	 * @exception InUseException
	 *            if the resource is locked by someone else.
	 * @exception ServerOverloadException
	 *            if the server is configured to write the resource body to the filesystem and the save fails.
	 * @return the ResourceProperties object for this resource.
	 */
	public ResourceProperties removeProperty(String id, String name) throws PermissionException, IdUnusedException, TypeException,
			InUseException, ServerOverloadException
	{
		checkExplicitLock(id);
		unlock(EVENT_RESOURCE_WRITE, id);

		boolean collectionHint = id.endsWith(Entity.SEPARATOR);
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
		if (o == null) throw new IdUnusedException(id);

		// get the properties
		ResourcePropertiesEdit props = o.getPropertiesEdit();

		// check for TypeException updating live properties
		if (props.isLiveProperty(name)) throw new TypeException(name);

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
	 * 
	 * @param id
	 *        The resource id.
	 * @return The resource URL.
	 */
	public String getUrl(String id)
	{
		// escape just the is part, not the access point
		return getAccessPoint(false) + Validator.escapeUrl(id);

	} // getUrl

	/**
	 * Access the internal reference from a resource id.
	 * 
	 * @param id
	 *        The resource id.
	 * @return The internal reference from a resource id.
	 */
	public String getReference(String id)
	{
		return getAccessPoint(true) + id;

	} // getReference

	/**
	 * Access the resource id of the collection which contains this collection or resource.
	 * 
	 * @param id
	 *        The resource id (reference, or URL) of the ContentCollection or ContentResource
	 * @return the resource id (reference, or URL, depending on the id parameter) of the collection which contains this resource.
	 */
	public String getContainingCollectionId(String id)
	{
		return isolateContainingId(id);

	} // getContainingCollectionId

	/**
	 * Get the depth of the resource/collection object in the hireachy based on the given collection id
	 * 
	 * @param resourceId
	 *        The Id of the resource/collection object to be tested
	 * @param baseCollectionId
	 *        The Id of the collection as the relative root level
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
			while (s.indexOf(Entity.SEPARATOR) != -1)
			{
				if (s.indexOf(Entity.SEPARATOR) != (s.length() - 1))
				{
					// the resource seperator character is not the last character
					i++;
					s = s.substring(s.indexOf(Entity.SEPARATOR) + 1);
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
	 * 
	 * @param id
	 *        The resource id (reference, or URL) of a ContentCollection
	 * @return true if this is the root collection
	 */
	public boolean isRootCollection(String id)
	{
		// test for the root local id
		if (id.equals(Entity.SEPARATOR)) return true;

		// test for the root reference
		if (id.equals(getReference(Entity.SEPARATOR))) return true;

		// test for the root URL
		if (id.equals(getUrl(Entity.SEPARATOR))) return true;

		return false;

	} // isRootCollection

	/**
	 * Construct a stand-alone, not associated with any particular resource, ResourceProperties object.
	 * 
	 * @return The new ResourceProperties object.
	 */
	public ResourcePropertiesEdit newResourceProperties()
	{
		return new BaseResourcePropertiesEdit();

	} // newResourceProperties

	/**********************************************************************************************************************************************************************************************************************************************************
	 * ResourceService implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * {@inheritDoc}
	 */
	public String getLabel()
	{
		return "content";
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean willArchiveMerge()
	{
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean willImport()
	{
		return true;
	}

	/** stream content requests if true, read all into memory and send if false. */
	protected static final boolean STREAM_CONTENT = true;

	/** The chunk size used when streaming (100k). */
	protected static final int STREAM_BUFFER_SIZE = 102400;

	/**
	 * {@inheritDoc}
	 */

	protected boolean writeFile(String name, String type, byte[] data, 
	                     String dir, HttpServletResponse resp, boolean mkdir) {
	    // /content
	    String accessPoint = getAccessPoint(true);  

	    // System.out.println("writefile " + accessPoint + ":" + dir + " / " + name);
	    try {
		// validate filename. Need to be fairly careful.
		int i = name.lastIndexOf(Entity.SEPARATOR);
		if (i >= 0)
		    name = name.substring(i+1);
		if (name.length() < 1) {
		    // System.out.println("no name left / removal");
		    resp.sendError(HttpServletResponse.SC_FORBIDDEN);
		    return false;
		}

		// dirname better end in /
		if (dir.lastIndexOf(Entity.SEPARATOR) != (dir.length()-1))
		    dir = dir + Entity.SEPARATOR;


		if (dir.startsWith(accessPoint))
		    dir = dir.substring(accessPoint.length());

	        String path = dir + name;
	
		ResourcePropertiesEdit resourceProperties = newResourceProperties();

		// Try to delete the resource
		try {
			// System.out.println("Trying Del  "  + path);
			//The existing document may be a collection or a file.
			boolean isCollection = getProperties (path).getBooleanProperty (ResourceProperties.PROP_IS_COLLECTION);
			
			if (isCollection) {
			    // System.out.println("Can't del, iscoll");
			    resp.sendError(HttpServletResponse.SC_FORBIDDEN);
			    return false;
			} else {
			    // not sure why removeesource(path) didn't 
			    // work for my workspace
			    BaseResourceEdit edit = (BaseResourceEdit) editResource(path);
			    // if (edit != null)
			    //	System.out.println("Got edit");
			    removeResource(edit);
			}
		} catch (IdUnusedException e) {
			// Normal situation - nothing to do
		} catch (Exception e) {
			// System.out.println("Can't del, exception " + e.getClass() + ": " + e.getMessage());
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
			return false;
		}

		// Add the resource

		try {
			User user = UserDirectoryService.getCurrentUser();
			
			TimeBreakdown timeBreakdown = TimeService.newTime().breakdownLocal();
			String mycopyright = "copyright (c)" + " " + timeBreakdown.getYear () +", " + user.getDisplayName() + ". All Rights Reserved. ";
			
			resourceProperties.addProperty (ResourceProperties.PROP_COPYRIGHT, mycopyright);

			resourceProperties.addProperty(ResourceProperties.PROP_DISPLAY_NAME, name);

			// System.out.println("Trying Add "  + path);
			ContentResource resource = 
			    addResource (path,
					 type, data,
					 resourceProperties,
					 NotificationService.NOTI_NONE);

		} catch (InconsistentException e) {
			// get this error if containing dir doesn't exist
			if (mkdir) {
			    try {
				ContentCollection collection = addCollection (dir, resourceProperties);
				return writeFile(name, type, data, dir, resp, false);
			    } catch (Throwable ee) {
			    }
			}
			// System.out.println("Add fail, inconsistent");
			resp.sendError(HttpServletResponse.SC_CONFLICT);
			return false;
		} catch (IdUsedException e) {
			// Should not happen because we deleted above (unless tawo requests at same time)
		        // System.out.println("Add fail, in use");
			Log.warn("sakai","access post IdUsedException:" + e.getMessage());
			
			resp.sendError(HttpServletResponse.SC_CONFLICT);
			return false;
		} catch (Exception e) {
			// System.out.println("Add failed, exception " + e.getClass() + ": " + e.getMessage());
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
			return false;
		}
	    } catch (IOException e) {
		// System.out.println("overall fail IOException " + e);
	    }
	    return true;
	}

	public void doPost(HttpServletRequest req, HttpServletResponse res)
	{
		String path = req.getPathInfo();
		// System.out.println("path " + path);
		if (path == null) path = "";
		// assume caller has verified that it is a request for content and that it's multipart
		// loop over attributes in request, picking out the ones
		// that are file uploads and doing them
		for (Enumeration e = req.getAttributeNames() ; e.hasMoreElements() ;) {
		    String iname = (String)e.nextElement();
		    // System.out.println("Item " + iname);
		    Object o = req.getAttribute(iname);
		    // NOTE: Fileitem is from
		    // org.apache.commons.fileupload.FileItem, not
		    // sakai's parameterparser version
		    if (o != null && o instanceof FileItem) {
			FileItem fi = (FileItem) o;
			// System.out.println("found file " +  fi.getName());
			if (!writeFile(fi.getName(),
				       fi.getContentType(),
				       fi.get(),
				       path,
				       res, true))
			    return;
		    }
		}
		return;
	}   // doPost


	class directoryDone extends Exception
	{
	}

	class redirectDone extends Exception
	{
	}

	class noAccess extends Exception
	{
	}

        public class RuComparator implements Comparator {

	    public RuComparator() {}

	    public int compare(Object o1, Object o2) {

		ResourceProperties p1 = ((Entity)o1).getProperties();
		ResourceProperties p2 = ((Entity)o2).getProperties();

		String so1 = p1.getProperty("RU:resourceorder");
		String so2 = p2.getProperty("RU:resourceorder");

		int t1;
		int t2;

		// first sort on type, so that those with specified order
		// come first

		if (so1 != null && !so1.equals(""))
		    t1 = 1;
		else if (o1 instanceof ContentCollection)
		    t1 = 2;
		else
		    t1 = 3;

		if (so2 != null && !so2.equals(""))
		    t2 = 1;
		else if (o2 instanceof ContentCollection)
		    t2 = 2;
		else
		    t2 = 3;

		if (t1 < t2)
		    return -1;
		else if (t1 > t2)
		    return 1;

		// now, they are the same type, so compare within type
		// if order properties, use them

		if (so1 != null && !so1.equals("") 
		    && so2 != null && !so2.equals("")) {

		    int i1 = Integer.parseInt(so1);
		    int i2 = Integer.parseInt(so2);
		    if (i1 == i2)
			return 0;
		    else if (i1 < i2)
			return -1;
		    else return 1;
		}

                // else do a formatted interpretation - case insensitive
                String s1 = p1.getPropertyFormatted (ResourceProperties.PROP_DISPLAY_NAME);
		String s2 = p2.getPropertyFormatted (ResourceProperties.PROP_DISPLAY_NAME);
		return s1.compareToIgnoreCase(s2);
	    }
	}

        public int countSlashes(String s)
        {
	    int count = 0;
	    int loc = s.indexOf ('/');

	    while (loc >= 0) {
		count ++;
		loc ++;
		loc = s.indexOf ('/', loc);
	    }

	    return count;
	}

        // see if the path is a directory
        //   return index.html to be displayed,
        //   do directory listing then throw directoryDone
        //   issue a redirect then throw redirectDone
        //   return null in which case we continue
        // 
        //   throwing directoryDone is done in cases where we want the
        //     caller to return immediately, because we've handled the request

    	public Reference doDirectory(Reference ref, HttpServletRequest req, HttpServletResponse res)
	    throws PermissionException
	{
	    // the reason for all this magic with paths is that I'm
	    // trying to use appropriate functions like getAccessPoint
	    // rather than hardcoding strings like /access or /content.
	    // also, I remove the http://host, so the redirects work
	    // whether the user is using http or https

	    // System.out.println("abs: " + getAccessPoint(false) + " rel: " + getAccessPoint(true));
	    String path = ref.getId();
	    String basedir = req.getParameter("sbasedir");
	    // path may have been transformed by an alias
	    // we need origpath to check whether there was as trailing /
	    String origpath = req.getPathInfo();
	    String querystring = req.getQueryString();
	    // set access to /access/content, must skip http://host
	    String access = getAccessPoint(false);
	    int i = access.indexOf("://");
	    if (i > 0) 
		i = access.indexOf("/", i+3);
	    if (i > 0)
		access = access.substring(i);

	    // compute string for redirect. It's the original
	    // with slash at the end of the path.
	    // the problem is that getpathinfo is missing /access
	    i = access.indexOf(getAccessPoint(true));
	    String redpath = access.substring(0, i) + origpath;
	    if (!redpath.endsWith(Entity.SEPARATOR))
		redpath = redpath + Entity.SEPARATOR;
	    // if there's a query string, add it
	    if (querystring != null && !querystring.equals(""))
		redpath = redpath + "?" + querystring;

	    // System.out.println("redpath " + redpath);

	    boolean sferyx = true;
	    if (basedir == null || basedir.equals("")) {
		sferyx = false;
		basedir = req.getParameter("basedir");
	    }
	    String field = req.getParameter("field");
	    
	    // System.out.println("basedir " + basedir);

	    if (field == null)
		field = "url";

	    // See if we need to look up index.html. If it's a
	    // collection name, index.html exists, and the original
	    // path didn't end in /, we have to redirect to the name
	    // with the slash. Otherwise any relative URL's in
	    // index.html will fail. The redirect is necessary because
	    // the client has to adjust it's idea of the path.

	    // implement index.html 
	    // now see if the user can access it.
	    // This is complicated by the way collections are
	    // implemented. We have to try adding /index.html pretty
	    // much no matter what. Permissions are checked before
	    // existence. But the user might not be permitted to see
	    // the main dir but still be allowed to see index.html. So
	    // we really have to try both

	    boolean found = false;
	    String colpath = "";
	    Reference colref = null;
	    boolean permitted = true;
	    boolean iscoll = false;
			    
	    // permission is checked first, so the default should be true
	    
	    // this code only has an effect if we actually find and can
	    // read index.html. Otherwise leave failure what it was
	    // This is true only because permissions are never
	    // tighter inside subdirs.

	    if (path.endsWith(Entity.SEPARATOR))
		colpath = path + "index.html";
	    else
		colpath = path + "/index.html";

	    // System.out.println("second try " + colpath);
	    // sferyx and basedir are file pickers. We need to see the
	    // real directory for them, so ignore index.html
	    // System.out.println("origpath " + origpath);
	    if (!sferyx && basedir == null)
	    try {
		checkResource(colpath);
		// if we get here, index.html exists and we can read it
		// System.out.println("second check ok");
		// If path doesn't end with /, redirect. The problem
		// is that relative references won't work. That's why
		// Apache issues redirects in the same situation.
		if (!origpath.endsWith(Entity.SEPARATOR)) {
		    // System.out.println("need redirect");
		    try {
			res.sendRedirect(redpath);
			// System.out.println("redirect ok");
			return null;
		    } catch (IOException ignore) {
			// System.out.println("redirect failed");
			return null; // ???
		    }
		    // only here if redirect fails. Note sure what to do.
		    // There's an index.html, but without the redirect
		    // relative references on it will fail.
		    // for the moment forget index.html.
 		} else {
		    // don't need redirect - process index.html
		    return m_entityManager.newReference(getAccessPoint(true) + colpath);
		}
	    } catch (Throwable tt) {
		// System.out.println("no index.html")
		;
	    }

	    // index.html not usable, do directory listing

	    if (path.endsWith(Entity.SEPARATOR))
		colpath = path;
	    else
		colpath = path + "/";

	    // System.out.println("colpath " + colpath);
	    try {
		checkCollection(colpath);
		// if nothing thrown, it exists and we can read it
		// System.out.println("is coll");
	    } catch (PermissionException t) {
		// System.out.println("permission " + ref.getReference());
		// note that we throw the original path before any alias
		// mapping. 
		String throwpath = origpath;
		if (!throwpath.endsWith(Entity.SEPARATOR))
		    throwpath = throwpath + "/";
		throw new PermissionException(EVENT_RESOURCE_READ, throwpath);
	    } catch (Exception t) {
		try {
		    res.sendError(HttpServletResponse.SC_FORBIDDEN);
		} catch (Exception tt) {
		}
		return null;
	    }

	    // OK, it's a collection and we can read it. Do a listing.
	    // System.out.println("got to final check");

	    PrintWriter out = null;
	    // don't set the writer until we verify that
	    // getallresources is going to work.

	    try {
		ContentCollection x = 
		    getCollection(colpath);

		// I want to use relative paths in the listing,
		// so we need to redirect if there's no trailing /
		// for the usual reasons.
		if (!origpath.endsWith(Entity.SEPARATOR)) {
		    // System.out.println("need redirect");
		    try {
			res.sendRedirect(redpath);
			return null;
		    } catch (IOException ignore) {
			return null;
		    }
		}

		List members = x.getMemberResources();
		// we will need resources. getting them once makes the sort a whole lot faster

		// System.out.println("before sort have " + members.size());

		if (sferyx || basedir != null)
		    Collections.sort(members, new ContentHostingComparator (ResourceProperties.PROP_DISPLAY_NAME, true));
		else
		    Collections.sort (members, new RuComparator ());

		// System.out.println("after sort have " + members.size());

		Iterator xi = members.iterator();

		res.setContentType("text/html; charset=UTF-8");

		out = res.getWriter();

		if (sferyx) {
		    out.println("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\"><title>Control Panel - FileBrowser</title></head><body bgcolor=\"#FFFFFF\" topmargin=\"0\" leftmargin=\"0\"><b><font color=\"#000000\" face=\"Arial\" size=\"3\">Path:&nbsp;" + access + Validator.escapeHtml(path) + "</font></b><table border=\"0\" width=\"100%\" bgcolor=\"#FFFFFF\" cellspacing=\"0\" cellpadding=\"0\">");
		} else {
		    ResourceProperties pl = x.getProperties();
		    out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
		    out.println("<html><head>");
		    out.println("<title>" + "Index of " + pl.getProperty(ResourceProperties.PROP_DISPLAY_NAME) + "</title>");
		    String webappRoot = m_serverConfigurationService.getServerUrl();
		    out.println("<link href=\"" + webappRoot + "/library/skin/default/access.css\" type=\"text/css\" rel=\"stylesheet\" media=\"screen\" />");
		    if (basedir != null) {
			    out.println("<script language=\"javascript\">");
			    out.println("function seturl(url) {");
			    out.println("window.opener.document.forms[0]." + field + ".value = url;  window.close();");
			    out.println("}");
			    out.println("</script>");
		    }
		
		    out.println("</head><body>");
		    out.println("<div class=\"directoryIndex\">");
		    // for content listing it's best to use a real title
		    if (basedir != null)
			out.println("<h2>Contents of " + access + path + "</h2>");
		    else {
			out.println("<h2>" + pl.getProperty(ResourceProperties.PROP_DISPLAY_NAME) + "</h2>");
			String desc = pl.getProperty(ResourceProperties.PROP_DESCRIPTION);
			if (desc != null && !desc.equals(""))
			    out.println("<p>" + desc + "</p>");
		    }

		    out.println("<table summary=\"Directory index\">");

		}

		int slashes = countSlashes(path);

		// basedir will be a full url: 
		// http://host:8080/access/content/group/db5a4d0c-3dfd-4d10-8018-41db42ac7c8b/
		// possibly with a file name on the end.
		// xss is just the file name. Compute a prefix

		String filepref = "";
		//  /content
		String relaccess = getAccessPoint(true);
		if (basedir != null && !basedir.equals("none")) {
		    // start bases after /access/content, since it isn't in path
		    String bases = basedir.substring(basedir.indexOf(relaccess)+ relaccess.length());
		    int lastslash = 0;
		    // path is always a directory, so it ends in /
		    // do that for base as well
		    if (!bases.endsWith("/")) {
			lastslash = bases.lastIndexOf("/");
			if (lastslash > 0)
			    bases = bases.substring(0, lastslash+1);
		    }
		    // path and bases should now be comparable, starting
		    // at /user or /group and ending in /
		    // bases: /a/b/c/
		    // path: /a/b/d/
		    // need ../d
		    // this code is used in a context where we know there
		    // actually is overlap
		    while (bases.length() > path.length() ||
			   (!bases.equals("/") &&
			   !bases.equals(path.substring(0, bases.length())))) {
			lastslash = bases.lastIndexOf("/",bases.length()-2);
			if (lastslash < 0)
			    break;
			filepref = filepref + "../";
			bases = bases.substring(0, lastslash+1);
		    }
		    // bases is now the common part, e.g. /a/b/  /a/b/c
		    // add the rest of path
		    if (path.length() > bases.length())
			filepref = filepref + Validator.escapeUrl(path.substring(bases.length()));
		} else if (basedir != null && basedir.equals("none")) {
		    filepref= access + Validator.escapeUrl(path);
		}

		// for web content format, need to be able to choose main URL

		String baseparam = "";
		if (sferyx)
		    baseparam = "?sbasedir=" + Validator.escapeUrl(basedir);
		else if (basedir != null)
		    baseparam = "?basedir=" + Validator.escapeUrl(basedir)
			        + "&field=" + Validator.escapeUrl(field);

		if (slashes > 3) {
		    // go up a level
		    String uplev = path.substring(0, path.length() - 1);
		    uplev = access + uplev.substring(0, uplev.lastIndexOf('/')+1);

		    if (sferyx)
			out.println("<tr><td align=\"center\" left=\"50%\" height=\"20\"><b><a href=\"../" + baseparam + "\">Up one level</a></b></td><td width=\"20%\"></td><td width=\"30%\"></td></tr><form name=\"fileSelections\">");
		    else if (basedir != null)
			out.println("<tr><td><a href=\"../" + baseparam + "\">Up one level</a></td><td><b>Folder</b>" +
				"</td><td>" +
				"</td><td>" +
				"</td><td>" +
				"</td></tr>");
		    else
			out.println("<tr><td><a href=\"../\">Up one level</a> [Folder]</td><td></td></tr>");
		} else if (sferyx) 
		    out.println("<tr><td align=\"center\" left=\"50%\" height=\"20\">&nbsp;</td><td width=\"20%\"></td><td width=\"30%\"></td></tr><form name=\"fileSelections\">");

		while (xi.hasNext()) {
		    // System.out.println("hasnext");
		    Entity nextres = (Entity) xi.next();
		    ResourceProperties properties = nextres.getProperties();
		    boolean isCollection = properties.getBooleanProperty(ResourceProperties.PROP_IS_COLLECTION);
		    String xs = nextres.getId();

		    ContentResource content = null;
		    if (isCollection) {
			xs = xs.substring(0, xs.length()-1);
			xs = xs.substring(xs.lastIndexOf('/')+1) + '/';
		    } else {
			content = (ContentResource)nextres;
			xs = xs.substring(xs.lastIndexOf('/')+1);
		    }

		    // System.out.println("id " + xs);
		    
		    try {

			if (isCollection) {
			    if (sferyx)  
				out.println("<tr><td bgcolor=\"#FFF678\" align=\"LEFT\"><font face=\"Arial\" size=\"3\">&nbsp;<a href=\"" + Validator.escapeUrl(xs) + baseparam + "\">" + Validator.escapeHtml(xs) + "</a></font></td><td bgcolor=\"#FFF678\" align=\"RIGHT\"><font face=\"Arial\" size=\"3\" color=\"#000000\">File Folder</font></td><td>&nbsp;</td></tr>");
			    else if (basedir != null) 
				out.println("<tr><td><button type=button onclick=\"seturl('" + filepref + Validator.escapeHtml(xs)+ "')\">Choose</button>&nbsp;<a href=\"" + Validator.escapeUrl(xs) + baseparam + "\">" + Validator.escapeHtml(xs) + "</a></td><td><b>Folder</b>" +
				    "</td><td>" +
				    "</td><td>" +
				    "</td><td>" +
				    "</td></tr>");
			    else {
				String desc = properties.getProperty(ResourceProperties.PROP_DESCRIPTION);
				if (desc == null)
				    desc = "";

				out.println("<tr><td><a href=\"" + Validator.escapeUrl(xs) + baseparam + "\">" + Validator.escapeHtml(properties.getProperty(ResourceProperties.PROP_DISPLAY_NAME)) + "</a> [Folder]</td><td>" + Validator.escapeHtml(desc) + "</td></tr>");
			    }
			} else {
			    long filesize = ((content.getContentLength() - 1) / 1024) + 1;
			    String createdBy = properties.getUserProperty(ResourceProperties.PROP_CREATOR).getDisplayName();
			    Time modTime = properties.getTimeProperty(ResourceProperties.PROP_MODIFIED_DATE);
			    String modifiedTime = modTime.toStringLocalShortDate() + " " + modTime.toStringLocalShort();
			    String filetype = content.getContentType();

			    if (sferyx) 
				out.println("<tr><td bgcolor=\"#FFFFFF\" align=\"LEFT\"><font face=\"Arial\" size=\"3\">&nbsp;&nbsp;</font><input type=\"submit\" name=\"selectedFiles\" value=\"" + filepref + Validator.escapeUrl(xs) + "\"></td><td bgcolor=\"#FFFFFF\" align=\"RIGHT\"><font face=\"Arial\" size=\"3\">" + filesize + "</font></td><td bgcolor=\"#FFFFFF\" align=\"LEFT\"><font face=\"Arial\" size=\"3\">&nbsp;&nbsp;" + modifiedTime + "</font></td></tr>");
			    else if (basedir != null)
				out.println("<tr><td><button type=button onclick=\"seturl('" + filepref + Validator.escapeHtml(xs)+ "')\">Choose</button>&nbsp;&nbsp;" + 
					    Validator.escapeHtml(xs) + "</td><td>" +
					    filesize + "</td><td>" +
					    createdBy + "</td><td>" +
					    filetype + "</td><td>" +
					    modifiedTime + "</td></tr>");
			    else {
				String desc = properties.getProperty(ResourceProperties.PROP_DESCRIPTION);
				if (desc == null)
				    desc = "";

				out.println("<tr><td><a href=\"" + Validator.escapeUrl(xs) + "\" target=_blank>" + Validator.escapeHtml(properties.getProperty(ResourceProperties.PROP_DISPLAY_NAME)) + "</a></td><td>" + Validator.escapeHtml(desc) + "</td></tr>");
			    }
			}
		    } catch (Throwable ignore) {
			if (sferyx) 
			    out.println("<tr><td bgcolor=\"#FFFFFF\" align=\"LEFT\"><font face=\"Arial\" size=\"3\">&nbsp;&nbsp;</font><input type=\"submit\" name=\"selectedFiles\" value=\"" + filepref + Validator.escapeHtml(xs) + "\"></td><td bgcolor=\"#FFFFFF\" align=\"RIGHT\"><font face=\"Arial\" size=\"3\">&nbsp</font></td><td bgcolor=\"#FFFFFF\" align=\"LEFT\"><font face=\"Arial\" size=\"3\">&nbsp;&nbsp;</font></td></tr>");
			else if (basedir != null) 
			    out.println("<tr><td><button type=button onclick=\"seturl('" + filepref + Validator.escapeHtml(xs)+ "')\">Choose</button>&nbsp;&nbsp;" + 
				    Validator.escapeHtml(xs) + "</td><td>" +
				    "</td><td>" +
				    "</td><td>" +
				    "</td><td>" +
				    "</td></tr>");
			else
			    out.println("<tr><td><a href=\"" + Validator.escapeUrl(xs) + "\" target=_blank>" + Validator.escapeHtml(xs) + "</a></td><td></tr>");
		    }
		}

                // Close document
                out.println("</table></div></body></html>");

	    } catch (Throwable ignore) {
	    } 
	    return null;
	}

	public HttpAccess getHttpAccess()
	{
		return new HttpAccess()
		{

			// Can't find anyplace we can import these. Please fix if you know.
			private static final String METHOD_POST = "POST";

			public void handleAccess(HttpServletRequest req, HttpServletResponse res, Reference ref, Collection copyrightAcceptedRefs) throws PermissionException,
					IdUnusedException, ServerOverloadException, CopyrightException
			{
			    // System.out.println("handleaccess " + req.getMethod());
				if (req.getMethod().equals(METHOD_POST) && FileUpload.isMultipartContent(req)) {
				    doPost(req, res);
				    return;
				}

				// we only access resources, not collections
				if (ref.getId().endsWith(Entity.SEPARATOR)) {
				    ref = doDirectory(ref, req, res);
				    if (ref == null)
					return;
				} else try {

				// If the user omits the trailing /, we do
				// want to find it. findCollection ignores
				// permissions, so it will really tell us
				// whether we have a collection

				    ContentCollection collection = findCollection(ref.getId() + "/");
				    if (collection != null) {
					// only get here if it's really a collection
					ref = doDirectory(ref, req, res);
				        if (ref == null)
					   return;
				    }
				} catch (Exception e) {
				    // most likely not a collection
				}

				// need read permission
				// if (!allowGetResource(ref.getId())) throw new PermissionException(EVENT_RESOURCE_READ, ref.getReference());
				// need to use original path in case the user
				// accessed this through an alias. getpathinfo
				// will use the origional alias, whereas 
				// ref.getid will be mapped.
				if (!allowGetResource(ref.getId())) throw new PermissionException(EVENT_RESOURCE_READ, req.getPathInfo());

				BaseResourceEdit resource = null;
				try
				{
					resource = (BaseResourceEdit) getResource(ref.getId());
				}
				catch (TypeException e)
				{
				    // TypeException means it's a directory
				    // presumably without / at the end
				        ref = doDirectory(ref, req, res);
					if (ref == null)
					    return;
					try {
					    resource = (BaseResourceEdit) getResource(ref.getId());
					} catch (TypeException ee) {
					    throw new IdUnusedException(ref.getReference());
					}
				}

				// if this entity requires a copyright agreement, and has not yet been set, get one
				if (resource.requiresCopyrightAgreement() && !copyrightAcceptedRefs.contains(ref.getReference()))
				{
					throw new CopyrightException();
				}

				try
				{
					ResourceProperties properties = resource.getProperties();

					// changed to int from long because res.setContentLength won't take long param -- JE
					int len = resource.getContentLength();
					String contentType = resource.getContentType();
			
					// for url content type, encode a redirect to the body URL
					if (contentType.equalsIgnoreCase(ResourceProperties.TYPE_URL))
					{		
						byte[] content = resource.getContent();
						if ((content == null) || (content.length == 0))
						{
							throw new IdUnusedException(ref.getReference()	);
						}

						String one = new String(content);
						String two = "";
						for (int i=0; i < one.length(); i++)
						{
							if (one.charAt(i) == '+')
							{
								two += "%2b";
							}
							else
							{
								two += one.charAt(i);
							}
						}
						res.sendRedirect(two);
					}
			
					else
					{
						// use the last part, the file name part of the id, for the download file name
						String fileName = Validator.getFileName(ref.getId());
						fileName = Validator.escapeResourceName(fileName);
			
						String disposition = null;
						if (Validator.letBrowserInline(contentType))
						{
							disposition = "inline; filename=\"" + fileName+"\"";
						}
						else
						{
							disposition = "attachment; filename=\"" + fileName +"\"";
						}
			
						// NOTE:  Only set the encoding on the content we have to.
						// Files uploaded by the user may have been created with different encodings, such as ISO-8859-1;
						// rather than (sometimes wrongly) saying its UTF-8, let the browser auto-detect the encoding.
						// If the content was created through the WYSIWYG editor, the encoding does need to be set (UTF-8).
						String encoding = resource.getProperties().getProperty(ResourceProperties.PROP_CONTENT_ENCODING);
						if (encoding != null && encoding.length() > 0)
						{
							contentType = contentType + "; charset="+encoding;
						}
						
						// stream the content using a small buffer to keep memory managed
						if (STREAM_CONTENT)
						{
							InputStream content = null;
							OutputStream out = null;
			
							try
							{
								content = resource.streamContent();
								if (content == null)
								{
									throw new IdUnusedException(ref.getReference());
								}
			
								res.setContentType(contentType);
								res.addHeader("Content-Disposition", disposition);
								res.setContentLength(len);
			
								// set the buffer of the response to match what we are reading from the request
								if (len < STREAM_BUFFER_SIZE)
								{						
									res.setBufferSize(len);
								}
								else
								{
									res.setBufferSize(STREAM_BUFFER_SIZE);
								}
			
								out = res.getOutputStream();
			
								// chunk
								byte[] chunk = new byte[STREAM_BUFFER_SIZE];
								int lenRead;
								while ((lenRead = content.read(chunk)) != -1)
								{
									out.write(chunk, 0, lenRead);					
								}
							}
							catch (ServerOverloadException e)
							{
								throw e;
							}
							catch (Throwable ignore)
							{
							}
							finally
							{
								// be a good little program and close the stream - freeing up valuable system resources
								if (content != null)
								{
									content.close();
								}
			
								if (out != null)
								{
									try
									{
										out.close();
									}
									catch (Throwable ignore)
									{
									}
								}
							}
						}
			
						// read the entire content into memory and send it from there
						else
						{
							byte[] content = resource.getContent();
							if (content == null)
							{
								throw new IdUnusedException(ref.getReference());
							}
			
							res.setContentType(contentType);
							res.addHeader("Content-Disposition", disposition);
							res.setContentLength(len);
			
							// Increase the buffer size for more speed. - don't - we don't want a 20 meg buffer size,right? -ggolden
							//res.setBufferSize(len);
			
							OutputStream out = null;
							try
							{
								out = res.getOutputStream();
								out.write(content);
								out.flush();
								out.close();
							}
							catch (Throwable ignore)
							{
							}
							finally
							{
								if (out != null)
								{
									try
									{
										out.close();
									}
									catch (Throwable ignore)
									{
									}
								}
							}
						}
					}

					// track event
					EventTrackingService.post(EventTrackingService.newEvent(EVENT_RESOURCE_READ, resource.getReference(), false));
				}
				catch (Throwable t)
				{
					throw new IdUnusedException(ref.getReference());
				}
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	public Entity getEntity(Reference ref)
	{
		// double check that it's mine
		if (SERVICE_NAME != ref.getType()) return null;

		Entity rv = null;

		try
		{
			ResourceProperties props = getProperties(ref.getId());
			if (props.getBooleanProperty(ResourceProperties.PROP_IS_COLLECTION))
			{
				rv = getCollection(ref.getId());
			}
			else
			{
				rv = getResource(ref.getId());
			}
		}
		catch (PermissionException e)
		{
		}
		catch (IdUnusedException e)
		{
		}
		catch (TypeException e)
		{
		}
		catch (EmptyException e)
		{
		}
		catch (NullPointerException e)
		{
		}
		
		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getEntityUrl(Reference ref)
	{
		// double check that it's mine
		if (SERVICE_NAME != ref.getType()) return null;

		return getUrl(ref.getId());		
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection getEntityAuthzGroups(Reference ref)
	{
		// double check that it's mine
		if (SERVICE_NAME != ref.getType()) return null;

		// use the resources realm, all container (folder) realms

		Collection rv = new Vector();

		try
		{
			// try the resource, all the folders above it (don't include /)
			String paths[] = StringUtil.split(ref.getId(), Entity.SEPARATOR);
			boolean container = ref.getId().endsWith(Entity.SEPARATOR);
			if (paths.length > 1)
			{
				String root = getReference(Entity.SEPARATOR + paths[1] + Entity.SEPARATOR);
				rv.add(root);

				for (int next = 2; next < paths.length; next++)
				{
					root = root + paths[next];
					if ((next < paths.length - 1) || container)
					{
						root = root + Entity.SEPARATOR;
					}
					rv.add(root);
				}
			}

			// special check for group-user : the grant's in the user's My Workspace site
			String parts[] = StringUtil.split(ref.getId(), Entity.SEPARATOR);
			if ((parts.length > 3) && (parts[1].equals("group-user")))
			{
				rv.add(SiteService.siteReference(SiteService.getUserSiteId(parts[3])));
			}

			// site
			ref.addSiteContextAuthzGroup(rv);
		}
		catch (Throwable e)
		{
		}

		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public String archive(String siteId, Document doc, Stack stack, String archivePath, List attachments)
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
	public String archiveResources(List attachments, Document doc, Stack stack, String archivePath)
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
				ContentResource resource = (ContentResource) ref.getEntity();

				if (resource != null)
				{
					results.append(archiveResource(resource, doc, stack, archivePath, null));
				}
			}
			catch (Exception any)
			{
				results.append("Error archiving resource: " + ref + " " + any.toString() + "\n");
				m_logger.warn(this + ".archveResources: exception archiving resource: " + ref + ": ", any);
			}
		}

		stack.pop();

		return results.toString();
	}

	/**
	 * Replace the WT user id with the new qualified id
	 * 
	 * @param el
	 *        The XML element holding the perproties
	 * @param useIdTrans
	 *        The HashMap to track old WT id to new CTools id
	 */
	protected void WTUserIdTrans(Element el, Map userIdTrans)
	{
		NodeList children4 = el.getChildNodes();
		int length4 = children4.getLength();
		for (int i4 = 0; i4 < length4; i4++)
		{
			Node child4 = children4.item(i4);
			if (child4.getNodeType() == Node.ELEMENT_NODE)
			{
				Element element4 = (Element) child4;
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
						String newCreatorId = (String) userIdTrans.get(creatorId);
						if (newCreatorId != null)
						{
							Xml.encodeAttribute(element4, "CHEF:creator", newCreatorId);
							element4.setAttribute("enc", "BASE64");
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
						String newModifierId = (String) userIdTrans.get(modifierId);
						if (newModifierId != null)
						{
							Xml.encodeAttribute(element4, "CHEF:creator", newModifierId);
							element4.setAttribute("enc", "BASE64");
						}
					}
				}
			}
		}

	} // WTUserIdTrans

	/**
	 * Merge the resources from the archive into the given site.
	 * 
	 * @param siteId
	 *        The id of the site getting imported into.
	 * @param root
	 *        The XML DOM tree of content to merge.
	 * @param archviePath
	 *        The path to the folder where we are reading auxilary files.
	 * @return A log of status messages from the archive.
	 */
	public String merge(String siteId, Element root, String archivePath, String mergeId, Map attachmentNames, Map userIdTrans,
			Set userListAllowImport)
	{
		// get the system name: FROM_WT, FROM_CT, FROM_SAKAI
		String source = "";
		// root: <service> node
		Node parent = root.getParentNode(); // parent: <archive> node containing "system"
		if (parent.getNodeType() == Node.ELEMENT_NODE)
		{
			Element parentEl = (Element) parent;
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
				if (child.getNodeType() != Node.ELEMENT_NODE) continue;
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
					if (source.equalsIgnoreCase(ArchiveService.FROM_SAKAI) || source.equalsIgnoreCase(ArchiveService.FROM_CT))
					{
						NodeList children2 = element.getChildNodes();
						int length2 = children2.getLength();
						for (int i2 = 0; i2 < length2; i2++)
						{
							Node child2 = children2.item(i2);
							if (child2.getNodeType() == Node.ELEMENT_NODE)
							{
								Element element2 = (Element) child2;

								// get the "channel" child
								if (element2.getTagName().equals("properties"))
								{
									NodeList children3 = element2.getChildNodes();
									final int length3 = children3.getLength();
									for (int i3 = 0; i3 < length3; i3++)
									{
										Node child3 = children3.item(i3);
										if (child3.getNodeType() == Node.ELEMENT_NODE)
										{
											Element element3 = (Element) child3;

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
							id = ATTACHMENTS_COLLECTION + IdService.getUniqueId()
									+ id.substring(id.indexOf('/', ATTACHMENTS_COLLECTION.length()));

							// record the rename
							attachmentNames.put(oldRef, id);
						}

						// otherwise move it into the site
						else
						{
							if (relId == null)
							{
								m_logger.warn(this + ".mergeContent(): no rel-id attribute in resource");
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

							// resource: add if missing
							r = mergeResource(element, body.getBytes());
						}

						else
						{
							// resource: add if missing
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
			m_logger.warn(this + ".mergeContent(): exception: ", any);
		}

		return results.toString();

	} // merge

	/**
	 * import tool(s) contents from the source context into the destination context
	 * 
	 * @param fromContext
	 *        The source context
	 * @param toContext
	 *        The destination context
	 * @param resourceIds
	 *        when null, all resources will be imported; otherwise, only resources with those ids will be imported
	 */
	public void importEntities(String fromContext, String toContext, List resourceIds)
	{
		// default to import all resources
		boolean toBeImported = true;

		// get the list of all resources for importing
		try
		{
			// get the root collection
			ContentCollection oCollection = getCollection(fromContext);

			// Get the collection members from the 'new' collection
			List oResources = oCollection.getMemberResources();
			for (int i = 0; i < oResources.size(); i++)
			{
				// get the original resource
				Entity oResource = (Entity) oResources.get(i);
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
					catch (EmptyException e)
					{
					}
					catch (TypeException e)
					{
					}

					if (isCollection)
					{
						// add collection
						try
						{
							ContentCollectionEdit edit = addCollection(nId);
							// import properties
							ResourcePropertiesEdit p = edit.getPropertiesEdit();
							p.clear();
							p.addAll(oProperties);
							// complete the edit
							m_storage.commitCollection(edit);
							((BaseCollectionEdit) edit).closeEdit();
						}
						catch (IdUsedException e)
						{
						}
						catch (IdInvalidException e)
						{
						}
						catch (PermissionException e)
						{
						}
						catch (InconsistentException e)
						{
						}

						importEntities(oResource.getId(), nId, resourceIds);
					}
					else
					{
						try
						{
							// add resource
							ContentResourceEdit edit = addResource(nId);
							edit.setContentType(((ContentResource) oResource).getContentType());
							edit.setContent(((ContentResource) oResource).getContent());
							// import properties
							ResourcePropertiesEdit p = edit.getPropertiesEdit();
							p.clear();
							p.addAll(oProperties);
							// complete the edit
							m_storage.commitResource(edit);
							((BaseResourceEdit) edit).closeEdit();
						}
						catch (PermissionException e)
						{
						}
						catch (IdUsedException e)
						{
						}
						catch (IdInvalidException e)
						{
						}
						catch (InconsistentException e)
						{
						}
						catch(ServerOverloadException e)
						{
						}
					} // if
				} // if
			} // for
		}
		catch (IdUnusedException e)
		{
		}
		catch (TypeException e)
		{
		}
		catch (PermissionException e)
		{
		}

	} // importResources

	/**
	 * {@inheritDoc}
	 */
	public boolean parseEntityReference(String reference, Reference ref)
	{
		String id = null;
		String context = "";

		// for content hosting resources and collections
		if (reference.startsWith(REFERENCE_ROOT))
		{
			// parse out the local resource id
			id = reference.substring(REFERENCE_ROOT.length(), reference.length());
		}

		// for content hosting resources and collections - full url
		else if (reference.startsWith(getUrl("")))
		{
			// parse out the local resource id
			id = reference.substring(getUrl("").length(), reference.length());
		}
		
		// not mine
		else
		{
			return false;
		}

		// parse out the associated site id
		String parts[] = StringUtil.split(id, Entity.SEPARATOR);
		if (parts.length >= 3)
		{
			if (parts[1].equals("group"))
			{
				context = parts[2];
			}
			else if (parts[1].equals("user"))
			{
				context = SiteService.getUserSiteId(parts[2]);
			}
			else if (parts[1].equals("group-user"))
			{
				// use just the group context
				context = parts[2];
			}
		}

		ref.set(SERVICE_NAME, null, id, null, context);

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getEntityDescription(Reference ref)
	{
		// double check that it's mine
		if (SERVICE_NAME != ref.getType()) return null;

		String rv = "Content: " + ref.getId();

		try
		{
			ResourceProperties props = getProperties(ref.getId());
			if (props.getBooleanProperty(ResourceProperties.PROP_IS_COLLECTION))
			{
				ContentCollection c = getCollection(ref.getId());
				rv = "Collection: " + c.getProperties().getPropertyFormatted(ResourceProperties.PROP_DISPLAY_NAME) + " ("
						+ c.getId() + ")\n" + " Created: "
						+ c.getProperties().getPropertyFormatted(ResourceProperties.PROP_CREATION_DATE) + " by "
						+ c.getProperties().getPropertyFormatted(ResourceProperties.PROP_CREATOR) + "(User Id:"
						+ c.getProperties().getProperty(ResourceProperties.PROP_CREATOR) + ")\n"
						+ StringUtil.limit(c.getProperties().getPropertyFormatted(ResourceProperties.PROP_DESCRIPTION), 30);
			}
			else
			{
				ContentResource r = getResource(ref.getId());
				rv = "Resource: " + r.getProperties().getPropertyFormatted(ResourceProperties.PROP_DISPLAY_NAME) + " ("
						+ r.getId() + ")\n" + " Created: "
						+ r.getProperties().getPropertyFormatted(ResourceProperties.PROP_CREATION_DATE) + " by "
						+ r.getProperties().getPropertyFormatted(ResourceProperties.PROP_CREATOR) + "(User Id:"
						+ r.getProperties().getProperty(ResourceProperties.PROP_CREATOR) + ")\n"
						+ StringUtil.limit(r.getProperties().getPropertyFormatted(ResourceProperties.PROP_DESCRIPTION), 30);
			}
		}
		catch (PermissionException e)
		{
		}
		catch (IdUnusedException e)
		{
		}
		catch (TypeException e)
		{
		}
		catch (EmptyException e)
		{
		}
		catch (NullPointerException e)
		{
		}

		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public ResourceProperties getEntityResourceProperties(Reference ref)
	{
		// double check that it's mine
		if (SERVICE_NAME != ref.getType()) return null;

		ResourceProperties props = null;

		try
		{
			props = getProperties(ref.getId());
		}
		catch (PermissionException e)
		{
		}
		catch (IdUnusedException e)
		{
		}

		return props;
	}

	/**
	 * {@inheritDoc}
	 */
	public void syncWithSiteChange(Site site, EntityProducer.ChangeType change)
	{
		// handle both resources and dropbox
		syncWithSiteChangeResources(site, change);
		syncWithSiteChangeDropbox(site, change);
	}

	/**
	 * {@inheritDoc}
	 */
	public void syncWithSiteChangeResources(Site site, EntityProducer.ChangeType change)
	{
		// TODO: perhaps we should create resources for the site even without the tool? -ggolden

		String[] toolIds = {"sakai.resources"};

		// for a delete, just disable
		if (EntityProducer.ChangeType.REMOVE == change)
		{
			disableResources(site);
		}
		
		// otherwise enable if we now have the tool, disable otherwise
		else
		{
			// collect the tools from the site
			Collection tools = site.getTools(toolIds);

			// if we have the tool
			if (!tools.isEmpty())
			{
				enableResources(site);
			}
			
			// if we do not
			else
			{
				disableResources(site);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void syncWithSiteChangeDropbox(Site site, EntityProducer.ChangeType change)
	{
		// TODO: perhaps we should create resources for the site even without the tool? -ggolden

		String[] toolIds = {"sakai.dropbox"};

		// for a delete, just disable
		if (EntityProducer.ChangeType.REMOVE == change)
		{
			disableDropbox(site);
		}
		
		// otherwise enable if we now have the tool, disable otherwise
		else
		{
			// collect the tools from the site
			Collection tools = site.getTools(toolIds);

			// if we have the tool
			if (!tools.isEmpty())
			{
				enableDropbox(site);
			}
			
			// if we do not
			else
			{
				disableDropbox(site);
			}
		}
	}

	/**
	 * Make sure a home in resources exists for the site.
	 * 
	 * @param site
	 *        The site.
	 */
	protected void enableResources(Site site)
	{
		// it would be called
		String id = getSiteCollection(site.getId());

		// does it exist?
		try
		{
			ContentCollection collection = getCollection(id);

			// do we need to update the title?
			if (!site.getTitle().equals(collection.getProperties().getProperty(ResourceProperties.PROP_DISPLAY_NAME)))
			{
				try
				{
					ContentCollectionEdit edit = editCollection(id);
					edit.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, site.getTitle());
					commitCollection(edit);
				}
				catch (IdUnusedException e)
				{
					m_logger.warn(this + ".enableResources: " + e);
				}
				catch (PermissionException e)
				{
					m_logger.warn(this + ".enableResources: " + e);
				}
				catch (InUseException e)
				{
					m_logger.warn(this + ".enableResources: " + e);
				}
			}
		}
		catch (IdUnusedException un)
		{
			// make it
			try
			{
				ContentCollectionEdit collection = addCollection(id);
				collection.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME, site.getTitle());
				commitCollection(collection);
			}
			catch (IdUsedException e)
			{
				m_logger.warn(this + ".enableResources: " + e);
			}
			catch (IdInvalidException e)
			{
				m_logger.warn(this + ".enableResources: " + e);
			}
			catch (PermissionException e)
			{
				m_logger.warn(this + ".enableResources: " + e);
			}
			catch (InconsistentException e)
			{
				m_logger.warn(this + ".enableResources: " + e);
			}
		}
		catch (TypeException e)
		{
			m_logger.warn(this + ".enableResources: " + e);
		}
		catch (PermissionException e)
		{
			m_logger.warn(this + ".enableResources: " + e);
		}
	}

	/**
	 * Remove resources area for a site.
	 * @param site The site.
	 */
	protected void disableResources(Site site)
	{
		// TODO: we do nothing now - resources hang around after the tool is removed from the site or the site is deleted -ggolden
	}

	/**
	 * Make sure a home in resources for dropbox exists for the site.
	 * 
	 * @param site
	 *        The site.
	 */
	protected void enableDropbox(Site site)
	{
		// create it and the user folders within
		createDropboxCollection(site.getId());
	}

	/**
	 * Remove resources area for a site.
	 * @param site The site.
	 */
	protected void disableDropbox(Site site)
	{
		// TODO: we do nothing now - dropbox resources hang around after the tool is removed from the site or the site is deleted -ggolden
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * etc
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * Archive the collection, then the members of the collection - recursively for collection members.
	 * 
	 * @param collection
	 *        The collection whose members are to be archived.
	 * @param doc
	 *        The document to contain the xml.
	 * @param stack
	 *        The stack of elements, the top of which will be the containing element of the "collection" or "resource" element.
	 * @param storagePath
	 *        The path to the folder where we are writing files.
	 * @param siteCollectionId
	 *        The resource id of the site collection.
	 * @param results
	 *        A log of messages from the archive.
	 */
	protected void archiveCollection(ContentCollection collection, Document doc, Stack stack, String storagePath,
			String siteCollectionId, StringBuffer results)
	{
		// first the collection
		Element el = collection.toXml(doc, stack);

		// store the relative file id in the xml
		el.setAttribute("rel-id", collection.getId().substring(siteCollectionId.length()));

		results.append("archiving collection: " + collection.getId() + "\n");

		// now each member
		List members = collection.getMemberResources();
		if ((members == null) || (members.size() == 0)) return;
		for (int i = 0; i < members.size(); i++)
		{
			Object member = members.get(i);
			if (member instanceof ContentCollection)
			{
				archiveCollection((ContentCollection) member, doc, stack, storagePath, siteCollectionId, results);
			}
			else if (member instanceof ContentResource)
			{
				results.append(archiveResource((ContentResource) member, doc, stack, storagePath, siteCollectionId));
			}
		}

	} // archiveCollection

	/**
	 * Archive a singe resource
	 * 
	 * @param resource
	 *        The content resource to archive
	 * @param doc
	 *        The XML document.
	 * @param stack
	 *        The stack of elements.
	 * @param storagePath
	 *        The path to the folder where we are writing files.
	 * @param siteCollectionId
	 *        The resource id of the site collection (optional).
	 * @return A log of messages from the archive.
	 */
	protected String archiveResource(ContentResource resource, Document doc, Stack stack, String storagePath,
			String siteCollectionId)
	{
		byte[] content = null;
		try
		{
			// get the content bytes
			content = resource.getContent();
		}
		catch(ServerOverloadException e)
		{
			m_logger.warn(this + ".archiveResource(): while reading body for: " + resource.getId() + " : " + e);
			// return "failed to archive resource: " + resource.getId() + " body temporarily unavailable due to server error\n"
		}
		
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
			m_logger.warn(this + ".archiveResource(): while writing body for: " + resource.getId() + " : " + e);
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
	 * Merge in a collection from an XML DOM definition. Take whole if not defined already. Ignore if already here.
	 * 
	 * @param element
	 *        The XML DOM element containing the collection definition.
	 * @exception PermissionException
	 *            if the user does not have permission to add a collection.
	 * @exception InconsistentException
	 *            if the containing collection does not exist.
	 * @exception IdInvalidException
	 *            if the id is not valid.
	 * @return a new ContentCollection object, or null if it was not created.
	 */
	protected ContentCollection mergeCollection(Element element) throws PermissionException, InconsistentException,
			IdInvalidException
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
	 * Merge in a resource from an XML DOM definition. Ignore if already defined. Take whole if not.
	 * 
	 * @param element
	 *        The XML DOM element containing the collection definition.
	 * @exception PermissionException
	 *            if the user does not have permission to add a resource.
	 * @exception InconsistentException
	 *            if the containing collection does not exist.
	 * @exception IdInvalidException
	 *            if the id is not valid.
	 * @exception OverQuotaException
	 *            if this would result in being over quota.
	 * @exception ServerOverloadException
	 *            if the server is configured to write the resource body to the filesystem and the save fails.
	 * @return a new ContentResource object, or null if it was not created.
	 */
	protected ContentResource mergeResource(Element element) throws PermissionException, InconsistentException, IdInvalidException,
			OverQuotaException, ServerOverloadException
	{
		return mergeResource(element, null);

	} // mergeResource

	/**
	 * Merge in a resource from an XML DOM definition and a body bytes array. Ignore if already defined. Take whole if not.
	 * 
	 * @param element
	 *        The XML DOM element containing the collection definition.
	 * @param body
	 *        The body bytes.
	 * @exception PermissionException
	 *            if the user does not have permission to add a resource.
	 * @exception InconsistentException
	 *            if the containing collection does not exist.
	 * @exception IdInvalidException
	 *            if the id is not valid.
	 * @exception OverQuotaException
	 *            if this would result in being over quota.
	 * @return a new ContentResource object, or null if it was not created.
	 */
	protected ContentResource mergeResource(Element element, byte[] body) throws PermissionException, InconsistentException,
			IdInvalidException, OverQuotaException, ServerOverloadException
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
		EventTrackingService.post(EventTrackingService.newEvent(((BaseResourceEdit) edit).getEvent(), edit.getReference(), true,
				NotificationService.NOTI_NONE));

		// close the edit object
		((BaseResourceEdit) edit).closeEdit();

		return edit;

	} // mergeResource

	/**
	 * Find the containing collection id of a given resource id.
	 * 
	 * @param id
	 *        The resource id.
	 * @return the containing collection id.
	 */
	protected String isolateContainingId(String id)
	{
		// take up to including the last resource path separator, not counting one at the very end if there
		return id.substring(0, id.lastIndexOf('/', id.length() - 2) + 1);

	} // isolateContainingId

	/**
	 * Find the resource name of a given resource id.
	 * 
	 * @param id
	 *        The resource id.
	 * @return the resource name.
	 */
	protected String isolateName(String id)
	{
		if (id == null) return null;
		if (id.length() == 0) return null;

		// take after the last resource path separator, not counting one at the very end if there
		boolean lastIsSeparator = id.charAt(id.length() - 1) == '/';
		return id.substring(id.lastIndexOf('/', id.length() - 2) + 1, (lastIsSeparator ? id.length() - 1 : id.length()));

	} // isolateName

	/**
	 * Check the fixed type and id infomation: The same or better content type based on the known type for this id's extension, if any. The same or added extension id based on the know MIME type, if any Only if the type is the unknown type already.
	 * 
	 * @param id
	 *        The resource id with possible file extension to check.
	 * @param type
	 *        The content type.
	 * @return the best guess content type based on this resource's id and resource id with extension based on this resource's MIME type.
	 */
	protected Hashtable fixTypeAndId(String id, String type)
	{
		// the Hashtable holds the id and mime type
		Hashtable extType = new Hashtable();
		extType.put("id", id);
		if (type == null) type = "";
		extType.put("type", type);
		String extension = Validator.getFileExtension(id);

		if (extension.length() != 0)
		{
			// if there's a file extension and a blank, null or unknown(application/binary) mime type,
			// fix the mime type by doing a lookup based on the extension
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
				//htripath- SAK-1811 remove '.bin' extension from binary file without any extension e.g makeFile 
				//id = id + ".bin";
				extType.put("id", id);
			}
		}

		return extType;

	} // fixTypeAndId

	/**
	 * Test if this resource edit would place the account" over quota.
	 * 
	 * @param edit
	 *        The proposed resource edit.
	 * @return true if this change would palce the "account" over quota, false if not.
	 */
	protected boolean overQuota(ContentResourceEdit edit)
	{
		// Note: This implementation is hard coded to just check for a quota in the "/user/"
		// or "/group/" area. -ggolden

		// Note: this does NOT count attachments (/attachments/*) nor dropbox (/group-user/<site id>/<user id>/*) -ggolden

		// quick exits if we are not doing site quotas
		// if (m_siteQuota == 0)
		// return false;

		long quota = 0;

		// use this quota unless we have one more specific
		quota = m_siteQuota;

		// some quick exits, if we are not doing user quota, or if this is not a user or group resource
		// %%% These constants should be from somewhere else -ggolden
		if (!((edit.getId().startsWith("/user/")) || (edit.getId().startsWith("/group/")))) return false;

		// expect null, "user" | "group", user/groupid, rest...
		String[] parts = StringUtil.split(edit.getId(), Entity.SEPARATOR);
		if (parts.length <= 2) return false;

		// get this collection
		String id = Entity.SEPARATOR + parts[1] + Entity.SEPARATOR + parts[2] + Entity.SEPARATOR;
		ContentCollection collection = null;
		try
		{
			collection = findCollection(id);
		}
		catch (TypeException ignore)
		{
		}

		if (collection == null) return false;

		// see if this collection has a quota property
		try
		{
			long siteSpecific = collection.getProperties().getLongProperty(ResourceProperties.PROP_COLLECTION_BODY_QUOTA);
			if (siteSpecific == 0) return false;

			quota = siteSpecific;
		}
		catch (EmptyException ignore)
		{
			// don't log or anything, this just means that this site doesn't have this quota property.
		}
		catch (Exception ignore)
		{
			m_logger.warn(this + ".overQuota: reading quota property of : " + collection.getId() + " : " + ignore);
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
	 * 
	 * @param bytes
	 *        The size in bytes.
	 * @return The size in Kbytes, rounded up.
	 */
	protected long bytes2k(long bytes)
	{
		return ((bytes - 1) / 1024) + 1;

	} // bytes2k

	/**
	 * Attempt to create any collections needed so that the parameter collection exists.
	 * 
	 * @param target
	 *        The collection that we want to exist.
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

	/**
	 * {@inheritDoc}
	 */
	public boolean isPubView(String id)
	{
		boolean pubView = SecurityService.unlock(UserDirectoryService.getAnonymousUser(), EVENT_RESOURCE_READ, getReference(id));
		return pubView;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isInheritingPubView(String id)
	{
		// the root does not inherit... and makes a bad ref if we try to isolateContainingId()
		if (isRootCollection(id)) return false;

		// check for pubview on the container
		String containerId = isolateContainingId(id);
		boolean pubView = SecurityService.unlock(UserDirectoryService.getAnonymousUser(), EVENT_RESOURCE_READ, getReference(containerId));
		return pubView;
	}

	/**
	 * Set this resource or collection to the pubview setting.
	 * 
	 * @param id
	 *        The resource or collection id.
	 * @param pubview
	 *        The desired public view setting.
	 */
	public void setPubView(String id, boolean pubview)
	{
		// TODO: check efficiency here -ggolden

		String ref = getReference(id);

		// edit the realm
		AuthzGroup edit = null;

		try
		{
			edit = AuthzGroupService.getAuthzGroup(ref);
		}
		catch (IdUnusedException e)
		{
			// if no realm yet, and we need one, make one
			if (pubview)
			{
				try
				{
					edit = AuthzGroupService.addAuthzGroup(ref);
				}
				catch (IdInvalidException ee)
				{
				}
				catch (IdUsedException ee)
				{
				}
				catch (PermissionException ee)
				{
				}
			}
		}

		// if we have no realm and don't need one, we are done
		if ((edit == null) && (!pubview))
			return;

		// if we need a realm and didn't get an edit, exception
		if ((edit == null) && pubview)
			return;

		boolean changed = false;
		boolean delete = false;

		// align the realm with our positive setting
		if (pubview)
		{
			// make sure the anon role exists and has "content.read" - the only client of pubview
			Role role = edit.getRole(AuthzGroupService.ANON_ROLE);
			if (role == null)
			{
				try
				{
					role = edit.addRole(AuthzGroupService.ANON_ROLE);
				}
				catch (IdUsedException ignore) {}
			}

			if (!role.isAllowed(EVENT_RESOURCE_READ))
			{
				role.allowFunction(EVENT_RESOURCE_READ);
				changed = true;
			}
		}

		// align the realm with our negative setting
		else
		{
			// get the role
			Role role = edit.getRole(AuthzGroupService.ANON_ROLE);
			if (role != null)
			{
				if (role.isAllowed(EVENT_RESOURCE_READ))
				{
					changed = true;
					role.disallowFunction(EVENT_RESOURCE_READ);
				}

				if (role.allowsNoFunctions())
				{
					edit.removeRole(role.getId());
					changed = true;
				}
			}

			// if "empty", we can delete the realm
			if (edit.isEmpty())
				delete = true;
		}

		// if we want the realm deleted
		if (delete)
		{
			try
			{
				AuthzGroupService.removeAuthzGroup(edit);
			}
			catch (PermissionException e) {}
		}

		// if we made a change
		else if (changed)
		{
			try
			{
				AuthzGroupService.save(edit);
			}
			catch (IdUnusedException e)
			{
				// TODO: IdUnusedException
			}
			catch (PermissionException e)
			{
				// TODO: PermissionException
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
   public List findResources(String type, String primaryMimeType, String subMimeType) {
      List globalList = new ArrayList();

      Map othersites = CollectionUtil.getCollectionMap();
      Iterator siteIt = othersites.keySet().iterator();
      while(siteIt.hasNext())
      {
         String displayName = (String) siteIt.next();
         String collId = (String) othersites.get(displayName);
         List artifacts = getFlatResources(collId);
         globalList.addAll(filterArtifacts(artifacts, type, primaryMimeType, subMimeType));
      }

      User user = UserDirectoryService.getCurrentUser();
      String userId = user.getId();
      String wsId = SiteService.getUserSiteId(userId);
      String wsCollectionId = getSiteCollection(wsId);

      List wsResources = getFlatResources(wsCollectionId);
      globalList.addAll(filterArtifacts(wsResources, type, primaryMimeType, subMimeType));

      return globalList;
   }

   /**
    * Return a map of Worksite collections roots that the user has access to.
    * @return Map of worksite title (String) to worksite resource root id (String)
    */
   public Map getCollectionMap() 
   {
      return CollectionUtil.getCollectionMap();
   }

   /**
    * get all the resources under a given directory.
    * @param parentId
    * @return List of all the ContentResource objects under this directory.
    */
   protected List getFlatResources(String parentId) {
      return getAllResources(parentId);
   }
   
   /**
    * Eliminate from the collection any duplicates as well as any items that are contained within 
    * another item whose resource-id is in the collection.
	* @param resourceIds A collection of strings (possibly empty) identifying items and/or collections. 
	*/
   public void eliminateDuplicates(Collection resourceIds)
   {
	   Collection dups = new Vector();
	   
	   // eliminate exact duplicates
	   Set others = new TreeSet(resourceIds);
	   
	   // eliminate items contained in other items
	   Iterator itemIt = resourceIds.iterator();
	   while(itemIt.hasNext())
	   {
		   String item = (String) itemIt.next();
		   Iterator otherIt = others.iterator();
		   while(otherIt.hasNext())
		   {
			   String other = (String) otherIt.next();
			   if(other.startsWith(item))
			   {
				   if(item.equals(other))
				   {
					   continue;
				   }
				   
				   // item contains other
				   otherIt.remove();
			   }
		   }
	   }
	   
	   // if any items have been removed, update the original collection
	   if(resourceIds.size() > others.size())
	   {
		   resourceIds.clear();
		   resourceIds.addAll(others);
	   }
	   
   }		// eliminate duplicates


   protected List filterArtifacts(List artifacts, String type, String primaryMimeType, String subMimeType) {
      for (Iterator i = artifacts.iterator();i.hasNext();) {
         ContentResource resource = (ContentResource)i.next();
         String currentType = resource.getProperties().getProperty(ResourceProperties.PROP_STRUCTOBJ_TYPE);
         String mimeType = resource.getProperties().getProperty(ResourceProperties.PROP_CONTENT_TYPE);

         if (type != null && !type.equals(ResourceProperties.FILE_TYPE)) {
            // process StructuredObject type
            if (currentType == null) {
               i.remove();
            }
            else if (!currentType.equals(type)) {
               i.remove();
            }
         }
         else if (currentType != null && type.equals(ResourceProperties.FILE_TYPE)) {
            // this one is a structured object, get rid of it
            i.remove();
         }
         else {
            String[] parts = mimeType.split("/");
            String currentPrimaryType = parts[0];
            String currentSubtype = null;
            if (parts.length > 1) currentSubtype = parts[1];

            // check the mime type match
            if (primaryMimeType != null && !primaryMimeType.equals(currentPrimaryType)) {
               i.remove();
            }
            else if (subMimeType != null && !subMimeType.equals(currentSubtype)) {
               i.remove();
            }
         }
      }
      return artifacts;
   }

   /**********************************************************************************************************************************************************************************************************************************************************
    * Dropbox Stuff
	*********************************************************************************************************************************************************************************************************************************************************/

	private static ResourceBundle rb = ResourceBundle.getBundle("content");

	/** The content root collection for dropboxes. */
	protected static final String COLLECTION_DROPBOX = "/group-user/";

 	protected static final String PROP_MEMBER_DROPBOX_DESCRIPTION = rb.getString("use1");
	protected static final String PROP_SITE_DROPBOX_DESCRIPTION =  rb.getString("use2");

	protected static final String DROPBOX_ID = " Drop Box";

	/**
	 * @inheritDoc
	 */
	public String getDropboxCollection()
	{
		return getDropboxCollection(ToolManager.getCurrentPlacement().getContext());
	}

	/**
	 * @inheritDoc
	 */
	public String getDropboxCollection(String siteId)
	{
		String rv = null;

		// make sure we are in a worksite, not a workspace
		if (SiteService.isUserSite(siteId) || SiteService.isSpecialSite(siteId))
		{
			return rv;
		}

		// form the site's dropbox collection
		rv = COLLECTION_DROPBOX + siteId + "/";

		// for maintainers, use the site level
		if (isDropboxMaintainer(siteId))
		{
			// return the site's dropbox collection
			return rv;
		}

		// form the current user's dropbox collection within this site's
		rv += StringUtil.trimToZero(SessionManager.getCurrentSessionUserId()) + "/";
		return rv;
	}

	/**
	 * Access the default dropbox collection display name for the current request. If the current user has permission to modify the site's dropbox collection, this is returned. Otherwise, the current user's collection within the site's dropbox is
	 * returned.
	 * 
	 * @return The default dropbox collection display name for the current request.
	 */
	public String getDropboxDisplayName()
	{
		return getDropboxDisplayName(ToolManager.getCurrentPlacement().getContext());
	}

	/**
	 * Access the default dropbox collection display name for the site. If the current user has permission to modify the site's dropbox collection, this is returned. Otherwise, the current user's collection within the site's dropbox is returned.
	 * 
	 * @param siteId
	 *        the Site id.
	 * @return The default dropbox collection display name for the site.
	 */
	public String getDropboxDisplayName(String siteId)
	{
		// make sure we are in a worksite, not a workspace
		if (SiteService.isUserSite(siteId) || SiteService.isSpecialSite(siteId))
		{
			return null;
		}

		// form the site's dropbox collection
		String id = COLLECTION_DROPBOX + siteId + "/";

		// for maintainers, use the site level dropbox
		if (isDropboxMaintainer(siteId))
		{
			// return the site's dropbox collection
			return siteId + DROPBOX_ID;
		}

		// return the current user's sort name
		return UserDirectoryService.getCurrentUser().getSortName();
	}

	/**
	 * Create the site's dropbox collection and one for each qualified user that the current user can make.
	 */
	public void createDropboxCollection()
	{
		createDropboxCollection(ToolManager.getCurrentPlacement().getContext());
	}

	/**
	 * Create the site's dropbox collection and one for each qualified user that the current user can make.
	 * 
	 * @param siteId
	 *        the Site id.
	 */
	public void createDropboxCollection(String siteId)
	{
		// make sure we are in a worksite, not a workspace
		if (SiteService.isUserSite(siteId) || SiteService.isSpecialSite(siteId))
		{
			return;
		}

		// form the site's dropbox collection
		String dropbox = COLLECTION_DROPBOX + siteId + "/";

		// try to create if it doesn't exist
		try
		{
			checkCollection(dropbox);
		}
		catch (IdUnusedException unused)
		{
			try
			{
				ContentCollectionEdit edit = addCollection(dropbox);
				ResourcePropertiesEdit props = edit.getPropertiesEdit();
				props.addProperty(ResourceProperties.PROP_DISPLAY_NAME, siteId + DROPBOX_ID);
				props.addProperty(ResourceProperties.PROP_DESCRIPTION, PROP_SITE_DROPBOX_DESCRIPTION);
				commitCollection(edit);
			}
			catch (PermissionException permissionException)
			{
				return;
			}
			catch (IdUsedException e)
			{
				m_logger.warn("createDropboxCollection: IdUsedException: " + dropbox);
				return;
			}
			catch (IdInvalidException e)
			{
				m_logger.warn("createDropboxCollection(): IdInvalidException: " + dropbox);
				return;
			}
			catch (InconsistentException e)
			{
				m_logger.warn("createDropboxCollection(): InconsistentException: " + dropbox);
				return;
			}
		}
		catch (PermissionException e)
		{
			return;
		}
		catch (TypeException typeException)
		{
			m_logger.warn("createDropboxCollection(): typeException: " + dropbox);
			return;
		}

		// The EVENT_DROPBOX_OWN is granted within the site, so we can ask for all the users who have this ability
		// using just the dropbox collection
		List users = SecurityService.unlockUsers(EVENT_DROPBOX_OWN, getReference(dropbox));
		for (Iterator it = users.iterator(); it.hasNext();)
		{
			User user = (User) it.next();

			// the folder id for this user's dropbox in this group
			String userFolder = dropbox + user.getId() + "/";

			// see if it exists
			try
			{
				 checkCollection(userFolder);
			}
			catch (IdUnusedException unused)
			{
				// doesn't exist, try to create it
				try
				{
					ContentCollectionEdit edit = addCollection(userFolder);
					ResourcePropertiesEdit props = edit.getPropertiesEdit();
					props.addProperty(ResourceProperties.PROP_DISPLAY_NAME, user.getSortName());
					props.addProperty(ResourceProperties.PROP_DESCRIPTION, PROP_MEMBER_DROPBOX_DESCRIPTION);
					commitCollection(edit);
				}
				catch (PermissionException ignore)
				{
				}
				catch (IdUsedException e)
				{
					m_logger.warn("createDropboxCollectionn(): idUsedException: " + userFolder);
				}
				catch (IdInvalidException e)
				{
					m_logger.warn("createDropboxCollection(): IdInvalidException: " + userFolder);
				}
				catch (InconsistentException e)
				{
					m_logger.warn("createDropboxCollection(): InconsistentException: " + userFolder);
				}
			}
			catch (PermissionException ignore)
			{
			}
			catch (TypeException typeException)
			{
				m_logger.warn("createDropboxCollection(): TypeException: " + userFolder);
			}
		}
	}

	/**
	* Determine whether the default dropbox collection id for this user in this site 
	* is the site's entire dropbox collection or just the current user's collection 
	* within the site's dropbox.	 
	* @return True if user sees all dropboxes in the site, false otherwise.
	*/
	public boolean isDropboxMaintainer()
	{
		return isDropboxMaintainer(ToolManager.getCurrentPlacement().getContext());
	}
	
	/**
	* Determine whether the default dropbox collection id for this user in some site 
	* is the site's entire dropbox collection or just the current user's collection 
	* within the site's dropbox.	 
	* @return True if user sees all dropboxes in the site, false otherwise.
	*/
	public boolean isDropboxMaintainer(String siteId)
	{
		String dropboxId = null;

		// make sure we are in a worksite, not a workspace
		if (SiteService.isUserSite(siteId) || SiteService.isSpecialSite(siteId))
		{
			return false;
		}

		// if the user has dropbox maintain in the site, they are the dropbox maintainer
		// (dropbox maintain in their myWorkspace just gives them access to their own dropbox)
		return SecurityService.unlock(EVENT_DROPBOX_MAINTAIN, SiteService.siteReference(siteId));
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	* ContentCollection implementation
	*********************************************************************************************************************************************************************************************************************************************************/

	public class BaseCollectionEdit implements ContentCollectionEdit, SessionBindingListener
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
		 * 
		 * @param id
		 *        The unique channel id.
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
		 * 
		 * @param other
		 *        The other to copy.
		 */
		public BaseCollectionEdit(ContentCollection other)
		{
			set(other);

		} // BaseCollectionEdit

		/**
		 * Construct from info in XML in a DOM element.
		 * 
		 * @param el
		 *        The XML DOM element.
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
				if (child.getNodeType() != Node.ELEMENT_NODE) continue;
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
		 * 
		 * @param user
		 *        The other object to take values from.
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
		 * 
		 * @return The URL which can be used to access the resource.
		 */
		public String getUrl()
		{
			return getAccessPoint(false) + m_id;

		} // getUrl

		/**
		 * Access the internal reference which can be used to access the resource from within the system.
		 * 
		 * @return The the internal reference which can be used to access the resource from within the system.
		 */
		public String getReference()
		{
			return getAccessPoint(true) + m_id;

		} // getReference

		/**
		 * Access the id of the resource.
		 * 
		 * @return The id.
		 */
		public String getId()
		{
			return m_id;

		} // getId

		/**
		 * Access a List of the collection's internal members, each a resource id string.
		 * 
		 * @return a List of the collection's internal members, each a resource id string (may be empty).
		 */
		public List getMembers()
		{
			// get the objects
			List memberResources = getMemberResources();

			// form the list of just ids
			List mbrs = new Vector();
			for (int i = 0; i < memberResources.size(); i++)
			{
				Entity res = (Entity) memberResources.get(i);
				if (res != null)
				{
					mbrs.add(res.getId());
				}
			}

			if (mbrs.size() == 0) return mbrs;

			// sort? %%%
			// Collections.sort(mbrs);

			return mbrs;

		} // getMembers

		/**
		 * Access the size of all the resource body bytes within this collection in Kbytes.
		 * 
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
				if (obj == null) continue;

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

			// if (m_logger.isDebugEnabled())
			// m_logger.debug(this + ".getBodySizeK(): collection: " + getId() + " size: " + size);

			return size;

		} // getBodySizeK

		/**
		 * Access a List of the collections' internal members as full ContentResource or ContentCollection objects.
		 * 
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
					// Note: while we are getting from storage, storage might change. These can be processed
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
								Entity mbr = (Entity) mbrs.get(i);
								m_cache.put(mbr.getReference(), mbr);
							}

							m_cache.setComplete(getReference());

							// now we are complete, process any cached events
							m_cache.processEvents();
						}
					}
				}
			}

			if (mbrs.size() == 0) return mbrs;

			// sort %%%
			// Collections.sort(mbrs);

			return mbrs;

		} // getMemberResources

		/**
		 * Access the collection's properties.
		 * 
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
		 * Clear all the members of the collection, all the way down. Security has already been checked!
		 */
		protected void clear() throws IdUnusedException, PermissionException, InconsistentException, TypeException, InUseException, ServerOverloadException
		{
			// get this collection's members
			List mbrs = getMemberResources();
			for (int i = 0; i < mbrs.size(); i++)
			{
				Object mbr = mbrs.get(i);
				if (mbr == null) continue;

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
		 * 
		 * @param doc
		 *        The DOM doc to contain the XML (or null for a string return).
		 * @param stack
		 *        The DOM elements, the top of which is the containing element of the new "resource" element.
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
		 * 
		 * @return The event code for this edit.
		 */
		protected String getEvent()
		{
			return m_event;
		}

		/**
		 * Set the event code for this edit.
		 * 
		 * @param event
		 *        The event code for this edit.
		 */
		protected void setEvent(String event)
		{
			m_event = event;
		}

		/**
		 * Access the resource's properties for modification
		 * 
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
		 * 
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

		/******************************************************************************************************************************************************************************************************************************************************
		 * SessionBindingListener implementation
		 *****************************************************************************************************************************************************************************************************************************************************/

		public void valueBound(SessionBindingEvent event)
		{
		}

		public void valueUnbound(SessionBindingEvent event)
		{
			if (m_logger.isDebugEnabled()) m_logger.debug(this + ".valueUnbound()");

			// catch the case where an edit was made but never resolved
			if (m_active)
			{
				cancelCollection(this);
			}

		} // valueUnbound

	} // class BaseCollection

	/**********************************************************************************************************************************************************************************************************************************************************
	 * ContentResource implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	public class BaseResourceEdit implements ContentResourceEdit, SessionBindingListener
	{
		/** The resource id. */
		protected String m_id = null;

		/** The content type. */
		protected String m_contentType = null;

		/** The body. May be missing - not yet read (null) */
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
		 * 
		 * @param id
		 *        The local resource id.
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
		 * 
		 * @param other
		 *        The other to copy.
		 */
		public BaseResourceEdit(ContentResource other)
		{
			set(other);

		} // BaseResourceEdit

		/**
		 * Set the file path for this resource
		 * 
		 * @param time
		 *        The time on which to based the path.
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
		 * 
		 * @param user
		 *        The other object to take values from.
		 */
		protected void set(ContentResource other)
		{
			m_id = other.getId();
			m_contentType = other.getContentType();
			m_contentLength = other.getContentLength();

			// if there's a body in the other, reference it, else leave this one null
			// Note: this treats the body byte array as immutable, so to update it one
			// *must* call setContent() not just getContent and mess with the bytes. -ggolden
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
		 * 
		 * @param el
		 *        The XML DOM element.
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
				if (child.getNodeType() != Node.ELEMENT_NODE) continue;
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
		 * 
		 * @return The URL which can be used to access the resource.
		 */
		public String getUrl()
		{
			return getAccessPoint(false) + m_id;

		} // getUrl

		/**
		 * Access the internal reference which can be used to access the resource from within the system.
		 * 
		 * @return The the internal reference which can be used to access the resource from within the system.
		 */
		public String getReference()
		{
			return getAccessPoint(true) + m_id;

		} // getReference

		/**
		 * @inheritDoc
		 */
		protected boolean requiresCopyrightAgreement()
		{
			// check my properties
			return m_properties.getProperty(ResourceProperties.PROP_COPYRIGHT_ALERT) != null;
		}

		/**
		 * Access the id of the resource.
		 * 
		 * @return The id.
		 */
		public String getId()
		{
			return m_id;

		} // getId

		/**
		 * Access the content byte length.
		 * 
		 * @return The content byte length.
		 */
		public int getContentLength()
		{
			// if we have a body, use it's length
			if (m_body != null) return m_body.length;

			// otherwise, use the content length
			return m_contentLength;

		} // getContentLength

		/**
		 * Access the resource MIME type.
		 * 
		 * @return The resource MIME type.
		 */
		public String getContentType()
		{
			return ((m_contentType == null) ? "" : m_contentType);

		} // getContentType

		/**
		 * Access the content bytes of the resource.
		 * 
		 * @return An array containing the bytes of the resource's content.
		 * @exception ServerOverloadException
		 * 			 if server is configured to store resource body in filesystem and error occurs trying to read from filesystem.
		 */
		public byte[] getContent() throws ServerOverloadException
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
		 * Access the content as a stream. Please close the stream when done as it may be holding valuable system resources.
		 * 
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
		 * 
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
		 * 
		 * @param length
		 *        The content byte length.
		 */
		public void setContentLength(int length)
		{
			m_contentLength = length;

		} // setContentLength

		/**
		 * Set the resource MIME type.
		 * 
		 * @param type
		 *        The resource MIME type.
		 */
		public void setContentType(String type)
		{
			type = (String) ((Hashtable) fixTypeAndId(getId(), type)).get("type");
			m_contentType = type;

		} // setContentType

		/**
		 * Set the resource content.
		 * 
		 * @param content
		 *        An array containing the bytes of the resource's content.
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
					m_logger.warn(this + ".setContent(): null content", e);
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
		 * 
		 * @param doc
		 *        The DOM doc to contain the XML (or null for a string return).
		 * @param stack
		 *        The DOM elements, the top of which is the containing element of the new "resource" element.
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

			// body may not be loaded; if not use m_contentLength
			int contentLength = m_contentLength;
			if (m_body != null) contentLength = m_body.length;
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
		 * 
		 * @return The event code for this edit.
		 */
		protected String getEvent()
		{
			return m_event;
		}

		/**
		 * Set the event code for this edit.
		 * 
		 * @param event
		 *        The event code for this edit.
		 */
		protected void setEvent(String event)
		{
			m_event = event;
		}

		/**
		 * Access the resource's properties for modification
		 * 
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
		 * 
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

		/******************************************************************************************************************************************************************************************************************************************************
		 * SessionBindingListener implementation
		 *****************************************************************************************************************************************************************************************************************************************************/

		public void valueBound(SessionBindingEvent event)
		{
		}

		public void valueUnbound(SessionBindingEvent event)
		{
			if (m_logger.isDebugEnabled()) m_logger.debug(this + ".valueUnbound()");

			// catch the case where an edit was made but never resolved
			if (m_active)
			{
				cancelResource(this);
			}

		} // valueUnbound

	} // BaseResource

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Storage implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

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

      public List getFlatResources(String collectionId);

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
		public void commitResource(ContentResourceEdit edit) throws ServerOverloadException;

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
		 * @exception ServerOverloadException
		 * 			if server is configured to save resource body in filesystem and an error occurs while 
		 * 			trying to access the filesystem.
		 */
		public byte[] getResourceBody(ContentResource resource) throws ServerOverloadException;

		/**
		 * Stream the resource's body.
		 * @exception ServerOverloadException
		 * 			if server is configured to save resource body in filesystem and an error occurs while 
		 * 			trying to access the filesystem.
		 */
		public InputStream streamResourceBody(ContentResource resource) throws ServerOverloadException;

		// htripath-storing into shadow table before deleting the resource
		public void commitDeleteResource(ContentResourceEdit edit, String uuid);

		public ContentResourceEdit putDeleteResource(String resourceId, String uuid, String userId);
   } // Storage

	/**********************************************************************************************************************************************************************************************************************************************************
	 * CacheRefresher implementation (no container)
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * Get a new value for this key whose value has already expired in the cache.
	 * 
	 * @param key
	 *        The key whose value has expired and needs to be refreshed.
	 * @param oldValue
	 *        The old exipred value of the key.
	 * @param event
	 *        The event which triggered this refresh.
	 * @return a new value for use in the cache for this key; if null, the entry will be removed.
	 */
	public Object refresh(Object key, Object oldValue, Event event)
	{
		Object rv = null;

		// key is a reference
		Reference ref = m_entityManager.newReference((String) key);
		String id = ref.getId();

		if (m_logger.isDebugEnabled()) m_logger.debug(this + ".refresh(): key " + key + " id : " + ref.getId());

		// get from storage only (not cache!)
		boolean collectionHint = id.endsWith(Entity.SEPARATOR);
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

