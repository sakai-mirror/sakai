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
package org.sakaiproject.tool.chat;

// imports
import java.util.ResourceBundle;

import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.service.framework.log.cover.Log;
import org.sakaiproject.service.framework.session.UsageSession;
import org.sakaiproject.service.framework.session.cover.UsageSessionService;
import org.sakaiproject.service.legacy.chat.ChatMessage;
import org.sakaiproject.service.legacy.chat.cover.ChatService;
import org.sakaiproject.service.legacy.message.MessageChannel;
import org.sakaiproject.service.legacy.resource.Reference;
import org.sakaiproject.service.legacy.resource.cover.EntityManager;
import org.sakaiproject.service.legacy.user.User;
import org.sakaiproject.service.legacy.user.cover.UserDirectoryService;
import org.sakaiproject.util.courier.BaseDelivery;
import org.sakaiproject.util.java.StringUtil;
import org.sakaiproject.util.web.Web;

/**
* <p>ChatDelivery is a Delivery that causes a chat message to be appended to a table of chat messages 
*    in the HTML element identified by the address and elementID.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/

// maybe this could be generalized to append anything to any HTML element with an "append" function,
// rather than specifying chat messages appended to chat transcripts?

public class ChatDelivery
	extends BaseDelivery
{
	/** Resource bundle using current language locale */
    private static ResourceBundle rb = ResourceBundle.getBundle("chat");
    
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

		Reference ref = EntityManager.newReference(m_messageId);
		ChatMessage msg = (ChatMessage) ref.getEntity();
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
		
		String retval;
		
		// if browser supports appendMessage function, we have to return 
		// sender, date, time, body and message number (in that order) 
		// as parameters to invocation of appendMessage function in HTML element 
		// identified by m_elementId.  message number is for debugging only.
		// otherwise return reload request
		//
		// also, if the user is allowed to delete the message, they need a reload request
		// so that they will see a delete icon for the message

		if (browserID.equals(UsageSession.UNKNOWN) || channel == null /* TODO || channel.allowRemoveMessage(msg) */)
		{
			retval = "try { " + m_elementId + ".location.replace(addAuto(" + m_elementId + ".location));} catch (error) {}";
		}
		else
		{
			String msgbody = Web.escapeJsQuoted(Web.escapeHtmlFormattedText(msg.getBody()));
			
			boolean removeable = false;
			if (channel.allowRemoveMessage(msg)) removeable = true;
			
			retval = "try { " + m_elementId + ".appendMessage('" + sender.getDisplayName()
					+ "', '" + sender.getId()
					+ "', '" + new Boolean(removeable)
					+ "', '" + msg.getHeader().getDate().toStringLocalDate()
					+ "', '" + msg.getHeader().getDate().toStringLocalTime()
					+ "', '" + msgbody
					+ "'); } catch (error) {} ";
		}
		
		if (m_beepOnDelivery && sender.compareTo(myself) != 0)
		{
			retval += "beep = true;";
		}
		
		return retval;

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



