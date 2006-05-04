/**********************************************************************************
*
* $Header: /cvs/presentation/present-tool/src/java/org/sakaiproject/util/observer/EventObservingCourier.java,v 1.1 2004/12/26 03:23:35 csev.umich.edu Exp $
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
package org.sakaiproject.util.observer;

// imports
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.event.EventTrackingService;
import org.sakaiproject.service.framework.session.SessionStateBindingListener;

/**
* <p>EventObservingCourier is an ObservingCourier that watches Events, of a particular
* reference root.  It automatically registeres / unregisteres as an observer with the event service.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.1 $
*/
public class EventObservingCourier
	extends ObservingCourier
	implements SessionStateBindingListener
{
	/** Constructor discovered injected EventTrackingService. */
	protected EventTrackingService m_eventTrackingService = null;

	/**
	* Construct.
	* @param deliveryId The key identifying the Portal Page Instance.
	* @param elementId The key identifying the element on the Portal Page that would
    * need a courier delivered message when things change.
	* @param The event resource pattern - we watch for only events whose ref
	* start with this.
	*/
	public EventObservingCourier(String deliveryId, String elementId,
					String resourcePattern)
	{
		super(deliveryId, elementId);
		m_resourcePattern = resourcePattern;

		// "inject" a eventTrackingService
		m_eventTrackingService = org.sakaiproject.service.legacy.event.cover.EventTrackingService.getInstance();

		// register to listen to events
		m_eventTrackingService.addObserver(this);
			//%%% add the pattern to have it filtered there?

	}	// EventObservingCourier

	/** The event resource pattern - we watch for only events that start with this */
	protected String m_resourcePattern = null;
	public String getResourcePattern() { return m_resourcePattern; }
	public void setResourcePattern(String pattern) { m_resourcePattern = pattern; }
		//%%% re-register? add the pattern to have it filtered there?

	/**
	* Check to see if we want to process or ignore this update.
	* @param arg The arg from the update.
	* @return true to continue, false to quit.
	*/
	protected boolean check(Object arg)
	{
		// arg is Event
		if (!(arg instanceof Event)) return false;
		Event event = (Event) arg;

		// if this is just a read, not a modify event, we can ignore it
		if (!event.getModify()) return false;

		String key = null;

		// filter out events not for us
		if (m_resourcePattern != null)
		{
			key = event.getResource();

			// if this resource is not in my pattern of resources, we can ignore it
			if (!key.startsWith(m_resourcePattern)) return false;
		}

		if (m_logger.isDebugEnabled())
			m_logger.debug(this + ".update() [" + m_resourcePattern + "] resource: " + key + " event: " + event.getEvent());

		return true;

	}	// check

	protected void finalize()
	{
		// stop observing the presence location
		m_eventTrackingService.deleteObserver(this);

		if (m_logger.isDebugEnabled())
			m_logger.debug(this + ".finalize()");

	}	// finalize

	/*******************************************************************************
	* SessionStateBindingListener implementation
	*******************************************************************************/

    /**
    * Accept notification that this object has been bound as a SessionState attribute.
    * @param sessionStateKey The id of the session state which holds the attribute.
    * @param attributeName The id of the attribute to which this object is now the value.
    */
    public void valueBound(String sessionStateKey, String attributeName) {}

    /**
    * Accept notification that this object has been removed from a SessionState attribute.
    * @param sessionStateKey The id of the session state which held the attribute.
    * @param attributeName The id of the attribute to which this object was the value.
    */
    public void valueUnbound(String sessionStateKey, String attributeName)
	{
		if (m_logger.isDebugEnabled())
			m_logger.debug(this + ".valueUnbound()");

		// stop observing the presence location
		m_eventTrackingService.deleteObserver(this);

	}	// valueUnbound

}	// EventObservingCourier

/**********************************************************************************
*
* $Header: /cvs/presentation/present-tool/src/java/org/sakaiproject/util/observer/EventObservingCourier.java,v 1.1 2004/12/26 03:23:35 csev.umich.edu Exp $
*
**********************************************************************************/