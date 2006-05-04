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
package org.sakaiproject.service.legacy.event;

// imports

/**
* <p>Event is the core interface for the CHEF Event object.</p>
* <p>Event objects are posted to the EventTracking service, and may be listened for.</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version $Revision:
*/
public interface Event
{
	/**
	* Access the event id string
	* @return The event id string.
	*/
	public String getEvent();

	/**
	* Access the resource reference.
	* @return The resource reference string.
	*/
	public String getResource();

	/**
	* Access the UsageSession id.  If null, check for a User id.
	* @return The UsageSession id string.
	*/
	public String getSessionId();

	/**
	* Access the User id.  If null, check for a session id.
	* @return The User id string.
	*/
	public String getUserId();

	/**
	* Is this event one that caused a modify to the resource, or just an access.
	* @return true if the event caused a modify to the resource, false if it was just an access.
	*/
	public boolean getModify();

	/**
	* Access the event's notification priority.
	* @return The event's notification priority.
	*/
	public int getPriority();

}	// Event



