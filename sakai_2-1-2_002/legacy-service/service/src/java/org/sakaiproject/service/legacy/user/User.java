/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2006 The Sakai Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 * 
 *      http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.service.legacy.user;

import org.sakaiproject.service.legacy.entity.Entity;
import org.sakaiproject.service.legacy.time.Time;

/**
 * <p>
 * User is the core interface for the Sakai User object.
 * </p>
 * <p>
 * User objects hold everything we know about Sakai users (except for their authentication password).
 * </p>
 */
public interface User extends Entity, Comparable
{
	// TODO: move these to Resource

	/**
	 * @return the user who created this.
	 */
	public User getCreatedBy();

	/**
	 * @return the user who last modified this.
	 */
	public User getModifiedBy();

	/**
	 * @return the time created.
	 */
	public Time getCreatedTime();

	/**
	 * @return the time last modified.
	 */
	public Time getModifiedTime();

	// TODO:

	/**
	 * Access the email address.
	 * 
	 * @return The email address string.
	 */
	public String getEmail();

	/**
	 * Access the user's name for display purposes.
	 * 
	 * @return The user's name for display purposes.
	 */
	public String getDisplayName();

	/**
	 * Access the user's name for sorting purposes.
	 * 
	 * @return The user's name for sorting purposes.
	 */
	public String getSortName();

	/**
	 * Access the user's first name.
	 * 
	 * @return The user's first name.
	 */
	public String getFirstName();

	/**
	 * Access the user's last name.
	 * 
	 * @return The user's last name.
	 */
	public String getLastName();

	/**
	 * Check if this is the user's password.
	 * 
	 * @param pw
	 *        The clear text password to check.
	 * @return true if the password matches, false if not.
	 */
	public boolean checkPassword(String pw);

	/**
	 * Access the user type.
	 * 
	 * @return The user type.
	 */
	public String getType();

	/**
	 * Access the user's enterprise id; the id they and the enterprise know as belonging to them.<br />
	 * The Enterprise id, like the User id, is unique among all defined users.<br />
	 * The EID may be used by the user to login, and will be used when communicating with the user directory provider.
	 * 
	 * @return The user's enterprise id.
	 */
	public String getEid();
}
