/**********************************************************************************
 *
 * $Header: /cvs/sakai2/high-util/courier/src/java/org/sakaiproject/util/courier/BaseDelivery.java,v 1.2 2005/05/12 23:49:00 ggolden.umich.edu Exp $
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
import org.sakaiproject.service.framework.courier.Delivery;
import org.sakaiproject.util.java.StringUtil;

/**
 * <p>
 * BaseDelivery is a base class for all Delivery objects.
 * </p>
 * 
 * @author University of Michigan, Sakai Software Development Team
 * @version $Revision$
 */
public class BaseDelivery implements Delivery
{
	/** The address. */
	protected String m_address = null;

	/** The elementId. */
	protected String m_elementId = null;

	/**
	 * Construct.
	 * 
	 * @param address
	 *        The address.
	 * @param elementId
	 *        The elementId.
	 */
	public BaseDelivery(String address, String elementId)
	{
		m_address = address;
		m_elementId = elementId;
	}

	/**
	 * Set the delivery address.
	 * 
	 * @param address
	 *        The delivery address.
	 */
	public void setAddress(String address)
	{
		m_address = address;
	}

	/**
	 * Access the delivery address.
	 * 
	 * @return The delivery address.
	 */
	public String getAddress()
	{
		return m_address;
	}

	/**
	 * Set the HTML Element Id that this delivery is in reference to.
	 * 
	 * @param id
	 *        The HTML Element Id that this delivery is in reference to.
	 */
	public void setElement(String id)
	{
		m_elementId = id;
	}

	/**
	 * Access the HTML Element Id that this delivery is in reference to.
	 * 
	 * @return The HTML Element Id that this delivery is in reference to.
	 */
	public String getElement()
	{
		return m_elementId;
	}

	/**
	 * Perform any pre-delivery actions. Note: this is run in the same usage session as is being delivered to.
	 */
	public void act()
	{
	}

	/**
	 * Compose a javascript message for delivery to the browser client window.
	 * 
	 * @return The javascript message to send to the browser client window.
	 */
	public String compose()
	{
		return "";
	}

	/**
	 * Display.
	 */
	public String toString()
	{
		return m_address + " : " + m_elementId;
	}

	/**
	 * Are these the same?
	 * 
	 * @return true if obj is the same Delivery as this one.
	 */
	public boolean equals(Object obj)
	{
		if (!(obj instanceof BaseDelivery)) return false;

		BaseDelivery bob = (BaseDelivery) obj;
		if (StringUtil.different(bob.getAddress(), getAddress())) return false;
		if (StringUtil.different(bob.getElement(), getElement())) return false;

		return true;
	}
}

/**************************************************************************************************************************************************************************************************************************************************************
 * $Header: /cvs/sakai2/high-util/courier/src/java/org/sakaiproject/util/courier/BaseDelivery.java,v 1.2 2005/05/12 23:49:00 ggolden.umich.edu Exp $
 *************************************************************************************************************************************************************************************************************************************************************/
