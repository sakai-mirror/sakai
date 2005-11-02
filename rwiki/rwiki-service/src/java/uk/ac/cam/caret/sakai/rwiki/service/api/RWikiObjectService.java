/**********************************************************************************
 *
 * $Header$
 *
 ***********************************************************************************
 *
 * Copyright (c) 2005 University of Cambridge
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
package uk.ac.cam.caret.sakai.rwiki.service.api;

import java.util.Date;
import java.util.List;

import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiCurrentObject;
import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiHistoryObject;
import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiObject;
import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiPermissions;
import uk.ac.cam.caret.sakai.rwiki.service.exception.PermissionException;
import uk.ac.cam.caret.sakai.rwiki.service.exception.VersionException;

//FIXME: Service

public interface RWikiObjectService {

	/**
	 * Gets the current object
	 * 
	 * @param name
	 * @param user
	 * @param realm
	 * @return
	 * @throws PermissionException
	 */
	RWikiCurrentObject getRWikiObject(String name, String user, String realm)
			throws PermissionException;

	/**
	 * Gets the object based on the ID. This
	 * 
	 * @param reference
	 *            the reference object
	 * @return
	 */
	RWikiCurrentObject getRWikiObject(RWikiObject reference);

	/**
	 * Search on current objects
	 * 
	 * @param criteria
	 * @param user
	 * @param realm
	 * @return
	 * @throws PermissionException
	 */
	List search(String criteria, String user, String realm)
			throws PermissionException;

	/**
	 * Check for read permission
	 * 
	 * @param rwo
	 * @param user
	 * @return
	 */
	boolean checkRead(RWikiObject rwo, String user);

	/**
	 * check for update permission
	 * 
	 * @param rwo
	 * @param user
	 * @return
	 */
	boolean checkUpdate(RWikiObject rwo, String user);

	/**
	 * check for admin permission
	 * 
	 * @param rwo
	 * @param user
	 * @return
	 */
	boolean checkAdmin(RWikiObject rwo, String user);

	/**
	 * Update the named page, with permissions
	 * 
	 * @param name
	 * @param user
	 * @param realm
	 * @param version
	 * @param content
	 * @param permissions
	 * @throws PermissionException
	 * @throws VersionException
	 */
	void update(String name, String user, String realm, Date version,
			String content, RWikiPermissions permissions)
			throws PermissionException, VersionException;

	/**
	 * Update the name page, no permissions
	 * 
	 * @param name
	 * @param user
	 * @param realm
	 * @param version
	 * @param content
	 * @throws PermissionException
	 * @throws VersionException
	 */
	void update(String name, String user, String realm, Date version,
			String content) throws PermissionException, VersionException;

	/**
	 * Update the name page's permissions
	 * 
	 * @param name
	 * @param user
	 * @param realm
	 * @param version
	 * @param permissions
	 * @throws PermissionException
	 * @throws VersionException
	 */
	void update(String name, String user, String realm, Date version,
			RWikiPermissions permissions) throws PermissionException,
			VersionException;

	/**
	 * Does the page exist
	 * 
	 * @param name A possibly non-globalised page name
	 * @param space Default space to globalise to
	 * @return
	 */
	boolean exists(String name, String space);
    // SAK-2519
	/**
	 * A list of pages that have changed since (current versions)
	 * 
	 * @param since
	 * @param user
	 * @param realm
	 * @return a list containing RWikiCurrentObjects
	 */
	List findChangedSince(Date since, String user, String realm);

	/**
	 * Finds pages that reference the given page name
	 * 
	 * @param name
	 * @return a non-null list of page names not rwikiObjects
	 */
	List findReferencingPages(String name);

	/**
	 * Revert current revision to a named revision, creates a new revision
	 * 
	 * @param name
	 * @param user
	 * @param realm
	 * @param version
	 * @param revision
	 */
	void revert(String name, String user, String realm, Date version,
			int revision);

	/**
	 * Get a previous version
	 * 
	 * @param referenceObject
	 *            the Rwiki object whore rwikiobjectid field will be used to
	 *            locate the revision
	 * @param revision
	 * @return
	 */
	RWikiHistoryObject getRWikiHistoryObject(RWikiObject refernceObject,
			int revision);

	/**
	 * get a list of all previous versions as RWikiHistoryObjects
	 * 
	 * @param id
	 * @return
	 */
	List findRWikiHistoryObjects(RWikiObject reference);
}
