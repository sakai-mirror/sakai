/**********************************************************************************
 *
 * $Header: /cvs/sakai2/high-util/courier/src/java/org/sakaiproject/util/courier/ObservingCourier.java,v 1.2 2005/05/12 23:49:00 ggolden.umich.edu Exp $
 *
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005 The Regents of the University of Michigan, Trustees of Indiana University,
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
package org.sakaiproject.util.courier;

// imports
import java.util.Observable;
import java.util.Observer;

import org.sakaiproject.api.kernel.session.cover.SessionManager;
import org.sakaiproject.service.framework.courier.CourierService;

/**
 * <p>
 * ObservingCourier is an observer which uses the courier service to notify when things change.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public abstract class ObservingCourier implements Observer
{
	/** Constructor discovered injected CourierService. */
	protected CourierService m_courierService = null;

	/** The location (id not ref). */
	protected String m_location = null;

	/**
	 * Construct.
	 * 
	 * @param location
	 *        The key identifying the client window to which this courier delivers updates.
	 * @param elementId
	 *        The key identifying the element on the Portal Page that would need a courier delivered message when things change.
	 */
	public ObservingCourier(String location, String elementId)
	{
		m_deliveryId = SessionManager.getCurrentSession().getId() + location;
		m_elementId = elementId;
		m_location = location;

		// "inject" a CourierService
		m_courierService = org.sakaiproject.service.framework.courier.cover.CourierService.getInstance();
	}

	/** The key identifying the Portal PageSession. */
	protected String m_deliveryId = "";

	public String getDeliveryId()
	{
		return m_deliveryId;
	}

	public void setDeliveryId(String id)
	{
		m_deliveryId = id;
	}

	/** The key identifying the element on the Portal Page. */
	protected String m_elementId = "";

	public String getElementId()
	{
		return m_elementId;
	}

	public void setElementId(String id)
	{
		m_elementId = id;
	}

	/** The enabled state. */
	protected boolean m_enabled = true;

	public boolean getEnabled()
	{
		return m_enabled;
	}

	public void enable()
	{
		m_enabled = true;
	}

	public void disable()
	{
		m_enabled = false;
	}

	/**
	 * Accept notification that the portal element has just been delivered. If there are pending requests to deliver, they can be cleared.
	 */
	public void justDelivered()
	{
		m_courierService.clear(getDeliveryId(), getElementId());
	}

	/**
	 * Check to see if we want to process or ignore this update.
	 * 
	 * @param arg
	 *        The arg from the update.
	 * @return true to continue, false to quit.
	 */
	protected boolean check(Object arg)
	{
		return true;
	}

	/**
	 * Access the location this observer is observing.
	 * 
	 * @return the location this observer is observing.
	 */
	public String getLocation()
	{
		return m_location;
	}

	/**********************************************************************************************************************************************************************************************************************************************************
	 * Observer implementation
	 *********************************************************************************************************************************************************************************************************************************************************/

	/**
	 * This method is called whenever the observed object is changed. An application calls an <tt>Observable</tt> object's <code>notifyObservers</code> method to have all the object's observers notified of the change. default implementation is to
	 * cause the courier service to deliver to the interface controlled by my controller. Extensions can override.
	 * 
	 * @param o
	 *        the observable object.
	 * @param arg
	 *        an argument passed to the <code>notifyObservers</code> method.
	 */
	public void update(Observable o, Object arg)
	{
		// ignore changes when not enabled
		if (!getEnabled())
		{
			return;
		}

		if (!check(arg)) return;

		m_courierService.deliver(new DirectRefreshDelivery(getDeliveryId(), getElementId()));
	}
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/high-util/courier/src/java/org/sakaiproject/util/courier/ObservingCourier.java,v 1.2 2005/05/12 23:49:00 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
