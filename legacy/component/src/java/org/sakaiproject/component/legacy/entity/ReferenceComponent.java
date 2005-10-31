/**********************************************************************************
 * $URL$
 * $Id$
 **********************************************************************************
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
package org.sakaiproject.component.legacy.entity;

// imports
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.service.legacy.entity.Entity;
import org.sakaiproject.service.legacy.entity.EntityProducer;
import org.sakaiproject.service.legacy.entity.Reference;
import org.sakaiproject.service.legacy.entity.ResourceProperties;
import org.sakaiproject.service.legacy.resource.cover.EntityManager;
import org.sakaiproject.service.legacy.site.cover.SiteService;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;

/**
 * <p>
 * Implementation of the Reference API
 * </p>
 * <p>
 * Note: a Reference is immutable.
 * </p>
 * 
 * @author Sakai Software Development Team
 * @version $Revision$
 */
public class ReferenceComponent
	implements Reference
{
	/** Our logger. */
	protected static Log M_log = LogFactory.getLog(ReferenceComponent.class);

	/** The reference string. */
	protected String m_reference = null;

	/** The reference type (a service name string). */
	protected String m_type = "";

	/** The reference sub-type. */
	protected String m_subType = "";

	/** The reference primary id. */
	protected String m_id = null;

	/** The reference containment ids. */
	protected String m_container = null;

	/** Another container, the context id. */
	protected String m_context = null;

	/** Set to true once the values are set. */
	protected boolean m_setAlready = false;

	/** The service owning the entity. */
	protected EntityProducer m_service = null;

	/** These are some more services known. */
	public static final String GRADEBOOK_ROOT = "gradebook";

	public static final String GRADTOOLS_ROOT = "dissertation";

	/**
	 * Construct with a reference string.
	 * 
	 * @param ref
	 *        The resource reference.
	 */
	public ReferenceComponent(String ref)
	{
		m_reference = ref;
		parse();
		
	} // Reference

	/**
	 * Construct with a Reference.
	 * 
	 * @param ref
	 *        The resource reference.
	 */
	public ReferenceComponent(Reference copyMe)
	{
		ReferenceComponent ref = (ReferenceComponent) copyMe;

		m_reference = ref.m_reference;
		m_type = ref.m_type;
		m_subType = ref.m_subType;
		m_id = ref.m_id;
		m_container = ref.m_container;
		m_context = ref.m_context;
		m_service = ref.m_service;

	} // Reference

	/**
	 * Access the reference.
	 * 
	 * @return The reference.
	 */
	public String getReference()
	{
		return m_reference;
	}

	/**
	 * Access the type, a service id string.
	 * 
	 * @return The type, a service id string.
	 */
	public String getType()
	{
		return m_type;
	}

	/**
	 * Check if the reference's type is known
	 * 
	 * @return true if known, false if not.
	 */
	public boolean isKnownType()
	{
		return m_type.length() > 0;
	}

	/**
	 * Access the subType.
	 * 
	 * @return The subType.
	 */
	public String getSubType()
	{
		return m_subType;
	}

	/**
	 * Access the primary id.
	 * 
	 * @return The primary id.
	 */
	public String getId()
	{
		return m_id;
	}

	/**
	 * Access a single container id, the from most general (or only)
	 * 
	 * @return The single or most general container, if any.
	 */
	public String getContainer()
	{
		return m_container;

	} // getContainer

	/**
	 * Access the context id, if any.
	 * 
	 * @return the context id, if any.
	 */
	public String getContext()
	{
		return m_context;

	} // getContext

	/**
	 * Find the ResourceProperties object for this reference.
	 * 
	 * @return A ResourcesProperties object found (or constructed) for this reference.
	 */
	public ResourceProperties getProperties()
	{
		ResourceProperties props = null;
		
		if (m_service != null)
		{
			props = m_service.getEntityResourceProperties(this);
		}

		return props;

	} // getProperties

	/**
	 * Find the Entity that is referenced.
	 * 
	 * @return The Entity object that this references.
	 */
	public Entity getEntity()
	{
		Entity e = null;
		
		if (m_service != null)
		{
			e = m_service.getEntity(this);
		}

		return e;

	} // getResource

	/**
	 * Access the URL which can be used to access the referenced resource.
	 * 
	 * @return The URL which can be used to access the referenced resource.
	 */
	public String getUrl()
	{
		String url = null;
		
		if (m_service != null)
		{
			url = m_service.getEntityUrl(this);
		}

		return url;

	} // getUrl

	/**
	 * @return a description of the resource referenced.
	 */
	public String getDescription()
	{
		String rv = "unknown";
		
		if (m_service != null)
		{
			rv = m_service.getEntityDescription(this);
		
			if (rv == null)
			{
				rv = m_service.getLabel() + " " + m_reference;
			}
		}

		return rv;

	} // getDescription

	/**
	 * Compute the set of AuthzGroup ids associated with this referenced resource.
	 * 
	 * @return List of AuthzGroup ids (String) associated with this referenced resource.
	 */
	public Collection getRealms()
	{
		Collection realms = null;
		
		if (m_service != null)
		{
			realms = m_service.getEntityAuthzGroups(this);
		}

		if (realms == null) realms = new Vector();

		return realms;
/*
		// for portal page access: no additional role realms
		// TODO: is this still used? -ggolden
		else if (getType().equals(PortalService.SERVICE_NAME))
		{
			try
			{
				// add a site reference
				if ("group".equals(getContainer()))
				{
					rv.add(SiteService.siteReference(getId()));
				}
				else if ("user".equals(getContainer()))
				{
					rv.add(SiteService.siteReference(SiteService.getUserSiteId(getId())));
				}
				else if ("role".equals(getContainer()) && "user".equals(getId()))
				{
					rv.add(SiteService.siteReference(SiteService.getUserSiteId(UsageSessionService.getSessionUserId())));
				}
				else
				{
					rv.add(SiteService.siteReference(SiteService.getSpecialSiteId(getContainer() + "-" + getId())));
				}

				// site helper
				rv.add("!site.helper");
			}
			catch (NullPointerException e)
			{
				Log.warn("chef", this + ".getRealms(): " + e);
			}

		}
*/
	} // getRealms

	/**
	 * Add the AuthzGroup(s) for context as a site.
	 * 
	 * @param rv
	 *        The list.
	 */
	public void addSiteContextAuthzGroup(Collection rv)
	{
		// site using context as id
		rv.add(SiteService.siteReference(getContext()));

		// site helper
		rv.add("!site.helper");
	}

	/**
	 * Add the AuthzGroup for this user id, or for the user's type template, or for the general template.
	 * 
	 * @param rv
	 *        The list.
	 * @param id
	 *        The user id.
	 */
	public void addUserAuthzGroup(Collection rv, String id)
	{
		if (id == null) id = "";

		// the user's realm
		rv.add(UserDirectoryService.userReference(id));

		addUserTemplateAuthzGroup(rv, id);
	}

	/**
	 * Add the AuthzGroup for this user id, or for the user's type template, or for the general template.
	 * 
	 * @param rv
	 *        The list.
	 * @param id
	 *        The user id.
	 */
	public void addUserTemplateAuthzGroup(Collection rv, String id)
	{
		if (id == null) id = "";

		// user type template
		String template = "!user.template";
		try
		{
			User user = UserDirectoryService.getUser(id);
			String type = user.getType();
			if (type != null)
			{
				rv.add(template + "." + type);
			}
		}
		catch (Exception ignore)
		{
		}

		// general user template
		rv.add("!user.template");
	}

	/**
	 * Accept the settings for a reference - may be rejected if already set
	 * @param type
	 * @param subType
	 * @param id
	 * @param container
	 * @param container2
	 * @param context
	 * @return true if settings are accepted, false if not.
	 */
	public boolean set(String type, String subType, String id, String container, String context)
	{
		if (m_setAlready) return false;
		
		// these must not be null
		m_type = type;
		m_subType = subType;
		if (m_type == null) m_type = "";
		if (m_subType == null) m_subType = "";

		// these should be null if empty
		m_id = id;
		m_container = container;
		m_context = context;
		if ((m_id != null) && (m_id.length() == 0)) m_id = null;
		if ((m_container != null) && (m_container.length() == 0)) m_container = null;
		if ((m_context != null) && (m_context.length() == 0)) m_context = null;

		m_setAlready = true;

		return true;
	}

	/**
	 * @inheritDoc
	 */
	public EntityProducer getEntityProducer()
	{
		return m_service;
	}

	/*
	 * Parse the reference
	 */
	protected void parse()
	{
		if (m_reference == null) return;

		// check with the resource services to see if anyone recognizes this
		for (Iterator iServices = EntityManager.getEntityProducers().iterator(); iServices.hasNext();)
		{
			EntityProducer service = (EntityProducer) iServices.next();
			
			// give each a change to recognize and parse the reference string, filling in this Reference with a call to set()
			if (service.parseEntityReference(m_reference, this))
			{
				// save the service
				m_service = service;

				// done
				return;
			}
		}
		
		if (M_log.isDebugEnabled()) M_log.debug("parse(): unhandled reference: " + m_reference);

	} // parse

} // Reference



