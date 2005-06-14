/**********************************************************************************
*
* $Header: /cvs/presentation/present-tool/src/java/org/sakaiproject/util/observer/EventsObservingCourier.java,v 1.1 2004/12/26 03:23:35 csev.umich.edu Exp $
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
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.sakaiproject.service.framework.session.SessionStateBindingListener;
import org.sakaiproject.service.legacy.event.Event;
import org.sakaiproject.service.legacy.event.EventTrackingService;

/**
* <p>EventsObservingCourier is an ObservingCourier that watches Events, of a set of particular
* reference root patterns.  It automatically registeres / unregisteres as an observer with the event service.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.1 $
*/
public class EventsObservingCourier
	extends ObservingCourier
	implements SessionStateBindingListener
{
	/** Constructor discovered injected EventTrackingService. */
	protected EventTrackingService m_eventTrackingService = null;

	/** The event resource pattern(s) (String)- we watch for only events that start with any of these */
	protected List m_resourcePattern = new Vector();

	/**
	* Construct.
	* @param deliveryId The key identifying the Portal Page Instance.
	* @param elementId The key identifying the element on the Portal Page that would
    * need a courier delivered message when things change.
	* @param pattern The event resource pattern - we watch for only events whose ref
	* start with this.
	*/
	public EventsObservingCourier(String deliveryId, String elementId, String pattern)
	{
		super(deliveryId, elementId);
		if (pattern != null)
		{
			m_resourcePattern.add(pattern);
		}

		// "inject" a eventTrackingService
		m_eventTrackingService = org.sakaiproject.service.legacy.event.cover.EventTrackingService.getInstance();

		// register to listen to events
		m_eventTrackingService.addObserver(this);

	}	// EventsObservingCourier

	/**
	* Construct.
	* @param deliveryId The key identifying the Portal Page Instance.
	* @param elementId The key identifying the element on the Portal Page that would
    * need a courier delivered message when things change.
	* @param patterns The event resource pattern list (String) - we watch for only events whose ref
	* start with any of these.
	*/
	public EventsObservingCourier(String deliveryId, String elementId, List resourcePatterns)
	{
		super(deliveryId, elementId);
		if (resourcePatterns != null)
		{
			m_resourcePattern.addAll(resourcePatterns);
		}

		// "inject" a eventTrackingService
		m_eventTrackingService = org.sakaiproject.service.legacy.event.cover.EventTrackingService.getInstance();

		// register to listen to events
		m_eventTrackingService.addObserver(this);

	}	// EventsObservingCourier

	/**
	* Get the first registered pattern.
	* @return The first registered resource pattern.
	*/
	public String getResourcePattern()
	{
		return (String) m_resourcePattern.get(0);

	}	// getResourcePattern

	/**
	* Get all registered pattern.
	* @return The List (String) of all registered resource patterns.
	*/
	public List getResourcePatterns()
	{
		return new Vector(m_resourcePattern);

	}	// getResourcePatterns

	/**
	* Replace all patterns with just this one.
	* @param pattern The new pattern.
	*/
	public void setResourcePattern(String pattern)
	{
		m_resourcePattern.clear();
		if (pattern != null)
		{
			m_resourcePattern.add(pattern);
		}

	}	// setResourcePattern

	/**
	* Replace all patterns with just these.
	* @param pattern The List (String) of new patterns.
	*/
	public void setResourcePatterns(List patterns)
	{
		m_resourcePattern.clear();
		if (patterns != null)
		{
			m_resourcePattern.addAll(patterns);
		}

	}	// setResourcePatterns

	/**
	* Add an additional pattern - existing ones are kept.
	* @param pattern The new pattern to add.
	*/
	public void addResourcePattern(String pattern)
	{
		if (pattern != null)
		{
			m_resourcePattern.add(pattern);
		}

	}	// setResourcePattern

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
		if ((m_resourcePattern != null) && (!m_resourcePattern.isEmpty()))
		{
			key = event.getResource();

			try
			{
				for (Iterator iPattern = m_resourcePattern.iterator(); iPattern.hasNext();)
				{
					String pattern = (String) iPattern.next();

					// if this resource is in my pattern of resources, accept it
					if (key.startsWith(pattern))
					{
						if (m_logger.isDebugEnabled())
							m_logger.debug(this + ".update() [" + pattern + "] resource: " + key + " event: " + event.getEvent());

						return true;
					}
				}
			}

			// if the pattern list is being modified, and we get this, lets just skip this event.
			catch (ConcurrentModificationException e) {}

			// didn't find any pattern match, so
			return false;
		}

		// if no patterns are set, we take ALL events!
		if (m_logger.isDebugEnabled())
			m_logger.debug(this + ".update() [] resource: " + key + " event: " + event.getEvent());

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

}	// EventsObservingCourier

/**********************************************************************************
*
* $Header: /cvs/presentation/present-tool/src/java/org/sakaiproject/util/observer/EventsObservingCourier.java,v 1.1 2004/12/26 03:23:35 csev.umich.edu Exp $
*
**********************************************************************************/
