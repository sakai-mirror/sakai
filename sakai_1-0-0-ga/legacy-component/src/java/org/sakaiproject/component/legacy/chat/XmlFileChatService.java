/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/chat/XmlFileChatService.java,v 1.9 2004/09/03 16:17:20 ggolden.umich.edu Exp $
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
package org.sakaiproject.component.legacy.chat;

// import
import java.util.List;

import org.sakaiproject.service.legacy.message.Message;
import org.sakaiproject.service.legacy.message.MessageChannel;
import org.sakaiproject.service.legacy.message.MessageChannelEdit;
import org.sakaiproject.service.legacy.message.MessageEdit;
import org.sakaiproject.service.legacy.time.Time;
import org.sakaiproject.util.storage.BaseXmlFileStorage;
import org.sakaiproject.util.storage.StorageUser;

/**
* <p>XmlFileChatService fills out the BaseChatService with a in-memory xml file backed up
* storage.  The full set of messages are read in at startup, kept in memory, and
* written out at shutdown.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.9 $
* @see org.chefproject.service.BaseChatService
*/
public class XmlFileChatService
	extends BaseChatService
{
	/** A full path and file name to the storage file. */
	protected String m_storagePath = null;

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
	* BaseMessageService extensions
	*******************************************************************************/

	/**
	* Construct a Storage object.
	* @return The new storage object.
	*/
	protected Storage newStorage()
	{
		return new XmlFileStorage(this);

	}	// newStorage

	/*******************************************************************************
	* Storage implementation
	*******************************************************************************/

	/**
	* Covers for the BaseXmlFileStorage, providing Chat parameters
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
			super(m_storagePath, "chat", "channel", "message", user);
	
		}	// XmlFileStorage

		/** Channels **/
		
		public boolean checkChannel(String ref) { return super.getContainer(ref) != null; }

		public MessageChannel getChannel(String ref) { return (MessageChannel) super.getContainer(ref); }

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
			{ return super.checkResource(channel.getReference(), id); }

		public Message getMessage(MessageChannel channel, String id)
			{ return (Message) super.getResource(channel.getReference(), id); }

		public Message getMessage(String channelRef, String id)
			{ return (Message) super.getResource(channelRef, id); }

		public List getMessages(MessageChannel channel)
			{ return super.getAllResources(channel.getReference()); }

		public MessageEdit putMessage(MessageChannel channel,String id)
			{ return (MessageEdit) super.putResource(channel.getReference(), id, null); }

		public MessageEdit editMessage(MessageChannel channel, String id)
			{ return (MessageEdit) super.editResource(channel.getReference(), id); }

		public void commitMessage(MessageChannel channel, MessageEdit edit)
			{ super.commitResource(channel.getReference(), edit); }

		public void cancelMessage(MessageChannel channel, MessageEdit edit)
			{ super.cancelResource(channel.getReference(), edit); }

		public void removeMessage(MessageChannel channel, MessageEdit edit)
			{ super.removeResource(channel.getReference(), edit); }

		public List getMessages(String channelRef, Time afterDate, int limitedToLatest, String draftsForId)
			{ return super.getResources(channelRef, afterDate, limitedToLatest, draftsForId); }

	}   // XmlFileStorage

}	// XmlFileChatService

/**********************************************************************************
*
* $Header: /cvs/sakai/legacy-component/src/java/org/sakaiproject/component/legacy/chat/XmlFileChatService.java,v 1.9 2004/09/03 16:17:20 ggolden.umich.edu Exp $
*
**********************************************************************************/
