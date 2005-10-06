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
package org.sakaiproject.service.legacy.entity;

// imports
import java.util.Collection;

/**
 * <p>
 * Reference holds a reference to a Sakai entity.
 * </p>
 * <p>
 * Note: a Reference is immutable.
 * </p>
 * 
 * @author Sakai Software Development Team
 * @version $Revision$
 */
public interface Reference
{
	/**
	 * Add the AuthzGroup(s) for context as a site.
	 * 
	 * @param rv
	 *        The list.
	 */
	void addSiteContextRealm(Collection rv);

	/**
	 * Add the AuthzGroup for this user id, or for the user's type template, or for the general template.
	 * 
	 * @param rv
	 *        The list.
	 * @param id
	 *        The user id.
	 */
	void addUserRealm(Collection rv, String id);

	/**
	 * Add the AuthzGroup for this user id, or for the user's type template, or for the general template.
	 * 
	 * @param rv
	 *        The list.
	 * @param id
	 *        The user id.
	 */
	void addUserTemplateRealm(Collection rv, String id);

	/**
	 * Access a single container id, the from most general (or only)
	 * 
	 * @return The single or most general container, if any.
	 */
	String getContainer();

	/**
	 * Access the context id, if any.
	 * 
	 * @return the context id, if any.
	 */
	String getContext();

	/**
	 * @return a description of the resource referenced.
	 */
	String getDescription();

	/**
	 * Find the Entity that is referenced.
	 * 
	 * @return The Entity object that this references.
	 */
	Entity getEntity();

	/**
	 * Access the primary id.
	 * 
	 * @return The primary id.
	 */
	String getId();

	/**
	 * Find the ResourceProperties object for this reference.
	 * 
	 * @return A ResourcesProperties object found (or constructed) for this reference.
	 */
	ResourceProperties getProperties();

	/**
	 * Compute the set of AuthzGroup ids associated with this referenced resource.
	 * 
	 * @return List of AuthzGroup ids (String) associated with this referenced resource.
	 */
	Collection getRealms();

	/**
	 * Access the reference.
	 * 
	 * @return The reference.
	 */
	String getReference();

	/**
	 * Access the subType.
	 * 
	 * @return The subType.
	 */
	String getSubType();

	/**
	 * Access the type, a service id string.
	 * 
	 * @return The type, a service id string.
	 */
	String getType();

	/**
	 * Access the URL which can be used to access the referenced resource.
	 * 
	 * @return The URL which can be used to access the referenced resource.
	 */
	String getUrl();

	/**
	 * Check if the reference's type is known
	 * 
	 * @return true if known, false if not.
	 */
	boolean isKnownType();

	/**
	 * Accept the settings for a reference - may be rejected if already set
	 * 
	 * @param type
	 * @param subType
	 * @param id
	 * @param container
	 * @param container2
	 * @param context
	 * @return true if settings are accepted, false if not.
	 */
	boolean set(String type, String subType, String id, String container, String context);
}
