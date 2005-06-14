/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/content/DbContentService.java,v 1.13 2004/10/07 08:08:26 janderse.umich.edu Exp $
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.service.framework.sql.SqlService;
import org.sakaiproject.service.legacy.content.ContentCollection;
import org.sakaiproject.service.legacy.content.ContentCollectionEdit;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.content.ContentResourceEdit;
import org.sakaiproject.service.legacy.resource.ResourceProperties;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.util.storage.BaseDbSingleStorage;
import org.sakaiproject.util.storage.StorageUser;

/**
* <p>DbContentService is an extension of the BaseContentService with a database implementation.</p>
* <p>The sql scripts in src/sql/chef_content.sql must be run on the database.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.13 $
* @see org.chefproject.service.component.BaseContentService
*/
public class DbContentService
	extends BaseContentService
{
	/** Table name for collections. */
	protected String m_collectionTableName = "CONTENT_COLLECTION";
	
	/** Table name for resources. */
	protected String m_resourceTableName = "CONTENT_RESOURCE";

	/** Table name for resources. */
	protected String m_resourceBodyTableName = "CONTENT_RESOURCE_BODY_BINARY";

	/** If true, we do our locks in the remote database, otherwise we do them here. */
	protected boolean m_locksInDb = true;

	/** The extra field(s) to write to the database. */
	protected static final String[] FIELDS = { "IN_COLLECTION" };

	/** Optional path to external file system file store for body binary. */
	protected String m_bodyPath = null;

	/*******************************************************************************
	* Constructors, Dependencies and their setter methods
	*******************************************************************************/

	/** Dependency: SqlService */
	protected SqlService m_sqlService = null;

	/**
	 * Dependency: SqlService.
	 * @param service The SqlService.
	 */
	public void setSqlService(SqlService service)
	{
		m_sqlService = service;
	}

	/**
	 * Configuration: set the table name for collections.
	 * @param path The table name for collections.
	 */
	public void setCollectionTableName(String name)
	{
		m_collectionTableName = name;
	}

	/**
	 * Configuration: set the table name for resources.
	 * @param path The table name for resources.
	 */
	public void setResourceTableName(String name)
	{
		m_resourceTableName = name;
	}

	/**
	 * Configuration: set the table name for resource body.
	 * @param path The table name for resource body.
	 */
	public void setResourceBodyTableName(String name)
	{
		m_resourceBodyTableName = name;
	}

	/**
	 * Configuration: set the locks-in-db
	 * @param value The locks-in-db value.
	 */
	public void setLocksInDb(String value)
	{
		m_locksInDb = new Boolean(value).booleanValue();
	}

	/**
	 * Configuration: set the external file system path for body storage
	 * If set, the resource binary database table will not be used.
	 * @param value The complete path to the root of the external file system storage area for resource body bytes.
	 */
	public void setBodyPath(String value)
	{
		m_bodyPath = value;
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

			// override from config
			m_bodyPath = m_serverConfigurationService.getString("content.filesystem.root", m_bodyPath);
			if ((m_bodyPath != null) && (!m_bodyPath.endsWith("/")))
			{
				m_bodyPath = m_bodyPath + "/";
			}

			m_logger.info(this +".init(): tables: "
				+ m_collectionTableName + " "
				+ m_resourceTableName + " " 
				+ m_resourceBodyTableName
				+ " locks-in-db: " + m_locksInDb
				+ " bodyPath: " + m_bodyPath);
		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}
	}

	/*******************************************************************************
	* BaseContentService extensions
	*******************************************************************************/

	/**
	* Construct a Storage object.
	* @return The new storage object.
	*/
	protected Storage newStorage()
	{
		return new DbStorage(new CollectionStorageUser(), new ResourceStorageUser());

	}	// newStorage

	/*******************************************************************************
	* Storage implementation
	*******************************************************************************/

	protected class DbStorage
		implements Storage
	{
		/** A storage for collections. */
		protected BaseDbSingleStorage m_collectionStore = null;

		/** A storage for resources. */
		protected BaseDbSingleStorage m_resourceStore = null;

		/**
		* Construct.
		* @param collectionUser The StorageUser class to call back for creation of collection objects.
		* @param resourceUser The StorageUser class to call back for creation of resource objects.
		*/
		public DbStorage(StorageUser collectionUser, StorageUser resourceUser)
		{
			// build the collection store - a single level store
			m_collectionStore = new BaseDbSingleStorage(
					m_collectionTableName, "COLLECTION_ID", FIELDS, m_locksInDb, "collection", collectionUser, m_sqlService);	

			// build the resources store - a single level store
			m_resourceStore = new BaseDbSingleStorage(
					m_resourceTableName, "RESOURCE_ID", FIELDS, m_locksInDb, "resource", resourceUser, m_sqlService);	

		}	// DbStorage

		/**
		* Open and be ready to read / write.
		*/
		public void open()
		{
			m_collectionStore.open();
			m_resourceStore.open();

		}	// open

		/**
		* Close.
		*/
		public void close()
		{
			m_collectionStore.close();
			m_resourceStore.close();

		}	// open

		/** Collections **/
		
		public boolean checkCollection(String id)
			{ return m_collectionStore.checkResource(id); }

		public ContentCollection getCollection(String id)
			{ return (ContentCollection) m_collectionStore.getResource(id); }

		/**
		* Get a list of all getCollections within a collection.
		*/
		public List getCollections(ContentCollection collection)
		{
			// limit to those whose reference path (based on id) matches the collection id
			final String target = collection.getId();

			/*
			// read all the records, then filter them to accept only those in this collection
			// Note: this is not desirable, as the read is linear to the database site -ggolden
			List rv = m_collectionStore.getSelectedResources(
					new Filter()
					{
						public boolean accept(Object o)
						{
							// o is a String, the collection id
							return StringUtil.referencePath((String) o).equals(target);
						}
					} );
			*/
			
			// read the records with a where clause to let the database select those in this collection
			List rv = m_collectionStore.getAllResourcesWhere("IN_COLLECTION", target);

			return rv;

		}	// getCollections

		public ContentCollectionEdit putCollection(String id)
			{ return (ContentCollectionEdit) m_collectionStore.putResource(id, null); }

		public ContentCollectionEdit editCollection(String id)
			{ return (ContentCollectionEdit) m_collectionStore.editResource(id); }

		public void commitCollection(ContentCollectionEdit edit)
			{ m_collectionStore.commitResource(edit); }

		public void cancelCollection(ContentCollectionEdit edit)
			{ m_collectionStore.cancelResource(edit); }

		public void removeCollection(ContentCollectionEdit edit)
			{ m_collectionStore.removeResource(edit); }

		/** Resources **/
		
		public boolean checkResource(String id)
			{ return m_resourceStore.checkResource(id); }

		public ContentResource getResource(String id)
			{ return (ContentResource) m_resourceStore.getResource(id); }

		public List getResources(ContentCollection collection)
		{
			// limit to those whose reference path (based on id) matches the collection id
			final String target = collection.getId();

			/*
			// read all the records, then filter them to accept only those in this collection
			// Note: this is not desirable, as the read is linear to the database site -ggolden
			List rv = m_resourceStore.getSelectedResources(
					new Filter()
					{
						public boolean accept(Object o)
						{
							// o is a String, the resource id
							return StringUtil.referencePath((String) o).equals(target);
						}
					} );
			*/

			// read the records with a where clause to let the database select those in this collection
			List rv = m_resourceStore.getAllResourcesWhere("IN_COLLECTION", target);

			return rv;

		}	// getResources

		public ContentResourceEdit putResource(String id)
			{ return (ContentResourceEdit) m_resourceStore.putResource(id, null); }

		public ContentResourceEdit editResource( String id)
			{ return (ContentResourceEdit) m_resourceStore.editResource(id); }

		public void commitResource(ContentResourceEdit edit)
		{
			// keep the body out of the XML
			byte[] body = ((BaseResourceEdit) edit).m_body;
			((BaseResourceEdit) edit).m_body = null;

			m_resourceStore.commitResource(edit);
			
			// update the resource body
			if (body != null)
			{
				// if we have been configured to use an external file system
				if (m_bodyPath != null)
				{
					putResourceBodyFilesystem(edit, body);
				}
				
				// otherwise use the database
				else
				{
					putResourceBodyDb(edit, body);
				}
			}
		}

		public void cancelResource(ContentResourceEdit edit)
		{
			// clear the memory image of the body
			byte[] body = ((BaseResourceEdit) edit).m_body;
			((BaseResourceEdit) edit).m_body = null;

			m_resourceStore.cancelResource(edit);
		}

		public void removeResource(ContentResourceEdit edit)
		{
			// delete the body

			// if we have been configured to use an external file system
			if (m_bodyPath != null)
			{
				delResourceBodyFilesystem(edit);
			}
				
			// otherwise use the database
			else
			{
				delResourceBodyDb(edit);
			}

			// clear the memory image of the body
			byte[] body = ((BaseResourceEdit) edit).m_body;
			((BaseResourceEdit) edit).m_body = null;

			m_resourceStore.removeResource(edit);			
		}

		/**
		* Read the resource's body.
		* @param resource The resource whose body is desired.
		* @return The resources's body content as a byte array.
		*/
		public byte[] getResourceBody(ContentResource resource)
		{
			if (((BaseResourceEdit)resource).m_contentLength <= 0)
			{
				m_logger.warn(this + ".getResourceBody(): non-positive content length: "
					+ ((BaseResourceEdit)resource).m_contentLength
					+ "  id: " + resource.getId());
				return null;
			}

			// if we have been configured to use an external file system
			if (m_bodyPath != null)
			{
				return getResourceBodyFilesystem(resource);
			}
				
			// otherwise use the database
			else
			{
				return getResourceBodyDb(resource);
			}
		}

		/**
		* Read the resource's body from the database.
		* @param resource The resource whose body is desired.
		* @return The resources's body content as a byte array.
		*/
		protected byte[] getResourceBodyDb(ContentResource resource)
		{
			// get the resource from the db
			String sql =
					"select BODY from " + m_resourceBodyTableName
				+	" where ( RESOURCE_ID = ? )";

			Object[] fields = new Object[1];
			fields[0] = resource.getId();

			// create the body to read into
			byte[] body = new byte[((BaseResourceEdit)resource).m_contentLength];
			m_sqlService.dbReadBinary(sql, fields, body);
			
			return body;
		}

		/**
		* Read the resource's body from the external file system.
		* @param resource The resource whose body is desired.
		* @return The resources's body content as a byte array.
		*/
		protected byte[] getResourceBodyFilesystem(ContentResource resource)
		{
			// form the file name
			File file = new File(externalResourceFileName(resource));

			// read the new
			try
			{
				byte[] body = new byte[((BaseResourceEdit)resource).m_contentLength];
				FileInputStream in = new FileInputStream(file);

				in.read(body);
				in.close();

				return body;
			}
			catch (Throwable t)
			{
				m_logger.warn(this + "failed to read resource: " + resource.getId() + " len: " + ((BaseResourceEdit)resource).m_contentLength + " : " + t);
				return null;
			}
		}

		// the body is already in the resource for this version of storage
		public InputStream streamResourceBody(ContentResource resource) throws ServerOverloadException
		{
			if (((BaseResourceEdit)resource).m_contentLength <= 0)
			{
				m_logger.warn(this + ".getResourceBody(): non-positive content length: "
					+ ((BaseResourceEdit)resource).m_contentLength
					+ "  id: " + resource.getId());
				return null;
			}

			// if we have been configured to use an external file system
			if (m_bodyPath != null)
			{
				return streamResourceBodyFilesystem(resource);
			}
				
			// otherwise use the database
			else
			{
				return streamResourceBodyDb(resource);
			}
		}

		protected InputStream streamResourceBodyFilesystem(ContentResource resource)
		{
			// form the file name
			File file = new File(externalResourceFileName(resource));

			// read the new
			try
			{
				FileInputStream in = new FileInputStream(file);
				return in;
			}
			catch (Throwable t)
			{
				m_logger.warn(this + "failed to read resource: " + resource.getId() + " len: " + ((BaseResourceEdit)resource).m_contentLength + " : " + t);
				return null;
			}
		}

		protected InputStream streamResourceBodyDb(ContentResource resource) throws ServerOverloadException
		{
			// get the resource from the db
			String sql =
					"select BODY from " + m_resourceBodyTableName
				+	" where ( RESOURCE_ID = ? )";

			Object[] fields = new Object[1];
			fields[0] = resource.getId();

			// get the stream, set expectations that this could be big
			InputStream in = m_sqlService.dbReadBinary(sql, fields, true);

			return in;
		}

		/**
		* Write the resource body to the database table.
		* @param resource The resource whose body is being written.
		* @param body The body bytes to write.
		*/
		protected void putResourceBodyDb(ContentResourceEdit resource, byte[] body)
		{
			if ((body == null) || (body.length == 0)) return;

			// delete the old
			String statement =
						"delete from " + m_resourceBodyTableName
					+	" where resource_id = ? ";
	
			Object[] fields = new Object[1];
			fields[0] = resource.getId();

			m_sqlService.dbWrite(statement, fields);

			// add the new
			statement =
						"insert into " + m_resourceBodyTableName
					+	" (RESOURCE_ID, BODY)"
					+	" values (? , ? )";

			m_sqlService.dbWriteBinary(statement, fields, body, 0, body.length);
	
			/* %%% BLOB code // read the record's blob and update
			statement =
					"select body from " + m_resourceTableName
				+	" where ( resource_id = '" + Validator.escapeSql(resource.getId()) + "' ) for update";
			Sql.dbReadBlobAndUpdate(statement, ((BaseResource)resource).m_body);*/
		}

		/**
		* Write the resource body to the external file system.
		* The file name is the m_bodyPath with the resource id appended.
		* @param resource The resource whose body is being written.
		* @param body The body bytes to write.
		*/
		protected void putResourceBodyFilesystem(ContentResourceEdit resource, byte[] body)
		{
			if ((body == null) || (body.length == 0)) return;

			// form the file name
			File file = new File(externalResourceFileName(resource));

			// delete the old
			if (file.exists())
			{
				file.delete();
			}

			// add the new
			try
			{
				// make sure all directories are there
				File container = file.getParentFile();
				if (container != null)
				{
					container.mkdirs();
				}

				// write the file
				FileOutputStream out = new FileOutputStream(file);
				out.write(body);
				out.close();
			}
			catch (Throwable t)
			{
				m_logger.warn(this + "failed to write resource: " + resource.getId() + " : " + t);
			}
		}

		/**
		* Delete the resource body from the database table.
		* @param resource The resource whose body is being deleted.
		*/
		protected void delResourceBodyDb(ContentResourceEdit resource)
		{
			// delete the record
			String statement =
						"delete from " + m_resourceBodyTableName
					+	" where resource_id = ?";
	
			Object[] fields = new Object[1];
			fields[0] = resource.getId();

			m_sqlService.dbWrite(statement, fields);
		}

		/**
		* Delete the resource body from the external file system.
		* The file name is the m_bodyPath with the resource id appended.
		* @param resource The resource whose body is being written.
		*/
		protected void delResourceBodyFilesystem(ContentResourceEdit resource)
		{
			// form the file name
			File file = new File(externalResourceFileName(resource));

			// delete
			if (file.exists())
			{
				file.delete();
			}
		}

	}   // DbStorage

	/**
	 * Form the full file path+name used to store the resource body in an external file system.
	 * @param resource The resource.
	 * @return The resource external file name.
	 */
	protected String externalResourceFileName(ContentResource resource)
	{
//		// based on just the resource id
//		return m_bodyPath + resource.getId();
		
		// we need a file name for this that's unique...
		String name = escapeResourceName(resource.getId());

		// based on the creation date
		try
		{
			Time creation = resource.getProperties().getTimeProperty(ResourceProperties.PROP_CREATION_DATE);
			return m_bodyPath + creation.toStringFilePath() + name;
		}
		catch (Exception any)
		{
			// no creation? not a time?
			return m_bodyPath + "trouble/" + name;
		}
	}

	/** We allow these characters to go un-escaped into the file name. */
	static protected final String VALID_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.";

	/**
	 * Return file system safe escaped name, that's also unique if the initial id is unique.	 * Use only the name, not the path part of the id
	 * @param value The id to escape.
	 * @return value escaped.
	 */
	protected String escapeResourceName(String id)
	{
		if (id == null) return null;

		try
		{
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < id.length(); i++)
			{
				char c = id.charAt(i);

				// if not valid, escape
				if (VALID_CHARS.indexOf(c) == -1)
				{
					// the escape character
					buf.append('_');

					// the character value.
					buf.append(Integer.toHexString(c));
				}
				else
				{
					buf.append(c);
				}
			}

			String rv = buf.toString();
			return rv;
		}
		catch (Exception e)
		{
			m_logger.warn(this + ".escapeResourceName: ", e);
			return id;
		}
	}

}	// DbCachedContentService

/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/content/DbContentService.java,v 1.13 2004/10/07 08:08:26 janderse.umich.edu Exp $
*
**********************************************************************************/
