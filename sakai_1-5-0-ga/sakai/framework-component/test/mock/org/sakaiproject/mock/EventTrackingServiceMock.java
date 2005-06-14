/**********************************************************************************
*
* $Header: /cvs/sakai/framework-component/test/mock/org/sakaiproject/mock/EventTrackingServiceMock.java,v 1.1 2004/08/25 13:38:25 brettm.umich.edu Exp $
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

package org.sakaiproject.mock;
import java.util.Observer;

import org.sakaiproject.service.framework.session.UsageSession;
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.event.EventTrackingService;

/**
 * Mock object for EventTrackingService
 */
public class EventTrackingServiceMock implements EventTrackingService
{
	private String defaultUserId="abcdef";
	private int defaultPriority=0;
	private String defaultSessionId="12345";

    /**
     * @param defaultPriority The defaultPriority to set.
     */
    public void setDefaultPriority(int defaultPriority)
    {
        this.defaultPriority = defaultPriority;
    }
    /**
     * @param defaultSessionId The defaultSessionId to set.
     */
    public void setDefaultSessionId(String defaultSessionId)
    {
        this.defaultSessionId = defaultSessionId;
    }
    /**
     * @param defaultUserId The defaultUserId to set.
     */
    public void setDefaultUserId(String defaultUserId)
    {
        this.defaultUserId = defaultUserId;
    }
	/**
	* Construct a Event object.
	* @param event The Event id.
	* @param resource The resource reference.
	* @param modify Set to true if this event caused a resource modification,
	* false if it was just an access.
	* @return A new Event object that can be used with this service.
	*/
	public Event newEvent(String event, String resource, boolean modify)
	{
	    return new EventMock(event, resource, defaultSessionId,
	            defaultUserId, modify, defaultPriority);
	}

	/**
	* Construct a Event object.
	* @param event The Event id.
	* @param resource The resource reference.
	* @param modify Set to true if this event caused a resource modification,
	* false if it was just an access.
	* @param priority The Event's notification priority.
	* @return A new Event object that can be used with this service.
	*/
	public Event newEvent(String event, String resource, boolean modify, int priority)
	{
	    return new EventMock(event, resource, defaultSessionId,
	            defaultUserId, modify, priority);
	}

	/**
	* Post an event
	* @param event The event object (created with newEvent()).
	* Note: the current session user will be used as the user responsible for the event.
	*/
	public void post(Event event)
	{
	}

	/**
	* Post an event on behalf of a user's session
	* @param event The event object (created with newEvent()).
	* @param session The usage session object of the user session responsible for the event.
	*/
	public void post(Event event, UsageSession session)
	{
	}

	/**
	* Add an observer of events.  The observer will be notified whenever there are new events.
	* @param observer The class observing.
	*/
	public void addObserver(Observer observer)
	{
	}

	/**
	* Add an observer of events.  The observer will be notified whenever there are new events.
	* Priority observers get notified first, before normal observers.
	* @param observer The class observing.
	*/
	public void addPriorityObserver(Observer observer)
	{
	}

	/**
	* Add an observer of events.  The observer will be notified whenever there are new events.
	* Local observers get notified only of event generated on this application server, not on those generated elsewhere.
	* @param observer The class observing.
	*/
	public void addLocalObserver(Observer observer)
	{
	}

	/**
	* Delete an observer of events.
	* @param observer The class observing to delete.
	*/
	public void deleteObserver(Observer observer)
	{
	}

}
