/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/event/EventTrackingService.java,v 1.1 2005/05/12 15:45:32 ggolden.umich.edu Exp $
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
package org.sakaiproject.service.legacy.event;

// imports
import java.util.Observer;

import org.sakaiproject.service.framework.session.UsageSession;

/**
* <p>EventTrackingService is the Interface for the CHEF event tracking service.</p>
* <p>The event tracking service provides activity event tracking and monitoring.
* Objects act as event generators, posting events to the service.  Other objects act
* as event monitors, and are notified by the service when certain events occur.
* Events posted are also stored in event archives by the service.</p>
* <p>Each event is represented by a core Event object</p>
* 
* @author University of Michigan, CHEF Software Development Team
* @version 1.0
* @see org.chefproject.core.Event
*/
public interface EventTrackingService
{
	/** This string can be used to find the service in the service manager. */
	public static final String SERVICE_NAME = EventTrackingService.class.getName();

	/**
	* Construct a Event object.
	* @param event The Event id.
	* @param resource The resource reference.
	* @param modify Set to true if this event caused a resource modification,
	* false if it was just an access.
	* @return A new Event object that can be used with this service.
	*/
	public Event newEvent(String event, String resource, boolean modify);

	/**
	* Construct a Event object.
	* @param event The Event id.
	* @param resource The resource reference.
	* @param modify Set to true if this event caused a resource modification,
	* false if it was just an access.
	* @param priority The Event's notification priority.
	* @return A new Event object that can be used with this service.
	*/
	public Event newEvent(String event, String resource, boolean modify, int priority);

	/**
	* Post an event
	* @param event The event object (created with newEvent()).
	* Note: the current session user will be used as the user responsible for the event.
	*/
	public void post(Event event);

	/**
	* Post an event on behalf of a user's session
	* @param event The event object (created with newEvent()).
	* @param session The usage session object of the user session responsible for the event.
	*/
	public void post(Event event, UsageSession session);

	/**
	* Add an observer of events.  The observer will be notified whenever there are new events.
	* @param observer The class observing.
	*/
	public void addObserver(Observer observer);

	/**
	* Add an observer of events.  The observer will be notified whenever there are new events.
	* Priority observers get notified first, before normal observers.
	* @param observer The class observing.
	*/
	public void addPriorityObserver(Observer observer);

	/**
	* Add an observer of events.  The observer will be notified whenever there are new events.
	* Local observers get notified only of event generated on this application server, not on those generated elsewhere.
	* @param observer The class observing.
	*/
	public void addLocalObserver(Observer observer);

	/**
	* Delete an observer of events.
	* @param observer The class observing to delete.
	*/
	public void deleteObserver(Observer observer);

}	// EventTracking

/**********************************************************************************
*
* $Header: /cvs/sakai2/legacy-service/service/src/java/org/sakaiproject/service/legacy/event/EventTrackingService.java,v 1.1 2005/05/12 15:45:32 ggolden.umich.edu Exp $
*
**********************************************************************************/
