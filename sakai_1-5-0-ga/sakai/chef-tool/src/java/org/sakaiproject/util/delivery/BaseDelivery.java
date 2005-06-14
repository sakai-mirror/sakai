/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/util/delivery/Attic/BaseDelivery.java,v 1.7 2004/06/22 03:04:57 ggolden Exp $
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
package org.sakaiproject.util.delivery;

// imports
import org.sakaiproject.service.framework.courier.Delivery;
import org.sakaiproject.util.StringUtil;

/**
* <p>BaseDelivery is a base class for all Delivery objects.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.7 $
*/
public class BaseDelivery
	implements Delivery
{
	/** The address. */
	protected String m_address = null;

	/** The elementId. */
	protected String m_elementId = null;

	/**
	* Construct.
	* @param address The address.
	* @param elementId The elementId.
	*/
	public BaseDelivery(String address, String elementId)
	{
		m_address = address;
		m_elementId = elementId;

	}	// BaseDelivery

	/**
	* Set the delivery address.
	* @param address The delivery address.
	*/
	public void setAddress(String address)
	{
		m_address = address;

	}	// setAddress

	/**
	* Access the delivery address.
	* @return The delivery address.
	*/
	public String getAddress()
	{
		return m_address;

	}	// getAddress

	/**
	* Set the HTML Element Id that this delivery is in reference to.
	* @param id The HTML Element Id that this delivery is in reference to.
	*/
	public void setElement(String id)
	{
		m_elementId = id;

	}	// setElement

	/**
	* Access the HTML Element Id that this delivery is in reference to.
	* @return The HTML Element Id that this delivery is in reference to.
	*/
	public String getElement()
	{
		return m_elementId;

	}	// getElement

	/**
	* Perform any pre-delivery actions.
	* Note: this is run in the same usage session as is being delivered to.
	*/
	public void act() {}

	/**
	* Compose a javascript message for delivery to the browser client window.
	* @return The javascript message to send to the browser client window.
	*/
	public String compose()
	{
		return "";

	}	// compose

	/**
	* Display.
	*/
	public String toString()
	{
		return m_address + " : " + m_elementId;

	}	// toString

	/**
	* Are these the same?
	* @return true if obj is the same Delivery as this one.
	*/
	public boolean equals(Object obj)
	{
		if (!(obj instanceof BaseDelivery)) return false;

		BaseDelivery bob = (BaseDelivery) obj;
		if (StringUtil.different(bob.getAddress(), getAddress())) return false;
		if (StringUtil.different(bob.getElement(), getElement())) return false;
		
		return true;

	}	// equals

	/**
	* Form a string of java script that tests the element id for existence - each doted part.
	* @param element The html element id, such as parent.Main234324243.List or parent or parent.Main23423432
	* @return Javascript "if" statements that test each part, such as:
	* "if (parent) if (parent.Main234324243) if (parent.Main234324243.List)"
	*/
	protected static String ifExistsScript(String element)
	{
		// split by the dot
		String[] parts = StringUtil.split(element, ".");
		if ((parts == null) || (parts.length == 0))
		{
			return "if (" + element + ")";
		}

		StringBuffer buf = new StringBuffer();
		String elementId = null;
		for (int i = 0; i < parts.length; i++)
		{
			if ((parts[i] == null) || (parts[i].length() == 0)) continue;
			if (elementId == null)
			{
				elementId = parts[i];
			}
			else
			{
				elementId = elementId + "." + parts[i];
			}
			buf.append("if (" + elementId + ") ");
		}

		return buf.toString();

	}	// ifExistsScript

}   // BaseDelivery

/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/util/delivery/Attic/BaseDelivery.java,v 1.7 2004/06/22 03:04:57 ggolden Exp $
*
**********************************************************************************/
