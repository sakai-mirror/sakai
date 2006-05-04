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
package org.sakaiproject.component.legacy.preference;

// imports
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.sakaiproject.api.kernel.function.cover.FunctionManager;
import org.sakaiproject.api.kernel.session.SessionBindingEvent;
import org.sakaiproject.api.kernel.session.SessionBindingListener;
import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.framework.config.ServerConfigurationService;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.memory.MemoryService;
import org.sakaiproject.service.legacy.announcement.cover.AnnouncementService;
import org.sakaiproject.service.legacy.content.cover.ContentHostingService;
import org.sakaiproject.service.legacy.email.cover.MailArchiveService;
import org.sakaiproject.service.legacy.entity.Edit;
import org.sakaiproject.service.legacy.entity.Entity;
import org.sakaiproject.service.legacy.entity.EntityManager;
import org.sakaiproject.service.legacy.entity.EntityProducer;
import org.sakaiproject.service.legacy.entity.HttpAccess;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.entity.ResourceProperties;
import org.sakaiproject.service.legacy.entity.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.event.cover.EventTrackingService;
import org.sakaiproject.service.legacy.notification.cover.NotificationService;
import org.sakaiproject.service.legacy.preference.Preferences;
import org.sakaiproject.service.legacy.preference.PreferencesEdit;
import org.sakaiproject.service.legacy.preference.PreferencesService;
import org.sakaiproject.service.legacy.security.cover.SecurityService;
import org.sakaiproject.service.legacy.site.Site;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.sakaiproject.util.java.StringUtil;
import org.sakaiproject.util.resource.BaseResourceProperties;
import org.sakaiproject.util.resource.BaseResourcePropertiesEdit;
import org.sakaiproject.util.storage.StorageUser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
* <p>BasePreferencesService is a Sakai Preferences implementation.</p>
* 
* @author University of Michigan, Sakai Software Development Team
* @version $Revision$
* @see org.chefproject.core.User
*/
public abstract class BasePreferencesService implements PreferencesService, StorageUser
{
	/** Storage manager for this service. */
	protected Storage m_storage = null;

	/** The initial portion of a relative access point URL. */
	protected String m_relativeAccessPoint = null;

	/*******************************************************************************
	* Abstractions, etc.
	*******************************************************************************/

	/**
	* Construct storage for this service.
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
	* Access the internal reference which can be used to access the resource from within the system.
	* @param id The preferences id string.
	* @return The the internal reference which can be used to access the resource from within the system.
	*/
	public String preferencesReference(String id)
	{
		return getAccessPoint(true) + Entity.SEPARATOR + id;

	} // preferencesReference

	/**
	* Access the preferences id extracted from a preferences reference.
	* @param ref The preferences reference string.
	* @return The the preferences id extracted from a preferences reference.
	*/
	protected String preferencesId(String ref)
	{
		String start = getAccessPoint(true) + Entity.SEPARATOR;
		int i = ref.indexOf(start);
		if (i == -1)
			return ref;
		String id = ref.substring(i + start.length());
		return id;

	} // preferencesId

	/**
	* Check security permission.
	* @param lock The lock id string.
	* @param resource The resource reference string, or null if no resource is involved.
	* @return true if allowd, false if not
	*/
	protected boolean unlockCheck(String lock, String resource)
	{
		if (!SecurityService.unlock(lock, resource))
		{
			return false;
		}

		return true;

	} // unlockCheck

	/**
	* Check security permission.
	* @param lock The lock id string.
	* @param resource The resource reference string, or null if no resource is involved.
	* @exception PermissionException Thrown if the user does not have access
	*/
	protected void unlock(String lock, String resource) throws PermissionException
	{
		if (!unlockCheck(lock, resource))
		{
			throw new PermissionException(lock, resource);
		}

	} // unlock

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

			// construct storage and read
			m_storage = newStorage();
			m_storage.open();

			// register as an entity producer
			m_entityManager.registerEntityProducer(this);

			// register functions
			FunctionManager.registerFunction(SECURE_ADD_PREFS);
			FunctionManager.registerFunction(SECURE_EDIT_PREFS);
			FunctionManager.registerFunction(SECURE_REMOVE_PREFS);

