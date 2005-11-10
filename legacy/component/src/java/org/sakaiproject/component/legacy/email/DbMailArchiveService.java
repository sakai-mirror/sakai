/**********************************************************************************
* $URL$
* $Id$
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
package org.sakaiproject.component.legacy.email;

// import
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

import org.sakaiproject.service.framework.sql.SqlReader;
import org.sakaiproject.service.framework.sql.SqlService;
import org.sakaiproject.service.legacy.message.Message;
import org.sakaiproject.service.legacy.message.MessageChannel;
import org.sakaiproject.service.legacy.message.MessageChannelEdit;
import org.sakaiproject.service.legacy.message.MessageEdit;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.util.xml.Xml;
import org.sakaiproject.util.storage.BaseDbDoubleStorage;
import org.sakaiproject.util.storage.StorageUser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
* <p>DbMailArchiveService fills out the BaseMailArchiveService with a database implementation.</p>
* <p>The sql scripts in src/sql/chef_mailarchive.sql must be run on the database.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
* @see org.chefproject.service.BaseMailArchiveService
*/
public class DbMailArchiveService
	extends BaseMailArchiveService
{
	/** The name of the db table holding mail archive channels. */
	protected String m_cTableName = "MAILARCHIVE_CHANNEL";

	/** The name of the db table holding mail archive messages. */
	protected String m_rTableName = "MAILARCHIVE_MESSAGE";

	/** If true, we do our locks in the remote database, otherwise we do them here. */
	protected boolean m_locksInDb = true;

	protected static final String[] FIELDS = { "MESSAGE_DATE", "OWNER", "DRAFT", "PUBVIEW" };

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
	 * Configuration: set the table name for the container.
	 * @param path The table name for the container.
	 */
	public void setContainerTableName(String name)
	{
		m_cTableName = name;
	}

	/**
	 * Configuration: set the table name for the resource.
	 * @param path The table name for the resource.
	 */
	public void setResourceTableName(String name)
	{
		m_rTableName = name;
	}

	/**
	 * Configuration: set the locks-in-db
	 * @param path The storage path.
	 */
	public void setLocksInDb(String value)
	{
		m_locksInDb = new Boolean(value).booleanValue();
	}

	/** Set if we are to run the to-draft/owner conversion. */
	protected boolean m_convertToDraft = false;

	/**
	 * Configuration: run the to-draft/owner conversion
	 * @param value The conversion desired value.
	 */
	public void setConvertDraft(String value)
	{
		m_convertToDraft = new Boolean(value).booleanValue();
	}

	/** Configuration: to run the ddl on init or not. */
	protected boolean m_autoDdl = false;

	/**
	 * Configuration: to run the ddl on init or not.
	 * 
	 * @param value
	 *        the auto ddl value.
	 */
	public void setAutoDdl(String value)
	{
		m_autoDdl = new Boolean(value).booleanValue();
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
			// if we are auto-creating our schema, check and create
			if (m_autoDdl)
			{
				m_sqlService.ddl(this.getClass().getClassLoader(), "sakai_mailarchive");
			}

			super.init();

			m_logger.info(this +".init(): tables: " + m_cTableName + " " + m_rTableName + " locks-in-db: " + m_locksInDb);

			// convert?
			if (m_convertToDraft)
			{
				m_convertToDraft = false;
				convertToDraft();
			}
		}
		catch (Throwable t)
		{
			m_logger.warn(this +".init(): ", t);
		}
	}

	/*******************************************************************************
	* BaseMessageService extensions
	*******************************************************************************/

	/**
	* Construct a Storage object.
	* @return The new storage object.
	*/
	protected Storage newStorage()
	{
		return new DbStorage(this);

	}	// newStorage

	/*******************************************************************************
	* Storage implementation
	*******************************************************************************/

	protected class DbStorage
		extends BaseDbDoubleStorage
		implements Storage
	{
		/**
		* Construct.
		* @param user The StorageUser class to call back for creation of Resource and Edit objects.
		*/
		public DbStorage(StorageUser user)
		{
			super(m_cTableName, "CHANNEL_ID", m_rTableName, "MESSAGE_ID", "CHANNEL_ID",
			"MESSAGE_DATE", "OWNER", "DRAFT", "PUBVIEW", FIELDS, m_locksInDb, "channel", "message", user, m_sqlService);

		}	// DbStorage

		/** Channels **/
		
		public boolean checkChannel(String ref)
			{ return super.getContainer(ref) != null; }

		public MessageChannel getChannel(String ref)
			{ return (MessageChannel) super.getContainer(ref); }

		public List getChannels() { return super.getAllContainers(); }

		public MessageChannelEdit putChannel(String ref)
			{ return (MessageChannelEdit) super.putContainer(ref); }

		public MessageChannelEdit editChannel(String ref)
			{ return (MessageChannelEdit) super.editContainer(ref); }

		public void commitChannel(MessageChannelEdit edit)
			{ super.commitContainer(edit); }

		public void cancelChannel(MessageChannelEdit edit)
			{ super.cancelContainer(edit); }

		public void removeChannel(MessageChannelEdit edit)
			{ super.removeContainer(edit); }

		public List getChannelIdsMatching(String root)
			{ return super.getContainerIdsMatching(root); }

		/** messages **/
		
		public boolean checkMessage(MessageChannel channel, String id)
			{ return super.checkResource(channel, id); }

		public Message getMessage(MessageChannel channel, String id)
			{ return (Message) super.getResource(channel, id); }

		public List getMessages(MessageChannel channel) { return super.getAllResources(channel); }

		public MessageEdit putMessage(MessageChannel channel,String id)
			{ return (MessageEdit) super.putResource(channel, id, null); }

		public MessageEdit editMessage(MessageChannel channel, String id)
			{ return (MessageEdit) super.editResource(channel, id); }

		public void commitMessage(MessageChannel channel, MessageEdit edit)
			{ super.commitResource(channel, edit); }

		public void cancelMessage(MessageChannel channel, MessageEdit edit)
			{ super.cancelResource(channel, edit); }

		public void removeMessage(MessageChannel channel, MessageEdit edit)
			{ super.removeResource(channel, edit); }

		public List getMessages(MessageChannel channel, Time afterDate, int limitedToLatest, String draftsForId, boolean pubViewOnly)
			{ return super.getResources(channel, afterDate, limitedToLatest, draftsForId, pubViewOnly); }

	}   // DbStorage

	/**
	 * fill in the draft and owner db fields
	 */
	protected void convertToDraft()
	{
		m_logger.info(this + ".convertToDraft");

		try
		{
			// get a connection
			final Connection connection = m_sqlService.borrowConnection();
			boolean wasCommit = connection.getAutoCommit();
			connection.setAutoCommit(false);

			// read all message records that need conversion
			String sql = "select CHANNEL_ID, MESSAGE_ID, XML from " + m_rTableName /*+ " where OWNER is null"*/;
			m_sqlService.dbRead(connection, sql, null,
				new SqlReader()
				{
					private int count = 0;
					public Object readSqlResultRecord(ResultSet result)
					{
						try
						{
							// create the Resource from the db xml
							String channelId = result.getString(1);
							String messageId = result.getString(2);
							String xml = result.getString(3);
							
							// read the xml
							Document doc =  Xml.readDocumentFromString(xml);

							// verify the root element
							Element root = doc.getDocumentElement();
							if (!root.getTagName().equals("message"))
							{
								m_logger.warn(this + ".convertToDraft(): XML root element not message: " + root.getTagName());
								return null;
							}
							Message m = new BaseMessageEdit(null, root);

							// pick up the fields
							String owner = m.getHeader().getFrom().getId();
							boolean draft = m.getHeader().getDraft();

							// update
							String update = "update " + m_rTableName + " set OWNER = ?, DRAFT = ? where CHANNEL_ID = ? and MESSAGE_ID = ?";
							Object fields[] = new Object[4];
							fields[0] = owner;
							fields[1] = (draft ? "1" : "0");
							fields[2] = channelId;
							fields[3] = messageId;
							boolean ok = m_sqlService.dbWrite(connection, update, fields);

							if (!ok)
								m_logger.info(this + ".convertToDraft: channel: " + channelId + " message: " + messageId + " owner: " + owner + " draft: " + draft + " ok: " + ok);

							count++;
							if (count % 100 == 0)
							{
								m_logger.debug(this + ".convertToDraft: " + count);
							}
							return null;
						}
						catch (Throwable ignore) { return null;}
					}
				} );

			connection.commit();
			connection.setAutoCommit(wasCommit);
			m_sqlService.returnConnection(connection);
		}
		catch (Throwable t)
		{
			m_logger.warn(this + ".convertToDraft: failed: " + t);		
		}

		m_logger.info(this + ".convertToDraft: done");
	}

}	// DbCachedMailArchiveService



