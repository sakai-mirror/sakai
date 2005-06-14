/**********************************************************************************
*
* $Header: /cvs/sakai/framework-component/src/java/org/sakaiproject/component/framework/courier/BasicCourierService.java,v 1.7 2004/06/22 03:13:31 ggolden Exp $
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
package org.sakaiproject.component.framework.courier;

// imports
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.sakaiproject.service.framework.courier.CourierService;
import org.sakaiproject.service.framework.courier.Delivery;
import org.sakaiproject.service.framework.log.Logger;

/**
* <p>BasicCourierService is the implementation for a CHEF CourierService,
* implemented as a Turbine service.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.7 $
*/
public class BasicCourierService
	implements CourierService
{
	/** Stores a List of Deliveries for each address, keyed by address. */
	protected Map m_addresses = new Hashtable();

	/*******************************************************************************
	* Dependencies and their setter methods
	*******************************************************************************/

	/** Dependency: logging service */
	protected Logger m_logger = null;

	/**
	 * Dependency: logging service.
	 * @param service The logging service.
	 */
	public void setLogger(Logger service)
	{
		m_logger = service;
	}

	/*******************************************************************************
	* Init and Destroy
	*******************************************************************************/

	/**
	 * Final initialization, once all dependencies are set.
	 */
	public void init()
	{
		m_addresses.clear();
		m_logger.info(this + ".init()");
	}

	/**
	* Returns to uninitialized state.
	*/
	public void destroy()
	{
		m_addresses.clear();
		m_logger.info(this + ".destroy()");
	}

	/*******************************************************************************
	* CourierService implementation
	*******************************************************************************/

	/**
	* Cause the particular client window to get this (html) element updated.
	* A shorthand for deliver(new RefreshDelivery(address, elementId));
	* @param address The address of the session client window.
	* @param elementId The id of the html element to refresh.
	*/
	public void deliver(String address, String elementId)
	{
		deliver(new RefreshDelivery(address, elementId));

	}	// deliver

	/**
	* Queue up a delivery for the client window identified in the Delivery object.
	* The particular form of delivery is determined by the type of Delivery object sent.
	* @param delivery The Delivery (or extension) object to deliver.
	*/
	public void deliver(Delivery delivery)
	{
		if (m_logger.isDebugEnabled())
			m_logger.debug(this + ".deliver(): " + delivery);

		String address = delivery.getAddress();

		// find the entry in m_addresses
		List deliveries = (List) m_addresses.get(address);

		// create if needed
		if (deliveries == null)
		{
			synchronized (m_addresses)
			{
				deliveries = (List) m_addresses.get(address);
				if (deliveries == null)
				{
					deliveries = new Vector();
					m_addresses.put(address, deliveries);
				}
			}
		}

		// if this doesn't exist in the list already, add it
		synchronized (deliveries)
		{
			if (!deliveries.contains(delivery))
			{
				deliveries.add(delivery);
			}
		}

	}	// deliver

	/**
	* Clear any pending delivery requests to the particular client window for this element.
	* @param address The address of the client window.
	* @param elementId The id of the html element.
	*/
	public void clear(String address, String elementId)
	{
		if (m_logger.isDebugEnabled())
			m_logger.debug(this + ".clear(): " + address + ", " + elementId);

		// find the entry in m_addresses
		List deliveries = (List) m_addresses.get(address);

		// if not there we are done
		if (deliveries == null) return;

		// remove any Deliveries with this elementId
		synchronized (deliveries)
		{
			for (Iterator it = deliveries.iterator(); it.hasNext();)
			{
				Delivery delivery = (Delivery) it.next();
				if (delivery.getElement().equals(elementId))
				{
					it.remove();
				}
			}
		}

		// if none left, remove it from the list
		if (deliveries.isEmpty())
		{
			synchronized (m_addresses)
			{
				m_addresses.remove(address);
			}
		}

	}	// clear

	/**
	* Clear any pending delivery requests to this session client window.
	* @param address The address of client window.
	*/
	public void clear(String address)
	{
		if (m_logger.isDebugEnabled())
			m_logger.debug(this + ".clear(): " + address);

		// remove this portal from m_addresses
		synchronized (m_addresses)
		{
			m_addresses.remove(address);
		}

	}	// clear

	/**
	* Access and de-queue the Deliveries queued up for a particular session client window.
	* @param address The address of client window.
	* @return a List of Delivery objects addressed to this session client window.
	*/
	public List getDeliveries(String address)
	{
		if (m_logger.isDebugEnabled())
			m_logger.debug(this + ".getDeliveries(): " + address);

		// find the entry in m_addresses
		List deliveries = null;
		synchronized (m_addresses)
		{
			deliveries = (List) m_addresses.get(address);
			if (deliveries != null)
			{
				m_addresses.remove(address);
			}
		}

		// if empty, return something
		if (deliveries == null)
		{
			deliveries = new Vector();
		}

		// "act" all the deliveries
		for (Iterator it = deliveries.iterator(); it.hasNext(); )
		{
			Delivery delivery = (Delivery) it.next();
			delivery.act();
		}

		return deliveries;

	}	// getDeliveries

	/**
	* Check to see if there are any deliveries queued up for a particular session client window.
	* @param address The address of the client window.
	* @return true if there are deliveries for this client window, false if not.
	*/
	public boolean hasDeliveries(String address)
	{
		if (m_logger.isDebugEnabled())
			m_logger.debug(this + ".hasDeliveries(): " + address);

		// find the entry in m_addresses
		List deliveries = (List) m_addresses.get(address);
		if (deliveries == null) return false;

		return (!deliveries.isEmpty());

	}	// hasDeliveries

}	// BasicCourierService

/**********************************************************************************
*
* $Header: /cvs/sakai/framework-component/src/java/org/sakaiproject/component/framework/courier/BasicCourierService.java,v 1.7 2004/06/22 03:13:31 ggolden Exp $
*
**********************************************************************************/
