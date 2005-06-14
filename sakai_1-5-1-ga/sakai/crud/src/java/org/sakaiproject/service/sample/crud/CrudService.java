/**********************************************************************************
*
* $Header: /cvs/sakai/crud/src/java/org/sakaiproject/service/sample/crud/CrudService.java,v 1.4 2004/06/22 03:10:39 ggolden Exp $
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

package org.sakaiproject.service.sample.crud;

import java.util.List;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.VersionException;

/**
 * <p>CrudService is a sample Service API (Create, Read, Update, Delete some CrudObject).</p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision: 1.4 $
 */
public interface CrudService
{
	/**
	 * Check if the current user has permission to create.
	 * @return true if the current user has permission to perform this action, false if not.
	 */
	boolean allowCreate();

	/**
	 * Create a new thing.
	 * @return a new thing.
	 * @throws PermissionException if the user does not have permission to perform this action.
	 */
	CrudObject create() throws PermissionException;

	/**
	 * Check if the current user has permission to get.
	 * @return true if the current user has permission to perform this action, false if not.
	 */
	boolean allowGet(String id);

	/**
	 * Access a thing.
	 * @param id The thing's id.
	 * @return The thing.
	 * @throws IdUnusedException if the id is not a thing id.
	 * @throws PermissionException if the user does not have permission to perform this action.
	 */
	CrudObject get(String id) throws IdUnusedException, PermissionException;

	/**
	 * Access all the things "matching" the name criteria.
	 * @param criteria Search criteria (ok, so what this means is not well defined! -ggolden)
	 * @return A List (CrudObject) of matching things, sorted by their natural sort order.
	 * @throws PermissionException if the user does not have permission to perform this action.
	 */
	List findByName(String criteria) throws PermissionException;

	/**
	 * Check if the current user has permission to update.
	 * @return true if the current user has permission to perform this action, false if not.
	 */
	boolean allowUpdate(CrudObject co);

	/**
	 * Update the thing
	 * @param co The thing with new values set for update.
	 * @throws PermissionException if the user does not have permission to perform this action.
	 * @throws VersionException if the thing is not of the latest version.
	 */
	void update(CrudObject co) throws PermissionException, VersionException;

	/**
	 * Check if the current user has permission to delete.
	 * @return true if the current user has permission to perform this action, false if not.
	 */
	boolean allowDelete(String id);

	/**
	 * Delete the thing.
	 * @param id The thing's id.
	 * @throws PermissionException if the user does not have permission to perform this action.
	 * @throws IdUnusedException if the id is not a thing id.
	 */
	void delete(String id) throws PermissionException, IdUnusedException;
}

/**********************************************************************************
*
* $Header: /cvs/sakai/crud/src/java/org/sakaiproject/service/sample/crud/CrudService.java,v 1.4 2004/06/22 03:10:39 ggolden Exp $
*
**********************************************************************************/
