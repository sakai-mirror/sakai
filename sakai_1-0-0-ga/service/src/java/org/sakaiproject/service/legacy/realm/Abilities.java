/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/realm/Abilities.java,v 1.1 2004/07/07 17:13:29 ggolden.umich.edu Exp $
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
package org.sakaiproject.service.legacy.realm;

// imports
import java.util.List;
import java.util.Set;

/**
* <p>A set of abilities - locks that may be unlocked, roles that may be used for their abilities.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.1 $
*/
public interface Abilities extends Set
{
	/**
	 * Check this ability set for a lock.
	 * @param lock The lock.
	 * @param helperRealms Other realms that might refine role abilities.
	 * @return true if this lock is within this abilities, false if not.
	 */
	boolean unlock(String lock, List helperRealms);

	/**
	 * Check for this role in this abilities.
	 * @param role The Role.
	 */
	boolean hasRole(Role role);

	/**
	 * Access all the locks this ability set can open.
	 * @param helperRealms Other realms that might refine role abilities.
	 * @return The Set of locks (String) this ability set can open.
	 */
	Set collectLocks(List helperRealms);

	/**
	* Access all the Roles this ability set can open.
	* @return The Set of roles (Role) this ability set can open.
	*/
	Set collectRoles();

	/**
	 * Access all the locks directly added to this ability set.
	 * @return The Set of locks (String) directly added to this ability set.
	 */
	Set getLocks();

	/**
	 * Access all roles directly added to this ability set.
	 * @return The Set of roles (Role) directly added to this ability set.
	 */
	Set getRoles();
}

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/realm/Abilities.java,v 1.1 2004/07/07 17:13:29 ggolden.umich.edu Exp $
*
**********************************************************************************/
