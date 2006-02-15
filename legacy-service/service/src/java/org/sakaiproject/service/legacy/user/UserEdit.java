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

import org.sakaiproject.service.legacy.entity.Edit;

/**
 * <p>
 * UserEdit is the core interface for the editable CHEF User object.
 * </p>
 */
public interface UserEdit extends User, Edit
{
	/**
	 * Set the user's id. Note: this is a special purpose routine that is used only to establish the id field, when the id is null, and cannot be used to change a user's id, which is defined to be an un-changing value.
	 * 
	 * @param name
	 *        The user id.
	 */
	public void setId(String id);

	/**
	 * Set the user's enterprise id. Must be unique among all users.
	 * 
	 * @param eid
	 *        The new eid value.
	 */
	public void setEid(String eid);

	/**
	 * Set the email address.
	 * 
	 * @param email
	 *        The email address string.
	 */
	public void setEmail(String email);

	/**
	 * Set the user's first name.
	 * 
	 * @param name
	 *        The user's first name.
	 */
	public void setFirstName(String name);

	/**
	 * Set the user's last name.
	 * 
	 * @param name
	 *        The user's last name.
	 */
	public void setLastName(String name);

	/**
	 * Set the user's password
	 * 
	 * @param pw
	 *        The user's new password.
	 */
	public void setPassword(String pw);

	/**
	 * Set the user type.
	 * 
	 * @param type
	 *        The user type.
	 */
	public void setType(String type);
}
