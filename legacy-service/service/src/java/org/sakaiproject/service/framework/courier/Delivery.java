/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/framework/courier/Delivery.java,v 1.2 2005/05/12 23:48:06 ggolden.umich.edu Exp $
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
package org.sakaiproject.service.framework.courier;

// imports

/**
* <p>Delivery is the core interface for things sent to the Courier service that represent
* various sorts of deliveries to the client windows.</p>
* <p>Address is a client window address. </p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public interface Delivery
{
	/**
	* Set the delivery address.
	* @param address The delivery address.
	*/
	public void setAddress(String address);

	/**
	* Access the delivery address.
	* @return The delivery address.
	*/
	public String getAddress();

	/**
	* Set the HTML Element Id that this delivery is in reference to.
	* @param id The HTML Element Id that this delivery is in reference to.
	*/
	public void setElement(String id);

	/**
	* Access the HTML Element Id that this delivery is in reference to.
	* @return The HTML Element Id that this delivery is in reference to.
	*/
	public String getElement();

	/**
	* Perform any pre-delivery actions.
	* Note: this is run in the same usage session as is being delivered to.
	*/
	public void act();

	/**
	* Compose a javascript message for delivery to the browser client window.
	* @return The javascript message to send to the browser client window.
	*/
	public String compose();

}   // Delivery

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/framework/courier/Delivery.java,v 1.2 2005/05/12 23:48:06 ggolden.umich.edu Exp $
*
**********************************************************************************/
