/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/tools/src/java/org/sakaiproject/tool/chat/ChatObservingCourier.java,v 1.2 2005/05/12 18:53:20 ggolden.umich.edu Exp $
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
import java.util.Observable;
import java.util.ResourceBundle;

import org.sakaiproject.service.framework.courier.cover.CourierService;
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.framework.log.cover.Log;
import org.sakaiproject.util.courier.EventObservingCourier;

/**
* <p>ChatObservingCourier is an ObservingCourier that watches chat events and delivers them with
* extra information, specifically the reference to the message referenced by the event.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public class ChatObservingCourier
	extends EventObservingCourier
{
	/** Resource bundle using current language locale */
    private static ResourceBundle rb = ResourceBundle.getBundle("chat");
    
	protected boolean m_alertEnabled;
	
	/**
	* Construct.
	* @param deliveryId The key identifying the Portal Page Instance.
	* @param elementId The key identifying the element on the Portal Page that would
    * need a courier delivered message when things change.
	* @param The event resource pattern - we watch for only events whose ref
	* start with this.
	*/
	public ChatObservingCourier(String deliveryId, String elementId,
					String resourcePattern, boolean wantsBeeps)
	{
		super(deliveryId, elementId, resourcePattern);
		m_alertEnabled = wantsBeeps;

	}	// ChatObservingCourier

	/**
	* This method is called whenever the observed object is changed. An
	* application calls an <tt>Observable</tt> object's
	* <code>notifyObservers</code> method to have all the object's
	* observers notified of the change.
	*
	* default implementation is to cause the courier service to deliver to the
	* interface controlled by my controller.  Extensions can override.
	*
	* @param   o     the observable object.
	* @param   arg   an argument passed to the <code>notifyObservers</code>
	*                 method.
	*/
	public void update(Observable o, Object arg)
	{
		// ignore changes when not enabled
		if (!getEnabled())
		{
			if (Log.getLogger("chef").isDebugEnabled())
				Log.debug("chef", this + ".update [DISABLED]: " + ((arg == null) ? "null" : arg.toString()));
			return;
		}

		if (!check(arg)) return;

		CourierService.deliver(new ChatDelivery(getDeliveryId(), getElementId(), ((Event) arg).getResource(), m_alertEnabled));

	}	// update

	public void alertEnabled(boolean newVal)
	{
		m_alertEnabled = newVal;
		
	}	// alertEnabled
	
}	// ChatObservingCourier

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy/tools/src/java/org/sakaiproject/tool/chat/ChatObservingCourier.java,v 1.2 2005/05/12 18:53:20 ggolden.umich.edu Exp $
*
**********************************************************************************/
