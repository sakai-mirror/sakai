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
package org.sakaiproject.service.legacy.security;

// imports
import java.util.List;

import org.sakaiproject.service.legacy.user.User;

/**
* <p>SecurityService is the interface for Sakai security services.</p>
* 
* @author Sakai Software Development Team
*/
public interface SecurityService
{
	/** This string can be used to find the service in the service manager. */
	public static final String SERVICE_NAME = SecurityService.class.getName();

	/**
	* Can the current session user unlock the lock for use with this resource?
	* @param lock The lock id string.
	* @param reference The resource reference string.
	* @return true, if the user can unlock the lock, false otherwise.
	*/
	public boolean unlock(String lock, String reference);

	/**
	* Can the specificed user unlock the lock for use with this resource?
	* @param user The user.
	* @param lock The lock id string.
	* @param reference The resource reference string.
	* @return true, if the user can unlock the lock, false otherwise.
	*/
	public boolean unlock(User user, String lock, String reference);

	/**
	* Access the List of Users who can unlock the lock for use with this resource.
	* @param lock The lock id string.
	* @param reference The resource reference string.
	* @return A List (User) of the users can unlock the lock (may be empty).
	*/
	public List unlockUsers(String lock, String reference);

	/**
	* Is this a super special super (admin) user?
	* @return true, if the user is a cut above the rest, false if a mere mortal.
	*/
	public boolean isSuperUser();

	/**
	* Add a new key.
	* @param userOrGroup The id of the user or user group which is given the key.
	* @param lockOrRole The id of the lock or role (lock group) which the key opens.
	* @param resourceOrGroup the id of the resource or resource group which restricts the key
	* (the key will work only for these resources.  null if no resource is involved).
	* @param allow true if the key allows access, false if it denys access.
	*/
	public void addKey(String userOrGroup, String lockOrRole, String resourceOrGroup, boolean allow);

	/**
	* Remove any keys that exactly match this key specification.
	* @param userOrGroup The id of the user or user group which is given the key.
	* @param lockOrRole The id of the lock or role (lock group) which the key opens.
	* @param resourceOrGroup the id of the resource or resource group which restricts the key
	* (the key will work only for these resources.  null if no resource is involved).
	* @param allow true if the key allows access, false if it denys access.
	*/
	public void removeKey(String userOrGroup, String lockOrRole, String resourceOrGroup, boolean allow);

	/**
	 * Establish a new SecurityAdvisor for this thread, at the top of the stack (it gets first dibs on the answer).
	 * @param advisor The advisor to establish
	 */
	void pushAdvisor(SecurityAdvisor advisor);

	/**
	 * Remove one SecurityAdvisor from the stack for this thread, if any exist.
	 * @return advisor The advisor popped of, or null if the stack is empty.
	 */
	SecurityAdvisor popAdvisor();
	
	/**
	 * Check if there are any security advisors stacked for this thread.
	 * @return true if some advisors are defined, false if not.
	 */
	boolean hasAdvisors();
	
	/**
	 * Remove any SecurityAdvisors from this thread.
	 */
	void clearAdvisors();
}
