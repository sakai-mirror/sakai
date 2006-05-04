/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/content/DbContentService.java,v 1.19 2005/01/06 19:52:03 ggolden.umich.edu Exp $
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
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.Stack;

import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.service.framework.sql.SqlReader;
import org.sakaiproject.service.framework.sql.SqlService;
import org.sakaiproject.service.legacy.content.ContentCollection;
import org.sakaiproject.service.legacy.content.ContentCollectionEdit;
import org.sakaiproject.service.legacy.content.ContentResource;
import org.sakaiproject.service.legacy.content.ContentResourceEdit;
import org.sakaiproject.service.legacy.resource.ResourceProperties;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.service.legacy.time.cover.TimeService;
import org.sakaiproject.util.Xml;
import org.sakaiproject.util.storage.BaseDbSingleStorage;
import org.sakaiproject.util.storage.StorageUser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
* <p>DbContentService is an extension of the BaseContentService with a database implementation.</p>
* <p>The sql scripts in src/sql/chef_content.sql must be run on the database.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.19 $
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

	/** The extra field(s) to write to the database - collections. */
	protected static final String[] COLLECTION_FIELDS = { "IN_COLLECTION" };

	/** The extra field(s) to write to the database - resources - when we are doing bodys in files. */
	protected static final String[] RESOURCE_FIELDS_FILE = { "IN_COLLECTION", "FILE_PATH" };

	/** The extra field(s) to write to the database - resources - when we are doing bodys the db. */
	protected static final String[] RESOURCE_FIELDS = { "IN_COLLECTION" };

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

	/** Set if we are to run the to-file conversion. */
	protected boolean m_convertToFile = false;

	/**
	 * Configuration: run the to-file conversion.
	 * @param value The conversion desired value.
	 */
	public void setConvertToFile(String value)
	{
		m_convertToFile = new Boolean(value).booleanValue();
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

			// convert?
			if (m_convertToFile)
			{
				m_convertToFile = false;
				convertToFile();
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
		return new DbStorage(new CollectionStorageUser(), new ResourceStorageUser(), (m_bodyPath != null));

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
		public DbStorage(StorageUser collectionUser, StorageUser resourceUser, boolean bodyInFile)
		{
			// build the collection store - a single level store
			m_collectionStore = new BaseDbSingleStorage(
					m_collectionTableName, "COLLECTION_ID", COLLECTION_FIELDS, m_locksInDb, "collection", collectionUser, m_sqlService);	

			// build the resources store - a single level store
			m_resourceStore = new BaseDbSingleStorage(
					m_resourceTableName, "RESOURCE_ID", (bodyInFile ? RESOURCE_FIELDS_FILE : RESOURCE_FIELDS), m_locksInDb, "resource", resourceUser, m_sqlService);	

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
		{
			return (ContentResourceEdit) m_resourceStore.putResource(id, null);
		}

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
				m_logger.warn(this + ": failed to read resource: " + resource.getId() + " len: " + ((BaseResourceEdit)resource).m_contentLength + " : " + t);
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
				m_logger.warn(this + ": failed to read resource: " + resource.getId() + " len: " + ((BaseResourceEdit)resource).m_contentLength + " : " + t);
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
		protected boolean putResourceBodyFilesystem(ContentResourceEdit resource, byte[] body)
		{
			if ((body == null) || (body.length == 0)) return false;

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
				m_logger.warn(this + ": failed to write resource: " + resource.getId() + " : " + t);
				return false;
			}
			
			return true;
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
		return m_bodyPath + ((BaseResourceEdit) resource).m_filePath;
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

	/**
	 * Create a file system body binary for any content_resource record that has a null file_path.
	 */
	protected void convertToFile()
	{
		m_logger.info(this + ".convertToFile");

		try
		{
			// get a connection for the updates
			final Connection connection = m_sqlService.borrowConnection();
			boolean wasCommit = connection.getAutoCommit();
			connection.setAutoCommit(false);

			// get a connection for reading binary
			final Connection sourceConnection = m_sqlService.borrowConnection();

			final Counter count = new Counter();

			// read content_resource records that have null file path
			String sql = "select RESOURCE_ID, XML from CONTENT_RESOURCE where FILE_PATH IS NULL";
			m_sqlService.dbRead(sql, null,
				new SqlReader()
				{
					public Object readSqlResultRecord(ResultSet result)
					{
						String id = null;
						try
						{
							// create the Resource from the db xml
							id = result.getString(1);
							String xml = result.getString(2);
							if (xml == null)
							{
								m_logger.warn(this + ".convertToFile: null xml : " + id);
								return null;
							}
							
							// read the xml
							Document doc =  Xml.readDocumentFromString(xml);
							if (doc == null)
							{
								m_logger.warn(this + ".convertToFile: null xml doc : " + id);
								return null;
							}

							// verify the root element
							Element root = doc.getDocumentElement();
							if (!root.getTagName().equals("resource"))
							{
								m_logger.warn(this + ".convertToFile: XML root element not resource: " + root.getTagName());
								return null;
							}
							BaseResourceEdit edit = new BaseResourceEdit(root);

							// zero length?
							if (edit.getContentLength() == 0)
							{
								m_logger.warn(this + ".convertToFile: zero length body : " + id);
								return null;
							}

							// is it there?
							String sql = "select RESOURCE_ID from CONTENT_RESOURCE_BODY_BINARY where (RESOURCE_ID = ?)";
							Object[] fields = new Object[1];
							fields[0] = id;
							List found = m_sqlService.dbRead(sourceConnection, sql, fields, null);
							if ((found == null) || (found.size() == 0))
							{
								// not found
								m_logger.warn(this + ".convertToFile: body not found in source : " + id);
								return null;
							}
							
							// get the creation date (or modified date, or now)
							Time created = null;
							try
							{
								created = edit.getProperties().getTimeProperty(ResourceProperties.PROP_CREATION_DATE);
							}
							catch (Exception any)
							{
								try
								{
									created = edit.getProperties().getTimeProperty(ResourceProperties.PROP_MODIFIED_DATE);
								}
								catch (Exception e)
								{
									created = TimeService.newTime();
								}
							}
							
							// form the file name
							edit.setFilePath(created);
							
							// read the body from the source
							sql = "select BODY from CONTENT_RESOURCE_BODY_BINARY where (RESOURCE_ID = ?)";
							byte[] body = new byte[edit.m_contentLength];
							m_sqlService.dbReadBinary(sourceConnection, sql, fields, body);

							// write the body to the file
							boolean ok = ((DbStorage) m_storage).putResourceBodyFilesystem(edit, body);
							if (!ok)
							{
								m_logger.warn(this + ".convertToFile: body file failure : " + id + " file: " + edit.m_filePath);
								return null;
							}
							
							// regenerate the xml, now with file path set
							doc = Xml.createDocument();
							edit.toXml(doc, new Stack());
							xml = Xml.writeDocumentToString(doc);

							// update the record
							sql = "update CONTENT_RESOURCE set FILE_PATH = ?, XML = ? where RESOURCE_ID = ?";
							fields = new Object[3];
							fields[0] = edit.m_filePath;
							fields[1] = xml;
							fields[2] = id;
							m_sqlService.dbWrite(connection, sql, fields);

							// m_logger.info(" ** converted: " + id + " size: " + edit.m_contentLength);
							count.value++;
							if ((count.value % 1000) == 0)
							{
								connection.commit();
								m_logger.info(" ** converted: " + count.value);
							}

							return null;
						}
						catch (Throwable e)
						{
							m_logger.info(" ** exception converting : " + id + " : ", e);
							return null;
						}
					}
				} );

			connection.commit();
			
			m_logger.info(this + ".convertToFile: converted resources: " + count.value);

			m_sqlService.returnConnection(sourceConnection);

			connection.setAutoCommit(wasCommit);
			m_sqlService.returnConnection(connection);
		}
		catch (Throwable t)
		{
			m_logger.warn(this + ".convertToFile: failed: " + t);		
		}

		m_logger.info(this + ".convertToFile: done");
	}

	/**
	 * <p>Counter is is a counter that can be marked final.</p>
	 */
	public class Counter
	{
		public int value = 0;
	}

}	// DbCachedContentService

/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/content/DbContentService.java,v 1.19 2005/01/06 19:52:03 ggolden.umich.edu Exp $
*
**********************************************************************************/