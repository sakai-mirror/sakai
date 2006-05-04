/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/legacy/alias/BaseAliasService.java,v 1.3 2005/05/12 01:38:24 ggolden.umich.edu Exp $
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
package org.sakaiproject.component.legacy.alias;

// imports
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.sakaiproject.api.kernel.session.SessionBindingEvent;
import org.sakaiproject.api.kernel.session.SessionBindingListener;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.framework.config.cover.ServerConfigurationService;
import org.sakaiproject.service.framework.log.Logger;
import org.sakaiproject.service.framework.memory.MemoryService;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.legacy.alias.Alias;
import org.sakaiproject.service.legacy.alias.AliasEdit;
import org.sakaiproject.service.legacy.alias.AliasService;
import org.sakaiproject.service.legacy.email.cover.MailArchiveService;
import org.sakaiproject.service.legacy.event.cover.EventTrackingService;
import org.sakaiproject.service.legacy.resource.Edit;
import org.sakaiproject.service.legacy.resource.Reference;
import org.sakaiproject.service.legacy.resource.Resource;
import org.sakaiproject.service.legacy.resource.ResourceProperties;
import org.sakaiproject.service.legacy.resource.ResourcePropertiesEdit;
import org.sakaiproject.service.legacy.security.cover.SecurityService;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.time.cover.TimeService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.sakaiproject.util.java.StringUtil;
import org.sakaiproject.util.Validator;
import org.sakaiproject.util.resource.BaseResourceProperties;
import org.sakaiproject.util.resource.BaseResourcePropertiesEdit;
import org.sakaiproject.util.storage.StorageUser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
* <p>BaseAliasService is a CHEF Alias services implemented as a Turbine Service.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public abstract class BaseAliasService
	implements AliasService, StorageUser
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
		return (relative ? "" : ServerConfigurationService.getAccessUrl()) + m_relativeAccessPoint;

	}   // getAccessPoint

	/**
	* Access the internal reference which can be used to access the resource from within the system.
	* @param id The alias id string.
	* @return The the internal reference which can be used to access the resource from within the system.
	*/
	public String aliasReference(String id)
	{
		return getAccessPoint(true) + Resource.SEPARATOR + id;

	}   // aliasReference

	/**
	* Access the alias id extracted from a alias reference.
	* @param ref The alias reference string.
	* @return The the alias id extracted from a alias reference.
	*/
	protected String aliasId(String ref)
	{
		String start = getAccessPoint(true) + Resource.SEPARATOR;
		int i = ref.indexOf(start);
		if (i == -1) return ref;
		String id = ref.substring(i+start.length());
		return id;

	}   // aliasId

	/**
	* Check security permission.
	* @param lock The lock id string.
	* @param resource The resource reference string, or null if no resource is involved.
	* @return true if allowed, false if not
	*/
	protected boolean unlockCheck(String lock, String resource)
	{
		if (!SecurityService.unlock(lock, resource))
		{
			return false;
		}

		return true;

	}	// unlockCheck

	/**
	* Check security permission.
	* @param lock The lock id string.
	* @param resource The resource reference string, or null if no resource is involved.
	* @exception PermissionException Thrown if the user does not have access
	*/
	protected void unlock(String lock, String resource)
		throws PermissionException
	{
		if (!unlockCheck(lock, resource))
		{
			throw new PermissionException(UsageSessionService.getSessionUserId(), lock, resource);
		}

	}	// unlock

	/**
	* Check security permission, target modify based.
	* @param target The target resource reference string.
	* @return true if allowed, false if not
	*/
	protected boolean unlockTargetCheck(String target)
	{
		// check the target for modify access.
		// %%% Note: this is setup only for sites and mail archive channels,
		// we need a Reference based generic "allowModify()" -ggolden.
		Reference ref = new Reference(target);
		if (ref.getType().equals(SiteService.SERVICE_NAME))
		{
			 return SiteService.allowUpdateSite(ref.getId());
		}
		
		else if (ref.getType().equals(MailArchiveService.SERVICE_NAME))
		{
			return MailArchiveService.allowEditChannel(target);
		}

		return false;

	}	// unlockTargetCheck

	/**
	* Create the live properties for the user.
	*/
	protected void addLiveProperties(ResourcePropertiesEdit props)
	{
		String current = UsageSessionService.getSessionUserId();

		props.addProperty(ResourceProperties.PROP_CREATOR, current);
		props.addProperty(ResourceProperties.PROP_MODIFIED_BY, current);
		
		String now = TimeService.newTime().toString();
		props.addProperty(ResourceProperties.PROP_CREATION_DATE, now);
		props.addProperty(ResourceProperties.PROP_MODIFIED_DATE, now);

	}   //  addLiveProperties

	/**
	* Update the live properties for a user for when modified.
	*/
	protected void addLiveUpdateProperties(ResourcePropertiesEdit props)
	{
		String current = UsageSessionService.getSessionUserId();

		props.addProperty(ResourceProperties.PROP_MODIFIED_BY, current);
		props.addProperty(ResourceProperties.PROP_MODIFIED_DATE, TimeService.newTime().toString());

	}   //  addLiveUpdateProperties

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

			m_logger.info(this +".init()");
		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}

	} // init

	/**
	* Returns to uninitialized state.
	*/
	public void destroy()
	{
		m_storage.close();
		m_storage = null;

		m_logger.info(this + ".destroy()");
	}

	/*******************************************************************************
	* AliasService implementation
	*******************************************************************************/

	/**
	* Check if the current user has permission to set this alias.
	* @param alias The alias.
	* @param target The resource reference string alias target.
	* @return true if the current user has permission to set this alias, false if not.
	*/
	public boolean allowSetAlias(String alias, String target)
	{
		return unlockTargetCheck(target);

	}	// allowSetAlias

	/**
	* Allocate an alias for a resource
	* @param alias The alias.
	* @param target The resource reference string alias target.
	* @throws IdUsedException if the alias is already used.
	* @throws IdInvalidException if the alias id is invalid.
	* @throws PermissionException if the current user does not have permission to set this alias.
	*/
	public void setAlias(String alias, String target)
		throws IdUsedException, IdInvalidException, PermissionException
	{
		// check for a valid alias name
		Validator.checkResourceId(alias);

		if (!unlockTargetCheck(target))
		{
			throw new PermissionException(UsageSessionService.getSessionUserId(), SECURE_ADD_ALIAS, target);
		}

		// attempt to register this alias with storage - if it's in use, this will return null
		AliasEdit a = m_storage.put(alias);
		if (a == null)
		{
			throw new IdUsedException(alias);
		}
		a.setTarget(target);

		// update the properties
		addLiveUpdateProperties(a.getPropertiesEdit());

		// complete the edit
		m_storage.commit(a);

		// track it
		EventTrackingService.post(
			EventTrackingService.newEvent(SECURE_ADD_ALIAS, aliasReference(alias), true));

	}	// setAlias

	/**
	* Check to see if the current user can remove this alias.
	* @param alias The alias.
	* @return true if the current user can remove this alias, false if not.
	*/
	public boolean allowRemoveAlias(String alias)
	{
		return unlockCheck(SECURE_REMOVE_ALIAS, aliasReference(alias));

	}	// allowRemoveAlias

	/**
	* Remove an alias.
	* @param alias The alias.
	* @exception IdUnusedException if not found.
	* @exception PermissionException if the current user does not have permission to remove this alias.
	* @exception InUseException if the Alias object is locked by someone else.
	*/
	public void removeAlias(String alias)
		throws IdUnusedException, PermissionException, InUseException
	{
		AliasEdit a = edit(alias);
		remove(a);

	}	// removeAlias

	/**
	* Check to see if the current user can remove these aliasese for this target resource reference.
	* @param target The target resource reference string.
	* @return true if the current user can remove these aliasese for this target resource reference, false if not.
	*/
	public boolean allowRemoveTargetAliases(String target)
	{
		return unlockTargetCheck(target);

	}	// allowRemoveTargetAliases

	/**
	* Remove all aliases for this target resource reference, if any.
	* @param target The target resource reference string.
	* @throws PermissionException if the current user does not have permission to remove these aliases.
	*/
	public void removeTargetAliases(String target)
		throws PermissionException
	{
		if (!unlockTargetCheck(target))
		{
			throw new PermissionException(UsageSessionService.getSessionUserId(), SECURE_REMOVE_ALIAS, target);
		}

		List all = getAliases(target);
		for (Iterator iAll = all.iterator(); iAll.hasNext();)
		{
			Alias alias = (Alias) iAll.next();
			try
			{
				AliasEdit a = m_storage.edit(alias.getId());
				if (a != null)
				{
					// complete the edit
					m_storage.remove(a);
			
					// track it
					EventTrackingService.post(
						EventTrackingService.newEvent(SECURE_REMOVE_ALIAS, a.getReference(), true));
				}
			}
			catch (Exception ignore) {}
		}

	}	// removeTargetAliases

	/**
	* Find the target resource reference string associated with this alias.
	* @param alias The alias.
	* @return The target resource reference string associated with this alias.
	* @throws IdUnusedException if the alias is not defined.
	*/
	public String getTarget(String alias)
		throws IdUnusedException
	{
		BaseAliasEdit a = (BaseAliasEdit) m_storage.get(alias);
		if (a == null) throw new IdUnusedException(alias);

		return a.getTarget();

	}	// getTarget

	/**
	* Find all the aliases defined for this target.
	* @param target The target resource reference string.
	* @return A list (Alias) of all the aliases defined for this target.
	*/
	public List getAliases(String target)
	{
		List allForTarget = m_storage.getAll(target);

		return allForTarget;

	}	// getAliases

	/**
	* Find all the aliases defined for this target, within the record range given (sorted by id).
	* @param target The target resource reference string.
	* @param first The first record position to return.
	* @param last The last record position to return.
	* @return A list (Alias) of all the aliases defined for this target, within the record range given (sorted by id).
	*/
	public List getAliases(String target, int first, int last)
	{
		List allForTarget = m_storage.getAll(target, first, last);

		return allForTarget;

	}	// getAliases

	/**
	* Find all the aliases within the record range given (sorted by id).
	* @param first The first record position to return.
	* @param last The last record position to return.
	* @return A list (Alias) of all the aliases within the record range given (sorted by id).
	*/
	public List getAliases(int first, int last)
	{
		List all = m_storage.getAll(first, last);

		return all;

	}	// getAliases

	/**
	 * {@inheritDoc}
	 */
	public int countAliases()
	{
		return m_storage.count();
	}

	/**
	 * {@inheritDoc}
	 */
	public List searchAliases(String criteria, int first, int last)
	{
		return m_storage.search(criteria, first, last);
	}

	/**
	 * {@inheritDoc}
	 */
	public int countSearchAliases(String criteria)
	{
		return m_storage.countSearch(criteria);
	}

	/**
	* Check to see if the current user can add an alias.
	* @return true if the current user can add an alias, false if not.
	*/
	public boolean allowAdd()
	{
		return unlockCheck(SECURE_ADD_ALIAS, aliasReference(""));

	}	// allowAdd

	/**
	* Add a new alias.  Must commit() to make official, or cancel() when done!
	* @param id The alias id.
	* @return A locked AliasEdit object (reserving the id).
	* @exception IdInvalidException if the alias id is invalid.
	* @exception IdUsedException if the alias id is already used.
	* @exception PermissionException if the current user does not have permission to add an alias.
	*/
	public AliasEdit add(String id)
		throws IdInvalidException, IdUsedException, PermissionException
	{
		// check for a valid user name
		Validator.checkResourceId(id);

		// check security (throws if not permitted)
		unlock(SECURE_ADD_ALIAS, aliasReference(id));

		// reserve an alias with this id from the info store - if it's in use, this will return null
		AliasEdit a = m_storage.put(id);
		if (a == null)
		{
			throw new IdUsedException(id);
		}

		((BaseAliasEdit) a).setEvent(SECURE_ADD_ALIAS);

		return a;

	}	// add

	/**
	* Check to see if the current user can edit this alias.
	* @param id The alias id string.
	* @return true if the current user can edit this alias, false if not.
	*/
	public boolean allowEdit(String id)
	{
		return unlockCheck(SECURE_UPDATE_ALIAS, aliasReference(id));

	}	// allowEdit

	/**
	* Get a locked alias object for editing. Must commit() to make official, or cancel() (or remove()) when done!
	* @param id The alias id string.
	* @return An AliasEdit object for editing.
	* @exception IdUnusedException if not found.
	* @exception PermissionException if the current user does not have permission to mess with this alias.
	* @exception InUseException if the Alias object is locked by someone else.
	*/
	public AliasEdit edit(String id)
		throws IdUnusedException, PermissionException, InUseException
	{
		if (id == null) throw new IdUnusedException("null");

		// check security (throws if not permitted)
		unlock(SECURE_UPDATE_ALIAS, aliasReference(id));

		// check for existance
		if (!m_storage.check(id))
		{
			throw new IdUnusedException(id);
		}

		// ignore the cache - get the user with a lock from the info store
		AliasEdit a = m_storage.edit(id);
		if (a == null) throw new InUseException(id);

		((BaseAliasEdit) a).setEvent(SECURE_UPDATE_ALIAS);

		return a;

	}	// edit

	/**
	* Commit the changes made to a AliasEdit object, and release the lock.
	* The AliasEdit is disabled, and not to be used after this call.
	* @param user The AliasEdit object to commit.
	*/
	public void commit(AliasEdit edit)
	{
		// check for closed edit
		if (!edit.isActiveEdit())
		{
			try { throw new Exception(); }
			catch (Exception e) { m_logger.warn(this + ".commit(): closed AliasEdit", e); }
			return;
		}

		// update the properties
		addLiveUpdateProperties(edit.getPropertiesEdit());

		// complete the edit
		m_storage.commit(edit);

		// track it
		EventTrackingService.post(
			EventTrackingService.newEvent(((BaseAliasEdit) edit).getEvent(), edit.getReference(), true));

		// close the edit object
		((BaseAliasEdit) edit).closeEdit();

	}	// commit

	/**
	* Cancel the changes made to a AliasEdit object, and release the lock.
	* The AliasEdit is disabled, and not to be used after this call.
	* @param user The AliasEdit object to commit.
	*/
	public void cancel(AliasEdit edit)
	{
		// check for closed edit
		if (!edit.isActiveEdit())
		{
			try { throw new Exception(); }
			catch (Exception e) { m_logger.warn(this + ".cancel(): closed AliasEdit", e); }
			return;
		}

		// release the edit lock
		m_storage.cancel(edit);

		// close the edit object
		((BaseAliasEdit) edit).closeEdit();

	}	// cancel

	/**
	* Remove this alias information - it must be a user with a lock from edit().
	* The AliasEdit is disabled, and not to be used after this call.
	* @param edit The locked AliasEdit object to remove.
	* @exception PermissionException if the current user does not have permission to remove this alias.
	*/
	public void remove(AliasEdit edit)
		throws PermissionException
	{
		// check for closed edit
		if (!edit.isActiveEdit())
		{
			try { throw new Exception(); }
			catch (Exception e) { m_logger.warn(this + ".remove(): closed AliasEdit", e); }
			return;
		}

		// check security (throws if not permitted)
		unlock(SECURE_REMOVE_ALIAS, edit.getReference());

		// complete the edit
		m_storage.remove(edit);

		// track it
		EventTrackingService.post(
			EventTrackingService.newEvent(SECURE_REMOVE_ALIAS, edit.getReference(), true));

		// close the edit object
		((BaseAliasEdit) edit).closeEdit();

	}	// remove

	/*******************************************************************************
	* Alias implementation
	*******************************************************************************/

	/**
	* <p>BaseAlias is an implementation of the CHEF Alias object.</p>
	* 
	* @author University of Michigan, CHEF Software Development Team
	*/
	public class BaseAliasEdit
		implements AliasEdit, SessionBindingListener
	{
		/** The alias id. */
		protected String  m_id = null;

		/** The alias target. */
		protected String m_target = null;

		/** The properties. */
		protected ResourcePropertiesEdit m_properties = null;

		/** The created user id. */
		protected String m_createdUserId = null;

		/** The last modified user id. */
		protected String m_lastModifiedUserId = null;

		/** The time created. */
		protected Time m_createdTime = null;

		/** The time last modified. */
		protected Time m_lastModifiedTime = null;

		/** The event code for this edit. */
		protected String m_event = null;

		/** Active flag. */
		protected boolean m_active = false;

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

		}	// finalize

		/**
		* Construct.
		* @param id The id.
		*/
		public BaseAliasEdit(String id)
		{
			m_id = id;

			// setup for properties
			ResourcePropertiesEdit props = new BaseResourcePropertiesEdit();
			m_properties = props;

			// if not a reconstruction, add properties
			if ((m_id != null) && (m_id.length() > 0)) addLiveProperties(props);

		}   // BaseAlias

		/**
		* ReConstruct.
		* @param id The id.
		* @param target The target.
		* @param createdBy The createdBy property.
		* @param createdOn The createdOn property.
		* @param modifiedBy The modified by property.
		* @param modifiedOn The modified on property.
		*/
		public BaseAliasEdit(String id, String target, String createdBy, Time createdOn, String modifiedBy, Time modifiedOn)
		{
			m_id = id;
			m_target = target;
			m_createdUserId = createdBy;
			m_lastModifiedUserId = modifiedBy;
			m_createdTime = createdOn;
			m_lastModifiedTime = modifiedOn;

			// setup for properties, but mark them lazy since we have not yet established them from data
			BaseResourcePropertiesEdit props = new BaseResourcePropertiesEdit();
			props.setLazy(true);
			m_properties = props;

		}   // BaseAlias


		/**
		* Construct from another Alias object.
		* @param alias The alias object to use for values.
		*/
		public BaseAliasEdit(BaseAliasEdit alias)
		{
			setAll(alias);

		}	// BaseAlias

		/**
		* Construct from information in XML.
		* @param el The XML DOM Element definining the alias.
		*/
		public BaseAliasEdit(Element el)
		{
			// setup for properties
			m_properties = new BaseResourcePropertiesEdit();

			m_id = el.getAttribute("id");
			m_target = el.getAttribute("target");

			m_createdUserId = StringUtil.trimToNull(el.getAttribute("created-id"));
			m_lastModifiedUserId = StringUtil.trimToNull(el.getAttribute("modified-id"));
			
			String time = StringUtil.trimToNull(el.getAttribute("created-time"));
			if (time != null)
			{
				m_createdTime = TimeService.newTimeGmt(time);
			}

			time = StringUtil.trimToNull(el.getAttribute("modified-time"));
			if (time != null)
			{
				m_lastModifiedTime = TimeService.newTimeGmt(time);
			}

			// the children (properties)
			NodeList children = el.getChildNodes();
			final int length = children.getLength();
			for (int i = 0; i < length; i++)
			{
				Node child = children.item(i);
				if (child.getNodeType() != Node.ELEMENT_NODE) continue;
				Element element = (Element)child;

				// look for properties
				if (element.getTagName().equals("properties"))
				{
					// re-create properties
					m_properties = new BaseResourcePropertiesEdit(element);

					// pull out some properties into fields to convert old (pre 1.18) versions
					if (m_createdUserId == null)
					{
						m_createdUserId = m_properties.getProperty("CHEF:creator");
					}
					if (m_lastModifiedUserId == null)
					{
						m_lastModifiedUserId = m_properties.getProperty("CHEF:modifiedby");
					}
					if (m_createdTime == null)
					{
						try
						{
							m_createdTime = m_properties.getTimeProperty("DAV:creationdate");
						}
						catch (Exception ignore) {}
					}
					if (m_lastModifiedTime == null)
					{
						try
						{
							m_lastModifiedTime = m_properties.getTimeProperty("DAV:getlastmodified");
						}
						catch (Exception ignore) {}
					}
					m_properties.removeProperty("CHEF:creator");
					m_properties.removeProperty("CHEF:modifiedby");
					m_properties.removeProperty("DAV:creationdate");
					m_properties.removeProperty("DAV:getlastmodified");
				}
			}

		}	// BaseAlias

		/**
		* Take all values from this object.
		* @param alias The alias object to take values from.
		*/
		protected void setAll(BaseAliasEdit alias)
		{
			m_id = alias.m_id;
			m_target = alias.m_target;
			m_createdUserId = ((BaseAliasEdit) alias).m_createdUserId;
			m_lastModifiedUserId = ((BaseAliasEdit) alias).m_lastModifiedUserId;
			if (((BaseAliasEdit) alias).m_createdTime != null) m_createdTime = (Time) ((BaseAliasEdit) alias).m_createdTime.clone();
			if (((BaseAliasEdit) alias).m_lastModifiedTime != null) m_lastModifiedTime = (Time) ((BaseAliasEdit) alias).m_lastModifiedTime.clone();

			m_properties = new BaseResourcePropertiesEdit();
			m_properties.addAll(alias.getProperties());
			((BaseResourcePropertiesEdit) m_properties).setLazy(((BaseResourceProperties) alias.getProperties()).isLazy());

		}   // setAll

		/**
		* Serialize the resource into XML, adding an element to the doc under the top of the stack element.
		* @param doc The DOM doc to contain the XML (or null for a string return).
		* @param stack The DOM elements, the top of which is the containing element of the new "resource" element.
		* @return The newly added element.
		*/
		public Element toXml(Document doc, Stack stack)
		{
			Element alias = doc.createElement("alias");

			if (stack.isEmpty())
			{
				doc.appendChild(alias);
			}
			else
			{
				((Element)stack.peek()).appendChild(alias);
			}

			stack.push(alias);

			alias.setAttribute("id", m_id);
			alias.setAttribute("target", m_target);
			alias.setAttribute("created-id", m_createdUserId);
			alias.setAttribute("modified-id", m_lastModifiedUserId);
			alias.setAttribute("created-time", m_createdTime.toString());
			alias.setAttribute("modified-time", m_lastModifiedTime.toString());

			// properties
			getProperties().toXml(doc, stack);

			stack.pop();

			return alias;

		}	// toXml

		/**
		* Access the alias id.
		* @return The alias id string.
		*/
		public String getId()
		{
			return m_id;

		}   // getId

		/**
		 * {@inheritDoc}
		 */
		public User getCreatedBy()
		{
			try
			{
				return UserDirectoryService.getUser(m_createdUserId);
			}
			catch (Exception e)
			{
				return UserDirectoryService.getAnonymousUser();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public User getModifiedBy()
		{
			try
			{
				return UserDirectoryService.getUser(m_lastModifiedUserId);
			}
			catch (Exception e)
			{
				return UserDirectoryService.getAnonymousUser();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public Time getCreatedTime()
		{
			return m_createdTime;
		}

		/**
		 * {@inheritDoc}
		 */
		public Time getModifiedTime()
		{
			return m_lastModifiedTime;
		}

		/**
		* Access the alias target.
		* @return The alias target.
		*/
		public String getTarget()
		{
			return m_target;

		}   // getTarget

		/**
		* Set the alias target.
		* @param target The alias target.
		*/
		public void setTarget(String target)
		{
			m_target = target;

		}   // setTarget

		/**
		* Access the URL which can be used to access the resource.
		* @return The URL which can be used to access the resource.
		*/
		public String getUrl()
		{
			return getAccessPoint(false) + m_id;

		}   // getUrl

		/**
		* Access the internal reference which can be used to access the resource from within the system.
		* @return The the internal reference which can be used to access the resource from within the system.
		*/
		public String getReference()
		{
			return aliasReference(m_id);

		}   // getReference

		/**
		* Access the resources's properties.
		* @return The resources's properties.
		*/
		public ResourceProperties getProperties()
		{
			// if lazy, resolve
			if (((BaseResourceProperties) m_properties).isLazy())
			{
				((BaseResourcePropertiesEdit) m_properties).setLazy(false);
				m_storage.readProperties(this, m_properties);
			}

			return m_properties;

		}   // getProperties

		/**
		* Are these objects equal?  If they are both Alias objects, and they have
		* matching id's, they are.
		* @return true if they are equal, false if not.
		*/
		public boolean equals(Object obj)
		{
			if (!(obj instanceof BaseAliasEdit)) return false;
			return ((BaseAliasEdit)obj).getId().equals(getId());

		}   // equals

		/**
		* Make a hash code that reflects the equals() logic as well.
		* We want two objects, even if different instances, if they have the same id to hash the same.
		*/
		public int hashCode()
		{
			return getId().hashCode();

		}	// hashCode

		/**
		* Compare this object with the specified object for order.
		* @return A negative integer, zero, or a positive integer as this object is
		* less than, equal to, or greater than the specified object.
		*/
		public int compareTo(Object obj)
		{
			if (!(obj instanceof BaseAliasEdit)) throw new ClassCastException();

			// if the object are the same, say so
			if (obj == this) return 0;

			// sort based on (unique) id
			int compare = getId().compareTo(((BaseAliasEdit)obj).getId());

			return compare;

		}	// compareTo

		/**
		* Access the event code for this edit.
		* @return The event code for this edit.
		*/
		protected String getEvent() { return m_event; }
	
		/**
		* Set the event code for this edit.
		* @param event The event code for this edit.
		*/
		protected void setEvent(String event) { m_event = event; }

		/**
		* Access the resource's properties for modification
		* @return The resource's properties.
		*/
		public ResourcePropertiesEdit getPropertiesEdit()
		{
			// if lazy, resolve
			if (((BaseResourceProperties) m_properties).isLazy())
			{
				((BaseResourcePropertiesEdit) m_properties).setLazy(false);
				m_storage.readProperties(this, m_properties);
			}

			return m_properties;

		}	// getPropertiesEdit

		/**
		* Enable editing.
		*/
		protected void activate()
		{
			m_active = true;

		}	// activate

		/**
		* Check to see if the edit is still active, or has already been closed.
		* @return true if the edit is active, false if it's been closed.
		*/
		public boolean isActiveEdit()
		{
			return m_active;

		}	// isActiveEdit

		/**
		* Close the edit object - it cannot be used after this.
		*/
		protected void closeEdit()
		{
			m_active = false;

		}	// closeEdit

		/**
		* @return a description of the item this alias's target applies to.
		*/
		public String getDescription()
		{
			try
			{
				// the rest are references to some resource
				Reference ref = new Reference(getTarget());
				return ref.getDescription();
			}
			catch (Throwable any)
			{
				return "unknown";
			}

		}	// getDescription

		/*******************************************************************************
		* SessionBindingListener implementation
		*******************************************************************************/
	
		public void valueBound(SessionBindingEvent event) {}
	
		public void valueUnbound(SessionBindingEvent event)
		{
			if (m_logger.isDebugEnabled())
				m_logger.debug(this + ".valueUnbound()");
	
			// catch the case where an edit was made but never resolved
			if (m_active)
			{
				cancel(this);
			}
	
		}	// valueUnbound

	}   // BaseAliasEdit

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
		* Check if an alias with this id exists.
		* @param id The alias id (case insensitive).
		* @return true if an alias by this id exists, false if not.
		*/
		public boolean check(String id);

		/**
		* Get the alias with this id, or null if not found.
		* @param id The alias id (case insensitive).
		* @return The alias with this id, or null if not found.
		*/
		public AliasEdit get(String id);

		/**
		* Get all the alias.
		* @return The List (BaseAliasEdit) of all alias.
		*/
		public List getAll();

		/**
		* Get all the alias in record range.
		* @param first The first record position to return.
		* @param last The last record position to return.
		* @return The List (BaseAliasEdit) of all alias.
		*/
		public List getAll(int first, int last);

		/**
		* Count all the aliases.
		* @return The count of all aliases.
		*/
		public int count();

		/**
		* Search for aliases with id or target matching criteria, in range.
		* @param criteria The search criteria.
		* @param first The first record position to return.
		* @param last The last record position to return.
		* @return The List (BaseAliasEdit) of all alias.
		*/
		public List search(String criteria, int first, int last);

		/**
		* Count all the aliases with id or target matching criteria.
		* @param criteria The search criteria.
		* @return The count of all aliases with id or target matching criteria.
		*/
		public int countSearch(String criteria);

		/**
		* Get all the alias that point at this target.
		* @return The List (BaseAliasEdit) of all alias that point at this target
		*/
		public List getAll(String target);

		/**
		* Get all the alias that point at this target, in record range.
		* @param first The first record position to return.
		* @param last The last record position to return.
		* @return The List (BaseAliasEdit) of all alias that point at this target, in record range.
		*/
		public List getAll(String target, int first, int last);

		/**
		* Add a new alias with this id.
		* @param id The alias id.
		* @return The locked Alias object with this id, or null if the id is in use.
		*/
		public AliasEdit put(String id);

		/**
		* Get a lock on the alias with this id, or null if a lock cannot be gotten.
		* @param id The alias id (case insensitive).
		* @return The locked Alias with this id, or null if this records cannot be locked.
		*/
		public AliasEdit edit(String id);

		/**
		* Commit the changes and release the lock.
		* @param user The alias to commit.
		*/
		public void commit(AliasEdit alias);

		/**
		* Cancel the changes and release the lock.
		* @param user The alias to commit.
		*/
		public void cancel(AliasEdit alias);

		/**
		* Remove this alias.
		* @param user The alias to remove.
		*/
		public void remove(AliasEdit alias);

		/**
		 * Read properties from storage into the edit's properties.
		 * @param edit The user to read properties for.
		 */
		public void readProperties(AliasEdit edit, ResourcePropertiesEdit props);

	}   // Storage

	/*******************************************************************************
	* StorageUser implementation (no container)
	*******************************************************************************/

	/**
	* Construct a new continer given just an id.
	* @param id The id for the new object.
	* @return The new containe Resource.
	*/
	public Resource newContainer(String ref) { return null; }

	/**
	* Construct a new container resource, from an XML element.
	* @param element The XML.
	* @return The new container resource.
	*/
	public Resource newContainer(Element element) { return null; }

	/**
	* Construct a new container resource, as a copy of another
	* @param other The other contianer to copy.
	* @return The new container resource.
	*/
	public Resource newContainer(Resource other) { return null; }

	/**
	* Construct a new resource given just an id.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param id The id for the new object.
	* @param others (options) array of objects to load into the Resource's fields.
	* @return The new resource.
	*/
	public Resource newResource(Resource container, String id, Object[] others)
	{ return new BaseAliasEdit(id); }

	/**
	* Construct a new resource, from an XML element.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param element The XML.
	* @return The new resource from the XML.
	*/
	public Resource newResource(Resource container, Element element)
	{ return new BaseAliasEdit(element); }

	/**
	* Construct a new resource from another resource of the same type.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param other The other resource.
	* @return The new resource as a copy of the other.
	*/
	public Resource newResource(Resource container, Resource other)
	{ return new BaseAliasEdit((BaseAliasEdit) other); }

	/**
	* Construct a new continer given just an id.
	* @param id The id for the new object.
	* @return The new containe Resource.
	*/
	public Edit newContainerEdit(String ref) { return null; }

	/**
	* Construct a new container resource, from an XML element.
	* @param element The XML.
	* @return The new container resource.
	*/
	public Edit newContainerEdit(Element element) { return null; }

	/**
	* Construct a new container resource, as a copy of another
	* @param other The other contianer to copy.
	* @return The new container resource.
	*/
	public Edit newContainerEdit(Resource other) { return null; }

	/**
	* Construct a new resource given just an id.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param id The id for the new object.
	* @param others (options) array of objects to load into the Resource's fields.
	* @return The new resource.
	*/
	public Edit newResourceEdit(Resource container, String id, Object[] others)
	{
		BaseAliasEdit e = new BaseAliasEdit(id);
		e.activate();
		return e;
	}

	/**
	* Construct a new resource, from an XML element.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param element The XML.
	* @return The new resource from the XML.
	*/
	public Edit newResourceEdit(Resource container, Element element)
	{
		BaseAliasEdit e =  new BaseAliasEdit(element);
		e.activate();
		return e;
	}

	/**
	* Construct a new resource from another resource of the same type.
	* @param container The Resource that is the container for the new resource (may be null).
	* @param other The other resource.
	* @return The new resource as a copy of the other.
	*/
	public Edit newResourceEdit(Resource container, Resource other)
	{
		BaseAliasEdit e = new BaseAliasEdit((BaseAliasEdit) other);
		e.activate();
		return e;
	}

	/**
	* Collect the fields that need to be stored outside the XML (for the resource).
	* @return An array of field values to store in the record outside the XML (for the resource).
	*/
	public Object[] storageFields(Resource r) { return null; }

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

}   // BaseAliasService

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/component/src/java/org/sakaiproject/component/legacy/alias/BaseAliasService.java,v 1.3 2005/05/12 01:38:24 ggolden.umich.edu Exp $
*
**********************************************************************************/