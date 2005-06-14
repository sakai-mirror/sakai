/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/realm/XmlFileRealmService.java,v 1.7 2004/06/22 03:13:55 ggolden Exp $
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
package org.sakaiproject.component.legacy.realm;

// import
import java.util.List;

import org.sakaiproject.service.legacy.realm.Realm;
import org.sakaiproject.service.legacy.realm.RealmEdit;
import org.sakaiproject.util.storage.BaseXmlFileStorage;
import org.sakaiproject.util.storage.StorageUser;

/**
* <p>XmlFileRealmService is an extension of the BaseRealmService  with a in-memory xml file backed up
* storage.  The full set of realms are read in at startup, kept in memory, and
* written out at shutdown.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.7 $
*/
public class XmlFileRealmService
	extends BaseRealmService
{
	/** A full path and file name to the storage file. */
	protected String m_storagePath = "db/realm.xml";

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
		try
		{
			super.init();

			m_logger.info(this +".init(): storage path: " + m_storagePath);

			// we don't need the cache, since we hold everything in memory already
			m_cache.disable();
		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}

	} // init

	/*******************************************************************************
	* BaseRealmService extensions
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
	* Covers for the BaseXmlFileStorage, providing Realm and RealmEdit parameters
	*/
	protected class XmlFileStorage
		extends BaseXmlFileStorage
		implements Storage
	{
		/**
		* Construct.
		* @param user The StorageUser class to call back for creation of Resource and Edit objects.
		*/
		public XmlFileStorage(StorageUser user)
		{
			super(m_storagePath, "realms", null, "realm", user);
	
		}	// XmlFileStorage

		public boolean check(String id) { return super.checkResource(null, id); }

		public Realm get(String id) { return (Realm) super.getResource(null, id); }

		public List getAll() { return super.getAllResources(null); }

		public RealmEdit put(String id) { return (RealmEdit) super.putResource(null, id, null); }

		public RealmEdit edit(String id) { return (RealmEdit) super.editResource(null, id); }

		public void commit(RealmEdit realm) { super.commitResource(null, realm); }

		public void cancel(RealmEdit realm) { super.cancelResource(null, realm); }

		public void remove(RealmEdit realm) { super.removeResource(null, realm); }

	}   // XmlFileStorage

}   // XmlFileRealmService

/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/realm/XmlFileRealmService.java,v 1.7 2004/06/22 03:13:55 ggolden Exp $
*
**********************************************************************************/
