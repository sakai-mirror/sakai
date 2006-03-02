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
import java.util.Map;

import org.sakaiproject.service.legacy.entity.Entity;
import org.sakaiproject.service.legacy.entity.EntityProducer;
import org.sakaiproject.service.legacy.entity.Reference;

import uk.ac.cam.caret.sakai.rwiki.service.api.dao.ObjectProxy;
import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiCurrentObject;
import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiHistoryObject;
import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiObject;
import uk.ac.cam.caret.sakai.rwiki.service.api.model.RWikiPermissions;
import uk.ac.cam.caret.sakai.rwiki.service.exception.PermissionException;
import uk.ac.cam.caret.sakai.rwiki.service.exception.VersionException;

//FIXME: Service

public interface RWikiObjectService extends EntityProducer {

	
	/** This string can be used to find the service in the service manager. */
	static final String SERVICE_NAME = RWikiObjectService.class.getName();

	/** This string starts the references to resources in this service. */
	static final String REFERENCE_ROOT = Entity.SEPARATOR + "wiki";
	
	/** This string starts the references to resources in this service. */
	static final String REFERENCE_LABEL =  "wiki";
	
	/** Name of the event when creating a resource. */
	public static final String EVENT_RESOURCE_ADD = "wiki.new";

	/** Name of the event when reading a resource. */
	public static final String EVENT_RESOURCE_READ = "wiki.read";

	/** Name of the event when writing a resource. */
	public static final String EVENT_RESOURCE_WRITE = "wiki.revise";

	/** Name of the event when removing a resource. */
	public static final String EVENT_RESOURCE_REMOVE = "wiki.delete";

	
	/**
	 * Gets the current object
	 * 
	 * @param name
	 * @param realm
	 * @return
	 * @throws PermissionException
	 */
	RWikiCurrentObject getRWikiObject(String name, String realm)
			throws PermissionException;
    /**
     * Gets the current object using a named template if it does not exist
     * 
     * @param name
     * @param realm the page space the page is in, used to localise and globalise the name
     * @param templateName
     * @return
     * @throws PermissionException
     */
    RWikiCurrentObject getRWikiObject(String name, String realm, RWikiObject ignore,  String templateName)
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
	 * @param realm
	 * @return
	 * @throws PermissionException
	 */
	List search(String criteria, String realm)
			throws PermissionException;

	
	/**
	 * Update the named page, with permissions
	 * 
	 * @param name
	 * @param realm
	 * @param version
	 * @param content
	 * @param permissions
	 * @throws PermissionException
	 * @throws VersionException
	 */
	void update(String name, String realm, Date version,
			String content, RWikiPermissions permissions)
			throws PermissionException, VersionException;

	/**
	 * Update the name page, no permissions
	 * 
	 * @param name
	 * @param realm
	 * @param version
	 * @param content
	 * @throws PermissionException
	 * @throws VersionException
	 */
	void update(String name, String realm, Date version,
			String content) throws PermissionException, VersionException;

    
	/**
	 * Update the name page's permissions
	 * 
	 * @param name
	 * @param realm
	 * @param version
	 * @param permissions
	 * @throws PermissionException
	 * @throws VersionException
	 */
	void update(String name, String realm, Date version,
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
	 * @param realm
	 * @return a list containing RWikiCurrentObjects
	 */
	List findChangedSince(Date since,  String realm);

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
	 * @param realm
	 * @param version
	 * @param revision
	 */
	void revert(String name,  String realm, Date version,
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
	/**
	 * Finds the history objects sorted in reverse order
	 * @param rwo
	 * @return
	 */
	List findRWikiHistoryObjectsInReverse(RWikiObject rwo);
    
    /**
     * get list of subpages of the supplied page. The list will be alphabetiallcy sorted
     * @param globalParentPageName is the page on which we want to find sub pages. THIS IS A GLOBAL NAME. DONT CONFUSE WITH A LOCAL NAME
     * @return a list of pages sorted by name alphabetically.
     */
    List findRWikiSubPages(String globalParentPageName);
    
    /**
     * Updates and creates a new comment on the page
     * 
     * @param name
     * @param realm
     * @param version
     * @param content
     * @throws PermissionException
     * @throws VersionException
     */
    void updateNewComment(String name, String realm, Date version,
            String content) throws PermissionException, VersionException;
	/**
	 * Create a list proxy based on the List and Object Proxy
	 * @param commentsList
	 * @param lop
	 * @return
	 */
	List createListProxy(List commentsList, ObjectProxy lop);
	/**
	 * Creates a new rwiki Current Object according to the implementation
	 * @return
	 */
	RWikiObject createNewRWikiCurrentObject();
	/**
	 * Creates a new RWiki Permissions Bean
	 * @return
	 */
	RWikiPermissions createNewRWikiPermissionsImpl();

	/**
	 * fetches the entity based on the RWikiObject
	 * @param rwo
	 * @return
	 */
	Entity getEntity(RWikiObject rwo);
	
	/**
	 * Fetches the Reference Object from the Entity manager based on the RWikiObject
	 * @param rwo
	 * @return
	 */
	Reference getReference(RWikiObject rwo );
	/**
	 * A Map containing EntityHandlers for the Service, Each entity handler handles a subtype
	 * @return
	 */
	Map getHandlers();
	/**
	 * Find all the changes under this point and under since the time specified
	 * @param time the time after which to consider changes
	 * @param basepath the base path
	 * @return a list of RWikiCurrentObjects
	 */
	List findAllChangedSince(Date time,  String basepath);

	 /**
	 * Check for read permission
	 * 
	 * @param rwo
	 * @return
	 */
	boolean checkRead(RWikiObject rwo);

	/**
	 * check for update permission
	 * 
	 * @param rwo
	 * @return
	 */
	boolean checkUpdate(RWikiObject rwo);

	/**
	 * check for admin permission
	 * 
	 * @param rwo
	 * @return
	 */
	boolean checkAdmin(RWikiObject rwo);
	
	/**
	 * Find all pages in the database just reture
	 * @return
	 */
	List findAllPageNames();
    
}
