/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/digest/XmlFileDigestService.java,v 1.8 2004/09/16 20:11:47 ggolden.umich.edu Exp $
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
package org.sakaiproject.component.legacy.digest;

// import
import java.util.List;

import org.sakaiproject.service.legacy.digest.Digest;
import org.sakaiproject.service.legacy.digest.DigestEdit;
import org.sakaiproject.util.storage.BaseXmlFileStorage;
import org.sakaiproject.util.storage.StorageUser;

/**
* <p>XmlFileDigestService is an extension of the BaseDigestService  with a in-memory xml file backed up
* storage.  The full set of digests are read in at startup, kept in memory, and
* written out at shutdown.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.8 $
*/
public class XmlFileDigestService
	extends BaseDigestService
{
	/** A full path and file name to the storage file. */
	protected String m_storagePath = "db/digest.xml";

	/*******************************************************************************
	* Constructors, Dependencies and their setter methods
	*******************************************************************************/

	/**
	 * Configuration: set the storage path
	 * @param path The storage path.
	 */
	public void setStoragePath(String path)
	{
		m_storagePath = path;
	}

	/*******************************************************************************
	* Init and Destroy
	*******************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		super.init();

		try
		{
			m_logger.debug(this +".init(): storage path: " + m_storagePath);
		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}

	} // init

	/*******************************************************************************
	* BaseDigestService extensions
	*******************************************************************************/

	/**
	* Construct a Storage object.
	* @return The new storage object.
	*/
	protected Storage newStorage()
	{
		return new XmlFileStorage(this);

	}   // newStorage

	/*******************************************************************************
	* Storage implementation
	*******************************************************************************/

	/**
	* Covers for the BaseXmlFileStorage, providing Digest and DigestEdit parameters
	*/
	protected class XmlFileStorage
		extends BaseXmlFileStorage
		implements Storage
	{
		/**
		* Construct.
		* @param preferences The StorageUser class to call back for creation of Resource and Edit objects.
		*/
		public XmlFileStorage(StorageUser user)
		{
			super(m_storagePath, "digests", null, "digest", user);
	
		}	// XmlFileStorage

		public boolean check(String id) { return super.checkResource(null, id); }

		public Digest get(String id) { return (Digest) super.getResource(null, id); }

		public List getAll() { return super.getAllResources(null); }

		public DigestEdit put(String id) { return (DigestEdit) super.putResource(null, id, null); }

		public DigestEdit edit(String id) { return (DigestEdit) super.editResource(null, id); }

		public void commit(DigestEdit preferences) { super.commitResource(null, preferences); }

		public void cancel(DigestEdit preferences) { super.cancelResource(null, preferences); }

		public void remove(DigestEdit preferences) { super.removeResource(null, preferences); }

	}   // XmlFileStorage

}   // XmlFileDigestService

/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/digest/XmlFileDigestService.java,v 1.8 2004/09/16 20:11:47 ggolden.umich.edu Exp $
*
**********************************************************************************/
