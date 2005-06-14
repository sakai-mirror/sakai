/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/chat/ChatDelivery.java,v 1.10 2004/09/30 20:21:41 ggolden.umich.edu Exp $
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
package org.sakaiproject.tool.chat;

// imports
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.framework.log.cover.Log;
import org.sakaiproject.service.framework.session.UsageSession;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.legacy.chat.ChatMessage;
import org.sakaiproject.service.legacy.chat.cover.ChatService;
import org.sakaiproject.service.legacy.message.MessageChannel;
import org.sakaiproject.service.legacy.resource.Reference;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.sakaiproject.util.StringUtil;
import org.sakaiproject.util.Validator;
import org.sakaiproject.util.delivery.BaseDelivery;
import org.sakaiproject.util.delivery.RefreshDelivery;

/**
* <p>ChatDelivery is a Delivery that causes a chat message to be appended to a table of chat messages 
*    in the HTML element identified by the address and elementID.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.10 $
*/

// maybe this could be generalized to append anything to any HTML element with an "append" function,
// rather than specifying chat messages appended to chat transcripts?

public class ChatDelivery
	extends BaseDelivery
{
	/** The messageId. */
	protected String m_messageId = null;
	
	protected boolean m_beepOnDelivery = false;

	/**
	* Construct.
	* @param address The address.
	* @param elementId The elementId.
	*/
	public ChatDelivery(String address, String elementId, String messageID, boolean beepOnDelivery)
	{
		super(address, elementId);
		m_messageId = messageID;
		m_beepOnDelivery = beepOnDelivery;
		
	}	// ChatDelivery

	/**
	* Set the Message Id that this delivery is in reference to.
	* @param id The message Id that this delivery is in reference to.
	*/
	public void setMessage(String id)
	{
		m_messageId = id;

	}	// setMessage

	/**
	* Access the Message Id that this delivery is in reference to.
	* @return The Message Id that this delivery is in reference to.
	*/
	public String getMessage()
	{
		return m_messageId;

	}	// getMessage

	/**
	* Compose a javascript message for delivery to the browser client window.
	* @return The javascript message to send to the browser client window.
	*/
	public String compose()
	{
		if (Log.getLogger("chef").isDebugEnabled())
			Log.debug("chef", this + ".compose() element: " + m_elementId + ", message: " + m_messageId);

		// generate a string of JavaScript commands to update the message log

		Reference ref = new Reference(m_messageId);
		ChatMessage msg = (ChatMessage) ref.getResource();
		User sender = msg.getHeader().getFrom();
		User myself = UserDirectoryService.getCurrentUser();

		MessageChannel channel = null;
		try
		{
			String channelRef = ChatService.channelReference(ref.getContext(), ref.getContainer());
			channel = ChatService.getChannel(channelRef);	
		}
		catch (PermissionException e) {}
		catch (IdUnusedException e) {}
				
		String browserID = UsageSessionService.getSession().getBrowserId();
		
		String jsCommand;
		
		// if browser supports appendMessage function, we have to return 
		// sender, date, time, body and message number (in that order) 
		// as parameters to invocation of appendMessage function in HTML element 
		// identified by m_elementId.  message number is for debugging only.
		// otherwise return reload request
		//
		// also, if the user is allowed to delete the message, they need a reload request
		// so that they will see a delete icon for the message

		if(browserID.equals(UsageSession.UNKNOWN) || channel == null || channel.allowRemoveMessage(msg))
		{
			jsCommand = RefreshDelivery.composeRefresh(m_elementId);
		}
		else
		{
			// %%% JANDERSE - Message bodies are now formatted text, even though
			// chat doesn't yet allow formatted text entry.
			String msgbody = Validator.escapeJsQuoted(Validator.escapeHtmlFormattedText(msg.getBody()));
			jsCommand = ifExistsScript("parent." + m_elementId) + "\n"
						+ "{\n"
						+ "\tparent." + m_elementId 
						+ ".appendMessage(\'" + sender.getDisplayName()
						+ "\', \'" + sender.getId()
						+ "\', \'" + msg.getHeader().getDate().toStringLocalDate()
						+ "\', \'" + msg.getHeader().getDate().toStringLocalTime()
						+ "\', \'" + msgbody
						+ "\');\n}\n";
		}
		
		if(m_beepOnDelivery && sender.compareTo(myself) != 0)
		{
			jsCommand += "beep = true;\n";
		}
		
		return jsCommand;

	}	// compose

	/**
	* Display.
	*/
	public String toString()
	{
		return super.toString() + " : " + m_messageId;

	}	// toString

	/**
	* Are these the same?
	* @return true if obj is the same Delivery as this one.
	*/
	public boolean equals(Object obj)
	{
		if (!super.equals(obj)) return false;

		ChatDelivery cob = (ChatDelivery) obj;
		if (StringUtil.different(cob.getMessage(), getMessage())) return false;
		
		return true;

	}	// equals

}   // ChatDelivery

/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/tool/chat/ChatDelivery.java,v 1.10 2004/09/30 20:21:41 ggolden.umich.edu Exp $
*
**********************************************************************************/
