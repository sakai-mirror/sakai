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
package org.sakaiproject.service.framework.courier;

// imports
import java.util.List;

/**
* <p>GenericCourierService is the Interface for a CHEF service which can be used to push
* messages from the CHEF server components to the user interface in the browser.
* It is used mostly to cause a tool in a particular portal instance to be refreshed
* to respond to a change noticed at the server. </p>
* <p> An Address identifies a particular client's window: it merges the Usage session, the window's
* portal page location, and perhaps the tool id (for floating tool windows). </p>
* <p> A Delivery object captures the Address, the HTML Element Id involved, and any other details of a
* particular type of delivery. </p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision$
*/
public interface CourierService
{
	/** This string can be used to find the service in the service manager. */
	public static final String SERVICE_NAME = CourierService.class.getName();

	/**
	* Queue up a delivery for the client window identified in the Delivery object.
	* The particular form of delivery is determined by the type of Delivery object sent.
	* @param delivery The Delivery (or extension) object to deliver.
	*/
	public void deliver(Delivery delivery);

	/**
	* Clear any pending delivery requests to the particular client window for this element.
	* @param address The address of the client window.
	* @param elementId The id of the html element.
	*/
	public void clear(String address, String elementId);

	/**
	* Clear any pending delivery requests to this session client window.
	* @param address The address of client window.
	*/
	public void clear(String address);

	/**
	* Access and de-queue the Deliveries queued up for a particular session client window.
	* @param address The address of client window.
	* @return a List of Delivery objects addressed to this session client window.
	*/
	public List getDeliveries(String address);

	/**
	* Check to see if there are any deliveries queued up for a particular session client window.
	* @param address The address of the client window.
	* @return true if there are deliveries for this client window, false if not.
	*/
	public boolean hasDeliveries(String address);

}	// CourierService



