/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/alias/AliasService.java,v 1.1 2005/05/12 15:45:38 ggolden.umich.edu Exp $
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
package org.sakaiproject.service.legacy.alias;

// imports
import java.util.List;

import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.legacy.resource.Resource;

/**
* <p>AliasService provides aliases for CHEF resources.  Aliases are not case sensitive.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public interface AliasService
{
	/** This string can be used to find the service in the service manager. */
	public static final String SERVICE_NAME = AliasService.class.getName();

	/** This string starts the references to resources in this service. */
	public static final String REFERENCE_ROOT = Resource.SEPARATOR + "alias";

	/** Name for the event of adding an alias. */
	public static final String SECURE_ADD_ALIAS = "alias.add";

	/** Name for the event of updating an alias. */
	public static final String SECURE_UPDATE_ALIAS = "alias.upd";

	/** Name for the event of removing an alias. */
	public static final String SECURE_REMOVE_ALIAS = "alias.del";

	/**
	* Check if the current user has permission to set this alias.
	* @param alias The alias.
	* @param target The resource reference string alias target.
	* @return true if the current user has permission to set this alias, false if not.
	*/
	public boolean allowSetAlias(String alias, String target);

	/**
	* Allocate an alias for a resource
	* @param alias The alias.
	* @param target The resource reference string alias target.
	* @throws IdUsedException if the alias is already used.
	* @throws IdInvalidException if the alias id is invalid.
	* @throws PermissionException if the current user does not have permission to set this alias.
	*/
	public void setAlias(String alias, String target)
		throws IdUsedException, IdInvalidException, PermissionException;

	/**
	* Check to see if the current user can remove this alias.
	* @param alias The alias.
	* @return true if the current user can remove this alias, false if not.
	*/
	public boolean allowRemoveAlias(String alias);

	/**
	* Remove an alias.
	* @param alias The alias.
	* @exception IdUnusedException if not found.
	* @exception PermissionException if the current user does not have permission to remove this alias.
	* @exception InUseException if the Alias object is locked by someone else.
	*/
	public void removeAlias(String alias)
		throws IdUnusedException, PermissionException, InUseException;

	/**
	* Check to see if the current user can remove these aliasese for this target resource reference.
	* @param target The target resource reference string.
	* @return true if the current user can remove these aliasese for this target resource reference, false if not.
	*/
	public boolean allowRemoveTargetAliases(String target);

	/**
	* Remove all aliases for this target resource reference, if any.
	* @param target The target resource reference string.
	* @throws PermissionException if the current user does not have permission to remove these aliases.
	*/
	public void removeTargetAliases(String target)
		throws PermissionException;

	/**
	* Find the target resource reference string associated with this alias.
	* @param alias The alias.
	* @return The target resource reference string associated with this alias.
	* @throws IdUnusedException if the alias is not defined.
	*/
	public String getTarget(String alias)
		throws IdUnusedException;

	/**
	* Find all the aliases defined for this target.
	* @param target The target resource reference string.
	* @return A list (Alias) of all the aliases defined for this target.
	*/
	public List getAliases(String target);

	/**
	* Find all the aliases defined for this target, within the record range given (sorted by id).
	* @param target The target resource reference string.
	* @param first The first record position to return.
	* @param last The last record position to return.
	* @return A list (Alias) of all the aliases defined for this target, within the record range given (sorted by id).
	*/
	public List getAliases(String target, int first, int last);

	/**
	* Find all the aliases within the record range given (sorted by id).
	* @param first The first record position to return.
	* @param last The last record position to return.
	* @return A list (Alias) of all the aliases within the record range given (sorted by id).
	*/
	public List getAliases(int first, int last);

	/**
	* Count all the aliases.
	* @return The count of all aliases.
	*/
	public int countAliases();

	/**
	* Search all the aliases that match this criteria in id or target, returning a subset of records
	* within the record range given (sorted by id).
	* @param criteria The search criteria.
	* @param first The first record position to return.
	* @param last The last record position to return.
	* @return A list (Alias) of all the aliases matching the criteria, within the record range given (sorted by id).
	*/
	public List searchAliases(String criteria, int first, int last);

	/**
	* Count all the aliases that match this criteria in id or target.
	* @return The count of all aliases matching the criteria.
	*/
	public int countSearchAliases(String criteria);

	/**
	* Access the internal reference which can be used to access the resource from within the system.
	* @param id The alias id string.
	* @return The the internal reference which can be used to access the resource from within the system.
	*/
	public String aliasReference(String id);

	/**
	* Check to see if the current user can add an alias.
	* @return true if the current user can add an alias, false if not.
	*/
	public boolean allowAdd();

	/**
	* Add a new alias.  Must commit() to make official, or cancel() when done!
	* @param id The alias id.
	* @return A locked AliasEdit object (reserving the id).
	* @exception IdInvalidException if the alias id is invalid.
	* @exception IdUsedException if the alias id is already used.
	* @exception PermissionException if the current user does not have permission to add an alias.
	*/
	public AliasEdit add(String id)
		throws IdInvalidException, IdUsedException, PermissionException;

	/**
	* Check to see if the current user can edit this alias.
	* @param id The alias id string.
	* @return true if the current user can edit this alias, false if not.
	*/
	public boolean allowEdit(String id);

	/**
	* Get a locked alias object for editing. Must commit() to make official, or cancel() (or remove()) when done!
	* @param id The alias id string.
	* @return An AliasEdit object for editing.
	* @exception IdUnusedException if not found.
	* @exception PermissionException if the current user does not have permission to mess with this alias.
	* @exception InUseException if the Alias object is locked by someone else.
	*/
	public AliasEdit edit(String id)
		throws IdUnusedException, PermissionException, InUseException;

	/**
	* Commit the changes made to a AliasEdit object, and release the lock.
	* The AliasEdit is disabled, and not to be used after this call.
	* @param user The AliasEdit object to commit.
	*/
	public void commit(AliasEdit edit);

	/**
	* Cancel the changes made to a AliasEdit object, and release the lock.
	* The AliasEdit is disabled, and not to be used after this call.
	* @param user The AliasEdit object to commit.
	*/
	public void cancel(AliasEdit edit);

	/**
	* Remove this alias information - it must be a user with a lock from edit().
	* The AliasEdit is disabled, and not to be used after this call.
	* @param edit The locked AliasEdit object to remove.
	* @exception PermissionException if the current user does not have permission to remove this alias.
	*/
	public void remove(AliasEdit edit)
		throws PermissionException;

}	// GenericAliasService

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/alias/AliasService.java,v 1.1 2005/05/12 15:45:38 ggolden.umich.edu Exp $
*
**********************************************************************************/
