/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/preference/PreferencesService.java,v 1.1 2005/05/12 15:45:37 ggolden.umich.edu Exp $
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
package org.sakaiproject.service.legacy.preference;

// imports
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.legacy.resource.Resource;

/**
* <p>The PreferencesService keeps sets of preferences for each user.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public interface PreferencesService
{
	/** This string can be used to find the service in the service manager. */
	public static final String SERVICE_NAME = PreferencesService.class.getName();

	/** This string starts the references to resources in this service. */
	public static final String REFERENCE_ROOT = Resource.SEPARATOR + "prefs";

	/** Securiy / Event for adding a preferences. */
	public static final String SECURE_ADD_PREFS = "prefs.add";

	/** Securiy / Event for updating a preferences. */
	public static final String SECURE_EDIT_PREFS = "prefs.upd";

	/** Securiy / Event for removing a preferences. */
	public static final String SECURE_REMOVE_PREFS = "prefs.del";

	/**
	* Access a set of preferences associated with this id.
	* @param id The preferences id.
	* @return The Preferences object.
	*/
	public Preferences getPreferences(String id);

	/**
	* Check to see if the current user can add or modify permissions with this id.
	* @param id The preferences id.
	* @return true if the user is allowed to update or add these preferences, false if not.
	*/
	public boolean allowUpdate(String id);

	/**
	* Add a new set of preferences with this id.  Must commit(), remove() or cancel() when done.
	* @param id The preferences id.
	* @return A PreferencesEdit object for editing, possibly new.
	* @exception PermissionException if the current user does not have permission add preferences for this id.
	* @exception IdUsedException if these preferences already exist.
	*/
	public PreferencesEdit add(String id)
		throws PermissionException, IdUsedException;

	/**
	* Get a locked Preferences object for editing. Must commit(), cancel() or remove() when done.
	* @param id The preferences id.
	* @return A PreferencesEdit object for editing, possibly new.
	* @exception PermissionException if the current user does not have permission to edit these preferences.
	* @exception InUseException if the preferences object is locked by someone else.
	* @exception IdUnusedException if there is not preferences object with this id.
	*/
	public PreferencesEdit edit(String id)
		throws PermissionException, InUseException, IdUnusedException;

	/**
	* Commit the changes made to a PreferencesEdit object, and release the lock.
	* The PreferencesEdit is disabled, and not to be used after this call.
	* @param user The PreferencesEdit object to commit.
	*/
	public void commit(PreferencesEdit edit);

	/**
	* Cancel the changes made to a PreferencesEdit object, and release the lock.
	* The PreferencesEdit is disabled, and not to be used after this call.
	* @param user The PreferencesEdit object to commit.
	*/
	public void cancel(PreferencesEdit edit);

	/**
	* Remove this PreferencesEdit - it must be locked from edit().
	* The PreferencesEdit is disabled, and not to be used after this call.
	* @param user The PreferencesEdit object to remove.
	*/
	public void remove(PreferencesEdit edit);

}	// PreferencesService

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/preference/PreferencesService.java,v 1.1 2005/05/12 15:45:37 ggolden.umich.edu Exp $
*
**********************************************************************************/
