/**********************************************************************************
*
* $Header: /cvs/sakai/faces/src/java/org/sakaiproject/jsf/custom/MessageSaver.java,v 1.7 2004/09/30 20:20:29 ggolden.umich.edu Exp $
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

package org.sakaiproject.jsf.custom;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.sakaiproject.service.framework.portal.cover.PortalService;
import org.sakaiproject.service.framework.session.SessionState;

/**
 * <p>MessageSaver is utility methods to save JavaServer Faces messages from one request in the tool state
 * for the next time.</p>
 * 
 * @author University of Michigan, CHEF Software Development Team
 * @version $Revision: 1.7 $
 */
public class MessageSaver
{
	/**
	 * Restore saved messages for the current tool.
	 * @param context The current faces context.
	 */
	public static void restoreMessages(FacesContext context)
	{
		SessionState state = PortalService.getCurrentToolState();

		// use this key in the state for finding messages
		String attrName = getAttributeName(state);

		// get messages
		List msgs = (List) state.getAttribute(attrName);
		if (msgs != null)
		{
			// process each one - add it to this context's message set
			for (Iterator iMessages = msgs.iterator(); iMessages.hasNext();)
			{
				FacesMessage msg = (FacesMessage) iMessages.next();
				// Note: attributed to no specific tree element
				context.addMessage(null, msg);
			}

			state.removeAttribute(attrName);
		}
	}

	/**
	 * Save current messages for later restoration in the current tool.
	 * @param context The current faces context.
	 */
	public static void saveMessages(FacesContext context)
	{
		SessionState state = PortalService.getCurrentToolState();

		// collect the messages from the context for restoration on the next rendering
		List msgs = new Vector();
		for (Iterator iMessages = context.getMessages(); iMessages.hasNext();)
		{
			FacesMessage msg = (FacesMessage) iMessages.next();
			msgs.add(msg);
		}

		// store the messages for this mode to find
		state.setAttribute(getAttributeName(state), msgs);
	}

	/**
	 * Compute the attribute name in SessionState where the messages are stored.
		 * @param state The Tool SessionState.
	 * @return The attribute name in SessionState where the messages are stored.
	 */
	protected static String getAttributeName(SessionState state)
	{
		// get the tool mode, with default
		String mode = null;

		if (state != null)
		{
			mode = (String) state.getAttribute("mode");
		}

		if (mode == null)
		{
			mode = "main";
		}

		// use this key in the state for finding messages
		String attrName = "org.sakaiproject.jsf.messages." + mode;

		return attrName;
	}
}

/**********************************************************************************
*
* $Header: /cvs/sakai/faces/src/java/org/sakaiproject/jsf/custom/MessageSaver.java,v 1.7 2004/09/30 20:20:29 ggolden.umich.edu Exp $
*
**********************************************************************************/
