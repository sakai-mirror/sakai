/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/digest/DigestService.java,v 1.4 2004/06/22 03:14:45 ggolden Exp $
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
package org.sakaiproject.service.legacy.digest;

// imports
import java.util.List;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.service.legacy.resource.Resource;

/**
* <p>The DigestService collects sets of messages for different users, and sends them out periodically.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.4 $
*/
public interface DigestService
{
	/** This string can be used to find the service in the service manager. */
	public static final String SERVICE_NAME = DigestService.class.getName();

	/** This string starts the references to resources in this service. */
	public static final String REFERENCE_ROOT = Resource.SEPARATOR + "digest";

	/** Securiy / Event for adding a digest. */
	public static final String SECURE_ADD_DIGEST = "digest.add";

	/** Securiy / Event for updating a digest. */
	public static final String SECURE_EDIT_DIGEST = "digest.upd";

	/** Securiy / Event for removing a digest. */
	public static final String SECURE_REMOVE_DIGEST = "digest.del";

	/**
	* Access a digest associated with this id.
	* @param id The digest id.
	* @return The Digest object.
	* @exception IdUnusedException if there is not digest object with this id.
	*/
	public Digest getDigest(String id)
		throws IdUnusedException;

	/**
	* Access all digest objects.
	* @return A List (Digest) of all defined digests.
	*/
	public List getDigests();

	/**
	* Add a new message to a digest, creating one if needed.
	* This returns right away; the digest will be added as soon as possible.
	* @param message The message to digest.
	*/
	public void digest(DigestMessage message);

	/**
	* Add a new digest with this id.  Must commit(), remove() or cancel() when done.
	* @param id The digest id.
	* @return A new DigestEdit object for editing.
	* @exception IdUsedException if these digest already exist.
	*/
	public DigestEdit add(String id)
		throws IdUsedException;

	/**
	* Get a locked Digest object for editing. May be new.
	* Must commit(), cancel() or remove() when done.
	* @param id The digest id.
	* @return A DigestEdit object for editing.
	* @exception InUseException if the digest object is locked by someone else.
	*/
	public DigestEdit edit(String id)
		throws InUseException;

	/**
	* Commit the changes made to a DigestEdit object, and release the lock.
	* The DigestEdit is disabled, and not to be used after this call.
	* @param user The DigestEdit object to commit.
	*/
	public void commit(DigestEdit edit);

	/**
	* Cancel the changes made to a DigestEdit object, and release the lock.
	* The DigestEdit is disabled, and not to be used after this call.
	* @param user The DigestEdit object to commit.
	*/
	public void cancel(DigestEdit edit);

	/**
	* Remove this DigestEdit - it must be locked from edit().
	* The DigestEdit is disabled, and not to be used after this call.
	* @param user The DigestEdit object to remove.
	*/
	public void remove(DigestEdit edit);

}	// DigestService

/**********************************************************************************
*
* $Header: /cvs/sakai/service/src/java/org/sakaiproject/service/legacy/digest/DigestService.java,v 1.4 2004/06/22 03:14:45 ggolden Exp $
*
**********************************************************************************/
