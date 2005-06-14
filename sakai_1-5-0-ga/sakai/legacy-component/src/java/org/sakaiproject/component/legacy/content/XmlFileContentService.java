/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/content/XmlFileContentService.java,v 1.9 2004/09/16 03:49:36 ggolden.umich.edu Exp $
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
package org.sakaiproject.component.legacy.content;

// import
import java.io.InputStream;
import java.util.List;
import java.util.Vector;

import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.service.legacy.content.ContentCollection;
import org.sakaiproject.service.legacy.content.ContentCollectionEdit;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.content.ContentResourceEdit;
import org.sakaiproject.service.legacy.resource.Resource;
import org.sakaiproject.util.StringUtil;
import org.sakaiproject.util.storage.BaseXmlFileStorage;
import org.sakaiproject.util.storage.StorageUser;

/**
* <p>XmlFileContentService is an extension of the BaseContentService  with a in-memory xml file backed up
* storage.  The full set of resources are read in at startup, kept in memory, and
* written out at shutdown.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.9 $
*/
public class XmlFileContentService extends BaseContentService
{
	/** A full path and file name to the storage file. */
	protected String m_storagePath = "db/content.xml";
	protected String m_storagePathR = "db/content_r.xml";
	protected String m_storagePathC = "db/content_c.xml";

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

	/**
	 * Configuration: set the storage path for Resources
	 * @param path The storage path.
	 */
	public void setStoragePathR(String path)
	{
		m_storagePathR = path;
	}

	/**
	 * Configuration: set the storage path for Collections
	 * @param path The storage path.
	 */
	public void setStoragePathC(String path)
	{
		m_storagePathC = path;
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

			m_logger.debug(
				this +".init(): storage paths: " + m_storagePath + " " + m_storagePathC + " " + m_storagePathR);

			// we don't need the cache, since we hold everything in memory already
			if (m_cache != null)
			{
				m_cache.disable();
			} 
		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}

	} // init

	/*******************************************************************************
	* BaseContentService extensions
	*******************************************************************************/

	/**
	* Construct a Storage object.
	* @return The new storage object.
	*/
	protected Storage newStorage()
	{
		return new XmlFileStorage(new CollectionStorageUser(), new ResourceStorageUser());

	} // newStorage

	/*******************************************************************************
	* Storage implementation
	*******************************************************************************/

	protected class XmlFileStorage implements Storage
	{
		/** A storage for collections. */
		protected BaseXmlFileStorage m_collectionStore = null;

		/** A storage for resources. */
		protected BaseXmlFileStorage m_resourceStore = null;

		/**
		* Construct.
		* @param collectionUser The StorageUser class to call back for creation of collection objects.
		* @param resourceUser The StorageUser class to call back for creation of resource objects.
		*/
		public XmlFileStorage(StorageUser collectionUser, StorageUser resourceUser)
		{
			//%%% use the same file?  close must not re-write...
			// build the collection store - a single level store
			m_collectionStore = new BaseXmlFileStorage(m_storagePathC, "content", null, "collection", collectionUser);

			// build the resources store - a single level store
			m_resourceStore = new BaseXmlFileStorage(m_storagePathR, "content", null, "resource", resourceUser);

		} // XmlFileStorage

		/**
		* Open and be ready to read / write.
		*/
		public void open()
		{
			m_collectionStore.open();
			m_resourceStore.open();

		} // open

		/**
		* Close.
		*/
		public void close()
		{
			m_collectionStore.close();
			m_resourceStore.close();

		} // open

		/** Collections **/

		public boolean checkCollection(String id)
		{
			return m_collectionStore.checkResource(null, id);
		}

		public ContentCollection getCollection(String id)
		{
			return (ContentCollection) m_collectionStore.getResource(null, id);
		}

		/**
		* Get a list of all getCollections within a collection.
		*/
		public List getCollections(ContentCollection collection)
		{
			List rv = new Vector();

			// limit to those whose reference path (based on id) matches the collection id
			String target = collection.getId();

			List all = m_collectionStore.getAllResources(null);
			for (int i = 0; i < all.size(); i++)
			{
				Resource res = (Resource) all.get(i);
				if (StringUtil.referencePath(res.getId()).equals(target))
				{
					rv.add(res);
				}
			}
			return rv;

		} // getCollections

		public ContentCollectionEdit putCollection(String id)
		{
			return (ContentCollectionEdit) m_collectionStore.putResource(null, id, null);
		}

		public ContentCollectionEdit editCollection(String id)
		{
			return (ContentCollectionEdit) m_collectionStore.editResource(null, id);
		}

		public void commitCollection(ContentCollectionEdit edit)
		{
			m_collectionStore.commitResource(null, edit);
		}

		public void cancelCollection(ContentCollectionEdit edit)
		{
			m_collectionStore.cancelResource(null, edit);
		}

		public void removeCollection(ContentCollectionEdit edit)
		{
			m_collectionStore.removeResource(null, edit);
		}

		/** Resources **/

		public boolean checkResource(String id)
		{
			return m_resourceStore.checkResource(null, id);
		}

		public ContentResource getResource(String id)
		{
			return (ContentResource) m_resourceStore.getResource(null, id);
		}

		public List getResources(ContentCollection collection)
		{
			List rv = new Vector();

			// limit to those whose reference path (based on id) matches the collection id
			String target = collection.getId();

			List all = m_resourceStore.getAllResources(null);
			for (int i = 0; i < all.size(); i++)
			{
				Resource res = (Resource) all.get(i);
				if (StringUtil.referencePath(res.getId()).equals(target))
				{
					rv.add(res);
				}
			}
			return rv;

		} // getResources

		public ContentResourceEdit putResource(String id)
		{
			return (ContentResourceEdit) m_resourceStore.putResource(null, id, null);
		}

		public ContentResourceEdit editResource(String id)
		{
			return (ContentResourceEdit) m_resourceStore.editResource(null, id);
		}

		public void commitResource(ContentResourceEdit edit)
		{
			m_resourceStore.commitResource(null, edit);
		}

		public void cancelResource(ContentResourceEdit edit)
		{
			m_resourceStore.cancelResource(null, edit);
		}

		public void removeResource(ContentResourceEdit edit)
		{
			m_resourceStore.removeResource(null, edit);
		}

		public byte[] getResourceBody(ContentResource resource)
		{
			return null;
		}

		// the body is already in the resource for this version of storage
		public InputStream streamResourceBody(ContentResource resource)  throws ServerOverloadException
		{
			return null;
		}


	} // XmlFileStorage

} // XmlFileContentService

/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/content/XmlFileContentService.java,v 1.9 2004/09/16 03:49:36 ggolden.umich.edu Exp $
*
**********************************************************************************/