			m_logger.info(this +".init()");
		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}
	}

	/**
	* Returns to uninitialized state.
	*/
	public void destroy()
	{
		m_storage.close();
		m_storage = null;

		m_logger.info(this +".destroy()");
	}

	/*******************************************************************************
	* PreferencesService implementation
	*******************************************************************************/

	/**
	* Access a set of preferences associated with this id.
	* @param id The preferences id.
	* @return The Preferences object.
	*/
	public Preferences getPreferences(String id)
	{
		Preferences prefs = findPreferences(id);

		// if not found at all
		if (prefs == null)
		{
			// throwaway empty preferences %%%
			prefs = new BasePreferences(id);
		}

		return prefs;

	} // getPreferences

	/**
	* Get a locked Preferences object for editing. Must commit(), cancel() or remove() when done.
	* @param id The preferences id.
	* @return A PreferencesEdit object for editing, possibly new.
	* @exception PermissionException if the current user does not have permission to edit these preferences.
	* @exception InUseException if the preferences object is locked by someone else.
	* @exception IdUnusedException if there is not preferences object with this id.
	*/
	public PreferencesEdit edit(String id) throws PermissionException, InUseException, IdUnusedException
	{
		// security
		unlock(SECURE_EDIT_PREFS, preferencesReference(id));

		// check for existance
		if (!m_storage.check(id))
		{
			throw new IdUnusedException(id);
		}

		// ignore the cache - get the user with a lock from the info store
		PreferencesEdit edit = m_storage.edit(id);
		if (edit == null)
			throw new InUseException(id);

		((BasePreferences) edit).setEvent(SECURE_EDIT_PREFS);

		return edit;

	} // edit

	/**
	* Commit the changes made to a PreferencesEdit object, and release the lock.
	* The PreferencesEdit is disabled, and not to be used after this call.
	* @param user The PreferencesEdit object to commit.
	*/
	public void commit(PreferencesEdit edit)
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
				m_logger.warn(this +".commit(): closed PreferencesEdit", e);
			}
			return;
		}

		// update the properties
		// addLiveUpdateProperties(user.getPropertiesEdit());

		// complete the edit
		m_storage.commit(edit);

		// track it
		EventTrackingService.post(
			EventTrackingService.newEvent(((BasePreferences) edit).getEvent(), edit.getReference(), true));

		// close the edit object
		 ((BasePreferences) edit).closeEdit();

	} // commit

	/**
	* Cancel the changes made to a PreferencesEdit object, and release the lock.
	* The PreferencesEdit is disabled, and not to be used after this call.
	* @param user The PreferencesEdit object to commit.
	*/
	public void cancel(PreferencesEdit edit)
	{
		// if this was an add, remove it
		if (((BasePreferences) edit).m_event.equals(SECURE_ADD_PREFS))
		{
			remove(edit);
		}
		else
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
					m_logger.warn(this +".cancel(): closed PreferencesEdit", e);
				}
				return;
			}
	
			// release the edit lock
			m_storage.cancel(edit);
	
			// close the edit object
			((BasePreferences) edit).closeEdit();
		}

	} // cancel

	/**
	* Remove this PreferencesEdit - it must be locked from edit().
	* The PreferencesEdit is disabled, and not to be used after this call.
	* @param user The PreferencesEdit object to remove.
	*/
	public void remove(PreferencesEdit edit)
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
				m_logger.warn(this +".remove(): closed PreferencesEdit", e);
			}
			return;
		}

		// complete the edit
		m_storage.remove(edit);

		// track it
		EventTrackingService.post(EventTrackingService.newEvent(SECURE_REMOVE_PREFS, edit.getReference(), true));

		// close the edit object
		 ((BasePreferences) edit).closeEdit();

	} // remove

	/**
	* Find the preferences object, in cache or storage.
	* @param id The preferences id.
	* @return The preferences object found in cache or storage, or null if not found.
	*/
	protected BasePreferences findPreferences(String id)
	{
		BasePreferences prefs = (BasePreferences) m_storage.get(id);
		return prefs;

	} // findPreferences

	/**
	* Check to see if the current user can add or modify permissions with this id.
	* @param id The preferences id.
	* @return true if the user is allowed to update or add these preferences, false if not.
	*/
	public boolean allowUpdate(String id)
	{
		return unlockCheck(SECURE_EDIT_PREFS, preferencesReference(id));

	} // allowUpdate

	/**
	* Add a new set of preferences with this id.  Must commit(), remove() or cancel() when done.
	* @param id The preferences id.
	* @return A PreferencesEdit object for editing, possibly new.
	* @exception PermissionException if the current user does not have permission add preferences for this id.
	* @exception IdUsedException if these preferences already exist.
	*/
	public PreferencesEdit add(String id) throws PermissionException, IdUsedException
	{
		// check security (throws if not permitted)
		unlock(SECURE_ADD_PREFS, preferencesReference(id));

		// reserve a user with this id from the info store - if it's in use, this will return null
		PreferencesEdit edit = m_storage.put(id);
		if (edit == null)
		{
			throw new IdUsedException(id);
		}

		((BasePreferences) edit).setEvent(SECURE_ADD_PREFS);

		return edit;

	} // add

	/**********************************************************************************************************************************************************************************************************************************************************
	 * EntityProducer implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
 	 * {@inheritDoc}
	 */
	public String getLabel()
	{
		return "preferences";
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean willArchiveMerge()
	{
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean willImport()
	{
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public HttpAccess getHttpAccess()
	{
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean parseEntityReference(String reference, Reference ref)
	{
		// for preferences access
		if (reference.startsWith(REFERENCE_ROOT))
		{
			String id = null;

			// we will get null, service, user/preferences Id
			String[] parts = StringUtil.split(reference, Entity.SEPARATOR);

			if (parts.length > 2)
			{
				id = parts[2];
			}

			ref.set(SERVICE_NAME, null, id, null, null);
			
			return true;
		}
		
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getEntityDescription(Reference ref)
	{
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public ResourceProperties getEntityResourceProperties(Reference ref)
	{
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Entity getEntity(Reference ref)
	{
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection getEntityAuthzGroups(Reference ref)
	{
		// double check that it's mine
		if (SERVICE_NAME != ref.getType()) return null;

		Collection rv = new Vector();

		// for preferences access: no additional role realms
		try
		{
			rv.add(UserDirectoryService.userReference(ref.getId()));

			ref.addUserTemplateAuthzGroup(rv, SessionManager.getCurrentSessionUserId());
		}
		catch (NullPointerException e)
		{
			m_logger.warn("getEntityRealms(): " + e);
		}

		return rv;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getEntityUrl(Reference ref)
	{
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public String archive(String siteId, Document doc, Stack stack, String archivePath, List attachments)
	{
		return "";
	}

	/**
	 * {@inheritDoc}
	 */
	public String merge(String siteId, Element root, String archivePath, String fromSiteId, Map attachmentNames,
			Map userIdTrans, Set userListAllowImport)
	{
		return "";
	}

	/**
	 * {@inheritDoc}
	 */
	public void importEntities(String fromContext, String toContext, List ids)
	{
	}

	/**
	 * {@inheritDoc}
	 */
	public void syncWithSiteChange(Site site, EntityProducer.ChangeType change)
	{
	}

	/*******************************************************************************
	* Preferences implementation
	*******************************************************************************/

	public class BasePreferences implements PreferencesEdit, SessionBindingListener
	{
		/** The user id. */
		protected String m_id = null;

		/** The properties. */
		protected ResourcePropertiesEdit m_properties = null;

		/** The sets of keyed ResourceProperties. */
		protected Map m_props = null;

		/**
		* Construct.
		* @param id The user id.
		*/
		public BasePreferences(String id)
		{
			m_id = id;

			// setup for properties
			ResourcePropertiesEdit props = new BaseResourcePropertiesEdit();
			m_properties = props;

			m_props = new Hashtable();

			// if the id is not null (a new user, rather than a reconstruction)
			// and not the anon (id == "") user,
			// add the automatic (live) properties
			// %%% if ((m_id != null) && (m_id.length() > 0)) addLiveProperties(props);

		} // BasePreferences

		/**
		* Construct from another Preferences object.
		* @param user The user object to use for values.
		*/
		public BasePreferences(Preferences prefs)
		{
			setAll(prefs);

		} // BasePreferences

		/**
		* Construct from information in XML.
		* @param el The XML DOM Element definining the user.
		*/
		public BasePreferences(Element el)
		{
			// setup for properties
			m_properties = new BaseResourcePropertiesEdit();

			m_props = new Hashtable();

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

				// look for a set of preferences
				else if (element.getTagName().equals("prefs"))
				{
					String key = element.getAttribute("key");

					// TODO: [DONE] convert old CHEF 1.2.10 service ids!
					if (key.startsWith(NotificationService.PREFS_TYPE))
					{
						if (key.endsWith("AnnouncementService"))
						{
							key = NotificationService.PREFS_TYPE + AnnouncementService.SERVICE_NAME;
						}
						else if (key.endsWith("ContentHostingService"))
						{
							key = NotificationService.PREFS_TYPE + ContentHostingService.SERVICE_NAME;
						}
						else if (key.endsWith("MailArchiveService"))
						{
							key = NotificationService.PREFS_TYPE + MailArchiveService.SERVICE_NAME;
						}
						else if (key.endsWith("SyllabusService"))
						{
							key = NotificationService.PREFS_TYPE + "org.sakaiproject.api.app.syllabus.SyllabusService";
						}
					}

					BaseResourcePropertiesEdit props = null;

					// the children (properties)
					NodeList kids = element.getChildNodes();
					final int len = kids.getLength();
					for (int i2 = 0; i2 < len; i2++)
					{
						Node kid = kids.item(i2);
						if (kid.getNodeType() != Node.ELEMENT_NODE)
							continue;
						Element k = (Element) kid;

						// look for properties
						if (k.getTagName().equals("properties"))
						{
							props = new BaseResourcePropertiesEdit(k);
						}
					}

					if (props != null)
					{
						m_props.put(key, props);
					}
				}
			}

		} // BasePreferences

		/**
		* Take all values from this object.
		* @param user The user object to take values from.
		*/
		protected void setAll(Preferences prefs)
		{
			m_id = prefs.getId();

			m_properties = new BaseResourcePropertiesEdit();
			m_properties.addAll(prefs.getProperties());

			// %%% is this deep enough? -ggolden
			m_props = new Hashtable();
			m_props.putAll(((BasePreferences) prefs).m_props);

		} // setAll

		/**
		* Serialize the resource into XML, adding an element to the doc under the top of the stack element.
		* @param doc The DOM doc to contain the XML (or null for a string return).
		* @param stack The DOM elements, the top of which is the containing element of the new "resource" element.
		* @return The newly added element.
		*/
		public Element toXml(Document doc, Stack stack)
		{
			Element prefs = doc.createElement("preferences");

			if (stack.isEmpty())
			{
				doc.appendChild(prefs);
			}
			else
			{
				((Element) stack.peek()).appendChild(prefs);
			}

			stack.push(prefs);

			prefs.setAttribute("id", getId());

			// properties
			m_properties.toXml(doc, stack);

			// for each keyed property
			for (Iterator it = m_props.entrySet().iterator(); it.hasNext();)
			{
				Map.Entry entry = (Map.Entry) it.next();

				Element props = doc.createElement("prefs");
				prefs.appendChild(props);
				
				String key = (String) entry.getKey();
				
				// TODO: [DONE] adjust for CHEF 1.2.10 compatibility
				if (key.startsWith(NotificationService.PREFS_TYPE))
				{
					if (key.endsWith("AnnouncementService"))
					{
						key = NotificationService.PREFS_TYPE + "org.chefproject.service.generic.GenericAnnouncementService";
					}
					else if (key.endsWith("ContentHostingService"))
					{
						key = NotificationService.PREFS_TYPE + "org.chefproject.service.generic.GenericContentHostingService";
					}
					else if (key.endsWith("MailArchiveService"))
					{
						key = NotificationService.PREFS_TYPE + "org.chefproject.service.generic.GenericMailArchiveService";
					}
					else if (key.endsWith("SyllabusService"))
					{
						key = NotificationService.PREFS_TYPE + "org.sakaiproject.api.app.syllabus.SyllabusService";
					}
				}

				props.setAttribute("key", key);
				stack.push(props);
				((ResourceProperties) entry.getValue()).toXml(doc, stack);
				stack.pop();
			}
			stack.pop();

			return prefs;

		} // toXml

		/**
		* Access the user id.
		* @return The preferences id.
		*/
		public String getId()
		{
			if (m_id == null)
				return "";
			return m_id;

		} // getId

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
			return preferencesReference(m_id);

		} // getReference

		/**
		* Access the resources's properties.
		* @return The resources's properties.
		*/
		public ResourceProperties getProperties()
		{
			return m_properties;

		} // getProperties

		/**
		* Access the properties keyed by the specified value.
		* @param key The key to the properties.
		* @return The properties keyed by the specified value (possibly empty)
		*/
		public ResourceProperties getProperties(String key)
		{
			ResourceProperties rv = (ResourceProperties) m_props.get(key);
			if (rv == null)
			{
				// new, throwaway empty one
				rv = new BaseResourceProperties();
			}

			return rv;

		} // getProperties

		/**
		 * @inheritDoc
		 */
		public Collection getKeys()
		{
			return m_props.keySet();
		}

		/**
		* Are these objects equal?  If they are both User objects, and they have
		* matching id's, they are.
		* @return true if they are equal, false if not.
		*/
		public boolean equals(Object obj)
		{
			if (!(obj instanceof Preferences))
				return false;
			return ((Preferences) obj).getId().equals(getId());

		} // equals

		/**
		* Make a hash code that reflects the equals() logic as well.
		* We want two objects, even if different instances, if they have the same id to hash the same.
		*/
		public int hashCode()
		{
			return getId().hashCode();

		} // hashCode

		/**
		* Compare this object with the specified object for order.
		* @return A negative integer, zero, or a positive integer as this object is
		* less than, equal to, or greater than the specified object.
		*/
		public int compareTo(Object obj)
		{
			if (!(obj instanceof Preferences))
				throw new ClassCastException();

			// if the object are the same, say so
			if (obj == this)
				return 0;

			// sort based on (unique) id
			int compare = getId().compareTo(((Preferences) obj).getId());

			return compare;

		} // compareTo

		/*******************************************************************************
		* Edit implementation
		*******************************************************************************/

		/** The event code for this edit. */
		protected String m_event = null;

		/** Active flag. */
		protected boolean m_active = false;

		/**
		* Access the properties keyed by the specified value.
		* If the key does not yet exist, create it.
		* @param key The key to the properties.
		* @return The properties keyed by the specified value (possibly empty)
		*/
		public ResourcePropertiesEdit getPropertiesEdit(String key)
		{
			synchronized (m_props)
			{
				ResourcePropertiesEdit rv = (ResourcePropertiesEdit) m_props.get(key);
				if (rv == null)
				{
					// new one saved in the map
					rv = new BaseResourcePropertiesEdit();
					m_props.put(key, rv);
				}

				return rv;
			}

		} // getPropertiesEdit

		/**
		* Clean up.
		*/
		protected void finalize()
		{
			// catch the case where an edit was made but never resolved
			if (m_active)
			{
				cancel(this);
			}

		} // finalize

		/**
		* Take all values from this object.
		* @param user The user object to take values from.
		*/
		protected void set(Preferences prefs)
		{
			setAll(prefs);

		} // set

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
		* SessionBindingListener implementation
		*******************************************************************************/

		public void valueBound(SessionBindingEvent event)
		{
		}

		public void valueUnbound(SessionBindingEvent event)
		{
			if (m_logger.isDebugEnabled())
				m_logger.debug(this +".valueUnbound()");

			// catch the case where an edit was made but never resolved
			if (m_active)
			{
				cancel(this);
			}

		} // valueUnbound

	} // BasePreferences

	/*******************************************************************************
	* Storage
	*******************************************************************************/

	protected interface Storage
	{
		/**
		* Open.
		*/
		public void open();

		/**
		* Close.
		*/
		public void close();

		/**
		* Check if a preferences by this id exists.
		* @param id The user id.
		* @return true if a preferences for this id exists, false if not.
		*/
		public boolean check(String id);

		/**
		* Get the preferences with this id, or null if not found.
		* @param id The preferences id.
		* @return The preferences with this id, or null if not found.
		*/
		public Preferences get(String id);

		/**
		* Add a new preferences with this id.
		* @param id The preferences id.
		* @return The locked Preferences object with this id, or null if the id is in use.
		*/
		public PreferencesEdit put(String id);

		/**
		* Get a lock on the preferences with this id, or null if a lock cannot be gotten.
		* @param id The preferences id.
		* @return The locked Preferences with this id, or null if this records cannot be locked.
		*/
		public PreferencesEdit edit(String id);

		/**
		* Commit the changes and release the lock.
		* @param user The edit to commit.
		*/
		public void commit(PreferencesEdit edit);

		/**
		* Cancel the changes and release the lock.
		* @param user The edit to commit.
		*/
		public void cancel(PreferencesEdit edit);

		/**
		* Remove this edit and release the lock.
		* @param user The edit to remove.
		*/
		public void remove(PreferencesEdit edit);

	} // Storage

	/*******************************************************************************
	* StorageUser implementation (no container)
	*******************************************************************************/

	/**
	* Construct a new continer given just an id.
	* @param id The id for the new object.
	* @return The new containe Resource.
	*/
	public Entity newContainer(String ref)
	{
		return null;
	}

	/**
	* Construct a new container resource, from an XML element.
	* @param element The XML.
	* @return The new container resource.
	*/
	public Entity newContainer(Element element)
	{
		return null;
	}

	/**
	* Construct a new container resource, as a copy of another
	* @param other The other contianer to copy.
	* @return The new container resource.
	*/
	public Entity newContainer(Entity other)
	{
		return null;
	}

	/**
	* Construct a new resource given just an id.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param id The id for the new object.
	* @param others (options) array of objects to load into the Resource's fields.
	* @return The new resource.
	*/
	public Entity newResource(Entity container, String id, Object[] others)
	{
		return new BasePreferences(id);
	}

	/**
	* Construct a new resource, from an XML element.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param element The XML.
	* @return The new resource from the XML.
	*/
	public Entity newResource(Entity container, Element element)
	{
		return new BasePreferences(element);
	}

	/**
	* Construct a new resource from another resource of the same type.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param other The other resource.
	* @return The new resource as a copy of the other.
	*/
	public Entity newResource(Entity container, Entity other)
	{
		return new BasePreferences((Preferences) other);
	}

	/**
	* Construct a new continer given just an id.
	* @param id The id for the new object.
	* @return The new containe Resource.
	*/
	public Edit newContainerEdit(String ref)
	{
		return null;
	}

	/**
	* Construct a new container resource, from an XML element.
	* @param element The XML.
	* @return The new container resource.
	*/
	public Edit newContainerEdit(Element element)
	{
		return null;
	}

	/**
	* Construct a new container resource, as a copy of another
	* @param other The other contianer to copy.
	* @return The new container resource.
	*/
	public Edit newContainerEdit(Entity other)
	{
		return null;
	}

	/**
	* Construct a new resource given just an id.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param id The id for the new object.
	* @param others (options) array of objects to load into the Resource's fields.
	* @return The new resource.
	*/
	public Edit newResourceEdit(Entity container, String id, Object[] others)
	{
		BasePreferences e = new BasePreferences(id);
		e.activate();
		return e;
	}

	/**
	* Construct a new resource, from an XML element.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param element The XML.
	* @return The new resource from the XML.
	*/
	public Edit newResourceEdit(Entity container, Element element)
	{
		BasePreferences e = new BasePreferences(element);
		e.activate();
		return e;
	}

	/**
	* Construct a new resource from another resource of the same type.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param other The other resource.
	* @return The new resource as a copy of the other.
	*/
	public Edit newResourceEdit(Entity container, Entity other)
	{
		BasePreferences e = new BasePreferences((Preferences) other);
		e.activate();
		return e;
	}

	/**
	* Collect the fields that need to be stored outside the XML (for the resource).
	* @return An array of field values to store in the record outside the XML (for the resource).
	*/
	public Object[] storageFields(Entity r)
	{
		return null;
	}

	/**
	 * Check if this resource is in draft mode.
	 * @param r The resource.
	 * @return true if the resource is in draft mode, false if not.
	 */
	public boolean isDraft(Entity r)
	{
		return false;
	}

	/**
	 * Access the resource owner user id.
	 * @param r The resource.
	 * @return The resource owner user id.
	 */
	public String getOwnerId(Entity r)
	{
		return null;
	}

	/**
	 * Access the resource date.
	 * @param r The resource.
	 * @return The resource date.
	 */
	public Time getDate(Entity r)
	{
		return null;
	}

} // BasePreferencesService


