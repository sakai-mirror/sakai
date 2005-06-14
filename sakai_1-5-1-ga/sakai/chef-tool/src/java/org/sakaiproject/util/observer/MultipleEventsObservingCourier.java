/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/util/observer/MultipleEventsObservingCourier.java,v 1.7 2004/09/20 13:13:51 ggolden.umich.edu Exp $
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

import org.sakaiproject.service.legacy.event.Event;

/**
* <p>MultipleEventsObservingCourier is an EventObservingCourier that handles many different event patterns.</p>
*
* @author University of Michigan, CHEF Software Development Team
* @version $Revision: 1.7 $
*/
public class MultipleEventsObservingCourier
	extends EventObservingCourier
{
	/** Other patterns to watch for. */
	protected List m_otherPatterns = null;

	/**
	* Construct.
	* @param deliveryId The key identifying the Portal Page Instance.
	* @param elementId The key identifying the element on the Portal Page that would
    * need a courier delivered message when things change.
	* @param One event resource pattern - we watch for only events whose ref
	* start with this. (Use setResourcePattern() and addResourcePattern() to clear or add more)
	*/
	public MultipleEventsObservingCourier(String deliveryId, String elementId,
					String resourcePattern)
	{
		super(deliveryId, elementId, resourcePattern);

		// setup for other patterns
		m_otherPatterns = new Vector();

	}	// MultipleEventsObservingCourier

	/**
	* Set the resource pattern to this one and only value - clear any others.  More can be set after this
	* using addResourcePattern.
	* @param pattern The new pattern to use.
	*/
	public void setResourcePattern(String pattern)
	{
		super.setResourcePattern(pattern);
		m_otherPatterns.clear();

	}	// setResourcePattern

	/**
	* Add another resource pattern to watch for.
	* @param pattern The new pattern to use.
	*/
	public void addResourcePattern(String pattern)
	{
		m_otherPatterns.add(pattern);

	}	// addResourcePattern

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

			// check the one pattern
			boolean match = false;
			if (key.startsWith(m_resourcePattern)) match = true;
			
			// check the others
			if (!match)
			{
				try
				{
					for (Iterator iPatterns = m_otherPatterns.iterator(); (iPatterns.hasNext() && !match);)
					{
						String pattern = (String) iPatterns.next();
						if (key.startsWith(pattern)) match = true;
					}
				}

				// if the pattern list is being modified, and we get this, lets just skip this event.
				catch (ConcurrentModificationException e) {}
			}
				
			// if this resource is not in my pattern of resources, we can ignore it
			if (!match) return false;
		}

		if (m_logger.isDebugEnabled())
			m_logger.debug(this + ".update() [" + m_resourcePattern + "] resource: " + key + " event: " + event.getEvent());

		return true;

	}	// check

}	// MultipleEventsObservingCourier

/**********************************************************************************
*
* $Header: /cvs/sakai/chef-tool/src/java/org/sakaiproject/util/observer/MultipleEventsObservingCourier.java,v 1.7 2004/09/20 13:13:51 ggolden.umich.edu Exp $
*
**********************************************************************************/
