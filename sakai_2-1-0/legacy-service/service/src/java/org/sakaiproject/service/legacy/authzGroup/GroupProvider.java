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
package org.sakaiproject.service.legacy.authzGroup;

// imports
import java.util.Map;

/**
 * <p>
 * GroupProvider provides user / role membership in a group, from an external system.
 * </p>
 * 
 * @author Sakai Software Development Team
 */
public interface GroupProvider
{
	/**
	 * Access the role name for this particular user in the external group.
	 * 
	 * @param id
	 *        The external group id.
	 * @param userId
	 *        The user Id.
	 * @return the role name for this particular user in the external group, or null if none.
	 */
	String getRole(String id, String user);

	/**
	 * Access the user id - role name map for all users in the external group.
	 * 
	 * @param id
	 *        The external group id.
	 * @return the user id - role name map for all users in the external group (may be empty).
	 */
	Map getUserRolesForGroup(String id);

	/**
	 * Access the external group id - role name map for this user in all external groups.
	 * <p>
	 * If the provider supports compound external ids, the get() of this Map needs to accept a compount external id<br />
	 * and return the single role (if any) resolved from any individual external realm ids in the map.
	 * </p>
	 * 
	 * @param userId
	 *        The user id.
	 * @return the the external group id - role name map for this users in all external groups. (may be empty).
	 */
	Map getGroupRolesForUser(String userId);

	/**
	 * Unpack a possibly compound id into it's component ids, returning at least the id unchanged if not compound.
	 * 
	 * @param id
	 *        The external realm id.
	 * @return a String array of one or more ids upacked from this possibly compound id.
	 */
	String[] unpackId(String id);
}
